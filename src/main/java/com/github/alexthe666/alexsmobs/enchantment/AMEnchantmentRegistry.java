package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Enchantments are now data-driven in NeoForge 1.21.
 * Custom enchantments must be defined in JSON files under:
 * data/alexsmobs/enchantment/
 */
public class AMEnchantmentRegistry {
    // ResourceKeys for data-driven enchantments
    public static final ResourceKey<Enchantment> STRADDLE_JUMP = createKey("straddle_jump");
    public static final ResourceKey<Enchantment> STRADDLE_LAVAWAX = createKey("lavawax");
    public static final ResourceKey<Enchantment> STRADDLE_SERPENTFRIEND = createKey("serpentfriend");
    public static final ResourceKey<Enchantment> STRADDLE_BOARDRETURN = createKey("board_return");
    
    private static ResourceKey<Enchantment> createKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, name));
    }
    
    public static void init() {
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        context.register(STRADDLE_JUMP, Enchantment.enchantment(
                Enchantment.definition(
                    HolderSet.direct(AMItemRegistry.STRADDLEBOARD),
                    5, 3,
                    new Enchantment.Cost(15, 9), new Enchantment.Cost(65, 9),
                    4,
                    EquipmentSlotGroup.MAINHAND
                )
            )
            .build(STRADDLE_JUMP.location()));

        context.register(STRADDLE_LAVAWAX, Enchantment.enchantment(
                Enchantment.definition(
                    HolderSet.direct(AMItemRegistry.STRADDLEBOARD),
                    5, 1,
                    new Enchantment.Cost(15, 9), new Enchantment.Cost(65, 9),
                    4,
                    EquipmentSlotGroup.MAINHAND
                )
            )
            .build(STRADDLE_LAVAWAX.location()));

        context.register(STRADDLE_SERPENTFRIEND, Enchantment.enchantment(
                Enchantment.definition(
                    HolderSet.direct(AMItemRegistry.STRADDLEBOARD),
                    5, 1,
                    new Enchantment.Cost(15, 9), new Enchantment.Cost(65, 9),
                    4,
                    EquipmentSlotGroup.MAINHAND
                )
            )
            .build(STRADDLE_SERPENTFRIEND.location()));

        context.register(STRADDLE_BOARDRETURN, Enchantment.enchantment(
                Enchantment.definition(
                    HolderSet.direct(AMItemRegistry.STRADDLEBOARD),
                    5, 1,
                    new Enchantment.Cost(15, 9), new Enchantment.Cost(65, 9),
                    4,
                    EquipmentSlotGroup.MAINHAND
                )
            )
            .build(STRADDLE_BOARDRETURN.location()));
    }
}
