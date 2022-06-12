package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class EntityCombJelly extends WaterAnimal implements Bucketable {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> JELLYPITCH = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> JELLY_SCALE = SynchedEntityData.defineId(EntityCombJelly.class, EntityDataSerializers.FLOAT);
    public float prevOnLandProgress;
    public float onLandProgress;
    private BlockPos moveTarget;
    public float prevjellyPitch = 0;
    public float spin;
    public float prevSpin;

    protected EntityCombJelly(EntityType<? extends WaterAnimal> animal, Level level) {
        super(animal, level);
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double p_213397_1_) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.terrapinSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canCombJellySpawn(EntityType<EntityCombJelly> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || iServerWorld.getBlockState(pos).getMaterial() == Material.WATER && iServerWorld.getBlockState(pos.above()).getMaterial() == Material.WATER && isLightLevelOk(pos, iServerWorld);
    }

    private static boolean isLightLevelOk(BlockPos pos, ServerLevelAccessor iServerWorld) {
        float time = iServerWorld.getTimeOfDay(1.0F);
        int light = iServerWorld.getMaxLocalRawBrightness(pos);
        return light <= 4 && time > 0.27F && time <= 0.8F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(JELLYPITCH, 0F);
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(JELLY_SCALE, 1.0F);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.COMB_JELLY_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.COMB_JELLY_HURT.get();
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public float getJellyPitch() {
        return Mth.clamp(this.entityData.get(JELLYPITCH).floatValue(), -90, 90);
    }

    public void setJellyPitch(float pitch) {
        this.entityData.set(JELLYPITCH, Mth.clamp(pitch, -90, 90));
    }

    public float getJellyScale() {
        return this.entityData.get(JELLY_SCALE);
    }

    public void setJellyScale(float scale) {
        this.entityData.set(JELLY_SCALE, scale);
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

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.COMB_JELLY_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    public void tick() {
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        this.prevjellyPitch = this.getJellyPitch();
        this.prevSpin = this.spin;
        if (!this.isInWater() && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (this.isInWater() && onLandProgress > 0F) {
            onLandProgress--;
        }
        if(!this.isInWater() && !level.isClientSide) {
            this.setNoGravity(false);
        }
        if(this.isInWater() && !level.isClientSide){
            this.setNoGravity(true);
            if(moveTarget == null || this.random.nextInt(120) == 0 || this.distanceToSqr(moveTarget.getX() + 0.5F, moveTarget.getY() + 0.5F, moveTarget.getZ() + 0.5F) < 5 || tickCount % 10 == 0 && !canBlockPosBeSeen(moveTarget)){
                BlockPos randPos = this.blockPosition().offset(random.nextInt(10) - 5, random.nextInt(6) - 3, random.nextInt(10) - 5);
                if(level.getFluidState(randPos).is(Fluids.WATER) && level.getFluidState(randPos.above()).is(Fluids.WATER)){
                    moveTarget = randPos;
                }
            }
            if(this.getFluidHeight(FluidTags.WATER) < this.getBbHeight()){
                moveTarget = null;
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.02, 0));
            }
            if(moveTarget != null){
                double d0 = moveTarget.getX() + 0.5F - this.getX();
                double d1 = moveTarget.getY() + 0.5F - this.getY();
                double d2 = moveTarget.getZ() + 0.5F - this.getZ();
                double d3 = (double) Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F;
                this.setYRot(rotlerp(this.getYRot(), f, 1));
                this.yBodyRot = this.getYRot();
                float movSpeed = 0.004F;
                Vec3 movingVec = new Vec3(d0/d3, d1/d3, d2/d3).normalize();
                this.setDeltaMovement(this.getDeltaMovement().add(movingVec.scale(movSpeed)));
            }
            float dist = (float) ((Math.abs(this.getDeltaMovement().x()) + Math.abs(this.getDeltaMovement().z())) * 30);
            this.incrementJellyPitch(dist);
            if (this.horizontalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2F, 0));
            }
            if (this.getJellyPitch() > 0F) {
                float decrease = Math.min(0.5F, this.getJellyPitch());
                this.decrementJellyPitch(decrease);
            }
            if (this.getJellyPitch() < 0F) {
                float decrease = Math.min(0.5F, -this.getJellyPitch());
                this.incrementJellyPitch(decrease);
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putFloat("JellyScale", this.getJellyScale());
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setJellyScale(compound.getFloat("JellyScale"));
        this.setVariant(compound.getInt("Variant"));
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        double x = pos.getX() + 0.5F;
        double y = pos.getY() + 0.5F;
        double z = pos.getZ() + 0.5F;
        HitResult result = this.level.clip(new ClipContext(this.getEyePosition(), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, 0.6D, 0.9D));
        } else {
            super.travel(travelVector);
        }

    }

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
        CompoundTag compoundnbt = bucket.getOrCreateTag();
        compoundnbt.putFloat("BucketScale", this.getJellyScale());
        compoundnbt.putInt("BucketVariantTag", this.getVariant());
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
        if (compound.contains("BucketScale")){
            this.setJellyScale(compound.getFloat("BucketScale"));
        }
        if (compound.contains("BucketVariantTag")){
            this.setVariant(compound.getInt("BucketVariantTag"));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setVariant(random.nextInt(3));
        this.setJellyScale(0.8F + random.nextFloat() * 0.4F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void incrementJellyPitch(float pitch) {
        entityData.set(JELLYPITCH, getJellyPitch() + pitch);
    }

    public void decrementJellyPitch(float pitch) {
        entityData.set(JELLYPITCH, getJellyPitch() - pitch);
    }


    protected float rotlerp(float p_24992_, float p_24993_, float p_24994_) {
        float f = Mth.wrapDegrees(p_24993_ - p_24992_);
        if (f > p_24994_) {
            f = p_24994_;
        }

        if (f < -p_24994_) {
            f = -p_24994_;
        }

        float f1 = p_24992_ + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

}
