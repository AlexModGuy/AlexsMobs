package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class EntityShoebill extends Animal implements IAnimatedEntity, ITargetsDroppedItems {

    public static final Animation ANIMATION_FISH = Animation.create(40);
    public static final Animation ANIMATION_BEAKSHAKE = Animation.create(20);
    public static final Animation ANIMATION_ATTACK = Animation.create(20);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityShoebill.class, EntityDataSerializers.BOOLEAN);
    public float prevFlyProgress;
    public float flyProgress;
    public int revengeCooldown = 0;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isLandNavigator;
    public int fishingCooldown = 1200 + random.nextInt(1200);
    public int lureLevel = 0;
    public int luckLevel = 0;
    public static final Predicate<LivingEntity> TARGET_BABY  = (animal) -> {
        return animal.isBaby();
    };

    protected EntityShoebill(EntityType type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.shoebillSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SHOEBILL_HURT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SHOEBILL_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SHOEBILL_HURT.get();
    }

    public boolean isFood(ItemStack stack) {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev && source.getEntity() != null && !(source.getEntity() instanceof AbstractFish)) {
            double range = 15;
            int fleeTime = 100 + getRandom().nextInt(150);
            this.revengeCooldown = fleeTime;
            List<? extends EntityShoebill> list = this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range / 2, range));
            for (EntityShoebill gaz : list) {
                gaz.revengeCooldown = fleeTime;
            }
        }
        return prev;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 0.7F, false);
            this.navigation = new DirectPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AnimalAIWadeSwimming(this));
        this.goalSelector.addGoal(1, new ShoebillAIFish(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new ShoebillAIFlightFlee(this));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, Ingredient.of(AMTagRegistry.SHOEBILL_FOODSTUFFS), false));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1D, 1400));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, AbstractFish.class, 30, false, true, null));
        this.targetSelector.addGoal(2, new CreatureAITargetItems(this, false, 10));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, Player.class)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, EntityAlligatorSnappingTurtle.class, 40, false, false, TARGET_BABY));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 40, false, false, TARGET_BABY));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, EntityCrocodile.class, 40, false, false, TARGET_BABY));
        this.targetSelector.addGoal(7, new EntityAINearestTarget3D(this, EntityTerrapin.class, 100, false, true, null));

    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void tick() {
        super.tick();
        if(this.isInWater()){
            maxUpStep = 1.2F;
        }else{
            maxUpStep = 0.6F;
        }
        prevFlyProgress = flyProgress;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (revengeCooldown > 0) {
            revengeCooldown--;
        }
        if (revengeCooldown == 0 && this.getLastHurtByMob() != null) {
            this.setLastHurtByMob(null);
        }
        if (!this.level().isClientSide) {
            if(fishingCooldown > 0){
                fishingCooldown--;
            }
            if(this.getAnimation() == NO_ANIMATION && this.getRandom().nextInt(700) == 0){
                this.setAnimation(ANIMATION_BEAKSHAKE);
            }
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.revengeCooldown > 0 && !this.isFlying()) {
                if (this.onGround || this.isInWater()) {
                    this.setFlying(false);
                }
            }
            if (isFlying()) {
                this.setNoGravity(true);
            } else {
                this.setNoGravity(false);
            }
        }
        if (!this.level().isClientSide && this.getTarget() != null && this.hasLineOfSight(this.getTarget()) && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 9) {
            float f1 = this.getYRot() * ((float) Math.PI / 180F);
            getTarget().knockback(0.3F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
            this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("FishingTimer", this.fishingCooldown);
        compound.putInt("FishingLuck", this.lucklevel());
        compound.putInt("FishingLure", this.lurelevel());
        compound.putInt("RevengeCooldownTimer", this.revengeCooldown);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.fishingCooldown = compound.getInt("FishingTimer");
        this.luckLevel = compound.getInt("FishingLuck");
        this.lureLevel = compound.getInt("FishingLure");
        this.revengeCooldown = compound.getInt("RevengeCooldownTimer");

    }


    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }


    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
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
        return new Animation[]{ANIMATION_FISH, ANIMATION_BEAKSHAKE, ANIMATION_ATTACK};
    }

    public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
        ItemStack lvt_3_1_ = p_230254_1_.getItemInHand(p_230254_2_);
         if (lvt_3_1_.getItem() == AMBlockRegistry.TERRAPIN_EGG.get().asItem() && this.isAlive()) {
             if(this.luckLevel < 10) {
                 luckLevel = Mth.clamp(luckLevel + 1, 0, 10);
                 for (int i = 0; i < 6 + random.nextInt(3); i++) {
                     double d2 = this.random.nextGaussian() * 0.02D;
                     double d0 = this.random.nextGaussian() * 0.02D;
                     double d1 = this.random.nextGaussian() * 0.02D;
                     this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, lvt_3_1_), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
                 }
                 this.gameEvent(GameEvent.EAT);
                 this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
                 lvt_3_1_.shrink(1);
                 return net.minecraft.world.InteractionResult.sidedSuccess(this.level().isClientSide);
             }else{
                 if(this.getAnimation() == NO_ANIMATION){
                     this.setAnimation(ANIMATION_BEAKSHAKE);
                 }
                 return InteractionResult.SUCCESS;
             }
         } else if (lvt_3_1_.getItem() == AMBlockRegistry.CROCODILE_EGG.get().asItem() && this.isAlive()) {
             if(this.lureLevel < 10){
                 lureLevel = Mth.clamp(lureLevel + 1, 0, 10);
                 fishingCooldown = Mth.clamp(fishingCooldown - 200, 200, 2400);
                 for (int i = 0; i < 6 + random.nextInt(3); i++) {
                     double d2 = this.random.nextGaussian() * 0.02D;
                     double d0 = this.random.nextGaussian() * 0.02D;
                     double d1 = this.random.nextGaussian() * 0.02D;
                     this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, lvt_3_1_), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
                 }
                 lvt_3_1_.shrink(1);
                 this.gameEvent(GameEvent.EAT);
                 this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
                 return net.minecraft.world.InteractionResult.sidedSuccess(this.level().isClientSide);
             }else{
                 if(this.getAnimation() == NO_ANIMATION){
                     this.setAnimation(ANIMATION_BEAKSHAKE);
                 }
                 return InteractionResult.SUCCESS;
             }

         } else {
            return super.mobInteract(p_230254_1_, p_230254_2_);
        }
    }


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.SHOEBILL.get().create(serverWorld);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.is(AMTagRegistry.SHOEBILL_FOODSTUFFS) || stack.getItem() == AMItemRegistry.BLOBFISH.get() && luckLevel < 10 || stack.getItem() == AMBlockRegistry.CROCODILE_EGG.get().asItem() && lureLevel < 10;
    }

    public void resetFishingCooldown(){
        fishingCooldown = Math.max(1200 + random.nextInt(1200) - lureLevel * 120, 200);
    }
    @Override
    public void onGetItem(ItemEntity e) {
        this.gameEvent(GameEvent.EAT);
        this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
        if(e.getItem().getItem() == AMItemRegistry.BLOBFISH.get()){
            luckLevel = Mth.clamp(luckLevel + 1, 0, 10);
        }
        if(e.getItem().getItem() == AMBlockRegistry.CROCODILE_EGG.get().asItem()){
            lureLevel = Mth.clamp(lureLevel + 1, 0, 10);
        }
        this.heal(5);
    }
}
