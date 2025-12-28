package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class AMPointOfInterestRegistry {

    public static final DeferredRegister<PoiType> DEF_REG = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, AlexsMobs.MODID);
    public static final DeferredHolder<PoiType, PoiType> END_PORTAL_FRAME = DEF_REG.register("end_portal_frame", () ->new PoiType(getBlockStates(Blocks.END_PORTAL_FRAME), 32, 6));
    public static final DeferredHolder<PoiType, PoiType> LEAFCUTTER_ANT_HILL = DEF_REG.register("leafcutter_anthill", () ->new PoiType(getBlockStates(AMBlockRegistry.LEAFCUTTER_ANTHILL.get()), 32, 6));
    public static final DeferredHolder<PoiType, PoiType> BEACON = DEF_REG.register("am_beacon", () -> new PoiType(getBlockStates(Blocks.BEACON), 32, 6));
    public static final DeferredHolder<PoiType, PoiType> HUMMINGBIRD_FEEDER = DEF_REG.register("hummingbird_feeder", () -> new PoiType(getBlockStates(AMBlockRegistry.HUMMINGBIRD_FEEDER.get()), 32, 6));

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

}
