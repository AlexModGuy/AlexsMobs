package com.github.alexthe666.alexsmobs.world;

import java.util.List;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class AMFeatureRegistry {
    public static final DeferredRegister<Feature<?>> DEF_REG = DeferredRegister.create(Registries.FEATURE, AlexsMobs.MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> LEAFCUTTER_ANTHILL = DEF_REG.register("leafcutter_anthill", () -> new FeatureLeafcutterAnthill(NoneFeatureConfiguration.CODEC));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(ResourceKey.create(Registries.CONFIGURED_FEATURE, LEAFCUTTER_ANTHILL.getId()),
            new ConfiguredFeature<>(LEAFCUTTER_ANTHILL.value(), FeatureConfiguration.NONE));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        Holder<ConfiguredFeature<?, ?>> configuredFeature = context.lookup(Registries.CONFIGURED_FEATURE)
            .getOrThrow(ResourceKey.create(Registries.CONFIGURED_FEATURE, LEAFCUTTER_ANTHILL.getId()));
        context.register(ResourceKey.create(Registries.PLACED_FEATURE, LEAFCUTTER_ANTHILL.getId()),
            new PlacedFeature(configuredFeature, List.of(new InSquarePlacement())));
    }
}
