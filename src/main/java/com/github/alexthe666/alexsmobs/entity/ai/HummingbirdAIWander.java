package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class HummingbirdAIWander extends Goal {
    private EntityHummingbird fly;
    private int rangeXZ;
    private int rangeY;
    private int chance;
    private float speed;
    private Vector3d moveToPoint = null;

    public HummingbirdAIWander(EntityHummingbird fly, int rangeXZ, int rangeY, int chance, float speed) {
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        this.fly = fly;
        this.rangeXZ = rangeXZ;
        this.rangeY = rangeY;
        this.chance = chance;
        this.speed = speed;
    }

    public boolean shouldExecute() {
        return fly.hummingStill > 10 && fly.getRNG().nextInt(chance) == 0 && !fly.getMoveHelper().isUpdating();
    }

    public void resetTask() {
        moveToPoint = null;
    }

    public boolean shouldContinueExecuting() {
        return moveToPoint != null && fly.getDistanceSq(moveToPoint) > 0.85D;
    }

    public void startExecuting() {
        moveToPoint = this.getRandomLocation();
        if (moveToPoint != null) {
            fly.getMoveHelper().setMoveTo(moveToPoint.x, moveToPoint.y, moveToPoint.z, speed);
        }
    }

    public void tick() {
        if (moveToPoint != null) {
            fly.getMoveHelper().setMoveTo(moveToPoint.x, moveToPoint.y, moveToPoint.z, speed);
        }
    }

    @Nullable
    private Vector3d getRandomLocation() {
        Random random = fly.getRNG();
        BlockPos blockpos = null;
        for(int i = 0; i < 15; i++){
            BlockPos blockpos1 = this.fly.getPosition().add(random.nextInt(rangeXZ) - rangeXZ/2, random.nextInt(rangeY) - rangeY/2, random.nextInt(rangeXZ) - rangeXZ/2);
            if(this.fly.world.isAirBlock(blockpos1.down()) && this.fly.world.isAirBlock(blockpos1) && !this.fly.world.isAirBlock(blockpos1.down(2))){
                blockpos = blockpos1;
            }
        }
        return blockpos == null ? null : new Vector3d(blockpos.getX() +  0.5D, blockpos.getY() +  0.5D, blockpos.getZ() + 0.5D);
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        double x = pos.getX() + 0.5F;
        double y = pos.getY() + 0.5F;
        double z = pos.getZ() + 0.5F;
        RayTraceResult result = fly.world.rayTraceBlocks(new RayTraceContext(new Vector3d(fly.getPosX(), fly.getPosY() + (double) fly.getEyeHeight(), fly.getPosZ()), new Vector3d(x, y, z), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, fly));
        double dist = result.getHitVec().squareDistanceTo(x, y, z);
        return dist <= 1.0D || result.getType() == RayTraceResult.Type.MISS;
    }

}
