package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderSeagull extends MobRenderer<EntitySeagull, ModelSeagull> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/seagull.png");
    private static final ResourceLocation TEXTURE_WINGULL = new ResourceLocation("alexsmobs:textures/entity/seagull_wingull.png");

    public RenderSeagull(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelSeagull(), 0.2F);
    }

    protected void preRenderCallback(EntitySeagull entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getEntityTexture(EntitySeagull entity) {
        return TEXTURE;
    }
}
