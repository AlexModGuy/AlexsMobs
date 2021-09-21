package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectLavaVision extends MobEffect {

    public EffectLavaVision() {
        super(MobEffectCategory.BENEFICIAL, 0XFF6A00);
        this.setRegistryName(AlexsMobs.MODID, "lava_vision");

    }

    public void applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.lava_vision";
    }

}