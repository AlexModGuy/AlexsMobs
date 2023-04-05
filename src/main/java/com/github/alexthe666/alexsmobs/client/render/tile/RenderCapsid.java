package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.Random;

public class RenderCapsid<T extends TileEntityCapsid> implements BlockEntityRenderer<T> {

    private final Random random = new Random();
    public RenderCapsid(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    protected int getModelCount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack stack = entity.getItem(0);
        if (!stack.isEmpty()) {
            int i =  Item.getId(stack.getItem()) + stack.getDamageValue();
            this.random.setSeed((long)i);
            float floatProgress = entity.prevFloatUpProgress + (entity.floatUpProgress - entity.prevFloatUpProgress) * partialTicks;
            float yaw = entity.prevYawSwitchProgress + (entity.yawSwitchProgress - entity.prevYawSwitchProgress) * partialTicks;
            int j = this.getModelCount(stack);
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F + floatProgress, 0.5F);
            matrixStackIn.mulPose((new Quaternionf()).rotateY((float) Math.toRadians(entity.getBlockAngle() + yaw)));
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, -0.1F, 0);
            if(entity.vibratingThisTick && entity.getLevel() != null){
                float vibrate = 0.05F;
                matrixStackIn.translate((entity.getLevel().random.nextFloat() - 0.5F)* vibrate, (entity.getLevel().random.nextFloat() - 0.5F) * vibrate, (entity.getLevel().random.nextFloat() - 0.5F)* vibrate);
            }
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            BakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getModel(stack, entity.getLevel(), (LivingEntity)null, 0);
            boolean flag = ibakedmodel.isGui3d();
            if (!flag) {
                float f7 = -0.0F * (float)(j - 1) * 0.5F;
                float f8 = -0.0F * (float)(j - 1) * 0.5F;
                float f9 = -0.09375F * (float)(j - 1) * 0.5F;
                matrixStackIn.translate((double)f7, (double)f8, (double)f9);
            }

            for(int k = 0; k < j; ++k) {
                matrixStackIn.pushPose();
                if (k > 0) {
                    if (flag) {
                        float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        matrixStackIn.translate(f11, f13, f10);
                    } else {
                        float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        matrixStackIn.translate(f12, f14, 0.0D);
                    }
                }

                Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
                matrixStackIn.popPose();
                if (!flag) {
                    matrixStackIn.translate(0.0, 0.0, 0.09375F);
                }
            }


            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

    }
}
