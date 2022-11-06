package com.github.alexthe666.alexsmobs.compat.jei;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.misc.CapsidRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class AlexMobsJEIPlugin implements IModPlugin {
    public static final ResourceLocation MOD = new ResourceLocation("alexsmobs:alexsmobs");
    @Nullable
    private IRecipeCategory<CapsidRecipe> capsidCategory;

    public static final RecipeType<CapsidRecipe> CAPID_RECIPE_TYPE = RecipeType.create("alexsmobs", "capsid", CapsidRecipe.class);
    @Override
    public ResourceLocation getPluginUid() {
        return MOD;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(capsidCategory = new CapsidRecipeCategory(guiHelper));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CAPID_RECIPE_TYPE, AlexsMobs.PROXY.getCapsidRecipeManager().getCapsidRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AMBlockRegistry.CAPSID.get()), CAPID_RECIPE_TYPE);

    }
}
