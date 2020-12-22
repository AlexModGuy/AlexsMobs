package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class AMWorldRegistry {

    public static void onBiomesLoad(BiomeLoadingEvent event) {
        Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.grizzlyBear, biome) && AMConfig.grizzlyBearSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GRIZZLY_BEAR, AMConfig.grizzlyBearSpawnWeight, 2, 3));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.roadrunner, biome) && AMConfig.roadrunnerSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ROADRUNNER, AMConfig.roadrunnerSpawnWeight, 2, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.boneSerpent, biome) && AMConfig.boneSerpentSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.BONE_SERPENT, AMConfig.boneSerpentSpawnWeight, 1, 1));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.gazelle, biome) && AMConfig.gazelleSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GAZELLE, AMConfig.gazelleSpawnWeight, 7, 7));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.crocodile, biome) && AMConfig.crocodileSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CROCODILE, AMConfig.crocodileSpawnWeight, 1, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.fly, biome) && AMConfig.flySpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.FLY, AMConfig.flySpawnWeight, 2, 3));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.hummingbird, biome) && AMConfig.hummingbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.HUMMINGBIRD, AMConfig.hummingbirdSpawnWeight, 7, 7));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.orca, biome) && AMConfig.orcaSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ORCA, AMConfig.orcaSpawnWeight, 3, 4));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.sunbird, biome) && AMConfig.sunbirdSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SUNBIRD, AMConfig.sunbirdSpawnWeight, 1, 1));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.gorilla, biome) && AMConfig.gorillaSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GORILLA, AMConfig.gorillaSpawnWeight, 7, 7));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.crimsonMosquito, biome) && AMConfig.crimsonMosquitoSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CRIMSON_MOSQUITO, AMConfig.crimsonMosquitoSpawnWeight, 4, 4));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.rattlesnake, biome) && AMConfig.rattlesnakeSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.RATTLESNAKE, AMConfig.rattlesnakeSpawnWeight, 1, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.endergrade, biome) && AMConfig.endergradeSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ENDERGRADE, AMConfig.endergradeSpawnWeight, 2, 6));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.hammerheadShark, biome) && AMConfig.hammerheadSharkSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.HAMMERHEAD_SHARK, AMConfig.hammerheadSharkSpawnWeight, 2, 3));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.lobster, biome) && AMConfig.lobsterSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT).add(new MobSpawnInfo.Spawners(AMEntityRegistry.LOBSTER, AMConfig.lobsterSpawnWeight, 3, 5));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.komodoDragon, biome) && AMConfig.komodoDragonSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.KOMODO_DRAGON, AMConfig.komodoDragonSpawnWeight, 1, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.capuchinMonkey, biome) && AMConfig.capuchinMonkeySpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CAPUCHIN_MONKEY, AMConfig.capuchinMonkeySpawnWeight, 9, 16));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.caveCentipede, biome) && AMConfig.caveCentipedeSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CENTIPEDE_HEAD, AMConfig.caveCentipedeSpawnWeight, 1, 1));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.warpedToad, biome) && AMConfig.warpedToadSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.WARPED_TOAD, AMConfig.warpedToadSpawnWeight, 5, 5));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.moose, biome) && AMConfig.mooseSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.MOOSE, AMConfig.mooseSpawnWeight, 3, 4));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.mimicube, biome) && AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.raccoon, biome) && AMConfig.raccoonSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.RACCOON, AMConfig.raccoonSpawnWeight, 2, 4));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.blobfish, biome) && AMConfig.blobfishSpawnWeight > 0) {
            event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT).add(new MobSpawnInfo.Spawners(AMEntityRegistry.BLOBFISH, AMConfig.blobfishSpawnWeight, 2, 2));
        }
    }
}
