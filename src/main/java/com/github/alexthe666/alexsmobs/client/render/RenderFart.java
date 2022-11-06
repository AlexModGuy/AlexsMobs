package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFart;
import com.github.alexthe666.alexsmobs.entity.EntityFart;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.LlamaSpit;

public class RenderFart extends EntityRenderer<EntityFart> {
    private static final ResourceLocation FART_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/fart.png");
    private static final ModelFart MODEL = new ModelFart();

    public RenderFart(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(EntityFart entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float f = Math.min(entityIn.tickCount + partialTicks, 30F) / 30F;
        float alpha = 1F - f;
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double)0.15F, 0.0D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(FART_TEXTURE));
        MODEL.setupAnim(entityIn, 0.0F, 0.0F, partialTicks, 0.0F, 0.0F);
        MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(EntityFart entity) {
        return FART_TEXTURE;
    }
}
