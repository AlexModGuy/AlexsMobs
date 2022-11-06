package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelSoulVulture;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderSoulVulture;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class LayerSoulVultureGlow extends RenderLayer<EntitySoulVulture, ModelSoulVulture> {
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/soul_vulture/soul_vulture_glow.png");
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/soul_vulture/soul_vulture_flames_2.png");

    public LayerSoulVultureGlow(RenderSoulVulture renderSoulVulture) {
        super(renderSoulVulture);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySoulVulture entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(AMRenderTypes.getGhost(TEXTURE_GLOW)), 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1F);
        if(entitylivingbaseIn.hasSoulHeart()){
            this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(AMRenderTypes.getGhost(getFlames(entitylivingbaseIn.tickCount))), 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1F);
        }
    }

    private ResourceLocation getFlames(int tickCount) {
        int i = tickCount / 3 % 3;
        switch (i){
            case 2:
                return TEXTURE_2;
            case 1:
                return TEXTURE_1;
            default:
                return TEXTURE_0;
        }
    }
}
