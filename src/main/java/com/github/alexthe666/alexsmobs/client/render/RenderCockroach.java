package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCockroachMaracas;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCockroachRainbow;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCockroach extends MobRenderer<EntityCockroach, ModelCockroach> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cockroach.png");

    public RenderCockroach(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCockroach(), 0.3F);
        this.addLayer(new LayerCockroachRainbow(this));
        this.addLayer(new LayerCockroachMaracas(this));
    }

    protected void preRenderCallback(EntityCockroach entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
    }


    public ResourceLocation getEntityTexture(EntityCockroach entity) {
        return TEXTURE;
    }
}
