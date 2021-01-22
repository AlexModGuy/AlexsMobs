package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroachEgg;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodileEgg;
import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMItemRegistry {
    public static CustomArmorMaterial ROADRUNNER_ARMOR_MATERIAL = new AMArmorMaterial("roadrunner", 18, new int[]{3, 3, 3, 3}, 20, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0);
    public static CustomArmorMaterial CROCODILE_ARMOR_MATERIAL = new AMArmorMaterial("crocodile", 22, new int[]{2, 5, 7, 3}, 25, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 1);
    public static CustomArmorMaterial CENTIPEDE_ARMOR_MATERIAL = new AMArmorMaterial("centipede", 20, new int[]{6, 6, 6, 6}, 22, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0.5F);
    public static CustomArmorMaterial MOOSE_ARMOR_MATERIAL = new AMArmorMaterial("moose", 19, new int[]{5, 5, 5, 5}, 21, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0.5F);
    public static CustomArmorMaterial RACCOON_ARMOR_MATERIAL = new AMArmorMaterial("raccoon", 17, new int[]{3, 3, 3, 3}, 21, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 2.5F);
    public static CustomArmorMaterial SOMBRERO_ARMOR_MATERIAL = new AMArmorMaterial("sombrero", 14, new int[]{2, 2, 2, 2}, 30, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.5F);

    public static final Item TAB_ICON = new ItemTabIcon(AlexsMobs.PROXY.setupISTER(new Item.Properties())).setRegistryName("alexsmobs:tab_icon");
    public static final Item ANIMAL_DICTIONARY = new ItemAnimalDictionary(new Item.Properties().group(AlexsMobs.TAB).maxStackSize(1)).setRegistryName("alexsmobs:animal_dictionary");
    public static final Item BEAR_FUR = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:bear_fur");
    public static final Item ROADRUNNER_FEATHER = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:roadrunner_feather");
    public static final Item ROADDRUNNER_BOOTS = new ItemModArmor(ROADRUNNER_ARMOR_MATERIAL, EquipmentSlotType.FEET).setRegistryName("alexsmobs:roadrunner_boots");
    public static final Item LAVA_BOTTLE = new Item(new Item.Properties().group(AlexsMobs.TAB).maxStackSize(1)).setRegistryName("alexsmobs:lava_bottle");
    public static final Item BONE_SERPENT_TOOTH = new Item(new Item.Properties().group(AlexsMobs.TAB).isImmuneToFire()).setRegistryName("alexsmobs:bone_serpent_tooth");
    public static final Item GAZELLE_HORN = new Item(new Item.Properties().group(AlexsMobs.TAB).isImmuneToFire()).setRegistryName("alexsmobs:gazelle_horn");
    public static final Item CROCODILE_SCUTE = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:crocodile_scute");
    public static final Item CROCODILE_CHESTPLATE = new ItemModArmor(CROCODILE_ARMOR_MATERIAL, EquipmentSlotType.CHEST).setRegistryName("alexsmobs:crocodile_chestplate");
    public static final Item CROCODILE_EGG = new ItemAnimalEgg(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:crocodile_egg");
    public static final Item MAGGOT = new Item(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(1).saturation(0.2F).build())).setRegistryName("alexsmobs:maggot");
    public static final Item BANANA = new Item(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(4).saturation(0.3F).build())).setRegistryName("alexsmobs:banana");
    public static final Item BANANA_PEEL = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:banana_peel");
    public static final Item HALO = new Item(new Item.Properties()).setRegistryName("alexsmobs:halo");
    public static final Item BLOOD_SAC = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:blood_sac");
    public static final Item MOSQUITO_PROBOSCIS = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:mosquito_proboscis");
    public static final Item BLOOD_SPRAYER = new ItemBloodSprayer(new Item.Properties().group(AlexsMobs.TAB).maxDamage(100)).setRegistryName("alexsmobs:blood_sprayer");
    public static final Item RATTLESNAKE_RATTLE = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:rattlesnake_rattle");
    public static final Item CHORUS_ON_A_STICK = new Item(new Item.Properties().group(AlexsMobs.TAB).maxStackSize(1)).setRegistryName("alexsmobs:chorus_on_a_stick");
    public static final Item SHARK_TOOTH = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:shark_tooth");
    public static final Item SHARK_TOOTH_ARROW = new ItemModArrow(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:shark_tooth_arrow");
    public static final Item LOBSTER_TAIL = new Item(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(2).saturation(0.4F).meat().build())).setRegistryName("alexsmobs:lobster_tail");
    public static final Item COOKED_LOBSTER_TAIL = new Item(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(6).saturation(0.65F).meat().build())).setRegistryName("alexsmobs:cooked_lobster_tail");
    public static final Item LOBSTER_BUCKET = new ItemModFishBucket(AMEntityRegistry.LOBSTER, Fluids.WATER, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:lobster_bucket");
    public static final Item KOMODO_SPIT = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:komodo_spit");
    public static final Item KOMODO_SPIT_BOTTLE = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:komodo_spit_bottle");
    public static final Item POISON_BOTTLE = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:poison_bottle");
    public static final Item SOPA_DE_MACACO = new SoupItem(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(5).saturation(0.4F).meat().build()).maxStackSize(1)).setRegistryName("alexsmobs:sopa_de_macaco");
    public static final Item CENTIPEDE_LEG = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:centipede_leg");
    public static final Item CENTIPEDE_LEGGINGS = new ItemModArmor(CENTIPEDE_ARMOR_MATERIAL, EquipmentSlotType.LEGS).setRegistryName("alexsmobs:centipede_leggings");
    public static final Item MOSQUITO_LARVA = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:mosquito_larva");
    public static final Item MOOSE_ANTLER = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:moose_antler");
    public static final Item MOOSE_HEADGEAR = new ItemModArmor(MOOSE_ARMOR_MATERIAL, EquipmentSlotType.HEAD).setRegistryName("alexsmobs:moose_headgear");
    public static final Item MIMICREAM = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:mimicream");
    public static final Item RACCOON_TAIL = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:raccoon_tail");
    public static final Item FRONTIER_CAP = new ItemModArmor(RACCOON_ARMOR_MATERIAL, EquipmentSlotType.HEAD).setRegistryName("alexsmobs:frontier_cap");
    public static final Item BLOBFISH = new Item(new Item.Properties().group(AlexsMobs.TAB).food(new Food.Builder().hunger(3).saturation(0.4F).meat().effect(new EffectInstance(Effects.POISON, 120, 0), 1F).build())).setRegistryName("alexsmobs:blobfish");
    public static final Item BLOBFISH_BUCKET = new ItemModFishBucket(AMEntityRegistry.BLOBFISH, Fluids.WATER, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:blobfish_bucket");
    public static final Item FISH_OIL = new ItemFishOil(new Item.Properties().group(AlexsMobs.TAB).containerItem(Items.GLASS_BOTTLE).food(new Food.Builder().hunger(0).saturation(0.2F).effect(new EffectInstance(AMEffectRegistry.OILED, 1200, 0), 1F).build())).setRegistryName("alexsmobs:fish_oil");
    public static final Item MARACA = new ItemMaraca(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:maraca");
    public static final Item SOMBRERO = new ItemModArmor(SOMBRERO_ARMOR_MATERIAL, EquipmentSlotType.HEAD).setRegistryName("alexsmobs:sombrero");
    public static final Item COCKROACH_WING_FRAGMENT = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:cockroach_wing_fragment");
    public static final Item COCKROACH_WING = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:cockroach_wing");
    public static final Item COCKROACH_OOTHECA = new ItemAnimalEgg(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:cockroach_ootheca");
    public static final Item ACACIA_BLOSSOM = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:acacia_blossom");
    public static final Item SOUL_HEART = new Item(new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:soul_heart");


    public static final BannerPattern PATTERN_BEAR = addBanner("bear");

    private static BannerPattern addBanner(String name) {
        return BannerPattern.create(name.toUpperCase(), name, "alexsmobs." + name, true);
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GRIZZLY_BEAR, 0X693A2C, 0X976144, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_grizzly_bear"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ROADRUNNER, 0X3A2E26, 0XFBE9CE, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_roadrunner"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.BONE_SERPENT, 0XE5D9C4, 0XFF6038, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_bone_serpent"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GAZELLE, 0XDDA675,0X2C2925, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gazelle"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CROCODILE, 0X738940,0XA6A15E, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crocodile"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.FLY, 0X464241,0X892E2E, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_fly"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.HUMMINGBIRD, 0X325E7F,0X44A75F, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_hummingbird"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ORCA, 0X2C2C2C,0XD6D8E4, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_orca"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SUNBIRD, 0XF6694F,0XFFDDA0, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_sunbird"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.GORILLA, 0X595B5D,0X1C1C21, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_gorilla"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CRIMSON_MOSQUITO, 0X53403F,0XC11A1A, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_crimson_mosquito"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.RATTLESNAKE, 0XCEB994,0X937A5B, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_rattlesnake"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ENDERGRADE, 0XE6E6A4,0XB29BDD, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_endergrade"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.HAMMERHEAD_SHARK, 0X8A92B5,0XB9BED8, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_hammerhead_shark"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.LOBSTER, 0XC43123,0XDD5F38, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_lobster"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.KOMODO_DRAGON, 0X746C4F,0X564231, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_komodo_dragon"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CAPUCHIN_MONKEY, 0X25211F,0XF1DAB3, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_capuchin_monkey"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.CENTIPEDE_HEAD, 0X342B2E,0X733449, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_centipede"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.WARPED_TOAD, 0X1F968E,0XFEAC6D, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_warped_toad"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MOOSE, 0X36302A,0XD4B183, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_moose"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.MIMICUBE, 0X8A80C1,0X5E4F6F, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_mimicube"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.RACCOON, 0X85827E,0X2A2726, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_raccoon"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.BLOBFISH, 0XDBC6BD,0X9E7A7F, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_blobfish"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SEAL, 0X483C32,0X66594C, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_seal"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.COCKROACH, 0X0D0909,0X42241E, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_cockroach"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SHOEBILL, 0X828282,0XD5B48A, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_shoebill"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.ELEPHANT, 0X8D8987,0XEDE5D1, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_elephant"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SOUL_VULTURE, 0X23262D,0X57F4FF, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_soul_vulture"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SNOW_LEOPARD, 0XACA293,0X26201D, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_snow_leopard"));
        event.getRegistry().register(new SpawnEggItem(AMEntityRegistry.SPECTRE, 0X6D72B9,0X8AFFA1, new Item.Properties().group(AlexsMobs.TAB)).setRegistryName("alexsmobs:spawn_egg_spectre"));
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
        event.getRegistry().register(new BannerPatternItem(PATTERN_BEAR, (new Item.Properties()).maxStackSize(1).group(AlexsMobs.TAB)).setRegistryName("alexsmobs:banner_pattern_bear"));
        CROCODILE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.fromItems(CROCODILE_SCUTE));
        ROADRUNNER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.fromItems(ROADRUNNER_FEATHER));
        CENTIPEDE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.fromItems(CENTIPEDE_LEG));
        MOOSE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.fromItems(MOOSE_ANTLER));
        RACCOON_ARMOR_MATERIAL.setRepairMaterial(Ingredient.fromItems(RACCOON_TAIL));
        SOMBRERO_ARMOR_MATERIAL.setRepairMaterial(Ingredient.fromItems(Items.HAY_BLOCK));
        DispenserBlock.registerDispenseBehavior(SHARK_TOOTH_ARROW, new ProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                EntitySharkToothArrow entityarrow = new EntitySharkToothArrow(AMEntityRegistry.SHARK_TOOTH_ARROW, position.getX(), position.getY(), position.getZ(), worldIn);
                entityarrow.pickupStatus = EntitySharkToothArrow.PickupStatus.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerDispenseBehavior(CROCODILE_EGG, new ProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                EntityCrocodileEgg entityarrow = new EntityCrocodileEgg(worldIn, position.getX(), position.getY(), position.getZ());
                return entityarrow;
            }
        });
        DispenserBlock.registerDispenseBehavior(COCKROACH_OOTHECA, new ProjectileDispenseBehavior() {
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                EntityCockroachEgg entityarrow = new EntityCockroachEgg(worldIn, position.getX(), position.getY(), position.getZ());
                return entityarrow;
            }
        });
    }
}
