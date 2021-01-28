package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrow;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrowItem;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCrow extends MobRenderer<EntityCrow, ModelCrow> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/crow.png");

    public RenderCrow(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCrow(), 0.2F);
        this.addLayer(new LayerCrowItem(this));
    }

    protected void preRenderCallback(EntityCrow entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getEntityTexture(EntityCrow entity) {
        return TEXTURE;
    }
}
