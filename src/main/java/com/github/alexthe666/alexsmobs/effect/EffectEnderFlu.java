package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

public class EffectEnderFlu extends Effect {

    private int lastDuration = -1;

    public EffectEnderFlu() {
        super(EffectType.HARMFUL, 0X6836AA);
        this.setRegistryName(AlexsMobs.MODID, "ender_flu");
    }

    public void performEffect(LivingEntity entity, int amplifier) {
        if (lastDuration == 1) {
            int phages = amplifier + 1;
            entity.attackEntityFrom(DamageSource.MAGIC, phages * 10);
            for (int i = 0; i < phages; i++) {
                EntityEnderiophage phage = AMEntityRegistry.ENDERIOPHAGE.create(entity.world);
                phage.copyLocationAndAnglesFrom(entity);
                phage.onSpawnFromEffect();
                phage.setSkinForDimension();
                if (!entity.world.isRemote) {
                    phage.setStandardFleeTime();
                    entity.world.addEntity(phage);
                }
            }
        }
    }

    public boolean isReady(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.ender_flu";
    }

}