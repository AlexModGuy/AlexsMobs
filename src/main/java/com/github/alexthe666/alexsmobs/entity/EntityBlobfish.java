package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityBlobfish extends WaterAnimal implements FlyingAnimal, Bucketable {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityBlobfish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> BLOBFISH_SCALE = SynchedEntityData.defineId(EntityBlobfish.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DEPRESSURIZED = SynchedEntityData.defineId(EntityBlobfish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLIMED = SynchedEntityData.defineId(EntityBlobfish.class, EntityDataSerializers.BOOLEAN);
    public float squishFactor;
    public float prevSquishFactor;
    public float squishAmount;
    private boolean wasOnGround;

    protected EntityBlobfish(EntityType type, Level world) {
        super(type, world);
        this.moveControl = new AquaticMoveController(this, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D);
    }


    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.blobfishSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }

    protected void handleAirSupply(int p_209207_1_) {
        if (this.isAlive() && !this.isInWaterOrBubble() && !isSlimed()) {
            this.setAirSupply(p_209207_1_ - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().drown(), random.nextInt(2) == 0 ? 1F : 0F);
            }
        } else {
            this.setAirSupply(2000);
        }
    }

    protected float getStandingEyeHeight(Pose p_213348_1_, EntityDimensions p_213348_2_) {
        return p_213348_2_.height * 0.65F;
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName() || this.fromBucket() || isSlimed();
    }

    public boolean removeWhenFarAway(double p_213397_1_) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(BLOBFISH_SCALE, 1.0F);
        this.entityData.define(DEPRESSURIZED, false);
        this.entityData.define(SLIMED, false);
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return super.getDimensions(poseIn).scale(this.getBlobfishScale());
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
        compound.putBoolean("Depressurized", this.isDepressurized());
        compound.putBoolean("Slimed", this.isSlimed());
        compound.putFloat("BlobfishScale", this.getBlobfishScale());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setDepressurized(compound.getBoolean("Depressurized"));
        this.setSlimed(compound.getBoolean("Slimed"));
        this.setBlobfishScale(compound.getFloat("BlobfishScale"));
    }

    private boolean hasClearance() {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        for (int l1 = 0; l1 < 10; ++l1) {
            BlockState blockstate = level().getBlockState(blockpos$mutable.set(this.getX(), this.getY() + l1, this.getZ()));
            if (!blockstate.getFluidState().is(FluidTags.WATER) && !blockstate.isSolid()) {
                return false;
            }
        }

        return true;
    }


    public float getBlobfishScale() {
        return this.entityData.get(BLOBFISH_SCALE);
    }

    public void setBlobfishScale(float scale) {
        this.entityData.set(BLOBFISH_SCALE, scale);
    }

    public boolean isDepressurized() {
        return this.entityData.get(DEPRESSURIZED);
    }

    public void setDepressurized(boolean depressurized) {
        this.entityData.set(DEPRESSURIZED, depressurized);
    }

    public boolean isSlimed() {
        return this.entityData.get(SLIMED);
    }

    public void setSlimed(boolean slimed) {
        this.entityData.set(SLIMED, slimed);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(3, new AnimalAISwimBottom(this, 1F, 7));
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

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        final ItemStack lvt_3_1_ = player.getItemInHand(hand);
        if (lvt_3_1_.getItem() == Items.SLIME_BALL && this.isAlive() && !this.isSlimed()) {
            this.setSlimed(true);
            for (int i = 0; i < 6 + random.nextInt(3); i++) {
                final double d2 = this.random.nextGaussian() * 0.02D;
                final double d0 = this.random.nextGaussian() * 0.02D;
                final double d1 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, lvt_3_1_), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
            }
            lvt_3_1_.shrink(1);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
    }

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.BLOBFISH_BUCKET.get());
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
        compound.putFloat("BucketScale", this.getBlobfishScale());
        compound.putBoolean("Slimed", this.isSlimed());
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
        if (compound.contains("BucketScale")){
            this.setBlobfishScale(compound.getFloat("BucketScale"));
        }
        if (compound.contains("Slimed")){
            this.setSlimed(compound.getBoolean("Slimed"));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setBlobfishScale(0.75F + random.nextFloat() * 0.5F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void tick() {
        super.tick();
        this.prevSquishFactor = this.squishFactor;
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;

        final float f2 = (float) -((float) this.getDeltaMovement().y * 2.2F * Mth.RAD_TO_DEG);
        this.setXRot(f2);
        if (!isInWater()) {
            if (this.onGround()) {
                if (!this.wasOnGround)
                    this.squishAmount = -0.35F;
            } else {
                if (this.wasOnGround)
                    this.squishAmount = 2F;
            }
        }
        this.wasOnGround = this.onGround();

        this.alterSquishAmount();
        final boolean clear = hasClearance();
        if (clear) {
            if (this.isDepressurized())
                this.setDepressurized(false);
        } else {
            if (!isDepressurized())
                this.setDepressurized(true);
        }
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COD_HURT;
    }

    public static boolean canBlobfishSpawn(EntityType<EntityBlobfish> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || pos.getY() <= AMConfig.blobfishSpawnHeight && iServerWorld.getFluidState(pos).is(FluidTags.WATER) && iServerWorld.getFluidState(pos.above()).is(FluidTags.WATER);
    }

    @Override
    public boolean isFlying() {
        return false;
    }

}
