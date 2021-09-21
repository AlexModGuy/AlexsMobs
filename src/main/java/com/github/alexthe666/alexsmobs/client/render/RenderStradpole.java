package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelStradpole;
import com.github.alexthe666.alexsmobs.entity.EntityStradpole;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderStradpole extends MobRenderer<EntityStradpole, ModelStradpole> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/stradpole.png");

    public RenderStradpole(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelStradpole(), 0.25F);
    }

    protected void scale(EntityStradpole entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        //matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getTextureLocation(EntityStradpole entity) {
        return TEXTURE;
    }
}
