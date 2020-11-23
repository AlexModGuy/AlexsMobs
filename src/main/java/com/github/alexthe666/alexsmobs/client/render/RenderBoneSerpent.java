package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBoneSerpentHead;
import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderBoneSerpent extends MobRenderer<EntityBoneSerpent, ModelBoneSerpentHead> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/bone_serpent_head.png");

    public RenderBoneSerpent(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelBoneSerpentHead(), 0.3F);
    }

    protected void preRenderCallback(EntityBoneSerpent entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      //  matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getEntityTexture(EntityBoneSerpent entity) {
        return TEXTURE;
    }
}
