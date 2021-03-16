package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSunbird;
import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class RenderSunbird extends MobRenderer<EntitySunbird, ModelSunbird> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/sunbird.png");

    public RenderSunbird(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelSunbird(), 0.5F);
    }

    protected void preRenderCallback(EntitySunbird entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
    }

    protected int getBlockLight(EntitySunbird entityIn, BlockPos partialTicks) {
        return 15;
    }

    public ResourceLocation getEntityTexture(EntitySunbird entity) {
        return TEXTURE;
    }
}
