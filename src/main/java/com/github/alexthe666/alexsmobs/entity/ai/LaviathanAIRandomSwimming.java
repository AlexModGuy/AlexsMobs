package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class LaviathanAIRandomSwimming extends LavaAndWaterAIRandomSwimming {


    public LaviathanAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
        super(creature, speed, chance);
    }

    @Nullable
    protected Vec3 getPosition() {
        if(this.mob.getRandom().nextFloat() < (this.mob.isInLava() ? 0.7F : 0.3F)){
            Vec3 vector3d = findSurfaceTarget(this.mob, 32, 16);
            if(vector3d != null){
                return vector3d;
            }
        }
        BlockPos pos = this.mob.blockPosition().offset(RandomPos.generateRandomDirection(this.mob.getRandom(), 16, 5));

        for(int i = 0; pos != null && !this.mob.level.getBlockState(new BlockPos(pos)).isPathfindable(this.mob.level, new BlockPos(pos), PathComputationType.WATER) && i++ < 10; pos = this.mob.blockPosition().offset(RandomPos.generateRandomDirection(this.mob.getRandom(), 16, 5))) {
        }

        return new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
    }
}
