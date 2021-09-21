package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.RenderCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LayerCrimsonMosquitoBlood extends RenderLayer<EntityCrimsonMosquito, ModelCrimsonMosquito> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_blood.png");
    private static final ResourceLocation TEXTURE_SICK = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_blood_blue.png");

    public LayerCrimsonMosquitoBlood(RenderCrimsonMosquito renderCrimsonMosquito) {
        super(renderCrimsonMosquito);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCrimsonMosquito entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entitylivingbaseIn.getBloodLevel() > 0){
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(entitylivingbaseIn.isSick() ? TEXTURE_SICK : TEXTURE));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
