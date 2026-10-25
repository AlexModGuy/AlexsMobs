package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class AMPaintingRegistry {
    public static final ResourceKey<PaintingVariant> NFT = create("nft");
    public static final ResourceKey<PaintingVariant> DOG_POKER = create("dog_poker");

    private static ResourceKey<PaintingVariant> create(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, name));
    }

    public static void bootstrap(BootstrapContext<PaintingVariant> context) {
        context.register(NFT, new PaintingVariant(2, 2, AlexsMobs.prefix("nft")));
        context.register(DOG_POKER, new PaintingVariant(2, 1, AlexsMobs.prefix("dog_poker")));
    }
}
