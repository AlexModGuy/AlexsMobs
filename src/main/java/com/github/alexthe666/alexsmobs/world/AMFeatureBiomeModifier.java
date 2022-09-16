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

public class AMFeatureBiomeModifier implements BiomeModifier {

    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(new ResourceLocation(AlexsMobs.MODID, "am_features"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
    private HolderSet<PlacedFeature> placedFeatureHolderSet;

    public AMFeatureBiomeModifier(HolderSet<PlacedFeature> placedFeatureHolderSet){
        this.placedFeatureHolderSet = placedFeatureHolderSet;
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            AMWorldRegistry.addBiomeFeatures(biome, placedFeatureHolderSet, builder);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return (Codec)SERIALIZER.get();
    }

    public static Codec<AMFeatureBiomeModifier> makeCodec() {
        return RecordCodecBuilder.create(builder -> builder.group(
                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter((amFeatureBiomeModifier -> amFeatureBiomeModifier.placedFeatureHolderSet))
                ).apply(builder, AMFeatureBiomeModifier::new));
    }
}
