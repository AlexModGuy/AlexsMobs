package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IHurtableMultipart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
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

    public static MessageHurtMultipart read(FriendlyByteBuf buf) {
        return new MessageHurtMultipart(buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public static void write(MessageHurtMultipart message, FriendlyByteBuf buf) {
        buf.writeInt(message.part);
        buf.writeInt(message.parent);
        buf.writeFloat(message.damage);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageHurtMultipart message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.level != null) {
                    Entity part = player.level.getEntity(message.part);
                    Entity parent = player.level.getEntity(message.parent);
                    if(part instanceof IHurtableMultipart && parent instanceof LivingEntity){
                        ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity)parent, message.damage);
                    }
                }
            }
        }
    }
}