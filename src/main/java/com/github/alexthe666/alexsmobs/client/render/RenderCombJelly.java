package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCombJelly;
import com.github.alexthe666.alexsmobs.entity.EntityCombJelly;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class RenderCombJelly extends MobRenderer<EntityCombJelly, ModelCombJelly> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/comb_jelly_blue.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/comb_jelly_green.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/comb_jelly_red.png");
    private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/comb_jelly_overlay.png");
    private static final ModelCombJelly STRIPES_MODEL = new ModelCombJelly(0.05F);
    public RenderCombJelly(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCombJelly(0.0F), 0.3F);
        this.addLayer(new RainbowLayer(this));
    }

    protected void scale(EntityCombJelly jelly, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(jelly.getJellyScale(), jelly.getJellyScale(), jelly.getJellyScale());
    }

    protected float getFlipDegrees(EntityCombJelly jelly) {
        return 0.0F;
    }

    @Nullable
    protected RenderType getRenderType(EntityCombJelly jelly, boolean normal, boolean invis, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(jelly);
        if (invis) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }


    public ResourceLocation getTextureLocation(EntityCombJelly entity) {
        return entity.getVariant() == 0 ? TEXTURE_0 : entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_2;
    }

    static class RainbowLayer extends RenderLayer<EntityCombJelly, ModelCombJelly> {

        public RainbowLayer(RenderCombJelly render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCombJelly entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer rainbow = AMRenderTypes.createMergedVertexConsumer(bufferIn.getBuffer(AMRenderTypes.COMBJELLY_RAINBOW_GLINT), bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_OVERLAY)));
            STRIPES_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            STRIPES_MODEL.renderToBuffer(matrixStackIn, rainbow, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
        }
    }
}
