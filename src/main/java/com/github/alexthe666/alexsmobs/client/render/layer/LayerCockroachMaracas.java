package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.model.layered.ModelSombrero;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class LayerCockroachMaracas extends RenderLayer<EntityCockroach, ModelCockroach> {

    private ItemStack stack;
    private ModelSombrero sombrero;
    private static final ResourceLocation SOMBRERO_TEX = new ResourceLocation("alexsmobs:textures/armor/sombrero.png");

    public LayerCockroachMaracas(RenderCockroach render, EntityRendererProvider.Context renderManagerIn) {
        super(render);
        stack = new ItemStack(AMItemRegistry.MARACA);
        this.sombrero = new ModelSombrero(renderManagerIn.bakeLayer(AMModelLayers.SOMBRERO));

    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCockroach entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entitylivingbaseIn.hasMaracas()){
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                matrixStackIn.translate(0.0D, 0.815D, 0.125D);
            }
            matrixStackIn.pushPose();
            translateToHand(0, matrixStackIn);
            matrixStackIn.translate(-0.25F, 0.0F, 0);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90F));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(60F));
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, stack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            translateToHand(1, matrixStackIn);
            matrixStackIn.translate(0.25F, 0.0F, 0);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90F));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-120F));
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, stack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            translateToHand(2, matrixStackIn);
            matrixStackIn.translate(-0.35F, 0.0F, 0);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90F));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(60F));
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, stack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            translateToHand(3, matrixStackIn);
            matrixStackIn.translate(0.35F, 0.0F, 0);
            matrixStackIn.scale(1.4F, 1.4F, 1.4F);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90F));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-120F));
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, stack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            if(!entitylivingbaseIn.isHeadless()){
                matrixStackIn.pushPose();
                translateToHand(4, matrixStackIn);
                matrixStackIn.translate(0F, -0.4F, -0.01F);
                matrixStackIn.translate(0F, entitylivingbaseIn.danceProgress * 0.045F, entitylivingbaseIn.danceProgress * -0.09F);
                matrixStackIn.scale(0.8F, 0.8F, 0.8F);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(60F * entitylivingbaseIn.danceProgress * 0.2F));
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(SOMBRERO_TEX));
                sombrero.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
            matrixStackIn.popPose();
        }
    }

    protected void translateToHand(int hand, PoseStack matrixStack) {
        this.getParentModel().root.translateAndRotate(matrixStack);
        this.getParentModel().abdomen.translateAndRotate(matrixStack);
        if (hand == 0) {
            this.getParentModel().right_leg_front.translateAndRotate(matrixStack);
        } else if (hand == 1) {
            this.getParentModel().left_leg_front.translateAndRotate(matrixStack);
        } else if (hand == 2) {
            this.getParentModel().right_leg_mid.translateAndRotate(matrixStack);
        } else if (hand == 3) {
            this.getParentModel().left_leg_mid.translateAndRotate(matrixStack);
        }else{
            this.getParentModel().neck.translateAndRotate(matrixStack);
            this.getParentModel().head.translateAndRotate(matrixStack);
        }
    }
}
