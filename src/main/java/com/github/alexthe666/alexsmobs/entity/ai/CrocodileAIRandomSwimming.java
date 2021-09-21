package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class CrocodileAIRandomSwimming extends RandomStrollGoal {
    public CrocodileAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
        super(creature, speed, chance, false);
    }

    public boolean canUse() {
        if (this.mob.isVehicle() || ((EntityCrocodile)mob).isSitting() || mob.getTarget() != null || !this.mob.isInWater() && this.mob instanceof ISemiAquatic && !((ISemiAquatic) this.mob).shouldEnterWater()) {
            return false;
        } else {
            if (!this.forceTrigger) {
                if (this.mob.getRandom().nextInt(this.interval) != 0) {
                    return false;
                }
            }
            Vec3 vector3d = this.getPosition();
            if (vector3d == null) {
                return false;
            } else {
                this.wantedX = vector3d.x;
                this.wantedY = vector3d.y;
                this.wantedZ = vector3d.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        if(this.mob.hasRestriction() && this.mob.distanceToSqr(Vec3.atCenterOf(this.mob.getRestrictCenter())) > this.mob.getRestrictRadius() * this.mob.getRestrictRadius()){
            return RandomPos.getPosTowards(this.mob, 7, 3, Vec3.atBottomCenterOf(this.mob.getRestrictCenter()));
        }
        if(this.mob.getRandom().nextFloat() < 0.3F){
            Vec3 vector3d = findSurfaceTarget(this.mob, 15, 7);
            if(vector3d != null){
                return vector3d;
            }
        }
        Vec3 vector3d = RandomPos.getPos(this.mob, 7, 3);

        for(int i = 0; vector3d != null && !this.mob.level.getBlockState(new BlockPos(vector3d)).isPathfindable(this.mob.level, new BlockPos(vector3d), PathComputationType.WATER) && i++ < 15; vector3d = RandomPos.getPos(this.mob, 10, 7)) {
        }

        return vector3d;
    }

    private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
        BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
        return this.mob.level.getFluidState(blockpos).is(FluidTags.WATER) && !this.mob.level.getBlockState(blockpos).getMaterial().blocksMotion();
    }

    private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
        return this.mob.level.getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir() && this.mob.level.getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
    }

    private Vec3 findSurfaceTarget(PathfinderMob creature, int i, int i1) {
        BlockPos upPos = creature.blockPosition();
        while(creature.level.getFluidState(upPos).is(FluidTags.WATER)){
            upPos = upPos.above();
        }
        if(isAirAbove(upPos.below(), 0, 0, 0) && canJumpTo(upPos.below(), 0, 0, 0)){
            return new Vec3(upPos.getX() + 0.5F, upPos.getY() - 1F, upPos.getZ() + 0.5F);
        }
        return null;
    }
}
