package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelPlatypus;
import com.github.alexthe666.alexsmobs.client.model.ModelPlatypus;
import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderPlatypus extends MobRenderer<EntityPlatypus, ModelPlatypus> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/platypus.png");
    private static final ResourceLocation TEXTURE_PERRY = new ResourceLocation("alexsmobs:textures/entity/platypus_perry.png");

    public RenderPlatypus(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelPlatypus(), 0.45F);
        this.addLayer(new FedoraLayer(this));
    }

    protected void preRenderCallback(EntityPlatypus entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
         matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    public ResourceLocation getEntityTexture(EntityPlatypus entity) {
        return entity.isPerry() ? TEXTURE_PERRY : TEXTURE;
    }

    class FedoraLayer extends LayerRenderer<EntityPlatypus, ModelPlatypus> {
        private final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/platypus_fedora.png");

        public FedoraLayer(RenderPlatypus renderGrizzlyBear) {
            super(renderGrizzlyBear);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityPlatypus entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(entitylivingbaseIn.hasFedora()){
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutout(TEXTURE));
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
