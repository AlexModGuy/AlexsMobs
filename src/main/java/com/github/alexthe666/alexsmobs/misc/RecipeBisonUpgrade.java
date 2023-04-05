package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class RecipeBisonUpgrade extends CustomRecipe {

    public RecipeBisonUpgrade(ResourceLocation idIn) {
        super(idIn);
    }


    private ItemStack createBoots(Container container){
        ItemStack boots = ItemStack.EMPTY;
        int fur = 0;
        for (int j = 0; j < container.getContainerSize(); ++j) {
            ItemStack itemstack1 = container.getItem(j);
            if (itemstack1.is(AMBlockRegistry.BISON_FUR_BLOCK.get().asItem())) {
                fur++;
            }
        }
        if(fur == 1){
            for (int j = 0; j < container.getContainerSize(); ++j) {
                ItemStack itemstack1 = container.getItem(j);
                boolean notFurred = !itemstack1.hasTag() || itemstack1.getTag() != null && !itemstack1.getTag().getBoolean("BisonFur");
                if (!itemstack1.isEmpty() && notFurred && LivingEntity.getEquipmentSlotForItem(itemstack1) == EquipmentSlot.FEET) {
                    boots = itemstack1;
                }
            }
            if(!boots.isEmpty()){
                ItemStack stack = boots.copy();
                CompoundTag tag = stack.getOrCreateTag();
                tag.putBoolean("BisonFur", true);
                stack.setTag(tag);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return !createBoots(inv).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
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
