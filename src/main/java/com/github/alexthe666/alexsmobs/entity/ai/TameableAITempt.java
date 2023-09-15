package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.crafting.Ingredient;

public class TameableAITempt extends TemptGoal {

    private static final TargetingConditions DEF = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
    private final Animal tameable;
    private int calmDown;
    private final TargetingConditions targetingConditions;
    private final Ingredient items;

    public TameableAITempt(Animal tameable, double speedIn, Ingredient temptItemsIn, boolean scaredByPlayerMovementIn) {
        super(tameable, speedIn, temptItemsIn, scaredByPlayerMovementIn);
        this.tameable = tameable;
        this.items = temptItemsIn;
        this.targetingConditions = DEF.copy().selector(this::shouldFollowAM);
    }


    public boolean shouldFollowAM(LivingEntity p_148139_) {
        return this.items.test(p_148139_.getMainHandItem()) || this.items.test(p_148139_.getOffhandItem());
    }

    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            this.player = this.mob.level.getNearestPlayer(this.targetingConditions, this.mob);
            return  (!(tameable instanceof TamableAnimal) || !((TamableAnimal)tameable).isTame()) && this.player != null;
        }
    }


    public void stop() {
        super.stop();
        this.calmDown = reducedTickDelay(100);
    }

}
