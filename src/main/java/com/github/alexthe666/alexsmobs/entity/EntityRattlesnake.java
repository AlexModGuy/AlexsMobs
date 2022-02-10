package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class EntityRattlesnake extends Animal implements IAnimatedEntity {

    public float prevCurlProgress;
    public float curlProgress;
    public int randomToungeTick = 0;
    public int maxCurlTime = 75;
    private int curlTime = 0;
    private static final EntityDataAccessor<Boolean> RATTLING = SynchedEntityData.defineId(EntityRattlesnake.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CURLED = SynchedEntityData.defineId(EntityRattlesnake.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<LivingEntity> WARNABLE_PREDICATE = (mob) -> {
        return mob instanceof Player && !((Player) mob).isCreative() && !mob.isSpectator() || mob instanceof EntityRoadrunner;
    };
    private static final Predicate<LivingEntity> TARGETABLE_PREDICATE = (mob) -> {
        return mob instanceof Player && !((Player) mob).isCreative() && !mob.isSpectator() || mob instanceof EntityRoadrunner;
    };
    private int animationTick;
    private Animation currentAnimation;
    public static final Animation ANIMATION_BITE = Animation.create(20);
    private int loopSoundTick = 0;
    protected EntityRattlesnake(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new WarnPredatorsGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 60, 1.0D, 7, 7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Rabbit.class, 15, true, true, null));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityJerboa.class, 15, true, true, null));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(4, new ShortDistanceTarget());
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.RATTLESNAKE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.RATTLESNAKE_HURT;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.rattlesnakeSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean doHurtTarget(Entity entityIn) {
        this.setAnimation(ANIMATION_BITE);
        return true;
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(potioneffectIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CURLED, Boolean.valueOf(false));
        this.entityData.define(RATTLING, Boolean.valueOf(false));
    }

    public boolean isCurled() {
        return this.entityData.get(CURLED).booleanValue();
    }

    public void setCurled(boolean curled) {
        this.entityData.set(CURLED, curled);
    }

    public boolean isRattling() {
        return this.entityData.get(RATTLING).booleanValue();
    }

    public void setRattling(boolean rattling) {
        this.entityData.set(RATTLING, rattling);
    }

    public void tick(){
        super.tick();
        prevCurlProgress = curlProgress;
        if (this.isCurled() && curlProgress < 5) {
            curlProgress += 0.5F;
        }
        if (!this.isCurled() && curlProgress > 0) {
            curlProgress -= 1;
        }
        if (random.nextInt(15) == 0 && randomToungeTick == 0) {
            randomToungeTick = 10 + random.nextInt(20);
        }
        if (randomToungeTick > 0) {
            randomToungeTick--;
        }
        if (isCurled() && !isRattling() && ++curlTime > maxCurlTime) {
            this.setCurled(false);
            curlTime = 0;
            maxCurlTime = 75 + random.nextInt(50);
        }
        if(!level.isClientSide && this.isCurled() && (this.getTarget() != null && this.getTarget().isAlive())){
            this.setCurled(false);
        }
        if(!level.isClientSide && this.isRattling()  && this.getTarget() == null){
            this.setCurled(true);

        }
        if (!level.isClientSide && !this.isCurled() && this.getTarget() == null && random.nextInt(500) == 0) {
            maxCurlTime = 300 + random.nextInt(250);
            this.setCurled(true);
        }
        if(this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 4){
             this.playSound(AMSoundRegistry.RATTLESNAKE_ATTACK, getSoundVolume(), getVoicePitch());
        }
        LivingEntity target = this.getTarget();
        if(this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 8 && target != null && this.distanceTo(target) < 2D){
            boolean meepMeep = target instanceof EntityRoadrunner;
            int f = isBaby() ? 2 : 1;
            target.hurt(DamageSource.mobAttack(this), meepMeep ? 1.0F : f * (float)getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            if(!meepMeep){
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 300, f * 2));
            }
        }
        if(isRattling()){
            if(loopSoundTick == 0){
                this.playSound(AMSoundRegistry.RATTLESNAKE_LOOP, this.getSoundVolume() * 0.5F, this.getVoicePitch());
            }
            loopSoundTick++;
            if(loopSoundTick > 50){
                loopSoundTick = 0;
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void travel(Vec3 vec3d) {
        if (this.isOnGround() && this.isCurled()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.28F);
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties() != null && stack.getItem().getFoodProperties().isMeat();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.RATTLESNAKE.create(p_241840_1_);
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
        return new Animation[]{ANIMATION_BITE};
    }

    public static boolean canRattlesnakeSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.getAllTags().getTag(AMTagRegistry.RATTLESNAKE_SPAWNS).contains(worldIn.getBlockState(pos.below()).getBlock());
        return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
}

    class WarnPredatorsGoal extends Goal {
        int executionChance = 20;
        Entity target = null;

        @Override
        public boolean canUse() {
            if(EntityRattlesnake.this.getRandom().nextInt(executionChance) == 0){
                double dist = 5D;
                List<LivingEntity> list = EntityRattlesnake.this.level.getEntitiesOfClass(LivingEntity.class, EntityRattlesnake.this.getBoundingBox().inflate(dist, dist, dist), WARNABLE_PREDICATE);
                double d0 = Double.MAX_VALUE;
                Entity possibleTarget = null;
                for(Entity entity : list) {
                    double d1 = EntityRattlesnake.this.distanceToSqr(entity);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        possibleTarget = entity;
                    }
                }
                target = possibleTarget;
                return !list.isEmpty();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse(){
            return target != null && EntityRattlesnake.this.distanceTo(target) < 5D && EntityRattlesnake.this.getTarget() == null;
        }

        @Override
        public void stop() {
            target = null;
            EntityRattlesnake.this.setRattling(false);
        }

        @Override
        public void tick(){
            EntityRattlesnake.this.setRattling(true);
            EntityRattlesnake.this.setCurled(true);
            EntityRattlesnake.this.curlTime = 0;
            EntityRattlesnake.this.getLookControl().setLookAt(target, 30, 30);
        }
    }

    class ShortDistanceTarget extends NearestAttackableTargetGoal<Player> {
        public ShortDistanceTarget() {
            super(EntityRattlesnake.this, Player.class, 3, true, true, TARGETABLE_PREDICATE);
        }

        public boolean canUse() {
            if (EntityRattlesnake.this.isBaby()) {
                return false;
            } else {
                return super.canUse();
            }
        }

        public void start(){
            super.start();
            EntityRattlesnake.this.setCurled(false);
            EntityRattlesnake.this.setRattling(true);
        }

        protected double getFollowDistance() {
            return 2D;
        }
    }

}
