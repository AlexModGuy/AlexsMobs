package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.FroststalkerAIFollowLeader;
import com.github.alexthe666.alexsmobs.entity.ai.FroststalkerAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.SnowLeopardAIMelee;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class EntityFroststalker extends Animal implements IAnimatedEntity {

    public static final Animation ANIMATION_BITE = Animation.create(13);
    public static final Animation ANIMATION_SPEAK = Animation.create(11);
    public static final Animation ANIMATION_SLASH_L = Animation.create(12);
    public static final Animation ANIMATION_SLASH_R = Animation.create(12);
    public static final Animation ANIMATION_SHOVE = Animation.create(12);
    private static final EntityDataAccessor<Boolean> SPIKES = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPIKE_SHAKING = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BIPEDAL = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TURN_ANGLE = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.FLOAT);
    public float bipedProgress;
    public float prevBipedProgress;
    public float tackleProgress;
    public float prevTackleProgress;
    public float spikeShakeProgress;
    public float prevSpikeShakeProgress;
    public float prevTurnAngle;
    private int animationTick;
    private Animation currentAnimation;
    private int standingTime = 400 - random.nextInt(700);
    private int currentSpeedMode = -1;
    private LivingEntity leader;
    private int packSize = 1;
    private int shakeTime = 0;

    protected EntityFroststalker(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 24D).add(Attributes.ARMOR, 2.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev && this.hasSpikes() && !this.isSpikeShaking() && source.getEntity() != null && source.getEntity().distanceTo(this) < 10) {
            this.setSpikeShaking(true);
            shakeTime = 20 + random.nextInt(60);
            standFor(shakeTime + 10);
        }
        return prev;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new FroststalkerAIMelee(this));
        this.goalSelector.addGoal(4, new FroststalkerAIFollowLeader(this));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 90, 1.0D, 7, 7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, EntityFroststalker.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 20, false, false, (p_213487_0_) -> {
            return p_213487_0_ instanceof Pig;
        }));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TURN_ANGLE, 0F);
        this.entityData.define(SPIKES, Boolean.valueOf(true));
        this.entityData.define(BIPEDAL, Boolean.valueOf(false));
        this.entityData.define(SPIKE_SHAKING, Boolean.valueOf(false));
        this.entityData.define(TACKLING, Boolean.valueOf(false));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Spiked", this.hasSpikes());
        compound.putBoolean("Bipedal", this.isBipedal());
        compound.putBoolean("SpikeShaking", this.isSpikeShaking());
        compound.putInt("StandingTime", standingTime);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSpiked(compound.getBoolean("Spiked"));
        this.setBipedal(compound.getBoolean("Bipedal"));
        this.setSpikeShaking(compound.getBoolean("SpikeShaking"));
        this.standingTime = compound.getInt("StandingTime");
    }

    public BlockPos getRestrictCenter() {
        return this.leader == null ? super.getRestrictCenter() : this.leader.getOnPos();
    }

    public boolean hasRestriction() {
        return this.isFollower();
    }

    public void tick() {
        super.tick();
        this.prevBipedProgress = bipedProgress;
        this.prevTackleProgress = tackleProgress;
        this.prevSpikeShakeProgress = spikeShakeProgress;
        this.prevTurnAngle = getTurnAngle();
        if (this.isBipedal() && bipedProgress < 5.0F) {
            bipedProgress++;
        }
        if (!this.isBipedal() && bipedProgress > 0.0F) {
            bipedProgress--;
        }
        if (this.isTackling() && tackleProgress < 5.0F) {
            tackleProgress++;
        }
        if (!this.isTackling() && tackleProgress > 0.0F) {
            tackleProgress--;
        }
        if (this.isSpikeShaking() && spikeShakeProgress < 5.0F) {
            spikeShakeProgress++;
        }
        if (!this.isSpikeShaking() && spikeShakeProgress > 0.0F) {
            spikeShakeProgress--;
        }
        if(this.isSpikeShaking() && currentSpeedMode != 2){
            currentSpeedMode = 2;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1F);
        }
        if (!this.isSpikeShaking() && isBipedal() && currentSpeedMode != 0) {
            currentSpeedMode = 0;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
        }
        if (!this.isSpikeShaking() && !isBipedal() && currentSpeedMode != 1) {
            currentSpeedMode = 1;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        }
        if (!level.isClientSide) {
            if (this.tickCount % 200 == 0) {
                if (this.isHotBiome()) {
                    this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400));
                    if(random.nextInt(2) == 0 && !this.isInWaterRainOrBubble()){
                        this.setSpiked(false);
                    }
                }
            }
            float threshold = 1F;
            boolean flag = false;
            if (isBipedal() && this.yRotO - this.getYRot() > threshold) {
                this.setTurnAngle(this.getTurnAngle() + 5);
                flag = true;
            }
            if (isBipedal() && this.yRotO - this.getYRot() < -threshold) {
                this.setTurnAngle(this.getTurnAngle() - 5);
                flag = true;
            }
            if (!flag) {
                if (this.getTurnAngle() > 0) {
                    this.setTurnAngle(Math.max(this.getTurnAngle() - 10, 0));
                }
                if (this.getTurnAngle() < 0) {
                    this.setTurnAngle(Math.min(this.getTurnAngle() + 10, 0));
                }
            }
            this.setTurnAngle(Mth.clamp(this.getTurnAngle(), -60, 60));
            if (standingTime > 0) {
                standingTime--;
            }
            if (standingTime < 0) {
                standingTime++;
            }
            if (this.isBipedal() && standingTime <= 0) {
                standingTime = -200 - random.nextInt(400);
                this.setBipedal(false);
            }
            if (!this.isBipedal() && standingTime == 0 && this.getDeltaMovement().lengthSqr() >= 0.03D) {
                standingTime = 200 + random.nextInt(600);
                this.setBipedal(true);
            }
            if (shakeTime > 0) {
                if (this.shakeTime % 5 == 0) {
                    int spikeCount = 2 + random.nextInt(4);
                    for (int i = 0; i < spikeCount; i++) {
                        float f = ((i + 1) / (float) spikeCount) * 360F;
                        EntityIceShard shard = new EntityIceShard(level, this);
                        shard.shootFromRotation(this, this.getXRot() - random.nextInt(40), f, 0.0F, 0.15F + random.nextFloat() * 0.2F, 1.0F);
                        level.addFreshEntity(shard);
                    }
                }
                shakeTime--;
            }
            if (this.isSpikeShaking() && shakeTime == 0) {
                this.setSpikeShaking(false);
                if(random.nextInt(2) == 0){
                    this.setSpiked(false);
                }
            }
            //Makes entire pack attack target
            if(this.getTarget() != null && this.getTarget().isAlive() && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().isAlive()) ){
                this.setLastHurtByMob(this.getTarget());
            }

            boolean attackAnim = this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5 ||
                    this.getAnimation() == ANIMATION_SHOVE && this.getAnimationTick() == 8 ||
                    this.getAnimation() == ANIMATION_SLASH_L && this.getAnimationTick() == 7 ||
                    this.getAnimation() == ANIMATION_SLASH_R && this.getAnimationTick() == 7;
            if (this.getTarget() != null && attackAnim) {
                getTarget().knockback(0.2F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
                this.getTarget().hurt(DamageSource.mobAttack(this), 2);// (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean isHotBiome() {
        if (this.isNoAi()) {
            return false;
        }
        if (this.level.dimension() == Level.NETHER) {
            return true;
        } else {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY());
            int k = Mth.floor(this.getZ());
            return this.level.getBiome(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F;
        }
    }

    public void standFor(int time){
        this.setBipedal(true);
        standingTime = time;
    }
    @Override
    protected float getJumpPower() {
        return 0.52F * this.getBlockJumpFactor();
    }

    @Override
    protected void jumpFromGround() {
        double d0 = (double) this.getJumpPower() + this.getJumpBoostPower();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d0, vec3.z);
        float f = this.getYRot() * ((float) Math.PI / 180F);
        this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f) * 0.2F, 0.0D, Mth.cos(f) * 0.2F));
        this.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
    }

    public void frostJump(){
        jumpFromGround();
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
        return new Animation[]{ANIMATION_BITE, ANIMATION_SPEAK, ANIMATION_SLASH_L, ANIMATION_SLASH_R, ANIMATION_SHOVE};
    }

    public float getTurnAngle() {
        return this.entityData.get(TURN_ANGLE);
    }

    public void setTurnAngle(float progress) {
        this.entityData.set(TURN_ANGLE, progress);
    }

    public boolean hasSpikes() {
        return this.entityData.get(SPIKES).booleanValue();
    }

    public void setSpiked(boolean bar) {
        this.entityData.set(SPIKES, Boolean.valueOf(bar));
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLING).booleanValue();
    }

    public void setTackling(boolean bar) {
        this.entityData.set(TACKLING, Boolean.valueOf(bar));
    }

    public boolean isBipedal() {
        return this.entityData.get(BIPEDAL).booleanValue();
    }

    public void setBipedal(boolean bar) {
        this.entityData.set(BIPEDAL, Boolean.valueOf(bar));
    }

    public boolean isSpikeShaking() {
        return this.entityData.get(SPIKE_SHAKING).booleanValue();
    }

    public void setSpikeShaking(boolean bar) {
        this.entityData.set(SPIKE_SHAKING, Boolean.valueOf(bar));
    }

    public boolean isFollower() {
        return this.leader != null && this.leader.isAlive();
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            int anim = this.random.nextInt(3);
            if(anim == 0){
                this.setAnimation(ANIMATION_SHOVE);
            }else if(anim == 1){
                this.setAnimation(ANIMATION_BITE);
            }else if(anim == 2){
                this.setAnimation(ANIMATION_SLASH_L);
            }else if(anim == 3){
                this.setAnimation(ANIMATION_SLASH_R);
            }
        }
        return true;
    }


    public LivingEntity startFollowing(LivingEntity leader) {
        this.leader = leader;
        if (leader instanceof EntityFroststalker) {
            ((EntityFroststalker) leader).addFollower();
        }
        return leader;
    }

    public void stopFollowing() {
        if (this.leader instanceof EntityFroststalker) {
            ((EntityFroststalker) this.leader).removeFollower();
        }
        this.leader = null;
    }

    private void addFollower() {
        ++this.packSize;
    }

    private void removeFollower() {
        --this.packSize;
    }

    public boolean canBeFollowed() {
        return this.hasFollowers() && this.packSize < getMaxPackSize();
    }

    public boolean hasFollowers() {
        return this.packSize > 1;
    }

    public int getMaxSpawnClusterSize() {
        return 6;
    }

    public int getMaxPackSize() {
        return this.getMaxSpawnClusterSize();
    }

    public void addFollowers(Stream<EntityFroststalker> p_27534_) {
        p_27534_.limit(getMaxPackSize() - this.packSize).filter((p_27538_) -> {
            return p_27538_ != this;
        }).forEach((p_27536_) -> {
            p_27536_.startFollowing(this);
        });
    }

    public boolean inRangeOfLeader() {
        return this.distanceTo(this.leader) <= 60.0D;
    }

    public void pathToLeader() {
        if (this.isFollower()) {
            if (this.distanceTo(leader) > 6 && this.getNavigation().isDone()) {
                this.getNavigation().moveTo(this.leader, 1.0D);
            }
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_27528_, DifficultyInstance p_27529_, MobSpawnType
            p_27530_, @Nullable SpawnGroupData p_27531_, @Nullable CompoundTag p_27532_) {
        super.finalizeSpawn(p_27528_, p_27529_, p_27530_, p_27531_, p_27532_);
        if (p_27531_ == null) {
            p_27531_ = new SchoolSpawnGroupData(this);
        } else {
            this.startFollowing(((SchoolSpawnGroupData) p_27531_).leader);
        }

        return p_27531_;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    public static class SchoolSpawnGroupData implements SpawnGroupData {
        public final EntityFroststalker leader;

        public SchoolSpawnGroupData(EntityFroststalker p_27553_) {
            this.leader = p_27553_;
        }
    }
}
