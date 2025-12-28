package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AMLeafcutterAntBiomeModifier implements BiomeModifier {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = 
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
    
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AMLeafcutterAntBiomeModifier>> SERIALIZER = 
            BIOME_MODIFIER_SERIALIZERS.register("am_leafcutter_ant_spawns", AMLeafcutterAntBiomeModifier::makeCodec);
    
    private final HolderSet<PlacedFeature> features;

    public AMLeafcutterAntBiomeModifier(HolderSet<PlacedFeature> features) {
        this.features = features;
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            AMWorldRegistry.addLeafcutterAntSpawns(biome, this.features, builder);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static MapCodec<AMLeafcutterAntBiomeModifier> makeCodec() {
        return RecordCodecBuilder.mapCodec((config) -> {
            return config.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter((otherConfig) -> {
                return otherConfig.features;
            })).apply(config, AMLeafcutterAntBiomeModifier::new);
        });
    }
}
