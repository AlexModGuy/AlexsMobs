package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.scores.Team;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class CapuchinAITargetBalloons extends Goal {

    private EntityCapuchinMonkey monkey;
    protected final boolean shouldCheckSight;
    private final boolean nearbyOnly;
    private int targetSearchStatus;
    private int targetSearchDelay;
    private int targetUnseenTicks;
    protected Entity target;
    protected int unseenMemoryTicks = 60;
    protected final int targetChance;
    public static final Predicate<Entity> TARGET_BLOON = (balloon) -> {
        return balloon.getEncodeId() != null && (balloon.getEncodeId().contains("balloon") || balloon.getEncodeId().contains("balloom"));
    };

    public CapuchinAITargetBalloons(EntityCapuchinMonkey mobIn, boolean checkSight) {
        this(mobIn, checkSight, false, 40);
    }

    public CapuchinAITargetBalloons(EntityCapuchinMonkey mobIn, boolean checkSight, boolean nearbyOnlyIn, int targetChance) {
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.monkey = mobIn;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = nearbyOnlyIn;
        this.targetChance = targetChance;
    }

    @Override
    public boolean canUse() {
        if (this.targetChance > 0 && this.monkey.getRandom().nextInt(this.targetChance) != 0) {
            return false;
        } else {
            this.findNearestTarget();
            return this.target != null;
        }
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.monkey.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
    }

    protected void findNearestTarget() {
        Entity closest = null;
        for(Entity bloon : this.monkey.level.getEntitiesOfClass(Entity.class, getTargetableArea(getTargetDistance()), TARGET_BLOON)){
            if(closest == null || closest.distanceTo(monkey) > bloon.distanceTo(monkey)){
                closest = bloon;
            }
        }
        this.target = closest;
    }

    public boolean canContinueToUse() {
        Entity livingentity = this.monkey.getDartTarget();
        if (livingentity == null) {
            livingentity = this.target;
        }

        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else {
            Team team = this.monkey.getTeam();
            Team team1 = livingentity.getTeam();
            if (team != null && team1 == team) {
                return false;
            } else {
                double d0 = this.getTargetDistance();
                if (this.monkey.distanceToSqr(livingentity) > d0 * d0) {
                    return false;
                } else {
                    if (this.shouldCheckSight) {
                        if (this.monkey.getSensing().hasLineOfSight(livingentity)) {
                            this.targetUnseenTicks = 0;
                        } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                            return false;
                        }
                    }

                    if (livingentity instanceof Player && ((Player)livingentity).getAbilities().invulnerable) {
                        return false;
                    } else {
                        this.monkey.setDartTarget(livingentity);
                        return true;
                    }
                }
            }
        }
    }

    protected double getTargetDistance() {
        return this.monkey.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    public void start() {
        this.monkey.setDartTarget(this.target);
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.monkey.setTarget((LivingEntity)null);
        this.monkey.setDartTarget(null);
        this.target = null;
    }

    /**
     * checks if is is a suitable target
     */
    protected boolean isSuitableTarget(@Nullable LivingEntity potentialTarget, TargetingConditions targetPredicate) {
        if (potentialTarget == null) {
            return false;
        } else if (!targetPredicate.test(this.monkey, potentialTarget)) {
            return false;
        } else if (!this.monkey.isWithinRestriction(potentialTarget.blockPosition())) {
            return false;
        } else {
            if (this.nearbyOnly) {
                if (--this.targetSearchDelay <= 0) {
                    this.targetSearchStatus = 0;
                }

                if (this.targetSearchStatus == 0) {
                    this.targetSearchStatus = this.canEasilyReach(potentialTarget) ? 1 : 2;
                }

                if (this.targetSearchStatus == 2) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Checks to see if this entity can find a short path to the given target.
     */
    private boolean canEasilyReach(LivingEntity target) {
        this.targetSearchDelay = 10 + this.monkey.getRandom().nextInt(5);
        Path path = this.monkey.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            Node pathpoint = path.getEndNode();
            if (pathpoint == null) {
                return false;
            } else {
                int i = pathpoint.x - Mth.floor(target.getX());
                int j = pathpoint.z - Mth.floor(target.getZ());
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }

    public CapuchinAITargetBalloons setUnseenMemoryTicks(int unseenMemoryTicksIn) {
        this.unseenMemoryTicks = unseenMemoryTicksIn;
        return this;
    }
}
