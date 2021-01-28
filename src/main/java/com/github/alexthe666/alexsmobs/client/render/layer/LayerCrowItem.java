package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelCrow;
import com.github.alexthe666.alexsmobs.client.render.RenderCrow;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class LayerCrowItem extends LayerRenderer<EntityCrow, ModelCrow> {

    public LayerCrowItem(RenderCrow render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityCrow entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        matrixStackIn.push();
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.push();
        translateToHand(matrixStackIn);
        matrixStackIn.translate(0, -0.09F, -0.125F);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-2.5F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90F));
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    protected void translateToHand(MatrixStack matrixStack) {
        this.getEntityModel().root.translateRotate(matrixStack);
        this.getEntityModel().body.translateRotate(matrixStack);
        this.getEntityModel().head.translateRotate(matrixStack);

    }
}
