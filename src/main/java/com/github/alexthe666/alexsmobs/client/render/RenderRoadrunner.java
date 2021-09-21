package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderRoadrunner extends MobRenderer<EntityRoadrunner, ModelRoadrunner> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/roadrunner.png");

    public RenderRoadrunner(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelRoadrunner(), 0.3F);
    }

    protected void scale(EntityRoadrunner entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      //  matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getTextureLocation(EntityRoadrunner entity) {
        return TEXTURE;
    }
}
