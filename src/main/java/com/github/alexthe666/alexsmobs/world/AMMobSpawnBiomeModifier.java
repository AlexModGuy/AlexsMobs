package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public record AMMobSpawnBiomeModifier() implements BiomeModifier {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = 
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
    
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AMMobSpawnBiomeModifier>> SERIALIZER = 
            BIOME_MODIFIER_SERIALIZERS.register("am_mob_spawns", () -> MapCodec.unit(AMMobSpawnBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            AMWorldRegistry.addBiomeSpawns(biome, builder);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SERIALIZER.getId()), new AMMobSpawnBiomeModifier());
        context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AlexsMobs.prefix("am_leafcutter_ant_spawns")), new AMLeafcutterAntBiomeModifier(
            HolderSet.direct(context.lookup(Registries.PLACED_FEATURE)
                .getOrThrow(ResourceKey.create(Registries.PLACED_FEATURE, AMFeatureRegistry.LEAFCUTTER_ANTHILL.getId())))
        ));
    }
}
