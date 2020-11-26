package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Random;

public class AnimalAIFindWater extends Goal {
    private final CreatureEntity creature;
    private BlockPos targetPos;

    public AnimalAIFindWater(CreatureEntity creature) {
        this.creature = creature;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean shouldExecute() {
        if (this.creature.isOnGround() && !this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.WATER)){
            if(this.creature instanceof ISemiAquatic && ((ISemiAquatic) this.creature).shouldEnterWater()){
                targetPos = generateTarget();
                return targetPos != null;
            }
        }
        return false;
    }

    public void startExecuting() {
        if(targetPos != null){
            this.creature.getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1D);
        }
    }

    public void tick() {
        if(targetPos != null){
            this.creature.getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1D);
        }
    }

    public boolean shouldContinueExecuting() {
        if(this.creature instanceof ISemiAquatic && !((ISemiAquatic) this.creature).shouldEnterWater()){
            this.creature.getNavigator().clearPath();
            return false;
        }
        return !this.creature.getNavigator().noPath() && targetPos != null && !this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.WATER);
    }

    public BlockPos generateTarget() {
        BlockPos blockpos = null;
        Random random = new Random();
        int range = this.creature instanceof ISemiAquatic ? ((ISemiAquatic) this.creature).getWaterSearchRange() : 14;
        for(int i = 0; i < 15; i++){
            BlockPos blockpos1 = this.creature.getPosition().add(random.nextInt(range) - range/2, 3, random.nextInt(range) - range/2);
            while(this.creature.world.isAirBlock(blockpos1) && blockpos1.getY() > 1){
                blockpos1 = blockpos1.down();
            }
            if(this.creature.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)){
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }
}
