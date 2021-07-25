package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockHummingbirdFeeder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMPointOfInterestRegistry {

    public static final PointOfInterestType END_PORTAL_FRAME = new PointOfInterestType("alexsmobs:end_portal_frame", PointOfInterestType.getAllStates(Blocks.END_PORTAL_FRAME), 32, 6);
    public static final PointOfInterestType LEAFCUTTER_ANT_HILL = new PointOfInterestType("alexsmobs:leafcutter_anthill", PointOfInterestType.getAllStates(AMBlockRegistry.LEAFCUTTER_ANTHILL), 32, 6);
    public static final PointOfInterestType BEACON = new PointOfInterestType("alexsmobs:am_beacon", PointOfInterestType.getAllStates(Blocks.BEACON), 32, 6);
    public static final PointOfInterestType HUMMINGBIRD_FEEDER = new PointOfInterestType("alexsmobs:hummingbird_feeder", PointOfInterestType.getAllStates(AMBlockRegistry.HUMMINGBIRD_FEEDER), 32, 6);

    private static Set<BlockState> getHummingbirdFeederStates() {
        BlockState state = AMBlockRegistry.HUMMINGBIRD_FEEDER.getDefaultState().with(BlockHummingbirdFeeder.CONTENTS, 3);
        return ImmutableSet.of(state, state.with(BlockHummingbirdFeeder.HANGING, true), state.with(BlockHummingbirdFeeder.WATERLOGGED, true), state.with(BlockHummingbirdFeeder.HANGING, true).with(BlockHummingbirdFeeder.WATERLOGGED, true));
    }

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().register(END_PORTAL_FRAME.setRegistryName("alexsmobs:end_portal_frame"));
        event.getRegistry().register(LEAFCUTTER_ANT_HILL.setRegistryName("alexsmobs:leafcutter_anthill"));
        event.getRegistry().register(BEACON.setRegistryName("alexsmobs:am_beacon"));
        event.getRegistry().register(HUMMINGBIRD_FEEDER.setRegistryName("alexsmobs:hummingbird_feeder"));
    }
}
