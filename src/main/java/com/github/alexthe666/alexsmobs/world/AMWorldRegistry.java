package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMWorldRegistry {

    public static Feature<NoneFeatureConfiguration> LEAFCUTTER_ANTHILL = (Feature<NoneFeatureConfiguration>) new FeatureLeafcutterAnthill(NoneFeatureConfiguration.CODEC).setRegistryName("alexsmobs:leafcutter_hill");
    public static boolean initBiomes = false;
    public static PlacedFeature LEAFCUTTER_ANTHILL_PF;
    @SubscribeEvent
    public static void registerFeature(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(LEAFCUTTER_ANTHILL);
        ResourceLocation res = new ResourceLocation("alexsmobs:leafcutter_anthill");
        ConfiguredFeature<?, ?> feature = LEAFCUTTER_ANTHILL.configured(FeatureConfiguration.NONE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, res, feature);
        LEAFCUTTER_ANTHILL_PF = feature.placed();
        Registry.register(BuiltinRegistries.PLACED_FEATURE, res, LEAFCUTTER_ANTHILL_PF);
    }

    public static void onBiomesLoad(BiomeLoadingEvent event) {
        initBiomes = true;
        Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());
        if (testBiome(BiomeConfig.grizzlyBear, biome) && AMConfig.grizzlyBearSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GRIZZLY_BEAR, AMConfig.grizzlyBearSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.roadrunner, biome) && AMConfig.roadrunnerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ROADRUNNER, AMConfig.roadrunnerSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.boneSerpent, biome) && AMConfig.boneSerpentSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BONE_SERPENT, AMConfig.boneSerpentSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gazelle, biome) && AMConfig.gazelleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GAZELLE, AMConfig.gazelleSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crocodile, biome) && AMConfig.crocodileSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROCODILE, AMConfig.crocodileSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.fly, biome) && AMConfig.flySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLY, AMConfig.flySpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.hummingbird, biome) && AMConfig.hummingbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.HUMMINGBIRD, AMConfig.hummingbirdSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.orca, biome) && AMConfig.orcaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ORCA, AMConfig.orcaSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.sunbird, biome) && AMConfig.sunbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SUNBIRD, AMConfig.sunbirdSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gorilla, biome) && AMConfig.gorillaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GORILLA, AMConfig.gorillaSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crimsonMosquito, biome) && AMConfig.crimsonMosquitoSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CRIMSON_MOSQUITO, AMConfig.crimsonMosquitoSpawnWeight, 4, 4));
        }
        if (testBiome(BiomeConfig.rattlesnake, biome) && AMConfig.rattlesnakeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.RATTLESNAKE, AMConfig.rattlesnakeSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.endergrade, biome) && AMConfig.endergradeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERGRADE, AMConfig.endergradeSpawnWeight, 2, 6));
        }
        if (testBiome(BiomeConfig.hammerheadShark, biome) && AMConfig.hammerheadSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.HAMMERHEAD_SHARK, AMConfig.hammerheadSharkSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.lobster, biome) && AMConfig.lobsterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.LOBSTER, AMConfig.lobsterSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.komodoDragon, biome) && AMConfig.komodoDragonSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.KOMODO_DRAGON, AMConfig.komodoDragonSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.capuchinMonkey, biome) && AMConfig.capuchinMonkeySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CAPUCHIN_MONKEY, AMConfig.capuchinMonkeySpawnWeight, 9, 16));
        }
        if (testBiome(BiomeConfig.caveCentipede, biome) && AMConfig.caveCentipedeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CENTIPEDE_HEAD, AMConfig.caveCentipedeSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.warpedToad, biome) && AMConfig.warpedToadSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_TOAD, AMConfig.warpedToadSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.moose, biome) && AMConfig.mooseSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MOOSE, AMConfig.mooseSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.mimicube, biome) && AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.raccoon, biome) && AMConfig.raccoonSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.RACCOON, AMConfig.raccoonSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.blobfish, biome) && AMConfig.blobfishSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BLOBFISH, AMConfig.blobfishSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.seal, biome) && AMConfig.sealSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAL, AMConfig.sealSpawnWeight, 3, 8));
        }
        if (testBiome(BiomeConfig.cockroach, biome) && AMConfig.cockroachSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COCKROACH, AMConfig.cockroachSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.shoebill, biome) && AMConfig.shoebillSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SHOEBILL, AMConfig.shoebillSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.elephant, biome) && AMConfig.elephantSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ELEPHANT, AMConfig.elephantSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.soulVulture, biome) && AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.snowLeopard, biome) && AMConfig.snowLeopardSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SNOW_LEOPARD, AMConfig.snowLeopardSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.spectre, biome) && AMConfig.spectreSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SPECTRE, AMConfig.spectreSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.crow, biome) && AMConfig.crowSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROW, AMConfig.crowSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.alligatorSnappingTurtle, biome) && AMConfig.alligatorSnappingTurtleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, AMConfig.alligatorSnappingTurtleSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.mungus, biome) && AMConfig.mungusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MUNGUS, AMConfig.mungusSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.mantisShrimp, biome) && AMConfig.mantisShrimpSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANTIS_SHRIMP, AMConfig.mantisShrimpSpawnWeight, 1, 4));
        }
        if (testBiome(BiomeConfig.guster, biome) && AMConfig.gusterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GUSTER, AMConfig.gusterSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.warpedMosco, biome) && AMConfig.warpedMoscoSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_MOSCO, AMConfig.warpedMoscoSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.straddler, biome) && AMConfig.straddlerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADDLER, AMConfig.straddlerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.stradpole, biome) && AMConfig.stradpoleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADPOLE, AMConfig.stradpoleSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.emu, biome) && AMConfig.emuSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.EMU, AMConfig.emuSpawnWeight, 2, 5));
        }
        if (testBiome(BiomeConfig.platypus, biome) && AMConfig.platypusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.PLATYPUS, AMConfig.platypusSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.dropbear, biome) && AMConfig.dropbearSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.DROPBEAR, AMConfig.dropbearSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.tasmanianDevil, biome) && AMConfig.tasmanianDevilSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TASMANIAN_DEVIL, AMConfig.tasmanianDevilSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.kangaroo, biome) && AMConfig.kangarooSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.KANGAROO, AMConfig.kangarooSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.cachalot_whale_spawns, biome) && AMConfig.cachalotWhaleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CACHALOT_WHALE, AMConfig.cachalotWhaleSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.leafcutter_anthill_spawns, biome) && AMConfig.leafcutterAnthillSpawnChance > 0) {
        // TODO    event.getGeneration().addFeature(GenerationStep.Decoration.RAW_GENERATION, LEAFCUTTER_ANTHILL_PF).build();
        }
        if (testBiome(BiomeConfig.enderiophage_spawns, biome) && AMConfig.enderiophageSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERIOPHAGE, AMConfig.enderiophageSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.baldEagle, biome) && AMConfig.baldEagleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BALD_EAGLE, AMConfig.baldEagleSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.tiger, biome) && AMConfig.tigerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TIGER, AMConfig.tigerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.tarantula_hawk, biome) && AMConfig.tarantulaHawkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TARANTULA_HAWK, AMConfig.tarantulaHawkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.void_worm, biome) && AMConfig.voidWormSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.VOID_WORM, AMConfig.voidWormSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.frilled_shark, biome) && AMConfig.frilledSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FRILLED_SHARK, AMConfig.frilledSharkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.mimic_octopus, biome) && AMConfig.mimicOctopusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMIC_OCTOPUS, AMConfig.mimicOctopusSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.seagull, biome) && AMConfig.seagullSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAGULL, AMConfig.seagullSpawnWeight, 3, 6));
        }
        if (testBiome(BiomeConfig.froststalker, biome) && AMConfig.froststalkerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FROSTSTALKER, AMConfig.froststalkerSpawnWeight, 5, 7));
        }
        if (testBiome(BiomeConfig.tusklin, biome) && AMConfig.tusklinSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TUSKLIN, AMConfig.tusklinSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.laviathan, biome) && AMConfig.laviathanSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.LAVIATHAN, AMConfig.laviathanSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.cosmaw, biome) && AMConfig.cosmawSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COSMAW, AMConfig.cosmawSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.toucan, biome) && AMConfig.toucanSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TOUCAN, AMConfig.toucanSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.maned_wolf, biome) && AMConfig.manedWolfSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANED_WOLF, AMConfig.manedWolfSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.anaconda, biome) && AMConfig.anacondaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANACONDA, AMConfig.anacondaSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.anteater, biome) && AMConfig.anteaterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANTEATER, AMConfig.anteaterSpawnWeight, 1, 3));
        }
    }

    public static boolean testBiome(Pair<String, SpawnBiomeData> entry, Biome biome) {
        boolean result = false;
        try {
            result = BiomeConfig.test(entry, biome);
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("could not test biome config for " + entry.getLeft() + ", defaulting to no spawns for mob");
            result = false;
        }
        return result;
    }
}
