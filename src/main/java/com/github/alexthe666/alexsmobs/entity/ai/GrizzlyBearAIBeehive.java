package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

import java.util.Random;

public class GrizzlyBearAIBeehive extends MoveToBlockGoal {

    private EntityGrizzlyBear bear;
    private int idleAtHiveTime = 0;
    private boolean isAboveDestinationBear;

    public GrizzlyBearAIBeehive(EntityGrizzlyBear bear) {
        super(bear, 1D, 32, 8);
        this.bear = bear;
    }

    public boolean shouldExecute() {
        return !bear.isChild() && super.shouldExecute();
    }

    public void resetTask() {
        idleAtHiveTime = 0;
    }

    public double getTargetDistanceSq() {
        return 2D;
    }

    public void tick() {
        super.tick();
        BlockPos blockpos = this.func_241846_j();
        if (!isWithinXZDist(blockpos, this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            this.isAboveDestinationBear = false;
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                this.creature.getNavigator().tryMoveToXYZ((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY(), (double) ((float) blockpos.getZ()) + 0.5D, this.movementSpeed);
            }
        } else {
            this.isAboveDestinationBear = true;
            --this.timeoutCounter;
        }

        if (this.getIsAboveDestination() && Math.abs(bear.getPosY() - destinationBlock.getY()) <= 3) {
            bear.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            if (bear.getPosY() + 2 < destinationBlock.getY()) {
                bear.setAnimation(EntityGrizzlyBear.ANIMATION_MAUL);
                bear.maxStandTime = 60;
                bear.setStanding(true);
            } else {
                if (bear.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    bear.setAnimation(bear.getRNG().nextBoolean() ? EntityGrizzlyBear.ANIMATION_SWIPE_L : EntityGrizzlyBear.ANIMATION_SWIPE_R);

                }
            }
            if (this.idleAtHiveTime >= 20) {
                this.eatHive();
            } else {
                ++this.idleAtHiveTime;
            }
        }

    }

    private boolean isWithinXZDist(BlockPos blockpos, Vector3d positionVec, double distance) {
        return blockpos.distanceSq(positionVec.getX(), blockpos.getY(), positionVec.getZ(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void eatHive() {
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(bear.world, bear)) {
            BlockState blockstate = bear.world.getBlockState(this.destinationBlock);
            if (BlockTags.getCollection().get(AMTagRegistry.GRIZZLY_BEEHIVE).contains(blockstate.getBlock())) {
                if (bear.world.getTileEntity(this.destinationBlock) instanceof BeehiveTileEntity) {
                    Random rand = new Random();
                    BeehiveTileEntity beehivetileentity = (BeehiveTileEntity) bear.world.getTileEntity(this.destinationBlock);
                    beehivetileentity.angerBees(null, blockstate, BeehiveTileEntity.State.EMERGENCY);
                    bear.world.updateComparatorOutputLevel(this.destinationBlock, blockstate.getBlock());
                    ItemStack stack = new ItemStack(Items.HONEYCOMB);
                    int level = 0;
                    if (blockstate.getBlock() instanceof BeehiveBlock) {
                        level = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                    }
                    for (int i = 0; i < level; i++) {
                        ItemEntity itementity = new ItemEntity(bear.world, destinationBlock.getX() + rand.nextFloat(), destinationBlock.getY() + rand.nextFloat(), destinationBlock.getZ() + rand.nextFloat(), stack);
                        itementity.setDefaultPickupDelay();
                        bear.world.addEntity(itementity);
                    }
                    bear.world.destroyBlock(destinationBlock, false);
                    if (blockstate.getBlock() instanceof BeehiveBlock) {
                        bear.world.setBlockState(destinationBlock, blockstate.with(BeehiveBlock.HONEY_LEVEL, 0));
                    }
                    double d0 = 15;
                    for (BeeEntity bee : bear.world.getEntitiesWithinAABB(BeeEntity.class, new AxisAlignedBB((double) destinationBlock.getX() - d0, (double) destinationBlock.getY() - d0, (double) destinationBlock.getZ() - d0, (double) destinationBlock.getX() + d0, (double) destinationBlock.getY() + d0, (double) destinationBlock.getZ() + d0))) {
                        bee.setAngerTime(100);
                        bee.setAttackTarget(bear);
                        bee.setStayOutOfHiveCountdown(400);
                    }
                    resetTask();
                }
            }
        }
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        if (BlockTags.getCollection().get(AMTagRegistry.GRIZZLY_BEEHIVE).contains(worldIn.getBlockState(pos).getBlock())) {
            if (worldIn.getTileEntity(pos) instanceof BeehiveTileEntity && worldIn.getBlockState(pos).getBlock() instanceof BeehiveBlock) {
                int i = worldIn.getBlockState(pos).get(BeehiveBlock.HONEY_LEVEL);
                return i > 0;
            }
        }
        return false;
    }
}
