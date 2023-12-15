package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.model.*;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemStinkRay;
import com.github.alexthe666.alexsmobs.item.ItemTabIcon;
import com.github.alexthe666.alexsmobs.item.ItemVineLasso;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AMItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    public static int ticksExisted = 0;
    private static final ModelShieldOfTheDeep SHIELD_OF_THE_DEEP_MODEL = new ModelShieldOfTheDeep();
    private static final ResourceLocation SHIELD_OF_THE_DEEP_TEXTURE = new ResourceLocation("alexsmobs:textures/armor/shield_of_the_deep.png");
    private static final ModelMysteriousWorm MYTERIOUS_WORM_MODEL = new ModelMysteriousWorm();
    private static final ResourceLocation MYTERIOUS_WORM_TEXTURE = new ResourceLocation("alexsmobs:textures/item/mysterious_worm_model.png");
    private static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();
    private static final ResourceLocation ANCHOR_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor.png");
    private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
    private static final ResourceLocation WINCH_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor_winch.png");
    private static final ModelEndPirateShipWheel SHIP_WHEEL_MODEL = new ModelEndPirateShipWheel();
    private static final ResourceLocation SHIP_WHEEL_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/ship_wheel.png");
    private static final ResourceLocation TRANSMUTATION_TABLE_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/farseer/transmutation_table.png");
    private static final ResourceLocation TRANSMUTATION_TABLE_GLOW_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/farseer/transmutation_table_glow.png");
    private static final ResourceLocation TRANSMUTATION_TABLE_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/farseer/transmutation_table_overlay.png");
    private static final ModelTransmutationTable TRANSMUTATION_TABLE_MODEL = new ModelTransmutationTable(0F);
    private static final ModelTransmutationTable TRANSMUTATION_TABLE_OVERLAY_MODEL = new ModelTransmutationTable(0.01F);
    private static List<ItemStack> DIMENSIONAL_CARVER_SHARDS;


    private final Map<String, Entity> renderedEntites = new HashMap<>();
    private final List<EntityType> blockedRenderEntities = new ArrayList<>();

    public AMItemstackRenderer() {
        super(null, null);
    }

    public static void incrementTick() {
        ticksExisted++;
    }

    private static float getScaleFor(EntityType type, List<Pair<EntityType, Float>> mobIcons) {
        for (Pair<EntityType, Float> pair : mobIcons) {
            if (pair.getFirst() == type) {
                return pair.getSecond();
            }
        }
        return 1.0F;
    }

    private static List<ItemStack> getDimensionalCarverShards() {
        if (DIMENSIONAL_CARVER_SHARDS == null || DIMENSIONAL_CARVER_SHARDS.isEmpty()) {
            DIMENSIONAL_CARVER_SHARDS = Util.make(Lists.newArrayList(), (list) -> {
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_0"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_1"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_2"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_3"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_4"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_5"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_6"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_7"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_8"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_9"))));
                list.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_10"))));
            });
        }
        return DIMENSIONAL_CARVER_SHARDS;
    }

    public static void drawEntityOnScreen(PoseStack matrixstack, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity) {
        float f = (float) Math.atan(-mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        matrixstack.scale(scale, scale, scale);
        entity.setOnGround(false);
        float partialTicks = Minecraft.getInstance().getFrameTime();
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion1 = Axis.XP.rotationDegrees(20.0F);
        float partialTicksForRender = Minecraft.getInstance().isPaused() || entity instanceof EntityMimicOctopus ? 0 : partialTicks;
        int tick;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = Minecraft.getInstance().player.tickCount;
        }
        if (follow) {
            float yaw = f * 45.0F;
            entity.setYRot(yaw);
            entity.tickCount = tick;
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).yBodyRot = yaw;
                ((LivingEntity) entity).yBodyRotO = yaw;
                ((LivingEntity) entity).yHeadRot = yaw;
                ((LivingEntity) entity).yHeadRotO = yaw;
            }

            quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
            quaternion.mul(quaternion1);
        }

        matrixstack.mulPose(quaternion);
        matrixstack.mulPose(Axis.XP.rotationDegrees((float) (-xRot)));
        matrixstack.mulPose(Axis.YP.rotationDegrees((float) yRot));
        matrixstack.mulPose(Axis.ZP.rotationDegrees((float) zRot));
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicksForRender, matrixstack, multibuffersource$buffersource, 15728880);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        entity.setYRot(0.0F);
        entity.setXRot(0.0F);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).yBodyRot = 0.0F;
            ((LivingEntity) entity).yHeadRotO = 0.0F;
            ((LivingEntity) entity).yHeadRot = 0.0F;
        }
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int tick;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = Minecraft.getInstance().player.tickCount;
        }
        Level level = Minecraft.getInstance().level;
        if (itemStackIn.getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.4F, -0.75F, 0.5F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180));
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(SHIELD_OF_THE_DEEP_TEXTURE), false, itemStackIn.hasFoil());
            SHIELD_OF_THE_DEEP_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if (itemStackIn.getItem() == AMItemRegistry.MYSTERIOUS_WORM.get()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, -2F, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180));
            MYTERIOUS_WORM_MODEL.animateStack(itemStackIn);
            MYTERIOUS_WORM_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(MYTERIOUS_WORM_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if (itemStackIn.getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.FALCONRY_GLOVE_HAND.get()), transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.FALCONRY_GLOVE_INVENTORY.get()), transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }
        if (itemStackIn.getItem() == AMItemRegistry.VINE_LASSO.get()) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                if (ItemVineLasso.isItemInUse(itemStackIn)) {
                    if (transformType.firstPerson()) {
                        matrixStackIn.translate(transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.0f, -0.5f);
                    }
                    matrixStackIn.mulPose(Axis.YP.rotation(tick + Minecraft.getInstance().getFrameTime()));
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.VINE_LASSO_HAND.get()), transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.VINE_LASSO_INVENTORY.get()), transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }
        if (itemStackIn.getItem() == AMItemRegistry.SKELEWAG_SWORD.get()) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(AMItemRegistry.SKELEWAG_SWORD_INVENTORY.get());
            ItemStack handItem = new ItemStack(AMItemRegistry.SKELEWAG_SWORD_HAND.get());
            spriteItem.setTag(itemStackIn.getTag());
            handItem.setTag(itemStackIn.getTag());
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                Minecraft.getInstance().getItemRenderer().renderStatic(handItem, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }
        if (itemStackIn.getItem() == AMBlockRegistry.TRANSMUTATION_TABLE.get().asItem()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.6F, 0.5F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
            TRANSMUTATION_TABLE_MODEL.resetToDefaultPose();
            TRANSMUTATION_TABLE_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TRANSMUTATION_TABLE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            TRANSMUTATION_TABLE_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucentEmissive(TRANSMUTATION_TABLE_GLOW_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            TRANSMUTATION_TABLE_OVERLAY_MODEL.resetToDefaultPose();
            VertexConsumer staticyOverlay = bufferIn.getBuffer(RenderType.eyes(TRANSMUTATION_TABLE_OVERLAY));
            TRANSMUTATION_TABLE_OVERLAY_MODEL.renderToBuffer(matrixStackIn, staticyOverlay, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if (itemStackIn.getItem() == AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER.get()) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            float f = tick + Minecraft.getInstance().getFrameTime();
            List<ItemStack> shards = getDimensionalCarverShards();
            matrixStackIn.pushPose();
            if(transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
                matrixStackIn.translate(-0.2F, 0, 0);
                matrixStackIn.scale(1.3F, 1.3F, 1.3F);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(60));
            }
            for (int i = 0; i < shards.size(); i++) {
                matrixStackIn.pushPose();
                ItemStack shard = shards.get(i);
                matrixStackIn.translate((float) Math.sin(f * 0.15F + i * 1F) * 0.035F, -(float) Math.cos(f * 0.15F + i * 1F) * 0.035F, (float) Math.cos(f * 0.15F + i * 0.5F + Math.PI / 2F) * 0.025F);
                Minecraft.getInstance().getItemRenderer().renderStatic(shard, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
                matrixStackIn.popPose();
            }
            matrixStackIn.popPose();
        }
        if (itemStackIn.getItem() == AMItemRegistry.STINK_RAY.get()) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack hand = new ItemStack(ItemStinkRay.isUsable(itemStackIn) ? AMItemRegistry.STINK_RAY_HAND.get() : AMItemRegistry.STINK_RAY_EMPTY_HAND.get());
            ItemStack inventory = new ItemStack(ItemStinkRay.isUsable(itemStackIn) ? AMItemRegistry.STINK_RAY_INVENTORY.get() : AMItemRegistry.STINK_RAY_EMPTY_INVENTORY.get());
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                Minecraft.getInstance().getItemRenderer().renderStatic(hand, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(inventory, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }
        //TODO reimplement
        /*
        if(itemStackIn.getItem() == AMBlockRegistry.END_PIRATE_ANCHOR.get().asItem()){
            matrixStackIn.pushPose();
            matrixStackIn.translate(1F, 0F, 0);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
            matrixStackIn.scale(0.75F, 0.75F, 0.75F);
            ANCHOR_MODEL.animateStack(itemStackIn);
            ANCHOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(ANCHOR_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMBlockRegistry.END_PIRATE_ANCHOR_WINCH.get().asItem()){
            matrixStackIn.pushPose();
            matrixStackIn.translate(1, -1F, 0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180));
            WINCH_MODEL.animateStack(itemStackIn);
            WINCH_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(WINCH_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMBlockRegistry.END_PIRATE_SHIP_WHEEL.get().asItem()){
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90));
            matrixStackIn.scale(0.8F, 0.8F, 0.8F);
            SHIP_WHEEL_MODEL.resetToDefaultPose();
            SHIP_WHEEL_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(SHIP_WHEEL_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        */
        if (itemStackIn.getItem() == AMItemRegistry.TAB_ICON.get()) {
            Entity fakeEntity = null;
            List<Pair<EntityType, Float>> mobIcons = AMMobIcons.getMobIcons();
            int entityIndex = (tick / 40) % (mobIcons.size());
            float scale = 1.0F;
            int flags = 0;
            if (level != null) {
                if (ItemTabIcon.hasCustomEntityDisplay(itemStackIn)) {
                    flags = itemStackIn.getTag().getInt("DisplayMobFlags");
                    String index = ItemTabIcon.getCustomDisplayEntityString(itemStackIn);
                    EntityType local = ItemTabIcon.getEntityType(itemStackIn.getTag());
                    scale = getScaleFor(local, mobIcons);
                    if (itemStackIn.getTag().getFloat("DisplayMobScale") > 0) {
                        scale = itemStackIn.getTag().getFloat("DisplayMobScale");
                    }
                    if (this.renderedEntites.get(index) == null && !blockedRenderEntities.contains(local)) {
                        try {
                            Entity entity = local.create(level);
                            if (entity instanceof EntityBlobfish) {
                                ((EntityBlobfish) entity).setDepressurized(true);
                            }
                            this.renderedEntites.put(local.getDescriptionId(), entity);
                            fakeEntity = entity;
                        } catch (Exception e) {
                            blockedRenderEntities.add(local);
                            AlexsMobs.LOGGER.error("Could not render item for entity: " + local);
                        }
                    } else {
                        fakeEntity = this.renderedEntites.get(local.getDescriptionId());
                    }
                } else {
                    EntityType type = mobIcons.get(entityIndex).getFirst();
                    scale = mobIcons.get(entityIndex).getSecond();
                    if (type != null) {
                        if (this.renderedEntites.get(type.getDescriptionId()) == null && !blockedRenderEntities.contains(type)) {
                            try {
                                Entity entity = type.create(level);
                                if (entity instanceof EntityBlobfish) {
                                    ((EntityBlobfish) entity).setDepressurized(true);
                                }
                                this.renderedEntites.put(type.getDescriptionId(), entity);
                                fakeEntity = entity;
                            } catch (Exception e) {
                                blockedRenderEntities.add(type);
                                AlexsMobs.LOGGER.error("Could not render item for entity: " + type);
                            }
                        } else {
                            fakeEntity = this.renderedEntites.get(type.getDescriptionId());
                        }
                    }
                }
            }

            if (fakeEntity instanceof EntityCockroach) {
                if (flags == 99) {
                    matrixStackIn.translate(0, 0.25F, 0);
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-80));
                    ((EntityCockroach) fakeEntity).setMaracas(true);
                } else {
                    ((EntityCockroach) fakeEntity).setMaracas(false);
                }
            }
            if (fakeEntity instanceof EntityElephant) {
                if (flags == 99) {
                    ((EntityElephant) fakeEntity).setTusked(true);
                    ((EntityElephant) fakeEntity).setColor(null);
                } else if (flags == 98) {
                    ((EntityElephant) fakeEntity).setTusked(false);
                    ((EntityElephant) fakeEntity).setColor(DyeColor.BROWN);
                } else {
                    ((EntityElephant) fakeEntity).setTusked(false);
                    ((EntityElephant) fakeEntity).setColor(null);
                }
            }
            if (fakeEntity instanceof EntityBaldEagle) {
                if (flags == 98) {
                    ((EntityBaldEagle) fakeEntity).setCap(true);
                } else {
                    ((EntityBaldEagle) fakeEntity).setCap(false);
                }
            }
            if (fakeEntity instanceof EntityVoidWorm) {
                matrixStackIn.translate(0, 0.5F, 0);
            }
            if (fakeEntity instanceof EntityMimicOctopus) {
                matrixStackIn.translate(0, 0.5F, 0);
            }
            if (fakeEntity instanceof EntityLaviathan) {
                RenderLaviathan.renderWithoutShaking = true;
                matrixStackIn.translate(0, 0.3F, 0);
            }
            if (fakeEntity instanceof EntityCosmaw) {
                matrixStackIn.translate(0, 0.2F, 0);
            }
            if (fakeEntity instanceof EntityGiantSquid) {
                matrixStackIn.translate(0, 0.5F, 0.3F);
            }
            if (fakeEntity instanceof EntityUnderminer) {
                RenderUnderminer.renderWithPickaxe = true;
            }
            if (fakeEntity instanceof EntityMurmur) {
                RenderMurmurBody.renderWithHead = true;
                matrixStackIn.translate(0, -0.2F, 0);
            }
            if (fakeEntity != null) {
                MouseHandler mouseHelper = Minecraft.getInstance().mouseHandler;
                double mouseX = (mouseHelper.xpos() * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth()) / (double) Minecraft.getInstance().getWindow().getScreenWidth();
                double mouseY = mouseHelper.ypos() * (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double) Minecraft.getInstance().getWindow().getScreenHeight();
                matrixStackIn.translate(0.5F, 0F, 0);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(180F));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180F));
                if (transformType != ItemDisplayContext.GUI) {
                    mouseX = 0;
                    mouseY = 0;
                }
                try {
                    drawEntityOnScreen(matrixStackIn, 0, 0, scale, true, 0, -45, 0, (float) mouseX, (float) mouseY, fakeEntity);
                } catch (Exception e) {

                }
            }
            if (fakeEntity instanceof EntityLaviathan) {
                RenderLaviathan.renderWithoutShaking = false;
            }
            if (fakeEntity instanceof EntityUnderminer) {
                RenderUnderminer.renderWithPickaxe = false;
            }
            if (fakeEntity instanceof EntityMurmur) {
                RenderMurmurBody.renderWithHead = false;
            }
        }
    }

}
