package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectSoulsteal extends MobEffect {

    public EffectSoulsteal() {
        super(MobEffectCategory.BENEFICIAL, 0X93FDFF);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.soulsteal";
    }

}