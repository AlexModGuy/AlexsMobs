package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateRigging;
import com.github.alexthe666.alexsmobs.entity.EntityEndPirateRigging;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderEndPirateRigging extends EntityRenderer<EntityEndPirateRigging> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/rigging.png");
    private static ModelEndPirateRigging MODEL = new ModelEndPirateRigging();

    private static final float ROPE_COLOR_R = 149F / 255F;
    private static final float ROPE_COLOR_G = 0;
    private static final float ROPE_COLOR_B = 235F / 255F;
    private static final float ROPE_COLOR_R2 = 101F / 255F;
    private static final float ROPE_COLOR_G2 = 0;
    private static final float ROPE_COLOR_B2 = 168F / 255F;

    public RenderEndPirateRigging(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    public boolean shouldRender(EntityEndPirateRigging entity, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(entity, camera, camX, camY, camZ)) {
            return true;
        } else {
            Entity connection = entity.getConnection();
            if (connection != null) {
                Vec3 vector3d = connection.getPosition(1.0F);
                Vec3 vector3d1 = entity.getPosition(1.0F);
                return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z).inflate(2, 2, 2));
            }
            return false;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityEndPirateRigging entity) {
        return TEXTURE;
    }

    @Override
    public void render(EntityEndPirateRigging entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        Entity connection = entityIn.getConnection();
        matrixStackIn.pushPose();
        matrixStackIn.mulPose((new Quaternionf()).rotateX((float) Math.toRadians(180)));
        matrixStackIn.pushPose();
        MODEL.animate(entityIn.getAttachmentRotation());
        matrixStackIn.translate(0, -1.5F, 0);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(ForgeRenderTypes.getUnlitTranslucent(getTextureLocation(entityIn)));
        MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
        if(connection != null){
            Vec3 position = entityIn.getPosition(partialTicks);
            Vec3 connectionToVec = connection.getRopeHoldPosition(partialTicks).subtract(position);
            matrixStackIn.pushPose();
            renderRope(new Vec3(0, entityIn.getBbHeight() * 0.5F, 0), partialTicks, matrixStackIn, bufferIn, connectionToVec);
            matrixStackIn.popPose();
        }
    }
    private static void addVertexPairAlex(VertexConsumer consumer, Matrix4f matrix4f, float xDiff, float yDiff, float zDiff, int lighting1, int lighting2, int lighting3, int lighting4, float yVertex, float ropeWidth, float xVertex, float zVertex, int segments, boolean alt, int maxSegments) {
        float f = (float) segments / maxSegments;
        int i = (int) Mth.lerp(f, (float) lighting1, (float) lighting2);
        int j = (int) Mth.lerp(f, (float) lighting3, (float) lighting4);
        int k = LightTexture.pack(i, j);
        float f2 = ROPE_COLOR_R;
        float f3 = ROPE_COLOR_G;
        float f4 = ROPE_COLOR_B;
        if (segments % 2 == (alt ? 1 : 0)) {
            f2 = ROPE_COLOR_R2;
            f3 = ROPE_COLOR_G2;
            f4 = ROPE_COLOR_B2;
        }
        float slack = 3;
        float f5 = xDiff * f;
        float f6 = (float) (yDiff > 0.0F ? yDiff * Math.pow(f, slack) : yDiff - yDiff * Math.pow((1.0F - f), slack));
        float f7 = zDiff * f;
        if(xDiff == 0 && zDiff == 0){
            xVertex += yVertex * 0.5F;
            zVertex += yVertex * 0.5F;
        }
        consumer.vertex(matrix4f, f5 - xVertex, f6 - yVertex * 0.5F + ropeWidth, f7 + zVertex).color(f2, f3, f4, 1.0F).uv2(240).endVertex();
        consumer.vertex(matrix4f, f5 + xVertex, f6 + yVertex * 0.5F - ropeWidth, f7 - zVertex).color(f2, f3, f4, 1.0F).uv2(240).endVertex();
    }

    public static void renderRope(Vec3 from, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 to) {
        poseStack.pushPose();
        poseStack.translate(from.x, from.y, from.z);
        float f = (float) (to.x - from.x);
        float f1 = (float) (to.y - from.y);
        float f2 = (float) (to.z - from.z);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix4f = poseStack.last().pose();
        float f4 = Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = new BlockPos(from);
        BlockPos blockpos1 = new BlockPos(to);
        int i = Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, blockpos);
        int j = Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, blockpos1);
        int k = Minecraft.getInstance().level.getBrightness(LightLayer.SKY, blockpos);
        int l = Minecraft.getInstance().level.getBrightness(LightLayer.SKY, blockpos1);
        float width = 0.1F;
        int maxSegments = Math.max(3, (int) Math.ceil(from.distanceTo(to) * 4));
        for (int i1 = 0; i1 <= maxSegments; ++i1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, i1, false, maxSegments);
        }
        for (int j1 = maxSegments; j1 >= 0; --j1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, j1, true, maxSegments);
        }
        poseStack.popPose();
    }


}
