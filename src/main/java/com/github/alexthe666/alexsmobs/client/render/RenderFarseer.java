package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFarseer;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderNameTagEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderFarseer extends MobRenderer<EntityFarseer, ModelFarseer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer_angry.png");
    private static final ResourceLocation TEXTURE_CLAWS = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer_claws.png");
    private static final ResourceLocation TEXTURE_EYE = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer_eye.png");
    private static final ResourceLocation TEXTURE_SCARS = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer_scars.png");
    private static final ResourceLocation[] PORTAL_TEXTURES = new ResourceLocation[]{
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_0.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_1.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_2.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_3.png")};
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0D) / 2.0D);
    private static final ModelFarseer EYE_MODEL = new ModelFarseer(0.1f);
    private static final ModelFarseer SCARS_MODEL = new ModelFarseer(0.05f);
    private static final ModelFarseer AFTERIMAGE_MODEL = new ModelFarseer(0.05f);

    public RenderFarseer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFarseer(0.0F), 0.9F);
        this.addLayer(new LayerOverlay());
    }

    public boolean shouldRender(EntityFarseer livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.hasLaser()) {
                LivingEntity livingentity = livingEntityIn.getLaserTarget();
                if (livingentity != null) {
                    Vec3 vector3d = this.getPosition(livingentity, (double) livingentity.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight(), 1.0F);
                    return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
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

    public void render(EntityFarseer entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<EntityFarseer, ModelFarseer>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn)))
            return;
        LivingEntity laserTarget = entityIn.getLaserTarget();
        float faceCameraAmount = entityIn.getFacingCameraAmount(partialTicks);
        Quaternionf camera = this.entityRenderDispatcher.cameraOrientation();

        matrixStackIn.pushPose();
        this.model.attackTime = this.getAttackAnim(entityIn, partialTicks);

        boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = entityIn.isBaby();
        float f = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float f1 = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entityIn.getVehicle();
            f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        if (entityIn.getPose() == Pose.SLEEPING) {
            Direction direction = entityIn.getBedOrientation();
            if (direction != null) {
                float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStackIn.translate((float) (-direction.getStepX()) * f4, 0.0D, (float) (-direction.getStepZ()) * f4);
            }
        }

        float f7 = this.getBob(entityIn, partialTicks);
        if(faceCameraAmount != 0){
            matrixStackIn.mulPose(camera);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
        }
        this.setupRotations(entityIn, matrixStackIn, f7, f, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0.0D, -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = entityIn.walkAnimation.position(partialTicks);
            f5 = entityIn.walkAnimation.position() - entityIn.walkAnimation.speed() * (1.0F - partialTicks);
            if (entityIn.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.model.prepareMobModel(entityIn, f5, f8, partialTicks);
        this.model.setupAnim(entityIn, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(entityIn);
        boolean flag1 = !flag && !entityIn.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entityIn);
        RenderType rendertype = this.getRenderType(entityIn, flag, flag1, flag2);
        EYE_MODEL.setupAnim(entityIn, f5, f8, f7, f2, f6);
        SCARS_MODEL.setupAnim(entityIn, f5, f8, f7, f2, f6);
        AFTERIMAGE_MODEL.setupAnim(entityIn, f5, f8, f7, f2, f6);
        if (rendertype != null) {
            float portalLevel = entityIn.getFarseerOpacity(partialTicks);
            this.shadowRadius = 0.9F * portalLevel;
            int i = getOverlayCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));
            this.renderFarseerModel(matrixStackIn, bufferIn, rendertype, partialTicks, packedLightIn, i, flag1 ? 0.15F : Mth.clamp(portalLevel, 0, 1), entityIn);
        }
        if (!entityIn.isSpectator()) {
            for (RenderLayer layerrenderer : this.layers) {
                layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
            }
        }

        matrixStackIn.popPose();
        RenderNameTagEvent renderNameplateEvent = new RenderNameTagEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
        if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(entityIn))) {
            this.renderNameTag(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<EntityFarseer, ModelFarseer>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));

        //emergence portal
        if(entityIn.getAnimation() == EntityFarseer.ANIMATION_EMERGE){
            matrixStackIn.pushPose();
            matrixStackIn.scale(3.0F, 3.0F, 3.0F);
            matrixStackIn.mulPose(camera);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            int portalTexture = Mth.clamp(entityIn.getPortalFrame(), 0, PORTAL_TEXTURES.length - 1);
            VertexConsumer portalStatic = AMRenderTypes.createMergedVertexConsumer(bufferIn.getBuffer(AMRenderTypes.STATIC_PORTAL), bufferIn.getBuffer(RenderType.entityTranslucent(PORTAL_TEXTURES[portalTexture])));
            float portalAlpha =  entityIn.getPortalOpacity(partialTicks);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1, portalAlpha);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1, portalAlpha);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0, portalAlpha);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0, portalAlpha);
            matrixStackIn.popPose();
        }
        //laser target
        if(entityIn.hasLaser() && laserTarget != null && !laserTarget.isRemoved()){
            float laserProgress = (entityIn.prevLaserLvl + (entityIn.getLaserAttackLvl() - entityIn.prevLaserLvl) * partialTicks) / (float)EntityFarseer.LASER_ATTACK_DURATION;
            float laserHeight = entityIn.getEyeHeight();
            //float angryProgress = entityIn.prevAngryProgress + (entityIn.angryProgress - entityIn.prevAngryProgress) * partialTicks;
            Vec3 angryShake = Vec3.ZERO;
            double d0 = Mth.lerp(partialTicks, laserTarget.xo, laserTarget.getX()) - Mth.lerp(partialTicks, entityIn.xo, entityIn.getX()) - angryShake.x;
            double d1 = Mth.lerp(partialTicks, laserTarget.yo, laserTarget.getY()) + laserTarget.getEyeHeight() - Mth.lerp(partialTicks, entityIn.yo, entityIn.getY()) - angryShake.y - laserHeight;
            double d2 = Mth.lerp(partialTicks, laserTarget.zo, laserTarget.getZ()) - Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ()) - angryShake.z;
            double d4 = Math.sqrt(d0 * d0 + d2 * d2);
            float laserY = (float) (Mth.atan2(d2, d0) * (double) Mth.RAD_TO_DEG) - 90.0F;
            float laserX = (float) (-(Mth.atan2(d1, d4) * (double) Mth.RAD_TO_DEG));
            VertexConsumer beamStatic = bufferIn.getBuffer(AMRenderTypes.getFarseerBeam());
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, laserHeight, 0);
            matrixStackIn.mulPose(Axis.YN.rotationDegrees(laserY));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(laserX));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));
            float length = entityIn.getLaserDistance() * laserProgress;
            float width = (1.5F - laserProgress) * 2F;
            float speed = 1F + laserProgress * laserProgress * 5F;
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            int j = 255;
            //apparently its static? should be moving so has to be moved manually through UV
            long systemTime = Util.getMillis() * 7L;
            float u = (float)(systemTime % 30000L) / 30000.0F;
            float v = (float)Math.floor((systemTime % 3000L) / 3000.0F * 4.0F) * 0.25F + (float)Math.sin(systemTime / 30000F) * 0.05F + ((float)(systemTime % 20000L) / 20000.0F * speed);
            laserOriginVertex(beamStatic, matrix4f, matrix3f, j, u, v);
            laserLeftCornerVertex(beamStatic, matrix4f, matrix3f, length, width,  u, v);
            laserRightCornerVertex(beamStatic, matrix4f, matrix3f, length, width,  u, v);
            laserLeftCornerVertex(beamStatic, matrix4f, matrix3f, length, width,  u, v);

            matrixStackIn.popPose();
        }

    }

    private void renderFarseerModel(PoseStack matrixStackIn, MultiBufferSource source, RenderType defRenderType, float partialTicks, int packedLightIn, int overlayColors, float alphaIn, EntityFarseer entityIn) {
        if(entityIn.hasLaser()){
            VertexConsumer staticyInsides = AMRenderTypes.createMergedVertexConsumer(source.getBuffer(AMRenderTypes.STATIC_ENTITY), source.getBuffer(RenderType.entityTranslucent(TEXTURE_EYE)));
            EYE_MODEL.renderToBuffer(matrixStackIn, staticyInsides, packedLightIn, NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1F);
        }
        VertexConsumer consumer;
        float hurt = Math.max(entityIn.hurtTime, entityIn.deathTime);
        float defAlpha = alphaIn * 0.2F;
        float afterimageSpeed = 0.3F;
        if(hurt > 0){
            afterimageSpeed = Math.min(hurt / 20F, 1F) + 0.3F;
            VertexConsumer staticyScars = AMRenderTypes.createMergedVertexConsumer(source.getBuffer(AMRenderTypes.STATIC_ENTITY), source.getBuffer(RenderType.entityTranslucent(TEXTURE_SCARS)));
            SCARS_MODEL.renderToBuffer(matrixStackIn, staticyScars, packedLightIn, overlayColors, 1.0F, 1.0F, 1.0F, 0.3F);
        }
        this.model.renderToBuffer(matrixStackIn, source.getBuffer(defRenderType), packedLightIn, overlayColors, 1.0F, 1.0F, 1.0F, alphaIn);

        matrixStackIn.pushPose();
        matrixStackIn.popPose();

        AFTERIMAGE_MODEL.eye.showModel = false;
        RenderType afterimage = RenderType.entityTranslucentEmissive(this.getTextureLocation(entityIn));
        Vec3 colorOffset = entityIn.getLatencyOffsetVec(10, partialTicks).scale(-0.2F).add(entityIn.angryShakeVec.scale(0.3F));
        Vec3 redOffset = colorOffset.add(entityIn.calculateAfterimagePos(partialTicks, false, afterimageSpeed));
        Vec3 blueOffset = colorOffset.add(entityIn.calculateAfterimagePos(partialTicks, true, afterimageSpeed));
        float scale = (float) Mth.clamp(colorOffset.length() * 0.1F, 0, 1F);
        float angryProgress = entityIn.prevAngryProgress + (entityIn.angryProgress - entityIn.prevAngryProgress) * partialTicks;
        float afterimageAlpha1 = defAlpha * Math.max(((float) Math.sin((entityIn.tickCount + partialTicks) * 0.2F) + 1F) * 0.3F, angryProgress * 0.2F);
        float afterimageAlpha2 = defAlpha * Math.max(((float) Math.cos((entityIn.tickCount + partialTicks) * 0.2F) + 1F) * 0.3F, angryProgress * 0.2F);

        matrixStackIn.pushPose();
        matrixStackIn.scale(scale + 1F, scale + 1F, scale + 1F);
        matrixStackIn.pushPose();
        matrixStackIn.translate(redOffset.x, redOffset.y, redOffset.z);
        AFTERIMAGE_MODEL.renderToBuffer(matrixStackIn, source.getBuffer(afterimage), 240, overlayColors, 1.0F, 0F, 0F, afterimageAlpha1);
        matrixStackIn.popPose();
        matrixStackIn.pushPose();
        matrixStackIn.translate(blueOffset.x, blueOffset.y, blueOffset.z);
        AFTERIMAGE_MODEL.renderToBuffer(matrixStackIn, source.getBuffer(afterimage), 240, overlayColors, 0F, 0F, 1.0F, afterimageAlpha2);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
        AFTERIMAGE_MODEL.eye.showModel = true;
    }

    private static void laserOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, int p_114222_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(255, 255, 255, 255).uv(xOffset + 0.5F, yOffset).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void laserLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -HALF_SQRT_3 * p_114218_, p_114217_, 0).color(255, 255, 255, 0).uv(xOffset, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void laserRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, HALF_SQRT_3 * p_114227_, p_114226_, 0).color(255, 255, 255, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void portalVertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, int p_114095_, int p_114096_, int p_114097_, float alpha) {
        p_114090_.vertex(p_114091_, p_114094_ - 0.5F, (float)p_114095_ - 0.25F, 0.0F).color(1F, 1F, 1F,  alpha).uv((float)p_114096_, (float)p_114097_).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }
    @Override
    protected void setupRotations(EntityFarseer farseer, PoseStack matrixStackIn, float f1, float f2, float f3) {
        float invCameraAmount = 1F - farseer.getFacingCameraAmount(Minecraft.getInstance().getFrameTime());

        if (this.isShaking(farseer)) {
            f2 += (float)(Math.cos((double)farseer.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }

        if (!farseer.hasPose(Pose.SLEEPING)) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees((180.0F - f2 * invCameraAmount)));
        }

        if (farseer.deathTime > 0) {
            float f = ((float)farseer.deathTime + f3 - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * this.getFlipDegrees(farseer) * invCameraAmount) );
        } else if (isEntityUpsideDown(farseer)) {
            matrixStackIn.translate(0.0D, (double)(farseer.getBbHeight() + 0.1F), 0.0D);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }

    }


    @Nullable
    protected RenderType getRenderType(EntityFarseer farseer, boolean normal, boolean invis, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(farseer);
        if (invis || farseer.getAnimation() == EntityFarseer.ANIMATION_EMERGE) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return this.model.renderType(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    public ResourceLocation getTextureLocation(EntityFarseer entity) {
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }

    class LayerOverlay extends RenderLayer<EntityFarseer, ModelFarseer> {

        public LayerOverlay() {
            super(RenderFarseer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityFarseer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.getAnimation() == EntityFarseer.ANIMATION_EMERGE) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_CLAWS));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }

        }
    }
}
