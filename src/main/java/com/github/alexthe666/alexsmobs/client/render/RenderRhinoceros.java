package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRhinoceros;
import com.github.alexthe666.alexsmobs.entity.EntityRhinoceros;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RenderRhinoceros extends MobRenderer<EntityRhinoceros, ModelRhinoceros> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/rhinoceros.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/rhinoceros_angry.png");
    private static final ResourceLocation TEXTURE_POTION = new ResourceLocation("alexsmobs:textures/entity/rhinoceros_potion.png");

    public RenderRhinoceros(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelRhinoceros(), 0.9F);
        this.addLayer(new PotionLayer(this));
    }


    protected void scale(EntityRhinoceros rabbit, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.1F, 1.1F, 1.1F);
    }

    public ResourceLocation getTextureLocation(EntityRhinoceros entity) {
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }

    private static class PotionLayer extends RenderLayer<EntityRhinoceros, ModelRhinoceros> {
        public PotionLayer(RenderRhinoceros parent) {
            super(parent);
        }

        public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, EntityRhinoceros rhino, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            int color = rhino.getPotionColor();
            if (color != -1 && !rhino.isInvisible()) {
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                this.getParentModel().renderToBuffer(p_225628_1_, p_225628_2_.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_POTION)), p_225628_3_, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
            }
        }
    }
}
