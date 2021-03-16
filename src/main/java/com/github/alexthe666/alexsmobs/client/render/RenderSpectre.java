package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSpectre;
import com.github.alexthe666.alexsmobs.entity.EntitySpectre;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class RenderSpectre extends MobRenderer<EntitySpectre, ModelSpectre> {
    private static final ResourceLocation TEXTURE_BONE = new ResourceLocation("alexsmobs:textures/entity/spectre_bone.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/spectre.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/spectre_glow.png");
    private static final ResourceLocation TEXTURE_LEAD = new ResourceLocation("alexsmobs:textures/entity/spectre_lead.png");

    public RenderSpectre(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelSpectre(), 0.5F);
        this.addLayer(new SpectreEyesLayer(this));
        this.addLayer(new SpectreMembraneLayer(this));
    }

    protected void preRenderCallback(EntitySpectre entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }

    protected int getBlockLight(EntitySpectre entityIn, BlockPos partialTicks) {
        return 15;
    }

    public ResourceLocation getEntityTexture(EntitySpectre entity) {
        return TEXTURE_BONE;
    }

    public float getAlphaForRender(EntitySpectre entityIn, float partialTicks) {
        return ((float) Math.sin((entityIn.ticksExisted + partialTicks) * 0.1F) + 1.5F) * 0.1F + 0.5F;
    }

    class SpectreEyesLayer extends AbstractEyesLayer<EntitySpectre, ModelSpectre> {

        public SpectreEyesLayer(RenderSpectre p_i50928_1_) {
            super(p_i50928_1_);
        }

        public RenderType getRenderType() {
            return RenderType.getEyes(TEXTURE_EYES);
        }
    }

    class SpectreMembraneLayer extends LayerRenderer<EntitySpectre, ModelSpectre> {

        public SpectreMembraneLayer(RenderSpectre p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntitySpectre entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder lvt_11_1_ = bufferIn.getBuffer(this.getRenderType());
            this.getEntityModel().render(matrixStackIn, lvt_11_1_, 15728640, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, getAlphaForRender(entitylivingbaseIn, partialTicks));
            if (entitylivingbaseIn.getLeashed()) {
                IVertexBuilder lead = bufferIn.getBuffer(AMRenderTypes.getEntityCutoutNoCull(TEXTURE_LEAD));
                this.getEntityModel().render(matrixStackIn, lead, 15728640, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        public RenderType getRenderType() {
            return AMRenderTypes.getGhost(TEXTURE);
        }
    }
}
