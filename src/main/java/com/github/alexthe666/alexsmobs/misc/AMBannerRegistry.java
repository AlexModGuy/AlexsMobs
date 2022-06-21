package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.DeferredRegister;

public class AMBannerRegistry {

    public static final DeferredRegister<BannerPattern> DEF_REG = DeferredRegister.create(Registry.BANNER_PATTERN_REGISTRY, AlexsMobs.MODID);

    static{
        DEF_REG.register("bear", () ->  new BannerPattern("bear"));
        DEF_REG.register("australia_0", () ->  new BannerPattern("australia_0"));
        DEF_REG.register("australia_1", () ->  new BannerPattern("australia_1"));
        DEF_REG.register("new_mexico", () ->  new BannerPattern("new_mexico"));
        DEF_REG.register("brazil", () ->  new BannerPattern("brazil"));
    }
}
