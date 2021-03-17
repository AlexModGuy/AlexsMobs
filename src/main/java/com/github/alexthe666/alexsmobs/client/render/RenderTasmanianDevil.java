package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTasmanianDevil;
import com.github.alexthe666.alexsmobs.entity.EntityTasmanianDevil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderTasmanianDevil extends MobRenderer<EntityTasmanianDevil, ModelTasmanianDevil> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tasmanian_devil.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/tasmanian_devil_eyes.png");

    public RenderTasmanianDevil(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelTasmanianDevil(), 0.3F);
        this.addLayer(new EyeLayer(this));
    }

    protected void preRenderCallback(EntityTasmanianDevil entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getEntityTexture(EntityTasmanianDevil entity) {
        return TEXTURE;
    }

    class EyeLayer extends LayerRenderer<EntityTasmanianDevil, ModelTasmanianDevil> {

        public EyeLayer(RenderTasmanianDevil render) {
            super(render);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityTasmanianDevil entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
          if(entitylivingbaseIn.getAnimation() == EntityTasmanianDevil.ANIMATION_HOWL && entitylivingbaseIn.getAnimationTick() < 20){
              IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(TEXTURE_EYES));
              this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
          }
        }
    }
}