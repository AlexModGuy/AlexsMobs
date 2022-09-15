package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMurmurHead;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.alexsmobs.entity.EntityMurmurHead;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderMurmurHead extends MobRenderer<EntityMurmurHead, ModelMurmurHead> {

    private static final ModelMurmurNeck NECK_MODEL = new ModelMurmurNeck();
    public static final int MAX_NECK_SEGMENTS = 128;

    public RenderMurmurHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelMurmurHead(), 0.3F);
    }

    protected void scale(EntityMurmurHead entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    public boolean shouldRender(EntityMurmurHead livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else if(livingEntityIn.hasNeckBottom()){
            Vec3 vector3d = livingEntityIn.getNeckBottom(1.0F);
            Vec3 vector3d1 = livingEntityIn.getNeckTop(1.0F);
            return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
        }else{
            return false;
        }
    }

    protected float getFlipDegrees(EntityMurmurHead head) {
        return 0.0F;
    }

    public void render(EntityMurmurHead head, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(head, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        if (head.hasNeckBottom()) {
            float headYaw = Mth.rotLerp(partialTicks, head.yBodyRotO, head.yBodyRot);
            Vec3 renderingAt = new Vec3(Mth.lerp(partialTicks, head.xo, head.getX()), Mth.lerp(partialTicks, head.yo, head.getY()), Mth.lerp(partialTicks, head.zo, head.getZ()));
            Vec3 bottom = head.getNeckBottom(partialTicks).subtract(renderingAt);
            Vec3 top = head.getNeckTop(partialTicks).subtract(renderingAt);
            Vec3 moveDownFrom = bottom.subtract(top);
            Vec3 moveUpTowards = top.subtract(bottom);
            RenderType renderType = RenderType.entityCutoutNoCull(getTextureLocation(head));
            int overlayCoords = getOverlayCoords(head, this.getWhiteOverlayProgress(head, partialTicks));
            matrixStackIn.translate(moveDownFrom.x, moveDownFrom.y - 0.5F, moveDownFrom.z);
            Vec3 currentNeckButt = Vec3.ZERO;
            int segmentCount = 0;
            while (segmentCount < MAX_NECK_SEGMENTS && currentNeckButt.distanceTo(moveUpTowards) > 0.2) {
                double remainingDistance = Math.min(currentNeckButt.distanceTo(moveUpTowards), 1F);
                Vec3 linearVec = moveUpTowards.subtract(currentNeckButt);
                Vec3 powVec = new Vec3(modifyVecAngle(linearVec.x), modifyVecAngle(linearVec.y), modifyVecAngle(linearVec.z));
                Vec3 smoothedVec = remainingDistance < 1F ? linearVec : powVec;
                Vec3 next = smoothedVec.normalize().scale(remainingDistance).add(currentNeckButt);
                int neckLight = getLightColor(head, bottom.add(currentNeckButt).add(renderingAt));
                renderNeckCube(currentNeckButt, next, matrixStackIn, bufferIn.getBuffer(renderType), neckLight, overlayCoords, headYaw);
                currentNeckButt = next;
                segmentCount++;
            }
        }
        matrixStackIn.popPose();
    }



    private double modifyVecAngle(double dimension) {
        float abs = (float) Math.abs(dimension);
        return Math.signum(dimension) * Mth.clamp(Math.pow(abs, 0.1), 0.01 * abs, abs);
    }

    public static void renderNeckCube(Vec3 from, Vec3 to, PoseStack poseStack, VertexConsumer buffer, int packedLightIn, int overlayCoords, float additionalYaw) {
        Vec3 sub = from.subtract(to);
        double d = sub.horizontalDistance();
        float rotY = (float) (Mth.atan2(sub.x, sub.z) * (double) (180F / (float) Math.PI));
        float rotX = (float) (-(Mth.atan2(sub.y, d) * (double) (180F / (float) Math.PI))) - 90.0F;
        poseStack.pushPose();
        poseStack.translate(from.x, from.y, from.z);
        NECK_MODEL.setAttributes((float) sub.length(), rotX, rotY, additionalYaw);
        NECK_MODEL.renderToBuffer(poseStack, buffer, packedLightIn, overlayCoords, 1, 1F, 1, 1);
        poseStack.popPose();
    }

    private int getLightColor(EntityMurmurHead head, Vec3 vec3) {
        BlockPos blockpos = new BlockPos(vec3);
        return head.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(head.level, blockpos) : 0;
    }


    public ResourceLocation getTextureLocation(EntityMurmurHead entity) {
        return entity.isAngry() ? RenderMurmurBody.TEXTURE_ANGRY : RenderMurmurBody.TEXTURE;
    }
}
