package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMItemRegistry {

    public static final Item TAB_ICON = new Item(AlexsMobs.PROXY.setupISTER(new Item.Properties())).setRegistryName("alexsmobs:tab_icon");
    public static final Item BEAR_FUR = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:bear_fur");
    public static final Item ROADRUNNER_FEATHER = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:roadrunner_feather");
    public static final Item LAVA_BOTTLE = new Item(new Item.Properties().group(AlexsMobs.TAB).maxStackSize(1)).setRegistryName("alexsmobs:lava_bottle");
    public static final Item BONE_SERPENT_TOOTH = new Item(new Item.Properties().group(AlexsMobs.TAB).isImmuneToFire()).setRegistryName("alexsmobs:bone_serpent_tooth");
    public static final Item MAGGOT = new Item(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(1).saturation(0.2F).build())).setRegistryName("alexsmobs:maggot");
    public static final Item BANANA = new Item(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(4).saturation(0.3F).build())).setRegistryName("alexsmobs:banana");
    public static final Item HALO = new Item(new Item.Properties()).setRegistryName("alexsmobs:halo");
    public static final Item RATTLESNAKE_RATTLE = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:rattlesnake_rattle");

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GRIZZLY_BEAR, 0X693A2C, 0X976144, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_grizzly_bear"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ROADRUNNER, 0X3A2E26, 0XFBE9CE, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_roadrunner"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.BONE_SERPENT, 0XE5D9C4, 0XFF6038, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bone_serpent"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GAZELLE, 0XDDA675,0X2C2925, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gazelle"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CROCODILE, 0X738940,0XA6A15E, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crocodile"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.FLY, 0X464241,0X892E2E, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_fly"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.HUMMINGBIRD, 0X325E7F,0X44A75F, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_hummingbird"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ORCA, 0X2C2C2C,0XD6D8E4, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_orca"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SUNBIRD, 0XF6694F,0XFFDDA0, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_sunbird"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GORILLA, 0X595B5D,0X1C1C21, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gorilla"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CRIMSON_MOSQUITO, 0X53403F,0XC11A1A, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crimson_mosquito"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.RATTLESNAKE, 0XCEB994,0X937A5B, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_rattlesnake"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ENDERGRADE, 0XE6E6A4,0XB29BDD, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_endergrade"));
        try {
            for (Field f : AMItemRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Item) {
                    event.getRegistry().register((Item) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
