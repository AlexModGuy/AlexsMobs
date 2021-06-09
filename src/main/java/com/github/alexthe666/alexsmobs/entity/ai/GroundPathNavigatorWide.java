package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class GroundPathNavigatorWide extends GroundPathNavigator {
    private float distancemodifier = 0.75F;

    public GroundPathNavigatorWide(MobEntity entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    public GroundPathNavigatorWide(MobEntity entitylivingIn, World worldIn, float distancemodifier) {
        super(entitylivingIn, worldIn);
        this.distancemodifier = distancemodifier;
    }

    protected void pathFollow() {
        Vector3d vector3d = this.getEntityPosition();
        this.maxDistanceToWaypoint = this.entity.getWidth() * distancemodifier;
        Vector3i vector3i = this.currentPath.func_242948_g();
        double d0 = Math.abs(this.entity.getPosX() - ((double)vector3i.getX() + 0.5D));
        double d1 = Math.abs(this.entity.getPosY() - (double)vector3i.getY());
        double d2 = Math.abs(this.entity.getPosZ() - ((double)vector3i.getZ() + 0.5D));
        boolean flag = d0 < (double)this.maxDistanceToWaypoint && d2 < (double)this.maxDistanceToWaypoint && d1 < 1.0D;
        if (flag || this.entity.func_233660_b_(this.currentPath.getCurrentPoint().nodeType) && this.func_234112_b_(vector3d)) {
            this.currentPath.incrementPathIndex();
        }

        this.checkForStuck(vector3d);
    }

    private boolean func_234112_b_(Vector3d currentPosition) {
        if (this.currentPath.getCurrentPathIndex() + 1 >= this.currentPath.getCurrentPathLength()) {
            return false;
        } else {
            Vector3d vector3d = Vector3d.copyCenteredHorizontally(this.currentPath.func_242948_g());
            if (!currentPosition.isWithinDistanceOf(vector3d, 2.0D)) {
                return false;
            } else {
                Vector3d vector3d1 = Vector3d.copyCenteredHorizontally(this.currentPath.func_242947_d(this.currentPath.getCurrentPathIndex() + 1));
                Vector3d vector3d2 = vector3d1.subtract(vector3d);
                Vector3d vector3d3 = currentPosition.subtract(vector3d);
                return vector3d2.dotProduct(vector3d3) > 0.0D;
            }
        }
    }

}
