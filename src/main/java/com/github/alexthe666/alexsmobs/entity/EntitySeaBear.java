package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class EntitySeaBear extends WaterAnimal implements IAnimatedEntity {

    public static final Animation ANIMATION_ATTACK = Animation.create(17);
    public static final Animation ANIMATION_POINT = Animation.create(25);
    public float prevOnLandProgress;
    public float onLandProgress;
    public int circleCooldown = 0;
    private int animationTick;
    private Animation currentAnimation;
    private BlockPos lastCircle = null;
    public static final Predicate<LivingEntity> SOMBRERO = (player) -> {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.SOMBRERO.get());
    };

    protected EntitySeaBear(EntityType entityType, Level level) {
        super(entityType, level);
        this.moveControl = new AquaticMoveController(this, 1F, 10);
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !requiresCustomPersistence();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 200.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.325F);
    }

    public static boolean isMobSafe(Entity entity) {
        if(entity instanceof Player && ((Player) entity).isCreative()){
            return true;
        }
        BlockState state = entity.level.getBlockState(entity.blockPosition().below());
        return state.is(AMBlockRegistry.SAND_CIRCLE.get()) || state.is(AMBlockRegistry.RED_SAND_CIRCLE.get());
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

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new AttackAI());
        this.goalSelector.addGoal(3, new AvoidCircleAI());
        this.goalSelector.addGoal(4, new AnimalAISwimBottom(this, 1F, 7){

            public boolean canUse() {
                return super.canUse() && EntitySeaBear.this.getAnimation() == NO_ANIMATION;
            }

            public boolean canContinueToUse() {
                return super.canContinueToUse() && EntitySeaBear.this.getAnimation() == NO_ANIMATION;
            }
        });
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, LivingEntity.class, false, SOMBRERO));
    }

    public void tick() {
        super.tick();
        this.prevOnLandProgress = onLandProgress;

        if (this.isInWater()) {
            if (onLandProgress > 0F)
                onLandProgress--;
        } else {
            if (onLandProgress < 5F)
                onLandProgress++;
        }

        if (this.isOnGround() && !this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.setYRot(this.random.nextFloat() * 360.0F);
            this.setOnGround(false);
            this.hasImpulse = true;
        }
        if (circleCooldown > 0) {
            circleCooldown--;
            this.setTarget(null);
            this.setLastHurtByMob(null);
        }
        if(this.getAnimation() == ANIMATION_POINT){
            this.yBodyRot = this.getYHeadRot();
            this.rotOffs = this.getYHeadRot();
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new SemiAquaticPathNavigator(this, worldIn);
    }

    public boolean isPushable() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean canCollideWith(Entity e) {
        return !isMobSafe(e);
    }

    public void travel(Vec3 travelVector) {
        if(this.getAnimation() == ANIMATION_POINT){
            super.travel(Vec3.ZERO);
        }else {
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
        return new Animation[]{ANIMATION_POINT, ANIMATION_ATTACK};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public void setTarget(@Nullable LivingEntity entity) {
        if (entity == null || !isMobSafe(entity)) {
            super.setTarget(entity);
        }
    }

    public void push(Entity entity) {
        if (!isMobSafe(entity)) {
            super.push(entity);
        }
    }


    private class AttackAI extends Goal {

        public AttackAI() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntitySeaBear.this.getTarget() != null && EntitySeaBear.this.getTarget().isInWaterOrBubble() && EntitySeaBear.this.getTarget().isAlive() && (EntitySeaBear.this.circleCooldown == 0 || EntitySeaBear.this.getAnimation() == ANIMATION_POINT);
        }

        public void tick() {
            LivingEntity enemy = EntitySeaBear.this.getTarget();
            if(EntitySeaBear.this.getAnimation() == ANIMATION_POINT){
                EntitySeaBear.this.getNavigation().stop();
                EntitySeaBear.this.setDeltaMovement(EntitySeaBear.this.getDeltaMovement().multiply(0, 1, 0));
                EntitySeaBear.this.lookAt(enemy, 360, 50);
            }else if (isMobSafe(enemy) && EntitySeaBear.this.distanceTo(enemy) < 6) {
                EntitySeaBear.this.circleCooldown = 100 + random.nextInt(100);
                EntitySeaBear.this.setAnimation(ANIMATION_POINT);
                EntitySeaBear.this.lookAt(enemy, 360, 50);
                EntitySeaBear.this.lastCircle = enemy.blockPosition();
            } else {
                EntitySeaBear.this.getNavigation().moveTo(enemy.getX(), enemy.getY(0.5F), enemy.getZ(), 1.6D);
                if (EntitySeaBear.this.hasLineOfSight(enemy) && EntitySeaBear.this.distanceTo(enemy) < 3.5F) {
                    EntitySeaBear.this.setAnimation(ANIMATION_ATTACK);
                    if (EntitySeaBear.this.getAnimationTick() % 5 == 0) {
                        enemy.hurt(DamageSource.mobAttack(EntitySeaBear.this), 6);
                    }
                }
            }
        }
    }

    private class AvoidCircleAI extends Goal {
        private Vec3 target = null;

        public AvoidCircleAI() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntitySeaBear.this.circleCooldown > 0 && EntitySeaBear.this.lastCircle != null && EntitySeaBear.this.getAnimation() != ANIMATION_POINT;
        }

        public void tick() {
            BlockPos pos = EntitySeaBear.this.lastCircle;
            if (target == null || EntitySeaBear.this.distanceToSqr(target) < 2 || !EntitySeaBear.this.level.getFluidState(AMBlockPos.get(target).above()).is(FluidTags.WATER)) {
                target = DefaultRandomPos.getPosAway(EntitySeaBear.this, 20, 7, Vec3.atCenterOf(pos));
            }
            if (target != null && EntitySeaBear.this.level.getFluidState(AMBlockPos.get(target).above()).is(FluidTags.WATER)) {
                EntitySeaBear.this.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
            }
        }
    }
}
