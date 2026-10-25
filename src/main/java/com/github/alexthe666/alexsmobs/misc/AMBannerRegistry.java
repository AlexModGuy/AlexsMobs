package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class AMBannerRegistry {
    public static final ResourceKey<BannerPattern> BEAR = key("bear");
    public static final ResourceKey<BannerPattern> AUSTRALIA_0 = key("australia_0");
    public static final ResourceKey<BannerPattern> AUSTRALIA_1 = key("australia_1");
    public static final ResourceKey<BannerPattern> NEW_MEXICO = key("new_mexico");
    public static final ResourceKey<BannerPattern> BRAZIL = key("brazil");

    private static ResourceKey<BannerPattern> key(String name) {
        return ResourceKey.create(Registries.BANNER_PATTERN, AlexsMobs.prefix(name));
    }

    public static void bootstrap(BootstrapContext<BannerPattern> context) {
        context.register(BEAR, new BannerPattern(BEAR.location(), BEAR.location().getPath()));
        context.register(AUSTRALIA_0, new BannerPattern(AUSTRALIA_0.location(), AUSTRALIA_0.location().getPath()));
        context.register(AUSTRALIA_1, new BannerPattern(AUSTRALIA_1.location(), AUSTRALIA_1.location().getPath()));
        context.register(NEW_MEXICO, new BannerPattern(NEW_MEXICO.location(), NEW_MEXICO.location().getPath()));
        context.register(BRAZIL, new BannerPattern(BRAZIL.location(), BRAZIL.location().getPath()));
    }
}
