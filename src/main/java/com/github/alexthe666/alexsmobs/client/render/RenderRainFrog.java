package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRainFrog;
import com.github.alexthe666.alexsmobs.entity.EntityRainFrog;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderRainFrog extends MobRenderer<EntityRainFrog, ModelRainFrog> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/rain_frog_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/rain_frog_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/rain_frog_2.png");

    public RenderRainFrog(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelRainFrog(), 0.2F);
    }

    protected void scale(EntityRainFrog entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    public ResourceLocation getTextureLocation(EntityRainFrog entity) {
        return entity.getVariant() == 2 ? TEXTURE_2 : entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
