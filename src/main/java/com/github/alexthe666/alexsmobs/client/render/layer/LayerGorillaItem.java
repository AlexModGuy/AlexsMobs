package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelGorilla;
import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.RenderGorilla;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class LayerGorillaItem extends LayerRenderer<EntityGorilla, ModelGorilla> {

    public LayerGorillaItem(RenderGorilla render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityGorilla entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        matrixStackIn.push();
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(-0.1D, 2D, -1.15D);
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(-0.4F, 0.75F, -0.0F);
            matrixStackIn.scale(2.8F, 2.8F, 2.8F);
        }else{
            translateToHand(false, matrixStackIn);
            matrixStackIn.translate(0.3F, 0.75F, -0.0F);
        }
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-2.5F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90F));
        if(itemstack.getItem() instanceof BlockItem){
            matrixStackIn.scale(2, 2, 2);
        }
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }

    protected void translateToHand(boolean left, MatrixStack matrixStack) {
        this.getEntityModel().root.translateRotate(matrixStack);
        this.getEntityModel().body.translateRotate(matrixStack);
        this.getEntityModel().bodyfront.translateRotate(matrixStack);
        this.getEntityModel().armR.translateRotate(matrixStack);
    }
}
