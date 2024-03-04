package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;

public class EntitySkelewag extends Monster implements IAnimatedEntity {

    public static final Animation ANIMATION_STAB = Animation.create(10);
    public static final Animation ANIMATION_SLASH = Animation.create(25);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntitySkelewag.class, EntityDataSerializers.INT);
    private int animationTick;
    private Animation currentAnimation;
    public float prevOnLandProgress;
    public float onLandProgress;

    protected EntitySkelewag(EntityType<? extends Monster> monster, Level level) {
        super(monster, level);
        this.xpReward = 10;
        this.moveControl = new AquaticMoveController(this, 1.0F, 15F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new SemiAquaticPathNavigator(this, worldIn);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.skelewagSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canSkelewagSpawn(EntityType<EntitySkelewag> type, ServerLevelAccessor levelAccessor, MobSpawnType p_32352_, BlockPos below, RandomSource random) {
        if (!levelAccessor.getFluidState(below.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            return levelAccessor.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(levelAccessor, below, random) && (p_32352_ == MobSpawnType.SPAWNER || random.nextInt(40) == 0 && levelAccessor.getFluidState(below).is(FluidTags.WATER));
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SKELEWAG_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SKELEWAG_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SKELEWAG_HURT.get();
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level().getFluidState(pos).is(FluidTags.WATER) ? 10.0F + level.getLightLevelDependentMagicValue(pos) - 0.5F : super.getWalkTargetValue(pos, level());
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new AttackGoal(this));
        this.goalSelector.addGoal(3, new AnimalAIRandomSwimming(this, 1F, 12, 5));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Drowned.class, EntitySkelewag.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, Dolphin.class, true));

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, Integer.valueOf(0));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.45D).add(Attributes.MAX_HEALTH, 20.0D);
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public void tick(){
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        boolean onLand = !this.isInWaterOrBubble() && this.onGround();
        if (onLand && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (!onLand && onLandProgress > 0F) {
            onLandProgress--;
        }
        float targetXRot = 0;
        if(this.getDeltaMovement().length() > 0.09){
            targetXRot = -((float)(Mth.atan2(this.getDeltaMovement().y, this.getDeltaMovement().horizontalDistance()) * (double)Mth.RAD_TO_DEG));
        }
        if(targetXRot < this.getXRot() - 5){
            targetXRot = this.getXRot() - 5;
        }
        if(targetXRot > this.getXRot() + 5){
            targetXRot = this.getXRot() + 5;
        }
        this.setXRot(targetXRot);
        if (!this.level().isClientSide && this.getTarget() != null && this.distanceTo(this.getTarget()) < 2.0F + this.getTarget().getBbWidth()) {
            this.lookAt(this.getTarget(), 350, 200);
            if(this.getAnimation() == ANIMATION_STAB && this.getAnimationTick() == 7 && this.hasLineOfSight(this.getTarget())){
                float f1 = this.getYRot() * Mth.DEG_TO_RAD;
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
                getTarget().knockback(1F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
                this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
            if(this.getAnimation() == ANIMATION_SLASH && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() > 0 && this.getAnimationTick() < 25 && this.hasLineOfSight(this.getTarget())){
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getTarget().getBoundingBox().inflate(2.0D))) {
                    if (!entity.isPassengerOfSameVehicle(this) && entity != this && !entity.isAlliedTo(this)) {
                        entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 0.5F);
                    }
                }
            }
        }
        if(onLandProgress >= 5.0F && this.isVehicle()){
            this.ejectPassengers();
        }
        if(!isInWaterOrBubble()){
            if (this.onGround() && random.nextFloat() < 0.2F) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(AMSoundRegistry.SKELEWAG_HURT.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int command) {
        this.entityData.set(VARIANT, Integer.valueOf(command));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            passenger.setYBodyRot(this.yBodyRot);
            Vec3 vec = new Vec3(0, this.getBbHeight() * 0.4F, this.getBbWidth() * -0.2F).xRot(-this.getXRot() * Mth.DEG_TO_RAD).yRot(-this.getYRot() * Mth.DEG_TO_RAD);
            passenger.setPos(this.getX() + vec.x, this.getY() + vec.y + passenger.getMyRidingOffset(), this.getZ() + vec.z);
        }
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setVariant(this.getRandom().nextFloat() < 0.3F ? 1 : 0);
        if (this.random.nextFloat() < 0.2F) {
            Drowned drowned = EntityType.DROWNED.create(level());
            drowned.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
            drowned.copyPosition(this);
            drowned.startRiding(this);
            worldIn.addFreshEntityWithPassengers(drowned);
        }
        if(reason == MobSpawnType.STRUCTURE){
            this.restrictTo(this.blockPosition(), 15);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBreatheUnderwater() {
        return true;
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
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SLASH, ANIMATION_STAB};
    }

    private class AttackGoal extends Goal {
        private final EntitySkelewag fish;
        private boolean isCharging = false;

        public AttackGoal(EntitySkelewag skelewag) {
            this.fish = skelewag;
        }

        @Override
        public boolean canUse() {
            return this.fish.getTarget() != null;
        }

        public void tick(){
            LivingEntity target = this.fish.getTarget();
            if(target != null){
                double dist = this.fish.distanceTo(target);
                if(dist > 5){
                    isCharging = true;
                }
                this.fish.getNavigation().moveTo(target, isCharging ? 1.3F : 0.8F);
                if(dist < 2.0F + 3.0F + target.getBbWidth() / 2){
                    this.fish.setAnimation(isCharging ? ANIMATION_STAB : random.nextBoolean() ? ANIMATION_SLASH : ANIMATION_STAB);
                    isCharging = false;
                }
            }
        }

        public void stop(){
            isCharging = false;
        }
    }
}
