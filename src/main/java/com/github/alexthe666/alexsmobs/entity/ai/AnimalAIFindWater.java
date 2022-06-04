package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class AnimalAIFindWater extends Goal {
    private final PathfinderMob creature;
    private BlockPos targetPos;
    private final int executionChance = 30;

    public AnimalAIFindWater(PathfinderMob creature) {
        this.creature = creature;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.creature.isOnGround() && !this.creature.level.getFluidState(this.creature.blockPosition()).is(FluidTags.WATER)) {
            if (this.creature instanceof ISemiAquatic && ((ISemiAquatic) this.creature).shouldEnterWater() && (this.creature.getTarget() != null || this.creature.getRandom().nextInt(executionChance) == 0)) {
                targetPos = generateTarget();
                return targetPos != null;
            }
        }
        return false;
    }

    public void start() {
        if (targetPos != null) {
            this.creature.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1D);
        }
    }

    public void tick() {
        if (targetPos != null) {
            this.creature.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1D);
        }
    }

    public boolean canContinueToUse() {
        if (this.creature instanceof ISemiAquatic && !((ISemiAquatic) this.creature).shouldEnterWater()) {
            this.creature.getNavigation().stop();
            return false;
        }
        return !this.creature.getNavigation().isDone() && targetPos != null && !this.creature.level.getFluidState(this.creature.blockPosition()).is(FluidTags.WATER);
    }

    public BlockPos generateTarget() {
        BlockPos blockpos = null;
        final Random random = new Random();
        final int range = this.creature instanceof ISemiAquatic ? ((ISemiAquatic) this.creature).getWaterSearchRange() : 14;
        for(int i = 0; i < 15; i++) {
            BlockPos blockPos = this.creature.blockPosition().offset(random.nextInt(range) - range/2, 3, random.nextInt(range) - range/2);
            while (this.creature.level.isEmptyBlock(blockPos) && blockPos.getY() > 1) {
                blockPos = blockPos.below();
            }

            if (this.creature.level.getFluidState(blockPos).is(FluidTags.WATER)) {
                blockpos = blockPos;
            }
        }
        return blockpos;
    }
}
