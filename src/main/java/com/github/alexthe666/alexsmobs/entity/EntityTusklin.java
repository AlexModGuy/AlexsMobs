package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityTusklin extends Animal implements IAnimatedEntity {

    public static final Animation ANIMATION_RUT = Animation.create(26);
    public static final Animation ANIMATION_GORE_L = Animation.create(25);
    public static final Animation ANIMATION_GORE_R = Animation.create(25);
    public static final Animation ANIMATION_FLING = Animation.create(15);
    public static final Animation ANIMATION_BUCK = Animation.create(15);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(EntityTusklin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PASSIVETICKS = SynchedEntityData.defineId(EntityTusklin.class, EntityDataSerializers.INT);
    private int animationTick;
    private Animation currentAnimation;
    private int ridingTime = 0;
    private int entityToLaunchId = -1;
    private int conversionTime = 0;

    protected EntityTusklin(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.tusklinSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canTusklinSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getRawBrightness(pos, 0) > 8 && (worldIn.getBlockState(pos.below()).getMaterial().isSolid() || worldIn.getBlockState(pos.below()).is(Blocks.SNOW_BLOCK));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40D).add(Attributes.ATTACK_DAMAGE, 9.0D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.KNOCKBACK_RESISTANCE, 0.9F);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.TUSKLIN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TUSKLIN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TUSKLIN_HURT.get();
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    public boolean isInNether() {
        return this.level.dimension() == Level.NETHER && !this.isNoAi();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIMeleeNearby(this, 15, 1.25D));
        this.goalSelector.addGoal(3, new TameableAIRide(this, 2D, false) {
            @Override
            public boolean shouldMoveForward() {
                return true;
            }

            @Override
            public boolean shouldMoveBackwards() {
                return false;
            }
        });
        this.goalSelector.addGoal(4, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 120, 0.6F, 14, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, 100, true, false, this::isAngryAt));
    }

    public boolean isAngryAt(LivingEntity p_21675_) {
        return this.canAttack(p_21675_);
    }

    @Nullable
    public Entity getControllingPassenger() {
        if (this.isSaddled()) {
            for (Entity passenger : this.getPassengers()) {
                if (passenger instanceof Player) {
                    Player player = (Player) passenger;
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        boolean prev = super.canAttack(entity);

        if (entity instanceof Player && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().equals(entity))) {
            if (this.getPassiveTicks() > 0 || isMushroom(entity.getItemInHand(InteractionHand.MAIN_HAND)) || isMushroom(entity.getItemInHand(InteractionHand.OFF_HAND))) {
                return false;
            }
        }
        return prev;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            int anim = this.random.nextInt(3);
            if (anim == 0) {
                this.setAnimation(ANIMATION_FLING);
            } else if (anim == 1) {
                this.setAnimation(ANIMATION_GORE_L);
            } else if (anim == 2) {
                this.setAnimation(ANIMATION_GORE_R);
            }
        }
        return true;
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float radius = 0.4F;
            if (this.getAnimation() == ANIMATION_GORE_L || this.getAnimation() == ANIMATION_GORE_R) {
                if (this.getAnimationTick() <= 4) {
                    radius -= this.getAnimationTick() * 0.1F;
                } else {
                    radius -= -0.4F + Math.min(this.getAnimationTick() - 4, 4) * 0.1F;
                }
            }
            if (this.getAnimation() == ANIMATION_BUCK) {
                if (this.getAnimationTick() < 5) {
                    radius -= this.getAnimationTick() * 0.1F;
                } else if (this.getAnimationTick() < 10) {
                    radius -= 0.4F - (this.getAnimationTick() - 5) * 0.1F;
                }
            }
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    public double getPassengersRidingOffset() {
        float f = this.animationPosition;
        float f1 = this.animationSpeed;
        float f2 = 0;
        if (this.getAnimation() == ANIMATION_FLING) {
            if (this.getAnimationTick() <= 3F) {
                f2 = this.getAnimationTick() * -0.1F;
            } else {
                f2 = -0.3F + Mth.clamp(this.getAnimationTick() - 3, 0, 3) * 0.1F;
            }
        }
        if (this.getAnimation() == ANIMATION_BUCK) {
            if (this.getAnimationTick() < 5) {
                f2 = (this.getAnimationTick() * 0.2F) * 0.8F;
            } else if (this.getAnimationTick() < 10) {
                f2 = (0.8F - (this.getAnimationTick() - 5) * 0.2F) * 0.8F;
            }
        }
        return (double) this.getBbHeight() - 0.3D + (float) (Math.abs(Math.sin(f * 0.7F) * (double) f1 * 0.0625F * 1.6F)) + f2;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (item == Items.SADDLE && !this.isSaddled() && !this.isBaby()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setSaddled(true);
            return InteractionResult.SUCCESS;
        }
        if (item == AMItemRegistry.PIGSHOES.get() && this.getShoeStack().isEmpty() && !this.isBaby()) {
            this.setShoeStack(itemstack.copy());
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        if (isMushroom(itemstack) && (this.getPassiveTicks() <= 0 || this.getHealth() < this.getMaxHealth())) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.heal(6);
            this.setPassiveTicks(this.getPassiveTicks() + 1200);
            return InteractionResult.SUCCESS;
        }
        InteractionResult type = super.mobInteract(player, hand);
        if (type != InteractionResult.SUCCESS && !isFood(itemstack)) {
            if (!player.isShiftKeyDown() && !this.isBaby() && this.isSaddled() && this.getAnimation() != ANIMATION_BUCK) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.RED_MUSHROOM);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(SADDLED, false);
        this.getEntityData().define(PASSIVETICKS, 0);
    }

    public void addAdditionalSaveData(CompoundTag p_31808_) {
        super.addAdditionalSaveData(p_31808_);
        if (!this.getShoeStack().isEmpty()) {
            p_31808_.put("ShoeItem", this.getShoeStack().save(new CompoundTag()));
        }
        p_31808_.putInt("PassiveTicks", this.getPassiveTicks());

        p_31808_.putBoolean("Saddle", this.isSaddled());
    }

    public void readAdditionalSaveData(CompoundTag p_31795_) {
        super.readAdditionalSaveData(p_31795_);
        this.setSaddled(p_31795_.getBoolean("Saddle"));
        this.setPassiveTicks(p_31795_.getInt("PassiveTicks"));
        CompoundTag compoundtag = p_31795_.getCompound("ShoeItem");
        if (compoundtag != null && !compoundtag.isEmpty()) {
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (itemstack.isEmpty()) {
                AlexsMobs.LOGGER.warn("Unable to load item from: {}", compoundtag);
            }
            this.setShoeStack(itemstack);
        }
    }

    public boolean isMushroom(ItemStack stack) {
        return stack.is(Items.BROWN_MUSHROOM);
    }

    public int getPassiveTicks() {
        return this.entityData.get(PASSIVETICKS);
    }

    private void setPassiveTicks(int passiveTicks) {
        this.entityData.set(PASSIVETICKS, passiveTicks);
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED).booleanValue();
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, Boolean.valueOf(saddled));
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Items.SADDLE);
            }
        }
        if (!this.getShoeStack().isEmpty()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(this.getShoeStack().copy());
            }
        }
        this.setSaddled(false);
        this.setShoeStack(ItemStack.EMPTY);
    }

    public ItemStack getShoeStack() {
        return this.getItemBySlot(EquipmentSlot.FEET);
    }

    public void setShoeStack(ItemStack shoe) {
        this.setItemSlot(EquipmentSlot.FEET, shoe);
    }

    public void tick() {
        super.tick();
        if(isInNether()) {
            conversionTime++;
            if (conversionTime > 300 && !level.isClientSide) {
                Hoglin hoglin = this.convertTo(EntityType.HOGLIN, false);
                if(hoglin != null){
                    hoglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                    this.dropEquipment();
                    level.addFreshEntity(hoglin);
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
        if (entityToLaunchId != -1 && this.isAlive()) {
            Entity launch = this.level.getEntity(entityToLaunchId);
            this.ejectPassengers();
            entityToLaunchId = -1;
            if (launch != null && !launch.isPassenger()) {
                if (launch instanceof LivingEntity) {
                    launch.setPos(this.getEyePosition().add(0, 1, 0));
                    float rot = 180F + this.getYRot();
                    float strength = (float) (getLaunchStrength() * (1.0D - ((LivingEntity) launch).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                    float x = Mth.sin(rot * ((float) Math.PI / 180F));
                    float z = -Mth.cos(rot * ((float) Math.PI / 180F));
                    if (!(strength <= 0.0D)) {
                        launch.hasImpulse = true;
                        Vec3 vec3 = this.getDeltaMovement();
                        Vec3 vec31 = vec3.add((new Vec3(x, 0.0D, z)).normalize().scale(strength));
                        launch.setDeltaMovement(vec31.x, strength, vec31.z);
                    }
                }
            }
        }
        if (this.getAnimation() == ANIMATION_BUCK && this.getAnimationTick() >= 5) {
            Entity passenger = this.getControllingPassenger();
            if (passenger instanceof LivingEntity) {
                entityToLaunchId = passenger.getId();
            }
        }
        if (!level.isClientSide) {
            if (this.isVehicle()) {
                ridingTime++;
                if (ridingTime >= this.getMaxRidingTime() && this.getAnimation() != ANIMATION_BUCK) {
                    this.setAnimation(ANIMATION_BUCK);
                }
            } else {
                ridingTime = 0;
            }
            if (this.isAlive() && ridingTime > 0 && this.getDeltaMovement().horizontalDistanceSqr() > 0.1D) {
                for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                    if (!(entity instanceof EntityTusklin) && !entity.isPassengerOfSameVehicle(this)) {
                        entity.hurt(DamageSource.mobAttack(this), 4F + random.nextFloat() * 3.0F);
                        if (entity.isOnGround()) {
                            double d0 = entity.getX() - this.getX();
                            double d1 = entity.getZ() - this.getZ();
                            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                            float f = 0.5F;
                            entity.push(d0 / d2 * f, f, d1 / d2 * f);
                        }
                    }
                }
                maxUpStep = 2;
            }
            if (this.getTarget() != null && this.hasLineOfSight(this.getTarget()) && distanceTo(this.getTarget()) < this.getTarget().getBbWidth() + this.getBbWidth() + 1.8F) {
                if (this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 6) {
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackTarget(this.getTarget(), 0.9F, 0F);
                }
                if ((this.getAnimation() == ANIMATION_GORE_L) && this.getAnimationTick() == 6) {
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackTarget(this.getTarget(), 0.5F, -90F);
                }
                if ((this.getAnimation() == ANIMATION_GORE_R) && this.getAnimationTick() == 6) {
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackTarget(this.getTarget(), 0.5F, 90F);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_RUT && this.getAnimationTick() == 23) {
            if (level.getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && getRandom().nextInt(3) == 0) {
                if (this.isBaby()) {
                    if (level.getBlockState(this.blockPosition()).getMaterial().isReplaceable() && random.nextInt(3) == 0) {
                        level.setBlockAndUpdate(this.blockPosition(), Blocks.BROWN_MUSHROOM.defaultBlockState());
                        this.playSound(SoundEvents.CROP_PLANTED, this.getSoundVolume(), this.getVoicePitch());
                    }
                }
                this.level.levelEvent(2001, blockPosition().below(), Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                this.level.setBlock(blockPosition().below(), Blocks.DIRT.defaultBlockState(), 2);
                this.heal(5);
            }
        }
        if (!level.isClientSide && this.getAnimation() == NO_ANIMATION && getRandom().nextInt(isBaby() ? 140 : 70) == 0 && (this.getLastHurtByMob() == null || this.distanceTo(this.getLastHurtByMob()) > 30)) {
            if (level.getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && getRandom().nextInt(3) == 0) {
                this.setAnimation(ANIMATION_RUT);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private float getLaunchStrength() {
        return this.getShoeStack().is(AMItemRegistry.PIGSHOES.get()) ? 0.4F : 0.9F;
    }

    private int getMaxRidingTime() {
        return this.getShoeStack().is(AMItemRegistry.PIGSHOES.get()) ? 160 : 60;
    }

    private void knockbackTarget(LivingEntity entity, float strength, float angle) {
        float rot = getYRot() + angle;
        if(entity != null){
            entity.knockback(strength, Mth.sin(rot * ((float) Math.PI / 180F)), -Mth.cos(rot * ((float) Math.PI / 180F)));
        }
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
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableMob.AgeableMobGroupData(0.34F);
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }


    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_RUT, ANIMATION_GORE_L, ANIMATION_GORE_R, ANIMATION_FLING, ANIMATION_BUCK};
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return AMEntityRegistry.TUSKLIN.get().create(level);
    }
}
