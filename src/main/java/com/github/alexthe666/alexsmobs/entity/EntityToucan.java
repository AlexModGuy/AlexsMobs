package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAITargetDroppedItems;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class EntityToucan extends Animal implements ITargetsDroppedItems {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PECK_TICK = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GOLDEN_TIME = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENCHANTED = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockState>> SAPLING_STATE = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Integer> SAPLING_TIME = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
    private static final HashMap<String, String> FEEDING_DATA = new HashMap<>();
    private static final List<ItemStack> FEEDING_STACKS = new ArrayList<>();
    private static boolean initFeedingData = false;
    public float prevFlyProgress;
    public float flyProgress;
    public float prevPeckProgress;
    public float peckProgress;
    private boolean isLandNavigator;
    private int timeFlying;
    private int heldItemTime;
    private boolean aiItemFlag;

    protected EntityToucan(EntityType type, Level worldIn) {
        super(type, worldIn);
        initFeedingData();
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        switchNavigator(true);
    }

    public static boolean canToucanSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return true;
    }

    private static void initFeedingData() {
        if (!initFeedingData || FEEDING_DATA.isEmpty()) {
            initFeedingData = true;
            for (String str : AMConfig.toucanFruitMatches) {
                String[] split = str.split("\\|");
                if (split.length >= 2) {
                    FEEDING_DATA.put(split[0], split[1]);
                    FEEDING_STACKS.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0]))));
                }
            }
        }
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.TOUCAN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TOUCAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TOUCAN_HURT.get();
    }

    public boolean checkSpawnObstruction(LevelReader p_29005_) {
        if (p_29005_.isUnobstructed(this) && !p_29005_.containsAnyLiquid(this.getBoundingBox())) {
            BlockPos blockpos = this.blockPosition();
            if (blockpos.getY() < p_29005_.getSeaLevel()) {
                return false;
            }
            BlockState blockstate2 = p_29005_.getBlockState(blockpos.below());
            return blockstate2.is(Blocks.GRASS_BLOCK) || blockstate2.is(BlockTags.LEAVES);
        }
        return false;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.toucanSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    @Nullable
    private BlockState getSaplingFor(ItemStack stack) {
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (!stack.isEmpty() && name != null && FEEDING_DATA.containsKey(name.toString())) {
            String str = FEEDING_DATA.get(name.toString());
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(str));
            if (block != null) {
                return block.defaultBlockState();
            }
        }
        return null;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (getSaplingFor(itemstack) != null && this.getSaplingTime() <= 0 && this.getMainHandItem().isEmpty()) {
            peck();
            ItemStack duplicate = itemstack.copy();
            duplicate.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        } else {
            return type;
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        initFeedingData();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3D));
        this.goalSelector.addGoal(2, new AIPlantTrees());
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(FEEDING_STACKS.stream()), false) {
            public boolean canUse() {
                return !EntityToucan.this.aiItemFlag && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new AIWanderIdle());
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new FlyingAITargetDroppedItems(this, false, false, 15, 16));
    }

    @Override
    public void setItemFlag(boolean itemAIFlag) {
        aiItemFlag = itemAIFlag;
    }


    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.EGG;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SAPLING_STATE, Optional.empty());
        this.entityData.define(FLYING, false);
        this.entityData.define(PECK_TICK, 0);
        this.entityData.define(VARIANT, 0);
        this.entityData.define(GOLDEN_TIME, 0);
        this.entityData.define(SAPLING_TIME, 0);
        this.entityData.define(ENCHANTED, false);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    public void tick() {
        super.tick();
        prevFlyProgress = flyProgress;
        prevPeckProgress = peckProgress;
        if (this.getGoldenTime() > 0 && !this.level().isClientSide) {
            this.setGoldenTime(this.getGoldenTime() - 1);
        }

        final boolean flying = isFlying();
        if (flying) {
            if (flyProgress < 5F)
                flyProgress++;
        } else {
            if (flyProgress > 0F)
                flyProgress--;
        }
        if (!this.level().isClientSide) {
            if (flying) {
                if (this.isLandNavigator)
                    switchNavigator(false);
            } else {
                if (!this.isLandNavigator)
                    switchNavigator(true);
            }
            if (flying) {
                this.setNoGravity(true);
                if (this.isFlying() && !this.onGround()) {
                    if (!this.isInWaterOrBubble()) {
                        this.setDeltaMovement(this.getDeltaMovement().multiply(1F, 0.6F, 1F));
                    }
                }
                this.timeFlying++;
            } else {
                this.setNoGravity(false);
                this.timeFlying = 0;
            }
        }
        if (this.entityData.get(PECK_TICK) > 0) {
            this.entityData.set(PECK_TICK, this.entityData.get(PECK_TICK) - 1);
            if (peckProgress < 5F) {
                peckProgress++;
            }
        } else {
            if (peckProgress > 0F) {
                peckProgress--;
            }
        }
        if (peckProgress >= 5 && this.getMainHandItem().isEmpty() && this.getSaplingState() != null) {
            peckBlockEffect();
        }
        if (!this.getMainHandItem().isEmpty()) {
            heldItemTime++;
            if (heldItemTime > 10 && canTargetItem(this.getMainHandItem())) {
                heldItemTime = 0;
                this.heal(4);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                if (this.getMainHandItem().hasCraftingRemainingItem()) {
                    this.spawnAtLocation(this.getMainHandItem().getCraftingRemainingItem());
                }
                final var mainHandItem = this.getMainHandItem().getItem();
                if (mainHandItem == Items.GOLDEN_APPLE) {
                    this.setGoldenTime(12000);
                } else if (mainHandItem == Items.ENCHANTED_GOLDEN_APPLE) {
                    this.setGoldenTime(-1);
                    this.setEnchanted(true);
                }
                this.setSaplingState(getSaplingFor(this.getMainHandItem()));
                eatItemEffect(this.getMainHandItem());
                this.getMainHandItem().shrink(1);
            }
        } else {
            heldItemTime = 0;
        }
        if (this.isFlying() && this.getFeetBlockState().is(Blocks.VINE)) {
            float f = this.getYRot() * Mth.DEG_TO_RAD;
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f) * 0.2F, 0.4F, Mth.cos(f) * 0.2F));
        }
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
        if (flying && isBaby()) {
            return;
        }
        this.entityData.set(FLYING, flying);
    }

    public void peck() {
        if (this.peckProgress == 0) {
            this.entityData.set(PECK_TICK, 7);
        }
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        final float radius = 7 + radiusAdd + this.getRandom().nextInt(8);
        final float neg = this.getRandom().nextBoolean() ? 1 : -1;
        final float renderYawOffset = this.yBodyRot;
        final float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        final double extraX = radius * Mth.sin((float) (Math.PI + angle));
        final double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), 0, (int) (fleePos.z() + extraZ));
        BlockPos ground = getToucanGround(radialPos);
        final int distFromGround = (int) this.getY() - ground.getY();
        final int flightHeight = 8 + this.getRandom().nextInt(4);
        final int j = this.getRandom().nextInt(6) + 18;

        BlockPos newPos = ground.above(distFromGround > 9 ? flightHeight : j);
        if (level().getBlockState(ground).is(BlockTags.LEAVES)) {
            newPos = ground.above(1 + this.getRandom().nextInt(3));
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    public BlockPos getToucanGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() < 320 && !level().getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > -64 && !level().getBlockState(position).isSolid() && level().getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return position;
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        final float radius = 10 + this.getRandom().nextInt(15);
        final float neg = this.getRandom().nextBoolean() ? 1 : -1;
        final float renderYawOffset = this.yBodyRot;
        final float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        final double extraX = radius * Mth.sin((float) (Math.PI + angle));
        final double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getToucanGround(radialPos);
        if (ground.getY() == -64) {
            return this.position();
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -62 && !level().getBlockState(ground).isSolid()) {
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

        return this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 0.6F, false, true);
            this.navigation = new DirectPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        BlockState blockstate = this.getSaplingState();
        if (blockstate != null) {
            compound.put("SaplingState", NbtUtils.writeBlockState(blockstate));
        }
        compound.putInt("Variant", this.getVariant());
        compound.putInt("GoldenTime", this.getGoldenTime());
        compound.putBoolean("Enchanted", this.isEnchanted());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        BlockState blockstate = null;
        if (compound.contains("SaplingState", 10)) {
            blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("SaplingState"));
            if (blockstate.isAir()) {
                blockstate = null;
            }
        }
        this.setSaplingState(blockstate);
        this.setVariant(compound.getInt("Variant"));
        this.setGoldenTime(compound.getInt("GoldenTime"));
        this.setEnchanted(compound.getBoolean("Enchanted"));
    }

    public boolean isSam() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && s.toLowerCase().contains("sam");
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public int getSaplingTime() {
        return this.entityData.get(SAPLING_TIME).intValue();
    }

    public void setSaplingTime(int time) {
        this.entityData.set(SAPLING_TIME, Integer.valueOf(time));
    }

    public boolean isGolden() {
        return this.getGoldenTime() > 0 || this.getGoldenTime() == -1 || this.isEnchanted();
    }

    public int getGoldenTime() {
        return this.entityData.get(GOLDEN_TIME).intValue();
    }

    public void setGoldenTime(int goldenTime) {
        this.entityData.set(GOLDEN_TIME, Integer.valueOf(goldenTime));
    }

    public boolean isEnchanted() {
        return this.entityData.get(ENCHANTED).booleanValue();
    }

    public void setEnchanted(boolean enchanted) {
        this.entityData.set(ENCHANTED, enchanted);
    }

    @Nullable
    public BlockState getSaplingState() {
        return this.entityData.get(SAPLING_STATE).orElse(null);
    }

    public void setSaplingState(@Nullable BlockState state) {
        this.entityData.set(SAPLING_STATE, Optional.ofNullable(state));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setVariant(this.getRandom().nextInt(4));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        EntityToucan toucan = AMEntityRegistry.TOUCAN.get().create(level());
        toucan.setVariant(this.getVariant());
        return toucan;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getBlockState(pos).is(BlockTags.LEAVES) ? 10.0F : super.getWalkTargetValue(pos, worldIn);
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -62 && level().isEmptyBlock(position)) {
            position = position.below();
        }
        return !level().getFluidState(position).isEmpty() || level().getBlockState(position).is(Blocks.VINE) || position.getY() <= 0;
    }

    private boolean isOverLeaves() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -62 && level().isEmptyBlock(position)) {
            position = position.below();
        }
        return level().getBlockState(position).is(BlockTags.LEAVES) || level().getBlockState(position).is(Blocks.VINE);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return this.getSaplingTime() <= 0 && getSaplingFor(stack) != null;
    }

    private void peckBlockEffect() {
        BlockState beneath = this.getBlockStateOn();
        if (this.level().isClientSide && !beneath.isAir() && beneath.getFluidState().isEmpty()) {
            for (int i = 0; i < 2 + random.nextInt(2); i++) {
                final double d2 = this.random.nextGaussian() * 0.02D;
                final double d0 = this.random.nextGaussian() * 0.02D;
                final double d1 = this.random.nextGaussian() * 0.02D;
                final float radius = this.getBbWidth() * 0.65F;
                final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
                final double extraX = radius * Mth.sin((float) (Math.PI + angle));
                final double extraZ = radius * Mth.cos(angle);
                ParticleOptions data = new BlockParticleOption(ParticleTypes.BLOCK, beneath);
                this.level().addParticle(data, this.getX() + extraX, this.getY() + 0.1F, this.getZ() + extraZ, d0, d1, d2);
            }
        }
    }

    private void eatItemEffect(ItemStack heldItemMainhand) {
        for (int i = 0; i < 2 + random.nextInt(2); i++) {
            final double d2 = this.random.nextGaussian() * 0.02D;
            final double d0 = this.random.nextGaussian() * 0.02D;
            final double d1 = this.random.nextGaussian() * 0.02D;
            final float radius = this.getBbWidth() * 0.65F;
            final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
            final double extraX = radius * Mth.sin((float) (Math.PI + angle));
            final double extraZ = radius * Mth.cos(angle);
            ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
            if (heldItemMainhand.getItem() instanceof BlockItem) {
                data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) heldItemMainhand.getItem()).getBlock().defaultBlockState());
            }
            this.level().addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
        }
    }


    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        peck();
        this.setFlying(true);
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
    }

    private boolean hasLineOfSightSapling(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }

    private class AIWanderIdle extends Goal {
        protected final EntityToucan toucan;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWanderIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.toucan = EntityToucan.this;
        }

        @Override
        public boolean canUse() {
            if (this.toucan.isVehicle() || toucan.getSaplingState() != null || EntityToucan.this.aiItemFlag || (toucan.getTarget() != null && toucan.getTarget().isAlive()) || this.toucan.isPassenger()) {
                return false;
            } else {
                if (this.toucan.getRandom().nextInt(45) != 0 && !toucan.isFlying()) {
                    return false;
                }
                if (this.toucan.onGround()) {
                    this.flightTarget = random.nextInt(6) == 0;
                } else {
                    this.flightTarget = random.nextInt(5) != 0 && toucan.timeFlying < 200;
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
                toucan.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.toucan.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && toucan.onGround()) {
                toucan.setFlying(false);
            }
            if (isFlying() && toucan.onGround() && toucan.timeFlying > 10) {
                toucan.setFlying(false);
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = toucan.position();
            if (toucan.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (toucan.timeFlying > 50 && toucan.isOverLeaves() && !toucan.onGround()) {
                    return toucan.getBlockGrounding(vector3d);
                } else if (toucan.timeFlying < 200 || toucan.isOverWaterOrVoid()) {
                    return toucan.getBlockInViewAway(vector3d, 0);
                } else {
                    return toucan.getBlockGrounding(vector3d);
                }
            } else if (!toucan.onGround()) {
                return toucan.getBlockGrounding(vector3d);
            } else {
                if (this.toucan.isOverLeaves()) {
                    for (int i = 0; i < 15; i++) {
                        BlockPos pos = this.toucan.blockPosition().offset(random.nextInt(16) - 8, random.nextInt(8) - 4, random.nextInt(16) - 8);
                        if (!toucan.level().getBlockState(pos.above()).isSolid() && toucan.level().getBlockState(pos).isSolid() && toucan.getWalkTargetValue(pos) >= 0.0F) {
                            return Vec3.atBottomCenterOf(pos);
                        }
                    }
                }
                return LandRandomPos.getPos(this.toucan, 16, 7);
            }
        }

        public boolean canContinueToUse() {
            if (toucan.aiItemFlag) {
                return false;
            }
            if (flightTarget) {
                return toucan.isFlying() && toucan.distanceToSqr(x, y, z) > 2F;
            } else {
                return (!this.toucan.getNavigation().isDone()) && !this.toucan.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                toucan.setFlying(true);
                toucan.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.toucan.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.toucan.getNavigation().stop();
            super.stop();
        }

    }

    private class AIPlantTrees extends Goal {
        protected final EntityToucan toucan;
        protected BlockPos pos;
        private int runCooldown = 0;
        private int encircleTime = 0;
        private int plantTime = 0;
        private boolean clockwise;

        public AIPlantTrees() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.toucan = EntityToucan.this;
        }

        @Override
        public boolean canUse() {
            if (toucan.getSaplingState() != null) {
                if (runCooldown-- <= 0) {
                    BlockPos target = getSaplingPlantPos();
                    runCooldown = resetCooldown();
                    if (target != null) {
                        pos = target;
                        clockwise = random.nextBoolean();
                        encircleTime = (toucan.isGolden() ? 20 : 100) + random.nextInt(100);
                        return true;
                    }
                }
            }
            return false;
        }

        private int resetCooldown() {
            return toucan.isGolden() && !toucan.isEnchanted() ? 50 + random.nextInt(40) : 200 + random.nextInt(200);
        }

        public void tick() {
            toucan.aiItemFlag = true;
            double up = 3.0;
            if (encircleTime > 0) {
                encircleTime--;
            }
            if (isWithinXZDist(pos, toucan.position(), 5) && encircleTime <= 0) {
                up = 0.0D;
            }
            if (toucan.distanceToSqr(Vec3.atCenterOf(pos)) < 3.0D) {
                toucan.setFlying(false);
                toucan.peck();
                plantTime++;
                if (plantTime > 60) {
                    BlockState state = toucan.getSaplingState();
                    if (state != null) {
                        if (state.canSurvive(toucan.level(), pos) && toucan.level().getBlockState(pos).canBeReplaced()) {
                            toucan.level().setBlockAndUpdate(pos, state);
                            if (!toucan.isEnchanted()) {
                                toucan.setSaplingState(null);
                            }
                        }
                    }
                    stop();
                }
            } else {
                BlockPos moveTo = pos;

                if (encircleTime > 0) {
                    moveTo = getVultureCirclePos(pos, 3, up);
                }
                if (moveTo != null) {
                    if (encircleTime <= 0 && !toucan.hasLineOfSightSapling(pos)) {
                        toucan.setFlying(false);
                        toucan.getNavigation().moveTo(moveTo.getX() + 0.5F, moveTo.getY() + up + 0.5F, moveTo.getZ() + 0.5F, 1F);
                    } else {
                        toucan.setFlying(true);
                        toucan.getMoveControl().setWantedPosition(moveTo.getX() + 0.5F, moveTo.getY() + up + 0.5F, moveTo.getZ() + 0.5F, 1F);
                    }
                }
            }
        }

        public BlockPos getVultureCirclePos(BlockPos target, float circleDistance, double yLevel) {
            final float angle = (Maths.STARTING_ANGLE * 8 * (clockwise ? -encircleTime : encircleTime));
            final double extraX = circleDistance * Mth.sin((angle));
            final double extraZ = circleDistance * Mth.cos(angle);
            BlockPos pos;
            pos = new BlockPos((int) (target.getX() + 0.5F + extraX), (int) (target.getY() + 1 + yLevel), (int) (target.getZ() + 0.5F + extraZ));
            if (toucan.level().isEmptyBlock(pos)) {
                return pos;
            }
            return null;
        }

        public void stop() {
            toucan.aiItemFlag = false;
            pos = null;
            plantTime = 0;
            encircleTime = 0;
        }

        public boolean canContinueToUse() {
            return pos != null && toucan.getSaplingState() != null;
        }

        private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
            return blockpos.distSqr(new BlockPos((int) positionVec.x(), blockpos.getY(), (int) positionVec.z())) < distance * distance;
        }

        private BlockPos getSaplingPlantPos() {
            BlockState state = toucan.getSaplingState();
            if (state != null) {
                for (int i = 0; i < 15; i++) {
                    BlockPos pos = this.toucan.blockPosition().offset(random.nextInt(10) - 8, random.nextInt(8) - 4, random.nextInt(16) - 8);
                    if (state.canSurvive(toucan.level(), pos) && toucan.level().isEmptyBlock(pos.above()) && toucan.hasLineOfSightSapling(pos)) {
                        return pos;
                    }
                }
            }
            return null;
        }
    }
}
