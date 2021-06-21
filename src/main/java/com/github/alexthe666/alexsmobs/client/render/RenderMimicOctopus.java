package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class RenderMimicOctopus extends MobRenderer<EntityMimicOctopus, ModelMimicOctopus> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus.png");
    private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_overlay.png");
    private static final ResourceLocation TEXTURE_CREEPER = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_creeper.png");
    private static final ResourceLocation TEXTURE_GUARDIAN = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_guardian.png");
    private static final ResourceLocation TEXTURE_PUFFERFISH = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_pufferfish.png");
    private static final ResourceLocation TEXTURE_MIMICUBE = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_mimicube.png");
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType field_229107_h_ = RenderType.getEntityCutoutNoCull(GUARDIAN_BEAM_TEXTURE);

    public RenderMimicOctopus(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelMimicOctopus(), 0.4F);
        this.addLayer(new OverlayLayer(this));
    }

    private static void func_229108_a_(IVertexBuilder p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float p_229108_9_, float p_229108_10_) {
        p_229108_0_.pos(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).tex(p_229108_9_, p_229108_10_).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public void render(EntityMimicOctopus entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        LivingEntity livingentity = entityIn.getGuardianLaser();
        if (livingentity != null) {
            float f = entityIn.getLaserAttackAnimationScale(partialTicks);
            float f1 = (float) entityIn.world.getGameTime() + partialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = entityIn.getEyeHeight();
            matrixStackIn.push();
            matrixStackIn.translate(0.0D, f3, 0.0D);
            Vector3d vector3d = this.getPosition(livingentity, (double) livingentity.getHeight() * 0.5D, partialTicks);
            Vector3d vector3d1 = this.getPosition(entityIn, f3, partialTicks);
            Vector3d vector3d2 = vector3d.subtract(vector3d1);
            float f4 = (float) (vector3d2.length() + 1.0D);
            vector3d2 = vector3d2.normalize();
            float f5 = (float) Math.acos(vector3d2.y);
            float f6 = (float) Math.atan2(vector3d2.z, vector3d2.x);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int) (f8 * 191.0F);
            int k = 32 + (int) (f8 * 191.0F);
            int l = 128 - (int) (f8 * 64.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = MathHelper.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = MathHelper.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = MathHelper.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
            float f14 = MathHelper.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
            float f15 = MathHelper.cos(f7 + 3.926991F) * 0.282F;
            float f16 = MathHelper.sin(f7 + 3.926991F) * 0.282F;
            float f17 = MathHelper.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = MathHelper.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = MathHelper.cos(f7 + (float) Math.PI) * 0.2F;
            float f20 = MathHelper.sin(f7 + (float) Math.PI) * 0.2F;
            float f21 = MathHelper.cos(f7 + 0.0F) * 0.2F;
            float f22 = MathHelper.sin(f7 + 0.0F) * 0.2F;
            float f23 = MathHelper.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
            float f24 = MathHelper.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
            float f25 = MathHelper.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
            float f26 = MathHelper.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(field_229107_h_);
            MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
            Matrix4f matrix4f = matrixstack$entry.getMatrix();
            Matrix3f matrix3f = matrixstack$entry.getNormal();
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (entityIn.ticksExisted % 2 == 0) {
                f31 = 0.5F;
            }

            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            matrixStackIn.pop();
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

    }

    protected void preRenderCallback(EntityMimicOctopus octo, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.translate(0, -0.02F, 0);
        matrixStackIn.scale(0.9F * octo.getRenderScale(), 0.9F * octo.getRenderScale(), 0.9F * octo.getRenderScale());
    }

    public boolean shouldRender(EntityMimicOctopus livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.hasGuardianLaser()) {
                LivingEntity livingentity = livingEntityIn.getGuardianLaser();
                if (livingentity != null) {
                    Vector3d vector3d = this.getPosition(livingentity, (double) livingentity.getHeight() * 0.5D, 1.0F);
                    Vector3d vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight(), 1.0F);
                    return camera.isBoundingBoxInFrustum(new AxisAlignedBB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
                }
            }

            return false;
        }
    }

    private Vector3d getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
        double d0 = MathHelper.lerp(p_177110_4_, entityLivingBaseIn.lastTickPosX, entityLivingBaseIn.getPosX());
        double d1 = MathHelper.lerp(p_177110_4_, entityLivingBaseIn.lastTickPosY, entityLivingBaseIn.getPosY()) + p_177110_2_;
        double d2 = MathHelper.lerp(p_177110_4_, entityLivingBaseIn.lastTickPosZ, entityLivingBaseIn.getPosZ());
        return new Vector3d(d0, d1, d2);
    }


    public ResourceLocation getEntityTexture(EntityMimicOctopus entity) {
        return TEXTURE;
    }

    class OverlayLayer extends LayerRenderer<EntityMimicOctopus, ModelMimicOctopus> {


        public OverlayLayer(RenderMimicOctopus render) {
            super(render);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, EntityMimicOctopus entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float transProgress = entitylivingbaseIn.prevTransProgress + (entitylivingbaseIn.transProgress - entitylivingbaseIn.prevTransProgress) * partialTicks;
            float colorProgress = (entitylivingbaseIn.prevColorShiftProgress + (entitylivingbaseIn.colorShiftProgress - entitylivingbaseIn.prevColorShiftProgress) * partialTicks) * 0.2F;
            float r = 1F;
            float g = 1F;
            float b = 1F;
            float a = 1F;
            float startR = 1.0F;
            float startG = 1.0F;
            float startB = 1.0F;
            float startA = 1.0F;
            float finR = 1.0F;
            float finG = 1.0F;
            float finB = 1.0F;
            float finA = 1.0F;
            if (entitylivingbaseIn.getPrevMimicState() == EntityMimicOctopus.MimicState.OVERLAY) {
                if (entitylivingbaseIn.getPrevMimickedBlock() != null) {
                    int j = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getPrevMimickedBlock());
                    startR = (float) (j >> 16 & 255) / 255.0F;
                    startG = (float) (j >> 8 & 255) / 255.0F;
                    startB = (float) (j & 255) / 255.0F;
                } else {
                    startA = 0.0F;
                }
            }
            if ((entitylivingbaseIn.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY)) {
                if (entitylivingbaseIn.getMimickedBlock() != null) {
                    int i = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getMimickedBlock());
                    finR = (float) (i >> 16 & 255) / 255.0F;
                    finG = (float) (i >> 8 & 255) / 255.0F;
                    finB = (float) (i & 255) / 255.0F;
                } else {
                    finA = 0.0F;
                }
                r = startR + (finR - startR) * colorProgress;
                g = startG + (finG - startG) * colorProgress;
                b = startB + (finB - startB) * colorProgress;
                a = startA + (finA - startA) * colorProgress;
            }
            if (a == 1.0F) {
                a *= 0.9F + 0.1F * (float) Math.sin(entitylivingbaseIn.ticksExisted * 0.1F);
            }
            if (entitylivingbaseIn.getPrevMimicState() != null) {
                float alphaPrev = 1 - transProgress * 0.2F;
                IVertexBuilder prev = buffer.getBuffer(AMRenderTypes.getEntityTranslucent(getFor(entitylivingbaseIn.getPrevMimicState())));
                if(entitylivingbaseIn.getPrevMimicState() == entitylivingbaseIn.getMimicState()){
                    alphaPrev *= a;
                }
                this.getEntityModel().render(matrixStackIn, prev, packedLightIn, getPackedOverlay(entitylivingbaseIn, 0), r, g, b, alphaPrev);
            }
            float alphaCurrent = transProgress * 0.2F;
            IVertexBuilder current = buffer.getBuffer(AMRenderTypes.getEntityTranslucent(getFor(entitylivingbaseIn.getMimicState())));
            this.getEntityModel().render(matrixStackIn, current, packedLightIn, getPackedOverlay(entitylivingbaseIn, 0), r, g, b, a * alphaCurrent);
        }

        public ResourceLocation getFor(EntityMimicOctopus.MimicState state) {
            if (state == EntityMimicOctopus.MimicState.CREEPER) {
                return TEXTURE_CREEPER;
            }
            if (state == EntityMimicOctopus.MimicState.GUARDIAN) {
                return TEXTURE_GUARDIAN;
            }
            if (state == EntityMimicOctopus.MimicState.PUFFERFISH) {
                return TEXTURE_PUFFERFISH;
            }
            if (state == EntityMimicOctopus.MimicState.MIMICUBE) {
                return TEXTURE_MIMICUBE;
            }
            return TEXTURE_OVERLAY;
        }
    }
}
