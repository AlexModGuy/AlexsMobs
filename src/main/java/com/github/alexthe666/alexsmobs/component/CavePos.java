package com.github.alexthe666.alexsmobs.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CavePos(
    BlockPos pos,
    boolean isValid
) {
    public static final Codec<CavePos> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BlockPos.CODEC.fieldOf("pos")
                .forGetter(CavePos::pos),
            Codec.BOOL.optionalFieldOf("is_valid", false)
                .forGetter(CavePos::isValid)
        )
            .apply(instance, CavePos::new)
    );

    public static final StreamCodec<ByteBuf, CavePos> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, CavePos::pos,
        ByteBufCodecs.BOOL, CavePos::isValid,
        CavePos::new
    );
}
