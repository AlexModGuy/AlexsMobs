package com.github.alexthe666.alexsmobs.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ConfigHolder {

    public static final ModConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;

    static {
        {
            final Pair<CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }
}