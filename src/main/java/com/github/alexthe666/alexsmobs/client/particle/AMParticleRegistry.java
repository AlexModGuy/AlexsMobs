package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMParticleRegistry {

    public static final BasicParticleType GUSTER_SAND_SPIN = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:guster_sand_spin");
    public static final BasicParticleType GUSTER_SAND_SHOT = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:guster_sand_shot");
    public static final BasicParticleType HEMOLYMPH = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:hemolymph");
    public static final BasicParticleType PLATYPUS_SENSE = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:platypus_sense");
    public static final BasicParticleType WHALE_SPLASH = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:whale_splash");
    public static final BasicParticleType DNA = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:dna");
    public static final BasicParticleType SHOCKED = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:shocked");
    public static final BasicParticleType WORM_PORTAL = (BasicParticleType) new BasicParticleType(false).setRegistryName("alexsmobs:worm_portal");
    public static final BasicParticleType INVERT_DIG = (BasicParticleType) new BasicParticleType(true).setRegistryName("alexsmobs:invert_dig");

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        try {
            for (Field f : AMParticleRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof ParticleType) {
                    event.getRegistry().register((ParticleType) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
