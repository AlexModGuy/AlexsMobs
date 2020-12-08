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
    public static int capuchinMonkeySpawnWeight = 55;
    public static int capuchinMonkeySpawnRolls = 0;
    public static int caveCentipedeSpawnWeight = 8;
    public static int caveCentipedeSpawnRolls = 1;
    public static int caveCentipedeSpawnHeight = 30;
    public static double lavaOpacity = 0.65F;
    public static boolean lavaBottleEnabled = true;
    public static boolean bananasDropFromLeaves = true;
    public static int bananaChance = 200;
    public static boolean spidersAttackFlies = true;
    public static boolean giveBookOnStartup = true;

    public static void bake(ModConfig config) {
        try {
            lavaOpacity = ConfigHolder.COMMON.lavaOpacity.get();
            grizzlyBearSpawnWeight = ConfigHolder.COMMON.grizzlyBearSpawnWeight.get();
            grizzlyBearSpawnRolls = ConfigHolder.COMMON.grizzlyBearSpawnRolls.get();
            roadrunnerSpawnWeight = ConfigHolder.COMMON.roadrunnerSpawnWeight.get();
            roadrunnerSpawnRolls = ConfigHolder.COMMON.roadrunnerSpawnRolls.get();
            boneSerpentSpawnWeight = ConfigHolder.COMMON.boneSerpentSpawnWeight.get();
            boneSeprentSpawnRolls = ConfigHolder.COMMON.boneSeprentSpawnRolls.get();
            gazelleSpawnWeight = ConfigHolder.COMMON.gazelleSpawnWeight.get();
            gazelleSpawnRolls = ConfigHolder.COMMON.gazelleSpawnRolls.get();
            crocodileSpawnWeight = ConfigHolder.COMMON.crocodileSpawnWeight.get();
            crocSpawnRolls = ConfigHolder.COMMON.crocSpawnRolls.get();
            flySpawnWeight = ConfigHolder.COMMON.flySpawnWeight.get();
            flySpawnRolls = ConfigHolder.COMMON.flySpawnRolls.get();
            hummingbirdSpawnWeight = ConfigHolder.COMMON.hummingbirdSpawnWeight.get();
            hummingbirdSpawnRolls = ConfigHolder.COMMON.hummingbirdSpawnRolls.get();
            orcaSpawnWeight = ConfigHolder.COMMON.orcaSpawnWeight.get();
            orcaSpawnRolls = ConfigHolder.COMMON.orcaSpawnRolls.get();
            sunbirdSpawnWeight = ConfigHolder.COMMON.sunbirdSpawnWeight.get();
            sunbirdSpawnRolls = ConfigHolder.COMMON.sunbirdSpawnRolls.get();
            gorillaSpawnWeight = ConfigHolder.COMMON.gorillaSpawnWeight.get();
            gorillaSpawnRolls = ConfigHolder.COMMON.gorillaSpawnRolls.get();
            crimsonMosquitoSpawnWeight = ConfigHolder.COMMON.crimsonMosquitoSpawnWeight.get();
            crimsonMosquitoSpawnRolls = ConfigHolder.COMMON.crimsonMosquitoSpawnRolls.get();
            rattlesnakeSpawnWeight = ConfigHolder.COMMON.rattlesnakeSpawnWeight.get();
            rattlesnakeSpawnRolls = ConfigHolder.COMMON.rattlesnakeSpawnRolls.get();
            endergradeSpawnWeight = ConfigHolder.COMMON.endergradeSpawnWeight.get();
            endergradeSpawnRolls = ConfigHolder.COMMON.endergradeSpawnRolls.get();
            hammerheadSharkSpawnWeight = ConfigHolder.COMMON.hammerheadSharkSpawnWeight.get();
            hammerheadSharkSpawnRolls = ConfigHolder.COMMON.hammerheadSharkSpawnRolls.get();
            lobsterSpawnWeight = ConfigHolder.COMMON.lobsterSpawnWeight.get();
            lobsterSpawnRolls = ConfigHolder.COMMON.lobsterSpawnRolls.get();
            komodoDragonSpawnWeight = ConfigHolder.COMMON.komodoDragonSpawnWeight.get();
            komodoDragonSpawnRolls = ConfigHolder.COMMON.komodoDragonSpawnRolls.get();
            capuchinMonkeySpawnWeight = ConfigHolder.COMMON.capuchinMonkeySpawnWeight.get();
            capuchinMonkeySpawnRolls = ConfigHolder.COMMON.capuchinMonkeySpawnRolls.get();
            caveCentipedeSpawnWeight = ConfigHolder.COMMON.caveCentipedeSpawnWeight.get();
            caveCentipedeSpawnRolls = ConfigHolder.COMMON.caveCentipedeSpawnRolls.get();
            caveCentipedeSpawnHeight = ConfigHolder.COMMON.caveCentipedeSpawnHeight.get();
            lavaBottleEnabled = ConfigHolder.COMMON.lavaBottleEnabled.get();
            bananasDropFromLeaves = ConfigHolder.COMMON.bananasDropFromLeaves.get();
            spidersAttackFlies = ConfigHolder.COMMON.spidersAttackFlies.get();
            bananaChance = ConfigHolder.COMMON.bananaChance.get();
            giveBookOnStartup = ConfigHolder.COMMON.giveBookOnStartup.get();
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("An exception was caused trying to load the config for Alex's Mobs.");
            e.printStackTrace();
        }
    }

}
