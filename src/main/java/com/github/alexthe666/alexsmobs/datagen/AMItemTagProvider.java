package com.github.alexthe666.alexsmobs.datagen;

import java.util.concurrent.CompletableFuture;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.neoforged.neoforge.common.Tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

public class AMItemTagProvider extends ItemTagsProvider {
    public AMItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.MEAT)
            .add(AMItemRegistry.RAW_CATFISH.value(), AMItemRegistry.COOKED_CATFISH.value(), AMItemRegistry.FLYING_FISH.value())
            .add(AMItemRegistry.KANGAROO_BURGER.value(), AMItemRegistry.KANGAROO_MEAT.value(), AMItemRegistry.COOKED_KANGAROO_MEAT.value())
            .add(AMItemRegistry.BOILED_EMU_EGG.value())
            .add(AMItemRegistry.BLOBFISH.value())
            .add(AMItemRegistry.MOOSE_RIBS.value(), AMItemRegistry.COOKED_MOOSE_RIBS.value())
            .add(AMItemRegistry.SOPA_DE_MACACO.value())
            .add(AMItemRegistry.LOBSTER_TAIL.value(), AMItemRegistry.COOKED_LOBSTER_TAIL.value());

        tag(ItemTags.ARROWS)
            .add(AMItemRegistry.SHARK_TOOTH_ARROW.value());

        tag(ItemTags.BOOKSHELF_BOOKS)
            .add(AMItemRegistry.ANIMAL_DICTIONARY.value());

        tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
            .add(AMItemRegistry.GHOSTLY_PICKAXE.value());

        tag(ItemTags.FISHES)
            .add(AMItemRegistry.BLOBFISH.value())
            .add(AMItemRegistry.FLYING_FISH.value())
            .add(AMItemRegistry.RAW_CATFISH.value())
            .add(AMItemRegistry.COOKED_CATFISH.value());

        tag(ItemTags.FLOWERS)
            .add(AMItemRegistry.ACACIA_BLOSSOM.value());

        tag(ItemTags.PICKAXES)
            .add(AMItemRegistry.GHOSTLY_PICKAXE.value());

        tag(ItemTags.SWORDS)
            .add(AMItemRegistry.SKELEWAG_SWORD.value());

        tag(ItemTags.FOOT_ARMOR)
            .add(AMItemRegistry.ROADDRUNNER_BOOTS.value())
            .add(AMItemRegistry.FLYING_FISH_BOOTS.value());

        tag(ItemTags.CHEST_ARMOR)
            .add(AMItemRegistry.CROCODILE_CHESTPLATE.value())
            .add(AMItemRegistry.ROCKY_CHESTPLATE.value())
            .add(AMItemRegistry.UNSETTLING_KIMONO.value());

        tag(ItemTags.HEAD_ARMOR)
            .add(AMItemRegistry.FROSTSTALKER_HELMET.value())
            .add(AMItemRegistry.FRONTIER_CAP.value())
            .add(AMItemRegistry.MOOSE_HEADGEAR.value())
            .add(AMItemRegistry.SPIKED_TURTLE_SHELL.value())
            .add(AMItemRegistry.FEDORA.value())
            .add(AMItemRegistry.SOMBRERO.value());

        tag(ItemTags.LEG_ARMOR)
            .add(AMItemRegistry.CENTIPEDE_LEGGINGS.value())
            .add(AMItemRegistry.EMU_LEGGINGS.value());

        tag(Tags.Items.FOODS_FRUIT)
            .add(AMItemRegistry.BANANA.value());

        tag(Tags.Items.EGGS)
            .add(AMBlockRegistry.CROCODILE_EGG.value().asItem())
            .add(AMBlockRegistry.PLATYPUS_EGG.value().asItem())
            .add(AMItemRegistry.EMU_EGG.value())
            .add(AMBlockRegistry.TERRAPIN_EGG.value().asItem())
            .add(AMBlockRegistry.CAIMAN_EGG.value().asItem());

        tag(Tags.Items.MUSIC_DISCS)
            .add(AMItemRegistry.MUSIC_DISC_THIME.value())
            .add(AMItemRegistry.MUSIC_DISC_DAZE.value());
    }
}
