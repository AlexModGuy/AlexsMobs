package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

public class BoneSerpentPathNavigator extends PathNavigation {

    public BoneSerpentPathNavigator(Mob entitylivingIn, Level worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder createPathFinder(int p_179679_1_) {
        this.nodeEvaluator = new BoneSerpentNodeProcessor();
        return new PathFinder(this.nodeEvaluator, p_179679_1_);
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canUpdatePath() {
        return true;
    }

    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.mob.getY(0.5D), this.mob.getZ());
    }

    public void tick() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }

        if (!this.isDone()) {
            if (this.canUpdatePath()) {
                this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
                Vec3 vector3d = this.path.getNextEntityPos(this.mob);
                if (Mth.floor(this.mob.getX()) == Mth.floor(vector3d.x) && Mth.floor(this.mob.getY()) == Mth.floor(vector3d.y) && Mth.floor(this.mob.getZ()) == Mth.floor(vector3d.z)) {
                    this.path.advance();
                }
            }

            DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.isDone()) {
                Vec3 vector3d1 = this.path.getNextEntityPos(this.mob);
                this.mob.getMoveControl().setWantedPosition(vector3d1.x, vector3d1.y, vector3d1.z, this.speedModifier);
            }
        }
    }

    protected void followThePath() {
        if (this.path != null) {
            Vec3 vector3d = this.getTempMobPos();
            float f = this.mob.getBbWidth();
            float f1 = 3;
            Vec3 vector3d1 = this.mob.getDeltaMovement();
            if (Math.abs(vector3d1.x) > 0.2D || Math.abs(vector3d1.z) > 0.2D) {
                f1 = (float)((double)f1 * vector3d1.length() * 6.0D);
            }

            int i = 6;
            Vec3 vector3d2 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (Math.abs(this.mob.getX() - vector3d2.x) < (double)f1 && Math.abs(this.mob.getZ() - vector3d2.z) < (double)f1 && Math.abs(this.mob.getY() - vector3d2.y) < (double)(f1 * 2.0F)) {
                this.path.advance();
            }

            for(int j = Math.min(this.path.getNextNodeIndex() + 6, this.path.getNodeCount() - 1); j > this.path.getNextNodeIndex(); --j) {
                vector3d2 = this.path.getEntityPosAtNode(this.mob, j);
                if (!(vector3d2.distanceToSqr(vector3d) > 36.0D) && this.canMoveDirectly(vector3d, vector3d2, 0, 0, 0)) {
                    this.path.setNextNodeIndex(j);
                    break;
                }
            }

            this.doStuckDetection(vector3d);
        }
    }

    protected void doStuckDetection(Vec3 positionVec3) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (positionVec3.distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
                this.stop();
            }

            this.lastStuckCheck = this.tick;
            this.lastStuckCheckPos = positionVec3;
        }

        if (this.path != null && !this.path.isDone()) {
            Vec3i vector3i = this.path.getNextNodePos();
            if (vector3i.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = vector3i;
                double d0 = positionVec3.distanceTo(Vec3.atCenterOf(this.timeoutCachedNode));
                this.timeoutLimit = this.mob.getSpeed() > 0.0F ? d0 / (double)this.mob.getSpeed() * 100.0D : 0.0D;
            }

            if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 2.0D) {
                this.timeoutCachedNode = Vec3i.ZERO;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0D;
                this.stop();
            }

            this.lastTimeoutCheck = Util.getMillis();
        }

    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
        Vec3 vector3d = new Vec3(posVec32.x, posVec32.y + (double)this.mob.getBbHeight() * 0.5D, posVec32.z);
        return this.level.clip(new ClipContext(posVec31, vector3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
    }

    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos).isSolidRender(this.level, pos);
    }

    public void setCanFloat(boolean canSwim) {
    }
}
