package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

public class LeafcutterAntAIForageLeaves extends MoveToBlockGoal {

    private EntityLeafcutterAnt ant;
    private int idleAtLeavesTime = 0;
    private int randomLeafCheckCooldown = 40;
    private BlockPos logStartPos = null;
    private BlockPos logTopPos = null;

    public LeafcutterAntAIForageLeaves(EntityLeafcutterAnt LeafcutterAnt) {
        super(LeafcutterAnt, 1D, 15, 3);
        this.ant = LeafcutterAnt;
    }

    public boolean shouldExecute() {
        return !ant.isChild() && !ant.hasLeaf() && !ant.isChild() && !ant.isQueen() && super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && !ant.hasLeaf();
    }

    public void resetTask() {
        idleAtLeavesTime = 0;
        logStartPos = null;
        logTopPos = null;
    }

    public double getTargetDistanceSq() {
        return 2.0D;
    }

    public boolean shouldMove() {
        return this.timeoutCounter % 40 == 0 && logStartPos == null;
    }

    public void tick() {
        if (randomLeafCheckCooldown > 0) {
            randomLeafCheckCooldown--;
        } else {
            randomLeafCheckCooldown = 30 + ant.getRNG().nextInt(50);
            for (Direction dir : Direction.values()) {
                BlockPos offset = this.ant.getPosition().offset(dir);
                if (shouldMoveTo(this.ant.world, offset) && ant.getRNG().nextInt(1) == 0) {
                    destinationBlock = offset;
                    logStartPos = null;
                }
            }
        }

        if (ant.getAttachmentFacing() == Direction.UP) {
            this.ant.getMoveHelper().setMoveTo(destinationBlock.getX() + 0.5F, destinationBlock.getY() - 1D, destinationBlock.getZ() + 0.5F, 1);
            this.ant.setMotion(ant.getMotion().add(0, 0.5, 0));
            if (ant.getRNG().nextInt(2) == 0 && shouldMoveTo(this.ant.world, ant.getPosition().up())) {
                destinationBlock = ant.getPosition().up();
            }
        } else if (destinationBlock.getY() > ant.getPosY() + 2F || logStartPos != null) {
            ant.getNavigator().clearPath();
            if (ant.getRNG().nextInt(5) == 0 && shouldMoveTo(this.ant.world, ant.getPosition().down())) {
                destinationBlock = ant.getPosition().down();
            }
            if (logStartPos != null) {
                double xDif = logStartPos.getX() + 0.5 - ant.getPosX();
                double zDif = logStartPos.getZ() + 0.5 - ant.getPosZ();
                float f = (float)(MathHelper.atan2(zDif, xDif) * (double)(180F / (float)Math.PI)) - 90.0F;
                ant.rotationYaw = f;
                ant.renderYawOffset = ant.rotationYaw;
                Vector3d vec = new Vector3d(logStartPos.getX() + 0.5, ant.getPosY(), logStartPos.getZ() + 0.5);
                vec = vec.subtract(ant.getPositionVec());
                if(ant.isOnGround() || ant.isOnLadder())
                this.ant.setMotion(vec.normalize().mul(0.1, 0, 0.1).add(0, ant.getMotion().y, 0));

                this.ant.getNavigator().tryMoveToXYZ(logStartPos.getX(), ant.getPosY(), logStartPos.getZ(), 1);
                if (Math.abs(xDif) < 0.6 && Math.abs(zDif) < 0.6) {
                    ant.setMotion(ant.getMotion().mul(0D, 1D, 0D));
                    this.ant.getMoveHelper().setMoveTo(logStartPos.getX() + 0.5D, ant.getPosY() + 2, logStartPos.getZ() + 0.5D, 1);
                    BlockPos test = new BlockPos(logStartPos.getX(), ant.getPosY(), logStartPos.getZ());
                    if (!BlockTags.LOGS.contains(ant.world.getBlockState(test).getBlock()) && ant.getAttachmentFacing() == Direction.DOWN) {
                        this.resetTask();
                        return;
                    }
                }
            }else {
                for (int i = 0; i < 15; i++) {
                    BlockPos test = destinationBlock.add(6 - ant.getRNG().nextInt(12), -ant.getRNG().nextInt(7), 6 - ant.getRNG().nextInt(12));
                    if (BlockTags.LOGS.contains(ant.world.getBlockState(test).getBlock())) {
                        logStartPos = test;
                        break;
                    }
                }
            }
        timeoutCounter++;
        } else {
            super.tick();
            logStartPos = null;
        }
        if (this.getIsAboveDestination() || ant.getPosition().up().equals(destinationBlock)) {
            ant.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            ant.setAnimation(EntityLeafcutterAnt.ANIMATION_BITE);
            if (this.idleAtLeavesTime >= 6) {
                ant.setLeafHarvestedPos(destinationBlock);
                ant.setLeafHarvestedState(ant.world.getBlockState(destinationBlock));
                if (!ant.hasLeaf()) {
                    this.breakLeaves();
                }
                ant.setLeaf(true);
                resetTask();
                this.idleAtLeavesTime = 0;
            } else {
                ++this.idleAtLeavesTime;
            }
        }

    }

    private void breakLeaves() {
        BlockState blockstate = ant.world.getBlockState(this.destinationBlock);
        if (BlockTags.getCollection().get(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES).contains(blockstate.getBlock())) {
            if(!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(ant.world, ant)){
                ant.world.destroyBlock(destinationBlock, false);
                if (ant.getRNG().nextFloat() > AMConfig.leafcutterAntBreakLeavesChance) {
                    ant.world.setBlockState(destinationBlock, blockstate);
                }
            }
        }
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return BlockTags.getCollection().get(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES).contains(worldIn.getBlockState(pos).getBlock());
    }
}
