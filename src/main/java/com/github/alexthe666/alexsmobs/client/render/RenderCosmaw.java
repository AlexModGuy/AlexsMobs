package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCosmaw;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RenderCosmaw extends MobRenderer<EntityCosmaw, ModelCosmaw> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cosmaw.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/cosmaw_glow.png");

    public RenderCosmaw(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCosmaw(), 0.9F);
        this.addLayer(new LayerHeldItem());
        this.addLayer(new LayerBasicGlow(this, TEXTURE_GLOW));
    }

    protected void scale(EntityCosmaw entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.translate(0, -0.5F, 0);
    }

    public ResourceLocation getTextureLocation(EntityCosmaw entity) {
        return TEXTURE;
    }

    class LayerHeldItem extends RenderLayer<EntityCosmaw, ModelCosmaw> {

        public LayerHeldItem() {
            super(RenderCosmaw.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCosmaw entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
            matrixStackIn.pushPose();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(-0.0, 0.1F, -1.35F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-45F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(135F));
            matrixStackIn.scale(2, 2, 2);
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().mouthArm1.translateAndRotate(matrixStack);
            this.getParentModel().mouthArm2.translateAndRotate(matrixStack);

        }
    }
}
