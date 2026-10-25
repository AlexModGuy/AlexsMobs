package com.github.alexthe666.alexsmobs.datagen;

import java.util.concurrent.CompletableFuture;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

public class AMDatamapProvider extends DataMapProvider {
    protected AMDatamapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.COMPOSTABLES)
            .add(AMItemRegistry.BANANA, new Compostable(0.65f), false)
            .add(AMBlockRegistry.BANANA_PEEL.value().asItem().builtInRegistryHolder(), new Compostable(1f), false)
            .add(AMItemRegistry.ACACIA_BLOSSOM, new Compostable(0.65f), false)
            .add(AMItemRegistry.GONGYLIDIA, new Compostable(0.9f), false);
    }
}
