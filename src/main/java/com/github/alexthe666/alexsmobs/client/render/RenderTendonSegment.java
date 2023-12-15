package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.client.model.ModelTendonClaw;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class RenderTendonSegment extends EntityRenderer<EntityTendonSegment> {

    private static final ResourceLocation CLAW_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tendon_whip_claw.png");
    private static final ModelTendonClaw CLAW_MODEL = new ModelTendonClaw();

    public RenderTendonSegment(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public boolean shouldRender(EntityTendonSegment entity, Frustum frustum, double x, double y, double z) {
        Entity next = entity.getFromEntity();
        return next != null && frustum.isVisible(entity.getBoundingBox().minmax(next.getBoundingBox())) || super.shouldRender(entity, frustum, x, y, z);
    }

    @Override
    public void render(EntityTendonSegment entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        super.render(entity, yaw, partialTicks, poseStack, buffer, light);
        poseStack.pushPose();
        Entity fromEntity = entity.getFromEntity();
        float x = (float)Mth.lerp(partialTicks, entity.xo, entity.getX());
        float y = (float)Mth.lerp(partialTicks, entity.yo, entity.getY());
        float z = (float)Mth.lerp(partialTicks, entity.zo, entity.getZ());

        if (fromEntity != null) {
            float progress = (entity.prevProgress + (entity.getProgress() - entity.prevProgress) * partialTicks) / EntityTendonSegment.MAX_EXTEND_TIME;
            Vec3 distVec = getPositionOfPriorMob(entity, fromEntity, partialTicks).subtract(x, y, z);
            Vec3 to = distVec.scale(1F - progress);
            Vec3 from = distVec;
            int segmentCount = 0;
            Vec3 currentNeckButt = from;
            VertexConsumer neckConsumer;
            if(entity.hasGlint()){
                neckConsumer = AMRenderTypes.createMergedVertexConsumer(buffer.getBuffer(AMRenderTypes.entityGlintDirect()), buffer.getBuffer(RenderType.entityCutoutNoCull(RenderMurmurBody.TEXTURE)));
            }else{
                neckConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(RenderMurmurBody.TEXTURE));
            }
            ModelMurmurNeck.THIN = true;
            double remainingDistance = to.distanceTo(from);
            while (segmentCount < RenderMurmurHead.MAX_NECK_SEGMENTS && remainingDistance > 0) {
                remainingDistance = Math.min(from.distanceTo(to), 0.5F);
                Vec3 linearVec = to.subtract(currentNeckButt);
                Vec3 powVec = new Vec3(modifyVecAngle(linearVec.x), modifyVecAngle(linearVec.y), modifyVecAngle(linearVec.z));
                Vec3 smoothedVec = powVec;
                Vec3 next = smoothedVec.normalize().scale(remainingDistance).add(currentNeckButt);
                int neckLight = getLightColor(entity, to.add(currentNeckButt).add(x, y, z));
                RenderMurmurHead.renderNeckCube(currentNeckButt, next, poseStack, neckConsumer, neckLight, OverlayTexture.NO_OVERLAY, 0);
                currentNeckButt = next;
                segmentCount++;
            }
            ModelMurmurNeck.THIN = false;
            VertexConsumer clawConsumer;
            if(entity.hasGlint()){
                clawConsumer = AMRenderTypes.createMergedVertexConsumer(buffer.getBuffer(AMRenderTypes.entityGlintDirect()), buffer.getBuffer(RenderType.entityCutoutNoCull(CLAW_TEXTURE)));
            }else{
                clawConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(CLAW_TEXTURE));
            }
            if(entity.hasClaw() || entity.isRetracting()){
                poseStack.pushPose();
                poseStack.translate(to.x, to.y, to.z);
                float rotY = (float) (Mth.atan2(to.x, to.z) * (double) Mth.RAD_TO_DEG);
                float rotX = (float) (-(Mth.atan2(to.y, to.horizontalDistance()) * (double) Mth.RAD_TO_DEG));
                CLAW_MODEL.setAttributes(rotX, rotY, 1 - progress);
                CLAW_MODEL.renderToBuffer(poseStack, clawConsumer, getLightColor(entity, to.add(x, y, z)), OverlayTexture.NO_OVERLAY, 1, 1F, 1, 1F);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    private Vec3 getPositionOfPriorMob(EntityTendonSegment segment, Entity mob, float partialTicks){
        double d4 = Mth.lerp(partialTicks, mob.xo, mob.getX());
        double d5 = Mth.lerp(partialTicks, mob.yo, mob.getY());
        double d6 = Mth.lerp(partialTicks, mob.zo, mob.getZ());
        float f3 = 0;
        if(mob instanceof Player && segment.isCreator(mob)){
            Player player = (Player) mob;
            float f = player.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * Mth.PI);
            float f2 = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * Mth.DEG_TO_RAD;
            int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            double d0 = (double) Mth.sin(f2);
            double d1 = (double) Mth.cos(f2);
            double d2 = (double) i * 0.35D;
            ItemStack itemstack = player.getMainHandItem();
            if (!itemstack.is(AMItemRegistry.TENDON_WHIP.get())) {
                i = -i;
            }
            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
                double d7 = 960.0D / (double)this.entityRenderDispatcher.options.fov().get().intValue();
                Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float) i * 0.6F, -1);
                vec3 = vec3.scale(d7);
                vec3 = vec3.yRot(f1 * 0.25F);
                vec3 = vec3.xRot(-f1 * 0.35F);
                d4 = Mth.lerp((double) partialTicks, player.xo, player.getX()) + vec3.x;
                d5 = Mth.lerp((double) partialTicks, player.yo, player.getY()) + vec3.y;
                d6 = Mth.lerp((double) partialTicks, player.zo, player.getZ()) + vec3.z;
                f3 = player.getEyeHeight() * 0.4F;
            } else {
                d4 = Mth.lerp((double) partialTicks, player.xo, player.getX()) - d1 * d2 - d0 * 0.2D;
                d5 = player.yo + (double) player.getEyeHeight() + (player.getY() - player.yo) * (double) partialTicks - 1D;
                d6 = Mth.lerp((double) partialTicks, player.zo, player.getZ()) - d0 * d2 + d1 * 0.2D;
                f3 = (player.isCrouching() ? -0.1875F : 0.0F) - player.getEyeHeight() * 0.3F;
            }
        }

        return new Vec3(d4, d5 + f3, d6);
    }

    private double modifyVecAngle(double dimension) {
        float abs = (float) Math.abs(dimension);
        return Math.signum(dimension) * Mth.clamp(Math.pow(abs, 0.1), 0.05 * abs, abs);
    }

    private int getLightColor(Entity head, Vec3 vec3) {
        BlockPos blockpos = AMBlockPos.fromVec3(vec3);
        if(head.level().hasChunkAt(blockpos)){
            int i = LevelRenderer.getLightColor(head.level(), blockpos);
            int j = LevelRenderer.getLightColor(head.level(), blockpos.above());
            int k = i & 255;
            int l = j & 255;
            int i1 = i >> 16 & 255;
            int j1 = j >> 16 & 255;
            return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;
        }else{
            return 0;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityTendonSegment entity) {
        return null;
    }

}