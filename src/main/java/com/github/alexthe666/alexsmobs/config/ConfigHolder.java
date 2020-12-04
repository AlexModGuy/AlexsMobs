package com.github.alexthe666.alexsmobs.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ConfigHolder {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec BIOME_SPEC;
    public static final CommonConfig COMMON;
    public static final BiomeConfig BIOME;

    static {
        {
            final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
        {
            final Pair<BiomeConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(BiomeConfig::new);
            BIOME = specPair.getLeft();
            BIOME_SPEC = specPair.getRight();
        }
    }
}