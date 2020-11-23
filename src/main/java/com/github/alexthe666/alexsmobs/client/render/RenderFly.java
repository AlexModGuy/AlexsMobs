package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFly;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderFly extends MobRenderer<EntityFly, ModelFly> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/fly.png");

    public RenderFly(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelFly(), 0.2F);
    }

    protected void preRenderCallback(EntityFly entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
    }

    protected boolean func_230495_a_(EntityFly fly) {
        return fly.isInNether();
    }

    public ResourceLocation getEntityTexture(EntityFly entity) {
        return TEXTURE;
    }
}
