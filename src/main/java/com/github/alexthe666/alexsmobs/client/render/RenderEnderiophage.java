package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEnderiophage;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class RenderEnderiophage extends MobRenderer<EntityEnderiophage, ModelEnderiophage> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/enderiophage.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/enderiophage_glow.png");
    private static final ResourceLocation TEXTURE_OVERWORLD = new ResourceLocation("alexsmobs:textures/entity/enderiophage_overworld.png");
    private static final ResourceLocation TEXTURE_OVERWORLD_GLOW = new ResourceLocation("alexsmobs:textures/entity/enderiophage_overworld_glow.png");
    private static final ResourceLocation TEXTURE_NETHER = new ResourceLocation("alexsmobs:textures/entity/enderiophage_nether.png");
    private static final ResourceLocation TEXTURE_NETHER_GLOW = new ResourceLocation("alexsmobs:textures/entity/enderiophage_nether_glow.png");

    public RenderEnderiophage(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelEnderiophage(), 0.5F);
        this.addLayer(new EnderiophageEyesLayer(this));
    }

    @Nullable
    @Override
    protected RenderType func_230496_a_(EntityEnderiophage p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        ResourceLocation resourcelocation = this.getEntityTexture(p_230496_1_);
        if (p_230496_3_) {
            return RenderType.getItemEntityTranslucentCull(resourcelocation);
        } else if (p_230496_2_) {
            return RenderType.getEntityTranslucent(resourcelocation);
        } else {
            return p_230496_4_ ? RenderType.getOutline(resourcelocation) : null;
        }
    }

    protected void preRenderCallback(EntityEnderiophage entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = entitylivingbaseIn.prevEnderiophageScale + (entitylivingbaseIn.getPhageScale() - entitylivingbaseIn.prevEnderiophageScale) * partialTickTime;
        matrixStackIn.scale(0.8F * scale, 0.8F * scale, 0.8F * scale);
    }


    public ResourceLocation getEntityTexture(EntityEnderiophage entity) {
        return entity.getVariant() == 2 ? TEXTURE_NETHER : entity.getVariant() == 1 ? TEXTURE_OVERWORLD : TEXTURE;
    }

    class EnderiophageEyesLayer extends AbstractEyesLayer<EntityEnderiophage, ModelEnderiophage> {

        public EnderiophageEyesLayer(RenderEnderiophage p_i50928_1_) {
            super(p_i50928_1_);
        }


        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityEnderiophage entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.getRenderType(entitylivingbaseIn));
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public RenderType getRenderType() {
            return AMRenderTypes.getGhost(TEXTURE_GLOW);
        }

        public RenderType getRenderType(EntityEnderiophage entity) {
            return AMRenderTypes.getGhost(entity.getVariant() == 2 ? TEXTURE_NETHER_GLOW : entity.getVariant() == 1 ? TEXTURE_OVERWORLD_GLOW : TEXTURE_GLOW);
        }
    }

}
