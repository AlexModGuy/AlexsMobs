package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.*;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class SemiAquaticPathNavigator extends SwimmerPathNavigator {

    public SemiAquaticPathNavigator(MobEntity entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new WalkAndSwimNodeProcessor();
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }

    protected boolean canNavigate() {
        return true;
    }

    protected Vector3d getEntityPosition() {
        return new Vector3d(this.entity.getPosX(), this.entity.getPosYHeight(0.5D), this.entity.getPosZ());
    }

    protected boolean isDirectPathBetweenPoints(Vector3d posVec31, Vector3d posVec32, int sizeX, int sizeY, int sizeZ) {
        Vector3d vector3d = new Vector3d(posVec32.x, posVec32.y + (double)this.entity.getHeight() * 0.5D, posVec32.z);
        return this.world.rayTraceBlocks(new RayTraceContext(posVec31, vector3d, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.entity)).getType() == RayTraceResult.Type.MISS;
    }

    public boolean canEntityStandOnPos(BlockPos pos) {
        return  !this.world.getBlockState(pos.down()).isAir();
    }

    public void setCanSwim(boolean canSwim) {
    }
}
