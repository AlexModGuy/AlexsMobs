package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

/**
 * Painting variants are now data-driven in 1.21+.
 * The actual painting variants are defined in JSON files at:
 * data/alexsmobs/painting_variant/nft.json
 * data/alexsmobs/painting_variant/dog_poker.json
 * 
 * This class only holds ResourceKeys for reference.
 */
public class AMPaintingRegistry {
    public static final ResourceKey<PaintingVariant> NFT = create("nft");
    public static final ResourceKey<PaintingVariant> DOG_POKER = create("dog_poker");

    private static ResourceKey<PaintingVariant> create(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, name));
    }
}
