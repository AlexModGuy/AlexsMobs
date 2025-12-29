package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMEffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECT_DEF_REG = DeferredRegister.create(Registries.MOB_EFFECT, AlexsMobs.MODID);
    public static final DeferredRegister<Potion> POTION_DEF_REG = DeferredRegister.create(Registries.POTION, AlexsMobs.MODID);

    public static final Holder<MobEffect> KNOCKBACK_RESISTANCE = EFFECT_DEF_REG.register("knockback_resistance", ()-> new EffectKnockbackResistance());
    public static final Holder<MobEffect> LAVA_VISION = EFFECT_DEF_REG.register("lava_vision", ()-> new EffectLavaVision());
    public static final Holder<MobEffect> SUNBIRD_BLESSING = EFFECT_DEF_REG.register("sunbird_blessing", ()-> new EffectSunbird(false));
    public static final Holder<MobEffect> SUNBIRD_CURSE = EFFECT_DEF_REG.register("sunbird_curse", ()-> new EffectSunbird(true));
    public static final Holder<MobEffect> POISON_RESISTANCE = EFFECT_DEF_REG.register("poison_resistance", ()-> new EffectPoisonResistance());
    public static final Holder<MobEffect> OILED = EFFECT_DEF_REG.register("oiled", ()-> new EffectOiled());
    public static final Holder<MobEffect> ORCAS_MIGHT = EFFECT_DEF_REG.register("orcas_might", ()-> new EffectOrcaMight());
    public static final Holder<MobEffect> BUG_PHEROMONES = EFFECT_DEF_REG.register("bug_pheromones", ()-> new EffectBugPheromones());
    public static final Holder<MobEffect> SOULSTEAL = EFFECT_DEF_REG.register("soulsteal", ()-> new EffectSoulsteal());
    public static final Holder<MobEffect> CLINGING = EFFECT_DEF_REG.register("clinging", ()-> new EffectClinging());
    public static final Holder<MobEffect> ENDER_FLU = EFFECT_DEF_REG.register("ender_flu", ()-> new EffectEnderFlu());
    public static final Holder<MobEffect> FEAR = EFFECT_DEF_REG.register("fear", ()-> new EffectFear());
    public static final Holder<MobEffect> TIGERS_BLESSING = EFFECT_DEF_REG.register("tigers_blessing", ()-> new EffectTigersBlessing());
    public static final Holder<MobEffect> DEBILITATING_STING = EFFECT_DEF_REG.register("debilitating_sting", ()-> new EffectDebilitatingSting());
    public static final Holder<MobEffect> EXSANGUINATION = EFFECT_DEF_REG.register("exsanguination", ()-> new EffectExsanguination());
    public static final Holder<MobEffect> EARTHQUAKE = EFFECT_DEF_REG.register("earthquake", ()-> new EffectEarthquake());
    public static final Holder<MobEffect> FLEET_FOOTED = EFFECT_DEF_REG.register("fleet_footed", ()-> new EffectFleetFooted());
    public static final Holder<MobEffect> POWER_DOWN = EFFECT_DEF_REG.register("power_down", ()-> new EffectPowerDown());

    public static final Holder<MobEffect> MOSQUITO_REPELLENT = EFFECT_DEF_REG.register("mosquito_repellent", ()-> new EffectMosquitoRepellent());
    public static final DeferredHolder<Potion, Potion> KNOCKBACK_RESISTANCE_POTION = POTION_DEF_REG.register("knockback_resistance", ()-> new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_KNOCKBACK_RESISTANCE_POTION = POTION_DEF_REG.register("long_knockback_resistance", ()-> new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 9600)));
    public static final DeferredHolder<Potion, Potion> STRONG_KNOCKBACK_RESISTANCE_POTION = POTION_DEF_REG.register("strong_knockback_resistance", ()-> new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> LAVA_VISION_POTION = POTION_DEF_REG.register("lava_vision", ()-> new Potion(new MobEffectInstance(LAVA_VISION, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_LAVA_VISION_POTION = POTION_DEF_REG.register("long_lava_vision", ()-> new Potion(new MobEffectInstance(LAVA_VISION, 9600)));
    public static final DeferredHolder<Potion, Potion> SPEED_III_POTION = POTION_DEF_REG.register("speed_iii", ()-> new Potion(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2200, 2)));
    public static final DeferredHolder<Potion, Potion> POISON_RESISTANCE_POTION = POTION_DEF_REG.register("poison_resistance", ()-> new Potion(new MobEffectInstance(POISON_RESISTANCE, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_POISON_RESISTANCE_POTION = POTION_DEF_REG.register("long_poison_resistance", ()-> new Potion(new MobEffectInstance(POISON_RESISTANCE, 9600)));
    public static final DeferredHolder<Potion, Potion> BUG_PHEROMONES_POTION = POTION_DEF_REG.register("bug_pheromones", ()-> new Potion(new MobEffectInstance(BUG_PHEROMONES, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_BUG_PHEROMONES_POTION = POTION_DEF_REG.register("long_bug_pheromones", ()-> new Potion(new MobEffectInstance(BUG_PHEROMONES, 9600)));
    public static final DeferredHolder<Potion, Potion> SOULSTEAL_POTION = POTION_DEF_REG.register("soulsteal", ()-> new Potion(new MobEffectInstance(SOULSTEAL, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_SOULSTEAL_POTION = POTION_DEF_REG.register("long_soulsteal", ()-> new Potion(new MobEffectInstance(SOULSTEAL, 9600)));
    public static final DeferredHolder<Potion, Potion> STRONG_SOULSTEAL_POTION = POTION_DEF_REG.register("strong_soulsteal", ()-> new Potion(new MobEffectInstance(SOULSTEAL, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> CLINGING_POTION = POTION_DEF_REG.register("clinging", ()-> new Potion(new MobEffectInstance(CLINGING, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_CLINGING_POTION = POTION_DEF_REG.register("long_clinging", ()-> new Potion(new MobEffectInstance(CLINGING, 9600)));

    public static ItemStack createPotion(Holder<Potion> potion){
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static void registerBrewingRecipes(net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent event){
        var builder = event.getBuilder();
        builder.addMix(Potions.STRENGTH, AMItemRegistry.BEAR_FUR.get(), KNOCKBACK_RESISTANCE_POTION);
        builder.addMix(KNOCKBACK_RESISTANCE_POTION, Items.REDSTONE, LONG_KNOCKBACK_RESISTANCE_POTION);
        builder.addMix(KNOCKBACK_RESISTANCE_POTION, Items.GLOWSTONE_DUST, STRONG_KNOCKBACK_RESISTANCE_POTION);
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(AMItemRegistry.KOMODO_SPIT_BOTTLE.get()), Ingredient.of(AMItemRegistry.RATTLESNAKE_RATTLE.get()), new ItemStack(AMItemRegistry.POISON_BOTTLE.get())));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.POISON)), Ingredient.of(AMItemRegistry.RATTLESNAKE_RATTLE.get()), new ItemStack(AMItemRegistry.POISON_BOTTLE.get())));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(AMItemRegistry.KOMODO_SPIT_BOTTLE.get()), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG.get()), new ItemStack(AMItemRegistry.POISON_BOTTLE.get())));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(AMItemRegistry.POISON_BOTTLE.get()), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG.get()), createPotion(POISON_RESISTANCE_POTION)));
        builder.addMix(POISON_RESISTANCE_POTION, AMItemRegistry.KOMODO_SPIT.get(), LONG_POISON_RESISTANCE_POTION);
        builder.addMix(Potions.STRONG_SWIFTNESS, AMItemRegistry.GAZELLE_HORN.get(), SPEED_III_POTION);
        builder.addMix(Potions.AWKWARD, AMItemRegistry.COCKROACH_WING.get(), BUG_PHEROMONES_POTION);
        builder.addMix(BUG_PHEROMONES_POTION, Items.REDSTONE, LONG_BUG_PHEROMONES_POTION);
        builder.addMix(Potions.AWKWARD, AMItemRegistry.SOUL_HEART.get(), SOULSTEAL_POTION);
        builder.addMix(SOULSTEAL_POTION, Items.REDSTONE, LONG_SOULSTEAL_POTION);
        builder.addMix(SOULSTEAL_POTION, Items.GLOWSTONE_DUST, STRONG_SOULSTEAL_POTION);
        builder.addMix(Potions.AWKWARD, AMItemRegistry.DROPBEAR_CLAW.get(), CLINGING_POTION);
        builder.addMix(CLINGING_POTION, Items.REDSTONE, LONG_CLINGING_POTION);
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(AMItemRegistry.LAVA_BOTTLE.get()), Ingredient.of(AMItemRegistry.BONE_SERPENT_TOOTH.get()), createPotion(LAVA_VISION_POTION)));
        builder.addMix(LAVA_VISION_POTION, Items.REDSTONE, LONG_LAVA_VISION_POTION);
    }

    public static void init(){
        // Brewing recipes are now registered via RegisterBrewingRecipesEvent
    }
}
