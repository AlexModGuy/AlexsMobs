package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class EntitySnowLeopard extends Animal implements IAnimatedEntity, ITargetsDroppedItems {

    public static final Animation ANIMATION_ATTACK_R = Animation.create(13);
    public static final Animation ANIMATION_ATTACK_L = Animation.create(13);
    private int animationTick;
    private Animation currentAnimation;
    public float prevSneakProgress;
    public float sneakProgress;
    public float prevTackleProgress;
    public float tackleProgress;
    public float prevSitProgress;
    public float sitProgress;
    private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SL_SNEAKING = SynchedEntityData.defineId(EntitySnowLeopard.class, EntityDataSerializers.BOOLEAN);
    private boolean hasSlowedDown = false;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    public float prevSleepProgress;
    public float sleepProgress;

    protected EntitySnowLeopard(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.maxUpStep = 2F;
    }


    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.snowLeopardSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static <T extends Mob> boolean canSnowLeopardSpawn(EntityType<EntitySnowLeopard> snowleperd, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, Random random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
        return (blockstate.is(BlockTags.BASE_STONE_OVERWORLD) || blockstate.is(Blocks.DIRT) || blockstate.is(Blocks.GRASS_BLOCK)) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.MOOSE_RIBS.get() || stack.getItem() == AMItemRegistry.COOKED_MOOSE_RIBS.get();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(3, new SnowLeopardAIMelee(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this,  1.0D, 70));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getAllTags().getTag(AMTagRegistry.SNOW_LEOPARD_TARGETS))));
        this.targetSelector.addGoal(3, new CreatureAITargetItems(this, false, 30));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30D).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.35F).add(Attributes.FOLLOW_RANGE, 64F);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SNOW_LEOPARD_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SNOW_LEOPARD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SNOW_LEOPARD_HURT;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, Boolean.valueOf(false));
        this.entityData.define(SLEEPING, Boolean.valueOf(false));
        this.entityData.define(SL_SNEAKING, Boolean.valueOf(false));
        this.entityData.define(TACKLING, Boolean.valueOf(false));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setSitting(boolean bar) {
        this.entityData.set(SITTING, Boolean.valueOf(bar));
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLING).booleanValue();
    }

    public void setTackling(boolean bar) {
        this.entityData.set(TACKLING, Boolean.valueOf(bar));
    }

    public boolean isSLSneaking() {
        return this.entityData.get(SL_SNEAKING).booleanValue();
    }

    public void setSlSneaking(boolean bar) {
        this.entityData.set(SL_SNEAKING, Boolean.valueOf(bar));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.SNOW_LEOPARD.get().create(serverWorld);
    }

    public void tick(){
        super.tick();
        this.prevSitProgress = sitProgress;
        this.prevSneakProgress = sneakProgress;
        this.prevTackleProgress = tackleProgress;
        this.prevSleepProgress = sleepProgress;
        if (this.isSitting() && sitProgress < 5F) {
            sitProgress += 0.5F;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress -= 0.5F;
        }
        if (this.isSLSneaking() && sneakProgress < 5F) {
            sneakProgress++;
        }
        if (!isSLSneaking() && sneakProgress > 0F) {
            sneakProgress--;
        }
        if (this.isTackling() && tackleProgress < 3F) {
            tackleProgress++;
        }
        if (!isTackling() && tackleProgress > 0F) {
            tackleProgress--;
        }
        if (this.isSleeping() && sleepProgress < 5F) {
            sleepProgress += 0.5F;
        }
        if (!isSleeping() && sleepProgress > 0F) {
            sleepProgress -= 0.5F;
        }
        if(isSLSneaking() && !hasSlowedDown){
            hasSlowedDown = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        }
        if(!isSLSneaking() && hasSlowedDown){
            hasSlowedDown = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
        }
        if(isTackling()){
            this.yBodyRot = this.getYRot();
        }
        if(!level.isClientSide) {
            if (this.getTarget() != null && (this.isSitting() || this.isSleeping())) {
                this.setSitting(false);
                this.setSleeping(false);
            }
            if ((isSitting() || isSleeping()) && (++sittingTime > maxSitTime || this.getTarget() != null || this.isInLove() || this.isInWaterOrBubble())) {
                this.setSitting(false);
                this.setSleeping(false);
                sittingTime = 0;
                maxSitTime = 100 + random.nextInt(50);
            }
            if (this.getTarget() == null && this.getDeltaMovement().lengthSqr() < 0.03D && this.getAnimation() == NO_ANIMATION && !this.isSleeping() && !this.isSitting() && !this.isInWaterOrBubble() && random.nextInt(340) == 0) {
                sittingTime = 0;
                if (this.getRandom().nextInt(2) != 0) {
                    maxSitTime = 200 + random.nextInt(800);
                    this.setSitting(true);
                    this.setSleeping(false);
                } else {
                    maxSitTime = 2000 + random.nextInt(2600);
                    this.setSitting(false);
                    this.setSleeping(true);
                }
            }
        }
        LivingEntity attackTarget = this.getTarget();
        if (attackTarget != null) {
            if (distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 0.6D && this.hasLineOfSight(attackTarget)) {
                if (this.getAnimation() == ANIMATION_ATTACK_L && this.getAnimationTick() == 7) {
                    doHurtTarget(attackTarget);
                    float rot = getYRot() + 90;
                    attackTarget.knockback(0.5F, Mth.sin(rot * ((float) Math.PI / 180F)), -Mth.cos(rot * ((float) Math.PI / 180F)));
                }
                if (this.getAnimation() == ANIMATION_ATTACK_R && this.getAnimationTick() == 7) {
                    doHurtTarget(attackTarget);
                    float rot = getYRot() - 90;
                    attackTarget.knockback(0.5F, Mth.sin(rot * ((float) Math.PI / 180F)), -Mth.cos(rot * ((float) Math.PI / 180F)));
                }

            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev) {
            sittingTime = 0;
            this.setSleeping(false);
            this.setSitting(false);
        }
        return prev;
    }

    public void travel(Vec3 vec3d) {
        if (this.isSitting() || this.isSleeping()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    protected boolean isImmobile() {
        return super.isImmobile();
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING).booleanValue();
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, Boolean.valueOf(sleeping));
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
        return new Animation[]{ANIMATION_ATTACK_L, ANIMATION_ATTACK_R};
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties() != null && stack.getItem().getFoodProperties().isMeat();
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.heal(5);
    }
}
