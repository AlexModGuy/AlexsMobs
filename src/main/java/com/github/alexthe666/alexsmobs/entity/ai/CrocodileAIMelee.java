package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.InteractionHand;

public class CrocodileAIMelee extends MeleeAttackGoal {

    private EntityCrocodile crocodile;

    public CrocodileAIMelee(EntityCrocodile crocodile, double speedIn, boolean useLongMemory) {
        super(crocodile, speedIn, useLongMemory);
        this.crocodile = crocodile;
    }

    public boolean canUse() {

        return super.canUse() && crocodile.getPassengers().isEmpty();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && crocodile.getPassengers().isEmpty();
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
