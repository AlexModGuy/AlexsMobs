package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.passive.AnimalEntity;

public class AnimalAIHurtByTargetNotBaby extends HurtByTargetGoal {

    private AnimalEntity animal;

    public AnimalAIHurtByTargetNotBaby(AnimalEntity creatureIn, Class<?>... excludeReinforcementTypes) {
        super(creatureIn, excludeReinforcementTypes);
        this.animal = creatureIn;
    }

    public void startExecuting() {
        super.startExecuting();
        if (animal.isChild()) {
            this.alertOthers();
            this.resetTask();
        }

    }

    protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
        if (!mobIn.isChild()) {
            super.setAttackTarget(mobIn, targetIn);
        }
    }
}
