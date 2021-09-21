package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;

public class AMItemGroup extends CreativeModeTab {
    public AMItemGroup() {
        super(AlexsMobs.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(AMItemRegistry.TAB_ICON);
    }

    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> items) {
        super.fillItemList(items);
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
                        items.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchant, enchant.getMaxLevel())));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
