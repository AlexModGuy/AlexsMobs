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

public class MessageCrowMountPlayer implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageCrowMountPlayer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "crow_mount_player"));
    public static final StreamCodec<FriendlyByteBuf, MessageCrowMountPlayer> CODEC = StreamCodec.ofMember(MessageCrowMountPlayer::write, MessageCrowMountPlayer::read);

    public int rider;
    public int mount;

    public MessageCrowMountPlayer(int rider, int mount) {
        this.rider = rider;
        this.mount = mount;
    }

    public MessageCrowMountPlayer() {}

    public static MessageCrowMountPlayer read(FriendlyByteBuf buf) {
        return new MessageCrowMountPlayer(buf.readInt(), buf.readInt());
    }

    public static void write(MessageCrowMountPlayer message, FriendlyByteBuf buf) {
        buf.writeInt(message.rider);
        buf.writeInt(message.mount);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageCrowMountPlayer message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(message.rider);
                Entity mountEntity = player.level().getEntity(message.mount);
                if (entity instanceof EntityCrow && mountEntity instanceof Player && entity.distanceTo(mountEntity) < 16D) {
                    entity.startRiding(mountEntity, true);
                }
            }
        });
    }
}
