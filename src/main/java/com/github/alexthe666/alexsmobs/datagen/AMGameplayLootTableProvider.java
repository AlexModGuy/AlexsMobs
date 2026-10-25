package com.github.alexthe666.alexsmobs.datagen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class AMGameplayLootTableProvider implements LootTableSubProvider {
    private final Map<ResourceKey<LootTable>, LootTable.Builder> map = new HashMap<>();
    private final HolderLookup.Provider registries;

    public AMGameplayLootTableProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        this.generate();
        this.map.forEach(output);
    }

    protected void generate() {
        add("trader_elephant_chest", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .name("trader_elephant_chest_emerald")
                    .setRolls(UniformGenerator.between(0f, 1f))
                    .add(
                        LootItem.lootTableItem(Items.EMERALD)
                            .setWeight(1)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0f, 1f)))
                    )
            )
            .withPool(
                LootPool.lootPool()
                    .name("trader_elephant_book")
                    .setRolls(UniformGenerator.between(0f, 1f))
                    .add(
                        LootItem.lootTableItem(AMItemRegistry.ANIMAL_DICTIONARY.value())
                            .setWeight(1)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0f, 1f)))
                    )
            )
            .withPool(
                LootPool.lootPool()
                    .name("trader_elephant_chest")
                    .setRolls(UniformGenerator.between(2f, 13f))
                    .add(LootItem.lootTableItem(AMItemRegistry.CROCODILE_SCUTE.value())
                        .setWeight(1))
                    .add(LootItem.lootTableItem(AMItemRegistry.BANANA.value())
                        .setWeight(5))
                    .add(LootItem.lootTableItem(AMItemRegistry.ACACIA_BLOSSOM.value())
                        .setWeight(5))
                    .add(LootItem.lootTableItem(Items.STICK)
                        .setWeight(3))
                    .add(LootItem.lootTableItem(AMItemRegistry.COCKROACH_WING_FRAGMENT.value())
                        .setWeight(5)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1f, 2f)))
                    )
                    .add(LootItem.lootTableItem(AMItemRegistry.SHARK_TOOTH.value())
                        .setWeight(5))
                    .add(LootItem.lootTableItem(AMItemRegistry.MOSQUITO_LARVA.value())
                        .setWeight(11))
                    .add(LootItem.lootTableItem(AMItemRegistry.CENTIPEDE_LEG.value())
                        .setWeight(3))
                    .add(LootItem.lootTableItem(AMItemRegistry.EMU_EGG.value())
                        .setWeight(4))
                    .add(LootItem.lootTableItem(AMItemRegistry.EMU_FEATHER.value())
                        .setWeight(2))
                    .add(LootItem.lootTableItem(AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT.value())
                        .setWeight(5))
                    .add(LootItem.lootTableItem(AMItemRegistry.MAGGOT.value())
                        .setWeight(14)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1f, 2f))))
                    .add(LootItem.lootTableItem(Items.SAND)
                        .setWeight(20)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1f, 2f))))
                    .add(LootItem.lootTableItem(Items.COBWEB)
                        .setWeight(3))
            )
        );

        add("transmutation_table_common", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.DIRT).setWeight(25))
                    .add(LootItem.lootTableItem(Items.COBBLESTONE).setWeight(20))
                    .add(LootItem.lootTableItem(Items.SAND).setWeight(15))
                    .add(LootItem.lootTableItem(Items.RED_SAND).setWeight(2))
                    .add(LootItem.lootTableItem(Items.STICK).setWeight(22))
                    .add(LootItem.lootTableItem(Items.TORCH).setWeight(3))
                    .add(LootItem.lootTableItem(Items.KELP).setWeight(3))
                    .add(LootItem.lootTableItem(Items.SNOWBALL).setWeight(15))
                    .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(6))
                    .add(LootItem.lootTableItem(Items.GRANITE).setWeight(4))
                    .add(LootItem.lootTableItem(Items.DIORITE).setWeight(4))
                    .add(LootItem.lootTableItem(Items.ANDESITE).setWeight(4))
                    .add(LootItem.lootTableItem(Items.COBBLED_DEEPSLATE).setWeight(1))
                    .add(LootItem.lootTableItem(Items.NETHERRACK).setWeight(1))
            )
        );

        add("transmutation_table_rare", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(1))
                    .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2))
                    .add(LootItem.lootTableItem(AMItemRegistry.MIMICREAM.value()).setWeight(1))
                    .add(LootItem.lootTableItem(Items.RAW_COPPER).setWeight(12))
                    .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(12))
                    .add(LootItem.lootTableItem(Items.AMETHYST_SHARD).setWeight(10))
                    .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(10))
                    .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(9))
                    .add(LootItem.lootTableItem(Items.BLAZE_ROD).setWeight(12))
                    .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).setWeight(12))
                    .add(LootItem.lootTableItem(Items.PRISMARINE_SHARD).setWeight(12))
                    .add(LootItem.lootTableItem(Items.SHULKER_SHELL).setWeight(1))
                    .add(LootItem.lootTableItem(Items.END_ROD).setWeight(4))
                    .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL).setWeight(1))
                    .add(LootItem.lootTableItem(Items.GILDED_BLACKSTONE).setWeight(20))
                    .add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(5))
            )
        );

        add("transmutation_table_uncommon", LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(20))
                    .add(LootItem.lootTableItem(Items.RAW_COPPER).setWeight(25))
                    .add(LootItem.lootTableItem(Items.RAW_GOLD).setWeight(2))
                    .add(LootItem.lootTableItem(Items.COAL).setWeight(20))
                    .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(4))
                    .add(LootItem.lootTableItem(Items.SLIME_BALL).setWeight(3))
                    .add(LootItem.lootTableItem(Items.FEATHER).setWeight(3))
                    .add(LootItem.lootTableItem(Items.BOOK).setWeight(4))
                    .add(LootItem.lootTableItem(Items.FLINT).setWeight(22))
                    .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(5))
                    .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(5))
                    .add(LootItem.lootTableItem(Items.PUMPKIN).setWeight(2))
                    .add(LootItem.lootTableItem(Items.MELON_SLICE).setWeight(3))
                    .add(LootItem.lootTableItem(Items.WHEAT).setWeight(9))
                    .add(LootItem.lootTableItem(AMBlockRegistry.CAPSID.value()).setWeight(1))
                    .add(LootItem.lootTableItem(AMItemRegistry.ACACIA_BLOSSOM.value()).setWeight(1))
            )
        );
    }

    private void add(String path, LootTable.Builder builder) {
        this.map.put(ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("gameplay/" + path)), builder);
    }
}
