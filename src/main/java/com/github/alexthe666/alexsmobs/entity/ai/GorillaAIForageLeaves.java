package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GorillaAIForageLeaves extends MoveToBlockGoal {

    private final EntityGorilla gorilla;
    private int idleAtLeavesTime = 0;
    private boolean isAboveDestinationBear;

    public GorillaAIForageLeaves(EntityGorilla gorilla) {
        super(gorilla, 1D, 32, 3);
        this.gorilla = gorilla;
    }

    public boolean canUse() {
        return !gorilla.isBaby() && gorilla.getMainHandItem().isEmpty() && super.canUse();
    }

    public void stop() {
        idleAtLeavesTime = 0;
    }

    public double acceptedDistance() {
        return 2D;
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

        if (this.isReachedTarget() && Math.abs(gorilla.getY() - blockPos.getY()) <= 3) {
            gorilla.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5));
            if (gorilla.getY() + 2 < blockPos.getY()) {
                gorilla.setAnimation(gorilla.getRandom().nextBoolean() ? EntityGorilla.ANIMATION_BREAKBLOCK_L : EntityGorilla.ANIMATION_BREAKBLOCK_R);
                gorilla.maxStandTime = 60;
                gorilla.setStanding(true);
            } else {
                if (gorilla.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    gorilla.setAnimation(gorilla.getRandom().nextBoolean() ? EntityGorilla.ANIMATION_BREAKBLOCK_L : EntityGorilla.ANIMATION_BREAKBLOCK_R);
                }
            }
            if (this.idleAtLeavesTime >= 20) {
                this.breakLeaves();
            } else {
                ++this.idleAtLeavesTime;
            }
        }

    }

    private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
        return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
    }

    protected boolean isReachedTarget() {
        return this.isAboveDestinationBear;
    }

    private void breakLeaves() {
        if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(gorilla.level, gorilla)) {
            BlockState blockstate = gorilla.level.getBlockState(this.blockPos);
            if (blockstate.is(AMTagRegistry.GORILLA_BREAKABLES)) {
                gorilla.level.destroyBlock(blockPos, false);
                final RandomSource rand = this.gorilla.getRandom();
                ItemStack stack = new ItemStack(blockstate.getBlock().asItem());
                ItemEntity itementity = new ItemEntity(gorilla.level, blockPos.getX() + rand.nextFloat(), blockPos.getY() + rand.nextFloat(), blockPos.getZ() + rand.nextFloat(), stack);
                itementity.setDefaultPickUpDelay();
                gorilla.level.addFreshEntity(itementity);
                if(blockstate.is(AMTagRegistry.DROPS_BANANAS) && rand.nextInt(30) == 0){
                    ItemStack banana = new ItemStack(AMItemRegistry.BANANA.get());
                    ItemEntity itementity2 = new ItemEntity(gorilla.level, blockPos.getX() + rand.nextFloat(), blockPos.getY() + rand.nextFloat(), blockPos.getZ() + rand.nextFloat(), banana);
                    itementity2.setDefaultPickUpDelay();
                    gorilla.level.addFreshEntity(itementity2);

                }
                stop();
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(AMTagRegistry.GORILLA_BREAKABLES);
    }
}
