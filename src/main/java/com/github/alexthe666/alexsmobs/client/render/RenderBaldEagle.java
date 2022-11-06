package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class RenderBaldEagle extends MobRenderer<EntityBaldEagle, ModelBaldEagle> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/bald_eagle.png");
    private static final ResourceLocation TEXTURE_CAP = new ResourceLocation("alexsmobs:textures/entity/bald_eagle_hood.png");

    public RenderBaldEagle(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelBaldEagle(), 0.3F);
        this.addLayer(new CapLayer(this));
    }

    public boolean shouldRender(EntityBaldEagle baldEagle, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        if( baldEagle.isPassenger() && baldEagle.getVehicle() instanceof Player && Minecraft.getInstance().player == baldEagle.getVehicle() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON){
            return false;
        }
        return super.shouldRender(baldEagle, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_);
    }

    protected void scale(EntityBaldEagle eagle, PoseStack matrixStackIn, float partialTickTime) {
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
                        matrixStackIn.translate(-0.2F, 0.5F, -0.18F);
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(40F));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(70F));
                    }else{
                        matrixStackIn.translate(0.3F, -0.7F, 0.5F);
                        ((HumanoidModel) ((LivingEntityRenderer) playerRender).getModel()).rightArm.translateAndRotate(matrixStackIn);
                        matrixStackIn.translate(0.2F, 0.5F, -0.18F);
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(40F));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-70F));
                    }
                }
            }
        }
    }


    public ResourceLocation getTextureLocation(EntityBaldEagle entity) {
        return TEXTURE;
    }

    class CapLayer extends RenderLayer<EntityBaldEagle, ModelBaldEagle> {

        public CapLayer(RenderBaldEagle p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityBaldEagle entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.hasCap()) {
                VertexConsumer lead = bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE_CAP));
                this.getParentModel().renderToBuffer(matrixStackIn, lead, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
