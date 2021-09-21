package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSunbird;
import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class RenderSunbird extends MobRenderer<EntitySunbird, ModelSunbird> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/sunbird.png");

    public RenderSunbird(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelSunbird(), 0.5F);
    }

    protected void scale(EntitySunbird entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
    }

    protected int getBlockLightLevel(EntitySunbird entityIn, BlockPos partialTicks) {
        return 15;
    }

    public ResourceLocation getTextureLocation(EntitySunbird entity) {
        return TEXTURE;
    }
}
