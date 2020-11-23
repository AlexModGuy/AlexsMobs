package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class LayerGrizzlyHoney extends LayerRenderer<EntityGrizzlyBear, ModelGrizzlyBear> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/grizzly_bear_honey.png");

    public LayerGrizzlyHoney(RenderGrizzlyBear renderGrizzlyBear) {
        super(renderGrizzlyBear);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityGrizzlyBear entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entitylivingbaseIn.isHoneyed()){
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(TEXTURE));
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
