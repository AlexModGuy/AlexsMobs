package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.misc.AMVoxelShapeSpliterator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ICustomCollisions {
    boolean canPassThrough(BlockPos mutablePos, BlockState blockstate, VoxelShape voxelshape);

    default Vector3d collideBoundingBoxHeuristicallyPassable(@Nullable Entity entity, Vector3d vec, AxisAlignedBB collisionBox, World world, ISelectionContext context, ReuseableStream<VoxelShape> potentialHits) {
        boolean flag = vec.x == 0.0D;
        boolean flag1 = vec.y == 0.0D;
        boolean flag2 = vec.z == 0.0D;
        if ((!flag || !flag1) && (!flag || !flag2) && (!flag1 || !flag2)) {
            ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(potentialHits.createStream(), StreamSupport.stream(new AMVoxelShapeSpliterator(world, entity,  collisionBox.expand(vec)), false)));
            return Entity.collideBoundingBox(vec, collisionBox, reuseablestream);
        } else {
            return Entity.getAllowedMovement(vec, collisionBox, world, context, potentialHits);
        }
    }
}
