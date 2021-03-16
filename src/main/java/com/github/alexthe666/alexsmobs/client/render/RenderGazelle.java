package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelGazelle;
import com.github.alexthe666.alexsmobs.entity.EntityGazelle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderGazelle extends MobRenderer<EntityGazelle, ModelGazelle> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/gazelle.png");

    public RenderGazelle(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelGazelle(), 0.4F);
    }

    protected void preRenderCallback(EntityGazelle entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getEntityTexture(EntityGazelle entity) {
        return TEXTURE;
    }
}
