package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelSeal;
import com.github.alexthe666.alexsmobs.client.render.RenderSeal;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class LayerSealItem extends LayerRenderer<EntitySeal, ModelSeal> {

    public LayerSealItem(RenderSeal render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntitySeal entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getHeldItemMainhand();
        matrixStackIn.push();
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
        }
        matrixStackIn.push();
        translateToHand(matrixStackIn);
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.translate(0.0D, 0, -0.1D);
        }
        matrixStackIn.translate(-0.1F, 0.05F, -0.1F);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-45F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90F));
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    protected void translateToHand(MatrixStack matrixStack) {
        this.getEntityModel().root.translateRotate(matrixStack);
        this.getEntityModel().body.translateRotate(matrixStack);
        this.getEntityModel().head.translateRotate(matrixStack);
        this.getEntityModel().snout.translateRotate(matrixStack);

    }
}
