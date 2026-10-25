package com.github.alexthe666.alexsmobs.datagen;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.component.AMDataComponentRegistry;
import com.github.alexthe666.alexsmobs.component.DisplayEntity;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTrigger;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.item.component.CustomRenderDisplay;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class AMAdvancementProvider extends AdvancementProvider {
    public AMAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(
            new AMEntityAdvancementGenerator()
        ));
    }

    public static class AMEntityAdvancementGenerator implements AdvancementGenerator {
        private AdvancementHolder meetEntity(Consumer<AdvancementHolder> consumer, Holder<EntityType<?>> entityTypeHolder, AdvancementHolder parent) {
            EntityType<?> entityType = entityTypeHolder.value();
            ResourceLocation entityId = entityType.builtInRegistryHolder().key().location();
            ResourceLocation id = entityId.withPrefix("alexsmobs/");
            ItemStack icon = new ItemStack(AMItemRegistry.TAB_ICON.get());
            icon.set(AMDataComponentRegistry.DISPLAY_ENTITY, DisplayEntity.of(entityType));

            Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .parent(parent)
                .display(icon, Component.translatable(entityId.toLanguageKey("advancements", "title")), Component.translatable(entityId.toLanguageKey("advancements", "desc")), null, AdvancementType.TASK,
                    true, true, false);

            EntityPredicate.Builder entityPredicate = EntityPredicate.Builder.entity()
                .entityType(EntityTypePredicate.of(entityType));

            advancementBuilder.addCriterion("killed", KilledTrigger.TriggerInstance.playerKilledEntity(entityPredicate));
            advancementBuilder.addCriterion("killed_by", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(Optional.of(entityPredicate.build())));
            advancementBuilder.addCriterion("interact", CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new PlayerInteractTrigger.TriggerInstance(Optional.empty(), Optional.empty(),
                Optional.of(EntityPredicate.wrap(entityPredicate)))));

            advancementBuilder.requirements(new AdvancementRequirements(List.of(
                List.of("killed", "killed_by", "interact")
            )));

            return register(consumer, id, advancementBuilder);
        }

        private static Advancement.Builder customTrigger(Advancement.Builder builder, DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> trigger) {
            return builder
                .addCriterion("ex", new Criterion<>(trigger.value(), new AMAdvancementTrigger.Instance(Optional.empty())))
                .requirements(new AdvancementRequirements(List.of(List.of("ex"))));
        }

        private static Criterion<UsingItemTrigger.TriggerInstance> lookAtThroughItem(EntityType<?> entity, ItemLike item) {
            return UsingItemTrigger.TriggerInstance.lookingAt(
                EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().setLookingAt(EntityPredicate.Builder.entity().of(entity)).build()),
                ItemPredicate.Builder.item().of(item)
            );
        }

        private ItemStack tabIcon(EntityType<?> entityType) {
            ItemStack stack = new ItemStack(AMItemRegistry.TAB_ICON.get(), 1);
            stack.set(AMDataComponentRegistry.DISPLAY_ENTITY, DisplayEntity.of(entityType));
            return stack;
        }

        private ItemStack tabIcon(EntityType<?> entityType, int flags) {
            ItemStack stack = new ItemStack(AMItemRegistry.TAB_ICON.get(), 1);
            stack.set(AMDataComponentRegistry.DISPLAY_ENTITY, new DisplayEntity(entityType, flags, Optional.empty()));
            return stack;
        }

        private ItemStack fancyItem(CustomRenderDisplay display) {
            ItemStack stack = new ItemStack(Citadel.FANCY_ITEM, 1);
            stack.set(Citadel.CUSTOM_RENDER_DISPLAY, display);
            return stack;
        }

        private ItemStack effectItem(Holder<MobEffect> effect) {
            ItemStack stack = new ItemStack(Citadel.EFFECT_ITEM.get(), 1);
            stack.set(Citadel.DISPLAY_EFFECT, BuiltInRegistries.MOB_EFFECT.getResourceKey(effect.value()).orElseThrow());
            return stack;
        }

        private AdvancementHolder tameEntity(Consumer<AdvancementHolder> output, EntityType<?> entityType, AdvancementHolder parent) {
            return tameEntity(output, entityType, parent, builder -> builder);
        }

        private AdvancementHolder tameEntity(Consumer<AdvancementHolder> output, EntityType<?> entityType, int flags, String suffix, AdvancementHolder parent) {
            return tameEntity(output, entityType, flags, parent, suffix, builder -> builder);
        }

        private AdvancementHolder tameEntity(Consumer<AdvancementHolder> output, EntityType<?> entityType, AdvancementHolder parent, UnaryOperator<EntityPredicate.Builder> entityBuilderMutator) {
            return tameEntity(output, entityType, 0, parent, "", entityBuilderMutator);
        }

        private AdvancementHolder tameEntity(Consumer<AdvancementHolder> output, EntityType<?> entityType, int flags, AdvancementHolder parent, String suffix, UnaryOperator<EntityPredicate.Builder> entityBuilderMutator) {
            ResourceLocation entityId = entityType.builtInRegistryHolder().key().location();
            String path = "tame_" + entityId.getPath() + suffix;
            return add(output, path, builder ->
                builder
                    .display(simpleDisplay(AdvancementType.TASK, tabIcon(entityType, flags), advancementComponent(path + ".title"), advancementComponent(path + ".desc")))
                    .parent(parent)
                    .addCriterion("tame", TameAnimalTrigger.TriggerInstance.tamedAnimal(entityBuilderMutator.apply(EntityPredicate.Builder.entity()
                        .entityType(EntityTypePredicate.of(entityType))
                    )))
                    .requirements(new AdvancementRequirements(List.of(List.of("tame"))))
            );
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, Holder<Item> item, AdvancementHolder parent) {
            return acquireItem(output, item.value(), parent);
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, Holder<Item> item, AdvancementType type, AdvancementHolder parent) {
            return acquireItem(output, item.value(), new ItemStack(item), type, parent);
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, Holder<Item> item, ItemStack display, AdvancementHolder parent) {
            return acquireItem(output, item.value(), display, parent);
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, Item item, AdvancementHolder parent) {
            return acquireItem(output, item, new ItemStack(item), parent);
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, Item item, ItemStack display, AdvancementHolder parent) {
            return acquireItem(output, item, display, AdvancementType.TASK, parent);
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, Item item, ItemStack display, AdvancementType type, AdvancementHolder parent) {
            return acquireItem(output, item.builtInRegistryHolder().key().location().getPath(), type, item, display, parent);
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, String path, AdvancementType type, Holder<Item> item, ItemStack display, AdvancementHolder parent) {
            return acquireItem(output, path, type, item.value(), display, parent);
        }

        private AdvancementHolder acquireItem(Consumer<AdvancementHolder> output, String path, AdvancementType type, Item item, ItemStack display, AdvancementHolder parent) {
            return add(output, path, type, display, builder -> builder
                .parent(parent)
                .addCriterion(path, InventoryChangeTrigger.TriggerInstance.hasItems(item))
                .requirements(new AdvancementRequirements(List.of(List.of(path))))
            );
        }

        private AdvancementHolder killEntity(Consumer<AdvancementHolder> output, Holder<EntityType<?>> entityType, ItemStack display, AdvancementHolder parent) {
            return killEntity(output, entityType, display, AdvancementType.TASK, parent);
        }

        private AdvancementHolder killEntity(Consumer<AdvancementHolder> output, Holder<EntityType<?>> entityType, ItemStack display, AdvancementType type, AdvancementHolder parent) {
            return killEntity(output, entityType.value(), display, type, parent);
        }

        private AdvancementHolder killEntity(Consumer<AdvancementHolder> output, EntityType<?> entityType, ItemStack display, AdvancementType type, AdvancementHolder parent) {
            String path = entityType.builtInRegistryHolder().key().location().getPath() + "_kill";
            return add(output, path, AdvancementType.TASK, display, builder -> builder
                .parent(parent)
                .addCriterion("ex", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity()
                    .entityType(EntityTypePredicate.of(entityType))
                ))
                .requirements(new AdvancementRequirements(List.of(List.of("ex"))))
            );
        }

        private AdvancementHolder acquireEffect(Consumer<AdvancementHolder> output, Holder<MobEffect> effect, ItemStack display, AdvancementType type, AdvancementHolder parent) {
            return add(output, effect.unwrapKey().orElseThrow().location().getPath(), type, display, builder ->
                builder
                    .parent(parent)
                    .addCriterion("all_effects", EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects()
                        .and(effect)
                    ))
            );
        }

        private AdvancementHolder add(Consumer<AdvancementHolder> output, String path, UnaryOperator<Advancement.Builder> builder) {
            var advancement = builder.apply(Advancement.Builder.advancement())
                .build(AlexsMobs.prefix("alexsmobs/" + path));

            output.accept(advancement);
            return advancement;
        }

        private AdvancementHolder add(Consumer<AdvancementHolder> output, String path, AdvancementType type, ItemLike icon, UnaryOperator<Advancement.Builder> builder) {
            return add(output, path, type, new ItemStack(icon), builder);
        }

        private AdvancementHolder add(Consumer<AdvancementHolder> output, String path, AdvancementType type, ItemStack icon, UnaryOperator<Advancement.Builder> builder) {
            var advancement = builder.apply(Advancement.Builder.advancement())
                .display(simpleDisplay(type, icon, advancementComponent(path + ".title"), advancementComponent(path + ".desc")))
                .build(AlexsMobs.prefix("alexsmobs/" + path));
            output.accept(advancement);

            return advancement;
        }

        private Component advancementComponent(String path) {
            return Component.translatable("advancements.alexsmobs." + path);
        }

        private DisplayInfo simpleDisplay(AdvancementType type, ItemStack icon, Component title, Component description) {
            return new DisplayInfo(icon, title, description, Optional.empty(), type, true, true, false);
        }

        private AdvancementHolder register(Consumer<AdvancementHolder> consumer, ResourceLocation id, Advancement.Builder builder) {
            var holder = builder.build(id);
            consumer.accept(holder);

            return holder;
        }

        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> output, ExistingFileHelper existingFileHelper) {
            var root = add(output, "root", builder -> builder
                .display(AMItemRegistry.ANIMAL_DICTIONARY.value(), Component.translatable("advancements.alexsmobs.root.title"), Component.translatable("advancements.alexsmobs.root.desc"), AlexsMobs.prefix("textures/advancement_background.png"), AdvancementType.TASK, true, true, false)
                .addCriterion("useitem", ConsumeItemTrigger.TriggerInstance.usedItem(AMItemRegistry.ANIMAL_DICTIONARY.value()))
                .requirements(new AdvancementRequirements(List.of(List.of("useitem"))))
            );

            var acaciaBlossom = acquireItem(output, AMItemRegistry.ACACIA_BLOSSOM, root);
            var froststalkerKill = killEntity(output, AMEntityRegistry.FROSTSTALKER, AMItemRegistry.FROSTSTALKER_HORN.toStack(), root);
            var froststalkerHelmet = acquireItem(output, AMItemRegistry.FROSTSTALKER_HELMET, froststalkerKill);

            var tameBaldEagle = tameEntity(output, AMEntityRegistry.BALD_EAGLE.value(), root);
            var tameCapuchin = tameEntity(output, AMEntityRegistry.CAPUCHIN_MONKEY.value(), root);
            var tameCosmaw = tameEntity(output, AMEntityRegistry.COSMAW.value(), root);
            var tameElephant = tameEntity(output, AMEntityRegistry.ELEPHANT.value(), root);
            var tameElephantTusked = tameEntity(output, AMEntityRegistry.ELEPHANT.value(), 99, "_tusked", root);
            var tameFlutter = tameEntity(output, AMEntityRegistry.FLUTTER.value(), root);
            var tameGorilla = tameEntity(output, AMEntityRegistry.GORILLA.value(), root);
            var tameGrizzlyBear = tameEntity(output, AMEntityRegistry.GRIZZLY_BEAR.value(), root);
            var tameMantisShrimp = tameEntity(output, AMEntityRegistry.MANTIS_SHRIMP.value(), root);
            var tameTarantulaHawk = tameEntity(output, AMEntityRegistry.TARANTULA_HAWK.value(), root);
            var tameWarpedToad = tameEntity(output, AMEntityRegistry.WARPED_TOAD.value(), root);

            var falconryGlove = acquireItem(output, AMItemRegistry.FALCONRY_GLOVE, fancyItem(new CustomRenderDisplay(new ItemStack(AMItemRegistry.FALCONRY_GLOVE.get()), false, true, false, false, 1f)), tameBaldEagle);
            var falconryHood = acquireItem(output, AMItemRegistry.FALCONRY_HOOD, falconryGlove);

            var baldEagleChallenge = add(output, "bald_eagle_challenge", AdvancementType.CHALLENGE, tabIcon(AMEntityRegistry.BALD_EAGLE.get(), 98), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.BALD_EAGLE_CHALLENGE)
                    .parent(falconryHood)
            );

            var banana = acquireItem(output, AMItemRegistry.BANANA, root);

            var bisonSpyglass = add(output, "bison_spyglass", AdvancementType.CHALLENGE, tabIcon(AMEntityRegistry.BISON.get()), builder -> builder
                .parent(root)
                .addCriterion("spyglass_at_bison", lookAtThroughItem(AMEntityRegistry.BISON.get(), Items.SPYGLASS))
            );

            var laviathanSpyglass = add(output, "laviathan_spyglass", AdvancementType.CHALLENGE, Items.SPYGLASS, builder -> builder
                .parent(root)
                .addCriterion("spyglass_at_bison", lookAtThroughItem(AMEntityRegistry.LAVIATHAN.get(), Items.SPYGLASS))
            );

            var kangaroo = meetEntity(output, AMEntityRegistry.KANGAROO, root);
            var bananaSlug = meetEntity(output, AMEntityRegistry.BANANA_SLUG, banana);
            var crimsonMosquito = meetEntity(output, AMEntityRegistry.CRIMSON_MOSQUITO, root);

            var bloodSprayer = acquireItem(output, AMItemRegistry.BLOOD_SPRAYER, crimsonMosquito);

            var stompLeafcutterAnthill = add(output, "stomp_leafcutter_anthill", AdvancementType.TASK, tabIcon(AMEntityRegistry.LEAFCUTTER_ANT.get()), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.STOMP_LEAFCUTTER_ANTHILL)
                    .parent(root)
            );
            var gongylidia = acquireItem(output, AMItemRegistry.GONGYLIDIA, stompLeafcutterAnthill);
            var leafcutterAntPupa = acquireItem(output, AMItemRegistry.LEAFCUTTER_ANT_PUPA, gongylidia);

            var breedAnteater = add(output, "breed_anteater", AdvancementType.TASK, tabIcon(AMEntityRegistry.ANTEATER.get()), builder -> builder
                .parent(leafcutterAntPupa)
                .addCriterion("breed", BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(AMEntityRegistry.ANTEATER.get())))
            );

            var crocodile = meetEntity(output, AMEntityRegistry.CROCODILE, root);

            var breedCrocodile = add(output, "breed_crocodile", AdvancementType.TASK, tabIcon(AMEntityRegistry.CROCODILE.get()), builder -> builder
                .parent(crocodile)
                .addCriterion("breed", BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(AMEntityRegistry.CROCODILE.get())))
            );

            var breedFroststalker = add(output, "breed_froststalker", AdvancementType.CHALLENGE, tabIcon(AMEntityRegistry.FROSTSTALKER.get()), builder -> builder
                .parent(froststalkerHelmet)
                .addCriterion("breed", BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(AMEntityRegistry.FROSTSTALKER.get())))
            );

            var breedHummingbird = add(output, "breed_hummingbird", AdvancementType.TASK, tabIcon(AMEntityRegistry.HUMMINGBIRD.get()), builder -> builder
                .parent(root)
                .addCriterion("breed", BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(AMEntityRegistry.HUMMINGBIRD.get())))
            );

            var crimsonMosquitoLarva = acquireItem(output, AMItemRegistry.MOSQUITO_LARVA, crimsonMosquito);

            var crimsonMosquitoSick = add(output, "crimson_mosquito_sick", AdvancementType.TASK, fancyItem(new CustomRenderDisplay(new ItemStack(Items.WARPED_FUNGUS), true, false, false, false, 1f)), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.MOSQUITO_SICK)
                    .parent(crimsonMosquito)
            );

            var crocodileChestplate = acquireItem(output, AMItemRegistry.CROCODILE_CHESTPLATE, AdvancementType.CHALLENGE, breedCrocodile);

            var skelewag = meetEntity(output, AMEntityRegistry.SKELEWAG, root);
            var fishBones = acquireItem(output, AMItemRegistry.FISH_BONES, skelewag);
            var strangeFishFinder = acquireItem(output, AMItemRegistry.PUPFISH_LOCATOR, fishBones);
            var devilsHolePupfishBucket = acquireItem(output, AMItemRegistry.DEVILS_HOLE_PUPFISH_BUCKET, strangeFishFinder);

            var enderiophage = meetEntity(output, AMEntityRegistry.ENDERIOPHAGE, root);
            var capsid = add(output, "capsid", AdvancementType.TASK, AMBlockRegistry.CAPSID.value(), builder -> builder
                .parent(enderiophage)
                .addCriterion("capsid", InventoryChangeTrigger.TriggerInstance.hasItems(AMBlockRegistry.CAPSID.value()))
            );
            var mysteriousWorm = acquireItem(output, AMItemRegistry.MYSTERIOUS_WORM, capsid);
            var voidWormSummon = add(output, "void_worm_summon", AdvancementType.GOAL, tabIcon(AMEntityRegistry.VOID_WORM.value()), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.VOID_WORM_SUMMON)
                    .parent(mysteriousWorm)
            );
            var voidWormSplit = add(output, "void_worm_split", AdvancementType.TASK, AMItemRegistry.VOID_WORM_EYE, builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.VOID_WORM_SPLIT)
                    .parent(voidWormSummon)
            );
            var voidWormKill = add(output, "void_worm_kill", AdvancementType.CHALLENGE, AMBlockRegistry.VOID_WORM_BEAK.value(), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.VOID_WORM_SLAY_HEAD)
                    .parent(voidWormSummon)
            );
            var dimensionalCarver = acquireItem(output, AMItemRegistry.DIMENSIONAL_CARVER.value(), fancyItem(new CustomRenderDisplay(new ItemStack(AMItemRegistry.DIMENSIONAL_CARVER.get()), false, false, false, true, 1f)), AdvancementType.CHALLENGE, voidWormKill);

            var saveCachalotWhale = acquireItem(output, "save_cachalot_whale", AdvancementType.CHALLENGE, AMItemRegistry.AMBERGRIS, tabIcon(AMEntityRegistry.CACHALOT_WHALE.value(), 99), root);
            var echolocator = acquireItem(output, AMItemRegistry.ECHOLOCATOR, saveCachalotWhale);

            var elephantSwag = add(output, "elephant_swag", AdvancementType.TASK, tabIcon(AMEntityRegistry.ELEPHANT.get(), 98), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.ELEPHANT_SWAG)
                    .parent(tameElephant)
            );

            var alligatorSnappingTurtle = meetEntity(output, AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, crocodile);

            var emu = meetEntity(output, AMEntityRegistry.EMU, kangaroo);
            var emuDodge = add(output, "emu_dodge", AdvancementType.TASK, Items.BLUE_BANNER, builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.EMU_DODGE)
                    .parent(emu)
            );

            var cosmicCod = add(output, "cosmic_cod", AdvancementType.TASK, AMItemRegistry.COSMIC_COD, builder -> builder
                .parent(capsid)
                .addCriterion("cosmic_cod", InventoryChangeTrigger.TriggerInstance.hasItems(AMItemRegistry.COSMIC_COD.value()))
            );

            var enderFlu = add(output, "ender_flu", AdvancementType.TASK, effectItem(AMEffectRegistry.ENDER_FLU), builder -> builder
                .parent(enderiophage)
                .addCriterion("all_effects", EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(AMEffectRegistry.ENDER_FLU)))
            );

            var enderiophageRocket = add(output, "enderiophage_rocket", AdvancementType.TASK, AMItemRegistry.ENDERIOPHAGE_ROCKET, builder -> builder
                .parent(capsid)
                .addCriterion("enderiophage_rocket", InventoryChangeTrigger.TriggerInstance.hasItems(AMItemRegistry.ENDERIOPHAGE_ROCKET.value()))
            );

            var endolocator =  add(output, "endolocator", AdvancementType.TASK, AMItemRegistry.ENDOLOCATOR, builder -> builder
                .parent(echolocator)
                .addCriterion("endolocator", InventoryChangeTrigger.TriggerInstance.hasItems(AMItemRegistry.ENDOLOCATOR.value()))
            );

            var shatteredDimensionalCarver = acquireItem(output, AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER, dimensionalCarver);
            var farseer = meetEntity(output, AMEntityRegistry.FARSEER, shatteredDimensionalCarver);
            var grizzlyBear = meetEntity(output, AMEntityRegistry.GRIZZLY_BEAR, root);
            var guster = meetEntity(output, AMEntityRegistry.GUSTER, root);
            var murmur = meetEntity(output, AMEntityRegistry.MURMUR, root);
            var rattlesnake = meetEntity(output, AMEntityRegistry.RATTLESNAKE, root);
            var rockyRoller = meetEntity(output, AMEntityRegistry.ROCKY_ROLLER, root);

            var skreecher = meetEntity(output, AMEntityRegistry.SKREECHER, root);
            var skunk = meetEntity(output, AMEntityRegistry.SKUNK, root);
            var soulVulture = meetEntity(output, AMEntityRegistry.SOUL_VULTURE, root);
            var soulHeart = acquireItem(output, AMItemRegistry.SOUL_HEART, soulVulture);
            var spectre = meetEntity(output, AMEntityRegistry.SPECTRE, soulHeart);
            var straddler = meetEntity(output, AMEntityRegistry.STRADDLER, root);
            var tarantulaHawk = meetEntity(output, AMEntityRegistry.TARANTULA_HAWK, root);
            var underminer = meetEntity(output, AMEntityRegistry.UNDERMINER, root);
            var ghostlyPickaxe = acquireItem(output, AMItemRegistry.GHOSTLY_PICKAXE, underminer);

            var gustmaker = acquireItem(output, AMBlockRegistry.GUSTMAKER.value().asItem(), guster);
            var hummingbirdFeeder = acquireItem(output, AMBlockRegistry.HUMMINGBIRD_FEEDER.value().asItem(), breedHummingbird);
            var lostTentacle = acquireItem(output, AMItemRegistry.LOST_TENTACLE, root);
            var maraca = acquireItem(output, AMItemRegistry.MARACA, rattlesnake);
            var rainbowJelly = acquireItem(output, AMItemRegistry.RAINBOW_JELLY, tabIcon(AMEntityRegistry.COMB_JELLY.value()), root);
            var rainbowGlass = acquireItem(output, AMBlockRegistry.RAINBOW_GLASS.value().asItem(), rainbowJelly);
            var rockyShell = acquireItem(output, AMItemRegistry.ROCKY_SHELL, rockyRoller);
            var rockyChestplate = acquireItem(output, AMItemRegistry.ROCKY_CHESTPLATE, rockyShell);
            var sculkBoomer = acquireItem(output, AMBlockRegistry.SCULK_BOOMER.value().asItem(), skreecher);
            var spikedScute = acquireItem(output, AMItemRegistry.SPIKED_SCUTE, alligatorSnappingTurtle);
            var spikedTurtleShell = acquireItem(output, AMItemRegistry.SPIKED_TURTLE_SHELL, spikedScute);
            var squidGrapple = acquireItem(output, AMItemRegistry.SQUID_GRAPPLE, lostTentacle);
            var stinkBottle = acquireItem(output, AMItemRegistry.STINK_BOTTLE, skunk);
            var stinkRay = acquireItem(output, AMItemRegistry.STINK_RAY, stinkBottle);
            var straddleboard = acquireItem(output, AMItemRegistry.STRADDLEBOARD, straddler);
            var straddleSaddle = acquireItem(output, AMItemRegistry.STRADDLE_SADDLE, straddler);
            var transmutationTable = acquireItem(output, AMBlockRegistry.TRANSMUTATION_TABLE.value().asItem(), farseer);

            var transmute1000Items = add(output, "transmute_1000_items", AdvancementType.CHALLENGE, AMBlockRegistry.TRANSMUTATION_TABLE.value(), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.TRANSMUTE_1000_ITEMS)
                    .parent(voidWormSummon)
            );

            var warpedMoscoKill = killEntity(output, AMEntityRegistry.WARPED_MOSCO, tabIcon(AMEntityRegistry.WARPED_MOSCO.value()), AdvancementType.CHALLENGE, crimsonMosquitoSick);

            var stradpoleFeed = add(output, "stradpole_feed", AdvancementType.TASK, tabIcon(AMEntityRegistry.STRADPOLE.value()), builder ->
                builder.parent(straddler)
                    .addCriterion("tame", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item()
                        .of(AMItemRegistry.MOSQUITO_LARVA.value()),
                        Optional.of(
                            EntityPredicate.wrap(EntityPredicate.Builder.entity()
                                .of(AMEntityRegistry.STRADPOLE.value())
                            )
                        )
                    ))
            );

            var tigersBlessing = acquireEffect(output, AMEffectRegistry.TIGERS_BLESSING, tabIcon(AMEntityRegistry.TIGER.value()), AdvancementType.CHALLENGE, root);
            var sunbirdBlessing = acquireEffect(output, AMEffectRegistry.SUNBIRD_BLESSING, tabIcon(AMEntityRegistry.SUNBIRD.value()), AdvancementType.CHALLENGE, root);
            var orcasMight = acquireEffect(output, AMEffectRegistry.ORCAS_MIGHT, tabIcon(AMEntityRegistry.ORCA.value()), AdvancementType.CHALLENGE, root);

            var skelewagSkull = acquireItem(output, "skelewag_skull", AdvancementType.TASK, AMItemRegistry.SKELEWAG_SWORD, fancyItem(new CustomRenderDisplay(AMItemRegistry.SKELEWAG_SWORD.toStack(), false, true, false, false, 1f)), skelewag);
            var noveltyHat = acquireItem(output, AMItemRegistry.NOVELTY_HAT, fancyItem(new CustomRenderDisplay(AMItemRegistry.NOVELTY_HAT.toStack(), false, false, false, true, 1f)), skelewagSkull);
            var seagullSteal = add(output, "seagull_steal", AdvancementType.TASK, tabIcon(AMEntityRegistry.SEAGULL.value()), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.SEAGULL_STEAL)
                    .parent(root)
            );

            var laCucaracha = add(output, "la_cucaracha", AdvancementType.CHALLENGE, tabIcon(AMEntityRegistry.COCKROACH.value(), 99), builder ->
                builder.parent(maraca)
                    .addCriterion("tame", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item()
                            .of(AMItemRegistry.MARACA.value()),
                        Optional.of(
                            EntityPredicate.wrap(EntityPredicate.Builder.entity()
                                .of(AMEntityRegistry.COCKROACH.value())
                            )
                        )
                    ))
            );

            var laviathanFourPassenger = add(output, "laviathan_four_passenger", AdvancementType.TASK, tabIcon(AMEntityRegistry.LAVIATHAN.value()), builder ->
                customTrigger(builder, AMAdvancementTriggerRegistry.LAVIATHAN_FOUR_PASSENGERS)
                    .parent(straddleSaddle)
            );

            var pocketSand = acquireItem(output, AMItemRegistry.POCKET_SAND, fancyItem(new CustomRenderDisplay(AMItemRegistry.POCKET_SAND.toStack(), false, false, false, true, 1f)), guster);
            var mantisShrimpBucket = add(output, "mantis_shrimp_bucket", AdvancementType.TASK, Items.WATER_BUCKET, builder ->
                builder.parent(tameMantisShrimp)
                    .addCriterion("tame",
                        PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                            Optional.of(
                                EntityPredicate.wrap(EntityPredicate.Builder.entity()
                                    .flags(EntityFlagsPredicate.Builder.flags()
                                        .setCrouching(true)
                                    ))
                            ),
                            ItemPredicate.Builder.item()
                                .of(Items.WATER_BUCKET),
                            Optional.of(
                                EntityPredicate.wrap(EntityPredicate.Builder.entity()
                                    .of(AMEntityRegistry.MANTIS_SHRIMP.value())
                                )
                            )
                        )
                    )
            );

            var hemolymphBlaster = acquireItem(output, "hemolymph_blaster", AdvancementType.TASK, AMItemRegistry.HEMOLYMPH_BLASTER, fancyItem(new CustomRenderDisplay(AMItemRegistry.HEMOLYMPH_BLASTER.toStack(), false, false, false, true, 1f)), warpedMoscoKill);
            var manedWolf = add(output, "maned_wolf_apple", AdvancementType.TASK, tabIcon(AMEntityRegistry.MANED_WOLF.value()), builder ->
                builder.parent(root)
                    .addCriterion("tame", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item()
                            .of(Items.APPLE),
                        Optional.of(
                            EntityPredicate.wrap(EntityPredicate.Builder.entity()
                                .of(AMEntityRegistry.MANED_WOLF.value())
                            )
                        )
                    ))
            );

            var sopaDeMacaco = acquireItem(output, "sopa_de_macaco", AdvancementType.CHALLENGE, AMItemRegistry.SOPA_DE_MACACO, fancyItem(new CustomRenderDisplay(AMItemRegistry.SOPA_DE_MACACO.toStack(), false, true, false, false, 1f)), tameCapuchin);
        }
    }
}
