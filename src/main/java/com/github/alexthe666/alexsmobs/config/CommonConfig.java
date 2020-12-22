package com.github.alexthe666.alexsmobs.config;

import com.google.common.collect.Lists;
import cpw.mods.modlauncher.LaunchPluginHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {

    public final ForgeConfigSpec.DoubleValue lavaOpacity;
    public final ForgeConfigSpec.BooleanValue lavaBottleEnabled;
    public final ForgeConfigSpec.BooleanValue spidersAttackFlies;
    public final ForgeConfigSpec.BooleanValue wolvesAttackMoose;
    public final ForgeConfigSpec.BooleanValue bananasDropFromLeaves;
    public final ForgeConfigSpec.IntValue bananaChance;
    public final ForgeConfigSpec.IntValue grizzlyBearSpawnWeight;
    public final ForgeConfigSpec.IntValue grizzlyBearSpawnRolls;
    public final ForgeConfigSpec.IntValue roadrunnerSpawnWeight;
    public final ForgeConfigSpec.IntValue roadrunnerSpawnRolls;
    public final ForgeConfigSpec.IntValue boneSerpentSpawnWeight;
    public final ForgeConfigSpec.IntValue boneSeprentSpawnRolls;
    public final ForgeConfigSpec.IntValue gazelleSpawnWeight;
    public final ForgeConfigSpec.IntValue gazelleSpawnRolls;
    public final ForgeConfigSpec.IntValue crocodileSpawnWeight;
    public final ForgeConfigSpec.IntValue crocSpawnRolls;
    public final ForgeConfigSpec.IntValue flySpawnWeight;
    public final ForgeConfigSpec.IntValue flySpawnRolls;
    public final ForgeConfigSpec.IntValue hummingbirdSpawnWeight;
    public final ForgeConfigSpec.IntValue hummingbirdSpawnRolls;
    public final ForgeConfigSpec.IntValue orcaSpawnWeight;
    public final ForgeConfigSpec.IntValue orcaSpawnRolls;
    public final ForgeConfigSpec.IntValue sunbirdSpawnWeight;
    public final ForgeConfigSpec.IntValue sunbirdSpawnRolls;
    public final ForgeConfigSpec.IntValue gorillaSpawnWeight;
    public final ForgeConfigSpec.IntValue gorillaSpawnRolls;
    public final ForgeConfigSpec.IntValue crimsonMosquitoSpawnWeight;
    public final ForgeConfigSpec.IntValue crimsonMosquitoSpawnRolls;
    public final ForgeConfigSpec.IntValue rattlesnakeSpawnWeight;
    public final ForgeConfigSpec.IntValue rattlesnakeSpawnRolls;
    public final ForgeConfigSpec.IntValue endergradeSpawnWeight;
    public final ForgeConfigSpec.IntValue endergradeSpawnRolls;
    public final ForgeConfigSpec.IntValue hammerheadSharkSpawnWeight;
    public final ForgeConfigSpec.IntValue hammerheadSharkSpawnRolls;
    public final ForgeConfigSpec.IntValue lobsterSpawnWeight;
    public final ForgeConfigSpec.IntValue lobsterSpawnRolls;
    public final ForgeConfigSpec.IntValue komodoDragonSpawnWeight;
    public final ForgeConfigSpec.IntValue komodoDragonSpawnRolls;
    public final ForgeConfigSpec.IntValue capuchinMonkeySpawnWeight;
    public final ForgeConfigSpec.IntValue capuchinMonkeySpawnRolls;
    public final ForgeConfigSpec.IntValue caveCentipedeSpawnWeight;
    public final ForgeConfigSpec.IntValue caveCentipedeSpawnRolls;
    public final ForgeConfigSpec.IntValue caveCentipedeSpawnHeight;
    public final ForgeConfigSpec.IntValue warpedToadSpawnWeight;
    public final ForgeConfigSpec.IntValue warpedToadSpawnRolls;
    public final ForgeConfigSpec.IntValue mooseSpawnWeight;
    public final ForgeConfigSpec.IntValue mooseSpawnRolls;
    public final ForgeConfigSpec.IntValue mimicubeSpawnWeight;
    public final ForgeConfigSpec.IntValue mimicubeSpawnRolls;
    public final ForgeConfigSpec.IntValue raccoonSpawnWeight;
    public final ForgeConfigSpec.IntValue raccoonSpawnRolls;
    public final ForgeConfigSpec.IntValue blobfishSpawnWeight;
    public final ForgeConfigSpec.IntValue blobfishSpawnRolls;
    public final ForgeConfigSpec.IntValue blobfishSpawnHeight;
    public final ForgeConfigSpec.BooleanValue giveBookOnStartup;
    public final ForgeConfigSpec.BooleanValue mimicubeSpawnInEndCity;
    public final ForgeConfigSpec.BooleanValue mimicreamRepair;
    public final ForgeConfigSpec.ConfigValue mimicreamBlacklist;

    public CommonConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        giveBookOnStartup = buildBoolean(builder, "giveBookOnStartup", "all", true, "Whether all players should get an Animal Dictionary when joining the world for the first time.");
        lavaOpacity = buildDouble(builder, "lavaVisionOpacity", "all", 0.65D, 0.01D, 1D, "Lava Opacity for the Lava Vision Potion.");
        bananasDropFromLeaves = buildBoolean(builder, "bananasDropFromLeaves", "all", true, "Whether bananas should drop from blocks tagged with #alexsmobs:drops_bananas");
        bananaChance = buildInt(builder, "bananaChance", "all", AMConfig.bananaChance, 0, Integer.MAX_VALUE, "1 out of this number chance for leaves to drop a banana when broken. Fortune is automatically factored in");
        spidersAttackFlies = buildBoolean(builder, "spidersAttackFlies", "all", true, "Whether spiders should target fly mobs.");
        wolvesAttackMoose = buildBoolean(builder, "wolvesAttackMoose", "all", true, "Whether wolves should target moose mobs.");
        lavaBottleEnabled = buildBoolean(builder, "lavaBottleEnabled", "all", true, "Whether lava can be bottled with a right click of a glass bottle.");
        caveCentipedeSpawnHeight = buildInt(builder, "caveCentipedeSpawnHeight", "all", AMConfig.caveCentipedeSpawnHeight, 0, 256, "Maximum world y-level that cave centipedes can spawn at");
        blobfishSpawnHeight = buildInt(builder, "blobfishSpawnHeight", "all", AMConfig.blobfishSpawnHeight, 0, 256, "Maximum world y-level that blobfish can spawn at");
        mimicubeSpawnInEndCity = buildBoolean(builder, "mimicubeSpawnInEndCity", "all", true, "Whether mimicubes spawns should be restricted solely to the end city structure or to whatever biome is specified in their respective biome config.");
        mimicreamRepair = buildBoolean(builder, "mimicreamRepair", "all", true, "Whether mimicream can be used to duplicate items.");
        mimicreamBlacklist = builder.comment("Blacklist for items that mimicream cannot make a copy of. Ex: \"minecraft:stone_sword\", \"alexsmobs:blood_sprayer\"").defineList("mimicreamBlacklist", Lists.newArrayList("alexsmobs:blood_sprayer"), o -> o instanceof String);
        builder.push("spawning");
        grizzlyBearSpawnWeight = buildInt(builder, "grizzlyBearSpawnWeight", "spawns", AMConfig.grizzlyBearSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        grizzlyBearSpawnRolls = buildInt(builder, "grizzlyBearSpawnRolls", "spawns", AMConfig.grizzlyBearSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        roadrunnerSpawnWeight = buildInt(builder, "roadrunnerSpawnWeight", "spawns", AMConfig.roadrunnerSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        roadrunnerSpawnRolls = buildInt(builder, "roadrunnerSpawnRolls", "spawns", AMConfig.roadrunnerSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        boneSerpentSpawnWeight = buildInt(builder, "boneSerpentSpawnWeight", "spawns", AMConfig.boneSerpentSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        boneSeprentSpawnRolls = buildInt(builder, "boneSeprentSpawnRolls", "spawns", AMConfig.boneSeprentSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        gazelleSpawnWeight = buildInt(builder, "gazelleSpawnWeight", "spawns", AMConfig.gazelleSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        gazelleSpawnRolls = buildInt(builder, "gazelleSpawnRolls", "spawns", AMConfig.gazelleSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        crocodileSpawnWeight = buildInt(builder, "crocodileSpawnWeight", "spawns", AMConfig.crocodileSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        crocSpawnRolls = buildInt(builder, "crocSpawnRolls", "spawns", AMConfig.crocSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        flySpawnWeight = buildInt(builder, "flySpawnWeight", "spawns", AMConfig.flySpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        flySpawnRolls = buildInt(builder, "flySpawnRolls", "spawns", AMConfig.flySpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        hummingbirdSpawnWeight = buildInt(builder, "hummingbirdSpawnWeight", "spawns", AMConfig.hummingbirdSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        hummingbirdSpawnRolls = buildInt(builder, "hummingbirdSpawnRolls", "spawns", AMConfig.flySpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        orcaSpawnWeight = buildInt(builder, "orcaSpawnWeight", "spawns", AMConfig.orcaSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        orcaSpawnRolls = buildInt(builder, "orcaSpawnRolls", "spawns", AMConfig.orcaSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        sunbirdSpawnWeight = buildInt(builder, "sunbirdSpawnWeight", "spawns", AMConfig.sunbirdSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        sunbirdSpawnRolls= buildInt(builder, "sunbirdSpawnRolls", "spawns", AMConfig.sunbirdSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        gorillaSpawnWeight = buildInt(builder, "gorillaSpawnWeight", "spawns", AMConfig.gorillaSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        gorillaSpawnRolls= buildInt(builder, "gorillaSpawnRolls", "spawns", AMConfig.gorillaSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        crimsonMosquitoSpawnWeight = buildInt(builder, "crimsonMosquitoSpawnWeight", "spawns", AMConfig.crimsonMosquitoSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        crimsonMosquitoSpawnRolls = buildInt(builder, "crimsonMosquitoSpawnRolls", "spawns", AMConfig.crimsonMosquitoSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        rattlesnakeSpawnWeight = buildInt(builder, "rattlesnakeSpawnWeight", "spawns", AMConfig.rattlesnakeSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        rattlesnakeSpawnRolls = buildInt(builder, "rattlesnakeSpawnRolls", "spawns", AMConfig.rattlesnakeSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        endergradeSpawnWeight = buildInt(builder, "endergradeSpawnWeight", "spawns", AMConfig.endergradeSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        endergradeSpawnRolls = buildInt(builder, "endergradeSpawnRolls", "spawns", AMConfig.endergradeSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        hammerheadSharkSpawnWeight = buildInt(builder, "hammerheadSharkSpawnWeight", "spawns", AMConfig.hammerheadSharkSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        hammerheadSharkSpawnRolls = buildInt(builder, "hammerheadSharkSpawnRolls", "spawns", AMConfig.hammerheadSharkSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        lobsterSpawnWeight = buildInt(builder, "lobsterSpawnWeight", "spawns", AMConfig.lobsterSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        lobsterSpawnRolls = buildInt(builder, "lobsterSpawnRolls", "spawns", AMConfig.lobsterSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        komodoDragonSpawnWeight = buildInt(builder, "komodoDragonSpawnWeight", "spawns", AMConfig.komodoDragonSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        komodoDragonSpawnRolls = buildInt(builder, "komodoDragonSpawnRolls", "spawns", AMConfig.komodoDragonSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        capuchinMonkeySpawnWeight = buildInt(builder, "capuchinMonkeySpawnWeight", "spawns", AMConfig.capuchinMonkeySpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        capuchinMonkeySpawnRolls = buildInt(builder, "capuchinMonkeySpawnRolls", "spawns", AMConfig.capuchinMonkeySpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        caveCentipedeSpawnWeight = buildInt(builder, "caveCentipedeSpawnWeight", "spawns", AMConfig.caveCentipedeSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        caveCentipedeSpawnRolls = buildInt(builder, "caveCentipedeSpawnRolls", "spawns", AMConfig.caveCentipedeSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        warpedToadSpawnWeight = buildInt(builder, "warpedToadSpawnWeight", "spawns", AMConfig.warpedToadSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        warpedToadSpawnRolls = buildInt(builder, "warpedToadSpawnRolls", "spawns", AMConfig.warpedToadSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        mooseSpawnWeight = buildInt(builder, "mooseSpawnWeight", "spawns", AMConfig.mooseSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        mooseSpawnRolls = buildInt(builder, "mooseSpawnRolls", "spawns", AMConfig.mooseSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        mimicubeSpawnWeight = buildInt(builder, "mimicubeSpawnWeight", "spawns", AMConfig.mimicubeSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        mimicubeSpawnRolls = buildInt(builder, "mimicubeSpawnRolls", "spawns", AMConfig.mimicubeSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        raccoonSpawnWeight = buildInt(builder, "raccoonSpawnWeight", "spawns", AMConfig.raccoonSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        raccoonSpawnRolls = buildInt(builder, "raccoonSpawnRolls", "spawns", AMConfig.raccoonSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
        blobfishSpawnWeight = buildInt(builder, "blobfishSpawnWeight", "spawns", AMConfig.blobfishSpawnWeight, 0, 1000, "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn");
        blobfishSpawnRolls = buildInt(builder, "blobfishSpawnRolls", "spawns", AMConfig.blobfishSpawnRolls, 0, Integer.MAX_VALUE, "Random roll chance to enable mob spawning. Higher number = lower chance of spawning");
    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, String catagory, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.DoubleValue buildDouble(ForgeConfigSpec.Builder builder, String name, String catagory, double defaultValue, double min, double max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
}
