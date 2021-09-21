package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSoulVulture;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerSoulVultureGlow;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderSoulVulture extends MobRenderer<EntitySoulVulture, ModelSoulVulture> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/soul_vulture.png");

    public RenderSoulVulture(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelSoulVulture(), 0.3F);
        this.addLayer(new LayerSoulVultureGlow(this));
    }

    protected void scale(EntitySoulVulture entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      //  matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getTextureLocation(EntitySoulVulture entity) {
        return TEXTURE;
    }
}
