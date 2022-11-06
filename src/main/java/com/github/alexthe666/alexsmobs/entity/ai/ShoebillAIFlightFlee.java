package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ShoebillAIFlightFlee extends Goal {

    private EntityShoebill bird;
    private BlockPos currentTarget = null;
    private int executionTime = 0;

    public ShoebillAIFlightFlee(EntityShoebill bird) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.bird = bird;
    }

    public void stop(){
        currentTarget = null;
        executionTime = 0;
        bird.setFlying(false);
    }

    public boolean canContinueToUse(){
        return bird.isFlying() && (executionTime < 15 || !bird.isOnGround());
    }

    @Override
    public boolean canUse() {
        return bird.revengeCooldown > 0 && bird.isOnGround();
    }

    public void start(){
        if(bird.isOnGround()){
            bird.setFlying(true);
        }
    }

    public void tick() {
        executionTime++;
        if (currentTarget == null) {
            if (bird.revengeCooldown == 0) {
                currentTarget = getBlockGrounding(bird.position());
            } else {
                currentTarget = getBlockInViewAway(bird.position());
            }
        }
        if (currentTarget != null) {
            bird.getNavigation().moveTo(currentTarget.getX() + 0.5F, currentTarget.getY() + 0.5F, currentTarget.getZ() + 0.5F, 1F);
            if(this.bird.distanceToSqr(Vec3.atCenterOf(currentTarget)) < 4){
                currentTarget = null;
            }
        }
        if (bird.revengeCooldown == 0 && (bird.isInWater() || !bird.level.isEmptyBlock(bird.blockPosition().below()))) {
            stop();
            bird.setFlying(false);
        }
    }
    public BlockPos getBlockInViewAway(Vec3 fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - bird.getRandom().nextInt(24);
        float neg = bird.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = bird.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (bird.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, 0, fleePos.z() + extraZ);
        BlockPos ground = bird.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
        int distFromGround = (int) bird.getY() - ground.getY();
        int flightHeight = 4 + bird.getRandom().nextInt(10);
        BlockPos newPos = radialPos.above(distFromGround > 8 ? flightHeight : (int) bird.getY() + bird.getRandom().nextInt(6) + 1);
        if (!bird.isTargetBlocked(Vec3.atCenterOf(newPos)) && bird.distanceToSqr(Vec3.atCenterOf(newPos)) > 6) {
            return newPos;
        }
        return null;
    }

    public BlockPos getBlockGrounding(Vec3 fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - bird.getRandom().nextInt(24);
        float neg = bird.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = bird.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (bird.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, 0, fleePos.z() + extraZ);
        BlockPos ground = bird.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
        if (!bird.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return ground;
        }
        return null;
    }
}
