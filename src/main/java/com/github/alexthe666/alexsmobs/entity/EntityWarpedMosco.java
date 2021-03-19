package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class EntityWarpedMosco extends MonsterEntity implements IAnimatedEntity {

    public static final Animation ANIMATION_PUNCH_R = Animation.create(25);
    public static final Animation ANIMATION_PUNCH_L = Animation.create(25);
    public static final Animation ANIMATION_SLAM = Animation.create(35);
    public static final Animation ANIMATION_SUCK = Animation.create(60);
    public static final Animation ANIMATION_SPIT = Animation.create(60);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityWarpedMosco.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAND_SIDE = EntityDataManager.createKey(EntityWarpedMosco.class, DataSerializers.BOOLEAN);
    public float flyLeftProgress;
    public float prevLeftFlyProgress;
    public float flyRightProgress;
    public float prevFlyRightProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isLandNavigator;
    private int timeFlying;
    private int loopSoundTick = 0;

    protected EntityWarpedMosco(EntityType entityType, World world) {
        super(entityType, world);
        this.experienceValue = 30;
        switchNavigator(false);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 100D).createMutableAttribute(Attributes.FOLLOW_RANGE, 128.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 10.0D).createMutableAttribute(Attributes.ARMOR, 10D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1D).createMutableAttribute(Attributes.ARMOR_TOUGHNESS, 2D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    private static Animation getRandomAttack(Random rand) {
        switch (rand.nextInt(4)) {
            case 0:
                return ANIMATION_PUNCH_L;
            case 1:
                return ANIMATION_PUNCH_R;
            case 2:
                return ANIMATION_SLAM;
            case 3:
                return ANIMATION_SUCK;
        }
        return ANIMATION_SUCK;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.WARPED_MOSCO_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.WARPED_MOSCO_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.WARPED_MOSCO_HURT;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new AttackGoal());
        this.goalSelector.addGoal(4, new AIWalkIdle());
        this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 32F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntityCrimsonMosquito.class, EntityWarpedMosco.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS))));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new FlightMoveController(this, 0.7F, false);
            this.navigator = new DirectPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, false);
        this.dataManager.register(HAND_SIDE, true);
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        setDashRight(flying != this.isFlying() ? rand.nextBoolean() : this.isDashRight());
        this.dataManager.set(FLYING, flying);
    }

    public boolean isDashRight() {
        return this.dataManager.get(HAND_SIDE);
    }

    public void setDashRight(boolean right) {
        this.dataManager.set(HAND_SIDE, right);
    }

    public void tick() {
        super.tick();
        prevFlyRightProgress = flyRightProgress;
        prevLeftFlyProgress = flyLeftProgress;
        if (this.isFlying() && isDashRight() && flyRightProgress < 5F) {
            flyRightProgress++;
        }
        if ((!this.isFlying() || !isDashRight()) && flyRightProgress > 0F) {
            flyRightProgress--;
        }
        if (this.isFlying() && !isDashRight() && flyLeftProgress < 5F) {
            flyLeftProgress++;
        }
        if ((!this.isFlying() || isDashRight()) && flyLeftProgress > 0F) {
            flyLeftProgress--;
        }
        if (!world.isRemote) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
        }
        if (isFlying()) {
            if (loopSoundTick == 0) {
                this.playSound(AMSoundRegistry.MOSQUITO_LOOP, this.getSoundVolume(), this.getSoundPitch() * 0.3F);
            }
            loopSoundTick++;
            if (loopSoundTick > 100) {
                loopSoundTick = 0;
            }
        }
        if (isFlying()) {
            timeFlying++;
            this.setNoGravity(true);
            if (this.isPassenger() || this.isBeingRidden()) {
                this.setFlying(false);
            }
        } else {
            timeFlying = 0;
            this.setNoGravity(false);
        }
        ITag<Block> transformMatches = BlockTags.getCollection().get(AMTagRegistry.WARPED_MOSCO_BREAKABLES);
        if (this.collidedHorizontally && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
            boolean flag = false;
            AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(0.2D);
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ))) {
                BlockState blockstate = this.world.getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (transformMatches.contains(block)) {
                    flag = this.world.destroyBlock(blockpos, true, this) || flag;
                }
            }
            if (!flag && this.onGround) {
                this.jump();
            }
        }

        LivingEntity target = this.getAttackTarget();
        if (target != null && this.isAlive()) {
            if (this.getAnimation() == ANIMATION_SUCK && this.getAnimationTick() == 3 && this.getDistance(target) < 4.7F) {
                target.startRiding(this, true);
            }
            if (this.getAnimation() == ANIMATION_SLAM) {
                if (this.getAnimationTick() == 19) {
                    for (Entity entity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(5.0D), null)) {
                        if (!isOnSameTeam(entity) && !(entity instanceof EntityWarpedMosco) && entity != this) {
                            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F + rand.nextFloat() * 8.0F);
                            launch(entity, true);
                        }
                    }

                }
            }
            if ((this.getAnimation() == ANIMATION_PUNCH_R || this.getAnimation() == ANIMATION_PUNCH_L) && this.getAnimationTick() == 13) {
                if (this.getDistance(target) < 4.7F) {
                    target.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackRidiculous(target, 0.9F);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() == 19) {
            spawnGroundEffects();
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void spawnGroundEffects() {
        float radius = 2.3F;
        for (int i = 0; i < 4; i++) {
            for (int i1 = 0; i1 < 20 + rand.nextInt(12); i1++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.renderYawOffset) + i1;
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * MathHelper.cos(angle);
                BlockPos ground = getMoscoGround(new BlockPos(MathHelper.floor(this.getPosX() + extraX), MathHelper.floor(this.getPosY() + extraY) - 1, MathHelper.floor(this.getPosZ() + extraZ)));
                BlockState BlockState = this.world.getBlockState(ground);
                if (BlockState.getMaterial() != Material.AIR) {
                    if (world.isRemote) {
                        world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState), true, this.getPosX() + extraX, ground.getY() + extraY, this.getPosZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.isOnGround()) {
            double d0 = e.getPosX() - this.getPosX();
            double d1 = e.getPosZ() - this.getPosZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = huge ? 2F : 0.5F;
            e.addVelocity(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUNCH_L, ANIMATION_PUNCH_R, ANIMATION_SLAM, ANIMATION_SUCK, ANIMATION_SPIT};
    }

    private BlockPos getMoscoGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getPosY(), in.getZ());
        while (position.getY() > 2 && world.isAirBlock(position) && world.getFluidState(position).isEmpty()) {
            position = position.down();
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
        BlockPos ground = this.getMoscoGround(radialPos);
        if (ground.getY() == 0) {
            return this.getPositionVec();
        } else {
            ground = this.getPosition();
            while (ground.getY() > 2 && world.isAirBlock(ground)) {
                ground = ground.down();
            }
        }
        if (!this.isTargetBlocked(Vector3d.copyCentered(ground.up()))) {
            return Vector3d.copyCentered(ground);
        }
        return null;
    }

    public Vector3d getBlockInViewAway(Vector3d fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24) - radiusAdd;
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = getMoscoGround(radialPos);
        int distFromGround = (int) this.getPosY() - ground.getY();
        int flightHeight = 4 + this.getRNG().nextInt(10);
        BlockPos newPos = ground.up(distFromGround > 8 ? flightHeight : this.getRNG().nextInt(6) + 1);
        if (!this.isTargetBlocked(Vector3d.copyCentered(newPos)) && this.getDistanceSq(Vector3d.copyCentered(newPos)) > 1) {
            return Vector3d.copyCentered(newPos);
        }
        return null;
    }

    public void knockbackRidiculous(LivingEntity target, float power) {
        target.applyKnockback(power, this.getPosX() - target.getPosX(), this.getPosZ() - target.getPosZ());
        float knockbackResist = (float) MathHelper.clamp((1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
        target.setMotion(target.getMotion().add(0, knockbackResist * power * 0.45F, 0));
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());

        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    private boolean isOverLiquid() {
        BlockPos position = this.getPosition();
        while (position.getY() > 2 && world.isAirBlock(position)) {
            position = position.down();
        }
        return !world.getFluidState(position).isEmpty();
    }

    public void travel(Vector3d travelVector) {
        if ((this.getAnimation() == ANIMATION_SUCK || this.getAnimation() == ANIMATION_SLAM) && this.getAnimationTick() > 8) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            travelVector = Vector3d.ZERO;
            super.travel(travelVector);
            return;
        }
        super.travel(travelVector);
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (isPassenger(passenger)) {
            int tick = 5;
            if (this.getAnimation() == ANIMATION_SUCK) {
                tick = this.getAnimationTick();
            } else {
                passenger.stopRiding();
            }
            float radius = 2F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            double extraY = tick < 10 ? 0 : 0.15F * MathHelper.clamp(tick - 10, 0, 15);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + extraY + 0.1F, this.getPosZ() + extraZ);
            if ((tick - 10) % 4 == 0) {
                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 100, 1));
                passenger.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
        }
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.warpedMoscoSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    private void spit(LivingEntity target) {
        if (this.getAnimation() != ANIMATION_SPIT) {
            return;
        }
        this.faceEntity(target, 100, 100);
        this.renderYawOffset = rotationYawHead;
        for (int i = 0; i < 2 + rand.nextInt(2); i++) {
            EntityHemolymph llamaspitentity = new EntityHemolymph(this.world, this);
            double d0 = target.getPosX() - this.getPosX();
            double d1 = target.getPosYHeight(0.3333333333333333D) - llamaspitentity.getPosY();
            double d2 = target.getPosZ() - this.getPosZ();
            float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            llamaspitentity.shoot(d0, d1 + (double) f, d2, 1.5F, 5.0F);
            if (!this.isSilent()) {
                this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            }
            this.world.addEntity(llamaspitentity);
        }
    }

    private class AIWalkIdle extends Goal {
        protected final EntityWarpedMosco mosco;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWalkIdle() {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
            this.mosco = EntityWarpedMosco.this;
        }

        @Override
        public boolean shouldExecute() {
            if (this.mosco.isBeingRidden() || (mosco.getAttackTarget() != null && mosco.getAttackTarget().isAlive()) || this.mosco.isPassenger()) {
                return false;
            } else {
                if (this.mosco.getRNG().nextInt(30) != 0 && !mosco.isFlying()) {
                    return false;
                }
                if (this.mosco.isOnGround()) {
                    this.flightTarget = rand.nextInt(8) == 0;
                } else {
                    this.flightTarget = rand.nextInt(5) > 0 && mosco.timeFlying < 200;
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
                mosco.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                this.mosco.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && mosco.onGround) {
                mosco.setFlying(false);
            }
            if (isFlying() && mosco.onGround && mosco.timeFlying > 10) {
                mosco.setFlying(false);
            }
        }

        @Nullable
        protected Vector3d getPosition() {
            Vector3d vector3d = mosco.getPositionVec();

            if (mosco.isOverLiquid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (mosco.timeFlying < 50 || mosco.isOverLiquid()) {
                    return mosco.getBlockInViewAway(vector3d, 0);
                } else {
                    return mosco.getBlockGrounding(vector3d);
                }
            } else {

                return RandomPositionGenerator.findRandomTarget(this.mosco, 20, 7);
            }
        }

        public boolean shouldContinueExecuting() {
            if (flightTarget) {
                return mosco.isFlying() && mosco.getDistanceSq(x, y, z) > 20F && !mosco.collidedHorizontally;
            } else {
                return (!this.mosco.getNavigator().noPath()) && !this.mosco.isBeingRidden();
            }
        }

        public void startExecuting() {
            if (flightTarget) {
                mosco.setFlying(true);
                mosco.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                this.mosco.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
        }

        public void resetTask() {
            this.mosco.getNavigator().clearPath();
            super.resetTask();
        }
    }

    private class AttackGoal extends Goal {
        private int upTicks = 0;
        private int dashCooldown = 0;
        private boolean ranged = false;
        private BlockPos farTarget = null;

        public AttackGoal() {
        }

        public boolean shouldExecute() {
            return EntityWarpedMosco.this.getAttackTarget() != null;
        }

        public void tick() {
            if (dashCooldown > 0) {
                dashCooldown--;
            }
            if (EntityWarpedMosco.this.getAttackTarget() != null) {
                LivingEntity target = EntityWarpedMosco.this.getAttackTarget();
                ranged = EntityWarpedMosco.this.shouldRangeAttack(target);
                if (EntityWarpedMosco.this.isFlying() || ranged || EntityWarpedMosco.this.getDistance(target) > 12 && !EntityWarpedMosco.this.isTargetBlocked(target.getPositionVec().add(0, target.getHeight() * 0.6F, 0))) {
                    float speedRush = 5F;
                    upTicks++;
                    EntityWarpedMosco.this.setFlying(true);
                    if (ranged) {
                        if (farTarget == null || EntityWarpedMosco.this.getDistanceSq(Vector3d.copyCentered(farTarget)) < 9) {
                            farTarget = this.getAvoidTarget(target);
                        }
                        if (farTarget != null) {
                            EntityWarpedMosco.this.getMoveHelper().setMoveTo(farTarget.getX(), farTarget.getY() + target.getEyeHeight() * 0.6F, farTarget.getZ(), 3D);
                        }
                        EntityWarpedMosco.this.setAnimation(ANIMATION_SPIT);
                        if(upTicks % 30 == 0){
                            EntityWarpedMosco.this.heal(1);
                        }
                        int tick = EntityWarpedMosco.this.getAnimationTick();
                        if (tick == 10 || tick == 20 || tick == 30 || tick == 40) {
                            EntityWarpedMosco.this.spit(target);
                        }
                    } else {
                        if (upTicks > 20 || EntityWarpedMosco.this.getDistance(target) < 6) {
                            EntityWarpedMosco.this.getMoveHelper().setMoveTo(target.getPosX(), target.getPosY() + target.getEyeHeight() * 0.6F, target.getPosZ(), speedRush);
                        } else {
                            EntityWarpedMosco.this.getMoveHelper().setMoveTo(EntityWarpedMosco.this.getPosX(), EntityWarpedMosco.this.getPosY() + 3, EntityWarpedMosco.this.getPosZ(), 0.5F);
                        }
                    }
                } else {
                    EntityWarpedMosco.this.getNavigator().tryMoveToEntityLiving(EntityWarpedMosco.this.getAttackTarget(), 1.25F);
                }
                if (EntityWarpedMosco.this.isFlying()) {
                    if (EntityWarpedMosco.this.getDistance(target) < 4.3F) {
                        if (dashCooldown == 0 || target.isOnGround() || target.isInLava() || target.isInWater()) {
                            target.attackEntityFrom(DamageSource.causeMobDamage(EntityWarpedMosco.this), 5F);
                            EntityWarpedMosco.this.knockbackRidiculous(target, 1.0F);
                            dashCooldown = 30;
                        }
                        float groundHeight = EntityWarpedMosco.this.getMoscoGround(EntityWarpedMosco.this.getPosition()).getY();
                        if (Math.abs(EntityWarpedMosco.this.getPosY() - groundHeight) < 3.0F && !EntityWarpedMosco.this.isOverLiquid()) {
                            EntityWarpedMosco.this.timeFlying += 300;
                            EntityWarpedMosco.this.setFlying(false);
                        }
                    }
                } else {
                    if (EntityWarpedMosco.this.getDistance(target) < 4F && EntityWarpedMosco.this.getAnimation() == NO_ANIMATION) {
                        Animation animation = getRandomAttack(rand);
                        if (animation == ANIMATION_SUCK && target.isPassenger()) {
                            animation = ANIMATION_SLAM;
                        }
                        EntityWarpedMosco.this.setAnimation(animation);
                    }
                }
            }
        }

        public BlockPos getAvoidTarget(LivingEntity target) {
            float radius = 10 + EntityWarpedMosco.this.getRNG().nextInt(8);
            float neg = EntityWarpedMosco.this.getRNG().nextBoolean() ? 1 : -1;
            float angle = (0.01745329251F * (target.rotationYawHead + 90F + EntityWarpedMosco.this.getRNG().nextInt(180)));
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(target.getPosX() + extraX, target.getPosY() + 1, target.getPosZ() + extraZ);
            BlockPos ground = radialPos;
            if (EntityWarpedMosco.this.getDistanceSq(Vector3d.copyCentered(ground)) > 30) {
                if (!EntityWarpedMosco.this.isTargetBlocked(Vector3d.copyCentered(ground)) && EntityWarpedMosco.this.getDistanceSq(Vector3d.copyCentered(ground)) > 6) {
                    return ground;
                }
            }
            return EntityWarpedMosco.this.getPosition();
        }

        public void resetTask() {
            upTicks = 0;
            dashCooldown = 0;
            ranged = false;
        }
    }

    private boolean shouldRangeAttack(LivingEntity target) {
        if(this.getHealth() < Math.floor(this.getMaxHealth() * 0.25F)){
            return true;
        }
        return this.getHealth() < this.getHealth() * 0.5F && this.getDistance(target) > 10;
    }
}
