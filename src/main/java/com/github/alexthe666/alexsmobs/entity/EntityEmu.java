package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class EntityEmu extends Animal implements IAnimatedEntity, IHerdPanic {

    public static final Animation ANIMATION_DODGE_LEFT = Animation.create(10);
    public static final Animation ANIMATION_DODGE_RIGHT = Animation.create(10);
    public static final Animation ANIMATION_PECK_GROUND = Animation.create(25);
    public static final Animation ANIMATION_SCRATCH = Animation.create(20);
    public static final Animation ANIMATION_PUZZLED = Animation.create(30);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityEmu.class, EntityDataSerializers.INT);
    private int animationTick;
    private Animation currentAnimation;
    private int revengeCooldown = 0;
    private boolean emuAttackedDirectly = false;
    public int timeUntilNextEgg = this.random.nextInt(6000) + 6000;

    protected EntityEmu(EntityType type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.35F).add(Attributes.ATTACK_DAMAGE, 3F);
    }


    public static <T extends Mob> boolean canEmuSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.getAllTags().getTag(AMTagRegistry.EMU_SPAWNS).contains(worldIn.getBlockState(pos.below()).getBlock());
        return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.emuSpawnRolls, this.getRandom(), spawnReasonIn);
    }


    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.EMU_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.EMU_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.EMU_HURT;
    }
    
    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, true){
            protected double getAttackReachSqr(LivingEntity attackTarget) {
                return super.getAttackReachSqr(attackTarget) + 2.5D;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && EntityEmu.this.revengeCooldown <= 0;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && EntityEmu.this.revengeCooldown <= 0;
            }
        });
        this.goalSelector.addGoal(2, new AnimalAIHerdPanic(this, 1.5D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 110, 1.0D, 10, 7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new EntityEmu.HurtByTargetGoal());
        if(AMConfig.emuTargetSkeletons){
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Pillager.class, false));
        }
    }

    public boolean canAttack(LivingEntity target) {
        return !this.isBaby() && super.canAttack(target);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev) {
            double range = 15;
            int fleeTime = 100 + getRandom().nextInt(5);
            this.revengeCooldown = fleeTime;
            List<? extends EntityEmu> list = this.level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range / 2, range));
            for (EntityEmu emu : list) {
                emu.revengeCooldown = fleeTime;
                if(emu.isBaby() && random.nextInt(2) == 0){
                    emu.emuAttackedDirectly = this.getLastHurtByMob() != null;
                    emu.revengeCooldown = emu.emuAttackedDirectly ? 10 + getRandom().nextInt(30) : fleeTime;
                }
            }
            emuAttackedDirectly = this.getLastHurtByMob() != null;
            this.revengeCooldown = emuAttackedDirectly ? 10 + getRandom().nextInt(30) : revengeCooldown;
        }
        return prev;
    }

    public void travel(Vec3 travelVector) {
        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.getAnimation() == ANIMATION_PECK_GROUND || this.getAnimation() == ANIMATION_PUZZLED ? 0.15F : 1F) * (isInLava() ? 0.2F : 1F));
        super.travel(travelVector);
    }

    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (this.getLastHurtByMob() == null && this.getTarget() == null) {
                if (this.getDeltaMovement().lengthSqr() < 0.03D && this.getRandom().nextInt(190) == 0 && this.getAnimation() == NO_ANIMATION) {
                    if (getRandom().nextInt(3) == 0) {
                        this.setAnimation(ANIMATION_PUZZLED);
                    } else if (this.onGround) {
                        this.setAnimation(ANIMATION_PECK_GROUND);
                    }
                }
            }
            if (revengeCooldown > 0) {
                revengeCooldown--;
            }
            if (revengeCooldown <= 0 && this.getLastHurtByMob() != null && !emuAttackedDirectly) {
                this.setLastHurtByMob(null);
                revengeCooldown = 0;
            }
            if (this.getTarget() != null && this.getAnimation() == ANIMATION_SCRATCH && this.distanceTo(this.getTarget()) < 4F && (this.getAnimationTick() == 8 || this.getAnimationTick() == 15)) {
                float f1 = this.getYRot() * ((float) Math.PI / 180F);
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
                getTarget().knockback(0.4F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
                this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        }
        if (!this.level.isClientSide && this.isAlive() && !this.isBaby() && --this.timeUntilNextEgg <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(AMItemRegistry.EMU_EGG);
            this.timeUntilNextEgg = this.random.nextInt(6000) + 6000;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
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
        return new Animation[]{ANIMATION_DODGE_LEFT, ANIMATION_DODGE_RIGHT, ANIMATION_PECK_GROUND, ANIMATION_SCRATCH, ANIMATION_PUZZLED};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        EntityEmu emu = AMEntityRegistry.EMU.create(serverWorld);
        emu.setVariant(this.getVariant());
        return emu;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SCRATCH);
        }
        return true;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
        if (compound.contains("EggLayTime")) {
            this.timeUntilNextEgg = compound.getInt("EggLayTime");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("EggLayTime", this.timeUntilNextEgg);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if(this.random.nextInt(200) == 0){
            this.setVariant(2);
        }else if(random.nextInt(3) == 0){
            this.setVariant(1);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void onPanic() {

    }

    @Override
    public boolean canPanic() {
        return true;
    }

    class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(EntityEmu.this);
        }

        public void start() {
            if (EntityEmu.this.isBaby() || !emuAttackedDirectly) {
                this.alertOthers();
                this.stop();
            } else {
                super.start();
            }
        }

        protected void alertOther(Mob mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityEmu && !mobIn.isBaby() && !emuAttackedDirectly && ((EntityEmu) mobIn).revengeCooldown <= 0) {
                super.alertOther(mobIn, targetIn);
            }

        }
    }
}
