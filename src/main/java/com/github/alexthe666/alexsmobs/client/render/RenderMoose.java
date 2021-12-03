package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMoose;
import com.github.alexthe666.alexsmobs.client.model.ModelMoose;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderMoose extends MobRenderer<EntityMoose, ModelMoose> {
    private static final ResourceLocation TEXTURE_ANTLERED = new ResourceLocation("alexsmobs:textures/entity/moose_antlered.png");
    private static final ResourceLocation TEXTURE_SNOWY = new ResourceLocation("alexsmobs:textures/entity/moose_snowy.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/moose.png");

    public RenderMoose(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelMoose(), 0.8F);
        this.addLayer(new LayerSnow());
    }

    protected void scale(EntityMoose entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }


    public ResourceLocation getTextureLocation(EntityMoose entity) {
        return entity.isAntlered() && !entity.isBaby() ? TEXTURE_ANTLERED : TEXTURE;
    }


    class LayerSnow extends RenderLayer<EntityMoose, ModelMoose> {

        public LayerSnow() {
            super(RenderMoose.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityMoose entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isSnowy()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_SNOWY));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
