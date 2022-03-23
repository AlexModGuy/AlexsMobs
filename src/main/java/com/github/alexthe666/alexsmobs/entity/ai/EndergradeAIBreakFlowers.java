package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelReader;

public class EndergradeAIBreakFlowers extends MoveToBlockGoal {

    private EntityEndergrade endergrade;
    private int idleAtFlowerTime = 0;
    private boolean isAboveDestinationBear;

    public EndergradeAIBreakFlowers(EntityEndergrade bird) {
        super(bird, 1D, 32, 8);
        this.endergrade = bird;
    }

    public boolean canUse() {
        return !endergrade.isBaby() && !endergrade.hasItemTarget && super.canUse();
    }

    public boolean canContinueToUse() {
        return !endergrade.hasItemTarget && super.canContinueToUse();
    }

    public void stop() {
        idleAtFlowerTime = 0;
        this.endergrade.stopWandering = false;
    }

    public double acceptedDistance() {
        return 2D;
    }

    public void tick() {
        super.tick();
        this.endergrade.stopWandering = true;
        BlockPos blockpos = this.getMoveToTarget();
        if (!isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
            this.isAboveDestinationBear = false;
            ++this.tryTicks;
            this.mob.getMoveControl().setWantedPosition((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY() - 0.5D, (double) ((float) blockpos.getZ()) + 0.5D, 1);
        } else {
            this.isAboveDestinationBear = true;
            --this.tryTicks;
        }

        if (this.isReachedTarget() && Math.abs(endergrade.getY() - blockPos.getY()) <= 2) {
            endergrade.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5));
            if (this.idleAtFlowerTime >= 20) {
                endergrade.bite();
                this.pollinate();
                this.stop();
            } else {
                ++this.idleAtFlowerTime;
            }
        }

    }

    private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
        return blockpos.distSqr(new BlockPos(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
    }

    protected boolean isReachedTarget() {
        return this.isAboveDestinationBear;
    }

    private void pollinate() {
        endergrade.level.destroyBlock(blockPos, true);
        stop();
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() == Blocks.CHORUS_FLOWER;
    }
}
