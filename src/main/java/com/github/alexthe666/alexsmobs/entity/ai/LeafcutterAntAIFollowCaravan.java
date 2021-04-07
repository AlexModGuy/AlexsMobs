package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.List;

public class LeafcutterAntAIFollowCaravan extends Goal {
    public final EntityLeafcutterAnt LeafcutterAnt;
    private double speedModifier;
    private int distCheckCounter;

    public LeafcutterAntAIFollowCaravan(EntityLeafcutterAnt llamaIn, double speedModifierIn) {
        this.LeafcutterAnt = llamaIn;
        this.speedModifier = speedModifierIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.LeafcutterAnt.shouldLeadCaravan() && !LeafcutterAnt.isChild() && !this.LeafcutterAnt.isQueen() && !this.LeafcutterAnt.inCaravan() && !this.LeafcutterAnt.hasLeaf()) {
            double dist = 15D;
            List<EntityLeafcutterAnt> list = LeafcutterAnt.world.getEntitiesWithinAABB(EntityLeafcutterAnt.class, LeafcutterAnt.getBoundingBox().grow(dist, dist/2, dist));
            EntityLeafcutterAnt LeafcutterAnt = null;
            double d0 = Double.MAX_VALUE;

            for(Entity entity : list) {
                EntityLeafcutterAnt LeafcutterAnt1 = (EntityLeafcutterAnt)entity;
                if (LeafcutterAnt1.inCaravan() && !LeafcutterAnt1.hasCaravanTrail()) {
                    double d1 = this.LeafcutterAnt.getDistanceSq(LeafcutterAnt1);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        LeafcutterAnt = LeafcutterAnt1;
                    }
                }
            }

            if (LeafcutterAnt == null) {
                for(Entity entity1 : list) {
                    EntityLeafcutterAnt llamaentity2 = (EntityLeafcutterAnt)entity1;
                    if (llamaentity2.shouldLeadCaravan() && !llamaentity2.hasCaravanTrail()) {
                        double d2 = this.LeafcutterAnt.getDistanceSq(llamaentity2);
                        if (!(d2 > d0)) {
                            d0 = d2;
                            LeafcutterAnt = llamaentity2;
                        }
                    }
                }
            }

            if (LeafcutterAnt == null) {
                return false;
            } else if (d0 < 2.0D) {
                return false;
            } else if (!LeafcutterAnt.shouldLeadCaravan() && !this.firstIsSilverback(LeafcutterAnt, 1)) {
                return false;
            } else {
                this.LeafcutterAnt.joinCaravan(LeafcutterAnt);
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {

        if (this.LeafcutterAnt.inCaravan() && this.LeafcutterAnt.getCaravanHead().isAlive() && this.firstIsSilverback(this.LeafcutterAnt, 0)) {
            double d0 = this.LeafcutterAnt.getDistanceSq(this.LeafcutterAnt.getCaravanHead());
            if (d0 > 676.0D) {
                if (this.speedModifier <= 1.5D) {
                    this.speedModifier *= 1.2D;
                    this.distCheckCounter = 40;
                    return true;
                }

                if (this.distCheckCounter == 0) {
                    return false;
                }
            }

            if (this.distCheckCounter > 0) {
                --this.distCheckCounter;
            }

            return true;
        } else {
            return false;
        }
    }

    public void resetTask() {
        this.LeafcutterAnt.leaveCaravan();
        this.speedModifier = 1.5D;
    }

    public void tick() {
        if (this.LeafcutterAnt.inCaravan() && !this.LeafcutterAnt.shouldLeadCaravan()) {
            EntityLeafcutterAnt llamaentity = this.LeafcutterAnt.getCaravanHead();
            if (llamaentity != null) {
                double d0 = (double) this.LeafcutterAnt.getDistance(llamaentity);
                Vector3d vector3d = (new Vector3d(llamaentity.getPosX() - this.LeafcutterAnt.getPosX(), llamaentity.getPosY() - this.LeafcutterAnt.getPosY(), llamaentity.getPosZ() - this.LeafcutterAnt.getPosZ())).normalize().scale(Math.max(d0 - 2.0D, 0.0D));
                try {
                    this.LeafcutterAnt.getNavigator().tryMoveToXYZ(this.LeafcutterAnt.getPosX() + vector3d.x, this.LeafcutterAnt.getPosY() + vector3d.y, this.LeafcutterAnt.getPosZ() + vector3d.z, this.speedModifier);
                } catch (NullPointerException e) {
                    AlexsMobs.LOGGER.warn("leafcutter ant encountered issue following caravan head");
                }
            }
        }
    }

    private boolean firstIsSilverback(EntityLeafcutterAnt llama, int p_190858_2_) {
        if (p_190858_2_ > 8) {
            return false;
        } else if (llama.inCaravan()) {
            if (llama.getCaravanHead().shouldLeadCaravan()) {
                return true;
            } else {
                EntityLeafcutterAnt llamaentity = llama.getCaravanHead();
                ++p_190858_2_;
                return this.firstIsSilverback(llamaentity, p_190858_2_);
            }
        } else {
            return false;
        }
    }
}
