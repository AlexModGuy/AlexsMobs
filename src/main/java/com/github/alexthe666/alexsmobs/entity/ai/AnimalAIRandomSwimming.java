package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class AnimalAIRandomSwimming extends RandomWalkingGoal {
    private int xzSpread;
    private boolean submerged;

    public AnimalAIRandomSwimming(AnimalEntity creature, double speed, int chance, int xzSpread) {
        super(creature, speed, chance, false);
        this.xzSpread = xzSpread;
        this.submerged = false;
    }

    public AnimalAIRandomSwimming(AnimalEntity creature, double speed, int chance, int xzSpread, boolean submerged) {
        super(creature, speed, chance, false);
        this.xzSpread = xzSpread;
        this.submerged = submerged;
    }

    public boolean shouldExecute() {
        if (this.creature.isBeingRidden()|| creature.getAttackTarget() != null || !this.creature.isInWater() && !this.creature.isInLava()) {
            return false;
        } else {
            if (!this.mustUpdate) {
                if (this.creature.getRNG().nextInt(this.executionChance) != 0) {
                    return false;
                }
            }
            Vector3d vector3d = this.getPosition();
            if (vector3d == null) {
                return false;
            } else {
                this.x = vector3d.x;
                this.y = vector3d.y;
                this.z = vector3d.z;
                this.mustUpdate = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vector3d getPosition() {
        if(this.creature.detachHome() && this.creature.getDistanceSq(Vector3d.copyCentered(this.creature.getHomePosition())) > this.creature.getMaximumHomeDistance() * this.creature.getMaximumHomeDistance()){
            return RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, xzSpread, 3, Vector3d.copyCenteredHorizontally(this.creature.getHomePosition()));
        }
        if(this.creature.getRNG().nextFloat() < 0.3F){
            Vector3d vector3d = findSurfaceTarget(this.creature, xzSpread, 7);
            if(vector3d != null){
                return vector3d;
            }
        }
        Vector3d vector3d = RandomPositionGenerator.findRandomTarget(this.creature, xzSpread, 3);

        for(int i = 0; vector3d != null && !this.creature.world.getBlockState(new BlockPos(vector3d)).allowsMovement(this.creature.world, new BlockPos(vector3d), PathType.WATER) && i++ < 15; vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7)) {
        }
        if(submerged && vector3d != null){
            if(!this.creature.world.getFluidState(new BlockPos(vector3d).up()).isTagged(FluidTags.WATER)){
                vector3d = vector3d.add(0, -2, 0);
            }else if(!this.creature.world.getFluidState(new BlockPos(vector3d).up(2)).isTagged(FluidTags.WATER)){
                vector3d = vector3d.add(0, -3, 0);
            }
        }
        return vector3d;
    }

    private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
        BlockPos blockpos = pos.add(dx * scale, 0, dz * scale);
        return this.creature.world.getFluidState(blockpos).isTagged(FluidTags.LAVA) || this.creature.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.creature.world.getBlockState(blockpos).getMaterial().blocksMovement();
    }

    private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
        return this.creature.world.getBlockState(pos.add(dx * scale, 1, dz * scale)).isAir() && this.creature.world.getBlockState(pos.add(dx * scale, 2, dz * scale)).isAir();
    }

    private Vector3d findSurfaceTarget(CreatureEntity creature, int i, int i1) {
        BlockPos upPos = creature.getPosition();
        while(creature.world.getFluidState(upPos).isTagged(FluidTags.WATER) || creature.world.getFluidState(upPos).isTagged(FluidTags.LAVA)){
            upPos = upPos.up();
        }
        if(isAirAbove(upPos.down(), 0, 0, 0) && canJumpTo(upPos.down(), 0, 0, 0)){
            return new Vector3d(upPos.getX() + 0.5F, upPos.getY() - 1F, upPos.getZ() + 0.5F);
        }
        return null;
    }
}
