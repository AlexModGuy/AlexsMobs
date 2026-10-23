package com.github.alexthe666.alexsmobs.attachment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.ItemRainbowJelly;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.minecraft.network.codec.ByteBufCodecs;

public class AMAttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> DEF_REG = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AlexsMobs.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ROCKY_ROLL_TICKS = DEF_REG.register("rocky_roll_ticks", () -> AttachmentType.builder(() -> 0)
        .serialize(Codec.INT)
        .sync(ByteBufCodecs.VAR_INT)
        .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ROCKY_ROLL_TIMESTAMP = DEF_REG.register("rocky_roll_timestamp",  () -> AttachmentType.builder(() -> 0)
        .serialize(Codec.INT)
        .sync(ByteBufCodecs.VAR_INT)
        .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LassoData>> LASSO_DATA = DEF_REG.register("lasso_data",  () -> AttachmentType.builder(() -> LassoData.EMPTY)
        .serialize(LassoData.CODEC)
        .sync(LassoData.STREAM_CODEC)
        .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SquidGrappleData>> SQUID_GRAPPLE_DATA = DEF_REG.register("squid_grapple_data", () -> AttachmentType.builder(() -> SquidGrappleData.EMPTY)
        .serialize(SquidGrappleData.CODEC)
        .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ItemRainbowJelly.RainbowType>> RAINBOW_TYPE = DEF_REG.register("rainbow_type", () -> AttachmentType.builder(() -> ItemRainbowJelly.RainbowType.RAINBOW)
        .serialize(ItemRainbowJelly.RainbowType.CODEC)
        .sync(ItemRainbowJelly.RainbowType.STREAM_CODEC)
        .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntityReference>> TENDON_WHIP = DEF_REG.register("tendon_whip", () -> AttachmentType.<EntityReference>builder(() -> EntityReference.Empty.INSTANCE)
        .serialize(EntityReference.CODEC)
        .sync(EntityReference.STREAM_CODEC)
        .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> FLYING_FISH_BOOST = DEF_REG.register("flying_fish_boost", () -> AttachmentType.builder(() -> 0)
        .serialize(Codec.INT)
        .sync(ByteBufCodecs.VAR_INT)
        .build()
    );

    public static void init() {}
}
