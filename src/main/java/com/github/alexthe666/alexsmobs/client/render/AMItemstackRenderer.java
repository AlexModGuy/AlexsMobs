package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.model.*;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemTabIcon;
import com.github.alexthe666.alexsmobs.item.ItemVineLasso;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AMItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    private static List<Pair<EntityType, Float>> MOB_ICONS = Util.make(Lists.newArrayList(), (list) -> {
        list.add(new Pair<>(AMEntityRegistry.GRIZZLY_BEAR.get(),  0.6F));
        list.add(new Pair<>(AMEntityRegistry.ROADRUNNER.get(),  0.8F));
        list.add(new Pair<>(AMEntityRegistry.BONE_SERPENT.get(),  0.55F));
        list.add(new Pair<>(AMEntityRegistry.GAZELLE.get(),  0.6F));
        list.add(new Pair<>(AMEntityRegistry.CROCODILE.get(),  0.3F));
        list.add(new Pair<>(AMEntityRegistry.FLY.get(),  1.3F));
        list.add(new Pair<>(AMEntityRegistry.HUMMINGBIRD.get(),  1.5F));
        list.add(new Pair<>(AMEntityRegistry.ORCA.get(),  0.2F));
        list.add(new Pair<>(AMEntityRegistry.SUNBIRD.get(),  0.3F));
        list.add(new Pair<>(AMEntityRegistry.GORILLA.get(),  0.85F));
        list.add(new Pair<>(AMEntityRegistry.CRIMSON_MOSQUITO.get(),  0.6F));
        list.add(new Pair<>(AMEntityRegistry.RATTLESNAKE.get(),  0.6F));
        list.add(new Pair<>(AMEntityRegistry.ENDERGRADE.get(),  0.8F));
        list.add(new Pair<>(AMEntityRegistry.HAMMERHEAD_SHARK.get(),  0.5F));
        list.add(new Pair<>(AMEntityRegistry.LOBSTER.get(),  0.85F));
        list.add(new Pair<>(AMEntityRegistry.KOMODO_DRAGON.get(),  0.5F));
        list.add(new Pair<>(AMEntityRegistry.CAPUCHIN_MONKEY.get(),  0.85F));
        list.add(new Pair<>(AMEntityRegistry.CENTIPEDE_HEAD.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.WARPED_TOAD.get(),  0.6F));
        list.add(new Pair<>(AMEntityRegistry.MOOSE.get(),  0.38F));
        list.add(new Pair<>(AMEntityRegistry.MIMICUBE.get(),  0.95F));
        list.add(new Pair<>(AMEntityRegistry.RACCOON.get(),  0.8F));
        list.add(new Pair<>(AMEntityRegistry.BLOBFISH.get(),  1F));
        list.add(new Pair<>(AMEntityRegistry.SEAL.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.COCKROACH.get(),  1F));
        list.add(new Pair<>(AMEntityRegistry.SHOEBILL.get(),  0.8F));
        list.add(new Pair<>(AMEntityRegistry.ELEPHANT.get(),  0.3F));
        list.add(new Pair<>(AMEntityRegistry.SOUL_VULTURE.get(),  0.8F));
        list.add(new Pair<>(AMEntityRegistry.SNOW_LEOPARD.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.SPECTRE.get(),  0.3F));
        list.add(new Pair<>(AMEntityRegistry.CROW.get(),  1.3F));
        list.add(new Pair<>(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.MUNGUS.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.MANTIS_SHRIMP.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.GUSTER.get(),  0.55F));
        list.add(new Pair<>(AMEntityRegistry.WARPED_MOSCO.get(),  0.35F));
        list.add(new Pair<>(AMEntityRegistry.STRADDLER.get(),  0.38F));
        list.add(new Pair<>(AMEntityRegistry.STRADPOLE.get(),  0.9F));
        list.add(new Pair<>(AMEntityRegistry.EMU.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.PLATYPUS.get(),  1F));
        list.add(new Pair<>(AMEntityRegistry.DROPBEAR.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.TASMANIAN_DEVIL.get(),  1.2F));
        list.add(new Pair<>(AMEntityRegistry.KANGAROO.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.CACHALOT_WHALE.get(),  0.1F));
        list.add(new Pair<>(AMEntityRegistry.LEAFCUTTER_ANT.get(),  1.2F));
        list.add(new Pair<>(AMEntityRegistry.ENDERIOPHAGE.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.BALD_EAGLE.get(),  0.85F));
        list.add(new Pair<>(AMEntityRegistry.TIGER.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.TARANTULA_HAWK.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.VOID_WORM.get(),  0.3F));
        list.add(new Pair<>(AMEntityRegistry.FRILLED_SHARK.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.MIMIC_OCTOPUS.get(),  0.7F));
        list.add(new Pair<>(AMEntityRegistry.SEAGULL.get(),  1.2F));
        list.add(new Pair<>(AMEntityRegistry.FROSTSTALKER.get(),  0.8F));
        list.add(new Pair<>(AMEntityRegistry.TUSKLIN.get(),  0.6F));
        list.add(new Pair<>(AMEntityRegistry.LAVIATHAN.get(),  0.2F));
        list.add(new Pair<>(AMEntityRegistry.COSMAW.get(),  0.32F));
        list.add(new Pair<>(AMEntityRegistry.TOUCAN.get(),  1.3F));
        list.add(new Pair<>(AMEntityRegistry.MANED_WOLF.get(),  0.85F));
        list.add(new Pair<>(AMEntityRegistry.ANACONDA.get(),  1.0F));
        list.add(new Pair<>(AMEntityRegistry.ANTEATER.get(),  0.5F));
        list.add(new Pair<>(AMEntityRegistry.ROCKY_ROLLER.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.FLUTTER.get(),  1.15F));
        list.add(new Pair<>(AMEntityRegistry.GELADA_MONKEY.get(),  0.65F));
        list.add(new Pair<>(AMEntityRegistry.JERBOA.get(),  1.3F));
        list.add(new Pair<>(AMEntityRegistry.TERRAPIN.get(),  1.1F));
        list.add(new Pair<>(AMEntityRegistry.COMB_JELLY.get(),  1.0F));
        list.add(new Pair<>(AMEntityRegistry.COSMIC_COD.get(),  1.3F));
        list.add(new Pair<>(AMEntityRegistry.BUNFUNGUS.get(),  0.5F));
        list.add(new Pair<>(AMEntityRegistry.BISON.get(),  0.45F));
        list.add(new Pair<>(AMEntityRegistry.GIANT_SQUID.get(),  0.3F));
    });
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
    private static final ModelEndPirateFlag FLAG_MODEL = new ModelEndPirateFlag();
    private static final ResourceLocation FLAG_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/flag.png");
    private Map<String, Entity> renderedEntites = new HashMap();

    public AMItemstackRenderer() {
        super(null, null);
    }

    public static void incrementTick() {
        ticksExisted++;
    }

    private static float getScaleFor(EntityType type) {
        for (Pair<EntityType, Float> pair : MOB_ICONS) {
            if (pair.getFirst() == type) {
                return pair.getSecond();
            }
        }
        return 1.0F;
    }

    public static void drawEntityOnScreen(PoseStack matrixstack, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity) {
        float f = (float) Math.atan(-mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        matrixstack.scale(scale, scale, scale);
        entity.setOnGround(false);
        float partialTicks = Minecraft.getInstance().getFrameTime();
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(20.0F);
        float partialTicksForRender = Minecraft.getInstance().isPaused() || entity instanceof EntityMimicOctopus ? 0 : partialTicks;
        int tick = Minecraft.getInstance().player.tickCount;
        if(Minecraft.getInstance().isPaused()){
            tick = ticksExisted;
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

            quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
            quaternion.mul(quaternion1);
        }

        matrixstack.mulPose(quaternion);
        matrixstack.mulPose(Vector3f.XP.rotationDegrees((float) (-xRot)));
        matrixstack.mulPose(Vector3f.YP.rotationDegrees((float) yRot));
        matrixstack.mulPose(Vector3f.ZP.rotationDegrees((float) zRot));
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
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
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if(itemStackIn.getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()){
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.4F, -0.75F, 0.5F);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-180));
            SHIELD_OF_THE_DEEP_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(SHIELD_OF_THE_DEEP_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMItemRegistry.MYSTERIOUS_WORM.get()){
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, -2F, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-180));
            MYTERIOUS_WORM_MODEL.animateStack(itemStackIn);
            MYTERIOUS_WORM_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(MYTERIOUS_WORM_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMItemRegistry.FALCONRY_GLOVE.get()){
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if(p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND){
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.FALCONRY_GLOVE_HAND.get()), p_239207_2_, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }else{
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.FALCONRY_GLOVE_INVENTORY.get()), p_239207_2_, p_239207_2_ == ItemTransforms.TransformType.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }
        }
        if(itemStackIn.getItem() == AMItemRegistry.VINE_LASSO.get()){
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if(p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND){
                if(ItemVineLasso.isItemInUse(itemStackIn)){
                    if(p_239207_2_.firstPerson()){
                        matrixStackIn.translate(p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.0f, -0.5f);
                    }
                    matrixStackIn.mulPose(Vector3f.YP.rotation(Minecraft.getInstance().player.tickCount + Minecraft.getInstance().getFrameTime()));
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.VINE_LASSO_HAND.get()), p_239207_2_, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }else{
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.VINE_LASSO_INVENTORY.get()), p_239207_2_, p_239207_2_ == ItemTransforms.TransformType.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }
        }
        if(itemStackIn.getItem() == AMBlockRegistry.END_PIRATE_ANCHOR.get().asItem()){
            matrixStackIn.pushPose();
            matrixStackIn.translate(1F, 0F, 0);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-180));
            matrixStackIn.scale(0.75F, 0.75F, 0.75F);
            ANCHOR_MODEL.animateStack(itemStackIn);
            ANCHOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(ANCHOR_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMBlockRegistry.END_PIRATE_ANCHOR_WINCH.get().asItem()){
            matrixStackIn.pushPose();
            matrixStackIn.translate(1, -1F, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-180));
            WINCH_MODEL.animateStack(itemStackIn);
            WINCH_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(WINCH_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMBlockRegistry.END_PIRATE_SHIP_WHEEL.get().asItem()){
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90));
            matrixStackIn.scale(0.8F, 0.8F, 0.8F);
            SHIP_WHEEL_MODEL.resetToDefaultPose();
            SHIP_WHEEL_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(SHIP_WHEEL_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if (itemStackIn.getItem() == AMItemRegistry.TAB_ICON.get()) {
            Entity fakeEntity = null;
            int entityIndex = (Minecraft.getInstance().player.tickCount / 40) % (MOB_ICONS.size());
            float scale = 1.0F;
            int flags = 0;
            if (ItemTabIcon.hasCustomEntityDisplay(itemStackIn)) {
                flags = itemStackIn.getTag().getInt("DisplayMobFlags");
                String index = ItemTabIcon.getCustomDisplayEntityString(itemStackIn);
                EntityType local = ItemTabIcon.getEntityType(itemStackIn.getTag());
                scale = getScaleFor(local);
                if(itemStackIn.getTag().getFloat("DisplayMobScale") > 0){
                    scale = itemStackIn.getTag().getFloat("DisplayMobScale");
                }
                if (this.renderedEntites.get(index) == null) {
                    Entity entity = local.create(Minecraft.getInstance().level);
                    if (entity instanceof EntityBlobfish) {
                        ((EntityBlobfish) entity).setDepressurized(true);
                    }
                    this.renderedEntites.put(local.getDescriptionId(), entity);
                    fakeEntity = entity;
                } else {
                    fakeEntity = this.renderedEntites.get(local.getDescriptionId());
                }
            } else {
                EntityType type = MOB_ICONS.get(entityIndex).getFirst();
                scale = MOB_ICONS.get(entityIndex).getSecond();
                if (type != null) {
                    if (this.renderedEntites.get(type.getDescriptionId()) == null) {
                        Entity entity = type.create(Minecraft.getInstance().level);
                        if (entity instanceof EntityBlobfish) {
                            ((EntityBlobfish) entity).setDepressurized(true);
                        }
                        this.renderedEntites.put(type.getDescriptionId(), entity);
                        fakeEntity = entity;
                    } else {
                        fakeEntity = this.renderedEntites.get(type.getDescriptionId());
                    }
                }
            }
            if (fakeEntity instanceof EntityCockroach) {
                if (flags == 99) {
                    matrixStackIn.translate(0, 0.25F, 0);
                    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-80));
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
            if(fakeEntity instanceof EntityVoidWorm){
                matrixStackIn.translate(0, 0.5F, 0);
            }
            if(fakeEntity instanceof EntityMimicOctopus){
                matrixStackIn.translate(0, 0.5F, 0);
            }
            if(fakeEntity instanceof EntityLaviathan){
                matrixStackIn.translate(0, 0.3F, 0);
            }
            if(fakeEntity instanceof EntityCosmaw){
                matrixStackIn.translate(0, 0.2F, 0);
            }
            if(fakeEntity instanceof EntityGiantSquid){
                matrixStackIn.translate(0, 0.5F, 0.3F);
            }
            if (fakeEntity != null) {
                MouseHandler mouseHelper = Minecraft.getInstance().mouseHandler;
                double mouseX = (mouseHelper.xpos() * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth()) / (double) Minecraft.getInstance().getWindow().getScreenWidth();
                double mouseY = mouseHelper.ypos() * (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double) Minecraft.getInstance().getWindow().getScreenHeight();
                matrixStackIn.translate(0.5F, 0F, 0);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180F));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180F));
                if (p_239207_2_ != ItemTransforms.TransformType.GUI) {
                    mouseX = 0;
                    mouseY = 0;
                }
                drawEntityOnScreen(matrixStackIn, 0, 0, scale, true, 0, -45, 0, (float) mouseX, (float) mouseY, fakeEntity);
            }
        }
    }

}
