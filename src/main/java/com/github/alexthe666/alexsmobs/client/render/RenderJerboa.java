package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelJerboa;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderJerboa extends MobRenderer<EntityJerboa, ModelJerboa> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/jerboa.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/jerboa_sleeping.png");

    public RenderJerboa(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelJerboa(), 0.1F);
    }

    protected void scale(EntityJerboa entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getTextureLocation(EntityJerboa entity) {
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }
}
