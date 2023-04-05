package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTasmanianDevil;
import com.github.alexthe666.alexsmobs.entity.EntityTasmanianDevil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderTasmanianDevil extends MobRenderer<EntityTasmanianDevil, ModelTasmanianDevil> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tasmanian_devil.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tasmanian_devil_angry.png");

    public RenderTasmanianDevil(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTasmanianDevil(), 0.3F);
    }

    protected void scale(EntityTasmanianDevil entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getTextureLocation(EntityTasmanianDevil entity) {
        return entity.getAnimation() == EntityTasmanianDevil.ANIMATION_HOWL && entity.getAnimationTick() < 34 ? TEXTURE_ANGRY : TEXTURE;
    }

}