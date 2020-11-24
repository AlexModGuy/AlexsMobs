package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import net.minecraft.block.*;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

public class EndergradeAIBreakFlowers extends MoveToBlockGoal {

    private EntityEndergrade endergrade;
    private int idleAtFlowerTime = 0;
    private boolean isAboveDestinationBear;

    public EndergradeAIBreakFlowers(EntityEndergrade bird) {
        super(bird, 1D, 32, 8);
        this.endergrade = bird;
    }

    public boolean shouldExecute() {
        return !endergrade.isChild() && !endergrade.hasItemTarget && super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        return !endergrade.hasItemTarget && super.shouldContinueExecuting();
    }

    public void resetTask() {
        idleAtFlowerTime = 0;
        this.endergrade.stopWandering = false;
    }

    public double getTargetDistanceSq() {
        return 2D;
    }

    public void tick() {
        super.tick();
        this.endergrade.stopWandering = true;
        BlockPos blockpos = this.func_241846_j();
        if (!isWithinXZDist(blockpos, this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            this.isAboveDestinationBear = false;
            ++this.timeoutCounter;
            this.creature.getMoveHelper().setMoveTo((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY() - 0.5D, (double) ((float) blockpos.getZ()) + 0.5D, 1);
        } else {
            this.isAboveDestinationBear = true;
            --this.timeoutCounter;
        }

        if (this.getIsAboveDestination() && Math.abs(endergrade.getPosY() - destinationBlock.getY()) <= 2) {
            endergrade.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            if (this.idleAtFlowerTime >= 20) {
                endergrade.bite();
                this.pollinate();
                this.resetTask();
            } else {
                ++this.idleAtFlowerTime;
            }
        }

    }

    private boolean isWithinXZDist(BlockPos blockpos, Vector3d positionVec, double distance) {
        return blockpos.distanceSq(positionVec.getX(), positionVec.getY(), positionVec.getZ(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void pollinate() {
        endergrade.world.destroyBlock(destinationBlock, true);
        resetTask();
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() == Blocks.CHORUS_FLOWER;
    }
}
