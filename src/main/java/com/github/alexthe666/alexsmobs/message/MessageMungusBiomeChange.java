package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageMungusBiomeChange  {

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

    public MessageMungusBiomeChange() {
    }

    public static MessageMungusBiomeChange read(FriendlyByteBuf buf) {
        return new MessageMungusBiomeChange(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public static void write(MessageMungusBiomeChange message, FriendlyByteBuf buf) {
        buf.writeInt(message.mungusID);
        buf.writeInt(message.posX);
        buf.writeInt(message.posZ);
        buf.writeUtf(message.biomeOption);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageMungusBiomeChange message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.level != null) {
                    Entity entity = player.level.getEntity(message.mungusID);
                    Registry<Biome> registry = player.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
                    Biome biome =  registry.get(new ResourceLocation(message.biomeOption));
                    if(AMConfig.mungusBiomeTransformationType == 2) {
                        if (entity instanceof EntityMungus && entity.distanceToSqr(message.posX, entity.getY(), message.posZ) < 1000 && biome != null) {
                            LevelChunk chunk = player.level.getChunkAt(new BlockPos(message.posX, 0, message.posZ));
                            ChunkBiomeContainer container = chunk.getBiomes();
                            if (container != null) {
                                for (int i = 0; i < container.biomes.length; i++) {
                                    container.biomes[i] = biome;
                                }
                            }
                            AlexsMobs.PROXY.updateBiomeVisuals(message.posX, message.posZ);
                        }
                    }
                }
            }
        }
    }

}
