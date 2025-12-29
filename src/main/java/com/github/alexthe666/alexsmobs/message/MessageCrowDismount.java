package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageCrowDismount implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageCrowDismount> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "crow_dismount"));
    public static final StreamCodec<FriendlyByteBuf, MessageCrowDismount> CODEC = StreamCodec.ofMember(MessageCrowDismount::write, MessageCrowDismount::read);

    public int rider;
    public int mount;

    public MessageCrowDismount(int rider, int mount) {
        this.rider = rider;
        this.mount = mount;
    }

    public MessageCrowDismount() {}

    public static MessageCrowDismount read(FriendlyByteBuf buf) {
        return new MessageCrowDismount(buf.readInt(), buf.readInt());
    }

    public static void write(MessageCrowDismount message, FriendlyByteBuf buf) {
        buf.writeInt(message.rider);
        buf.writeInt(message.mount);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageCrowDismount message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(message.rider);
                Entity mountEntity = player.level().getEntity(message.mount);
                if (entity instanceof EntityCrow && mountEntity != null) {
                    entity.stopRiding();
                }
            }
        });
    }
}
