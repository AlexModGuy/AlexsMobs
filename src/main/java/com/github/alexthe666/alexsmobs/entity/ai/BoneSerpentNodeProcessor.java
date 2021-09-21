package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.pathfinding.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;

public class BoneSerpentNodeProcessor extends NodeEvaluator {

    public BoneSerpentNodeProcessor() {
    }

    public Node getStart() {
        return super.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    public Target getGoal(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
        return new Target(super.getNode(Mth.floor(p_224768_1_ - (double)(this.mob.getBbWidth() / 2.0F)), Mth.floor(p_224768_3_ + 0.5D), Mth.floor(p_224768_5_ - (double)(this.mob.getBbWidth() / 2.0F))));
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

    public BlockPathTypes getBlockPathType(BlockGetter blockaccessIn, int x, int y, int z, Mob entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return this.getBlockPathType(blockaccessIn, x, y, z);
    }

    public BlockPathTypes getBlockPathType(BlockGetter blockaccessIn, int x, int y, int z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        FluidState fluidstate = blockaccessIn.getFluidState(blockpos);
        BlockState blockstate = blockaccessIn.getBlockState(blockpos);
        if (fluidstate.isEmpty() && blockstate.isPathfindable(blockaccessIn, blockpos.below(), PathComputationType.WATER) && blockstate.isAir()) {
            return BlockPathTypes.BREACH;
        } else {
            return fluidstate.is(FluidTags.LAVA) || fluidstate.is(FluidTags.WATER) && blockstate.isPathfindable(blockaccessIn, blockpos, PathComputationType.WATER) ? BlockPathTypes.WATER : BlockPathTypes.BLOCKED;
        }
    }

    @Nullable
    private Node getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        BlockPathTypes pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype != BlockPathTypes.BREACH && pathnodetype != BlockPathTypes.WATER && pathnodetype != BlockPathTypes.LAVA ? null : this.getNode(p_186328_1_, p_186328_2_, p_186328_3_);
    }

    /**
     * Returns a mapped point or creates and adds one
     */
    @Nullable
    protected Node getNode(int x, int y, int z) {
        Node pathpoint = null;
        BlockPathTypes pathnodetype = this.getBlockPathType(this.mob.level, x, y, z);
        float f = this.mob.getPathfindingMalus(pathnodetype);
        if (f >= 0.0F) {
            pathpoint = super.getNode(x, y, z);
            pathpoint.type = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            if (this.level.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
                pathpoint.costMalus += 8.0F;
            }
        }

        return pathnodetype == BlockPathTypes.OPEN ? pathpoint : pathpoint;
    }

    private BlockPathTypes isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for(int i = p_186327_1_; i < p_186327_1_ + this.entityWidth; ++i) {
            for(int j = p_186327_2_; j < p_186327_2_ + this.entityHeight; ++j) {
                for(int k = p_186327_3_; k < p_186327_3_ + this.entityDepth; ++k) {
                    FluidState fluidstate = this.level.getFluidState(blockpos$mutable.set(i, j, k));
                    BlockState blockstate = this.level.getBlockState(blockpos$mutable.set(i, j, k));
                    if (fluidstate.isEmpty() && blockstate.isPathfindable(this.level, blockpos$mutable.below(), PathComputationType.WATER) && blockstate.isAir()) {
                        return BlockPathTypes.BREACH;
                    }

                    if (!fluidstate.is(FluidTags.WATER) && !fluidstate.is(FluidTags.LAVA)) {
                        return BlockPathTypes.BLOCKED;
                    }
                }
            }
        }

        BlockState blockstate1 = this.level.getBlockState(blockpos$mutable);
        return blockstate1.getFluidState().is(FluidTags.LAVA) || blockstate1.isPathfindable(this.level, blockpos$mutable, PathComputationType.WATER) ? BlockPathTypes.WATER : BlockPathTypes.BLOCKED;
    }
}
