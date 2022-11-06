package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateTransmutablesToDisplay {

    private int playerId;
    public ItemStack stack1;
    public ItemStack stack2;
    public ItemStack stack3;

    public MessageUpdateTransmutablesToDisplay(int playerId, ItemStack stack1, ItemStack stack2, ItemStack stack3) {
        this.stack1 = stack1;
        this.stack2 = stack2;
        this.stack3 = stack3;
        this.playerId = playerId;
    }

    public MessageUpdateTransmutablesToDisplay() {
    }

    public static MessageUpdateTransmutablesToDisplay read(FriendlyByteBuf buf) {
        return new MessageUpdateTransmutablesToDisplay(buf.readInt(), PacketBufferUtils.readItemStack(buf), PacketBufferUtils.readItemStack(buf), PacketBufferUtils.readItemStack(buf));
    }

    public static void write(MessageUpdateTransmutablesToDisplay message, FriendlyByteBuf buf) {
        buf.writeInt(message.playerId);
        PacketBufferUtils.writeItemStack(buf, message.stack1);
        PacketBufferUtils.writeItemStack(buf, message.stack2);
        PacketBufferUtils.writeItemStack(buf, message.stack3);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateTransmutablesToDisplay message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }
            if(player.getId() == message.playerId){
                AlexsMobs.PROXY.setDisplayTransmuteResult(0, message.stack1);
                AlexsMobs.PROXY.setDisplayTransmuteResult(1, message.stack2);
                AlexsMobs.PROXY.setDisplayTransmuteResult(2, message.stack3);
            }
        }
    }

}