package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaveCentipede;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeTail;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderCentipedeTail extends MobRenderer<EntityCentipedeTail, AdvancedEntityModel<EntityCentipedeTail>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede.png");

    public RenderCentipedeTail(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCaveCentipede<>(2), 0.5F);
    }

    protected float getFlipDegrees(EntityCentipedeTail centipede) {
        return 180.0F;
    }

    @Override
    protected void setupRotations(EntityCentipedeTail entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
        float newYaw = entity.yHeadRot;
        if (this.isShaking(entity)) {
            newYaw += (float) (Math.cos((double) entity.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }

        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
            stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - newYaw));
            stack.mulPose(Vector3f.XP.rotationDegrees(entity.getXRot()));
        }

        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            stack.translate(0, f * 1.15F, 0);
            stack.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entity.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                stack.translate(0.0D, (double) (entity.getBbHeight() + 0.1F), 0.0D);
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            }
        }
    }

    public ResourceLocation getTextureLocation(EntityCentipedeTail entity) {
        return TEXTURE;
    }
}
