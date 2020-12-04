package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraftforge.fml.config.ModConfig;

public class AMConfig {
    public static int grizzlyBearSpawnWeight = 8;
    public static int grizzlyBearSpawnRolls = 0;
    public static int roadrunnerSpawnWeight = 15;
    public static int roadrunnerSpawnRolls = 1;
    public static int boneSerpentSpawnWeight = 8;
    public static int boneSeprentSpawnRolls = 40;
    public static int gazelleSpawnWeight = 40;
    public static int gazelleSpawnRolls = 0;
    public static int crocodileSpawnWeight = 40;
    public static int crocSpawnRolls = 1;
    public static int flySpawnWeight = 3;
    public static int flySpawnRolls = 1;
    public static int hummingbirdSpawnWeight = 39;
    public static int hummingbirdSpawnRolls = 0;
    public static int orcaSpawnWeight = 2;
    public static int orcaSpawnRolls = 6;
    public static int sunbirdSpawnWeight = 2;
    public static int sunbirdSpawnRolls = 15;
    public static int gorillaSpawnWeight = 50;
    public static int gorillaSpawnRolls = 0;
    public static int crimsonMosquitoSpawnWeight = 30;
    public static int crimsonMosquitoSpawnRolls = 0;
    public static int rattlesnakeSpawnWeight = 12;
    public static int rattlesnakeSpawnRolls = 0;
    public static int endergradeSpawnWeight = 10;
    public static int endergradeSpawnRolls = 0;
    public static int hammerheadSharkSpawnWeight = 8;
    public static int hammerheadSharkSpawnRolls = 1;
    public static int lobsterSpawnWeight = 7;
    public static int lobsterSpawnRolls = 0;
    public static int komodoDragonSpawnWeight = 4;
    public static int komodoDragonSpawnRolls = 1;
    public static int capuchinMonkeySpawnWeight = 50;
    public static int capuchinMonkeySpawnRolls = 0;
    public static int caveCentipedeSpawnWeight = 8;
    public static int caveCentipedeSpawnRolls = 1;
    public static double lavaOpacity = 0.65F;

    public static void bake(ModConfig config) {
        try {
            lavaOpacity = ConfigHolder.COMMON.lavaOpacity.get();
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("An exception was caused trying to load the config for Alex's Mobs.");
            e.printStackTrace();
        }
    }

}
