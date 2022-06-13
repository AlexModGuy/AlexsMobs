package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelToucan;
import com.github.alexthe666.alexsmobs.entity.EntityToucan;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class RenderToucan extends MobRenderer<EntityToucan, ModelToucan> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_3.png");
    private static final ResourceLocation TEXTURE_GOLDEN = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_gold.png");
    private static final ResourceLocation TEXTURE_SAM = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_sam.png");

    public RenderToucan(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelToucan(), 0.2F);
        this.addLayer(new RenderToucan.LayerGlint(this));
        this.addLayer(new RenderToucan.LayerHeldItem(this));
    }

    protected void scale(EntityToucan entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    public ResourceLocation getTextureLocation(EntityToucan entity) {
        if(entity.isSam()){
            return TEXTURE_SAM;
        }
        if(entity.isGolden()){
            return TEXTURE_GOLDEN;
        }
        switch (entity.getVariant()){
            case 3:
                return TEXTURE_3;
            case 2:
                return TEXTURE_2;
            case 1:
                return TEXTURE_1;
            default:
                return TEXTURE_0;
        }
    }

    class LayerGlint extends RenderLayer<EntityToucan, ModelToucan> {

        public LayerGlint(RenderToucan render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityToucan entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(entitylivingbaseIn.isEnchanted()){
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(TEXTURE_GOLDEN), false, true);
                this.getParentModel().renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1, 1, 1, 1.0F);
            }
        }
    }

    class LayerHeldItem extends RenderLayer<EntityToucan, ModelToucan> {

        public LayerHeldItem(RenderToucan render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityToucan entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0D);
            }
            matrixStackIn.pushPose();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(-0.07F, -0.1F, -0.25F);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-45F));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90F));
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().head.translateAndRotate(matrixStack);

        }
    }
}
