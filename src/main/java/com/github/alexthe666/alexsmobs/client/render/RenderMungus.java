package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMungus;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;

public class RenderMungus extends MobRenderer<EntityMungus, ModelMungus> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mungus.png");
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mungus_beam.png");
    private static final ResourceLocation TEXTURE_BEAM_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/mungus_beam_overlay.png");
    private static final ResourceLocation TEXTURE_SACK_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/mungus_sack.png");
    private static final ResourceLocation TEXTURE_SHOES = new ResourceLocation("alexsmobs:textures/entity/mungus_shoes.png");
    private static final RenderType beamType = AMRenderTypes.getMungusBeam(BEAM_TEXTURE);

    public RenderMungus(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelMungus(0), 0.5F);
        this.addLayer(new MungusSackLayer(this));
        this.addLayer(new MungusMushroomLayer(this));
    }

    protected boolean isShaking(EntityMungus mungus) {
        return mungus.isReverting();
    }

    private static void vertex(VertexConsumer p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float p_229108_9_, float p_229108_10_) {
        p_229108_0_.vertex(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).uv(p_229108_9_, p_229108_10_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    protected void setupRotations(EntityMungus entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (entityLiving.deathTime > 0) {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
            float f = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f * -90));
        } else {
            super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        }
    }

    protected float getFlipDegrees(EntityMungus p_77037_1_) {
        return 0F;
    }

    protected void scale(EntityMungus entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        String s = ChatFormatting.stripFormatting(entitylivingbaseIn.getName().getString());
        if (s != null && s.toLowerCase().contains("drip")) {
            matrixStackIn.translate(0F,  entitylivingbaseIn.isBaby() ? -0.075F : -0.15F, 0F);
        }
    }

    public boolean shouldRender(EntityMungus livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.getBeamTarget() != null) {
                BlockPos pos = livingEntityIn.getBeamTarget();
                if (pos != null) {
                    Vec3 vector3d = Vec3.atLowerCornerOf(pos);
                    Vec3 vector3dCorner = Vec3.atLowerCornerOf(pos).add(1, 1, 1);
                    Vec3 vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight(), 1.0F);
                    return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z))
                            || camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3dCorner.x, vector3dCorner.y, vector3dCorner.z));
                }
            }

            return false;
        }
    }

    private Vec3 getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
        double d0 = Mth.lerp(p_177110_4_, entityLivingBaseIn.xOld, entityLivingBaseIn.getX());
        double d1 = Mth.lerp(p_177110_4_, entityLivingBaseIn.yOld, entityLivingBaseIn.getY()) + p_177110_2_;
        double d2 = Mth.lerp(p_177110_4_, entityLivingBaseIn.zOld, entityLivingBaseIn.getZ());
        return new Vec3(d0, d1, d2);
    }

    public void render(EntityMungus entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        BlockPos target = entityIn.getBeamTarget();
        if (target != null) {
            float f = 1.0F;
            float f1 = (float) entityIn.level.getGameTime() + partialTicks;
            float f2 = -1.0F * (f1 * 0.15F % 1.0F);
            float f3 = 1.13F;
            if(entityIn.isBaby()){
                f3 = 0.555F;
            }
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0D, f3, 0.0D);
            Vec3 vector3d = Vec3.upFromBottomCenterOf(target, 0.15F);
            Vec3 vector3d1 = this.getPosition(entityIn, f3, partialTicks);
            Vec3 vector3d2 = vector3d.subtract(vector3d1);
            float f4 = (float) (vector3d2.length());
            vector3d2 = vector3d2.normalize();
            float f5 = (float) Math.acos(vector3d2.y);
            float f6 = (float) Math.atan2(vector3d2.z, vector3d2.x);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * 1.5F;
            float f8 = 1F;
            int j = (int) (f8 * 255.0F);
            int k = (int) (f8 * 255.0F);
            int l = (int) (f8 * 255.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(0 + 2.3561945F) * 0.8F;
            float f12 = Mth.sin(0 + 2.3561945F) * 0.8F;
            float f13 = Mth.cos(0 + ((float) Math.PI / 4F)) * 0.8F;
            float f14 = Mth.sin(0 + ((float) Math.PI / 4F)) * 0.8F;
            float f15 = Mth.cos(0 + 3.926991F) * 0.8F;
            float f16 = Mth.sin(0 + 3.926991F) * 0.8F;
            float f17 = Mth.cos(0 + 5.4977875F) * 0.8F;
            float f18 = Mth.sin(0 + 5.4977875F) * 0.8F;
            float f19 = Mth.cos(0 + (float) Math.PI) * 0.4F;
            float f20 = Mth.sin(0 + (float) Math.PI) * 0.4F;
            float f21 = Mth.cos(0 + 0.0F) * 0.4F;
            float f22 = Mth.sin(0 + 0.0F) * 0.4F;
            float f23 = Mth.cos(0 + ((float) Math.PI / 2F)) * 0.4F;
            float f24 = Mth.sin(0 + ((float) Math.PI / 2F)) * 0.4F;
            float f25 = Mth.cos(0 + ((float) Math.PI * 1.5F)) * 0.4F;
            float f26 = Mth.sin(0 + ((float) Math.PI * 1.5F)) * 0.4F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 0.5F + f29;
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(beamType);
            PoseStack.Pose matrixstack$entry = matrixStackIn.last();
            Matrix4f matrix4f = matrixstack$entry.pose();
            Matrix3f matrix3f = matrixstack$entry.normal();
            vertex(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (entityIn.tickCount % 4 > 1) {
                f31 = 0.5F;
            }

            vertex(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            matrixStackIn.popPose();
        }

    }

    public ResourceLocation getTextureLocation(EntityMungus entity) {
        return TEXTURE;
    }

    class MungusSackLayer extends RenderLayer<EntityMungus, ModelMungus> {

        public MungusSackLayer(RenderMungus p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityMungus entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer lead = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE_SACK_OVERLAY, 0));
            float alpha = 0.75F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.125F;
            this.getParentModel().renderToBuffer(matrixStackIn, lead, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
            if (entitylivingbaseIn.getBeamTarget() != null) {
                VertexConsumer beam = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE_BEAM_OVERLAY, 0));
                float beamAlpha = 0.75F + (Mth.cos(ageInTicks * 1) + 1F) * 0.125F;
                this.getParentModel().renderToBuffer(matrixStackIn, beam, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, beamAlpha);
            }
            String s = ChatFormatting.stripFormatting(entitylivingbaseIn.getName().getString());
            if (s != null && s.toLowerCase().contains("drip")) {
                VertexConsumer shoeBuffer = bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_SHOES));
                matrixStackIn.pushPose();
                this.getParentModel().renderShoes();
                this.getParentModel().renderToBuffer(matrixStackIn, shoeBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                this.getParentModel().postRenderShoes();
                matrixStackIn.popPose();
            }
        }
    }

    class MungusMushroomLayer extends RenderLayer<EntityMungus, ModelMungus> {

        public MungusMushroomLayer(RenderMungus p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityMungus entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
            BlockState blockstate = entitylivingbaseIn.getMushroomState();
            if (blockstate == null) {
                return;
            }
            int i = LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F);
            boolean altOrder = entitylivingbaseIn.isAltOrderMushroom();
            int mushroomCount = entitylivingbaseIn.getMushroomCount();
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0D);
            }
            matrixStackIn.pushPose();
            translateToBody(matrixStackIn);
            if (mushroomCount == 1 && !altOrder || mushroomCount >= 2) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.2F, -1.4F, 0.15D);
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                blockrendererdispatcher.renderSingleBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
                matrixStackIn.popPose();
            }
            if (mushroomCount == 1 && altOrder || mushroomCount >= 2) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(-0.2F, -1.5F, -0.2D);
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                blockrendererdispatcher.renderSingleBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
                matrixStackIn.popPose();
            }
            if (mushroomCount >= 3) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.76F, -0.4F, 0.1D);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90F));
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                blockrendererdispatcher.renderSingleBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
                matrixStackIn.popPose();
            }
            if (mushroomCount >= 4) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(-0.76F, -1.0F, 0.1D);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-60F));
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                blockrendererdispatcher.renderSingleBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
                matrixStackIn.popPose();
            }
            if (mushroomCount >= 5) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(-0.76F, -0.1F, 0.1D);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-100F));
                matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
                blockrendererdispatcher.renderSingleBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
                matrixStackIn.popPose();
            }
            matrixStackIn.popPose();
            matrixStackIn.popPose();

        }

        protected void translateToBody(PoseStack matrixStack) {
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);
        }
    }

}
