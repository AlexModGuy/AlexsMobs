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
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.grizzlyBear, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GRIZZLY_BEAR, AMConfig.grizzlyBearSpawnWeight, 2, 3));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.roadrunner, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ROADRUNNER, AMConfig.roadrunnerSpawnWeight, 2, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.boneSerpent, biome)) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.BONE_SERPENT, AMConfig.boneSerpentSpawnWeight, 1, 1));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.gazelle, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GAZELLE, AMConfig.gazelleSpawnWeight, 7, 7));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.crocodile, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CROCODILE, AMConfig.crocodileSpawnWeight, 1, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.fly, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.FLY, AMConfig.flySpawnWeight, 2, 3));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.hummingbird, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.HUMMINGBIRD, AMConfig.hummingbirdSpawnWeight, 7, 7));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.orca, biome)) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ORCA, AMConfig.orcaSpawnWeight, 3, 4));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.sunbird, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.SUNBIRD, AMConfig.sunbirdSpawnWeight, 1, 1));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.gorilla, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.GORILLA, AMConfig.gorillaSpawnWeight, 7, 7));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.crimsonMosquito, biome)) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CRIMSON_MOSQUITO, AMConfig.crimsonMosquitoSpawnWeight, 4, 4));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.rattlesnake, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.RATTLESNAKE, AMConfig.rattlesnakeSpawnWeight, 1, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.endergrade, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.ENDERGRADE, AMConfig.endergradeSpawnWeight, 2, 6));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.hammerheadShark, biome)) {
            event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.HAMMERHEAD_SHARK, AMConfig.hammerheadSharkSpawnWeight, 2, 3));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.lobster, biome)) {
            event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT).add(new MobSpawnInfo.Spawners(AMEntityRegistry.LOBSTER, AMConfig.lobsterSpawnWeight, 3, 5));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.komodoDragon, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.KOMODO_DRAGON, AMConfig.komodoDragonSpawnWeight, 1, 2));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.capuchinMonkey, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CAPUCHIN_MONKEY, AMConfig.capuchinMonkeySpawnWeight, 8, 16));
        }
        if (AMBiomeUtil.parseListForBiomeCheck(BiomeConfig.caveCentipede, biome)) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(AMEntityRegistry.CENTIPEDE_HEAD, AMConfig.caveCentipedeSpawnWeight, 1, 1));
        }
    }
}
