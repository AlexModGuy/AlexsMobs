package com.github.alexthe666.alexsmobs.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;

public record LassoData(EntityReference lassoedTo) {
    public static final LassoData EMPTY = new LassoData(new EntityReference.Empty());

    public static final Codec<LassoData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            EntityReference.CODEC
                .fieldOf("LassoOwner")
                .forGetter(LassoData::lassoedTo)
        )
            .apply(instance, LassoData::new)
    );

    public static final StreamCodec<ByteBuf, LassoData> STREAM_CODEC = StreamCodec.composite(
        EntityReference.STREAM_CODEC, LassoData::lassoedTo,
        LassoData::new
    );
}
