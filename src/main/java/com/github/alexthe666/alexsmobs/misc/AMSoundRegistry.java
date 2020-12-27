package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMSoundRegistry {

    public static final SoundEvent GRIZZLY_BEAR_IDLE = createSoundEvent("grizzly_bear_idle");

    public static final SoundEvent GRIZZLY_BEAR_HURT = createSoundEvent("grizzly_bear_hurt");

    public static final SoundEvent GRIZZLY_BEAR_DIE = createSoundEvent("grizzly_bear_die");

    public static final SoundEvent ROADRUNNER_IDLE = createSoundEvent("roadrunner_idle");

    public static final SoundEvent ROADRUNNER_HURT = createSoundEvent("roadrunner_hurt");

    public static final SoundEvent GAZELLE_HURT = createSoundEvent("gazelle_hurt");

    public static final SoundEvent CROCODILE_IDLE = createSoundEvent("crocodile_idle");

    public static final SoundEvent CROCODILE_HURT = createSoundEvent("crocodile_hurt");

    public static final SoundEvent CROCODILE_BITE = createSoundEvent("crocodile_bite");

    public static final SoundEvent CROCODILE_BABY = createSoundEvent("crocodile_baby");

    public static final SoundEvent FLY_IDLE = createSoundEvent("fly_idle");

    public static final SoundEvent FLY_HURT = createSoundEvent("fly_hurt");

    public static final SoundEvent HUMMINGBIRD_IDLE = createSoundEvent("hummingbird_idle");

    public static final SoundEvent HUMMINGBIRD_HURT = createSoundEvent("hummingbird_hurt");

    public static final SoundEvent HUMMINGBIRD_LOOP = createSoundEvent("hummingbird_loop");

    public static final SoundEvent ORCA_IDLE = createSoundEvent("orca_idle");

    public static final SoundEvent ORCA_HURT = createSoundEvent("orca_hurt");

    public static final SoundEvent ORCA_DIE = createSoundEvent("orca_die");

    public static final SoundEvent GORILLA_IDLE = createSoundEvent("gorilla_idle");

    public static final SoundEvent GORILLA_HURT = createSoundEvent("gorilla_hurt");

    public static final SoundEvent MOSQUITO_LOOP = createSoundEvent("mosquito_loop");

    public static final SoundEvent MOSQUITO_HURT = createSoundEvent("mosquito_hurt");

    public static final SoundEvent MOSQUITO_DIE = createSoundEvent("mosquito_die");

    public static final SoundEvent RATTLESNAKE_LOOP = createSoundEvent("rattlesnake_loop");

    public static final SoundEvent RATTLESNAKE_HURT = createSoundEvent("rattlesnake_hurt");

    public static final SoundEvent RATTLESNAKE_ATTACK = createSoundEvent("rattlesnake_attack");

    public static final SoundEvent ENDERGRADE_IDLE = createSoundEvent("endergrade_idle");

    public static final SoundEvent ENDERGRADE_HURT = createSoundEvent("endergrade_hurt");

    public static final SoundEvent LOBSTER_HURT = createSoundEvent("lobster_hurt");

    public static final SoundEvent LOBSTER_ATTACK = createSoundEvent("lobster_attack");

    public static final SoundEvent KOMODO_DRAGON_IDLE = createSoundEvent("komodo_dragon_idle");

    public static final SoundEvent KOMODO_DRAGON_HURT = createSoundEvent("komodo_dragon_hurt");

    public static final SoundEvent SUNBIRD_IDLE = createSoundEvent("sunbird_idle");

    public static final SoundEvent SUNBIRD_HURT = createSoundEvent("sunbird_hurt");

    public static final SoundEvent CAPUCHIN_MONKEY_IDLE = createSoundEvent("capuchin_monkey_idle");

    public static final SoundEvent CAPUCHIN_MONKEY_HURT = createSoundEvent("capuchin_monkey_hurt");

    public static final SoundEvent BONE_SERPENT_IDLE = createSoundEvent("bone_serpent_idle");

    public static final SoundEvent BONE_SERPENT_HURT = createSoundEvent("bone_serpent_hurt");

    public static final SoundEvent CENTIPEDE_WALK = createSoundEvent("centipede_walk");

    public static final SoundEvent CENTIPEDE_HURT = createSoundEvent("centipede_hurt");

    public static final SoundEvent CENTIPEDE_ATTACK = createSoundEvent("centipede_attack");

    public static final SoundEvent WARPED_TOAD_IDLE = createSoundEvent("warped_toad_idle");

    public static final SoundEvent WARPED_TOAD_HURT = createSoundEvent("warped_toad_hurt");

    public static final SoundEvent MOOSE_IDLE = createSoundEvent("moose_idle");

    public static final SoundEvent MOOSE_HURT = createSoundEvent("moose_hurt");

    public static final SoundEvent MOOSE_JOSTLE = createSoundEvent("moose_jostle");

    public static final SoundEvent MIMICUBE_JUMP = createSoundEvent("mimicube_jump");

    public static final SoundEvent MIMICUBE_HURT = createSoundEvent("mimicube_hurt");

    public static final SoundEvent RACCOON_IDLE = createSoundEvent("raccoon_idle");

    public static final SoundEvent RACCOON_HURT = createSoundEvent("raccoon_hurt");

    public static final SoundEvent MARACA = createSoundEvent("maraca");

    public static final SoundEvent LA_CUCARACHA = createSoundEvent("la_cucaracha");

    private static SoundEvent createSoundEvent(final String soundName) {
        final ResourceLocation soundID = new ResourceLocation(AlexsMobs.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
        try {
            for (Field f : AMSoundRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof SoundEvent) {
                    event.getRegistry().register((SoundEvent) obj);
                } else if (obj instanceof SoundEvent[]) {
                    for (SoundEvent soundEvent : (SoundEvent[]) obj) {
                        event.getRegistry().register(soundEvent);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
