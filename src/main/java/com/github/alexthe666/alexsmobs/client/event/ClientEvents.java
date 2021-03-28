package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.EntityFocalPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
    public void onPreRenderEntity(RenderLivingEvent.Pre event) {
        if(event.getEntity() instanceof WanderingTraderEntity){
            if(event.getEntity().getRidingEntity() instanceof EntityElephant){
                if(!(event.getRenderer().entityModel instanceof ModelWanderingVillagerRider)){
                    event.getRenderer().entityModel = new ModelWanderingVillagerRider();
                }
            }
        }
        if(event.getEntity().isPotionActive(AMEffectRegistry.CLINGING) && event.getEntity().getEyeHeight() < event.getEntity().getHeight() * 0.45F){
            event.getMatrixStack().push();
            event.getMatrixStack().translate(0.0D, (double)(event.getEntity().getHeight() + 0.1F), 0.0D);
            event.getMatrixStack().rotate(Vector3f.ZP.rotationDegrees(180.0F));
            event.getEntity().prevRenderYawOffset = - event.getEntity().prevRenderYawOffset;
            event.getEntity().renderYawOffset = - event.getEntity().renderYawOffset;
            event.getEntity().prevRotationYawHead = - event.getEntity().prevRotationYawHead;
            event.getEntity().rotationYawHead = - event.getEntity().rotationYawHead;

        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPostRenderEntity(RenderLivingEvent.Post event) {
        if(event.getEntity().isPotionActive(AMEffectRegistry.CLINGING) && event.getEntity().getEyeHeight() < event.getEntity().getHeight() * 0.45F) {
            event.getMatrixStack().pop();
            event.getEntity().prevRenderYawOffset = - event.getEntity().prevRenderYawOffset;
            event.getEntity().renderYawOffset = - event.getEntity().renderYawOffset;
            event.getEntity().prevRotationYawHead = - event.getEntity().prevRotationYawHead;
            event.getEntity().rotationYawHead = - event.getEntity().rotationYawHead;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        AMItemstackRenderer.incrementTick();
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
