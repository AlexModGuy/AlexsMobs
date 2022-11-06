package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityAnacondaPart;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderAnacondaPart extends LivingEntityRenderer<EntityAnacondaPart, AdvancedEntityModel<EntityAnacondaPart>> {
    private ModelAnaconda<EntityAnacondaPart> neckModel = new ModelAnaconda<>(AnacondaPartIndex.NECK);
    private ModelAnaconda<EntityAnacondaPart> bodyModel = new ModelAnaconda<>(AnacondaPartIndex.BODY);
    private ModelAnaconda<EntityAnacondaPart> tailModel = new ModelAnaconda<>(AnacondaPartIndex.TAIL);

    public RenderAnacondaPart(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelAnaconda<>(AnacondaPartIndex.NECK), 0.3F);
    }

    protected void setupRotations(EntityAnacondaPart entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
        float newYaw = entity.yHeadRot;
        if (this.isShaking(entity)) {
            newYaw += (float)(Math.cos((double)entity.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }

        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
         //   stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yawIn));
            stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - newYaw));
            stack.mulPose(Vector3f.XP.rotationDegrees(entity.getXRot()));
        }

        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            stack.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
         } else if (entity.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entity.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                stack.translate(0.0D, (double)(entity.getBbHeight() + 0.1F), 0.0D);
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            }
        }

    }

    protected boolean shouldShowName(EntityAnacondaPart entity) {
        return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

    protected void scale(EntityAnacondaPart entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        this.model = getModelForType(entitylivingbaseIn.getPartType());
        matrixStackIn.scale(entitylivingbaseIn.getScale(), entitylivingbaseIn.getScale(), entitylivingbaseIn.getScale());
    }

    private AdvancedEntityModel<EntityAnacondaPart> getModelForType(AnacondaPartIndex partType) {
        switch (partType){
            case BODY: return bodyModel;
            case NECK: return neckModel;
            case TAIL: return tailModel;
        }
        return bodyModel;
    }


    public ResourceLocation getTextureLocation(EntityAnacondaPart entity) {
        return RenderAnaconda.getAnacondaTexture(entity.isYellow(), entity.isShedding());
    }
}
