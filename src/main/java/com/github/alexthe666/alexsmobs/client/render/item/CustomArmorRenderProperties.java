package com.github.alexthe666.alexsmobs.client.render.item;

import com.github.alexthe666.alexsmobs.client.model.layered.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;

public class CustomArmorRenderProperties implements IItemRenderProperties {

    private static boolean init;

    public static ModelAMElytra ELYTRA_MODEL;
    public static ModelRoadrunnerBoots ROADRUNNER_BOOTS_MODEL;
    public static ModelMooseHeadgear MOOSE_HEADGEAR_MODEL;
    public static ModelFrontierCap FRONTIER_CAP_MODEL;
    public static ModelSpikedTurtleShell SPIKED_TURTLE_SHELL_MODEL;
    public static ModelFedora FEDORA_MODEL;
    public static ModelSombrero SOMBRERO_MODEL;
    public static ModelFroststalkerHelmet FROSTSTALKER_HELMET_MODEL;

    public static void initializeModels() {
        init = true;
        ROADRUNNER_BOOTS_MODEL = new ModelRoadrunnerBoots(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.ROADRUNNER_BOOTS));
        MOOSE_HEADGEAR_MODEL = new ModelMooseHeadgear(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.MOOSE_HEADGEAR));
        FRONTIER_CAP_MODEL = new ModelFrontierCap(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.FRONTIER_CAP));
        FEDORA_MODEL = new ModelFedora(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.FEDORA));
        SPIKED_TURTLE_SHELL_MODEL = new ModelSpikedTurtleShell(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.SPIKED_TURTLE_SHELL));
        SOMBRERO_MODEL = new ModelSombrero(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.SOMBRERO));
        FROSTSTALKER_HELMET_MODEL = new ModelFroststalkerHelmet(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.FROSTSTALKER_HELMET));
        ELYTRA_MODEL = new ModelAMElytra(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.AM_ELYTRA));
    }

    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        if(!init){
            initializeModels();
        }
        if(itemStack.getItem() == AMItemRegistry.TARANTULA_HAWK_ELYTRA){
            return (A)ELYTRA_MODEL.withAnimations(entityLiving);
        }
        if(itemStack.getItem() == AMItemRegistry.ROADDRUNNER_BOOTS){
            return (A)ROADRUNNER_BOOTS_MODEL;
        }
        if(itemStack.getItem() == AMItemRegistry.MOOSE_HEADGEAR){
            return (A)MOOSE_HEADGEAR_MODEL;
        }
        if(itemStack.getItem() == AMItemRegistry.FRONTIER_CAP){
            return (A)FRONTIER_CAP_MODEL.withAnimations(entityLiving);
        }
        if(itemStack.getItem() == AMItemRegistry.FEDORA){
            return (A)FEDORA_MODEL;
        }
        if(itemStack.getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL){
            return (A)SPIKED_TURTLE_SHELL_MODEL;
        }
        if(itemStack.getItem() == AMItemRegistry.SOMBRERO){
            return (A)SOMBRERO_MODEL;
        }
        if(itemStack.getItem() == AMItemRegistry.FROSTSTALKER_HELMET){
            return (A)FROSTSTALKER_HELMET_MODEL;
        }
        return _default;
    }
}
