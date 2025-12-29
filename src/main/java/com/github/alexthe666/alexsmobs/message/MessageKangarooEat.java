package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageKangarooEat implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageKangarooEat> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "kangaroo_eat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageKangarooEat> CODEC = StreamCodec.ofMember(MessageKangarooEat::write, MessageKangarooEat::read);

    public int kangaroo;
    public ItemStack stack;

    public MessageKangarooEat(int kangaroo, ItemStack stack) {
        this.kangaroo = kangaroo;
        this.stack = stack;
    }

    public MessageKangarooEat() {}

    public static MessageKangarooEat read(RegistryFriendlyByteBuf buf) {
        return new MessageKangarooEat(buf.readInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static void write(MessageKangarooEat message, RegistryFriendlyByteBuf buf) {
        buf.writeInt(message.kangaroo);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.stack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageKangarooEat message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(message.kangaroo);
                if (entity instanceof EntityKangaroo kangaroo && ((EntityKangaroo) entity).kangarooInventory != null) {
                    for (int i = 0; i < 7; i++) {
                        double d2 = kangaroo.getRandom().nextGaussian() * 0.02D;
                        double d0 = kangaroo.getRandom().nextGaussian() * 0.02D;
                        double d1 = kangaroo.getRandom().nextGaussian() * 0.02D;
                        entity.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, message.stack), entity.getX() + (double) (kangaroo.getRandom().nextFloat() * entity.getBbWidth()) - (double) entity.getBbWidth() * 0.5F, entity.getY() + entity.getBbHeight() * 0.5F + (double) (kangaroo.getRandom().nextFloat() * entity.getBbHeight() * 0.5F), entity.getZ() + (double) (kangaroo.getRandom().nextFloat() * entity.getBbWidth()) - (double) entity.getBbWidth() * 0.5F, d0, d1, d2);
                    }
                }
            }
        });
    }
}
