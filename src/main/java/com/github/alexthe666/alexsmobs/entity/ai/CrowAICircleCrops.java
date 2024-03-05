package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrowAICircleCrops extends MoveToBlockGoal {

    private final EntityCrow crow;
    private int idleAtFlowerTime = 0;
    private boolean isAboveDestinationBear;
    float circlingTime = 0;
    float circleDistance = 2;
    float maxCirclingTime = 80;
    float yLevel = 2;
    boolean clockwise = false;
    boolean circlePhase = false;

    public CrowAICircleCrops(EntityCrow bird) {
        super(bird, 1D, 32, 8);
        this.crow = bird;
    }

    public void start() {
        super.start();
        circlePhase = true;
        clockwise = crow.getRandom().nextBoolean();
        yLevel = 1 + crow.getRandom().nextInt(3);
        circleDistance = 1 + crow.getRandom().nextInt(3);
    }

    public boolean canUse() {
        return !crow.isBaby() && AMConfig.crowsStealCrops && (crow.getTarget() == null || !crow.getTarget().isAlive()) && !crow.isTame() && crow.fleePumpkinFlag == 0 && !crow.aiItemFlag && super.canUse();
    }

    public boolean canContinueToUse() {
        return blockPos != null && AMConfig.crowsStealCrops && (crow.getTarget() == null || !crow.getTarget().isAlive()) && !crow.isTame() && !crow.aiItemFlag && crow.fleePumpkinFlag == 0 && super.canContinueToUse();
    }

    public void stop() {
        idleAtFlowerTime = 0;
        circlingTime = 0;
        tryTicks = 0;
        blockPos = BlockPos.ZERO;
    }

    public double acceptedDistance() {
        return 1D;
    }

    public void tick() {
        if(blockPos == null){
            return;
        }
        BlockPos blockpos = this.getMoveToTarget();
        if(circlePhase){
            this.tryTicks = 0;
            BlockPos circlePos = getVultureCirclePos(blockpos);
            if (circlePos != null) {
                crow.setFlying(true);
                crow.getMoveControl().setWantedPosition(circlePos.getX() + 0.5D, circlePos.getY() + 0.5D, circlePos.getZ() + 0.5D, 0.7F);
            }
            circlingTime++;
            if(circlingTime > 200){
                circlingTime = 0;
                circlePhase = false;
            }
        }else{
            super.tick();
            if(crow.onGround()){
                crow.setFlying(false);
            }
            if (!isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
                this.isAboveDestinationBear = false;
                ++this.tryTicks;
                this.mob.getNavigation().moveTo((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY() - 0.5D, (double) ((float) blockpos.getZ()) + 0.5D, 1);
            } else {
                this.isAboveDestinationBear = true;
                --this.tryTicks;
            }

            if (this.isReachedTarget()) {
                crow.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5));
                if (this.idleAtFlowerTime >= 5) {
                    this.destroyCrop();
                    this.stop();
                } else {
                    crow.peck();
                    ++this.idleAtFlowerTime;
                }
            }
        }
    }

    public BlockPos getVultureCirclePos(BlockPos target) {
        float angle = (Maths.EIGHT_STARTING_ANGLE * (clockwise ? -circlingTime : circlingTime));
        double extraX = circleDistance * Mth.sin((angle));
        double extraZ = circleDistance * Mth.cos(angle);
        BlockPos pos = AMBlockPos.fromCoords(target.getX() + 0.5F + extraX, target.getY() + 1 + yLevel, target.getZ() + 0.5F + extraZ);
        if (crow.level().isEmptyBlock(pos)) {
            return pos;
        }
        return null;
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
        return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
    }

    protected boolean isReachedTarget() {
        return this.isAboveDestinationBear;
    }

    private void destroyCrop() {
        if(!canSeeBlock(blockPos)){
            stop();
            tryTicks = 1200;
            return;
        }
        if(crow.level().getBlockState(blockPos).getBlock() instanceof CropBlock){
            if(crow.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)){
                CropBlock block = (CropBlock)crow.level().getBlockState(blockPos).getBlock();
                int cropAge = block.getAge(crow.level().getBlockState(blockPos));
                if(cropAge > 0){
                    crow.level().setBlockAndUpdate(blockPos, block.getStateForAge(Math.max(0, cropAge - 1)));
                }else{
                    crow.level().destroyBlock(blockPos, true);
                }
            }
        }else{
            if(crow.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                crow.level().destroyBlock(blockPos, true);
            }
        }
        stop();
        tryTicks = 1200;
    }


    private boolean canSeeBlock(BlockPos destinationBlock) {
        final Vec3 Vector3d = new Vec3(crow.getX(), crow.getEyeY(), crow.getZ());
        final Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        final BlockHitResult result = crow.level().clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, crow));
        return result.getBlockPos().equals(destinationBlock);
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(AMTagRegistry.CROW_FOODBLOCKS);
    }
}
