package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class EntityTriops extends WaterAnimal implements ITargetsDroppedItems, Bucketable {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityTriops.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TRIOPS_SCALE = SynchedEntityData.defineId(EntityTriops.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> BABY_AGE = SynchedEntityData.defineId(EntityTriops.class, EntityDataSerializers.INT);
    public float prevOnLandProgress;
    public float onLandProgress;
    public float prevSwimRot;
    public float swimRot;
    public boolean fedCarrot = false;
    public int breedCooldown = 0;
    public float tail1Yaw;
    public float prevTail1Yaw;
    public float tail2Yaw;
    public float prevTail2Yaw;
    public float moveDistance;

    private EntityTriops breedWith;
    private boolean pregnant;

    public EntityTriops(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new AquaticMoveController(this, 1.0F, 15F);
        tail1Yaw = this.getYRot();
        prevTail1Yaw = this.getYRot();
        tail2Yaw = this.getYRot();
        prevTail2Yaw = this.getYRot();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(TRIOPS_SCALE, 1F);
        this.entityData.define(BABY_AGE, 0);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BreedGoal());
        this.goalSelector.addGoal(1, new LayEggGoal());
        this.goalSelector.addGoal(2, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(4, new AnimalAISwimBottom(this, 1F, 7));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 10));
    }

    public int getMaxSpawnClusterSize() {
        return 5;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }


    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, 0.8D, 0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
            moveDistance += travelVector.horizontalDistance();
        } else {
            super.travel(travelVector);
        }

    }

    protected void playSwimSound(float f) {
        if (random.nextInt(2) == 0) {
            this.playSound(this.getSwimSound(), 0.2F, 1.3F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean sit) {
        this.entityData.set(FROM_BUCKET, sit);
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket() || this.isBaby() || fedCarrot;
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isBaby() && !this.fromBucket() && !fedCarrot;
    }


    protected void handleAirSupply(int i) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(i - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().dryOut(), random.nextInt(2) == 0 ? 1F : 0F);
            }
        } else {
            this.setAirSupply(2000);
        }
    }

    public int getBabyAge() {
        return this.entityData.get(BABY_AGE);
    }

    public void setBabyAge(int babyAge) {
        this.entityData.set(BABY_AGE, babyAge);
    }

    public float getTriopsScale() {
        return this.entityData.get(TRIOPS_SCALE);
    }

    public void setTriopsScale(float scale) {
        this.entityData.set(TRIOPS_SCALE, scale);
    }

    public boolean isBaby() {
        return getBabyAge() < 0;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putBoolean("FedCarrot", this.fedCarrot);
        compound.putBoolean("Pregnant", this.pregnant);
        compound.putInt("BreedCooldown", this.breedCooldown);
        compound.putFloat("TriopsScale", this.getTriopsScale());
        compound.putInt("BabyAge", this.getBabyAge());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.fedCarrot = compound.getBoolean("FedCarrot");
        this.pregnant = compound.getBoolean("Pregnant");
        this.breedCooldown = compound.getInt("BreedCooldown");
        this.setTriopsScale(compound.getFloat("TriopsScale"));
        this.setBabyAge(compound.getInt("BabyAge"));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setTriopsScale(0.9F + random.nextFloat() * 0.2F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public void tick() {
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        this.prevSwimRot = swimRot;
        this.prevTail1Yaw = tail1Yaw;
        this.prevTail2Yaw = tail2Yaw;
        final boolean onLand = !this.isInWaterOrBubble() && this.onGround();
        this.setXRot((float) -((float) this.getDeltaMovement().y * 2.2F * Mth.RAD_TO_DEG));
        if (onLand && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (!onLand && onLandProgress > 0F) {
            onLandProgress--;
        }
        if (breedCooldown > 0) {
            breedCooldown--;
        }
        tail1Yaw = Mth.approachDegrees(this.tail1Yaw, yBodyRot, 7);
        tail2Yaw = Mth.approachDegrees(this.tail2Yaw, this.tail1Yaw, 7);
        if (onLandProgress == 0) {
            float f = (float) (20 * Math.sin(this.walkAnimation.position()) * walkAnimation.speed());
            swimRot = Mth.approachDegrees(this.swimRot, f, 2);
        }
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 67) {
            for (int i = 0; i < 5; i++) {
                level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.5F), this.getY(0.8F), this.getRandomZ(0.5F), 0.0D, 0.0D, 0.0D);
            }
        } else if (id == 68) {
            level().addParticle(ParticleTypes.HEART, this.getX(), this.getY(0.8F), this.getZ(), 0.0D, 0.0D, 0.0D);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return (stack.is(Tags.Items.CROPS_CARROT) || stack.is(AMItemRegistry.MOSQUITO_LARVA.get())) && !fedCarrot;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack stack = e.getItem();
        if (stack.getItem().isEdible() && stack.getItem().getFoodProperties() != null) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.CAT_EAT, this.getVoicePitch(), this.getSoundVolume());
            this.heal(5);
            if (!this.level().isClientSide) {
                if (breedCooldown == 0 && !fedCarrot) {
                    this.fedCarrot = true;
                    this.level().broadcastEntityEvent(this, (byte) 67);
                }
            }
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!type.consumesAction() && canTargetItem(itemstack) && !this.fedCarrot) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.CAT_EAT, this.getVoicePitch(), this.getSoundVolume());
            this.heal(5);
            if (itemstack.is(Tags.Items.CROPS_CARROT)) {
                if (!this.level().isClientSide) {
                    if (breedCooldown == 0) {
                        this.level().broadcastEntityEvent(this, (byte) 67);
                    }
                }
                this.fedCarrot = true;
            }
            return InteractionResult.SUCCESS;
        }
        return Bucketable.bucketMobPickup(player, hand, this).orElse(type);
    }

    public boolean isSearchingForMate() {
        return this.isAlive() && this.isInWaterOrBubble() && this.fedCarrot && this.breedCooldown <= 0;
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.put("TriopsTag", platTag);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("TriopsTag")) {
            this.readAdditionalSaveData(compound.getCompound("TriopsTag"));
        }
        this.setAirSupply(2000);
    }

    @Override
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.TRIOPS_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TRIOPS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TRIOPS_HURT.get();
    }

    private class BreedGoal extends Goal {
        private final Predicate<Entity> validBreedPartner;
        private EntityTriops breedPartner;

        private int executionCooldown = 50;

        public BreedGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.validBreedPartner = (shrimp -> shrimp instanceof EntityTriops otherFish && otherFish.getId() != EntityTriops.this.getId() && otherFish.isSearchingForMate());
        }

        @Override
        public boolean canUse() {
            if (!EntityTriops.this.isInWaterOrBubble() || !EntityTriops.this.fedCarrot || EntityTriops.this.breedCooldown > 0 || EntityTriops.this.breedWith != null) {
                return false;
            }
            if (executionCooldown > 0) {
                executionCooldown--;
            } else {
                executionCooldown = 50 + random.nextInt(50);
                List<EntityTriops> list = EntityTriops.this.level().getEntitiesOfClass(EntityTriops.class, EntityTriops.this.getBoundingBox().inflate(10, 8, 10), EntitySelector.NO_SPECTATORS.and(validBreedPartner));
                list.sort(Comparator.comparingDouble(EntityTriops.this::distanceToSqr));
                if (!list.isEmpty()) {
                    EntityTriops closestPupfish = list.get(0);
                    if (closestPupfish != null) {
                        breedPartner = closestPupfish;
                        breedPartner.breedWith = EntityTriops.this;
                        return true;
                    }
                }

            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return breedPartner != null && !EntityTriops.this.pregnant && !breedPartner.pregnant && EntityTriops.this.breedWith == null && breedPartner.isSearchingForMate() && EntityTriops.this.isSearchingForMate();
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            EntityTriops.this.fedCarrot = false;
            EntityTriops.this.breedCooldown = 1200 + random.nextInt(3600);
        }

        @Override
        public void tick() {
            EntityTriops.this.getNavigation().moveTo(breedPartner, 1D);
            breedPartner.getNavigation().moveTo(EntityTriops.this, 1D);
            if (EntityTriops.this.distanceTo(breedPartner) < 1.2F) {
                EntityTriops.this.level().broadcastEntityEvent(EntityTriops.this, (byte) 68);
                EntityTriops.this.pregnant = true;
            }
        }
    }

    class LayEggGoal extends Goal {
        private BlockPos eggPos;

        LayEggGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public void stop() {
            eggPos = null;
        }

        public boolean canUse() {
            if (EntityTriops.this.pregnant && EntityTriops.this.getRandom().nextInt(30) == 0) {
                BlockPos egg = getEggLayPos();
                if (egg != null) {
                    eggPos = egg;
                    return true;
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return eggPos != null && EntityTriops.this.pregnant && EntityTriops.this.level().getBlockState(eggPos).isAir();
        }

        public boolean isValidPos(BlockPos pos) {
            BlockState state = EntityTriops.this.level().getBlockState(pos);
            FluidState stateBelow = EntityTriops.this.level().getFluidState(pos.below());
            return stateBelow.is(FluidTags.WATER) && state.isAir();
        }

        public BlockPos getEggLayPos() {
            for (int i = 0; i < 10; i++) {
                BlockPos offset = EntityTriops.this.blockPosition().offset(EntityTriops.this.getRandom().nextInt(10) - 5, 10, EntityTriops.this.getRandom().nextInt(10) - 5);
                while (level().getBlockState(offset.below()).isAir() && offset.getY() > EntityTriops.this.level().getMinBuildHeight()) {
                    offset = offset.below();
                }
                if (isValidPos(offset)) {
                    return offset;
                }
            }
            return null;
        }

        public void tick() {
            super.tick();
            EntityTriops.this.getNavigation().moveTo(eggPos.getX(), eggPos.getY(), eggPos.getZ(), 1);
            if (EntityTriops.this.distanceToSqr(Vec3.atBottomCenterOf(eggPos)) < 2.0F) {
                EntityTriops.this.pregnant = false;
                EntityTriops.this.level().setBlockAndUpdate(eggPos, AMBlockRegistry.TRIOPS_EGGS.get().defaultBlockState());
            }
        }
    }
}
