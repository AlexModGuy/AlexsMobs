package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityMudBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RenderMudBall extends EntityRenderer<EntityMudBall> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mud_ball.png");

    public RenderMudBall(EntityRendererProvider.Context p_173962_) {
        super(p_173962_);
    }

    public void render(EntityMudBall entityMudBall, float f, float f2, PoseStack p_114083_, MultiBufferSource p_114084_, int p_114085_) {
        p_114083_.pushPose();
        p_114083_.scale(0.7F, 0.7F, 0.7F);
        p_114083_.mulPose(this.entityRenderDispatcher.cameraOrientation());
        p_114083_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        PoseStack.Pose $$6 = p_114083_.last();
        Matrix4f $$7 = $$6.pose();
        Matrix3f $$8 = $$6.normal();
        VertexConsumer $$9 = p_114084_.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        vertex($$9, $$7, $$8, p_114085_, 0.0F, 0, 0, 1);
        vertex($$9, $$7, $$8, p_114085_, 1.0F, 0, 1, 1);
        vertex($$9, $$7, $$8, p_114085_, 1.0F, 1, 1, 0);
        vertex($$9, $$7, $$8, p_114085_, 0.0F, 1, 0, 0);
        p_114083_.popPose();
        super.render(entityMudBall, f, f2, p_114083_, p_114084_, p_114085_);
    }

    private static void vertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, int p_114095_, int p_114096_, int p_114097_) {
        p_114090_.vertex(p_114091_, p_114094_ - 0.5F, (float)p_114095_ - 0.25F, 0.0F).color(255, 255, 255, 255).uv((float)p_114096_, (float)p_114097_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114093_).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(EntityMudBall mudball) {
        return TEXTURE;
  }
}
