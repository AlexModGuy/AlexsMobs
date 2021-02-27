package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelGuster;
import com.github.alexthe666.alexsmobs.client.model.ModelGuster;
import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderGuster extends MobRenderer<EntityGuster, ModelGuster> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/guster.png");
    private static final ResourceLocation TEXTURE_GOOGLY = new ResourceLocation("alexsmobs:textures/entity/guster_silly.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/guster_eye.png");

    public RenderGuster(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelGuster(), 0.25F);
        this.addLayer(new RenderGuster.GusterEyesLayer(this));
    }

    @Nullable
    protected RenderType func_230496_a_(EntityGuster p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        ResourceLocation resourcelocation = this.getEntityTexture(p_230496_1_);
        if (p_230496_3_) {
            return RenderType.getEntityTranslucent(resourcelocation);
        } else if (p_230496_2_) {
            return RenderType.getEntityTranslucent(resourcelocation);
        } else {
            return p_230496_4_ ? RenderType.getOutline(resourcelocation) : null;
        }
    }


    public ResourceLocation getEntityTexture(EntityGuster entity) {
        return entity.isGooglyEyes() ? TEXTURE_GOOGLY : TEXTURE;
    }

    class GusterEyesLayer extends AbstractEyesLayer<EntityGuster, ModelGuster> {

        public GusterEyesLayer(RenderGuster p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityGuster entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(!entitylivingbaseIn.isGooglyEyes()){
                super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,  headPitch);
            }
        }

        public RenderType getRenderType() {
            return AMRenderTypes.getEyesNoCull(TEXTURE_EYES);
        }
    }
}
