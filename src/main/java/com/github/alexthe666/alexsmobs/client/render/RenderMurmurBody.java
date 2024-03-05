package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMurmurBody;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurHead;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderMurmurBody extends MobRenderer<EntityMurmur, ModelMurmurBody> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/murmur.png");
    public static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/murmur_angry.png");
    public static boolean renderWithHead = false;
    private static final ModelMurmurNeck NECK_MODEL = new ModelMurmurNeck();
    private static final ModelMurmurHead HEAD_MODEL = new ModelMurmurHead();

    public RenderMurmurBody(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelMurmurBody(), 0.5F);
    }

    protected void scale(EntityMurmur entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    public void render(EntityMurmur body, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(body, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (renderWithHead || body.shouldRenderFakeHead()) {
            float f = Mth.rotLerp(partialTicks, body.yBodyRotO, body.yBodyRot);
            float f7 = this.getBob(body, partialTicks);
            ResourceLocation loc = this.getTextureLocation(body);
            int overlayCoords = getOverlayCoords(body, this.getWhiteOverlayProgress(body, partialTicks));
            matrixStackIn.pushPose();
            this.setupRotations(body, matrixStackIn, f7, f, partialTicks);
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, -2.9F, 0);
            scale(body, matrixStackIn, partialTicks);
            HEAD_MODEL.resetToDefaultPose();
            HEAD_MODEL.animateHair(f7);
            HEAD_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(loc)), packedLightIn, overlayCoords, 1, 1F, 1, 1);
            matrixStackIn.translate(0, 0.5F, 0);
            NECK_MODEL.resetToDefaultPose();
            NECK_MODEL.setAttributes(0.5F, 0, 0, 0);
            NECK_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(loc)), packedLightIn, overlayCoords, 1, 1F, 1, 1);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    public ResourceLocation getTextureLocation(EntityMurmur entity) {
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
