package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelHammerheadShark;
import com.github.alexthe666.alexsmobs.entity.EntityHammerheadShark;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderHammerheadShark extends MobRenderer<EntityHammerheadShark, ModelHammerheadShark> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/hammerhead_shark.png");

    public RenderHammerheadShark(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelHammerheadShark(), 0.8F);
    }

    protected void scale(EntityHammerheadShark entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getTextureLocation(EntityHammerheadShark entity) {
        return TEXTURE;
    }
}
