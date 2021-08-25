package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

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
        return balloon.getEntityString() != null && (balloon.getEntityString().contains("balloon") || balloon.getEntityString().contains("balloom"));
    };

    public CapuchinAITargetBalloons(EntityCapuchinMonkey mobIn, boolean checkSight) {
        this(mobIn, checkSight, false, 40);
    }

    public CapuchinAITargetBalloons(EntityCapuchinMonkey mobIn, boolean checkSight, boolean nearbyOnlyIn, int targetChance) {
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
        this.monkey = mobIn;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = nearbyOnlyIn;
        this.targetChance = targetChance;
    }

    @Override
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.monkey.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        } else {
            this.findNearestTarget();
            return this.target != null;
        }
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.monkey.getBoundingBox().grow(targetDistance, targetDistance, targetDistance);
    }

    protected void findNearestTarget() {
        Entity closest = null;
        for(Entity bloon : this.monkey.world.getEntitiesWithinAABB(Entity.class, getTargetableArea(getTargetDistance()), TARGET_BLOON)){
            if(closest == null || closest.getDistance(monkey) > bloon.getDistance(monkey)){
                closest = bloon;
            }
        }
        this.target = closest;
    }

    public boolean shouldContinueExecuting() {
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
                if (this.monkey.getDistanceSq(livingentity) > d0 * d0) {
                    return false;
                } else {
                    if (this.shouldCheckSight) {
                        if (this.monkey.getEntitySenses().canSee(livingentity)) {
                            this.targetUnseenTicks = 0;
                        } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                            return false;
                        }
                    }

                    if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage) {
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

    public void startExecuting() {
        this.monkey.setDartTarget(this.target);
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.monkey.setAttackTarget((LivingEntity)null);
        this.monkey.setDartTarget(null);
        this.target = null;
    }

    /**
     * checks if is is a suitable target
     */
    protected boolean isSuitableTarget(@Nullable LivingEntity potentialTarget, EntityPredicate targetPredicate) {
        if (potentialTarget == null) {
            return false;
        } else if (!targetPredicate.canTarget(this.monkey, potentialTarget)) {
            return false;
        } else if (!this.monkey.isWithinHomeDistanceFromPosition(potentialTarget.getPosition())) {
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
        this.targetSearchDelay = 10 + this.monkey.getRNG().nextInt(5);
        Path path = this.monkey.getNavigator().pathfind(target, 0);
        if (path == null) {
            return false;
        } else {
            PathPoint pathpoint = path.getFinalPathPoint();
            if (pathpoint == null) {
                return false;
            } else {
                int i = pathpoint.x - MathHelper.floor(target.getPosX());
                int j = pathpoint.z - MathHelper.floor(target.getPosZ());
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }

    public CapuchinAITargetBalloons setUnseenMemoryTicks(int unseenMemoryTicksIn) {
        this.unseenMemoryTicks = unseenMemoryTicksIn;
        return this;
    }
}
