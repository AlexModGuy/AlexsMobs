package com.github.alexthe666.alexsmobs.client;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRainbow;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientLayerRegistry {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
                BuiltInRegistries.ENTITY_TYPE.stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                        .collect(Collectors.toList()));
        entityTypes.forEach((entityType -> {
            addLayerIfApplicable(entityType, event);
        }));
        // Add rainbow layer to player renderers
        for (PlayerSkin.Model model : event.getSkins()){
            PlayerRenderer skin = event.getSkin(model);
            if (skin != null) {
                skin.addLayer(new LayerRainbow(skin));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void addLayerIfApplicable(EntityType<? extends LivingEntity> entityType, EntityRenderersEvent.AddLayers event) {
        LivingEntityRenderer renderer = null;
        if(entityType != EntityType.ENDER_DRAGON){
            try{
                renderer = (LivingEntityRenderer) event.getRenderer(entityType);
            }catch (Exception e){
                AlexsMobs.LOGGER.warn("Could not apply rainbow color layer to " + BuiltInRegistries.ENTITY_TYPE.getKey(entityType) + ", has custom renderer that is not LivingEntityRenderer.");
            }
            if(renderer != null){
                renderer.addLayer(new LayerRainbow(renderer));
            }
        }
    }
}
