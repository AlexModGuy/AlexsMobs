package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Map;

public class LayerKangarooArmor extends LayerRenderer<EntityKangaroo, ModelKangaroo> {

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private final BipedModel defaultBipedModel = new BipedModel(1.0F);
    private RenderKangaroo renderer;

    public LayerKangarooArmor(RenderKangaroo render) {
        super(render);
        this.renderer = render;
    }

    public static ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EquipmentSlotType slot, @javax.annotation.Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getArmorMaterial().getName();
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

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityKangaroo roo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.push();
        if(roo.isRoger()){
            ItemStack haloStack = new ItemStack(AMItemRegistry.HALO);
            matrixStackIn.push();
            translateToHead(matrixStackIn);
            float f = 0.1F * (float) Math.sin((roo.ticksExisted + partialTicks) * 0.1F) + (roo.isChild() ? 0.2F : 0F);
            matrixStackIn.translate(0.0F, -0.75F - f, -0.2F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90F));
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(roo, haloStack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        }
        if(!roo.isChild()) {
            {
                matrixStackIn.push();
                ItemStack itemstack = roo.getItemStackFromSlot(EquipmentSlotType.HEAD);
                if (itemstack.getItem() instanceof ArmorItem) {
                    ArmorItem armoritem = (ArmorItem) itemstack.getItem();
                    if (itemstack.canEquip(EquipmentSlotType.HEAD, roo)) {
                        BipedModel a = defaultBipedModel;
                        a = getArmorModelHook(roo, itemstack, EquipmentSlotType.HEAD, a);
                        boolean notAVanillaModel = a != defaultBipedModel;
                        this.setModelSlotVisible(a, EquipmentSlotType.HEAD);
                        translateToHead(matrixStackIn);
                        matrixStackIn.translate(0, 0.015F, -0.05F);
                        if(itemstack.getItem() == AMItemRegistry.FEDORA){
                            matrixStackIn.translate(0, 0.05F, 0F);

                        }
                        matrixStackIn.scale(0.7F, 0.7F, 0.7F);
                        boolean flag1 = itemstack.hasEffect();
                        int clampedLight = packedLightIn;
                        if (armoritem instanceof net.minecraft.item.IDyeableArmorItem) { // Allow this for anything, not only cloth
                            int i = ((net.minecraft.item.IDyeableArmorItem) armoritem).getColor(itemstack);
                            float f = (float) (i >> 16 & 255) / 255.0F;
                            float f1 = (float) (i >> 8 & 255) / 255.0F;
                            float f2 = (float) (i & 255) / 255.0F;
                            renderHelmet(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(roo, itemstack, EquipmentSlotType.HEAD, null), notAVanillaModel);
                            renderHelmet(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlotType.HEAD, "overlay"), notAVanillaModel);
                        } else {
                            renderHelmet(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlotType.HEAD, null), notAVanillaModel);
                        }

                    }
                }else{
                    translateToHead(matrixStackIn);
                    matrixStackIn.translate(0, -0.2, -0.1F);
                    matrixStackIn.rotate(new Quaternion(Vector3f.XP, 180, true));
                    matrixStackIn.rotate(new Quaternion(Vector3f.YP, 180, true));
                    matrixStackIn.scale(1.0F, 1.0F, 1.0F);
                    Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
                }
                matrixStackIn.pop();
            }

            {
                matrixStackIn.push();
                ItemStack itemstack = roo.getItemStackFromSlot(EquipmentSlotType.CHEST);
                if (itemstack.getItem() instanceof ArmorItem) {
                    ArmorItem armoritem = (ArmorItem) itemstack.getItem();
                    if (armoritem.getEquipmentSlot() == EquipmentSlotType.CHEST) {
                        BipedModel a = defaultBipedModel;
                        a = getArmorModelHook(roo, itemstack, EquipmentSlotType.CHEST, a);
                        boolean notAVanillaModel = a != defaultBipedModel;
                        this.setModelSlotVisible(a, EquipmentSlotType.CHEST);
                        translateToChest(matrixStackIn);
                        matrixStackIn.translate(0, 0.25F, 0F);
                        matrixStackIn.scale(1F, 1F, 1F);
                        boolean flag1 = itemstack.hasEffect();
                        int clampedLight = packedLightIn;
                        if (armoritem instanceof net.minecraft.item.IDyeableArmorItem) { // Allow this for anything, not only cloth
                            int i = ((net.minecraft.item.IDyeableArmorItem) armoritem).getColor(itemstack);
                            float f = (float) (i >> 16 & 255) / 255.0F;
                            float f1 = (float) (i >> 8 & 255) / 255.0F;
                            float f2 = (float) (i & 255) / 255.0F;
                            renderChestplate(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(roo, itemstack, EquipmentSlotType.CHEST, null), notAVanillaModel);
                            renderChestplate(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlotType.CHEST, "overlay"), notAVanillaModel);
                        } else {
                            renderChestplate(roo, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlotType.CHEST, null), notAVanillaModel);
                        }

                    }
                }
                matrixStackIn.pop();
            }
        }
        matrixStackIn.pop();

    }

    private void translateToHead(MatrixStack matrixStackIn) {
        translateToChest(matrixStackIn);
        this.renderer.getEntityModel().neck.translateRotate(matrixStackIn);
        this.renderer.getEntityModel().head.translateRotate(matrixStackIn);
    }

    private void translateToChest(MatrixStack matrixStackIn) {
        this.renderer.getEntityModel().root.translateRotate(matrixStackIn);
        this.renderer.getEntityModel().body.translateRotate(matrixStackIn);
        this.renderer.getEntityModel().chest.translateRotate(matrixStackIn);
    }


    private void renderChestplate(EntityKangaroo entity, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, BipedModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
        renderer.getEntityModel().copyModelAttributesTo(modelIn);
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * Minecraft.getInstance().getRenderPartialTicks();
        modelIn.bipedBody.rotateAngleX = 90 * 0.017453292F;
        modelIn.bipedBody.rotateAngleY = 0;
        modelIn.bipedBody.rotateAngleZ = 0;
        modelIn.bipedBody.rotationPointX = 0;
        modelIn.bipedBody.rotationPointY = 0.25F;
        modelIn.bipedBody.rotationPointZ = -7.6F;
        modelIn.bipedRightArm.copyModelAngles(renderer.getEntityModel().arm_right);
        modelIn.bipedLeftArm.copyModelAngles(renderer.getEntityModel().arm_left);
        modelIn.bipedLeftArm.rotationPointY = renderer.getEntityModel().arm_left.rotationPointY - 4 + (sitProgress * 0.25F);
        modelIn.bipedRightArm.rotationPointY = renderer.getEntityModel().arm_right.rotationPointY - 4 + (sitProgress * 0.25F);
        modelIn.bipedLeftArm.rotationPointZ = renderer.getEntityModel().arm_left.rotationPointZ - 0.5F;
        modelIn.bipedRightArm.rotationPointZ = renderer.getEntityModel().arm_right.rotationPointZ - 0.5F;
        modelIn.bipedBody.showModel = false;
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        modelIn.bipedBody.showModel = true;
        modelIn.bipedRightArm.showModel = false;
        modelIn.bipedLeftArm.showModel = false;
        matrixStackIn.push();
        matrixStackIn.scale(1.1F, 1.65F, 1.1F);
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        matrixStackIn.pop();
        modelIn.bipedRightArm.showModel = true;
        modelIn.bipedLeftArm.showModel = true;

    }

    private void renderHelmet(EntityKangaroo entity, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, BipedModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
        renderer.getEntityModel().copyModelAttributesTo(modelIn);
        modelIn.bipedHead.rotateAngleX = 0F;
        modelIn.bipedHead.rotateAngleY = 0F;
        modelIn.bipedHead.rotateAngleZ = 0F;
        modelIn.bipedHeadwear.rotateAngleX = 0F;
        modelIn.bipedHeadwear.rotateAngleY = 0F;
        modelIn.bipedHeadwear.rotateAngleZ = 0F;
        modelIn.bipedHead.rotationPointX = 0F;
        modelIn.bipedHead.rotationPointY = 0F;
        modelIn.bipedHead.rotationPointZ = 0F;
        modelIn.bipedHeadwear.rotationPointX = 0F;
        modelIn.bipedHeadwear.rotationPointY = 0F;
        modelIn.bipedHeadwear.rotationPointZ = 0F;
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);

    }


    protected void setModelSlotVisible(BipedModel p_188359_1_, EquipmentSlotType slotIn) {
        this.setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD:
                p_188359_1_.bipedHead.showModel = true;
                p_188359_1_.bipedHeadwear.showModel = true;
                break;
            case CHEST:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightArm.showModel = true;
                p_188359_1_.bipedLeftArm.showModel = true;
                break;
            case LEGS:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
                break;
            case FEET:
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
        }
    }

    protected void setModelVisible(BipedModel model) {
        model.setVisible(false);

    }


    protected BipedModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlotType slot, BipedModel model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }
}
