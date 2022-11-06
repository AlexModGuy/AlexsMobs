package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ElephantAIForageLeaves extends MoveToBlockGoal {

    private EntityElephant elephant;
    private int idleAtLeavesTime = 0;
    private boolean isAboveDestinationBear;

    public ElephantAIForageLeaves(EntityElephant elephant) {
        super(elephant, 0.7D, 32, 5);
        this.elephant = elephant;
    }

    public boolean canUse() {
        return !elephant.isBaby() && elephant.getControllingPassenger() == null && elephant.getControllingVillager() == null && elephant.getMainHandItem().isEmpty() && !elephant.aiItemFlag && super.canUse();
    }

    public void stop() {
        idleAtLeavesTime = 0;
    }

    public double acceptedDistance() {
        return 4D;
    }

    public void tick() {
        super.tick();
        BlockPos blockpos = this.getMoveToTarget();
        if (!isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
            this.isAboveDestinationBear = false;
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.mob.getNavigation().moveTo((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY(), (double) ((float) blockpos.getZ()) + 0.5D, this.speedModifier);
            }
        } else {
            this.isAboveDestinationBear = true;
            --this.tryTicks;
        }

        if (this.isReachedTarget() && Math.abs(elephant.getY() - blockPos.getY()) <= 3) {
            elephant.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5));
            if (elephant.getY() + 2 < blockPos.getY()) {
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

    protected int nextStartTick(PathfinderMob p_203109_1_) {
        return 100 + p_203109_1_.getRandom().nextInt(200);
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
        return blockpos.distSqr(new BlockPos(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
    }

    protected boolean isReachedTarget() {
        return this.isAboveDestinationBear;
    }

    private void breakLeaves() {
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(elephant.level, elephant)) {
            BlockState blockstate = elephant.level.getBlockState(this.blockPos);
            if (blockstate.is(AMTagRegistry.ELEPHANT_FOODBLOCKS)) {
                elephant.level.destroyBlock(blockPos, false);
                final RandomSource rand = this.elephant.getRandom();
                ItemStack stack = new ItemStack(blockstate.getBlock().asItem());
                ItemEntity itementity = new ItemEntity(elephant.level, blockPos.getX() + rand.nextFloat(), blockPos.getY() + rand.nextFloat(), blockPos.getZ() + rand.nextFloat(), stack);
                itementity.setDefaultPickUpDelay();
                elephant.level.addFreshEntity(itementity);
                if(blockstate.is(AMTagRegistry.DROPS_ACACIA_BLOSSOMS) && rand.nextInt(30) == 0){
                    ItemStack banana = new ItemStack(AMItemRegistry.ACACIA_BLOSSOM.get());
                    ItemEntity itementity2 = new ItemEntity(elephant.level, blockPos.getX() + rand.nextFloat(), blockPos.getY() + rand.nextFloat(), blockPos.getZ() + rand.nextFloat(), banana);
                    itementity2.setDefaultPickUpDelay();
                    elephant.level.addFreshEntity(itementity2);
                }
                stop();
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return !elephant.aiItemFlag && worldIn.getBlockState(pos).is(AMTagRegistry.ELEPHANT_FOODBLOCKS);
    }
}
