package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class LeafcutterAntAIFollowCaravan extends Goal {
    public final EntityLeafcutterAnt LeafcutterAnt;
    private double speedModifier;
    private int distCheckCounter;

    public LeafcutterAntAIFollowCaravan(EntityLeafcutterAnt llamaIn, double speedModifierIn) {
        this.LeafcutterAnt = llamaIn;
        this.speedModifier = speedModifierIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.LeafcutterAnt.shouldLeadCaravan() && !LeafcutterAnt.isBaby() && !this.LeafcutterAnt.isQueen() && !this.LeafcutterAnt.inCaravan() && !this.LeafcutterAnt.hasLeaf()) {
            double dist = 15D;
            List<EntityLeafcutterAnt> list = LeafcutterAnt.level.getEntitiesOfClass(EntityLeafcutterAnt.class, LeafcutterAnt.getBoundingBox().inflate(dist, dist/2, dist));
            EntityLeafcutterAnt LeafcutterAnt = null;
            double d0 = Double.MAX_VALUE;

            for(Entity entity : list) {
                EntityLeafcutterAnt LeafcutterAnt1 = (EntityLeafcutterAnt)entity;
                if (LeafcutterAnt1.inCaravan() && !LeafcutterAnt1.hasCaravanTrail()) {
                    double d1 = this.LeafcutterAnt.distanceToSqr(LeafcutterAnt1);
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
                        double d2 = this.LeafcutterAnt.distanceToSqr(llamaentity2);
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
    public boolean canContinueToUse() {

        if (this.LeafcutterAnt.inCaravan() && this.LeafcutterAnt.getCaravanHead().isAlive() && this.firstIsSilverback(this.LeafcutterAnt, 0)) {
            double d0 = this.LeafcutterAnt.distanceToSqr(this.LeafcutterAnt.getCaravanHead());
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

    public void stop() {
        this.LeafcutterAnt.leaveCaravan();
        this.speedModifier = 1.5D;
    }

    public void tick() {
        if (this.LeafcutterAnt.inCaravan() && !this.LeafcutterAnt.shouldLeadCaravan()) {
            EntityLeafcutterAnt llamaentity = this.LeafcutterAnt.getCaravanHead();
            if (llamaentity != null) {
                double d0 = (double) this.LeafcutterAnt.distanceTo(llamaentity);
                Vec3 vector3d = (new Vec3(llamaentity.getX() - this.LeafcutterAnt.getX(), llamaentity.getY() - this.LeafcutterAnt.getY(), llamaentity.getZ() - this.LeafcutterAnt.getZ())).normalize().scale(Math.max(d0 - 2.0D, 0.0D));
                if(LeafcutterAnt.getNavigation().isDone()) {
                    try {
                        this.LeafcutterAnt.getNavigation().moveTo(this.LeafcutterAnt.getX() + vector3d.x, this.LeafcutterAnt.getY() + vector3d.y, this.LeafcutterAnt.getZ() + vector3d.z, this.speedModifier);
                    } catch (NullPointerException e) {
                        AlexsMobs.LOGGER.warn("leafcutter ant encountered issue following caravan head");
                    }
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
