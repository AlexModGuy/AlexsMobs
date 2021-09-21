package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.render.RenderTiger;
import com.github.alexthe666.alexsmobs.client.render.RenderTiger;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;

public class LayerTigerEyes  extends RenderLayer<EntityTiger, ModelTiger> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_eyes.png");
    private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_white_eyes.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_angry_eyes.png");

    public LayerTigerEyes(RenderTiger render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityTiger tiger, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!tiger.isSleeping()){
            long roundedTime = tiger.level.getDayTime() % 24000;
            boolean night = roundedTime >= 13000 && roundedTime <= 22000;
            BlockPos ratPos = tiger.getLightPosition();
            int i = tiger.level.getBrightness(LightLayer.SKY, ratPos);
            int j = tiger.level.getBrightness(LightLayer.BLOCK, ratPos);
            int brightness;
            if (night) {
                brightness = j;
            } else {
                brightness = Math.max(i, j);
            }
            if (brightness < 7 || tiger.getRemainingPersistentAngerTime() > 0) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(tiger.getRemainingPersistentAngerTime() > 0 ? TEXTURE_ANGRY : tiger.isWhite() ? TEXTURE_WHITE : TEXTURE));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(tiger, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
