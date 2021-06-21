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

    public static final SoundEvent SEAL_IDLE = createSoundEvent("seal_idle");

    public static final SoundEvent SEAL_HURT = createSoundEvent("seal_hurt");

    public static final SoundEvent COCKROACH_HURT = createSoundEvent("cockroach_hurt");

    public static final SoundEvent SHOEBILL_HURT = createSoundEvent("shoebill_hurt");

    public static final SoundEvent ELEPHANT_IDLE = createSoundEvent("elephant_idle");

    public static final SoundEvent ELEPHANT_HURT = createSoundEvent("elephant_hurt");

    public static final SoundEvent ELEPHANT_DIE = createSoundEvent("elephant_die");

    public static final SoundEvent ELEPHANT_TRUMPET = createSoundEvent("elephant_trumpet");

    public static final SoundEvent ELEPHANT_WALK = createSoundEvent("elephant_walk");

    public static final SoundEvent SOUL_VULTURE_IDLE = createSoundEvent("soul_vulture_idle");

    public static final SoundEvent SOUL_VULTURE_HURT = createSoundEvent("soul_vulture_hurt");

    public static final SoundEvent SNOW_LEOPARD_IDLE = createSoundEvent("snow_leopard_idle");

    public static final SoundEvent SNOW_LEOPARD_HURT = createSoundEvent("snow_leopard_hurt");

    public static final SoundEvent SPECTRE_IDLE = createSoundEvent("spectre_idle");

    public static final SoundEvent SPECTRE_HURT = createSoundEvent("spectre_hurt");

    public static final SoundEvent CROW_IDLE = createSoundEvent("crow_idle");

    public static final SoundEvent CROW_HURT = createSoundEvent("crow_hurt");

    public static final SoundEvent ALLIGATOR_SNAPPING_TURTLE_IDLE = createSoundEvent("alligator_snapping_turtle_idle");

    public static final SoundEvent ALLIGATOR_SNAPPING_TURTLE_HURT = createSoundEvent("alligator_snapping_turtle_hurt");

    public static final SoundEvent MUNGUS_IDLE = createSoundEvent("mungus_idle");

    public static final SoundEvent MUNGUS_HURT = createSoundEvent("mungus_hurt");

    public static final SoundEvent MUNGUS_LASER_END = createSoundEvent("mungus_laser_end");

    public static final SoundEvent MUNGUS_LASER_LOOP = createSoundEvent("mungus_laser_loop");

    public static final SoundEvent MANTIS_SHRIMP_SNAP = createSoundEvent("mantis_shrimp_snap");

    public static final SoundEvent MANTIS_SHRIMP_HURT = createSoundEvent("mantis_shrimp_hurt");

    public static final SoundEvent GUSTER_IDLE = createSoundEvent("guster_idle");

    public static final SoundEvent GUSTER_HURT = createSoundEvent("guster_hurt");
    
    public static final SoundEvent WARPED_MOSCO_IDLE = createSoundEvent("warped_mosco_idle");

    public static final SoundEvent WARPED_MOSCO_HURT = createSoundEvent("warped_mosco_hurt");

    public static final SoundEvent STRADDLER_IDLE = createSoundEvent("straddler_idle");

    public static final SoundEvent STRADDLER_HURT = createSoundEvent("straddler_hurt");

    public static final SoundEvent EMU_IDLE = createSoundEvent("emu_idle");

    public static final SoundEvent EMU_HURT = createSoundEvent("emu_hurt");

    public static final SoundEvent PLATYPUS_IDLE = createSoundEvent("platypus_idle");

    public static final SoundEvent PLATYPUS_HURT = createSoundEvent("platypus_hurt");

    public static final SoundEvent DROPBEAR_IDLE = createSoundEvent("dropbear_idle");

    public static final SoundEvent DROPBEAR_HURT = createSoundEvent("dropbear_hurt");

    public static final SoundEvent TASMANIAN_DEVIL_IDLE = createSoundEvent("tasmanian_devil_idle");

    public static final SoundEvent TASMANIAN_DEVIL_HURT = createSoundEvent("tasmanian_devil_hurt");

    public static final SoundEvent TASMANIAN_DEVIL_ROAR = createSoundEvent("tasmanian_devil_roar");

    public static final SoundEvent KANGAROO_IDLE = createSoundEvent("kangaroo_idle");

    public static final SoundEvent KANGAROO_HURT = createSoundEvent("kangaroo_hurt");

    public static final SoundEvent CACHALOT_WHALE_IDLE = createSoundEvent("cachalot_whale_idle");

    public static final SoundEvent CACHALOT_WHALE_HURT = createSoundEvent("cachalot_whale_hurt");

    public static final SoundEvent CACHALOT_WHALE_CLICK = createSoundEvent("cachalot_whale_click");

    public static final SoundEvent LEAFCUTTER_ANT_HURT = createSoundEvent("leafcutter_ant_hurt");

    public static final SoundEvent LEAFCUTTER_ANT_QUEEN_HURT = createSoundEvent("leafcutter_ant_queen_hurt");

    public static final SoundEvent ENDERIOPHAGE_HURT = createSoundEvent("enderiophage_hurt");

    public static final SoundEvent ENDERIOPHAGE_SQUISH = createSoundEvent("enderiophage_squish");

    public static final SoundEvent ENDERIOPHAGE_WALK = createSoundEvent("enderiophage_walk");

    public static final SoundEvent MUSIC_DISC_THIME = createSoundEvent("music_disc_thime");

    public static final SoundEvent BALD_EAGLE_IDLE = createSoundEvent("bald_eagle_idle");

    public static final SoundEvent BALD_EAGLE_HURT = createSoundEvent("bald_eagle_hurt");

    public static final SoundEvent TIGER_IDLE = createSoundEvent("tiger_idle");

    public static final SoundEvent TIGER_HURT = createSoundEvent("tiger_hurt");

    public static final SoundEvent TIGER_ANGRY = createSoundEvent("tiger_angry");

    public static final SoundEvent TARANTULA_HAWK_WING = createSoundEvent("tarantula_hawk_wing");

    public static final SoundEvent TARANTULA_HAWK_HURT = createSoundEvent("tarantula_hawk_hurt");

    public static final SoundEvent MUSIC_WORMBOSS = createSoundEvent("music_wormboss");

    public static final SoundEvent VOID_WORM_IDLE = createSoundEvent("void_worm_idle");

    public static final SoundEvent VOID_WORM_HURT = createSoundEvent("void_worm_hurt");

    public static final SoundEvent VOID_PORTAL_CLOSE = createSoundEvent("void_portal_close");

    public static final SoundEvent VOID_PORTAL_OPEN = createSoundEvent("void_portal_open");

    public static final SoundEvent MIMIC_OCTOPUS_IDLE = createSoundEvent("mimic_octopus_idle");

    public static final SoundEvent MIMIC_OCTOPUS_HURT = createSoundEvent("mimic_octopus_hurt");

    public static final SoundEvent MUSIC_DISC_DAZE = createSoundEvent("music_disc_daze");

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
