package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigatorWide extends GroundPathNavigation {
    private float distancemodifier = 0.75F;

    public GroundPathNavigatorWide(Mob entitylivingIn, Level worldIn) {
        super(entitylivingIn, worldIn);
    }

    public GroundPathNavigatorWide(Mob entitylivingIn, Level worldIn, float distancemodifier) {
        super(entitylivingIn, worldIn);
        this.distancemodifier = distancemodifier;
    }

    protected void followThePath() {
        Vec3 vector3d = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() * distancemodifier;
        Vec3i vector3i = this.path.getNextNodePos();
        double d0 = Math.abs(this.mob.getX() - ((double)vector3i.getX() + 0.5D));
        double d1 = Math.abs(this.mob.getY() - (double)vector3i.getY());
        double d2 = Math.abs(this.mob.getZ() - ((double)vector3i.getZ() + 0.5D));
        boolean flag = d0 < (double)this.maxDistanceToWaypoint && d2 < (double)this.maxDistanceToWaypoint && d1 < 1.0D;
        if (flag || this.mob.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vector3d)) {
            this.path.advance();
        }

        this.doStuckDetection(vector3d);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 currentPosition) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        } else {
            Vec3 vector3d = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!currentPosition.closerThan(vector3d, 2.0D)) {
                return false;
            } else {
                Vec3 vector3d1 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 vector3d2 = vector3d1.subtract(vector3d);
                Vec3 vector3d3 = currentPosition.subtract(vector3d);
                return vector3d2.dot(vector3d3) > 0.0D;
            }
        }
    }

}
