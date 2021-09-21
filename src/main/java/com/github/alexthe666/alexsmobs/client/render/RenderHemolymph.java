package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityHemolymph;
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
import com.mojang.math.Vector3f;

public class RenderHemolymph extends EntityRenderer<EntityHemolymph> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/hemolymph.png");

    public RenderHemolymph(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(EntityHemolymph p_225623_1_, float p_225623_2_, float p_225623_3_, PoseStack p_225623_4_, MultiBufferSource p_225623_5_, int p_225623_6_) {
        p_225623_4_.pushPose();
        p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(p_225623_3_, p_225623_1_.yRotO, p_225623_1_.yRot) - 90.0F));
        p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(p_225623_3_, p_225623_1_.xRotO, p_225623_1_.xRot)));
        float lvt_17_1_ = 0;
        if (lvt_17_1_ > 0.0F) {
            float lvt_18_1_ = -Mth.sin(lvt_17_1_ * 3.0F) * lvt_17_1_;
            p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(lvt_18_1_));
        }

        p_225623_4_.mulPose(Vector3f.XP.rotationDegrees(45.0F));
        p_225623_4_.scale(0.05625F, 0.05625F, 0.05625F);
        p_225623_4_.translate(-4.0D, 0.0D, 0.0D);
        VertexConsumer lvt_18_2_ = p_225623_5_.getBuffer(RenderType.entityCutout(this.getTextureLocation(p_225623_1_)));
        PoseStack.Pose lvt_19_1_ = p_225623_4_.last();
        Matrix4f lvt_20_1_ = lvt_19_1_.pose();
        Matrix3f lvt_21_1_ = lvt_19_1_.normal();
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, 240);

        for(int lvt_22_1_ = 0; lvt_22_1_ < 4; ++lvt_22_1_) {
            p_225623_4_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, 240);
        }

        p_225623_4_.popPose();
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }

    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, VertexConsumer p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.vertex(p_229039_1_, (float)p_229039_4_, (float)p_229039_5_, (float)p_229039_6_).color(255, 255, 255, 255).uv(p_229039_7_, p_229039_8_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229039_12_).normal(p_229039_2_, (float)p_229039_9_, (float)p_229039_11_, (float)p_229039_10_).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityHemolymph entity) {
        return TEXTURE;
    }
}
