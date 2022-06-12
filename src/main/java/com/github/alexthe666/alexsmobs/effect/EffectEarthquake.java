package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EffectEarthquake extends MobEffect {

    public EffectEarthquake() {
        super(MobEffectCategory.HARMFUL, 0XF0E9E1);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.earthquake";
    }
}
