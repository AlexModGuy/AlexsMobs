package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTarantulaHawkSting {

    public int hawk;
    public int spider;

    public MessageTarantulaHawkSting(int rider, int mount) {
        this.hawk = rider;
        this.spider = mount;
    }

    public MessageTarantulaHawkSting() {
    }

    public static MessageTarantulaHawkSting read(PacketBuffer buf) {
        return new MessageTarantulaHawkSting(buf.readInt(), buf.readInt());
    }

    public static void write(MessageTarantulaHawkSting message, PacketBuffer buf) {
        buf.writeInt(message.hawk);
        buf.writeInt(message.spider);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageTarantulaHawkSting message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.hawk);
                    Entity spider = player.world.getEntityByID(message.spider);
                    if (entity instanceof EntityTarantulaHawk && spider instanceof LivingEntity && ((LivingEntity) spider).getCreatureAttribute() == CreatureAttribute.ARTHROPOD) {
                        ((LivingEntity) spider).addPotionEffect(new EffectInstance(AMEffectRegistry.DEBILITATING_STING, EntityTarantulaHawk.STING_DURATION));
                    }
                }
            }
        }
    }
}