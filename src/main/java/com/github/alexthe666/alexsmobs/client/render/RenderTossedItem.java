package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

public class RenderTossedItem  extends EntityRenderer<EntityTossedItem> {
    public static final ResourceLocation DART_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/ancient_dart.png");
    public static final ModelAncientDart DART_MODEL = new ModelAncientDart();

    public RenderTossedItem(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityTossedItem entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(EntityTossedItem entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        if(entityIn.isDart()){
            matrixStackIn.translate(0.0D, (double)-0.15F, 0.0D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180F));
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
            matrixStackIn.translate(0, 0.5F, 0);
            matrixStackIn.scale(1F, 1F, 1F);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(DART_MODEL.renderType(DART_TEXTURE));
            DART_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }else{
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
            matrixStackIn.translate(0, 0.5F, 0);
            matrixStackIn.scale(1F, 1F, 1F);
            matrixStackIn.mulPose((new Quaternionf()).rotateZ((float) Math.toRadians(-(entityIn.tickCount + partialTicks) * 30F)));
            matrixStackIn.translate(0, -0.15F, 0);
            Minecraft.getInstance().getItemRenderer().renderStatic(entityIn.getItem(), ItemDisplayContext.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, entityIn.level, 0);
        }
        matrixStackIn.popPose();
    }

}
