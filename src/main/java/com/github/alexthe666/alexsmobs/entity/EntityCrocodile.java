package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockCrocodileEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ToolActions;

public class EntityCrocodile extends TamableAnimal implements IAnimatedEntity, ISemiAquatic {

    public static final Animation ANIMATION_LUNGE = Animation.create(23);
    public static final Animation ANIMATION_DEATHROLL = Animation.create(40);
    public static final Predicate<Entity> NOT_CREEPER = (entity) -> {
        return entity.isAlive() && !(entity instanceof Creeper);
    };
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityCrocodile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCrocodile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESERT = SynchedEntityData.defineId(EntityCrocodile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(EntityCrocodile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_DIGGING = SynchedEntityData.defineId(EntityCrocodile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STUN_TICKS = SynchedEntityData.defineId(EntityCrocodile.class, EntityDataSerializers.INT);
    public float groundProgress = 0;
    public float prevGroundProgress = 0;
    public float swimProgress = 0;
    public float prevSwimProgress = 0;
    public float baskingProgress = 0;
    public float prevBaskingProgress = 0;
    public float grabProgress = 0;
    public float prevGrabProgress = 0;
    public int baskingType = 0;
    public boolean forcedSit = false;
    private int baskingTimer = 0;
    private int swimTimer = -1000;
    private int ticksSinceInWater = 0;
    private int passengerTimer = 0;
    private boolean isLandNavigator;
    private boolean hasSpedUp = false;
    private int animationTick;
    private Animation currentAnimation;

    protected EntityCrocodile(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
        this.baskingType = random.nextInt(1);
    }

    public static boolean canCrocodileSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.CROCODILE_SPAWNS);
        return spawnBlock && pos.getY() < worldIn.getSeaLevel() + 4;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 15).add(Attributes.ARMOR, 8.0D).add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.4F).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.crocSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(new ItemStack(AMItemRegistry.CROCODILE_SCUTE.get(), random.nextInt(1) + 1), 1);
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setDesert(this.isBiomeDesert(worldIn, this.blockPosition()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private boolean isBiomeDesert(LevelAccessor worldIn, BlockPos position) {
        return worldIn.getBiome(position).is(AMTagRegistry.SPAWNS_DESERT_CROCODILES);
    }

    protected SoundEvent getAmbientSound() {
        return isBaby() ? AMSoundRegistry.CROCODILE_BABY.get() : AMSoundRegistry.CROCODILE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CROCODILE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CROCODILE_HURT.get();
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("CrocodileSitting", this.isSitting());
        compound.putBoolean("Desert", this.isDesert());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("BaskingStyle", this.baskingType);
        compound.putInt("BaskingTimer", this.baskingTimer);
        compound.putInt("SwimTimer", this.swimTimer);
        compound.putInt("StunTimer", this.getStunTicks());
        compound.putBoolean("HasEgg", this.hasEgg());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("CrocodileSitting"));
        this.setDesert(compound.getBoolean("Desert"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.baskingType = compound.getInt("BaskingStyle");
        this.baskingTimer = compound.getInt("BaskingTimer");
        this.swimTimer = compound.getInt("SwimTimer");
        this.setHasEgg(compound.getBoolean("HasEgg"));
        this.setStunTicks(compound.getInt("StunTimer"));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            PathNavigation prevNav = this.navigation;
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AquaticMoveController(this, 1F);
            PathNavigation prevNav = this.navigation;
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, Boolean.valueOf(false));
        this.entityData.define(DESERT, Boolean.valueOf(false));
        this.entityData.define(HAS_EGG, Boolean.valueOf(false));
        this.entityData.define(IS_DIGGING, Boolean.valueOf(false));
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(STUN_TICKS, 0);
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }
        this.entityData.set(CLIMBING, b0);
    }

    public void tick() {
        super.tick();
        this.prevGroundProgress = groundProgress;
        this.prevSwimProgress = swimProgress;
        this.prevBaskingProgress = baskingProgress;
        this.prevGrabProgress = grabProgress;
        boolean ground = !this.isInWater();
        boolean groundAnimate = !this.isInWater();
        boolean basking = groundAnimate && this.isSitting();
        boolean grabbing = !this.getPassengers().isEmpty();
        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (groundAnimate && this.groundProgress < 10F) {
            this.groundProgress++;
        }
        if (!groundAnimate && this.groundProgress > 0F) {
            this.groundProgress--;
        }
        if (!groundAnimate && this.swimProgress < 10F) {
            this.swimProgress++;
        }
        if (groundAnimate && this.swimProgress > 0F) {
            this.swimProgress--;
        }
        if (basking && this.baskingProgress < 10F) {
            this.baskingProgress++;
        }
        if (!basking && this.baskingProgress > 0F) {
            this.baskingProgress--;
        }
        if (grabbing && this.grabProgress < 10F) {
            this.grabProgress++;
        }
        if (!grabbing && this.grabProgress > 0F) {
            this.grabProgress--;
        }
        if (this.getTarget() != null && !hasSpedUp) {
            hasSpedUp = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28F);
        }
        if (this.getTarget() == null && hasSpedUp) {
            hasSpedUp = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        }
        if (!this.level.isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
        if (baskingTimer < 0) {
            baskingTimer++;
        }
        if (passengerTimer > 0 && this.getPassengers().isEmpty()) {
            passengerTimer = 0;
        }
        if (!level.isClientSide) {
            if (isInWater()) {
                swimTimer++;
                ticksSinceInWater = 0;
            } else {
                ticksSinceInWater++;
                swimTimer--;
            }
        }
        if (!level.isClientSide && !this.isInWater() && this.isOnGround()) {
            if (!this.isTame()) {
                if (!this.isSitting() && baskingTimer == 0 && this.getTarget() == null && this.getNavigation().isDone()) {
                    this.setOrderedToSit(true);
                    this.baskingTimer = 1000 + random.nextInt(750);
                }
                if (this.isSitting() && (baskingTimer <= 0 || this.getTarget() != null || swimTimer < -1000)) {
                    this.setOrderedToSit(false);
                    this.baskingTimer = -2000 - random.nextInt(750);
                }
                if (this.isSitting() && baskingTimer > 0) {
                    baskingTimer--;
                }
            }
        }
        if (!level.isClientSide && this.getStunTicks() == 0 && this.isAlive() && this.getTarget() != null && this.getAnimation() == ANIMATION_LUNGE  && (level.getDifficulty() != Difficulty.PEACEFUL || !(this.getTarget() instanceof Player)) && this.getAnimationTick() > 5 && this.getAnimationTick() < 9) {
            float f1 = this.getYRot() * ((float) Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
            if (this.distanceTo(this.getTarget()) < 3.5F && this.hasLineOfSight(this.getTarget())) {
                boolean flag = this.getTarget().isBlocking();
                if (!flag) {
                    if (this.getTarget().getBbWidth() < this.getBbWidth() && this.getPassengers().isEmpty() && !this.getTarget().isShiftKeyDown()) {
                        this.getTarget().startRiding(this, true);
                    }
                }
                if (flag) {
                    if (this.getTarget() instanceof Player) {
                        this.damageShieldFor(((Player) this.getTarget()), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                    }
                    if (this.getStunTicks() == 0) {
                        this.setStunTicks(25 + random.nextInt(20));
                    }
                } else {
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
                this.playSound(AMSoundRegistry.CROCODILE_BITE.get(), this.getSoundVolume(), this.getVoicePitch());

            }
        }
        if (!level.isClientSide && this.isAlive() && this.getTarget() != null && this.isInWater() && (level.getDifficulty() != Difficulty.PEACEFUL || !(this.getTarget() instanceof Player))) {
            if (this.getTarget().getVehicle() != null && this.getTarget().getVehicle() == this) {
                if (this.getAnimation() == NO_ANIMATION) {
                    this.setAnimation(ANIMATION_DEATHROLL);
                }
                if (this.getAnimation() == ANIMATION_DEATHROLL && this.getAnimationTick() % 10 == 0 && this.distanceTo(this.getTarget()) < 5D) {
                    this.getTarget().hurt(DamageSource.mobAttack(this), 5);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_DEATHROLL) {
            this.getNavigation().stop();
        }
        if (this.isInLove() && this.getTarget() != null) {
            this.setTarget(null);
        }
        if (this.getStunTicks() > 0) {
            this.setStunTicks(this.getStunTicks() - 1);
            if (level.isClientSide) {
                float angle = (0.01745329251F * this.yBodyRot);
                double headX = 1.5F * getScale() * Mth.sin((float) (Math.PI + angle));
                double headZ = 1.5F * getScale() * Mth.cos(angle);
                for (int i = 0; i < 5; i++) {
                    float innerAngle = (0.01745329251F * (this.yBodyRot + tickCount * 5) * (i + 1));
                    double extraX = 0.5F * Mth.sin((float) (Math.PI + innerAngle));
                    double extraZ = 0.5F * Mth.cos(innerAngle);
                    level.addParticle(ParticleTypes.CRIT, true, this.getX() + headX + extraX, this.getEyeY() + 0.5F, this.getZ() + headZ + extraZ, 0, 0, 0);
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    protected void damageShieldFor(Player holder, float damage) {
        if (holder.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            if (!this.level.isClientSide) {
                holder.awardStat(Stats.ITEM_USED.get(holder.getUseItem().getItem()));
            }

            if (damage >= 3.0F) {
                int i = 1 + Mth.floor(damage);
                InteractionHand hand = holder.getUsedItemHand();
                holder.getUseItem().hurtAndBreak(i, holder, (p_213833_1_) -> {
                    p_213833_1_.broadcastBreakEvent(hand);
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(holder, holder.getUseItem(), hand);
                });
                if (holder.getUseItem().isEmpty()) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        holder.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        holder.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    holder.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
                }
            }

        }
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.getStunTicks() > 0;
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
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

    public void positionRider(Entity passenger) {
        if (!this.getPassengers().isEmpty()) {
            this.yBodyRot = Mth.wrapDegrees(this.getYRot() - 180F);
        }
        if (this.hasPassenger(passenger)) {
            float radius = 2F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + 0.1F, this.getZ() + extraZ);
            passengerTimer++;
            if (this.isAlive() && passengerTimer > 0 && passengerTimer % 40 == 0) {
                passenger.hurt(DamageSource.mobAttack(this), 2);
            }
        }
    }

    public boolean onClimbable() {
        return isInWater() && this.isBesideClimbableBlock();
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION && this.getPassengers().isEmpty() && this.getStunTicks() == 0) {
            this.setAnimation(ANIMATION_LUNGE);
        }
        return true;
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
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return super.getWalkTargetValue(pos, worldIn);

    }

    public boolean shouldLeaveWater() {
        if (!this.getPassengers().isEmpty()) {
            return false;
        }
        if (this.getTarget() != null && !this.getTarget().isInWater()) {
            return true;
        }
        return swimTimer > 600;
    }

    @Override
    public boolean shouldStopMoving() {
        return this.getAnimation() == ANIMATION_DEATHROLL;
    }

    @Override
    public int getWaterSearchRange() {
        return this.getPassengers().isEmpty() ? 15 : 45;
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isDesert() {
        return this.entityData.get(DESERT).booleanValue();
    }

    public void setDesert(boolean desert) {
        this.entityData.set(DESERT, Boolean.valueOf(desert));
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    private void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    public boolean isDigging() {
        return this.entityData.get(IS_DIGGING);
    }

    private void setDigging(boolean isDigging) {
        this.entityData.set(IS_DIGGING, isDigging);
    }

    public int getStunTicks() {
        return this.entityData.get(STUN_TICKS);
    }

    private void setStunTicks(int stun) {
        this.entityData.set(STUN_TICKS, stun);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new MateGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new LayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new BreathAirGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new CrocodileAIMelee(this, 1, true));
        this.goalSelector.addGoal(5, new CrocodileAIRandomSwimming(this, 1.0D, 7));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, Player.class, 80, false, true, null) {
            public boolean canUse() {
                return !isBaby() && !isTame() && level.getDifficulty() != Difficulty.PEACEFUL && super.canUse();
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CROCODILE_TARGETS)) {
            public boolean canUse() {
                return !isBaby() && !isTame() && super.canUse();
            }
        });
        this.targetSelector.addGoal(6, new EntityAINearestTarget3D(this, Monster.class, 180, false, true, NOT_CREEPER) {
            public boolean canUse() {
                return !isBaby() && isTame() && super.canUse();
            }
        });
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            this.setOrderedToSit(false);
            if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.hurt(source, amount);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.CROCODILE.get().create(p_241840_1_);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (itemstack.getItem() == Items.NAME_TAG) {
            return super.mobInteract(player, hand);
        }
        if (isTame() && item.isEdible() && item.getFoodProperties() != null && item.getFoodProperties().isMeat() && this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.heal(10);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        InteractionResult type = super.mobInteract(player, hand);
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack)) {
            if (this.isSitting()) {
                this.forcedSit = false;
                this.setOrderedToSit(false);
                return InteractionResult.SUCCESS;
            } else {
                this.forcedSit = true;
                this.setOrderedToSit(true);
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }

    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (!this.isBaby()) {
            super.setTarget(entitylivingbaseIn);
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH;
    }

    @Override
    public boolean shouldEnterWater() {
        if (!this.getPassengers().isEmpty()) {
            return true;
        }
        return this.getTarget() == null && !this.isSitting() && this.baskingTimer <= 0 && !shouldLeaveWater() && swimTimer <= -1000;
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
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_LUNGE, ANIMATION_DEATHROLL};
    }

    public boolean isCrowned() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && (s.toLowerCase().contains("crown") || s.toLowerCase().contains("king") || s.toLowerCase().contains("rool"));
    }

    static class MateGoal extends BreedGoal {
        private final EntityCrocodile crocodile;

        MateGoal(EntityCrocodile crocodile, double speedIn) {
            super(crocodile, speedIn);
            this.crocodile = crocodile;
        }

        public boolean canUse() {
            return super.canUse() && !this.crocodile.hasEgg();
        }

        protected void breed() {
            ServerPlayer serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.partner.getLoveCause() != null) {
                serverplayerentity = this.partner.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, this.animal);
            }

            this.crocodile.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            this.animal.setAge(6000);
            this.partner.setAge(6000);

            RandomSource random = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }

        }
    }

    static class LayEggGoal extends MoveToBlockGoal {
        private final EntityCrocodile turtle;
        private int digTime;

        LayEggGoal(EntityCrocodile turtle, double speedIn) {
            super(turtle, speedIn, 16);
            this.turtle = turtle;
        }

        public void stop() {
            digTime = 0;
        }

        public boolean canUse() {
            return this.turtle.hasEgg() && super.canUse();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.turtle.hasEgg();
        }

        public double acceptedDistance() {
            return turtle.getBbWidth() + 0.5D;
        }

        public void tick() {
            super.tick();
            BlockPos blockpos = this.turtle.blockPosition();
            turtle.setOrderedToSit(false);
            turtle.baskingTimer = -100;
            if (!this.turtle.isInWater() && this.isReachedTarget()) {
                Level world = this.turtle.level;
                world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                world.setBlock(this.blockPos.above(), AMBlockRegistry.CROCODILE_EGG.get().defaultBlockState().setValue(BlockCrocodileEgg.EGGS, Integer.valueOf(this.turtle.random.nextInt(1) + 1)), 3);
                this.turtle.setHasEgg(false);
                this.turtle.setDigging(false);
                this.turtle.setInLoveTime(600);
            }

        }

        protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
            return worldIn.isEmptyBlock(pos.above()) && BlockCrocodileEgg.isProperHabitat(worldIn, pos);
        }
    }
}
