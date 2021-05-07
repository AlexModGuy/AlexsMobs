package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSyncEntityPos {

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

    public MessageSyncEntityPos() {
    }

    public static MessageSyncEntityPos read(PacketBuffer buf) {
        return new MessageSyncEntityPos(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void write(MessageSyncEntityPos message, PacketBuffer buf) {
        buf.writeInt(message.eagleId);
        buf.writeDouble(message.posX);
        buf.writeDouble(message.posY);
        buf.writeDouble(message.posZ);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSyncEntityPos message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                PlayerEntity player = context.get().getSender();
                if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }
                if (player != null) {
                    if (player.world != null) {
                        Entity entity = player.world.getEntityByID(message.eagleId);
                        if (entity instanceof EntityBaldEagle || entity instanceof EntityStraddleboard) {
                            entity.setPosition(message.posX, message.posY, message.posZ);
                            entity.teleportKeepLoaded(message.posX, message.posY, message.posZ);
                        }
                    }
                }
            });
        }
    }
}