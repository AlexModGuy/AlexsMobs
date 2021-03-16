package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ILootsChests {

    boolean isLootable(IInventory inventory);

    boolean shouldLootItem(ItemStack stack);
}
