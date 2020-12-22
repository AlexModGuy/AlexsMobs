package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelGorilla;
import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.RenderGorilla;
import com.github.alexthe666.alexsmobs.client.render.RenderRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class LayerRaccoonItem extends LayerRenderer<EntityRaccoon, ModelRaccoon> {

    public LayerRaccoonItem(RenderRaccoon render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityRaccoon entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        matrixStackIn.push();
        boolean inHand = entitylivingbaseIn.begProgress > 0 || entitylivingbaseIn.standProgress > 0 || entitylivingbaseIn.washProgress > 0;
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.push();
        translateToHand(inHand, matrixStackIn);
        if(inHand){
            matrixStackIn.translate(0.2F, 0.4F, 0F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90F * entitylivingbaseIn.washProgress * 0.2F));
        }else {
            matrixStackIn.translate(0, 0.1F, -0.35F);
        }
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-2.5F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90F));
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    protected void translateToHand(boolean inHand, MatrixStack matrixStack) {
        if(inHand){
            this.getEntityModel().root.translateRotate(matrixStack);
            this.getEntityModel().body.translateRotate(matrixStack);
            this.getEntityModel().arm_right.translateRotate(matrixStack);
        }else{
            this.getEntityModel().root.translateRotate(matrixStack);
            this.getEntityModel().body.translateRotate(matrixStack);
            this.getEntityModel().head.translateRotate(matrixStack);
        }
    }
}
