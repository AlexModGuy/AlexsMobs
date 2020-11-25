package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEndergrade;
import com.github.alexthe666.alexsmobs.client.model.ModelHammerheadShark;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerEndergradeSaddle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityHammerheadShark;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderHammerheadShark extends MobRenderer<EntityHammerheadShark, ModelHammerheadShark> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/hammerhead_shark.png");

    public RenderHammerheadShark(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelHammerheadShark(), 0.8F);
    }

    protected void preRenderCallback(EntityHammerheadShark entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getEntityTexture(EntityHammerheadShark entity) {
        return TEXTURE;
    }
}
