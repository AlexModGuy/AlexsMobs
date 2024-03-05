package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeConfig;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BiomeConfig {
	public static final Pair<String, SpawnBiomeData> grizzlyBear = Pair.of("alexsmobs:grizzly_bear_spawns", DefaultBiomes.ALL_FOREST);
	public static final Pair<String, SpawnBiomeData> roadrunner = Pair.of("alexsmobs:roadrunner_spawns", DefaultBiomes.ROADRUNNER);
	public static final Pair<String, SpawnBiomeData> boneSerpent = Pair.of("alexsmobs:bone_serpent_spawns", DefaultBiomes.ALL_NETHER_MONSTER);
	public static final Pair<String, SpawnBiomeData> gazelle = Pair.of("alexsmobs:gazelle_spawns", DefaultBiomes.GAZELLE);
	public static final Pair<String, SpawnBiomeData> crocodile = Pair.of("alexsmobs:crocodile_spawns", DefaultBiomes.CROCODILE);
	public static final Pair<String, SpawnBiomeData> fly = Pair.of("alexsmobs:fly_spawns", DefaultBiomes.FLY);
	public static final Pair<String, SpawnBiomeData> hummingbird = Pair.of("alexsmobs:hummingbird_spawns", DefaultBiomes.HUMMINGBIRD);
	public static final Pair<String, SpawnBiomeData> orca = Pair.of("alexsmobs:orca_spawns", DefaultBiomes.ORCA);
	public static final Pair<String, SpawnBiomeData> sunbird = Pair.of("alexsmobs:sunbird_spawns", DefaultBiomes.SUNBIRD);
	public static final Pair<String, SpawnBiomeData> gorilla = Pair.of("alexsmobs:gorilla_spawns", DefaultBiomes.GORILLA);
	public static final Pair<String, SpawnBiomeData> crimsonMosquito = Pair.of("alexsmobs:crimson_mosquito_spawns", DefaultBiomes.CRIMSON_MOSQUITO);
	public static final Pair<String, SpawnBiomeData> rattlesnake = Pair.of("alexsmobs:rattlesnake_spawns", DefaultBiomes.RATTLESNAKE);
	public static final Pair<String, SpawnBiomeData> endergrade = Pair.of("alexsmobs:endergrade_spawns", DefaultBiomes.ENDERGRADE);
	public static final Pair<String, SpawnBiomeData> hammerheadShark = Pair.of("alexsmobs:hammerhead_shark_spawns", DefaultBiomes.HAMMERHEAD);
	public static final Pair<String, SpawnBiomeData> lobster = Pair.of("alexsmobs:lobster_spawns", DefaultBiomes.LOBSTER);
	public static final Pair<String, SpawnBiomeData> komodoDragon = Pair.of("alexsmobs:komodo_dragon_spawns", DefaultBiomes.KOMODO_DRAGON);
	public static final Pair<String, SpawnBiomeData> capuchinMonkey = Pair.of("alexsmobs:capuchin_monkey_spawns", DefaultBiomes.CAPUCHIN_MONKEY);
	public static final Pair<String, SpawnBiomeData> caveCentipede = Pair.of("alexsmobs:cave_centipede_spawns", DefaultBiomes.CAVES_MONSTER);
	public static final Pair<String, SpawnBiomeData> warpedToad = Pair.of("alexsmobs:warped_toad_spawns", DefaultBiomes.WARPED_TOAD);
	public static final Pair<String, SpawnBiomeData> moose = Pair.of("alexsmobs:moose_spawns", DefaultBiomes.MOOSE);
	public static final Pair<String, SpawnBiomeData> mimicube = Pair.of("alexsmobs:mimicube_spawns", DefaultBiomes.MIMICUBE);
	public static final Pair<String, SpawnBiomeData> raccoon = Pair.of("alexsmobs:raccoon_spawns", DefaultBiomes.RACCOON);
	public static final Pair<String, SpawnBiomeData> blobfish = Pair.of("alexsmobs:blobfish_spawns", DefaultBiomes.DEEP_SEA);
	public static final Pair<String, SpawnBiomeData> seal = Pair.of("alexsmobs:seal_spawns", DefaultBiomes.SEAL);
	public static final Pair<String, SpawnBiomeData> cockroach = Pair.of("alexsmobs:cockroach_spawns", DefaultBiomes.COCKROACH);
	public static final Pair<String, SpawnBiomeData> shoebill = Pair.of("alexsmobs:shoebill_spawns", DefaultBiomes.SHOEBILL);
	public static final Pair<String, SpawnBiomeData> elephant = Pair.of("alexsmobs:elephant_spawns", DefaultBiomes.ELEPHANT);
	public static final Pair<String, SpawnBiomeData> soulVulture = Pair.of("alexsmobs:soul_vulture_spawns", DefaultBiomes.SOUL_VULTURE);
	public static final Pair<String, SpawnBiomeData> snowLeopard = Pair.of("alexsmobs:snow_leopard_spawns", DefaultBiomes.SNOW_LEOPARD);
	public static final Pair<String, SpawnBiomeData> spectre = Pair.of("alexsmobs:spectre_spawns", DefaultBiomes.SPECTRE);
	public static final Pair<String, SpawnBiomeData> crow = Pair.of("alexsmobs:crow_spawns", DefaultBiomes.CROW);
	public static final Pair<String, SpawnBiomeData> alligatorSnappingTurtle = Pair.of("alexsmobs:alligator_snapping_turtle_spawns", DefaultBiomes.ALLIGATOR_SNAPPING_TURTLE);
	public static final Pair<String, SpawnBiomeData> mungus = Pair.of("alexsmobs:mungus_spawns", DefaultBiomes.MUNGUS);
	public static final Pair<String, SpawnBiomeData> mantisShrimp = Pair.of("alexsmobs:mantis_shrimp_spawns", DefaultBiomes.MANTIS_SHRIMP);
	public static final Pair<String, SpawnBiomeData> guster = Pair.of("alexsmobs:guster_spawns", DefaultBiomes.GUSTER);
	public static final Pair<String, SpawnBiomeData> warpedMosco = Pair.of("alexsmobs:warped_mosco_spawns", DefaultBiomes.EMPTY);
	public static final Pair<String, SpawnBiomeData> straddler = Pair.of("alexsmobs:straddler_spawns", DefaultBiomes.STRADDLER);
	public static final Pair<String, SpawnBiomeData> stradpole = Pair.of("alexsmobs:stradpole_spawns", DefaultBiomes.STRADDLER);
	public static final Pair<String, SpawnBiomeData> emu = Pair.of("alexsmobs:emu_spawns", DefaultBiomes.SAVANNA_AND_MESA);
	public static final Pair<String, SpawnBiomeData> platypus = Pair.of("alexsmobs:platypus_spawns", DefaultBiomes.ICE_FREE_RIVER);
	public static final Pair<String, SpawnBiomeData> dropbear = Pair.of("alexsmobs:dropbear_spawns", DefaultBiomes.DROPBEAR);
	public static final Pair<String, SpawnBiomeData> tasmanianDevil = Pair.of("alexsmobs:tasmanian_devil_spawns", DefaultBiomes.TASMANIAN_DEVIL);
	public static final Pair<String, SpawnBiomeData> kangaroo = Pair.of("alexsmobs:kangaroo_spawns", DefaultBiomes.SAVANNA_AND_MESA);
	public static final Pair<String, SpawnBiomeData> cachalot_whale_spawns = Pair.of("alexsmobs:cachalot_whale_spawns", DefaultBiomes.CACHALOT_WHALE);
	public static final Pair<String, SpawnBiomeData> cachalot_whale_beached_spawns = Pair.of("alexsmobs:cachalot_whale_beached_spawns", DefaultBiomes.BEACHED_CACHALOT_WHALE);
	public static final Pair<String, SpawnBiomeData> leafcutter_anthill_spawns = Pair.of("alexsmobs:leafcutter_anthill_spawns", DefaultBiomes.LEAFCUTTER_ANTHILL);
	public static final Pair<String, SpawnBiomeData> enderiophage_spawns = Pair.of("alexsmobs:enderiophage_spawns", DefaultBiomes.ENDERIOPHAGE);
	public static final Pair<String, SpawnBiomeData> baldEagle = Pair.of("alexsmobs:bald_eagle_spawns", DefaultBiomes.BALD_EAGLE);
	public static final Pair<String, SpawnBiomeData> tiger = Pair.of("alexsmobs:tiger_spawns", DefaultBiomes.TIGER);
	public static final Pair<String, SpawnBiomeData> tarantula_hawk = Pair.of("alexsmobs:tarantula_hawk_spawns", DefaultBiomes.DESERT);
	public static final Pair<String, SpawnBiomeData> void_worm = Pair.of("alexsmobs:void_worm_spawns", DefaultBiomes.EMPTY);
	public static final Pair<String, SpawnBiomeData> frilled_shark = Pair.of("alexsmobs:frilled_shark_spawns", DefaultBiomes.DEEP_SEA);
	public static final Pair<String, SpawnBiomeData> mimic_octopus = Pair.of("alexsmobs:mimic_octopus_spawns", DefaultBiomes.MIMIC_OCTOPUS);
	public static final Pair<String, SpawnBiomeData> seagull = Pair.of("alexsmobs:seagull_spawns", DefaultBiomes.SEAGULL);
	public static final Pair<String, SpawnBiomeData> froststalker = Pair.of("alexsmobs:froststalker_spawns", DefaultBiomes.FROSTSTALKER);
	public static final Pair<String, SpawnBiomeData> tusklin = Pair.of("alexsmobs:tusklin_spawns", DefaultBiomes.TUSKLIN);
	public static final Pair<String, SpawnBiomeData> laviathan = Pair.of("alexsmobs:laviathan_spawns", DefaultBiomes.ALL_NETHER);
	public static final Pair<String, SpawnBiomeData> cosmaw = Pair.of("alexsmobs:cosmaw_spawns", DefaultBiomes.COSMAW);
	public static final Pair<String, SpawnBiomeData> toucan = Pair.of("alexsmobs:toucan_spawns", DefaultBiomes.TOUCAN);
	public static final Pair<String, SpawnBiomeData> maned_wolf = Pair.of("alexsmobs:maned_wolf_spawns", DefaultBiomes.MANED_WOLF);
	public static final Pair<String, SpawnBiomeData> anaconda = Pair.of("alexsmobs:anaconda_spawns", DefaultBiomes.ANACONDA);
	public static final Pair<String, SpawnBiomeData> anteater = Pair.of("alexsmobs:anteater_spawns", DefaultBiomes.ANTEATER);
	public static final Pair<String, SpawnBiomeData> rocky_roller = Pair.of("alexsmobs:rocky_roller_spawns", DefaultBiomes.ROCKY_ROLLER);
	public static final Pair<String, SpawnBiomeData> flutter = Pair.of("alexsmobs:flutter_spawns", DefaultBiomes.FLUTTER);
	public static final Pair<String, SpawnBiomeData> gelada_monkey = Pair.of("alexsmobs:gelada_monkey_spawns", DefaultBiomes.MEADOWS);
	public static final Pair<String, SpawnBiomeData> jerboa = Pair.of("alexsmobs:jerboa_spawns", DefaultBiomes.DESERT);
	public static final Pair<String, SpawnBiomeData> terrapin = Pair.of("alexsmobs:terrapin_spawns", DefaultBiomes.ICE_FREE_RIVER);
	public static final Pair<String, SpawnBiomeData> comb_jelly = Pair.of("alexsmobs:comb_jelly_spawns", DefaultBiomes.COMB_JELLY);
	public static final Pair<String, SpawnBiomeData> cosmic_cod = Pair.of("alexsmobs:cosmic_cod_spawns", DefaultBiomes.COSMIC_COD);
	public static final Pair<String, SpawnBiomeData> bunfungus = Pair.of("alexsmobs:bunfungus_spawns", DefaultBiomes.MUNGUS);
	public static final Pair<String, SpawnBiomeData> bison = Pair.of("alexsmobs:bison_spawns", DefaultBiomes.BISON);
	public static final Pair<String, SpawnBiomeData> giant_squid = Pair.of("alexsmobs:giant_squid_spawns", DefaultBiomes.GIANT_SQUID);
	public static final Pair<String, SpawnBiomeData> devils_hole_pupfish = Pair.of("alexsmobs:devils_hole_pupfish_spawns", DefaultBiomes.ALL_OVERWORLD);
	public static final Pair<String, SpawnBiomeData> catfish = Pair.of("alexsmobs:catfish_spawns", DefaultBiomes.CATFISH);
	public static final Pair<String, SpawnBiomeData> flying_fish = Pair.of("alexsmobs:flying_fish_spawns", DefaultBiomes.FLYING_FISH);
	public static final Pair<String, SpawnBiomeData> skelewag = Pair.of("alexsmobs:skelewag_spawns", DefaultBiomes.SKELEWAG);
	public static final Pair<String, SpawnBiomeData> rain_frog = Pair.of("alexsmobs:rain_frog_spawns", DefaultBiomes.DESERT);
	public static final Pair<String, SpawnBiomeData> potoo = Pair.of("alexsmobs:potoo_spawns", DefaultBiomes.POTOO);
	public static final Pair<String, SpawnBiomeData> mudskipper = Pair.of("alexsmobs:mudskipper_spawns", DefaultBiomes.MANGROVE);
	public static final Pair<String, SpawnBiomeData> rhinoceros = Pair.of("alexsmobs:rhinoceros_spawns", DefaultBiomes.RHINOCEROS);
	public static final Pair<String, SpawnBiomeData> sugar_glider = Pair.of("alexsmobs:sugar_glider_spawns", DefaultBiomes.SUGAR_GLIDER);
	public static final Pair<String, SpawnBiomeData> farseer = Pair.of("alexsmobs:farseer", DefaultBiomes.FARSEER);
	public static final Pair<String, SpawnBiomeData> skreecher = Pair.of("alexsmobs:skreecher", DefaultBiomes.SKREECHER);
	public static final Pair<String, SpawnBiomeData> underminer = Pair.of("alexsmobs:underminer", DefaultBiomes.CAVES);
	public static final Pair<String, SpawnBiomeData> murmur = Pair.of("alexsmobs:murmur", DefaultBiomes.CAVES_MONSTER);
	public static final Pair<String, SpawnBiomeData> skunk = Pair.of("alexsmobs:skunk_spawns", DefaultBiomes.SKUNK);
	public static final Pair<String, SpawnBiomeData> banana_slug = Pair.of("alexsmobs:banana_slug_spawns", DefaultBiomes.BANANA_SLUG);
	public static final Pair<String, SpawnBiomeData> blue_jay = Pair.of("alexsmobs:blue_jay_spawns", DefaultBiomes.ALL_FOREST);
	public static final Pair<String, SpawnBiomeData> caiman = Pair.of("alexsmobs:caiman_spawns", DefaultBiomes.MANGROVE);
	public static final Pair<String, SpawnBiomeData> triops = Pair.of("alexsmobs:triops_spawns", DefaultBiomes.DESERT);

	private static boolean init = false;
	private static final Map<String, SpawnBiomeData> biomeConfigValues = new HashMap<>();

    public static void init() {
        try {
            for (Field f : BiomeConfig.class.getDeclaredFields()) {
                Object obj = f.get(null);
               if(obj instanceof Pair){
				   String id = (String)((Pair) obj).getLeft();
				   SpawnBiomeData data = (SpawnBiomeData)((Pair) obj).getRight();
				   biomeConfigValues.put(id, SpawnBiomeConfig.create(new ResourceLocation(id), data));
               }
            }
        }catch (Exception e){
            AlexsMobs.LOGGER.warn("Encountered error building alexsmobs biome config .json files");
            e.printStackTrace();
        }
		init = true;
    }

    public static boolean test(Pair<String, SpawnBiomeData> entry, Holder<Biome> biome, ResourceLocation name){
    	if(!init){
    		return false;
		}
		return biomeConfigValues.get(entry.getKey()).matches(biome, name);
	}

	public static boolean test(Pair<String, SpawnBiomeData> spawns, Holder<Biome> biome) {
		return test(spawns, biome, ForgeRegistries.BIOMES.getKey(biome.value()));
	}
}
