package com.github.alexthe666.alexsmobs.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.datagen.client.AMItemModelProvider;
import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBannerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import com.github.alexthe666.alexsmobs.misc.AMPaintingRegistry;
import com.github.alexthe666.alexsmobs.world.AMFeatureRegistry;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnBiomeModifier;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnStructureModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

@EventBusSubscriber(modid = AlexsMobs.MODID)
public class AMDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        RegistrySetBuilder builder = new RegistrySetBuilder();
        buildRegistries(builder);

        generator.addProvider(event.includeServer(), new AMRecipeProvider(output, lookupProvider));
        AMBlockTagProvider blockTagGenerator = generator.addProvider(event.includeServer(), new AMBlockTagProvider(output, lookupProvider, AlexsMobs.MODID, existingFileHelper));
        generator.addProvider(event.includeServer(), new AMItemTagProvider(output, lookupProvider, blockTagGenerator.contentsGetter()));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(
            new LootTableProvider.SubProviderEntry(provider -> new AMBlockLootTableProvider(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider), LootContextParamSets.BLOCK),
            new LootTableProvider.SubProviderEntry(provider -> new AMEntityLootTableProvider(FeatureFlags.DEFAULT_FLAGS, provider), LootContextParamSets.ENTITY),
            new LootTableProvider.SubProviderEntry(AMBarteringLootTableProvider::new, LootContextParamSets.PIGLIN_BARTER),
            new LootTableProvider.SubProviderEntry(AMGameplayLootTableProvider::new, LootContextParamSets.EMPTY)
        ), lookupProvider));
        generator.addProvider(event.includeServer(), new AMAdvancementProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new AMEntityTypeTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new AMDatamapProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider, builder, Set.of(AlexsMobs.MODID)));

        generator.addProvider(event.includeClient(), new AMItemModelProvider(output, existingFileHelper));
    }

    public static void buildRegistries(RegistrySetBuilder builder) {
        builder.add(Registries.DAMAGE_TYPE, AMDamageTypes::bootstrap);
        builder.add(Registries.ENCHANTMENT, AMEnchantmentRegistry::bootstrap);
        builder.add(Registries.JUKEBOX_SONG, AMItemRegistry::bootstrapJukebox);
        builder.add(Registries.CONFIGURED_FEATURE, AMFeatureRegistry::bootstrapConfigured);
        builder.add(Registries.PLACED_FEATURE, AMFeatureRegistry::bootstrapPlaced);
        builder.add(Registries.PAINTING_VARIANT, AMPaintingRegistry::bootstrap);
        builder.add(Registries.BANNER_PATTERN, AMBannerRegistry::bootstrap);
        builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AMMobSpawnBiomeModifier::bootstrap);
        builder.add(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, AMMobSpawnStructureModifier::bootstrap);
    }
}
