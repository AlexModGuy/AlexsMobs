package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

public class CrocodileAIMelee extends MeleeAttackGoal {

    private EntityCrocodile crocodile;

    public CrocodileAIMelee(EntityCrocodile crocodile, double speedIn, boolean useLongMemory) {
        super(crocodile, speedIn, useLongMemory);
        this.crocodile = crocodile;
    }

    public boolean shouldExecute() {

        return super.shouldExecute() && crocodile.getPassengers().isEmpty();
    }

    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && crocodile.getPassengers().isEmpty();
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= d0) {
            this.func_234039_g_();
            this.attacker.swingArm(Hand.MAIN_HAND);
            this.attacker.attackEntityAsMob(enemy);
        }

    }

}
