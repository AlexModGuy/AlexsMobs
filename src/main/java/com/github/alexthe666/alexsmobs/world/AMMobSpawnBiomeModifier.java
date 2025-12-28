package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AMMobSpawnBiomeModifier implements BiomeModifier {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = 
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
    
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AMMobSpawnBiomeModifier>> SERIALIZER = 
            BIOME_MODIFIER_SERIALIZERS.register("am_mob_spawns", () -> MapCodec.unit(AMMobSpawnBiomeModifier::new));

    public AMMobSpawnBiomeModifier() {
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            AMWorldRegistry.addBiomeSpawns(biome, builder);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }
}
