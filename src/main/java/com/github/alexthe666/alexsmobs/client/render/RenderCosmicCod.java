package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCosmicCod;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmicCod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCosmicCod extends MobRenderer<EntityCosmicCod, EntityModel<EntityCosmicCod>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cosmic_cod.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/cosmic_cod_eyes.png");

    public RenderCosmicCod(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCosmicCod(), 0.25F);
        this.addLayer(new LayerBasicGlow<>(this, TEXTURE_EYES));
    }

    public ResourceLocation getTextureLocation(EntityCosmicCod entity) {
        return TEXTURE;
    }
}

