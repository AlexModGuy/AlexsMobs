package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockVoidWormBeak;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBeak;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class RenderVoidWormBeak<T extends TileEntityVoidWormBeak> extends BlockEntityRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm_beak.png");
    private static ModelVoidWormBeak HEAD_MODEL = new ModelVoidWormBeak();

    public RenderVoidWormBeak(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        Direction dir = tileEntityIn.getBlockState().getValue(BlockVoidWormBeak.FACING);
        if(dir == Direction.UP){
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        }else if(dir == Direction.DOWN){
            matrixStackIn.translate(0.5F, -0.5F, 0.5F);

        }else if(dir == Direction.NORTH){
            matrixStackIn.translate(0.5, 0.5F, -0.5F);
        }else if(dir == Direction.EAST){
            matrixStackIn.translate(1.5F, 0.5F, 0.5F);

        }else if(dir == Direction.SOUTH){
            matrixStackIn.translate(0.5, 0.5F, 1.5F);

        }else if(dir == Direction.WEST){
            matrixStackIn.translate(-0.5F, 0.5F, 0.5F);
        }
        matrixStackIn.mulPose(dir.getOpposite().getRotation());
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, -0.01F, 0.0F);
        HEAD_MODEL.renderBeak(tileEntityIn, partialTicks);
        HEAD_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1, 1F, 1, 1);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
