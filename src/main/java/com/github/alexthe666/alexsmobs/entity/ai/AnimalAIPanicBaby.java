package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.passive.AnimalEntity;

public class AnimalAIPanicBaby extends PanicGoal {

    private AnimalEntity animal;

    public AnimalAIPanicBaby(AnimalEntity creatureIn, double speed) {
        super(creatureIn, speed);
        this.animal = creatureIn;
    }

    public boolean shouldExecute() {
        return animal.isChild() && super.shouldExecute();
    }
}
