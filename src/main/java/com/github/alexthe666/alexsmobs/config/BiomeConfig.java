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
	public static Pair<String, SpawnBiomeData> grizzlyBear = Pair.of("alexsmobs:grizzly_bear_spawns", DefaultBiomes.GRIZZLY_BEAR);
	public static Pair<String, SpawnBiomeData> roadrunner = Pair.of("alexsmobs:roadrunner_spawns", DefaultBiomes.ROADRUNNER);
	public static Pair<String, SpawnBiomeData> boneSerpent = Pair.of("alexsmobs:bone_serpent_spawns", DefaultBiomes.ALL_NETHER);
	public static Pair<String, SpawnBiomeData> gazelle = Pair.of("alexsmobs:gazelle_spawns", DefaultBiomes.GAZELLE);
	public static Pair<String, SpawnBiomeData> crocodile = Pair.of("alexsmobs:crocodile_spawns", DefaultBiomes.CROCODILE);
	public static Pair<String, SpawnBiomeData> fly = Pair.of("alexsmobs:fly_spawns", DefaultBiomes.FLY);
	public static Pair<String, SpawnBiomeData> hummingbird = Pair.of("alexsmobs:hummingbird_spawns", DefaultBiomes.HUMMINGBIRD);
	public static Pair<String, SpawnBiomeData> orca = Pair.of("alexsmobs:orca_spawns", DefaultBiomes.ORCA);
	public static Pair<String, SpawnBiomeData> sunbird = Pair.of("alexsmobs:sunbird_spawns", DefaultBiomes.SUNBIRD);
	public static Pair<String, SpawnBiomeData> gorilla = Pair.of("alexsmobs:gorilla_spawns", DefaultBiomes.GORILLA);
	public static Pair<String, SpawnBiomeData> crimsonMosquito = Pair.of("alexsmobs:crimson_mosquito_spawns", DefaultBiomes.CRIMSON_MOSQUITO);
	public static Pair<String, SpawnBiomeData> rattlesnake = Pair.of("alexsmobs:rattlesnake_spawns", DefaultBiomes.RATTLESNAKE);
	public static Pair<String, SpawnBiomeData> endergrade = Pair.of("alexsmobs:endergrade_spawns", DefaultBiomes.ENDERGRADE);
	public static Pair<String, SpawnBiomeData> hammerheadShark = Pair.of("alexsmobs:hammerhead_shark_spawns", DefaultBiomes.HAMMERHEAD);
	public static Pair<String, SpawnBiomeData> lobster = Pair.of("alexsmobs:lobster_spawns", DefaultBiomes.LOBSTER);
	public static Pair<String, SpawnBiomeData> komodoDragon = Pair.of("alexsmobs:komodo_dragon_spawns", DefaultBiomes.KOMODO_DRAGON);
	public static Pair<String, SpawnBiomeData> capuchinMonkey = Pair.of("alexsmobs:capuchin_monkey_spawns", DefaultBiomes.CAPUCHIN_MONKEY);
	public static Pair<String, SpawnBiomeData> caveCentipede = Pair.of("alexsmobs:cave_centipede_spawns", DefaultBiomes.CENTIPEDE);
	public static Pair<String, SpawnBiomeData> warpedToad = Pair.of("alexsmobs:warped_toad_spawns", DefaultBiomes.WARPED_TOAD);
	public static Pair<String, SpawnBiomeData> moose = Pair.of("alexsmobs:moose_spawns", DefaultBiomes.MOOSE);
	public static Pair<String, SpawnBiomeData> mimicube = Pair.of("alexsmobs:mimicube_spawns", DefaultBiomes.MIMICUBE);
	public static Pair<String, SpawnBiomeData> raccoon = Pair.of("alexsmobs:raccoon_spawns", DefaultBiomes.RACCOON);
	public static Pair<String, SpawnBiomeData> blobfish = Pair.of("alexsmobs:blobfish_spawns", DefaultBiomes.DEEP_SEA);
	public static Pair<String, SpawnBiomeData> seal = Pair.of("alexsmobs:seal_spawns", DefaultBiomes.SEAL);
	public static Pair<String, SpawnBiomeData> cockroach = Pair.of("alexsmobs:cockroach_spawns", DefaultBiomes.COCKROACH);
	public static Pair<String, SpawnBiomeData> shoebill = Pair.of("alexsmobs:shoebill_spawns", DefaultBiomes.SHOEBILL);
	public static Pair<String, SpawnBiomeData> elephant = Pair.of("alexsmobs:elephant_spawns", DefaultBiomes.ELEPHANT);
	public static Pair<String, SpawnBiomeData> soulVulture = Pair.of("alexsmobs:soul_vulture_spawns", DefaultBiomes.SOUL_VULTURE);
	public static Pair<String, SpawnBiomeData> snowLeopard = Pair.of("alexsmobs:snow_leopard_spawns", DefaultBiomes.SNOW_LEOPARD);
	public static Pair<String, SpawnBiomeData> spectre = Pair.of("alexsmobs:spectre_spawns", DefaultBiomes.SPECTRE);
	public static Pair<String, SpawnBiomeData> crow = Pair.of("alexsmobs:crow_spawns", DefaultBiomes.CROW);
	public static Pair<String, SpawnBiomeData> alligatorSnappingTurtle = Pair.of("alexsmobs:alligator_snapping_turtle_spawns", DefaultBiomes.ALLIGATOR_SNAPPING_TURTLE);
	public static Pair<String, SpawnBiomeData> mungus = Pair.of("alexsmobs:mungus_spawns", DefaultBiomes.MUNGUS);
	public static Pair<String, SpawnBiomeData> mantisShrimp = Pair.of("alexsmobs:mantis_shrimp_spawns", DefaultBiomes.MANTIS_SHRIMP);
	public static Pair<String, SpawnBiomeData> guster = Pair.of("alexsmobs:guster_spawns", DefaultBiomes.GUSTER);
	public static Pair<String, SpawnBiomeData> warpedMosco = Pair.of("alexsmobs:warped_mosco_spawns", DefaultBiomes.EMPTY);
	public static Pair<String, SpawnBiomeData> straddler = Pair.of("alexsmobs:straddler_spawns", DefaultBiomes.STRADDLER);
	public static Pair<String, SpawnBiomeData> stradpole = Pair.of("alexsmobs:stradpole_spawns", DefaultBiomes.STRADDLER);
	public static Pair<String, SpawnBiomeData> emu = Pair.of("alexsmobs:emu_spawns", DefaultBiomes.SAVANNA_AND_MESA);
	public static Pair<String, SpawnBiomeData> platypus = Pair.of("alexsmobs:platypus_spawns", DefaultBiomes.ICE_FREE_RIVER);
	public static Pair<String, SpawnBiomeData> dropbear = Pair.of("alexsmobs:dropbear_spawns", DefaultBiomes.DROPBEAR);
	public static Pair<String, SpawnBiomeData> tasmanianDevil = Pair.of("alexsmobs:tasmanian_devil_spawns", DefaultBiomes.TASMANIAN_DEVIL);
	public static Pair<String, SpawnBiomeData> kangaroo = Pair.of("alexsmobs:kangaroo_spawns", DefaultBiomes.SAVANNA_AND_MESA);
	public static Pair<String, SpawnBiomeData> cachalot_whale_spawns = Pair.of("alexsmobs:cachalot_whale_spawns", DefaultBiomes.CACHALOT_WHALE);
	public static Pair<String, SpawnBiomeData> cachalot_whale_beached_spawns = Pair.of("alexsmobs:cachalot_whale_beached_spawns", DefaultBiomes.BEACHED_CACHALOT_WHALE);
	public static Pair<String, SpawnBiomeData> leafcutter_anthill_spawns = Pair.of("alexsmobs:leafcutter_anthill_spawns", DefaultBiomes.LEAFCUTTER_ANTHILL);
	public static Pair<String, SpawnBiomeData> enderiophage_spawns = Pair.of("alexsmobs:enderiophage_spawns", DefaultBiomes.ENDERIOPHAGE);
	public static Pair<String, SpawnBiomeData> baldEagle = Pair.of("alexsmobs:bald_eagle_spawns", DefaultBiomes.BALD_EAGLE);
	public static Pair<String, SpawnBiomeData> tiger = Pair.of("alexsmobs:tiger_spawns", DefaultBiomes.TIGER);
	public static Pair<String, SpawnBiomeData> tarantula_hawk = Pair.of("alexsmobs:tarantula_hawk_spawns", DefaultBiomes.DESERT);
	public static Pair<String, SpawnBiomeData> void_worm = Pair.of("alexsmobs:void_worm_spawns", DefaultBiomes.EMPTY);
	public static Pair<String, SpawnBiomeData> frilled_shark = Pair.of("alexsmobs:frilled_shark_spawns", DefaultBiomes.DEEP_SEA);
	public static Pair<String, SpawnBiomeData> mimic_octopus = Pair.of("alexsmobs:mimic_octopus_spawns", DefaultBiomes.MIMIC_OCTOPUS);
	public static Pair<String, SpawnBiomeData> seagull = Pair.of("alexsmobs:seagull_spawns", DefaultBiomes.SEAGULL);
	public static Pair<String, SpawnBiomeData> froststalker = Pair.of("alexsmobs:froststalker_spawns", DefaultBiomes.FROSTSTALKER);
	public static Pair<String, SpawnBiomeData> tusklin = Pair.of("alexsmobs:tusklin_spawns", DefaultBiomes.TUSKLIN);
	public static Pair<String, SpawnBiomeData> laviathan = Pair.of("alexsmobs:laviathan_spawns", DefaultBiomes.ALL_NETHER);
	public static Pair<String, SpawnBiomeData> cosmaw = Pair.of("alexsmobs:cosmaw_spawns", DefaultBiomes.COSMAW);
	public static Pair<String, SpawnBiomeData> toucan = Pair.of("alexsmobs:toucan_spawns", DefaultBiomes.TOUCAN);
	public static Pair<String, SpawnBiomeData> maned_wolf = Pair.of("alexsmobs:maned_wolf_spawns", DefaultBiomes.MANED_WOLF);
	public static Pair<String, SpawnBiomeData> anaconda = Pair.of("alexsmobs:anaconda_spawns", DefaultBiomes.ANACONDA);
	public static Pair<String, SpawnBiomeData> anteater = Pair.of("alexsmobs:anteater_spawns", DefaultBiomes.ANTEATER);
	public static Pair<String, SpawnBiomeData> rocky_roller = Pair.of("alexsmobs:rocky_roller_spawns", DefaultBiomes.ROCKY_ROLLER);
	public static Pair<String, SpawnBiomeData> flutter = Pair.of("alexsmobs:flutter_spawns", DefaultBiomes.FLUTTER);
	public static Pair<String, SpawnBiomeData> gelada_monkey = Pair.of("alexsmobs:gelada_monkey_spawns", DefaultBiomes.MEADOWS);
	public static Pair<String, SpawnBiomeData> jerboa = Pair.of("alexsmobs:jerboa_spawns", DefaultBiomes.DESERT);
	public static Pair<String, SpawnBiomeData> terrapin = Pair.of("alexsmobs:terrapin_spawns", DefaultBiomes.ICE_FREE_RIVER);
	public static Pair<String, SpawnBiomeData> comb_jelly = Pair.of("alexsmobs:comb_jelly_spawns", DefaultBiomes.COMB_JELLY);
	public static Pair<String, SpawnBiomeData> cosmic_cod = Pair.of("alexsmobs:cosmic_cod_spawns", DefaultBiomes.COSMIC_COD);
	public static Pair<String, SpawnBiomeData> bunfungus = Pair.of("alexsmobs:bunfungus_spawns", DefaultBiomes.MUNGUS);
	public static Pair<String, SpawnBiomeData> bison = Pair.of("alexsmobs:bison_spawns", DefaultBiomes.BISON);
	public static Pair<String, SpawnBiomeData> giant_squid = Pair.of("alexsmobs:giant_squid_spawns", DefaultBiomes.GIANT_SQUID);
	public static Pair<String, SpawnBiomeData> devils_hole_pupfish = Pair.of("alexsmobs:devils_hole_pupfish_spawns", DefaultBiomes.ALL_OVERWORLD);
	public static Pair<String, SpawnBiomeData> catfish = Pair.of("alexsmobs:catfish_spawns", DefaultBiomes.CATFISH);
	public static Pair<String, SpawnBiomeData> flying_fish = Pair.of("alexsmobs:flying_fish_spawns", DefaultBiomes.FLYING_FISH);
	public static Pair<String, SpawnBiomeData> skelewag = Pair.of("alexsmobs:skelewag_spawns", DefaultBiomes.SKELEWAG);
	public static Pair<String, SpawnBiomeData> rain_frog = Pair.of("alexsmobs:rain_frog_spawns", DefaultBiomes.DESERT);
	public static Pair<String, SpawnBiomeData> potoo = Pair.of("alexsmobs:potoo_spawns", DefaultBiomes.POTOO);
	public static Pair<String, SpawnBiomeData> mudskipper = Pair.of("alexsmobs:mudskipper_spawns", DefaultBiomes.MUDSKIPPER);
	public static Pair<String, SpawnBiomeData> rhinoceros = Pair.of("alexsmobs:rhinoceros_spawns", DefaultBiomes.RHINOCEROS);
	public static Pair<String, SpawnBiomeData> sugar_glider = Pair.of("alexsmobs:sugar_glider_spawns", DefaultBiomes.SUGAR_GLIDER);

	private static boolean init = false;
	private static Map<String, SpawnBiomeData> biomeConfigValues = new HashMap<>();

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
