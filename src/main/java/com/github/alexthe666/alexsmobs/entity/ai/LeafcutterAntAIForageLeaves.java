package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelReader;

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

    public boolean canUse() {
        return !ant.isBaby() && !ant.hasLeaf() && !ant.isBaby() && !ant.isQueen() && super.canUse();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && !ant.hasLeaf();
    }

    public void stop() {
        idleAtLeavesTime = 0;
        logStartPos = null;
        logTopPos = null;
    }

    public double acceptedDistance() {
        return 2.0D;
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0 && logStartPos == null;
    }

    public void tick() {
        if (randomLeafCheckCooldown > 0) {
            randomLeafCheckCooldown--;
        } else {
            randomLeafCheckCooldown = 30 + ant.getRandom().nextInt(50);
            for (Direction dir : Direction.values()) {
                BlockPos offset = this.ant.blockPosition().relative(dir);
                if (isValidTarget(this.ant.level, offset) && ant.getRandom().nextInt(1) == 0) {
                    blockPos = offset;
                    logStartPos = null;
                }
            }
        }

        if (ant.getAttachmentFacing() == Direction.UP) {
            this.ant.getMoveControl().setWantedPosition(blockPos.getX() + 0.5F, blockPos.getY() - 1D, blockPos.getZ() + 0.5F, 1);
            this.ant.setDeltaMovement(ant.getDeltaMovement().add(0, 0.5, 0));
            if (ant.getRandom().nextInt(2) == 0 && isValidTarget(this.ant.level, ant.blockPosition().above())) {
                blockPos = ant.blockPosition().above();
            }
        } else if (blockPos.getY() > ant.getY() + 2F || logStartPos != null) {
            ant.getNavigation().stop();
            if (ant.getRandom().nextInt(5) == 0 && isValidTarget(this.ant.level, ant.blockPosition().below())) {
                blockPos = ant.blockPosition().below();
            }
            if (logStartPos != null) {
                double xDif = logStartPos.getX() + 0.5 - ant.getX();
                double zDif = logStartPos.getZ() + 0.5 - ant.getZ();
                float f = (float)(Mth.atan2(zDif, xDif) * (double)(180F / (float)Math.PI)) - 90.0F;
                ant.setYRot(f);
                ant.yBodyRot = ant.getYRot();
                Vec3 vec = new Vec3(logStartPos.getX() + 0.5, ant.getY(), logStartPos.getZ() + 0.5);
                vec = vec.subtract(ant.position());
                if(ant.isOnGround() || ant.onClimbable())
                this.ant.setDeltaMovement(vec.normalize().multiply(0.1, 0, 0.1).add(0, ant.getDeltaMovement().y, 0));

                this.ant.getNavigation().moveTo(logStartPos.getX(), ant.getY(), logStartPos.getZ(), 1);
                if (Math.abs(xDif) < 0.6 && Math.abs(zDif) < 0.6) {
                    ant.setDeltaMovement(ant.getDeltaMovement().multiply(0D, 1D, 0D));
                    this.ant.getMoveControl().setWantedPosition(logStartPos.getX() + 0.5D, ant.getY() + 2, logStartPos.getZ() + 0.5D, 1);
                    BlockPos test = new BlockPos(logStartPos.getX(), ant.getY(), logStartPos.getZ());
                    if (!BlockTags.LOGS.contains(ant.level.getBlockState(test).getBlock()) && ant.getAttachmentFacing() == Direction.DOWN) {
                        this.stop();
                        return;
                    }
                }
            }else {
                for (int i = 0; i < 15; i++) {
                    BlockPos test = blockPos.offset(6 - ant.getRandom().nextInt(12), -ant.getRandom().nextInt(7), 6 - ant.getRandom().nextInt(12));
                    if (BlockTags.LOGS.contains(ant.level.getBlockState(test).getBlock())) {
                        logStartPos = test;
                        break;
                    }
                }
            }
        tryTicks++;
        } else {
            super.tick();
            logStartPos = null;
        }
        if (this.isReachedTarget() || ant.blockPosition().above().equals(blockPos)) {
            ant.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5));
            ant.setAnimation(EntityLeafcutterAnt.ANIMATION_BITE);
            if (this.idleAtLeavesTime >= 6) {
                ant.setLeafHarvestedPos(blockPos);
                ant.setLeafHarvestedState(ant.level.getBlockState(blockPos));
                if (!ant.hasLeaf()) {
                    this.breakLeaves();
                }
                ant.setLeaf(true);
                stop();
                this.idleAtLeavesTime = 0;
            } else {
                ++this.idleAtLeavesTime;
            }
        }

    }

    private void breakLeaves() {
        BlockState blockstate = ant.level.getBlockState(this.blockPos);
        if (BlockTags.getAllTags().getTag(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES).contains(blockstate.getBlock())) {
            if(net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(ant.level, ant)){
                ant.level.destroyBlock(blockPos, false);
                if (ant.getRandom().nextFloat() > AMConfig.leafcutterAntBreakLeavesChance) {
                    ant.level.setBlockAndUpdate(blockPos, blockstate);
                }
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return BlockTags.getAllTags().getTag(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES).contains(worldIn.getBlockState(pos).getBlock());
    }
}
