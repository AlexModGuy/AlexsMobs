package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSquidGrapple;
import com.github.alexthe666.alexsmobs.entity.EntitySquidGrapple;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderSquidGrapple extends EntityRenderer<EntitySquidGrapple> {
    private static final ResourceLocation SQUID_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/giant_squid.png");
    private static final ModelSquidGrapple SQUID_MODEL = new ModelSquidGrapple();
    private static final float TENTACLES_COLOR_R = 181F / 255F;
    private static final float TENTACLES_COLOR_G = 87F / 255F;
    private static final float TENTACLES_COLOR_B = 85F / 255F;
    private static final float TENTACLES_COLOR_R2 = 191F / 255F;
    private static final float TENTACLES_COLOR_G2 = 98F / 255F;
    private static final float TENTACLES_COLOR_B2 = 89F / 255F;

    public RenderSquidGrapple(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    private static void addVertexPairAlex(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float) p_174321_ / 24.0F;
        int i = (int) Mth.lerp(f, (float) p_174313_, (float) p_174314_);
        int j = (int) Mth.lerp(f, (float) p_174315_, (float) p_174316_);
        int k = LightTexture.pack(i, j);
        float f2 = TENTACLES_COLOR_R;
        float f3 = TENTACLES_COLOR_G;
        float f4 = TENTACLES_COLOR_B;
        if (p_174321_ % 2 == (p_174322_ ? 1 : 0)) {
            f2 = TENTACLES_COLOR_R2;
            f3 = TENTACLES_COLOR_G2;
            f4 = TENTACLES_COLOR_B2;
        }
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.vertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
        p_174308_.vertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
    }

    public static <E extends Entity> void renderTentacle(Entity mob, float partialTick, PoseStack p_115464_, MultiBufferSource p_115465_, LivingEntity player, boolean left, float zOffset) {
        p_115464_.pushPose();
        float bodyRot = mob instanceof LivingEntity ? ((LivingEntity) mob).yBodyRot : mob.getYRot();
        float bodyRot0 = mob instanceof LivingEntity ? ((LivingEntity) mob).yBodyRotO : mob.yRotO;
        Vec3 vec3 = player.getRopeHoldPosition(partialTick);
        double d0 = (double) (Mth.lerp(partialTick, bodyRot, bodyRot0) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
        Vec3 vec31 = new Vec3(0, 0F, 0);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp(partialTick, mob.xo, mob.getX()) + d1;
        double d4 = Mth.lerp(partialTick, mob.yo, mob.getY()) + vec31.y;
        double d5 = Mth.lerp(partialTick, mob.zo, mob.getZ()) + d2;
        p_115464_.translate(d3, d4, d5);
        float f = (float) (vec3.x - d3);
        float f1 = (float) (vec3.y - d4);
        float f2 = (float) (vec3.z - d5);
        float f3 = 0.025F;
        VertexConsumer vertexconsumer = p_115465_.getBuffer(RenderType.leash());
        Matrix4f matrix4f = p_115464_.last().pose();
        float f4 = (float) (Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F);
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = AMBlockPos.fromVec3(mob.getEyePosition(partialTick));
        BlockPos blockpos1 = AMBlockPos.fromVec3(player.getEyePosition(partialTick));
        int i = getTentacleLightLevel(mob, blockpos);
        int j = mob.level().getBrightness(LightLayer.BLOCK, blockpos1);
        int k = mob.level().getBrightness(LightLayer.SKY, blockpos);
        int l = mob.level().getBrightness(LightLayer.SKY, blockpos1);
        float width = 0.2F;
        for (int i1 = 0; i1 <= 24; ++i1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, i1, false);
        }
        for (int j1 = 24; j1 >= 0; --j1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, j1, true);
        }
        p_115464_.popPose();
    }

    protected static int getTentacleLightLevel(Entity p_114496_, BlockPos p_114497_) {
        return p_114496_.isOnFire() ? 15 : p_114496_.level().getBrightness(LightLayer.BLOCK, p_114497_);
    }

    public boolean shouldRender(EntitySquidGrapple grapple, Frustum f, double d1, double d2, double d3) {
        return super.shouldRender(grapple, f, d1, d2, d3) || grapple.getOwner() != null && (f.isVisible(grapple.getOwner().getBoundingBox()) || grapple.getOwner() == Minecraft.getInstance().player);
    }

    public void render(EntitySquidGrapple entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        matrixStackIn.translate(0, -1.5F, -0.25F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entityIn)));
        SQUID_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (entityIn.getOwner() instanceof LivingEntity) {
            LivingEntity holder = (LivingEntity) entityIn.getOwner();
            double d0 = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
            double d1 = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
            double d2 = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
            matrixStackIn.pushPose();
            matrixStackIn.translate(-d0, -d1, -d2);
            renderTentacle(entityIn, partialTicks, matrixStackIn, bufferIn, holder, holder.getMainArm() != HumanoidArm.LEFT, -0.1F);
            matrixStackIn.popPose();
        }
    }

    public ResourceLocation getTextureLocation(EntitySquidGrapple entity) {
        return SQUID_TEXTURE;
    }

    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, VertexConsumer p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.vertex(p_229039_1_, (float) p_229039_4_, (float) p_229039_5_, (float) p_229039_6_).color(255, 255, 255, 255).uv(p_229039_7_, p_229039_8_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229039_12_).normal(p_229039_2_, (float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_).endVertex();
    }
}
