package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;

import java.util.EnumSet;

public class AnimalAIWadeSwimming  extends Goal {
    private final MobEntity entity;

    public AnimalAIWadeSwimming(MobEntity entity) {
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Flag.JUMP));
        entity.getNavigator().setCanSwim(true);
    }

    public boolean shouldExecute() {
        return this.entity.isInWater() && this.entity.func_233571_b_(FluidTags.WATER) > 1F  || this.entity.isInLava();
    }

    public void tick() {
        if (this.entity.getRNG().nextFloat() < 0.8F) {
            this.entity.getJumpController().setJumping();
        }

    }
}
