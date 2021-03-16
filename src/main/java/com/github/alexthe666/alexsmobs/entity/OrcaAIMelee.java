package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class OrcaAIMelee extends MeleeAttackGoal {

    public OrcaAIMelee(EntityOrca orca, double v, boolean b) {
        super(orca, v, b);
    }

    public boolean shouldExecute(){
        if(this.attacker.getAttackTarget() == null || ((EntityOrca)this.attacker).shouldUseJumpAttack(this.attacker.getAttackTarget())){
            return false;
        }
        return super.shouldExecute();
    }
}
