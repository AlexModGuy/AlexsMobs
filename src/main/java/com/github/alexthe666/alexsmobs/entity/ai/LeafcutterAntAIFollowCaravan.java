package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class LeafcutterAntAIFollowCaravan extends Goal {
    public final EntityLeafcutterAnt ant;
    private double speedModifier;
    private int distCheckCounter;
    private long cachedGameTime = -1;

    public LeafcutterAntAIFollowCaravan(EntityLeafcutterAnt ant, double speedModifier) {
        this.ant = ant;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    private long getCachedGameTime() {
        if (cachedGameTime == -1 || this.ant.level().getGameTime() % 10 == 0) {
            cachedGameTime = this.ant.level().getGameTime();
        }
        return cachedGameTime;
    }

    public boolean canUse() {
        long worldTime = getCachedGameTime() % 10;
        if (this.ant.getNoActionTime() >= 100 && worldTime != 0) {
            return false;
        }
        int executionChance = 30;
        if (this.ant.getRandom().nextInt(executionChance) != 0 && worldTime != 0) {
            return false;
        }
        if (!this.ant.shouldLeadCaravan() && !ant.isBaby() && !this.ant.isQueen() && !this.ant.inCaravan() && !this.ant.hasLeaf()) {
            double dist = 15D;
            List<EntityLeafcutterAnt> list = ant.level().getEntitiesOfClass(EntityLeafcutterAnt.class, ant.getBoundingBox().inflate(dist, dist / 2, dist));
            EntityLeafcutterAnt closestAnt = null;
            double closestDistance = Double.MAX_VALUE;

            for (EntityLeafcutterAnt entity : list) {
                if (entity.inCaravan() && !entity.hasCaravanTrail()) {
                    double distance = this.ant.distanceToSqr(entity);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestAnt = entity;
                    }
                }
            }

            if (closestAnt == null) {
                for (EntityLeafcutterAnt entity : list) {
                    if (entity.shouldLeadCaravan() && !entity.hasCaravanTrail()) {
                        double distance = this.ant.distanceToSqr(entity);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestAnt = entity;
                        }
                    }
                }
            }

            if (closestAnt == null || closestDistance < 2.0D || (!closestAnt.shouldLeadCaravan() && !this.firstIsSilverback(closestAnt, 1))) {
                return false;
            } else {
                this.ant.joinCaravan(closestAnt);
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        if (this.ant.inCaravan() && this.ant.getCaravanHead().isAlive() && this.firstIsSilverback(this.ant, 0)) {
            double distance = this.ant.distanceToSqr(this.ant.getCaravanHead());
            if (distance > 676.0D) {
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
        this.ant.leaveCaravan();
        this.speedModifier = 1.5D;
    }

    public void tick() {
        if (this.ant.inCaravan() && !this.ant.shouldLeadCaravan()) {
            EntityLeafcutterAnt caravanHead = this.ant.getCaravanHead();
            if (caravanHead != null) {
                double distance = this.ant.distanceTo(caravanHead);
                Vec3 direction = new Vec3(caravanHead.getX() - this.ant.getX(), caravanHead.getY() - this.ant.getY(), caravanHead.getZ() - this.ant.getZ()).normalize().scale(Math.max(distance - 2.0D, 0.0D));
                if (ant.getNavigation().isDone()) {
                    try {
                        this.ant.getNavigation().moveTo(this.ant.getX() + direction.x, this.ant.getY() + direction.y, this.ant.getZ() + direction.z, this.speedModifier);
                    } catch (NullPointerException e) {
                        AlexsMobs.LOGGER.warn("Leafcutter ant encountered issue following caravan head");
                    }
                }
            }
        }
    }

    private boolean firstIsSilverback(EntityLeafcutterAnt ant, int depth) {
        if (depth > 8) {
            return false;
        } else if (ant.inCaravan()) {
            if (ant.getCaravanHead().shouldLeadCaravan()) {
                return true;
            } else {
                return this.firstIsSilverback(ant.getCaravanHead(), depth + 1);
            }
        } else {
            return false;
        }
    }
}