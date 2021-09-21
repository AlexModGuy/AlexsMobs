package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class RenderSeagull extends MobRenderer<EntitySeagull, ModelSeagull> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/seagull.png");
    private static final ResourceLocation TEXTURE_WINGULL = new ResourceLocation("alexsmobs:textures/entity/seagull_wingull.png");

    public RenderSeagull(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelSeagull(), 0.2F);
        this.addLayer(new LayerHeldItem(this));
    }

    protected void scale(EntitySeagull entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getTextureLocation(EntitySeagull entity) {
        return entity.isWingull() ? TEXTURE_WINGULL : TEXTURE;
    }

    class LayerHeldItem extends RenderLayer<EntitySeagull, ModelSeagull> {

        public LayerHeldItem(RenderSeagull render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySeagull entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0D);
            }
            matrixStackIn.pushPose();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(0, -0.24F, -0.25F);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-2.5F));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90F));
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, itemstack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
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
