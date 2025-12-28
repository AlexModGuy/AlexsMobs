package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageSendVisualFlagFromServer implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageSendVisualFlagFromServer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "send_visual_flag"));
    public static final StreamCodec<FriendlyByteBuf, MessageSendVisualFlagFromServer> CODEC = StreamCodec.ofMember(MessageSendVisualFlagFromServer::write, MessageSendVisualFlagFromServer::read);

    public int entityID;
    public int flag;

    public MessageSendVisualFlagFromServer(int entityID, int flag) {
        this.entityID = entityID;
        this.flag = flag;
    }

    public MessageSendVisualFlagFromServer() {}

    public static MessageSendVisualFlagFromServer read(FriendlyByteBuf buf) {
        return new MessageSendVisualFlagFromServer(buf.readInt(), buf.readInt());
    }

    public static void write(MessageSendVisualFlagFromServer message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.flag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageSendVisualFlagFromServer message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(message.entityID);
                AlexsMobs.PROXY.processVisualFlag(entity, message.flag);
            }
        });
    }
}
