package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSpectre;
import com.github.alexthe666.alexsmobs.entity.EntitySpectre;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class RenderSpectre extends MobRenderer<EntitySpectre, ModelSpectre> {
    private static final ResourceLocation TEXTURE_BONE = new ResourceLocation("alexsmobs:textures/entity/spectre_bone.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/spectre.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/spectre_glow.png");
    private static final ResourceLocation TEXTURE_LEAD = new ResourceLocation("alexsmobs:textures/entity/spectre_lead.png");

    public RenderSpectre(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelSpectre(), 0.5F);
        this.addLayer(new SpectreEyesLayer(this));
        this.addLayer(new SpectreMembraneLayer(this));
    }

    protected void scale(EntitySpectre entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }

    protected int getBlockLightLevel(EntitySpectre entityIn, BlockPos partialTicks) {
        return 15;
    }

    public ResourceLocation getTextureLocation(EntitySpectre entity) {
        return TEXTURE_BONE;
    }

    public float getAlphaForRender(EntitySpectre entityIn, float partialTicks) {
        return ((float) Math.sin((entityIn.tickCount + partialTicks) * 0.1F) + 1.5F) * 0.1F + 0.5F;
    }

    class SpectreEyesLayer extends EyesLayer<EntitySpectre, ModelSpectre> {

        public SpectreEyesLayer(RenderSpectre p_i50928_1_) {
            super(p_i50928_1_);
        }

        public RenderType renderType() {
            return RenderType.eyes(TEXTURE_EYES);
        }
    }

    class SpectreMembraneLayer extends RenderLayer<EntitySpectre, ModelSpectre> {

        public SpectreMembraneLayer(RenderSpectre p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySpectre entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer lvt_11_1_ = bufferIn.getBuffer(this.getRenderType());
            this.getParentModel().renderToBuffer(matrixStackIn, lvt_11_1_, 15728640, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, getAlphaForRender(entitylivingbaseIn, partialTicks));
            if (entitylivingbaseIn.isLeashed()) {
                VertexConsumer lead = bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_LEAD));
                this.getParentModel().renderToBuffer(matrixStackIn, lead, 15728640, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        public RenderType getRenderType() {
            return AMRenderTypes.getGhost(TEXTURE);
        }
    }
}
