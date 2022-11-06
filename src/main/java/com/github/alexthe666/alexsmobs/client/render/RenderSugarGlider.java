package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSugarGlider;
import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class RenderSugarGlider extends MobRenderer<EntitySugarGlider, ModelSugarGlider> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/sugar_glider.png");

    public RenderSugarGlider(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSugarGlider(), 0.35F);
    }

    private Direction rotate(Direction attachmentFacing){
        return attachmentFacing.getAxis() == Direction.Axis.Y ? Direction.UP : attachmentFacing;
    }

    @Override
    protected void setupRotations(EntitySugarGlider entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
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
            float prevProg = (entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks);
            float yawMul = 0F;
            if(entityLiving.prevAttachDir == entityLiving.getAttachmentFacing() && entityLiving.getAttachmentFacing().getAxis() == Direction.Axis.Y){
                yawMul = 1.0F;
            }
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees ( (180.0F - yawMul * rotationYaw)));

            if(entityLiving.getAttachmentFacing() == Direction.DOWN){
                matrixStackIn.translate(0.0D, trans, 0.0D);
                if(entityLiving.yo <= entityLiving.getY()){
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90 * prevProg));
                }else{
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90 * prevProg));
                }
                matrixStackIn.translate(0.0D, -trans, 0.0D);
            }

            matrixStackIn.translate(0.0D, trans, 0.0D);
            Quaternion current = rotate(entityLiving.getAttachmentFacing()).getRotation();
            current.mul(1F - prevProg);
            matrixStackIn.mulPose(current);
            //Quaternion prev = rotate(entityLiving.prevAttachDir).getRotation();
            //prev.mul(prevProg);
            //matrixStackIn.mulPose(prev);
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

    protected void scale(EntitySugarGlider mob, PoseStack matrixStackIn, float partialTickTime) {
        if(mob.isPassenger() && mob.getVehicle() != null) {
            if (mob.getVehicle() instanceof Player) {
                Player mount = (Player)mob.getVehicle();
                EntityRenderer playerRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(mount);
                if(Minecraft.getInstance().player == mount && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON){
                    //handled via event
                }else if (playerRender instanceof LivingEntityRenderer && ((LivingEntityRenderer) playerRender).getModel() instanceof HumanoidModel) {
                    matrixStackIn.translate(0.0F, 0.5F, 0.0F);
                    ((HumanoidModel) ((LivingEntityRenderer) playerRender).getModel()).head.translateAndRotate(matrixStackIn);
                    matrixStackIn.translate(0.0F, -0.5F, 0.0F);
                }
            }
        }
    }


    public ResourceLocation getTextureLocation(EntitySugarGlider entity) {
        return TEXTURE;
    }
}

