package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class LaviathanAIRandomSwimming extends LavaAndWaterAIRandomSwimming {


    public LaviathanAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
        super(creature, speed, chance);
    }

    @Nullable
    protected Vec3 getPosition() {

        BlockPos pos = this.mob.blockPosition().offset(RandomPos.generateRandomDirection(this.mob.getRandom(), 16, 5));

        for (int i = 0; pos != null && this.mob.level.getBlockState(new BlockPos(pos)).getFluidState().isEmpty() && i++ < 10; pos = this.mob.blockPosition().offset(RandomPos.generateRandomDirection(this.mob.getRandom(), 16, 5))) {
        }
        if (this.mob.level.getBlockState(new BlockPos(pos)).getFluidState().isEmpty()) {
            return null;
        }
        if(mob.getRandom().nextInt(3) == 0){
            while(!this.mob.level.getBlockState(pos).getFluidState().isEmpty() && pos.getY() < 255){
                pos = pos.above();
            }
            pos = pos.below();
        }
        return new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
    }

}
