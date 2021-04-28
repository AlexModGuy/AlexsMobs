package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMPointOfInterestRegistry {

    public static final PointOfInterestType END_PORTAL_FRAME = new PointOfInterestType("alexsmobs:end_portal_frame", PointOfInterestType.getAllStates(Blocks.END_PORTAL_FRAME), 32, 6);
    public static final PointOfInterestType LEAFCUTTER_ANT_HILL = new PointOfInterestType("alexsmobs:leafcutter_anthill", PointOfInterestType.getAllStates(AMBlockRegistry.LEAFCUTTER_ANTHILL), 32, 6);
    public static final PointOfInterestType BEACON = new PointOfInterestType("alexsmobs:am_beacon", PointOfInterestType.getAllStates(Blocks.BEACON), 32, 6);

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().register(END_PORTAL_FRAME.setRegistryName("alexsmobs:end_portal_frame"));
        event.getRegistry().register(LEAFCUTTER_ANT_HILL.setRegistryName("alexsmobs:leafcutter_anthill"));
        event.getRegistry().register(BEACON.setRegistryName("alexsmobs:am_beacon"));
    }
}
