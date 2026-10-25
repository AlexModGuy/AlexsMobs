package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public record AMMobSpawnStructureModifier() implements StructureModifier {
    public static final DeferredRegister<MapCodec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = 
            DeferredRegister.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
    
    public static final DeferredHolder<MapCodec<? extends StructureModifier>, MapCodec<AMMobSpawnStructureModifier>> SERIALIZER = 
            STRUCTURE_MODIFIER_SERIALIZERS.register("am_structure_spawns", () -> MapCodec.unit(AMMobSpawnStructureModifier::new));

    @Override
    public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == StructureModifier.Phase.ADD) {
            AMWorldRegistry.modifyStructure(structure, builder);
        }
    }

    @Override
    public MapCodec<? extends StructureModifier> codec() {
        return SERIALIZER.get();
    }

    public static void bootstrap(BootstrapContext<StructureModifier> context) {
        context.register(ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, AlexsMobs.prefix("am_mob_spawns")), new AMMobSpawnStructureModifier());
    }
}
