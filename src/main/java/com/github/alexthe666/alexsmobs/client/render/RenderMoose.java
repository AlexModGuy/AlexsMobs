package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMoose;
import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderMoose extends MobRenderer<EntityMoose, ModelMoose> {
    private static final ResourceLocation TEXTURE_ANTLERED = new ResourceLocation("alexsmobs:textures/entity/moose_antlered.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/moose.png");

    public RenderMoose(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelMoose(), 0.8F);
    }

    protected void preRenderCallback(EntityMoose entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }


    public ResourceLocation getEntityTexture(EntityMoose entity) {
        return entity.isAntlered() && !entity.isChild() ? TEXTURE_ANTLERED : TEXTURE;
    }
}
