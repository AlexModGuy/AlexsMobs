package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectKnockbackResistance extends MobEffect {

    public EffectKnockbackResistance() {
        super(MobEffectCategory.BENEFICIAL, 0X865337);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.parse("alexsmobs:knockback_resistance"), 0.5D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.knockback_resistance";
    }

}

