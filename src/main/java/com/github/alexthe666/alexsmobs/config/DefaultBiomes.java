package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.citadel.config.biome.BiomeEntryType;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;

public class DefaultBiomes {

    public static final SpawnBiomeData EMPTY = new SpawnBiomeData();

    public static final SpawnBiomeData GRIZZLY_BEAR = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:sparse_jungle", 1);

    public static final SpawnBiomeData ROADRUNNER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "dry", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "sandy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "desert", 2);

    public static final SpawnBiomeData ALL_NETHER = new SpawnBiomeData()
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
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "ocean", 0);

    public static final SpawnBiomeData HUMMINGBIRD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:flower_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:sunflower_plains", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:meadow", 3);

    public static final SpawnBiomeData ORCA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "cold", 0);

    public static final SpawnBiomeData SUNBIRD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountain", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:snowy_slopes", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_peaks", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:jagged_peaks", 3);

    public static final SpawnBiomeData GORILLA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0);

    public static final SpawnBiomeData CRIMSON_MOSQUITO = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:crimson_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:crimson_gardens", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:visceral_heap", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:ash_barrens", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:infernal_dunes", 4);

    public static final SpawnBiomeData RATTLESNAKE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "dry", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "sandy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "desert", 2);

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
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "dense", 0);

    public static final SpawnBiomeData CAPUCHIN_MONKEY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0);

    public static final SpawnBiomeData CENTIPEDE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "mushroom", 0);

    public static final SpawnBiomeData WARPED_TOAD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:warped_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:crimson_gardens", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:warped_desert", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:inverted_forest", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:quartz_flats", 4);
        
    public static final SpawnBiomeData MOOSE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "wasteland", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowy_coniferous_forest", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowy_fir_clearing", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "snowy_maple_woods", 4);
    

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

    public static final SpawnBiomeData DEEP_SEA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_lukewarm_ocean", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_cold_ocean", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_frozen_ocean", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:ocean", 4);


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
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0)

    public static final SpawnBiomeData ELEPHANT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 1);

    public static final SpawnBiomeData SOUL_VULTURE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:soul_sand_valley", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:warped_desert", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:weeping_valley", 2);

    public static final SpawnBiomeData SPECTRE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0);

    public static final SpawnBiomeData SNOW_LEOPARD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountain", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:snowy_slopes", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_peaks", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:jagged_peaks", 3);


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
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:basalt_deltas", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:withered_abyss", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:volcanic_deltas", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:withered_forest", 3);

    public static final SpawnBiomeData SAVANNA_AND_MESA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 1);

    public static final SpawnBiomeData ICE_FREE_RIVER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "river", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:tundra_bog", 1);

    public static final SpawnBiomeData DROPBEAR = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:nether_wastes", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:crystalline_chasm", 1);

    public static final SpawnBiomeData TASMANIAN_DEVIL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:sparse_jungle", 0);

    public static final SpawnBiomeData CACHALOT_WHALE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "cold", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:lukewarm_ocean", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_ocean", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_lukewarm_ocean", 3);

    public static final SpawnBiomeData BEACHED_CACHALOT_WHALE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "beach", 0);

    public static final SpawnBiomeData LEAFCUTTER_ANTHILL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0);

    public static final SpawnBiomeData ENDERIOPHAGE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:end_barrens", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:end_highlands", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:small_end_islands", 0);

    public static final SpawnBiomeData BALD_EAGLE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountain", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:grove", 1);

    public static final SpawnBiomeData TIGER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:bamboo_grove", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:cherry_blossom_grove", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 4);

    public static final SpawnBiomeData DESERT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "desert", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "dry", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "sandy", 1);
    
    public static final SpawnBiomeData MIMIC_OCTOPUS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_warm_ocean", 0);

    public static final SpawnBiomeData SEAGULL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "beach", 0);

    public static final SpawnBiomeData FROSTSTALKER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:ice_spikes", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_peaks", 1);

    public static final SpawnBiomeData TUSKLIN = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:ice_spikes", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "forest", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "mountain", 1);


    public static final SpawnBiomeData COSMAW = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:small_end_islands", 0);

    public static final SpawnBiomeData TOUCAN = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0);

    public static final SpawnBiomeData MANED_WOLF = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 1);

    public static final SpawnBiomeData ANACONDA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0);

    public static final SpawnBiomeData ANTEATER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0);

    public static final SpawnBiomeData ROCKY_ROLLER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:dripstone_caves", 0);

    public static final SpawnBiomeData FLUTTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:lush_caves", 0);

    public static final SpawnBiomeData MEADOWS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "plains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "plateau", 0);
        
    public static final SpawnBiomeData COMB_JELLY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_frozen_ocean", 1);

    public static final SpawnBiomeData COSMIC_COD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "end", 0);

    public static final SpawnBiomeData BISON = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "plains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "hot", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:meadow", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:field", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:forested_field", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:grassland", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:pasture", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:prairie", 6);

    public static final SpawnBiomeData GIANT_SQUID = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_lukewarm_ocean", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_cold_ocean", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_frozen_ocean", 3);

    public static final SpawnBiomeData ALL_OVERWORLD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0);

    public static final SpawnBiomeData CATFISH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "river", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 1);

    public static final SpawnBiomeData LARGE_CATFISH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0);

    public static final SpawnBiomeData FLYING_FISH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "hot", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_lukewarm_ocean", 0);

    public static final SpawnBiomeData SKELEWAG = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 0);
}
