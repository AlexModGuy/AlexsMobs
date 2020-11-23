package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.Random;

public class AnimalAILeaveWater extends Goal {
    private final AnimalEntity creature;
    private BlockPos targetPos;

    public AnimalAILeaveWater(AnimalEntity creature) {
        this.creature = creature;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean shouldExecute() {
        if (this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.WATER)){
            if(this.creature instanceof ISemiAquatic && ((ISemiAquatic) this.creature).shouldLeaveWater()){
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
        if(this.creature.collidedHorizontally && this.creature.isInWater()){
            float f1 = creature.rotationYaw * ((float)Math.PI / 180F);
            creature.setMotion(creature.getMotion().add((double)(-MathHelper.sin(f1) * 0.2F), 0.1D, (double)(MathHelper.cos(f1) * 0.2F)));

        }
    }

    public boolean shouldContinueExecuting() {
        if(this.creature instanceof ISemiAquatic && !((ISemiAquatic) this.creature).shouldLeaveWater()){
            this.creature.getNavigator().clearPath();
            return false;
        }
        return !this.creature.getNavigator().noPath() && targetPos != null && !this.creature.world.getFluidState(targetPos).isTagged(FluidTags.WATER);
    }

    public BlockPos generateTarget() {
        Vector3d vector3d = RandomPositionGenerator.getLandPos(this.creature, 23, 7);
        int tries = 0;
        while(vector3d != null && tries < 8){
            boolean waterDetected = false;
            for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(MathHelper.floor(vector3d.x - 2.0D), MathHelper.floor(vector3d.y - 1.0D), MathHelper.floor(vector3d.z - 2.0D), MathHelper.floor(vector3d.x + 2.0D), MathHelper.floor(vector3d.y), MathHelper.floor(vector3d.z + 2.0D))) {
                if (this.creature.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)) {
                    waterDetected = true;
                    break;
                }
            }
            if(waterDetected){
                vector3d = RandomPositionGenerator.getLandPos(this.creature, 23, 7);
            }else{
                return new BlockPos(vector3d);
            }
            tries++;
        }
        return null;
    }
}
