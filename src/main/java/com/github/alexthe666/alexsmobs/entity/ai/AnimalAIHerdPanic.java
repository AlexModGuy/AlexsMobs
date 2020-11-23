package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IHerdPanic;
import com.google.common.base.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class AnimalAIHerdPanic  extends Goal {
    protected final CreatureEntity creature;
    protected final double speed;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    protected boolean running;
    protected final Predicate<? super CreatureEntity> targetEntitySelector;

    public AnimalAIHerdPanic(CreatureEntity creature, double speedIn) {
        this.creature = creature;
        this.speed = speedIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        this.targetEntitySelector = new Predicate<CreatureEntity>() {
            @Override
            public boolean apply(@Nullable CreatureEntity animal) {
                if(animal instanceof IHerdPanic && animal.getType() == creature.getType()){
                    return ((IHerdPanic)animal).canPanic();
                }
                return false;
            }
        };
    }

    public boolean shouldExecute() {
        if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
            return false;
        } else {
            if (this.creature.isBurning()) {
                BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 5, 4);
                if (blockpos != null) {
                    this.randPosX = (double)blockpos.getX();
                    this.randPosY = (double)blockpos.getY();
                    this.randPosZ = (double)blockpos.getZ();
                    return true;
                }
            }
            if(this.creature.getRevengeTarget() != null && this.creature instanceof IHerdPanic && ((IHerdPanic) this.creature).canPanic()){

                List<CreatureEntity> list = this.creature.world.getEntitiesWithinAABB(this.creature.getClass(), this.getTargetableArea(), this.targetEntitySelector);
                for(CreatureEntity creatureEntity : list){
                    creatureEntity.setRevengeTarget(this.creature.getRevengeTarget());
                }
                return this.findRandomPositionFrom(this.creature.getRevengeTarget());
            }
            return this.findRandomPosition();
        }
    }

    private boolean findRandomPositionFrom(LivingEntity revengeTarget) {
        Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 7, revengeTarget.getPositionVec());
        if (vector3d == null) {
            return false;
        } else {
            this.randPosX = vector3d.x;
            this.randPosY = vector3d.y;
            this.randPosZ = vector3d.z;
            return true;
        }
    }

    protected AxisAlignedBB getTargetableArea() {
        Vector3d renderCenter = new Vector3d(this.creature.getPosX() + 0.5, this.creature.getPosY()+ 0.5D, this.creature.getPosZ() + 0.5D);
        double searchRadius = 15;
        AxisAlignedBB aabb = new AxisAlignedBB(-searchRadius, -searchRadius, -searchRadius, searchRadius, searchRadius, searchRadius);
        return aabb.offset(renderCenter);
    }

    protected boolean findRandomPosition() {
        Vector3d vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 5, 4);
        if (vector3d == null) {
            return false;
        } else {
            this.randPosX = vector3d.x;
            this.randPosY = vector3d.y;
            this.randPosZ = vector3d.z;
            return true;
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        if(this.creature instanceof IHerdPanic){
            ((IHerdPanic) this.creature).onPanic();
        }
        this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
        this.running = true;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.running = false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    @Nullable
    protected BlockPos getRandPos(IBlockReader worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
        BlockPos blockpos = entityIn.getPosition();
        int i = blockpos.getX();
        int j = blockpos.getY();
        int k = blockpos.getZ();
        float f = (float)(horizontalRange * horizontalRange * verticalRange * 2);
        BlockPos blockpos1 = null;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(int l = i - horizontalRange; l <= i + horizontalRange; ++l) {
            for(int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1) {
                for(int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1) {
                    blockpos$mutable.setPos(l, i1, j1);
                    if (worldIn.getFluidState(blockpos$mutable).isTagged(FluidTags.WATER)) {
                        float f1 = (float)((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));
                        if (f1 < f) {
                            f = f1;
                            blockpos1 = new BlockPos(blockpos$mutable);
                        }
                    }
                }
            }
        }

        return blockpos1;
    }
}
