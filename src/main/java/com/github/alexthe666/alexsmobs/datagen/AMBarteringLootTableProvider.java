package com.github.alexthe666.alexsmobs.datagen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class AMBarteringLootTableProvider implements LootTableSubProvider {
    private final Map<ResourceKey<LootTable>, LootTable.Builder> map = new HashMap<>();
    private final HolderLookup.Provider registries;

    public AMBarteringLootTableProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        this.generate();
        this.map.forEach(output);
    }

    protected void generate() {
        add("anteater_reward", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(UniformGenerator.between(-4f, 1f))
                    .add(LootItem.lootTableItem(Items.DIRT).setWeight(20))
                    .add(LootItem.lootTableItem(Items.COARSE_DIRT).setWeight(10))
                    .add(LootItem.lootTableItem(Items.ROOTED_DIRT).setWeight(5))
                    .add(LootItem.lootTableItem(AMItemRegistry.LEAFCUTTER_ANT_PUPA.value()).setWeight(5))
                    .add(LootItem.lootTableItem(Items.HANGING_ROOTS).setWeight(5))
                    .add(LootItem.lootTableItem(AMItemRegistry.MAGGOT.value()).setWeight(2))
                    .add(LootItem.lootTableItem(Items.BEETROOT).setWeight(1))
                    .add(LootItem.lootTableItem(Items.POTATO).setWeight(1))
            )
        );

        add("platypus_reward", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(20))
                    .add(LootItem.lootTableItem(AMItemRegistry.MAGGOT.value()).setWeight(7))
            )
        );

        add("platypus_supercharged_reward", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(20)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1f, 2f))))
                    .add(LootItem.lootTableItem(AMItemRegistry.MAGGOT.value()).setWeight(10))
                    .add(LootItem.lootTableItem(AMItemRegistry.FEDORA.value()).setWeight(1))
            )
        );

        add("pupfish_reward", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(LootItem.lootTableItem(Items.SLIME_BALL).setWeight(5))
            )
        );

        add("seal_reward", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(DynamicLoot.dynamicEntry(BuiltInLootTables.FISHING_JUNK.location())
                        .setWeight(4)
                        .setQuality(-2))
                    .add(
                        LootItem.lootTableItem(Items.POTION)
                            .setWeight(10)
                            .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER)))
                    )
                    .add(LootItem.lootTableItem(Items.TURTLE_SCUTE)
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.INK_SAC)
                        .setWeight(19))
                    .add(LootItem.lootTableItem(AMItemRegistry.SHARK_TOOTH.value())
                        .setWeight(21))
                    .add(LootItem.lootTableItem(AMItemRegistry.SERRATED_SHARK_TOOTH.value())
                        .setWeight(8))
                    .add(LootItem.lootTableItem(Items.SAND)
                        .setWeight(60)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1f, 10f))))
                    .add(LootItem.lootTableItem(Items.KELP)
                        .setWeight(60)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2f, 4f))))
                    .add(LootItem.lootTableItem(Items.PRISMARINE_SHARD)
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2f, 5f))))
                    .add(LootItem.lootTableItem(Items.CLAY_BALL)
                        .setWeight(20)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5f, 32f))))
                    .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS)
                        .setWeight(10))
                    .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL)
                        .setWeight(1))
                    .add(LootItem.lootTableItem(AMItemRegistry.FISH_BONES.value())
                        .setWeight(6))
                    .add(LootItem.lootTableItem(Items.TURTLE_EGG)
                        .setWeight(1))
                    .add(LootItem.lootTableItem(Items.TUBE_CORAL)
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.BRAIN_CORAL)
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.BUBBLE_CORAL)
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.FIRE_CORAL)
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.HORN_CORAL)
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.TUBE_CORAL_BLOCK)
                        .setWeight(1))
                    .add(LootItem.lootTableItem(Items.BRAIN_CORAL_BLOCK)
                        .setWeight(1))
                    .add(LootItem.lootTableItem(Items.BUBBLE_CORAL_BLOCK)
                        .setWeight(1))
                    .add(LootItem.lootTableItem(Items.FIRE_CORAL_BLOCK)
                        .setWeight(1))
                    .add(LootItem.lootTableItem(Items.HORN_CORAL_BLOCK)
                        .setWeight(1))
                    .add(LootItem.lootTableItem(AMItemRegistry.LOBSTER_BUCKET.value())
                        .setWeight(2))
                    .add(LootItem.lootTableItem(AMItemRegistry.MUSIC_DISC_THIME.value())
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.WET_SPONGE)
                        .setWeight(20)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1f, 2f)))
                    )
                    .add(LootItem.lootTableItem(Items.GRANITE)
                        .setWeight(40)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(8f, 16f)))
                    )
            )
        );

        add("sugar_glider_reward", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(LootItem.lootTableItem(AMItemRegistry.MAGGOT.value())
                        .setWeight(5))
                    .add(LootItem.lootTableItem(Items.STICK)
                        .setWeight(45))
                    .add(LootItem.lootTableItem(Items.HANGING_ROOTS)
                        .setWeight(10))
                    .add(LootItem.lootTableItem(Items.VINE)
                        .setWeight(3))
                    .add(LootItem.lootTableItem(Items.WHEAT_SEEDS)
                        .setWeight(7))
                    .add(LootItem.lootTableItem(Items.FERN)
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.MOSS_CARPET)
                        .setWeight(4))
                    .add(LootItem.lootTableItem(Items.FEATHER)
                        .setWeight(10))
                    .add(LootItem.lootTableItem(Items.ARROW)
                        .setWeight(7))
                    .add(LootItem.lootTableItem(Items.ARROW)
                        .setWeight(7))
                    .add(LootItem.lootTableItem(Items.SWEET_BERRIES)
                        .setWeight(10))
                    .add(LootItem.lootTableItem(Items.EGG)
                        .setWeight(10))
                    .add(LootItem.lootTableItem(AMItemRegistry.BEAR_FUR.value())
                        .setWeight(2))
                    .add(LootItem.lootTableItem(Items.GLOW_BERRIES)
                        .setWeight(3))
                    .add(LootItem.lootTableItem(AMItemRegistry.COCKROACH_WING.value())
                        .setWeight(3))
                    .add(LootItem.lootTableItem(AMItemRegistry.MOOSE_ANTLER.value())
                        .setWeight(1))
                    .add(LootItem.lootTableItem(AMItemRegistry.COCKROACH_OOTHECA.value())
                        .setWeight(3))
                    .add(LootItem.lootTableItem(Items.COBWEB)
                        .setWeight(9))
                    .add(LootItem.lootTableItem(Items.BAMBOO)
                        .setWeight(5))
                    .add(LootItem.lootTableItem(Items.HONEYCOMB)
                        .setWeight(3))
            )
        );
    }

    private void add(String path, LootTable.Builder builder) {
        this.map.put(ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("gameplay/" + path)), builder);
    }
}
