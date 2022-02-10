package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class RecipeBisonUpgrade implements Recipe<CraftingContainer> {
    private final ResourceLocation id;

    public RecipeBisonUpgrade(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(CraftingContainer container, Level lvl) {
        return !createBoots(container).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        return createBoots(container);
    }

    private ItemStack createBoots(CraftingContainer container){
        ItemStack boots = ItemStack.EMPTY;
        if(container.getItem(1).is(AMItemRegistry.BISON_FUR)){
            for (int j = 0; j < container.getContainerSize(); ++j) {
                ItemStack itemstack1 = container.getItem(j);
                if (!itemstack1.isEmpty() && itemstack1.getEquipmentSlot() == EquipmentSlot.FEET) {
                    boots = itemstack1;
                }
            }
        }
        if(!boots.isEmpty()){
            ItemStack stack = boots.copy();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CommonProxy.BISON_UPGRADE_RECIPE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(AMItemRegistry.BISON_FUR));
    }
}
