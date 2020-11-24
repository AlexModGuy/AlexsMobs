package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRattlesnake;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderRattlesnake extends MobRenderer<EntityRattlesnake, ModelRattlesnake> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/rattlesnake.png");

    public RenderRattlesnake(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelRattlesnake(), 0.2F);
    }

    protected void preRenderCallback(EntityRattlesnake entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getEntityTexture(EntityRattlesnake entity) {
        return TEXTURE;
    }
}
