package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectBugPheromones extends MobEffect {

    public EffectBugPheromones() {
        super(MobEffectCategory.BENEFICIAL, 0X78464B);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.bug_pheromones";
    }

}