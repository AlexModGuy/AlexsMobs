package com.github.alexthe666.alexsmobs.world;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class AMConfiguredFeatures {

    public static final Holder<ConfiguredFeature<NoneFeatureConfiguration,?>> CONFIGURED_ANTHILL = FeatureUtils.register("alexsmobs:leafcutter_anthill", AMFeatureRegistry.ANTHILL.get(), FeatureConfiguration.NONE);
    public static final Holder<PlacedFeature> PLACED_ANTHILL = PlacementUtils.register("alexsmobs:leafcutter_anthill", CONFIGURED_ANTHILL, List.of());

}
