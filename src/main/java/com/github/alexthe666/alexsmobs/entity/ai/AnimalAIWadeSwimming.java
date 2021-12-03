package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class AnimalAIWadeSwimming  extends Goal {
    private final Mob entity;

    public AnimalAIWadeSwimming(Mob entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.JUMP));
        entity.getNavigation().setCanFloat(true);
    }

    public boolean canUse() {
        return this.entity.isInWater() && this.entity.getFluidHeight(FluidTags.WATER) > 1F  || this.entity.isInLava();
    }

    public void tick() {
        if (this.entity.getRandom().nextFloat() < 0.8F) {
            this.entity.getJumpControl().jump();
        }

    }
}
