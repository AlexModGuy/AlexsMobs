package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ITargetsDroppedItems;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;

public class FlyingAITempt extends TemptGoal {

    public FlyingAITempt(PathfinderMob mob, double speed, Ingredient ingredient, boolean skittish) {
        super(mob, speed, ingredient, skittish);
    }

    public void tick() {
        super.tick();
        if(mob instanceof ITargetsDroppedItems hasFlyingItemAI && mob.isOnGround()){
            hasFlyingItemAI.setFlying(false);
        }
    }
}
