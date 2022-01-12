package com.github.alexthe666.alexsmobs.client.render;
import com.github.alexthe666.alexsmobs.client.model.ModelPollenBall;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderPollenBall extends EntityRenderer<EntityPollenBall> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/pollen_ball.png");
    private static final ModelPollenBall MODEL_POLLEN_BALL = new ModelPollenBall();
    public RenderPollenBall(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityPollenBall entity) {
        return TEXTURE;
    }

    @Override
    public void render(EntityPollenBall entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double)-0.25F, 0.0D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180F));
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 0.5F, 0);
        matrixStackIn.scale(1F, 1F, 1F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getFullBright(getTextureLocation(entityIn)));
        MODEL_POLLEN_BALL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

}
