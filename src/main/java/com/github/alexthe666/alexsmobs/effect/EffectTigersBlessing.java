package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class EffectTigersBlessing extends Effect {

    protected EffectTigersBlessing() {
        super(EffectType.BENEFICIAL, 0XFFD75E);
        this.setRegistryName(AlexsMobs.MODID, "tigers_blessing");
    }

    public void performEffect(LivingEntity entity, int amplifier) {
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.tigers_blessing";
    }
}
