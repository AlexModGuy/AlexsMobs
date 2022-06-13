package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMWorldRegistry {

    public static boolean initBiomes = false;

    /*
    public static void onBiomesLoad(BiomeLoadingEvent event) {
        initBiomes = true;
        if (testBiome(BiomeConfig.grizzlyBear, event.getCategory(), event.getName()) && AMConfig.grizzlyBearSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GRIZZLY_BEAR.get(), AMConfig.grizzlyBearSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.roadrunner, event.getCategory(), event.getName()) && AMConfig.roadrunnerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ROADRUNNER.get(), AMConfig.roadrunnerSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.boneSerpent, event.getCategory(), event.getName()) && AMConfig.boneSerpentSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BONE_SERPENT.get(), AMConfig.boneSerpentSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gazelle, event.getCategory(), event.getName()) && AMConfig.gazelleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GAZELLE.get(), AMConfig.gazelleSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crocodile, event.getCategory(), event.getName()) && AMConfig.crocodileSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROCODILE.get(), AMConfig.crocodileSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.fly, event.getCategory(), event.getName()) && AMConfig.flySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLY.get(), AMConfig.flySpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.hummingbird, event.getCategory(), event.getName()) && AMConfig.hummingbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.HUMMINGBIRD.get(), AMConfig.hummingbirdSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.orca, event.getCategory(), event.getName()) && AMConfig.orcaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ORCA.get(), AMConfig.orcaSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.sunbird, event.getCategory(), event.getName()) && AMConfig.sunbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SUNBIRD.get(), AMConfig.sunbirdSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gorilla, event.getCategory(), event.getName()) && AMConfig.gorillaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GORILLA.get(), AMConfig.gorillaSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crimsonMosquito, event.getCategory(), event.getName()) && AMConfig.crimsonMosquitoSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CRIMSON_MOSQUITO.get(), AMConfig.crimsonMosquitoSpawnWeight, 4, 4));
        }
        if (testBiome(BiomeConfig.rattlesnake, event.getCategory(), event.getName()) && AMConfig.rattlesnakeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.RATTLESNAKE.get(), AMConfig.rattlesnakeSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.endergrade, event.getCategory(), event.getName()) && AMConfig.endergradeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERGRADE.get(), AMConfig.endergradeSpawnWeight, 2, 6));
        }
        if (testBiome(BiomeConfig.hammerheadShark, event.getCategory(), event.getName()) && AMConfig.hammerheadSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.HAMMERHEAD_SHARK.get(), AMConfig.hammerheadSharkSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.lobster, event.getCategory(), event.getName()) && AMConfig.lobsterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.LOBSTER.get(), AMConfig.lobsterSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.komodoDragon, event.getCategory(), event.getName()) && AMConfig.komodoDragonSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.KOMODO_DRAGON.get(), AMConfig.komodoDragonSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.capuchinMonkey, event.getCategory(), event.getName()) && AMConfig.capuchinMonkeySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CAPUCHIN_MONKEY.get(), AMConfig.capuchinMonkeySpawnWeight, 9, 16));
        }
        if (testBiome(BiomeConfig.caveCentipede, event.getCategory(), event.getName()) && AMConfig.caveCentipedeSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CENTIPEDE_HEAD.get(), AMConfig.caveCentipedeSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.warpedToad, event.getCategory(), event.getName()) && AMConfig.warpedToadSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_TOAD.get(), AMConfig.warpedToadSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.moose, event.getCategory(), event.getName()) && AMConfig.mooseSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MOOSE.get(), AMConfig.mooseSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.mimicube, event.getCategory(), event.getName()) && AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMICUBE.get(), AMConfig.mimicubeSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.raccoon, event.getCategory(), event.getName()) && AMConfig.raccoonSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.RACCOON.get(), AMConfig.raccoonSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.blobfish, event.getCategory(), event.getName()) && AMConfig.blobfishSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BLOBFISH.get(), AMConfig.blobfishSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.seal, event.getCategory(), event.getName()) && AMConfig.sealSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAL.get(), AMConfig.sealSpawnWeight, 3, 8));
        }
        if (testBiome(BiomeConfig.cockroach, event.getCategory(), event.getName()) && AMConfig.cockroachSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COCKROACH.get(), AMConfig.cockroachSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.shoebill, event.getCategory(), event.getName()) && AMConfig.shoebillSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SHOEBILL.get(), AMConfig.shoebillSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.elephant, event.getCategory(), event.getName()) && AMConfig.elephantSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ELEPHANT.get(), AMConfig.elephantSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.soulVulture, event.getCategory(), event.getName()) && AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SOUL_VULTURE.get(), AMConfig.soulVultureSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.snowLeopard, event.getCategory(), event.getName()) && AMConfig.snowLeopardSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SNOW_LEOPARD.get(), AMConfig.snowLeopardSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.spectre, event.getCategory(), event.getName()) && AMConfig.spectreSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SPECTRE.get(), AMConfig.spectreSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.crow, event.getCategory(), event.getName()) && AMConfig.crowSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CROW.get(), AMConfig.crowSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.alligatorSnappingTurtle, event.getCategory(), event.getName()) && AMConfig.alligatorSnappingTurtleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(), AMConfig.alligatorSnappingTurtleSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.mungus, event.getCategory(), event.getName()) && AMConfig.mungusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MUNGUS.get(), AMConfig.mungusSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.mantisShrimp, event.getCategory(), event.getName()) && AMConfig.mantisShrimpSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANTIS_SHRIMP.get(), AMConfig.mantisShrimpSpawnWeight, 1, 4));
        }
        if (testBiome(BiomeConfig.guster, event.getCategory(), event.getName()) && AMConfig.gusterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GUSTER.get(), AMConfig.gusterSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.warpedMosco, event.getCategory(), event.getName()) && AMConfig.warpedMoscoSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.WARPED_MOSCO.get(), AMConfig.warpedMoscoSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.straddler, event.getCategory(), event.getName()) && AMConfig.straddlerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADDLER.get(), AMConfig.straddlerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.stradpole, event.getCategory(), event.getName()) && AMConfig.stradpoleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.STRADPOLE.get(), AMConfig.stradpoleSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.emu, event.getCategory(), event.getName()) && AMConfig.emuSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.EMU.get(), AMConfig.emuSpawnWeight, 2, 5));
        }
        if (testBiome(BiomeConfig.platypus, event.getCategory(), event.getName()) && AMConfig.platypusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.PLATYPUS.get(), AMConfig.platypusSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.dropbear, event.getCategory(), event.getName()) && AMConfig.dropbearSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.DROPBEAR.get(), AMConfig.dropbearSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.tasmanianDevil, event.getCategory(), event.getName()) && AMConfig.tasmanianDevilSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TASMANIAN_DEVIL.get(), AMConfig.tasmanianDevilSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.kangaroo, event.getCategory(), event.getName()) && AMConfig.kangarooSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.KANGAROO.get(), AMConfig.kangarooSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.cachalot_whale_spawns, event.getCategory(), event.getName()) && AMConfig.cachalotWhaleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CACHALOT_WHALE.get(), AMConfig.cachalotWhaleSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.enderiophage_spawns, event.getCategory(), event.getName()) && AMConfig.enderiophageSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ENDERIOPHAGE.get(), AMConfig.enderiophageSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.baldEagle, event.getCategory(), event.getName()) && AMConfig.baldEagleSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BALD_EAGLE.get(), AMConfig.baldEagleSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.tiger, event.getCategory(), event.getName()) && AMConfig.tigerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TIGER.get(), AMConfig.tigerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.tarantula_hawk, event.getCategory(), event.getName()) && AMConfig.tarantulaHawkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TARANTULA_HAWK.get(), AMConfig.tarantulaHawkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.void_worm, event.getCategory(), event.getName()) && AMConfig.voidWormSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.VOID_WORM.get(), AMConfig.voidWormSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.frilled_shark, event.getCategory(), event.getName()) && AMConfig.frilledSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FRILLED_SHARK.get(), AMConfig.frilledSharkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.mimic_octopus, event.getCategory(), event.getName()) && AMConfig.mimicOctopusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMIC_OCTOPUS.get(), AMConfig.mimicOctopusSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.seagull, event.getCategory(), event.getName()) && AMConfig.seagullSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SEAGULL.get(), AMConfig.seagullSpawnWeight, 3, 6));
        }
        if (testBiome(BiomeConfig.froststalker, event.getCategory(), event.getName()) && AMConfig.froststalkerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FROSTSTALKER.get(), AMConfig.froststalkerSpawnWeight, 5, 7));
        }
        if (testBiome(BiomeConfig.tusklin, event.getCategory(), event.getName()) && AMConfig.tusklinSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TUSKLIN.get(), AMConfig.tusklinSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.laviathan, event.getCategory(), event.getName()) && AMConfig.laviathanSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.LAVIATHAN.get(), AMConfig.laviathanSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.cosmaw, event.getCategory(), event.getName()) && AMConfig.cosmawSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COSMAW.get(), AMConfig.cosmawSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.toucan, event.getCategory(), event.getName()) && AMConfig.toucanSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TOUCAN.get(), AMConfig.toucanSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.maned_wolf, event.getCategory(), event.getName()) && AMConfig.manedWolfSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.MANED_WOLF.get(), AMConfig.manedWolfSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.anaconda, event.getCategory(), event.getName()) && AMConfig.anacondaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANACONDA.get(), AMConfig.anacondaSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.anteater, event.getCategory(), event.getName()) && AMConfig.anteaterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ANTEATER.get(), AMConfig.anteaterSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.rocky_roller, event.getCategory(), event.getName()) && AMConfig.rockyRollerSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.ROCKY_ROLLER.get(), AMConfig.rockyRollerSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.flutter, event.getCategory(), event.getName()) && AMConfig.flutterSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLUTTER.get(), AMConfig.flutterSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.gelada_monkey, event.getCategory(), event.getName()) && AMConfig.geladaMonkeySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GELADA_MONKEY.get(), AMConfig.geladaMonkeySpawnWeight, 9, 16));
        }
        if (testBiome(BiomeConfig.leafcutter_anthill_spawns, event.getCategory(), event.getName()) && AMConfig.leafcutterAnthillSpawnChance > 0) {
            event.getGeneration().getFeatures(GenerationStep.Decoration.SURFACE_STRUCTURES).add(AMConfiguredFeatures.PLACED_ANTHILL);
        }
        if (testBiome(BiomeConfig.jerboa, event.getCategory(), event.getName()) && AMConfig.jerboaSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.JERBOA.get(), AMConfig.jerboaSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.terrapin, event.getCategory(), event.getName()) && AMConfig.terrapinSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.TERRAPIN.get(), AMConfig.terrapinSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.comb_jelly, event.getCategory(), event.getName()) && AMConfig.combJellySpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COMB_JELLY.get(), AMConfig.combJellySpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.cosmic_cod, event.getCategory(), event.getName()) && AMConfig.cosmicCodSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.COSMIC_COD.get(), AMConfig.cosmicCodSpawnWeight, 9, 13));
        }
        if (testBiome(BiomeConfig.bunfungus, event.getCategory(), event.getName()) && AMConfig.bunfungusSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BUNFUNGUS.get(), AMConfig.bunfungusSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.bison, event.getCategory(), event.getName()) && AMConfig.bisonSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.BISON.get(), AMConfig.bisonSpawnWeight, 6, 10));
        }
        if (testBiome(BiomeConfig.giant_squid, event.getCategory(), event.getName()) && AMConfig.giantSquidSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_CREATURE).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.GIANT_SQUID.get(), AMConfig.giantSquidSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.devils_hole_pupfish, event.getCategory(), event.getName()) && AMConfig.devilsHolePupfishSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(), AMConfig.devilsHolePupfishSpawnWeight, 5, 12));
        }
        if (testBiome(BiomeConfig.catfish, event.getCategory(), event.getName()) && AMConfig.catfishSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.CATFISH.get(), AMConfig.catfishSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.flying_fish, event.getCategory(), event.getName()) && AMConfig.flyingFishSpawnWeight > 0) {
            event.getSpawns().getSpawner(MobCategory.WATER_AMBIENT).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.FLYING_FISH.get(), AMConfig.flyingFishSpawnWeight, 3, 6));
        }
        if (testBiome(BiomeConfig.skelewag, event.getCategory(), event.getName()) && AMConfig.skelewagSpawnWeight > 0 && !AMConfig.restrictSkelewagSpawns) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SKELEWAG.get(), AMConfig.skelewagSpawnWeight, 2, 3));
        }
    }*/

    public static void addStructureSpawns(MinecraftServer server){
        Registry<Structure> registry = server.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
        Structure endCity = registry.get(BuiltinStructures.END_CITY);
        Structure netherFossil = registry.get(BuiltinStructures.NETHER_FOSSIL);
        Structure shipwreck = registry.get(BuiltinStructures.SHIPWRECK);
        if (endCity != null && AMConfig.mimicubeSpawnInEndCity && AMConfig.mimicubeSpawnWeight > 0) {
            addSpawnToStructure(endCity, StructureSpawnOverride.BoundingBoxType.PIECE, MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMICUBE.get(), AMConfig.mimicubeSpawnWeight, 1, 3));
        }
        if (netherFossil != null && AMConfig.soulVultureSpawnOnFossil && AMConfig.soulVultureSpawnWeight > 0) {
            addSpawnToStructure(netherFossil, StructureSpawnOverride.BoundingBoxType.PIECE, MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SOUL_VULTURE.get(), AMConfig.soulVultureSpawnWeight, 1, 1));
        }
        if (shipwreck != null && AMConfig.restrictSkelewagSpawns && AMConfig.skelewagSpawnWeight > 0) {
            addSpawnToStructure(shipwreck, StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SKELEWAG.get(), AMConfig.skelewagSpawnWeight, 1, 2));
        }
    }

    private static void addSpawnToStructure(Structure feature, StructureSpawnOverride.BoundingBoxType bbType, MobCategory category, MobSpawnSettings.SpawnerData spawn){
        if(feature.settings.spawnOverrides().isEmpty() || feature.settings.spawnOverrides().get(category) == null){
            WeightedRandomList<MobSpawnSettings.SpawnerData> spawns = WeightedRandomList.create(spawn);
            StructureSpawnOverride override = new StructureSpawnOverride(bbType, spawns);
            HashMap<MobCategory, StructureSpawnOverride> newMap = new HashMap<>(feature.settings.spawnOverrides());
            newMap.put(category, override);
            feature.settings = new Structure.StructureSettings(feature.biomes(), ImmutableMap.copyOf(newMap), feature.step(), feature.terrainAdaptation());
        }else{
            StructureSpawnOverride previous = (StructureSpawnOverride) feature.settings.spawnOverrides().get(category);
            List<MobSpawnSettings.SpawnerData> l = new ArrayList<>(previous.spawns().unwrap());
            boolean contained = false;
            for(MobSpawnSettings.SpawnerData data : l){
                if(data.type.equals(spawn.type)){
                    contained = true;
                }
            }
            WeightedRandomList<MobSpawnSettings.SpawnerData> spawns = WeightedRandomList.create(l);
            StructureSpawnOverride override = new StructureSpawnOverride(previous.boundingBox(), spawns);
            HashMap<MobCategory, StructureSpawnOverride> newMap = new HashMap<>(feature.settings.spawnOverrides());
            if(!contained){
                l.add(spawn);
            }
            newMap.put(category, override);
            feature.settings = new Structure.StructureSettings(feature.biomes(), ImmutableMap.copyOf(newMap), feature.step(), feature.terrainAdaptation());
        }
    }

    public static boolean testBiome(Pair<String, SpawnBiomeData> entry, Holder<Biome> biome, ResourceLocation registryName) {
        boolean result = false;
        try {
            result = BiomeConfig.test(entry, biome, registryName);
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("could not test biome config for " + entry.getLeft() + ", defaulting to no spawns for mob");
            result = false;
        }
        return result;
    }

}
