package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class EntityElephant extends TamableAnimal implements ITargetsDroppedItems, IAnimatedEntity {

    public static final Animation ANIMATION_TRUMPET_0 = Animation.create(20);
    public static final Animation ANIMATION_TRUMPET_1 = Animation.create(30);
    public static final Animation ANIMATION_CHARGE_PREPARE = Animation.create(25);
    public static final Animation ANIMATION_STOMP = Animation.create(20);
    public static final Animation ANIMATION_FLING = Animation.create(25);
    public static final Animation ANIMATION_EAT = Animation.create(30);
    public static final Animation ANIMATION_BREAKLEAVES = Animation.create(20);
    protected static final EntityDimensions TUSKED_SIZE = EntityDimensions.fixed(3.7F, 3.75F);
    private static final EntityDataAccessor<Boolean> TUSKED = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CARPET_COLOR = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TRADER = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
    public static final Map<DyeColor, Item> DYE_COLOR_ITEM_MAP = Util.make(Maps.newHashMap(), (map) -> {
        map.put(DyeColor.WHITE, Items.WHITE_CARPET);
        map.put(DyeColor.ORANGE, Items.ORANGE_CARPET);
        map.put(DyeColor.MAGENTA, Items.MAGENTA_CARPET);
        map.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_CARPET);
        map.put(DyeColor.YELLOW, Items.YELLOW_CARPET);
        map.put(DyeColor.LIME, Items.LIME_CARPET);
        map.put(DyeColor.PINK, Items.PINK_CARPET);
        map.put(DyeColor.GRAY, Items.GRAY_CARPET);
        map.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_CARPET);
        map.put(DyeColor.CYAN, Items.CYAN_CARPET);
        map.put(DyeColor.PURPLE, Items.PURPLE_CARPET);
        map.put(DyeColor.BLUE, Items.BLUE_CARPET);
        map.put(DyeColor.BROWN, Items.BROWN_CARPET);
        map.put(DyeColor.GREEN, Items.GREEN_CARPET);
        map.put(DyeColor.RED, Items.RED_CARPET);
        map.put(DyeColor.BLACK, Items.BLACK_CARPET);
    });
    private static final ResourceLocation TRADER_LOOT = new ResourceLocation("alexsmobs", "gameplay/trader_elephant_chest");
    public boolean forcedSit = false;
    public float prevSitProgress;
    public float sitProgress;
    public float prevStandProgress;
    public float standProgress;
    public int maxStandTime = 75;
    public boolean aiItemFlag = false;
    public SimpleContainer elephantInventory;
    private int animationTick;
    private Animation currentAnimation;
    private boolean hasTuskedAttributes = false;
    private int standingTime = 0;
    @Nullable
    private EntityElephant caravanHead;
    @Nullable
    private EntityElephant caravanTail;
    private boolean hasChestVarChanged = false;
    private boolean hasChargedSpeed = false;
    private boolean charging;
    private int chargeCooldown = 0;
    private int chargingTicks = 0;
    @Nullable
    private UUID blossomThrowerUUID = null;
    private int despawnDelay = 47999;

    protected EntityElephant(EntityType type, Level world) {
        super(type, world);
        initElephantInventory();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 65.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9F).add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    @Nullable
    public static DyeColor getCarpetColor(ItemStack stack) {
        Block lvt_1_1_ = Block.byItem(stack.getItem());
        return lvt_1_1_ instanceof WoolCarpetBlock ? ((WoolCarpetBlock) lvt_1_1_).getColor() : null;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.ELEPHANT_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ELEPHANT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ELEPHANT_DIE.get();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.elephantSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    private void initElephantInventory() {
        SimpleContainer animalchest = this.elephantInventory;
        this.elephantInventory = new SimpleContainer(54){

            public boolean stillValid(Player player) {
                return EntityElephant.this.isAlive() && !EntityElephant.this.isInsidePortal;
            }
        };
        if (animalchest != null) {
            int i = Math.min(animalchest.getContainerSize(), this.elephantInventory.getContainerSize());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.elephantInventory.setItem(j, itemstack.copy());
                }
            }
        }
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    public int getMaxHeadYRot() {
        return super.getMaxHeadYRot();
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSitting() || this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getAnimationTick() < 10;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TameableAIRide(this, 1D));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1D, true));
        this.goalSelector.addGoal(2, new EntityElephant.PanicGoal());
        this.goalSelector.addGoal(2, new ElephantAIVillagerRide(this, 1D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(AMItemRegistry.ACACIA_BLOSSOM.get()), false));
        this.goalSelector.addGoal(5, new ElephantAIForageLeaves(this));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1D));
        this.goalSelector.addGoal(7, new ElephantAIFollowCaravan(this, 0.5D));
        this.goalSelector.addGoal(8, new AvoidEntityGoal<>(this, Bee.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(9, new EntityElephant.AIWalkIdle(this, 0.5D));
        this.targetSelector.addGoal(1, new EntityElephant.HurtByTargetGoal().setAlertOthers());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && item == AMItemRegistry.ACACIA_BLOSSOM.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!isBaby()) {
            this.playSound(AMSoundRegistry.ELEPHANT_WALK.get(), 0.2F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                return passenger;
            }
        }
        return null;
    }

    @Nullable
    public AbstractVillager getControllingVillager() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof AbstractVillager) {
                return (AbstractVillager) passenger;
            }
        }
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TUSKED, Boolean.valueOf(false));
        this.entityData.define(SITTING, Boolean.valueOf(false));
        this.entityData.define(STANDING, Boolean.valueOf(false));
        this.entityData.define(CHESTED, Boolean.valueOf(false));
        this.entityData.define(TRADER, Boolean.valueOf(false));
        this.entityData.define(CARPET_COLOR, -1);
    }

    public void tick() {
        super.tick();
        prevSitProgress = sitProgress;
        prevStandProgress = standProgress;
        if (isSitting() && this.sitProgress < 5F) {
            this.sitProgress++;
        }
        if (!isSitting() && this.sitProgress > 0F) {
            this.sitProgress--;
        }
        if (this.isStanding() && standProgress < 5) {
            standProgress += 0.5F;
        }
        if (!this.isStanding() && standProgress > 0) {
            standProgress -= 0.5F;
        }
        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + random.nextInt(50);
        }
        if (isSitting() && isStanding()) {
            this.setStanding(false);
        }
        if (hasChestVarChanged && elephantInventory != null && !this.isChested()) {
            for (int i = 3; i < 18; i++) {
                if (!elephantInventory.getItem(i).isEmpty()) {
                    if (!level.isClientSide) {
                        this.spawnAtLocation(elephantInventory.getItem(i), 1);
                    }
                    elephantInventory.removeItemNoUpdate(i);
                }
            }
            hasChestVarChanged = false;
        }
        if (isTusked() && !isBaby() && !hasTuskedAttributes) {
            refreshDimensions();
        }
        if (!isTusked() && !isBaby() && hasTuskedAttributes) {
            refreshDimensions();
        }
        if (charging) {
            chargingTicks++;
        }
        if (!this.getMainHandItem().isEmpty() && this.canTargetItem(this.getMainHandItem())) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_EAT);
            }
            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() == 17) {
                this.eatItemEffect(this.getMainHandItem());
                if (this.getMainHandItem().getItem() == AMItemRegistry.ACACIA_BLOSSOM.get() && !this.isTame() && (!isTusked() || isBaby()) && blossomThrowerUUID != null) {
                    if (random.nextInt(3) == 0) {
                        this.setTame(true);
                        this.setOwnerUUID(blossomThrowerUUID);
                        Player player = this.level.getPlayerByUUID(blossomThrowerUUID);
                        if (player != null) {
                            this.tame(player);
                        }
                        for (Entity passenger : this.getPassengers()) {
                            passenger.removeVehicle();
                        }
                        this.level.broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                    }
                }
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                this.heal(10);
            }
        }
        if (chargeCooldown > 0) {
            chargeCooldown--;
        }
        if (charging) {
            chargingTicks++;
        } else {
            chargingTicks = 0;
        }
        if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
            this.yBodyRot = getYRot();
            if (this.getAnimationTick() == 20) {
                this.charging = true;
            }
        }
        if (this.getControllingPassenger() != null && charging && chargingTicks > 100) {
            this.charging = false;
            this.chargeCooldown = 200;
        }
        LivingEntity target = this.getTarget();
        double maxAttackMod = 0.0F;
        if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
            Player rider = (Player) this.getControllingPassenger();
            if (rider.getLastHurtMob() != null && !this.isAlliedTo(rider.getLastHurtMob())) {
                UUID preyUUID = rider.getLastHurtMob().getUUID();
                if (!this.getUUID().equals(preyUUID)) {
                    target = rider.getLastHurtMob();
                    maxAttackMod = 4F;
                }
            }
        }
        if (!level.isClientSide && target != null) {
            if (this.distanceTo(target) > this.getBbWidth() * 0.5F + 0.5F && this.getControllingPassenger() == null && this.isTusked() && this.hasLineOfSight(target) && this.getAnimation() == NO_ANIMATION && !charging && chargeCooldown == 0) {
                this.setAnimation(ANIMATION_CHARGE_PREPARE);
            }
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getControllingPassenger() == null) {
                this.lookAt(target, 360, 30);
                this.yBodyRot = getYRot();
                if (this.getAnimationTick() == 20) {
                    this.charging = true;
                }
            }
            if (this.distanceTo(target) < 10D && charging) {
                this.setAnimation(ANIMATION_FLING);
            }
            if (this.distanceTo(target) < 2.1D && charging) {
                target.knockback(1F, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.hasImpulse = true;
                target.setDeltaMovement(target.getDeltaMovement().add(0, 0.7F, 0));
                target.hurt(DamageSource.mobAttack(this), 2.4F * (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                launch(target, true);
                this.charging = false;
                this.chargeCooldown = 400;
            }
            double dist = this.distanceTo(target);
            if (dist < 4.5D + maxAttackMod && this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 15) {
                target.knockback(1F, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.setDeltaMovement(target.getDeltaMovement().add(0, 0.3F, 0));
                launch(target, false);
                target.hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
            if (dist < 4.5D + maxAttackMod && this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 17) {
                target.knockback(0.3F, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        }
        if (!level.isClientSide && this.getTarget() == null && this.getControllingPassenger() == null) {
            charging = false;
        }
        if (charging && !hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.65F);
            hasChargedSpeed = true;
        }
        if (!charging && hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
            hasChargedSpeed = false;
        }
        if (!level.isClientSide && this.getRandom().nextInt(400) == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_TRUMPET_0 : ANIMATION_TRUMPET_1);
        }
        if (this.getAnimation() == ANIMATION_TRUMPET_0 && this.getAnimationTick() == 8 || this.getAnimation() == ANIMATION_TRUMPET_1 && this.getAnimationTick() == 4) {
            this.gameEvent(GameEvent.ENTITY_ROAR);
            this.playSound(AMSoundRegistry.ELEPHANT_TRUMPET.get(), this.getSoundVolume(), this.getVoicePitch());
        }
        if (this.isAlive() && charging) {
            for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                if (!(this.isTame() && isAlliedTo(entity)) && !(!this.isTame() && entity instanceof EntityElephant) && entity != this) {
                    entity.hurt(DamageSource.mobAttack(this), 8.0F + random.nextFloat() * 8.0F);
                    launch(entity, true);
                }
            }
            maxUpStep = 2F;
        }else{
            maxUpStep = 0.6F;
        }
        if (!isTame() && isTrader()) {
            if (!this.level.isClientSide) {
                this.tryDespawn();
            }
        }
        if (this.getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget(null);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void aiStep() {
        super.aiStep();
        if (this.isBaby() && this.getEyeHeight() > this.getBbHeight()) {
            this.refreshDimensions();
        }
    }

    private boolean canDespawn() {
        return !this.isTame() && this.isTrader();
    }

    private void tryDespawn() {
        if (this.canDespawn()) {
            if (this.getControllingVillager() instanceof WanderingTrader) {
                int riderDelay = ((WanderingTrader) this.getControllingVillager()).getDespawnDelay();
                if (riderDelay > 0) {
                    this.despawnDelay = riderDelay;
                }
            }
            this.despawnDelay = this.despawnDelay - 1;
            if (this.despawnDelay <= 0) {
                this.dropLeash(true, false);
                this.elephantInventory.clearContent();
                if(this.getControllingVillager() != null){
                    this.getControllingVillager().remove(RemovalReason.DISCARDED);
                }
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.isOnGround()) {
            double d0 = e.getX() - this.getX();
            double d1 = e.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = huge ? 2F : 0.5F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    private void eatItemEffect(ItemStack heldItemMainhand) {
        this.gameEvent(GameEvent.EAT);
        this.playSound(SoundEvents.STRIDER_EAT, this.getVoicePitch(), this.getSoundVolume());
        for (int i = 0; i < 8 + random.nextInt(3); i++) {
            double d2 = this.random.nextGaussian() * 0.02D;
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            float radius = this.getBbWidth() * 0.65F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
            if (heldItemMainhand.getItem() instanceof BlockItem) {
                data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) heldItemMainhand.getItem()).getBlock().defaultBlockState());
            }
            this.level.addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
        }
    }

    private boolean isChargePlayer(Entity controllingPassenger) {
        return true;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION && !this.charging) {
            this.setAnimation(random.nextBoolean() ? ANIMATION_FLING : ANIMATION_STOMP);
        }
        return true;
    }


    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean owner = this.isTame() && isOwnedBy(player);
        InteractionResult type = super.mobInteract(player, hand);
        if (isChested() && player.isShiftKeyDown()) {
            this.openGUI(player);
            return InteractionResult.SUCCESS;
        } else if (canTargetItem(stack) && this.getMainHandItem().isEmpty()) {
            ItemStack rippedStack = stack.copy();
            rippedStack.setCount(1);
            stack.shrink(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
            if (rippedStack.getItem() == AMItemRegistry.ACACIA_BLOSSOM.get()) {
                blossomThrowerUUID = player.getUUID();
            }
            return InteractionResult.SUCCESS;
        } else if (owner && stack.is(ItemTags.WOOL_CARPETS)) {
            DyeColor color = getCarpetColor(stack);
            if (color != this.getColor()) {
                if (this.getColor() != null) {
                    this.spawnAtLocation(this.getCarpetItemBeingWorn());
                }
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.LLAMA_SWAG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                stack.shrink(1);
                this.setColor(color);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        } else if (owner && this.getColor() != null && stack.getItem() == Items.SHEARS) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.getColor() != null) {
                this.spawnAtLocation(this.getCarpetItemBeingWorn());
            }
            this.setColor(null);
            return InteractionResult.SUCCESS;
        } else if (owner && !this.isChested() && stack.is(Tags.Items.CHESTS_WOODEN)) {
            this.setChested(true);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (owner && isChested() && stack.getItem() == Items.SHEARS) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(Blocks.CHEST);
            for (int i = 0; i < elephantInventory.getContainerSize(); i++) {
                this.spawnAtLocation(elephantInventory.getItem(i));
            }
            elephantInventory.clearContent();
            this.setChested(false);
            return InteractionResult.SUCCESS;
        } else if (owner && !this.isBaby() && type != InteractionResult.CONSUME) {
            if(!level.isClientSide){
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return isTusked() && !isBaby() ? TUSKED_SIZE : super.getDimensions(poseIn);
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
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_TRUMPET_0, ANIMATION_TRUMPET_1, ANIMATION_CHARGE_PREPARE, ANIMATION_STOMP, ANIMATION_FLING, ANIMATION_EAT, ANIMATION_BREAKLEAVES};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public Item getCarpetItemBeingWorn() {
        if (this.getColor() != null) {
            return DYE_COLOR_ITEM_MAP.get(this.getColor());
        }
        return Items.AIR;
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isChested()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }
            for (int i = 0; i < elephantInventory.getContainerSize(); i++) {
                this.spawnAtLocation(elephantInventory.getItem(i));
            }
            elephantInventory.clearContent();
            this.setChested(false);
        }
        if (!this.isTrader() && this.getColor() != null) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(this.getCarpetItemBeingWorn());
            }
            this.setColor(null);
        }

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        EntityElephant baby = AMEntityRegistry.ELEPHANT.get().create(serverWorld);
        baby.setTusked(this.getNearestTusked(level, 15) == null || random.nextInt(2) == 0);
        return baby;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Tusked", this.isTusked());
        compound.putBoolean("ElephantSitting", this.isSitting());
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Trader", this.isTrader());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putBoolean("Tamed", this.isTame());
        compound.putInt("ChargeCooldown", this.chargeCooldown);
        compound.putInt("Carpet", this.entityData.get(CARPET_COLOR));
        compound.putInt("DespawnDelay", this.despawnDelay);
        if (elephantInventory != null) {
            ListTag nbttaglist = new ListTag();
            for (int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.elephantInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag CompoundNBT = new CompoundTag();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.save(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            compound.put("Items", nbttaglist);
        }
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == MobEffects.WITHER) {
            return false;
        }
        return super.canBeAffected(potioneffectIn);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTame(compound.getBoolean("Tamed"));
        this.setTusked(compound.getBoolean("Tusked"));
        this.setStanding(compound.getBoolean("Standing"));
        this.setOrderedToSit(compound.getBoolean("ElephantSitting"));
        this.setChested(compound.getBoolean("Chested"));
        this.setTrader(compound.getBoolean("Trader"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.chargeCooldown = compound.getInt("ChargeCooldown");
        this.entityData.set(CARPET_COLOR, compound.getInt("Carpet"));
        if (elephantInventory != null) {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initElephantInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.elephantInventory.setItem(j, ItemStack.of(CompoundNBT));
            }
        } else {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initElephantInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.initElephantInventory();
                this.elephantInventory.setItem(j, ItemStack.of(CompoundNBT));
            }
        }
        if (compound.contains("DespawnDelay", 99)) {
            this.despawnDelay = compound.getInt("DespawnDelay");
        }

    }

    public boolean isChested() {
        return Boolean.valueOf(this.entityData.get(CHESTED).booleanValue());
    }

    public void setChested(boolean chested) {
        this.entityData.set(CHESTED, Boolean.valueOf(chested));
        this.hasChestVarChanged = true;
    }

    public boolean setSlot(int inventorySlot, @Nullable ItemStack itemStackIn) {
        int j = inventorySlot - 500 + 2;
        if (j >= 0 && j < this.elephantInventory.getContainerSize()) {
            this.elephantInventory.setItem(j, itemStackIn);
            return true;
        } else {
            return false;
        }
    }

    public void die(DamageSource cause) {
        super.die(cause);
        if (elephantInventory != null && !this.level.isClientSide) {
            for (int i = 0; i < elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = elephantInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    this.spawnAtLocation(itemstack, 0.0F);
                }
            }
        }
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

    @Nullable
    public DyeColor getColor() {
        int lvt_1_1_ = this.entityData.get(CARPET_COLOR);
        return lvt_1_1_ == -1 ? null : DyeColor.byId(lvt_1_1_);
    }

    public void setColor(@Nullable DyeColor color) {
        this.entityData.set(CARPET_COLOR, color == null ? -1 : color.getId());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn instanceof AgeableMob.AgeableMobGroupData) {
            AgeableMob.AgeableMobGroupData lvt_6_1_ = (AgeableMob.AgeableMobGroupData) spawnDataIn;
            if (lvt_6_1_.getGroupSize() == 0) {
                this.setTusked(true);
            }
        }else{
            this.setTusked(this.getRandom().nextBoolean());
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    public EntityElephant getNearestTusked(LevelAccessor world, double dist) {
        List<? extends EntityElephant> list = world.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(dist, dist / 2, dist));
        if (list.isEmpty()) {
            return null;
        }
        EntityElephant elephant1 = null;
        double d0 = Double.MAX_VALUE;
        for (EntityElephant elephant : list) {
            if (elephant.isTusked()) {
                double d1 = this.distanceToSqr(elephant);
                if (!(d1 > d0)) {
                    d0 = d1;
                    elephant1 = elephant;
                }
            }
        }
        return elephant1;
    }

    public boolean isTusked() {
        return this.entityData.get(TUSKED).booleanValue();
    }

    public void setTusked(boolean tusked) {
        boolean prev = isTusked();
        if (!prev && tusked) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(80.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(15.0D);
            this.setHealth(150.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(65.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0D);
        }
        this.entityData.set(TUSKED, tusked);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.is(AMTagRegistry.ELEPHANT_FOODSTUFFS) || stack.getItem() == AMItemRegistry.ACACIA_BLOSSOM.get();
    }

    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        if (duplicate.getItem() == AMItemRegistry.ACACIA_BLOSSOM.get()) {
            blossomThrowerUUID = e.getThrower();
        } else {
            blossomThrowerUUID = null;
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        this.aiItemFlag = false;
    }

    @Override
    public void onFindTarget(ItemEntity e) {
        this.aiItemFlag = true;
    }

    public void addElephantLoot(@Nullable Player player, int seed) {
        if (this.level.getServer() != null) {
            LootTable loottable = this.level.getServer().getLootTables().get(TRADER_LOOT);

            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) this.level)).withOptionalRandomSeed(seed);

            loottable.fill(this.elephantInventory, lootcontext$builder.create(LootContextParamSets.EMPTY));
        }

    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(EntityElephant caravanHeadIn) {
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
    public EntityElephant getCaravanHead() {
        return this.caravanHead;
    }

    public double getMaxDistToItem() {
        return 5.0D;
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float standAdd = -0.3F * standProgress;
            float scale = this.isBaby() ? 0.5F : this.isTusked() ? 1.1F : 1.0F;
            float sitAdd = -0.065F * sitProgress;
            float scaleY = scale * (2.4F * sitAdd - 0.4F * standAdd);
            if (passenger instanceof AbstractVillager) {
                AbstractVillager villager = (AbstractVillager) passenger;
                scaleY -= 0.3F;
            }
            float radius = scale * (0.5F + standAdd);
            float angle = (0.01745329251F * this.yBodyRot);
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
                float sinWave = Mth.sin((float) (Math.PI * (this.getAnimationTick() / 25F)));
                radius += sinWave * 0.2F * scale;
            }
            if (this.getAnimation() == ANIMATION_STOMP) {
                float sinWave = Mth.sin((float) (Math.PI * (this.getAnimationTick() / 20F)));
                radius -= sinWave * 1.0F * scale;
                scaleY += sinWave * 0.7F * scale;
            }
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);

            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + scaleY + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    public boolean canBeControlledByRider() {
        return false;
    }


    public boolean isControlledByLocalInstance() {
        return false;
    }

    public double getPassengersRidingOffset() {
        float scale = this.isBaby() ? 0.5F : this.isTusked() ? 1.1F : 1.0F;
        float f = Math.min(0.25F, this.animationSpeed);
        float f1 = this.animationPosition;
        float sitAdd = 0.01F * 0;
        float standAdd = 0.07F * 0;
        return (double) this.getBbHeight() - 0.05F - scale * ((double) (0.1F * Mth.cos(f1 * 1.4F) * 1.4F * f) + sitAdd + standAdd);
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

    public void travel(Vec3 vec3d) {
        if (this.isSitting()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void openGUI(Player playerEntity) {
        if (!this.level.isClientSide && (!this.hasPassenger(playerEntity))) {
            NetworkHooks.openScreen((ServerPlayer) playerEntity, new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                    return ChestMenu.sixRows(p_createMenu_1_, p_createMenu_2_, elephantInventory);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("entity.alexsmobs.elephant.chest");
                }
            });
        }
    }

    public boolean isTrader() {
        return this.entityData.get(TRADER).booleanValue();
    }

    public void setTrader(boolean trader) {
        this.entityData.set(TRADER, trader);
    }

    public boolean triggerCharge(ItemStack stack) {
        if (this.getControllingPassenger() != null && chargeCooldown == 0 && !charging && this.getAnimation() == NO_ANIMATION && this.isTusked()) {
            this.setAnimation(ANIMATION_CHARGE_PREPARE);
            this.eatItemEffect(stack);
            this.heal(2);
            return true;
        }
        return false;
    }

    public boolean canSpawnWithTraderHere() {
        return this.checkSpawnObstruction(level) && level.isEmptyBlock(this.blockPosition().above(4));
    }

    private class AIWalkIdle extends RandomStrollGoal {
        public AIWalkIdle(EntityElephant e, double v) {
            super(e, v);
        }

        public boolean canUse() {
            this.interval = EntityElephant.this.isTusked() || !EntityElephant.this.inCaravan() ? 50 : 120;
            return super.canUse();
        }

        @Nullable
        protected Vec3 getPosition() {
            return LandRandomPos.getPos(this.mob, EntityElephant.this.isTusked() || !EntityElephant.this.inCaravan() ? 25 : 10, 7);
        }

    }

    class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(EntityElephant.this);
        }

        public void start() {
            if (EntityElephant.this.isBaby() || !EntityElephant.this.isTusked()) {
                this.alertOthers();
                this.stop();
            } else {
                super.start();
            }
        }

        protected void alertOther(Mob mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityElephant && (!mobIn.isBaby() || !((EntityElephant) mobIn).isTusked())) {
                super.alertOther(mobIn, targetIn);
            }

        }
    }

    class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(EntityElephant.this, 1.0D);
        }

        public boolean canUse() {
            return (EntityElephant.this.isBaby() || !EntityElephant.this.isTusked() || EntityElephant.this.isOnFire()) && super.canUse();
        }
    }
}
