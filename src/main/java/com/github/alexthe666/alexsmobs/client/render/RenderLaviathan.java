package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelKomodoDragon;
import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderLaviathan  extends MobRenderer<EntityLaviathan, ModelLaviathan> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/laviathan.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/laviathan_glow.png");
    private static final ResourceLocation TEXTURE_OBSIDIAN = new ResourceLocation("alexsmobs:textures/entity/laviathan_obsidian.png");
    private static final ResourceLocation TEXTURE_GEAR = new ResourceLocation("alexsmobs:textures/entity/laviathan_gear.png");
    private static final ResourceLocation TEXTURE_HELMET = new ResourceLocation("alexsmobs:textures/entity/laviathan_helmet.png");

    public RenderLaviathan(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelLaviathan(), 4.0F);
        this.addLayer(new LayerOverlays(this));
    }

    public boolean shouldRender(EntityLaviathan livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            for(EntityLaviathanPart part : livingEntityIn.allParts){
                if(camera.isVisible(part.getBoundingBox())){
                    return true;
                }
            }
            return false;
        }
    }

    protected void scale(EntityLaviathan entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    protected boolean isShaking(EntityLaviathan entity) {
        return entity.isInWaterRainOrBubble() && !entity.isObsidian();
    }

    public ResourceLocation getTextureLocation(EntityLaviathan entity) {
        return entity.isObsidian() ? TEXTURE_OBSIDIAN : TEXTURE;
    }

    class LayerOverlays extends RenderLayer<EntityLaviathan, ModelLaviathan> {

        public LayerOverlays(RenderLaviathan render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityLaviathan laviathan, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!laviathan.isObsidian()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_GLOW));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if(laviathan.hasBodyGear()){
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_GEAR));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if(laviathan.hasHeadGear()){
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_HELMET));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

    }
}
