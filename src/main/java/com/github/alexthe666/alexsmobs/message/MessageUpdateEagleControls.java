package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateEagleControls {

    public int eagleId;
    public float rotationYaw;
    public float rotationPitch;
    public boolean chunkLoad;
    public int overEntityId;

    public MessageUpdateEagleControls(int eagleId, float rotationYaw, float rotationPitch, boolean chunkLoad, int overEntityId) {
        this.eagleId = eagleId;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.chunkLoad = chunkLoad;
        this.overEntityId = overEntityId;
    }

    public MessageUpdateEagleControls() {
    }

    public static MessageUpdateEagleControls read(FriendlyByteBuf buf) {
        return new MessageUpdateEagleControls(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readInt());
    }

    public static void write(MessageUpdateEagleControls message, FriendlyByteBuf buf) {
        buf.writeInt(message.eagleId);
        buf.writeFloat(message.rotationYaw);
        buf.writeFloat(message.rotationPitch);
        buf.writeBoolean(message.chunkLoad);
        buf.writeInt(message.overEntityId);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateEagleControls message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }
                if (player != null) {
                    if (player.level != null) {
                        Entity entity = player.level.getEntity(message.eagleId);
                        if (entity instanceof EntityBaldEagle) {
                            Entity over = null;
                            if(message.overEntityId >= 0){
                                over = player.level.getEntity(message.overEntityId);
                            }
                            ((EntityBaldEagle) entity).directFromPlayer(message.rotationYaw, message.rotationPitch, message.chunkLoad, over);
                        }
                    }
                }
            });
        }
    }
}