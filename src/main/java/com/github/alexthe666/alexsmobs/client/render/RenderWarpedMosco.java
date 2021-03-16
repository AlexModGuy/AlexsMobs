package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelWarpedMosco;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderWarpedMosco extends MobRenderer<EntityWarpedMosco, ModelWarpedMosco> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/warped_mosco.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/warped_mosco_glow.png");

    public RenderWarpedMosco(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelWarpedMosco(), 1F);
        this.addLayer(new RenderWarpedMosco.WarpedMoscoGlowLayer(this));
    }

    public ResourceLocation getEntityTexture(EntityWarpedMosco entity) {
        return TEXTURE;
    }

    class WarpedMoscoGlowLayer extends LayerRenderer<EntityWarpedMosco, ModelWarpedMosco> {

        public WarpedMoscoGlowLayer(RenderWarpedMosco p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityWarpedMosco entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE_EYES, 0));
            float alpha = 0.5F + (MathHelper.cos(ageInTicks * 0.2F) + 1F) * 0.2F;
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, 240, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 0.5F, 1.0F, 1.0F, alpha);

        }
    }
}
