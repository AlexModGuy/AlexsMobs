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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.github.alexthe666.alexsmobs.AlexsMobs.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

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

    public void setPupfishChunkForItem(int chunkX, int chunkZ) {
    }
}
