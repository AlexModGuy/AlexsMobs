package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSendVisualFlagFromServer {

    public int entityID;
    public int flag;

    public MessageSendVisualFlagFromServer(int entityID, int flag) {
        this.entityID = entityID;
        this.flag = flag;
    }

    public MessageSendVisualFlagFromServer() {
    }

    public static MessageSendVisualFlagFromServer read(FriendlyByteBuf buf) {
        return new MessageSendVisualFlagFromServer(buf.readInt(), buf.readInt());
    }

    public static void write(MessageSendVisualFlagFromServer message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.flag);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSendVisualFlagFromServer message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }

                if (player != null) {
                    if (player.level != null) {
                        Entity entity = player.level.getEntity(message.entityID);
                        AlexsMobs.PROXY.processVisualFlag(entity, message.flag);
                    }
                }
            });
        }
    }
}