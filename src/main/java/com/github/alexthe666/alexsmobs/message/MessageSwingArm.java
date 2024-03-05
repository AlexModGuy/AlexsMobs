package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.item.ILeftClick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
                    ItemStack leftItem = player.getItemInHand(InteractionHand.OFF_HAND);
                    ItemStack rightItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                    if(leftItem.getItem() instanceof ILeftClick){
                        ((ILeftClick)leftItem.getItem()).onLeftClick(leftItem, player);
                    }
                    if(rightItem.getItem() instanceof ILeftClick){
                        ((ILeftClick)rightItem.getItem()).onLeftClick(rightItem, player);
                    }
                }
            });
        }
    }

}
