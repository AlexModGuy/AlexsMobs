package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.message.MessageUpdateEagleControls;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    private static final ResourceLocation RADIUS_TEXTURE = new ResourceLocation("alexsmobs:textures/falconry_radius.png");
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
        if (event.getEntity() instanceof WanderingTraderEntity) {
            if (event.getEntity().getRidingEntity() instanceof EntityElephant) {
                if (!(event.getRenderer().entityModel instanceof ModelWanderingVillagerRider)) {
                    event.getRenderer().entityModel = new ModelWanderingVillagerRider();
                }
            }
        }
        if (event.getEntity().isPotionActive(AMEffectRegistry.CLINGING) && event.getEntity().getEyeHeight() < event.getEntity().getHeight() * 0.45F || event.getEntity().isPotionActive(AMEffectRegistry.DEBILITATING_STING) && event.getEntity().getCreatureAttribute() == CreatureAttribute.ARTHROPOD && event.getEntity().getWidth() > event.getEntity().getHeight()) {
            event.getMatrixStack().push();
            event.getMatrixStack().translate(0.0D, event.getEntity().getHeight() + 0.1F, 0.0D);
            event.getMatrixStack().rotate(Vector3f.ZP.rotationDegrees(180.0F));
            event.getEntity().prevRenderYawOffset = -event.getEntity().prevRenderYawOffset;
            event.getEntity().renderYawOffset = -event.getEntity().renderYawOffset;
            event.getEntity().prevRotationYawHead = -event.getEntity().prevRotationYawHead;
            event.getEntity().rotationYawHead = -event.getEntity().rotationYawHead;

        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPostRenderEntity(RenderLivingEvent.Post event) {
        if (event.getEntity().isPotionActive(AMEffectRegistry.CLINGING) && event.getEntity().getEyeHeight() < event.getEntity().getHeight() * 0.45F || event.getEntity().isPotionActive(AMEffectRegistry.DEBILITATING_STING) && event.getEntity().getCreatureAttribute() == CreatureAttribute.ARTHROPOD && event.getEntity().getWidth() > event.getEntity().getHeight()) {
            event.getMatrixStack().pop();
            event.getEntity().prevRenderYawOffset = -event.getEntity().prevRenderYawOffset;
            event.getEntity().renderYawOffset = -event.getEntity().renderYawOffset;
            event.getEntity().prevRotationYawHead = -event.getEntity().prevRotationYawHead;
            event.getEntity().rotationYawHead = -event.getEntity().rotationYawHead;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().getRenderViewEntity() instanceof EntityBaldEagle) {
            event.setCanceled(true);
        }
        if (!Minecraft.getInstance().player.getPassengers().isEmpty() && event.getHand() == Hand.MAIN_HAND) {
            PlayerEntity player = Minecraft.getInstance().player;
            boolean leftHand = false;
            if (player.getHeldItem(Hand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                leftHand = player.getPrimaryHand() == HandSide.LEFT;
            } else if (player.getHeldItem(Hand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                leftHand = player.getPrimaryHand() != HandSide.LEFT;
            }
            for (Entity entity : player.getPassengers()) {
                if (entity instanceof EntityBaldEagle) {
                    float yaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * event.getPartialTicks();
                    ClientProxy.currentUnrenderedEntities.remove(entity.getUniqueID());
                    MatrixStack matrixStackIn = event.getMatrixStack();
                    matrixStackIn.push();
                    matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                    matrixStackIn.translate(leftHand ? -0.8F : 0.8F, -0.6F, -1F);
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(yaw));
                    if (leftHand) {
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
                    } else {
                        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(90));
                    }
                    renderEntity(entity, 0, 0, 0, 0, event.getPartialTicks(), matrixStackIn, event.getBuffers(), event.getLight());
                    matrixStackIn.pop();
                    ClientProxy.currentUnrenderedEntities.add(entity.getUniqueID());
                }
            }
        }
        if (event.getItemStack().getItem() == AMItemRegistry.DIMENSIONAL_CARVER) {
          //  Minecraft.getInstance().player.resetActiveHand();
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLight) {
        EntityRenderer<? super E> render = null;
        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        try {
            render = manager.getRenderer(entityIn);

            if (render != null) {
                try {
                    render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
                } catch (Throwable throwable1) {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Rendering entity in world"));
                }
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
            entityIn.fillCrashReport(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
            crashreportcategory1.addDetail("Assigned renderer", render);
            crashreportcategory1.addDetail("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
            crashreportcategory1.addDetail("Rotation", Float.valueOf(yaw));
            crashreportcategory1.addDetail("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderNameplate(RenderNameplateEvent event) {
        if (Minecraft.getInstance().getRenderViewEntity() instanceof EntityBaldEagle && event.getEntity() == Minecraft.getInstance().player) {
            if (Minecraft.getInstance().isSingleplayer()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        AMItemstackRenderer.incrementTick();
        if (!AMConfig.shadersCompat) {
            if (Minecraft.getInstance().player.isPotionActive(AMEffectRegistry.LAVA_VISION)) {
                if (!previousLavaVision) {
                    RenderType lavaType = RenderType.getTranslucent();
                    RenderTypeLookup.setRenderLayer(Fluids.LAVA, lavaType);
                    RenderTypeLookup.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
                    previousFluidRenderer = Minecraft.getInstance().getBlockRendererDispatcher().fluidRenderer;
                    Minecraft.getInstance().getBlockRendererDispatcher().fluidRenderer = new LavaVisionFluidRenderer();
                    updateAllChunks();
                }
            } else {
                if (previousLavaVision) {
                    if (previousFluidRenderer != null) {
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
        if (Minecraft.getInstance().getRenderViewEntity() instanceof EntityBaldEagle) {
            EntityBaldEagle eagle = (EntityBaldEagle) Minecraft.getInstance().getRenderViewEntity();
            ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

            if (((EntityBaldEagle) Minecraft.getInstance().getRenderViewEntity()).shouldHoodedReturn() || eagle.removed) {
                Minecraft.getInstance().setRenderViewEntity(playerEntity);
                Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.values()[AlexsMobs.PROXY.getPreviousPOV()]);
            } else {
                float rotX = MathHelper.wrapDegrees(playerEntity.rotationYaw + playerEntity.rotationYawHead);
                float rotY = playerEntity.rotationPitch;
                Entity over = null;
                if (Minecraft.getInstance().objectMouseOver instanceof EntityRayTraceResult) {
                    over = ((EntityRayTraceResult) Minecraft.getInstance().objectMouseOver).getEntity();
                } else {
                    Minecraft.getInstance().objectMouseOver = null;
                }
                boolean loadChunks = playerEntity.world.getDayTime() % 10 == 0;
                ((EntityBaldEagle) Minecraft.getInstance().getRenderViewEntity()).directFromPlayer(rotX, rotY, false, over);
                AlexsMobs.NETWORK_WRAPPER.sendToServer(new MessageUpdateEagleControls(Minecraft.getInstance().getRenderViewEntity().getEntityId(), rotX, rotY, loadChunks, over == null ? -1 : over.getEntityId()));
            }
        }
    }

    private void updateAllChunks() {
        if (Minecraft.getInstance().worldRenderer.viewFrustum != null) {
            int length = Minecraft.getInstance().worldRenderer.viewFrustum.renderChunks.length;
            for (int i = 0; i < length; i++) {
                Minecraft.getInstance().worldRenderer.viewFrustum.renderChunks[i].needsUpdate = true;
            }
        }
    }
}
