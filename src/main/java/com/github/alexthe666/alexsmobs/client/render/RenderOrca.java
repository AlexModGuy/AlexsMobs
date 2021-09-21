package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelOrca;
import com.github.alexthe666.alexsmobs.entity.EntityOrca;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderOrca extends MobRenderer<EntityOrca, ModelOrca> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/orca.png");

    public RenderOrca(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelOrca(), 1.0F);
    }

    protected void scale(EntityOrca entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }


    public ResourceLocation getTextureLocation(EntityOrca entity) {
        return TEXTURE;
    }
}
