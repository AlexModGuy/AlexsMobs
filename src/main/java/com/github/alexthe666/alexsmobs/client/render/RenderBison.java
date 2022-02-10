package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.*;
import com.github.alexthe666.alexsmobs.entity.EntityBison;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderBison extends MobRenderer<EntityBison, AdvancedEntityModel<EntityBison>> {
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexsmobs:textures/entity/bison_baby.png");
    private static final ResourceLocation TEXTURE_BABY_SNOWY = new ResourceLocation("alexsmobs:textures/entity/bison_baby_snowy.png");
    private static final ResourceLocation TEXTURE_SNOWY = new ResourceLocation("alexsmobs:textures/entity/bison_snowy.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/bison.png");
    private static final ResourceLocation TEXTURE_SHEARED = new ResourceLocation("alexsmobs:textures/entity/bison_sheared.png");
    private final ModelBison modelBison = new ModelBison();
    private final ModelBisonBaby modelBaby = new ModelBisonBaby();

    public RenderBison(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelBison(), 0.8F);
        this.addLayer(new LayerSnow());
    }

    protected void scale(EntityBison entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        if(entitylivingbaseIn.isBaby()){
            model = modelBaby;
        }else{
            model = modelBison;
        }
    }

    public ResourceLocation getTextureLocation(EntityBison entity) {
        return entity.isBaby() ? TEXTURE_BABY : entity.isSheared() ? TEXTURE_SHEARED : TEXTURE;
    }

    class LayerSnow extends RenderLayer<EntityBison, AdvancedEntityModel<EntityBison>> {

        public LayerSnow() {
            super(RenderBison.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityBison entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isSnowy()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(entitylivingbaseIn.isBaby() ? TEXTURE_BABY_SNOWY : TEXTURE_SNOWY));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
