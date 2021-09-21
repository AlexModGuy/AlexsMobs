package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBoneSerpentHead;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderBoneSerpent extends MobRenderer<EntityBoneSerpent, ModelBoneSerpentHead> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/bone_serpent_head.png");

    public RenderBoneSerpent(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelBoneSerpentHead(), 0.3F);
    }

    protected void scale(EntityBoneSerpent entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      //  matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getTextureLocation(EntityBoneSerpent entity) {
        return TEXTURE;
    }
}
