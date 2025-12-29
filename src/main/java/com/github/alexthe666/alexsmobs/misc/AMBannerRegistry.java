package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMBannerRegistry {

    public static final DeferredRegister<BannerPattern> DEF_REG = DeferredRegister.create(Registries.BANNER_PATTERN, AlexsMobs.MODID);

    static{
        DEF_REG.register("bear", () ->  new BannerPattern(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "bear"), "bear"));
        DEF_REG.register("australia_0", () ->  new BannerPattern(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "australia_0"), "australia_0"));
        DEF_REG.register("australia_1", () ->  new BannerPattern(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "australia_1"), "australia_1"));
        DEF_REG.register("new_mexico", () ->  new BannerPattern(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "new_mexico"), "new_mexico"));
        DEF_REG.register("brazil", () ->  new BannerPattern(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "brazil"), "brazil"));
    }
}
