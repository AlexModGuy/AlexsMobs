package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMurmurHead;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.entity.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderMurmurHead extends MobRenderer<EntityMurmurHead, ModelMurmurHead> {

    private static final ModelMurmurNeck NECK_MODEL = new ModelMurmurNeck();
    private static final int MAX_NECK_SEGMENTS = 128;

    public RenderMurmurHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelMurmurHead(), 0.3F);
    }

    protected void scale(EntityMurmurHead entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    public boolean shouldRender(EntityMurmurHead livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            Entity parent = livingEntityIn.getBody();
            if (parent instanceof EntityMurmur) {
                EntityMurmur body = (EntityMurmur)parent;
                Vec3 vector3d = body.getNeckBottom(1.0F);
                Vec3 vector3d1 = livingEntityIn.getNeckTop(1.0F);
                return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
            }
            return false;
        }
    }

    public void render(EntityMurmurHead head, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(head, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        Entity parent = head.getBody();
        matrixStackIn.pushPose();
        if (parent instanceof EntityMurmur) {
            EntityMurmur body = (EntityMurmur) parent;
            Vec3 renderingAt = new Vec3(Mth.lerp(partialTicks, head.xo, head.getX()), Mth.lerp(partialTicks, head.yo, head.getY()), Mth.lerp(partialTicks, head.zo, head.getZ()));
            Vec3 bottom = body.getNeckBottom(partialTicks).subtract(renderingAt);
            Vec3 top = head.getNeckTop(partialTicks).subtract(renderingAt);
            Vec3 moveDownFrom = bottom.subtract(top);
            Vec3 moveUpTowards = top.subtract(bottom);
            RenderType renderType = RenderType.entityCutoutNoCull(getTextureLocation(head));
            int overlayCoords = getOverlayCoords(head, this.getWhiteOverlayProgress(head, partialTicks));
            matrixStackIn.translate(moveDownFrom.x, moveDownFrom.y - 0.5F, moveDownFrom.z);
            Vec3 currentNeckButt = Vec3.ZERO;
            int segmentCount = 0;
            while(segmentCount < MAX_NECK_SEGMENTS && currentNeckButt.distanceTo(moveUpTowards) > 0.2){
                double remainingDistance = Math.min(currentNeckButt.distanceTo(moveUpTowards), 1F);
                Vec3 linearVec = moveUpTowards.subtract(currentNeckButt);
                Vec3 next = linearVec.normalize().scale(remainingDistance).add(currentNeckButt)
                renderSegment(currentNeckButt, next, matrixStackIn, bufferIn.getBuffer(renderType), packedLightIn, overlayCoords);
                currentNeckButt = next;
                segmentCount++;
            }
        }
        matrixStackIn.popPose();
    }

    private void renderSegment(Vec3 from, Vec3 to, PoseStack poseStack, VertexConsumer buffer, int packedLightIn, int overlayCoords){
        Vec3 sub = from.subtract(to);
        float rotY = (float) (Mth.atan2(sub.x, sub.z) * (double) (180F / (float) Math.PI));
        float rotX = (float) (-(Mth.atan2(sub.y, sub.horizontalDistance()) * (double) (180F / (float) Math.PI))) - 90.0F;
        poseStack.pushPose();
        poseStack.translate(from.x, from.y, from.z);
        NECK_MODEL.setAttributes((float)sub.length(), rotX, rotY);
        NECK_MODEL.renderToBuffer(poseStack, buffer, packedLightIn, overlayCoords, 1, 1F, 1, 1);
        poseStack.popPose();

    }

    private Vec3 calcOffsetVec(float height, float xRot, float yRot){
        Vec3 toRotate = new Vec3(0, height, 0);
        if(Math.max(Math.abs(xRot), Math.abs(yRot)) <= 1){
            return toRotate;
        }
        return toRotate.xRot(xRot * ((float)Math.PI / 180F)).yRot(-yRot * ((float)Math.PI / 180F));
    }

    public ResourceLocation getTextureLocation(EntityMurmurHead entity) {
        return RenderMurmurBody.TEXTURE;
    }
}
