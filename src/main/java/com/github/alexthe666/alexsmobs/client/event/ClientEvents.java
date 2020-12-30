package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
    private FluidBlockRenderer previousFluidRenderer;

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
        if(!AMConfig.shadersCompat){
            if (Minecraft.getInstance().player.isPotionActive(AMEffectRegistry.LAVA_VISION)) {
                if (!previousLavaVision) {
                    RenderType lavaType = RenderType.getTranslucent();
                    RenderTypeLookup.setRenderLayer(Fluids.LAVA, lavaType);
                    RenderTypeLookup.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
                    previousFluidRenderer = Minecraft.getInstance().getBlockRendererDispatcher().fluidRenderer;
                    Minecraft.getInstance().getBlockRendererDispatcher().fluidRenderer = new LavaVisionFluidRenderer();
                    updateAllChunks();
                }
            }else{
                if (previousLavaVision) {
                    if(previousFluidRenderer != null){
                        RenderType lavaType = RenderType.getSolid();
                        RenderTypeLookup.setRenderLayer(Fluids.LAVA, lavaType);
                        RenderTypeLookup.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
                        Minecraft.getInstance().getBlockRendererDispatcher().fluidRenderer = previousFluidRenderer;
                    }
                    updateAllChunks();
                }
            }
            previousLavaVision = Minecraft.getInstance().player.isPotionActive(AMEffectRegistry.LAVA_VISION);
        }
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
