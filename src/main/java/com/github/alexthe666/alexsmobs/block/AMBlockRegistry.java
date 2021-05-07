package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMBlockRegistry {

    public static final Block BANANA_PEEL = new BlockBananaPeel();
    public static final Block CROCODILE_EGG = new BlockCrocodileEgg();
    public static final Block GUSTMAKER = new BlockGustmaker();
    public static final Block LEAFCUTTER_ANTHILL = new BlockLeafcutterAnthill();
    public static final Block LEAFCUTTER_ANT_CHAMBER = new BlockLeafcutterAntChamber();
    public static final Block CAPSID = new BlockCapsid();

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
