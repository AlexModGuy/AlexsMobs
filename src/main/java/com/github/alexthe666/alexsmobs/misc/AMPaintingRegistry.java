package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMPaintingRegistry {
    public static final DeferredRegister<PaintingVariant> DEF_REG = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, AlexsMobs.MODID);

    public static final RegistryObject<PaintingVariant> NFT = DEF_REG.register("nft", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> DOG_POKER = DEF_REG.register("dog_poker", () -> new PaintingVariant(32, 16));
}
