package com.github.alexthe666.alexsmobs.attachment;

import java.util.UUID;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public sealed interface EntityReference {
    Codec<EntityReference> CODEC = Codec.either(
        Codec.either(EntityUuid.CODEC, EntityId.CODEC),
        Empty.CODEC
    )
        .xmap(
            either -> Either.unwrap(either.mapLeft(Either::unwrap)),
            EntityReference::map
        );

    StreamCodec<ByteBuf, EntityReference> STREAM_CODEC = ByteBufCodecs.either(
        ByteBufCodecs.either(EntityUuid.STREAM_CODEC, EntityId.STREAM_CODEC),
        Empty.STREAM_CODEC
    )
        .map(
            either -> Either.unwrap(either.mapLeft(Either::unwrap)),
            EntityReference::map
        );

    @Nullable Entity getEntity(Level level);

    private static Either<Either<EntityUuid, EntityId>, Empty> map(EntityReference reference) {
        if (reference instanceof EntityId entity)
            return Either.left(Either.right(entity));
        else if (reference instanceof EntityUuid player)
            return Either.left(Either.left(player));
        else
            return Either.right(new Empty());
    }

    static EntityReference getReferenceForEntity(@Nullable Entity entity) {
        if (entity instanceof Player player)
            return new EntityUuid(player.getUUID());
        else if (entity != null)
            return new EntityId(entity.getId());
        else
            return new Empty();
    }

    record Empty() implements EntityReference {
        public static final Empty INSTANCE = new Empty();
        public static final Codec<Empty> CODEC = Codec.unit(INSTANCE);
        public static final StreamCodec<ByteBuf, Empty> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public Entity getEntity(Level level) {
            return null;
        }
    }

    record EntityId(int id) implements EntityReference {
        public static final Codec<EntityId> CODEC = Codec.INT.xmap(EntityId::new, EntityId::id);
        public static final StreamCodec<ByteBuf, EntityId> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(EntityId::new, EntityId::id);

        @Override
        public @Nullable Entity getEntity(Level level) {
            return level.getEntity(this.id());
        }
    }

    record EntityUuid(UUID uuid) implements EntityReference {
        public static final Codec<EntityUuid> CODEC = UUIDUtil.CODEC.xmap(EntityUuid::new, EntityUuid::uuid);
        public static final StreamCodec<ByteBuf, EntityUuid> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(EntityUuid::new, EntityUuid::uuid);

        @Override
        public @Nullable Entity getEntity(Level level) {
            if (level instanceof ServerLevel serverLevel) {
                var entity = serverLevel.getEntity(this.uuid());
                if (entity != null)
                    return entity;
            }

            return level.getPlayerByUUID(this.uuid());
        }
    }
}
