package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class HummingbirdAIWander extends Goal {
    private EntityHummingbird fly;
    private int rangeXZ;
    private int rangeY;
    private int chance;
    private float speed;
    private Vec3 moveToPoint = null;

    public HummingbirdAIWander(EntityHummingbird fly, int rangeXZ, int rangeY, int chance, float speed) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.fly = fly;
        this.rangeXZ = rangeXZ;
        this.rangeY = rangeY;
        this.chance = chance;
        this.speed = speed;
    }

    public boolean canUse() {
        return fly.hummingStill > 10 && fly.getRandom().nextInt(chance) == 0 && !fly.getMoveControl().hasWanted();
    }

    public void stop() {
        moveToPoint = null;
    }

    public boolean canContinueToUse() {
        return moveToPoint != null && fly.distanceToSqr(moveToPoint) > 0.85D;
    }

    public void start() {
        moveToPoint = this.getRandomLocation();
        if (moveToPoint != null) {
            fly.getMoveControl().setWantedPosition(moveToPoint.x, moveToPoint.y, moveToPoint.z, speed);
        }
    }

    public void tick() {
        if (moveToPoint != null) {
            fly.getMoveControl().setWantedPosition(moveToPoint.x, moveToPoint.y, moveToPoint.z, speed);
        }
    }

    @Nullable
    private Vec3 getRandomLocation() {
        final RandomSource random = this.fly.getRandom();
        BlockPos blockpos = null;
        BlockPos origin = fly.getFeederPos() == null ? this.fly.blockPosition() : fly.getFeederPos();
        for(int i = 0; i < 15; i++){
            BlockPos blockpos1 = origin.offset(random.nextInt(rangeXZ) - rangeXZ/2, 1, random.nextInt(rangeXZ) - rangeXZ/2);
            while(fly.level.isEmptyBlock(blockpos1) && blockpos1.getY() > 0){
                blockpos1 = blockpos1.below();
            }
            blockpos1 = blockpos1.above(1 + random.nextInt(3));
            if(this.fly.level.isEmptyBlock(blockpos1.below()) && this.fly.canBlockBeSeen(blockpos1) && this.fly.level.isEmptyBlock(blockpos1) && !this.fly.level.isEmptyBlock(blockpos1.below(2))){
                blockpos = blockpos1;
            }
        }
        return blockpos == null ? null : new Vec3(blockpos.getX() +  0.5D, blockpos.getY() +  0.5D, blockpos.getZ() + 0.5D);
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        double x = pos.getX() + 0.5F;
        double y = pos.getY() + 0.5F;
        double z = pos.getZ() + 0.5F;
        HitResult result = fly.level.clip(new ClipContext(new Vec3(fly.getX(), fly.getY() + (double) fly.getEyeHeight(), fly.getZ()), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, fly));
        double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

}
