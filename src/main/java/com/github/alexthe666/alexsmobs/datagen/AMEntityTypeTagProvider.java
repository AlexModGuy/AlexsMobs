package com.github.alexthe666.alexsmobs.datagen;

import java.util.concurrent.CompletableFuture;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;

public class AMEntityTypeTagProvider extends EntityTypeTagsProvider {
    public AMEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, AlexsMobs.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
            .add(AMEntityRegistry.MOOSE.value())
            .add(AMEntityRegistry.SNOW_LEOPARD.value())
            .add(AMEntityRegistry.FROSTSTALKER.value())
            .add(AMEntityRegistry.TUSKLIN.value())
            .add(AMEntityRegistry.RACCOON.value())
            .add(AMEntityRegistry.BISON.value());

        tag(EntityTypeTags.FROG_FOOD)
            .add(AMEntityRegistry.FLY.value());

        tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)
            .add(AMEntityRegistry.SUNBIRD.value())
            .add(AMEntityRegistry.CAPUCHIN_MONKEY.value())
            .add(AMEntityRegistry.MIMICUBE.value())
            .add(AMEntityRegistry.RACCOON.value())
            .add(AMEntityRegistry.SEAL.value())
            .add(AMEntityRegistry.SNOW_LEOPARD.value())
            .add(AMEntityRegistry.CROW.value())
            .add(AMEntityRegistry.TASMANIAN_DEVIL.value())
            .add(AMEntityRegistry.BALD_EAGLE.value())
            .add(AMEntityRegistry.TIGER.value())
            .add(AMEntityRegistry.MANED_WOLF.value())
            .add(AMEntityRegistry.JERBOA.value())
            .add(AMEntityRegistry.BISON.value());
    }
}
