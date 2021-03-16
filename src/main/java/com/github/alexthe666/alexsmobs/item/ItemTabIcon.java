package com.github.alexthe666.alexsmobs.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public class ItemTabIcon extends Item {
    public ItemTabIcon(Item.Properties properties) {
        super(properties);
    }

    public static boolean hasCustomEntityDisplay(ItemStack stack){
        return stack.getTag() != null && stack.getTag().contains("DisplayEntityType");
    }

    public static String getCustomDisplayEntityString(ItemStack stack){
        return stack.getTag().getString("DisplayEntityType");
    }

    @Nullable
    public static EntityType getEntityType(@Nullable CompoundNBT tag) {
        if (tag != null && tag.contains("DisplayEntityType")) {
            String entityType = tag.getString("DisplayEntityType");
           return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryCreate(entityType)).orElse(null);
        }
        return null;
    }
}
