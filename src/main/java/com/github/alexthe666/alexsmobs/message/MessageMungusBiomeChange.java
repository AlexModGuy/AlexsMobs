package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Field;

public class MessageMungusBiomeChange implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageMungusBiomeChange> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "mungus_biome_change"));
    public static final StreamCodec<FriendlyByteBuf, MessageMungusBiomeChange> CODEC = StreamCodec.ofMember(MessageMungusBiomeChange::write, MessageMungusBiomeChange::read);

    public int mungusID;
    public int posX;
    public int posZ;
    public String biomeOption;

    public MessageMungusBiomeChange(int mungusID, int posX, int posY, String biomeOption) {
        this.mungusID = mungusID;
        this.posX = posX;
        this.posZ = posY;
        this.biomeOption = biomeOption;
    }

    public MessageMungusBiomeChange() {}

    public static MessageMungusBiomeChange read(FriendlyByteBuf buf) {
        return new MessageMungusBiomeChange(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public static void write(MessageMungusBiomeChange message, FriendlyByteBuf buf) {
        buf.writeInt(message.mungusID);
        buf.writeInt(message.posX);
        buf.writeInt(message.posZ);
        buf.writeUtf(message.biomeOption);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(MessageMungusBiomeChange message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(message.mungusID);
                Registry<Biome> registry = player.level().registryAccess().registryOrThrow(Registries.BIOME);
                Biome biome = registry.get(ResourceLocation.parse(message.biomeOption));
                ResourceKey<Biome> resourceKey = registry.getResourceKey(biome).orElse(null);
                Holder<Biome> holder = resourceKey != null ? registry.getHolder(resourceKey).orElse(null) : null;
                if (AMConfig.mungusBiomeTransformationType == 2) {
                    if (entity instanceof EntityMungus && entity.distanceToSqr(message.posX, entity.getY(), message.posZ) < 1000 && biome != null && holder != null) {
                        LevelChunk chunk = player.level().getChunkAt(new BlockPos(message.posX, 0, message.posZ));
                        int i = QuartPos.fromBlock(chunk.getMinBuildHeight());
                        int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
                        int l = Mth.clamp(QuartPos.fromBlock((int)entity.getY()), i, k);
                        int j = chunk.getSectionIndex(QuartPos.toBlock(l));
                        LevelChunkSection section = chunk.getSection(j);
                        if (section != null) {
                            PalettedContainer<Holder<Biome>> container = section.getBiomes().recreate();
                            for (int biomeX = 0; biomeX < 4; ++biomeX) {
                                for (int biomeY = 0; biomeY < 4; ++biomeY) {
                                    for (int biomeZ = 0; biomeZ < 4; ++biomeZ) {
                                        container.getAndSetUnchecked(biomeX, biomeY, biomeZ, holder);
                                    }
                                }
                            }
                            try {
                                Field biomesField = ObfuscationReflectionHelper.findField(LevelChunkSection.class, "biomes");
                                biomesField.setAccessible(true);
                                biomesField.set(section, container);
                            } catch (Exception e) {
                                // Fallback: log error
                                e.printStackTrace();
                            }
                        }
                        AlexsMobs.PROXY.updateBiomeVisuals(message.posX, message.posZ);
                    }
                }
            }
        });
    }
}
