package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMFeatureRegistry {
    public static final DeferredRegister<Feature<?>> DEF_REG = DeferredRegister.create(
            ForgeRegistries.FEATURES, AlexsMobs.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ANTHILL = DEF_REG.register("anthill_feature", () -> new FeatureLeafcutterAnthill(NoneFeatureConfiguration.CODEC));

}
