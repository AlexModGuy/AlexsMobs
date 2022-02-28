package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMBlockRegistry {

    public static final Block BANANA_PEEL = new BlockBananaPeel();
    public static final Block HUMMINGBIRD_FEEDER = new BlockHummingbirdFeeder();
    public static final Block CROCODILE_EGG = new BlockCrocodileEgg();
    public static final Block GUSTMAKER = new BlockGustmaker();
    public static final Block STRADDLITE_BLOCK = new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(1.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS)).setRegistryName("alexsmobs:straddlite_block");
    public static final Block LEAFCUTTER_ANTHILL = new BlockLeafcutterAnthill();
    public static final Block LEAFCUTTER_ANT_CHAMBER = new BlockLeafcutterAntChamber();
    public static final Block CAPSID = new BlockCapsid();
    public static final Block VOID_WORM_BEAK = new BlockVoidWormBeak();
    public static final Block VOID_WORM_EFFIGY = new BlockVoidWormEffigy();
    public static final Block TERRAPIN_EGG = new BlockTerrapinEgg();
    public static final Block RAINBOW_GLASS = new BlockRainbowGlass();
    public static final Block BISON_FUR_BLOCK = new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN).strength(0.6F, 1.0F).sound(SoundType.WOOL)).setRegistryName("alexsmobs:bison_fur_block");
    public static final Block BISON_CARPET = new BlockBisonCarpet();
    public static final Block PURPUR_PLANKS = new Block(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MaterialColor.COLOR_PINK).strength(0.5F, 1.0F).sound(SoundType.WOOD)).setRegistryName("alexsmobs:purpur_planks");
    public static final Block PURPUR_PLANKS_STAIRS = new StairBlock(PURPUR_PLANKS.defaultBlockState(), BlockBehaviour.Properties.copy(PURPUR_PLANKS)).setRegistryName("alexsmobs:purpur_planks_stairs");
    public static final Block PURPUR_PLANKS_SLAB = new SlabBlock(BlockBehaviour.Properties.copy(PURPUR_PLANKS)).setRegistryName("alexsmobs:purpur_planks_slab");
    public static final Block PURPUR_PLANKS_WALL = new WallBlock(BlockBehaviour.Properties.copy(PURPUR_PLANKS)).setRegistryName("alexsmobs:purpur_planks_wall");
    public static final Block END_PIRATE_DOOR = new BlockEndPirateDoor();
    public static final Block END_PIRATE_TRAPDOOR = new TrapDoorBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.TERRACOTTA_PURPLE).lightLevel((state) -> 3).strength(3.0F).sound(SoundType.GLASS).noOcclusion()).setRegistryName("alexsmobs:end_pirate_trapdoor");
    public static final Block END_PIRATE_ANCHOR = new BlockEndPirateAnchor();
    public static final Block END_PIRATE_ANCHOR_WINCH = new BlockEndPirateAnchorWinch();
    public static final Block END_PIRATE_SHIP_WHEEL = new BlockEndPirateShipWheel();
    public static final Block END_PIRATE_FLAG = new BlockEndPirateFlag();
    public static final Block ENDER_RESIDUE = new BlockEnderResidue();
    public static final Block PHANTOM_SAIL = new BlockEndPirateSail(false);
    public static final Block SPECTRE_SAIL = new BlockEndPirateSail(true);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        try {
            for (Field f : AMBlockRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Block) {
                    event.getRegistry().register((Block) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
