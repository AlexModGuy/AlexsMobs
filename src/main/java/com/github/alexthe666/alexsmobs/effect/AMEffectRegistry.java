package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMEffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECT_DEF_REG = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AlexsMobs.MODID);
    public static final DeferredRegister<Potion> POTION_DEF_REG = DeferredRegister.create(ForgeRegistries.POTIONS, AlexsMobs.MODID);

    public static final RegistryObject<MobEffect> KNOCKBACK_RESISTANCE = EFFECT_DEF_REG.register("knockback_resistance", ()-> new EffectKnockbackResistance());
    public static final RegistryObject<MobEffect> LAVA_VISION = EFFECT_DEF_REG.register("lava_vision", ()-> new EffectLavaVision());
    public static final RegistryObject<MobEffect> SUNBIRD_BLESSING = EFFECT_DEF_REG.register("sunbird_blessing", ()-> new EffectSunbird(false));
    public static final RegistryObject<MobEffect> SUNBIRD_CURSE = EFFECT_DEF_REG.register("sunbird_curse", ()-> new EffectSunbird(true));
    public static final RegistryObject<MobEffect> POISON_RESISTANCE = EFFECT_DEF_REG.register("poison_resistance", ()-> new EffectPoisonResistance());
    public static final RegistryObject<MobEffect> OILED = EFFECT_DEF_REG.register("oiled", ()-> new EffectOiled());
    public static final RegistryObject<MobEffect> ORCAS_MIGHT = EFFECT_DEF_REG.register("orcas_might", ()-> new EffectOrcaMight());
    public static final RegistryObject<MobEffect> BUG_PHEROMONES = EFFECT_DEF_REG.register("bug_pheromones", ()-> new EffectBugPheromones());
    public static final RegistryObject<MobEffect> SOULSTEAL = EFFECT_DEF_REG.register("soulsteal", ()-> new EffectSoulsteal());
    public static final RegistryObject<MobEffect> CLINGING = EFFECT_DEF_REG.register("clinging", ()-> new EffectClinging());
    public static final RegistryObject<MobEffect> ENDER_FLU = EFFECT_DEF_REG.register("ender_flu", ()-> new EffectEnderFlu());
    public static final RegistryObject<MobEffect> FEAR = EFFECT_DEF_REG.register("fear", ()-> new EffectFear());
    public static final RegistryObject<MobEffect> TIGERS_BLESSING = EFFECT_DEF_REG.register("tigers_blessing", ()-> new EffectTigersBlessing());
    public static final RegistryObject<MobEffect> DEBILITATING_STING = EFFECT_DEF_REG.register("debilitating_sting", ()-> new EffectDebilitatingSting());
    public static final RegistryObject<MobEffect> EXSANGUINATION = EFFECT_DEF_REG.register("exsanguination", ()-> new EffectExsanguination());
    public static final RegistryObject<MobEffect> EARTHQUAKE = EFFECT_DEF_REG.register("earthquake", ()-> new EffectEarthquake());
    public static final RegistryObject<MobEffect> FLEET_FOOTED = EFFECT_DEF_REG.register("fleet_footed", ()-> new EffectFleetFooted());
    public static final RegistryObject<MobEffect> POWER_DOWN = EFFECT_DEF_REG.register("power_down", ()-> new EffectPowerDown());

    public static final RegistryObject<MobEffect> MOSQUITO_REPELLENT = EFFECT_DEF_REG.register("mosquito_repellent", ()-> new EffectMosquitoRepellent());
    public static final RegistryObject<Potion> KNOCKBACK_RESISTANCE_POTION = POTION_DEF_REG.register("knockback_resistance", ()-> new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE.get(), 3600)));
    public static final RegistryObject<Potion> LONG_KNOCKBACK_RESISTANCE_POTION = POTION_DEF_REG.register("long_knockback_resistance", ()-> new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE.get(), 9600)));
    public static final RegistryObject<Potion> STRONG_KNOCKBACK_RESISTANCE_POTION = POTION_DEF_REG.register("strong_knockback_resistance", ()-> new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE.get(), 1800, 1)));
    public static final RegistryObject<Potion> LAVA_VISION_POTION = POTION_DEF_REG.register("lava_vision", ()-> new Potion(new MobEffectInstance(LAVA_VISION.get(), 3600)));
    public static final RegistryObject<Potion> LONG_LAVA_VISION_POTION = POTION_DEF_REG.register("long_lava_vision", ()-> new Potion(new MobEffectInstance(LAVA_VISION.get(), 9600)));
    public static final RegistryObject<Potion> SPEED_III_POTION = POTION_DEF_REG.register("speed_iii", ()-> new Potion(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2200, 2)));
    public static final RegistryObject<Potion> POISON_RESISTANCE_POTION = POTION_DEF_REG.register("poison_resistance", ()-> new Potion(new MobEffectInstance(POISON_RESISTANCE.get(), 3600)));
    public static final RegistryObject<Potion> LONG_POISON_RESISTANCE_POTION = POTION_DEF_REG.register("long_poison_resistance", ()-> new Potion(new MobEffectInstance(POISON_RESISTANCE.get(), 9600)));
    public static final RegistryObject<Potion> BUG_PHEROMONES_POTION = POTION_DEF_REG.register("bug_pheromones", ()-> new Potion(new MobEffectInstance(BUG_PHEROMONES.get(), 3600)));
    public static final RegistryObject<Potion> LONG_BUG_PHEROMONES_POTION = POTION_DEF_REG.register("long_bug_pheromones", ()-> new Potion(new MobEffectInstance(BUG_PHEROMONES.get(), 9600)));
    public static final RegistryObject<Potion> SOULSTEAL_POTION = POTION_DEF_REG.register("soulsteal", ()-> new Potion(new MobEffectInstance(SOULSTEAL.get(), 3600)));
    public static final RegistryObject<Potion> LONG_SOULSTEAL_POTION = POTION_DEF_REG.register("long_soulsteal", ()-> new Potion(new MobEffectInstance(SOULSTEAL.get(), 9600)));
    public static final RegistryObject<Potion> STRONG_SOULSTEAL_POTION = POTION_DEF_REG.register("strong_soulsteal", ()-> new Potion(new MobEffectInstance(SOULSTEAL.get(), 1800, 1)));
    public static final RegistryObject<Potion> CLINGING_POTION = POTION_DEF_REG.register("clinging", ()-> new Potion(new MobEffectInstance(CLINGING.get(), 3600)));
    public static final RegistryObject<Potion> LONG_CLINGING_POTION = POTION_DEF_REG.register("long_clinging", ()-> new Potion(new MobEffectInstance(CLINGING.get(), 9600)));

    public static ItemStack createPotion(RegistryObject<Potion> potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get());
    }

    public static ItemStack createPotion(Potion potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    public static void init(){
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.STRENGTH)), Ingredient.of(AMItemRegistry.BEAR_FUR.get()), createPotion(KNOCKBACK_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(KNOCKBACK_RESISTANCE_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_KNOCKBACK_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(KNOCKBACK_RESISTANCE_POTION)), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_KNOCKBACK_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(AMItemRegistry.LAVA_BOTTLE.get()), Ingredient.of(AMItemRegistry.BONE_SERPENT_TOOTH.get()), createPotion(LAVA_VISION_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(LAVA_VISION_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_LAVA_VISION_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.POISON)), Ingredient.of(AMItemRegistry.RATTLESNAKE_RATTLE.get()), new ItemStack(AMItemRegistry.POISON_BOTTLE.get())));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(AMItemRegistry.POISON_BOTTLE.get()), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG.get()), createPotion(POISON_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(AMItemRegistry.KOMODO_SPIT_BOTTLE.get()), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG.get()), createPotion(POISON_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(POISON_RESISTANCE_POTION)), Ingredient.of(AMItemRegistry.KOMODO_SPIT.get()), createPotion(LONG_POISON_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.STRONG_SWIFTNESS)), Ingredient.of(AMItemRegistry.GAZELLE_HORN.get()), createPotion(SPEED_III_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.COCKROACH_WING.get()), createPotion(BUG_PHEROMONES_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(BUG_PHEROMONES_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_BUG_PHEROMONES_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.SOUL_HEART.get()), createPotion(SOULSTEAL_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(SOULSTEAL_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_SOULSTEAL_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(SOULSTEAL_POTION)), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_SOULSTEAL_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.DROPBEAR_CLAW.get()), createPotion(CLINGING_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(CLINGING_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_CLINGING_POTION)));


    }
}
