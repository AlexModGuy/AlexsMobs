package com.github.alexthe666.alexsmobs.datagen;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.neoforged.neoforge.common.Tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class AMRecipeProvider extends RecipeProvider {
    public AMRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItemRegistry.ANIMAL_DICTIONARY)
            .requires(Items.BOOK)
            .requires(AMTagRegistry.ANIMAL_DICTIONARY_INGREDIENT)
            .requires(Tags.Items.DYES_GREEN)
            .unlockedBy(getHasName(AMItemRegistry.ANIMAL_DICTIONARY), has(Items.BOOK))
            .group("animal_dictionary")
            .save(output);

        oneToOneConversionRecipe(output, AMItemRegistry.BANANA_SLUG_SLIME, AMBlockRegistry.BANANA_SLUG_SLIME_BLOCK, "banana_slug_slime", 9);
        threeByThreePacker(output, RecipeCategory.MISC, AMBlockRegistry.BANANA_SLUG_SLIME_BLOCK, AMItemRegistry.BANANA_SLUG_SLIME);

        bannerPattern(output, AMItemRegistry.BANNER_PATTERN_AUSTRALIA_0, AMItemRegistry.KANGAROO_HIDE);
        bannerPattern(output, AMItemRegistry.BANNER_PATTERN_AUSTRALIA_1, AMItemRegistry.EMU_FEATHER);
        bannerPattern(output, AMItemRegistry.BANNER_PATTERN_BEAR, AMItemRegistry.BEAR_FUR);
        bannerPattern(output, AMItemRegistry.BANNER_PATTERN_BRAZIL, AMItemRegistry.SHED_SNAKE_SKIN);
        bannerPattern(output, AMItemRegistry.BANNER_PATTERN_NEW_MEXICO, AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT);

        oneToOneConversionRecipe(output, AMItemRegistry.BISON_FUR, AMBlockRegistry.BISON_FUR_BLOCK, "bison_fur", 9);
        threeByThreePacker(output, RecipeCategory.MISC, AMBlockRegistry.BISON_FUR_BLOCK, AMItemRegistry.BISON_FUR);
        twoByTwoPacker(output, "bison_fur_to_wool", RecipeCategory.MISC, Items.BROWN_WOOL, AMItemRegistry.BISON_FUR);

        carpet(output, AMBlockRegistry.BISON_CARPET, AMItemRegistry.BISON_FUR);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AMItemRegistry.BLOOD_SPRAYER)
            .group("blood_sprayer")
            .unlockedBy(getHasName(AMItemRegistry.BLOOD_SPRAYER), has(AMItemRegistry.MOSQUITO_PROBOSCIS))
            .pattern("SS ")
            .pattern("BBP")
            .pattern("B  ")
            .define('S', AMItemRegistry.BLOOD_SAC)
            .define('P', AMItemRegistry.MOSQUITO_PROBOSCIS)
            .define('B', Items.NETHER_BRICK)
            .save(output);

        food(output, AMItemRegistry.BOILED_EMU_EGG, AMItemRegistry.EMU_EGG, 0.15f, 200, 600, 100);

        oneToOneConversionRecipe(output, AMItemRegistry.FISH_BONES, Items.BONE_MEAL, "bone_meal", 2);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AMItemRegistry.CENTIPEDE_LEGGINGS)
            .group("centipede_leggings")
            .unlockedBy(getHasName(AMItemRegistry.CENTIPEDE_LEGGINGS), has(AMItemRegistry.CENTIPEDE_LEG))
            .pattern("SSS")
            .pattern("S S")
            .pattern("S S")
            .define('S', AMItemRegistry.CENTIPEDE_LEG)
            .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, AMItemRegistry.CHORUS_ON_A_STICK)
            .requires(Tags.Items.TOOLS_FISHING_ROD)
            .requires(Items.CHORUS_FRUIT)
            .unlockedBy(getHasName(AMItemRegistry.CHORUS_ON_A_STICK), has(Items.CHORUS_FRUIT))
            .group("chorus_on_a_stick")
            .save(output);

        threeByThreePacker(output, RecipeCategory.MISC, AMItemRegistry.COCKROACH_WING, AMItemRegistry.COCKROACH_WING_FRAGMENT);

        food(output, AMItemRegistry.COOKED_CATFISH, AMItemRegistry.RAW_CATFISH, 0.15f, 200, 600, 100);
        food(output, AMItemRegistry.COOKED_KANGAROO_MEAT, AMItemRegistry.KANGAROO_MEAT, 0.15f, 200, 600, 100);
        food(output, AMItemRegistry.COOKED_LOBSTER_TAIL, AMItemRegistry.LOBSTER_TAIL, 0.15f, 200, 600, 100);
        food(output, AMItemRegistry.COOKED_MOOSE_RIBS, AMItemRegistry.MOOSE_RIBS, 0.15f, 200, 600, 100);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AMItemRegistry.CROCODILE_CHESTPLATE)
            .group("crocodile_chestplate")
            .unlockedBy(getHasName(AMItemRegistry.CROCODILE_CHESTPLATE), has(AMItemRegistry.CROCODILE_SCUTE))
            .pattern("S S")
            .pattern("SSS")
            .pattern("SSS")
            .define('S', AMItemRegistry.CROCODILE_SCUTE)
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, AMItemRegistry.DIMENSIONAL_CARVER)
            .group("dimensional_carver")
            .unlockedBy(getHasName(AMItemRegistry.DIMENSIONAL_CARVER), has(AMItemRegistry.VOID_WORM_EYE))
            .pattern("MEM")
            .pattern(" X ")
            .pattern(" X ")
            .define('M', AMItemRegistry.VOID_WORM_MANDIBLE)
            .define('E', AMItemRegistry.VOID_WORM_EYE)
            .define('X', Tags.Items.INGOTS_NETHERITE)
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, AMItemRegistry.ECHOLOCATOR)
            .group("echolocator")
            .unlockedBy(getHasName(AMItemRegistry.ECHOLOCATOR), has(AMItemRegistry.AMBERGRIS))
            .pattern("CPC")
            .pattern("PAP")
            .pattern(" P ")
            .define('A', AMItemRegistry.AMBERGRIS)
            .define('C', AMItemRegistry.CACHALOT_WHALE_TOOTH)
            .define('P', Tags.Items.INGOTS_IRON)
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AMItemRegistry.EMU_LEGGINGS)
            .group("emu_leggings")
            .unlockedBy(getHasName(AMItemRegistry.EMU_LEGGINGS), has(AMItemRegistry.EMU_FEATHER))
            .pattern("FKF")
            .pattern("K K")
            .pattern("F F")
            .define('F', AMItemRegistry.EMU_FEATHER)
            .define('K', AMItemRegistry.KANGAROO_HIDE)
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AMItemRegistry.ENDERIOPHAGE_ROCKET)
            .group("enderiophage_rocket")
            .unlockedBy(getHasName(AMItemRegistry.ENDERIOPHAGE_ROCKET), has(AMBlockRegistry.CAPSID))
            .pattern(" C ")
            .pattern(" I ")
            .pattern("EIE")
            .define('C', AMBlockRegistry.CAPSID)
            .define('E', Tags.Items.END_STONES)
            .define('I', Tags.Items.NUGGETS_IRON)
            .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 1)
            .requires(AMItemRegistry.FISH_BONES)
            .unlockedBy(getHasName(AMItemRegistry.FISH_BONES), has(AMItemRegistry.FISH_BONES))
            .save(output, AlexsMobs.prefix("bonemeal_from_fish_bones"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.ENDOLOCATOR, 1)
            .pattern(" P ")
            .pattern("PAP")
            .pattern(" P ")
            .define('A', AMItemRegistry.ECHOLOCATOR)
            .define('P', Items.ENDER_PEARL)
            .unlockedBy(getHasName(AMItemRegistry.ECHOLOCATOR), has(AMItemRegistry.ECHOLOCATOR))
            .save(output, AlexsMobs.prefix("endolocator"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.FALCONRY_GLOVE, 1)
            .pattern(" L ")
            .pattern("LLL")
            .pattern(" H ")
            .define('H', AMItemRegistry.BEAR_FUR)
            .define('L', Items.LEATHER)
            .unlockedBy(getHasName(AMItemRegistry.BEAR_FUR), has(AMItemRegistry.BEAR_FUR))
            .save(output, AlexsMobs.prefix("falconry_glove"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.FALCONRY_HOOD, 1)
            .pattern(" LF")
            .pattern("L L")
            .define('F', AMItemRegistry.ROADRUNNER_FEATHER)
            .define('L', Items.LEATHER)
            .unlockedBy(getHasName(AMItemRegistry.ROADRUNNER_FEATHER), has(AMItemRegistry.ROADRUNNER_FEATHER))
            .save(output, AlexsMobs.prefix("falconry_hood"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.FALCONRY_HOOD, 1)
            .pattern("FL ")
            .pattern("L L")
            .define('F', AMItemRegistry.ROADRUNNER_FEATHER)
            .define('L', Items.LEATHER)
            .unlockedBy(getHasName(AMItemRegistry.ROADRUNNER_FEATHER), has(AMItemRegistry.ROADRUNNER_FEATHER))
            .save(output, AlexsMobs.prefix("falconry_hood_alt"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItemRegistry.FISH_OIL, 1)
            .requires(Items.GLASS_BOTTLE)
            .requires(AMItemRegistry.BLOBFISH, 4)
            .unlockedBy(getHasName(AMItemRegistry.BLOBFISH), has(AMItemRegistry.BLOBFISH))
            .save(output, AlexsMobs.prefix("fish_oil"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FLINT_AND_STEEL, 1)
            .requires(AMItemRegistry.DROPBEAR_CLAW)
            .requires(Items.FLINT)
            .unlockedBy(getHasName(AMItemRegistry.DROPBEAR_CLAW), has(AMItemRegistry.DROPBEAR_CLAW))
            .save(output, AlexsMobs.prefix("flint_n_steel_dropbear_claw"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.FLYING_FISH_BOOTS, 1)
            .pattern("S S")
            .pattern("F F")
            .define('F', AMItemRegistry.FLYING_FISH)
            .define('S', Tags.Items.STRINGS)
            .unlockedBy(getHasName(AMItemRegistry.FLYING_FISH), has(AMItemRegistry.FLYING_FISH))
            .save(output, AlexsMobs.prefix("flying_fish_boots"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.FRONTIER_CAP, 1)
            .pattern("HHH")
            .pattern("HCH")
            .pattern("  T")
            .define('C', Items.LEATHER_HELMET)
            .define('T', AMItemRegistry.RACCOON_TAIL)
            .define('H', AMItemRegistry.BEAR_FUR)
            .unlockedBy(getHasName(AMItemRegistry.BEAR_FUR), has(AMItemRegistry.BEAR_FUR))
            .save(output, AlexsMobs.prefix("frontier_cap"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.FRONTIER_CAP, 1)
            .pattern("HHH")
            .pattern("HCH")
            .pattern("T  ")
            .define('C', Items.LEATHER_HELMET)
            .define('T', AMItemRegistry.RACCOON_TAIL)
            .define('H', AMItemRegistry.BEAR_FUR)
            .unlockedBy(getHasName(AMItemRegistry.BEAR_FUR), has(AMItemRegistry.BEAR_FUR))
            .save(output, AlexsMobs.prefix("frontier_cap_alt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.FROSTSTALKER_HELMET, 1)
            .pattern(" H ")
            .pattern("III")
            .pattern("I I")
            .define('H', AMItemRegistry.FROSTSTALKER_HORN)
            .define('I', Items.PACKED_ICE)
            .unlockedBy(getHasName(AMItemRegistry.FROSTSTALKER_HORN), has(AMItemRegistry.FROSTSTALKER_HORN))
            .save(output, AlexsMobs.prefix("froststalker_helmet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMBlockRegistry.GUSTMAKER, 1)
            .pattern("SSS")
            .pattern("SES")
            .pattern("SRS")
            .define('E', AMItemRegistry.GUSTER_EYE)
            .define('S', Items.CUT_SANDSTONE)
            .define('R', Items.REDSTONE)
            .unlockedBy(getHasName(AMItemRegistry.GUSTER_EYE), has(AMItemRegistry.GUSTER_EYE))
            .save(output, AlexsMobs.prefix("gustmaker"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.HEMOLYMPH_BLASTER, 1)
            .pattern("SSS")
            .pattern("CBP")
            .pattern("M  ")
            .define('C', AMItemRegistry.MIMICREAM)
            .define('S', AMItemRegistry.HEMOLYMPH_SAC)
            .define('P', AMItemRegistry.MOSQUITO_PROBOSCIS)
            .define('M', AMItemRegistry.WARPED_MUSCLE)
            .define('B', AMItemRegistry.BLOOD_SPRAYER)
            .unlockedBy(getHasName(AMItemRegistry.BLOOD_SPRAYER), has(AMItemRegistry.BLOOD_SPRAYER))
            .save(output, AlexsMobs.prefix("hemolymph_blaster"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMBlockRegistry.HUMMINGBIRD_FEEDER, 1)
            .pattern(" P ")
            .pattern("PGP")
            .pattern("FPF")
            .define('G', Items.GLASS_BOTTLE)
            .define('P', Items.COPPER_INGOT)
            .define('F', Items.SUNFLOWER)
            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
            .save(output, AlexsMobs.prefix("hummingbird_feeder"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.KANGAROO_BURGER, 1)
            .pattern("B")
            .pattern("K")
            .pattern("B")
            .define('K', AMItemRegistry.COOKED_KANGAROO_MEAT)
            .define('B', Items.BREAD)
            .unlockedBy(getHasName(AMItemRegistry.COOKED_KANGAROO_MEAT), has(AMItemRegistry.COOKED_KANGAROO_MEAT))
            .save(output, AlexsMobs.prefix("kangaroo_burger"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LEATHER, 1)
            .requires(AMItemRegistry.KANGAROO_HIDE, 2)
            .unlockedBy(getHasName(AMItemRegistry.KANGAROO_HIDE), has(AMItemRegistry.KANGAROO_HIDE))
            .save(output, AlexsMobs.prefix("kangaroo_hide_to_leather"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItemRegistry.KOMODO_SPIT_BOTTLE, 1)
            .requires(Items.GLASS_BOTTLE)
            .requires(AMItemRegistry.KOMODO_SPIT, 4)
            .unlockedBy(getHasName(AMItemRegistry.KOMODO_SPIT), has(AMItemRegistry.KOMODO_SPIT))
            .save(output, AlexsMobs.prefix("komodo_spit_bottle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SLIME_BALL, 1)
            .requires(AMItemRegistry.KOMODO_SPIT, 3)
            .unlockedBy(getHasName(AMItemRegistry.KOMODO_SPIT), has(AMItemRegistry.KOMODO_SPIT))
            .save(output, AlexsMobs.prefix("komodo_spit_to_slime"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.MARACA, 1)
            .pattern(" P ")
            .pattern("PRP")
            .pattern(" S ")
            .define('R', AMItemRegistry.RATTLESNAKE_RATTLE)
            .define('P', ItemTags.PLANKS)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy(getHasName(AMItemRegistry.RATTLESNAKE_RATTLE), has(AMItemRegistry.RATTLESNAKE_RATTLE))
            .save(output, AlexsMobs.prefix("maraca"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.MOOSE_HEADGEAR, 1)
            .pattern("AIA")
            .pattern("S S")
            .define('S', Tags.Items.STRINGS)
            .define('A', AMItemRegistry.MOOSE_ANTLER)
            .define('I', Tags.Items.INGOTS_IRON)
            .unlockedBy(getHasName(AMItemRegistry.MOOSE_ANTLER), has(AMItemRegistry.MOOSE_ANTLER))
            .save(output, AlexsMobs.prefix("moose_headgear"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItemRegistry.MOSQUITO_LARVA, 1)
            .requires(AMItemRegistry.MAGGOT)
            .requires(AMItemRegistry.MOSQUITO_PROBOSCIS)
            .unlockedBy(getHasName(AMItemRegistry.MOSQUITO_PROBOSCIS), has(AMItemRegistry.MOSQUITO_PROBOSCIS))
            .save(output, AlexsMobs.prefix("mosquito_larva"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItemRegistry.MOSQUITO_REPELLENT_STEW, 1)
            .requires(Items.BOWL)
            .requires(AMBlockRegistry.TRIOPS_EGGS, 2)
            .requires(AMItemRegistry.STINK_BOTTLE)
            .unlockedBy(getHasName(AMBlockRegistry.TRIOPS_EGGS), has(AMBlockRegistry.TRIOPS_EGGS))
            .save(output, AlexsMobs.prefix("mosquito_repellent_stew"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.POCKET_SAND, 1)
            .pattern("SSS")
            .pattern("LEL")
            .pattern("LLL")
            .define('E', AMItemRegistry.GUSTER_EYE)
            .define('S', ItemTags.SAND)
            .define('L', Items.LEATHER)
            .unlockedBy(getHasName(AMItemRegistry.GUSTER_EYE), has(AMItemRegistry.GUSTER_EYE))
            .save(output, AlexsMobs.prefix("pocket_sand"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.PUPFISH_LOCATOR, 1)
            .pattern("SBS")
            .pattern("BAB")
            .pattern("SBS")
            .define('S', Items.SLIME_BALL)
            .define('B', AMItemRegistry.FISH_BONES)
            .define('A', AMItemRegistry.ECHOLOCATOR)
            .unlockedBy(getHasName(AMItemRegistry.ECHOLOCATOR), has(AMItemRegistry.ECHOLOCATOR))
            .save(output, AlexsMobs.prefix("pupfish_locator"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMBlockRegistry.RAINBOW_GLASS, 1)
            .pattern("GGG")
            .pattern("GRG")
            .pattern("GGG")
            .define('R', AMItemRegistry.RAINBOW_JELLY)
            .define('G', Tags.Items.GLASS_BLOCKS)
            .unlockedBy(getHasName(AMItemRegistry.RAINBOW_JELLY), has(AMItemRegistry.RAINBOW_JELLY))
            .save(output, AlexsMobs.prefix("rainbow_glass"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.ROADDRUNNER_BOOTS, 1)
            .pattern("F F")
            .pattern("F F")
            .pattern("SBS")
            .define('S', Items.CHISELED_SANDSTONE)
            .define('F', AMItemRegistry.ROADRUNNER_FEATHER)
            .define('B', Items.LEATHER_BOOTS)
            .unlockedBy(getHasName(AMItemRegistry.ROADRUNNER_FEATHER), has(AMItemRegistry.ROADRUNNER_FEATHER))
            .save(output, AlexsMobs.prefix("roadrunner_boots"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.ROCKY_CHESTPLATE, 1)
            .pattern("S S")
            .pattern("SSS")
            .pattern("SSS")
            .define('S', AMItemRegistry.ROCKY_SHELL)
            .unlockedBy(getHasName(AMItemRegistry.ROCKY_SHELL), has(AMItemRegistry.ROCKY_SHELL))
            .save(output, AlexsMobs.prefix("rocky_chestplate"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMBlockRegistry.SCULK_BOOMER, 1)
            .pattern("FFF")
            .pattern("SSS")
            .pattern("BBB")
            .define('S', AMItemRegistry.SKREECHER_SOUL)
            .define('F', Items.SCULK)
            .define('B', Items.BONE_BLOCK)
            .unlockedBy(getHasName(AMItemRegistry.SKREECHER_SOUL), has(AMItemRegistry.SKREECHER_SOUL))
            .save(output, AlexsMobs.prefix("sculk_boomer"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.SHARK_TOOTH_ARROW, 1)
            .pattern("TSK")
            .define('T', AMItemRegistry.SHARK_TOOTH)
            .define('K', Items.KELP)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy(getHasName(AMItemRegistry.SHARK_TOOTH), has(AMItemRegistry.SHARK_TOOTH))
            .save(output, AlexsMobs.prefix("shark_tooth_arrow"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.SHIELD_OF_THE_DEEP, 1)
            .pattern("SSS")
            .pattern("PHP")
            .pattern("TPT")
            .define('T', AMItemRegistry.SHARK_TOOTH)
            .define('S', AMItemRegistry.SERRATED_SHARK_TOOTH)
            .define('P', Items.PRISMARINE_BRICKS)
            .define('H', Items.HEART_OF_THE_SEA)
            .unlockedBy(getHasName(Items.HEART_OF_THE_SEA), has(Items.HEART_OF_THE_SEA))
            .save(output, AlexsMobs.prefix("shield_of_the_deep"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItemRegistry.SOPA_DE_MACACO, 1)
            .requires(Items.BOWL)
            .requires(AMBlockRegistry.BANANA_PEEL)
            .requires(Items.BROWN_MUSHROOM)
            .requires(Items.BONE)
            .unlockedBy(getHasName(AMBlockRegistry.BANANA_PEEL), has(AMBlockRegistry.BANANA_PEEL))
            .save(output, AlexsMobs.prefix("sopa_de_macaco"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.SPIKED_TURTLE_SHELL, 1)
            .pattern("HHH")
            .pattern("HCH")
            .define('H', AMItemRegistry.SPIKED_SCUTE)
            .define('C', Items.TURTLE_HELMET)
            .unlockedBy(getHasName(AMItemRegistry.SPIKED_SCUTE), has(AMItemRegistry.SPIKED_SCUTE))
            .save(output, AlexsMobs.prefix("spiked_turtle_shell"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.SQUID_GRAPPLE, 1)
            .pattern("TTT")
            .pattern("CBC")
            .pattern(" C ")
            .define('C', Items.COPPER_INGOT)
            .define('T', AMItemRegistry.LOST_TENTACLE)
            .define('B', Items.CROSSBOW)
            .unlockedBy(getHasName(AMItemRegistry.LOST_TENTACLE), has(AMItemRegistry.LOST_TENTACLE))
            .save(output, AlexsMobs.prefix("squid_grapple"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.STINK_RAY, 1)
            .pattern("SS ")
            .pattern("IIH")
            .pattern("I  ")
            .define('S', AMItemRegistry.STINK_BOTTLE)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('H', Items.HOPPER)
            .unlockedBy(getHasName(AMItemRegistry.STINK_BOTTLE), has(AMItemRegistry.STINK_BOTTLE))
            .save(output, AlexsMobs.prefix("stink_ray"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.STRADDLEBOARD, 1)
            .pattern(" SN")
            .pattern("SNS")
            .pattern("NS ")
            .define('N', Tags.Items.INGOTS_NETHERITE)
            .define('S', AMItemRegistry.STRADDLITE)
            .unlockedBy(getHasName(AMItemRegistry.STRADDLITE), has(AMItemRegistry.STRADDLITE))
            .save(output, AlexsMobs.prefix("straddleboard"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.STRADDLE_HELMET, 1)
            .pattern(" S ")
            .pattern("S S")
            .pattern("L L")
            .define('S', AMItemRegistry.STRADDLITE)
            .define('L', Items.STRING)
            .unlockedBy(getHasName(AMItemRegistry.STRADDLITE), has(AMItemRegistry.STRADDLITE))
            .save(output, AlexsMobs.prefix("straddle_helmet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.STRADDLE_SADDLE, 1)
            .pattern("EES")
            .pattern("S  ")
            .define('S', AMItemRegistry.STRADDLITE)
            .define('E', Items.SADDLE)
            .unlockedBy(getHasName(AMItemRegistry.STRADDLITE), has(AMItemRegistry.STRADDLITE))
            .save(output, AlexsMobs.prefix("straddle_saddle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMBlockRegistry.STRADDLITE_BLOCK, 1)
            .pattern("SSS")
            .pattern("SSS")
            .pattern("SSS")
            .define('S', AMItemRegistry.STRADDLITE)
            .unlockedBy(getHasName(AMItemRegistry.STRADDLITE), has(AMItemRegistry.STRADDLITE))
            .save(output, AlexsMobs.prefix("straddlite_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItemRegistry.STRADDLITE, 1)
            .requires(AMBlockRegistry.STRADDLITE_BLOCK)
            .unlockedBy(getHasName(AMBlockRegistry.STRADDLITE_BLOCK), has(AMBlockRegistry.STRADDLITE_BLOCK))
            .save(output, AlexsMobs.prefix("straddlite_from_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.TARANTULA_HAWK_ELYTRA, 1)
            .pattern("WEW")
            .define('W', AMItemRegistry.TARANTULA_HAWK_WING)
            .define('E', Items.ELYTRA)
            .unlockedBy(getHasName(AMItemRegistry.TARANTULA_HAWK_WING), has(AMItemRegistry.TARANTULA_HAWK_WING))
            .save(output, AlexsMobs.prefix("tarantula_hawk_elytra"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.TARANTULA_HAWK_WING, 1)
            .pattern("FFF")
            .pattern("FFF")
            .pattern("FFF")
            .define('F', AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT)
            .unlockedBy(getHasName(AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT), has(AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT))
            .save(output, AlexsMobs.prefix("tarantula_hawk_wing"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.TENDON_WHIP, 1)
            .pattern(" CC")
            .pattern("TTC")
            .pattern("ST ")
            .define('C', AMItemRegistry.DROPBEAR_CLAW)
            .define('T', AMItemRegistry.ELASTIC_TENDON)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy(getHasName(AMItemRegistry.ELASTIC_TENDON), has(AMItemRegistry.ELASTIC_TENDON))
            .save(output, AlexsMobs.prefix("tendon_whip"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMBlockRegistry.TRANSMUTATION_TABLE, 1)
            .pattern(" N ")
            .pattern("S S")
            .pattern("OOO")
            .define('O', Items.OBSIDIAN)
            .define('S', AMItemRegistry.FARSEER_ARM)
            .define('N', Items.NETHER_STAR)
            .unlockedBy(getHasName(AMItemRegistry.FARSEER_ARM), has(AMItemRegistry.FARSEER_ARM))
            .save(output, AlexsMobs.prefix("transmutation_table"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItemRegistry.VINE_LASSO, 1)
            .pattern(" VL")
            .pattern("VSV")
            .pattern("SV ")
            .define('L', Items.LEAD)
            .define('S', AMItemRegistry.SHED_SNAKE_SKIN)
            .define('V', Items.VINE)
            .unlockedBy(getHasName(Items.VINE), has(Items.VINE))
            .save(output, AlexsMobs.prefix("vine_lasso"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMBlockRegistry.VOID_WORM_BEAK, 1)
            .requires(AMItemRegistry.VOID_WORM_MANDIBLE, 2)
            .unlockedBy(getHasName(AMItemRegistry.VOID_WORM_MANDIBLE), has(AMItemRegistry.VOID_WORM_MANDIBLE))
            .save(output, AlexsMobs.prefix("void_worm_beak"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMBlockRegistry.VOID_WORM_EFFIGY, 1)
            .pattern(" B ")
            .pattern("PEP")
            .pattern("EPE")
            .define('E', Items.ENDER_PEARL)
            .define('P', Items.PURPUR_BLOCK)
            .define('B', AMBlockRegistry.VOID_WORM_BEAK)
            .unlockedBy(getHasName(AMBlockRegistry.VOID_WORM_BEAK), has(AMBlockRegistry.VOID_WORM_BEAK))
            .save(output, AlexsMobs.prefix("void_worm_effigy"));

    }

    private void food(RecipeOutput output, ItemLike cooked, ItemLike raw, float experience, int furnaceTime, int campfireTime, int smokerTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(raw), RecipeCategory.FOOD, cooked, experience, furnaceTime)
            .group(getItemName(cooked))
            .unlockedBy("has_" + getItemName(raw), has(raw))
            .save(output, AlexsMobs.prefix(getItemName(cooked)));
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(raw), RecipeCategory.FOOD, cooked, experience, campfireTime)
            .group(getItemName(cooked))
            .unlockedBy("has_" + getItemName(raw), has(raw))
            .save(output, AlexsMobs.prefix(getItemName(cooked) + "_campfire"));
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(raw), RecipeCategory.FOOD, cooked, experience, smokerTime)
            .group(getItemName(cooked))
            .unlockedBy("has_" + getItemName(raw), has(raw))
            .save(output, AlexsMobs.prefix(getItemName(cooked) + "_smoke"));
    }

    private void bannerPattern(RecipeOutput output, ItemLike pattern, ItemLike requirement) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, pattern)
            .requires(Items.PAPER)
            .requires(requirement)
            .unlockedBy(getHasName(pattern), has(requirement))
            .group("banner_pattern")
            .save(output);
    }

    protected static void oneToOneConversionRecipe(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient, @Nullable String group) {
        oneToOneConversionRecipe(recipeOutput, result, ingredient, group, 1);
    }

    protected static void oneToOneConversionRecipe(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient, @Nullable String group, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result, resultCount)
            .requires(ingredient)
            .group(group)
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(recipeOutput, AlexsMobs.prefix(getConversionRecipeName(result, ingredient)));
    }

    protected static void twoByTwoPacker(RecipeOutput recipeOutput, String name, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        ShapedRecipeBuilder.shaped(category, packed, 1)
            .define('#', unpacked)
            .pattern("##")
            .pattern("##")
            .unlockedBy(getHasName(unpacked), has(unpacked))
            .save(recipeOutput, AlexsMobs.prefix(name));
    }

    protected static void threeByThreePacker(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked, String criterionName) {
        ShapelessRecipeBuilder.shapeless(category, packed)
            .requires(unpacked, 9)
            .unlockedBy(criterionName, has(unpacked))
            .save(recipeOutput, AlexsMobs.prefix(getConversionRecipeName(packed, unpacked)));
    }

    protected static void threeByThreePacker(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        threeByThreePacker(recipeOutput, category, packed, unpacked, getHasName(unpacked));
    }
}
