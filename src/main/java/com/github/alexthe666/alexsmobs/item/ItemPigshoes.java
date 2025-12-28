package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPigshoes extends Item {

    public ItemPigshoes(Item.Properties props) {
        super(props);
    }

    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    // In 1.21, enchantment compatibility is handled via tags and data-driven enchantments
    // Custom enchantment filtering was removed as enchantments are now data-driven
}
