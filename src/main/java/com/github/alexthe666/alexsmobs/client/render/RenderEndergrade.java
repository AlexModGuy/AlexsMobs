package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEndergrade;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerEndergradeSaddle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderEndergrade extends MobRenderer<EntityEndergrade, ModelEndergrade> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/endergrade.png");

    public RenderEndergrade(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelEndergrade(), 0.6F);
        this.addLayer(new LayerEndergradeSaddle(this));
    }

    protected void preRenderCallback(EntityEndergrade entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getEntityTexture(EntityEndergrade entity) {
        return TEXTURE;
    }
}
