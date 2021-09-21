package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMParticleRegistry {

    public static final SimpleParticleType GUSTER_SAND_SPIN = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:guster_sand_spin");
    public static final SimpleParticleType GUSTER_SAND_SHOT = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:guster_sand_shot");
    public static final SimpleParticleType GUSTER_SAND_SPIN_RED = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:guster_sand_spin_red");
    public static final SimpleParticleType GUSTER_SAND_SHOT_RED = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:guster_sand_shot_red");
    public static final SimpleParticleType GUSTER_SAND_SPIN_SOUL = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:guster_sand_spin_soul");
    public static final SimpleParticleType GUSTER_SAND_SHOT_SOUL = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:guster_sand_shot_soul");
    public static final SimpleParticleType HEMOLYMPH = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:hemolymph");
    public static final SimpleParticleType PLATYPUS_SENSE = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:platypus_sense");
    public static final SimpleParticleType WHALE_SPLASH = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:whale_splash");
    public static final SimpleParticleType DNA = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:dna");
    public static final SimpleParticleType SHOCKED = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:shocked");
    public static final SimpleParticleType WORM_PORTAL = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:worm_portal");
    public static final SimpleParticleType INVERT_DIG = (SimpleParticleType) new SimpleParticleType(true).setRegistryName("alexsmobs:invert_dig");
    public static final SimpleParticleType TEETH_GLINT = (SimpleParticleType) new SimpleParticleType(false).setRegistryName("alexsmobs:teeth_glint");

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
