package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StraddlerAIShoot  extends Goal {
    private final EntityStraddler entity;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private int animationCooldown = 0;

    public StraddlerAIShoot(EntityStraddler mob, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn) {
        this.entity = mob;
        this.moveSpeedAmp = moveSpeedAmpIn;
        this.attackCooldown = attackCooldownIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setAttackCooldown(int attackCooldownIn) {
        this.attackCooldown = attackCooldownIn;
    }

    public boolean canUse() {
        return this.entity.getTarget() == null ? false : this.isBowInMainhand();
    }

    protected boolean isBowInMainhand() {
        return this.entity.shouldShoot();
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.entity.getNavigation().isDone()) && this.isBowInMainhand();
    }

    public void start() {
        super.start();
        this.entity.setAggressive(true);
    }


    public void stop() {
        super.stop();
        this.entity.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.stopUsingItem();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        LivingEntity livingentity = this.entity.getTarget();
        if(animationCooldown > 0){
            animationCooldown--;
        }
        if (livingentity != null) {
            double d0 = this.entity.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.entity.canSee(livingentity);
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
                if(entity.getAnimation() != EntityStraddler.ANIMATION_LAUNCH){
                    entity.setAnimation(EntityStraddler.ANIMATION_LAUNCH);
                    this.attackTime = this.attackCooldown;
                }
            }
        }
    }
}

