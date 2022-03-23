package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;

public class AMBlockRegistry {
    public static final BlockBehaviour.Properties PURPUR_PLANKS_PROPERTIES = BlockBehaviour.Properties.of(Material.NETHER_WOOD, MaterialColor.COLOR_PINK).strength(0.5F, 1.0F).sound(SoundType.WOOD);

    public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, AlexsMobs.MODID);
    public static final RegistryObject<Block> BANANA_PEEL = DEF_REG.register("banana_peel", () -> new BlockBananaPeel());
    public static final RegistryObject<Block> HUMMINGBIRD_FEEDER = DEF_REG.register("hummingbird_feeder", () -> new BlockHummingbirdFeeder());
    public static final RegistryObject<Block> CROCODILE_EGG = DEF_REG.register("crocodile_egg", () -> new BlockCrocodileEgg());
    public static final RegistryObject<Block> GUSTMAKER = DEF_REG.register("gustmaker", () -> new BlockGustmaker());
    public static final RegistryObject<Block> STRADDLITE_BLOCK = DEF_REG.register("straddlite_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(1.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS)));
    public static final RegistryObject<Block> LEAFCUTTER_ANTHILL = DEF_REG.register("leafcutter_anthill", () -> new BlockLeafcutterAnthill());
    public static final RegistryObject<Block> LEAFCUTTER_ANT_CHAMBER = DEF_REG.register("leafcutter_ant_chamber", () -> new BlockLeafcutterAntChamber());
    public static final RegistryObject<Block> CAPSID = DEF_REG.register("capsid", () -> new BlockCapsid());
    public static final RegistryObject<Block> VOID_WORM_BEAK = DEF_REG.register("void_worm_beak", () -> new BlockVoidWormBeak());
    public static final RegistryObject<Block> VOID_WORM_EFFIGY = DEF_REG.register("void_worm_effigy", () -> new BlockVoidWormEffigy());
    public static final RegistryObject<Block> TERRAPIN_EGG = DEF_REG.register("terrapin_egg", () -> new BlockTerrapinEgg());
    public static final RegistryObject<Block> RAINBOW_GLASS = DEF_REG.register("rainbow_glass", () -> new BlockRainbowGlass());
    public static final RegistryObject<Block> BISON_FUR_BLOCK = DEF_REG.register("bison_fur_block", () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN).strength(0.6F, 1.0F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BISON_CARPET = DEF_REG.register("bison_carpet", () -> new BlockBisonCarpet());
    /*
        public static final RegistryObject<Block> PURPUR_PLANKS = DEF_REG.register("purpur_planks", () -> new Block(PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> PURPUR_PLANKS_STAIRS = DEF_REG.register("purpur_planks_stairs", () -> new StairBlock(PURPUR_PLANKS.get().defaultBlockState(), PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> PURPUR_PLANKS_SLAB = DEF_REG.register("purpur_planks_slab", () -> new SlabBlock(PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> PURPUR_PLANKS_WALL = DEF_REG.register("purpur_planks_wall", () -> new WallBlock(PURPUR_PLANKS_PROPERTIES));;
    public static final RegistryObject<Block> END_PIRATE_DOOR = DEF_REG.register("end_pirate_door", () -> new BlockEndPirateDoor());
    public static final RegistryObject<Block> END_PIRATE_TRAPDOOR = DEF_REG.register("end_pirate_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.TERRACOTTA_PURPLE).lightLevel((state) -> 3).strength(3.0F).sound(SoundType.GLASS).noOcclusion()));;
    public static final RegistryObject<Block> END_PIRATE_ANCHOR = DEF_REG.register("end_pirate_anchor", () -> new BlockEndPirateAnchor());
    public static final RegistryObject<Block> END_PIRATE_ANCHOR_WINCH = DEF_REG.register("end_pirate_anchor_winch", () -> new BlockEndPirateAnchorWinch());
    public static final RegistryObject<Block> END_PIRATE_SHIP_WHEEL = DEF_REG.register("end_pirate_ship_wheel", () -> new BlockEndPirateShipWheel());
    public static final RegistryObject<Block> END_PIRATE_FLAG = DEF_REG.register("end_pirate_flag", () -> new BlockEndPirateFlag());
    public static final RegistryObject<Block> ENDER_RESIDUE = DEF_REG.register("ender_residue", () -> new BlockEnderResidue());
    public static final RegistryObject<Block> PHANTOM_SAIL = DEF_REG.register("phantom_sail", () -> new BlockEndPirateSail(false));
    public static final RegistryObject<Block> SPECTRE_SAIL = DEF_REG.register("spectre_sail", () -> new BlockEndPirateSail(true));

     */
}
