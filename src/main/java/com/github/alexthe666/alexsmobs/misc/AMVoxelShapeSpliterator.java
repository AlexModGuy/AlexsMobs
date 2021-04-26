package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.entity.ICustomCollisions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ICollisionReader;
import net.minecraft.world.border.WorldBorder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class AMVoxelShapeSpliterator extends Spliterators.AbstractSpliterator<VoxelShape> {
    @Nullable
    private final Entity entity;
    private final AxisAlignedBB aabb;
    private final ISelectionContext context;
    private final CubeCoordinateIterator cubeCoordinateIterator;
    private final BlockPos.Mutable mutablePos;
    private final VoxelShape shape;
    private final ICollisionReader reader;
    private final BiPredicate<BlockState, BlockPos> statePositionPredicate;
    private boolean field_234875_h_;

    public AMVoxelShapeSpliterator(ICollisionReader reader, @Nullable Entity entity, AxisAlignedBB aabb) {
        this(reader, entity, aabb, (p_241459_0_, p_241459_1_) -> {
            return true;
        });
    }

    public AMVoxelShapeSpliterator(ICollisionReader reader, @Nullable Entity entity, AxisAlignedBB aabb, BiPredicate<BlockState, BlockPos> statePositionPredicate) {
        super(Long.MAX_VALUE, 1280);
        this.context = entity == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(entity);
        this.mutablePos = new BlockPos.Mutable();
        this.shape = VoxelShapes.create(aabb);
        this.reader = reader;
        this.field_234875_h_ = entity != null;
        this.entity = entity;
        this.aabb = aabb;
        this.statePositionPredicate = statePositionPredicate;
        int i = MathHelper.floor(aabb.minX - 1.0E-7D) - 1;
        int j = MathHelper.floor(aabb.maxX + 1.0E-7D) + 1;
        int k = MathHelper.floor(aabb.minY - 1.0E-7D) - 1;
        int l = MathHelper.floor(aabb.maxY + 1.0E-7D) + 1;
        int i1 = MathHelper.floor(aabb.minZ - 1.0E-7D) - 1;
        int j1 = MathHelper.floor(aabb.maxZ + 1.0E-7D) + 1;
        this.cubeCoordinateIterator = new CubeCoordinateIterator(i, k, i1, j, l, j1);
    }

    private static boolean func_241460_a_(VoxelShape p_241460_0_, AxisAlignedBB p_241460_1_) {
        return VoxelShapes.compare(p_241460_0_, VoxelShapes.create(p_241460_1_.grow(1.0E-7D)), IBooleanFunction.AND);
    }

    private static boolean func_241461_b_(VoxelShape p_241461_0_, AxisAlignedBB p_241461_1_) {
        return VoxelShapes.compare(p_241461_0_, VoxelShapes.create(p_241461_1_.shrink(1.0E-7D)), IBooleanFunction.AND);
    }

    public static boolean func_234877_a_(WorldBorder p_234877_0_, AxisAlignedBB p_234877_1_) {
        double d0 = MathHelper.floor(p_234877_0_.minX());
        double d1 = MathHelper.floor(p_234877_0_.minZ());
        double d2 = MathHelper.ceil(p_234877_0_.maxX());
        double d3 = MathHelper.ceil(p_234877_0_.maxZ());
        return p_234877_1_.minX > d0 && p_234877_1_.minX < d2 && p_234877_1_.minZ > d1 && p_234877_1_.minZ < d3 && p_234877_1_.maxX > d0 && p_234877_1_.maxX < d2 && p_234877_1_.maxZ > d1 && p_234877_1_.maxZ < d3;
    }

    public boolean tryAdvance(Consumer<? super VoxelShape> p_tryAdvance_1_) {
        return this.field_234875_h_ && this.func_234879_b_(p_tryAdvance_1_) || this.func_234878_a_(p_tryAdvance_1_);
    }

    boolean func_234878_a_(Consumer<? super VoxelShape> p_234878_1_) {
        while (true) {
            if (this.cubeCoordinateIterator.hasNext()) {
                int i = this.cubeCoordinateIterator.getX();
                int j = this.cubeCoordinateIterator.getY();
                int k = this.cubeCoordinateIterator.getZ();
                int l = this.cubeCoordinateIterator.numBoundariesTouched();
                if (l == 3) {
                    continue;
                }

                IBlockReader iblockreader = this.func_234876_a_(i, k);
                if (iblockreader == null) {
                    continue;
                }

                this.mutablePos.setPos(i, j, k);
                BlockState blockstate = iblockreader.getBlockState(this.mutablePos);
                if (!this.statePositionPredicate.test(blockstate, this.mutablePos) || l == 1 && !blockstate.isCollisionShapeLargerThanFullBlock() || l == 2 && !blockstate.isIn(Blocks.MOVING_PISTON)) {
                    continue;
                }
                VoxelShape voxelshape = blockstate.getCollisionShape(this.reader, this.mutablePos, this.context);
                if (entity instanceof ICustomCollisions && ((ICustomCollisions) entity).canPassThrough(mutablePos, blockstate, voxelshape)) {
                    continue;
                }
                if (voxelshape == VoxelShapes.fullCube()) {
                    if (!this.aabb.intersects(i, j, k, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D)) {
                        continue;
                    }

                    p_234878_1_.accept(voxelshape.withOffset(i, j, k));
                    return true;
                }

                VoxelShape voxelshape1 = voxelshape.withOffset(i, j, k);
                if (!VoxelShapes.compare(voxelshape1, this.shape, IBooleanFunction.AND)) {
                    continue;
                }

                p_234878_1_.accept(voxelshape1);
                return true;
            }

            return false;
        }
    }

    @Nullable
    private IBlockReader func_234876_a_(int p_234876_1_, int p_234876_2_) {
        int i = p_234876_1_ >> 4;
        int j = p_234876_2_ >> 4;
        return this.reader.getBlockReader(i, j);
    }

    boolean func_234879_b_(Consumer<? super VoxelShape> p_234879_1_) {
        Objects.requireNonNull(this.entity);
        this.field_234875_h_ = false;
        WorldBorder worldborder = this.reader.getWorldBorder();
        AxisAlignedBB axisalignedbb = this.entity.getBoundingBox();
        if (!func_234877_a_(worldborder, axisalignedbb)) {
            VoxelShape voxelshape = worldborder.getShape();
            if (!func_241461_b_(voxelshape, axisalignedbb) && func_241460_a_(voxelshape, axisalignedbb)) {
                p_234879_1_.accept(voxelshape);
                return true;
            }
        }

        return false;
    }
}
