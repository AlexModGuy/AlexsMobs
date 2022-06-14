package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EntityCatfish extends WaterAnimal implements FlyingAnimal, Bucketable, ContainerListener {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CATFISH_SIZE = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SPIT_TIME = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_SWALLOWED_ENTITY = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> SWALLOWED_ENTITY_TYPE = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<CompoundTag> SWALLOWED_ENTITY_DATA = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDimensions SMALL_SIZE = EntityDimensions.scalable(0.9F, 0.6F);
    private static final EntityDimensions MEDIUM_SIZE = EntityDimensions.scalable(1.25F, 0.9F);
    private static final EntityDimensions LARGE_SIZE = EntityDimensions.scalable(1.9F, 0.9F);
    public static final ResourceLocation MEDIUM_LOOT = new ResourceLocation("alexsmobs", "entities/catfish_medium");
    public static final ResourceLocation LARGE_LOOT = new ResourceLocation("alexsmobs", "entities/catfish_large");
    public SimpleContainer catfishInventory;
    private int eatCooldown = 0;

    protected EntityCatfish(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        initCatfishInventory();
        this.moveControl = new AquaticMoveController(this, 1.0F, 15F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    public boolean isMaxGroupSizeReached(int sze) {
        return sze > 2;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(3, new TargetFoodGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(Items.SEA_LANTERN), false));
        this.goalSelector.addGoal(5, new FascinateLanternGoal(this));
        this.goalSelector.addGoal(6, new AnimalAISwimBottom(this, 1F, 7));
    }


    public boolean removeWhenFarAway(double p_27492_) {
        return !this.fromBucket() && !requiresCustomPersistence() && !this.hasCustomName();
    }

    private void initCatfishInventory() {
        SimpleContainer animalchest = this.catfishInventory;
        int size = this.getCatfishSize() > 2 ? 1 : this.getCatfishSize() == 1 ? 9 : 3;
        this.catfishInventory = new SimpleContainer(size) {
            public boolean stillValid(Player player) {
                return EntityCatfish.this.isAlive() && !EntityCatfish.this.isInsidePortal;
            }
        };
        catfishInventory.addListener(this);
        if (animalchest != null) {
            int i = Math.min(animalchest.getContainerSize(), this.catfishInventory.getContainerSize());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.catfishInventory.setItem(j, itemstack.copy());
                }
            }
        }
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.catfishInventory != null) {
            for (int i = 0; i < catfishInventory.getContainerSize(); i++) {
                this.spawnAtLocation(catfishInventory.getItem(i));
            }
            catfishInventory.clearContent();
        }
        if(this.getCatfishSize() == 2){
            this.spit();
        }
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket() || this.hasSwallowedEntity() || !this.catfishInventory.isEmpty();
    }

    public static boolean canCatfishSpawn(EntityType<EntityCatfish> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || iServerWorld.getBlockState(pos).getFluidState().is(Fluids.WATER) && random.nextInt(1) == 0;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.catfishSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(CATFISH_SIZE, 0);
        this.entityData.define(SPIT_TIME, 0);
        this.entityData.define(SWALLOWED_ENTITY_TYPE, "minecraft:pig");
        this.entityData.define(SWALLOWED_ENTITY_DATA, new CompoundTag());
        this.entityData.define(HAS_SWALLOWED_ENTITY, false);
    }

    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if(this.getSpitTime() > 0){
                this.setSpitTime(this.getSpitTime() - 1);
            }
            if(eatCooldown > 0){
                eatCooldown--;
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        boolean inSeaPickle = false;
        int width = (int)Math.ceil(this.getBbWidth() / 2F);
        int height = (int)Math.ceil(this.getBbHeight() / 2F);
        BlockPos.MutableBlockPos pos = this.blockPosition().mutable();
        BlockPos vomitTo = null;
        for(int i = -width; i <= width; i++){
            for(int j = -height; j <= height; j++){
                for(int k = -width; k <= width; k++){
                    pos.set(this.getX() + i, this.getY() + j, this.getZ() + k);
                    if(level.getBlockState(pos).is(Blocks.SEA_PICKLE)){
                        inSeaPickle = true;
                        vomitTo = pos;
                        break;
                    }
                }
            }
        }
        if(inSeaPickle && this.canSpit()){
            if(this.getSpitTime() == 0){
                this.playSound(SoundEvents.PLAYER_BURP, this.getSoundVolume(), this.getVoicePitch());
            }
            if(vomitTo != null){
                Vec3 face = Vec3.atCenterOf(vomitTo).subtract(this.getMouthVec());
                double d0 = face.horizontalDistance();
                this.setXRot((float)(-Mth.atan2(face.y, d0) * (double)(180F / (float)Math.PI)));
                this.setYRot(((float) Mth.atan2(face.z, face.x)) * (180F / (float) Math.PI) - 90F);
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();
            }
            this.spit();
        }
    }

    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        if (this.getCatfishSize() == 2) {
            return LARGE_LOOT;
        }
        return this.getCatfishSize() == 1 ? MEDIUM_LOOT : super.getDefaultLootTable();
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (CATFISH_SIZE.equals(accessor)) {
            this.refreshDimensions();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10F * this.getCatfishSize() + 10F);
            this.heal(50F);
        }
        super.onSyncedDataUpdated(accessor);
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
        addAdditionalSaveData(compound);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
        readAdditionalSaveData(compound);
    }

    @Override
    public ItemStack getBucketItemStack() {
        Item item = AMItemRegistry.SMALL_CATFISH_BUCKET.get();
        if(this.getCatfishSize() == 1){
            item = AMItemRegistry.MEDIUM_CATFISH_BUCKET.get();
        }else if(this.getCatfishSize() == 2){
            item = AMItemRegistry.LARGE_CATFISH_BUCKET.get();
        }
        return new ItemStack(item);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    public int getCatfishSize() {
        return Mth.clamp(this.entityData.get(CATFISH_SIZE), 0, 2);
    }

    public void setCatfishSize(int catfishSize) {
        this.entityData.set(CATFISH_SIZE, catfishSize);
    }

    public int getSpitTime() {
        return this.entityData.get(SPIT_TIME);
    }

    public void setSpitTime(int time) {
        this.entityData.set(SPIT_TIME, time);
    }

    public boolean isSpitting() {
        return getSpitTime() > 0;
    }

    public String getSwallowedEntityType() {
        return this.entityData.get(SWALLOWED_ENTITY_TYPE);
    }

    public void setSwallowedEntityType(String containedEntityType) {
        this.entityData.set(SWALLOWED_ENTITY_TYPE, containedEntityType);
    }

    public CompoundTag getSwallowedData() {
        return this.entityData.get(SWALLOWED_ENTITY_DATA);
    }

    public void setSwallowedData(CompoundTag containedData) {
        this.entityData.set(SWALLOWED_ENTITY_DATA, containedData);
    }

    public boolean hasSwallowedEntity() {
        return this.entityData.get(HAS_SWALLOWED_ENTITY);
    }

    public void setHasSwallowedEntity(boolean swallowedEntity) {
        this.entityData.set(HAS_SWALLOWED_ENTITY, swallowedEntity);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return getDimsForCatfish().scale(this.getScale());
    }

    public boolean hurt(DamageSource source, float f) {
        if(super.hurt(source, f)){
            this.spit();
            return true;
        }else{
            return false;
        }
    }

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.SEA_PICKLE) {
            this.spit();
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putFloat("CatfishSize", this.getCatfishSize());
        if (catfishInventory != null) {
            ListTag nbttaglist = new ListTag();
            for (int i = 0; i < this.catfishInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.catfishInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag CompoundNBT = new CompoundTag();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.save(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            compound.put("Items", nbttaglist);
        }
        compound.putString("ContainedEntityType", this.getSwallowedEntityType());
        compound.put("ContainedData", this.getSwallowedData());
        compound.putBoolean("HasSwallowedEntity", this.hasSwallowedEntity());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setCatfishSize(compound.getInt("CatfishSize"));
        if (catfishInventory != null) {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initCatfishInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.catfishInventory.setItem(j, ItemStack.of(CompoundNBT));
            }
        } else {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initCatfishInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.catfishInventory.setItem(j, ItemStack.of(CompoundNBT));
            }
        }
        this.setSwallowedEntityType(compound.getString("ContainedEntityType"));
        if (!compound.getCompound("ContainedData").isEmpty()) {
            this.setSwallowedData(compound.getCompound("ContainedData"));
        }
        this.setHasSwallowedEntity(compound.getBoolean("HasSwallowedEntity"));
    }

    private EntityDimensions getDimsForCatfish() {
        if (this.getCatfishSize() == 2) {
            return LARGE_SIZE;
        } else if (this.getCatfishSize() == 1) {
            return MEDIUM_SIZE;
        } else {
            return SMALL_SIZE;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setCatfishSize(random.nextFloat() < 0.35F ? 1 : 0);
        Holder<Biome> holder = worldIn.getBiome(this.blockPosition());
        if(random.nextFloat() < 0.1F && (holder != null && holder.is(AMTagRegistry.SPAWNS_HUGE_CATFISH) || reason == MobSpawnType.SPAWN_EGG)){
            this.setCatfishSize(2);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
    }

    @Override
    public void containerChanged(Container p_18983_) {

    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        if (this.getCatfishSize() != 2 && !isFull() && this.catfishInventory.addItem(itemstack).isEmpty()) {
            this.onItemPickup(itemEntity);
            this.take(itemEntity, itemstack.getCount());
            itemEntity.discard();
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public boolean isFull() {
        if (this.getCatfishSize() == 2) {
            return this.hasSwallowedEntity();
        } else {
            for (int i = 0; i < this.catfishInventory.getContainerSize(); i++) {
                if (this.catfishInventory.getItem(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    public float getVoicePitch() {
        float f = (3 - this.getCatfishSize()) * 0.33F;
        return (float) (super.getVoicePitch() * Math.sqrt(f) * 1.2F);
    }

    public boolean swallowEntity(Entity entity) {
        if (this.getCatfishSize() == 2 && entity instanceof Mob mob) {
            this.setHasSwallowedEntity(true);
            ResourceLocation mobtype = ForgeRegistries.ENTITIES.getKey(mob.getType());
            if(mobtype != null){
                this.setSwallowedEntityType(mobtype.toString());
            }
            CompoundTag tag = new CompoundTag();
            mob.addAdditionalSaveData(tag);
            this.setSwallowedData(tag);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            return true;
        }
        if (this.getCatfishSize() < 2 && entity instanceof ItemEntity item) {
            this.pickUpItem(item);
        }
        return false;
    }

    public boolean canSpit() {
        return this.getCatfishSize() == 2 ? this.hasSwallowedEntity() : !this.catfishInventory.isEmpty();
    }

    public void spit() {
        this.setSpitTime(10);
        this.eatCooldown = 60 + random.nextInt(60);
        if (this.getCatfishSize() == 2) {
            if (this.hasSwallowedEntity()) {
                EntityType type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.getSwallowedEntityType()));
                if (type != null) {
                    Entity entity = type.create(level);
                    if (entity instanceof LivingEntity alive) {
                        alive.readAdditionalSaveData(this.getSwallowedData());
                        alive.setHealth(Math.max(2, alive.getMaxHealth() * 0.25F));
                        alive.setYRot(random.nextFloat() * 360 - 180);
                        alive.setPos(this.getMouthVec());
                        if (level.addFreshEntity(alive)) {
                            this.setHasSwallowedEntity(false);
                            this.setSwallowedEntityType("minecraft:pig");
                            this.setSwallowedData(new CompoundTag());
                        }
                    }
                }
            }
        } else {
            ItemStack itemStack = ItemStack.EMPTY;
            int index = -1;
            for (int i = 0; i < this.catfishInventory.getContainerSize(); i++) {
                if (!this.catfishInventory.getItem(i).isEmpty()) {
                    itemStack = this.catfishInventory.getItem(i);
                    index = i;
                    break;
                }
            }
            if (!itemStack.isEmpty()) {
                Vec3 vec3 = this.getMouthVec();
                Vec3 vec32 = vec3.subtract(position()).normalize().scale(0.14F);
                ItemEntity item = new ItemEntity(level, vec3.x, vec3.y, vec3.z, itemStack, vec32.x, vec32.y, vec32.z);
                item.setDeltaMovement(Vec3.ZERO);
                item.setPickUpDelay(30);
                if (level.addFreshEntity(item)) {
                    this.catfishInventory.setItem(index, ItemStack.EMPTY);
                }
            }
        }
    }

    private Vec3 getMouthVec(){
        Vec3 vec3 = new Vec3(0, this.getBbHeight() * 0.25F, this.getBbWidth() * 0.8F).xRot(this.getXRot() * ((float)Math.PI / 180F)).yRot(-this.getYRot() * ((float)Math.PI / 180F));
        return this.position().add(vec3);
    }

    private boolean isFood(Entity entity) {
        if (this.getCatfishSize() == 2) {
            return entity instanceof Mob && !(entity instanceof EntityCatfish) && entity.getBbHeight() <= 1.0F;
        } else {
            return entity instanceof ItemEntity && ((ItemEntity) entity).getAge() > 35;
        }
    }

    private boolean canSeeBlock(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level.clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COD_HURT;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    private class TargetFoodGoal extends Goal {
        private final EntityCatfish catfish;
        private Entity food;
        private int executionCooldown = 50;

        public TargetFoodGoal(EntityCatfish catfish) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.catfish = catfish;
        }

        @Override
        public boolean canUse() {
            if (!catfish.isInWaterOrBubble() || catfish.eatCooldown > 0) {
                return false;
            }
            if (executionCooldown > 0) {
                executionCooldown--;
            } else {
                executionCooldown = 50 + random.nextInt(50);
                if (!this.catfish.isFull()) {
                    List<Entity> list = catfish.level.getEntitiesOfClass(Entity.class, catfish.getBoundingBox().inflate(8, 8, 8), EntitySelector.NO_SPECTATORS.and(entity -> entity != catfish && catfish.isFood(entity)));
                    list.sort(Comparator.comparingDouble(catfish::distanceToSqr));
                    if (!list.isEmpty()) {
                        food = list.get(0);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return food != null && food.isAlive() && !this.catfish.isFull();
        }

        public void stop() {
            executionCooldown = 5;
        }

        @Override
        public void tick() {
            catfish.getNavigation().moveTo(food.getX(), food.getY(0.5F), food.getZ(), 1.0F);
            float eatDist = catfish.getBbWidth() * 0.65F + food.getBbWidth();
            if (catfish.distanceTo(food) < eatDist + 3 && catfish.hasLineOfSight(food)) {
                Vec3 delta = catfish.getMouthVec().subtract(food.position()).normalize().scale(0.1F);
                food.setDeltaMovement(food.getDeltaMovement().add(delta));
                if (catfish.distanceTo(food) < eatDist) {
                    if(food instanceof Player){
                        food.hurt(DamageSource.mobAttack(catfish), 12000);
                    }else if (catfish.swallowEntity(food)) {
                        catfish.playSound(SoundEvents.GENERIC_EAT, catfish.getSoundVolume(), catfish.getVoicePitch());
                        food.discard();
                    }
                }
            }
        }
    }

    private class FascinateLanternGoal extends Goal {
        private final int searchLength;
        private final int verticalSearchRange;
        protected BlockPos destinationBlock;
        private EntityCatfish fish;
        private int runDelay = 70;
        private int chillTime = 0;
        private int maxChillTime = 200;

        private FascinateLanternGoal(EntityCatfish fish) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.fish = fish;
            searchLength = 16;
            verticalSearchRange = 6;
        }

        public boolean canContinueToUse() {
            return destinationBlock != null && isSeaLantern(fish.level, destinationBlock.mutable()) && isCloseToLantern(16) && !fish.isFull();
        }

        public boolean isCloseToLantern(double dist) {
            return destinationBlock == null || fish.distanceToSqr(Vec3.atCenterOf(destinationBlock)) < dist * dist;
        }

        @Override
        public boolean canUse() {
            if (!fish.isInWaterOrBubble()) {
                return false;
            }
            if (this.runDelay > 0) {
                --this.runDelay;
                return false;
            } else {
                this.runDelay = 70 + fish.random.nextInt(70);
                return !this.fish.isFull() && this.searchForDestination();
            }
        }

        public void start(){
            chillTime = 0;
            maxChillTime = 10 + random.nextInt(20);
        }

        public void tick() {
            Vec3 vec = Vec3.atCenterOf(destinationBlock);
            if (vec != null) {
                fish.getNavigation().moveTo(vec.x, vec.y, vec.z, 1F);
                if(fish.distanceToSqr(vec) < 1F + fish.getBbWidth() * 0.6F){
                    Vec3 face = vec.subtract(fish.position());
                    fish.setDeltaMovement(fish.getDeltaMovement().add(face.normalize().scale(0.1F)));
                    if(chillTime++ > maxChillTime){
                        destinationBlock = null;
                    }
                }
            }
        }

        public void stop() {
            destinationBlock = null;
        }

        protected boolean searchForDestination() {
            int lvt_1_1_ = this.searchLength;
            int lvt_2_1_ = this.verticalSearchRange;
            BlockPos lvt_3_1_ = fish.blockPosition();
            BlockPos.MutableBlockPos lvt_4_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
                for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; ++lvt_6_1_) {
                    for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                        for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0; lvt_8_1_ <= lvt_6_1_; lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_) {
                            lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                            if (this.isSeaLantern(fish.level, lvt_4_1_) && fish.canSeeBlock(lvt_4_1_)) {
                                this.destinationBlock = lvt_4_1_;
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        private boolean isSeaLantern(Level world, BlockPos.MutableBlockPos pos) {
            return world.getBlockState(pos).is(Blocks.SEA_LANTERN);
        }
    }
}
