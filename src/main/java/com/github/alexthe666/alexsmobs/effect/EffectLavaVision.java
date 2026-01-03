package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectLavaVision extends MobEffect {

    public EffectLavaVision() {
        super(MobEffectCategory.BENEFICIAL, 0XFF6A00);

    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.lava_vision";
    }

}