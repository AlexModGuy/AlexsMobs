package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.render.RenderTiger;
import com.github.alexthe666.alexsmobs.client.render.RenderTiger;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class LayerTigerEyes  extends LayerRenderer<EntityTiger, ModelTiger> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_eyes.png");
    private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_white_eyes.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_angry_eyes.png");

    public LayerTigerEyes(RenderTiger render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityTiger tiger, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!tiger.isSleeping()){
            long roundedTime = tiger.world.getDayTime() % 24000;
            boolean night = roundedTime >= 13000 && roundedTime <= 22000;
            BlockPos ratPos = tiger.getLightPosition();
            int i = tiger.world.getLightFor(LightType.SKY, ratPos);
            int j = tiger.world.getLightFor(LightType.BLOCK, ratPos);
            int brightness;
            if (night) {
                brightness = j;
            } else {
                brightness = Math.max(i, j);
            }
            if (brightness < 7 || tiger.getAngerTime() > 0) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(tiger.getAngerTime() > 0 ? TEXTURE_ANGRY : tiger.isWhite() ? TEXTURE_WHITE : TEXTURE));
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(tiger, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
