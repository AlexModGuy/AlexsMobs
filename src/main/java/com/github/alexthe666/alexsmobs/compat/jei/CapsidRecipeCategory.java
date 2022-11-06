package com.github.alexthe666.alexsmobs.compat.jei;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.misc.CapsidRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
public class CapsidRecipeCategory  implements IRecipeCategory<CapsidRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public CapsidRecipeCategory(IGuiHelper guiHelper) {
        background = new CapsidDrawable();
        icon = guiHelper.createDrawableItemStack(new ItemStack(AMBlockRegistry.CAPSID.get()));
    }

    @Override
    public RecipeType<CapsidRecipe> getRecipeType() {
        return AlexMobsJEIPlugin.CAPID_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return AMBlockRegistry.CAPSID.get().getName().append(Component.literal(" ")).append(Component.translatable("alexsmobs.gui.capsid_transformation"));
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CapsidRecipe recipe, IFocusGroup focuses) {
        for(int i = 0; i < recipe.getIngredients().size(); i++){
            Ingredient ingredient = recipe.getIngredients().get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, 21 + i * 15, 23).addIngredients(ingredient);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 23).addItemStack(recipe.getResult());
    }

    @Override
    public boolean isHandled(CapsidRecipe recipe) {
        return true;
    }
}

