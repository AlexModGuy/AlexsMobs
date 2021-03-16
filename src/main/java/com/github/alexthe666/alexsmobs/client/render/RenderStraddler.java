package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelStraddler;
import com.github.alexthe666.alexsmobs.client.model.ModelStradpole;
import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderStraddler extends MobRenderer<EntityStraddler, ModelStraddler> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/straddler.png");
    private static final ModelStradpole STRADPOLE_MODEL = new ModelStradpole();
    public RenderStraddler(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelStraddler(), 0.6F);
        this.addLayer(new StradpoleLayer(this));
    }

    protected void preRenderCallback(EntityStraddler entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getEntityTexture(EntityStraddler entity) {
        return TEXTURE;
    }

    class StradpoleLayer extends LayerRenderer<EntityStraddler, ModelStraddler> {

        public StradpoleLayer(RenderStraddler p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityStraddler straddler, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            int t = straddler.getAnimationTick();
            if(straddler.getAnimation() == EntityStraddler.ANIMATION_LAUNCH && t < 20 && t > 6){
                matrixStackIn.push();
                translateToModel(matrixStackIn);
                float back = t <= 15 ? (t-6) * 0.05F : 0.25F;
                matrixStackIn.translate(0F, -2.5F + back * 0.5F, 0.35F + back);
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(RenderStradpole.TEXTURE));
                STRADPOLE_MODEL.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(straddler, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.pop();
            }
        }

        protected void translateToModel(MatrixStack matrixStack) {
            this.getEntityModel().root.translateRotate(matrixStack);
            this.getEntityModel().body.translateRotate(matrixStack);

        }
    }
}