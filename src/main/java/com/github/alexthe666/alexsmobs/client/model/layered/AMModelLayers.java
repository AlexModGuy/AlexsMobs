package com.github.alexthe666.alexsmobs.client.model.layered;

import com.github.alexthe666.alexsmobs.client.model.ModelFroststalker;
import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class AMModelLayers {

    public static final ModelLayerLocation AM_ELYTRA = createLocation("am_elytra", "main");
    public static final ModelLayerLocation SITTING_WANDERING_VILLAGER = createLocation("sitting_wandering_villager", "main");
    public static final ModelLayerLocation ROADRUNNER_BOOTS = createLocation("roadrunner_boots", "main");
    public static final ModelLayerLocation MOOSE_HEADGEAR = createLocation("moose_headgear", "main");
    public static final ModelLayerLocation FRONTIER_CAP = createLocation("frontier_cap", "main");
    public static final ModelLayerLocation SPIKED_TURTLE_SHELL = createLocation("spiked_turtle_shell", "main");
    public static final ModelLayerLocation FEDORA = createLocation("fedora", "main");
    public static final ModelLayerLocation SOMBRERO = createLocation("sombrero", "main");
    public static final ModelLayerLocation SOMBRERO_GOOFY_FASHION = createLocation("sombrero_goofy_fashion", "main");
    public static final ModelLayerLocation FROSTSTALKER_HELMET = createLocation("froststalker_helmet", "main");
    public static final ModelLayerLocation ROCKY_CHESTPLATE = createLocation("rocky_chestplate", "main");
    public static final ModelLayerLocation FLYING_FISH_BOOTS = createLocation("flying_fish_boots", "main");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SITTING_WANDERING_VILLAGER, () -> LayerDefinition.create(ModelWanderingVillagerRider.createBodyModel(), 64, 64));
        event.registerLayerDefinition(ROADRUNNER_BOOTS, () -> ModelRoadrunnerBoots.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(MOOSE_HEADGEAR, () -> ModelMooseHeadgear.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(FRONTIER_CAP, () -> ModelFrontierCap.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(SPIKED_TURTLE_SHELL, () -> ModelSpikedTurtleShell.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(FEDORA, () -> ModelFedora.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(AM_ELYTRA, () -> ModelAMElytra.createLayer(new CubeDeformation(1.0F)));
        event.registerLayerDefinition(SOMBRERO, () -> ModelSombrero.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(SOMBRERO_GOOFY_FASHION, () -> ModelSombrero.createArmorLayerAprilFools(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(FROSTSTALKER_HELMET, () -> ModelFroststalkerHelmet.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(ROCKY_CHESTPLATE, () -> ModelRockyChestplate.createArmorLayer(new CubeDeformation(0.7F)));
        event.registerLayerDefinition(FLYING_FISH_BOOTS, () -> ModelFlyingFishBoots.createArmorLayer(new CubeDeformation(0.5F)));
    }

    private static ModelLayerLocation createLocation(String model, String layer) {
        return new ModelLayerLocation(new ResourceLocation("alexsmobs", model), layer);
    }


}
