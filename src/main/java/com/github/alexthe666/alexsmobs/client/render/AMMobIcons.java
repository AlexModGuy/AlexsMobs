package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class AMMobIcons {

    private static List<Pair<EntityType, Float>> MOB_ICONS = Util.make(Lists.newArrayList(), (list) -> {
        list.add(new Pair<>(AMEntityRegistry.GRIZZLY_BEAR.get(), 0.6F));
        list.add(new Pair<>(AMEntityRegistry.ROADRUNNER.get(), 0.8F));
        list.add(new Pair<>(AMEntityRegistry.BONE_SERPENT.get(), 0.55F));
        list.add(new Pair<>(AMEntityRegistry.GAZELLE.get(), 0.6F));
        list.add(new Pair<>(AMEntityRegistry.CROCODILE.get(), 0.3F));
        list.add(new Pair<>(AMEntityRegistry.FLY.get(), 1.3F));
        list.add(new Pair<>(AMEntityRegistry.HUMMINGBIRD.get(), 1.5F));
        list.add(new Pair<>(AMEntityRegistry.ORCA.get(), 0.2F));
        list.add(new Pair<>(AMEntityRegistry.SUNBIRD.get(), 0.2F));
        list.add(new Pair<>(AMEntityRegistry.GORILLA.get(), 0.85F));
        list.add(new Pair<>(AMEntityRegistry.CRIMSON_MOSQUITO.get(), 0.6F));
        list.add(new Pair<>(AMEntityRegistry.RATTLESNAKE.get(), 0.6F));
        list.add(new Pair<>(AMEntityRegistry.ENDERGRADE.get(), 0.8F));
        list.add(new Pair<>(AMEntityRegistry.HAMMERHEAD_SHARK.get(), 0.5F));
        list.add(new Pair<>(AMEntityRegistry.LOBSTER.get(), 0.85F));
        list.add(new Pair<>(AMEntityRegistry.KOMODO_DRAGON.get(), 0.5F));
        list.add(new Pair<>(AMEntityRegistry.CAPUCHIN_MONKEY.get(), 0.85F));
        list.add(new Pair<>(AMEntityRegistry.CENTIPEDE_HEAD.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.WARPED_TOAD.get(), 0.6F));
        list.add(new Pair<>(AMEntityRegistry.MOOSE.get(), 0.38F));
        list.add(new Pair<>(AMEntityRegistry.MIMICUBE.get(), 0.95F));
        list.add(new Pair<>(AMEntityRegistry.RACCOON.get(), 0.8F));
        list.add(new Pair<>(AMEntityRegistry.BLOBFISH.get(), 1F));
        list.add(new Pair<>(AMEntityRegistry.SEAL.get(), 0.5F));
        list.add(new Pair<>(AMEntityRegistry.COCKROACH.get(), 1F));
        list.add(new Pair<>(AMEntityRegistry.SHOEBILL.get(), 0.8F));
        list.add(new Pair<>(AMEntityRegistry.ELEPHANT.get(), 0.3F));
        list.add(new Pair<>(AMEntityRegistry.SOUL_VULTURE.get(), 0.8F));
        list.add(new Pair<>(AMEntityRegistry.SNOW_LEOPARD.get(), 0.7F));
        list.add(new Pair<>(AMEntityRegistry.SPECTRE.get(), 0.3F));
        list.add(new Pair<>(AMEntityRegistry.CROW.get(), 1.3F));
        list.add(new Pair<>(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.MUNGUS.get(), 0.7F));
        list.add(new Pair<>(AMEntityRegistry.MANTIS_SHRIMP.get(), 0.7F));
        list.add(new Pair<>(AMEntityRegistry.GUSTER.get(), 0.55F));
        list.add(new Pair<>(AMEntityRegistry.WARPED_MOSCO.get(), 0.35F));
        list.add(new Pair<>(AMEntityRegistry.STRADDLER.get(), 0.38F));
        list.add(new Pair<>(AMEntityRegistry.STRADPOLE.get(), 0.9F));
        list.add(new Pair<>(AMEntityRegistry.EMU.get(), 0.7F));
        list.add(new Pair<>(AMEntityRegistry.PLATYPUS.get(), 1F));
        list.add(new Pair<>(AMEntityRegistry.DROPBEAR.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.TASMANIAN_DEVIL.get(), 1.2F));
        list.add(new Pair<>(AMEntityRegistry.KANGAROO.get(), 0.7F));
        list.add(new Pair<>(AMEntityRegistry.CACHALOT_WHALE.get(), 0.1F));
        list.add(new Pair<>(AMEntityRegistry.LEAFCUTTER_ANT.get(), 1.2F));
        list.add(new Pair<>(AMEntityRegistry.ENDERIOPHAGE.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.BALD_EAGLE.get(), 0.85F));
        list.add(new Pair<>(AMEntityRegistry.TIGER.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.TARANTULA_HAWK.get(), 0.7F));
        list.add(new Pair<>(AMEntityRegistry.VOID_WORM.get(), 0.3F));
        list.add(new Pair<>(AMEntityRegistry.FRILLED_SHARK.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.MIMIC_OCTOPUS.get(), 0.7F));
        list.add(new Pair<>(AMEntityRegistry.SEAGULL.get(), 1.2F));
        list.add(new Pair<>(AMEntityRegistry.FROSTSTALKER.get(), 0.8F));
        list.add(new Pair<>(AMEntityRegistry.TUSKLIN.get(), 0.6F));
        list.add(new Pair<>(AMEntityRegistry.LAVIATHAN.get(), 0.2F));
        list.add(new Pair<>(AMEntityRegistry.COSMAW.get(), 0.32F));
        list.add(new Pair<>(AMEntityRegistry.TOUCAN.get(), 1.3F));
        list.add(new Pair<>(AMEntityRegistry.MANED_WOLF.get(), 0.85F));
        list.add(new Pair<>(AMEntityRegistry.ANACONDA.get(), 1.0F));
        list.add(new Pair<>(AMEntityRegistry.ANTEATER.get(), 0.5F));
        list.add(new Pair<>(AMEntityRegistry.ROCKY_ROLLER.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.FLUTTER.get(), 1.15F));
        list.add(new Pair<>(AMEntityRegistry.GELADA_MONKEY.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.JERBOA.get(), 1.3F));
        list.add(new Pair<>(AMEntityRegistry.TERRAPIN.get(), 1.1F));
        list.add(new Pair<>(AMEntityRegistry.COMB_JELLY.get(), 1.0F));
        list.add(new Pair<>(AMEntityRegistry.COSMIC_COD.get(), 1.3F));
        list.add(new Pair<>(AMEntityRegistry.BUNFUNGUS.get(), 0.5F));
        list.add(new Pair<>(AMEntityRegistry.BISON.get(), 0.45F));
        list.add(new Pair<>(AMEntityRegistry.GIANT_SQUID.get(), 0.3F));
        list.add(new Pair<>(AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(), 1.4F));
        list.add(new Pair<>(AMEntityRegistry.CATFISH.get(), 1.15F));
        list.add(new Pair<>(AMEntityRegistry.FLYING_FISH.get(), 1.2F));
        list.add(new Pair<>(AMEntityRegistry.SKELEWAG.get(), 0.5F));
        list.add(new Pair<>(AMEntityRegistry.RAIN_FROG.get(), 1.8F));
        list.add(new Pair<>(AMEntityRegistry.POTOO.get(), 1.2F));
        list.add(new Pair<>(AMEntityRegistry.MUDSKIPPER.get(), 1.2F));
        list.add(new Pair<>(AMEntityRegistry.RHINOCEROS.get(), 0.4F));
        list.add(new Pair<>(AMEntityRegistry.SUGAR_GLIDER.get(), 1.3F));
        list.add(new Pair<>(AMEntityRegistry.FARSEER.get(), 0.6F));
        list.add(new Pair<>(AMEntityRegistry.SKREECHER.get(), 0.77F));
        list.add(new Pair<>(AMEntityRegistry.UNDERMINER.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.MURMUR.get(), 0.65F));
        list.add(new Pair<>(AMEntityRegistry.SKUNK.get(), 1F));
        list.add(new Pair<>(AMEntityRegistry.BANANA_SLUG.get(), 1.5F));
        list.add(new Pair<>(AMEntityRegistry.BLUE_JAY.get(), 1.4F));
    });

    public static List<Pair<EntityType, Float>> getMobIcons() {
        return MOB_ICONS;
    }


}
