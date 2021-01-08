package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelSoulVulture;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderSoulVulture;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class LayerSoulVultureGlow extends LayerRenderer<EntitySoulVulture, ModelSoulVulture> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/soul_vulture_glow.png");

    public LayerSoulVultureGlow(RenderSoulVulture renderSoulVulture) {
        super(renderSoulVulture);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntitySoulVulture entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE, 0));
        float alpha = 0.75F + (MathHelper.cos(ageInTicks * 0.2F) + 1F) * 0.125F;
        this.getEntityModel().render(matrixStackIn, ivertexbuilder, 240, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);

    }
}
