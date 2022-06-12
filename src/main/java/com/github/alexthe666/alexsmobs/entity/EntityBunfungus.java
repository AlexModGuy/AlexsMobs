package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class EntityBunfungus extends PathfinderMob implements IAnimatedEntity {

    public static final Animation ANIMATION_SLAM = Animation.create(20);
    public static final Animation ANIMATION_BELLY = Animation.create(10);
    public static final Animation ANIMATION_EAT = Animation.create(20);
    private static final EntityDataAccessor<Boolean> JUMP_ACTIVE = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BEGGING = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CARROTED = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TRANSFORMS_IN = SynchedEntityData.defineId(EntityBunfungus.class, EntityDataSerializers.INT);
    public float jumpProgress;
    public float prevJumpProgress;
    public float reboundProgress;
    public float prevReboundProgress;
    public float sleepProgress;
    public float prevSleepProgress;
    public float interestedProgress;
    public float prevInterestedProgress;
    private int animationTick;
    private Animation currentAnimation;
    public int prevTransformTime;
    public static final int MAX_TRANSFORM_TIME = 50;

    protected EntityBunfungus(EntityType t, Level lvl) {
        super(t, lvl);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 80.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.21F);
    }

    public void playAmbientSound() {
        if(!this.isSleeping()){
            super.playAmbientSound();
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.BUNFUNGUS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BUNFUNGUS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BUNFUNGUS_HURT.get();
    }

    public boolean removeWhenFarAway(double p_27598_) {
        return false;
    }

    public static boolean canBunfungusSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return worldIn.getBlockState(pos.below()).canOcclude();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mungusSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BunfungusAIMelee(this));
        this.goalSelector.addGoal(2, new BunfungusAIBeg(this, 1.0D));
        this.goalSelector.addGoal(3, new AnimalAIWanderRanged(this, 60, 1.0D, 16, 7){
            public boolean canUse(){
                return super.canUse() && EntityBunfungus.this.canUseComplexAI();
            }
        });
        this.goalSelector.addGoal(4, new AnimalAILeapRandomly(this, 60, 7){
            public boolean canUse(){
                return super.canUse() && EntityBunfungus.this.canUseComplexAI();
            }
        });
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 10.0F){
            public boolean canUse(){
                return super.canUse() && EntityBunfungus.this.canUseComplexAI();
            }
        });
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this){
            public boolean canUse(){
                return super.canUse() && EntityBunfungus.this.canUseComplexAI();
            }
        });
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (p_28879_) -> {
            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
        }));
    }

    private boolean canUseComplexAI() {
        return !this.isRabbitForm() && !this.isSleeping();
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(JUMP_ACTIVE, Boolean.valueOf(false));
        this.entityData.define(SLEEPING, Boolean.valueOf(false));
        this.entityData.define(BEGGING, Boolean.valueOf(false));
        this.entityData.define(CARROTED, Boolean.valueOf(false));
        this.entityData.define(TRANSFORMS_IN, 0);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void tick() {
        super.tick();
        prevJumpProgress = jumpProgress;
        prevReboundProgress = reboundProgress;
        prevSleepProgress = sleepProgress;
        prevInterestedProgress = interestedProgress;
        prevTransformTime = this.transformsIn();
        if (!level.isClientSide) {
            this.entityData.set(JUMP_ACTIVE, !this.isOnGround());
        }
        if (this.entityData.get(JUMP_ACTIVE) && !isInWaterOrBubble()) {
            if (jumpProgress < 5F) {
                jumpProgress += 0.5F;
                if (reboundProgress > 0) {
                    reboundProgress--;
                }
            }
            if (jumpProgress >= 5F) {
                if (reboundProgress < 5F) {
                    reboundProgress += 0.5F;
                }
            }
        } else {
            if (reboundProgress > 0) {
                reboundProgress = Math.max(reboundProgress - 1F, 0);
            }
            if (jumpProgress > 0) {
                jumpProgress = Math.max(jumpProgress - 1F, 0);
            }
        }
        if (this.isSleepingPose() && sleepProgress < 5F) {
            sleepProgress++;
        }
        if (!this.isSleepingPose() && sleepProgress > 0F) {
            sleepProgress--;
        }
        if (this.isBegging() && interestedProgress < 5F) {
            interestedProgress++;
        }
        if (!this.isBegging() && interestedProgress > 0F) {
            interestedProgress--;
        }
        LivingEntity target = this.getTarget();
        if (!level.isClientSide) {
            if (target != null && target.isAlive()) {
                if(this.isSleeping()){
                    this.setSleeping(false);
                }
                double dist = this.distanceTo(target);
                boolean flag = false;
                if (dist < 3.5D && this.getAnimation() == ANIMATION_BELLY && this.getAnimationTick() == 5) {
                    for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D))) {
                        if (entity == target || entity instanceof Monster) {
                            flag = true;
                            launch(entity);
                            entity.hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                        }
                    }
                }
                if (dist < 2.5D && this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() == 5) {
                    for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D))) {
                        if (entity == target || entity instanceof Monster) {
                            flag = true;
                            entity.knockback(0.2F, entity.getX() - this.getX(), entity.getZ() - this.getZ());
                            entity.hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                        }
                    }
                }
                if(flag){
                    this.playSound(AMSoundRegistry.BUNFUNGUS_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
                }
            }
            if (this.tickCount % 40 == 0) {
                this.heal(1);
            }
        }
        if (this.getAnimation() == NO_ANIMATION && this.isCarrot(this.getItemInHand(InteractionHand.MAIN_HAND))) {
            this.setAnimation(ANIMATION_EAT);
        }
        if (this.getAnimation() == ANIMATION_EAT) {
            if (this.getAnimationTick() % 4 == 0) {
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            }
            if (this.getAnimationTick() >= 18) {
                ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                    this.setCarroted(true);
                    this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000));
                    this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1000, 1));
                    this.heal(8);
                }
            }else{
                for (int i = 0; i < 3; i++) {
                    double d2 = this.random.nextGaussian() * 0.02D;
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
                }
            }
        }
        if(!level.isClientSide){
            if(this.transformsIn() > 0){
                this.setTransformsIn(this.transformsIn() - 1);
            }
        }
        if(isRabbitForm() && level.isClientSide){
            for (int i = 0; i < 3; i++) {
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                float f1 = (EntityBunfungus.MAX_TRANSFORM_TIME - this.transformsIn()) / (float)EntityBunfungus.MAX_TRANSFORM_TIME;
                float scale = f1 * 0.5F + 0.15F;
                this.level.addParticle(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(), this.getRandomX(scale), this.getY(this.random.nextDouble() * scale), this.getRandomZ(scale), d0, d1, d2);
            }
        }
        if(isSleeping() && level.isClientSide && random.nextFloat() < 0.3F){
            double d0 = this.random.nextGaussian() * 0.02D;
            float radius = this.getBbWidth() * (0.7F + random.nextFloat() * 0.1F);
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle)) + random.nextFloat() * 0.5F - 0.25F;
            double extraZ = radius * Mth.cos(angle) + random.nextFloat() * 0.5F - 0.25F;
            ParticleOptions data = random.nextFloat() < 0.3F ? AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get() : AMParticleRegistry.FUNGUS_BUBBLE.get();
            this.level.addParticle(data, this.getX() + extraX, this.getY() + random.nextFloat() * 0.1F, this.getZ() + extraZ, 0, d0, 0);

        }
        if (!this.level.isClientSide) {
            if (this.level.isDay() && this.getTarget() == null && !this.isBegging() && !this.isInWaterOrBubble()) {
                if (tickCount % 10 == 0 && this.getRandom().nextInt(300) == 0) {
                    this.setSleeping(true);
                }
            } else if (this.isSleeping()) {
                this.setSleeping(false);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void launch(LivingEntity target) {
        if (target.isOnGround()) {
            double d0 = target.getX() - this.getX();
            double d1 = target.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = 6 + random.nextFloat() * 2;
            target.push(d0 / d2 * f, 0.6F + random.nextFloat() * 0.7F, d1 / d2 * f);
        }
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING).booleanValue();
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, Boolean.valueOf(sleeping));
    }

    public boolean isSleepingPose() {
        return this.isSleeping() || this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() < 10;
    }

    public boolean isCarroted() {
        return this.entityData.get(CARROTED).booleanValue();
    }

    public void setCarroted(boolean head) {
        this.entityData.set(CARROTED, head);
    }

    public boolean isBegging() {
        return this.entityData.get(BEGGING).booleanValue() && this.getAnimation() != ANIMATION_EAT;
    }

    public void setBegging(boolean begging) {
        this.entityData.set(BEGGING, Boolean.valueOf(begging));
    }

    public int transformsIn() {
        return Math.min(this.entityData.get(TRANSFORMS_IN).intValue(), MAX_TRANSFORM_TIME);
    }

    public boolean isRabbitForm() {
        return this.transformsIn() > 0;
    }

    public void setTransformsIn(int time) {
        this.entityData.set(TRANSFORMS_IN, time);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            if (isCarrot(itemstack) && this.getMainHandItem().isEmpty()) {
                ItemStack cop = itemstack.copy();
                cop.setCount(1);
                this.setItemInHand(InteractionHand.MAIN_HAND, cop);
                if(!player.isCreative()){
                    itemstack.shrink(1);
                }
            }
        }
        return type;
    }

    public void travel(Vec3 travelVector) {
        if (!this.isRabbitForm() && !this.isSleeping()) {
            super.travel(travelVector);
        }else{
            super.travel(Vec3.ZERO);
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

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_EAT, ANIMATION_BELLY, ANIMATION_SLAM};
    }

    public boolean isCarrot(ItemStack stack) {
        return stack.getItem() == Items.CARROT || stack.getItem() == Items.GOLDEN_CARROT;
    }

    public boolean defendsMungusAgainst(LivingEntity lastHurtByMob) {
        return !(lastHurtByMob instanceof Player) || this.isCarroted();
    }

    public void onJump() {
        //sound was too annoying
        //this.playSound(AMSoundRegistry.BUNFUNGUS_JUMP, this.getSoundVolume(), this.getVoicePitch());
    }
}
