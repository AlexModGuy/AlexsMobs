package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSkreecher;
import com.github.alexthe666.alexsmobs.entity.EntitySkreecher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class RenderSkreecher extends MobRenderer<EntitySkreecher, ModelSkreecher> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/skreecher.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/skreecher_glow.png");

    public RenderSkreecher(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSkreecher(), 0.35F);
        this.addLayer(new LayerScorch(this));
    }

    protected void scale(EntitySkreecher entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getTextureLocation(EntitySkreecher entity) {
        return TEXTURE;
    }

    class LayerScorch extends RenderLayer<EntitySkreecher, ModelSkreecher> {

        public LayerScorch(RenderSkreecher render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySkreecher entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer scorch = bufferIn.getBuffer(AMRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            float alpha = 1F;
            this.getParentModel().renderToBuffer(matrixStackIn, scorch, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}
