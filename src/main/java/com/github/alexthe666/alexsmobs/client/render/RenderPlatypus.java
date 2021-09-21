package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelPlatypus;
import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderPlatypus extends MobRenderer<EntityPlatypus, ModelPlatypus> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/platypus.png");
    private static final ResourceLocation TEXTURE_PERRY = new ResourceLocation("alexsmobs:textures/entity/platypus_perry.png");

    public RenderPlatypus(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelPlatypus(), 0.45F);
        this.addLayer(new FedoraLayer(this));
    }

    protected void scale(EntityPlatypus entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
         matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    public ResourceLocation getTextureLocation(EntityPlatypus entity) {
        return entity.isPerry() ? TEXTURE_PERRY : TEXTURE;
    }

    class FedoraLayer extends RenderLayer<EntityPlatypus, ModelPlatypus> {
        private final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/platypus_fedora.png");

        public FedoraLayer(RenderPlatypus renderGrizzlyBear) {
            super(renderGrizzlyBear);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityPlatypus entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(entitylivingbaseIn.hasFedora()){
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutout(TEXTURE));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
