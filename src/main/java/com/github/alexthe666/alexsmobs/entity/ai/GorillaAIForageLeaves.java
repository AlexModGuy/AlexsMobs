package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

import java.util.Random;

public class GorillaAIForageLeaves extends MoveToBlockGoal {

    private EntityGorilla gorilla;
    private int idleAtLeavesTime = 0;
    private boolean isAboveDestinationBear;

    public GorillaAIForageLeaves(EntityGorilla gorilla) {
        super(gorilla, 1D, 32, 3);
        this.gorilla = gorilla;
    }

    public boolean shouldExecute() {
        return !gorilla.isChild() && gorilla.getHeldItemMainhand().isEmpty() && super.shouldExecute();
    }

    public void resetTask() {
        idleAtLeavesTime = 0;
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

        if (this.getIsAboveDestination() && Math.abs(gorilla.getPosY() - destinationBlock.getY()) <= 3) {
            gorilla.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            if (gorilla.getPosY() + 2 < destinationBlock.getY()) {
                gorilla.setAnimation(gorilla.getRNG().nextBoolean() ? EntityGorilla.ANIMATION_BREAKBLOCK_L : EntityGorilla.ANIMATION_BREAKBLOCK_R);
                gorilla.maxStandTime = 60;
                gorilla.setStanding(true);
            } else {
                if (gorilla.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    gorilla.setAnimation(gorilla.getRNG().nextBoolean() ? EntityGorilla.ANIMATION_BREAKBLOCK_L : EntityGorilla.ANIMATION_BREAKBLOCK_R);
                }
            }
            if (this.idleAtLeavesTime >= 20) {
                this.breakLeaves();
            } else {
                ++this.idleAtLeavesTime;
            }
        }

    }

    private boolean isWithinXZDist(BlockPos blockpos, Vector3d positionVec, double distance) {
        return blockpos.distanceSq(positionVec.getX(), blockpos.getY(), positionVec.getZ(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void breakLeaves() {
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(gorilla.world, gorilla)) {
            BlockState blockstate = gorilla.world.getBlockState(this.destinationBlock);
            if (BlockTags.getCollection().get(AMTagRegistry.GORILLA_BREAKABLES).contains(blockstate.getBlock())) {
                gorilla.world.destroyBlock(destinationBlock, false);
                Random rand = new Random();
                ItemStack stack = new ItemStack(blockstate.getBlock().asItem());
                ItemEntity itementity = new ItemEntity(gorilla.world, destinationBlock.getX() + rand.nextFloat(), destinationBlock.getY() + rand.nextFloat(), destinationBlock.getZ() + rand.nextFloat(), stack);
                itementity.setDefaultPickupDelay();
                gorilla.world.addEntity(itementity);
                if(BlockTags.getCollection().get(AMTagRegistry.DROPS_BANANAS).contains(blockstate.getBlock()) && rand.nextInt(30) == 0){
                    ItemStack banana = new ItemStack(AMItemRegistry.BANANA);
                    ItemEntity itementity2 = new ItemEntity(gorilla.world, destinationBlock.getX() + rand.nextFloat(), destinationBlock.getY() + rand.nextFloat(), destinationBlock.getZ() + rand.nextFloat(), banana);
                    itementity2.setDefaultPickupDelay();
                    gorilla.world.addEntity(itementity2);

                }
                resetTask();
            }
        }
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return BlockTags.getCollection().get(AMTagRegistry.GORILLA_BREAKABLES).contains(worldIn.getBlockState(pos).getBlock());
    }
}
