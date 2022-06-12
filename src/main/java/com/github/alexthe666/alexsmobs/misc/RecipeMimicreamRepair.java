package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;

public class RecipeMimicreamRepair extends CustomRecipe {
    public RecipeMimicreamRepair(ResourceLocation idIn) {
        super(idIn);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer inv, Level worldIn) {
        if(!AMConfig.mimicreamRepair){
            return false;
        }
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageableItem() && !isBlacklisted(itemstack1)) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.getItem() == AMItemRegistry.MIMICREAM.get()) {
                        mimicreamCount++;
                    }
                }
            }
        }

        return !damageableStack.isEmpty() && mimicreamCount >= 8;
    }

    public boolean isBlacklisted(ItemStack stack) {
        String name = stack.getItem().getRegistryName().toString();
        return AMConfig.mimicreamBlacklist.contains(name);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageableItem() && !isBlacklisted(itemstack1)) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.getItem() == AMItemRegistry.MIMICREAM.get()) {
                        mimicreamCount++;
                    }
                }
            }
        }

        if (!damageableStack.isEmpty() && mimicreamCount >= 8) {
            ItemStack itemstack2 = damageableStack.copy();
            CompoundTag compoundnbt = damageableStack.getTag().copy();
            ListTag oldNBTList = compoundnbt.getList("Enchantments", 10);
            ListTag newNBTList = new ListTag();
            ResourceLocation mendingName = Registry.ENCHANTMENT.getKey(Enchantments.MENDING);
            for (int i = 0; i < oldNBTList.size(); ++i) {
                CompoundTag compoundnbt2 = oldNBTList.getCompound(i);
                ResourceLocation resourcelocation1 = ResourceLocation.tryParse(compoundnbt2.getString("id"));
                if (resourcelocation1 == null || !resourcelocation1.equals(mendingName)) {
                    newNBTList.add(compoundnbt2);
                }
            }
            compoundnbt.put("Enchantments", newNBTList);
            itemstack2.setTag(compoundnbt);
            itemstack2.setDamageValue(itemstack2.getMaxDamage());
            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.hasContainerItem()) {
                nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.getItem().canBeDepleted()) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    public RecipeSerializer<?> getSerializer() {
        return AMRecipeRegistry.MIMICREAM_RECIPE.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
