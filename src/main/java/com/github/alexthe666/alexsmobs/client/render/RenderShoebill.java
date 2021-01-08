package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelShoebill;
import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderShoebill extends MobRenderer<EntityShoebill, ModelShoebill> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/shoebill.png");

    public RenderShoebill(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelShoebill(), 0.3F);
    }

    protected void preRenderCallback(EntityShoebill entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getEntityTexture(EntityShoebill entity) {
        return TEXTURE;
    }
}
