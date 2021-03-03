package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelStradpole;
import com.github.alexthe666.alexsmobs.entity.EntityStradpole;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderStradpole extends MobRenderer<EntityStradpole, ModelStradpole> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/stradpole.png");

    public RenderStradpole(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelStradpole(), 0.25F);
    }

    protected void preRenderCallback(EntityStradpole entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        //matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getEntityTexture(EntityStradpole entity) {
        return TEXTURE;
    }
}
