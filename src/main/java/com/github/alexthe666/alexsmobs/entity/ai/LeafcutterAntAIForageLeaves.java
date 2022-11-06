package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LeafcutterAntAIForageLeaves extends MoveToBlockGoal {

    private final EntityLeafcutterAnt ant;
    private int idleAtLeavesTime = 0;
    private int randomLeafCheckCooldown = 40;
    private BlockPos logStartPos = null;
    private BlockPos logTopPos = null;
    private final int searchRange;
    private final int verticalSearchRange;

    public LeafcutterAntAIForageLeaves(EntityLeafcutterAnt LeafcutterAnt) {
        super(LeafcutterAnt, 1D, 15, 3);
        searchRange = 15;
        verticalSearchRange = 3;
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
                float f = (float) (Mth.atan2(zDif, xDif) * (double) (180F / (float) Math.PI)) - 90.0F;
                ant.setYRot(f);
                ant.yBodyRot = ant.getYRot();
                Vec3 vec = new Vec3(logStartPos.getX() + 0.5, ant.getY(), logStartPos.getZ() + 0.5);
                vec = vec.subtract(ant.position());
                if (ant.isOnGround() || ant.onClimbable())
                    this.ant.setDeltaMovement(vec.normalize().multiply(0.1, 0, 0.1).add(0, ant.getDeltaMovement().y, 0));

                this.ant.getNavigation().moveTo(logStartPos.getX(), ant.getY(), logStartPos.getZ(), 1);
                if (Math.abs(xDif) < 0.6 && Math.abs(zDif) < 0.6) {
                    ant.setDeltaMovement(ant.getDeltaMovement().multiply(0D, 1D, 0D));
                    this.ant.getMoveControl().setWantedPosition(logStartPos.getX() + 0.5D, ant.getY() + 2, logStartPos.getZ() + 0.5D, 1);
                    BlockPos test = new BlockPos(logStartPos.getX(), ant.getY(), logStartPos.getZ());
                    if (!ant.level.getBlockState(test).is(BlockTags.LOGS) && ant.getAttachmentFacing() == Direction.DOWN) {
                        this.stop();
                        return;
                    }
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    BlockPos test = blockPos.offset(6 - ant.getRandom().nextInt(12), -ant.getRandom().nextInt(7), 6 - ant.getRandom().nextInt(12));
                    if (ant.level.getBlockState(test).is(BlockTags.LOGS)) {
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
        if (blockstate.is(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES)) {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(ant.level, ant)) {
                ant.level.destroyBlock(blockPos, false);
                if (ant.getRandom().nextFloat() > AMConfig.leafcutterAntBreakLeavesChance) {
                    ant.level.setBlockAndUpdate(blockPos, blockstate);
                }
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES);
    }


    @Override
    protected boolean findNearestBlock() {
        int i = this.searchRange;
        int j = this.verticalSearchRange;
        BlockPos blockpos = this.mob.blockPosition();
        if(ant.hasHive() && ant.getHivePos() != null){
            blockpos = ant.getHivePos();
            i *= 2;
        }
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k = this.verticalSearchStart; k <= j; k = k > 0 ? -k : 1 - k) {
            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, i1, k - 1, j1);
                        if (this.mob.isWithinRestriction(blockpos$mutableblockpos) && this.isValidTarget(this.mob.level, blockpos$mutableblockpos)) {
                            this.blockPos = blockpos$mutableblockpos;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
