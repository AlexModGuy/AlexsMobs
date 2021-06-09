package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.citadel.server.entity.collision.CitadelVoxelShapeSpliterator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ICustomCollisions {
    static Vector3d getAllowedMovementForEntity(Entity entity, Vector3d vec) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        ISelectionContext iselectioncontext = ISelectionContext.forEntity(entity);
        VoxelShape voxelshape = entity.world.getWorldBorder().getShape();
        Stream<VoxelShape> stream = VoxelShapes.compare(voxelshape, VoxelShapes.create(axisalignedbb.shrink(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
        Stream<VoxelShape> stream1 = entity.world.func_230318_c_(entity, axisalignedbb.expand(vec), (p_233561_0_) -> {
            return true;
        });
        ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream(Stream.concat(stream1, stream));
        Vector3d vector3d = vec.lengthSquared() == 0.0D ? vec : ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, vec, axisalignedbb, entity.world, iselectioncontext, reuseablestream);
        boolean flag = vec.x != vector3d.x;
        boolean flag1 = vec.y != vector3d.y;
        boolean flag2 = vec.z != vector3d.z;
        boolean flag3 = entity.isOnGround() || flag1 && vec.y < 0.0D;
        if (entity.stepHeight > 0.0F && flag3 && (flag || flag2)) {
            Vector3d vector3d1 = ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vector3d(vec.x, (double)entity.stepHeight, vec.z), axisalignedbb, entity.world, iselectioncontext, reuseablestream);
            Vector3d vector3d2 = ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vector3d(0.0D, (double)entity.stepHeight, 0.0D), axisalignedbb.expand(vec.x, 0.0D, vec.z), entity.world, iselectioncontext, reuseablestream);
            if (vector3d2.y < (double)entity.stepHeight) {
                Vector3d vector3d3 = ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vector3d(vec.x, 0.0D, vec.z), axisalignedbb.offset(vector3d2), entity.world, iselectioncontext, reuseablestream).add(vector3d2);
                if (Entity.horizontalMag(vector3d3) > Entity.horizontalMag(vector3d1)) {
                    vector3d1 = vector3d3;
                }
            }

            if (Entity.horizontalMag(vector3d1) > Entity.horizontalMag(vector3d)) {
                return vector3d1.add(((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vector3d(0.0D, -vector3d1.y + vec.y, 0.0D), axisalignedbb.offset(vector3d1), entity.world, iselectioncontext, reuseablestream));
            }
        }

        return vector3d;
    }

    boolean canPassThrough(BlockPos var1, BlockState var2, VoxelShape var3);

    default Vector3d collideBoundingBoxHeuristicallyPassable(@Nullable Entity entity, Vector3d vec, AxisAlignedBB collisionBox, World world, ISelectionContext context, ReuseableStream<VoxelShape> potentialHits) {
        boolean flag = vec.x == 0.0D;
        boolean flag1 = vec.y == 0.0D;
        boolean flag2 = vec.z == 0.0D;
        if (flag && flag1 || flag && flag2 || flag1 && flag2) {
            return Entity.getAllowedMovement(vec, collisionBox, world, context, potentialHits);
        } else {
            ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream(Stream.concat(potentialHits.createStream(), StreamSupport.stream(new CitadelVoxelShapeSpliterator(world, entity, collisionBox.expand(vec)), false)));
            return Entity.collideBoundingBox(vec, collisionBox, reuseablestream);
        }
    }
}