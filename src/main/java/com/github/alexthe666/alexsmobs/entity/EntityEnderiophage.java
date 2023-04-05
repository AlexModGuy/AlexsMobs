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
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class EntityEnderiophage extends Animal implements Enemy, FlyingAnimal {

    private static final EntityDataAccessor<Float> PHAGE_PITCH = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MISSING_EYE = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> PHAGE_SCALE = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityEnderiophage.class, EntityDataSerializers.INT);
    private static final Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = (entity) -> {
        return entity instanceof EntityEndergrade || entity.hasEffect(AMEffectRegistry.ENDER_FLU.get());
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
    private PathfinderMob angryEnderman = null;

    protected EntityEnderiophage(EntityType type, Level world) {
        super(type, world);
        this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
        switchNavigator(false);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.ATTACK_DAMAGE, 2F);
    }

    public static boolean canEnderiophageSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return true;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.enderiophageSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.getPhageGround(this.blockPosition());
        this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        setSkinForDimension();
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    public float getPhageScale() {
        return this.entityData.get(PHAGE_SCALE);
    }

    public void setPhageScale(float scale) {
        this.entityData.set(PHAGE_SCALE, scale);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FlyTowardsTarget(this));
        this.goalSelector.addGoal(2, new AIWalkIdle());
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, EnderMan.class, 15, true, true, null) {
            public boolean canUse() {
                return EntityEnderiophage.this.isMissingEye() && super.canUse();
            }

            public boolean canContinueToUse() {
                return EntityEnderiophage.this.isMissingEye() && super.canContinueToUse();
            }
        });
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
            public boolean canUse() {
                return !EntityEnderiophage.this.isMissingEye() && EntityEnderiophage.this.fleeAfterStealTime == 0 && super.canUse();
            }

            public boolean canContinueToUse() {
                return !EntityEnderiophage.this.isMissingEye() && super.canContinueToUse();
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this, EnderMan.class));

    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 1F, false, true);
            this.navigation = new DirectPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(PHAGE_PITCH, 0F);
        this.entityData.define(PHAGE_SCALE, 1F);
        this.entityData.define(FLYING, false);
        this.entityData.define(MISSING_EYE, false);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isInOverworld() {
        return this.level.dimension() == Level.OVERWORLD && !this.isNoAi();
    }

    public boolean isInNether() {
        return this.level.dimension() == Level.NETHER && !this.isNoAi();
    }

    public void setStandardFleeTime() {
        this.fleeAfterStealTime = 20;
    }

    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                attachTime++;
                Entity mount = this.getVehicle();
                if (mount instanceof LivingEntity) {
                    passengerIndex = mount.getPassengers().indexOf(this);
                    this.yBodyRot = ((LivingEntity) mount).yBodyRot;
                    this.setYRot( ((LivingEntity) mount).getYRot());
                    this.yHeadRot = ((LivingEntity) mount).yHeadRot;
                    this.yRotO = ((LivingEntity) mount).yHeadRot;
                    float radius = mount.getBbWidth();
                    float angle = (0.01745329251F * (((LivingEntity) mount).yBodyRot + passengerIndex * 90F));
                    double extraX = radius * Mth.sin((float) (Math.PI + angle));
                    double extraZ = radius * Mth.cos(angle);
                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getEyeHeight() * 0.25F, mount.getY()), mount.getZ() + extraZ);
                    if (!mount.isAlive() || mount instanceof Player && ((Player) mount).isCreative()) {
                        this.removeVehicle();
                    }
                    this.setPhagePitch(0F);
                    if (!level.isClientSide && attachTime > 15) {
                        LivingEntity target = (LivingEntity) mount;
                        float dmg = 1F;
                        if (target.getHealth() > target.getMaxHealth() * 0.2F) {
                            dmg = 6F;
                        }
                        if ((target.getHealth() < 1.5D || mount.hurt(this.damageSources().mobAttack(this), dmg)) && mount instanceof LivingEntity) {
                            dismountCooldown = 100;
                            if (mount instanceof EnderMan) {
                                this.setMissingEye(false);
                                this.gameEvent(GameEvent.EAT);
                                this.playSound(SoundEvents.ENDER_EYE_DEATH, this.getSoundVolume(), this.getVoicePitch());
                                this.heal(5);
                                ((EnderMan) mount).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
                                this.fleeAfterStealTime = 400;
                                this.setFlying(true);
                                this.angryEnderman = (PathfinderMob) mount;
                            } else {
                                if (random.nextInt(3) == 0) {
                                    if (!this.isMissingEye()) {
                                        if (target.getEffect(AMEffectRegistry.ENDER_FLU.get()) == null) {
                                            target.addEffect(new MobEffectInstance(AMEffectRegistry.ENDER_FLU.get(), 12000));
                                        } else {
                                            MobEffectInstance inst = target.getEffect(AMEffectRegistry.ENDER_FLU.get());
                                            int duration = 12000;
                                            int level = 0;
                                            if (inst != null) {
                                                duration = inst.getDuration();
                                                level = inst.getAmplifier();
                                            }
                                            target.removeEffect(AMEffectRegistry.ENDER_FLU.get());
                                            target.addEffect(new MobEffectInstance(AMEffectRegistry.ENDER_FLU.get(), duration, Math.min(level + 1, 4)));
                                        }
                                        this.heal(5);
                                        this.gameEvent(GameEvent.ENTITY_ROAR);
                                        this.playSound(SoundEvents.ITEM_BREAK, this.getSoundVolume(), this.getVoicePitch());
                                        this.setMissingEye(true);
                                    }
                                    if (!level.isClientSide) {
                                        this.setTarget(null);
                                        this.setLastHurtMob(null);
                                        this.setLastHurtByMob(null);
                                        this.goalSelector.getRunningGoals().forEach(Goal::stop);
                                        this.targetSelector.getRunningGoals().forEach(Goal::stop);
                                    }
                                }
                            }
                        }
                        if (((LivingEntity) mount).getHealth() <= 0 || this.fleeAfterStealTime > 0 || this.isMissingEye() && !(mount instanceof EnderMan) || !this.isMissingEye() && mount instanceof EnderMan) {
                            this.removeVehicle();
                            this.setTarget(null);
                            dismountCooldown = 100;
                            AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.getId(), mount.getId()));
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

    public void setSkinForDimension(){
        if(isInNether()){
            this.setVariant(2);
        }else if(isInOverworld()){
            this.setVariant(1);
        }else{
            this.setVariant(0);
        }
    }
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ENDERIOPHAGE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ENDERIOPHAGE_HURT.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(AMSoundRegistry.ENDERIOPHAGE_WALK.get(), 0.4F, 1.0F);
    }

    protected float nextStep() {
        return this.moveDist + 0.3F;
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
        if (dismountCooldown > 0) {
            dismountCooldown--;
        }
        if (squishCooldown > 0) {
            squishCooldown--;
        }
        if (!level.isClientSide) {
            if (!this.isPassenger() && attachTime != 0) {
                attachTime = 0;
            }
            if (fleeAfterStealTime > 0) {
                if (angryEnderman != null) {
                    Vec3 vec = this.getBlockInViewAway(angryEnderman.position(), 10);
                    if (fleeAfterStealTime < 5) {
                        if (angryEnderman instanceof NeutralMob) {
                            ((NeutralMob) angryEnderman).stopBeingAngry();
                        }
                        try {
                            angryEnderman.goalSelector.getRunningGoals().forEach(Goal::stop);
                            angryEnderman.targetSelector.getRunningGoals().forEach(Goal::stop);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        angryEnderman = null;
                    }
                    if (vec != null) {
                        this.setFlying(true);
                        this.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.3F);
                    }
                }
                fleeAfterStealTime--;
            }
        }
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        this.setPhagePitch(-90F);
        if (this.isAlive() && this.isFlying() && randomMotionSpeed > 0.75F && this.getDeltaMovement().lengthSqr() > 0.02D) {
            if (level.isClientSide) {
                float pitch = -this.getPhagePitch() / 90F;
                float radius = this.getBbWidth() * 0.2F * -pitch;
                float angle = (0.01745329251F * this.getYRot());
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.2F - (1 - pitch) * 0.15F;
                double extraZ = radius * Mth.cos(angle);
                double motX = extraX * 8 + random.nextGaussian() * 0.05F;
                double motY = -0.1F;
                double motZ = extraZ + random.nextGaussian() * 0.05F;
                this.level.addParticle(AMParticleRegistry.DNA.get(), this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ, motX, motY, motZ);
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
            if (this.level.isClientSide) {
                this.phageRotation = ((float) Math.PI * 2F);
            } else {
                this.phageRotation = (float) ((double) this.phageRotation - (Math.PI * 2D));
                if (this.random.nextInt(10) == 0) {
                    this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
                }
                this.level.broadcastEntityEvent(this, (byte) 19);
            }
        }
        if (this.phageRotation < (float) Math.PI) {
            float f = this.phageRotation / (float) Math.PI;
            this.tentacleAngle = Mth.sin(f * f * (float) Math.PI) * 4.275F;
            if ((double) f > 0.75D) {
                if (squishCooldown == 0 && this.isFlying()) {
                    squishCooldown = 20;
                    this.playSound(AMSoundRegistry.ENDERIOPHAGE_SQUISH.get(), 3F, this.getVoicePitch());
                }
                this.randomMotionSpeed = 1.0F;
            } else {
                randomMotionSpeed = 0.01F;
            }
        }
        if (!this.level.isClientSide) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.isFlying()) {
                this.setDeltaMovement(this.getDeltaMovement().x * this.randomMotionSpeed * extraMotionSlow, this.getDeltaMovement().y * this.randomMotionSpeed * extraMotionSlowY, this.getDeltaMovement().z * this.randomMotionSpeed * extraMotionSlow);
                timeFlying++;
                if (this.isOnGround() && timeFlying > 100) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
            }
            if (this.isMissingEye() && this.getTarget() != null) {
                if (!(this.getTarget() instanceof EnderMan)) {
                    this.setTarget(null);
                }
            }
        }
        if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
        if (this.isFlying()) {
            float phageDist = -(float) ((Math.abs(this.getDeltaMovement().x()) + Math.abs(this.getDeltaMovement().z())) * 6F);
            this.incrementPhagePitch(phageDist * 1);
            this.setPhagePitch(Mth.clamp(this.getPhagePitch(), -90, 10));
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
            if (this.horizontalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2F, 0));
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

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("MissingEye", this.isMissingEye());
        compound.putInt("Variant", this.getVariant());
        compound.putInt("SlowDownTicks", slowDownTicks);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setMissingEye(compound.getBoolean("MissingEye"));
        this.setVariant(compound.getInt("Variant"));
        this.slowDownTicks = compound.getInt("SlowDownTicks");
    }

    public boolean isMissingEye() {
        return this.entityData.get(MISSING_EYE);
    }

    public void setMissingEye(boolean missingEye) {
        this.entityData.set(MISSING_EYE, missingEye);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public float getPhagePitch() {
        return entityData.get(PHAGE_PITCH).floatValue();
    }

    public void setPhagePitch(float pitch) {
        entityData.set(PHAGE_PITCH, pitch);
    }

    public void incrementPhagePitch(float pitch) {
        entityData.set(PHAGE_PITCH, getPhagePitch() + pitch);
    }

    public void decrementPhagePitch(float pitch) {
        entityData.set(PHAGE_PITCH, getPhagePitch() - pitch);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.8F;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return null;
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -63 && !level.getBlockState(position).getMaterial().isSolidBlocking()) {
            position = position.below();
        }
        return !level.getFluidState(position).isEmpty() || position.getY() < -63;
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24) - radiusAdd;
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), 0, (int) (fleePos.z() + extraZ));
        BlockPos ground = getPhageGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 6 + this.getRandom().nextInt(10);
        BlockPos newPos = ground.above(distFromGround > 8 || fleeAfterStealTime > 0 ? flightHeight : this.getRandom().nextInt(6) + 5);
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    private BlockPos getPhageGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() > -63 && !level.getBlockState(position).getMaterial().isSolidBlocking()) {
            position = position.below();
        }
        if (position.getY() < -62) {
            return position.above(120 + random.nextInt(5));
        }

        return position;
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getPhageGround(radialPos);
        if (ground.getY() <= -63) {
            return Vec3.upFromBottomCenterOf(ground, 110 + random.nextInt(20));
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -63 && !level.getBlockState(ground).getMaterial().isSolidBlocking()) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground);
        }
        return null;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            if (entity instanceof EnderMan) {
                amount = (amount + 1.0F) * 0.35F;
                angryEnderman = (EnderMan) entity;
            }
            return super.hurt(source, amount);
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
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.phage = EntityEnderiophage.this;
        }

        @Override
        public boolean canUse() {
            if (this.phage.isVehicle() || (phage.getTarget() != null && phage.getTarget().isAlive()) || this.phage.isPassenger()) {
                return false;
            } else {
                if (this.phage.getRandom().nextInt(30) != 0 && !phage.isFlying() && phage.fleeAfterStealTime == 0) {
                    return false;
                }
                if (this.phage.isOnGround()) {
                    this.flightTarget = random.nextInt(12) == 0;
                } else {
                    this.flightTarget = random.nextInt(5) > 0 && phage.timeFlying < 100;
                }
                if (phage.fleeAfterStealTime > 0) {
                    this.flightTarget = true;
                }
                Vec3 lvt_1_1_ = this.getPosition();
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
                phage.getMoveControl().setWantedPosition(x, y, z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            } else {
                this.phage.getNavigation().moveTo(this.x, this.y, this.z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            }
            if (!flightTarget && isFlying() && phage.onGround) {
                phage.setFlying(false);
            }
            if (isFlying() && phage.onGround && phage.timeFlying > 100 && phage.fleeAfterStealTime == 0) {
                phage.setFlying(false);
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = phage.position();
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
                return LandRandomPos.getPos(this.phage, 10, 7);
            }
        }

        public boolean canContinueToUse() {
            if (flightTarget) {
                return phage.isFlying() && phage.distanceToSqr(x, y, z) > 2F;
            } else {
                return (!this.phage.getNavigation().isDone()) && !this.phage.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                phage.setFlying(true);
                phage.getMoveControl().setWantedPosition(x, y, z, fleeAfterStealTime == 0 ? 1.3F : 1F);
            } else {
                this.phage.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.phage.getNavigation().stop();
            super.stop();
        }
    }

    public class FlyTowardsTarget extends Goal {
        private final EntityEnderiophage parentEntity;

        public FlyTowardsTarget(EntityEnderiophage phage) {
            this.parentEntity = phage;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {

            return !parentEntity.isPassenger() && parentEntity.getTarget() != null && !isBittenByPhage(parentEntity.getTarget()) && parentEntity.fleeAfterStealTime == 0;
        }

        public boolean canContinueToUse() {
            return parentEntity.getTarget() != null && !isBittenByPhage(parentEntity.getTarget()) && !parentEntity.horizontalCollision && !parentEntity.isPassenger() && parentEntity.isFlying() && parentEntity.getMoveControl().hasWanted() && parentEntity.fleeAfterStealTime == 0 && (parentEntity.getTarget() instanceof EnderMan || !parentEntity.isMissingEye());
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

        public void stop() {
        }

        public void tick() {
            if (parentEntity.getTarget() != null) {
                float width =  parentEntity.getTarget().getBbWidth() + parentEntity.getBbWidth() + 2;
                boolean isWithinReach = parentEntity.distanceToSqr(parentEntity.getTarget()) < width * width;
                if (parentEntity.isFlying() || isWithinReach) {
                    this.parentEntity.getMoveControl().setWantedPosition(parentEntity.getTarget().getX(), parentEntity.getTarget().getY(), parentEntity.getTarget().getZ(), isWithinReach ? 1.6D : 1.0D);
                } else {
                    this.parentEntity.getNavigation().moveTo(parentEntity.getTarget().getX(), parentEntity.getTarget().getY(), parentEntity.getTarget().getZ(), 1.2D);
                }
                if (parentEntity.getTarget().getY() > this.parentEntity.getY() + 1.2F) {
                    parentEntity.setFlying(true);
                }
                if (parentEntity.dismountCooldown == 0 && parentEntity.getBoundingBox().inflate(0.3, 0.3, 0.3).intersects(parentEntity.getTarget().getBoundingBox()) && !isBittenByPhage(parentEntity.getTarget())) {
                    parentEntity.startRiding(parentEntity.getTarget(), true);
                    if (!parentEntity.level.isClientSide) {
                        AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(parentEntity.getId(), parentEntity.getTarget().getId()));
                    }
                }
            }
        }
    }

}
