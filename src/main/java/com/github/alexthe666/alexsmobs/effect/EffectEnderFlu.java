package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.damagesource.DamageSource;

public class EffectEnderFlu extends MobEffect {

    private int lastDuration = -1;

    public EffectEnderFlu() {
        super(MobEffectCategory.HARMFUL, 0X6836AA);
        this.setRegistryName(AlexsMobs.MODID, "ender_flu");
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (lastDuration == 1) {
            int phages = amplifier + 1;
            entity.hurt(DamageSource.MAGIC, phages * 10);
            for (int i = 0; i < phages; i++) {
                EntityEnderiophage phage = AMEntityRegistry.ENDERIOPHAGE.get().create(entity.level);
                phage.copyPosition(entity);
                phage.onSpawnFromEffect();
                phage.setSkinForDimension();
                if (!entity.level.isClientSide) {
                    phage.setStandardFleeTime();
                    entity.level.addFreshEntity(phage);
                }
            }
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.ender_flu";
    }

}