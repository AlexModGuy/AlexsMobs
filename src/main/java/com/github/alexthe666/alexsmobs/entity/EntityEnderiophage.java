package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;

public class EntityEnderiophage extends AnimalEntity implements IMob, IFlyingAnimal {

    private static final DataParameter<Float> PHAGE_PITCH = EntityDataManager.createKey(EntityEnderiophage.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityEnderiophage.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> MISSING_EYE = EntityDataManager.createKey(EntityEnderiophage.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> PHAGE_SCALE = EntityDataManager.createKey(EntityEnderiophage.class, DataSerializers.FLOAT);
    private static final Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = (entity) -> {
        return entity instanceof EntityEndergrade || entity.isPotionActive(AMEffectRegistry.ENDER_FLU);
    };
    public float prevPhagePitch;
    public float tentacleAngle;
    public float lastTentacleAngle;
    public float phageRotation;
    public float prevFlyProgress;
    public float flyProgress;
    public int passengerIndex = 0;
    public float prevEnderiophageScale = 1F;
    private float rotationVelocity;
    private int slowDownTicks = 0;
    private float randomMotionSpeed;
    private boolean isLandNavigator;
    private int timeFlying = 0;
    private int fleeAfterStealTime = 0;
    private int attachTime = 0;
    private int dismountCooldown = 0;
    private int squishCooldown = 0;
    private CreatureEntity angryEnderman = null;

    protected EntityEnderiophage(EntityType type, World world) {
        super(type, world);
        this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
        switchNavigator(false);
        this.experienceValue = 5;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.enderiophageSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public static boolean canEnderiophageSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        return worldIn.getBlockState(pos.down()).isSolid();
    }

    public int getMaxSpawnedInChunk() {
        return 1;
    }

    public float getPhageScale() {
        return this.dataManager.get(PHAGE_SCALE);
    }

    public void setPhageScale(float scale) {
        this.dataManager.set(PHAGE_SCALE, scale);
    }


    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new FlyTowardsTarget(this));
        this.goalSelector.addGoal(2, new AIWalkIdle());
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, EndermanEntity.class, 15, true, true, null) {
            public boolean shouldExecute() {
                return EntityEnderiophage.this.isMissingEye() && super.shouldExecute();
            }

            public boolean shouldContinueExecuting() {
                return EntityEnderiophage.this.isMissingEye() && super.shouldContinueExecuting();
            }
        });
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
            public boolean shouldExecute() {
                return !EntityEnderiophage.this.isMissingEye() && EntityEnderiophage.this.fleeAfterStealTime == 0 && super.shouldExecute();
            }

            public boolean shouldContinueExecuting() {
                return !EntityEnderiophage.this.isMissingEye() && super.shouldContinueExecuting();
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this, EndermanEntity.class));

    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new FlightMoveController(this, 1F, false, true);
            this.navigator = new DirectPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PHAGE_PITCH, 0F);
        this.dataManager.register(PHAGE_SCALE, 1F);
        this.dataManager.register(FLYING, false);
        this.dataManager.register(MISSING_EYE, false);
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void setStandardFleeTime() {
        this.fleeAfterStealTime = 20;
    }

    public void updateRidden() {
        Entity entity = this.getRidingEntity();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setMotion(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                attachTime++;
                Entity mount = this.getRidingEntity();
                if (mount instanceof LivingEntity) {
                    passengerIndex = mount.getPassengers().indexOf(this);
                    this.renderYawOffset = ((LivingEntity) mount).renderYawOffset;
                    this.rotationYaw = ((LivingEntity) mount).rotationYaw;
                    this.rotationYawHead = ((LivingEntity) mount).rotationYawHead;
                    this.prevRotationYaw = ((LivingEntity) mount).rotationYawHead;
                    float radius = mount.getWidth();
                    float angle = (0.01745329251F * (((LivingEntity) mount).renderYawOffset + passengerIndex * 90F));
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    this.setPosition(mount.getPosX() + extraX, Math.max(mount.getPosY() + mount.getEyeHeight() * 0.25F, mount.getPosY()), mount.getPosZ() + extraZ);
                    if (!mount.isAlive() || mount instanceof PlayerEntity && ((PlayerEntity) mount).isCreative()) {
                        this.dismount();
                    }
                    this.setPhagePitch(0F);
                    if (!world.isRemote && attachTime > 15) {
                        LivingEntity target = (LivingEntity) mount;
                        float dmg = 1F;
                        if (target.getHealth() > target.getMaxHealth() * 0.2F) {
                            dmg = 3F;
                        }
                        if ((target.getHealth() < 1.5D || mount.attackEntityFrom(DamageSource.causeMobDamage(this), dmg)) && mount instanceof LivingEntity) {
                            dismountCooldown = 100;
                            if (mount instanceof EndermanEntity) {
                                this.setMissingEye(false);
                                this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, this.getSoundVolume(), this.getSoundPitch());
                                this.heal(5);
                                ((EndermanEntity) mount).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 400));
                                this.fleeAfterStealTime = 400;
                                this.setFlying(true);
                                this.angryEnderman = (CreatureEntity) mount;
                            } else {
                                if (rand.nextInt(3) == 0) {
                                    if (target.getActivePotionEffect(AMEffectRegistry.ENDER_FLU) == null) {
                                        target.addPotionEffect(new EffectInstance(AMEffectRegistry.ENDER_FLU, 12000));
                                    } else {
                                        EffectInstance inst = target.getActivePotionEffect(AMEffectRegistry.ENDER_FLU);
                                        int duration = 12000;
                                        int level = 0;
                                        if (inst != null) {
                                            duration = inst.getDuration();
                                            level = inst.getAmplifier();
                                        }
                                        target.removePotionEffect(AMEffectRegistry.ENDER_FLU);
                                        target.addPotionEffect(new EffectInstance(AMEffectRegistry.ENDER_FLU, duration, Math.min(level + 1, 4)));
                                    }
                                    this.heal(5);
                                    this.playSound(SoundEvents.ENTITY_ITEM_BREAK, this.getSoundVolume(), this.getSoundPitch());
                                    this.setMissingEye(true);
                                }
                            }
                        }
                        if (((LivingEntity) mount).getHealth() <= 0 || this.fleeAfterStealTime > 0 || this.isMissingEye() && !(mount instanceof EndermanEntity) || !this.isMissingEye() && mount instanceof EndermanEntity) {
                            this.dismount();
                            this.setAttackTarget(null);
                            dismountCooldown = 100;
                            AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.getEntityId(), mount.getEntityId()));
                            this.setFlying(true);
                        }
                    }
                }

            }
        }

    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public void onSpawnFromEffect() {
        prevEnderiophageScale = 0.2F;
        this.setPhageScale(0.2F);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ENDERIOPHAGE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ENDERIOPHAGE_HURT;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(AMSoundRegistry.ENDERIOPHAGE_WALK, 0.4F, 1.0F);
    }

    protected float determineNextStepDistance() {
        return this.distanceWalkedOnStepModified + 0.3F;
    }

    public void tick() {
        super.tick();
        prevEnderiophageScale = this.getPhageScale();
        float extraMotionSlow = 1.0F;
        float extraMotionSlowY = 1.0F;
        if (slowDownTicks > 0) {
            slowDownTicks--;
            extraMotionSlow = 0.33F;
            extraMotionSlowY = 0.1F;
        }
        if(dismountCooldown > 0){
            dismountCooldown--;
        }
        if(squishCooldown > 0){
            squishCooldown--;
        }
        if (!world.isRemote) {
            if (!this.isPassenger() && attachTime != 0) {
                attachTime = 0;
            }
            if (fleeAfterStealTime > 0) {
                if (angryEnderman != null) {
                    Vector3d vec = this.getBlockInViewAway(angryEnderman.getPositionVec(), 10);
                    if (fleeAfterStealTime < 5) {
                        if (angryEnderman instanceof IAngerable) {
                            ((IAngerable) angryEnderman).func_241356_K__();
                        }
                        try {
                            angryEnderman.goalSelector.getRunningGoals().forEach(Goal::resetTask);
                            angryEnderman.targetSelector.getRunningGoals().forEach(Goal::resetTask);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        angryEnderman = null;
                    }
                    if (vec != null) {
                        this.setFlying(true);
                        this.getMoveHelper().setMoveTo(vec.x, vec.y, vec.z, 1.3F);
                    }
                }
                fleeAfterStealTime--;
            }
        }
        this.renderYawOffset = this.rotationYaw;
        this.rotationYawHead = this.rotationYaw;
        this.setPhagePitch(-90F);
        if (this.isAlive() && this.isFlying() && randomMotionSpeed > 0.75F && this.getMotion().lengthSquared() > 0.02D) {
            if(world.isRemote){
                float pitch = -this.getPhagePitch() / 90F;
                float radius = this.getWidth() * 0.2F * -pitch;
                float angle = (0.01745329251F * this.rotationYaw);
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.2F - (1 - pitch) * 0.15F;
                double extraZ = radius * MathHelper.cos(angle);
                double motX = extraX * 8 + rand.nextGaussian() * 0.05F;
                double motY = -0.1F;
                double motZ = extraZ + rand.nextGaussian() * 0.05F;
                this.world.addParticle(AMParticleRegistry.DNA, this.getPosX() + extraX, this.getPosY() + extraY, this.getPosZ() + extraZ, motX, motY, motZ);
            }
        }
        prevPhagePitch = this.getPhagePitch();
        prevFlyProgress = flyProgress;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        this.lastTentacleAngle = this.tentacleAngle;
        this.phageRotation += this.rotationVelocity;
        if ((double) this.phageRotation > (Math.PI * 2D)) {
            if (this.world.isRemote) {
                this.phageRotation = ((float) Math.PI * 2F);
            } else {
                this.phageRotation = (float) ((double) this.phageRotation - (Math.PI * 2D));
                if (this.rand.nextInt(10) == 0) {
                    this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
                }
                this.world.setEntityState(this, (byte) 19);
            }
        }
        if (this.phageRotation < (float) Math.PI) {
            float f = this.phageRotation / (float) Math.PI;
            this.tentacleAngle = MathHelper.sin(f * f * (float) Math.PI) * 4.275F;
            if ((double) f > 0.75D) {
                if(squishCooldown == 0 && this.isFlying()){
                    squishCooldown = 20;
                    this.playSound(AMSoundRegistry.ENDERIOPHAGE_SQUISH, 3F, this.getSoundPitch());
                }
                this.randomMotionSpeed = 1.0F;
            } else {
                randomMotionSpeed = 0.01F;
            }
        }
        if (!this.world.isRemote) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.isFlying()) {
                this.setMotion(this.getMotion().x * this.randomMotionSpeed * extraMotionSlow, this.getMotion().y * this.randomMotionSpeed * extraMotionSlowY, this.getMotion().z * this.randomMotionSpeed * extraMotionSlow);
                timeFlying++;
                if (this.isOnGround() && timeFlying > 100) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
            }
            if (this.isMissingEye() && this.getAttackTarget() != null) {
                if (!(this.getAttackTarget() instanceof EndermanEntity)) {
                    this.setAttackTarget(null);
                }
            }
        }
        if (!this.onGround && this.getMotion().y < 0.0D) {
            this.setMotion(this.getMotion().mul(1.0D, 0.6D, 1.0D));
        }
        if (this.isFlying()) {
            float phageDist = -(float) ((Math.abs(this.getMotion().getX()) + Math.abs(this.getMotion().getZ())) * 6F);
            this.incrementPhagePitch(phageDist * 1);
            this.setPhagePitch(MathHelper.clamp(this.getPhagePitch(), -90, 10));
            float plateau = 2;
            if (this.getPhagePitch() > plateau) {
                this.decrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90);
            }
            if (this.getPhagePitch() < -plateau) {
                this.incrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90);
            }
            if (this.getPhagePitch() > 2F) {
                this.decrementPhagePitch(1);
            } else if (this.getPhagePitch() < -2) {
                this.incrementPhagePitch(1);
            }
            if (this.collidedHorizontally) {
                this.setMotion(this.getMotion().add(0, 0.2F, 0));
            }
        } else {
            if (this.getPhagePitch() > 0F) {
                float decrease = Math.min(2, this.getPhagePitch());
                this.decrementPhagePitch(decrease);
            }
            if (this.getPhagePitch() < 0F) {
                float decrease = Math.min(2, -this.getPhagePitch());
                this.incrementPhagePitch(decrease);
            }
        }
        if (this.getPhageScale() < 1F) {
            this.setPhageScale(this.getPhageScale() + 0.05F);
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("MissingEye", this.isMissingEye());
        compound.putInt("SlowDownTicks", slowDownTicks);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setMissingEye(compound.getBoolean("MissingEye"));
        this.slowDownTicks = compound.getInt("SlowDownTicks");
    }

    public boolean isMissingEye() {
        return this.dataManager.get(MISSING_EYE);
    }

    public void setMissingEye(boolean missingEye) {
        this.dataManager.set(MISSING_EYE, missingEye);
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, flying);
    }

    public float getPhagePitch() {
        return dataManager.get(PHAGE_PITCH).floatValue();
    }

    public void setPhagePitch(float pitch) {
        dataManager.set(PHAGE_PITCH, pitch);
    }

    public void incrementPhagePitch(float pitch) {
        dataManager.set(PHAGE_PITCH, getPhagePitch() + pitch);
    }

    public void decrementPhagePitch(float pitch) {
        dataManager.set(PHAGE_PITCH, getPhagePitch() - pitch);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 1.8F;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return null;
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.getPosition();
        while (position.getY() > 1 && world.isAirBlock(position)) {
            position = position.down();
        }
        return !world.getFluidState(position).isEmpty() || position.getY() < 1;
    }

    public Vector3d getBlockInViewAway(Vector3d fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24) - radiusAdd;
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = getPhageGround(radialPos);
        int distFromGround = (int) this.getPosY() - ground.getY();
        int flightHeight = 6 + this.getRNG().nextInt(10);
        BlockPos newPos = ground.up(distFromGround > 8 || fleeAfterStealTime > 0 ? flightHeight : this.getRNG().nextInt(6) + 5);
        if (!this.isTargetBlocked(Vector3d.copyCentered(newPos)) && this.getDistanceSq(Vector3d.copyCentered(newPos)) > 1) {
            return Vector3d.copyCentered(newPos);
        }
        return null;
    }

    private BlockPos getPhageGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getPosY(), in.getZ());
        while (position.getY() > 1 && world.isAirBlock(position)) {
            position = position.down();
        }
        if (position.getY() < 2) {
            return position.up(60 + rand.nextInt(5));
        }

        return position;
    }

    public Vector3d getBlockGrounding(Vector3d fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24);
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, getPosY(), fleePos.getZ() + extraZ);
        BlockPos ground = this.getPhageGround(radialPos);
        if (ground.getY() == 0) {
            return Vector3d.copyCenteredWithVerticalOffset(ground, 50 + rand.nextInt(20));
        } else {
            ground = this.getPosition();
            while (ground.getY() > 1 && world.isAirBlock(ground)) {
                ground = ground.down();
            }
        }
        if (!this.isTargetBlocked(Vector3d.copyCentered(ground.up()))) {
            return Vector3d.copyCentered(ground);
        }
        return null;
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            if (entity instanceof EndermanEntity) {
                amount = (amount + 1.0F) * 0.35F;
                angryEnderman = (EndermanEntity) entity;
            }
            return super.attackEntityFrom(source, amount);
        }
    }


    private class AIWalkIdle extends Goal {
        protected final EntityEnderiophage phage;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWalkIdle() {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
            this.phage = EntityEnderiophage.this;
        }

        @Override
        public boolean shouldExecute() {
            if (this.phage.isBeingRidden() || (phage.getAttackTarget() != null && phage.getAttackTarget().isAlive()) || this.phage.isPassenger()) {
                return false;
            } else {
                if (this.phage.getRNG().nextInt(30) != 0 && !phage.isFlying() && phage.fleeAfterStealTime == 0) {
                    return false;
                }
                if (this.phage.isOnGround()) {
                    this.flightTarget = rand.nextInt(12) == 0;
                } else {
                    this.flightTarget = rand.nextInt(5) > 0 && phage.timeFlying < 100;
                }
                if (phage.fleeAfterStealTime > 0) {
                    this.flightTarget = true;
                }
                Vector3d lvt_1_1_ = this.getPosition();
                if (lvt_1_1_ == null) {
                    return false;
                } else {
                    this.x = lvt_1_1_.x;
                    this.y = lvt_1_1_.y;
                    this.z = lvt_1_1_.z;
                    return true;
                }
            }
        }

        public void tick() {
            if (flightTarget) {
                phage.getMoveHelper().setMoveTo(x, y, z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            } else {
                this.phage.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            }
            if (!flightTarget && isFlying() && phage.onGround) {
                phage.setFlying(false);
            }
            if (isFlying() && phage.onGround && phage.timeFlying > 100 && phage.fleeAfterStealTime == 0) {
                phage.setFlying(false);
            }
        }

        @Nullable
        protected Vector3d getPosition() {
            Vector3d vector3d = phage.getPositionVec();
            if (phage.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (phage.timeFlying < 50 || fleeAfterStealTime > 0 || phage.isOverWaterOrVoid()) {
                    return phage.getBlockInViewAway(vector3d, 0);
                } else {
                    return phage.getBlockGrounding(vector3d);
                }
            } else {
                return RandomPositionGenerator.findRandomTarget(this.phage, 10, 7);
            }
        }

        public boolean shouldContinueExecuting() {
            if (flightTarget) {
                return phage.isFlying() && phage.getDistanceSq(x, y, z) > 2F;
            } else {
                return (!this.phage.getNavigator().noPath()) && !this.phage.isBeingRidden();
            }
        }

        public void startExecuting() {
            if (flightTarget) {
                phage.setFlying(true);
                phage.getMoveHelper().setMoveTo(x, y, z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            } else {
                this.phage.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
        }

        public void resetTask() {
            this.phage.getNavigator().clearPath();
            super.resetTask();
        }
    }

    public class FlyTowardsTarget extends Goal {
        private final EntityEnderiophage parentEntity;

        public FlyTowardsTarget(EntityEnderiophage phage) {
            this.parentEntity = phage;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {

            return !parentEntity.isPassenger() && parentEntity.getAttackTarget() != null && !isBittenByPhage(parentEntity.getAttackTarget()) && parentEntity.fleeAfterStealTime == 0;
        }

        public boolean shouldContinueExecuting() {
            return parentEntity.getAttackTarget() != null && !isBittenByPhage(parentEntity.getAttackTarget()) && !parentEntity.collidedHorizontally && !parentEntity.isPassenger() && parentEntity.isFlying() && parentEntity.getMoveHelper().isUpdating() && parentEntity.fleeAfterStealTime == 0;
        }

        public boolean isBittenByPhage(Entity entity) {
            int phageCount = 0;
            for (Entity e : entity.getPassengers()) {
                if (e instanceof EntityEnderiophage) {
                    phageCount++;
                }
            }
            return phageCount > 3;
        }

        public void resetTask() {
        }

        public void tick() {
            if (parentEntity.getAttackTarget() != null) {
                if (parentEntity.isFlying()) {
                    this.parentEntity.getMoveHelper().setMoveTo(parentEntity.getAttackTarget().getPosX(), parentEntity.getAttackTarget().getPosY(), parentEntity.getAttackTarget().getPosZ(), 1.0D);
                } else {
                    this.parentEntity.getNavigator().tryMoveToXYZ(parentEntity.getAttackTarget().getPosX(), parentEntity.getAttackTarget().getPosY(), parentEntity.getAttackTarget().getPosZ(), 1.2D);
                }
                if (parentEntity.getAttackTarget().getPosY() > this.parentEntity.getPosY() + 1.2F) {
                    parentEntity.setFlying(true);
                }
                if (parentEntity.dismountCooldown == 0 && parentEntity.getBoundingBox().grow(1F, 1F, 1F).intersects(parentEntity.getAttackTarget().getBoundingBox()) && !isBittenByPhage(parentEntity.getAttackTarget())) {
                    parentEntity.startRiding(parentEntity.getAttackTarget(), true);
                    if (!parentEntity.world.isRemote) {
                        AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(parentEntity.getEntityId(), parentEntity.getAttackTarget().getEntityId()));
                    }
                }
            }
        }
    }

}
