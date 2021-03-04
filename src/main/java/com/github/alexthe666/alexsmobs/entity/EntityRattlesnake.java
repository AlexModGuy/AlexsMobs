package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class EntityRattlesnake extends AnimalEntity implements IAnimatedEntity {

    public float prevCurlProgress;
    public float curlProgress;
    public int randomToungeTick = 0;
    public int maxCurlTime = 75;
    private int curlTime = 0;
    private static final DataParameter<Boolean> RATTLING = EntityDataManager.createKey(EntityRattlesnake.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CURLED = EntityDataManager.createKey(EntityRattlesnake.class, DataSerializers.BOOLEAN);
    private static final Predicate<LivingEntity> WARNABLE_PREDICATE = (mob) -> {
        return mob instanceof PlayerEntity || mob instanceof EntityRoadrunner;
    };
    private static final Predicate<LivingEntity> TARGETABLE_PREDICATE = (mob) -> {
        return mob instanceof PlayerEntity && !((PlayerEntity) mob).isCreative() || mob instanceof EntityRoadrunner;
    };
    private int animationTick;
    private Animation currentAnimation;
    public static final Animation ANIMATION_BITE = Animation.create(20);
    private int loopSoundTick = 0;
    protected EntityRattlesnake(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new WarnPredatorsGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 60, 1.0D, 7, 7));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 15.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, RabbitEntity.class, 15, true, true, null));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new ShortDistanceTarget());
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.RATTLESNAKE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.RATTLESNAKE_HURT;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.rattlesnakeSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.setAnimation(ANIMATION_BITE);
        return true;
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        if (potioneffectIn.getPotion() == Effects.POISON) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CURLED, Boolean.valueOf(false));
        this.dataManager.register(RATTLING, Boolean.valueOf(false));
    }

    public boolean isCurled() {
        return this.dataManager.get(CURLED).booleanValue();
    }

    public void setCurled(boolean curled) {
        this.dataManager.set(CURLED, curled);
    }

    public boolean isRattling() {
        return this.dataManager.get(RATTLING).booleanValue();
    }

    public void setRattling(boolean rattling) {
        this.dataManager.set(RATTLING, rattling);
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
        if (rand.nextInt(15) == 0 && randomToungeTick == 0) {
            randomToungeTick = 10 + rand.nextInt(20);
        }
        if (randomToungeTick > 0) {
            randomToungeTick--;
        }
        if (isCurled() && !isRattling() && ++curlTime > maxCurlTime) {
            this.setCurled(false);
            curlTime = 0;
            maxCurlTime = 75 + rand.nextInt(50);
        }
        if(!world.isRemote && this.isCurled() && (this.getAttackTarget() != null && this.getAttackTarget().isAlive())){
            this.setCurled(false);
        }
        if(!world.isRemote && this.isRattling()  && this.getAttackTarget() == null){
            this.setCurled(true);

        }
        if (!world.isRemote && !this.isCurled() && this.getAttackTarget() == null && rand.nextInt(500) == 0) {
            maxCurlTime = 300 + rand.nextInt(250);
            this.setCurled(true);
        }
        if(this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 4){
             this.playSound(AMSoundRegistry.RATTLESNAKE_ATTACK, getSoundVolume(), getSoundPitch());
        }
        LivingEntity target = this.getAttackTarget();
        if(this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 8 && target != null && this.getDistance(target) < 2D){
            boolean meepMeep = target instanceof EntityRoadrunner;
            target.attackEntityFrom(DamageSource.causeMobDamage(this), meepMeep ? 1.0F : (float)getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            if(!meepMeep){
                target.addPotionEffect(new EffectInstance(Effects.POISON, 300, 2));
            }
        }
        if(isRattling()){
            if(loopSoundTick == 0){
                this.playSound(AMSoundRegistry.RATTLESNAKE_LOOP, this.getSoundVolume() * 0.5F, this.getSoundPitch());
            }
            loopSoundTick++;
            if(loopSoundTick > 50){
                loopSoundTick = 0;
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void travel(Vector3d vec3d) {
        if (this.isOnGround() && this.isCurled()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8D).createMutableAttribute(Attributes.ARMOR, 0.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28F);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem().isFood() && stack.getItem().getFood() != null && stack.getItem().getFood().isMeat();
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
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

    public static boolean canRattlesnakeSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.RATTLESNAKE_SPAWNS).contains(worldIn.getBlockState(pos.down()).getBlock());
        return spawnBlock && worldIn.getLightSubtracted(pos, 0) > 8;
}

    class WarnPredatorsGoal extends Goal {
        int executionChance = 20;
        Entity target = null;

        @Override
        public boolean shouldExecute() {
            if(EntityRattlesnake.this.getRNG().nextInt(executionChance) == 0){
                double dist = 5D;
                List<LivingEntity> list = EntityRattlesnake.this.world.getEntitiesWithinAABB(LivingEntity.class, EntityRattlesnake.this.getBoundingBox().grow(dist, dist, dist), WARNABLE_PREDICATE);
                double d0 = Double.MAX_VALUE;
                Entity possibleTarget = null;
                for(Entity entity : list) {
                    double d1 = EntityRattlesnake.this.getDistanceSq(entity);
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
        public boolean shouldContinueExecuting(){
            return target != null && EntityRattlesnake.this.getDistance(target) < 5D && EntityRattlesnake.this.getAttackTarget() == null;
        }

        @Override
        public void resetTask() {
            target = null;
            EntityRattlesnake.this.setRattling(false);
        }

        @Override
        public void tick(){
            EntityRattlesnake.this.setRattling(true);
            EntityRattlesnake.this.setCurled(true);
            EntityRattlesnake.this.curlTime = 0;
            EntityRattlesnake.this.getLookController().setLookPositionWithEntity(target, 30, 30);
        }
    }

    class ShortDistanceTarget extends NearestAttackableTargetGoal<PlayerEntity> {
        public ShortDistanceTarget() {
            super(EntityRattlesnake.this, PlayerEntity.class, 3, true, true, TARGETABLE_PREDICATE);
        }

        public boolean shouldExecute() {
            if (EntityRattlesnake.this.isChild()) {
                return false;
            } else {
                return super.shouldExecute();
            }
        }

        public void startExecuting(){
            super.startExecuting();
            EntityRattlesnake.this.setCurled(false);
            EntityRattlesnake.this.setRattling(true);
        }

        protected double getTargetDistance() {
            return 2D;
        }
    }

}
