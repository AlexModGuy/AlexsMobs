package com.github.alexthe666.alexsmobs.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class AMBlockTagProvider extends BlockTagsProvider {
    public AMBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(AMTagRegistry.AM_SPAWNS)
            .addTag(BlockTags.BASE_STONE_OVERWORLD)
            .addTag(BlockTags.DIRT)
            .addTag(BlockTags.SAND)
            .add(Blocks.GRANITE)
            .addTag(BlockTags.TERRACOTTA)
            .add(Blocks.MUD);

        tag(AMTagRegistry.CAPUCHIN_MONKEY_SPAWNS)
            .addTag(BlockTags.LEAVES)
            .addTag(BlockTags.LOGS)
            .add(Blocks.GRASS_BLOCK)
            .add(Blocks.MANGROVE_ROOTS)
            .add(Blocks.MUDDY_MANGROVE_ROOTS);

        tag(AMTagRegistry.CACHALOT_WHALE_BREAKABLES)
            .addTag(AMTagRegistry.ORCA_BREAKABLES)
            .addTag(BlockTags.PLANKS)
            .addTag(BlockTags.LOGS)
            .addTag(BlockTags.WOODEN_SLABS)
            .addTag(BlockTags.WOODEN_STAIRS)
            .addTag(BlockTags.WOOL);

        tag(AMTagRegistry.CATFISH_BLOCK_FASCINATIONS)
            .add(Blocks.SEA_LANTERN);

        tag(AMTagRegistry.CROW_FEARS)
            .add(Blocks.CARVED_PUMPKIN)
            .add(Blocks.JACK_O_LANTERN);

        tag(AMTagRegistry.CROW_FOODBLOCKS)
            .addTag(BlockTags.CROPS);

        tag(AMTagRegistry.CROW_HOME_BLOCKS)
            .add(Blocks.HAY_BLOCK);

        tag(AMTagRegistry.DROPS_ACACIA_BLOSSOMS)
            .add(Blocks.ACACIA_LEAVES);

        tag(AMTagRegistry.DROPS_BANANAS)
            .add(Blocks.JUNGLE_LEAVES);

        tag(AMTagRegistry.ELEPHANT_FOODBLOCKS)
            .addTag(BlockTags.LEAVES)
            .add(Blocks.HAY_BLOCK)
            .add(Blocks.MELON)
            .add(Blocks.HAY_BLOCK)
            .add(Blocks.SWEET_BERRY_BUSH);

        tag(AMTagRegistry.ENDERGRADE_BREAKABLES)
            .add(Blocks.CHORUS_FLOWER);

        tag(AMTagRegistry.FLUTTER_SPAWNS)
            .add(Blocks.MOSS_BLOCK);

        tag(AMTagRegistry.FROSTSTALKER_FEARS)
            .addTag(BlockTags.FIRE)
            .addTag(BlockTags.CAMPFIRES)
            .add(Blocks.MAGMA_BLOCK)
            .add(Blocks.LAVA)
            .add(Blocks.LAVA_CAULDRON);

        tag(AMTagRegistry.FROSTSTALKER_SPAWNS)
            .add(Blocks.ICE)
            .add(Blocks.SNOW_BLOCK);

        tag(AMTagRegistry.GELADA_MONKEY_GRASS)
            .add(Blocks.SHORT_GRASS)
            .add(Blocks.TALL_GRASS)
            .add(Blocks.DEAD_BUSH)
            .add(Blocks.FERN)
            .add(Blocks.LARGE_FERN)
            .add(Blocks.GLOW_LICHEN);

        tag(AMTagRegistry.GORILLA_BREAKABLES)
            .addTag(BlockTags.LEAVES)
            .add(Blocks.BAMBOO);

        tag(AMTagRegistry.GORILLA_SPAWNS)
            .addTag(BlockTags.LEAVES)
            .addTag(BlockTags.LOGS)
            .add(Blocks.GRASS_BLOCK);

        tag(AMTagRegistry.GRIZZLY_BEEHIVE)
            .addTag(BlockTags.BEEHIVES);

        tag(AMTagRegistry.HUMMINGBIRD_POLLINATES)
            .addTag(BlockTags.FLOWERS)
            .addTag(BlockTags.BEE_GROWABLES);

        tag(AMTagRegistry.HUMMINGBIRD_SPAWNS)
            .addTag(BlockTags.LEAVES)
            .addTag(BlockTags.LOGS)
            .add(Blocks.GRASS_BLOCK);

        tag(AMTagRegistry.LAVIATHAN_BREAKABLES)
            .add(Blocks.LILY_PAD);

        tag(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES)
            .addTag(BlockTags.LEAVES);

        tag(AMTagRegistry.LOBSTER_SPAWNS)
            .addTag(AMTagRegistry.AM_SPAWNS)
            .add(Blocks.WATER);

        tag(AMTagRegistry.MANTIS_SHRIMP_SPAWNS)
            .addTag(BlockTags.CORAL_BLOCKS)
            .add(Blocks.MUD)
            .add(Blocks.MUDDY_MANGROVE_ROOTS);

        tag(AMTagRegistry.MUNGUS_REPLACE_MUSHROOM)
            .add(Blocks.GRASS_BLOCK)
            .add(Blocks.DIRT)
            .add(Blocks.COARSE_DIRT)
            .add(Blocks.PODZOL)
            .add(Blocks.MYCELIUM);

        tag(AMTagRegistry.MUNGUS_REPLACE_NETHER)
            .add(Blocks.NETHERRACK)
            .add(Blocks.WARPED_NYLIUM)
            .add(Blocks.CRIMSON_NYLIUM);

        tag(AMTagRegistry.ORCA_BREAKABLES)
            .add(Blocks.ICE)
            .add(Blocks.FROSTED_ICE)
            .add(Blocks.LILY_PAD);

        tag(AMTagRegistry.PLATYPUS_DIGABLES)
            .add(Blocks.CLAY);

        tag(AMTagRegistry.POTOO_PERCHES)
            .addTag(BlockTags.LOGS)
            .addTag(BlockTags.LEAVES);

        tag(AMTagRegistry.PUPFISH_EATABLES)
            .add(Blocks.MOSS_BLOCK)
            .add(Blocks.MOSS_CARPET)
            .add(Blocks.MOSSY_COBBLESTONE)
            .add(Blocks.MOSSY_STONE_BRICKS);

        tag(AMTagRegistry.RAIN_FROG_SPAWNS)
            .addTag(BlockTags.SAND);

        tag(AMTagRegistry.ROCKY_ROLLER_SPAWNS)
            .add(Blocks.POINTED_DRIPSTONE)
            .add(Blocks.DRIPSTONE_BLOCK);

        tag(AMTagRegistry.SEAL_DIGABLES)
            .addTag(BlockTags.SAND)
            .add(Blocks.GRAVEL)
            .add(Blocks.GRASS_BLOCK)
            .add(Blocks.DIRT)
            .add(Blocks.CLAY);

        tag(AMTagRegistry.SEAL_SPAWNS)
            .addTag(AMTagRegistry.AM_SPAWNS)
            .addTag(BlockTags.ICE)
            .addTag(BlockTags.BASE_STONE_OVERWORLD)
            .add(Blocks.SNOW_BLOCK);

        tag(AMTagRegistry.SNOW_LEOPARD_SPAWNS)
            .addTag(BlockTags.BASE_STONE_OVERWORLD)
            .add(Blocks.DIRT)
            .add(Blocks.GRASS_BLOCK);

        tag(AMTagRegistry.SOUL_VULTURE_PERCHES)
            .add(Blocks.BONE_BLOCK)
            .addTag(BlockTags.LOGS);

        tag(AMTagRegistry.SOUL_VULTURE_SPAWNS)
            .addTag(AMTagRegistry.SOUL_VULTURE_PERCHES)
            .addTag(BlockTags.SOUL_FIRE_BASE_BLOCKS);

        tag(AMTagRegistry.TARANTULA_HAWK_SPAWNS)
            .addTag(BlockTags.SAND);

        tag(AMTagRegistry.TUSKLIN_SPAWNS)
            .add(Blocks.SNOW_BLOCK);

        tag(AMTagRegistry.VOID_WORM_BREAKABLES)
            .addTag(BlockTags.LEAVES)
            .add(Blocks.BAMBOO)
            .add(Blocks.CHORUS_PLANT)
            .add(Blocks.CHORUS_FLOWER);

        tag(AMTagRegistry.WARPED_MOSCO_BREAKABLES)
            .addTag(BlockTags.LEAVES)
            .add(Blocks.NETHER_WART_BLOCK)
            .add(Blocks.WARPED_WART_BLOCK);

        for (TagKey<Block> spawnTag : List.of(
            AMTagRegistry.ALLIGATOR_SNAPPING_TURTLE_SPAWNS,
            AMTagRegistry.ANACONDA_SPAWNS,
            AMTagRegistry.CAIMAN_SPAWNS,
            AMTagRegistry.CROCODILE_SPAWNS,
            AMTagRegistry.EMU_SPAWNS,
            AMTagRegistry.FLY_SPAWNS,
            AMTagRegistry.KANGAROO_SPAWNS,
            AMTagRegistry.KOMODO_DRAGON_SPAWNS,
            AMTagRegistry.LEAFCUTTER_PUPA_USABLE_ON,
            AMTagRegistry.MIMIC_OCTOPUS_SPAWNS,
            AMTagRegistry.PLATYPUS_SPAWNS,
            AMTagRegistry.RATTLESNAKE_SPAWNS,
            AMTagRegistry.ROADRUNNER_SPAWNS
        )) {
            tag(spawnTag)
                .addTag(AMTagRegistry.AM_SPAWNS);
        }

        tag(BlockTags.MINEABLE_WITH_HOE)
            .add(AMBlockRegistry.SCULK_BOOMER.value());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(AMBlockRegistry.HUMMINGBIRD_FEEDER.value())
            .add(AMBlockRegistry.GUSTMAKER.value())
            .add(AMBlockRegistry.STRADDLITE_BLOCK.value())
            .add(AMBlockRegistry.CAPSID.value())
            .add(AMBlockRegistry.VOID_WORM_BEAK.value())
            .add(AMBlockRegistry.RAINBOW_GLASS.value())
            .add(AMBlockRegistry.TRANSMUTATION_TABLE.value())
            .add(AMBlockRegistry.CRYSTALIZED_BANANA_SLUG_MUCUS.value());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.value())
            .add(AMBlockRegistry.LEAFCUTTER_ANTHILL.value())
            .add(AMBlockRegistry.BANANA_SLUG_SLIME_BLOCK.value());
    }
}
