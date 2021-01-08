package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;

import java.util.EnumSet;

public class ShoebillAIFlightFlee extends Goal {

    private EntityShoebill bird;
    private BlockPos currentTarget = null;
    private int executionTime = 0;

    public ShoebillAIFlightFlee(EntityShoebill bird) {
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        this.bird = bird;
    }

    public void resetTask(){
        currentTarget = null;
        executionTime = 0;
        bird.setFlying(false);
    }

    public boolean shouldContinueExecuting(){
        return bird.isFlying() && (executionTime < 15 || !bird.isOnGround());
    }

    @Override
    public boolean shouldExecute() {
        return bird.revengeCooldown > 0 && bird.isOnGround();
    }

    public void startExecuting(){
        if(bird.isOnGround()){
            bird.setFlying(true);
        }
    }

    public void tick() {
        executionTime++;
        if (currentTarget == null) {
            if (bird.revengeCooldown == 0) {
                currentTarget = getBlockGrounding(bird.getPositionVec());
            } else {
                currentTarget = getBlockInViewAway(bird.getPositionVec());
            }
        }
        if (currentTarget != null) {
            bird.getNavigator().tryMoveToXYZ(currentTarget.getX() + 0.5F, currentTarget.getY() + 0.5F, currentTarget.getZ() + 0.5F, 1F);
            if(this.bird.getDistanceSq(Vector3d.copyCentered(currentTarget)) < 4){
                currentTarget = null;
            }
        }
        if (bird.revengeCooldown == 0 && (bird.isInWater() || !bird.world.isAirBlock(bird.getPosition().down()))) {
            resetTask();
            bird.setFlying(false);
        }
    }
    public BlockPos getBlockInViewAway(Vector3d fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - bird.getRNG().nextInt(24);
        float neg = bird.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = bird.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (bird.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = bird.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, radialPos);
        int distFromGround = (int) bird.getPosY() - ground.getY();
        int flightHeight = 4 + bird.getRNG().nextInt(10);
        BlockPos newPos = radialPos.up(distFromGround > 8 ? flightHeight : (int) bird.getPosY() + bird.getRNG().nextInt(6) + 1);
        if (!bird.isTargetBlocked(Vector3d.copyCentered(newPos)) && bird.getDistanceSq(Vector3d.copyCentered(newPos)) > 6) {
            return newPos;
        }
        return null;
    }

    public BlockPos getBlockGrounding(Vector3d fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - bird.getRNG().nextInt(24);
        float neg = bird.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = bird.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (bird.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = bird.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, radialPos);
        if (!bird.isTargetBlocked(Vector3d.copyCentered(ground.up()))) {
            return ground;
        }
        return null;
    }
}
