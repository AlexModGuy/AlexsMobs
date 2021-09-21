package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCrocodile extends MobRenderer<EntityCrocodile, ModelCrocodile> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/crocodile_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/crocodile_1.png");

    public RenderCrocodile(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelCrocodile(), 0.8F);
    }

    protected void scale(EntityCrocodile entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }


    public ResourceLocation getTextureLocation(EntityCrocodile entity) {
        return entity.isDesert() ? TEXTURE_1 : TEXTURE_0;
    }
}
