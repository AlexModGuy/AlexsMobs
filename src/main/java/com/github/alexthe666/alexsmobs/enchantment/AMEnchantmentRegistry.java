package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.ItemStraddleboard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMEnchantmentRegistry {

    public static final DeferredRegister<Enchantment> DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AlexsMobs.MODID);
    public static final EnchantmentCategory STRADDLEBOARD = EnchantmentCategory.create("straddleboard", (item -> item instanceof ItemStraddleboard));

    public static final RegistryObject<Enchantment> STRADDLE_JUMP = DEF_REG.register("straddle_jump", () -> new StraddleJumpEnchantment(Enchantment.Rarity.COMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> STRADDLE_LAVAWAX = DEF_REG.register("lavawax", () -> new StraddleEnchantment(Enchantment.Rarity.UNCOMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> STRADDLE_SERPENTFRIEND = DEF_REG.register("serpentfriend", () -> new StraddleEnchantment(Enchantment.Rarity.RARE, STRADDLEBOARD, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> STRADDLE_BOARDRETURN = DEF_REG.register("board_return", () -> new StraddleEnchantment(Enchantment.Rarity.UNCOMMON, STRADDLEBOARD, EquipmentSlot.MAINHAND));
}
