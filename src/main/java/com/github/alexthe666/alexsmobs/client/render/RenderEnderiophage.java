package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEnderiophage;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class RenderEnderiophage extends MobRenderer<EntityEnderiophage, ModelEnderiophage> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/enderiophage.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/enderiophage_glow.png");
    private static final ResourceLocation TEXTURE_OVERWORLD = new ResourceLocation("alexsmobs:textures/entity/enderiophage_overworld.png");
    private static final ResourceLocation TEXTURE_OVERWORLD_GLOW = new ResourceLocation("alexsmobs:textures/entity/enderiophage_overworld_glow.png");
    private static final ResourceLocation TEXTURE_NETHER = new ResourceLocation("alexsmobs:textures/entity/enderiophage_nether.png");
    private static final ResourceLocation TEXTURE_NETHER_GLOW = new ResourceLocation("alexsmobs:textures/entity/enderiophage_nether_glow.png");

    public RenderEnderiophage(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelEnderiophage(), 0.5F);
        this.addLayer(new EnderiophageEyesLayer(this));
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EntityEnderiophage p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        ResourceLocation resourcelocation = this.getTextureLocation(p_230496_1_);
        if (p_230496_3_) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (p_230496_2_) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return p_230496_4_ ? RenderType.outline(resourcelocation) : null;
        }
    }

    protected void scale(EntityEnderiophage entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float scale = entitylivingbaseIn.prevEnderiophageScale + (entitylivingbaseIn.getPhageScale() - entitylivingbaseIn.prevEnderiophageScale) * partialTickTime;
        matrixStackIn.scale(0.8F * scale, 0.8F * scale, 0.8F * scale);
    }


    public ResourceLocation getTextureLocation(EntityEnderiophage entity) {
        return entity.getVariant() == 2 ? TEXTURE_NETHER : entity.getVariant() == 1 ? TEXTURE_OVERWORLD : TEXTURE;
    }

    static class EnderiophageEyesLayer extends EyesLayer<EntityEnderiophage, ModelEnderiophage> {

        public EnderiophageEyesLayer(RenderEnderiophage p_i50928_1_) {
            super(p_i50928_1_);
        }


        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityEnderiophage entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.getRenderType(entitylivingbaseIn));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public RenderType renderType() {
            return AMRenderTypes.getGhost(TEXTURE_GLOW);
        }

        public RenderType getRenderType(EntityEnderiophage entity) {
            return AMRenderTypes.getGhost(entity.getVariant() == 2 ? TEXTURE_NETHER_GLOW : entity.getVariant() == 1 ? TEXTURE_OVERWORLD_GLOW : TEXTURE_GLOW);
        }
    }

}
