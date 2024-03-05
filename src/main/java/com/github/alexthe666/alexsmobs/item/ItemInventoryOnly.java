package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ItemInventoryOnly extends Item implements CustomTabBehavior {

    public ItemInventoryOnly(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab.Output contents) {

    }
}
