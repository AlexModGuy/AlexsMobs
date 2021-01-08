package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSoulVulture;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerSoulVultureGlow;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderSoulVulture extends MobRenderer<EntitySoulVulture, ModelSoulVulture> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/soul_vulture.png");

    public RenderSoulVulture(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelSoulVulture(), 0.3F);
        this.addLayer(new LayerSoulVultureGlow(this));
    }

    protected void preRenderCallback(EntitySoulVulture entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      //  matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getEntityTexture(EntitySoulVulture entity) {
        return TEXTURE;
    }
}
