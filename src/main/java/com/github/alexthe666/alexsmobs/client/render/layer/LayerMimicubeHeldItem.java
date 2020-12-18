package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class LayerMimicubeHeldItem extends LayerRenderer<EntityMimicube, ModelMimicube> {

    public LayerMimicubeHeldItem(RenderMimicube render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityMimicube entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemRight = entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemLeft = entitylivingbaseIn.getHeldItemOffhand();
        float rightSwap = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevRightSwapProgress, entitylivingbaseIn.rightSwapProgress) * 0.2F;
        float leftSwap = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevLeftSwapProgress, entitylivingbaseIn.leftSwapProgress) * 0.2F;
        float attackprogress = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevAttackProgress, entitylivingbaseIn.attackProgress);
        double bob1 = Math.cos(ageInTicks * 0.1F) * 0.1F + 0.1F;
        double bob2 = Math.sin(ageInTicks * 0.1F) * 0.1F + 0.1F;
        if (!itemRight.isEmpty()) {
            matrixStackIn.push();
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(-0.5F, 0.1F - bob1, -0.1F);
            matrixStackIn.scale(0.9F * (1F - rightSwap), 0.9F * (1F - rightSwap), 0.9F * (1F - rightSwap));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
            if(itemRight.getItem() instanceof ShieldItem){
                matrixStackIn.translate(-0.1F,  0, -0.4F);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
            }
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-10));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(360 * rightSwap));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-40 * attackprogress));
            Minecraft.getInstance().getItemRenderer().renderItem(itemRight, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, rightSwap > 0 ? (int) (-100 * rightSwap) : packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), matrixStackIn, bufferIn);
            matrixStackIn.pop();
        }
        if (!itemLeft.isEmpty()) {
            matrixStackIn.push();
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(0.45F,  0.1F - bob2, -0.1F);
            matrixStackIn.scale(0.9F * (1F - leftSwap), 0.9F * (1F - leftSwap), 0.9F * (1F - leftSwap));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
            int clampedLight = (int) Math.floor(packedLightIn * (1F - leftSwap));
            if(itemLeft.getItem() instanceof ShieldItem){
                matrixStackIn.translate(-0.2F,  0, -0.4F);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
            }
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(10));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(360 * leftSwap));
            Minecraft.getInstance().getItemRenderer().renderItem(itemLeft, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, leftSwap > 0 ? (int) (-100 * leftSwap) : packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), matrixStackIn, bufferIn);
            matrixStackIn.pop();
        }
    }


    protected void translateToHand(boolean left, MatrixStack matrixStack) {
        this.getEntityModel().root.translateRotate(matrixStack);
        this.getEntityModel().innerbody.translateRotate(matrixStack);
    }
}
