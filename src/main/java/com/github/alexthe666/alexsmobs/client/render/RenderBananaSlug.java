package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderBananaSlug extends MobRenderer<EntityBananaSlug, ModelBananaSlug> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_3.png");
    private static final ResourceLocation TEXTURE_SLIME = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_slime.png");

    public RenderBananaSlug(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelBananaSlug(), 0.2F);
        this.addLayer(new LayerSlime());
    }

    protected void scale(EntityBananaSlug entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    private Direction rotate(Direction attachmentFacing){
        return attachmentFacing.getAxis() == Direction.Axis.Y ? Direction.UP : attachmentFacing;
    }

    private void rotateForAngle(PoseStack matrixStackIn, Direction rotate, float f){
        if(rotate.getAxis() != Direction.Axis.Y){
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F * f));
        }
        switch (rotate) {
            case DOWN:
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F * f));
                break;
            case UP:
                break;
            case NORTH:
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F * f));
                break;
            case SOUTH:
                break;
            case WEST:
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90F * f));
                break;
            case EAST:
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-90F * f));
                break;
        }
    }

    @Override
    protected void setupRotations(EntityBananaSlug entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if(entityLiving.isPassenger()){
            super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
            return;
        }
        if (this.isShaking(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }
        float trans = entityLiving.isBaby() ? 0.2F : 0.4F;
        Pose pose = entityLiving.getPose();
        if (pose != Pose.SLEEPING) {
            float progress = (entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks) * 0.2F;
            float yawMul = 0F;
            if(entityLiving.prevAttachDir == entityLiving.getAttachmentFacing() && entityLiving.getAttachmentFacing().getAxis() == Direction.Axis.Y){
                yawMul = 1.0F;
            }
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees ( (180.0F - yawMul * rotationYaw)));
            matrixStackIn.translate(0.0D, trans, 0.0D);
            float prevProg = 1F - progress;
            rotateForAngle(matrixStackIn, rotate(entityLiving.prevAttachDir), prevProg);
            rotateForAngle(matrixStackIn, rotate(entityLiving.getAttachmentFacing()), progress);
            if(entityLiving.getAttachmentFacing() != Direction.DOWN){
                matrixStackIn.translate(0.0D, trans, 0.0D);
                if(entityLiving.getDeltaMovement().y <= -0.001F){
                    matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(180 * progress));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);
            }
            matrixStackIn.translate(0.0D, -trans, 0.0D);
        }

        if (entityLiving.deathTime > 0) {
            float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(entityLiving)));
        } else if (entityLiving.isAutoSpinAttack()) {
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.getXRot()));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(((float)entityLiving.tickCount + partialTicks) * -75.0F));
        } else if (pose == Pose.SLEEPING) {

        } else if (entityLiving.hasCustomName() ) {
            String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                matrixStackIn.translate(0.0D, (double)(entityLiving.getBbHeight() + 0.1F), 0.0D);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            }
        }
    }


    public ResourceLocation getTextureLocation(EntityBananaSlug entity) {
        return switch (entity.getVariant()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            default -> TEXTURE_0;
        };
    }

    class LayerSlime extends RenderLayer<EntityBananaSlug, ModelBananaSlug> {

        public LayerSlime() {
            super(RenderBananaSlug.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityBananaSlug entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float alpha = entitylivingbaseIn.prevTrailVisability + (entitylivingbaseIn.trailVisability - entitylivingbaseIn.prevTrailVisability) * partialTicks;
            if(alpha > 0){
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE_SLIME));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }
}
