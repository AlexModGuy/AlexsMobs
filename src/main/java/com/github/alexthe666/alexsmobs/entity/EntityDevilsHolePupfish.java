package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EntityDevilsHolePupfish extends WaterAnimal implements FlyingAnimal, Bucketable {

    public static final ResourceLocation PUPFISH_REWARD = new ResourceLocation("alexsmobs", "gameplay/pupfish_reward");
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> PUPFISH_SCALE = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> FEEDING_TIME = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BABY_AGE = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> FEEDING_POS = SynchedEntityData.defineId(EntityDevilsHolePupfish.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public float prevOnLandProgress;
    public float onLandProgress;
    public float prevFeedProgress;
    public float feedProgress;
    private EntityDevilsHolePupfish chasePartner;
    private int chaseTime = 0;
    private boolean chaseDriver;
    private boolean breedNextChase;
    private int chaseCooldown = 0;
    private int maxChaseTime = 300;

    protected EntityDevilsHolePupfish(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new AquaticMoveController(this, 1.0F, 15F);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }


    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.DEVILS_HOLE_PUPFISH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.DEVILS_HOLE_PUPFISH_HURT.get();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new EatMossGoal(this));
        this.goalSelector.addGoal(3, new ChaseGoal(this));
        this.goalSelector.addGoal(4, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(5, new AnimalAIRandomSwimming(this, 1F, 12, 5));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.34F);
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName() || this.fromBucket();
    }

    public static boolean canPupfishSpawn(EntityType<EntityDevilsHolePupfish> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || isPupfishChunk(iServerWorld, pos) && iServerWorld.getFluidState(pos).is(FluidTags.WATER) && isInCave(iServerWorld, pos);
    }

    private static boolean isPupfishChunk(ServerLevelAccessor iServerWorld, BlockPos pos) {
        AMWorldData data = AMWorldData.get(iServerWorld.getLevel());
        return data != null && data.isInPupfishChunk(pos);
    }

    private static boolean isInCave(ServerLevelAccessor iServerWorld, BlockPos pos) {
        while(iServerWorld.getFluidState(pos).is(FluidTags.WATER)){
            pos = pos.above();
        }
        return !iServerWorld.canSeeSky(pos) && pos.getY() < iServerWorld.getSeaLevel();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.devilsHolePupfishSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public int getMaxSpawnClusterSize() {
        return 6;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(PUPFISH_SCALE, 1.0F);
        this.entityData.define(FEEDING_TIME, 0);
        this.entityData.define(BABY_AGE, 0);
        this.entityData.define(FEEDING_POS, Optional.empty());
    }

    public void tick() {
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        this.prevFeedProgress = feedProgress;
        if(chaseCooldown > 0){
            chaseCooldown--;
        }
        final boolean inWaterOrBubble = this.isInWaterOrBubble();
        if (!inWaterOrBubble && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (inWaterOrBubble && onLandProgress > 0F) {
            onLandProgress--;
        }
        final int feedingTime = this.getFeedingTime();
        if (feedingTime > 0 && feedProgress < 5F) {
            feedProgress++;
        }
        if (feedingTime <= 0 && feedProgress > 0F) {
            feedProgress--;
        }
        if(this.isBaby()){
            this.setBabyAge(this.getBabyAge() + 1);
        }
        BlockPos feedingPos = this.entityData.get(FEEDING_POS).orElse(null);
        if(feedingPos == null){
            float f2 = (float) -((float) this.getDeltaMovement().y * 2.2F * (double) Mth.RAD_TO_DEG);
            this.setXRot(f2);
        }else if(this.getFeedingTime() > 0){
            Vec3 face = Vec3.atCenterOf(feedingPos).subtract(this.position());
            double d0 = face.horizontalDistance();
            this.setXRot((float)(-Mth.atan2(face.y, d0) * (double)Mth.RAD_TO_DEG));
            this.setYRot(((float) Mth.atan2(face.z, face.x)) * Mth.RAD_TO_DEG - 90F);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            BlockState state = level().getBlockState(feedingPos);
            if(random.nextInt(2) == 0 && !state.isAir()){
                Vec3 mouth = new Vec3(0, this.getBbHeight() * 0.5F, 0.4F * this.getPupfishScale()).xRot(this.getXRot() * Mth.DEG_TO_RAD).yRot(-this.getYRot() * Mth.DEG_TO_RAD);
                for (int i = 0; i < 4 + random.nextInt(2); i++) {
                    double motX = this.random.nextGaussian() * 0.02D;
                    double motY = 0.1F + random.nextFloat() * 0.2F;
                    double motZ = this.random.nextGaussian() * 0.02D;
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), this.getX() + mouth.x, this.getY() + mouth.y, this.getZ() + mouth.z, motX, motY, motZ);
                }
            }
        }
        if(!isInWaterOrBubble() && this.isAlive()){
            if (this.onGround() && random.nextFloat() < 0.5F) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(SoundEvents.COD_FLOP, this.getSoundVolume(), this.getVoicePitch());
            }
        }
    }


    public EntityDimensions getDimensions(Pose poseIn) {
        return super.getDimensions(poseIn).scale(this.getPupfishScale());
    }

    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean bucketed) {
        this.entityData.set(FROM_BUCKET, bucketed);
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.putFloat("BucketScale", this.getPupfishScale());
        compound.putFloat("BabyAge", this.getBabyAge());
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
        if (compound.contains("BucketScale")){
            this.setPupfishScale(compound.getFloat("BucketScale"));
        }
        if (compound.contains("BabyAge")){
            this.setBabyAge(compound.getInt("BabyAge"));
        }
    }

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.DEVILS_HOLE_PUPFISH_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    public float getPupfishScale() {
        return this.entityData.get(PUPFISH_SCALE);
    }

    public void setPupfishScale(float scale) {
        this.entityData.set(PUPFISH_SCALE, scale);
    }

    public int getFeedingTime() {
        return this.entityData.get(FEEDING_TIME);
    }

    public void setFeedingTime(int feedingTime) {
        this.entityData.set(FEEDING_TIME, feedingTime);
    }

    public int getBabyAge() {
        return this.entityData.get(BABY_AGE);
    }

    public void setBabyAge(int babyAge) {
        this.entityData.set(BABY_AGE, babyAge);
    }

    public boolean isBaby(){
        return getBabyAge() < 0;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putBoolean("BreedNextChase", this.breedNextChase);
        compound.putFloat("PupfishScale", this.getPupfishScale());
        compound.putInt("BabyAge", this.getBabyAge());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.breedNextChase = compound.getBoolean("BreedNextChase");
        this.setPupfishScale(compound.getFloat("PupfishScale"));
        this.setBabyAge(compound.getInt("BabyAge"));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setPupfishScale(0.65F + random.nextFloat() * 0.35F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void handleAirSupply(int i) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(i - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(getMaxAirSupply());
        }
    }

    public int getMaxAirSupply() {
        return 600;
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


    protected void playSwimSound(float f) {
        if(random.nextInt(2) == 0){
            this.playSound(this.getSwimSound(), 0.2F, 1.3F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    private boolean canSeeBlock(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }

    private static List<ItemStack> getFoodLoot(EntityDevilsHolePupfish pupfish) {
        LootTable loottable = pupfish.level().getServer().getLootData().getLootTable(PUPFISH_REWARD);
        return loottable.getRandomItems((new LootParams.Builder((ServerLevel) pupfish.level())).withParameter(LootContextParams.THIS_ENTITY, pupfish).create(LootContextParamSets.PIGLIN_BARTER));
    }

    public boolean removeWhenFarAway(double dist) {
        return !this.fromBucket() && !this.hasCustomName() && !this.isBaby();
    }

    private class ChaseGoal extends Goal {
        private final EntityDevilsHolePupfish pupfish;
        private final Predicate<Entity> validChasePartner;
        private int executionCooldown = 50;

        public ChaseGoal(EntityDevilsHolePupfish pupfish) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.pupfish = pupfish;
            this.validChasePartner = (pupfish1 -> pupfish1 instanceof EntityDevilsHolePupfish otherFish && otherFish.getId() != this.pupfish.getId() && otherFish.chasePartner == null && otherFish.chaseCooldown <= 0);
        }

        @Override
        public boolean canUse() {
            if(!pupfish.isInWaterOrBubble() || pupfish.chaseTime > pupfish.maxChaseTime || pupfish.chaseCooldown > 0){
                return false;
            }
            if(pupfish.chasePartner != null && pupfish.chasePartner.isAlive()){
                return true;
            }
            if(executionCooldown > 0){
                executionCooldown--;
            }else{
                executionCooldown = 50 + random.nextInt(50);
                if(pupfish.chasePartner == null || !pupfish.chasePartner.isAlive()){
                    List<EntityDevilsHolePupfish> list = pupfish.level().getEntitiesOfClass(EntityDevilsHolePupfish.class, pupfish.getBoundingBox().inflate(10, 8, 10), EntitySelector.NO_SPECTATORS.and(validChasePartner));
                    list.sort(Comparator.comparingDouble(pupfish::distanceToSqr));
                    if(!list.isEmpty()){
                        EntityDevilsHolePupfish closestPupfish = list.get(0);
                        if(closestPupfish != null){
                            pupfish.chasePartner = closestPupfish;
                            closestPupfish.chasePartner = pupfish;
                            pupfish.chaseDriver = true;
                            return true;
                        }
                    }
                    return false;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return pupfish.chasePartner != null && pupfish.chasePartner.isAlive() && pupfish.chaseTime < pupfish.maxChaseTime;
        }

        @Override
        public void start() {
            pupfish.chaseDriver = !pupfish.chasePartner.chaseDriver;
            pupfish.chaseTime = 0;
            pupfish.maxChaseTime = 600;
        }

        @Override
        public void stop() {
            pupfish.chaseTime = 0;
            pupfish.chaseCooldown = 100 + random.nextInt(100);
            executionCooldown = 50 + random.nextInt(20);
            if(pupfish.breedNextChase){
                pupfish.spawnBabiesWith(pupfish.chasePartner);
                pupfish.chasePartner.breedNextChase = false;
                pupfish.breedNextChase = false;
            }
            pupfish.chasePartner = null;
        }

        @Override
        public void tick() {
            pupfish.chaseTime++;
            if(pupfish.chasePartner == null || !pupfish.chaseDriver){
                return;
            }
            float chaserSpeed = 1.2F + random.nextFloat() * 0.45F;
            float chasedSpeed = 0.2F + chaserSpeed * 0.7F;
            EntityDevilsHolePupfish flee = pupfish.chaseDriver ? pupfish.chasePartner : pupfish;
            EntityDevilsHolePupfish driver = pupfish.chaseDriver ? pupfish : pupfish.chasePartner;
            driver.getNavigation().moveTo(flee.getX(), flee.getY(0.5F), flee.getZ(), chaserSpeed);
            Vec3 from = flee.position().add(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).subtract(driver.position()).normalize().scale(2F + random.nextFloat() * 2F);
            Vec3 to = flee.position().add(from);
            flee.getNavigation().moveTo(to.x, to.y, to.z, chasedSpeed);
            if(random.nextInt(50) == 0){
                pupfish.chaseDriver = !pupfish.chaseDriver;
                pupfish.chasePartner.chaseDriver = !pupfish.chasePartner.chaseDriver;
            }
        }
    }

    private void spawnBabiesWith(EntityDevilsHolePupfish chasePartner) {
        EntityDevilsHolePupfish baby = AMEntityRegistry.DEVILS_HOLE_PUPFISH.get().create(level());
        baby.copyPosition(this);
        baby.setPupfishScale(0.65F + random.nextFloat() * 0.35F);
        baby.setBabyAge(-24000);
        level().addFreshEntity(baby);
    }

    private class EatMossGoal extends Goal {
        private final int searchLength;
        private final int verticalSearchRange;
        protected BlockPos destinationBlock;
        private final EntityDevilsHolePupfish pupfish;
        private int runDelay = 70;
        private int maxFeedTime = 200;

        private EatMossGoal(EntityDevilsHolePupfish pupfish) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.pupfish = pupfish;
            searchLength = 16;
            verticalSearchRange = 6;
        }

        public boolean canContinueToUse() {
            return destinationBlock != null && isMossBlock(pupfish.level(), destinationBlock.mutable()) && isCloseToMoss(16);
        }

        public boolean isCloseToMoss(double dist) {
            return destinationBlock == null || pupfish.distanceToSqr(Vec3.atCenterOf(destinationBlock)) < dist * dist;
        }

        @Override
        public boolean canUse() {
            if (!pupfish.isInWaterOrBubble()) {
                return false;
            }
            if (this.runDelay > 0) {
                --this.runDelay;
                return false;
            } else {
                this.runDelay = 200 + pupfish.random.nextInt(150);
                return this.searchForDestination();
            }
        }

        public void start(){
            maxFeedTime = 60 + random.nextInt(60);
        }

        public void tick() {
            Vec3 vec = Vec3.atCenterOf(destinationBlock);
            if (vec != null) {
                pupfish.getNavigation().moveTo(vec.x, vec.y, vec.z, 1F);
                if(pupfish.distanceToSqr(vec) < 1.15F){
                    pupfish.entityData.set(FEEDING_POS, Optional.of(destinationBlock));
                    Vec3 face = vec.subtract(pupfish.position());
                    pupfish.setDeltaMovement(pupfish.getDeltaMovement().add(face.normalize().scale(0.1F)));
                    pupfish.setFeedingTime(pupfish.getFeedingTime() + 1);
                    if(pupfish.getFeedingTime() > maxFeedTime){
                        destinationBlock = null;
                        if(random.nextInt(3) == 0){
                            List<ItemStack> lootList = getFoodLoot(pupfish);
                            if (!lootList.isEmpty()) {
                                for (ItemStack stack : lootList) {
                                    ItemEntity e = pupfish.spawnAtLocation(stack.copy());
                                    e.hasImpulse = true;
                                    e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
                                }
                            }
                        }
                        if(random.nextInt(3) == 0 && !pupfish.isBaby()){
                            pupfish.breedNextChase = true;
                        }
                    }
                }else{
                    pupfish.entityData.set(FEEDING_POS, Optional.empty());
                }
            }
        }

        public void stop() {
            pupfish.entityData.set(FEEDING_POS, Optional.empty());
            destinationBlock = null;
            pupfish.setFeedingTime(0);
        }

        protected boolean searchForDestination() {
            int lvt_1_1_ = this.searchLength;
            //int lvt_2_1_ = this.verticalSearchRange;
            BlockPos lvt_3_1_ = pupfish.blockPosition();
            BlockPos.MutableBlockPos lvt_4_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
                for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; ++lvt_6_1_) {
                    for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                        for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0; lvt_8_1_ <= lvt_6_1_; lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_) {
                            lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                            if (this.isMossBlock(pupfish.level(), lvt_4_1_) && pupfish.canSeeBlock(lvt_4_1_)) {
                                this.destinationBlock = lvt_4_1_;
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        private boolean isMossBlock(Level world, BlockPos.MutableBlockPos pos) {
            return world.getBlockState(pos).is(AMTagRegistry.PUPFISH_EATABLES);
        }

    }

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

}
