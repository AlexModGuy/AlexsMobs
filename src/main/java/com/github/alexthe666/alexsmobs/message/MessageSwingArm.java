package com.github.alexthe666.alexsmobs.message;

import java.util.function.Supplier;

import com.github.alexthe666.alexsmobs.item.ItemFalconryGlove;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.network.NetworkEvent;

public class MessageSwingArm {

    public static final MessageSwingArm INSTANCE = new MessageSwingArm();

    private MessageSwingArm() {
    }

    public static MessageSwingArm read(FriendlyByteBuf buf) {
        return INSTANCE;
    }

    public static void write(MessageSwingArm message, FriendlyByteBuf buf) {
    }

    public static class Handler {

        public Handler() {
        }

        public static void handle(MessageSwingArm message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (player != null) {
                    ItemFalconryGlove.onLeftClick(player, player.getItemInHand(InteractionHand.OFF_HAND));
                    ItemFalconryGlove.onLeftClick(player, player.getItemInHand(InteractionHand.MAIN_HAND));
                }
            });
        }
    }

}
