package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SmartClimbPathNavigator extends GroundPathNavigation {

    @Nullable
    private BlockPos pathToPosition;

    public SmartClimbPathNavigator(Mob entitylivingIn, Level worldIn) {
        super(entitylivingIn, worldIn);
    }

    public Path createPath(BlockPos p_26589_, int p_26590_) {
        this.pathToPosition = p_26589_;
        return super.createPath(p_26589_, p_26590_);
    }

    public Path createPath(Entity p_26586_, int p_26587_) {
        this.pathToPosition = p_26586_.blockPosition();
        return super.createPath(p_26586_, p_26587_);
    }

    public boolean moveTo(Entity p_26583_, double p_26584_) {
        Path path = this.createPath((Entity)p_26583_, 0);
        if (path != null) {
            return this.moveTo(path, p_26584_);
        } else {
            this.pathToPosition = p_26583_.blockPosition();
            this.speedModifier = p_26584_;
            return true;
        }
    }


    public void tick() {
        super.tick();
        if (!this.isDone()) {
            super.tick();
        } else if (this.pathToPosition != null) {
            Vec3 xzOff = new Vec3(this.pathToPosition.getX() + 0.5F - this.mob.getX(), 0, this.pathToPosition.getZ() + 0.5F - this.mob.getZ());
            double dist = xzOff.length();
            if (dist < this.mob.getBbWidth() || this.mob.getY() > (double)this.pathToPosition.getY()) {
                this.pathToPosition = null;
            } else {
                this.mob.getMoveControl().setWantedPosition((double)this.pathToPosition.getX(), (double)this.mob.getY(), (double)this.pathToPosition.getZ(), this.speedModifier);
            }
        }
    }

    @Override
    protected void doStuckDetection(Vec3 vec) {
        if (this.tick - this.lastStuckCheck > 40) {
            if (vec.distanceToSqr(new Vec3(this.lastStuckCheckPos.x, vec.y, this.lastStuckCheckPos.z)) < 2.25D) {
                this.stop();
            }
            this.lastStuckCheck = this.tick;
            this.lastStuckCheckPos = vec;
        }
    }
}
