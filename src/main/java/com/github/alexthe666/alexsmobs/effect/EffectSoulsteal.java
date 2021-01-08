package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class EffectSoulsteal extends Effect {

    public EffectSoulsteal() {
        super(EffectType.BENEFICIAL, 0X93FDFF);
        this.setRegistryName(AlexsMobs.MODID, "soulsteal");
    }

    public void performEffect(LivingEntity entity, int amplifier) {
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.soulsteal";
    }

}