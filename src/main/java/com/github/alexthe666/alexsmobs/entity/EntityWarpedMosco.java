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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class EntityWarpedMosco extends Monster implements IAnimatedEntity {

    public static final Animation ANIMATION_PUNCH_R = Animation.create(25);
    public static final Animation ANIMATION_PUNCH_L = Animation.create(25);
    public static final Animation ANIMATION_SLAM = Animation.create(35);
    public static final Animation ANIMATION_SUCK = Animation.create(60);
    public static final Animation ANIMATION_SPIT = Animation.create(60);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityWarpedMosco.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAND_SIDE = SynchedEntityData.defineId(EntityWarpedMosco.class, EntityDataSerializers.BOOLEAN);
    public float flyLeftProgress;
    public float prevLeftFlyProgress;
    public float flyRightProgress;
    public float prevFlyRightProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isLandNavigator;
    private int timeFlying;
    private int loopSoundTick = 0;

    protected EntityWarpedMosco(EntityType entityType, Level world) {
        super(entityType, world);
        this.xpReward = 30;
        switchNavigator(false);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100D).add(Attributes.FOLLOW_RANGE, 128.0D).add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.ARMOR, 10D).add(Attributes.KNOCKBACK_RESISTANCE, 1D).add(Attributes.ARMOR_TOUGHNESS, 2D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    private static Animation getRandomAttack(RandomSource rand) {
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
        return AMSoundRegistry.WARPED_MOSCO_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.WARPED_MOSCO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.WARPED_MOSCO_HURT.get();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AttackGoal());
        this.goalSelector.addGoal(4, new AIWalkIdle());
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntityCrimsonMosquito.class, EntityWarpedMosco.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 0.7F, false);
            this.navigation = new DirectPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(HAND_SIDE, true);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        setDashRight(flying != this.isFlying() ? random.nextBoolean() : this.isDashRight());
        this.entityData.set(FLYING, flying);
    }

    public boolean isDashRight() {
        return this.entityData.get(HAND_SIDE);
    }

    public void setDashRight(boolean right) {
        this.entityData.set(HAND_SIDE, right);
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
        if (!level.isClientSide) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
        }
        if (isFlying()) {
            if (loopSoundTick == 0) {
                this.playSound(AMSoundRegistry.MOSQUITO_LOOP.get(), this.getSoundVolume(), this.getVoicePitch() * 0.3F);
            }
            loopSoundTick++;
            if (loopSoundTick > 100) {
                loopSoundTick = 0;
            }
        }
        if (isFlying()) {
            timeFlying++;
            this.setNoGravity(true);
            if (this.isPassenger() || this.isVehicle()) {
                this.setFlying(false);
            }
        } else {
            timeFlying = 0;
            this.setNoGravity(false);
        }
        if (this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
            boolean flag = false;
            AABB axisalignedbb = this.getBoundingBox().inflate(0.2D);
            for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(axisalignedbb.minX), Mth.floor(axisalignedbb.minY), Mth.floor(axisalignedbb.minZ), Mth.floor(axisalignedbb.maxX), Mth.floor(axisalignedbb.maxY), Mth.floor(axisalignedbb.maxZ))) {
                BlockState blockstate = this.level.getBlockState(blockpos);
                if (blockstate.is(AMTagRegistry.WARPED_MOSCO_BREAKABLES)) {
                    flag = this.level.destroyBlock(blockpos, true, this) || flag;
                }
            }
            if (!flag && this.onGround) {
                this.jumpFromGround();
            }
        }

        LivingEntity target = this.getTarget();
        if (target != null && this.isAlive()) {
            if (this.getAnimation() == ANIMATION_SUCK && this.getAnimationTick() == 3 && this.distanceTo(target) < 4.7F) {
                target.startRiding(this, true);
            }
            if (this.getAnimation() == ANIMATION_SLAM) {
                if (this.getAnimationTick() == 19) {
                    for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D))) {
                        if (!isAlliedTo(entity) && !(entity instanceof EntityWarpedMosco) && entity != this) {
                            entity.hurt(DamageSource.mobAttack(this), 10.0F + random.nextFloat() * 8.0F);
                            launch(entity, true);
                        }
                    }

                }
            }
            if ((this.getAnimation() == ANIMATION_PUNCH_R || this.getAnimation() == ANIMATION_PUNCH_L) && this.getAnimationTick() == 13) {
                if (this.distanceTo(target) < 4.7F) {
                    target.hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
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
            for (int i1 = 0; i1 < 20 + random.nextInt(12); i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.yBodyRot) + i1;
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = getMoscoGround(new BlockPos(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY) - 1, Mth.floor(this.getZ() + extraZ)));
                BlockState BlockState = this.level.getBlockState(ground);
                if (BlockState.getMaterial() != Material.AIR) {
                    if (level.isClientSide) {
                        level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockState), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.isOnGround()) {
            double d0 = e.getX() - this.getX();
            double d1 = e.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = huge ? 2F : 0.5F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
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
        BlockPos position = new BlockPos(in.getX(), this.getY(), in.getZ());
        while (position.getY() > -62 && !level.getBlockState(position).getMaterial().isSolidBlocking() && level.getFluidState(position).isEmpty()) {
            position = position.below();
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
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getMoscoGround(radialPos);
        if (ground.getY() == -62) {
            return this.position();
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -62 && !level.getBlockState(ground).getMaterial().isSolidBlocking()) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground);
        }
        return null;
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24) - radiusAdd;
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, 0, fleePos.z() + extraZ);
        BlockPos ground = getMoscoGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 4 + this.getRandom().nextInt(10);
        BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(6) + 1);
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    public void knockbackRidiculous(LivingEntity target, float power) {
        target.knockback(power, this.getX() - target.getX(), this.getZ() - target.getZ());
        float knockbackResist = (float) Mth.clamp((1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
        target.setDeltaMovement(target.getDeltaMovement().add(0, knockbackResist * power * 0.45F, 0));
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private boolean isOverLiquid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > 2 && level.isEmptyBlock(position)) {
            position = position.below();
        }
        return !level.getFluidState(position).isEmpty();
    }

    public void travel(Vec3 travelVector) {
        if ((this.getAnimation() == ANIMATION_SUCK || this.getAnimation() == ANIMATION_SLAM) && this.getAnimationTick() > 8) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        super.travel(travelVector);
    }

    public void positionRider(Entity passenger) {
        super.positionRider(passenger);
        if (hasPassenger(passenger)) {
            int tick = 5;
            if (this.getAnimation() == ANIMATION_SUCK) {
                tick = this.getAnimationTick();
            } else {
                passenger.stopRiding();
            }
            float radius = 2F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            double extraY = tick < 10 ? 0 : 0.15F * Mth.clamp(tick - 10, 0, 15);
            passenger.setPos(this.getX() + extraX, this.getY() + extraY + 0.1F, this.getZ() + extraZ);
            if ((tick - 10) % 4 == 0) {
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                passenger.hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
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

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.warpedMoscoSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    private void spit(LivingEntity target) {
        if (this.getAnimation() != ANIMATION_SPIT) {
            return;
        }
        this.lookAt(target, 100, 100);
        this.yBodyRot = yHeadRot;
        for (int i = 0; i < 2 + random.nextInt(2); i++) {
            EntityHemolymph llamaspitentity = new EntityHemolymph(this.level, this);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - llamaspitentity.getY();
            double d2 = target.getZ() - this.getZ();
            float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.2F;
            llamaspitentity.shoot(d0, d1 + (double) f, d2, 1.5F, 5.0F);
            if (!this.isSilent()) {
                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
            this.level.addFreshEntity(llamaspitentity);
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
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.mosco = EntityWarpedMosco.this;
        }

        @Override
        public boolean canUse() {
            if (this.mosco.isVehicle() || (mosco.getTarget() != null && mosco.getTarget().isAlive()) || this.mosco.isPassenger()) {
                return false;
            } else {
                if (this.mosco.getRandom().nextInt(30) != 0 && !mosco.isFlying()) {
                    return false;
                }
                if (this.mosco.isOnGround()) {
                    this.flightTarget = random.nextInt(8) == 0;
                } else {
                    this.flightTarget = random.nextInt(5) > 0 && mosco.timeFlying < 200;
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
                mosco.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.mosco.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && mosco.onGround) {
                mosco.setFlying(false);
            }
            if (isFlying() && mosco.onGround && mosco.timeFlying > 10) {
                mosco.setFlying(false);
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = mosco.position();

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

                return LandRandomPos.getPos(this.mosco, 20, 7);
            }
        }

        public boolean canContinueToUse() {
            if (flightTarget) {
                return mosco.isFlying() && mosco.distanceToSqr(x, y, z) > 20F && !mosco.horizontalCollision;
            } else {
                return (!this.mosco.getNavigation().isDone()) && !this.mosco.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                mosco.setFlying(true);
                mosco.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.mosco.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.mosco.getNavigation().stop();
            super.stop();
        }
    }

    private class AttackGoal extends Goal {
        private int upTicks = 0;
        private int dashCooldown = 0;
        private boolean ranged = false;
        private BlockPos farTarget = null;

        public AttackGoal() {
        }

        public boolean canUse() {
            return EntityWarpedMosco.this.getTarget() != null;
        }

        public void tick() {
            if (dashCooldown > 0) {
                dashCooldown--;
            }
            if (EntityWarpedMosco.this.getTarget() != null) {
                LivingEntity target = EntityWarpedMosco.this.getTarget();
                ranged = EntityWarpedMosco.this.shouldRangeAttack(target);
                if (EntityWarpedMosco.this.isFlying() || ranged || EntityWarpedMosco.this.distanceTo(target) > 12 && !EntityWarpedMosco.this.isTargetBlocked(target.position().add(0, target.getBbHeight() * 0.6F, 0))) {
                    float speedRush = 5F;
                    upTicks++;
                    EntityWarpedMosco.this.setFlying(true);
                    if (ranged) {
                        if (farTarget == null || EntityWarpedMosco.this.distanceToSqr(Vec3.atCenterOf(farTarget)) < 9) {
                            farTarget = this.getAvoidTarget(target);
                        }
                        if (farTarget != null) {
                            EntityWarpedMosco.this.getMoveControl().setWantedPosition(farTarget.getX(), farTarget.getY() + target.getEyeHeight() * 0.6F, farTarget.getZ(), 3D);
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
                        if (upTicks > 20 || EntityWarpedMosco.this.distanceTo(target) < 6) {
                            EntityWarpedMosco.this.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() * 0.6F, target.getZ(), speedRush);
                        } else {
                            EntityWarpedMosco.this.getMoveControl().setWantedPosition(EntityWarpedMosco.this.getX(), EntityWarpedMosco.this.getY() + 3, EntityWarpedMosco.this.getZ(), 0.5F);
                        }
                    }
                } else {
                    EntityWarpedMosco.this.getNavigation().moveTo(EntityWarpedMosco.this.getTarget(), 1.25F);
                }
                if (EntityWarpedMosco.this.isFlying()) {
                    if (EntityWarpedMosco.this.distanceTo(target) < 4.3F) {
                        if (dashCooldown == 0 || target.isOnGround() || target.isInLava() || target.isInWater()) {
                            target.hurt(DamageSource.mobAttack(EntityWarpedMosco.this), 5F);
                            EntityWarpedMosco.this.knockbackRidiculous(target, 1.0F);
                            dashCooldown = 30;
                        }
                        float groundHeight = EntityWarpedMosco.this.getMoscoGround(EntityWarpedMosco.this.blockPosition()).getY();
                        if (Math.abs(EntityWarpedMosco.this.getY() - groundHeight) < 3.0F && !EntityWarpedMosco.this.isOverLiquid()) {
                            EntityWarpedMosco.this.timeFlying += 300;
                            EntityWarpedMosco.this.setFlying(false);
                        }
                    }
                } else {
                    if (EntityWarpedMosco.this.distanceTo(target) < 4F && EntityWarpedMosco.this.getAnimation() == NO_ANIMATION) {
                        Animation animation = getRandomAttack(random);
                        if (animation == ANIMATION_SUCK && target.isPassenger()) {
                            animation = ANIMATION_SLAM;
                        }
                        EntityWarpedMosco.this.setAnimation(animation);
                    }
                }
            }
        }

        public BlockPos getAvoidTarget(LivingEntity target) {
            float radius = 10 + EntityWarpedMosco.this.getRandom().nextInt(8);
            float neg = EntityWarpedMosco.this.getRandom().nextBoolean() ? 1 : -1;
            float angle = (0.01745329251F * (target.yHeadRot + 90F + EntityWarpedMosco.this.getRandom().nextInt(180)));
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos(target.getX() + extraX, target.getY() + 1, target.getZ() + extraZ);
            BlockPos ground = radialPos;
            if (EntityWarpedMosco.this.distanceToSqr(Vec3.atCenterOf(ground)) > 30) {
                if (!EntityWarpedMosco.this.isTargetBlocked(Vec3.atCenterOf(ground)) && EntityWarpedMosco.this.distanceToSqr(Vec3.atCenterOf(ground)) > 6) {
                    return ground;
                }
            }
            return EntityWarpedMosco.this.blockPosition();
        }

        public void stop() {
            upTicks = 0;
            dashCooldown = 0;
            ranged = false;
        }
    }

    private boolean shouldRangeAttack(LivingEntity target) {
        if(this.getHealth() < Math.floor(this.getMaxHealth() * 0.25F)){
            return true;
        }
        return this.getHealth() < this.getHealth() * 0.5F && this.distanceTo(target) > 10;
    }
}
