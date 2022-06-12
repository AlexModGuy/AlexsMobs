package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.damagesource.DamageSource;

public class EffectExsanguination extends MobEffect {

    private int lastDuration = -1;

    protected EffectExsanguination() {
        super(MobEffectCategory.HARMFUL, 0XED5151);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(DamageSource.MAGIC, Math.min(amplifier + 1, Math.round(lastDuration / 20F)));
        for(int i = 0; i < 3; i++){
            entity.level.addParticle(ParticleTypes.DAMAGE_INDICATOR, entity.getRandomX(1.0), entity.getRandomY(), entity.getRandomZ(1.0), 0, 0, 0);
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0 && duration % 20 == 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.exsanguination";
    }

}
