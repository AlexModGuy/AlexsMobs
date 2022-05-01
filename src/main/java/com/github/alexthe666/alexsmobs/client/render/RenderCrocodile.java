package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.client.model.ModelMungus;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderCrocodile extends MobRenderer<EntityCrocodile, ModelCrocodile> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/crocodile_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/crocodile_1.png");
    private static final ResourceLocation TEXTURE_CROWN = new ResourceLocation("alexsmobs:textures/entity/crocodile_crown.png");

    public RenderCrocodile(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCrocodile(), 0.8F);
        this.addLayer(new CrownLayer(this));
    }

    protected void scale(EntityCrocodile entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }


    public ResourceLocation getTextureLocation(EntityCrocodile entity) {
        return entity.isDesert() ? TEXTURE_1 : TEXTURE_0;
    }

    class CrownLayer extends RenderLayer<EntityCrocodile, ModelCrocodile> {

        public CrownLayer(RenderCrocodile p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCrocodile entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isCrowned()) {
                VertexConsumer shoeBuffer = bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_CROWN));
                matrixStackIn.pushPose();
                this.getParentModel().renderToBuffer(matrixStackIn, shoeBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
        }
    }


}
