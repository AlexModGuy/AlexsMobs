package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateCapsid  {

    public long blockPos;
    public ItemStack heldStack;

    public MessageUpdateCapsid(long blockPos, ItemStack heldStack) {
        this.blockPos = blockPos;
        this.heldStack = heldStack;

    }

    public MessageUpdateCapsid() {
    }

    public static MessageUpdateCapsid read(PacketBuffer buf) {
        return new MessageUpdateCapsid(buf.readLong(), PacketBufferUtils.readItemStack(buf));
    }

    public static void write(MessageUpdateCapsid message, PacketBuffer buf) {
        buf.writeLong(message.blockPos);
        PacketBufferUtils.writeItemStack(buf, message.heldStack);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateCapsid message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    BlockPos pos = BlockPos.fromLong(message.blockPos);
                    if (player.world.getTileEntity(pos) != null) {
                        if (player.world.getTileEntity(pos) instanceof TileEntityCapsid) {
                            TileEntityCapsid podium = (TileEntityCapsid) player.world.getTileEntity(pos);
                            podium.setInventorySlotContents(0, message.heldStack);
                        }
                    }
                }
            }
        }
    }

}