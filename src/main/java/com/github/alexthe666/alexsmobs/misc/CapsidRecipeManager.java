package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CapsidRecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(CapsidRecipe.class, new CapsidRecipe.Deserializer()).create();
    private static final RandomSource RANDOM = RandomSource.create();

    private final List<CapsidRecipe> capsidRecipes = Lists.newArrayList();

    public CapsidRecipeManager() {
        super(GSON, "capsid_recipes");
    }

    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profile) {
        this.capsidRecipes.clear();
        ImmutableMap.Builder<ResourceLocation, CapsidRecipe> builder = ImmutableMap.builder();
        AlexsMobs.LOGGER.log(Level.ALL, "Loading in capsid_recipes jsons...");
        jsonMap.forEach((resourceLocation, jsonElement) -> {
            try {
                CapsidRecipe capsidRecipe = GSON.fromJson(jsonElement, CapsidRecipe.class);
                builder.put(resourceLocation, capsidRecipe);
            } catch (Exception exception) {
                AlexsMobs.LOGGER.error("Couldn't parse capsid recipe {}", resourceLocation, exception);
            }
        });
        ImmutableMap<ResourceLocation, CapsidRecipe> immutablemap = builder.build();
        immutablemap.forEach((resourceLocation, capsidRecipe) -> {
            capsidRecipes.add(capsidRecipe);
        });
    }

    public CapsidRecipe getRecipeFor(ItemStack stack){
        for(CapsidRecipe recipe : capsidRecipes){
            if(recipe.matches(stack)){
                return recipe;
            }
        }

        return null;
    }

    @Override
    public String getName() {
        return "CapsidRecipeManager";
    }
}
