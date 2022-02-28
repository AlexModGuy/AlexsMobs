package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMTileEntityRegistry {

    public static BlockEntityType<TileEntityLeafcutterAnthill> LEAFCUTTER_ANTHILL = registerTileEntity(BlockEntityType.Builder.of(TileEntityLeafcutterAnthill::new, AMBlockRegistry.LEAFCUTTER_ANTHILL), "leafcutter_anthill");
    public static BlockEntityType<TileEntityCapsid> CAPSID = registerTileEntity(BlockEntityType.Builder.of(TileEntityCapsid::new, AMBlockRegistry.CAPSID), "capsid");
    public static BlockEntityType<TileEntityVoidWormBeak> VOID_WORM_BEAK = registerTileEntity(BlockEntityType.Builder.of(TileEntityVoidWormBeak::new, AMBlockRegistry.VOID_WORM_BEAK), "void_worm_beak");
    public static BlockEntityType<TileEntityTerrapinEgg> TERRAPIN_EGG = registerTileEntity(BlockEntityType.Builder.of(TileEntityTerrapinEgg::new, AMBlockRegistry.TERRAPIN_EGG), "terrapin_egg");
    public static BlockEntityType<TileEntityEndPirateDoor> END_PIRATE_DOOR = registerTileEntity(BlockEntityType.Builder.of(TileEntityEndPirateDoor::new, AMBlockRegistry.END_PIRATE_DOOR), "end_pirate_door");
    public static BlockEntityType<TileEntityEndPirateAnchor> END_PIRATE_ANCHOR = registerTileEntity(BlockEntityType.Builder.of(TileEntityEndPirateAnchor::new, AMBlockRegistry.END_PIRATE_ANCHOR), "end_pirate_anchor");
    public static BlockEntityType<TileEntityEndPirateAnchorWinch> END_PIRATE_ANCHOR_WINCH = registerTileEntity(BlockEntityType.Builder.of(TileEntityEndPirateAnchorWinch::new, AMBlockRegistry.END_PIRATE_ANCHOR_WINCH), "end_pirate_anchor_winch");
    public static BlockEntityType<TileEntityEndPirateShipWheel> END_PIRATE_SHIP_WHEEL = registerTileEntity(BlockEntityType.Builder.of(TileEntityEndPirateShipWheel::new, AMBlockRegistry.END_PIRATE_SHIP_WHEEL), "end_pirate_ship_wheel");
    public static BlockEntityType<TileEntityEndPirateFlag> END_PIRATE_FLAG = registerTileEntity(BlockEntityType.Builder.of(TileEntityEndPirateFlag::new, AMBlockRegistry.END_PIRATE_FLAG), "end_pirate_flag");

    public static BlockEntityType registerTileEntity(BlockEntityType.Builder builder, String entityName){
        ResourceLocation nameLoc = new ResourceLocation(AlexsMobs.MODID, entityName);
        return (BlockEntityType) builder.build(null).setRegistryName(nameLoc);
    }

    @SubscribeEvent
    public static void registerTileEntities(final RegistryEvent.Register<BlockEntityType<?>> event) {
        try {
            for (Field f : AMTileEntityRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof BlockEntityType) {
                    event.getRegistry().register((BlockEntityType) obj);
                } else if (obj instanceof BlockEntityType[]) {
                    for (BlockEntityType te : (BlockEntityType[]) obj) {
                        event.getRegistry().register(te);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
