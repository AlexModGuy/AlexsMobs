package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.List;

public class GorillaAIFollowCaravan extends Goal {
    public final EntityGorilla gorilla;
    private double speedModifier;
    private int distCheckCounter;

    public GorillaAIFollowCaravan(EntityGorilla llamaIn, double speedModifierIn) {
        this.gorilla = llamaIn;
        this.speedModifier = speedModifierIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.gorilla.isSilverback() && !this.gorilla.inCaravan() && !gorilla.isSitting()) {
            double dist = 15D;
            List<EntityGorilla> list = gorilla.world.getEntitiesWithinAABB(EntityGorilla.class, gorilla.getBoundingBox().grow(dist, dist / 2, dist));
            EntityGorilla gorilla = null;
            double d0 = Double.MAX_VALUE;

            for (Entity entity : list) {
                EntityGorilla gorilla1 = (EntityGorilla) entity;
                if (gorilla1.inCaravan() && !gorilla1.hasCaravanTrail()) {
                    double d1 = this.gorilla.getDistanceSq(gorilla1);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        gorilla = gorilla1;
                    }
                }
            }

            if (gorilla == null) {
                for (Entity entity1 : list) {
                    EntityGorilla llamaentity2 = (EntityGorilla) entity1;
                    if (llamaentity2.isSilverback() && !llamaentity2.hasCaravanTrail()) {
                        double d2 = this.gorilla.getDistanceSq(llamaentity2);
                        if (!(d2 > d0)) {
                            d0 = d2;
                            gorilla = llamaentity2;
                        }
                    }
                }
            }

            if (gorilla == null) {
                return false;
            } else if (d0 < 2.0D) {
                return false;
            } else if (!gorilla.isSilverback() && !this.firstIsSilverback(gorilla, 1)) {
                return false;
            } else {
                this.gorilla.joinCaravan(gorilla);
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
        if (gorilla.isSitting()) {
            return false;
        }
        if (this.gorilla.inCaravan() && this.gorilla.getCaravanHead().isAlive() && this.firstIsSilverback(this.gorilla, 0)) {
            double d0 = this.gorilla.getDistanceSq(this.gorilla.getCaravanHead());
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
        this.gorilla.leaveCaravan();
        this.speedModifier = 1.5D;
    }

    public void tick() {
        if (this.gorilla.inCaravan() && !this.gorilla.isSitting()) {
            EntityGorilla llamaentity = this.gorilla.getCaravanHead();
            if (llamaentity != null) {
                double d0 = this.gorilla.getDistance(llamaentity);
                Vector3d vector3d = (new Vector3d(llamaentity.getPosX() - this.gorilla.getPosX(), llamaentity.getPosY() - this.gorilla.getPosY(), llamaentity.getPosZ() - this.gorilla.getPosZ())).normalize().scale(Math.max(d0 - 2.0D, 0.0D));
                if(gorilla.getNavigator().noPath()) {
                    try {
                        this.gorilla.getNavigator().tryMoveToXYZ(this.gorilla.getPosX() + vector3d.x, this.gorilla.getPosY() + vector3d.y, this.gorilla.getPosZ() + vector3d.z, this.speedModifier);
                    } catch (NullPointerException e) {
                        AlexsMobs.LOGGER.warn("gorilla encountered issue following caravan head");
                    }
                }
            }
        }
    }

    private boolean firstIsSilverback(EntityGorilla llama, int p_190858_2_) {
        if (p_190858_2_ > 8) {
            return false;
        } else if (llama.inCaravan()) {
            if (llama.getCaravanHead().isSilverback()) {
                return true;
            } else {
                EntityGorilla llamaentity = llama.getCaravanHead();
                ++p_190858_2_;
                return this.firstIsSilverback(llamaentity, p_190858_2_);
            }
        } else {
            return false;
        }
    }
}
