package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMTileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AlexsMobs.MODID);

    public static final RegistryObject<BlockEntityType<TileEntityLeafcutterAnthill>> LEAFCUTTER_ANTHILL = DEF_REG.register("leafcutter_anthill_te", () -> BlockEntityType.Builder.of(TileEntityLeafcutterAnthill::new, AMBlockRegistry.LEAFCUTTER_ANTHILL.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityCapsid>> CAPSID = DEF_REG.register("capsid_te", () -> BlockEntityType.Builder.of(TileEntityCapsid::new, AMBlockRegistry.CAPSID.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityVoidWormBeak>> VOID_WORM_BEAK = DEF_REG.register("void_worm_beak_te", () -> BlockEntityType.Builder.of(TileEntityVoidWormBeak::new, AMBlockRegistry.VOID_WORM_BEAK.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityTerrapinEgg>> TERRAPIN_EGG = DEF_REG.register("terrapin_egg_te", () -> BlockEntityType.Builder.of(TileEntityTerrapinEgg::new, AMBlockRegistry.TERRAPIN_EGG.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityTransmutationTable>> TRANSMUTATION_TABLE = DEF_REG.register("transmutation_table", () -> BlockEntityType.Builder.of(TileEntityTransmutationTable::new, AMBlockRegistry.TRANSMUTATION_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntitySculkBoomer>> SCULK_BOOMER = DEF_REG.register("sculk_boomer", () -> BlockEntityType.Builder.of(TileEntitySculkBoomer::new, AMBlockRegistry.SCULK_BOOMER.get()).build(null));
    //TODO reimplement
    public static final RegistryObject<BlockEntityType<TileEntityEndPirateDoor>> END_PIRATE_DOOR = null;//DEF_REG.register("end_pirate_door_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateDoor::new, AMBlockRegistry.END_PIRATE_DOOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityEndPirateAnchor>> END_PIRATE_ANCHOR = null;// DEF_REG.register("end_pirate_anchor_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateAnchor::new, AMBlockRegistry.END_PIRATE_ANCHOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityEndPirateAnchorWinch>> END_PIRATE_ANCHOR_WINCH =  null;//DEF_REG.register("end_pirate_anchor_winch_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateAnchorWinch::new, AMBlockRegistry.END_PIRATE_ANCHOR_WINCH.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityEndPirateShipWheel>> END_PIRATE_SHIP_WHEEL = null;// DEF_REG.register("end_pirate_ship_wheel_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateShipWheel::new, AMBlockRegistry.END_PIRATE_SHIP_WHEEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityEndPirateFlag>> END_PIRATE_FLAG = null;// DEF_REG.register("end_pirate_flag_te", () -> BlockEntityType.Builder.of(TileEntityEndPirateFlag::new, AMBlockRegistry.END_PIRATE_FLAG.get()).build(null));

}
