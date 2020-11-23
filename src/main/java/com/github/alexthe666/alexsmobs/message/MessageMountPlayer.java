package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageMountPlayer {

    public int rider;
    public int mount;

    public MessageMountPlayer(int rider, int mount) {
        this.rider = rider;
        this.mount = mount;
    }

    public MessageMountPlayer() {
    }

    public static MessageMountPlayer read(PacketBuffer buf) {
        return new MessageMountPlayer(buf.readInt(), buf.readInt());
    }

    public static void write(MessageMountPlayer message, PacketBuffer buf) {
        buf.writeInt(message.rider);
        buf.writeInt(message.mount);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageMountPlayer message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.rider);
                    Entity mountEntity = player.world.getEntityByID(message.mount);
                    if (entity instanceof CreatureEntity && mountEntity != null) {
                        entity.startRiding(mountEntity, true);
                    }
                }
            }
        }
    }
}