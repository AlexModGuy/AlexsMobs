package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMPaintingRegistry {
    public static final DeferredRegister<PaintingVariant> DEF_REG = DeferredRegister.create(Registries.PAINTING_VARIANT, AlexsMobs.MODID);

    public static final DeferredHolder<PaintingVariant, PaintingVariant> NFT = DEF_REG.register("nft", () -> new PaintingVariant(32, 32, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "nft")));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> DOG_POKER = DEF_REG.register("dog_poker", () -> new PaintingVariant(32, 16, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "dog_poker")));
}
