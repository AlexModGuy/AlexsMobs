package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelWarpedMosco;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderWarpedMosco extends MobRenderer<EntityWarpedMosco, ModelWarpedMosco> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/warped_mosco.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/warped_mosco_glow.png");

    public RenderWarpedMosco(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelWarpedMosco(), 1F);
        this.addLayer(new WarpedMoscoGlowLayer(this));
    }

    public ResourceLocation getTextureLocation(EntityWarpedMosco entity) {
        return TEXTURE;
    }

    static class WarpedMoscoGlowLayer extends RenderLayer<EntityWarpedMosco, ModelWarpedMosco> {

        public WarpedMoscoGlowLayer(RenderWarpedMosco p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityWarpedMosco entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE_EYES, 0));
            float alpha = 0.5F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.2F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 0.5F, 1.0F, 1.0F, alpha);

        }
    }
}
