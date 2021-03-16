package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderRoadrunner extends MobRenderer<EntityRoadrunner, ModelRoadrunner> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/roadrunner.png");

    public RenderRoadrunner(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelRoadrunner(), 0.3F);
    }

    protected void preRenderCallback(EntityRoadrunner entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      //  matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getEntityTexture(EntityRoadrunner entity) {
        return TEXTURE;
    }
}
