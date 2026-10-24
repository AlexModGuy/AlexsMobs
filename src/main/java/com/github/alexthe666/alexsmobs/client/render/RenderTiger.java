package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerTigerEyes;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

public class RenderTiger extends MobRenderer<EntityTiger, ModelTiger> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("alexsmobs:textures/entity/tiger/tiger.png");
    private static final ResourceLocation TEXTURE_ANGRY = ResourceLocation
            .parse("alexsmobs:textures/entity/tiger/tiger_angry.png");
    private static final ResourceLocation TEXTURE_SLEEPING = ResourceLocation
            .parse("alexsmobs:textures/entity/tiger/tiger_sleeping.png");
    private static final ResourceLocation TEXTURE_WHITE = ResourceLocation
            .parse("alexsmobs:textures/entity/tiger/tiger_white.png");
    private static final ResourceLocation TEXTURE_ANGRY_WHITE = ResourceLocation
            .parse("alexsmobs:textures/entity/tiger/tiger_white_angry.png");
    private static final ResourceLocation TEXTURE_SLEEPING_WHITE = ResourceLocation
            .parse("alexsmobs:textures/entity/tiger/tiger_white_sleeping.png");

    public RenderTiger(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTiger(), 0.6F);
        this.addLayer(new LayerTigerEyes(this));
    }

    @Override
    protected void scale(EntityTiger entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EntityTiger tiger, boolean b0, boolean b1, boolean b2) {
        if (tiger.isStealth()) {
            ResourceLocation resourcelocation = this.getTextureLocation(tiger);
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else {
            return super.getRenderType(tiger, b0, b1, b2);
        }
    }

    public ResourceLocation getTextureLocation(EntityTiger entity) {
        if (entity.isSleeping()) {
            return entity.isWhite() ? TEXTURE_SLEEPING_WHITE : TEXTURE_SLEEPING;
        } else if (entity.getRemainingPersistentAngerTime() > 0) {
            return entity.isWhite() ? TEXTURE_ANGRY_WHITE : TEXTURE_ANGRY;
        } else {
            return entity.isWhite() ? TEXTURE_WHITE : TEXTURE;
        }
    }
}
