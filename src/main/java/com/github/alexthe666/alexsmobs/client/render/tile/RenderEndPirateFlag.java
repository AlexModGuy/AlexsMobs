package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateFlag;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateShipWheel;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateFlag;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class RenderEndPirateFlag<T extends TileEntityEndPirateFlag> implements BlockEntityRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/flag.png");
    private static ModelEndPirateFlag FLAG_MODEL = new ModelEndPirateFlag();


    public RenderEndPirateFlag(Context rendererDispatcherIn) {
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        Direction dir = tileEntityIn.getBlockState().getValue(BlockEndPirateFlag.FACING);
        if(dir == Direction.NORTH){
            matrixStackIn.translate(0.5, 1.5F, 0.5F);
        }else if(dir == Direction.EAST){
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        }else if(dir == Direction.SOUTH){
            matrixStackIn.translate(0.5, 1.5F, 0.5F);
        }else if(dir == Direction.WEST){
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        }
        matrixStackIn.mulPose(dir.getOpposite().getRotation());
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(dir.getAxis() == Direction.Axis.Y ? -90.0F : 90.0F));
        matrixStackIn.pushPose();
        FLAG_MODEL.renderFlag(tileEntityIn, partialTicks);
        FLAG_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1, 1F, 1, 1);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
