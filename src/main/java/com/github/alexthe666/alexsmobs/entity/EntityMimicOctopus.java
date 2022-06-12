package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.*;
import net.minecraft.util.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class EntityMimicOctopus extends TamableAnimal implements ISemiAquatic, IFollower, Bucketable {

    private static final EntityDataAccessor<Boolean> STOP_CHANGE = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> UPGRADED = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MIMIC_ORDINAL = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PREV_MIMIC_ORDINAL = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockState>> MIMICKED_BLOCK = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Optional<BlockState>> PREV_MIMICKED_BLOCK = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LAST_SCARED_MOB_ID = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> UPGRADED_LASER_ENTITY_ID = SynchedEntityData.defineId(EntityMimicOctopus.class, EntityDataSerializers.INT);
    public MimicState localMimicState = MimicState.OVERLAY;
    public float transProgress = 0F;
    public float prevTransProgress = 0F;
    public float colorShiftProgress = 0F;
    public float prevColorShiftProgress = 0F;
    public float groundProgress = 5F;
    public float prevGroundProgress = 0F;
    public float sitProgress = 0F;
    public float prevSitProgress = 0F;
    private boolean isLandNavigator;
    private int moistureAttackTime = 0;
    private int camoCooldown = 120 + random.nextInt(1200);
    private int mimicCooldown = 0;
    private int stopMimicCooldown = -1;
    private int fishFeedings;
    private int mimicreamFeedings;
    private int exclaimTime = 0;
    private BlockState localMimic;
    private LivingEntity laserTargetEntity;
    private int guardianLaserTime;

    protected EntityMimicOctopus(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public static boolean canMimicOctopusSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        BlockPos downPos = pos;
        while (downPos.getY() > 1 && !worldIn.getFluidState(downPos).isEmpty()) {
            downPos = downPos.below();
        }
        boolean spawnBlock = worldIn.getBlockState(downPos).is(AMTagRegistry.MIMIC_OCTOPUS_SPAWNS);
        return spawnBlock && downPos.getY() < worldIn.getSeaLevel() + 1;
    }

    public static MimicState getStateForItem(ItemStack stack) {
        if (stack.is(AMTagRegistry.MIMIC_OCTOPUS_CREEPER_ITEMS)) {
            return MimicState.CREEPER;
        }
        if (stack.is(AMTagRegistry.MIMIC_OCTOPUS_GUARDIAN_ITEMS)) {
            return MimicState.GUARDIAN;
        }
        if (stack.is(AMTagRegistry.MIMIC_OCTOPUS_PUFFERFISH_ITEMS)) {
            return MimicState.PUFFERFISH;
        }
        return null;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.MIMIC_OCTOPUS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MIMIC_OCTOPUS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MIMIC_OCTOPUS_HURT.get();
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mimicOctopusSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.entityData.set(PREV_MIMIC_ORDINAL, 0);
        this.setMimickedBlock(null);
        this.setMimicState(MimicState.OVERLAY);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(MIMIC_ORDINAL, compound.getInt("MimicState"));
        this.setUpgraded(compound.getBoolean("Upgraded"));
        this.setOrderedToSit(compound.getBoolean("Sitting"));
        this.setStopChange(compound.getBoolean("StopChange"));
        this.setCommand(compound.getInt("OctoCommand"));
        this.setMoistness(compound.getInt("Moistness"));
        this.setFromBucket(compound.getBoolean("FromBucket"));
        BlockState blockstate = null;
        if (compound.contains("MimickedBlockState", 10)) {
            blockstate = NbtUtils.readBlockState(compound.getCompound("MimickedBlockState"));
            if (blockstate.isAir()) {
                blockstate = null;
            }
        }
        this.setMimickedBlock(blockstate);
        this.camoCooldown = compound.getInt("CamoCooldown");
        this.mimicCooldown = compound.getInt("MimicCooldown");
        this.stopMimicCooldown = compound.getInt("StopMimicCooldown");
        this.fishFeedings = compound.getInt("FishFeedings");
        this.mimicreamFeedings = compound.getInt("MimicreamFeedings");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MimicState", this.getMimicState().ordinal());
        compound.putBoolean("Upgraded", this.isUpgraded());
        compound.putBoolean("Sitting", this.isSitting());
        compound.putInt("OctoCommand", this.getCommand());
        compound.putInt("Moistness", this.getMoistness());
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putBoolean("StopChange", this.isStopChange());
        BlockState blockstate = this.getMimickedBlock();
        if (blockstate != null) {
            compound.put("MimickedBlockState", NbtUtils.writeBlockState(blockstate));
        }
        compound.putInt("CamoCooldown", this.camoCooldown);
        compound.putInt("MimicCooldown", this.mimicCooldown);
        compound.putInt("StopMimicCooldown", this.stopMimicCooldown);
        compound.putInt("FishFeedings", this.fishFeedings);
        compound.putInt("MimicreamFeedings", this.mimicreamFeedings);
    }

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.MIMIC_OCTOPUS_BUCKET.get());
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
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.put("MimicOctopusData", platTag);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("MimicOctopusData")) {
            this.readAdditionalSaveData(compound.getCompound("MimicOctopusData"));
        }
        this.setMoistness(60000);
    }

    protected float getJumpPower() {
        return super.getJumpPower() * (this.isInWaterOrBubble() ? 1.3F : 1F);
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }
        return super.isAlliedTo(entityIn);
    }

    public boolean isPushedByFluid() {
        return false;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AIAttack());
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwner(this, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(AMItemRegistry.LOBSTER_TAIL.get(), AMItemRegistry.COOKED_LOBSTER_TAIL.get(), Items.TROPICAL_FISH), false) {
            @Override
            public void tick() {
                EntityMimicOctopus.this.setMimickedBlock(null);
                super.tick();
                EntityMimicOctopus.this.camoCooldown = 40;
                EntityMimicOctopus.this.stopMimicCooldown = 40;
            }
        });
        this.goalSelector.addGoal(5, new AIFlee());
        this.goalSelector.addGoal(7, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new AIMimicNearbyMobs());
        this.goalSelector.addGoal(9, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(10, new AISwim());
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return EntityMimicOctopus.this.isTame() && super.canUse();
            }
        });
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && (item == Items.TROPICAL_FISH);
    }

    public boolean isActiveCamo() {
        return this.getMimicState() == MimicState.OVERLAY && this.getMimickedBlock() != null;
    }

    public double getVisibilityPercent(@Nullable Entity lookingEntity) {
        if (isActiveCamo()) {
            return super.getVisibilityPercent(lookingEntity) * 0.1F;
        } else {
            return super.getVisibilityPercent(lookingEntity);
        }
    }

    @Override
    @Nonnull
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        MimicState readState = getStateForItem(itemstack);
        InteractionResult type = super.mobInteract(player, hand);
        if (readState != null && this.isTame()) {
            if (mimicCooldown == 0) {
                this.setMimicState(readState);
                mimicCooldown = 20;
                stopMimicCooldown = isUpgraded() ? 120 : 1200;
                camoCooldown = stopMimicCooldown;
                this.setMimickedBlock(null);
            }
            return InteractionResult.SUCCESS;
        }
        if (isTame() && (item == Items.INK_SAC)) {
            this.setStopChange(!this.isStopChange());
            if (this.isStopChange()) {
                this.makeEatingParticles(itemstack);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 6);
                this.mimicEnvironment();
            }
            return InteractionResult.SUCCESS;
        }
        if (!isTame() && (item == AMItemRegistry.LOBSTER_TAIL.get() || item == AMItemRegistry.COOKED_LOBSTER_TAIL.get())) {
            this.usePlayerItem(player, hand, itemstack);
            this.playSound(SoundEvents.DOLPHIN_EAT, this.getSoundVolume(), this.getVoicePitch());
            fishFeedings++;
            if (this.getMimicState() == MimicState.OVERLAY && this.getMimickedBlock() == null) {
                if (fishFeedings > 5 && getRandom().nextInt(2) == 0 || fishFeedings > 8) {
                    this.tame(player);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (isTame() && (item == AMItemRegistry.LOBSTER_TAIL.get() || item == AMItemRegistry.COOKED_LOBSTER_TAIL.get())) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.usePlayerItem(player, hand, itemstack);
                this.playSound(SoundEvents.DOLPHIN_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.heal(5);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (this.isTame()) {
            Optional<InteractionResult> result = Bucketable.bucketMobPickup(player, hand, this);
            if (result.isPresent()) {
                return result.get();
            }
        }
        if (this.isTame() && item == Items.SLIME_BALL && this.getMoistness() < 24000) {
            this.setMoistness(48000);
            this.makeEatingParticles(itemstack);
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }
        if (this.isTame() && !this.isUpgraded() && item == AMItemRegistry.MIMICREAM.get()) {
            mimicreamFeedings++;
            if (mimicreamFeedings > 5 || mimicreamFeedings > 2 && random.nextInt(2) == 0) {
                this.level.broadcastEntityEvent(this, (byte) 46);
                this.setUpgraded(true);
                this.setMimicState(MimicState.MIMICUBE);
                this.setStopChange(false);
                this.setMimickedBlock(null);
                this.stopMimicCooldown = 40;
            }
            this.makeEatingParticles(itemstack);
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player)) {
            if (player.isShiftKeyDown()) {
                if (this.getMainHandItem().isEmpty()) {
                    ItemStack cop = itemstack.copy();
                    cop.setCount(1);
                    this.setItemInHand(InteractionHand.MAIN_HAND, cop);
                    itemstack.shrink(1);
                    return InteractionResult.SUCCESS;
                } else {
                    this.spawnAtLocation(this.getMainHandItem().copy());
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    return InteractionResult.SUCCESS;
                }
            } else if (!isFood(itemstack)) {
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() == 3) {
                    this.setCommand(0);
                }
                player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.setOrderedToSit(true);
                    return InteractionResult.SUCCESS;
                } else {
                    this.setOrderedToSit(false);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return type;
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    private void makeEatingParticles(ItemStack item) {
        for (int i = 0; i < 6 + random.nextInt(3); i++) {
            double d2 = this.random.nextGaussian() * 0.02D;
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
        }
    }

    @Override
    public void calculateEntityAnimation(LivingEntity p_233629_1_, boolean p_233629_2_) {
        p_233629_1_.animationSpeedOld = p_233629_1_.animationSpeed;
        double d0 = p_233629_1_.getX() - p_233629_1_.xo;
        double d1 = p_233629_1_.getY() - p_233629_1_.yo;
        double d2 = p_233629_1_.getZ() - p_233629_1_.zo;
        float f = Mth.sqrt((float)(d0 * d0 + d1 * d1 + d2 * d2)) * (groundProgress < 2.5F ? 4.0F : 8.0F);
        if (f > 1.0F) {
            f = 1.0F;
        }

        p_233629_1_.animationSpeed += (f - p_233629_1_.animationSpeed) * 0.4F;
        p_233629_1_.animationPosition += p_233629_1_.animationSpeed;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AnimalSwimMoveControllerSink(this, 1.3F, 1);
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    public void tick() {
        super.tick();
        if (localMimic != this.getPrevMimickedBlock()) {
            localMimic = this.getPrevMimickedBlock();
            colorShiftProgress = 0.0F;
        }
        if (localMimicState != this.getPrevMimicState()) {
            localMimicState = this.getPrevMimicState();
            transProgress = 0.0F;
        }
        if (this.isInWater() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!this.isInWater() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        BlockPos pos = new BlockPos(this.getX(), this.getEyeY() - 1F, this.getZ());
        boolean ground = level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP) && this.getMimicState() != MimicState.GUARDIAN || !this.isInWaterOrBubble() || this.isSitting();
        this.prevTransProgress = transProgress;
        this.prevColorShiftProgress = colorShiftProgress;
        this.prevGroundProgress = groundProgress;
        this.prevSitProgress = sitProgress;
        if (this.getPrevMimicState() != this.getMimicState() && transProgress < 5.0F) {
            transProgress += 0.25F;
        }
        if (this.getPrevMimicState() == this.getMimicState() && transProgress > 0F) {
            transProgress -= 0.25F;
        }
        if (getPrevMimickedBlock() != this.getMimickedBlock() && colorShiftProgress < 5.0F) {
            colorShiftProgress += 0.25F;
        }
        if (getPrevMimickedBlock() == this.getMimickedBlock() && colorShiftProgress > 0F) {
            colorShiftProgress -= 0.25F;
        }
        if (ground && groundProgress < 5F) {
            groundProgress += 0.5F;
        }
        if (!ground && groundProgress > 0F) {
            groundProgress -= 0.5F;
        }
        if (isSitting() && sitProgress < 5F) {
            sitProgress += 0.5F;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress -= 0.5F;
        }
        if (this.isInWaterOrBubble()) {
            float f2 = (float) -((float) this.getDeltaMovement().y * 3 * (double) (180F / (float) Math.PI));
            this.setXRot(f2);
        }
        if (camoCooldown > 0) {
            camoCooldown--;
        }
        if (mimicCooldown > 0) {
            mimicCooldown--;
        }
        if (stopMimicCooldown > 0) {
            stopMimicCooldown--;
        }
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
        } else {
            if (this.isInWaterRainOrBubble() || this.getMainHandItem().getItem() == Items.WATER_BUCKET) {
                this.setMoistness(60000);
            } else {
                this.setMoistness(this.getMoistness() - 1);
                if (this.getMoistness() <= 0 && moistureAttackTime-- <= 0) {
                    this.setOrderedToSit(false);
                    this.hurt(DamageSource.DRY_OUT, random.nextInt(2) == 0 ? 1.0F : 0F);
                    moistureAttackTime = 20;
                }
            }
        }
        if (camoCooldown <= 0 && random.nextInt(300) == 0) {
            mimicEnvironment();
            camoCooldown = this.getRandom().nextInt(2200) + 200;
        }
        if ((this.getMimicState() != MimicState.OVERLAY || this.getMimickedBlock() != null) && stopMimicCooldown == 0 && !this.isStopChange()) {
            this.setMimicState(MimicState.OVERLAY);
            this.setMimickedBlock(null);
            stopMimicCooldown = -1;
        }
        if (level.isClientSide && exclaimTime > 0) {
            exclaimTime--;
            if (exclaimTime == 0) {
                Entity e = level.getEntity(this.entityData.get(LAST_SCARED_MOB_ID));
                if (e != null && transProgress >= 5.0F) {
                    double d2 = this.random.nextGaussian() * 0.1D;
                    double d0 = this.random.nextGaussian() * 0.1D;
                    double d1 = this.random.nextGaussian() * 0.1D;
                    this.level.addParticle(AMParticleRegistry.SHOCKED.get(), e.getX(), e.getEyeY() + e.getBbHeight() * 0.15F + (double) (this.random.nextFloat() * e.getBbHeight() * 0.15F), e.getZ(), d0, d1, d2);
                }
            }
        }

        if (this.hasGuardianLaser()) {
            if (this.guardianLaserTime < 30) {
                ++this.guardianLaserTime;
            }
            LivingEntity livingentity = this.getGuardianLaser();
            if (livingentity != null && this.isInWaterOrBubble()) {
                this.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
                this.getLookControl().tick();
                double d5 = this.getLaserAttackAnimationScale(0.0F);
                double d0 = livingentity.getX() - this.getX();
                double d1 = livingentity.getY(0.5D) - this.getEyeY();
                double d2 = livingentity.getZ() - this.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d0 = d0 / d3;
                d1 = d1 / d3;
                d2 = d2 / d3;
                double d4 = this.random.nextDouble();
                while (d4 < d3) {
                    d4 += 1.8D - d5 + this.random.nextDouble() * (1.7D - d5);
                    this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + d0 * d4, this.getEyeY() + d1 * d4, this.getZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
                }
                if (guardianLaserTime == 30) {
                    livingentity.hurt(DamageSource.mobAttack(this), 5);
                    guardianLaserTime = 0;
                    this.entityData.set(UPGRADED_LASER_ENTITY_ID, -1);
                }
            }
        }
        if (!level.isClientSide && tickCount % 40 == 0) {
            this.heal(2);
        }
    /*if(!world.isRemote){
            if(ticksExisted % 80 == 0){
                mimicEnvironment();
            }else if(ticksExisted % 40 == 0){
                this.setMimicState(MimicState.OVERLAY);
                this.setMimickedBlock(null);
            }
        }*/
    }

    public float getLaserAttackAnimationScale(float p_175477_1_) {
        return ((float) this.guardianLaserTime + p_175477_1_) / 30F;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 68) {
            if (exclaimTime == 0) {
                exclaimTime = 20;
            }
        } else if (id == 69) {
            this.creeperExplode();
        } else {
            super.handleEntityEvent(id);
        }
    }

    public void mimicEnvironment() {
        if (!this.isStopChange()) {
            BlockPos down = getPositionDown();
            if (!level.isEmptyBlock(down)) {
                this.setMimicState(MimicState.OVERLAY);
                this.setMimickedBlock(level.getBlockState(down));
            }
            stopMimicCooldown = this.getRandom().nextInt(2200);
        }
    }

    public int getMoistness() {
        return this.entityData.get(MOISTNESS);
    }

    public void setMoistness(int p_211137_1_) {
        this.entityData.set(MOISTNESS, p_211137_1_);
    }

    private BlockPos getPositionDown() {
        BlockPos pos = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
        while (pos.getY() > 1 && (level.isEmptyBlock(pos) || level.getBlockState(pos).getMaterial() == Material.WATER)) {
            pos = pos.below();
        }
        return pos;
    }

    public void travel(Vec3 travelVector) {
        if (this.isSitting()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
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

    public boolean isUpgraded() {
        return this.entityData.get(FROM_BUCKET).booleanValue();
    }

    public void setUpgraded(boolean sit) {
        this.entityData.set(FROM_BUCKET, Boolean.valueOf(sit));
    }

    public boolean isStopChange() {
        return this.entityData.get(STOP_CHANGE).booleanValue();
    }

    public void setStopChange(boolean sit) {
        this.entityData.set(STOP_CHANGE, Boolean.valueOf(sit));
    }

    public boolean hasGuardianLaser() {
        return this.entityData.get(UPGRADED_LASER_ENTITY_ID) != -1 && this.isUpgraded() && this.isInWaterOrBubble();
    }

    @Nullable
    public LivingEntity getGuardianLaser() {
        if (!this.hasGuardianLaser()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.laserTargetEntity != null) {
                return this.laserTargetEntity;
            } else {
                Entity lvt_1_1_ = this.level.getEntity(this.entityData.get(UPGRADED_LASER_ENTITY_ID));
                if (lvt_1_1_ instanceof LivingEntity) {
                    this.laserTargetEntity = (LivingEntity) lvt_1_1_;
                    return this.laserTargetEntity;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.MIMIC_OCTOPUS.get().create(serverWorld);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket() || this.isTame();
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.fromBucket();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MIMIC_ORDINAL, 0);
        this.entityData.define(PREV_MIMIC_ORDINAL, -1);
        this.entityData.define(MOISTNESS, 60000);
        this.entityData.define(MIMICKED_BLOCK, Optional.empty());
        this.entityData.define(PREV_MIMICKED_BLOCK, Optional.empty());
        this.entityData.define(SITTING, false);
        this.entityData.define(COMMAND, 0);
        this.entityData.define(LAST_SCARED_MOB_ID, -1);
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(UPGRADED, false);
        this.entityData.define(STOP_CHANGE, false);
        this.entityData.define(UPGRADED_LASER_ENTITY_ID, -1);
    }

    public MimicState getMimicState() {
        return MimicState.values()[Mth.clamp(entityData.get(MIMIC_ORDINAL), 0, 4)];
    }

    public void setMimicState(MimicState state) {
        if (getMimicState() != state) {
            this.entityData.set(PREV_MIMIC_ORDINAL, this.entityData.get(MIMIC_ORDINAL));
        }
        this.entityData.set(MIMIC_ORDINAL, state.ordinal());
    }

    public MimicState getPrevMimicState() {
        if (entityData.get(PREV_MIMIC_ORDINAL) == -1) {
            return null;
        }
        return MimicState.values()[Mth.clamp(entityData.get(PREV_MIMIC_ORDINAL), 0, 4)];
    }

    @Nullable
    public BlockState getMimickedBlock() {
        return this.entityData.get(MIMICKED_BLOCK).orElse(null);
    }

    public void setMimickedBlock(@Nullable BlockState state) {
        if (getMimickedBlock() != state) {
            this.entityData.set(PREV_MIMICKED_BLOCK, Optional.ofNullable(getMimickedBlock()));
        }
        this.entityData.set(MIMICKED_BLOCK, Optional.ofNullable(state));
    }

    @Nullable
    public BlockState getPrevMimickedBlock() {
        return this.entityData.get(PREV_MIMICKED_BLOCK).orElse(null);
    }

    protected void updateAir(int p_209207_1_) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(p_209207_1_ - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAirSupply(1200);
        }
    }

    @Override
    public boolean shouldEnterWater() {
        return !this.isSitting() && (this.getTarget() == null || this.getTarget().isInWaterOrBubble());
    }

    @Override
    public boolean shouldLeaveWater() {
        return this.getTarget() != null && !this.getTarget().isInWaterOrBubble();
    }

    @Override
    public boolean shouldStopMoving() {
        return isSitting();
    }

    @Override
    public int getWaterSearchRange() {
        return 16;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24) - radiusAdd;
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, 0, fleePos.z() + extraZ);
        BlockPos ground = getOctopusGround(radialPos);

        return ground != null ? Vec3.atCenterOf(ground) : null;
    }

    private BlockPos getOctopusGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getY(), in.getZ());
        while (position.getY() > 2 && level.getFluidState(position).is(FluidTags.WATER)) {
            position = position.below();
        }
        return position;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (UPGRADED_LASER_ENTITY_ID.equals(key)) {
            this.guardianLaserTime = 0;
            this.laserTargetEntity = null;
        }

    }

    private void creeperExplode() {
        Explosion explosion = new Explosion(level, this,  DamageSource.mobAttack(this), (ExplosionDamageCalculator)null, this.getX(), this.getY(), this.getZ(), 1 + random.nextFloat(), false, Explosion.BlockInteraction.NONE);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }

    public enum MimicState {
        OVERLAY,
        CREEPER,
        GUARDIAN,
        PUFFERFISH,
        MIMICUBE
    }

    private class AISwim extends SemiAquaticAIRandomSwimming {

        public AISwim() {
            super(EntityMimicOctopus.this, 1, 35);
        }

        protected Vec3 findSurfaceTarget(PathfinderMob creature, int i, int i1) {
            if (creature.getRandom().nextInt(5) == 0) {
                return super.findSurfaceTarget(creature, i, i1);
            } else {
                BlockPos downPos = creature.blockPosition();
                while (creature.level.getFluidState(downPos).is(FluidTags.WATER) || creature.level.getFluidState(downPos).is(FluidTags.LAVA)) {
                    downPos = downPos.below();
                }
                if (level.getBlockState(downPos).canOcclude() && level.getBlockState(downPos).getBlock() != Blocks.MAGMA_BLOCK) {
                    return new Vec3(downPos.getX() + 0.5F, downPos.getY(), downPos.getZ() + 0.5F);
                }
            }
            return null;
        }

    }

    private class AIFlee extends Goal {
        protected final EntitySorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vec3 flightTarget = null;
        private int cooldown = 0;

        AIFlee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new EntitySorter(EntityMimicOctopus.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && e.getType().is(AMTagRegistry.MIMIC_OCTOPUS_FEARS) || e instanceof Player && !((Player) e).isCreative();
                }
            };
        }

        @Override
        public boolean canUse() {
            if (EntityMimicOctopus.this.isPassenger() || EntityMimicOctopus.this.isVehicle() || EntityMimicOctopus.this.isTame()) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityMimicOctopus.this.level.getGameTime() % 10;
                if (EntityMimicOctopus.this.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityMimicOctopus.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntityMimicOctopus.this.level.getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return targetEntity != null && !EntityMimicOctopus.this.isTame() && EntityMimicOctopus.this.distanceTo(targetEntity) < 20;
        }

        public void stop() {
            flightTarget = null;
            this.targetEntity = null;
            EntityMimicOctopus.this.setMimicState(MimicState.OVERLAY);
            EntityMimicOctopus.this.setMimickedBlock(null);
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (!EntityMimicOctopus.this.isActiveCamo()) {
                EntityMimicOctopus.this.mimicEnvironment();
            }
            if (flightTarget != null) {
                EntityMimicOctopus.this.getNavigation().moveTo(flightTarget.x, flightTarget.y, flightTarget.z, 1.2F);
                if (cooldown == 0 && EntityMimicOctopus.this.isTargetBlocked(flightTarget)) {
                    cooldown = 30;
                    flightTarget = null;
                }
            }

            if (targetEntity != null) {
                if (flightTarget == null || flightTarget != null && EntityMimicOctopus.this.distanceToSqr(flightTarget) < 6) {
                    Vec3 vec;
                    vec = DefaultRandomPos.getPosAway(EntityMimicOctopus.this, 16, 7, targetEntity.position());
                    if (vec != null) {
                        flightTarget = vec;
                    }
                }
                if (EntityMimicOctopus.this.distanceTo(targetEntity) > 20.0F) {
                    this.stop();
                }
            }
        }

        protected double getTargetDistance() {
            return 10;
        }

        protected AABB getTargetableArea(double targetDistance) {
            Vec3 renderCenter = new Vec3(EntityMimicOctopus.this.getX(), EntityMimicOctopus.this.getY() + 0.5, EntityMimicOctopus.this.getZ());
            AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
            return aabb.move(renderCenter);
        }
    }

    public class EntitySorter implements Comparator<Entity> {
        private final Entity theEntity;

        public EntitySorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }

    public class FollowOwner extends Goal {
        private final EntityMimicOctopus tameable;
        private final LevelReader world;
        private final double followSpeed;
        private final float maxDist;
        private final float minDist;
        private final boolean teleportToLeaves;
        private LivingEntity owner;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public FollowOwner(EntityMimicOctopus p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
            this.tameable = p_i225711_1_;
            this.world = p_i225711_1_.level;
            this.followSpeed = p_i225711_2_;
            this.minDist = p_i225711_4_;
            this.maxDist = p_i225711_5_;
            this.teleportToLeaves = p_i225711_6_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity lvt_1_1_ = this.tameable.getOwner();
            if (lvt_1_1_ == null) {
                return false;
            } else if (lvt_1_1_.isSpectator()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else if (this.tameable.distanceToSqr(lvt_1_1_) < (double) (this.minDist * this.minDist)) {
                return false;
            } else if (this.tameable.getTarget() != null && this.tameable.getTarget().isAlive()) {
                return false;
            } else {
                this.owner = lvt_1_1_;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.tameable.getNavigation().isDone()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else if (this.tameable.getTarget() != null && this.tameable.getTarget().isAlive()) {
                return false;
            } else {
                return this.tameable.distanceToSqr(this.owner) > (double) (this.maxDist * this.maxDist);
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tameable.getPathfindingMalus(BlockPathTypes.WATER);
            this.tameable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.tameable.getNavigation().stop();
            this.tameable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {

            this.tameable.getLookControl().setLookAt(this.owner, 10.0F, (float) this.tameable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.tameable.isLeashed() && !this.tameable.isPassenger()) {
                    if (this.tameable.distanceToSqr(this.owner) >= 144.0D) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.tameable.getNavigation().moveTo(this.owner, this.followSpeed);
                    }

                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos lvt_1_1_ = this.owner.blockPosition();

            for (int lvt_2_1_ = 0; lvt_2_1_ < 10; ++lvt_2_1_) {
                int lvt_3_1_ = this.getRandomNumber(-3, 3);
                int lvt_4_1_ = this.getRandomNumber(-1, 1);
                int lvt_5_1_ = this.getRandomNumber(-3, 3);
                boolean lvt_6_1_ = this.tryToTeleportToLocation(lvt_1_1_.getX() + lvt_3_1_, lvt_1_1_.getY() + lvt_4_1_, lvt_1_1_.getZ() + lvt_5_1_);
                if (lvt_6_1_) {
                    return;
                }
            }

        }

        private boolean tryToTeleportToLocation(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
            if (Math.abs((double) p_226328_1_ - this.owner.getX()) < 2.0D && Math.abs((double) p_226328_3_ - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
                return false;
            } else {
                this.tameable.moveTo((double) p_226328_1_ + 0.5D, p_226328_2_, (double) p_226328_3_ + 0.5D, this.tameable.getYRot(), this.tameable.getXRot());
                this.tameable.getNavigation().stop();
                return true;
            }
        }

        private boolean isTeleportFriendlyBlock(BlockPos p_226329_1_) {
            BlockPathTypes lvt_2_1_ = WalkNodeEvaluator.getBlockPathTypeStatic(this.world, p_226329_1_.mutable());
            if (world.getFluidState(p_226329_1_).is(FluidTags.WATER) || !world.getFluidState(p_226329_1_).is(FluidTags.WATER) && world.getFluidState(p_226329_1_.below()).is(FluidTags.WATER)) {
                return true;
            }
            if (lvt_2_1_ != BlockPathTypes.WALKABLE || tameable.getMoistness() < 2000) {
                return false;
            } else {
                BlockState lvt_3_1_ = this.world.getBlockState(p_226329_1_.below());
                if (!this.teleportToLeaves && lvt_3_1_.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos lvt_4_1_ = p_226329_1_.subtract(this.tameable.blockPosition());
                    return this.world.noCollision(this.tameable, this.tameable.getBoundingBox().move(lvt_4_1_));
                }
            }
        }

        private int getRandomNumber(int p_226327_1_, int p_226327_2_) {
            return this.tameable.getRandom().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
        }
    }

    private class AIMimicNearbyMobs extends Goal {
        protected final EntitySorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 30;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vec3 flightTarget = null;
        private int cooldown = 0;

        AIMimicNearbyMobs() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new EntitySorter(EntityMimicOctopus.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && (e instanceof Creeper || e instanceof Guardian || e instanceof Pufferfish);
                }
            };
        }

        @Override
        public boolean canUse() {
            if (EntityMimicOctopus.this.isPassenger() || EntityMimicOctopus.this.isVehicle() || EntityMimicOctopus.this.getMimicState() != MimicState.OVERLAY || mimicCooldown > 0) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityMimicOctopus.this.level.getGameTime() % 10;
                if (EntityMimicOctopus.this.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityMimicOctopus.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntityMimicOctopus.this.level.getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return targetEntity != null && EntityMimicOctopus.this.distanceTo(targetEntity) < 10 && EntityMimicOctopus.this.getMimicState() == MimicState.OVERLAY;
        }

        public void stop() {
            EntityMimicOctopus.this.getNavigation().stop();
            flightTarget = null;
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (targetEntity != null) {
                EntityMimicOctopus.this.getNavigation().moveTo(targetEntity, 1.2F);
                if (EntityMimicOctopus.this.distanceTo(targetEntity) > 20.0F) {
                    this.stop();
                    EntityMimicOctopus.this.setMimicState(MimicState.OVERLAY);
                    EntityMimicOctopus.this.setMimickedBlock(null);
                } else if (EntityMimicOctopus.this.distanceTo(targetEntity) < 5.0F && EntityMimicOctopus.this.hasLineOfSight(targetEntity)) {
                    int i = 1200;
                    EntityMimicOctopus.this.stopMimicCooldown = i;
                    EntityMimicOctopus.this.camoCooldown = i + 40;
                    EntityMimicOctopus.this.mimicCooldown = 40;
                    if (targetEntity instanceof Creeper) {
                        EntityMimicOctopus.this.setMimicState(MimicState.CREEPER);
                    } else if (targetEntity instanceof Guardian) {
                        EntityMimicOctopus.this.setMimicState(MimicState.GUARDIAN);
                    } else if (targetEntity instanceof Pufferfish) {
                        EntityMimicOctopus.this.setMimicState(MimicState.PUFFERFISH);
                    } else {
                        EntityMimicOctopus.this.setMimicState(MimicState.OVERLAY);
                        EntityMimicOctopus.this.setMimickedBlock(null);
                    }
                    stop();
                }

            }
        }

        protected double getTargetDistance() {
            return 10;
        }

        protected AABB getTargetableArea(double targetDistance) {
            Vec3 renderCenter = new Vec3(EntityMimicOctopus.this.getX(), EntityMimicOctopus.this.getY() + 0.5, EntityMimicOctopus.this.getZ());
            AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
            return aabb.move(renderCenter);
        }
    }

    private class AIAttack extends Goal {
        private int executionCooldown = 0;
        private int scareMobTime = 0;
        private Vec3 fleePosition = null;

        public AIAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (executionCooldown > 0) {
                EntityMimicOctopus.this.entityData.set(UPGRADED_LASER_ENTITY_ID, -1);
                executionCooldown--;
            }
            if (EntityMimicOctopus.this.isStopChange() && EntityMimicOctopus.this.getMimicState() == MimicState.OVERLAY) {
                return false;
            }
            return executionCooldown == 0 && EntityMimicOctopus.this.isTame() && EntityMimicOctopus.this.getTarget() != null && EntityMimicOctopus.this.getTarget().isAlive();
        }

        public void stop() {
            fleePosition = null;
            scareMobTime = 0;
            executionCooldown = 100 + random.nextInt(200);
            if (EntityMimicOctopus.this.isUpgraded()) {
                executionCooldown = 30;
            } else {
                EntityMimicOctopus.this.setLastHurtByMob(null);
                EntityMimicOctopus.this.setTarget(null);
            }
            if (EntityMimicOctopus.this.stopMimicCooldown <= 0) {
                EntityMimicOctopus.this.mimicEnvironment();
            }
            EntityMimicOctopus.this.entityData.set(UPGRADED_LASER_ENTITY_ID, -1);
        }

        public Vec3 generateFleePosition(LivingEntity fleer) {
            for (int i = 0; i < 15; i++) {
                BlockPos pos = fleer.blockPosition().offset(random.nextInt(32) - 16, random.nextInt(16), random.nextInt(32) - 16);
                while (fleer.level.isEmptyBlock(pos) && pos.getY() > 1) {
                    pos = pos.below();
                }
                if (fleer instanceof PathfinderMob) {
                    if (((PathfinderMob) fleer).getWalkTargetValue(pos) >= 0.0F) {
                        return Vec3.atCenterOf(pos);
                    }
                } else {
                    return Vec3.atCenterOf(pos);
                }
            }
            return null;
        }

        public void tick() {
            LivingEntity target = EntityMimicOctopus.this.getTarget();
            if (target != null) {
                if (scareMobTime > 0) {
                    if (fleePosition == null || target.distanceToSqr(fleePosition) < target.getBbWidth() * target.getBbWidth() * 2) {
                        fleePosition = generateFleePosition(target);
                    }
                    if (target instanceof Mob) {
                        if (fleePosition != null) {
                            ((Mob) target).getNavigation().moveTo(fleePosition.x, fleePosition.y, fleePosition.z, 1.5F);
                            ((Mob) target).getMoveControl().setWantedPosition(fleePosition.x, fleePosition.y, fleePosition.z, 1.5F);
                            ((Mob) target).setTarget(null);
                        }
                    }
                    camoCooldown = Math.max(camoCooldown, 20);
                    stopMimicCooldown = Math.max(stopMimicCooldown, 20);
                    scareMobTime--;
                    if (scareMobTime == 0) {
                        stop();
                        return;
                    }
                }
                double dist = EntityMimicOctopus.this.distanceTo(target);
                boolean move = true;
                if (dist < 7F && EntityMimicOctopus.this.hasLineOfSight(target) && EntityMimicOctopus.this.getMimicState() == MimicState.GUARDIAN && EntityMimicOctopus.this.isUpgraded()) {
                    EntityMimicOctopus.this.entityData.set(UPGRADED_LASER_ENTITY_ID, target.getId());
                    move = false;
                }
                if (dist < 3) {
                    EntityMimicOctopus.this.entityData.set(LAST_SCARED_MOB_ID, target.getId());
                    if (move) {
                        move = EntityMimicOctopus.this.isUpgraded() && dist > 2;
                    }
                    EntityMimicOctopus.this.getNavigation().stop();
                    if (!EntityMimicOctopus.this.isStopChange()) {
                        EntityMimicOctopus.this.setMimickedBlock(null);
                        MimicState prev = EntityMimicOctopus.this.getMimicState();
                        if (EntityMimicOctopus.this.isInWaterOrBubble()) {
                            if (prev != MimicState.GUARDIAN && prev != MimicState.PUFFERFISH) {
                                if (random.nextBoolean()) {
                                    EntityMimicOctopus.this.setMimicState(MimicState.GUARDIAN);
                                } else {
                                    EntityMimicOctopus.this.setMimicState(MimicState.PUFFERFISH);
                                }
                            }
                        } else {
                            EntityMimicOctopus.this.setMimicState(MimicState.CREEPER);
                        }
                    }
                    if (EntityMimicOctopus.this.getMimicState() != MimicState.OVERLAY) {
                        EntityMimicOctopus.this.mimicCooldown = 40;
                        EntityMimicOctopus.this.stopMimicCooldown = Math.max(EntityMimicOctopus.this.stopMimicCooldown, 60);
                    }
                    if (EntityMimicOctopus.this.isUpgraded() && EntityMimicOctopus.this.transProgress >= 5.0F) {
                        if (EntityMimicOctopus.this.getMimicState() == MimicState.PUFFERFISH) {
                            if (EntityMimicOctopus.this.getBoundingBox().expandTowards(2, 1.3, 2).intersects(target.getBoundingBox())) {
                                target.hurt(DamageSource.mobAttack(EntityMimicOctopus.this), 4);
                                target.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 2));
                            }
                        }
                        if (EntityMimicOctopus.this.getMimicState() == MimicState.GUARDIAN) {
                            if (EntityMimicOctopus.this.getBoundingBox().expandTowards(1, 1, 1).intersects(target.getBoundingBox())) {
                                target.hurt(DamageSource.mobAttack(EntityMimicOctopus.this), 1);
                            }
                            EntityMimicOctopus.this.entityData.set(UPGRADED_LASER_ENTITY_ID, target.getId());
                        }
                        if (EntityMimicOctopus.this.getMimicState() == MimicState.CREEPER) {
                            EntityMimicOctopus.this.creeperExplode();
                            EntityMimicOctopus.this.level.broadcastEntityEvent(EntityMimicOctopus.this, (byte) 69);
                            executionCooldown = 300;
                        }
                    }
                    if (scareMobTime == 0) {
                        EntityMimicOctopus.this.level.broadcastEntityEvent(EntityMimicOctopus.this, (byte) 68);
                        scareMobTime = 60 + random.nextInt(60);
                    }
                }
                if (move) {
                    EntityMimicOctopus.this.lookAt(target, 30, 30);
                    EntityMimicOctopus.this.getNavigation().moveTo(target, 1.2F);
                }
            }
        }
    }
}
