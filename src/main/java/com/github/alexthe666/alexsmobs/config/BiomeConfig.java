package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeConfig {
	public static List<? extends String> grizzlyBear = Lists.newArrayList(DefaultBiomes.GRIZZLY_BEAR);
	public static List<? extends String> roadrunner = Lists.newArrayList(DefaultBiomes.ROADRUNNER);
	public static List<? extends String> boneSerpent = Lists.newArrayList(DefaultBiomes.BONE_SERPENT);
	public static List<? extends String> gazelle = Lists.newArrayList(DefaultBiomes.GAZELLE);
	public static List<? extends String> crocodile = Lists.newArrayList(DefaultBiomes.CROCODILE);
	public static List<? extends String> fly = Lists.newArrayList(DefaultBiomes.FLY);
	public static List<? extends String> hummingbird = Lists.newArrayList(DefaultBiomes.HUMMINGBIRD);
	public static List<? extends String> orca = Lists.newArrayList(DefaultBiomes.ORCA);
	public static List<? extends String> sunbird = Lists.newArrayList(DefaultBiomes.SUNBIRD);
	public static List<? extends String> gorilla = Lists.newArrayList(DefaultBiomes.GORILLA);
	public static List<? extends String> crimsonMosquito = Lists.newArrayList(DefaultBiomes.CRIMSON_MOSQUITO);
	public static List<? extends String> rattlesnake = Lists.newArrayList(DefaultBiomes.RATTLESNAKE);
	public static List<? extends String> endergrade = Lists.newArrayList(DefaultBiomes.ENDERGRADE);
	public static List<? extends String> hammerheadShark = Lists.newArrayList(DefaultBiomes.HAMMERHEAD);
	public static List<? extends String> lobster = Lists.newArrayList(DefaultBiomes.LOBSTER);
	public static List<? extends String> komodoDragon = Lists.newArrayList(DefaultBiomes.KOMODO_DRAGON);
	public static List<? extends String> capuchinMonkey = Lists.newArrayList(DefaultBiomes.CAPUCHIN_MONKEY);
	public static List<? extends String> caveCentipede = Lists.newArrayList(DefaultBiomes.CENTIPEDE);
	public static List<? extends String> warpedToad = Lists.newArrayList(DefaultBiomes.WARPED_TOAD);
	public static List<? extends String> moose = Lists.newArrayList(DefaultBiomes.MOOSE);

     public static Map<String, ForgeConfigSpec.ConfigValue<List<? extends String>>> biomeConfigValues = new HashMap<>();

    public BiomeConfig(final ForgeConfigSpec.Builder builder) {
        builder.comment(
    		"Biome config",
    		"To filter biomes by registry name \"mod_id:biome_id\"",
    		"To filter biomes by category \"@category\"",
    		"To filter biomes by tags \"#tag\"",
    		"\tExamples:",
                "\t\t\"minecraft:plains\"",
    		"\t\t\"@desert\"",
    		"\t\t\"#overworld\"",
    		"",
    		"If you want to exclude biomes put a ! before the biome identifier",
    		"\tExamples:",
    		"\t\t\"!minecraft:plains\"",
    		"\t\t\"!@desert\"",
    		"\t\t\"!#nether\"",
    		"",
    		"If you want to include biomes that would be satisfied by any in a set use |",
    		"\tExamples:",
    		"\t\t\"|minecraft:plains\"",
    		"\t\t\"|@desert\"",
    		"\t\t\"|#nether\"",
    		"",
    		"If you want a condition that MUST be satisfied use an & before the biome identifier",
    		"Please note using this on a registry name wouldn't be that useful",
    		"\tExamples:",
    		"\t\t\"&minecraft:plains\"",
    		"\t\t\"&@forest\"",
    		"\t\t\"&#overworld\"",
    		"",
    		"NOTE: Any entry without a !, |, or & symbol has a higher precedence",
    		"A list like [\"!minecraft:plains\", \"#overworld\"] would still see the plains as a viable biome",
    		"",
    		"Finally, you can create a expression that can be evaluated by itself using a + to combine identifiers",
    		"\tExamples:",
    		"\t\t\"!#hot+!#dry+!#mountain\"",
    		"",
    		"These expressions can be used to filter biomes in a lot of ways",
    		"Lets say we don't want anything to spawn in any place dry and sandy",
    		"\t\"!#dry+!#sandy\"",
    		"",
    		"But there is a hot place we want them to spawn that's also wet",
    		"\t\"#hot+#wet\"",
    		"",
    		"We just put them as separate values in the list and that'll work out",
    		"\t[\"!#dry+!#sandy\",\"#hot+#wet\"]",
    		"",
    		"NOTE: Any entry that's an expression will not be affected by anything else in the list")
        	.push("biome_configs");
        try {
            for (Field f : BiomeConfig.class.getDeclaredFields()) {
                Object obj = f.get(null);
               if(obj instanceof List){
                   biomeConfigValues.putIfAbsent(f.getName(), builder.defineList(f.getName(), (List)obj, o -> o instanceof String));
               }
            }
        }catch (Exception e){
            AlexsMobs.LOGGER.warn("Encountered error building alexsmobs-biomes.toml");
            e.printStackTrace();
        }
    }

    public static void bake(ModConfig config) {
        try {
            for (Field f : BiomeConfig.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if(obj instanceof List){
                    ForgeConfigSpec.ConfigValue<List<? extends String>> configValue = ConfigHolder.BIOME.biomeConfigValues.get(f.getName());
                    if(config != null){
                        f.set(null, configValue.get());
                    }
                }
            }
        }catch (Exception e){
            AlexsMobs.LOGGER.warn("Encountered error building alexsmobs-biomes.toml");
            e.printStackTrace();
        }
    }
}
