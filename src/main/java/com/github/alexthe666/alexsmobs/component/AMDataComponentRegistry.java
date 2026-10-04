package com.github.alexthe666.alexsmobs.component;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;

public class AMDataComponentRegistry {
    public static final DeferredRegister.DataComponents DEF_REG = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AlexsMobs.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> BISON_FUR = DEF_REG.register("bison_fur", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
}
