package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderSnowLeopard extends MobRenderer<EntitySnowLeopard, ModelSnowLeopard> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/snow_leopard.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/snow_leopard_sleeping.png");

    public RenderSnowLeopard(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelSnowLeopard(), 0.4F);
    }

    protected void preRenderCallback(EntitySnowLeopard entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9F,0.9F, 0.9F);
    }


    public ResourceLocation getEntityTexture(EntitySnowLeopard entity) {
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }
}
