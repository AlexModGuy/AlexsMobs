package com.github.alexthe666.alexsmobs.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class StraddleJumpEnchantment extends StraddleEnchantment {

    protected StraddleJumpEnchantment(Rarity p_i46729_1_, EnchantmentCategory p_i46729_2_, EquipmentSlot... p_i46729_3_) {
        super(p_i46729_1_, p_i46729_2_, p_i46729_3_);
    }

    public int getMinCost(int p_77321_1_) {
        return 15 + (p_77321_1_ - 1) * 9;
    }

    public int getMaxCost(int p_223551_1_) {
        return super.getMinCost(p_223551_1_) + 20;
    }

    public int getMaxLevel() {
        return 3;
    }
}
