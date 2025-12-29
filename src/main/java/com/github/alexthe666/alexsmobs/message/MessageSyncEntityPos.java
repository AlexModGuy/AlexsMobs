package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageSyncEntityPos implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageSyncEntityPos> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "sync_entity_pos"));
    public static final StreamCodec<FriendlyByteBuf, MessageSyncEntityPos> CODEC = StreamCodec.ofMember(MessageSyncEntityPos::write, MessageSyncEntityPos::read);

    public int eagleId;
    public double posX;
    public double posY;
    public double posZ;

    public MessageSyncEntityPos(int eagleId, double posX, double posY, double posZ) {
        this.eagleId = eagleId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public MessageSyncEntityPos() {}

    public static MessageSyncEntityPos read(FriendlyByteBuf buf) {
        return new MessageSyncEntityPos(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void write(MessageSyncEntityPos message, FriendlyByteBuf buf) {
        buf.writeInt(message.eagleId);
        buf.writeDouble(message.posX);
        buf.writeDouble(message.posY);
        buf.writeDouble(message.posZ);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageSyncEntityPos message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(message.eagleId);
                if (entity instanceof IFalconry || entity instanceof EntityStraddleboard) {
                    entity.setPos(message.posX, message.posY, message.posZ);
                    entity.teleportTo(message.posX, message.posY, message.posZ);
                }
            }
        });
    }
}
