package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.citadel.server.entity.collision.CitadelVoxelShapeSpliterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RewindableStream;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ICustomCollisions {
    static Vec3 getAllowedMovementForEntity(Entity entity, Vec3 vec) {
        AABB axisalignedbb = entity.getBoundingBox();
        CollisionContext iselectioncontext = CollisionContext.of(entity);
        VoxelShape voxelshape = entity.level.getWorldBorder().getCollisionShape();
        Stream<VoxelShape> stream = Shapes.joinIsNotEmpty(voxelshape, Shapes.create(axisalignedbb.deflate(1.0E-7D)), BooleanOp.AND) ? Stream.empty() : Stream.of(voxelshape);
        Stream<VoxelShape> stream1 = entity.level.getEntityCollisions(entity, axisalignedbb.expandTowards(vec), (p_233561_0_) -> {
            return true;
        });
        RewindableStream<VoxelShape> reuseablestream = new RewindableStream(Stream.concat(stream1, stream));
        Vec3 vector3d = vec.lengthSqr() == 0.0D ? vec : ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, vec, axisalignedbb, entity.level, iselectioncontext, reuseablestream);
        boolean flag = vec.x != vector3d.x;
        boolean flag1 = vec.y != vector3d.y;
        boolean flag2 = vec.z != vector3d.z;
        boolean flag3 = entity.isOnGround() || flag1 && vec.y < 0.0D;
        if (entity.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vector3d1 = ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vec3(vec.x, (double)entity.maxUpStep, vec.z), axisalignedbb, entity.level, iselectioncontext, reuseablestream);
            Vec3 vector3d2 = ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vec3(0.0D, (double)entity.maxUpStep, 0.0D), axisalignedbb.expandTowards(vec.x, 0.0D, vec.z), entity.level, iselectioncontext, reuseablestream);
            if (vector3d2.y < (double)entity.maxUpStep) {
                Vec3 vector3d3 = ((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vec3(vec.x, 0.0D, vec.z), axisalignedbb.move(vector3d2), entity.level, iselectioncontext, reuseablestream).add(vector3d2);
                if (vector3d3.horizontalDistanceSqr() > vector3d1.horizontalDistanceSqr()) {
                    vector3d1 = vector3d3;
                }
            }

            if ( vector3d1.horizontalDistanceSqr() >  vector3d.horizontalDistanceSqr()) {
                return vector3d1.add(((ICustomCollisions)entity).collideBoundingBoxHeuristicallyPassable(entity, new Vec3(0.0D, -vector3d1.y + vec.y, 0.0D), axisalignedbb.move(vector3d1), entity.level, iselectioncontext, reuseablestream));
            }
        }

        return vector3d;
    }

    boolean canPassThrough(BlockPos var1, BlockState var2, VoxelShape var3);

    default Vec3 collideBoundingBoxHeuristicallyPassable(@Nullable Entity entity, Vec3 vec, AABB collisionBox, Level world, CollisionContext context, RewindableStream<VoxelShape> potentialHits) {
        boolean flag = vec.x == 0.0D;
        boolean flag1 = vec.y == 0.0D;
        boolean flag2 = vec.z == 0.0D;
        if (flag && flag1 || flag && flag2 || flag1 && flag2) {
            return Entity.collideBoundingBox(vec, collisionBox, world, context, potentialHits);
        } else {
            RewindableStream<VoxelShape> reuseablestream = new RewindableStream(Stream.concat(potentialHits.getStream(), StreamSupport.stream(new CitadelVoxelShapeSpliterator(world, entity, collisionBox.expandTowards(vec)), false)));
            return Entity.collideBoundingBoxLegacy(vec, collisionBox, reuseablestream);
        }
    }
}