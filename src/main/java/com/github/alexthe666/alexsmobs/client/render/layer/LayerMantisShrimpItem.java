package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelMantisShrimp;
import com.github.alexthe666.alexsmobs.client.render.RenderMantisShrimp;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class LayerMantisShrimpItem extends LayerRenderer<EntityMantisShrimp, ModelMantisShrimp> {

    public LayerMantisShrimpItem(RenderMantisShrimp render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityMantisShrimp entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        matrixStackIn.push();
        boolean left = entitylivingbaseIn.isLeftHanded();
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.push();
        translateToHand(matrixStackIn, left);
        matrixStackIn.translate(left ? 0.075F : -0.075F, 0.45F, -0.125F);
        if(!Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(itemstack).isGui3d()){
            matrixStackIn.translate(0F, 0F, 0.05F);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(left ? -40F : 40F));
        }
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-2.5F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-180F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180F));
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    protected void translateToHand(MatrixStack matrixStack, boolean left) {
        this.getEntityModel().root.translateRotate(matrixStack);
        this.getEntityModel().body.translateRotate(matrixStack);
        this.getEntityModel().head.translateRotate(matrixStack);
        if(left){
            this.getEntityModel().arm_left.translateRotate(matrixStack);
            this.getEntityModel().fist_left.translateRotate(matrixStack);
        }else{
            this.getEntityModel().arm_right.translateRotate(matrixStack);
            this.getEntityModel().fist_right.translateRotate(matrixStack);
        }
    }
}
