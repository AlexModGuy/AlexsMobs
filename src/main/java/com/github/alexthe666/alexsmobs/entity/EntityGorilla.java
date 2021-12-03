package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;

public class EntityGorilla extends TamableAnimal implements IAnimatedEntity, ITargetsDroppedItems {
    public static final Animation ANIMATION_BREAKBLOCK_R = Animation.create(20);
    public static final Animation ANIMATION_BREAKBLOCK_L = Animation.create(20);
    public static final Animation ANIMATION_POUNDCHEST = Animation.create(40);
    public static final Animation ANIMATION_ATTACK = Animation.create(20);
    protected static final EntityDimensions SILVERBACK_SIZE = EntityDimensions.scalable(1.35F, 1.95F);
    private static final EntityDataAccessor<Boolean> SILVERBACK = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.defineId(EntityGorilla.class, EntityDataSerializers.BOOLEAN);
    public int maxStandTime = 75;
    public float prevStandProgress;
    public float prevSitProgress;
    public float standProgress;
    public float sitProgress;
    public boolean forcedSit = false;
    private int animationTick;
    private Animation currentAnimation;
    private int standingTime = 0;
    private int eatingTime;
    @Nullable
    private EntityGorilla caravanHead;
    @Nullable
    private EntityGorilla caravanTail;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    @Nullable
    private UUID bananaThrowerID = null;
    private boolean hasSilverbackAttributes = false;
    public int poundChestCooldown = 0;

    protected EntityGorilla(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 7.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.5F).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public static boolean isBanana(ItemStack stack) {
        return ItemTags.getAllTags().getTag(AMTagRegistry.BANANAS).contains(stack.getItem());
    }

    public static boolean canGorillaSpawn(EntityType<EntityGorilla> gorilla, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, Random random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
        return (blockstate.is(BlockTags.LEAVES) || blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(BlockTags.LOGS) || blockstate.is(Blocks.AIR)) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.gorillaSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && isBanana(stack);
    }

    public int getMaxSpawnClusterSize() {
        return 8;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            this.setOrderedToSit(false);
            if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                amount = (amount + 1.0F) / 2.0F;
            }
            return super.hurt(source, amount);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new GorillaAIFollowCaravan(this, 0.8D));
        this.goalSelector.addGoal(3, new GorillaAIChargeLooker(this, 1.6D));
        this.goalSelector.addGoal(4, new TameableAITempt(this, 1.1D, Ingredient.of(ItemTags.getAllTags().getTag(AMTagRegistry.BANANAS)), false));
        this.goalSelector.addGoal(4, new AnimalAIRideParent(this, 1.25D));
        this.goalSelector.addGoal(6, new AIWalkIdle(this, 0.8D));
        this.goalSelector.addGoal(5, new GorillaAIForageLeaves(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GORILLA_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GORILLA_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GORILLA_HURT;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }

    public void travel(Vec3 vec3d) {
        if (this.isSitting()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn instanceof AgeableMob.AgeableMobGroupData) {
            AgeableMob.AgeableMobGroupData lvt_6_1_ = (AgeableMob.AgeableMobGroupData) spawnDataIn;
            if (lvt_6_1_.getGroupSize() == 0) {
                this.setSilverback(true);
            }
        } else {
            this.setSilverback(this.getRandom().nextBoolean());
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    public EntityGorilla getNearestSilverback(LevelAccessor world, double dist) {
        List<? extends EntityGorilla> list = world.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(dist, dist / 2, dist));
        if (list.isEmpty()) {
            return null;
        }
        EntityGorilla gorilla = null;
        double d0 = Double.MAX_VALUE;
        for (EntityGorilla gorrila2 : list) {
            if (gorrila2.isSilverback()) {
                double d1 = this.distanceToSqr(gorrila2);
                if (!(d1 > d0)) {
                    d0 = d1;
                    gorilla = gorrila2;
                }
            }
        }
        return gorilla;
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return isSilverback() && !isBaby() ? SILVERBACK_SIZE.scale(this.getScale()) : super.getDimensions(poseIn);
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            this.setOrderedToSit(false);
            passenger.setYRot(this.getYRot());
            if (passenger instanceof EntityGorilla) {
                EntityGorilla babyGorilla = (EntityGorilla) passenger;
                babyGorilla.setStanding(this.isStanding());
                babyGorilla.setOrderedToSit(this.isSitting());
            }
            float sitAdd = -0.03F * this.sitProgress;
            float standAdd = -0.03F * this.standProgress;
            float radius = standAdd + sitAdd;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    public boolean canBeControlledByRider() {
        return false;
    }

    public double getPassengersRidingOffset() {
        return (double) this.getBbHeight() * 0.65F * getGorillaScale() * (isSilverback() ? 0.75F : 1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SILVERBACK, Boolean.valueOf(false));
        this.entityData.define(STANDING, Boolean.valueOf(false));
        this.entityData.define(SITTING, Boolean.valueOf(false));
        this.entityData.define(EATING, Boolean.valueOf(false));
    }

    public boolean isSilverback() {
        return this.entityData.get(SILVERBACK).booleanValue();
    }

    public void setSilverback(boolean silver) {
        this.entityData.set(SILVERBACK, silver);
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, Boolean.valueOf(standing));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isEating() {
        return this.entityData.get(EATING).booleanValue();
    }

    public void setEating(boolean eating) {
        this.entityData.set(EATING, Boolean.valueOf(eating));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Silverback", this.isSilverback());
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("GorillaSitting", this.isSitting());
        compound.putBoolean("ForcedToSit", this.forcedSit);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSilverback(compound.getBoolean("Silverback"));
        this.setStanding(compound.getBoolean("Standing"));
        this.setOrderedToSit(compound.getBoolean("GorillaSitting"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (itemstack.getItem() == Items.NAME_TAG) {
            return super.mobInteract(player, hand);
        }
        if (isTame() && isBanana(itemstack) && this.getHealth() < this.getMaxHealth()) {
            this.heal(5);
            this.usePlayerItem(player, hand, itemstack);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        InteractionResult type = super.mobInteract(player, hand);
        if (type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack)) {
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

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
        if (animation == ANIMATION_POUNDCHEST) {
            this.maxStandTime = 45;
            this.setStanding(true);
        }
        if (animation == ANIMATION_ATTACK) {
            this.maxStandTime = 10;
            this.setStanding(true);
        }
    }

    public void tick() {
        super.tick();
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.canTargetItem(this.getItemInHand(InteractionHand.MAIN_HAND))) {
            this.setEating(true);
            this.setOrderedToSit(true);
            this.setStanding(false);
        }
        if (isEating() && !this.canTargetItem(this.getItemInHand(InteractionHand.MAIN_HAND))) {
            this.setEating(false);
            eatingTime = 0;
            if (!forcedSit) {
                this.setOrderedToSit(true);
            }
        }
        if (isEating()) {
            eatingTime++;
            if (!ItemTags.LEAVES.contains(this.getMainHandItem().getItem())) {
                for (int i = 0; i < 3; i++) {
                    double d2 = this.random.nextGaussian() * 0.02D;
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
                }
            }
            if (eatingTime % 5 == 0) {
                this.playSound(SoundEvents.PANDA_EAT, this.getSoundVolume(), this.getVoicePitch());
            }
            if (eatingTime > 100) {
                ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
                if (!stack.isEmpty()) {
                    this.heal(4);
                    if (isBanana(stack) && bananaThrowerID != null) {
                        if (getRandom().nextFloat() < 0.3F) {
                            this.setTame(true);
                            this.setOwnerUUID(this.bananaThrowerID);
                            Player player = level.getPlayerByUUID(bananaThrowerID);
                            if (player instanceof ServerPlayer) {
                                CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
                            }
                            this.level.broadcastEntityEvent(this, (byte) 7);
                        } else {
                            this.level.broadcastEntityEvent(this, (byte) 6);
                        }
                    }
                    if (stack.hasContainerItem()) {
                        this.spawnAtLocation(stack.getContainerItem());
                    }
                    stack.shrink(1);
                }
                eatingTime = 0;
            }
        }
        prevSitProgress = sitProgress;
        prevStandProgress = standProgress;
        if (this.isSitting() && sitProgress < 10) {
            sitProgress += 1;
        }
        if (!this.isSitting() && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (this.isStanding() && standProgress < 10) {
            standProgress += 1;
        }
        if (!this.isStanding() && standProgress > 0) {
            standProgress -= 1;
        }
        if (this.isPassenger() && this.getVehicle() instanceof EntityGorilla && !this.isBaby()) {
            this.removeVehicle();
        }
        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + random.nextInt(50);
        }
        if (isSitting() && !forcedSit && ++sittingTime > maxSitTime) {
            this.setOrderedToSit(false);
            sittingTime = 0;
            maxSitTime = 75 + random.nextInt(50);
        }
        if (!forcedSit && this.isSitting() && (this.getTarget() != null || this.isStanding()) && !this.isEating()) {
            this.setOrderedToSit(false);
        }
        if (!level.isClientSide && this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && random.nextInt(1500) == 0) {
            maxSitTime = 300 + random.nextInt(250);
            this.setOrderedToSit(true);
        }
        if (this.forcedSit && !this.isVehicle() && this.isTame()) {
            this.setOrderedToSit(true);
        }
        if (this.isSilverback() && random.nextInt(800) == 0 && poundChestCooldown <= 0 && this.getAnimation() == NO_ANIMATION && !this.isSitting() && sitProgress == 0 && !this.isNoAi() && this.getMainHandItem().isEmpty()) {
            this.setAnimation(ANIMATION_POUNDCHEST);
        }
        if (!level.isClientSide && this.getTarget() != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 10) {
            float f1 = this.getYRot() * ((float) Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
            getTarget().knockback(1F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
            this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
        if (isSilverback() && !isBaby() && !hasSilverbackAttributes) {
            hasSilverbackAttributes = true;
            refreshDimensions();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(50F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10F);
            this.heal(50F);
        }
        if (!isSilverback() && !isBaby() && hasSilverbackAttributes) {
            hasSilverbackAttributes = false;
            refreshDimensions();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8F);
            this.heal(30F);
        }
        if(poundChestCooldown > 0){
            poundChestCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    public boolean canTargetItem(ItemStack stack) {
        return ItemTags.getAllTags().getTag(AMTagRegistry.GORILLA_FOODSTUFFS).contains(stack.getItem());
    }

    @Override
    public void onGetItem(ItemEntity targetEntity) {
        ItemStack duplicate = targetEntity.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        if (EntityGorilla.isBanana(targetEntity.getItem()) && !this.isTame()) {
            bananaThrowerID = targetEntity.getThrower();
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BREAKBLOCK_R, ANIMATION_BREAKBLOCK_L, ANIMATION_POUNDCHEST, ANIMATION_ATTACK};
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.GORILLA.create(p_241840_1_);
    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(EntityGorilla caravanHeadIn) {
        this.caravanHead = caravanHeadIn;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTrail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public EntityGorilla getCaravanHead() {
        return this.caravanHead;
    }

    public float getGorillaScale() {
        return isBaby() ? 0.5F : isSilverback() ? 1.3F : 1.0F;
    }

    public boolean isDonkeyKong() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && (s.toLowerCase().contains("donkey") && s.toLowerCase().contains("kong") || s.toLowerCase().equals("dk"));
    }

    public boolean isFunkyKong() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && (s.toLowerCase().contains("funky") && s.toLowerCase().contains("kong"));
    }

    private class AIWalkIdle extends RandomStrollGoal {
        public AIWalkIdle(EntityGorilla entityGorilla, double v) {
            super(entityGorilla, v);
        }

        public boolean canUse() {
            this.interval = EntityGorilla.this.isSilverback() ? 10 : 120;
            return super.canUse();
        }

        @Nullable
        protected Vec3 getPosition() {
            return LandRandomPos.getPos(this.mob, EntityGorilla.this.isSilverback() ? 25 : 10, 7);
        }

    }
}
