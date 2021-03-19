package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeConfig;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BiomeConfig {
	public static Pair<String, SpawnBiomeData> grizzlyBear = Pair.of("alexsmobs:grizzly_bear_spawns", DefaultBiomes.GRIZZLY_BEAR);
	public static Pair<String, SpawnBiomeData> roadrunner = Pair.of("alexsmobs:roadrunner_spawns", DefaultBiomes.ROADRUNNER);
	public static Pair<String, SpawnBiomeData> boneSerpent = Pair.of("alexsmobs:bone_serpent_spawns", DefaultBiomes.BONE_SERPENT);
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
	public static Pair<String, SpawnBiomeData> blobfish = Pair.of("alexsmobs:blobfish_spawns", DefaultBiomes.BLOBFISH);
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
	public static Pair<String, SpawnBiomeData> platypus = Pair.of("alexsmobs:platypus_spawns", DefaultBiomes.PLATYPUS);
	public static Pair<String, SpawnBiomeData> dropbear = Pair.of("alexsmobs:dropbear_spawns", DefaultBiomes.DROPBEAR);
	public static Pair<String, SpawnBiomeData> tasmanianDevil = Pair.of("alexsmobs:tasmanian_devil_spawns", DefaultBiomes.TASMANIAN_DEVIL);
	public static Pair<String, SpawnBiomeData> kangaroo = Pair.of("alexsmobs:kangaroo_spawns", DefaultBiomes.SAVANNA_AND_MESA);

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

    public static boolean test(Pair<String, SpawnBiomeData> entry, Biome biome){
    	if(!init){
    		return false;
		}
		return biomeConfigValues.get(entry.getKey()).matches(biome);
	}
}
