package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.AMSpecialRenderBlock;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMItemRegistry {
    public static AMArmorMaterial ROADRUNNER_ARMOR_MATERIAL = new AMArmorMaterial("roadrunner", 18, new int[]{3, 3, 3, 3}, 20, SoundEvents.ARMOR_EQUIP_TURTLE, 0);
    public static AMArmorMaterial CROCODILE_ARMOR_MATERIAL = new AMArmorMaterial("crocodile", 22, new int[]{2, 5, 7, 3}, 25, SoundEvents.ARMOR_EQUIP_TURTLE, 1);
    public static AMArmorMaterial CENTIPEDE_ARMOR_MATERIAL = new AMArmorMaterial("centipede", 20, new int[]{6, 6, 6, 6}, 22, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F);
    public static AMArmorMaterial MOOSE_ARMOR_MATERIAL = new AMArmorMaterial("moose", 19, new int[]{3, 3, 3, 3}, 21, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F);
    public static AMArmorMaterial RACCOON_ARMOR_MATERIAL = new AMArmorMaterial("raccoon", 17, new int[]{3, 3, 3, 3}, 21, SoundEvents.ARMOR_EQUIP_LEATHER, 2.5F);
    public static AMArmorMaterial SOMBRERO_ARMOR_MATERIAL = new AMArmorMaterial("sombrero", 14, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static AMArmorMaterial SPIKED_TURTLE_SHELL_ARMOR_MATERIAL = new AMArmorMaterial("spiked_turtle_shell", 35, new int[]{3, 3, 3, 3}, 30, SoundEvents.ARMOR_EQUIP_TURTLE, 1F, 0.2F);
    public static AMArmorMaterial FEDORA_ARMOR_MATERIAL = new AMArmorMaterial("fedora", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static AMArmorMaterial EMU_ARMOR_MATERIAL = new AMArmorMaterial("emu", 9, new int[]{4, 4, 4, 4}, 20, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static AMArmorMaterial TARANTULA_HAWK_ELYTRA_MATERIAL = new AMArmorMaterial("tarantula_hawk_elytra", 9, new int[]{3, 3, 3, 3}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0);
    public static AMArmorMaterial FROSTSTALKER_ARMOR_MATERIAL = new AMArmorMaterial("froststalker", 9, new int[]{3, 3, 3, 3}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
    public static AMArmorMaterial ROCKY_ARMOR_MATERIAL = new AMArmorMaterial("rocky_roller", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F);
    public static AMArmorMaterial FLYING_FISH_MATERIAL = new AMArmorMaterial("flying_fish", 9, new int[]{1, 1, 1, 1}, 8, SoundEvents.ARMOR_EQUIP_LEATHER, 0F);
    public static AMArmorMaterial NOVELTY_HAT_MATERIAL = new AMArmorMaterial("novelty_hat", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0F);

    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, AlexsMobs.MODID);

    public static final RegistryObject<Item> TAB_ICON = DEF_REG.register("tab_icon", () -> new ItemTabIcon(new Item.Properties()));
    public static final RegistryObject<Item> ANIMAL_DICTIONARY = DEF_REG.register("animal_dictionary", () -> new ItemAnimalDictionary(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)));
    public static final RegistryObject<Item> BEAR_FUR = DEF_REG.register("bear_fur", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> BEAR_DUST = DEF_REG.register("bear_dust", () -> new ItemBearDust(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ROADRUNNER_FEATHER = DEF_REG.register("roadrunner_feather", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> ROADDRUNNER_BOOTS = DEF_REG.register("roadrunner_boots", () -> new ItemModArmor(ROADRUNNER_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> LAVA_BOTTLE = DEF_REG.register("lava_bottle", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)));
    public static final RegistryObject<Item> BONE_SERPENT_TOOTH = DEF_REG.register("bone_serpent_tooth", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()));
    public static final RegistryObject<Item> GAZELLE_HORN = DEF_REG.register("gazelle_horn", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()));
    public static final RegistryObject<Item> CROCODILE_SCUTE = DEF_REG.register("crocodile_scute", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> CROCODILE_CHESTPLATE = DEF_REG.register("crocodile_chestplate", () -> new ItemModArmor(CROCODILE_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> MAGGOT = DEF_REG.register("maggot", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).build())));
    public static final RegistryObject<Item> BANANA = DEF_REG.register("banana", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build())));
    public static final RegistryObject<Item> ANCIENT_DART = DEF_REG.register("ancient_dart", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HALO = DEF_REG.register("halo", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_SAC = DEF_REG.register("blood_sac", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));

    public static final RegistryObject<Item> MOSQUITO_PROBOSCIS = DEF_REG.register("mosquito_proboscis", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> BLOOD_SPRAYER = DEF_REG.register("blood_sprayer", () -> new ItemBloodSprayer(new Item.Properties().tab(AlexsMobs.TAB).durability(100)));
    public static final RegistryObject<Item> RATTLESNAKE_RATTLE = DEF_REG.register("rattlesnake_rattle", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> CHORUS_ON_A_STICK = DEF_REG.register("chorus_on_a_stick", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)));
    public static final RegistryObject<Item> SHARK_TOOTH = DEF_REG.register("shark_tooth", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SHARK_TOOTH_ARROW = DEF_REG.register("shark_tooth_arrow", () -> new ItemModArrow(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> LOBSTER_TAIL = DEF_REG.register("lobster_tail", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).meat().build())));
    public static final RegistryObject<Item> COOKED_LOBSTER_TAIL = DEF_REG.register("cooked_lobster_tail", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(6).saturationMod(0.65F).meat().build())));
    public static final RegistryObject<Item> LOBSTER_BUCKET = DEF_REG.register("lobster_bucket", () -> new ItemModFishBucket(AMEntityRegistry.LOBSTER, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> KOMODO_SPIT = DEF_REG.register("komodo_spit", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> KOMODO_SPIT_BOTTLE = DEF_REG.register("komodo_spit_bottle", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> POISON_BOTTLE = DEF_REG.register("poison_bottle", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SOPA_DE_MACACO = DEF_REG.register("sopa_de_macaco", () -> new BowlFoodItem(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(5).saturationMod(0.4F).meat().build()).stacksTo(1)));
    public static final RegistryObject<Item> CENTIPEDE_LEG = DEF_REG.register("centipede_leg", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> CENTIPEDE_LEGGINGS = DEF_REG.register("centipede_leggings", () -> new ItemModArmor(CENTIPEDE_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> MOSQUITO_LARVA = DEF_REG.register("mosquito_larva", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> MOOSE_ANTLER = DEF_REG.register("moose_antler", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> MOOSE_HEADGEAR = DEF_REG.register("moose_headgear", () -> new ItemModArmor(MOOSE_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> MOOSE_RIBS = DEF_REG.register("moose_ribs", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(3).saturationMod(0.6F).meat().build())));
    public static final RegistryObject<Item> COOKED_MOOSE_RIBS = DEF_REG.register("cooked_moose_ribs", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(7).saturationMod(0.85F).meat().build())));
    public static final RegistryObject<Item> MIMICREAM = DEF_REG.register("mimicream", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> RACCOON_TAIL = DEF_REG.register("raccoon_tail", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FRONTIER_CAP = DEF_REG.register("frontier_cap", () -> new ItemModArmor(RACCOON_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> BLOBFISH = DEF_REG.register("blobfish", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).meat().effect(new MobEffectInstance(MobEffects.POISON, 120, 0), 1F).build())));
    public static final RegistryObject<Item> BLOBFISH_BUCKET = DEF_REG.register("blobfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.BLOBFISH, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FISH_OIL = DEF_REG.register("fish_oil", () -> new ItemFishOil(new Item.Properties().tab(AlexsMobs.TAB).craftRemainder(Items.GLASS_BOTTLE).food(new FoodProperties.Builder().nutrition(0).saturationMod(0.2F).build())));
    public static final RegistryObject<Item> MARACA = DEF_REG.register("maraca", () -> new ItemMaraca(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SOMBRERO = DEF_REG.register("sombrero", () -> new ItemModArmor(SOMBRERO_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> COCKROACH_WING_FRAGMENT = DEF_REG.register("cockroach_wing_fragment", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> COCKROACH_WING = DEF_REG.register("cockroach_wing", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> COCKROACH_OOTHECA = DEF_REG.register("cockroach_ootheca", () -> new ItemAnimalEgg(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> ACACIA_BLOSSOM = DEF_REG.register("acacia_blossom", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SOUL_HEART = DEF_REG.register("soul_heart", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SPIKED_SCUTE = DEF_REG.register("spiked_scute", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SPIKED_TURTLE_SHELL = DEF_REG.register("spiked_turtle_shell", () -> new ItemModArmor(SPIKED_TURTLE_SHELL_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> SHRIMP_FRIED_RICE = DEF_REG.register("shrimp_fried_rice", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(12).saturationMod(1F).build())));
    public static final RegistryObject<Item> GUSTER_EYE = DEF_REG.register("guster_eye", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> POCKET_SAND = DEF_REG.register("pocket_sand", () -> new ItemPocketSand(new Item.Properties().tab(AlexsMobs.TAB).durability(220)));
    public static final RegistryObject<Item> WARPED_MUSCLE = DEF_REG.register("warped_muscle", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> HEMOLYMPH_SAC = DEF_REG.register("hemolymph_sac", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> HEMOLYMPH_BLASTER = DEF_REG.register("hemolymph_blaster", () -> new ItemHemolymphBlaster(new Item.Properties().tab(AlexsMobs.TAB).durability(150)));
    public static final RegistryObject<Item> WARPED_MIXTURE = DEF_REG.register("warped_mixture", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).rarity(Rarity.RARE).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE)));
    public static final RegistryObject<Item> STRADDLITE = DEF_REG.register("straddlite", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()));
    public static final RegistryObject<Item> STRADPOLE_BUCKET = DEF_REG.register("stradpole_bucket", () -> new ItemModFishBucket(AMEntityRegistry.STRADPOLE, Fluids.LAVA, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> STRADDLEBOARD = DEF_REG.register("straddleboard", () -> new ItemStraddleboard(new Item.Properties().tab(AlexsMobs.TAB).fireResistant().durability(220)));
    public static final RegistryObject<Item> EMU_EGG = DEF_REG.register("emu_egg", () -> new ItemAnimalEgg(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(8)));
    public static final RegistryObject<Item> BOILED_EMU_EGG = DEF_REG.register("boiled_emu_egg", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(1F).meat().build())));
    public static final RegistryObject<Item> EMU_FEATHER = DEF_REG.register("emu_feather", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()));
    public static final RegistryObject<Item> EMU_LEGGINGS = DEF_REG.register("emu_leggings", () -> new ItemModArmor(EMU_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> PLATYPUS_BUCKET = DEF_REG.register("platypus_bucket", () -> new ItemModFishBucket(AMEntityRegistry.PLATYPUS, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FEDORA = DEF_REG.register("fedora", () -> new ItemModArmor(FEDORA_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DROPBEAR_CLAW = DEF_REG.register("dropbear_claw", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> KANGAROO_MEAT = DEF_REG.register("kangaroo_meat", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).meat().build())));
    public static final RegistryObject<Item> COOKED_KANGAROO_MEAT = DEF_REG.register("cooked_kangaroo_meat", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(8).saturationMod(0.85F).meat().build())));
    public static final RegistryObject<Item> KANGAROO_HIDE = DEF_REG.register("kangaroo_hide", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> KANGAROO_BURGER = DEF_REG.register("kangaroo_burger", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(12).saturationMod(1F).meat().build())));
    public static final RegistryObject<Item> AMBERGRIS = DEF_REG.register("ambergris", () -> new ItemFuel(new Item.Properties().tab(AlexsMobs.TAB), 12800));
    public static final RegistryObject<Item> CACHALOT_WHALE_TOOTH = DEF_REG.register("cachalot_whale_tooth", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> ECHOLOCATOR = DEF_REG.register("echolocator", () -> new ItemEcholocator(new Item.Properties().tab(AlexsMobs.TAB).durability(100), ItemEcholocator.EchoType.ECHOLOCATION));
    public static final RegistryObject<Item> ENDOLOCATOR = DEF_REG.register("endolocator", () -> new ItemEcholocator(new Item.Properties().tab(AlexsMobs.TAB).durability(25), ItemEcholocator.EchoType.ENDER));
    public static final RegistryObject<Item> GONGYLIDIA = DEF_REG.register("gongylidia", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(3).saturationMod(1.2F).build())));
    public static final RegistryObject<Item> LEAFCUTTER_ANT_PUPA = DEF_REG.register("leafcutter_ant_pupa", () -> new ItemLeafcutterPupa(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> ENDERIOPHAGE_ROCKET = DEF_REG.register("enderiophage_rocket", () -> new ItemEnderiophageRocket(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FALCONRY_GLOVE_INVENTORY = DEF_REG.register("falconry_glove_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FALCONRY_GLOVE_HAND = DEF_REG.register("falconry_glove_hand", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FALCONRY_GLOVE = DEF_REG.register("falconry_glove", () -> new ItemFalconryGlove(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)));
    public static final RegistryObject<Item> FALCONRY_HOOD = DEF_REG.register("falconry_hood", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> TARANTULA_HAWK_WING_FRAGMENT = DEF_REG.register("tarantula_hawk_wing_fragment", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> TARANTULA_HAWK_WING = DEF_REG.register("tarantula_hawk_wing", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> TARANTULA_HAWK_ELYTRA = DEF_REG.register("tarantula_hawk_elytra", () -> new ItemTarantulaHawkElytra(new Item.Properties().tab(AlexsMobs.TAB).durability(800).rarity(Rarity.UNCOMMON), TARANTULA_HAWK_ELYTRA_MATERIAL));
    public static final RegistryObject<Item> MYSTERIOUS_WORM = DEF_REG.register("mysterious_worm", () -> new ItemMysteriousWorm(new Item.Properties().tab(AlexsMobs.TAB).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> VOID_WORM_MANDIBLE = DEF_REG.register("void_worm_mandible", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> VOID_WORM_EYE = DEF_REG.register("void_worm_eye", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DIMENSIONAL_CARVER = DEF_REG.register("dimensional_carver", () -> new ItemDimensionalCarver(new Item.Properties().tab(AlexsMobs.TAB).durability(20).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SERRATED_SHARK_TOOTH = DEF_REG.register("serrated_shark_tooth", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FRILLED_SHARK_BUCKET = DEF_REG.register("frilled_shark_bucket", () -> new ItemModFishBucket(AMEntityRegistry.FRILLED_SHARK, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SHIELD_OF_THE_DEEP = DEF_REG.register("shield_of_the_deep", () -> new ItemShieldOfTheDeep(new Item.Properties().durability(400).rarity(Rarity.UNCOMMON).tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> MIMIC_OCTOPUS_BUCKET = DEF_REG.register("mimic_octopus_bucket", () -> new ItemModFishBucket(AMEntityRegistry.MIMIC_OCTOPUS, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FROSTSTALKER_HORN = DEF_REG.register("froststalker_horn", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FROSTSTALKER_HELMET = DEF_REG.register("froststalker_helmet", () -> new ItemModArmor(FROSTSTALKER_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> PIGSHOES = DEF_REG.register("pigshoes", () -> new ItemPigshoes(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)));
    public static final RegistryObject<Item> STRADDLE_HELMET = DEF_REG.register("straddle_helmet", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()));
    public static final RegistryObject<Item> STRADDLE_SADDLE = DEF_REG.register("straddle_saddle", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()));
    public static final RegistryObject<Item> COSMIC_COD = DEF_REG.register("cosmic_cod", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(6).saturationMod(0.3F).effect(new MobEffectInstance(AMEffectRegistry.ENDER_FLU, 12000), 0.15F).build())));
    public static final RegistryObject<Item> SHED_SNAKE_SKIN = DEF_REG.register("shed_snake_skin", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> VINE_LASSO_INVENTORY = DEF_REG.register("vine_lasso_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VINE_LASSO_HAND = DEF_REG.register("vine_lasso_hand", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VINE_LASSO = DEF_REG.register("vine_lasso", () -> new ItemVineLasso(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)));
    public static final RegistryObject<Item> ROCKY_SHELL = DEF_REG.register("rocky_shell", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> ROCKY_CHESTPLATE = DEF_REG.register("rocky_chestplate", () -> new ItemModArmor(ROCKY_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> POTTED_FLUTTER = DEF_REG.register("potted_flutter", () -> new ItemFlutterPot(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> TERRAPIN_BUCKET = DEF_REG.register("terrapin_bucket", () -> new ItemModFishBucket(AMEntityRegistry.TERRAPIN, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> COMB_JELLY_BUCKET = DEF_REG.register("comb_jelly_bucket", () -> new ItemModFishBucket(AMEntityRegistry.COMB_JELLY, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> RAINBOW_JELLY = DEF_REG.register("rainbow_jelly", () -> new ItemRainbowJelly(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).build())));
    public static final RegistryObject<Item> COSMIC_COD_BUCKET = DEF_REG.register("cosmic_cod_bucket", () -> new ItemCosmicCodBucket(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> MUNGAL_SPORES = DEF_REG.register("mungal_spores", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> BISON_FUR = DEF_REG.register("bison_fur", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> LOST_TENTACLE = DEF_REG.register("lost_tentacle", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SQUID_GRAPPLE = DEF_REG.register("squid_grapple", () -> new ItemSquidGrapple(new Item.Properties().tab(AlexsMobs.TAB).durability(450)));
    public static final RegistryObject<Item> DEVILS_HOLE_PUPFISH_BUCKET = DEF_REG.register("devils_hole_pupfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.DEVILS_HOLE_PUPFISH, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> PUPFISH_LOCATOR = DEF_REG.register("pupfish_locator", () -> new ItemEcholocator(new Item.Properties().tab(AlexsMobs.TAB).durability(200), ItemEcholocator.EchoType.PUPFISH));
    public static final RegistryObject<Item> SMALL_CATFISH_BUCKET = DEF_REG.register("small_catfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.CATFISH, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> MEDIUM_CATFISH_BUCKET = DEF_REG.register("medium_catfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.CATFISH, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> LARGE_CATFISH_BUCKET = DEF_REG.register("large_catfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.CATFISH, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> RAW_CATFISH = DEF_REG.register("raw_catfish", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).meat().build())));
    public static final RegistryObject<Item> COOKED_CATFISH = DEF_REG.register("cooked_catfish", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(5).saturationMod(0.5F).meat().build())));
    public static final RegistryObject<Item> FLYING_FISH = DEF_REG.register("flying_fish", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).meat().build())));
    public static final RegistryObject<Item> FLYING_FISH_BOOTS = DEF_REG.register("flying_fish_boots", () -> new ItemModArmor(FLYING_FISH_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> FLYING_FISH_BUCKET = DEF_REG.register("flying_fish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.FLYING_FISH, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> FISH_BONES = DEF_REG.register("fish_bones", () -> new Item(new Item.Properties().tab(AlexsMobs.TAB)));
    public static final RegistryObject<Item> SKELEWAG_SWORD_INVENTORY = DEF_REG.register("skelewag_sword_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SKELEWAG_SWORD_HAND = DEF_REG.register("skelewag_sword_hand", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SKELEWAG_SWORD = DEF_REG.register("skelewag_sword", () -> new ItemSkelewagSword(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1).durability(430)));
    public static final RegistryObject<Item> NOVELTY_HAT = DEF_REG.register("novelty_hat", () -> new ItemModArmor(NOVELTY_HAT_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> MUSIC_DISC_THIME = DEF_REG.register("music_disc_thime", () -> new RecordItem(14, AMSoundRegistry.MUSIC_DISC_THIME, new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> MUSIC_DISC_DAZE = DEF_REG.register("music_disc_daze", () -> new RecordItem(14, AMSoundRegistry.MUSIC_DISC_DAZE, new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1).rarity(Rarity.RARE)));

    public static final BannerPattern PATTERN_BEAR = addBanner("bear");
    public static final BannerPattern PATTER_AUSTRALIA_0 = addBanner("australia_0");
    public static final BannerPattern PATTER_AUSTRALIA_1 = addBanner("australia_1");
    public static final BannerPattern PATTERN_NEW_MEXICO = addBanner("new_mexico");
    public static final BannerPattern PATTERN_BRAZIL = addBanner("brazil");

    private static BannerPattern addBanner(String name) {
        return BannerPattern.create(name.toUpperCase(), name, "alexsmobs." + name, true);
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.GRIZZLY_BEAR, 0X693A2C, 0X976144, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_grizzly_bear"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ROADRUNNER, 0X3A2E26, 0XFBE9CE, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_roadrunner"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.BONE_SERPENT, 0XE5D9C4, 0XFF6038, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bone_serpent"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.GAZELLE, 0XDDA675,0X2C2925, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gazelle"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.CROCODILE, 0X738940,0XA6A15E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crocodile"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.FLY, 0X464241,0X892E2E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_fly"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.HUMMINGBIRD, 0X325E7F,0X44A75F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_hummingbird"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ORCA, 0X2C2C2C,0XD6D8E4, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_orca"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SUNBIRD, 0XF6694F,0XFFDDA0, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_sunbird"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.GORILLA, 0X595B5D,0X1C1C21, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gorilla"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.CRIMSON_MOSQUITO, 0X53403F,0XC11A1A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crimson_mosquito"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.RATTLESNAKE, 0XCEB994,0X937A5B, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_rattlesnake"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ENDERGRADE, 0X7862B3,0x81BDEB, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_endergrade"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.HAMMERHEAD_SHARK, 0X8A92B5,0XB9BED8, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_hammerhead_shark"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.LOBSTER, 0XC43123,0XDD5F38, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_lobster"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.KOMODO_DRAGON, 0X746C4F,0X564231, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_komodo_dragon"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.CAPUCHIN_MONKEY, 0X25211F,0XF1DAB3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_capuchin_monkey"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.CENTIPEDE_HEAD, 0X342B2E,0X733449, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_centipede"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.WARPED_TOAD, 0X1F968E,0XFEAC6D, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_warped_toad"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.MOOSE, 0X36302A,0XD4B183, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_moose"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.MIMICUBE, 0X8A80C1,0X5E4F6F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mimicube"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.RACCOON, 0X85827E,0X2A2726, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_raccoon"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.BLOBFISH, 0XDBC6BD,0X9E7A7F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_blobfish"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SEAL, 0X483C32,0X66594C, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_seal"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.COCKROACH, 0X0D0909,0X42241E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cockroach"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SHOEBILL, 0X828282,0XD5B48A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_shoebill"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ELEPHANT, 0X8D8987,0XEDE5D1, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_elephant"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SOUL_VULTURE, 0X23262D,0X57F4FF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_soul_vulture"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SNOW_LEOPARD, 0XACA293,0X26201D, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_snow_leopard"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SPECTRE, 0XC8D0EF,0X8791EF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_spectre"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.CROW, 0X0D111C,0X1C2030, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crow"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, 0X6C5C52,0X456926, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_alligator_snapping_turtle"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.MUNGUS, 0X836A8D,0X45454C, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mungus"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.MANTIS_SHRIMP, 0XDB4858,0X15991E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mantis_shrimp"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.GUSTER, 0XF8D49A,0XFF720A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_guster"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.WARPED_MOSCO, 0X322F58,0X5B5EF1, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_warped_mosco"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.STRADDLER, 0X5D5F6E,0XCDA886, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_straddler"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.STRADPOLE, 0X5D5F6E,0X576A8B, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_stradpole"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.EMU, 0X665346,0X3B3938, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_emu"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.PLATYPUS, 0X7D503E,0X363B43, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_platypus"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.DROPBEAR, 0X8A2D35,0X60A3A3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_dropbear"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.TASMANIAN_DEVIL, 0X252426,0XA8B4BF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tasmanian_devil"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.KANGAROO, 0XCE9D65,0XDEBDA0, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_kangaroo"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.CACHALOT_WHALE, 0X949899,0X5F666E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cachalot_whale"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.LEAFCUTTER_ANT, 0X964023,0XA65930, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_leafcutter_ant"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ENDERIOPHAGE, 0X872D83,0XF6E2CD, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_enderiophage"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.BALD_EAGLE, 0X321F18,0XF4F4F4, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bald_eagle"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.TIGER, 0XC7612E,0X2A3233, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tiger"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.TARANTULA_HAWK, 0X234763,0XE37B38, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tarantula_hawk"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.VOID_WORM, 0X0F1026,0X1699AB, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_void_worm"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.FRILLED_SHARK, 0X726B6B,0X873D3D, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_frilled_shark"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.MIMIC_OCTOPUS, 0XFFEBDC,0X1D1C1F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mimic_octopus"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SEAGULL, 0XC9D2DC,0XFFD850, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_seagull"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.FROSTSTALKER, 0X788AC1,0XA1C3FF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_froststalker"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.TUSKLIN, 0X735841,0XE8E2D5, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tusklin"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.LAVIATHAN, 0XD68356,0X3C3947, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_laviathan"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.COSMAW, 0X746DBD,0XD6BFE3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cosmaw"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.TOUCAN, 0XF58F33,0X1E2133, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_toucan"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.MANED_WOLF, 0XBB7A47,0X40271A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_maned_wolf"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ANACONDA, 0X565C22,0XD3763F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_anaconda"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ANTEATER, 0X4C3F3A, 0XCCBCB4, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_anteater"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.ROCKY_ROLLER, 0XB0856F, 0X999184, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_rocky_roller"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.FLUTTER, 0X70922D, 0XD07BE3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_flutter"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.GELADA_MONKEY, 0XB08C64, 0XFF4F53, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gelada_monkey"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.JERBOA, 0XDEC58A, 0XDE9D90, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_jerboa"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.TERRAPIN, 0X6E6E30, 0X929647, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_terrapin"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.COMB_JELLY, 0XCFE9FE, 0X6EFF8B, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_comb_jelly"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.COSMIC_COD, 0X6985C7, 0XE2D1FF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cosmic_cod"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.BUNFUNGUS, 0X6F6D91, 0XC92B29, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bunfungus"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.BISON, 0X4C3A2E, 0X7A6546, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bison"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.GIANT_SQUID, 0XAB4B4D, 0XD67D6B, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_giant_squid"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.DEVILS_HOLE_PUPFISH, 0X567BC4, 0X6C4475, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_devils_hole_pupfish"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.CATFISH, 0X807757, 0X8A7466, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_catfish"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.FLYING_FISH, 0X7BBCED, 0X6881B3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_flying_fish"));
        event.getRegistry().register(new ForgeSpawnEggItem(AMEntityRegistry.SKELEWAG, 0XD9FCB1, 0X3A4F30, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_skelewag"));
        event.getRegistry().register(new BannerPatternItem(PATTERN_BEAR, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_bear"));
        event.getRegistry().register(new BannerPatternItem(PATTER_AUSTRALIA_0, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_australia_0"));
        event.getRegistry().register(new BannerPatternItem(PATTER_AUSTRALIA_1, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_australia_1"));
        event.getRegistry().register(new BannerPatternItem(PATTERN_NEW_MEXICO, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_new_mexico"));
        event.getRegistry().register(new BannerPatternItem(PATTERN_BRAZIL, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_brazil"));
        CROCODILE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(CROCODILE_SCUTE.get()));
        ROADRUNNER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(ROADRUNNER_FEATHER.get()));
        CENTIPEDE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(CENTIPEDE_LEG.get()));
        MOOSE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(MOOSE_ANTLER.get()));
        RACCOON_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(RACCOON_TAIL.get()));
        SOMBRERO_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.HAY_BLOCK));
        SPIKED_TURTLE_SHELL_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(SPIKED_SCUTE.get()));
        FEDORA_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.LEATHER));
        EMU_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(EMU_FEATHER.get()));
        ROCKY_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(ROCKY_SHELL.get()));
        FLYING_FISH_MATERIAL.setRepairMaterial(Ingredient.of(FLYING_FISH.get()));
        DispenserBlock.registerBehavior(SHARK_TOOTH_ARROW.get(), new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntitySharkToothArrow entityarrow = new EntitySharkToothArrow(AMEntityRegistry.SHARK_TOOTH_ARROW.get(), position.x(), position.y(), position.z(), worldIn);
                entityarrow.pickup = EntitySharkToothArrow.Pickup.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(ANCIENT_DART.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityTossedItem tossedItem = new EntityTossedItem(worldIn, position.x(), position.y(), position.z());
                tossedItem.setDart(true);
                return tossedItem;
            }
        });
        DispenserBlock.registerBehavior(COCKROACH_OOTHECA.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityCockroachEgg entityarrow = new EntityCockroachEgg(worldIn, position.x(), position.y(), position.z());
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(EMU_EGG.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityEmuEgg entityarrow = new EntityEmuEgg(worldIn, position.x(), position.y(), position.z());
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(ENDERIOPHAGE_ROCKET.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityEnderiophageRocket entityarrow = new EntityEnderiophageRocket(worldIn, position.x(), position.y(), position.z(), stackIn);
                return entityarrow;
            }
        });
        AMBlockRegistry.DEF_REG.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(block -> event.getRegistry().register(registerItemBlock(block)));
        ComposterBlock.COMPOSTABLES.put(BANANA.get(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(AMBlockRegistry.BANANA_PEEL.get().asItem(), 1F);
        ComposterBlock.COMPOSTABLES.put(ACACIA_BLOSSOM.get(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(GONGYLIDIA.get(), 0.9F);
    }

    private static Item registerItemBlock(Block block) {
        Item.Properties props = new Item.Properties();
        if(block != AMBlockRegistry.SAND_CIRCLE.get() && block != AMBlockRegistry.RED_SAND_CIRCLE.get()){
            props.tab(AlexsMobs.TAB);
        }
        if(block == AMBlockRegistry.STRADDLITE_BLOCK.get()){
            props.fireResistant();
        }
        final BlockItem blockItem;
        if(block instanceof AMSpecialRenderBlock){
            blockItem = new BlockItemAMRender((Block) block, props);
        }else{
            blockItem = new BlockItem((Block) block, props);
        }
        blockItem.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return blockItem;
    }

    @SubscribeEvent
    public static void registerPaintings(RegistryEvent.Register<Motive> event) {
        event.getRegistry().register(new Motive(32, 32).setRegistryName("alexsmobs:nft"));
        event.getRegistry().register(new Motive(32, 16).setRegistryName("alexsmobs:dog_poker"));
    }
}
