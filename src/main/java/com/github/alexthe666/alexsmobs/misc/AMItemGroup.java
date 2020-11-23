package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        return new ItemStack(Items.FEATHER);
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
    }
}
