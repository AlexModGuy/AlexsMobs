package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class LayerMimicubeHeldItem extends RenderLayer<EntityMimicube, ModelMimicube> {

    public LayerMimicubeHeldItem(RenderMimicube render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityMimicube entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemRight = entitylivingbaseIn.getMainHandItem();
        ItemStack itemLeft = entitylivingbaseIn.getOffhandItem();
        float rightSwap = Mth.lerp(partialTicks, entitylivingbaseIn.prevRightSwapProgress, entitylivingbaseIn.rightSwapProgress) * 0.2F;
        float leftSwap = Mth.lerp(partialTicks, entitylivingbaseIn.prevLeftSwapProgress, entitylivingbaseIn.leftSwapProgress) * 0.2F;
        float attackprogress = Mth.lerp(partialTicks, entitylivingbaseIn.prevAttackProgress, entitylivingbaseIn.attackProgress);
        double bob1 = Math.cos(ageInTicks * 0.1F) * 0.1F + 0.1F;
        double bob2 = Math.sin(ageInTicks * 0.1F) * 0.1F + 0.1F;
        if (!itemRight.isEmpty()) {
            matrixStackIn.pushPose();
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(-0.5F, 0.1F - bob1, -0.1F);
            matrixStackIn.scale(0.9F * (1F - rightSwap), 0.9F * (1F - rightSwap), 0.9F * (1F - rightSwap));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
            if(itemRight.getItem() instanceof ShieldItem){
                matrixStackIn.translate(-0.1F,  0, -0.4F);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
            }
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-10));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(360 * rightSwap));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-40 * attackprogress));
            Minecraft.getInstance().getItemRenderer().renderStatic(itemRight, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, rightSwap > 0 ? (int) (-100 * rightSwap) : packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
        if (!itemLeft.isEmpty()) {
            matrixStackIn.pushPose();
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(0.45F,  0.1F - bob2, -0.1F);
            matrixStackIn.scale(0.9F * (1F - leftSwap), 0.9F * (1F - leftSwap), 0.9F * (1F - leftSwap));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
            int clampedLight = (int) Math.floor(packedLightIn * (1F - leftSwap));
            if(itemLeft.getItem() instanceof ShieldItem){
                matrixStackIn.translate(-0.2F,  0, -0.4F);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
            }
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(10));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(360 * leftSwap));
            Minecraft.getInstance().getItemRenderer().renderStatic(itemLeft, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, leftSwap > 0 ? (int) (-100 * leftSwap) : packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
    }


    protected void translateToHand(boolean left, PoseStack matrixStack) {
        this.getParentModel().root.translateAndRotate(matrixStack);
        this.getParentModel().innerbody.translateAndRotate(matrixStack);
    }
}
