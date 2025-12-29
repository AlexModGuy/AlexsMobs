package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Enchantments are now data-driven in NeoForge 1.21.
 * Custom enchantments must be defined in JSON files under:
 * data/alexsmobs/enchantment/
 */
public class AMEnchantmentRegistry {
    // ResourceKeys for data-driven enchantments
    public static final ResourceKey<Enchantment> STRADDLE_JUMP = createKey("straddle_jump");
    public static final ResourceKey<Enchantment> STRADDLE_LAVAWAX = createKey("straddle_lavawax");
    public static final ResourceKey<Enchantment> STRADDLE_SERPENTFRIEND = createKey("straddle_serpentfriend");
    public static final ResourceKey<Enchantment> STRADDLE_BOARDRETURN = createKey("straddle_boardreturn");
    
    private static ResourceKey<Enchantment> createKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, name));
    }
    
    public static void init() {
        AlexsMobs.LOGGER.info("AMEnchantmentRegistry: Enchantments are now data-driven in 1.21");
    }
}
