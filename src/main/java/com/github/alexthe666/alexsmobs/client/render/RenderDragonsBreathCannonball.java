package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityDragonsBreathCannonball;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderDragonsBreathCannonball extends EntityRenderer<EntityDragonsBreathCannonball> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");

    public RenderDragonsBreathCannonball(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(EntityDragonsBreathCannonball cannonball, BlockPos pos) {
        return 15;
    }

    public void render(EntityDragonsBreathCannonball cannonball, float f, float f2, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose $$6 = poseStack.last();
        Matrix4f $$7 = $$6.pose();
        Matrix3f $$8 = $$6.normal();
        VertexConsumer $$9 = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_LOCATION));
        vertex($$9, $$7, $$8, i, 0.0F, 0, 0, 1);
        vertex($$9, $$7, $$8, i, 1.0F, 0, 1, 1);
        vertex($$9, $$7, $$8, i, 1.0F, 1, 1, 0);
        vertex($$9, $$7, $$8, i, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(cannonball, f, f2, poseStack, bufferSource, i);
    }

    private static void vertex(VertexConsumer p_254095_, Matrix4f p_254477_, Matrix3f p_253948_, int p_253829_, float p_253995_, int p_254031_, int p_253641_, int p_254243_) {
        p_254095_.vertex(p_254477_, p_253995_ - 0.5F, (float)p_254031_ - 0.25F, 0.0F).color(255, 255, 255, 255).uv((float)p_253641_, (float)p_254243_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_253829_).normal(p_253948_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(EntityDragonsBreathCannonball p_114078_) {
        return TEXTURE_LOCATION;
    }

}
