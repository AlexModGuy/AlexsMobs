package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelSoulVulture;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderSoulVulture;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class LayerSoulVultureGlow extends RenderLayer<EntitySoulVulture, ModelSoulVulture> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/soul_vulture_glow.png");

    public LayerSoulVultureGlow(RenderSoulVulture renderSoulVulture) {
        super(renderSoulVulture);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySoulVulture entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE, 0));
        float alpha = 0.75F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.125F;
        this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);

    }
}
