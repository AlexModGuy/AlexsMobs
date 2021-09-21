package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelShoebill;
import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderShoebill extends MobRenderer<EntityShoebill, ModelShoebill> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/shoebill.png");

    public RenderShoebill(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelShoebill(), 0.3F);
    }

    protected void scale(EntityShoebill entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getTextureLocation(EntityShoebill entity) {
        return TEXTURE;
    }
}
