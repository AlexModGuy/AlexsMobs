package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;

public class EffectPoisonResistance extends Effect {

    public EffectPoisonResistance() {
        super(EffectType.BENEFICIAL, 0X51FFAF);
        this.setRegistryName(AlexsMobs.MODID, "poison_resistance");

    }

    public void performEffect(LivingEntity LivingEntityIn, int amplifier) {
        if(LivingEntityIn.isPotionActive(Effects.POISON)){
            LivingEntityIn.removePotionEffect(Effects.POISON);
        }
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.poison_resistance";
    }

}
