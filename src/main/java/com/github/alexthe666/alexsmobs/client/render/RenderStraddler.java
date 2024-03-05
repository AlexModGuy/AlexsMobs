package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelStraddler;
import com.github.alexthe666.alexsmobs.client.model.ModelStradpole;
import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderStraddler extends MobRenderer<EntityStraddler, ModelStraddler> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/straddler.png");
    private static final ModelStradpole STRADPOLE_MODEL = new ModelStradpole();
    public RenderStraddler(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelStraddler(), 0.6F);
        this.addLayer(new StradpoleLayer(this));
    }

    protected void scale(EntityStraddler entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getTextureLocation(EntityStraddler entity) {
        return TEXTURE;
    }

    static class StradpoleLayer extends RenderLayer<EntityStraddler, ModelStraddler> {

        public StradpoleLayer(RenderStraddler p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityStraddler straddler, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            int t = straddler.getAnimationTick();
            if(straddler.getAnimation() == EntityStraddler.ANIMATION_LAUNCH && t < 20 && t > 6){
                matrixStackIn.pushPose();
                translateToModel(matrixStackIn);
                final float back = t <= 15 ? (t-6) * 0.05F : 0.25F;
                matrixStackIn.translate(0F, -2.5F + back * 0.5F, 0.35F + back);
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(RenderStradpole.TEXTURE));
                STRADPOLE_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(straddler, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
        }

        protected void translateToModel(PoseStack matrixStack) {
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);

        }
    }
}