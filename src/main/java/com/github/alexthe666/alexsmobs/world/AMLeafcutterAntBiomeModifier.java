package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMLeafcutterAntBiomeModifier implements BiomeModifier {
    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(new ResourceLocation(AlexsMobs.MODID, "am_leafcutter_ant_spawns"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
    private final HolderSet<PlacedFeature> features;

    public AMLeafcutterAntBiomeModifier(HolderSet<PlacedFeature> features) {
        this.features = features;
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            AMWorldRegistry.addLeafcutterAntSpawns(biome, this.features, builder);
        }
    }

    public Codec<? extends BiomeModifier> codec() {
        return (Codec)SERIALIZER.get();
    }

    public static Codec<AMLeafcutterAntBiomeModifier> makeCodec() {
        return RecordCodecBuilder.create((config) -> {
            return config.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter((otherConfig) -> {
                return otherConfig.features;
            })).apply(config, AMLeafcutterAntBiomeModifier::new);
        });
    }
}
