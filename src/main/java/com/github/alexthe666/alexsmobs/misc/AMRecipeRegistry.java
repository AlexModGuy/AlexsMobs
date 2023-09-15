package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AMRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> DEF_REG = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, AlexsMobs.MODID);
    public static final RegistryObject<RecipeSerializer<?>> MIMICREAM_RECIPE = DEF_REG.register("mimicream_repair", () -> new SimpleRecipeSerializer<>(RecipeMimicreamRepair::new));
    public static final RegistryObject<RecipeSerializer<?>> BISON_UPGRADE = DEF_REG.register("bison_upgrade", () -> new SimpleRecipeSerializer<>(RecipeBisonUpgrade::new));

    public static void init(){
    }
}
