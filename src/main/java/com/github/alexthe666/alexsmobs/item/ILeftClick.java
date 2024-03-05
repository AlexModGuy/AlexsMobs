package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ILeftClick {

    boolean onLeftClick(ItemStack stack, LivingEntity playerIn);
}
