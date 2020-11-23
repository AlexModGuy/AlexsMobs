package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;

public interface ITargetsDroppedItems {

    boolean canTargetItem(ItemStack stack);

    void onGetItem(ItemEntity e);
}
