package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.sounds.SoundEvent;

public class AMArmorMaterial extends CustomArmorMaterial {

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private int maxDamageFactor;

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness) {
        super(name, durability, damageReduction, encantability, sound, toughness, 0);
        this.maxDamageFactor = durability;
    }

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResist) {
        super(name, durability, damageReduction, encantability, sound, toughness, knockbackResist);
        this.maxDamageFactor = durability;
    }

    public int getDurabilityForSlot(EquipmentSlot slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }
}
