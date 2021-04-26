package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class EffectFear extends Effect {

    protected EffectFear() {
        super(EffectType.NEUTRAL, 0X7474F7);
        this.addAttributesModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", (double)-1.0F, AttributeModifier.Operation.MULTIPLY_BASE);
        this.setRegistryName(AlexsMobs.MODID, "fear");
    }

    public void performEffect(LivingEntity entity, int amplifier) {
        if(entity.getMotion().y > 0 && !entity.isInWaterOrBubbleColumn()){
            entity.setMotion(entity.getMotion().mul(1, 0, 1));
        }
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.fear";
    }
}
