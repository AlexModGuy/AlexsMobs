package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.crafting.Ingredient;

public class TameableAITempt extends TemptGoal {

    private TameableEntity tameable;

    public TameableAITempt(TameableEntity tameable, double speedIn, Ingredient temptItemsIn, boolean scaredByPlayerMovementIn) {
        super(tameable, speedIn, temptItemsIn, scaredByPlayerMovementIn);
        this.tameable = tameable;
    }

    public boolean shouldExecute() {
        return tameable.isTamed() && super.shouldExecute();
    }
}
