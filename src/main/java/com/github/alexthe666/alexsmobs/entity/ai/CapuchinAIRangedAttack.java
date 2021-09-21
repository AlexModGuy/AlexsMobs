package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CapuchinAIRangedAttack extends Goal {
    private final EntityCapuchinMonkey entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistanceDef;
    private float maxAttackDistance;
    private int attackCooldown;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public CapuchinAIRangedAttack(EntityCapuchinMonkey mob, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn) {
        this.entity = mob;
        this.moveSpeedAmp = moveSpeedAmpIn;
        this.attackCooldown = attackCooldownIn;
        this.maxAttackDistanceDef = maxAttackDistanceIn * maxAttackDistanceIn;
        this.maxAttackDistance = maxAttackDistanceDef;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setAttackCooldown(int attackCooldownIn) {
        this.attackCooldown = attackCooldownIn;
    }

    public boolean canUse() {
        return this.entity.getDartTarget() != null && this.shouldRange();
    }

    protected boolean shouldRange() {
        return this.entity.attackDecision;
    }

    public boolean canContinueToUse() {
        return this.entity.getDartTarget() != null && this.entity.getDartTarget().isAlive() &&(this.canUse() || !this.entity.getNavigation().isDone()) && this.shouldRange();
    }

    public void start() {
        super.start();
        this.maxAttackDistance = maxAttackDistanceDef * (entity.hasDart() ? 2 : 1);
        this.entity.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.entity.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.setAnimation(EntityCapuchinMonkey.NO_ANIMATION);
        this.entity.stopUsingItem();
    }

    public void tick() {
        Entity livingentity = this.entity.getDartTarget();
        if (livingentity != null && livingentity.isAlive()) {
            double d0 = this.entity.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.entity.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(d0 > (double)this.maxAttackDistance) && this.seeTime >= 20) {
                this.entity.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.entity.getNavigation().moveTo(livingentity, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.entity.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.entity.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double)(this.maxAttackDistance * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double)(this.maxAttackDistance * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.entity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.lookAt(livingentity, 30.0F, 30.0F);
            } else {
                this.entity.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }

            if (!flag && this.seeTime < -60) {
                this.entity.stopUsingItem();
            } else if (flag) {
                this.entity.setAnimation(EntityCapuchinMonkey.ANIMATION_THROW);
                this.attackTime = this.attackCooldown;
            }

        }
    }
}
