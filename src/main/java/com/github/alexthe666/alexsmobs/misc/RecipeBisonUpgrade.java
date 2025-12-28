package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeBisonUpgrade extends CustomRecipe {

    public RecipeBisonUpgrade(CraftingBookCategory category) {
        super(category);
    }


    private ItemStack createBoots(CraftingInput container){
        ItemStack boots = ItemStack.EMPTY;
        int fur = 0;
        for (int j = 0; j < container.size(); ++j) {
            ItemStack itemstack1 = container.getItem(j);
            if (itemstack1.is(AMBlockRegistry.BISON_FUR_BLOCK.get().asItem())) {
                fur++;
            }
        }
        if(fur == 1){
            for (int j = 0; j < container.size(); ++j) {
                ItemStack itemstack1 = container.getItem(j);
                CustomData customData = itemstack1.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag tag = customData.copyTag();
                boolean notFurred = !tag.getBoolean("BisonFur");
                if (!itemstack1.isEmpty() && notFurred && itemstack1.getEquipmentSlot() == EquipmentSlot.FEET) {
                    boots = itemstack1;
                }
            }
            if(!boots.isEmpty()){
                ItemStack stack = boots.copy();
                CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag tag = customData.copyTag();
                tag.putBoolean("BisonFur", true);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        return !createBoots(inv).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        return createBoots(container);
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AMRecipeRegistry.BISON_UPGRADE.get();
    }
}
