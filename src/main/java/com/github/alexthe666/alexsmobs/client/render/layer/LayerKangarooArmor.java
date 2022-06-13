package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

public class LayerKangarooArmor extends RenderLayer<EntityKangaroo, ModelKangaroo> {

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private final HumanoidModel defaultBipedModel;
    private RenderKangaroo renderer;

    public LayerKangarooArmor(RenderKangaroo render, EntityRendererProvider.Context context) {
        super(render);
        defaultBipedModel = new HumanoidModel(context.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));
        this.renderer = render;
    }

    public static ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @javax.annotation.Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (1), type == null ? "" : String.format("_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityKangaroo roo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.pushPose();
        if(roo.isRoger()){
            ItemStack haloStack = new ItemStack(AMItemRegistry.HALO.get());
            matrixStackIn.pushPose();
            translateToHead(matrixStackIn);
            float f = 0.1F * (float) Math.sin((roo.tickCount + partialTicks) * 0.1F) + (roo.isBaby() ? 0.2F : 0F);
            matrixStackIn.translate(0.0F, -0.75F - f, -0.2F);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90F));
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(roo, haloStack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }
        if(!roo.isBaby()) {
            {
                matrixStackIn.pushPose();
                ItemStack itemstack = roo.getItemBySlot(EquipmentSlot.HEAD);
                if (itemstack.getItem() instanceof ArmorItem) {
                    ArmorItem armoritem = (ArmorItem) itemstack.getItem();
                    if (itemstack.canEquip(EquipmentSlot.HEAD, roo)) {
                        HumanoidModel a = defaultBipedModel;
                        a = getArmorModelHook(roo, itemstack, EquipmentSlot.HEAD, a);
                        boolean notAVanillaModel = a != defaultBipedModel;
                        this.setModelSlotVisible(a, EquipmentSlot.HEAD);
                        translateToHead(matrixStackIn);
                        matrixStackIn.translate(0, 0.015F, -0.05F);
                        if(itemstack.getItem() == AMItemRegistry.FEDORA.get()){
                            matrixStackIn.translate(0, 0.05F, 0F);

                        }
                        matrixStackIn.scale(0.7F, 0.7F, 0.7F);
                        boolean flag1 = itemstack.hasFoil();
                        int clampedLight = packedLightIn;
                        if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                            int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(itemstack);
                            float f = (float) (i >> 16 & 255) / 255.0F;
                            float f1 = (float) (i >> 8 & 255) / 255.0F;
                            float f2 = (float) (i & 255) / 255.0F;
                            renderHelmet(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                            renderHelmet(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, "overlay"), notAVanillaModel);
                        } else {
                            renderHelmet(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                        }
                    }
                }else{
                    translateToHead(matrixStackIn);
                    matrixStackIn.translate(0, -0.2, -0.1F);
                    matrixStackIn.mulPose(new Quaternion(Vector3f.XP, 180, true));
                    matrixStackIn.mulPose(new Quaternion(Vector3f.YP, 180, true));
                    matrixStackIn.scale(1.0F, 1.0F, 1.0F);
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);
                }
                matrixStackIn.popPose();
            }
            {
                matrixStackIn.pushPose();
                ItemStack itemstack = roo.getItemBySlot(EquipmentSlot.CHEST);
                if (itemstack.getItem() instanceof ArmorItem) {
                    ArmorItem armoritem = (ArmorItem) itemstack.getItem();
                    if (armoritem.getSlot() == EquipmentSlot.CHEST) {
                        HumanoidModel a = defaultBipedModel;
                        a = getArmorModelHook(roo, itemstack, EquipmentSlot.CHEST, a);
                        boolean notAVanillaModel = a != defaultBipedModel;
                        this.setModelSlotVisible(a, EquipmentSlot.CHEST);
                        translateToChest(matrixStackIn);
                        matrixStackIn.translate(0, 0.25F, 0F);
                        matrixStackIn.scale(1F, 1F, 1F);
                        boolean flag1 = itemstack.hasFoil();
                        int clampedLight = packedLightIn;
                        if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) { // Allow this for anything, not only cloth
                            int i = ((net.minecraft.world.item.DyeableLeatherItem) armoritem).getColor(itemstack);
                            float f = (float) (i >> 16 & 255) / 255.0F;
                            float f1 = (float) (i >> 8 & 255) / 255.0F;
                            float f2 = (float) (i & 255) / 255.0F;
                            renderChestplate(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(roo, itemstack, EquipmentSlot.CHEST, null), notAVanillaModel);
                            renderChestplate(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.CHEST, "overlay"), notAVanillaModel);
                        } else {
                            renderChestplate(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.CHEST, null), notAVanillaModel);
                        }

                    }
                }
                matrixStackIn.popPose();
            }
        }
        matrixStackIn.popPose();

    }

    private void translateToHead(PoseStack matrixStackIn) {
        translateToChest(matrixStackIn);
        this.renderer.getModel().neck.translateAndRotate(matrixStackIn);
        this.renderer.getModel().head.translateAndRotate(matrixStackIn);
    }

    private void translateToChest(PoseStack matrixStackIn) {
        this.renderer.getModel().root.translateAndRotate(matrixStackIn);
        this.renderer.getModel().body.translateAndRotate(matrixStackIn);
        this.renderer.getModel().chest.translateAndRotate(matrixStackIn);
    }


    private void renderChestplate(EntityKangaroo entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * Minecraft.getInstance().getFrameTime();
        modelIn.body.xRot = 90 * 0.017453292F;
        modelIn.body.yRot = 0;
        modelIn.body.zRot = 0;
        modelIn.body.x = 0;
        modelIn.body.y = 0.25F;
        modelIn.body.z = -7.6F;
        modelIn.rightArm.x = renderer.getModel().arm_right.rotationPointX;
        modelIn.rightArm.y = renderer.getModel().arm_right.rotationPointY;
        modelIn.rightArm.z = renderer.getModel().arm_right.rotationPointZ;
        modelIn.rightArm.xRot = renderer.getModel().arm_right.rotateAngleX;
        modelIn.rightArm.yRot = renderer.getModel().arm_right.rotateAngleY;
        modelIn.rightArm.zRot = renderer.getModel().arm_right.rotateAngleZ;
        modelIn.leftArm.x = renderer.getModel().arm_left.rotationPointX;
        modelIn.leftArm.y = renderer.getModel().arm_left.rotationPointY;
        modelIn.leftArm.z = renderer.getModel().arm_left.rotationPointZ;
        modelIn.leftArm.xRot = renderer.getModel().arm_left.rotateAngleX;
        modelIn.leftArm.yRot = renderer.getModel().arm_left.rotateAngleY;
        modelIn.leftArm.zRot = renderer.getModel().arm_left.rotateAngleZ;
        modelIn.leftArm.y = renderer.getModel().arm_left.rotationPointY - 4 + (sitProgress * 0.25F);
        modelIn.rightArm.y = renderer.getModel().arm_right.rotationPointY - 4 + (sitProgress * 0.25F);
        modelIn.leftArm.z = renderer.getModel().arm_left.rotationPointZ - 0.5F;
        modelIn.rightArm.z = renderer.getModel().arm_right.rotationPointZ - 0.5F;
        modelIn.body.visible = false;
        modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        modelIn.body.visible = true;
        modelIn.rightArm.visible = false;
        modelIn.leftArm.visible = false;
        matrixStackIn.pushPose();
        matrixStackIn.scale(1.1F, 1.65F, 1.1F);
        modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        matrixStackIn.popPose();
        modelIn.rightArm.visible = true;
        modelIn.leftArm.visible = true;

    }

    private void renderHelmet(EntityKangaroo entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
        renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.head.xRot = 0F;
        modelIn.head.yRot = 0F;
        modelIn.head.zRot = 0F;
        modelIn.hat.xRot = 0F;
        modelIn.hat.yRot = 0F;
        modelIn.hat.zRot = 0F;
        modelIn.head.x = 0F;
        modelIn.head.y = 0F;
        modelIn.head.z = 0F;
        modelIn.hat.x = 0F;
        modelIn.hat.y = 0F;
        modelIn.hat.z = 0F;
        modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);

    }


    protected void setModelSlotVisible(HumanoidModel p_188359_1_, EquipmentSlot slotIn) {
        this.setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD:
                p_188359_1_.head.visible = true;
                p_188359_1_.hat.visible = true;
                break;
            case CHEST:
                p_188359_1_.body.visible = true;
                p_188359_1_.rightArm.visible = true;
                p_188359_1_.leftArm.visible = true;
                break;
            case LEGS:
                p_188359_1_.body.visible = true;
                p_188359_1_.rightLeg.visible = true;
                p_188359_1_.leftLeg.visible = true;
                break;
            case FEET:
                p_188359_1_.rightLeg.visible = true;
                p_188359_1_.leftLeg.visible = true;
        }
    }

    protected void setModelVisible(HumanoidModel model) {
        model.setAllVisible(false);

    }


    protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
         Model basicModel = net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
         return basicModel instanceof HumanoidModel ? (HumanoidModel<?>) basicModel : model;
    }
}
