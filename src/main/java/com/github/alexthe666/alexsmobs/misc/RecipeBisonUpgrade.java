package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class RecipeBisonUpgrade extends UpgradeRecipe {

    public RecipeBisonUpgrade(ResourceLocation id, CraftingBookCategory category) {
        this(id);
    }

    public RecipeBisonUpgrade(ResourceLocation id) {
        super(id, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(Container container, Level lvl) {
        return !createBoots(container).isEmpty();
    }

    @Override
    public ItemStack assemble(Container container) {
        return createBoots(container);
    }

    private ItemStack createBoots(Container container){
        ItemStack boots = ItemStack.EMPTY;
        if(container.getItem(1).is(AMBlockRegistry.BISON_FUR_BLOCK.get().asItem())){
            for (int j = 0; j < container.getContainerSize(); ++j) {
                ItemStack itemstack1 = container.getItem(j);
                boolean notFurred = !itemstack1.hasTag() || itemstack1.getTag() != null && !itemstack1.getTag().getBoolean("BisonFur");
                if (!itemstack1.isEmpty() && notFurred && LivingEntity.getEquipmentSlotForItem(itemstack1) == EquipmentSlot.FEET) {
                    boots = itemstack1;
                }
            }
        }
        if(!boots.isEmpty()){
            ItemStack stack = boots.copy();
            CompoundTag tag = stack.getOrCreateTag();
            tag.putBoolean("BisonFur", true);
            stack.setTag(tag);
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

    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AMRecipeRegistry.BISON_UPGRADE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(AMItemRegistry.BISON_FUR.get()));
    }

    public static class Serializer implements RecipeSerializer<RecipeBisonUpgrade> {
        public RecipeBisonUpgrade fromJson(ResourceLocation resourceLocation, JsonObject json) {
            return new RecipeBisonUpgrade(resourceLocation);
        }

        public RecipeBisonUpgrade fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            return new RecipeBisonUpgrade(resourceLocation);
        }

        public void toNetwork(FriendlyByteBuf buf, RecipeBisonUpgrade recipeBisonUpgrade) {
        }
    }
}
