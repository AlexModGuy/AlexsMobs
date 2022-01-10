package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

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

    public static final Item TAB_ICON = new ItemTabIcon(new Item.Properties()).setRegistryName("alexsmobs:tab_icon");
    public static final Item ANIMAL_DICTIONARY = new ItemAnimalDictionary(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)).setRegistryName("alexsmobs:animal_dictionary");
    public static final Item BEAR_FUR = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:bear_fur");
    public static final Item BEAR_DUST = new ItemBearDust(new Item.Properties().rarity(Rarity.EPIC)).setRegistryName("alexsmobs:bear_dust");
    public static final Item ROADRUNNER_FEATHER = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:roadrunner_feather");
    public static final Item ROADDRUNNER_BOOTS = new ItemModArmor(ROADRUNNER_ARMOR_MATERIAL, EquipmentSlot.FEET).setRegistryName("alexsmobs:roadrunner_boots");
    public static final Item LAVA_BOTTLE = new Item(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)).setRegistryName("alexsmobs:lava_bottle");
    public static final Item BONE_SERPENT_TOOTH = new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()).setRegistryName("alexsmobs:bone_serpent_tooth");
    public static final Item GAZELLE_HORN = new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()).setRegistryName("alexsmobs:gazelle_horn");
    public static final Item CROCODILE_SCUTE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:crocodile_scute");
    public static final Item CROCODILE_CHESTPLATE = new ItemModArmor(CROCODILE_ARMOR_MATERIAL, EquipmentSlot.CHEST).setRegistryName("alexsmobs:crocodile_chestplate");
    public static final Item MAGGOT = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).build())).setRegistryName("alexsmobs:maggot");
    public static final Item BANANA = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build())).setRegistryName("alexsmobs:banana");
    public static final Item ANCIENT_DART = new Item(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1).rarity(Rarity.UNCOMMON)).setRegistryName("alexsmobs:ancient_dart");
    public static final Item HALO = new Item(new Item.Properties()).setRegistryName("alexsmobs:halo");
    public static final Item BLOOD_SAC = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:blood_sac");
    public static final Item MOSQUITO_PROBOSCIS = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:mosquito_proboscis");
    public static final Item BLOOD_SPRAYER = new ItemBloodSprayer(new Item.Properties().tab(AlexsMobs.TAB).durability(100)).setRegistryName("alexsmobs:blood_sprayer");
    public static final Item RATTLESNAKE_RATTLE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:rattlesnake_rattle");
    public static final Item CHORUS_ON_A_STICK = new Item(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)).setRegistryName("alexsmobs:chorus_on_a_stick");
    public static final Item SHARK_TOOTH = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:shark_tooth");
    public static final Item SHARK_TOOTH_ARROW = new ItemModArrow(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:shark_tooth_arrow");
    public static final Item LOBSTER_TAIL = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).meat().build())).setRegistryName("alexsmobs:lobster_tail");
    public static final Item COOKED_LOBSTER_TAIL = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(6).saturationMod(0.65F).meat().build())).setRegistryName("alexsmobs:cooked_lobster_tail");
    public static final Item LOBSTER_BUCKET = new ItemModFishBucket(AMEntityRegistry.LOBSTER, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:lobster_bucket");
    public static final Item KOMODO_SPIT = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:komodo_spit");
    public static final Item KOMODO_SPIT_BOTTLE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:komodo_spit_bottle");
    public static final Item POISON_BOTTLE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:poison_bottle");
    public static final Item SOPA_DE_MACACO = new BowlFoodItem(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(5).saturationMod(0.4F).meat().build()).stacksTo(1)).setRegistryName("alexsmobs:sopa_de_macaco");
    public static final Item CENTIPEDE_LEG = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:centipede_leg");
    public static final Item CENTIPEDE_LEGGINGS = new ItemModArmor(CENTIPEDE_ARMOR_MATERIAL, EquipmentSlot.LEGS).setRegistryName("alexsmobs:centipede_leggings");
    public static final Item MOSQUITO_LARVA = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:mosquito_larva");
    public static final Item MOOSE_ANTLER = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:moose_antler");
    public static final Item MOOSE_HEADGEAR = new ItemModArmor(MOOSE_ARMOR_MATERIAL, EquipmentSlot.HEAD).setRegistryName("alexsmobs:moose_headgear");
    public static final Item MOOSE_RIBS = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(3).saturationMod(0.6F).meat().build())).setRegistryName("alexsmobs:moose_ribs");
    public static final Item COOKED_MOOSE_RIBS = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(7).saturationMod(0.85F).meat().build())).setRegistryName("alexsmobs:cooked_moose_ribs");
    public static final Item MIMICREAM = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:mimicream");
    public static final Item RACCOON_TAIL = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:raccoon_tail");
    public static final Item FRONTIER_CAP = new ItemModArmor(RACCOON_ARMOR_MATERIAL, EquipmentSlot.HEAD).setRegistryName("alexsmobs:frontier_cap");
    public static final Item BLOBFISH = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).meat().effect(new MobEffectInstance(MobEffects.POISON, 120, 0), 1F).build())).setRegistryName("alexsmobs:blobfish");
    public static final Item BLOBFISH_BUCKET = new ItemModFishBucket(AMEntityRegistry.BLOBFISH, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:blobfish_bucket");
    public static final Item FISH_OIL = new ItemFishOil(new Item.Properties().tab(AlexsMobs.TAB).craftRemainder(Items.GLASS_BOTTLE).food(new FoodProperties.Builder().nutrition(0).saturationMod(0.2F).build())).setRegistryName("alexsmobs:fish_oil");
    public static final Item MARACA = new ItemMaraca(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:maraca");
    public static final Item SOMBRERO = new ItemModArmor(SOMBRERO_ARMOR_MATERIAL, EquipmentSlot.HEAD).setRegistryName("alexsmobs:sombrero");
    public static final Item COCKROACH_WING_FRAGMENT = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:cockroach_wing_fragment");
    public static final Item COCKROACH_WING = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:cockroach_wing");
    public static final Item COCKROACH_OOTHECA = new ItemAnimalEgg(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:cockroach_ootheca");
    public static final Item ACACIA_BLOSSOM = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:acacia_blossom");
    public static final Item SOUL_HEART = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:soul_heart");
    public static final Item SPIKED_SCUTE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spiked_scute");
    public static final Item SPIKED_TURTLE_SHELL = new ItemModArmor(SPIKED_TURTLE_SHELL_ARMOR_MATERIAL, EquipmentSlot.HEAD).setRegistryName("alexsmobs:spiked_turtle_shell");
    public static final Item SHRIMP_FRIED_RICE = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(12).saturationMod(1F).build())).setRegistryName("alexsmobs:shrimp_fried_rice");
    public static final Item GUSTER_EYE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:guster_eye");
    public static final Item POCKET_SAND = new ItemPocketSand(new Item.Properties().tab(AlexsMobs.TAB).durability(220)).setRegistryName("alexsmobs:pocket_sand");
    public static final Item WARPED_MUSCLE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:warped_muscle");
    public static final Item HEMOLYMPH_SAC = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:hemolymph_sac");
    public static final Item HEMOLYMPH_BLASTER = new ItemHemolymphBlaster(new Item.Properties().tab(AlexsMobs.TAB).durability(150)).setRegistryName("alexsmobs:hemolymph_blaster");
    public static final Item WARPED_MIXTURE = new Item(new Item.Properties().tab(AlexsMobs.TAB).rarity(Rarity.RARE).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE)).setRegistryName("alexsmobs:warped_mixture");
    public static final Item STRADDLITE = new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()).setRegistryName("alexsmobs:straddlite");
    public static final Item STRADPOLE_BUCKET = new ItemModFishBucket(AMEntityRegistry.STRADPOLE, Fluids.LAVA, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:stradpole_bucket");
    public static final Item STRADDLEBOARD = new ItemStraddleboard(new Item.Properties().tab(AlexsMobs.TAB).fireResistant().durability(220)).setRegistryName("alexsmobs:straddleboard");
    public static final Item EMU_EGG = new ItemAnimalEgg(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(8)).setRegistryName("alexsmobs:emu_egg");
    public static final Item BOILED_EMU_EGG = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(1F).meat().build())).setRegistryName("alexsmobs:boiled_emu_egg");
    public static final Item EMU_FEATHER = new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()).setRegistryName("alexsmobs:emu_feather");
    public static final Item EMU_LEGGINGS = new ItemModArmor(EMU_ARMOR_MATERIAL, EquipmentSlot.LEGS).setRegistryName("alexsmobs:emu_leggings");
    public static final Item PLATYPUS_BUCKET = new ItemModFishBucket(AMEntityRegistry.PLATYPUS, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:platypus_bucket");
    public static final Item FEDORA = new ItemModArmor(FEDORA_ARMOR_MATERIAL, EquipmentSlot.HEAD).setRegistryName("alexsmobs:fedora");
    public static final Item DROPBEAR_CLAW = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:dropbear_claw");
    public static final Item KANGAROO_MEAT = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).meat().build())).setRegistryName("alexsmobs:kangaroo_meat");
    public static final Item COOKED_KANGAROO_MEAT = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(8).saturationMod(0.85F).meat().build())).setRegistryName("alexsmobs:cooked_kangaroo_meat");
    public static final Item KANGAROO_HIDE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:kangaroo_hide");
    public static final Item KANGAROO_BURGER = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(12).saturationMod(1F).meat().build())).setRegistryName("alexsmobs:kangaroo_burger");
    public static final Item AMBERGRIS = new ItemFuel(new Item.Properties().tab(AlexsMobs.TAB), 12800).setRegistryName("alexsmobs:ambergris");
    public static final Item CACHALOT_WHALE_TOOTH = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:cachalot_whale_tooth");
    public static final Item ECHOLOCATOR = new ItemEcholocator(new Item.Properties().tab(AlexsMobs.TAB).durability(100), false).setRegistryName("alexsmobs:echolocator");
    public static final Item ENDOLOCATOR = new ItemEcholocator(new Item.Properties().tab(AlexsMobs.TAB).durability(25), true).setRegistryName("alexsmobs:endolocator");
    public static final Item GONGYLIDIA = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(3).saturationMod(1.2F).build())).setRegistryName("alexsmobs:gongylidia");
    public static final Item LEAFCUTTER_ANT_PUPA = new ItemLeafcutterPupa(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:leafcutter_ant_pupa");
    public static final Item ENDERIOPHAGE_ROCKET = new ItemEnderiophageRocket(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:enderiophage_rocket");
    public static final Item FALCONRY_GLOVE_INVENTORY = new Item(new Item.Properties()).setRegistryName("alexsmobs:falconry_glove_inventory");
    public static final Item FALCONRY_GLOVE_HAND = new Item(new Item.Properties()).setRegistryName("alexsmobs:falconry_glove_hand");
    public static final Item FALCONRY_GLOVE = new ItemFalconryGlove(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)).setRegistryName("alexsmobs:falconry_glove");
    public static final Item FALCONRY_HOOD = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:falconry_hood");
    public static final Item TARANTULA_HAWK_WING_FRAGMENT = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:tarantula_hawk_wing_fragment");
    public static final Item TARANTULA_HAWK_WING = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:tarantula_hawk_wing");
    public static final Item TARANTULA_HAWK_ELYTRA = new ItemTarantulaHawkElytra(new Item.Properties().tab(AlexsMobs.TAB).durability(800).rarity(Rarity.UNCOMMON), TARANTULA_HAWK_ELYTRA_MATERIAL).setRegistryName("alexsmobs:tarantula_hawk_elytra");
    public static final Item MYSTERIOUS_WORM = new ItemMysteriousWorm(new Item.Properties().tab(AlexsMobs.TAB).rarity(Rarity.RARE)).setRegistryName("alexsmobs:mysterious_worm");
    public static final Item VOID_WORM_MANDIBLE = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:void_worm_mandible");
    public static final Item VOID_WORM_EYE = new Item(new Item.Properties().tab(AlexsMobs.TAB).rarity(Rarity.RARE)).setRegistryName("alexsmobs:void_worm_eye");
    public static final Item DIMENSIONAL_CARVER = new ItemDimensionalCarver(new Item.Properties().tab(AlexsMobs.TAB).durability(20).rarity(Rarity.EPIC)).setRegistryName("alexsmobs:dimensional_carver");
    public static final Item SERRATED_SHARK_TOOTH = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:serrated_shark_tooth");
    public static final Item FRILLED_SHARK_BUCKET = new ItemModFishBucket(AMEntityRegistry.FRILLED_SHARK, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:frilled_shark_bucket");
    public static final Item SHIELD_OF_THE_DEEP = new ItemShieldOfTheDeep(new Item.Properties().durability(400).rarity(Rarity.UNCOMMON).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:shield_of_the_deep");
    public static final Item MIMIC_OCTOPUS_BUCKET = new ItemModFishBucket(AMEntityRegistry.MIMIC_OCTOPUS, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:mimic_octopus_bucket");
    public static final Item FROSTSTALKER_HORN = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:froststalker_horn");
    public static final Item FROSTSTALKER_HELMET = new ItemModArmor(FROSTSTALKER_ARMOR_MATERIAL, EquipmentSlot.HEAD).setRegistryName("alexsmobs:froststalker_helmet");
    public static final Item PIGSHOES = new ItemPigshoes(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)).setRegistryName("alexsmobs:pigshoes");
    public static final Item STRADDLE_HELMET = new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()).setRegistryName("alexsmobs:straddle_helmet");
    public static final Item STRADDLE_SADDLE = new Item(new Item.Properties().tab(AlexsMobs.TAB).fireResistant()).setRegistryName("alexsmobs:straddle_saddle");
    public static final Item COSMIC_COD = new Item(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(6).saturationMod(0.3F).effect(new MobEffectInstance(AMEffectRegistry.ENDER_FLU, 12000), 0.15F).build())).setRegistryName("alexsmobs:cosmic_cod");
    public static final Item SHED_SNAKE_SKIN = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:shed_snake_skin");
    public static final Item VINE_LASSO_INVENTORY = new Item(new Item.Properties()).setRegistryName("alexsmobs:vine_lasso_inventory");
    public static final Item VINE_LASSO_HAND = new Item(new Item.Properties()).setRegistryName("alexsmobs:vine_lasso_hand");
    public static final Item VINE_LASSO = new ItemVineLasso(new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1)).setRegistryName("alexsmobs:vine_lasso");
    public static final Item ROCKY_SHELL = new Item(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:rocky_shell");
    public static final Item ROCKY_CHESTPLATE = new ItemModArmor(ROCKY_ARMOR_MATERIAL, EquipmentSlot.CHEST).setRegistryName("alexsmobs:rocky_chestplate");
    public static final Item POTTED_FLUTTER = new ItemFlutterPot(new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:potted_flutter");
    public static final Item TERRAPIN_BUCKET = new ItemModFishBucket(AMEntityRegistry.TERRAPIN, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:terrapin_bucket");
    public static final Item COMB_JELLY_BUCKET = new ItemModFishBucket(AMEntityRegistry.COMB_JELLY, Fluids.WATER, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:comb_jelly_bucket");
    public static final Item RAINBOW_JELLY = new ItemRainbowJelly(new Item.Properties().tab(AlexsMobs.TAB).food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2F).build())).setRegistryName("alexsmobs:rainbow_jelly");
    public static final Item MUSIC_DISC_THIME = new RecordItem(14, AMSoundRegistry.MUSIC_DISC_THIME, new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1).rarity(Rarity.RARE)).setRegistryName("alexsmobs:music_disc_thime");
    public static final Item MUSIC_DISC_DAZE = new RecordItem(14, AMSoundRegistry.MUSIC_DISC_DAZE, new Item.Properties().tab(AlexsMobs.TAB).stacksTo(1).rarity(Rarity.RARE)).setRegistryName("alexsmobs:music_disc_daze");

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
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GRIZZLY_BEAR, 0X693A2C, 0X976144, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_grizzly_bear"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ROADRUNNER, 0X3A2E26, 0XFBE9CE, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_roadrunner"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.BONE_SERPENT, 0XE5D9C4, 0XFF6038, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bone_serpent"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GAZELLE, 0XDDA675,0X2C2925, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gazelle"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CROCODILE, 0X738940,0XA6A15E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crocodile"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.FLY, 0X464241,0X892E2E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_fly"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.HUMMINGBIRD, 0X325E7F,0X44A75F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_hummingbird"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ORCA, 0X2C2C2C,0XD6D8E4, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_orca"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SUNBIRD, 0XF6694F,0XFFDDA0, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_sunbird"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GORILLA, 0X595B5D,0X1C1C21, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gorilla"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CRIMSON_MOSQUITO, 0X53403F,0XC11A1A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crimson_mosquito"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.RATTLESNAKE, 0XCEB994,0X937A5B, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_rattlesnake"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ENDERGRADE, 0XE6E6A4,0XB29BDD, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_endergrade"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.HAMMERHEAD_SHARK, 0X8A92B5,0XB9BED8, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_hammerhead_shark"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.LOBSTER, 0XC43123,0XDD5F38, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_lobster"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.KOMODO_DRAGON, 0X746C4F,0X564231, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_komodo_dragon"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CAPUCHIN_MONKEY, 0X25211F,0XF1DAB3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_capuchin_monkey"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CENTIPEDE_HEAD, 0X342B2E,0X733449, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_centipede"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.WARPED_TOAD, 0X1F968E,0XFEAC6D, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_warped_toad"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MOOSE, 0X36302A,0XD4B183, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_moose"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MIMICUBE, 0X8A80C1,0X5E4F6F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mimicube"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.RACCOON, 0X85827E,0X2A2726, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_raccoon"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.BLOBFISH, 0XDBC6BD,0X9E7A7F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_blobfish"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SEAL, 0X483C32,0X66594C, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_seal"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.COCKROACH, 0X0D0909,0X42241E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cockroach"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SHOEBILL, 0X828282,0XD5B48A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_shoebill"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ELEPHANT, 0X8D8987,0XEDE5D1, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_elephant"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SOUL_VULTURE, 0X23262D,0X57F4FF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_soul_vulture"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SNOW_LEOPARD, 0XACA293,0X26201D, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_snow_leopard"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SPECTRE, 0XC8D0EF,0X8791EF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_spectre"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CROW, 0X0D111C,0X1C2030, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crow"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, 0X6C5C52,0X456926, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_alligator_snapping_turtle"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MUNGUS, 0X836A8D,0X45454C, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mungus"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MANTIS_SHRIMP, 0XDB4858,0X15991E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mantis_shrimp"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GUSTER, 0XF8D49A,0XFF720A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_guster"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.WARPED_MOSCO, 0X322F58,0X5B5EF1, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_warped_mosco"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.STRADDLER, 0X5D5F6E,0XCDA886, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_straddler"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.STRADPOLE, 0X5D5F6E,0X576A8B, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_stradpole"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.EMU, 0X665346,0X3B3938, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_emu"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.PLATYPUS, 0X7D503E,0X363B43, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_platypus"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.DROPBEAR, 0X8A2D35,0X60A3A3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_dropbear"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.TASMANIAN_DEVIL, 0X252426,0XA8B4BF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tasmanian_devil"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.KANGAROO, 0XCE9D65,0XDEBDA0, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_kangaroo"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CACHALOT_WHALE, 0X949899,0X5F666E, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cachalot_whale"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.LEAFCUTTER_ANT, 0X964023,0XA65930, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_leafcutter_ant"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ENDERIOPHAGE, 0X872D83,0XF6E2CD, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_enderiophage"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.BALD_EAGLE, 0X321F18,0XF4F4F4, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bald_eagle"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.TIGER, 0XC7612E,0X2A3233, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tiger"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.TARANTULA_HAWK, 0X234763,0XE37B38, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tarantula_hawk"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.VOID_WORM, 0X0F1026,0X1699AB, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_void_worm"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.FRILLED_SHARK, 0X726B6B,0X873D3D, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_frilled_shark"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MIMIC_OCTOPUS, 0XFFEBDC,0X1D1C1F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mimic_octopus"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SEAGULL, 0XC9D2DC,0XFFD850, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_seagull"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.FROSTSTALKER, 0X788AC1,0XA1C3FF, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_froststalker"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.TUSKLIN, 0X735841,0XE8E2D5, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_tusklin"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.LAVIATHAN, 0XD68356,0X3C3947, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_laviathan"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.COSMAW, 0X746DBD,0XD6BFE3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cosmaw"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.TOUCAN, 0XF58F33,0X1E2133, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_toucan"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MANED_WOLF, 0XBB7A47,0X40271A, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_maned_wolf"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ANACONDA, 0X565C22,0XD3763F, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_anaconda"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ANTEATER, 0X4C3F3A, 0XCCBCB4, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_anteater"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ROCKY_ROLLER, 0XB0856F, 0X999184, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_rocky_roller"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.FLUTTER, 0X70922D, 0XD07BE3, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_flutter"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GELADA_MONKEY, 0XB08C64, 0XFF4F53, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gelada_monkey"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.JERBOA, 0XDEC58A, 0XDE9D90, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_jerboa"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.TERRAPIN, 0X6E6E30, 0X929647, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_terrapin"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.COMB_JELLY, 0XCFE9FE, 0X6EFF8B, new Item.Properties().tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_comb_jelly"));
        try {
            for (Field f : AMItemRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Item) {
                    event.getRegistry().register((Item) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            for (Field f : AMBlockRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Block) {
                    Item.Properties props = new Item.Properties();
                    props.tab(AlexsMobs.TAB);
                    if(obj == AMBlockRegistry.STRADDLITE_BLOCK){
                        props.fireResistant();
                    }
                    BlockItem blockItem = new BlockItem((Block) obj, props);
                    blockItem.setRegistryName(((Block) obj).getRegistryName());
                    event.getRegistry().register(blockItem);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        event.getRegistry().register(new BannerPatternItem(PATTERN_BEAR, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_bear"));
        event.getRegistry().register(new BannerPatternItem(PATTER_AUSTRALIA_0, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_australia_0"));
        event.getRegistry().register(new BannerPatternItem(PATTER_AUSTRALIA_1, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_australia_1"));
        event.getRegistry().register(new BannerPatternItem(PATTERN_NEW_MEXICO, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_new_mexico"));
        event.getRegistry().register(new BannerPatternItem(PATTERN_BRAZIL, (new Item.Properties()).stacksTo(1).tab(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_brazil"));
        CROCODILE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(CROCODILE_SCUTE));
        ROADRUNNER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(ROADRUNNER_FEATHER));
        CENTIPEDE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(CENTIPEDE_LEG));
        MOOSE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(MOOSE_ANTLER));
        RACCOON_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(RACCOON_TAIL));
        SOMBRERO_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.HAY_BLOCK));
        SPIKED_TURTLE_SHELL_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(SPIKED_SCUTE));
        FEDORA_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.LEATHER));
        EMU_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(EMU_FEATHER));
        DispenserBlock.registerBehavior(SHARK_TOOTH_ARROW, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntitySharkToothArrow entityarrow = new EntitySharkToothArrow(AMEntityRegistry.SHARK_TOOTH_ARROW, position.x(), position.y(), position.z(), worldIn);
                entityarrow.pickup = EntitySharkToothArrow.Pickup.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(ANCIENT_DART, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityTossedItem tossedItem = new EntityTossedItem(worldIn, position.x(), position.y(), position.z());
                tossedItem.setDart(true);
                return tossedItem;
            }
        });
        DispenserBlock.registerBehavior(COCKROACH_OOTHECA, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityCockroachEgg entityarrow = new EntityCockroachEgg(worldIn, position.x(), position.y(), position.z());
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(EMU_EGG, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityEmuEgg entityarrow = new EntityEmuEgg(worldIn, position.x(), position.y(), position.z());
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(ENDERIOPHAGE_ROCKET, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityEnderiophageRocket entityarrow = new EntityEnderiophageRocket(worldIn, position.x(), position.y(), position.z(), stackIn);
                return entityarrow;
            }
        });
        ComposterBlock.COMPOSTABLES.put(BANANA, 0.65F);
        ComposterBlock.COMPOSTABLES.put(AMBlockRegistry.BANANA_PEEL.asItem(), 1F);
        ComposterBlock.COMPOSTABLES.put(ACACIA_BLOSSOM, 0.65F);
        ComposterBlock.COMPOSTABLES.put(GONGYLIDIA, 0.9F);
    }

    @SubscribeEvent
    public static void registerPaintings(RegistryEvent.Register<Motive> event) {
        event.getRegistry().register(new Motive(32, 32).setRegistryName("alexsmobs:nft"));
        event.getRegistry().register(new Motive(32, 16).setRegistryName("alexsmobs:dog_poker"));
    }
}
