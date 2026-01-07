package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

/**
 * A custom fluid renderer that applies transparency to lava when the player has the LAVA_VISION effect.
 * Uses VertexConsumerWrapper to modify the alpha value of the vanilla lava rendering.
 */
public class LavaVisionFluidRenderer extends LiquidBlockRenderer {

    @Override
    public void tesselate(BlockAndTintGetter lightReaderIn, BlockPos posIn, VertexConsumer vertexBuilderIn,
            BlockState blockstateIn, FluidState fluidStateIn) {
        // Only apply transparency to lava
        if (fluidStateIn.is(FluidTags.LAVA)) {
            final float alpha = (float) AMConfig.lavaOpacity;
            // Wrap the vertex consumer to modify the alpha value
            VertexConsumer wrappedConsumer = new VertexConsumerWrapper(vertexBuilderIn) {
                @Override
                public VertexConsumer setColor(int r, int g, int b, int a) {
                    // Apply custom alpha from config
                    return super.setColor(r, g, b, (int)(alpha * 255));
                }
            };
            // Use the vanilla renderer with our wrapped consumer
            super.tesselate(lightReaderIn, posIn, wrappedConsumer, blockstateIn, fluidStateIn);
        } else {
            // For non-lava fluids, use the default rendering
            super.tesselate(lightReaderIn, posIn, vertexBuilderIn, blockstateIn, fluidStateIn);
        }
    }
}
