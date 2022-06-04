package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.*;
import net.minecraft.server.level.ServerLevel;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

public class EntityAlligatorSnappingTurtle extends Animal implements ISemiAquatic, Shearable, net.minecraftforge.common.IForgeShearable {

    public static final Predicate<LivingEntity> TARGET_PRED = (animal) -> {
        return !(animal instanceof EntityAlligatorSnappingTurtle) && !(animal instanceof ArmorStand) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(animal) && animal.isAlive();
    };
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> MOSS = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WAITING = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACK_TARGET_FLAG = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LUNGE_FLAG = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TURTLE_SCALE = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.FLOAT);
    public float openMouthProgress;
    public float prevOpenMouthProgress;
    public float attackProgress;
    public float prevAttackProgress;
    public int chaseTime = 0;
    private int biteTick = 0;
    private int waitTime = 0;
    private int timeUntilWait = 0;
    private int mossTime = 0;

    protected EntityAlligatorSnappingTurtle(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        maxUpStep = 1F;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_HURT;
    }


    public static boolean canTurtleSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random randomIn) {
        boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.ALLIGATOR_SNAPPING_TURTLE_SPAWNS);
        return spawnBlock && pos.getY() < worldIn.getSeaLevel() + 4;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.alligatorSnappingTurtleSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 18.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.7D).add(Attributes.ARMOR, 8D).add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public float getScale() {
        return this.isBaby() ? 0.3F : 1.0F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, false));
        this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(3, new BottomFeederAIWander(this, 1.0D, 120, 150, 10));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this) {
            public boolean canContinueToUse() {
                return chaseTime >= 0 && super.canContinueToUse();
            }
        }));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 2, false, true, TARGET_PRED) {
            protected AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(0.5D, 2D, 0.5D);
            }
        });
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.COD;
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    public boolean doHurtTarget(Entity entityIn) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(MOSS, 0);
        this.entityData.define(TURTLE_SCALE, 1F);
        this.entityData.define(WAITING, false);
        this.entityData.define(ATTACK_TARGET_FLAG, false);
        this.entityData.define(LUNGE_FLAG, false);
    }

    public void tick() {
        super.tick();
        prevOpenMouthProgress = openMouthProgress;
        prevAttackProgress = attackProgress;
        final boolean attack = this.entityData.get(LUNGE_FLAG);
        final boolean open = this.isWaiting() || this.entityData.get(ATTACK_TARGET_FLAG) && !attack;

        if (attack) {
            if (attackProgress < 5F)
                attackProgress++;
        } else {
            if (attackProgress > 0F)
                attackProgress--;
        }

        if (open) {
            if (openMouthProgress < 5F)
                openMouthProgress++;
        } else {
            if (openMouthProgress > 0F)
                openMouthProgress--;
        }

        if (this.attackProgress == 4 && this.getTarget() != null && this.isAlive() && this.hasLineOfSight(this.getTarget()) && this.distanceTo(this.getTarget()) < 2.3F) {
            final float dmg = this.isBaby() ? 1F : (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
            this.getTarget().hurt(DamageSource.mobAttack(this), dmg);
        }

        if (this.attackProgress > 4)
            biteTick = 5;

        if (biteTick > 0)
            biteTick--;

        if (chaseTime < 0)
            chaseTime++;

        if (!this.level.isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision && this.isInWater());
            if (this.isWaiting()) {
                waitTime++;
                timeUntilWait = 1500;
                if (waitTime > 1500 || this.getTarget() != null) {
                    this.setWaiting(false);
                }
            } else {
                timeUntilWait--;
                waitTime = 0;
            }
            if ((this.getTarget() == null || !this.getTarget().isAlive()) && timeUntilWait <= 0 && this.isInWater()) {
                this.setWaiting(true);
            }
            if (this.getTarget() != null && biteTick == 0) {
                this.setWaiting(false);
                chaseTime++;
                this.entityData.set(ATTACK_TARGET_FLAG, true);
                this.lookAt(this.getTarget(), 360, 40);
                this.yBodyRot = this.getYRot();
                if (openMouthProgress > 4 && this.hasLineOfSight(this.getTarget()) && this.distanceTo(this.getTarget()) < 2.3F) {
                    this.entityData.set(LUNGE_FLAG, true);
                }
                if (chaseTime > 40 && this.distanceTo(this.getTarget()) > (this.getTarget() instanceof Player ? 5 : 10)) {
                    chaseTime = -50;
                    this.setTarget(null);
                    this.setLastHurtByMob(null);
                    this.setLastHurtMob(null);
                    this.lastHurtByPlayer = null;
                }
            } else {
                this.entityData.set(ATTACK_TARGET_FLAG, false);
                this.entityData.set(LUNGE_FLAG, false);
            }
            mossTime++;
            if (this.isInWater() && mossTime > 12000) {
                mossTime = 0;
                this.setMoss(Math.min(10, this.getMoss() + 1));
            }
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.chaseTime < 0 ? null : super.getTarget();
    }

    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (this.chaseTime >= 0) {
            super.setTarget(entitylivingbaseIn);
        } else {
            super.setTarget(null);
        }
    }

    @Nullable
    public LivingEntity getLastHurtByMob() {
        return this.chaseTime < 0 ? null : super.getLastHurtByMob();
    }

    public void setLastHurtByMob(@Nullable LivingEntity entitylivingbaseIn) {
        if (this.chaseTime >= 0) {
            super.setLastHurtByMob(entitylivingbaseIn);
        } else {
            super.setLastHurtByMob(null);
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setMoss(random.nextInt(6));
        this.setTurtleScale(0.8F + random.nextFloat() * 0.2F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public float getTurtleScale() {
        return this.entityData.get(TURTLE_SCALE);
    }

    public void setTurtleScale(float scale) {
        this.entityData.set(TURTLE_SCALE, scale);
    }


    protected PathNavigation createNavigation(Level worldIn) {
        return new SemiAquaticPathNavigator(EntityAlligatorSnappingTurtle.this, worldIn) {
            public boolean isStableDestination(BlockPos pos) {
                return this.level.getBlockState(pos).getFluidState().isEmpty();
            }
        };
    }

    public boolean isWaiting() {
        return this.entityData.get(WAITING);
    }

    public void setWaiting(boolean sit) {
        this.entityData.set(WAITING, sit);
    }

    public int getMoss() {
        return this.entityData.get(MOSS);
    }

    public void setMoss(int moss) {
        this.entityData.set(MOSS, moss);
    }

    protected void updateAir(int air) {

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Waiting", this.isWaiting());
        compound.putInt("MossLevel", this.getMoss());
        compound.putFloat("TurtleScale", this.getTurtleScale());
        compound.putInt("MossTime", this.mossTime);
        compound.putInt("WaitTime", this.waitTime);
        compound.putInt("WaitTime2", this.timeUntilWait);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setWaiting(compound.getBoolean("Waiting"));
        this.setMoss(compound.getInt("MossLevel"));
        this.setTurtleScale(compound.getFloat("TurtleScale"));
        this.mossTime = compound.getInt("MossTime");
        this.waitTime = compound.getInt("WaitTime");
        this.timeUntilWait = compound.getInt("WaitTime2");
    }

    @Override
    public boolean shouldEnterWater() {
        return true;
    }

    @Override
    public boolean shouldLeaveWater() {
        return false;
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isWaiting();
    }

    @Override
    public int getWaterSearchRange() {
        return 10;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getFluidState(pos.below()).isEmpty() && worldIn.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : super.getWalkTargetValue(pos, worldIn);
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(CLIMBING, b0);
    }


    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (this.jumping) {
                this.setDeltaMovement(this.getDeltaMovement().scale(1D));
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.72D, 0.0D));
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.4D));
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
            }

        } else {
            super.travel(travelVector);
        }

    }

    public boolean readyForShearing() {
        return this.isAlive() && this.getMoss() > 0;
    }

    @Override
    public boolean isShearable(@javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos) {
        return readyForShearing();
    }

    @Override
    public void shear(SoundSource category) {
        this.level.playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
        if (!this.level.isClientSide()) {
            if (random.nextFloat() < this.getMoss() * 0.05F) {
                this.spawnAtLocation(AMItemRegistry.SPIKED_SCUTE.get());
            } else {
                this.spawnAtLocation(Items.SEAGRASS);
            }
            this.setMoss(0);
        }
    }

    @javax.annotation.Nonnull
    @Override
    public java.util.List<ItemStack> onSheared(@javax.annotation.Nullable Player player, @javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
        world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
        if (!world.isClientSide()) {
            if (random.nextFloat() < this.getMoss() * 0.05F) {
                this.setMoss(0);
                return Collections.singletonList(new ItemStack(AMItemRegistry.SPIKED_SCUTE.get()));
            } else {
                this.setMoss(0);
                return Collections.singletonList(new ItemStack(Items.SEAGRASS));
            }
        }
        return java.util.Collections.emptyList();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get().create(p_241840_1_);
    }
}
