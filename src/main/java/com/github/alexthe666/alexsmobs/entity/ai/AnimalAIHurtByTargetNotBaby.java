package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;

public class AnimalAIHurtByTargetNotBaby extends HurtByTargetGoal {

    private final Animal animal;

    public AnimalAIHurtByTargetNotBaby(Animal creatureIn, Class<?>... excludeReinforcementTypes) {
        super(creatureIn, excludeReinforcementTypes);
        this.animal = creatureIn;
    }

    public void start() {
        super.start();
        if (animal.isBaby()) {
            this.alertOthers();
            this.stop();
        }

    }

    protected void alertOther(Mob mobIn, LivingEntity targetIn) {
        if (!mobIn.isBaby()) {
            super.alertOther(mobIn, targetIn);
        }
    }
}
