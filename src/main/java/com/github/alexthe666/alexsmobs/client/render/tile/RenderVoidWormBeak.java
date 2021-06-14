package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockVoidWormBeak;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBeak;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class RenderVoidWormBeak<T extends TileEntityVoidWormBeak> extends TileEntityRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm_beak.png");
    private static ModelVoidWormBeak HEAD_MODEL = new ModelVoidWormBeak();

    public RenderVoidWormBeak(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, -0.5F, 0.5F);
        matrixStackIn.rotate(new Quaternion(Vector3f.XP, this.getRotationX(tileEntityIn), true));
        matrixStackIn.rotate(new Quaternion(Vector3f.YP, this.getRotationY(tileEntityIn), true));
        matrixStackIn.push();
        HEAD_MODEL.renderBeak(tileEntityIn, partialTicks);
        HEAD_MODEL.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1, 1F, 1, 1);
        matrixStackIn.pop();
        matrixStackIn.pop();

    }

    private float getRotationY(TileEntityVoidWormBeak lectern) {
        switch (lectern.getBlockState().get(BlockVoidWormBeak.FACING)) {
            default:
                return 180;
            case EAST:
                return 90;
            case WEST:
                return -90;
            case SOUTH:
                return 0;

        }
    }

    private float getRotationX(TileEntityVoidWormBeak lectern) {
        switch (lectern.getBlockState().get(BlockVoidWormBeak.FACING)) {
            default:
                return 0;
            case UP:
                return -180;
            case DOWN:
                return 180;

        }
    }
}
