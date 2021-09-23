package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFroststalker;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderFroststalker extends MobRenderer<EntityFroststalker, ModelFroststalker> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/froststalker.png");
    private static final ResourceLocation TEXTURE_NOSPIKES = new ResourceLocation("alexsmobs:textures/entity/froststalker_nospikes.png");

    public RenderFroststalker(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFroststalker(), 0.4F);
    }

    protected void scale(EntityFroststalker entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getTextureLocation(EntityFroststalker entity) {
        return entity.hasSpikes() ? TEXTURE : TEXTURE_NOSPIKES;
    }
}
