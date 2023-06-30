package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HummingbirdAIPollinate  extends MoveToBlockGoal {

    private final EntityHummingbird bird;
    private int idleAtFlowerTime = 0;
    private boolean isAboveDestinationBear;

    public HummingbirdAIPollinate(EntityHummingbird bird) {
        super(bird, 1D, 32, 8);
        this.bird = bird;
    }

    public boolean canUse() {
        return !bird.isBaby() && bird.pollinateCooldown == 0 && super.canUse();
    }

    public void stop() {
        idleAtFlowerTime = 0;
    }

    public double acceptedDistance() {
        return 3D;
    }

    public void tick() {
        super.tick();
        BlockPos blockpos = this.getMoveToTarget();
        if (!isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
            this.isAboveDestinationBear = false;
            ++this.tryTicks;
            double speedLoc = speedModifier;
            if(this.mob.distanceToSqr(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D) >= 3){
                speedLoc = speedModifier * 0.3D;
            }
            this.mob.getMoveControl().setWantedPosition((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY(), (double) ((float) blockpos.getZ()) + 0.5D, speedLoc);

        } else {
            this.isAboveDestinationBear = true;
            --this.tryTicks;
        }

        if (this.isReachedTarget() && Math.abs(bird.getY() - blockPos.getY()) <= 2) {
            bird.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5));
            if (this.idleAtFlowerTime >= 20) {
                this.pollinate();
                this.stop();
            } else {
                ++this.idleAtFlowerTime;
            }
        }

    }

    private boolean isGrowable(BlockPos pos, ServerLevel world) {
        BlockState blockstate = world.getBlockState(pos);
        Block block = blockstate.getBlock();
        return block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockstate);
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
        return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
    }

    protected boolean isReachedTarget() {
        return this.isAboveDestinationBear;
    }

    private void pollinate() {
        bird.level().levelEvent(2005, blockPos, 0);
        bird.setCropsPollinated(bird.getCropsPollinated() + 1);
        bird.pollinateCooldown = 200;
        if(bird.getCropsPollinated() > 3){
            if(isGrowable(blockPos, (ServerLevel) bird.level())){
                BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), bird.level(), blockPos);
            }
            bird.setCropsPollinated(0);
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).is(BlockTags.BEE_GROWABLES) || worldIn.getBlockState(pos).is(BlockTags.FLOWERS)) {
            return bird.pollinateCooldown == 0 && bird.canBlockBeSeen(pos);
        }
        return false;
    }
}
