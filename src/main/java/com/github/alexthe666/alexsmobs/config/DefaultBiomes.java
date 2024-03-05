package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.citadel.config.biome.BiomeEntryType;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;

public class DefaultBiomes {

    public static final SpawnBiomeData EMPTY = new SpawnBiomeData();

    public static final SpawnBiomeData ALL_FOREST = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:sparse_jungle", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_taiga", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:alpine_grove", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_forest", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_valley", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_grove", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_valley", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:highlands", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shield_clearing", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:valley_clearing", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:alpine_highlands", 13)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cloud_forest", 14)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:forested_highlands", 15)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lush_valley", 16)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shield", 17)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_maple_forest", 18)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_shield", 19)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:temperate_highlands", 20)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:wintry_forest", 21)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:yosemite_lowlands", 22)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:bryce_canyon", 23)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 24)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:redwood_forest", 25)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:cherry_grove", 26)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowblossom_grove", 27);


    public static final SpawnBiomeData ROADRUNNER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_badlands", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_dry/overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_hot/overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_sandy", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ancient_sands", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_canyon", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_oasis", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_spires", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sandstone_valley", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:warped_mesa", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:white_mesa", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 10);

    public static final SpawnBiomeData ALL_NETHER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_nether", 0);

    public static final SpawnBiomeData ALL_NETHER_MONSTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_nether", 0);

    public static final SpawnBiomeData GAZELLE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:arid_highlands", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:brushland", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:fractured_savanna", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_badlands", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_slopes", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shrubland", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 7);

    public static final SpawnBiomeData CROCODILE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:mangrove_swamp", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_river", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_cold", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:tropic_beach", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:orchid_swamp", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:warm_river", 6);

    public static final SpawnBiomeData FLY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0);

    public static final SpawnBiomeData HUMMINGBIRD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:flower_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:sunflower_plains", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_jungle", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:meadow", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_forest", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_valley", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_grove", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_valley", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_canyon", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:jungle_mountains", 13)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_jungle", 14)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:tropical_jungle", 15)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_plateau", 16)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:valley_clearing", 17)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:orchid_swamp", 18)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 19)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_spring", 20)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 21)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:cherry_grove", 22);

    public static final SpawnBiomeData ORCA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_cold/overworld", 0);

    public static final SpawnBiomeData SUNBIRD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_mountain", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:snowy_slopes", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_peaks", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:jagged_peaks", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:emerald_peaks", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:painted_mountains", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_mountains", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:scarlet_mountains", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:stony_spires", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:volcanic_crater", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:volcanic_peaks", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_spring", 13)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 14)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_winter", 15)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_badlands", 16)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_slopes", 17)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:yellowstone", 18)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:yosemite_cliffs", 19)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:bryce_canyon", 20)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:jungle_mountains", 21)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:haze_mountain", 22)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:windswept_spires", 23)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_spires", 24)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_canyon", 25)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:basalt_cliffs", 26)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:granite_cliffs", 27)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:white_cliffs", 28);

    public static final SpawnBiomeData GORILLA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_canyon", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:jungle_mountains", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_jungle", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:tropical_jungle", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 6);

    public static final SpawnBiomeData CRIMSON_MOSQUITO = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:crimson_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:crimson_gardens", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:visceral_heap", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:ash_barrens", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:infernal_dunes", 4);

    public static final SpawnBiomeData RATTLESNAKE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_badlands", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_dry/overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_hot/overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_sandy", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ancient_sands", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_canyon", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_oasis", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_spires", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sandstone_valley", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:warped_mesa", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:white_mesa", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 10);

    public static final SpawnBiomeData ENDERGRADE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0);

    public static final SpawnBiomeData HAMMERHEAD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_hot/overworld", 0);

    public static final SpawnBiomeData LOBSTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_beach", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:gravel_beach", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:stony_shore", 2);

    public static final SpawnBiomeData KOMODO_DRAGON = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_jungle", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_dense/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sandstone_valley", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:tropics", 4);

    public static final SpawnBiomeData CAPUCHIN_MONKEY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:mangrove_swamp", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_canyon", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:jungle_mountains", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_jungle", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:tropical_jungle", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 7);

    public static final SpawnBiomeData CAVES = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_mushroom", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_dark", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/andesite_caves", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/desert_caves", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/diorite_caves", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/granite_caves", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/ice_caves", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/infested_caves", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/thermal_caves", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/crystal_caves", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/frostfire_caves", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/mantle_caves", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/deep_caves", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/tuff_caves", 12);

    public static final SpawnBiomeData CAVES_MONSTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_mushroom", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_dark", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/andesite_caves", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/desert_caves", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/diorite_caves", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/granite_caves", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/ice_caves", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/infested_caves", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/thermal_caves", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/crystal_caves", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/frostfire_caves", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/mantle_caves", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/deep_caves", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/tuff_caves", 12);

    public static final SpawnBiomeData WARPED_TOAD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:warped_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:crimson_gardens", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:warped_desert", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:inverted_forest", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:quartz_flats", 4);
        
    public static final SpawnBiomeData MOOSE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_snowy", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_wasteland", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_snowy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_taiga", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowy_coniferous_forest", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowy_fir_clearing", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "snowy_maple_woods", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:alpine_grove", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_badlands", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_maple_forest", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_shield", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:wintry_forest", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:wintry_lowlands", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:gravel_desert", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_winter", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowblossom_grove", 13);

    public static final SpawnBiomeData MIMICUBE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0);

    public static final SpawnBiomeData RACCOON = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_savanna", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_plains", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_taiga", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:alpine_grove", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:birch_taiga", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_forest", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_valley", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_grove", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_valley", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cold_shrubland", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:highlands", 13)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:hot_shrubland", 14)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_shrubland", 15)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shield_clearing", 16)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:steppe", 17)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:valley_clearing", 18)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:temperate_highlands", 19)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 20)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:mirage_isles", 21)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:redwood_forest", 22)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:cherry_grove", 23)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowblossom_grove", 24);


    public static final SpawnBiomeData DEEP_SEA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_deep_ocean", 0);

    public static final SpawnBiomeData SEAL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_beach", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_ocean", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_cold/overworld", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:gravel_beach", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:dune_beach", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:stony_shore", 4);

    public static final SpawnBiomeData COCKROACH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_mushroom", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_dark", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/andesite_caves", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/desert_caves", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/diorite_caves", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/granite_caves", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/ice_caves", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/infested_caves", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/thermal_caves", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/crystal_caves", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/frostfire_caves", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/mantle_caves", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/deep_caves", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/tuff_caves", 12);

    public static final SpawnBiomeData SHOEBILL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:mangrove_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:orchid_swamp", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 2);

    public static final SpawnBiomeData ELEPHANT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:arid_highlands", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:brushland", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:fractured_savanna", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_badlands", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_slopes", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shrubland", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 7);

    public static final SpawnBiomeData SOUL_VULTURE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:soul_sand_valley", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "byg:warped_desert", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:weeping_valley", 2);

    public static final SpawnBiomeData SPECTRE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0);

    public static final SpawnBiomeData SNOW_LEOPARD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_snowy", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:snowy_slopes", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_peaks", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:jagged_peaks", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:frozen_cliffs", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:glacial_chasm", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_badlands", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_maple_forest", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:emerald_peaks", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_mountains", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:scarlet_mountains", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_shield", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_winter", 11);

    public static final SpawnBiomeData CROW = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_plains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_forest", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_taiga", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cold_shrubland", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:highlands", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:hot_shrubland", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_shrubland", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shield_clearing", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:steppe", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:valley_clearing", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:alpine_grove", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:birch_taiga", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_forest", 13)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_valley", 14)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_grove", 15)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_valley", 16)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 17)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 18)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:highlands", 19)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shield_clearing", 20)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:alpine_highlands", 21)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:forested_highlands", 22)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lush_valley", 23)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shield", 24)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_maple_forest", 25)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:temperate_highlands", 26)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:wintry_forest", 27)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:yosemite_lowlands", 28)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 29)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_spring", 30)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_winter", 31)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:mirage_isles", 32)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cloud_forest", 33)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:cherry_grove", 34)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowblossom_grove", 35);


    public static final SpawnBiomeData ALLIGATOR_SNAPPING_TURTLE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:mangrove_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:tundra_bog", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ice_marsh", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:orchid_swamp", 3);

    public static final SpawnBiomeData MUNGUS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_mushroom", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_rare", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:mirage_isles", 1);

    public static final SpawnBiomeData MANTIS_SHRIMP = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_hot/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:mangrove_swamp", 1);

    public static final SpawnBiomeData GUSTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_hot/overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_dry/overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_sandy", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ancient_sands", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_canyon", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_spires", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ashen_savanna", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/desert_caves", 5);

    public static final SpawnBiomeData STRADDLER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:basalt_deltas", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:withered_abyss", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:volcanic_deltas", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "incendium:withered_forest", 3);

    public static final SpawnBiomeData SAVANNA_AND_MESA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_badlands", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_savanna", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:arid_highlands", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:brushland", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:fractured_savanna", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_badlands", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_slopes", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:warped_mesa", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:white_mesa", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:lush_desert", 10);

    public static final SpawnBiomeData ICE_FREE_RIVER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_river", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_cold/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:tundra_bog", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:warm_river", 2);

    public static final SpawnBiomeData DROPBEAR = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:nether_wastes", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:crystalline_chasm", 1);

    public static final SpawnBiomeData TASMANIAN_DEVIL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_cold/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:sparse_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:birch_taiga", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:mirage_isles", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_valley", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_forest", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_grove", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_valley", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:temperate_highlands", 10);

    public static final SpawnBiomeData CACHALOT_WHALE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_cold/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:lukewarm_ocean", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_ocean", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_lukewarm_ocean", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "alexscaves:abyssal_chasm", 4);

    public static final SpawnBiomeData BEACHED_CACHALOT_WHALE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_beach", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:gravel_beach", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:dune_beach", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:stony_shore", 3);

    public static final SpawnBiomeData LEAFCUTTER_ANTHILL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_canyon", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:jungle_mountains", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_jungle", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:tropical_jungle", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 6);

    public static final SpawnBiomeData ENDERIOPHAGE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:end_barrens", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:end_highlands", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:small_end_islands", 0);

    public static final SpawnBiomeData BALD_EAGLE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_hill", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_coniferous", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:grove", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:windswept_forest", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_plateau", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:bryce_canyon", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_spring", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_winter", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_forest", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_valley", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_valley", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_grove", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 13)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 14)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:haze_mountain", 15)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:temperate_highlands", 16)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:alpine_grove", 17)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:jade_cliffs", 18);

    public static final SpawnBiomeData TIGER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:bamboo_grove", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:cherry_grove", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_canyon", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_spring", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowblossom_grove", 8);

    public static final SpawnBiomeData DESERT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_dry/overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_hot/overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_sandy", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_badlands", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ancient_sands", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_canyon", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_oasis", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:desert_spires", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sandstone_valley", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 6);
    
    public static final SpawnBiomeData MIMIC_OCTOPUS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_hot/overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_warm_ocean", 0);

    public static final SpawnBiomeData SEAGULL = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_beach", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:basalt_cliffs", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:granite_cliffs", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:gravel_beach", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:white_cliffs", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_spring", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:dune_beach", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:stony_shore", 8);

    public static final SpawnBiomeData FROSTSTALKER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:ice_spikes", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_peaks", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/frostfire_caves", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:frozen_cliffs", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:glacial_chasm", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_badlands", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:gravel_desert", 6);

    public static final SpawnBiomeData TUSKLIN = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:ice_spikes", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_snowy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_plains", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:snowy_badlands", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:gravel_desert", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowblossom_grove", 4);

    public static final SpawnBiomeData COSMAW = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:the_end", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:small_end_islands", 0);

    public static final SpawnBiomeData TOUCAN = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_canyon", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:jungle_mountains", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_jungle", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:tropical_jungle", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 6);

    public static final SpawnBiomeData MANED_WOLF = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:arid_highlands", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:brushland", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:fractured_savanna", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_badlands", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_slopes", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shrubland", 6);

    public static final SpawnBiomeData ANACONDA = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:mangrove_swamp", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ice_marsh", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:orchid_swamp", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:tropical_jungle", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 6);

    public static final SpawnBiomeData ANTEATER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:bamboo_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_canyon", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:amethyst_rainforest", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:jungle_mountains", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_jungle", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:tropical_jungle", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_summer", 6);

    public static final SpawnBiomeData ROCKY_ROLLER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:dripstone_caves", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/andesite_caves", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/diorite_caves", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/granite_caves", 3);

    public static final SpawnBiomeData FLUTTER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:lush_caves", 0);

    public static final SpawnBiomeData MEADOWS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_plains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_plateau", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:highlands", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:hot_shrubland", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_shrubland", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:steppe", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:valley_clearing", 5);
        
    public static final SpawnBiomeData COMB_JELLY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:frozen_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:deep_frozen_ocean", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "alexscaves:abyssal_chasm", 2);

    public static final SpawnBiomeData COSMIC_COD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_end", 0);

    public static final SpawnBiomeData BISON = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_plains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_hot/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:meadow", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:field", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:forested_field", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:grassland", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:pasture", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:prairie", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cold_shrubland", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:rocky_shrubland", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:steppe", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:valley_clearing", 10);

    public static final SpawnBiomeData GIANT_SQUID = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_deep_ocean", 0);

    public static final SpawnBiomeData ALL_OVERWORLD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0);

    public static final SpawnBiomeData CATFISH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:mangrove_swamp", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_river", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_cold/overworld", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:orchid_swamp", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ice_marsh", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:warm_river", 4);

    public static final SpawnBiomeData LARGE_CATFISH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:mangrove_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:orchid_swamp", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:ice_marsh", 2);

    public static final SpawnBiomeData FLYING_FISH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_cold/overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_hot/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_ocean", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_lukewarm_ocean", 0);

    public static final SpawnBiomeData SKELEWAG = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_deep_ocean", 0);

    public static final SpawnBiomeData POTOO = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:dark_forest", 0);

    public static final SpawnBiomeData MANGROVE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:mangrove_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/underground_jungle", 1);   

    public static final SpawnBiomeData RHINOCEROS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:arid_highlands", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:brushland", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:fractured_savanna", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_badlands", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:savanna_slopes", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shrubland", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:red_oasis", 7);

    public static final SpawnBiomeData SUGAR_GLIDER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:birch_forest", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:old_growth_birch_forest", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:white_cliffs", 2);

    public static final SpawnBiomeData FARSEER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:mushroom_fields", 0);

    public static final SpawnBiomeData SKREECHER = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "alexsmobs:skreechers_can_spawn_wardens", 0);

    public static final SpawnBiomeData MURMUR = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_mushroom", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:deep_dark", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/andesite_caves", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/desert_caves", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/diorite_caves", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/granite_caves", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/ice_caves", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/infested_caves", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/thermal_caves", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/crystal_caves", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/frostfire_caves", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/mantle_caves", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/deep_caves", 11)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:cave/tuff_caves", 12)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:cherry_grove", 13)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:snowblossom_grove", 14);
    public static final SpawnBiomeData SKUNK = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_cold/overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:sparse_jungle", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:birch_taiga", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:blooming_valley", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:mirage_isles", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_valley", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:lavender_forest", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_grove", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:moonlight_valley", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_grove", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:sakura_valley", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:temperate_highlands", 10)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:cherry_grove", 11);


    public static final SpawnBiomeData BANANA_SLUG = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:old_growth_pine_taiga", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:old_growth_spruce_taiga", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_taiga", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_dense/overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_rare", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:forested_highlands", 3)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:shield", 4)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:skylands_autumn", 5)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "terralith:yosemite_lowlands", 6)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:redwood_forest", 7)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:coniferous_forest", 8)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "biomesoplenty:fir_clearing", 9)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "autumnity:maple_forest", 10);
}
