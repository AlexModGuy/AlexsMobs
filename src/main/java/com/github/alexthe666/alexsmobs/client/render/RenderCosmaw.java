package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCosmaw;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCosmaw extends MobRenderer<EntityCosmaw, ModelCosmaw> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cosmaw.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/cosmaw_glow.png");

    public RenderCosmaw(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCosmaw(), 0.9F);
        this.addLayer(new LayerBasicGlow(this, TEXTURE_GLOW));
    }

    protected void scale(EntityCosmaw entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.translate(0, 0.3F, 0);
    }

    public ResourceLocation getTextureLocation(EntityCosmaw entity) {
        return TEXTURE;
    }
}
