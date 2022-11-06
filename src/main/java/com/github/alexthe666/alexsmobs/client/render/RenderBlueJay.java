package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBlueJay;
import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class RenderBlueJay extends MobRenderer<EntityBlueJay, ModelBlueJay> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/blue_jay.png");
    private static final ResourceLocation TEXTURE_SHINY = new ResourceLocation("alexsmobs:textures/entity/blue_jay_shiny.png");

    public RenderBlueJay(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelBlueJay(), 0.2F);
        this.addLayer(new LayerShiny());
    }

    protected void scale(EntityBlueJay mob, PoseStack matrixStackIn, float partialTicks) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
        if(mob.isPassenger() && mob.getVehicle() != null) {
            if (mob.getVehicle() instanceof EntityRaccoon entityRaccoon) {
                EntityRenderer raccoonRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityRaccoon);
                if (raccoonRenderer instanceof LivingEntityRenderer && ((LivingEntityRenderer) raccoonRenderer).getModel() instanceof ModelRaccoon raccoonModel) {
                    float begProgress = entityRaccoon.prevBegProgress + (entityRaccoon.begProgress - entityRaccoon.prevBegProgress) * partialTicks;
                    float standProgress0 = entityRaccoon.prevStandProgress + (entityRaccoon.standProgress - entityRaccoon.prevStandProgress) * partialTicks;
                    float sitProgress = entityRaccoon.prevSitProgress + (entityRaccoon.sitProgress - entityRaccoon.prevSitProgress) * partialTicks;
                    float standProgress = Math.max(Math.max(begProgress, standProgress0) - sitProgress, 0);
                    matrixStackIn.translate(0F, -1.03F - sitProgress * 0.01F, 0F);
                    Vec3 vec = raccoonModel.getRidingPosition(new Vec3(0, 0, -0.1F + standProgress * 0.1F));
                    matrixStackIn.translate(vec.x, vec.y , vec.z);
                }
            }
        }
    }


    public ResourceLocation getTextureLocation(EntityBlueJay entity) {
        return TEXTURE;
    }

    class LayerShiny extends RenderLayer<EntityBlueJay, ModelBlueJay> {

        public LayerShiny() {
            super(RenderBlueJay.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityBlueJay entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(entitylivingbaseIn.getFeedTime() > 0){
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE_SHINY));
                float alpha = (float) (1F + Math.sin(ageInTicks * 0.3F)) * 0.1F + 0.8F;
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }
}
