package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSunbird;
import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class RenderSunbird extends MobRenderer<EntitySunbird, ModelSunbird> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/sunbird.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/sunbird_glow.png");

    public RenderSunbird(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSunbird(), 0.5F);
        this.addLayer(new LayerScorch(this));
    }

    private static void vertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, float p_114095_, int p_114096_, int p_114097_) {
        p_114090_.vertex(p_114091_, p_114094_, p_114095_, 0.0F).color(255, 255, 255, 100).uv((float) p_114096_, (float) p_114097_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114093_).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public void render(EntitySunbird entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        super.render(entity, yaw, partialTicks, poseStack, buffer, light);
        float ageInTicks = entity.tickCount + partialTicks;
        float scale = (12.0F + (float) Math.sin(ageInTicks * 0.3F)) * entity.getScorchProgress(partialTicks);
        if(scale > 0.0F) {
            poseStack.pushPose();
            poseStack.translate(0, entity.getBbHeight() * 0.5F, 0);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(ageInTicks * 8F));
            poseStack.translate(-scale * 0.5F, -scale * 0.5F, 0);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            VertexConsumer vertexconsumer = buffer.getBuffer(AMRenderTypes.getSunbirdShine());
            vertex(vertexconsumer, matrix4f, matrix3f, light, 0.0F, 0, 0, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, light, scale, 0, 1, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, light, scale, scale, 1, 0);
            vertex(vertexconsumer, matrix4f, matrix3f, light, 0.0F, scale, 0, 0);
            poseStack.popPose();
            poseStack.popPose();
        }
    }

    protected void scale(EntitySunbird entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    protected int getBlockLightLevel(EntitySunbird entityIn, BlockPos partialTicks) {
        return 15;
    }

    public ResourceLocation getTextureLocation(EntitySunbird entity) {
        return TEXTURE;
    }

    class LayerScorch extends RenderLayer<EntitySunbird, ModelSunbird> {

        public LayerScorch(RenderSunbird p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySunbird entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer scorch = bufferIn.getBuffer(AMRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            float alpha = entitylivingbaseIn.getScorchProgress(partialTicks);
            this.getParentModel().renderToBuffer(matrixStackIn, scorch, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}
