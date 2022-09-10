package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EtherealMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.MonsterAIWalkThroughHallsOfStructure;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;

public class EntityUnderminer extends PathfinderMob {

    private static final EntityDataAccessor<Boolean> PHASING = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.BOOLEAN);

    protected EntityUnderminer(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new EtherealMoveController(this, 1F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.FOLLOW_RANGE, 64F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new PathNavigator(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASING, false);
    }

    public boolean isPhasing() {
        return this.entityData.get(PHASING);
    }

    public void setPhasing(boolean phasing) {
        this.entityData.set(PHASING, phasing);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MonsterAIWalkThroughHallsOfStructure(this, 1.0D, 60, StructureTags.MINESHAFT, 50));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.isMagic() && source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer() || super.isInvulnerableTo(source);
    }

    private float calculateDistanceToFloor() {
        BlockPos floor = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
        while (!level.getBlockState(floor).isFaceSturdy(level, floor, Direction.UP) && floor.getY() > level.getMinBuildHeight()) {
            floor = floor.below();
        }
        return (float) (this.getBoundingBox().minY - (floor.getY() + 1));
    }


    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f = 0.6F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, f, 0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_218949_, DifficultyInstance p_218950_) {
        super.populateDefaultEquipmentSlots(p_218949_, p_218950_);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AMItemRegistry.GHOSTLY_PICKAXE.get()));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag) {
        spawnData = super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnData, tag);
        RandomSource randomsource = level.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficultyInstance);
        return spawnData;
    }


    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        if (!level.isClientSide) {
            double xzSpeed = this.getDeltaMovement().horizontalDistance();
            double distToFloor = Mth.clamp(calculateDistanceToFloor(), -1F, 1F);
            if (Math.abs(distToFloor) > 0.01 && xzSpeed < 0.05 && !this.isActuallyInAWall()) {
                if (distToFloor < 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -Math.min(distToFloor * 0.1F, 0F), 0));
                }
                if (distToFloor > 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -Math.max(distToFloor * 0.1F, 0F), 0));
                }
            }
        }
    }

    protected void jumpFromGround() {

    }

    public boolean isNoGravity() {
        return true;
    }

    private boolean isActuallyInAWall() {
        float f = this.getDimensions(this.getPose()).width * 0.1F;
        AABB aabb = AABB.ofSize(this.getEyePosition(), f, 1.0E-6D, f);
        return BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
            BlockState blockstate = this.level.getBlockState(p_201942_);
            return !blockstate.isAir() && blockstate.isSuffocating(this.level, p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, p_201942_).move(p_201942_.getX(), p_201942_.getY(), p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
        });
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getBrightness() {
        return 1.0F;
    }

    public boolean isDwarf() {
        return this.getId() % 3 == 0;
    }

    public int getVariant() {
        return this.getId() % 2;
    }

    private class PathNavigator extends GroundPathNavigation {

        public PathNavigator(EntityUnderminer underminer, Level level) {
            super(underminer, level);
        }

        @Override
        protected boolean canUpdatePath() {
            return !this.mob.isPassenger();
        }

        @Override
        protected Vec3 getTempMobPos() {
            return this.mob.position();
        }
    }
}
