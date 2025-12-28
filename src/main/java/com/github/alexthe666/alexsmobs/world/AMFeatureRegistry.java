package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMFeatureRegistry {
    public static final DeferredRegister<Feature<?>> DEF_REG = DeferredRegister.create(Registries.FEATURE, AlexsMobs.MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> LEAFCUTTER_ANTHILL = DEF_REG.register("leafcutter_anthill", () -> new FeatureLeafcutterAnthill(NoneFeatureConfiguration.CODEC));

}
