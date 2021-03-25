package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.citadel.config.biome.BiomeEntryType;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;

public class DefaultBiomes {

    public static final SpawnBiomeData EMPTY = new SpawnBiomeData();

    public static final SpawnBiomeData GRIZZLY_BEAR = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 1);

    public static final SpawnBiomeData ROADRUNNER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "desert", 1);

    public static final SpawnBiomeData BONE_SERPENT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "nether", 0);

    public static final SpawnBiomeData GAZELLE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 1);

    public static final SpawnBiomeData CROCODILE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "river", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 1);

    public static final SpawnBiomeData FLY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, true, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "ocean", 0)

    public static final SpawnBiomeData HUMMINGBIRD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:flower_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:sunflower_plains", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 2);

    public static final SpawnBiomeData ORCA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "cold", 0);

    public static final SpawnBiomeData SUNBIRD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountain", 0);

    public static final SpawnBiomeData GORILLA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle_hills", 0);

    public static final SpawnBiomeData CRIMSON_MOSQUITO = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:crimson_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:crimson_garden", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:visceral_heap", 2);
    
    public static final SpawnBiomeData RATTLESNAKE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "desert", 1);

    public static final SpawnBiomeData ENDERGRADE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0);

    public static final SpawnBiomeData HAMMERHEAD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 0);

    public static final SpawnBiomeData LOBSTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "beach", 0);

    public static final SpawnBiomeData KOMODO_DRAGON = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0);

    public static final SpawnBiomeData CAPUCHIN_MONKEY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle_hills", 0);

    public static final SpawnBiomeData CENTIPEDE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "mushroom", 0);

    public static final SpawnBiomeData WARPED_TOAD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:warped_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:crimson_garden", 1);
                
    public static final SpawnBiomeData MOOSE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "wasteland", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 1);

    public static final SpawnBiomeData MIMICUBE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0);

    public static final SpawnBiomeData RACCOON = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "plains", 1);

    public static final SpawnBiomeData BLOBFISH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_lukewarm_ocean", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_cold_ocean", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_frozen_ocean", 3);

    public static final SpawnBiomeData SEAL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "beach", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 1);

    public static final SpawnBiomeData COCKROACH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "mushroom", 0);

    public static final SpawnBiomeData SHOEBILL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0);

    public static final SpawnBiomeData ELEPHANT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 1);

    public static final SpawnBiomeData SOUL_VULTURE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:soul_sand_valley", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:warped_desert", 1);
    
    public static final SpawnBiomeData SPECTRE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:small_end_islands", 0);

    public static final SpawnBiomeData SNOW_LEOPARD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountain", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 0);

    public static final SpawnBiomeData CROW = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "plains", 1);

    public static final SpawnBiomeData ALLIGATOR_SNAPPING_TURTLE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0);

    public static final SpawnBiomeData MUNGUS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mushroom", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "rare", 0);

    public static final SpawnBiomeData MANTIS_SHRIMP = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 0);

    public static final SpawnBiomeData GUSTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "dry", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "sandy", 0);

    public static final SpawnBiomeData STRADDLER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:basalt_deltas", 0);

    public static final SpawnBiomeData SAVANNA_AND_MESA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 1);

    public static final SpawnBiomeData PLATYPUS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "river", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 0);

    public static final SpawnBiomeData DROPBEAR = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:nether_wastes", 0);

    public static final SpawnBiomeData TASMANIAN_DEVIL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 0);
}
