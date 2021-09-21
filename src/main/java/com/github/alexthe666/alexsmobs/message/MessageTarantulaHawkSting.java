package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
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

    public static MessageTarantulaHawkSting read(FriendlyByteBuf buf) {
        return new MessageTarantulaHawkSting(buf.readInt(), buf.readInt());
    }

    public static void write(MessageTarantulaHawkSting message, FriendlyByteBuf buf) {
        buf.writeInt(message.hawk);
        buf.writeInt(message.spider);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageTarantulaHawkSting message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null) {
                if (player.level != null) {
                    Entity entity = player.level.getEntity(message.hawk);
                    Entity spider = player.level.getEntity(message.spider);
                    if (entity instanceof EntityTarantulaHawk && spider instanceof LivingEntity && ((LivingEntity) spider).getMobType() == MobType.ARTHROPOD) {
                        ((LivingEntity) spider).addEffect(new MobEffectInstance(AMEffectRegistry.DEBILITATING_STING, EntityTarantulaHawk.STING_DURATION));
                    }
                }
            }
        }
    }
}