package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMWorldRegistry {

    public static Feature<NoFeatureConfig> LEAFCUTTER_ANTHILL =  new FeatureLeafcutterAnthill(NoFeatureConfig.CODEC);
    public static ConfiguredFeature<NoFeatureConfig, ?> LEAFCUTTER_ANTHILL_CF;
    @SubscribeEvent
    public static void registerFeature(final RegistryEvent.Register<Feature<?>> event) {
         Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "alexsmobs:leafcutter_hill", LEAFCUTTER_ANTHILL_CF = LEAFCUTTER_ANTHILL.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        event.getRegistry().register(LEAFCUTTER_ANTHILL.setRegistryName("alexsmobs:leafcutter_hill"));
    }

    public static void onBiomesLoad(BiomeLoadingEvent event) {
        Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());
        if (testBiome(BiomeConfig.grizzlyBear, biome) && AMConfig.grizzlyBearSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GRIZZLY_BEAR, AMConfig.grizzlyBearSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.roadrunner, biome) && AMConfig.roadrunnerSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ROADRUNNER, AMConfig.roadrunnerSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.boneSerpent, biome) && AMConfig.boneSerpentSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.BONE_SERPENT, AMConfig.boneSerpentSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gazelle, biome) && AMConfig.gazelleSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GAZELLE, AMConfig.gazelleSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crocodile, biome) && AMConfig.crocodileSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CROCODILE, AMConfig.crocodileSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.fly, biome) && AMConfig.flySpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.AMBIENT).add(new MobSpawnInfo.Spawners(AMEntityRegistry.FLY, AMConfig.flySpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.hummingbird, biome) && AMConfig.hummingbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.HUMMINGBIRD, AMConfig.hummingbirdSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.orca, biome) && AMConfig.orcaSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ORCA, AMConfig.orcaSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.sunbird, biome) && AMConfig.sunbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SUNBIRD, AMConfig.sunbirdSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.gorilla, biome) && AMConfig.gorillaSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GORILLA, AMConfig.gorillaSpawnWeight, 7, 7));
        }
        if (testBiome(BiomeConfig.crimsonMosquito, biome) && AMConfig.crimsonMosquitoSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CRIMSON_MOSQUITO, AMConfig.crimsonMosquitoSpawnWeight, 4, 4));
        }
        if (testBiome(BiomeConfig.rattlesnake, biome) && AMConfig.rattlesnakeSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.RATTLESNAKE, AMConfig.rattlesnakeSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.endergrade, biome) && AMConfig.endergradeSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ENDERGRADE, AMConfig.endergradeSpawnWeight, 2, 6));
        }
        if (testBiome(BiomeConfig.hammerheadShark, biome) && AMConfig.hammerheadSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.HAMMERHEAD_SHARK, AMConfig.hammerheadSharkSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.lobster, biome) && AMConfig.lobsterSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT).add(new MobSpawnInfo.Spawners(AMEntityRegistry.LOBSTER, AMConfig.lobsterSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.komodoDragon, biome) && AMConfig.komodoDragonSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.KOMODO_DRAGON, AMConfig.komodoDragonSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.capuchinMonkey, biome) && AMConfig.capuchinMonkeySpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CAPUCHIN_MONKEY, AMConfig.capuchinMonkeySpawnWeight, 9, 16));
        }
        if (testBiome(BiomeConfig.caveCentipede, biome) && AMConfig.caveCentipedeSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CENTIPEDE_HEAD, AMConfig.caveCentipedeSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.warpedToad, biome) && AMConfig.warpedToadSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.WARPED_TOAD, AMConfig.warpedToadSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.moose, biome) && AMConfig.mooseSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.MOOSE, AMConfig.mooseSpawnWeight, 3, 4));
        }
        if (testBiome(BiomeConfig.mimicube, biome) && AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.raccoon, biome) && AMConfig.raccoonSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.RACCOON, AMConfig.raccoonSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.blobfish, biome) && AMConfig.blobfishSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT).add(new MobSpawnInfo.Spawners(AMEntityRegistry.BLOBFISH, AMConfig.blobfishSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.seal, biome) && AMConfig.sealSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SEAL, AMConfig.sealSpawnWeight, 3, 8));
        }
        if (testBiome(BiomeConfig.cockroach, biome) && AMConfig.cockroachSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.AMBIENT).add(new MobSpawnInfo.Spawners(AMEntityRegistry.COCKROACH, AMConfig.cockroachSpawnWeight, 5, 5));
        }
        if (testBiome(BiomeConfig.shoebill, biome) && AMConfig.shoebillSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SHOEBILL, AMConfig.shoebillSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.elephant, biome) && AMConfig.elephantSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ELEPHANT, AMConfig.elephantSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.soulVulture, biome) && AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 2, 3));
        }
        if (testBiome(BiomeConfig.snowLeopard, biome) && AMConfig.snowLeopardSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SNOW_LEOPARD, AMConfig.snowLeopardSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.spectre, biome) && AMConfig.spectreSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SPECTRE, AMConfig.spectreSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.crow, biome) && AMConfig.crowSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CROW, AMConfig.crowSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.alligatorSnappingTurtle, biome) && AMConfig.alligatorSnappingTurtleSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, AMConfig.alligatorSnappingTurtleSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.mungus, biome) && AMConfig.mungusSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.MUNGUS, AMConfig.mungusSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.mantisShrimp, biome) && AMConfig.mantisShrimpSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.MANTIS_SHRIMP, AMConfig.mantisShrimpSpawnWeight, 1, 4));
        }
        if (testBiome(BiomeConfig.guster, biome) && AMConfig.gusterSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GUSTER, AMConfig.gusterSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.warpedMosco, biome) && AMConfig.warpedMoscoSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.WARPED_MOSCO, AMConfig.warpedMoscoSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.straddler, biome) && AMConfig.straddlerSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.STRADDLER, AMConfig.straddlerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.stradpole, biome) && AMConfig.stradpoleSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.STRADPOLE, AMConfig.stradpoleSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.emu, biome) && AMConfig.emuSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.EMU, AMConfig.emuSpawnWeight, 2, 5));
        }
        if (testBiome(BiomeConfig.platypus, biome) && AMConfig.platypusSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.PLATYPUS, AMConfig.platypusSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.dropbear, biome) && AMConfig.dropbearSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.DROPBEAR, AMConfig.dropbearSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.tasmanianDevil, biome) && AMConfig.tasmanianDevilSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.TASMANIAN_DEVIL, AMConfig.tasmanianDevilSpawnWeight, 1, 2));
        }
        if (testBiome(BiomeConfig.kangaroo, biome) && AMConfig.kangarooSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.KANGAROO, AMConfig.kangarooSpawnWeight, 3, 5));
        }
        if (testBiome(BiomeConfig.cachalot_whale_spawns, biome) && AMConfig.cachalotWhaleSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CACHALOT_WHALE, AMConfig.cachalotWhaleSpawnWeight, 1, 3));
        }
        if(testBiome(BiomeConfig.leafcutter_anthill_spawns, biome) && AMConfig.leafcutterAnthillSpawnChance > 0){
            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, LEAFCUTTER_ANTHILL_CF);
        }
        if (testBiome(BiomeConfig.enderiophage_spawns, biome) && AMConfig.enderiophageSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ENDERIOPHAGE, AMConfig.enderiophageSpawnWeight, 2, 2));
        }
        if (testBiome(BiomeConfig.baldEagle, biome) && AMConfig.baldEagleSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.BALD_EAGLE, AMConfig.baldEagleSpawnWeight, 2, 4));
        }
        if (testBiome(BiomeConfig.tiger, biome) && AMConfig.tigerSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.TIGER, AMConfig.tigerSpawnWeight, 1, 3));
        }
        if (testBiome(BiomeConfig.tarantula_hawk, biome) && AMConfig.tarantulaHawkSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.TARANTULA_HAWK, AMConfig.tarantulaHawkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.void_worm, biome) && AMConfig.voidWormSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.VOID_WORM, AMConfig.voidWormSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.frilled_shark, biome) && AMConfig.frilledSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.FRILLED_SHARK, AMConfig.frilledSharkSpawnWeight, 1, 1));
        }
        if (testBiome(BiomeConfig.mimic_octopus, biome) && AMConfig.mimicOctopusSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.MIMIC_OCTOPUS, AMConfig.mimicOctopusSpawnWeight, 1, 2));
        }
    }

    private static boolean testBiome(Pair<String, SpawnBiomeData> entry, Biome biome){
        boolean result = false;
        try {
            result = BiomeConfig.test(entry, biome);
        }catch (Exception e){
            AlexsMobs.LOGGER.warn("could not test biome config for " + entry.getLeft() + ", defaulting to no spawns for mob");
            result = false;
        }
        return result;
    }
}
