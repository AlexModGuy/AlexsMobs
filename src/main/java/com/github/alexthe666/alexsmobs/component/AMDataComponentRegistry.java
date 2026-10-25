package com.github.alexthe666.alexsmobs.component;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.Vec3;

public class AMDataComponentRegistry {
    public static final DeferredRegister.DataComponents DEF_REG = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AlexsMobs.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> BISON_FUR = DEF_REG.register("bison_fur", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> SWINGING = DEF_REG.register("swinging", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Vec3>> POSITION = DEF_REG.register("position", () -> DataComponentType.<Vec3>builder().persistent(Vec3.CODEC).networkSynchronized(ByteBufCodecs.VECTOR3F.map(Vec3::new, Vec3::toVector3f)).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DisplayEntity>> DISPLAY_ENTITY = DEF_REG.register("display_entity", () -> DataComponentType.<DisplayEntity>builder().persistent(DisplayEntity.CODEC).networkSynchronized(DisplayEntity.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CavePos>> CAVE_POS = DEF_REG.register("cave_pos", () -> DataComponentType.<CavePos>builder().persistent(CavePos.CODEC).networkSynchronized(CavePos.STREAM_CODEC).cacheEncoding().build());
}
