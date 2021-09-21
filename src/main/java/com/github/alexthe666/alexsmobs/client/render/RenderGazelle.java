package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelGazelle;
import com.github.alexthe666.alexsmobs.entity.EntityGazelle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderGazelle extends MobRenderer<EntityGazelle, ModelGazelle> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/gazelle.png");

    public RenderGazelle(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelGazelle(), 0.4F);
    }

    protected void scale(EntityGazelle entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getTextureLocation(EntityGazelle entity) {
        return TEXTURE;
    }
}
