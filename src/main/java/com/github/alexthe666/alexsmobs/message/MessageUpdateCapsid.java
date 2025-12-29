package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageUpdateCapsid implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageUpdateCapsid> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "update_capsid"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUpdateCapsid> CODEC = StreamCodec.ofMember(MessageUpdateCapsid::write, MessageUpdateCapsid::read);

    public long blockPos;
    public ItemStack heldStack;

    public MessageUpdateCapsid(long blockPos, ItemStack heldStack) {
        this.blockPos = blockPos;
        this.heldStack = heldStack;
    }

    public MessageUpdateCapsid() {}

    public static MessageUpdateCapsid read(RegistryFriendlyByteBuf buf) {
        return new MessageUpdateCapsid(buf.readLong(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static void write(MessageUpdateCapsid message, RegistryFriendlyByteBuf buf) {
        buf.writeLong(message.blockPos);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.heldStack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageUpdateCapsid message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                BlockPos pos = BlockPos.of(message.blockPos);
                if (player.level().getBlockEntity(pos) instanceof TileEntityCapsid podium) {
                    podium.setItem(0, message.heldStack);
                }
            }
        });
    }
}
