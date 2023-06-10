package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SwimmerJumpPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class EntityFlyingFish extends WaterAnimal implements FlyingAnimal, Bucketable {

    private static final EntityDataAccessor<Boolean> GLIDING = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.BOOLEAN);
    public float prevOnLandProgress;
    public float onLandProgress;
    public float prevFlyProgress;
    public float flyProgress;
    private int glideIn = random.nextInt(75) + 50;

    protected EntityFlyingFish(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new AquaticMoveController(this, 1.0F, 15F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new GlideGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(4, new AnimalAIRandomSwimming(this, 1F, 12, 5));
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COD_HURT;
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double p_27492_) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new SwimmerJumpPathNavigator(this, worldIn);
    }

    public int getMaxSpawnClusterSize() {
        return 8;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(GLIDING, false);
        this.entityData.define(VARIANT, 0);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.flyingFishSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if(prev && source.getEntity() != null){
            double range = 15;
            this.glideIn = 0;
            List<? extends EntityFlyingFish> list = this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range/2, range));
            for(EntityFlyingFish fsh : list){
                fsh.glideIn = 0;
            }
        }
        return prev;
    }

    public void tick(){
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        this.prevFlyProgress = flyProgress;
        boolean onLand = !this.isInWaterOrBubble() && this.onGround();
        if (onLand && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (!onLand && onLandProgress > 0F) {
            onLandProgress--;
        }
        if (isGliding() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isGliding() && flyProgress > 0F) {
            flyProgress--;
        }
        if(isGliding() && !this.isInWaterOrBubble() && this.getDeltaMovement().y < 0.0){
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0F, 0.5F, 1.0F));
        }
        if(glideIn > 0){
            glideIn--;
        }
        this.yBodyRot = this.getYRot();
        float f2 = (float) -((float) this.getDeltaMovement().y * 3F * (double) (180F / (float) Math.PI));
        if(this.isGliding()){
            f2 = -f2;
        }
        this.setXRot(rotlerp(this.getXRot(), f2, 9));
        if(!isInWaterOrBubble() && this.isAlive()){
            if (this.onGround() && random.nextFloat() < 0.05F) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(SoundEvents.COD_FLOP, this.getSoundVolume(), this.getVoicePitch());
            }
        }
    }

    protected float rotlerp(float current, float target, float maxChange) {
        float f = Mth.wrapDegrees(target - current);
        if (f > maxChange) {
            f = maxChange;
        }
        if (f < -maxChange) {
            f = -maxChange;
        }
        float f1 = current + f;
        return f1;
    }

    protected void handleAirSupply(int i) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(i - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(1000);
        }
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f = 0.6F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, f, 0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public boolean isGliding() {
        return this.entityData.get(GLIDING);
    }

    public void setGliding(boolean flying) {
        this.entityData.set(GLIDING, flying);
    }

    private boolean canSeeBlock(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean p_203706_1_) {
        this.entityData.set(FROM_BUCKET, p_203706_1_);
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setVariant(compound.getInt("Variant"));
    }

    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.FLYING_FISH_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.putInt("Variant", this.getVariant());
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
        if (compound.contains("Variant")){
            this.setVariant(compound.getInt("Variant"));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance diff, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        int i;
        if (data instanceof FlyingFishGroupData) {
            i = ((FlyingFishGroupData)data).variant;
        } else {
            i = this.random.nextInt(3);
            data = new FlyingFishGroupData(i);
        }

        this.setVariant(i);
        return super.finalizeSpawn(world, diff, spawnType, data, tag);
    }

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    public class FlyingFishGroupData extends AgeableMob.AgeableMobGroupData {

        public final int variant;

        FlyingFishGroupData(int variant) {
            super(true);
            this.variant = variant;
        }

    }

    private class GlideGoal extends Goal {
        private EntityFlyingFish fish;
        private Level level;
        private BlockPos surface;
        private BlockPos glide;

        public GlideGoal(EntityFlyingFish fish) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.fish = fish;
            this.level = fish.level();
        }

        @Override
        public boolean canUse() {
            if(!fish.isInWaterOrBubble()){
                return false;
            }else if(fish.glideIn == 0 || fish.getRandom().nextInt(80) == 0){
                BlockPos found = findSurfacePos();
                if(found != null){
                    BlockPos glideTo = findGlideToPos(fish.blockPosition(), found);
                    if(glideTo != null){
                        surface = found;
                        glide = glideTo;
                        fish.glideIn = 0;
                        return true;
                    }
                }
            }
            return false;
        }

        private BlockPos findSurfacePos(){
            BlockPos fishPos = fish.blockPosition();
            for(int i = 0; i < 15; i++){
                BlockPos offset = fishPos.offset(fish.random.nextInt(16) - 8, 0, fish.random.nextInt(16) - 8);
                while(level.isWaterAt(offset) && offset.getY() < level.getMaxBuildHeight()){
                    offset = offset.above();
                }
                if(!level.isWaterAt(offset) && level.isWaterAt(offset.below()) && fish.canSeeBlock(offset)){
                    return offset;
                }
            }
            return null;
        }

        private BlockPos findGlideToPos(BlockPos fishPos, BlockPos surface) {
            Vec3 sub = Vec3.atLowerCornerOf(surface.subtract(fishPos)).normalize();
            double scale = random.nextDouble() * 8 + 1;

            while(scale > 2){
                Vec3 scaled = sub.scale(scale);
                BlockPos at = surface.offset((int) scaled.x, 0, (int) scaled.z);
                if(!level.isWaterAt(at) && level.isWaterAt(at.below()) && fish.canSeeBlock(at)){
                    return at;
                }
                scale -= 1;
            }
            return null;
        }

        @Override
        public boolean canContinueToUse() {
            return surface != null && glide != null && (!fish.onGround() || fish.isInWaterOrBubble());
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            surface = null;
            glide = null;
            fish.glideIn = random.nextInt(75) + 150;
            fish.setGliding(false);
        }

        @Override
        public void tick() {
            if(fish.isInWaterOrBubble() && fish.distanceToSqr(Vec3.atCenterOf(surface)) > 3F){
                fish.getNavigation().moveTo(surface.getX() + 0.5F, surface.getY() + 1F, surface.getZ() + 0.5F, 1.2F);
                if(fish.isGliding()){
                    stop();
                }
            }else{
                fish.getNavigation().stop();
                Vec3 face = Vec3.atCenterOf(glide).subtract(Vec3.atCenterOf(surface));
                if(face.length() < 0.2F){
                    face = fish.getLookAngle();
                }
                Vec3 target = face.normalize().scale(0.1F);
                double y = 0;
                if(!fish.isGliding()){
                    y = 0.4F + random.nextFloat() * 0.2F;
                }else if(fish.isGliding() && fish.isInWaterOrBubble()){
                    stop();
                }
                Vec3 move = fish.getDeltaMovement().add(target.x, y, (double) (target.y));
                fish.setDeltaMovement(move);
                double d0 = move.horizontalDistance();
                fish.setXRot((float)(-Mth.atan2(move.y, d0) * (double)(180F / (float)Math.PI)));
                fish.setYRot(((float) Mth.atan2(move.z, move.x)) * (180F / (float) Math.PI) - 90F);
                fish.setGliding(true);
            }
        }
    }
}
