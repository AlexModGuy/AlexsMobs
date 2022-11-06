package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityGrizzlyBear extends TamableAnimal implements NeutralMob, IAnimatedEntity, ITargetsDroppedItems, IFollower {

    public static final Animation ANIMATION_MAUL = Animation.create(20);
    public static final Animation ANIMATION_SNIFF = Animation.create(12);
    public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
    public static final Animation ANIMATION_SWIPE_L = Animation.create(20);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HONEYED = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SNOWY = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> APRIL_FOOLS_MODE = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.INT);
    private static final UniformInt angerLogic = TimeUtil.rangeOfSeconds(20, 39);
    public float prevStandProgress;
    public float prevSitProgress;
    public float standProgress;
    public float sitProgress;
    public int maxStandTime = 75;
    public boolean forcedSit = false;
    private int animationTick;
    private Animation currentAnimation;
    private int standingTime = 0;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    private int eatingTime = 0;
    private int angerTime;
    private UUID angerTarget;
    private int honeyedTime;
    @Nullable
    private UUID salmonThrowerID = null;
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.SALMON, Items.HONEYCOMB, Items.HONEY_BOTTLE);
    public int timeUntilNextFur = this.random.nextInt(24000) + 24000;
    protected static final EntityDimensions STANDING_SIZE = EntityDimensions.scalable(1.7F,  2.75F);
    private boolean recalcSize = false;
    private int snowTimer = 0;
    private boolean permSnow = false;

    protected EntityGrizzlyBear(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 55.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.6F).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return isStanding() ? STANDING_SIZE.scale(this.getScale()) : super.getDimensions(poseIn);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.grizzlyBearSpawnRolls, this.getRandom(), spawnReasonIn);
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

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GRIZZLY_BEAR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GRIZZLY_BEAR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GRIZZLY_BEAR_DIE.get();
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float sitAdd = -0.065F * this.sitProgress;
            float standAdd = -0.07F * this.standProgress;
            float radius = standAdd + sitAdd;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.animationSpeed);
        float f1 = this.animationPosition;
        float sitAdd = 0.01F * this.sitProgress;
        float standAdd = 0.07F * this.standProgress;
        return (double)this.getBbHeight() - 0.3D + (double)(0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f) + sitAdd + standAdd;
    }

    public void playAmbientSound() {
        if(!isFreddy()){
            super.playAmbientSound();
        }
    }

    protected float getWaterSlowDown() {
        return isVehicle() ? 0.9F : 0.98F;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(angerLogic.sample(this.random));
    }

    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.angerTime = time;
    }

    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.angerTarget = target;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.msgId != null && source.msgId.equals("sting") || source == DamageSource.IN_WALL ||super.isInvulnerableTo(source);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new TameableAIRide(this, 1D));
        this.goalSelector.addGoal(2, new TameableAIFollowOwner(this, 1.2D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new GrizzlyBearAIAprilFools(this));
        this.goalSelector.addGoal(4, new EntityGrizzlyBear.MeleeAttackGoal());
        this.goalSelector.addGoal(4, new EntityGrizzlyBear.PanicGoal());
        this.goalSelector.addGoal(5, new TameableAITempt(this, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new GrizzlyBearAIBeehive(this));
        this.goalSelector.addGoal(6, new GrizzlyBearAIFleeBees(this, 14, 1D, 1D));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new EntityGrizzlyBear.HurtByTargetGoal());
        this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(5, new EntityGrizzlyBear.AttackPlayerGoal());
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, Fox.class, false, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(8, new NonTameRandomTargetGoal<>(this, Wolf.class, false, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Honeyed", this.isHoneyed());
        compound.putBoolean("Snowy", this.isSnowy());
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("BearSitting", this.isSitting());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putBoolean("SnowPerm", this.permSnow);
        compound.putInt("FurTime", this.timeUntilNextFur);
        compound.putInt("BearCommand", this.getCommand());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHoneyed(compound.getBoolean("Honeyed"));
        this.setSnowy(compound.getBoolean("Snowy"));
        this.setStanding(compound.getBoolean("Standing"));
        this.setOrderedToSit(compound.getBoolean("BearSitting"));
        this.setCommand(compound.getInt("BearCommand"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.permSnow = compound.getBoolean("SnowPerm");
        this.timeUntilNextFur = compound.getInt("FurTime");
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && item == Items.SALMON;
    }


    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 67) {
            AlexsMobs.PROXY.onEntityStatus(this, id);
        } else  if (id == 68) {
            AlexsMobs.PROXY.spawnSpecialParticle(0);
        } else{
            super.handleEntityEvent(id);
        }
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                Player player = (Player) passenger;
                return player;
            }
        }
        return null;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if(item == Items.SNOW && !this.isSnowy() && !level.isClientSide){
            this.usePlayerItem(player, hand, itemstack);
            this.permSnow = true;
            this.setSnowy(true);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SNOW_PLACE, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        if(item instanceof ShovelItem && this.isSnowy() && !level.isClientSide){
            this.permSnow = false;
            if(!player.isCreative()){
                itemstack.hurt(1, this.getRandom(), player instanceof ServerPlayer ? (ServerPlayer)player : null);
            }
            this.setSnowy(false);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SNOW_BREAK, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack)){
            if(!player.isShiftKeyDown() && !this.isBaby()){
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }else{
                this.setCommand((this.getCommand() + 1) % 3);

                if (this.getCommand() == 3) {
                    this.setCommand(0);
                }
                player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.forcedSit = true;
                    this.setOrderedToSit(true);
                    return InteractionResult.SUCCESS;
                } else {
                    this.forcedSit = false;
                    this.setOrderedToSit(false);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return type;
    }

    public boolean isControlledByLocalInstance() {
        return false;
    }

    public void travel(Vec3 vec3d) {
        if (!this.shouldMove()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void tick() {
        super.tick();
        if (this.isBaby() || this.getEyeHeight() > this.getBbHeight()) {
            this.refreshDimensions();
        }
        if(!isStanding() && this.getBbHeight() >= 2.75F){
            this.refreshDimensions();
        }
        this.prevStandProgress = this.standProgress;
        this.prevSitProgress = this.sitProgress;
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
        if(!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.canTargetItem(this.getItemInHand(InteractionHand.MAIN_HAND))){
            this.setEating(true);
            this.setOrderedToSit(true);
            this.setStanding(false);
        }
        if(recalcSize){
            recalcSize = false;
            this.refreshDimensions();
        }
        if(isEating() && !this.canTargetItem(this.getItemInHand(InteractionHand.MAIN_HAND))){
            this.setEating(false);
            eatingTime = 0;
            if(!forcedSit){
                this.setOrderedToSit(true);
            }
        }
        if(isEating()){
            eatingTime++;
            for(int i = 0; i < 3; i++){
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
            }
            if(eatingTime % 5 == 0){
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            }
            if(eatingTime > 100){
                ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
                if(!stack.isEmpty()){
                    if(stack.is(AMTagRegistry.GRIZZLY_HONEY)){
                        this.setHoneyed(true);
                        this.heal(10);
                        this.honeyedTime = 700;
                    }else{
                        this.heal(4);
                    }
                    if(stack.getItem() == Items.SALMON && !this.isTame() && this.salmonThrowerID != null){
                       if(getRandom().nextFloat() < 0.3F){
                           this.setTame(true);
                           this.setOwnerUUID(this.salmonThrowerID);
                           Player player = level.getPlayerByUUID(salmonThrowerID);
                           if (player instanceof ServerPlayer) {
                               CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
                           }
                           this.level.broadcastEntityEvent(this, (byte)7);
                       }else{
                           this.level.broadcastEntityEvent(this, (byte)6);
                       }
                    }
                    if(stack.hasCraftingRemainingItem()){
                        this.spawnAtLocation(stack.getCraftingRemainingItem());
                    }
                    stack.shrink(1);
                }
                eatingTime = 0;
            }
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
        if (!level.isClientSide && this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && random.nextInt(1500) == 0) {
            maxSitTime = 300 + random.nextInt(250);
            this.setOrderedToSit(true);
        }
        /*
        if(this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && rand.nextInt(1500) == 0){
            maxStandTime = 75 + rand.nextInt(50);
            this.setStanding(true);
        }
         */
        if (!forcedSit && this.isSitting() && (this.getTarget() != null || this.isStanding()) && !this.isEating()) {
            this.setOrderedToSit(false);
        }
        if (this.getAnimation() == NO_ANIMATION && this.getAprilFoolsFlag() < 1 && random.nextInt(isStanding() ? 350 : 2500) == 0) {
            this.setAnimation(ANIMATION_SNIFF);
        }
        if (this.isSitting()) {
            this.getNavigation().stop();
        }
        LivingEntity attackTarget = this.getTarget();
        if(this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player){
            Player rider = (Player)this.getControllingPassenger();
            if(rider.getLastHurtMob() != null && this.distanceTo(rider.getLastHurtMob()) < this.getBbWidth() + 3F && !this.isAlliedTo(rider.getLastHurtMob())){
                UUID preyUUID = rider.getLastHurtMob().getUUID();
                if (!this.getUUID().equals(preyUUID)) {
                    attackTarget = rider.getLastHurtMob();
                    if (getAnimation() == NO_ANIMATION || getAnimation() == ANIMATION_SNIFF) {
                        EntityGrizzlyBear.this.setAnimation(random.nextBoolean() ? ANIMATION_MAUL : random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
                    }
                }
            }
        }
        if (attackTarget != null) {
            if(!level.isClientSide){
                this.setSprinting(true);
            }
            if (distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 2) {
                if (this.getAnimation() == ANIMATION_MAUL && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() > 3) {
                    doHurtTarget(attackTarget);
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_L) && this.getAnimationTick() == 7) {
                    doHurtTarget(attackTarget);
                    float rot = getYRot() + 90;
                    attackTarget.knockback(0.5F, Mth.sin(rot * ((float) Math.PI / 180F)), -Mth.cos(rot * ((float) Math.PI / 180F)));
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_R) && this.getAnimationTick() == 7) {
                    doHurtTarget(attackTarget);
                    float rot = getYRot() - 90;
                    attackTarget.knockback(0.5F, Mth.sin(rot * ((float) Math.PI / 180F)), -Mth.cos(rot * ((float) Math.PI / 180F)));
                }

            }
        }else{
            if(!level.isClientSide && this.getControllingPassenger() == null){
                this.setSprinting(false);
            }
        }
        if(!level.isClientSide && isHoneyed() && --honeyedTime <= 0){
            this.setHoneyed(false);
            honeyedTime = 0;
        }
        if(this.forcedSit && !this.isVehicle() && this.isTame()){
            this.setOrderedToSit(true);
        }
        if(this.isVehicle() && this.isSitting()){
            this.setOrderedToSit(false);
        }
        if (!this.level.isClientSide && this.isAlive() && isTame() && !this.isBaby() && --this.timeUntilNextFur <= 0) {
            this.spawnAtLocation(AMItemRegistry.BEAR_FUR.get());
            this.timeUntilNextFur = this.random.nextInt(24000) + 24000;
        }
        if(snowTimer > 0){
            snowTimer--;
        }
        if (snowTimer == 0 && !level.isClientSide) {
            snowTimer = 200 + random.nextInt(400);
            if(this.isSnowy()){
               if(!permSnow){
                   if (!this.level.isClientSide || this.getRemainingFireTicks() > 0 || this.isInWaterOrBubble() || !isSnowingAt(level, this.blockPosition().above())) {
                       this.setSnowy(false);
                   }
               }
            }else{
                if (!this.level.isClientSide &&  isSnowingAt(level, this.blockPosition())) {
                    this.setSnowy(true);
                }
            }
        }
        if(this.isFreddy()){
            this.setStanding(true);
            this.standingTime = 0;
            this.maxStandTime = 40;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public static boolean isSnowingAt(Level world, BlockPos position) {
        if (!world.isRaining()) {
            return false;
        } else if (!world.canSeeSky(position)) {
            return false;
        } else if (world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) {
            return false;
        } else {
            return world.getBiome(position).value().getPrecipitation() == Biome.Precipitation.SNOW;
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

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STANDING, Boolean.valueOf(false));
        this.entityData.define(SITTING, Boolean.valueOf(false));
        this.entityData.define(HONEYED, Boolean.valueOf(false));
        this.entityData.define(SNOWY, Boolean.valueOf(false));
        this.entityData.define(EATING, Boolean.valueOf(false));
        this.entityData.define(APRIL_FOOLS_MODE, 0);
        this.entityData.define(COMMAND, 0);
    }

    public boolean isEating() {
        return this.entityData.get(EATING).booleanValue();
    }

    public void setEating(boolean eating) {
        this.entityData.set(EATING, Boolean.valueOf(eating));
    }

    public boolean isHoneyed() {
        return this.entityData.get(HONEYED).booleanValue();
    }

    public void setHoneyed(boolean honeyed) {
        this.entityData.set(HONEYED, Boolean.valueOf(honeyed));
    }

    public boolean isSnowy() {
        return this.entityData.get(SNOWY).booleanValue();
    }

    public void setSnowy(boolean honeyed) {
        this.entityData.set(SNOWY, Boolean.valueOf(honeyed));
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, Boolean.valueOf(standing));
        this.recalcSize = true;
    }

    public int getAprilFoolsFlag() {
        return this.entityData.get(APRIL_FOOLS_MODE);
    }

    /*
        0 - default bear mode
        1 - stalking player, normal bear texture
        2 - freddy texture
        3 - freddy texture, blind player
        4 - freddy texture, music box
        5 - freddy texture, attack
     */
    public void setAprilFoolsFlag(int i) {
        this.entityData.set(APRIL_FOOLS_MODE, i);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob p_241840_2_) {
        return AMEntityRegistry.GRIZZLY_BEAR.get().create(world);
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
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
        if (animation == ANIMATION_MAUL) {
            maxStandTime = 21;
            this.setStanding(true);
        }
        if (animation == ANIMATION_SWIPE_R || animation == ANIMATION_SWIPE_L) {
            maxStandTime = 2 + random.nextInt(5);
            this.setStanding(true);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_MAUL, ANIMATION_SNIFF, ANIMATION_SWIPE_R, ANIMATION_SWIPE_L};
    }

    public boolean shouldMove() {
        return !isSitting();
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableMob.AgeableMobGroupData(1.0F);
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void playWarningSound() {
    }

    public boolean canTargetItem(ItemStack stack) {
        return stack.is(AMTagRegistry.GRIZZLY_FOODSTUFFS);
    }

    public void onGetItem(ItemEntity targetEntity) {
        ItemStack duplicate = targetEntity.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        if(targetEntity.getItem().getItem() == Items.SALMON && this.isHoneyed()){
            salmonThrowerID = targetEntity.getThrower();
        }else{
            salmonThrowerID = null;
        }
    }

    public boolean isEatingHeldItem() {
        return false;
    }

    public boolean isFreddy() {
        return getAprilFoolsFlag() > 1;
    }

    @Override
    public boolean shouldFollow() {
        return this.getAprilFoolsFlag() == 0 && this.getCommand() == 1;
    }


    class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(EntityGrizzlyBear.this);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            super.start();
            if (EntityGrizzlyBear.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(Mob mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityGrizzlyBear && !mobIn.isBaby()) {
                super.alertOther(mobIn, targetIn);
            }

        }
    }

    class MeleeAttackGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {
        public MeleeAttackGoal() {
            super(EntityGrizzlyBear.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0) {
                if (getAnimation() == NO_ANIMATION || getAnimation() == ANIMATION_SNIFF) {
                    EntityGrizzlyBear.this.setAnimation(random.nextBoolean() ? ANIMATION_MAUL : random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
                }
            } else if (distToEnemySqr <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    this.resetAttackCooldown();
                }
                if (this.getTicksUntilNextAttack() <= 10) {
                    EntityGrizzlyBear.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
            }

        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            EntityGrizzlyBear.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 3.0F + attackTarget.getBbWidth();
        }
    }

    class AttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
        public AttackPlayerGoal() {
            super(EntityGrizzlyBear.this, Player.class, 3, true, true, null);
        }

        public boolean canUse() {
            if (EntityGrizzlyBear.this.isBaby() || EntityGrizzlyBear.this.getAprilFoolsFlag() >= 1 || EntityGrizzlyBear.this.isHoneyed()) {
                return false;
            } else {
                return super.canUse();
            }
        }

        protected double getFollowDistance() {
            return 3.0D;
        }
    }

    class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(EntityGrizzlyBear.this, 2.0D);
        }

        public boolean canUse() {
            return (EntityGrizzlyBear.this.isBaby() || EntityGrizzlyBear.this.isOnFire()) && super.canUse();
        }
    }

}
