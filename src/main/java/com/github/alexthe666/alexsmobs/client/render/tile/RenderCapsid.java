package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Random;

public class RenderCapsid<T extends TileEntityCapsid> extends TileEntityRenderer<T> {

    private final Random random = new Random();
    public RenderCapsid(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
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
    public void render(T entity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack stack = entity.getStackInSlot(0);
        if (!stack.isEmpty()) {
            int i =  Item.getIdFromItem(stack.getItem()) + stack.getDamage();
            this.random.setSeed((long)i);
            float floatProgress = entity.prevFloatUpProgress + (entity.floatUpProgress - entity.prevFloatUpProgress) * partialTicks;
            float yaw = entity.prevYawSwitchProgress + (entity.yawSwitchProgress - entity.prevYawSwitchProgress) * partialTicks;
            int j = this.getModelCount(stack);
            matrixStackIn.push();
            matrixStackIn.translate(0.5F, 0.5F + floatProgress, 0.5F);
            matrixStackIn.rotate(new Quaternion(Vector3f.YP, entity.getBlockAngle() + yaw, true));
            matrixStackIn.push();
            matrixStackIn.translate(0, -0.1F, 0);
            if(entity.vibrating && entity.getWorld() != null){
                float vibrate = 0.05F;
                matrixStackIn.translate((entity.getWorld().rand.nextFloat() - 0.5F)* vibrate, (entity.getWorld().rand.nextFloat() - 0.5F) * vibrate, (entity.getWorld().rand.nextFloat() - 0.5F)* vibrate);
            }
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, entity.getWorld(), (LivingEntity)null);
            boolean flag = ibakedmodel.isGui3d();
            if (!flag) {
                float f7 = -0.0F * (float)(j - 1) * 0.5F;
                float f8 = -0.0F * (float)(j - 1) * 0.5F;
                float f9 = -0.09375F * (float)(j - 1) * 0.5F;
                matrixStackIn.translate((double)f7, (double)f8, (double)f9);
            }

            for(int k = 0; k < j; ++k) {
                matrixStackIn.push();
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

                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
                matrixStackIn.pop();
                if (!flag) {
                    matrixStackIn.translate(0.0, 0.0, 0.09375F);
                }
            }


            matrixStackIn.pop();
            matrixStackIn.pop();
        }

    }
}
