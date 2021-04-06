package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageMosquitoMountPlayer {

    public int rider;
    public int mount;

    public MessageMosquitoMountPlayer(int rider, int mount) {
        this.rider = rider;
        this.mount = mount;
    }

    public MessageMosquitoMountPlayer() {
    }

    public static MessageMosquitoMountPlayer read(PacketBuffer buf) {
        return new MessageMosquitoMountPlayer(buf.readInt(), buf.readInt());
    }

    public static void write(MessageMosquitoMountPlayer message, PacketBuffer buf) {
        buf.writeInt(message.rider);
        buf.writeInt(message.mount);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageMosquitoMountPlayer message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.rider);
                    Entity mountEntity = player.world.getEntityByID(message.mount);
                    if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityEnderiophage) && mountEntity instanceof PlayerEntity && entity.getDistance(mountEntity) < 16D) {
                        entity.startRiding(mountEntity, true);
                    }
                }
            }
        }
    }
}