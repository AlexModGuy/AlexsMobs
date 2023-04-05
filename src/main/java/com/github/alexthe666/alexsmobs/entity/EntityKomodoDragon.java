package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityKomodoDragon extends TamableAnimal implements ITargetsDroppedItems, IFollower {

    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.ROTTEN_FLESH);
    public int slaughterCooldown = 0;
    public int timeUntilSpit = this.random.nextInt(12000) + 24000;
    public float nextJostleAngleFromServer;
    private int riderAttackCooldown = 0;
    public static final Predicate<EntityKomodoDragon> HURT_OR_BABY = (p_213616_0_) -> {
        return p_213616_0_.isBaby() || p_213616_0_.getHealth() <= 0.7F * p_213616_0_.getMaxHealth();
    };
    protected static final EntityDimensions JOSTLING_SIZE = EntityDimensions.scalable(1.35F, 1.85F);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> JOSTLING = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> JOSTLE_ANGLE = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> JOSTLER_UUID = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.BOOLEAN);
    public float prevJostleAngle;
    public float prevJostleProgress;
    public float jostleProgress;
    public float prevSitProgress;
    public float sitProgress;
    public boolean jostleDirection;
    public int jostleTimer = 0;
    public boolean instantlyTriggerJostleAI = false;
    public int jostleCooldown = 100 + random.nextInt(40);
    private boolean hasJostlingSize;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COMMAND, 0);
        this.entityData.define(JOSTLING, Boolean.valueOf(false));
        this.entityData.define(SADDLED, Boolean.valueOf(false));
        this.entityData.define(JOSTLE_ANGLE, 0F);
        this.entityData.define(JOSTLER_UUID, Optional.empty());
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public static <T extends Mob> boolean canKomodoDragonSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.KOMODO_DRAGON_SPAWNS);
        return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.komodoDragonSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2D, false));
        this.goalSelector.addGoal(2, new TameableAIRide(this, 2D));
        this.goalSelector.addGoal(3, new TameableAIFollowOwner(this, 1.2D, 6.0F, 3.0F, false));
        this.goalSelector.addGoal(4, new KomodoDragonAIJostle(this));
        this.goalSelector.addGoal(5, new TameableAITempt(this, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(5, new AnimalAIFleeAdult(this, 1.25D, 32));
        this.goalSelector.addGoal(6, new KomodoDragonAIBreed(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1D, 50));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, EntityKomodoDragon.class, 50, true, false, HURT_OR_BABY));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Player.class, 150, true, true, null));
        this.targetSelector.addGoal(8, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.KOMODO_DRAGON_TARGETS)));
    }

    public boolean isControlledByLocalInstance() {
        return false;
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
        return AMSoundRegistry.KOMODO_DRAGON_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.KOMODO_DRAGON_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.KOMODO_DRAGON_HURT.get();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SpitTime")) {
            this.timeUntilSpit = compound.getInt("SpitTime");
        }
        this.setCommand(compound.getInt("KomodoCommand"));
        this.jostleCooldown = compound.getInt("JostlingCooldown");
        this.setSaddled(compound.getBoolean("Saddle"));

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpitTime", this.timeUntilSpit);
        compound.putInt("KomodoCommand", this.getCommand());
        compound.putBoolean("Saddle", this.isSaddled());
        compound.putInt("JostlingCooldown", this.jostleCooldown);
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && item == Items.ROTTEN_FLESH;
    }

    public void tick() {
        prevJostleAngle = this.getJostleAngle();
        super.tick();
        prevJostleProgress = jostleProgress;
        prevSitProgress = sitProgress;

        if(slaughterCooldown > 0){
            slaughterCooldown--;
        }
        if (!this.level.isClientSide && this.isAlive() && !this.isBaby() && --this.timeUntilSpit <= 0) {
            this.spawnAtLocation(AMItemRegistry.KOMODO_SPIT.get());
            this.timeUntilSpit = this.random.nextInt(12000) + 24000;
        }
        if(riderAttackCooldown > 0){
            riderAttackCooldown--;
        }
        if(this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player){
            Player rider = (Player)this.getControllingPassenger();
            if(rider.getLastHurtMob() != null && this.distanceTo(rider.getLastHurtMob()) < this.getBbWidth() + 3F && !this.isAlliedTo(rider.getLastHurtMob())){
                UUID preyUUID = rider.getLastHurtMob().getUUID();
                if (!this.getUUID().equals(preyUUID) && riderAttackCooldown == 0) {
                    doHurtTarget(rider.getLastHurtMob());
                    riderAttackCooldown = 20;
                }
            }
        }
        if (isJostling() && !hasJostlingSize){
            refreshDimensions();
            hasJostlingSize = true;
        }
        if (!isJostling() && hasJostlingSize){
            refreshDimensions();
            hasJostlingSize = false;
        }
        if (this.isJostling() && jostleProgress < 5F) {
            jostleProgress++;
        }
        if (!this.isJostling() && jostleProgress > 0F) {
            jostleProgress--;
        }
        if (this.isOrderedToSit() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!this.isOrderedToSit() && sitProgress > 0F) {
            sitProgress--;
        }
        if(this.getCommand() == 2 && !this.isVehicle()){
            this.setOrderedToSit(true);
        }else{
            this.setOrderedToSit(false);
        }
        if (jostleCooldown > 0) {
            jostleCooldown--;
        }
        if(!level.isClientSide){
            if(this.getJostleAngle() < nextJostleAngleFromServer){
                this.setJostleAngle(this.getJostleAngle() + 1);

            }
            if(this.getJostleAngle() > nextJostleAngleFromServer) {
                this.setJostleAngle(this.getJostleAngle() - 1);
            }
        }
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return isJostling() && !isBaby() ? JOSTLING_SIZE.scale(this.getScale()) : super.getDimensions(poseIn);
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

    public boolean doHurtTarget(Entity entityIn) {
        if (super.doHurtTarget(entityIn)) {
            if (entityIn instanceof LivingEntity) {
                int i = 5;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 20;
                }
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0));
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(potioneffectIn);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                Player player = (Player) passenger;
                return player;
            }
        }
        return null;
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float radius = 0;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        return (double)this.getBbHeight() - 0.2D + (double)(0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f);
    }



    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);

        if(item == Items.ROTTEN_FLESH){
            if(!isTame()){
                int size = itemstack.getCount();
                int tameAmount = 58 + random.nextInt(16);
                if(size > tameAmount){
                    this.tame(player);
                }
                itemstack.shrink(size);
                return InteractionResult.SUCCESS;
            }else if(this.getHealth() <= this.getMaxHealth()){
                usePlayerItem(player, hand, itemstack);
                this.heal(10);
                return InteractionResult.SUCCESS;
            }
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player)){
            if(isFood(itemstack)){
                this.setInLoveTime(600);
                this.usePlayerItem(player, hand, itemstack);
                return InteractionResult.SUCCESS;
            }else if(itemstack.getItem() == Items.SADDLE && !this.isSaddled()){
                this.usePlayerItem(player, hand, itemstack);
                this.setSaddled(true);
                return InteractionResult.SUCCESS;
            }else if(itemstack.getItem() == Items.SHEARS && this.isSaddled()){
                this.setSaddled(false);
                this.spawnAtLocation(Items.SADDLE);
                return InteractionResult.SUCCESS;
            }else{
                if(!player.isShiftKeyDown() && !this.isBaby() && this.isSaddled()){
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
                        this.setOrderedToSit(true);
                        return InteractionResult.SUCCESS;
                    } else {
                        this.setOrderedToSit(false);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return type;
    }

    protected EntityKomodoDragon(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if(!this.isBaby() || slaughterCooldown > 0){
            super.setTarget(entitylivingbaseIn);
        }
    }
    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.23F);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.KOMODO_DRAGON.get().create(p_241840_1_);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH || stack.getItem().getFoodProperties() != null && stack.getItem().getFoodProperties().isMeat();
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED).booleanValue();
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, Boolean.valueOf(saddled));
    }

    public boolean isJostling() {
        return this.entityData.get(JOSTLING).booleanValue();
    }

    public void setJostling(boolean jostle) {
        this.entityData.set(JOSTLING, jostle);
    }

    public float getJostleAngle() {
        return this.entityData.get(JOSTLE_ANGLE);
    }

    public void setJostleAngle(float scale) {
        this.entityData.set(JOSTLE_ANGLE, scale);
    }

    @Nullable
    public UUID getJostlingPartnerUUID() {
        return this.entityData.get(JOSTLER_UUID).orElse(null);
    }

    public void setJostlingPartnerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(JOSTLER_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public Entity getJostlingPartner() {
        UUID id = getJostlingPartnerUUID();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public void setJostlingPartner(@Nullable Entity jostlingPartner) {
        if (jostlingPartner == null) {
            this.setJostlingPartnerUUID(null);
        } else {
            this.setJostlingPartnerUUID(jostlingPartner.getUUID());
        }
    }

    public void pushBackJostling(EntityKomodoDragon entityMoose, float strength) {
        applyKnockbackFromMoose(strength, entityMoose.getX() - this.getX(), entityMoose.getZ() - this.getZ());
    }

    private void applyKnockbackFromMoose(float strength, double ratioX, double ratioZ) {
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(this, strength, ratioX, ratioZ);
        if (event.isCanceled()) return;
        strength = event.getStrength();
        ratioX = event.getRatioX();
        ratioZ = event.getRatioZ();
        if (!(strength <= 0.0F)) {
            this.hasImpulse = true;
            Vec3 vector3d = this.getDeltaMovement();
            Vec3 vector3d1 = (new Vec3(ratioX, 0.0D, ratioZ)).normalize().scale(strength);
            this.setDeltaMovement(vector3d.x / 2.0D - vector3d1.x, 0.3F, vector3d.z / 2.0D - vector3d1.z);
        }
    }

    public boolean canJostleWith(EntityKomodoDragon moose) {
        return !moose.isOrderedToSit() && !moose.isVehicle() && !moose.isBaby() && moose.getJostlingPartnerUUID() == null && moose.jostleCooldown == 0;
    }

    public void playJostleSound() {
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Items.SADDLE);
            }
        }
        this.setSaddled(false);
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.heal(10);
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    public boolean isMaid() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && (s.toLowerCase().contains("maid") || s.toLowerCase().contains("coda"));

    }
}
