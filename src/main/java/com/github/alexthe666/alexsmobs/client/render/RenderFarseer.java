package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFarseer;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.client.event.RenderNameTagEvent;

import javax.annotation.Nullable;

public class RenderFarseer extends MobRenderer<EntityFarseer, ModelFarseer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer_angry.png");
    private static final ResourceLocation TEXTURE_CLAWS = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer_claws.png");
    private static final ResourceLocation TEXTURE_EYE = new ResourceLocation("alexsmobs:textures/entity/farseer/farseer_eye.png");
    private static final ResourceLocation[] PORTAL_TEXTURES = new ResourceLocation[]{
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_0.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_1.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_2.png"),
        new ResourceLocation("alexsmobs:textures/entity/farseer/portal_3.png")};

    public RenderFarseer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFarseer(), 0.9F);
        this.addLayer(new LayerOverlay());
    }

    public void render(EntityFarseer entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<EntityFarseer, ModelFarseer>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn)))
            return;
        float faceCameraAmount = entityIn.getFacingCameraAmount(partialTicks);
        Quaternion camera = this.entityRenderDispatcher.cameraOrientation().copy();
        camera.mul(faceCameraAmount);

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
        matrixStackIn.mulPose(camera);
        if(faceCameraAmount != 0){
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        }
        this.setupRotations(entityIn, matrixStackIn, f7, f, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0.0D, -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = Mth.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
            f5 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
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
        if (rendertype != null) {
            float portalLevel = entityIn.getFarseerOpacity(partialTicks);
            this.shadowRadius = 0.9F * portalLevel;
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(rendertype);
            int i = getOverlayCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : Mth.clamp(portalLevel, 0, 1));
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
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            int portalTexture = Mth.clamp(entityIn.getPortalFrame(), 0, PORTAL_TEXTURES.length - 1);
            VertexConsumer portalStatic = VertexMultiConsumer.create(bufferIn.getBuffer(AMRenderTypes.STATIC_OVERLAY), bufferIn.getBuffer(RenderType.entityTranslucent(PORTAL_TEXTURES[portalTexture])));
            float portalAlpha =  entityIn.getPortalOpacity(partialTicks);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1, portalAlpha);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1, portalAlpha);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0, portalAlpha);
            portalVertex(portalStatic, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0, portalAlpha);
            matrixStackIn.popPose();
        }
    }

    @Override
    protected void setupRotations(EntityFarseer farseer, PoseStack matrixStackIn, float f1, float f2, float f3) {
        float invCameraAmount = 1F - farseer.getFacingCameraAmount(Minecraft.getInstance().getFrameTime());

        if (this.isShaking(farseer)) {
            f2 += (float)(Math.cos((double)farseer.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }

        if (!farseer.hasPose(Pose.SLEEPING)) {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((180.0F - f2 * invCameraAmount)));
        }

        if (farseer.deathTime > 0) {
            float f = ((float)farseer.deathTime + f3 - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(farseer) * invCameraAmount) );
        } else if (isEntityUpsideDown(farseer)) {
            matrixStackIn.translate(0.0D, (double)(farseer.getBbHeight() + 0.1F), 0.0D);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        }

    }

    private static void portalVertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, int p_114095_, int p_114096_, int p_114097_, float alpha) {
        p_114090_.vertex(p_114091_, p_114094_ - 0.5F, (float)p_114095_ - 0.25F, 0.0F).color(1F, 1F, 1F,  alpha).uv((float)p_114096_, (float)p_114097_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114093_).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
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
