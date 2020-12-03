package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelCentipedeHead;
import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.RenderCentipedeHead;
import com.github.alexthe666.alexsmobs.client.render.RenderCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerCentipedeHeadEyes extends LayerRenderer<EntityCentipedeHead, ModelCentipedeHead> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/centipede_eyes.png");

    public LayerCentipedeHeadEyes(RenderCentipedeHead render) {
        super(render);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityCentipedeHead entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(TEXTURE));
        this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);

    }
}
