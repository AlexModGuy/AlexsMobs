package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelDropBear;
import com.github.alexthe666.alexsmobs.entity.EntityDropBear;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderDropBear extends MobRenderer<EntityDropBear, ModelDropBear> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/dropbear.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/dropbear_eyes.png");

    public RenderDropBear(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelDropBear(), 0.7F);
        this.addLayer(new EyeLayer(this));
    }

    protected void scale(EntityDropBear entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getTextureLocation(EntityDropBear entity) {
        return TEXTURE;
    }

    class EyeLayer extends RenderLayer<EntityDropBear, ModelDropBear> {

        public EyeLayer(RenderDropBear render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityDropBear entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);

        }
    }
}