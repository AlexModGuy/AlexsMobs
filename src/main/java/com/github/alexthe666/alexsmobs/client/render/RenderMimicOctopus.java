package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderMimicOctopus extends MobRenderer<EntityMimicOctopus, ModelMimicOctopus> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus.png");
    private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_overlay.png");
    private static final ResourceLocation TEXTURE_CREEPER = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_creeper.png");
    private static final ResourceLocation TEXTURE_GUARDIAN = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_guardian.png");
    private static final ResourceLocation TEXTURE_PUFFERFISH = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_pufferfish.png");

    public RenderMimicOctopus(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelMimicOctopus(), 0.4F);
        this.addLayer(new OverlayLayer(this));
    }

    protected void preRenderCallback(EntityMimicOctopus entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.translate(0, -0.02F, 0);
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    public ResourceLocation getEntityTexture(EntityMimicOctopus entity) {
        return TEXTURE;
    }

    class OverlayLayer extends LayerRenderer<EntityMimicOctopus, ModelMimicOctopus> {


        public OverlayLayer(RenderMimicOctopus render) {
            super(render);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, EntityMimicOctopus entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float transProgress = entitylivingbaseIn.prevTransProgress + (entitylivingbaseIn.transProgress - entitylivingbaseIn.prevTransProgress) * partialTicks;
            float colorProgress = (entitylivingbaseIn.prevColorShiftProgress + (entitylivingbaseIn.colorShiftProgress - entitylivingbaseIn.prevColorShiftProgress) * partialTicks) * 0.2F;
            float r = 1F;
            float g = 1F;
            float b = 1F;
            float a = 1F;
            float startR = 1.0F;
            float startG = 1.0F;
            float startB = 1.0F;
            float startA = 1.0F;
            float finR = 1.0F;
            float finG = 1.0F;
            float finB = 1.0F;
            float finA = 1.0F;
            if(entitylivingbaseIn.getPrevMimicState() == EntityMimicOctopus.MimicState.OVERLAY){
                if(entitylivingbaseIn.getPrevMimickedBlock() != null){
                    int j = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getPrevMimickedBlock());
                    startR = (float)(j >> 16 & 255) / 255.0F;
                    startG = (float)(j >> 8 & 255) / 255.0F;
                    startB = (float)(j & 255) / 255.0F;
                }else{
                    startA = 0.0F;
                }
            }
            if((entitylivingbaseIn.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY)){
                if(entitylivingbaseIn.getMimickedBlock() != null){
                    int i = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getMimickedBlock());
                    finR = (float)(i >> 16 & 255) / 255.0F;
                    finG = (float)(i >> 8 & 255) / 255.0F;
                    finB = (float)(i & 255) / 255.0F;
                }else{
                    finA = 0.0F;
                }
                r = startR + (finR - startR) * colorProgress;
                g = startG + (finG - startG) * colorProgress;
                b = startB + (finB - startB) * colorProgress;
                a = startA + (finA - startA) * colorProgress;
            }
            if(a == 1.0F){
               // a *= 0.9F + 0.1F * (float)Math.sin(entitylivingbaseIn.ticksExisted * 0.1F);
            }
            float alphaPrev = 0.0F;
            if(entitylivingbaseIn.getPrevMimicState() != null){
                alphaPrev = 1 - transProgress * 0.2F;
                IVertexBuilder prev = buffer.getBuffer(AMRenderTypes.getEntityTranslucent(getFor(entitylivingbaseIn.getPrevMimicState())));
                this.getEntityModel().render(matrixStackIn, prev, packedLightIn, getPackedOverlay(entitylivingbaseIn, 0), r, g, b, a * alphaPrev);
            }
            float alphaCurrent = transProgress * 0.2F;
            IVertexBuilder current = buffer.getBuffer(AMRenderTypes.getEntityTranslucent(getFor(entitylivingbaseIn.getMimicState())));
            this.getEntityModel().render(matrixStackIn, current, packedLightIn, getPackedOverlay(entitylivingbaseIn, 0), r, g, b, a * alphaCurrent);
        }

        public ResourceLocation getFor(EntityMimicOctopus.MimicState state){
            if(state == EntityMimicOctopus.MimicState.CREEPER){
                return TEXTURE_CREEPER;
            }
            if(state == EntityMimicOctopus.MimicState.GUARDIAN){
                return TEXTURE_GUARDIAN;
            }
            if(state == EntityMimicOctopus.MimicState.PUFFERFISH){
                return TEXTURE_PUFFERFISH;
            }
            return TEXTURE_OVERLAY;
        }
    }
}
