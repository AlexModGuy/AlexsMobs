package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.InteractionHand;

public class CapuchinAIMelee extends MeleeAttackGoal {

    private EntityCapuchinMonkey monkey;

    public CapuchinAIMelee(EntityCapuchinMonkey monkey, double speedIn, boolean useLongMemory) {
        super(monkey, speedIn, useLongMemory);
        this.monkey = monkey;
    }

    public boolean canUse() {
        return super.canUse() && !monkey.attackDecision;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && !monkey.attackDecision;
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= d0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(enemy);
        }

    }

}
