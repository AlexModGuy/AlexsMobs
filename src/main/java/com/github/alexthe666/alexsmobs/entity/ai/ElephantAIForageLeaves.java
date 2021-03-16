package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

import java.util.Random;

public class ElephantAIForageLeaves extends MoveToBlockGoal {

    private EntityElephant elephant;
    private int idleAtLeavesTime = 0;
    private boolean isAboveDestinationBear;

    public ElephantAIForageLeaves(EntityElephant elephant) {
        super(elephant, 0.7D, 32, 5);
        this.elephant = elephant;
    }

    public boolean shouldExecute() {
        return !elephant.isChild() && elephant.getControllingPassenger() == null && elephant.getHeldItemMainhand().isEmpty() && !elephant.aiItemFlag && super.shouldExecute();
    }

    public void resetTask() {
        idleAtLeavesTime = 0;
    }

    public double getTargetDistanceSq() {
        return 4D;
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

        if (this.getIsAboveDestination() && Math.abs(elephant.getPosY() - destinationBlock.getY()) <= 3) {
            elephant.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            if (elephant.getPosY() + 2 < destinationBlock.getY()) {
                if (elephant.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    elephant.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
                }
                elephant.setStanding(true);
                elephant.maxStandTime = 15;
            } else {
                elephant.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
                elephant.setStanding(false);
            }
            if (this.idleAtLeavesTime >= 10) {
                this.breakLeaves();
            } else {
                ++this.idleAtLeavesTime;
            }
        }

    }

    protected int getRunDelay(CreatureEntity p_203109_1_) {
        return 100 + p_203109_1_.getRNG().nextInt(200);
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vector3d positionVec, double distance) {
        return blockpos.distanceSq(positionVec.getX(), blockpos.getY(), positionVec.getZ(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void breakLeaves() {
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(elephant.world, elephant)) {
            BlockState blockstate = elephant.world.getBlockState(this.destinationBlock);
            if (BlockTags.getCollection().get(AMTagRegistry.ELEPHANT_FOODBLOCKS).contains(blockstate.getBlock())) {
                elephant.world.destroyBlock(destinationBlock, false);
                Random rand = new Random();
                ItemStack stack = new ItemStack(blockstate.getBlock().asItem());
                ItemEntity itementity = new ItemEntity(elephant.world, destinationBlock.getX() + rand.nextFloat(), destinationBlock.getY() + rand.nextFloat(), destinationBlock.getZ() + rand.nextFloat(), stack);
                itementity.setDefaultPickupDelay();
                elephant.world.addEntity(itementity);
                if(BlockTags.getCollection().get(AMTagRegistry.DROPS_ACACIA_BLOSSOMS).contains(blockstate.getBlock()) && rand.nextInt(30) == 0){
                    ItemStack banana = new ItemStack(AMItemRegistry.ACACIA_BLOSSOM);
                    ItemEntity itementity2 = new ItemEntity(elephant.world, destinationBlock.getX() + rand.nextFloat(), destinationBlock.getY() + rand.nextFloat(), destinationBlock.getZ() + rand.nextFloat(), banana);
                    itementity2.setDefaultPickupDelay();
                    elephant.world.addEntity(itementity2);
                }
                resetTask();
            }
        }
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return !elephant.aiItemFlag && BlockTags.getCollection().get(AMTagRegistry.ELEPHANT_FOODBLOCKS).contains(worldIn.getBlockState(pos).getBlock());
    }
}
