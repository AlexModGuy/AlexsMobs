package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.*;
import com.github.alexthe666.alexsmobs.world.AMWorldRegistry;
import com.github.alexthe666.citadel.server.item.CitadelRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.github.alexthe666.alexsmobs.AlexsMobs.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
    public static SimpleRecipeSerializer MIMICREAM_RECIPE;
    public static SimpleRecipeSerializer BISON_UPGRADE_RECIPE;
    public static LootItemConditionType matchesBanana;
    public static LootItemConditionType matchesBlossom;

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<RecipeSerializer<?>> event) {
        AMEffectRegistry.registerRecipes();
        if(AMConfig.mimicreamRepair){
            MIMICREAM_RECIPE = new SimpleRecipeSerializer<>(RecipeMimicreamRepair::new);
            MIMICREAM_RECIPE.setRegistryName(new ResourceLocation("alexsmobs:mimicream_repair_recipe"));
            event.getRegistry().register(MIMICREAM_RECIPE);
        }
        CitadelRecipes.registerSmithingRecipe(new RecipeBisonUpgrade(new ResourceLocation("alexsmobs:bison_fur_upgrade")));
    }

    @SubscribeEvent
    public static void registerLootData(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        matchesBanana = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation("alexsmobs:matches_banana_tag"), new LootItemConditionType(new MatchesBananaTagCondition.LootSerializer()));
        matchesBlossom = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation("alexsmobs:matches_blossom_tag"), new LootItemConditionType(new MatchesBlossomTagCondition.LootSerializer()));
        if (AMConfig.bananasDropFromLeaves) {
            event.getRegistry().register(new BananaLootModifier.Serializer().setRegistryName(new ResourceLocation("alexsmobs:banana_drop")));
        }
        if (AMConfig.acaciaBlossomsDropFromLeaves) {
            event.getRegistry().register(new BlossomLootModifier.Serializer().setRegistryName(new ResourceLocation("alexsmobs:blossom_drop")));
        }
    }

    public void init() {
    }

    public void clientInit() {
    }
    public Player getClientSidePlayer() {
        return null;
    }

    public void openBookGUI(ItemStack itemStackIn) {
    }

    public void openBookGUI(ItemStack itemStackIn, String page) {
    }

    public Object getArmorModel(int armorId, LivingEntity entity) {
        return null;
    }

    public void onEntityStatus(Entity entity, byte updateKind) {
    }

    public void updateBiomeVisuals(int x, int z) {
    }

    public void setupParticles() {
    }

    public void setRenderViewEntity(Entity entity) {

    }

    public void resetRenderViewEntity() {

    }

    public int getPreviousPOV(){
        return 0;
    }

    public boolean isFarFromCamera(double x, double y, double z) {
        return true;
    }

    public void resetVoidPortalCreation(Player player){}

    public Object getISTERProperties() {
        return null;
    }

    public Object getArmorRenderProperties() {
        return null;
    }

    public void spawnSpecialParticle(int i) {
    }

    public void processVisualFlag(Entity entity, int flag) {
    }
}
