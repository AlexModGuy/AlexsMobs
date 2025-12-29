package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageMosquitoMountPlayer implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageMosquitoMountPlayer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "mosquito_mount_player"));
    public static final StreamCodec<FriendlyByteBuf, MessageMosquitoMountPlayer> CODEC = StreamCodec.ofMember(MessageMosquitoMountPlayer::write, MessageMosquitoMountPlayer::read);

    public int rider;
    public int mount;

    public MessageMosquitoMountPlayer(int rider, int mount) {
        this.rider = rider;
        this.mount = mount;
    }

    public MessageMosquitoMountPlayer() {}

    public static MessageMosquitoMountPlayer read(FriendlyByteBuf buf) {
        return new MessageMosquitoMountPlayer(buf.readInt(), buf.readInt());
    }

    public static void write(MessageMosquitoMountPlayer message, FriendlyByteBuf buf) {
        buf.writeInt(message.rider);
        buf.writeInt(message.mount);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageMosquitoMountPlayer message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(message.rider);
                Entity mountEntity = player.level().getEntity(message.mount);
                if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityEnderiophage || entity instanceof EntityBaldEagle) && mountEntity instanceof Player && entity.distanceTo(mountEntity) < 16D) {
                    entity.startRiding(mountEntity, true);
                }
            }
        });
    }
}