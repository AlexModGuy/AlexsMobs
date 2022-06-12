package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

public class EntityOrca extends TamableAnimal implements IAnimatedEntity {

    public static final Animation ANIMATION_BITE = Animation.create(8);
    public static final Animation ANIMATION_TAILSWING = Animation.create(20);
    private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.defineId(EntityOrca.class, EntityDataSerializers.INT);
    private static final TargetingConditions PLAYER_PREDICATE = TargetingConditions.forNonCombat().range(24.0D).ignoreLineOfSight();
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityOrca.class, EntityDataSerializers.INT);
    public int jumpCooldown;
    private int animationTick;
    private Animation currentAnimation;
    private int blockBreakCounter;
    public static final Predicate<LivingEntity> TARGET_BABY  = (animal) -> {
        return animal.isBaby();
    };

    protected EntityOrca(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.moveControl = new MoveHelperController(this);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.orcaSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.7F).add(Attributes.MOVEMENT_SPEED, 1.35F);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new SwimmerJumpPathNavigator(this, worldIn);
    }

    public int getMoistness() {
        return this.entityData.get(MOISTNESS);
    }

    public void setMoistness(int p_211137_1_) {
        this.entityData.set(MOISTNESS, p_211137_1_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MOISTNESS, 2400);
        this.entityData.define(VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public int determineVariant(BlockPos coords){
        if(coords == null){
            return 0;
        }
        if(coords.getZ() < 0){//north
            //west
            return coords.getX() < 0 ? 1 : 0;
        }else{//south
            //west
            return coords.getX() < 0 ? 3 : 2;
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.ORCA_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ORCA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ORCA_DIE.get();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreathAirGoal(this));
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new EntityOrca.SwimWithPlayerGoal(this, 4.0D));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new OrcaAIJump(this, 10));
        this.goalSelector.addGoal(6, new OrcaAIMeleeJump(this));
        this.goalSelector.addGoal(6, new OrcaAIMelee(this, 1.2F, true));
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityCachalotWhale.class, 25, false, false, TARGET_BABY));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 200, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.ORCA_TARGETS)));
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
        return new Animation[]{ANIMATION_BITE, ANIMATION_TAILSWING};
    }

    public void travel(Vec3 travelVector) {
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

    public void customServerAiStep() {
        super.customServerAiStep();
        breakBlock();
    }

    public void breakBlock() {
        if (this.blockBreakCounter > 0) {
            --this.blockBreakCounter;
            return;
        }
        boolean flag = false;
        if (!level.isClientSide && this.blockBreakCounter == 0) {
            for (int a = (int) Math.round(this.getBoundingBox().minX); a <= (int) Math.round(this.getBoundingBox().maxX); a++) {
                for (int b = (int) Math.round(this.getBoundingBox().minY) - 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 1) && (b <= 127); b++) {
                    for (int c = (int) Math.round(this.getBoundingBox().minZ); c <= (int) Math.round(this.getBoundingBox().maxZ); c++) {
                        BlockPos pos = new BlockPos(a, b, c);
                        BlockState state = level.getBlockState(pos);
                        FluidState fluidState = level.getFluidState(pos);
                        Block block = state.getBlock();
                        if (!state.isAir() && !state.getShape(level, pos).isEmpty() && state.is(AMTagRegistry.ORCA_BREAKABLES) && fluidState.isEmpty()) {
                            if (block != Blocks.AIR) {
                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1, 0.6F));
                                flag = true;
                                level.destroyBlock(pos, true);
                                if (state.is(BlockTags.ICE)) {
                                    level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (flag) {
            blockBreakCounter = 20;
        }
    }

    public void tick() {
        super.tick();
        if (jumpCooldown > 0) {
            jumpCooldown--;
            float f2 = (float) -((float) this.getDeltaMovement().y * (double) (180F / (float) Math.PI));
            this.setXRot(f2);
        }
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
        } else {

            if (this.isInWaterRainOrBubble()) {
                this.setMoistness(2400);
            } else {
                this.setMoistness(this.getMoistness() - 1);
                if (this.getMoistness() <= 0) {
                    this.hurt(DamageSource.DRY_OUT, 1.0F);
                }

                if (this.onGround) {
                    this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                    this.setYRot( this.random.nextFloat() * 360.0F);
                    this.onGround = false;
                    this.hasImpulse = true;
                }
            }

            if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
                Vec3 vector3d = this.getViewVector(0.0F);
                float f = Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * 0.9F;
                float f1 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * 0.9F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;

                for (int i = 0; i < 2; ++i) {
                    this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vector3d.x * (double) f2 + (double) f, this.getY() - vector3d.y, this.getZ() - vector3d.z * (double) f2 + (double) f1, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vector3d.x * (double) f2 - (double) f, this.getY() - vector3d.y, this.getZ() - vector3d.z * (double) f2 - (double) f1, 0.0D, 0.0D, 0.0D);
                }
            }

        }
        LivingEntity attackTarget = this.getTarget();
        if (attackTarget != null && distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 2) {
            if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 4) {
                float damage =(float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                if(attackTarget instanceof Drowned || attackTarget instanceof Guardian){
                    damage *= 2F;
                }
                boolean flag = attackTarget.hurt(DamageSource.mobAttack(this), damage);
                if (flag) {
                    this.doEnchantDamageEffects(this, attackTarget);
                    this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
                }
            }
            if (this.getAnimation() == ANIMATION_TAILSWING && this.getAnimationTick() == 6) {
                float damage =(float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                if(attackTarget instanceof Drowned || attackTarget instanceof Guardian){
                    damage *= 2F;
                }
                boolean flag = attackTarget.hurt(DamageSource.mobAttack(this), damage);
                if (flag) {
                    this.doEnchantDamageEffects(this, attackTarget);
                    this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
                }
                attackTarget.knockback(1F, Mth.sin(getYRot() * ((float) Math.PI / 180F)), -Mth.cos(getYRot() * ((float) Math.PI / 180F)));
                float knockbackResist = (float) Mth.clamp((1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
                this.getTarget().setDeltaMovement(this.getTarget().getDeltaMovement().add(0, knockbackResist * 0.4F, 0));

            }
        }
        if (attackTarget != null && attackTarget instanceof Player && attackTarget.hasEffect(AMEffectRegistry.ORCAS_MIGHT)) {
            attackTarget.removeEffect(AMEffectRegistry.ORCAS_MIGHT);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if(this.isInWaterOrBubble() && random.nextBoolean()){
            this.setAnimation(ANIMATION_TAILSWING);
        }else{
            this.setAnimation(ANIMATION_BITE);
        }
        return true;
    }

    public int getMaxAirSupply() {
        return 4800;
    }

    protected int increaseAirSupply(int currentAir) {
        return this.getMaxAirSupply();
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.0F;
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.SALMON;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob p_241840_2_) {
        return AMEntityRegistry.ORCA.get().create(serverWorld);
    }

    public boolean shouldUseJumpAttack(LivingEntity attackTarget) {
        if (attackTarget.isInWater()) {
            BlockPos up = attackTarget.blockPosition().above();
            return level.getFluidState(up.above()).isEmpty() && level.getFluidState(up.above(2)).isEmpty() && this.jumpCooldown == 0;
        } else {
            return this.jumpCooldown == 0;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType
            reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setAirSupply(this.getMaxAirSupply());
        this.setVariant(determineVariant(this.blockPosition()));
        this.setXRot(0.0F);
        this.setMoistness(2400);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        this.updateAir(i);
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    protected void updateAir(int p_209207_1_) {
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Moistness", this.getMoistness());
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setMoistness(compound.getInt("Moistness"));
        this.setVariant(compound.getInt("Variant"));
    }

    public void onJumpHit(LivingEntity entityIn) {
        boolean flag = entityIn.hurt(DamageSource.mobAttack(this), (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, entityIn);
            this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
        }
    }

    public static boolean canOrcaSpawn(EntityType<EntityOrca> p_223364_0_, LevelAccessor p_223364_1_, MobSpawnType reason, BlockPos p_223364_3_, Random p_223364_4_) {
        if (p_223364_3_.getY() > 45 && p_223364_3_.getY() < p_223364_1_.getSeaLevel()) {
            return p_223364_1_.getFluidState(p_223364_3_).is(FluidTags.WATER);
        } else {
            return false;
        }
    }

    static class SwimWithPlayerGoal extends Goal {
        private final EntityOrca dolphin;
        private final double speed;
        private Player targetPlayer;

        SwimWithPlayerGoal(EntityOrca dolphinIn, double speedIn) {
            this.dolphin = dolphinIn;
            this.speed = speedIn;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            this.targetPlayer = this.dolphin.level.getNearestPlayer(EntityOrca.PLAYER_PREDICATE, this.dolphin);
            if (this.targetPlayer == null) {
                return false;
            } else {
                return this.targetPlayer.isSwimming() && this.dolphin.getTarget() != this.targetPlayer;
            }
        }

        public boolean canContinueToUse() {
            return this.targetPlayer != null  && this.dolphin.getTarget() != this.targetPlayer && this.targetPlayer.isSwimming() && this.dolphin.distanceToSqr(this.targetPlayer) < 256.0D;
        }

        public void start() {
        }

        public void stop() {
            this.targetPlayer = null;
            this.dolphin.getNavigation().stop();
        }

        public void tick() {
            this.dolphin.getLookControl().setLookAt(this.targetPlayer, (float) (this.dolphin.getMaxHeadYRot() + 20), (float) this.dolphin.getMaxHeadXRot());
            if (this.dolphin.distanceToSqr(this.targetPlayer) < 10D) {
                this.dolphin.getNavigation().stop();
            } else {
                this.dolphin.getNavigation().moveTo(this.targetPlayer, this.speed);
            }

            if (this.targetPlayer.isSwimming() && this.targetPlayer.level.random.nextInt(6) == 0) {
                this.targetPlayer.addEffect(new MobEffectInstance(AMEffectRegistry.ORCAS_MIGHT, 1000));
            }
        }
    }

    static class MoveHelperController extends MoveControl {
        private final EntityOrca dolphin;

        public MoveHelperController(EntityOrca dolphinIn) {
            super(dolphinIn);
            this.dolphin = dolphinIn;
        }

        public void tick() {
            if (this.dolphin.isInWater()) {
                this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
            }

            if (this.operation == MoveControl.Operation.MOVE_TO && !this.dolphin.getNavigation().isDone()) {
                double d0 = this.wantedX - this.dolphin.getX();
                double d1 = this.wantedY - this.dolphin.getY();
                double d2 = this.wantedZ - this.dolphin.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double) 2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                } else {
                    float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    this.dolphin.setYRot(this.rotlerp(this.dolphin.getYRot(), f, 10.0F));
                    this.dolphin.yBodyRot = this.dolphin.getYRot();
                    this.dolphin.yHeadRot = this.dolphin.getYRot();
                    float f1 = (float) (this.speedModifier * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    if (this.dolphin.isInWater()) {
                        this.dolphin.setSpeed(f1 * 0.02F);
                        float f2 = -((float) (Mth.atan2(d1, Mth.sqrt((float) (d0 * d0 + d2 * d2))) * (double) (180F / (float) Math.PI)));
                        f2 = Mth.clamp(Mth.wrapDegrees(f2), -85.0F, 85.0F);
                        this.dolphin.setXRot(this.rotlerp(this.dolphin.getXRot(), f2, 5.0F));
                        float f3 = Mth.cos(this.dolphin.getXRot() * ((float) Math.PI / 180F));
                        float f4 = Mth.sin(this.dolphin.getXRot() * ((float) Math.PI / 180F));
                        this.dolphin.zza = f3 * f1;
                        this.dolphin.yya = -f4 * f1;
                    } else {
                        this.dolphin.setSpeed(f1 * 0.1F);
                    }

                }
            } else {
                this.dolphin.setSpeed(0.0F);
                this.dolphin.setXxa(0.0F);
                this.dolphin.setYya(0.0F);
                this.dolphin.setZza(0.0F);
            }
        }
    }

}
