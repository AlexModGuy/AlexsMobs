package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

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
    public static EntityType getEntityType(@Nullable CompoundTag tag) {
        if (tag != null && tag.contains("DisplayEntityType")) {
            String entityType = tag.getString("DisplayEntityType");
           return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(entityType)).orElse(null);
        }
        return null;
    }
}
