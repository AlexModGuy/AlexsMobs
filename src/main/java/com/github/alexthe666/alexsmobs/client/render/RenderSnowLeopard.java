package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderSnowLeopard extends MobRenderer<EntitySnowLeopard, ModelSnowLeopard> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/snow_leopard.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/snow_leopard_sleeping.png");

    public RenderSnowLeopard(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSnowLeopard(), 0.4F);
    }

    protected void scale(EntitySnowLeopard entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9F,0.9F, 0.9F);
    }


    public ResourceLocation getTextureLocation(EntitySnowLeopard entity) {
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }
}
