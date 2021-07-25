package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class HummingbirdAIPollinate  extends MoveToBlockGoal {

    private EntityHummingbird bird;
    private int idleAtFlowerTime = 0;
    private boolean isAboveDestinationBear;

    public HummingbirdAIPollinate(EntityHummingbird bird) {
        super(bird, 1D, 32, 8);
        this.bird = bird;
    }

    public boolean shouldExecute() {
        return !bird.isChild() && bird.pollinateCooldown == 0 && super.shouldExecute();
    }

    public void resetTask() {
        idleAtFlowerTime = 0;
    }

    public double getTargetDistanceSq() {
        return 3D;
    }

    public void tick() {
        super.tick();
        BlockPos blockpos = this.func_241846_j();
        if (!isWithinXZDist(blockpos, this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            this.isAboveDestinationBear = false;
            ++this.timeoutCounter;
            double speedLoc = movementSpeed;
            if(this.creature.getDistanceSq(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D) >= 3){
                speedLoc = movementSpeed * 0.3D;
            }
            this.creature.getMoveHelper().setMoveTo((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY(), (double) ((float) blockpos.getZ()) + 0.5D, speedLoc);

        } else {
            this.isAboveDestinationBear = true;
            --this.timeoutCounter;
        }

        if (this.getIsAboveDestination() && Math.abs(bird.getPosY() - destinationBlock.getY()) <= 2) {
            bird.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            if (this.idleAtFlowerTime >= 20) {
                this.pollinate();
                this.resetTask();
            } else {
                ++this.idleAtFlowerTime;
            }
        }

    }

    private boolean isGrowable(BlockPos pos, ServerWorld world) {
        BlockState blockstate = world.getBlockState(pos);
        Block block = blockstate.getBlock();
        return block instanceof CropsBlock && !((CropsBlock)block).isMaxAge(blockstate);
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vector3d positionVec, double distance) {
        return blockpos.distanceSq(positionVec.getX(), positionVec.getY(), positionVec.getZ(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void pollinate() {
        bird.world.playEvent(2005, destinationBlock, 0);
        bird.setCropsPollinated(bird.getCropsPollinated() + 1);
        bird.pollinateCooldown = 200;
        if(bird.getCropsPollinated() > 3){
            if(isGrowable(destinationBlock, (ServerWorld) bird.world)){
                BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), bird.world, destinationBlock);
            }
            bird.setCropsPollinated(0);
        }
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock().isIn(BlockTags.BEE_GROWABLES) || worldIn.getBlockState(pos).getBlock().isIn(BlockTags.FLOWERS)) {
            return bird.pollinateCooldown == 0 && bird.canBlockBeSeen(pos);
        }
        return false;
    }
}
