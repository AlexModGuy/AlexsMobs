package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMantisShrimp;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerMantisShrimpItem;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderMantisShrimp extends MobRenderer<EntityMantisShrimp, ModelMantisShrimp> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/mantis_shrimp_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/mantis_shrimp_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/mantis_shrimp_2.png");

    public RenderMantisShrimp(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelMantisShrimp(), 0.6F);
        this.addLayer(new LayerMantisShrimpItem(this));
    }

    protected void preRenderCallback(EntityMantisShrimp entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getEntityTexture(EntityMantisShrimp entity) {
        return entity.getVariant() == 2 ? TEXTURE_2 : entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
