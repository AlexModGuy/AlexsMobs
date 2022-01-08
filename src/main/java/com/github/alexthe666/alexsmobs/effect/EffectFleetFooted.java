package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class EffectFleetFooted extends MobEffect {

    private static final UUID SPRINT_JUMP_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF29A");
    private static final AttributeModifier SPRINT_JUMP_SPEED_BONUS = new AttributeModifier(SPRINT_JUMP_SPEED_MODIFIER, "fleetfooted speed bonus", 0.2F, AttributeModifier.Operation.ADDITION);
    private int lastDuration = -1;
    private int removeEffectAfter = 0;

    public EffectFleetFooted() {
        super(MobEffectCategory.BENEFICIAL, 0X685441);
        this.setRegistryName(AlexsMobs.MODID, "fleet_footed");
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        AttributeInstance modifiableattributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        boolean applyEffect = entity.isSprinting() && !entity.isOnGround() && lastDuration > 2;
        if(removeEffectAfter > 0){
            removeEffectAfter--;
        }
        if (applyEffect) {
            if(!modifiableattributeinstance.hasModifier(SPRINT_JUMP_SPEED_BONUS)){
                modifiableattributeinstance.addPermanentModifier(SPRINT_JUMP_SPEED_BONUS);
            }
            removeEffectAfter = 5;
        }
        if (removeEffectAfter <= 0 || lastDuration < 2) {
            modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_BONUS);
        }
    }

    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int level) {
        AttributeInstance modifiableattributeinstance = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if(modifiableattributeinstance != null && modifiableattributeinstance.hasModifier(SPRINT_JUMP_SPEED_BONUS)){
            modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_BONUS);
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.fleet_footed";
    }

}