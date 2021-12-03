package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelLobster;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderLobster extends MobRenderer<EntityLobster, ModelLobster> {
    private static final ResourceLocation TEXTURE_RED = new ResourceLocation("alexsmobs:textures/entity/lobster_red.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("alexsmobs:textures/entity/lobster_blue.png");
    private static final ResourceLocation TEXTURE_YELLOW = new ResourceLocation("alexsmobs:textures/entity/lobster_yellow.png");
    private static final ResourceLocation TEXTURE_REDBLUE = new ResourceLocation("alexsmobs:textures/entity/lobster_redblue.png");
    private static final ResourceLocation TEXTURE_BLACK = new ResourceLocation("alexsmobs:textures/entity/lobster_black.png");
    private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("alexsmobs:textures/entity/lobster_white.png");

    public RenderLobster(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelLobster(), 0.25F);
    }

    protected void scale(EntityLobster entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getTextureLocation(EntityLobster entity) {
        switch (entity.getVariant()){
            case 1:
                return TEXTURE_BLUE;
            case 2:
                return TEXTURE_YELLOW;
            case 3:
                return TEXTURE_REDBLUE;
            case 4:
                return TEXTURE_BLACK;
            case 5:
                return TEXTURE_WHITE;
            default:
                return TEXTURE_RED;
        }
    }
}
