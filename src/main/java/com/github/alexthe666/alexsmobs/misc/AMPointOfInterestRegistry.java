package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockHummingbirdFeeder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class AMPointOfInterestRegistry {

    public static final DeferredRegister<PoiType> DEF_REG = DeferredRegister.create(ForgeRegistries.POI_TYPES, AlexsMobs.MODID);
    public static final RegistryObject<PoiType> END_PORTAL_FRAME = DEF_REG.register("end_portal_frame", () ->new PoiType(getBlockStates(Blocks.END_PORTAL_FRAME), 32, 6));
    public static final RegistryObject<PoiType> LEAFCUTTER_ANT_HILL = DEF_REG.register("leafcutter_anthill", () ->new PoiType(getBlockStates(AMBlockRegistry.LEAFCUTTER_ANTHILL.get()), 32, 6));
    public static final RegistryObject<PoiType> BEACON = DEF_REG.register("am_beacon", () -> new PoiType(getBlockStates(Blocks.BEACON), 32, 6));
    public static final RegistryObject<PoiType> HUMMINGBIRD_FEEDER = DEF_REG.register("hummingbird_feeder", () -> new PoiType(getBlockStates(AMBlockRegistry.HUMMINGBIRD_FEEDER.get()), 32, 6));

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

}
