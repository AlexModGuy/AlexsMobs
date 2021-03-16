package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelLobster;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderLobster extends MobRenderer<EntityLobster, ModelLobster> {
    private static final ResourceLocation TEXTURE_RED = new ResourceLocation("alexsmobs:textures/entity/lobster_red.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("alexsmobs:textures/entity/lobster_blue.png");
    private static final ResourceLocation TEXTURE_YELLOW = new ResourceLocation("alexsmobs:textures/entity/lobster_yellow.png");
    private static final ResourceLocation TEXTURE_REDBLUE = new ResourceLocation("alexsmobs:textures/entity/lobster_redblue.png");

    public RenderLobster(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelLobster(), 0.25F);
    }

    protected void preRenderCallback(EntityLobster entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getEntityTexture(EntityLobster entity) {
        switch (entity.getVariant()){
            case 1:
                return TEXTURE_BLUE;
            case 2:
                return TEXTURE_YELLOW;
            case 3:
                return TEXTURE_REDBLUE;
            default:
                return TEXTURE_RED;
        }
    }
}
