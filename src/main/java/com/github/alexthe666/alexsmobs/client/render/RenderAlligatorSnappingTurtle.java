package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderAlligatorSnappingTurtle extends MobRenderer<EntityAlligatorSnappingTurtle, ModelAlligatorSnappingTurtle> {
    private static final ResourceLocation TEXTURE_MOSS = new ResourceLocation("alexsmobs:textures/entity/alligator_snapping_turtle_moss.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/alligator_snapping_turtle.png");

    public RenderAlligatorSnappingTurtle(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelAlligatorSnappingTurtle(), 0.75F);
        this.addLayer(new AlligatorSnappingTurtleMossLayer(this));
    }

    protected void preRenderCallback(EntityAlligatorSnappingTurtle entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float d = entitylivingbaseIn.getTurtleScale() < 0.01F ? 1F : entitylivingbaseIn.getTurtleScale();
        matrixStackIn.scale(d, d, d);
    }

    public ResourceLocation getEntityTexture(EntityAlligatorSnappingTurtle entity) {
        return TEXTURE;
    }

    class AlligatorSnappingTurtleMossLayer extends LayerRenderer<EntityAlligatorSnappingTurtle, ModelAlligatorSnappingTurtle> {

        public AlligatorSnappingTurtleMossLayer(RenderAlligatorSnappingTurtle p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityAlligatorSnappingTurtle entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(entitylivingbaseIn.getMoss() > 0){
                float mossAlpha = 0.15F * MathHelper.clamp(entitylivingbaseIn.getMoss(), 0, 10);
                IVertexBuilder mossbuffer = bufferIn.getBuffer(AMRenderTypes.getEntityTranslucent(TEXTURE_MOSS));
                this.getEntityModel().render(matrixStackIn, mossbuffer, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, Math.min(1.0F, mossAlpha));
            }
        }
    }
}
