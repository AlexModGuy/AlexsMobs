package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMEffectRegistry {
    public static final MobEffect KNOCKBACK_RESISTANCE = new EffectKnockbackResistance();
    public static final MobEffect LAVA_VISION = new EffectLavaVision();
    public static final MobEffect SUNBIRD_BLESSING = new EffectSunbird(false);
    public static final MobEffect SUNBIRD_CURSE = new EffectSunbird(true);
    public static final MobEffect POISON_RESISTANCE = new EffectPoisonResistance();
    public static final MobEffect OILED = new EffectOiled();
    public static final MobEffect ORCAS_MIGHT = new EffectOrcaMight();
    public static final MobEffect BUG_PHEROMONES = new EffectBugPheromones();
    public static final MobEffect SOULSTEAL = new EffectSoulsteal();
    public static final MobEffect CLINGING = new EffectClinging();
    public static final MobEffect ENDER_FLU = new EffectEnderFlu();
    public static final MobEffect FEAR = new EffectFear();
    public static final MobEffect TIGERS_BLESSING = new EffectTigersBlessing();
    public static final MobEffect DEBILITATING_STING = new EffectDebilitatingSting();
    public static final MobEffect EXSANGUINATION = new EffectExsanguination();
    public static final Potion KNOCKBACK_RESISTANCE_POTION = new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 3600)).setRegistryName("alexsmobs:knockback_resistance");
    public static final Potion LONG_KNOCKBACK_RESISTANCE_POTION = new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 9600)).setRegistryName("alexsmobs:long_knockback_resistance");
    public static final Potion STRONG_KNOCKBACK_RESISTANCE_POTION = new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 1800, 1)).setRegistryName("alexsmobs:strong_knockback_resistance");
    public static final Potion LAVA_VISION_POTION = new Potion(new MobEffectInstance(LAVA_VISION, 3600)).setRegistryName("alexsmobs:lava_vision");
    public static final Potion LONG_LAVA_VISION_POTION = new Potion(new MobEffectInstance(LAVA_VISION, 9600)).setRegistryName("alexsmobs:long_lava_vision");
    public static final Potion SPEED_III_POTION = new Potion(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2200, 2)).setRegistryName("alexsmobs:speed_iii");
    public static final Potion POISON_RESISTANCE_POTION = new Potion(new MobEffectInstance(POISON_RESISTANCE, 3600)).setRegistryName("alexsmobs:poison_resistance");
    public static final Potion LONG_POISON_RESISTANCE_POTION = new Potion(new MobEffectInstance(POISON_RESISTANCE, 9600)).setRegistryName("alexsmobs:long_poison_resistance");
    public static final Potion BUG_PHEROMONES_POTION = new Potion(new MobEffectInstance(BUG_PHEROMONES, 3600)).setRegistryName("alexsmobs:bug_pheromones");
    public static final Potion LONG_BUG_PHEROMONES_POTION = new Potion(new MobEffectInstance(BUG_PHEROMONES, 9600)).setRegistryName("alexsmobs:long_bug_pheromones");
    public static final Potion SOULSTEAL_POTION = new Potion(new MobEffectInstance(SOULSTEAL, 3600)).setRegistryName("alexsmobs:soulsteal");
    public static final Potion LONG_SOULSTEAL_POTION = new Potion(new MobEffectInstance(SOULSTEAL, 9600)).setRegistryName("alexsmobs:long_soulsteal");
    public static final Potion STRONG_SOULSTEAL_POTION = new Potion(new MobEffectInstance(SOULSTEAL, 1800, 1)).setRegistryName("alexsmobs:strong_soulsteal");
    public static final Potion CLINGING_POTION = new Potion(new MobEffectInstance(CLINGING, 3600)).setRegistryName("alexsmobs:clinging");
    public static final Potion LONG_CLINGING_POTION = new Potion(new MobEffectInstance(CLINGING, 9600)).setRegistryName("alexsmobs:long_clinging");

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<MobEffect> event) {
        try {
            for (Field f : AMEffectRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof MobEffect) {
                    event.getRegistry().register((MobEffect) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        try {
            for (Field f : AMEffectRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Potion) {
                    event.getRegistry().register((Potion) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        AMEffectRegistry.onInitItems();
    }

    public static ItemStack createPotion(Potion potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    public static void onInitItems(){
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.STRENGTH)), Ingredient.of(AMItemRegistry.BEAR_FUR), createPotion(KNOCKBACK_RESISTANCE_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(KNOCKBACK_RESISTANCE_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_KNOCKBACK_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(KNOCKBACK_RESISTANCE_POTION)), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_KNOCKBACK_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(AMItemRegistry.LAVA_BOTTLE), Ingredient.of(AMItemRegistry.BONE_SERPENT_TOOTH), createPotion(LAVA_VISION_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(LAVA_VISION_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_LAVA_VISION_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.POISON)), Ingredient.of(AMItemRegistry.RATTLESNAKE_RATTLE), new ItemStack(AMItemRegistry.POISON_BOTTLE)));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(AMItemRegistry.POISON_BOTTLE), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG), createPotion(POISON_RESISTANCE_POTION));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(AMItemRegistry.KOMODO_SPIT_BOTTLE), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG), createPotion(POISON_RESISTANCE_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(POISON_RESISTANCE_POTION)), Ingredient.of(AMItemRegistry.KOMODO_SPIT), createPotion(LONG_POISON_RESISTANCE_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.STRONG_SWIFTNESS)), Ingredient.of(AMItemRegistry.GAZELLE_HORN), createPotion(SPEED_III_POTION)));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.COCKROACH_WING), createPotion(BUG_PHEROMONES_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(BUG_PHEROMONES_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_BUG_PHEROMONES_POTION)));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.SOUL_HEART), createPotion(SOULSTEAL_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(SOULSTEAL_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_SOULSTEAL_POTION)));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(SOULSTEAL_POTION)), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_SOULSTEAL_POTION)));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(AMItemRegistry.DROPBEAR_CLAW), createPotion(CLINGING_POTION));
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(CLINGING_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_CLINGING_POTION)));


    }
}
