package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Map;

public class LayerMimicubeHelmet extends LayerRenderer<EntityMimicube, ModelMimicube> {

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private final BipedModel defaultBipedModel = new BipedModel(1.0F);
    private RenderMimicube renderer;

    public LayerMimicubeHelmet(RenderMimicube render) {
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

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityMimicube cube, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.push();
        ItemStack itemstack = cube.getItemStackFromSlot(EquipmentSlotType.HEAD);
        float helmetSwap = MathHelper.lerp(partialTicks, cube.prevHelmetSwapProgress, cube.helmetSwapProgress) * 0.2F;
        if (itemstack.getItem() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem) itemstack.getItem();
            if (armoritem.getEquipmentSlot() == EquipmentSlotType.HEAD) {
                BipedModel a = defaultBipedModel;
                a = getArmorModelHook(cube, itemstack, EquipmentSlotType.HEAD, a);
                boolean notAVanillaModel = a != defaultBipedModel;

                this.setModelSlotVisible(a, EquipmentSlotType.HEAD);
                boolean flag = false;
                this.renderer.getEntityModel().root.translateRotate(matrixStackIn);
                this.renderer.getEntityModel().innerbody.translateRotate(matrixStackIn);
                matrixStackIn.translate(0,  notAVanillaModel ? 0.25F : -0.75F, 0F);
                matrixStackIn.scale(1F + 0.3F * (1 - helmetSwap), 1F + 0.3F * (1 - helmetSwap), 1F + 0.3F * (1 - helmetSwap));
                boolean flag1 = itemstack.hasEffect();
                int clampedLight = helmetSwap > 0 ? (int) (-100 * helmetSwap) : packedLightIn;
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360 * helmetSwap));
                if (armoritem instanceof net.minecraft.item.IDyeableArmorItem) { // Allow this for anything, not only cloth
                    int i = ((net.minecraft.item.IDyeableArmorItem) armoritem).getColor(itemstack);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    renderArmor(cube, matrixStackIn, bufferIn, clampedLight, flag1, a, f, f1, f2, getArmorResource(cube, itemstack, EquipmentSlotType.HEAD, null), notAVanillaModel);
                    renderArmor(cube, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(cube, itemstack, EquipmentSlotType.HEAD, "overlay"), notAVanillaModel);
                } else {
                    renderArmor(cube, matrixStackIn, bufferIn, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(cube, itemstack, EquipmentSlotType.HEAD, null), notAVanillaModel);
                }

            }
        }
        matrixStackIn.pop();
    }

    private void renderArmor(EntityMimicube entity, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, BipedModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
        if(notAVanillaModel){
            renderer.getEntityModel().copyModelAttributesTo(modelIn);
            modelIn.bipedBody.rotationPointY = 0;
            modelIn.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            modelIn.bipedHeadwear.rotationPointY = 0;
            modelIn.bipedHead.copyModelAngles(renderer.getEntityModel().body);
            modelIn.bipedHeadwear.copyModelAngles(renderer.getEntityModel().body);
            modelIn.bipedBody.copyModelAngles(renderer.getEntityModel().body);
        }
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
