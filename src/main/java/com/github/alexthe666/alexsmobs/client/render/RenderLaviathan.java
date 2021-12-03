package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathanPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class RenderLaviathan extends MobRenderer<EntityLaviathan, ModelLaviathan> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/laviathan.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/laviathan_glow.png");
    private static final ResourceLocation TEXTURE_OBSIDIAN = new ResourceLocation("alexsmobs:textures/entity/laviathan_obsidian.png");
    private static final ResourceLocation TEXTURE_GEAR = new ResourceLocation("alexsmobs:textures/entity/laviathan_gear.png");
    private static final ResourceLocation TEXTURE_HELMET = new ResourceLocation("alexsmobs:textures/entity/laviathan_helmet.png");
    private static final float REINS_COLOR_R = 98F / 255F;
    private static final float REINS_COLOR_G = 77F / 255F;
    private static final float REINS_COLOR_B = 52F / 255F;
    private static final float REINS_COLOR_R2 = 58F / 255F;
    private static final float REINS_COLOR_G2 = 40F / 255F;
    private static final float REINS_COLOR_B2 = 34F / 255F;

    public RenderLaviathan(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelLaviathan(), 4.0F);
        this.addLayer(new LayerOverlays(this));
    }

    private static void addVertexPairAlex(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float) p_174321_ / 24.0F;
        int i = (int) Mth.lerp(f, (float) p_174313_, (float) p_174314_);
        int j = (int) Mth.lerp(f, (float) p_174315_, (float) p_174316_);
        int k = LightTexture.pack(i, j);
        float f2 = REINS_COLOR_R;
        float f3 = REINS_COLOR_G;
        float f4 = REINS_COLOR_B;
        if (p_174321_ % 2 == (p_174322_ ? 1 : 0)) {
            f2 = REINS_COLOR_R2;
            f3 = REINS_COLOR_G2;
            f4 = REINS_COLOR_B2;
        }
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.vertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
        p_174308_.vertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
    }

    public boolean shouldRender(EntityLaviathan livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            for (EntityLaviathanPart part : livingEntityIn.allParts) {
                if (camera.isVisible(part.getBoundingBox())) {
                    return true;
                }
            }
            return false;
        }
    }

    public void render(EntityLaviathan mob, float p_115456_, float partialTick, PoseStack ms, MultiBufferSource p_115459_, int p_115460_) {
        super.render(mob, p_115456_, partialTick, ms, p_115459_, p_115460_);
        Entity entity = mob.getControllingPassenger();
        if (entity != null) {
            double d0 = Mth.lerp(partialTick, mob.xOld, mob.getX());
            double d1 = Mth.lerp(partialTick, mob.yOld, mob.getY());
            double d2 = Mth.lerp(partialTick, mob.zOld, mob.getZ());
            ms.pushPose();
            ms.translate(-d0, -d1, -d2);
            this.renderRein(mob, partialTick, ms, p_115459_, entity, true);
            this.renderRein(mob, partialTick, ms, p_115459_, entity, false);
            ms.popPose();
        }
    }

    protected void scale(EntityLaviathan entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    protected boolean isShaking(EntityLaviathan entity) {
        return entity.isInWaterRainOrBubble() && !entity.isObsidian();
    }

    public ResourceLocation getTextureLocation(EntityLaviathan entity) {
        return entity.isObsidian() ? TEXTURE_OBSIDIAN : TEXTURE;
    }

    private float getHeadShakeForReins(EntityLaviathan mob, float partialTick) {
        float hh1 = mob.prevHeadHeight;
        float hh2 = mob.getHeadHeight();
        float rawHeadHeight = (hh1 + (hh2 - hh1) * partialTick) / 3F;
        float clampedNeckRot = Mth.clamp(-rawHeadHeight, -1, 1);
        float headStillProgress = 1F - Math.abs(clampedNeckRot);
        float swim = Mth.lerp(partialTick, mob.prevSwimProgress, mob.swimProgress);
        float limbSwingAmount = mob.animationSpeedOld + (mob.animationSpeed - mob.animationSpeedOld) * partialTick;
        float swing = mob.animationPosition + partialTick;
        float swingAmount = limbSwingAmount * swim * 0.2F * headStillProgress;
        float swimSpeed = mob.swimProgress >= 5F ? 0.3F : 0.9F;
        float swimDegree = 0.5F + swim * 0.05F;
        float boxOffset = (float) (-21 * 3.141592653589793D / (double) (2 * 3));
        float moveScale = 1;
        return 1.3F * Mth.cos(swing * swimSpeed * moveScale + boxOffset * (float) 2) * swingAmount * swimDegree * moveScale;
    }

    private float getHeadBobForReins(EntityLaviathan mob, float partialTick) {
        float swing = mob.tickCount + partialTick;
        float swingAmount = 1.0F;
        float idleSpeed = 0.04f;
        float idleDegree = 0.3f;
        float boxOffset = (float) (9 * 3.141592653589793D / (double) (2 * 3));
        float moveScale = 1;
        return 0.8F * Mth.cos(swing * idleSpeed * moveScale + boxOffset * (float) 2) * swingAmount * idleDegree * moveScale;
    }

    private <E extends Entity> void renderRein(EntityLaviathan mob, float partialTick, PoseStack p_115464_, MultiBufferSource p_115465_, E rider, boolean left) {
        p_115464_.pushPose();
        Entity head = mob.headPart;
        if (head == null) {
            return;
        }
        float limbSwingAmount = mob.animationSpeedOld + (mob.animationSpeed - mob.animationSpeedOld) * partialTick;
        float shake = getHeadShakeForReins(mob, partialTick);
        float headYaw = Math.abs(mob.getHeadYaw(partialTick)) / 50F;
        float headPitch = 1F - Math.abs((mob.prevHeadHeight + (mob.getHeadHeight() - mob.prevHeadHeight) * partialTick) / 3F);
        float yawAdd = (1F - headYaw) * 0.4F * (1F - limbSwingAmount * 0.7F) - headPitch * 0.2F;
        Vec3 vec3 = rider instanceof LivingEntity ? getReinPosition((LivingEntity) rider, partialTick, left, shake) : rider.getRopeHoldPosition(partialTick);
        double d0 = (double) (Mth.lerp(partialTick, mob.yBodyRot, mob.yBodyRotO) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
        Vec3 vec31 = new Vec3((left ? -0.05F - yawAdd : 0.05F + yawAdd) + shake, 0.45F - headYaw * 0.2F + getHeadBobForReins(mob, partialTick), 0.1F);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp(partialTick, head.xo, head.getX()) + d1;
        double d4 = Mth.lerp(partialTick, head.yo, head.getY()) + vec31.y;
        double d5 = Mth.lerp(partialTick, head.zo, head.getZ()) + d2;
        p_115464_.translate(d3, d4, d5);
        float f = (float) (vec3.x - d3);
        float f1 = (float) (vec3.y - d4);
        float f2 = (float) (vec3.z - d5);
        float f3 = 0.025F;
        VertexConsumer vertexconsumer = p_115465_.getBuffer(RenderType.leash());
        Matrix4f matrix4f = p_115464_.last().pose();
        float f4 = Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = new BlockPos(mob.getEyePosition(partialTick));
        BlockPos blockpos1 = new BlockPos(rider.getEyePosition(partialTick));
        int i = this.getBlockLightLevel(mob, blockpos);
        int j = mob.level.getBrightness(LightLayer.BLOCK, blockpos1);
        int k = mob.level.getBrightness(LightLayer.SKY, blockpos);
        int l = mob.level.getBrightness(LightLayer.SKY, blockpos1);
        float width = 0.05F;
        for (int i1 = 0; i1 <= 24; ++i1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, i1, false);
        }
        for (int j1 = 24; j1 >= 0; --j1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, j1, true);
        }
        p_115464_.popPose();
    }

    private Vec3 getReinPosition(LivingEntity entity, float p_36374_, boolean left, float shake) {
        double d0 = 0.4D * (left ? -1.0D : 1.0D) - 0;
        float f = Mth.lerp(p_36374_ * 0.5F, entity.getXRot(), entity.xRotO) * ((float) Math.PI / 180F);
        float f1 = Mth.lerp(p_36374_, entity.yBodyRotO, entity.yBodyRot) * ((float) Math.PI / 180F);
        if (!entity.isFallFlying() && !entity.isAutoSpinAttack()) {
            if (entity.isVisuallySwimming()) {
                return entity.getPosition(p_36374_).add((new Vec3(d0, 0.3D, -0.34D)).xRot(-f).yRot(-f1));
            } else {
                double d5 = entity.getBoundingBox().getYsize() - 1.0D;
                double d6 = entity.isCrouching() ? -0.2D : 0.07D;
                return entity.getPosition(p_36374_).add((new Vec3(d0, d5, d6)).yRot(-f1));
            }
        } else {
            Vec3 vec3 = entity.getViewVector(p_36374_);
            Vec3 vec31 = entity.getDeltaMovement();
            double d1 = vec31.horizontalDistanceSqr();
            double d2 = vec3.horizontalDistanceSqr();
            float f2;
            if (d1 > 0.0D && d2 > 0.0D) {
                double d3 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d1 * d2);
                double d4 = vec31.x * vec3.z - vec31.z * vec3.x;
                f2 = (float) (Math.signum(d4) * Math.acos(d3));
            } else {
                f2 = 0.0F;
            }

            return entity.getPosition(p_36374_).add((new Vec3(d0, -0.11D, 0.85D)).zRot(-f2).xRot(-f).yRot(-f1));
        }
    }

    class LayerOverlays extends RenderLayer<EntityLaviathan, ModelLaviathan> {

        public LayerOverlays(RenderLaviathan render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityLaviathan laviathan, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!laviathan.isObsidian()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_GLOW));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (laviathan.hasBodyGear()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_GEAR));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (laviathan.hasHeadGear()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_HELMET));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

    }
}
