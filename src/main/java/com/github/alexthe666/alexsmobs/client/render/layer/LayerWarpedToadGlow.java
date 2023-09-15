package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelWarpedToad;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderWarpedToad;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class LayerWarpedToadGlow extends RenderLayer<EntityWarpedToad, ModelWarpedToad> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/warped_toad_glow.png");
    private static final ResourceLocation TEXTURE_BLINKING = new ResourceLocation("alexsmobs:textures/entity/warped_toad_glow_blink.png");

    public LayerWarpedToadGlow(RenderWarpedToad renderWarpedToad) {
        super(renderWarpedToad);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityWarpedToad entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!entitylivingbaseIn.isBased()){
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(entitylivingbaseIn.isBlinking() ? TEXTURE_BLINKING : TEXTURE, 0));
            final float alpha = 0.75F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.125F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}
