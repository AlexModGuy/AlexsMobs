package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntityMantisShrimp extends TamableAnimal implements ISemiAquatic, IFollower {

    private static final EntityDataAccessor<Float> RIGHT_EYE_PITCH = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RIGHT_EYE_YAW = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LEFT_EYE_PITCH = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LEFT_EYE_YAW = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> PUNCH_TICK = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.defineId(EntityMantisShrimp.class, EntityDataSerializers.INT);
    public float prevRightPitch;
    public float prevRightYaw;
    public float prevLeftPitch;
    public float prevLeftYaw;
    public float prevInWaterProgress;
    public float inWaterProgress;
    public float prevPunchProgress;
    public float punchProgress;
    private int leftLookCooldown = 0;
    private int rightLookCooldown = 0;
    private float targetRightPitch;
    private float targetRightYaw;
    private float targetLeftPitch;
    private float targetLeftYaw;
    private boolean isLandNavigator;
    private int fishFeedings;
    private int moistureAttackTime = 0;

    protected EntityMantisShrimp(EntityType type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
        this.maxUpStep = 1;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MANTIS_SHRIMP_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MANTIS_SHRIMP_HURT.get();
    }


    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            if (entity instanceof Shulker || entity instanceof ShulkerBullet) {
                amount = (amount + 1.0F) * 0.33F;
            }
            return super.hurt(source, amount);
        }
    }

    //killEntity
    public void awardKillScore(Entity entity, int score, DamageSource src) {
        if(entity instanceof LivingEntity living){
            if(living.getType() == EntityType.SHULKER){
                CompoundTag fishNbt = new CompoundTag();
                living.addAdditionalSaveData(fishNbt);
                fishNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
                living.readAdditionalSaveData(fishNbt);
                living.spawnAtLocation(Items.SHULKER_SHELL);
            }
        }
        super.awardKillScore(entity, score, src);
    }

    public static boolean canMantisShrimpSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        BlockPos downPos = pos;
        while (downPos.getY() > 1 && !worldIn.getFluidState(downPos).isEmpty()) {
            downPos = downPos.below();
        }
        boolean spawnBlock = worldIn.getBlockState(downPos).is(AMTagRegistry.MANTIS_SHRIMP_SPAWNS);
        //limit spawns in mangrove biomes
        if(worldIn.getBiome(pos).is(AMTagRegistry.SPAWNS_WHITE_MANTIS_SHRIMP) && randomIn.nextFloat() < 0.5F){
            return false;
        }
        return spawnBlock && downPos.getY() < worldIn.getSeaLevel() + 1;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.1D).add(Attributes.ARMOR, 8D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame();
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mantisShrimpSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MantisShrimpAIFryRice(this));
        this.goalSelector.addGoal(0, new MantisShrimpAIBreakBlocks(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwner(this, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2F, false));
        this.goalSelector.addGoal(4, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(4, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.0D, Ingredient.of(Items.TROPICAL_FISH, AMItemRegistry.LOBSTER_TAIL.get(), AMItemRegistry.COOKED_LOBSTER_TAIL.get()), false));
        this.goalSelector.addGoal(7, new SemiAquaticAIRandomSwimming(this, 1.0D, 30));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 120, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.MANTIS_SHRIMP_TARGETS)) {
            public boolean canUse() {
                return EntityMantisShrimp.this.getCommand() != 3 && !EntityMantisShrimp.this.isSitting() && super.canUse();
            }
        });
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AnimalSwimMoveControllerSink(this, 1F, 1F);
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
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

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || source == DamageSource.IN_WALL  || super.isInvulnerableTo(source);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RIGHT_EYE_PITCH, 0F);
        this.entityData.define(RIGHT_EYE_YAW, 0F);
        this.entityData.define(LEFT_EYE_PITCH, 0F);
        this.entityData.define(LEFT_EYE_YAW, 0F);
        this.entityData.define(PUNCH_TICK, 0);
        this.entityData.define(COMMAND, Integer.valueOf(0));
        this.entityData.define(VARIANT, Integer.valueOf(0));
        this.entityData.define(SITTING, Boolean.valueOf(false));
        this.entityData.define(MOISTNESS, Integer.valueOf(60000));
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && (item == AMItemRegistry.LOBSTER_TAIL.get() || item == AMItemRegistry.COOKED_LOBSTER_TAIL.get());
    }

    public boolean doHurtTarget(Entity entityIn) {
        this.punch();
        return true;
    }

    public void punch() {
        this.entityData.set(PUNCH_TICK, 4);
    }

    public float getEyeYaw(boolean left) {
        return entityData.get(left ? LEFT_EYE_YAW : RIGHT_EYE_YAW);
    }

    public float getEyePitch(boolean left) {
        return entityData.get(left ? LEFT_EYE_PITCH : RIGHT_EYE_PITCH);
    }

    public void setEyePitch(boolean left, float pitch) {
        entityData.set(left ? LEFT_EYE_PITCH : RIGHT_EYE_PITCH, pitch);
    }

    public void setEyeYaw(boolean left, float yaw) {
        entityData.set(left ? LEFT_EYE_YAW : RIGHT_EYE_YAW, yaw);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int command) {
        this.entityData.set(VARIANT, Integer.valueOf(command));
    }

    public int getMoistness() {
        return this.entityData.get(MOISTNESS);
    }

    public void setMoistness(int p_211137_1_) {
        this.entityData.set(MOISTNESS, p_211137_1_);
    }

    public void tick() {
        super.tick();
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
        } else {
            if (this.isInWaterRainOrBubble() || this.getMainHandItem().getItem() == Items.WATER_BUCKET) {
                this.setMoistness(60000);
            } else {
                this.setMoistness(this.getMoistness() - 1);
                if (this.getMoistness() <= 0 && moistureAttackTime-- <= 0) {
                    this.setCommand(0);
                    this.setOrderedToSit(false);
                    this.hurt(DamageSource.DRY_OUT, random.nextInt(2) == 0 ? 1.0F : 0F);
                    moistureAttackTime = 20;
                }
            }
        }
        if(this.hasEffect(MobEffects.LEVITATION)){
            this.setDeltaMovement(this.getDeltaMovement().multiply(1F, 0.5F, 1F));
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (!isTame() && item == Items.TROPICAL_FISH) {
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
            fishFeedings++;
            if (fishFeedings > 10 && getRandom().nextInt(6) == 0 || fishFeedings > 30) {
                this.tame(player);
                this.level.broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }
        if (isTame() && itemstack.is(ItemTags.FISHES)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.usePlayerItem(player, hand, itemstack);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.heal(5);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;

        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player)) {
            if (player.isShiftKeyDown() || itemstack.is(AMTagRegistry.SHRIMP_RICE_FRYABLES)) {
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
                if (this.getCommand() == 4) {
                    this.setCommand(0);
                }
                if (this.getCommand() == 3) {
                    player.displayClientMessage(Component.translatable("entity.alexsmobs.mantis_shrimp.command_3", this.getName()), true);
                } else {
                    player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                }
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

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("MantisShrimpSitting", this.isSitting());
        compound.putInt("Command", this.getCommand());
        compound.putInt("Moisture", this.getMoistness());
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("MantisShrimpSitting"));
        this.setCommand(compound.getInt("Command"));
        this.setVariant(compound.getInt("Variant"));
        this.setMoistness(compound.getInt("Moisture"));
    }

    public void aiStep() {
        super.aiStep();
        if (this.isBaby() && this.getEyeHeight() > this.getBbHeight()) {
            this.refreshDimensions();
        }
        prevLeftPitch = this.getEyePitch(true);
        prevRightPitch = this.getEyePitch(false);
        prevLeftYaw = this.getEyeYaw(true);
        prevRightYaw = this.getEyeYaw(false);
        prevInWaterProgress = this.inWaterProgress;
        prevPunchProgress = this.punchProgress;
        updateEyes();
        if (this.isSitting() && this.getNavigation().isDone()) {
            this.getNavigation().stop();
        }
        if (this.isInWater() && inWaterProgress < 5F) {
            inWaterProgress++;
        }
        if (!this.isInWater() && inWaterProgress > 0F) {
            inWaterProgress--;
        }
        if (this.isInWater() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!this.isInWater() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (this.entityData.get(PUNCH_TICK) > 0) {
            if (this.entityData.get(PUNCH_TICK) == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < 2.8D) {
                if (this.getTarget() instanceof AbstractFish && !this.isTame()) {
                    AbstractFish fish = (AbstractFish) this.getTarget();
                    CompoundTag fishNbt = new CompoundTag();
                    fish.addAdditionalSaveData(fishNbt);
                    fishNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
                    fish.readAdditionalSaveData(fishNbt);
                }
                this.getTarget().knockback(1.7F, this.getX() - this.getTarget().getX(), this.getZ() - this.getTarget().getZ());
                float knockbackResist = (float) Mth.clamp((1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
                this.getTarget().setDeltaMovement(this.getTarget().getDeltaMovement().add(0, knockbackResist * 0.8F, 0));
                if (!this.getTarget().isInWater()) {
                    this.getTarget().setSecondsOnFire(2);
                }
                this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
            if(punchProgress == 1){
                this.playSound(AMSoundRegistry.MANTIS_SHRIMP_SNAP.get(), this.getVoicePitch(), this.getSoundVolume());
            }
            if (punchProgress == 2 && level.isClientSide && this.isInWater()) {
                for (int i = 0; i < 10 + random.nextInt(8); i++) {
                    double d2 = this.random.nextGaussian() * 0.6D;
                    double d0 = this.random.nextGaussian() * 0.2D;
                    double d1 = this.random.nextGaussian() * 0.6D;
                    float radius = this.getBbWidth() * 0.85F;
                    float angle = (0.01745329251F * this.yBodyRot);
                    double extraX = radius * Mth.sin((float) (Math.PI + angle)) + random.nextFloat() * 0.5F - 0.25F;
                    double extraZ = radius * Mth.cos(angle) + random.nextFloat() * 0.5F - 0.25F;
                    ParticleOptions data = ParticleTypes.BUBBLE;
                    this.level.addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.3F + random.nextFloat() * 0.15F, this.getZ() + extraZ, d0, d1, d2);
                }
            }
            if (punchProgress < 2F) {
                punchProgress++;
            }
            this.entityData.set(PUNCH_TICK, this.entityData.get(PUNCH_TICK) - 1);
        } else {
            if (punchProgress > 0F) {
                punchProgress -= 0.25F;
            }
        }
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

    private void updateEyes() {
        float leftPitchDist = Math.abs(this.getEyePitch(true) - targetLeftPitch);
        float rightPitchDist = Math.abs(this.getEyePitch(false) - targetRightPitch);
        float leftYawDist = Math.abs(this.getEyeYaw(true) - targetLeftYaw);
        float rightYawDist = Math.abs(this.getEyeYaw(false) - targetRightYaw);
        if (rightLookCooldown == 0 && this.random.nextInt(20) == 0 && rightPitchDist < 0.5F && rightYawDist < 0.5F) {
            targetRightPitch = Mth.clamp(random.nextFloat() * 60F - 30, -30, 30);
            targetRightYaw = Mth.clamp(random.nextFloat() * 60F - 30, -30, 30);
            rightLookCooldown = 3 + random.nextInt(15);
        }
        if (leftLookCooldown == 0 && this.random.nextInt(20) == 0 && leftPitchDist < 0.5F && leftYawDist < 0.5F) {
            targetLeftPitch = Mth.clamp(random.nextFloat() * 60F - 30, -30, 30);
            targetLeftYaw = Mth.clamp(random.nextFloat() * 60F - 30, -30, 30);
            leftLookCooldown = 3 + random.nextInt(15);
        }
        if (this.getEyePitch(true) < this.targetLeftPitch && leftPitchDist > 0.5F) {
            this.setEyePitch(true, this.getEyePitch(true) + Math.min(leftPitchDist, 4F));
        }
        if (this.getEyePitch(true) > this.targetLeftPitch && leftPitchDist > 0.5F) {
            this.setEyePitch(true, this.getEyePitch(true) - Math.min(leftPitchDist, 4F));
        }
        if (this.getEyePitch(false) < this.targetRightPitch && rightPitchDist > 0.5F) {
            this.setEyePitch(false, this.getEyePitch(false) + Math.min(rightPitchDist, 4F));
        }
        if (this.getEyePitch(false) > this.targetRightPitch && rightPitchDist > 0.5F) {
            this.setEyePitch(false, this.getEyePitch(false) - Math.min(rightPitchDist, 4F));
        }
        if (this.getEyeYaw(true) < this.targetLeftYaw && leftYawDist > 0.5F) {
            this.setEyeYaw(true, this.getEyeYaw(true) + Math.min(leftYawDist, 4F));
        }
        if (this.getEyeYaw(true) > this.targetLeftYaw && leftYawDist > 0.5F) {
            this.setEyeYaw(true, this.getEyeYaw(true) - Math.min(leftYawDist, 4F));
        }
        if (this.getEyeYaw(false) < this.targetRightYaw && rightYawDist > 0.5F) {
            this.setEyeYaw(false, this.getEyeYaw(false) + Math.min(rightYawDist, 4F));
        }
        if (this.getEyeYaw(false) > this.targetRightYaw && rightYawDist > 0.5F) {
            this.setEyeYaw(false, this.getEyeYaw(false) - Math.min(rightYawDist, 4F));
        }
        if (rightLookCooldown > 0) {
            rightLookCooldown--;
        }
        if (leftLookCooldown > 0) {
            leftLookCooldown--;
        }
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        int i;
        if(reason == MobSpawnType.SPAWN_EGG){
            i = this.getRandom().nextInt(4);
        }else if(worldIn.getBiome(this.blockPosition()).is(AMTagRegistry.SPAWNS_WHITE_MANTIS_SHRIMP)){
            i = 3;
        }else{
            i = this.getRandom().nextInt(3);
        }
        this.setVariant(i);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        EntityMantisShrimp shrimp = AMEntityRegistry.MANTIS_SHRIMP.get().create(serverWorld);
        shrimp.setVariant(getRandom().nextInt(3));
        return shrimp;
    }

    @Override
    public boolean shouldEnterWater() {
        return (this.getMainHandItem().isEmpty() || this.getMainHandItem().getItem() != Items.WATER_BUCKET) && !this.isSitting();
    }

    @Override
    public boolean shouldLeaveWater() {
        return this.getMainHandItem().getItem() == Items.WATER_BUCKET;
    }

    @Override
    public boolean shouldStopMoving() {
        return isSitting();
    }

    @Override
    public int getWaterSearchRange() {
        return 16;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    protected void updateAir(int p_209207_1_) {
    }



    public class FollowOwner extends Goal {
        private final EntityMantisShrimp tameable;
        private final LevelReader world;
        private final double followSpeed;
        private final float maxDist;
        private final float minDist;
        private final boolean teleportToLeaves;
        private LivingEntity owner;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public FollowOwner(EntityMantisShrimp p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
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
}
