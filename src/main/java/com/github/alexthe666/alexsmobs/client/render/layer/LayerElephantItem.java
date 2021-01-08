package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.RenderElephant;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class LayerElephantItem extends LayerRenderer<EntityElephant, ModelElephant> {

    public LayerElephantItem(RenderElephant render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityElephant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getHeldItemMainhand();
        matrixStackIn.push();
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.8D, 0D);
        }
        matrixStackIn.push();
        translateToHand(matrixStackIn);
        if(entitylivingbaseIn.isChild()){
            matrixStackIn.translate(0.0D, 0.2F, -0.22D);
        }
        matrixStackIn.translate(-0.0, 1.0F, 0.15F);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180F));
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
        if(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(itemstack).isGui3d()){
            matrixStackIn.translate(-0.05F, -0.1F, -0.15F);
            matrixStackIn.scale(2, 2, 2);
        }
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    protected void translateToHand(MatrixStack matrixStack) {
        this.getEntityModel().root.translateRotate(matrixStack);
        this.getEntityModel().body.translateRotate(matrixStack);
        this.getEntityModel().head.translateRotate(matrixStack);
        this.getEntityModel().trunk1.translateRotate(matrixStack);
        this.getEntityModel().trunk2.translateRotate(matrixStack);

    }
}
