package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectTigersBlessing extends MobEffect {

    protected EffectTigersBlessing() {
        super(MobEffectCategory.BENEFICIAL, 0XFFD75E);
        this.setRegistryName(AlexsMobs.MODID, "tigers_blessing");
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.tigers_blessing";
    }
}
