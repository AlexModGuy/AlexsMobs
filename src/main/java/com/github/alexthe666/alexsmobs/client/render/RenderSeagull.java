package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class RenderSeagull extends MobRenderer<EntitySeagull, ModelSeagull> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/seagull.png");
    private static final ResourceLocation TEXTURE_WINGULL = new ResourceLocation("alexsmobs:textures/entity/seagull_wingull.png");

    public RenderSeagull(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelSeagull(), 0.2F);
        this.addLayer(new LayerHeldItem(this));
    }

    protected void preRenderCallback(EntitySeagull entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getEntityTexture(EntitySeagull entity) {
        return TEXTURE;
    }

    class LayerHeldItem extends LayerRenderer<EntitySeagull, ModelSeagull> {

        public LayerHeldItem(RenderSeagull render) {
            super(render);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntitySeagull entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            matrixStackIn.push();
            if (entitylivingbaseIn.isChild()) {
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0D);
            }
            matrixStackIn.push();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(0, -0.24F, -0.25F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-2.5F));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90F));
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
}
