package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelPotoo;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityPotoo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class RenderPotoo extends MobRenderer<EntityPotoo, ModelPotoo> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/potoo.png");

    public RenderPotoo(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelPotoo(), 0.35F);
    }

    public boolean shouldRender(EntityPotoo bird, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        if( bird.isPassenger() && bird.getVehicle() instanceof Player && Minecraft.getInstance().player == bird.getVehicle() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON){
            return false;
        }
        return super.shouldRender(bird, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_);
    }

    protected void scale(EntityPotoo eagle, PoseStack matrixStackIn, float partialTickTime) {
        if(eagle.isPassenger() && eagle.getVehicle() != null) {
            if (eagle.getVehicle() instanceof Player) {
                Player mount = (Player)eagle.getVehicle();
                boolean leftHand = false;
                if(mount.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()){
                    leftHand = mount.getMainArm() == HumanoidArm.LEFT;
                }else if(mount.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()){
                    leftHand = mount.getMainArm() != HumanoidArm.LEFT;
                }
                EntityRenderer playerRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(mount);
                if(Minecraft.getInstance().player == mount && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON){
                    //handled via event
                }else if (playerRender instanceof LivingEntityRenderer && ((LivingEntityRenderer) playerRender).getModel() instanceof HumanoidModel) {
                    if(leftHand){
                        matrixStackIn.translate(-0.3F, -0.7F, 0.5F);
                        ((HumanoidModel) ((LivingEntityRenderer) playerRender).getModel()).leftArm.translateAndRotate(matrixStackIn);
                        matrixStackIn.translate(-0.1F, 0.6F, -0.1F);
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(55F));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(70F));
                    }else{
                        matrixStackIn.translate(0.3F, -0.7F, 0.5F);
                        ((HumanoidModel) ((LivingEntityRenderer) playerRender).getModel()).rightArm.translateAndRotate(matrixStackIn);
                        matrixStackIn.translate(0.1F, 0.6F, -0.1F);
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(55F));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-70F));
                    }
                }
            }
        }
    }


    public ResourceLocation getTextureLocation(EntityPotoo entity) {
        return TEXTURE;
    }
}
