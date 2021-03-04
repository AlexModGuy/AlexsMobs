package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;

public class AMItemGroup extends ItemGroup {
    public AMItemGroup() {
        super(AlexsMobs.MODID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(AMItemRegistry.TAB_ICON);
    }

    @OnlyIn(Dist.CLIENT)
    public void fill(NonNullList<ItemStack> items) {
        super.fill(items);
        try {
            for (Field f : AMEffectRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Potion) {
                    ItemStack potionStack = AMEffectRegistry.createPotion((Potion)obj);
                    items.add(potionStack);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            for (Field f : AMEnchantmentRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Enchantment ) {
                    Enchantment enchant = (Enchantment)obj;
                    if(enchant.isAllowedOnBooks()){
                        items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(enchant, enchant.getMaxLevel())));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
