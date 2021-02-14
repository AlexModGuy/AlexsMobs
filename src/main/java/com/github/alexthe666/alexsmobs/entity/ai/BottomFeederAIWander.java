package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class BottomFeederAIWander extends RandomWalkingGoal {
    private int waterChance = 0;
    private int landChance = 0;
    private int range = 5;

    public BottomFeederAIWander(CreatureEntity creature, double speed, int waterChance, int landChance) {
        super(creature, speed, waterChance);
        this.waterChance = waterChance;
        this.landChance = landChance;
    }

    public BottomFeederAIWander(CreatureEntity creature, double speed, int waterChance, int landChance, int range) {
        super(creature, speed, waterChance);
        this.waterChance = waterChance;
        this.landChance = landChance;
        this.range = range;
    }

    public boolean shouldExecute(){
        if(creature instanceof ISemiAquatic && ((ISemiAquatic) creature).shouldStopMoving()){
            return false;
        }
        executionChance = creature.isInWater() ? waterChance : landChance;
        return super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        if(creature instanceof ISemiAquatic && ((ISemiAquatic) creature).shouldStopMoving()){
            return false;
        }
        return super.shouldContinueExecuting();
    }

    @Nullable
    protected Vector3d getPosition() {
        if(this.creature.isInWater()) {
            BlockPos blockpos = null;
            Random random = new Random();
            for (int i = 0; i < 15; i++) {
                BlockPos blockpos1 = this.creature.getPosition().add(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                while ((this.creature.world.isAirBlock(blockpos1) || this.creature.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)) && blockpos1.getY() > 1) {
                    blockpos1 = blockpos1.down();
                }
                if (isBottomOfSeafloor(this.creature.world, blockpos1.up())) {
                    blockpos = blockpos1;
                }
            }

            return blockpos != null ? new Vector3d(blockpos.getX() + 0.5F, blockpos.getY() + 0.5F, blockpos.getZ() + 0.5F) : null;
        }else{
            return super.getPosition();

        }
    }

    private boolean isBottomOfSeafloor(IWorld world, BlockPos pos){
        return world.getFluidState(pos).isTagged(FluidTags.WATER) && world.getFluidState(pos.down()).isEmpty() && world.getBlockState(pos.down()).isSolid();
    }
}
