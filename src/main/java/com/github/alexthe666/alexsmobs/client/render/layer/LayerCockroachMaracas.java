package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.ModelSombrero;
import com.github.alexthe666.alexsmobs.client.render.RenderCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class LayerCockroachMaracas extends LayerRenderer<EntityCockroach, ModelCockroach> {

    private ItemStack stack;
    private ModelSombrero sombrero;
    private static final ResourceLocation SOMBRERO_TEX = new ResourceLocation("alexsmobs:textures/armor/sombrero.png");

    public LayerCockroachMaracas(RenderCockroach render) {
        super(render);
        stack = new ItemStack(AMItemRegistry.MARACA);
        this.sombrero = new ModelSombrero(0);

    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityCockroach entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entitylivingbaseIn.hasMaracas()){
            matrixStackIn.push();
            if (entitylivingbaseIn.isChild()) {
                matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                matrixStackIn.translate(0.0D, 0.815D, 0.125D);
            }
            matrixStackIn.push();
            translateToHand(0, matrixStackIn);
            matrixStackIn.translate(-0.45F, 0.0F, -0.1F);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90F));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90F));
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
            matrixStackIn.push();
            translateToHand(1, matrixStackIn);
            matrixStackIn.translate(0.45F, 0.0F, -0.1F);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90F));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-90F));
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
            matrixStackIn.push();
            translateToHand(2, matrixStackIn);
            matrixStackIn.translate(-0.55F, 0.0F, 0.1F);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90F));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90F));
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
            matrixStackIn.push();
            translateToHand(3, matrixStackIn);
            matrixStackIn.translate(0.55F, 0.0F, 0.1F);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90F));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-90F));
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
            matrixStackIn.push();
            translateToHand(4, matrixStackIn);
            matrixStackIn.translate(0F, -0.45F, -0F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(40F * entitylivingbaseIn.danceProgress * 0.2F));
            matrixStackIn.translate(0F, entitylivingbaseIn.danceProgress * -0.015F, entitylivingbaseIn.danceProgress * -0.08F);
            matrixStackIn.scale(0.8F, 0.8F, 0.8F);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(SOMBRERO_TEX));
            sombrero.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
    }

    protected void translateToHand(int hand, MatrixStack matrixStack) {
        this.getEntityModel().root.translateRotate(matrixStack);
        this.getEntityModel().body.translateRotate(matrixStack);
        if (hand == 0) {
            this.getEntityModel().leg1_right.translateRotate(matrixStack);
        } else if (hand == 1) {
            this.getEntityModel().leg1_left.translateRotate(matrixStack);
        } else if (hand == 2) {
            this.getEntityModel().leg2_right.translateRotate(matrixStack);
        } else if (hand == 3) {
            this.getEntityModel().leg2_left.translateRotate(matrixStack);
        }else{
            this.getEntityModel().frontbody.translateRotate(matrixStack);
            this.getEntityModel().head.translateRotate(matrixStack);
        }
    }
}
