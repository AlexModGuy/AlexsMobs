package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMTileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AlexsMobs.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityLeafcutterAnthill>> LEAFCUTTER_ANTHILL = DEF_REG.register("leafcutter_anthill_te", () -> BlockEntityType.Builder.of(TileEntityLeafcutterAnthill::new, AMBlockRegistry.LEAFCUTTER_ANTHILL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityCapsid>> CAPSID = DEF_REG.register("capsid_te", () -> BlockEntityType.Builder.of(TileEntityCapsid::new, AMBlockRegistry.CAPSID.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityVoidWormBeak>> VOID_WORM_BEAK = DEF_REG.register("void_worm_beak_te", () -> BlockEntityType.Builder.of(TileEntityVoidWormBeak::new, AMBlockRegistry.VOID_WORM_BEAK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityTerrapinEgg>> TERRAPIN_EGG = DEF_REG.register("terrapin_egg_te", () -> BlockEntityType.Builder.of(TileEntityTerrapinEgg::new, AMBlockRegistry.TERRAPIN_EGG.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityTransmutationTable>> TRANSMUTATION_TABLE = DEF_REG.register("transmutation_table", () -> BlockEntityType.Builder.of(TileEntityTransmutationTable::new, AMBlockRegistry.TRANSMUTATION_TABLE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntitySculkBoomer>> SCULK_BOOMER = DEF_REG.register("sculk_boomer", () -> BlockEntityType.Builder.of(TileEntitySculkBoomer::new, AMBlockRegistry.SCULK_BOOMER.get()).build(null));
    
    // End Pirate TileEntities
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityEndPirateDoor>> END_PIRATE_DOOR = DEF_REG.register("end_pirate_door_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateDoor::new, AMBlockRegistry.END_PIRATE_DOOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityEndPirateAnchor>> END_PIRATE_ANCHOR = DEF_REG.register("end_pirate_anchor_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateAnchor::new, AMBlockRegistry.END_PIRATE_ANCHOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityEndPirateAnchorWinch>> END_PIRATE_ANCHOR_WINCH = DEF_REG.register("end_pirate_anchor_winch_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateAnchorWinch::new, AMBlockRegistry.END_PIRATE_ANCHOR_WINCH.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityEndPirateShipWheel>> END_PIRATE_SHIP_WHEEL = DEF_REG.register("end_pirate_ship_wheel_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateShipWheel::new, AMBlockRegistry.END_PIRATE_SHIP_WHEEL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityEndPirateFlag>> END_PIRATE_FLAG = DEF_REG.register("end_pirate_flag_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateFlag::new, AMBlockRegistry.END_PIRATE_FLAG.get()).build(null));

}
