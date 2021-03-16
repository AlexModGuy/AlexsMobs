package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
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

    public static MessageMungusBiomeChange read(PacketBuffer buf) {
        return new MessageMungusBiomeChange(buf.readInt(), buf.readInt(), buf.readInt(), buf.readString());
    }

    public static void write(MessageMungusBiomeChange message, PacketBuffer buf) {
        buf.writeInt(message.mungusID);
        buf.writeInt(message.posX);
        buf.writeInt(message.posZ);
        buf.writeString(message.biomeOption);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageMungusBiomeChange message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.mungusID);
                    Registry<Biome> registry = player.world.func_241828_r().getRegistry(Registry.BIOME_KEY);
                    Biome biome =  registry.getOrDefault(new ResourceLocation(message.biomeOption));
                    if(AMConfig.mungusBiomeTransformationType == 2) {
                        if (entity instanceof EntityMungus && entity.getDistanceSq(message.posX, entity.getPosY(), message.posZ) < 1000 && biome != null) {
                            Chunk chunk = player.world.getChunkAt(new BlockPos(message.posX, 0, message.posZ));
                            BiomeContainer container = chunk.getBiomes();
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
