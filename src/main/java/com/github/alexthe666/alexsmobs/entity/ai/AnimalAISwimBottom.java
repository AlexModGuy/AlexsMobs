package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class AnimalAISwimBottom extends RandomWalkingGoal {
    public AnimalAISwimBottom(CreatureEntity p_i48937_1_, double p_i48937_2_, int p_i48937_4_) {
        super(p_i48937_1_, p_i48937_2_, p_i48937_4_);
    }

    @Nullable
    protected Vector3d getPosition() {
        Vector3d vec = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7);

        for(int var2 = 0; vec != null && !this.creature.world.getBlockState(new BlockPos(vec)).allowsMovement(this.creature.world, new BlockPos(vec), PathType.WATER) && var2++ < 10; vec = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7)) {
        }
        int yDrop = 1 + this.creature.getRNG().nextInt(3);
        if(vec != null){
            BlockPos pos = new BlockPos(vec);
            while(this.creature.world.getFluidState(pos).isTagged(FluidTags.WATER) && this.creature.world.getBlockState(pos).allowsMovement(this.creature.world, new BlockPos(vec), PathType.WATER) && pos.getY() > 1){
                pos = pos.down();
            }
            pos = pos.up();
            int yUp = 0;
            while(this.creature.world.getFluidState(pos).isTagged(FluidTags.WATER) && this.creature.world.getBlockState(pos).allowsMovement(this.creature.world, new BlockPos(vec), PathType.WATER) && yUp < yDrop){
                pos = pos.up();
                yUp++;
            }
            return Vector3d.copyCentered(pos);
        }

        return vec;
    }
}
