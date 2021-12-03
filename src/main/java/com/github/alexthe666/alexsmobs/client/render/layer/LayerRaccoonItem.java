package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.RenderRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Vector3f;

public class LayerRaccoonItem extends RenderLayer<EntityRaccoon, ModelRaccoon> {

    public LayerRaccoonItem(RenderRaccoon render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityRaccoon entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        matrixStackIn.pushPose();
        boolean inHand = entitylivingbaseIn.begProgress > 0 || entitylivingbaseIn.standProgress > 0 || entitylivingbaseIn.washProgress > 0;
        if(entitylivingbaseIn.isBaby()){
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.pushPose();
        translateToHand(inHand, matrixStackIn);
        if(inHand){
            matrixStackIn.translate(0.2F, 0.4F, 0F);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90F * entitylivingbaseIn.washProgress * 0.2F));
        }else {
            matrixStackIn.translate(0, 0.1F, -0.35F);
        }
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-2.5F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90F));
        Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, itemstack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    protected void translateToHand(boolean inHand, PoseStack matrixStack) {
        if(inHand){
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().arm_right.translateAndRotate(matrixStack);
        }else{
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().head.translateAndRotate(matrixStack);
        }
    }
}
