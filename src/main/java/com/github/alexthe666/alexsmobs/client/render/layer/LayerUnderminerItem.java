package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelUnderminerDwarf;
import com.github.alexthe666.alexsmobs.client.render.RenderUnderminer;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerUnderminerItem extends RenderLayer<EntityUnderminer, EntityModel<EntityUnderminer>> {

    public LayerUnderminerItem(RenderUnderminer render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityUnderminer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!entitylivingbaseIn.isFullyHidden()){
            ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
            if(RenderUnderminer.renderWithPickaxe){
                itemstack = new ItemStack(AMItemRegistry.GHOSTLY_PICKAXE.get());
            }
            matrixStackIn.pushPose();
            matrixStackIn.pushPose();
            float f = entitylivingbaseIn.getMainArm() == HumanoidArm.LEFT ? 0.1F : -0.1F;
            float f1 = entitylivingbaseIn.isDwarf() ? 0.5F : 0.45F;
            if(entitylivingbaseIn.isDwarf()){
                matrixStackIn.translate(0F,  1F, 0F);
                f *= 0.3F;
            }else{
                matrixStackIn.translate(0F,  0.2F, 0);
            }
            translateToHand(entitylivingbaseIn.getMainArm(), matrixStackIn);
            matrixStackIn.translate(f,  f1,  -0.15F);

            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    protected void translateToHand(HumanoidArm arm, PoseStack matrixStack) {
        if(getParentModel() instanceof ModelUnderminerDwarf){
            ((ModelUnderminerDwarf)getParentModel()).translateToHand(arm, matrixStack);
        }else if(getParentModel() instanceof ArmedModel){
            ((ArmedModel)getParentModel()).translateToHand(arm, matrixStack);
        }
    }
}
