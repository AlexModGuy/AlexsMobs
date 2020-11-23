package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;

public class EffectKnockbackResistance extends Effect {

    public EffectKnockbackResistance() {
        super(EffectType.BENEFICIAL, 0X865337);
        this.setRegistryName(AlexsMobs.MODID, "knockback_resistance");
        this.addAttributesModifier(Attributes.KNOCKBACK_RESISTANCE, "03C3C89D-7037-4B42-869F-B146BCB64D2F", 0.5D, AttributeModifier.Operation.ADDITION);

    }

    public void performEffect(LivingEntity LivingEntityIn, int amplifier) {
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.knockback_resistance";
    }

}
