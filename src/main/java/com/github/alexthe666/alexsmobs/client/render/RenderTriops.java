package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaiman;
import com.github.alexthe666.alexsmobs.client.model.ModelTriops;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityDevilsHolePupfish;
import com.github.alexthe666.alexsmobs.entity.EntityTriops;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderTriops extends MobRenderer<EntityTriops, ModelTriops> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/triops.png");

    public RenderTriops(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTriops(), 0.2F);
    }

    protected void scale(EntityTriops entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float scale = entitylivingbaseIn.getTriopsScale();
        if(entitylivingbaseIn.isBaby()){
            scale *= 0.65F;
        }
        matrixStackIn.scale(scale, scale, scale);
    }

    public ResourceLocation getTextureLocation(EntityTriops entity) {
        return TEXTURE;
    }
}
