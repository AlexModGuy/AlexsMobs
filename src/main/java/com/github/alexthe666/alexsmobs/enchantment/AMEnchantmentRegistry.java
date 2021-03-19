package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.ItemStraddleboard;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMEnchantmentRegistry {

    public static final EnchantmentType STRADDLEBOARD = EnchantmentType.create("straddleboard", (item -> item instanceof ItemStraddleboard));
    public static Enchantment STRADDLE_JUMP = new StraddleJumpEnchantment(Enchantment.Rarity.COMMON, STRADDLEBOARD, EquipmentSlotType.MAINHAND).setRegistryName("alexsmobs:straddle_jump");
    public static Enchantment STRADDLE_LAVAWAX = new StraddleEnchantment(Enchantment.Rarity.UNCOMMON, STRADDLEBOARD, EquipmentSlotType.MAINHAND).setRegistryName("alexsmobs:lavawax");
    public static Enchantment STRADDLE_SERPENTFRIEND = new StraddleEnchantment(Enchantment.Rarity.RARE, STRADDLEBOARD, EquipmentSlotType.MAINHAND).setRegistryName("alexsmobs:serpentfriend");
    public static Enchantment STRADDLE_BOARDRETURN = new StraddleEnchantment(Enchantment.Rarity.UNCOMMON, STRADDLEBOARD, EquipmentSlotType.MAINHAND).setRegistryName("alexsmobs:board_return");

    @SubscribeEvent
    public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
        if(AMConfig.straddleboardEnchants){
            try {
                for (Field f : AMEnchantmentRegistry.class.getDeclaredFields()) {
                    Object obj = f.get(null);
                    if (obj instanceof Enchantment) {
                        event.getRegistry().register((Enchantment) obj);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
