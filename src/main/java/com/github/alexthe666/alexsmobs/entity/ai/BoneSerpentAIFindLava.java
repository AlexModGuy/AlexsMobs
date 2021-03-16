package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Random;

public class BoneSerpentAIFindLava extends Goal {
    private final EntityBoneSerpent creature;
    private BlockPos targetPos;

    public BoneSerpentAIFindLava(EntityBoneSerpent creature) {
        this.creature = creature;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean shouldExecute() {
        if ((this.creature.jumpCooldown == 0 || this.creature.isOnGround()) && !this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.WATER) && !this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.LAVA)){
            targetPos = generateTarget();
            return targetPos != null;
        }
        return false;
    }

    public void startExecuting() {
        if(targetPos != null){
            this.creature.getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.5D);
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath() && targetPos != null && !this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.WATER) && !this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.LAVA);
    }

    public BlockPos generateTarget() {
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 16;
        for(int i = 0; i < 15; i++){
            BlockPos blockpos1 = this.creature.getPosition().add(random.nextInt(range) - range/2, 3, random.nextInt(range) - range/2);
            while(this.creature.world.isAirBlock(blockpos1) && blockpos1.getY() > 1){
                blockpos1 = blockpos1.down();
            }
            if(this.creature.world.getFluidState(blockpos1).isTagged(FluidTags.WATER) || this.creature.world.getFluidState(blockpos1).isTagged(FluidTags.LAVA)){
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }
}
