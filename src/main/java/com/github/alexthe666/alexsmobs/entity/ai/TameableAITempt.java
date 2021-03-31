package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.crafting.Ingredient;

public class TameableAITempt extends TemptGoal {

    private AnimalEntity tameable;

    public TameableAITempt(AnimalEntity tameable, double speedIn, Ingredient temptItemsIn, boolean scaredByPlayerMovementIn) {
        super(tameable, speedIn, temptItemsIn, scaredByPlayerMovementIn);
        this.tameable = tameable;
    }

    public boolean shouldExecute() {
        return (!(tameable instanceof TameableEntity) || ((TameableEntity)tameable).isTamed()) && super.shouldExecute();
    }
}
