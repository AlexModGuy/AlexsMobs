package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMTileEntityRegistry {

    public static TileEntityType<TileEntityLeafcutterAnthill> LEAFCUTTER_ANTHILL = registerTileEntity(TileEntityType.Builder.create(TileEntityLeafcutterAnthill::new, AMBlockRegistry.LEAFCUTTER_ANTHILL), "leafcutter_anthill");
    public static TileEntityType<TileEntityCapsid> CAPSID = registerTileEntity(TileEntityType.Builder.create(TileEntityCapsid::new, AMBlockRegistry.CAPSID), "capsid");
    public static TileEntityType<TileEntityVoidWormBeak> VOID_WORM_BEAK = registerTileEntity(TileEntityType.Builder.create(TileEntityVoidWormBeak::new, AMBlockRegistry.VOID_WORM_BEAK), "void_worm_beak");

    public static TileEntityType registerTileEntity(TileEntityType.Builder builder, String entityName){
        ResourceLocation nameLoc = new ResourceLocation(AlexsMobs.MODID, entityName);
        return (TileEntityType) builder.build(null).setRegistryName(nameLoc);
    }

    @SubscribeEvent
    public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        try {
            for (Field f : AMTileEntityRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof TileEntityType) {
                    event.getRegistry().register((TileEntityType) obj);
                } else if (obj instanceof TileEntityType[]) {
                    for (TileEntityType te : (TileEntityType[]) obj) {
                        event.getRegistry().register(te);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
