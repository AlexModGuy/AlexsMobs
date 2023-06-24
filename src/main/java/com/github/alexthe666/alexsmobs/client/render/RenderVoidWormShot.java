package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderVoidWormShot extends EntityRenderer<EntityVoidWormShot> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm/void_worm_shot.png");
    private static ModelVoidWormShot MODEL = new ModelVoidWormShot();

    public RenderVoidWormShot(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityVoidWormShot entity) {
        return TEXTURE;
    }

    @Override
    public void render(EntityVoidWormShot entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose((new Quaternionf()).rotateX(Maths.rad(180)));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        matrixStackIn.pushPose();
        MODEL.animate(entityIn, entityIn.tickCount + partialTicks);
        float home = (entityIn.prevStopHomingProgress + (entityIn.getStopHomingProgress() - entityIn.prevStopHomingProgress) * partialTicks) / EntityVoidWormShot.HOME_FOR;
        float colorize = home;
        matrixStackIn.translate(0, -1.5F, 0);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getFullBright(getTextureLocation(entityIn)));
        MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, 210, NO_OVERLAY, Math.max(colorize, 0.2F), Math.max(colorize, 0.2F), 1.0F, 1.0F);
        matrixStackIn.popPose();
        matrixStackIn.popPose();


    }

}
