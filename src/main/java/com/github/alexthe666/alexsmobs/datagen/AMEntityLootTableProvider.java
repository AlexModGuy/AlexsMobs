package com.github.alexthe666.alexsmobs.datagen;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class AMEntityLootTableProvider extends EntityLootSubProvider {
    protected AMEntityLootTableProvider(FeatureFlagSet required, HolderLookup.Provider registries) {
        super(required, registries);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return AMEntityRegistry.DEF_REG.getEntries().stream().map(DeferredHolder::get);
    }

    private final Set<EntityType<?>> registered = new HashSet<>();

    @Override
    public void generate() {
        add(AMEntityRegistry.ANACONDA.get(), LootTable.lootTable());
        add(AMEntityRegistry.ANTEATER.get(), LootTable.lootTable());
        add(AMEntityRegistry.BALD_EAGLE.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("bald_eagle", Items.FEATHER, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.BANANA_SLUG.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("banana_slug", AMItemRegistry.BANANA_SLUG_SLIME, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.BISON.get(), LootTable.lootTable()
            .withPool(itemWithLootingSmeltablePool("bison_meat", Items.BEEF, 1, UniformGenerator.between(6f, 8f), UniformGenerator.between(0f, 2f)))
            .withPool(itemWithLootingSmeltablePool("bison", AMItemRegistry.BISON_FUR, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.BLOBFISH.get(), LootTable.lootTable()
            .withPool(itemPool("blobfish", AMItemRegistry.BLOBFISH, ConstantValue.exactly(1f)))
            .withPool(
                itemPool("blobfish_bm", Items.BONE_MEAL, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceCondition.randomChance(0.05f))
            )
        );

        add(AMEntityRegistry.BLUE_JAY.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("blue_jay", Items.FEATHER, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.BONE_SERPENT.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("bone_serpent", AMItemRegistry.BONE_SERPENT_TOOTH, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("bone_serpent", Items.BONE, 1, UniformGenerator.between(10f, 15f), UniformGenerator.between(0f, 3f)))
            .withPool(itemWithLootingPool("bone_serpent", Items.BONE_BLOCK, 1, UniformGenerator.between(1f, 4f), UniformGenerator.between(0f, 2f)))
        );

        add(AMEntityRegistry.BUNFUNGUS.get(), LootTable.lootTable()
            .withPool(itemPool("bunfungus", Items.RED_MUSHROOM, UniformGenerator.between(0f, 2f)))
        );

        add(AMEntityRegistry.CACHALOT_WHALE.get(), LootTable.lootTable());
        add(AMEntityRegistry.CAIMAN.get(), LootTable.lootTable());
        add(AMEntityRegistry.CAPUCHIN_MONKEY.get(), LootTable.lootTable());

        add(AMEntityRegistry.CATFISH.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("catfish", AMItemRegistry.RAW_CATFISH, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.CATFISH.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/catfish_medium")), LootTable.lootTable()
            .withPool(itemWithLootingPool("catfish_medium", AMItemRegistry.RAW_CATFISH, 1, UniformGenerator.between(2f, 3f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.CATFISH.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/catfish_large")), LootTable.lootTable()
            .withPool(itemWithLootingPool("catfish_large", AMItemRegistry.RAW_CATFISH, 1, UniformGenerator.between(4f, 6f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.CENTIPEDE_HEAD.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("centipede", AMItemRegistry.CENTIPEDE_LEG, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.COCKROACH.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("roach_wing", AMItemRegistry.COCKROACH_WING_FRAGMENT, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.COCKROACH.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/cockroach_maracas")), LootTable.lootTable()
            .withPool(itemWithLootingPool("roach_wing", AMItemRegistry.COCKROACH_WING_FRAGMENT, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
            .withPool(
                itemPool("sombrero", AMItemRegistry.SOMBRERO, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.2f, 0.01f))
            )
            .withPool(itemPool("cockroach_maraca", AMItemRegistry.MARACA, ConstantValue.exactly(1f)))
        );

        add(AMEntityRegistry.COCKROACH.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/cockroach_maracas_headless")), LootTable.lootTable()
            .withPool(itemWithLootingPool("roach_wing", AMItemRegistry.COCKROACH_WING_FRAGMENT, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
            .withPool(
                LootPool.lootPool()
                    .name("cockroach_maraca")
                    .setRolls(ConstantValue.exactly(1f))
                    .add(
                        LootItem.lootTableItem(AMItemRegistry.MARACA.value())
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                    )
            )
        );

        add(AMEntityRegistry.COMB_JELLY.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("combjelly", AMItemRegistry.RAINBOW_JELLY, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.COSMAW.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("cosmaw", Items.CHORUS_FRUIT, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.COSMIC_COD.get(), LootTable.lootTable()
            .withPool(itemPool("cosmic_cod", AMItemRegistry.COSMIC_COD, ConstantValue.exactly(1f)))
            .withPool(
                itemPool("cosmic_cod_bm", Items.BONE_MEAL, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceCondition.randomChance(0.05f))
            )
        );

        add(AMEntityRegistry.CRIMSON_MOSQUITO.get(), LootTable.lootTable()
            .withPool(
                itemPool("crimson_mosquito", AMItemRegistry.MOSQUITO_PROBOSCIS, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.1f, 0.01f))
            )
        );

        add(AMEntityRegistry.CRIMSON_MOSQUITO.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/crimson_mosquito_fly")), LootTable.lootTable()
            .withPool(
                itemPool("crimson_mosquito", AMItemRegistry.MOSQUITO_PROBOSCIS, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.3f, 0.03f))
            )
            .withPool(
                itemPool("crimson_mosquito", AMItemRegistry.BLOOD_SAC, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.1f, 0.01f))
            )
        );

        add(AMEntityRegistry.CRIMSON_MOSQUITO.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/crimson_mosquito_fly_full")), LootTable.lootTable()
            .withPool(
                itemPool("crimson_mosquito", AMItemRegistry.MOSQUITO_PROBOSCIS, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.5f, 0.03f))
            )
            .withPool(itemPool("crimson_mosquito", AMItemRegistry.BLOOD_SAC, ConstantValue.exactly(1f)))
        );

        add(AMEntityRegistry.CRIMSON_MOSQUITO.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/crimson_mosquito_full")), LootTable.lootTable()
            .withPool(
                itemPool("crimson_mosquito", AMItemRegistry.MOSQUITO_PROBOSCIS, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.1f, 0.01f))
            )
            .withPool(
                itemPool("crimson_mosquito", AMItemRegistry.BLOOD_SAC, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.8f, 0.01f))
            )
        );

        add(AMEntityRegistry.CROCODILE.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("crocodile", AMItemRegistry.CROCODILE_SCUTE, 1, UniformGenerator.between(-1f, 2f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("crocodile", AMBlockRegistry.CROCODILE_EGG.value(), 1, UniformGenerator.between(-2f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.CROW.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("crow", Items.FEATHER, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(), LootTable.lootTable());

        add(AMEntityRegistry.DROPBEAR.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("dropbear", AMItemRegistry.DROPBEAR_CLAW, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.ELEPHANT.get(), LootTable.lootTable());

        add(AMEntityRegistry.EMU.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("emu", AMItemRegistry.EMU_FEATHER, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("emu", Items.FEATHER, 1, UniformGenerator.between(-1f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.ENDERGRADE.get(), LootTable.lootTable());

        add(AMEntityRegistry.ENDERIOPHAGE.get(), LootTable.lootTable()
            .withPool(itemPool("capsid", AMBlockRegistry.CAPSID.value(), ConstantValue.exactly(1f)))
        );

        add(AMEntityRegistry.FARSEER.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("farseer", AMItemRegistry.FARSEER_ARM, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.FLUTTER.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("flutter", Blocks.SPORE_BLOSSOM, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.FLY.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("fly", AMItemRegistry.MAGGOT, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.FLYING_FISH.get(), LootTable.lootTable()
            .withPool(itemPool("flying_fish", AMItemRegistry.FLYING_FISH, ConstantValue.exactly(1f)))
            .withPool(
                itemPool("flying_fish_bm", Items.BONE_MEAL, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceCondition.randomChance(0.05f))
            )
        );

        add(AMEntityRegistry.FRILLED_SHARK.get(), LootTable.lootTable());

        add(AMEntityRegistry.FROSTSTALKER.get(), LootTable.lootTable()
            .withPool(
                itemPool("froststalker", AMItemRegistry.FROSTSTALKER_HORN, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.23f, 0.1f))
                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
            )
        );

        add(AMEntityRegistry.FROSTSTALKER.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/froststalker_spikes")), LootTable.lootTable()
            .withPool(
                itemPool("froststalker_spikes", AMItemRegistry.FROSTSTALKER_HORN, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.33f, 0.1f))
            )
            .withPool(itemWithLootingPool("froststalker_ice_packed", Blocks.PACKED_ICE, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("froststalker_ice_blue", Blocks.BLUE_ICE, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.GAZELLE.get(), LootTable.lootTable()
            .withPool(itemWithLootingSmeltablePool("gazelle", Items.MUTTON, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("gazelle_horn", AMItemRegistry.GAZELLE_HORN, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.GELADA_MONKEY.get(), LootTable.lootTable());
        add(AMEntityRegistry.UNDERMINER.get(), LootTable.lootTable());
        add(AMEntityRegistry.GIANT_SQUID.get(), LootTable.lootTable()
            .withPool(itemPool("giant_squid", Items.INK_SAC, UniformGenerator.between(4f, 8f)))
        );
        add(AMEntityRegistry.GORILLA.get(), LootTable.lootTable());

        add(AMEntityRegistry.GRIZZLY_BEAR.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("grizzly_bear", AMItemRegistry.BEAR_FUR, 1, UniformGenerator.between(-1f, 2f), UniformGenerator.between(0f, 1f)))
            .withPool(
                itemPool("bear_dust", AMItemRegistry.BEAR_DUST, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.01f, 0.01f))
            )
        );

        add(AMEntityRegistry.GUSTER.get(), LootTable.lootTable()
            .withPool(
                itemPool("guster_eye", AMItemRegistry.GUSTER_EYE, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.2f, 0.1f))
            )
            .withPool(
                itemWithLootingSmeltablePool("guster_sand", Blocks.SAND, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f))
            )
        );

        add(AMEntityRegistry.GUSTER.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/guster_red")), LootTable.lootTable()
            .withPool(
                itemPool("guster_eye", AMItemRegistry.GUSTER_EYE, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.2f, 0.1f))
            )
            .withPool(
                itemWithLootingSmeltablePool("guster_sand", Blocks.RED_SAND, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f))
            )
        );

        add(AMEntityRegistry.GUSTER.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/guster_soul")), LootTable.lootTable()
            .withPool(
                itemPool("guster_eye", AMItemRegistry.GUSTER_EYE, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.2f, 0.1f))
            )
            .withPool(
                itemWithLootingSmeltablePool("guster_sand", Blocks.SOUL_SAND, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f))
            )
        );

        add(AMEntityRegistry.HAMMERHEAD_SHARK.get(), LootTable.lootTable());
        add(AMEntityRegistry.HUMMINGBIRD.get(), LootTable.lootTable());
        add(AMEntityRegistry.JERBOA.get(), LootTable.lootTable());

        add(AMEntityRegistry.KANGAROO.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("kangaroo", AMItemRegistry.KANGAROO_HIDE, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingSmeltablePool("kangaroo", AMItemRegistry.KANGAROO_MEAT, 1, UniformGenerator.between(1f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.KOMODO_DRAGON.get(), LootTable.lootTable());

        add(AMEntityRegistry.LAVIATHAN.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("laviathan_magma", Blocks.MAGMA_BLOCK, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("laviathan_blackstone", Blocks.BLACKSTONE, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.LAVIATHAN.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/laviathan_obsidian")), LootTable.lootTable()
            .withPool(itemWithLootingPool("laviathan_magma", Blocks.OBSIDIAN, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("laviathan_blackstone", Blocks.BLACKSTONE, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.LEAFCUTTER_ANT.get(), LootTable.lootTable());
        add(AMEntityRegistry.LEAFCUTTER_ANT.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("entities/leafcutter_ant_queen")), LootTable.lootTable()
            .withPool(itemWithLootingPool("leafcutter_ant_queen", AMItemRegistry.LEAFCUTTER_ANT_PUPA, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.LOBSTER.get(), LootTable.lootTable()
            .withPool(itemWithLootingSmeltablePool("lobster", AMItemRegistry.LOBSTER_TAIL, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.MANED_WOLF.get(), LootTable.lootTable());

        add(AMEntityRegistry.MIMIC_OCTOPUS.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("mimic_octopus", Items.INK_SAC, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f))));

        add(AMEntityRegistry.MIMICUBE.get(), LootTable.lootTable()
            .withPool(itemPool("mimicube", AMItemRegistry.MIMICREAM, UniformGenerator.between(-1f, 1f))));

        add(AMEntityRegistry.MOOSE.get(), LootTable.lootTable()
            .withPool(itemWithLootingSmeltablePool("moose", AMItemRegistry.MOOSE_RIBS, 1, UniformGenerator.between(1f, 3f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.MUDSKIPPER.get(), LootTable.lootTable()
            .withPool(itemPool("mudskipper", Items.TROPICAL_FISH, ConstantValue.exactly(1f)))
        );

        add(AMEntityRegistry.MUNGUS.get(), LootTable.lootTable());
        add(AMEntityRegistry.MURMUR.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .name("murmur_clothes")
                    .setRolls(ConstantValue.exactly(1f))
                    .add(item(AMItemRegistry.UNSETTLING_KIMONO, ConstantValue.exactly(1f)).setWeight(1))
                    .add(itemWithLooting(Items.RED_WOOL, 9, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
            )
            .withPool(itemWithLootingPool("murmur_tendon", AMItemRegistry.ELASTIC_TENDON, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.ORCA.get(), LootTable.lootTable());
        add(AMEntityRegistry.PLATYPUS.get(), LootTable.lootTable());
        add(AMEntityRegistry.POTOO.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("potoo", Items.FEATHER, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f))));

        add(AMEntityRegistry.RACCOON.get(), LootTable.lootTable()
            .withPool(itemPool("raccoon", AMItemRegistry.RACCOON_TAIL, UniformGenerator.between(-1f, 1f))));

        add(AMEntityRegistry.RAIN_FROG.get(), LootTable.lootTable());
        add(AMEntityRegistry.RATTLESNAKE.get(), LootTable.lootTable()
            .withPool(itemPool("rattlesnake", AMItemRegistry.RATTLESNAKE_RATTLE, UniformGenerator.between(0f, 1f))));

        add(AMEntityRegistry.RHINOCEROS.get(), LootTable.lootTable());
        add(AMEntityRegistry.ROADRUNNER.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("roadrunner", AMItemRegistry.ROADRUNNER_FEATHER, 1, UniformGenerator.between(-2f, 1f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("roadrunner", Items.FEATHER, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.ROCKY_ROLLER.get(), LootTable.lootTable()
            .withPool(itemPool("rocky_roller_shell", AMItemRegistry.ROCKY_SHELL, ConstantValue.exactly(1f))
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.75f, 0.1f)))
            .withPool(itemWithLootingPool("rocky_roller", Blocks.TUFF, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("rocky_roller", Blocks.POINTED_DRIPSTONE, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 2f)))
        );

        add(AMEntityRegistry.SEA_BEAR.get(), LootTable.lootTable());
        add(AMEntityRegistry.SEAGULL.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("seagull", Items.FEATHER, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f))));

        add(AMEntityRegistry.SEAL.get(), LootTable.lootTable());
        add(AMEntityRegistry.SHOEBILL.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("shoebill", Items.FEATHER, 1, UniformGenerator.between(0f, 4f), UniformGenerator.between(0f, 1f))));

        add(AMEntityRegistry.SKELEWAG.get(), LootTable.lootTable()
            .withPool(itemPool("skelewag_sword", AMItemRegistry.SKELEWAG_SWORD, ConstantValue.exactly(1f))
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.1f, 0.05f)))
            .withPool(itemPool("skelewag_hat", AMItemRegistry.NOVELTY_HAT, ConstantValue.exactly(1f))
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.01f, 0.015f)))
            .withPool(itemPool("skelewag_bones", AMItemRegistry.FISH_BONES, ConstantValue.exactly(1f))
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.3f, 0.2f)))
            .withPool(itemWithLootingPool("bone", Items.BONE, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.SKREECHER.get(), LootTable.lootTable()
            .withPool(itemPool("skreecher", AMItemRegistry.SKREECHER_SOUL, ConstantValue.exactly(1f))
                .when(LootItemKilledByPlayerCondition.killedByPlayer()))
        );

        add(AMEntityRegistry.SKUNK.get(), LootTable.lootTable());
        add(AMEntityRegistry.SNOW_LEOPARD.get(), LootTable.lootTable());
        add(AMEntityRegistry.SOUL_VULTURE.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("soul_vulture", Items.BONE, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("soul_vulture_coal", Items.COAL, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.SOUL_VULTURE.get(), ResourceKey.create(Registries.LOOT_TABLE, AlexsMobs.prefix("soul_vulture_heart")), LootTable.lootTable()
            .withPool(itemWithLootingPool("soul_vulture", Items.BONE, 1, UniformGenerator.between(0f, 2f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("soul_vulture_coal", Items.COAL, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
            .withPool(itemPool("soul_vulture_heart", AMItemRegistry.SOUL_HEART, UniformGenerator.between(0f, 1f)))
        );

        add(AMEntityRegistry.SPECTRE.get(), LootTable.lootTable());
        add(AMEntityRegistry.STRADDLER.get(), LootTable.lootTable()
            .withPool(itemPool("straddler", AMItemRegistry.STRADDLITE, ConstantValue.exactly(1f))
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.2f, 0.05f)))
            .withPool(itemWithLootingPool("straddler_2", Blocks.BASALT, 1, UniformGenerator.between(0f, 3f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.STRADPOLE.get(), LootTable.lootTable());
        add(AMEntityRegistry.SUGAR_GLIDER.get(), LootTable.lootTable());
        add(AMEntityRegistry.SUNBIRD.get(), LootTable.lootTable());
        add(AMEntityRegistry.TARANTULA_HAWK.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("tarantula_hawk", AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.TASMANIAN_DEVIL.get(), LootTable.lootTable());
        add(AMEntityRegistry.TERRAPIN.get(), LootTable.lootTable());
        add(AMEntityRegistry.TIGER.get(), LootTable.lootTable());
        add(AMEntityRegistry.TOUCAN.get(), LootTable.lootTable()
            .withPool(itemWithLootingPool("toucan", Items.FEATHER, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.TRIOPS.get(), LootTable.lootTable());
        add(AMEntityRegistry.TUSKLIN.get(), LootTable.lootTable()
            .withPool(itemWithLootingSmeltablePool("tusklin", Items.PORKCHOP, 1, UniformGenerator.between(3f, 6f), UniformGenerator.between(0f, 1f)))
            .withPool(itemWithLootingPool("snowball", Items.SNOWBALL, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
        );
        add(AMEntityRegistry.VOID_WORM.get(), LootTable.lootTable()
            .withPool(itemPool("eye", AMItemRegistry.VOID_WORM_EYE, ConstantValue.exactly(1f)))
            .withPool(itemPool("mandible", AMItemRegistry.VOID_WORM_MANDIBLE, ConstantValue.exactly(2f)))
        );

        add(AMEntityRegistry.WARPED_MOSCO.get(), LootTable.lootTable()
            .withPool(
                itemPool("warped_mosco_1", AMItemRegistry.WARPED_MUSCLE, ConstantValue.exactly(1f))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.7f, 0.1f))
            )
            .withPool(
                itemWithLootingPool("warped_mosco_2", AMItemRegistry.HEMOLYMPH_SAC, 1, UniformGenerator.between(1f, 5f), UniformGenerator.between(0f, 1f))
            )
        );

        add(AMEntityRegistry.WARPED_TOAD.get(), LootTable.lootTable()
            .withPool(
                itemWithLootingPool("warped_toad", Items.SHROOMLIGHT, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f))
                    .add(itemWithLooting(Items.NETHER_WART, 1, UniformGenerator.between(0f, 1f), UniformGenerator.between(0f, 1f)))
            )
        );

        // Anything that isn't listed here, give them an empty loot table to avoid failed registration.
        this.getKnownEntityTypes().forEach(type -> {
            if (!this.registered.contains(type) && this.canHaveLootTable(type)) {
                add(type, LootTable.lootTable());
            }
        });
    }

    @Override
    protected void add(EntityType<?> entityType, LootTable.Builder builder) {
        super.add(entityType, builder);
        this.registered.add(entityType);
    }

    @Override
    protected void add(EntityType<?> entityType, ResourceKey<LootTable> defaultLootTable, LootTable.Builder builder) {
        super.add(entityType, defaultLootTable, builder);
        this.registered.add(entityType);
    }

    private LootPoolSingletonContainer.Builder<?> item(ItemLike item, NumberProvider count) {
        return LootItem.lootTableItem(item)
            .apply(SetItemCountFunction.setCount(count));
    }

    private LootPool.Builder itemPool(String name, ItemLike item, NumberProvider count) {
        return LootPool.lootPool()
            .name(name)
            .setRolls(ConstantValue.exactly(1f))
            .add(item(item, count));
    }

    private LootPoolSingletonContainer.Builder<?> itemWithLooting(ItemLike item, int weight, NumberProvider dropCount, NumberProvider lootingMultiplier) {
        return LootItem.lootTableItem(item)
            .setWeight(weight)
            .apply(SetItemCountFunction.setCount(dropCount))
            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, lootingMultiplier));
    }

    private LootPool.Builder itemWithLootingPool(String name, ItemLike item, int weight, NumberProvider dropCount, NumberProvider lootingMultiplier) {
        return LootPool.lootPool()
            .name(name)
            .setRolls(ConstantValue.exactly(1f))
            .add(itemWithLooting(item, weight, dropCount, lootingMultiplier));
    }
    
    private LootPool.Builder itemWithLootingSmeltablePool(String name, ItemLike item, int weight, NumberProvider dropCount, NumberProvider lootingMultiplier) {
        return LootPool.lootPool()
            .name(name)
            .setRolls(ConstantValue.exactly(1f))
            .add(itemWithLooting(item, weight, dropCount, lootingMultiplier)
                .apply(smeltWhenOnFire()));
    }

    private LootItemFunction.Builder smeltWhenOnFire() {
        return SmeltItemFunction.smelted().when(
            LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                EntityPredicate.Builder.entity()
                    .flags(
                        EntityFlagsPredicate.Builder.flags()
                            .setOnFire(true)
                    )
            )
        );
    }
}
