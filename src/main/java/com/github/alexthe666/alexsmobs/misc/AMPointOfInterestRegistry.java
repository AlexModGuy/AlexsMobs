package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockHummingbirdFeeder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMPointOfInterestRegistry {

    public static final PoiType END_PORTAL_FRAME = new PoiType("alexsmobs:end_portal_frame", PoiType.getBlockStates(Blocks.END_PORTAL_FRAME), 32, 6);
    public static final PoiType LEAFCUTTER_ANT_HILL = new PoiType("alexsmobs:leafcutter_anthill", PoiType.getBlockStates(AMBlockRegistry.LEAFCUTTER_ANTHILL), 32, 6);
    public static final PoiType BEACON = new PoiType("alexsmobs:am_beacon", PoiType.getBlockStates(Blocks.BEACON), 32, 6);
    public static final PoiType HUMMINGBIRD_FEEDER = new PoiType("alexsmobs:hummingbird_feeder", PoiType.getBlockStates(AMBlockRegistry.HUMMINGBIRD_FEEDER), 32, 6);

    private static Set<BlockState> getHummingbirdFeederStates() {
        BlockState state = AMBlockRegistry.HUMMINGBIRD_FEEDER.defaultBlockState().setValue(BlockHummingbirdFeeder.CONTENTS, 3);
        return ImmutableSet.of(state, state.setValue(BlockHummingbirdFeeder.HANGING, true), state.setValue(BlockHummingbirdFeeder.WATERLOGGED, true), state.setValue(BlockHummingbirdFeeder.HANGING, true).setValue(BlockHummingbirdFeeder.WATERLOGGED, true));
    }

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<PoiType> event) {
        event.getRegistry().register(END_PORTAL_FRAME.setRegistryName("alexsmobs:end_portal_frame"));
        event.getRegistry().register(LEAFCUTTER_ANT_HILL.setRegistryName("alexsmobs:leafcutter_anthill"));
        event.getRegistry().register(BEACON.setRegistryName("alexsmobs:am_beacon"));
        event.getRegistry().register(HUMMINGBIRD_FEEDER.setRegistryName("alexsmobs:hummingbird_feeder"));
    }
}
