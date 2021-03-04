package com.github.alexthe666.alexsmobs.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class StraddleEnchantment extends Enchantment {

    protected StraddleEnchantment(Rarity r, EnchantmentType type, EquipmentSlotType... types) {
        super(r, type, types);
    }

    public int getMinEnchantability(int i) {
        return 12 + (i + 1) * 9;
    }

    public int getMaxEnchantability(int i) {
        return super.getMinEnchantability(i) + 30;
    }

    public int getMaxLevel() {
        return 1;
    }
}
