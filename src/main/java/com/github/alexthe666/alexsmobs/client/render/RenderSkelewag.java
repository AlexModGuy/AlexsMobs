package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSkelewag;
import com.github.alexthe666.alexsmobs.client.model.ModelSunbird;
import com.github.alexthe666.alexsmobs.entity.EntitySkelewag;
import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class RenderSkelewag extends MobRenderer<EntitySkelewag, ModelSkelewag> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/skelewag_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/skelewag_1.png");

    public RenderSkelewag(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSkelewag(), 0.5F);
    }

    protected void scale(EntitySkelewag entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    protected int getBlockLightLevel(EntitySkelewag entityIn, BlockPos partialTicks) {
        return Math.max(2, super.getBlockLightLevel(entityIn, partialTicks));
    }


    public ResourceLocation getTextureLocation(EntitySkelewag entity) {
        return entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
