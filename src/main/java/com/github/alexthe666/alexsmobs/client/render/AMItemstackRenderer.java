package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMysteriousWorm;
import com.github.alexthe666.alexsmobs.client.model.ModelShieldOfTheDeep;
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
        list.add(new Pair<>(AMEntityRegistry.GRIZZLY_BEAR, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.ROADRUNNER, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.BONE_SERPENT, 0.55F));
        list.add(new Pair<>(AMEntityRegistry.GAZELLE, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.CROCODILE, 0.3F));
        list.add(new Pair<>(AMEntityRegistry.FLY, 1.3F));
        list.add(new Pair<>(AMEntityRegistry.HUMMINGBIRD, 1.5F));
        list.add(new Pair<>(AMEntityRegistry.ORCA, 0.2F));
        list.add(new Pair<>(AMEntityRegistry.SUNBIRD, 0.3F));
        list.add(new Pair<>(AMEntityRegistry.GORILLA, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.CRIMSON_MOSQUITO, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.RATTLESNAKE, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.ENDERGRADE, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.HAMMERHEAD_SHARK, 0.5F));
        list.add(new Pair<>(AMEntityRegistry.LOBSTER, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.KOMODO_DRAGON, 0.5F));
        list.add(new Pair<>(AMEntityRegistry.CAPUCHIN_MONKEY, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.CENTIPEDE_HEAD, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.WARPED_TOAD, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.MOOSE, 0.38F));
        list.add(new Pair<>(AMEntityRegistry.MIMICUBE, 0.95F));
        list.add(new Pair<>(AMEntityRegistry.RACCOON, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.BLOBFISH, 1F));
        list.add(new Pair<>(AMEntityRegistry.SEAL, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.COCKROACH, 1F));
        list.add(new Pair<>(AMEntityRegistry.SHOEBILL, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.ELEPHANT, 0.3F));
        list.add(new Pair<>(AMEntityRegistry.SOUL_VULTURE, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.SNOW_LEOPARD, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.SPECTRE, 0.3F));
        list.add(new Pair<>(AMEntityRegistry.CROW, 1.3F));
        list.add(new Pair<>(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.MUNGUS, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.MANTIS_SHRIMP, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.GUSTER, 0.55F));
        list.add(new Pair<>(AMEntityRegistry.WARPED_MOSCO, 0.35F));
        list.add(new Pair<>(AMEntityRegistry.STRADDLER, 0.38F));
        list.add(new Pair<>(AMEntityRegistry.STRADPOLE, 0.9F));
        list.add(new Pair<>(AMEntityRegistry.EMU, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.PLATYPUS, 1F));
        list.add(new Pair<>(AMEntityRegistry.DROPBEAR, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.TASMANIAN_DEVIL, 1.2F));
        list.add(new Pair<>(AMEntityRegistry.KANGAROO, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.CACHALOT_WHALE, 0.1F));
        list.add(new Pair<>(AMEntityRegistry.LEAFCUTTER_ANT, 1.2F));
        list.add(new Pair<>(AMEntityRegistry.ENDERIOPHAGE, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.BALD_EAGLE, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.TIGER, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.TARANTULA_HAWK, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.VOID_WORM, 0.3F));
        list.add(new Pair<>(AMEntityRegistry.FRILLED_SHARK, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.MIMIC_OCTOPUS, 0.7F));
        list.add(new Pair<>(AMEntityRegistry.SEAGULL, 1.2F));
        list.add(new Pair<>(AMEntityRegistry.FROSTSTALKER, 0.8F));
        list.add(new Pair<>(AMEntityRegistry.TUSKLIN, 0.6F));
        list.add(new Pair<>(AMEntityRegistry.LAVIATHAN, 0.2F));
        list.add(new Pair<>(AMEntityRegistry.COSMAW, 0.32F));
        list.add(new Pair<>(AMEntityRegistry.TOUCAN, 1.3F));
        list.add(new Pair<>(AMEntityRegistry.MANED_WOLF, 0.85F));
        list.add(new Pair<>(AMEntityRegistry.ANACONDA, 1.0F));
        list.add(new Pair<>(AMEntityRegistry.ANTEATER, 0.5F));
        list.add(new Pair<>(AMEntityRegistry.ROCKY_ROLLER, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.FLUTTER, 1.15F));
        list.add(new Pair<>(AMEntityRegistry.GELADA_MONKEY, 0.65F));
        list.add(new Pair<>(AMEntityRegistry.JERBOA, 1.3F));
        list.add(new Pair<>(AMEntityRegistry.TERRAPIN, 1.1F));
        list.add(new Pair<>(AMEntityRegistry.COMB_JELLY, 1.0F));
        list.add(new Pair<>(AMEntityRegistry.COSMIC_COD, 1.3F));
        list.add(new Pair<>(AMEntityRegistry.BUNFUNGUS, 0.5F));
        list.add(new Pair<>(AMEntityRegistry.BISON, 0.45F));
        list.add(new Pair<>(AMEntityRegistry.GIANT_SQUID, 0.3F));
    });
    public static int ticksExisted = 0;
    private static final ModelShieldOfTheDeep SHIELD_OF_THE_DEEP_MODEL = new ModelShieldOfTheDeep();
    private static final ResourceLocation SHIELD_OF_THE_DEEP_TEXTURE = new ResourceLocation("alexsmobs:textures/armor/shield_of_the_deep.png");
    private static final ModelMysteriousWorm MYTERIOUS_WORM_MODEL = new ModelMysteriousWorm();
    private static final ResourceLocation MYTERIOUS_WORM_TEXTURE = new ResourceLocation("alexsmobs:textures/item/mysterious_worm_model.png");
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
        if(itemStackIn.getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP){
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.4F, -0.75F, 0.5F);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-180));
            SHIELD_OF_THE_DEEP_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(SHIELD_OF_THE_DEEP_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMItemRegistry.MYSTERIOUS_WORM){
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, -2F, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-180));
            MYTERIOUS_WORM_MODEL.animateStack(itemStackIn);
            MYTERIOUS_WORM_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(MYTERIOUS_WORM_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.getItem() == AMItemRegistry.FALCONRY_GLOVE){
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if(p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND){
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.FALCONRY_GLOVE_HAND), p_239207_2_, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }else{
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.FALCONRY_GLOVE_INVENTORY), p_239207_2_, p_239207_2_ == ItemTransforms.TransformType.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }
        }
        if(itemStackIn.getItem() == AMItemRegistry.VINE_LASSO){
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if(p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || p_239207_2_ == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND){
                if(ItemVineLasso.isItemInUse(itemStackIn)){
                    if(p_239207_2_.firstPerson()){
                        matrixStackIn.translate(p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.0f, -0.5f);
                    }
                    matrixStackIn.mulPose(Vector3f.YP.rotation(Minecraft.getInstance().player.tickCount + Minecraft.getInstance().getFrameTime()));
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.VINE_LASSO_HAND), p_239207_2_, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }else{
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(AMItemRegistry.VINE_LASSO_INVENTORY), p_239207_2_, p_239207_2_ == ItemTransforms.TransformType.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            }
        }
        if (itemStackIn.getItem() == AMItemRegistry.TAB_ICON) {
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
