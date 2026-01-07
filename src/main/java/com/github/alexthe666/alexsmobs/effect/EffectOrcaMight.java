package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectOrcaMight extends MobEffect {

    public EffectOrcaMight() {
        super(MobEffectCategory.BENEFICIAL, 0X4A4A52);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.parse("alexsmobs:orca_might_attack_speed"), 3D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.orcas_might";
    }

}
