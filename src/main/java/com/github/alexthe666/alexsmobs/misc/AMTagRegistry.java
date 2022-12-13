package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class AMTagRegistry {
    public static final TagKey<Item> INSECT_ITEMS = registerItemTag("insect_items");
    public static final TagKey<Block> GRIZZLY_BEEHIVE = registerBlockTag("grizzly_beehive");
    public static final TagKey<Item> GRIZZLY_FOODSTUFFS = registerItemTag("grizzly_foodstuffs");
    public static final TagKey<Item> GRIZZLY_HONEY = registerItemTag("grizzly_honey");
    public static final TagKey<EntityType<?>> FLY_TARGETS = registerEntityTag("fly_hurt_targets");
    public static final TagKey<EntityType<?>> FLY_ANNOY_TARGETS = registerEntityTag("fly_annoy_targets");
    public static final TagKey<Block> DROPS_BANANAS = registerBlockTag("drops_bananas");
    public static final TagKey<Block> DROPS_ACACIA_BLOSSOMS = registerBlockTag("drops_acacia_blossoms");
    public static final TagKey<Block> ORCA_BREAKABLES = registerBlockTag("orca_breakables");
    public static final TagKey<EntityType<?>> SUNBIRD_SCORCH_TARGETS = registerEntityTag("sunbird_scorch_targets");
    public static final TagKey<Block> GORILLA_BREAKABLES = registerBlockTag("gorilla_breakables");
    public static final TagKey<Item> GORILLA_FOODSTUFFS = registerItemTag("gorilla_foodstuffs");
    public static final TagKey<EntityType<?>> ORCA_TARGETS = registerEntityTag("orca_targets");
    public static final TagKey<EntityType<?>> CRIMSON_MOSQUITO_TARGETS = registerEntityTag("crimson_mosquito_targets");
    public static final TagKey<EntityType<?>> WARPED_TOAD_TARGETS = registerEntityTag("warped_toad_targets");
    public static final TagKey<EntityType<?>> CROCODILE_TARGETS = registerEntityTag("crocodile_targets");
    public static final TagKey<EntityType<?>> KOMODO_DRAGON_TARGETS = registerEntityTag("komodo_dragon_targets");
    public static final TagKey<EntityType<?>> MANTIS_SHRIMP_TARGETS = registerEntityTag("mantis_shrimp_targets");
    public static final TagKey<EntityType<?>> VOID_PORTAL_IGNORES = registerEntityTag("void_portal_ignores");

    public static final TagKey<EntityType<?>> SKUNK_FEARS = registerEntityTag("skunk_fears");
    public static final TagKey<EntityType<?>> CAIMAN_TARGETS = registerEntityTag("caiman_targets");

    public static final TagKey<Item> BANANAS = registerItemTag("bananas");
    public static final TagKey<Item> RACCOON_FOODSTUFFS = registerItemTag("raccoon_foodstuffs");
    public static final TagKey<Item> SEAL_FOODSTUFFS = registerItemTag("seal_foodstuffs");
    public static final TagKey<Block> SEAL_DIGABLES = registerBlockTag("seal_digables");
    public static final TagKey<Item> SHOEBILL_FOODSTUFFS = registerItemTag("shoebill_foodstuffs");
    public static final TagKey<Block> ELEPHANT_FOODBLOCKS = registerBlockTag("elephant_foodblocks");
    public static final TagKey<Item> ELEPHANT_FOODSTUFFS = registerItemTag("elephant_foodstuffs");
    public static final TagKey<Block> SOUL_VULTURE_PERCHES = registerBlockTag("soul_vulture_perches");
    public static final TagKey<Block> SOUL_VULTURE_SPAWNS = registerBlockTag("soul_vulture_spawns");
    public static final TagKey<EntityType<?>> SNOW_LEOPARD_TARGETS = registerEntityTag("snow_leopard_targets");
    public static final TagKey<EntityType<?>> SCATTERS_CROWS = registerEntityTag("scatters_crows");
    public static final TagKey<Block> CROW_FOODBLOCKS = registerBlockTag("crow_foodblocks");
    public static final TagKey<Item> CROW_FOODSTUFFS = registerItemTag("crow_foodstuffs");
    public static final TagKey<Block> MUNGUS_REPLACE_MUSHROOM = registerBlockTag("mungus_replace_mushroom");
    public static final TagKey<Block> MUNGUS_REPLACE_NETHER = registerBlockTag("mungus_replace_nether");
    public static final TagKey<Block> WARPED_MOSCO_BREAKABLES = registerBlockTag("warped_mosco_breakables");
    public static final TagKey<Block> CROW_FEARS = registerBlockTag("crow_fears");
    public static final TagKey<Item> PLATYPUS_FOODSTUFFS = registerItemTag("platypus_foodstuffs");
    public static final TagKey<EntityType<?>> CACHALOT_WHALE_TARGETS = registerEntityTag("cachalot_whale_targets");
    public static final TagKey<Block> CACHALOT_WHALE_BREAKABLES = registerBlockTag("cachalot_whale_breakables");
    public static final TagKey<Block> LEAFCUTTER_ANT_BREAKABLES = registerBlockTag("leafcutter_ant_breakables");
    public static final TagKey<EntityType<?>> TIGER_TARGETS = registerEntityTag("tiger_targets");
    public static final TagKey<EntityType<?>> BALD_EAGLE_TARGETS = registerEntityTag("bald_eagle_targets");
    public static final TagKey<Block> VOID_WORM_BREAKABLES = registerBlockTag("void_worm_breakables");
    public static final TagKey<EntityType<?>> MIMIC_OCTOPUS_FEARS = registerEntityTag("mimic_octopus_fears");
    public static final TagKey<Item> MIMIC_OCTOPUS_CREEPER_ITEMS = registerItemTag("mimic_octopus_creeper_items");
    public static final TagKey<Item> MIMIC_OCTOPUS_GUARDIAN_ITEMS = registerItemTag("mimic_octopus_guardian_items");
    public static final TagKey<Item> MIMIC_OCTOPUS_PUFFERFISH_ITEMS = registerItemTag("mimic_octopus_pufferfish_items");
    public static final TagKey<Item> SHRIMP_RICE_FRYABLES = registerItemTag("shrimp_rice_fryables");
    public static final TagKey<Item> TIGER_BREEDABLES = registerItemTag("tiger_breedables");
    public static final TagKey<Item> BALD_EAGLE_TAMEABLES = registerItemTag("bald_eagle_tameables");
    public static final TagKey<Item> VOID_WORM_DROPS = registerItemTag("void_worm_drops");
    public static final TagKey<Item> UNDERMINER_ORES = registerItemTag("underminer_ores");
    public static final TagKey<Item> BLUE_JAY_FOODSTUFFS = registerItemTag("blue_jay_foodstuffs");

    public static final TagKey<EntityType<?>> FROSTSTALKER_TARGETS = registerEntityTag("froststalker_targets");
    public static final TagKey<Block> FROSTSTALKER_FEARS = registerBlockTag("froststalker_fears");
    public static final TagKey<EntityType<?>> ANACONDA_TARGETS = registerEntityTag("anaconda_targets");
    public static final TagKey<Block> GELADA_MONKEY_GRASS = registerBlockTag("gelada_monkey_grass");
    public static final TagKey<EntityType<?>> GIANT_SQUID_TARGETS = registerEntityTag("giant_squid_targets");
    public static final TagKey<EntityType<?>> MONKEY_TARGET_WITH_DART = registerEntityTag("monkey_target_with_dart");
    public static final TagKey<Item> RACOON_DISSOLVES = registerItemTag("raccoon_dissolves");
    public static final TagKey<Block> ROADRUNNER_SPAWNS = registerBlockTag("roadrunner_spawns");
    public static final TagKey<Block> LOBSTER_SPAWNS = registerBlockTag("lobster_spawns");
    public static final TagKey<Block> MIMIC_OCTOPUS_SPAWNS = registerBlockTag("mimic_octopus_spawns");
    public static final TagKey<Block> RATTLESNAKE_SPAWNS = registerBlockTag("rattlesnake_spawns");
    public static final TagKey<Block> KOMODO_DRAGON_SPAWNS = registerBlockTag("komodo_dragon_spawns");
    public static final TagKey<Block> CROCODILE_SPAWNS = registerBlockTag("crocodile_spawns");
    public static final TagKey<Block> SEAL_SPAWNS = registerBlockTag("seal_spawns");
    public static final TagKey<Block> ALLIGATOR_SNAPPING_TURTLE_SPAWNS = registerBlockTag("alligator_snapping_turtle_spawns");
    public static final TagKey<Block> MANTIS_SHRIMP_SPAWNS = registerBlockTag("mantis_shrimp_spawns");
    public static final TagKey<Block> EMU_SPAWNS = registerBlockTag("emu_spawns");
    public static final TagKey<Block> KANGAROO_SPAWNS = registerBlockTag("kangaroo_spawns");
    public static final TagKey<Block> PLATYPUS_SPAWNS = registerBlockTag("platypus_spawns");
    public static final TagKey<Block> ANACONDA_SPAWNS = registerBlockTag("anaconda_spawns");
    public static final TagKey<Block> FLY_SPAWNS = registerBlockTag("fly_spawns");
    public static final TagKey<Block> LEAFCUTTER_PUPA_USABLE_ON = registerBlockTag("leafcutter_pupa_usable_on");
    public static final TagKey<Block> PUPFISH_EATABLES = registerBlockTag("pupfish_eatables");
    public static final TagKey<Block> POTOO_PERCHES = registerBlockTag("potoo_perches");
    public static final TagKey<EntityType<?>> IGNORES_KIMONO = registerEntityTag("ignores_kimono");
    public static final TagKey<Block> LAVIATHAN_BREAKABLES = registerBlockTag("laviathan_breakables");
    public static final TagKey<EntityType<?>> BUNFUNGUS_IGNORES = registerEntityTag("bunfungus_ignores");
    public static final TagKey<EntityType<?>> BUNFUNGUS_IGNORE_AOE_ATTACKS = registerEntityTag("bunfungus_ignore_aoe_attacks");

    public static final TagKey<Biome> SPAWNS_DESERT_CROCODILES = registerBiomeTag("spawns_desert_crocodiles");
    public static final TagKey<Biome> SPAWNS_RED_GUSTERS = registerBiomeTag("spawns_red_gusters");
    public static final TagKey<Biome> SPAWNS_SOUL_GUSTERS = registerBiomeTag("spawns_soul_gusters");
    public static final TagKey<Biome> SPAWNS_NETHER_TARANTULA_HAWKS = registerBiomeTag("spawns_nether_tarantula_hawks");
    public static final TagKey<Biome> SPAWNS_WHITE_SEALS = registerBiomeTag("spawns_white_seals");
    public static final TagKey<Biome> SPAWNS_HUGE_CATFISH = registerBiomeTag("spawns_huge_catfish");
    public static final TagKey<Biome> SPAWNS_WHITE_MANTIS_SHRIMP = registerBiomeTag("spawns_white_mantis_shrimp");
    public static final TagKey<Biome> SKREECHERS_CAN_SPAWN_WARDENS = registerBiomeTag("skreechers_can_spawn_wardens");

    public static final TagKey<Structure> SPAWNS_UNDERMINERS = registerStructureTag("spawns_underminers");

    private static TagKey<EntityType<?>> registerEntityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AlexsMobs.MODID, name));
    }

    private static TagKey<Item> registerItemTag(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(AlexsMobs.MODID, name));
    }

    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(AlexsMobs.MODID, name));
    }

    private static TagKey<Biome> registerBiomeTag(String name) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(AlexsMobs.MODID, name));
    }

    private static TagKey<Structure> registerStructureTag(String name) {
        return TagKey.create(Registries.STRUCTURE, new ResourceLocation(AlexsMobs.MODID, name));
    }
}
