package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.client.render.RenderVineLasso;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageUpdateEagleControls;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    private static final ResourceLocation RADIUS_TEXTURE = new ResourceLocation("alexsmobs:textures/falconry_radius.png");
    private boolean previousLavaVision = false;
    private LiquidBlockRenderer previousFluidRenderer;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onOutlineEntityColor(EventGetOutlineColor event) {
        if (event.getEntityIn() instanceof ItemEntity && ItemTags.getAllTags().getTag(AMTagRegistry.VOID_WORM_DROPS).contains(((ItemEntity) event.getEntityIn()).getItem().getItem())) {
            int fromColor = 0;
            int toColor = 0X21E5FF;
            float startR = (float) (fromColor >> 16 & 255) / 255.0F;
            float startG = (float) (fromColor >> 8 & 255) / 255.0F;
            float startB = (float) (fromColor & 255) / 255.0F;
            float endR = (float) (toColor >> 16 & 255) / 255.0F;
            float endG = (float) (toColor >> 8 & 255) / 255.0F;
            float endB = (float) (toColor & 255) / 255.0F;
            float f = (float) (Math.cos(0.4F * (event.getEntityIn().tickCount + Minecraft.getInstance().getFrameTime())) + 1.0F) * 0.5F;
            float r = (endR - startR) * f + startR;
            float g = (endG - startG) * f + startG;
            float b = (endB - startB) * f + startB;
            int j = ((((int) (r * 255)) & 0xFF) << 16) |
                    ((((int) (g * 255)) & 0xFF) << 8) |
                    ((((int) (b * 255)) & 0xFF) << 0);
            event.setColor(j);
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onFogDensity(EntityViewRenderEvent.RenderFogEvent event) {
        FogType fogType = event.getCamera().getFluidInCamera();
        if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.LAVA_VISION) && fogType == FogType.LAVA) {
            RenderSystem.setShaderFogStart(-8.0F);
            RenderSystem.setShaderFogEnd(50.0F);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPreRenderEntity(RenderLivingEvent.Pre event) {
        if (event.getEntity() instanceof WanderingTrader) {
            if (event.getEntity().getVehicle() instanceof EntityElephant) {
                if (!(event.getRenderer().model instanceof ModelWanderingVillagerRider)) {
                    event.getRenderer().model = new ModelWanderingVillagerRider(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.SITTING_WANDERING_VILLAGER));
                }
            }
        }
        if (event.getEntity().hasEffect(AMEffectRegistry.CLINGING) && event.getEntity().getEyeHeight() < event.getEntity().getBbHeight() * 0.45F || event.getEntity().hasEffect(AMEffectRegistry.DEBILITATING_STING) && event.getEntity().getMobType() == MobType.ARTHROPOD && event.getEntity().getBbWidth() > event.getEntity().getBbHeight()) {
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(0.0D, event.getEntity().getBbHeight() + 0.1F, 0.0D);
            event.getPoseStack().mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            event.getEntity().yBodyRotO = -event.getEntity().yBodyRotO;
            event.getEntity().yBodyRot = -event.getEntity().yBodyRot;
            event.getEntity().yHeadRotO = -event.getEntity().yHeadRotO;
            event.getEntity().yHeadRot = -event.getEntity().yHeadRot;
        }
        if (event.getEntity().hasEffect(AMEffectRegistry.ENDER_FLU)) {
            event.getPoseStack().pushPose();
            event.getPoseStack().mulPose(Vector3f.YP.rotationDegrees((float) (Math.cos((double) event.getEntity().tickCount * 7F) * Math.PI * (double) 1.2F)));
            float vibrate = 0.05F;
            event.getPoseStack().translate((event.getEntity().getRandom().nextFloat() - 0.5F) * vibrate, (event.getEntity().getRandom().nextFloat() - 0.5F) * vibrate, (event.getEntity().getRandom().nextFloat() - 0.5F) * vibrate);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPostRenderEntity(RenderLivingEvent.Post event) {
        if (event.getEntity().hasEffect(AMEffectRegistry.ENDER_FLU)) {
            event.getPoseStack().popPose();
        }
        if (event.getEntity().hasEffect(AMEffectRegistry.CLINGING) && event.getEntity().getEyeHeight() < event.getEntity().getBbHeight() * 0.45F || event.getEntity().hasEffect(AMEffectRegistry.DEBILITATING_STING) && event.getEntity().getMobType() == MobType.ARTHROPOD && event.getEntity().getBbWidth() > event.getEntity().getBbHeight()) {
            event.getPoseStack().popPose();
            event.getEntity().yBodyRotO = -event.getEntity().yBodyRotO;
            event.getEntity().yBodyRot = -event.getEntity().yBodyRot;
            event.getEntity().yHeadRotO = -event.getEntity().yHeadRotO;
            event.getEntity().yHeadRot = -event.getEntity().yHeadRot;
        }
        if (VineLassoUtil.hasLassoData(event.getEntity()) && !(event.getEntity() instanceof Player)) {
            Entity lassoedOwner = VineLassoUtil.getLassoedTo(event.getEntity());
            if (lassoedOwner instanceof LivingEntity && lassoedOwner != event.getEntity()) {
                double d0 = Mth.lerp(event.getPartialTick(), event.getEntity().xOld, event.getEntity().getX());
                double d1 = Mth.lerp(event.getPartialTick(), event.getEntity().yOld, event.getEntity().getY());
                double d2 = Mth.lerp(event.getPartialTick(), event.getEntity().zOld, event.getEntity().getZ());
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(-d0, -d1, -d2);
                RenderVineLasso.renderVine(event.getEntity(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), (LivingEntity) lassoedOwner, ((LivingEntity) lassoedOwner).getMainArm() == HumanoidArm.LEFT, 0.1F);
                event.getPoseStack().popPose();
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPoseHand(EventPosePlayerHand event) {
        LivingEntity player = (LivingEntity) event.getEntityIn();
        float f = Minecraft.getInstance().getFrameTime();
        boolean leftHand = false;
        boolean usingLasso = player.isUsingItem() && player.getUseItem().is(AMItemRegistry.VINE_LASSO);
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.VINE_LASSO) {
            leftHand = player.getMainArm() == HumanoidArm.LEFT;
        } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.VINE_LASSO) {
            leftHand = player.getMainArm() != HumanoidArm.LEFT;
        }
        if (leftHand && event.isLeftHand() && usingLasso) {
            float swing = (float) Math.sin(player.tickCount + f) * 0.5F;
            event.setResult(Event.Result.ALLOW);
            event.getModel().leftArm.xRot = (float) Math.toRadians(-120F) + (float) Math.sin(player.tickCount + f) * 0.5F;
            event.getModel().leftArm.yRot = (float) Math.toRadians(-20F) + (float) Math.cos(player.tickCount + f) * 0.5F;
        }
        if (!leftHand && !event.isLeftHand() && usingLasso) {
            event.setResult(Event.Result.ALLOW);
            event.getModel().rightArm.xRot = (float) Math.toRadians(-120F) + (float) Math.sin(player.tickCount + f) * 0.5F;
            event.getModel().rightArm.yRot = (float) Math.toRadians(20F) - (float) Math.cos(player.tickCount + f) * 0.5F;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle) {
            event.setCanceled(true);
        }
        if (!Minecraft.getInstance().player.getPassengers().isEmpty() && event.getHand() == InteractionHand.MAIN_HAND) {
            Player player = Minecraft.getInstance().player;
            boolean leftHand = false;
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                leftHand = player.getMainArm() == HumanoidArm.LEFT;
            } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                leftHand = player.getMainArm() != HumanoidArm.LEFT;
            }
            for (Entity entity : player.getPassengers()) {
                if (entity instanceof EntityBaldEagle) {
                    float yaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * event.getPartialTicks();
                    ClientProxy.currentUnrenderedEntities.remove(entity.getUUID());
                    PoseStack matrixStackIn = event.getPoseStack();
                    matrixStackIn.pushPose();
                    matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                    matrixStackIn.translate(leftHand ? -0.8F : 0.8F, -0.6F, -1F);
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(yaw));
                    if (leftHand) {
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
                    } else {
                        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(90));
                    }
                    renderEntity(entity, 0, 0, 0, 0, event.getPartialTicks(), matrixStackIn, event.getMultiBufferSource(), event.getPackedLight());
                    matrixStackIn.popPose();
                    ClientProxy.currentUnrenderedEntities.add(entity.getUUID());
                }
            }
        }
        if (Minecraft.getInstance().player.getUseItem().getItem() == AMItemRegistry.DIMENSIONAL_CARVER && event.getItemStack().getItem() == AMItemRegistry.DIMENSIONAL_CARVER) {
            PoseStack matrixStackIn = event.getPoseStack();
            matrixStackIn.pushPose();
            ItemInHandRenderer renderer = Minecraft.getInstance().getItemInHandRenderer();
            InteractionHand hand = MoreObjects.firstNonNull(Minecraft.getInstance().player.swingingArm, InteractionHand.MAIN_HAND);
            float f = Minecraft.getInstance().player.getAttackAnim(event.getPartialTicks());
            float f1 = Mth.lerp(event.getPartialTicks(), Minecraft.getInstance().player.xRotO, Minecraft.getInstance().player.getXRot());
            float f5 = -0.4F * Mth.sin(Mth.sqrt(f) * (float) Math.PI);
            float f6 = 0.2F * Mth.sin(Mth.sqrt(f) * ((float) Math.PI * 2F));
            float f10 = -0.2F * Mth.sin(f * (float) Math.PI);
            HumanoidArm handside = hand == InteractionHand.MAIN_HAND ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
            boolean flag3 = handside == HumanoidArm.RIGHT;
            int l = flag3 ? 1 : -1;
            matrixStackIn.translate((float) l * f5, f6, f10);
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer<? super E> render = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            render = manager.getRenderer(entityIn);

            if (render != null) {
                try {
                    render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
                } catch (Throwable throwable1) {
                    throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
                }
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.forThrowable(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
            entityIn.fillCrashReportCategory(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
            crashreportcategory1.setDetail("Assigned renderer", render);
            crashreportcategory1.setDetail("Rotation", Float.valueOf(yaw));
            crashreportcategory1.setDetail("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderNameplate(RenderNameplateEvent event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle && event.getEntity() == Minecraft.getInstance().player) {
            if (Minecraft.getInstance().hasSingleplayerServer()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderWorldLastEvent(RenderLevelLastEvent event) {
        AMItemstackRenderer.incrementTick();
        if (!AMConfig.shadersCompat) {
            if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.LAVA_VISION)) {
                if (!previousLavaVision) {
                    RenderType lavaType = RenderType.translucent();
                    ItemBlockRenderTypes.setRenderLayer(Fluids.LAVA, lavaType);
                    ItemBlockRenderTypes.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
                    previousFluidRenderer = Minecraft.getInstance().getBlockRenderer().liquidBlockRenderer;
                    Minecraft.getInstance().getBlockRenderer().liquidBlockRenderer = new LavaVisionFluidRenderer();
                    updateAllChunks();
                }
            } else {
                if (previousLavaVision) {
                    if (previousFluidRenderer != null) {
                        RenderType lavaType = RenderType.solid();
                        ItemBlockRenderTypes.setRenderLayer(Fluids.LAVA, lavaType);
                        ItemBlockRenderTypes.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
                        Minecraft.getInstance().getBlockRenderer().liquidBlockRenderer = previousFluidRenderer;
                    }
                    updateAllChunks();
                }
            }
            previousLavaVision = Minecraft.getInstance().player.hasEffect(AMEffectRegistry.LAVA_VISION);
            if (AMConfig.clingingFlipEffect) {
                if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.CLINGING) && Minecraft.getInstance().player.getEyeHeight() < Minecraft.getInstance().player.getBbHeight() * 0.45F) {
                    Minecraft.getInstance().gameRenderer.loadEffect(new ResourceLocation("shaders/post/flip.json"));
                } else if (Minecraft.getInstance().gameRenderer.currentEffect() != null && Minecraft.getInstance().gameRenderer.currentEffect().getName().equals("minecraft:shaders/post/flip.json")) {
                    Minecraft.getInstance().gameRenderer.shutdownEffect();
                }
            }
        }
        if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle) {
            EntityBaldEagle eagle = (EntityBaldEagle) Minecraft.getInstance().getCameraEntity();
            LocalPlayer playerEntity = Minecraft.getInstance().player;

            if (((EntityBaldEagle) Minecraft.getInstance().getCameraEntity()).shouldHoodedReturn() || eagle.isRemoved()) {
                Minecraft.getInstance().setCameraEntity(playerEntity);
                Minecraft.getInstance().options.setCameraType(CameraType.values()[AlexsMobs.PROXY.getPreviousPOV()]);
            } else {
                float rotX = Mth.wrapDegrees(playerEntity.getYRot() + playerEntity.yHeadRot);
                float rotY = playerEntity.getXRot();
                Entity over = null;
                if (Minecraft.getInstance().hitResult instanceof EntityHitResult) {
                    over = ((EntityHitResult) Minecraft.getInstance().hitResult).getEntity();
                } else {
                    Minecraft.getInstance().hitResult = null;
                }
                boolean loadChunks = playerEntity.level.getDayTime() % 10 == 0;
                ((EntityBaldEagle) Minecraft.getInstance().getCameraEntity()).directFromPlayer(rotX, rotY, false, over);
                AlexsMobs.NETWORK_WRAPPER.sendToServer(new MessageUpdateEagleControls(Minecraft.getInstance().getCameraEntity().getId(), rotX, rotY, loadChunks, over == null ? -1 : over.getId()));
            }
        }
    }

    private void updateAllChunks() {
        if (Minecraft.getInstance().levelRenderer.viewArea != null) {
            int length = Minecraft.getInstance().levelRenderer.viewArea.chunks.length;
            for (int i = 0; i < length; i++) {
                Minecraft.getInstance().levelRenderer.viewArea.chunks[i].dirty = true;
            }
        }
    }
}
