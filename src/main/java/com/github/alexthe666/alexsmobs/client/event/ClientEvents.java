package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    private boolean previousLavaVision = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        FluidState fluidstate = event.getInfo().getFluidState();
        if (Minecraft.getInstance().player.isPotionActive(AMEffectRegistry.LAVA_VISION)) {
            if (fluidstate.isTagged(FluidTags.LAVA)) {
                event.setDensity(0.05F);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        if (Minecraft.getInstance().player.isPotionActive(AMEffectRegistry.LAVA_VISION)) {
            if (!previousLavaVision) {
                Minecraft.getInstance().getBlockRendererDispatcher().fluidRenderer = new LavaVisionFluidRenderer();
                updateAllChunks();
            }
        }else{
            if (previousLavaVision) {
                Minecraft.getInstance().getBlockRendererDispatcher().fluidRenderer = new FluidBlockRenderer();
                updateAllChunks();
            }
        }
        previousLavaVision = Minecraft.getInstance().player.isPotionActive(AMEffectRegistry.LAVA_VISION);
    }

    private void updateAllChunks(){
        if (Minecraft.getInstance().worldRenderer.viewFrustum != null) {
            int length = Minecraft.getInstance().worldRenderer.viewFrustum.renderChunks.length;
            for (int i = 0; i < length; i++) {
                Minecraft.getInstance().worldRenderer.viewFrustum.renderChunks[i].needsUpdate = true;
            }
        }
    }
}
