package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectFear extends MobEffect {

    protected EffectFear() {
        super(MobEffectCategory.NEUTRAL, 0X7474F7);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.parse("alexsmobs:fear_speed"), -1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.getDeltaMovement().y > 0 && !entity.isInWaterOrBubble()){
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.fear";
    }
}
