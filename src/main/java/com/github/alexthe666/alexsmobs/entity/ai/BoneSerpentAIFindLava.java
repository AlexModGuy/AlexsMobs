package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;

import java.util.EnumSet;
import java.util.Random;

public class BoneSerpentAIFindLava extends Goal {
    private final EntityBoneSerpent creature;
    private BlockPos targetPos;

    public BoneSerpentAIFindLava(EntityBoneSerpent creature) {
        this.creature = creature;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if ((this.creature.jumpCooldown == 0 || this.creature.isOnGround()) && !this.creature.level.getFluidState(this.creature.blockPosition()).is(FluidTags.WATER) && !this.creature.level.getFluidState(this.creature.blockPosition()).is(FluidTags.LAVA)){
            targetPos = generateTarget();
            return targetPos != null;
        }
        return false;
    }

    public void start() {
        if(targetPos != null){
            this.creature.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.5D);
        }
    }

    public boolean canContinueToUse() {
        return !this.creature.getNavigation().isDone() && targetPos != null && !this.creature.level.getFluidState(this.creature.blockPosition()).is(FluidTags.WATER) && !this.creature.level.getFluidState(this.creature.blockPosition()).is(FluidTags.LAVA);
    }

    public BlockPos generateTarget() {
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 16;
        for(int i = 0; i < 15; i++){
            BlockPos blockpos1 = this.creature.blockPosition().offset(random.nextInt(range) - range/2, 3, random.nextInt(range) - range/2);
            while(this.creature.level.isEmptyBlock(blockpos1) && blockpos1.getY() > 1){
                blockpos1 = blockpos1.below();
            }
            if(this.creature.level.getFluidState(blockpos1).is(FluidTags.WATER) || this.creature.level.getFluidState(blockpos1).is(FluidTags.LAVA)){
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }
}
