package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import com.github.alexthe666.citadel.server.generation.GenerationSettingsManager;
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

    public static final Feature<NoneFeatureConfiguration> LEAFCUTTER_ANTHILL = new FeatureLeafcutterAnthill(NoneFeatureConfiguration.CODEC);
    public static ConfiguredFeature<?, ?> LEAFCUTTER_ANTHILL_CF;
    public static PlacedFeature LEAFCUTTER_ANTHILL_PF;

    @SubscribeEvent
    public static void registerFeature(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(LEAFCUTTER_ANTHILL);
        LEAFCUTTER_ANTHILL_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation("alexsmobs:leafcutter_anthill"), LEAFCUTTER_ANTHILL.configured(FeatureConfiguration.NONE));
        LEAFCUTTER_ANTHILL_PF = Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation("alexsmobs:leafcutter_anthill"), LEAFCUTTER_ANTHILL_CF.placed());
    }

    public static boolean initBiomes = false;


    public static void onBiomesLoad(BiomeLoadingEvent event) {
        initBiomes = true;
        if (testBiome(BiomeConfig.grizzlyBear, event.getCategory(), event.getName()) && AMConfig.grizzlyBearSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GRIZZLY_BEAR, AMConfig.grizzlyBearSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.roadrunner, event.getCategory(), event.getName()) && AMConfig.roadrunnerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ROADRUNNER, AMConfig.roadrunnerSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.boneSerpent, event.getCategory(), event.getName()) && AMConfig.boneSerpentSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BONE_SERPENT, AMConfig.boneSerpentSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gazelle, event.getCategory(), event.getName()) && AMConfig.gazelleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GAZELLE, AMConfig.gazelleSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crocodile, event.getCategory(), event.getName()) && AMConfig.crocodileSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROCODILE, AMConfig.crocodileSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.fly, event.getCategory(), event.getName()) && AMConfig.flySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLY, AMConfig.flySpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.hummingbird, event.getCategory(), event.getName()) && AMConfig.hummingbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.HUMMINGBIRD, AMConfig.hummingbirdSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.orca, event.getCategory(), event.getName()) && AMConfig.orcaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ORCA, AMConfig.orcaSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.sunbird, event.getCategory(), event.getName()) && AMConfig.sunbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SUNBIRD, AMConfig.sunbirdSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gorilla, event.getCategory(), event.getName()) && AMConfig.gorillaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GORILLA, AMConfig.gorillaSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crimsonMosquito, event.getCategory(), event.getName()) && AMConfig.crimsonMosquitoSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CRIMSON_MOSQUITO, AMConfig.crimsonMosquitoSpawnWeight, 4, 4));
        }
        if (testBiome(BiomeConfig.rattlesnake, event.getCategory(), event.getName()) && AMConfig.rattlesnakeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.RATTLESNAKE, AMConfig.rattlesnakeSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.endergrade, event.getCategory(), event.getName()) && AMConfig.endergradeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERGRADE, AMConfig.endergradeSpawnWeight, 2, 6));
        }
        if (testBiome(BiomeConfig.hammerheadShark, event.getCategory(), event.getName()) && AMConfig.hammerheadSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.HAMMERHEAD_SHARK, AMConfig.hammerheadSharkSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.lobster, event.getCategory(), event.getName()) && AMConfig.lobsterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.LOBSTER, AMConfig.lobsterSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.komodoDragon, event.getCategory(), event.getName()) && AMConfig.komodoDragonSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.KOMODO_DRAGON, AMConfig.komodoDragonSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.capuchinMonkey, event.getCategory(), event.getName()) && AMConfig.capuchinMonkeySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CAPUCHIN_MONKEY, AMConfig.capuchinMonkeySpawnWeight, 9, 16));
        }
        if (testBiome(BiomeConfig.caveCentipede, event.getCategory(), event.getName()) && AMConfig.caveCentipedeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CENTIPEDE_HEAD, AMConfig.caveCentipedeSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.warpedToad, event.getCategory(), event.getName()) && AMConfig.warpedToadSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_TOAD, AMConfig.warpedToadSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.moose, event.getCategory(), event.getName()) && AMConfig.mooseSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MOOSE, AMConfig.mooseSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.mimicube, event.getCategory(), event.getName()) && AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.raccoon, event.getCategory(), event.getName()) && AMConfig.raccoonSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.RACCOON, AMConfig.raccoonSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.blobfish, event.getCategory(), event.getName()) && AMConfig.blobfishSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BLOBFISH, AMConfig.blobfishSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.seal, event.getCategory(), event.getName()) && AMConfig.sealSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAL, AMConfig.sealSpawnWeight, 3, 8));
        }
        if (testBiome(BiomeConfig.cockroach, event.getCategory(), event.getName()) && AMConfig.cockroachSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COCKROACH, AMConfig.cockroachSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.shoebill, event.getCategory(), event.getName()) && AMConfig.shoebillSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SHOEBILL, AMConfig.shoebillSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.elephant, event.getCategory(), event.getName()) && AMConfig.elephantSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ELEPHANT, AMConfig.elephantSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.soulVulture, event.getCategory(), event.getName()) && AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.snowLeopard, event.getCategory(), event.getName()) && AMConfig.snowLeopardSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SNOW_LEOPARD, AMConfig.snowLeopardSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.spectre, event.getCategory(), event.getName()) && AMConfig.spectreSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SPECTRE, AMConfig.spectreSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.crow, event.getCategory(), event.getName()) && AMConfig.crowSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROW, AMConfig.crowSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.alligatorSnappingTurtle, event.getCategory(), event.getName()) && AMConfig.alligatorSnappingTurtleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, AMConfig.alligatorSnappingTurtleSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.mungus, event.getCategory(), event.getName()) && AMConfig.mungusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MUNGUS, AMConfig.mungusSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.mantisShrimp, event.getCategory(), event.getName()) && AMConfig.mantisShrimpSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANTIS_SHRIMP, AMConfig.mantisShrimpSpawnWeight, 1, 4));
        }
        if (testBiome(BiomeConfig.guster, event.getCategory(), event.getName()) && AMConfig.gusterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GUSTER, AMConfig.gusterSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.warpedMosco, event.getCategory(), event.getName()) && AMConfig.warpedMoscoSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_MOSCO, AMConfig.warpedMoscoSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.straddler, event.getCategory(), event.getName()) && AMConfig.straddlerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADDLER, AMConfig.straddlerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.stradpole, event.getCategory(), event.getName()) && AMConfig.stradpoleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADPOLE, AMConfig.stradpoleSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.emu, event.getCategory(), event.getName()) && AMConfig.emuSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.EMU, AMConfig.emuSpawnWeight, 2, 5));
        }
        if (testBiome(BiomeConfig.platypus, event.getCategory(), event.getName()) && AMConfig.platypusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.PLATYPUS, AMConfig.platypusSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.dropbear, event.getCategory(), event.getName()) && AMConfig.dropbearSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.DROPBEAR, AMConfig.dropbearSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.tasmanianDevil, event.getCategory(), event.getName()) && AMConfig.tasmanianDevilSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TASMANIAN_DEVIL, AMConfig.tasmanianDevilSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.kangaroo, event.getCategory(), event.getName()) && AMConfig.kangarooSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.KANGAROO, AMConfig.kangarooSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.cachalot_whale_spawns, event.getCategory(), event.getName()) && AMConfig.cachalotWhaleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CACHALOT_WHALE, AMConfig.cachalotWhaleSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.enderiophage_spawns, event.getCategory(), event.getName()) && AMConfig.enderiophageSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERIOPHAGE, AMConfig.enderiophageSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.baldEagle, event.getCategory(), event.getName()) && AMConfig.baldEagleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BALD_EAGLE, AMConfig.baldEagleSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.tiger, event.getCategory(), event.getName()) && AMConfig.tigerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TIGER, AMConfig.tigerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.tarantula_hawk, event.getCategory(), event.getName()) && AMConfig.tarantulaHawkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TARANTULA_HAWK, AMConfig.tarantulaHawkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.void_worm, event.getCategory(), event.getName()) && AMConfig.voidWormSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.VOID_WORM, AMConfig.voidWormSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.frilled_shark, event.getCategory(), event.getName()) && AMConfig.frilledSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FRILLED_SHARK, AMConfig.frilledSharkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.mimic_octopus, event.getCategory(), event.getName()) && AMConfig.mimicOctopusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMIC_OCTOPUS, AMConfig.mimicOctopusSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.seagull, event.getCategory(), event.getName()) && AMConfig.seagullSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAGULL, AMConfig.seagullSpawnWeight, 3, 6));
        }
        if (testBiome(BiomeConfig.froststalker, event.getCategory(), event.getName()) && AMConfig.froststalkerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FROSTSTALKER, AMConfig.froststalkerSpawnWeight, 5, 7));
        }
        if (testBiome(BiomeConfig.tusklin, event.getCategory(), event.getName()) && AMConfig.tusklinSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TUSKLIN, AMConfig.tusklinSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.laviathan, event.getCategory(), event.getName()) && AMConfig.laviathanSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.LAVIATHAN, AMConfig.laviathanSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.cosmaw, event.getCategory(), event.getName()) && AMConfig.cosmawSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COSMAW, AMConfig.cosmawSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.toucan, event.getCategory(), event.getName()) && AMConfig.toucanSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TOUCAN, AMConfig.toucanSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.maned_wolf, event.getCategory(), event.getName()) && AMConfig.manedWolfSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANED_WOLF, AMConfig.manedWolfSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.anaconda, event.getCategory(), event.getName()) && AMConfig.anacondaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANACONDA, AMConfig.anacondaSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.anteater, event.getCategory(), event.getName()) && AMConfig.anteaterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANTEATER, AMConfig.anteaterSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.rocky_roller, event.getCategory(), event.getName()) && AMConfig.rockyRollerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ROCKY_ROLLER, AMConfig.rockyRollerSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.flutter, event.getCategory(), event.getName()) && AMConfig.flutterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLUTTER, AMConfig.flutterSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.gelada_monkey, event.getCategory(), event.getName()) && AMConfig.geladaMonkeySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GELADA_MONKEY, AMConfig.geladaMonkeySpawnWeight, 9, 16));
        }
        if (testBiome(BiomeConfig.leafcutter_anthill_spawns, event.getCategory(), event.getName()) && AMConfig.leafcutterAnthillSpawnChance > 0) {
            GenerationSettingsManager.register(event.getName().toString(), LEAFCUTTER_ANTHILL_PF);
        }
        if (testBiome(BiomeConfig.jerboa, event.getCategory(), event.getName()) && AMConfig.jerboaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.JERBOA, AMConfig.jerboaSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.terrapin, event.getCategory(), event.getName()) && AMConfig.terrapinSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TERRAPIN, AMConfig.terrapinSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.comb_jelly, event.getCategory(), event.getName()) && AMConfig.combJellySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COMB_JELLY, AMConfig.combJellySpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.cosmic_cod, event.getCategory(), event.getName()) && AMConfig.cosmicCodSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COSMIC_COD, AMConfig.cosmicCodSpawnWeight, 2, 3));
        }

    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> F registerFeature(String name, F eature) {
        eature.setRegistryName("alexsmobs:leafcutter_hill");
        return Registry.register(Registry.FEATURE, name, eature);
    }

    private static boolean testBiome(Pair<String, SpawnBiomeData> entry, Biome.BiomeCategory category, ResourceLocation registryName) {
        boolean result = false;
        try {
            result = BiomeConfig.test(entry, category, registryName);
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("could not test biome config for " + entry.getLeft() + ", defaulting to no spawns for mob");
            result = false;
        }
        return result;
    }
}
