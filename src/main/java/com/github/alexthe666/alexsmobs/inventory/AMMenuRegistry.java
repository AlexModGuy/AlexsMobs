package com.github.alexthe666.alexsmobs.inventory;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMMenuRegistry {

    public static final DeferredRegister<MenuType<?>> DEF_REG = DeferredRegister.create(Registries.MENU, AlexsMobs.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<MenuTransmutationTable>> TRANSMUTATION_TABLE = DEF_REG.register("transmutation_table", () -> new MenuType<>(MenuTransmutationTable::new, FeatureFlags.DEFAULT_FLAGS));

}
