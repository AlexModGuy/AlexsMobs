package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelOrca;
import com.github.alexthe666.alexsmobs.entity.EntityOrca;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderOrca extends MobRenderer<EntityOrca, ModelOrca> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/orca.png");

    public RenderOrca(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelOrca(), 1.0F);
    }

    protected void preRenderCallback(EntityOrca entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }


    public ResourceLocation getEntityTexture(EntityOrca entity) {
        return TEXTURE;
    }
}
