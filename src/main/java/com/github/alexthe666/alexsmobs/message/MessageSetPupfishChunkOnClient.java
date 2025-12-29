package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageSetPupfishChunkOnClient implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageSetPupfishChunkOnClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "set_pupfish_chunk"));
    public static final StreamCodec<FriendlyByteBuf, MessageSetPupfishChunkOnClient> CODEC = StreamCodec.ofMember(MessageSetPupfishChunkOnClient::write, MessageSetPupfishChunkOnClient::read);

    public int chunkX;
    public int chunkZ;

    public MessageSetPupfishChunkOnClient(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public MessageSetPupfishChunkOnClient() {}

    public static MessageSetPupfishChunkOnClient read(FriendlyByteBuf buf) {
        return new MessageSetPupfishChunkOnClient(buf.readInt(), buf.readInt());
    }

    public static void write(MessageSetPupfishChunkOnClient message, FriendlyByteBuf buf) {
        buf.writeInt(message.chunkX);
        buf.writeInt(message.chunkZ);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageSetPupfishChunkOnClient message, IPayloadContext context) {
        context.enqueueWork(() -> {
            AlexsMobs.PROXY.setPupfishChunkForItem(message.chunkX, message.chunkZ);
        });
    }
}
