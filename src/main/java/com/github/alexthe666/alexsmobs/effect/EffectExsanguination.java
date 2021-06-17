package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

public class EffectExsanguination extends Effect {

    private int lastDuration = -1;

    protected EffectExsanguination() {
        super(EffectType.HARMFUL, 0XED5151);
        this.setRegistryName(AlexsMobs.MODID, "exsanguination");
    }

    public void performEffect(LivingEntity entity, int amplifier) {
        entity.attackEntityFrom(DamageSource.MAGIC, Math.min(amplifier + 1, Math.round(lastDuration / 20F)));
        for(int i = 0; i < 3; i++){
            entity.world.addParticle(ParticleTypes.DAMAGE_INDICATOR, entity.getPosXRandom(1.0), entity.getPosYRandom(), entity.getPosZRandom(1.0), 0, 0, 0);
        }
    }

    public boolean isReady(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0 && duration % 20 == 0;
    }

    public String getName() {
        return "alexsmobs.potion.exsanguination";
    }

}
