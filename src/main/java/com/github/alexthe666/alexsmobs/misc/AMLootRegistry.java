package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMLootRegistry {

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.get(), AlexsMobs.MODID);
    public static final LootItemConditionType matchesBanana = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation("alexsmobs:matches_banana_tag"), new LootItemConditionType(new MatchesBananaTagCondition.LootSerializer()));
    public static final LootItemConditionType matchesBlossom = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation("alexsmobs:matches_blossom_tag"), new LootItemConditionType(new MatchesBlossomTagCondition.LootSerializer()));

    public static final RegistryObject<GlobalLootModifierSerializer<?>> BANANA_DROP = DEF_REG.register("banana_drop", () -> new BananaLootModifier.Serializer());
    public static final RegistryObject<GlobalLootModifierSerializer<?>> BLOSSOM_DROP = DEF_REG.register("blossom_drop", () -> new BlossomLootModifier.Serializer());
}
