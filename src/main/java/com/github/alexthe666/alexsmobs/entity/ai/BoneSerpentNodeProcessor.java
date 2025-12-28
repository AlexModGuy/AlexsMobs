package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.*;

import javax.annotation.Nullable;

public class BoneSerpentNodeProcessor extends NodeEvaluator {

    public BoneSerpentNodeProcessor() {
    }

    public Node getStart() {
        return super.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    public Target getGoal(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
        return new Target(super.getNode(Mth.floor(p_224768_1_ - (double)(this.mob.getBbWidth() / 2.0F)), Mth.floor(p_224768_3_ + 0.5D), Mth.floor(p_224768_5_ - (double)(this.mob.getBbWidth() / 2.0F))));
    }

    @Override
    public Target getTarget(double x, double y, double z) {
        return new Target(super.getNode(Mth.floor(x), Mth.floor(y), Mth.floor(z)));
    }

    public int getNeighbors(Node[] p_222859_1_, Node p_222859_2_) {
        int i = 0;

        for(Direction direction : Direction.values()) {
            Node pathpoint = this.getWaterNode(p_222859_2_.x + direction.getStepX(), p_222859_2_.y + direction.getStepY(), p_222859_2_.z + direction.getStepZ());
            if (pathpoint != null && !pathpoint.closed) {
                p_222859_1_[i++] = pathpoint;
            }
        }

        return i;
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        return this.getBlockPathType(context, x, y, z);
    }

    @Override
    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        return this.getBlockPathType(context, x, y, z);
    }

    public PathType getBlockPathType(PathfindingContext context, int x, int y, int z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        FluidState fluidstate = context.level().getFluidState(blockpos);
        BlockState blockstate = context.getBlockState(blockpos);
        if (fluidstate.isEmpty() && blockstate.isPathfindable(PathComputationType.WATER) && blockstate.isAir()) {
            return PathType.BREACH;
        } else {
            return fluidstate.is(FluidTags.LAVA) || fluidstate.is(FluidTags.WATER) && blockstate.isPathfindable(PathComputationType.WATER) ? PathType.WATER : PathType.BLOCKED;
        }
    }

    @Nullable
    private Node getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        PathType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype != PathType.BREACH && pathnodetype != PathType.WATER && pathnodetype != PathType.LAVA ? null : this.getNode(p_186328_1_, p_186328_2_, p_186328_3_);
    }

    /**
     * Returns a mapped point or creates and adds one
     */
    @Nullable
    protected Node getNode(int x, int y, int z) {
        Node pathpoint = null;
        PathType pathnodetype = this.getBlockPathType(this.currentContext, x, y, z);
        float f = this.mob.getPathfindingMalus(pathnodetype);
        if (f >= 0.0F) {
            pathpoint = super.getNode(x, y, z);
            pathpoint.type = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            if (this.currentContext.level().getFluidState(new BlockPos(x, y, z)).isEmpty()) {
                pathpoint.costMalus += 8.0F;
            }
        }

        return pathnodetype == PathType.OPEN ? pathpoint : pathpoint;
    }

    private PathType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for(int i = p_186327_1_; i < p_186327_1_ + this.entityWidth; ++i) {
            for(int j = p_186327_2_; j < p_186327_2_ + this.entityHeight; ++j) {
                for(int k = p_186327_3_; k < p_186327_3_ + this.entityDepth; ++k) {
                    FluidState fluidstate = this.currentContext.level().getFluidState(blockpos$mutable.set(i, j, k));
                    BlockState blockstate = this.currentContext.getBlockState(blockpos$mutable.set(i, j, k));
                    if (fluidstate.isEmpty() && blockstate.isPathfindable(PathComputationType.WATER) && blockstate.isAir()) {
                        return PathType.BREACH;
                    }

                    if (!fluidstate.is(FluidTags.WATER) && !fluidstate.is(FluidTags.LAVA)) {
                        return PathType.BLOCKED;
                    }
                }
            }
        }

        BlockState blockstate1 = this.currentContext.getBlockState(blockpos$mutable);
        return blockstate1.getFluidState().is(FluidTags.LAVA) || blockstate1.isPathfindable(PathComputationType.WATER) ? PathType.WATER : PathType.BLOCKED;
    }
}
