package com.github.alexthe666.alexsmobs.component;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;

public record DisplayEntity(
    EntityType<?> entityType,
    int flags,
    Optional<Float> scale
) {
    public static DisplayEntity of(EntityType<?> entityType) {
        return new DisplayEntity(entityType, 0, Optional.empty());
    }

    public static DisplayEntity of(Holder<EntityType<?>> entityType) {
        return of(entityType.value());
    }

    public static final Codec<DisplayEntity> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type")
                .forGetter(DisplayEntity::entityType),
            Codec.INT.optionalFieldOf("flags", 0)
                .forGetter(DisplayEntity::flags),
            Codec.FLOAT.optionalFieldOf("scale")
                .forGetter(DisplayEntity::scale)
        )
            .apply(instance, DisplayEntity::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DisplayEntity> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.ENTITY_TYPE), DisplayEntity::entityType,
        ByteBufCodecs.VAR_INT, DisplayEntity::flags,
        ByteBufCodecs.optional(ByteBufCodecs.FLOAT), DisplayEntity::scale,
        DisplayEntity::new
    );
}
