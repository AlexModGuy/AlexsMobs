package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.CropsBlock;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

public class CrowAICircleCrops extends MoveToBlockGoal {

    private EntityCrow crow;
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

    public void startExecuting() {
        super.startExecuting();
        circlePhase = true;
        clockwise = crow.getRNG().nextBoolean();
        yLevel = 1 + crow.getRNG().nextInt(3);
        circleDistance = 1 + crow.getRNG().nextInt(3);
    }

    public boolean shouldExecute() {
        return !crow.isChild() && (crow.getAttackTarget() == null || !crow.getAttackTarget().isAlive()) && !crow.isTamed() && crow.fleePumpkinFlag == 0 && !crow.aiItemFlag && super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        return destinationBlock != null && (crow.getAttackTarget() == null || !crow.getAttackTarget().isAlive()) && !crow.isTamed() && !crow.aiItemFlag && crow.fleePumpkinFlag == 0 && super.shouldContinueExecuting();
    }

    public void resetTask() {
        idleAtFlowerTime = 0;
        circlingTime = 0;
        timeoutCounter = 0;
        destinationBlock = null;
    }

    public double getTargetDistanceSq() {
        return 1D;
    }

    public void tick() {
        BlockPos blockpos = this.func_241846_j();
        if(circlePhase){
            this.timeoutCounter = 0;
            BlockPos circlePos = getVultureCirclePos(blockpos);
            if (circlePos != null) {
                crow.setFlying(true);
                crow.getMoveHelper().setMoveTo(circlePos.getX() + 0.5D, circlePos.getY() + 0.5D, circlePos.getZ() + 0.5D, 0.7F);
            }
            circlingTime++;
            if(circlingTime > 200){
                circlingTime = 0;
                circlePhase = false;
            }
        }else{
            super.tick();
            if(crow.isOnGround()){
                crow.setFlying(false);
            }
            if (!isWithinXZDist(blockpos, this.creature.getPositionVec(), this.getTargetDistanceSq())) {
                this.isAboveDestinationBear = false;
                ++this.timeoutCounter;
                this.creature.getNavigator().tryMoveToXYZ((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY() - 0.5D, (double) ((float) blockpos.getZ()) + 0.5D, 1);
            } else {
                this.isAboveDestinationBear = true;
                --this.timeoutCounter;
            }

            if (this.getIsAboveDestination()) {
                crow.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
                if (this.idleAtFlowerTime >= 5) {
                    this.pollinate();
                    this.resetTask();
                } else {
                    crow.peck();
                    ++this.idleAtFlowerTime;
                }
            }
        }
    }

    public BlockPos getVultureCirclePos(BlockPos target) {
        float angle = (0.01745329251F * 8 * (clockwise ? -circlingTime : circlingTime));
        double extraX = circleDistance * MathHelper.sin((angle));
        double extraZ = circleDistance * MathHelper.cos(angle);
        BlockPos pos = new BlockPos(target.getX() + 0.5F + extraX, target.getY() + 1 + yLevel, target.getZ() + 0.5F + extraZ);
        if (crow.world.isAirBlock(pos)) {
            return pos;
        }
        return null;
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vector3d positionVec, double distance) {
        return blockpos.distanceSq(positionVec.getX(), positionVec.getY(), positionVec.getZ(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void pollinate() {
        if(crow.world.getBlockState(destinationBlock).getBlock() instanceof CropsBlock){
            CropsBlock block = (CropsBlock)crow.world.getBlockState(destinationBlock).getBlock();
            int cropAge = crow.world.getBlockState(destinationBlock).get(block.getAgeProperty());
            if(cropAge > 0){
                crow.world.setBlockState(destinationBlock, crow.world.getBlockState(destinationBlock).with(block.getAgeProperty(), cropAge - 1));
            }else{
                crow.world.destroyBlock(destinationBlock, true);
            }
            resetTask();
        }else{
            crow.world.destroyBlock(destinationBlock, true);
            resetTask();
        }
        timeoutCounter = 1200;
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return BlockTags.getCollection().get(AMTagRegistry.CROW_FOODBLOCKS).contains(worldIn.getBlockState(pos).getBlock());
    }
}
