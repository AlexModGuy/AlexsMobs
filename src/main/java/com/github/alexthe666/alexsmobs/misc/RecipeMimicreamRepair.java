package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class RecipeMimicreamRepair extends SpecialRecipe {
    public RecipeMimicreamRepair(ResourceLocation idIn) {
        super(idIn);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageable()) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.getItem() == AMItemRegistry.MIMICREAM) {
                        mimicreamCount++;
                    }
                }
            }
        }

        return !damageableStack.isEmpty() && mimicreamCount >= 8;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack damageableStack = ItemStack.EMPTY;
        int mimicreamCount = 0;

        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.isDamageable()) {
                    damageableStack = itemstack1;
                } else {
                    if (itemstack1.getItem() == AMItemRegistry.MIMICREAM) {
                        mimicreamCount++;
                    }
                }
            }
        }

        if (!damageableStack.isEmpty() && mimicreamCount >= 8) {
            ItemStack itemstack2 = damageableStack.copy();
            CompoundNBT compoundnbt = damageableStack.getTag().copy();
            ListNBT oldNBTList = compoundnbt.getList("Enchantments", 10);
            ListNBT newNBTList = new ListNBT();
            ResourceLocation mendingName = Registry.ENCHANTMENT.getKey(Enchantments.MENDING);
            for (int i = 0; i < oldNBTList.size(); ++i) {
                CompoundNBT compoundnbt2 = oldNBTList.getCompound(i);
                ResourceLocation resourcelocation1 = ResourceLocation.tryCreate(compoundnbt2.getString("id"));
                if (resourcelocation1 == null || !resourcelocation1.equals(mendingName)) {
                    newNBTList.add(compoundnbt2);
                }
            }
            compoundnbt.put("Enchantments", newNBTList);
            itemstack2.setTag(compoundnbt);
            itemstack2.setDamage(itemstack2.getMaxDamage());
            return itemstack2;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.hasContainerItem()) {
                nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.getItem().isDamageable()) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    public IRecipeSerializer<?> getSerializer() {
        return new SpecialRecipeSerializer<>(RecipeMimicreamRepair::new);
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
