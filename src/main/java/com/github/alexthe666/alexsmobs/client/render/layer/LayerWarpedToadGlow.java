package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelWarpedToad;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderWarpedToad;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class LayerWarpedToadGlow extends LayerRenderer<EntityWarpedToad, ModelWarpedToad> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/warped_toad_glow.png");
    private static final ResourceLocation TEXTURE_BLINKING = new ResourceLocation("alexsmobs:textures/entity/warped_toad_glow_blink.png");

    public LayerWarpedToadGlow(RenderWarpedToad renderWarpedToad) {
        super(renderWarpedToad);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityWarpedToad entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!entitylivingbaseIn.isBased()){
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(entitylivingbaseIn.isBlinking() ? TEXTURE_BLINKING : TEXTURE, 0));
            float alpha = 0.75F + (MathHelper.cos(ageInTicks * 0.2F) + 1F) * 0.125F;
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, 240, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}
