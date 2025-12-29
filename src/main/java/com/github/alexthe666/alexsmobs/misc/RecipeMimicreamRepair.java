package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RecipeMimicreamRepair extends CustomRecipe {
    public RecipeMimicreamRepair(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInput inv, Level worldIn) {
        if (!AMConfig.mimicreamRepair) {
            return false;
        }
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.size(); ++j) {
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
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return name != null && AMConfig.mimicreamBlacklist.contains(name.toString());
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.size(); ++j) {
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

            if (damageableStack.is(AMItemRegistry.GHOSTLY_PICKAXE.get())) {
                CustomData.update(DataComponents.CUSTOM_DATA, itemstack2, tag -> {
                    if (tag.contains("Items")) {
                        tag.remove("Items");
                    }
                });
            }
            ItemEnchantments enchantments = itemstack2.getOrDefault(DataComponents.ENCHANTMENTS,
                    ItemEnchantments.EMPTY);
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
            mutable.removeIf(holder -> holder.is(Enchantments.MENDING));
            itemstack2.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
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
            if (itemstack.hasCraftingRemainingItem()) {
                nonnulllist.set(i, itemstack.getCraftingRemainingItem());
            } else if (itemstack.isDamageableItem()) {
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
