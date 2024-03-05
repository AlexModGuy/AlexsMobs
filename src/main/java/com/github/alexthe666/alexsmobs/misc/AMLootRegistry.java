package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMLootRegistry {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> DEF_REG = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BANANA_DROP = DEF_REG.register("banana_drop", BananaLootModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BLOSSOM_DROP = DEF_REG.register("blossom_drop", BlossomLootModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ANCIENT_DART = DEF_REG.register("ancient_dart", AncientDartLootModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> PIGSHOES = DEF_REG.register("pigshoes", PigshoesLootModifier.CODEC);
}
