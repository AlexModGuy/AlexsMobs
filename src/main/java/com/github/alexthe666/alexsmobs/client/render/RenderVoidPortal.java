package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class RenderVoidPortal extends EntityRenderer<EntityVoidPortal> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/portal/portal_idle_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/portal/portal_idle_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/portal/portal_idle_2.png");
    private static final ResourceLocation[] TEXTURE_PROGRESS = new ResourceLocation[10];
    public RenderVoidPortal(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn);
        for(int i = 0; i < 10; i++){
            TEXTURE_PROGRESS[i] = new ResourceLocation("alexsmobs:textures/entity/portal/portal_grow_" + i + ".png");
        }
    }

    public void render(EntityVoidPortal entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(entityIn.getAttachmentFacing().getOpposite().getRotation());
        matrixStackIn.translate(0.5D, 0, 0.5D);
        ResourceLocation tex;
        if(entityIn.getLifespan() < 20){
            tex = getGrowingTexture((int) ((entityIn.getLifespan() * 0.5F) % 10));
        }else if(entityIn.tickCount < 20){
            tex = getGrowingTexture((int) ((entityIn.tickCount * 0.5F) % 10));
        }else{
            tex = getIdleTexture(entityIn.tickCount % 9);
        }
        matrixStackIn.scale(2, 2, 2);
        renderArc(matrixStackIn, bufferIn, tex);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private void renderArc(PoseStack matrixStackIn, MultiBufferSource bufferIn, ResourceLocation res) {
        matrixStackIn.pushPose();

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getGhost(res));
        PoseStack.Pose lvt_19_1_ = matrixStackIn.last();
        Matrix4f lvt_20_1_ = lvt_19_1_.pose();
        Matrix3f lvt_21_1_ = lvt_19_1_.normal();
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0, 0, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0, 1, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1, 1, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1, 0, 1, 0, 1, 240);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityVoidPortal entity) {
        return TEXTURE_0;
    }


    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, VertexConsumer p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.vertex(p_229039_1_, (float) p_229039_4_, (float) p_229039_5_, (float) p_229039_6_).color(255, 255, 255, 255).uv(p_229039_7_, p_229039_8_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229039_12_).normal(p_229039_2_, (float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_).endVertex();
    }


    public ResourceLocation getIdleTexture(int age) {
        if (age < 3) {
            return TEXTURE_0;
        } else if (age < 6) {
            return TEXTURE_1;
        } else if (age < 10) {
            return TEXTURE_2;
        } else {
            return TEXTURE_0;
        }
    }

    public ResourceLocation getGrowingTexture(int age) {
        return TEXTURE_PROGRESS[Mth.clamp(age, 0, 9)];
    }
}
