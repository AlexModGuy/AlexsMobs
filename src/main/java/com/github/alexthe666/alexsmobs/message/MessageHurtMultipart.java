package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IHurtableMultipart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHurtMultipart {

    public int part;
    public int parent;
    public float damage;

    public MessageHurtMultipart(int part, int parent, float damage) {
        this.part = part;
        this.parent = parent;
        this.damage = damage;
    }

    public MessageHurtMultipart() {
    }

    public static MessageHurtMultipart read(PacketBuffer buf) {
        return new MessageHurtMultipart(buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public static void write(MessageHurtMultipart message, PacketBuffer buf) {
        buf.writeInt(message.part);
        buf.writeInt(message.parent);
        buf.writeFloat(message.damage);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageHurtMultipart message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.world != null) {
                    Entity part = player.world.getEntityByID(message.part);
                    Entity parent = player.world.getEntityByID(message.parent);
                    if(part instanceof IHurtableMultipart && parent instanceof LivingEntity){
                        ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity)parent, message.damage);
                    }
                }
            }
        }
    }
}