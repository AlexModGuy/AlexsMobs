package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTasmanianDevil;
import com.github.alexthe666.alexsmobs.entity.EntityTasmanianDevil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderTasmanianDevil extends MobRenderer<EntityTasmanianDevil, ModelTasmanianDevil> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tasmanian_devil.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/tasmanian_devil_eyes.png");

    public RenderTasmanianDevil(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelTasmanianDevil(), 0.3F);
        this.addLayer(new EyeLayer(this));
    }

    protected void scale(EntityTasmanianDevil entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getTextureLocation(EntityTasmanianDevil entity) {
        return TEXTURE;
    }

    class EyeLayer extends RenderLayer<EntityTasmanianDevil, ModelTasmanianDevil> {

        public EyeLayer(RenderTasmanianDevil render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityTasmanianDevil entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
          if(entitylivingbaseIn.getAnimation() == EntityTasmanianDevil.ANIMATION_HOWL && entitylivingbaseIn.getAnimationTick() < 34){
              VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYES));
              this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
          }
        }
    }
}