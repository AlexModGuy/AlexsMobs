package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
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
    public static final Block TERRAPIN_EGG = new BlockTerrapinEgg();
    public static final Block RAINBOW_GLASS = new BlockRainbowGlass();

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
