package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelWarpedToad;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerWarpedToadGlow;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderWarpedToad extends MobRenderer<EntityWarpedToad, ModelWarpedToad> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/warped_toad.png");
    private static final ResourceLocation TEXTURE_BLINKING = new ResourceLocation("alexsmobs:textures/entity/warped_toad_blink.png");

    public RenderWarpedToad(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelWarpedToad(), 0.85F);
        this.addLayer(new LayerWarpedToadGlow(this));
    }

    protected void preRenderCallback(EntityWarpedToad entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
    }


    public ResourceLocation getEntityTexture(EntityWarpedToad entity) {
        return entity.isBlinking() ? TEXTURE_BLINKING : TEXTURE;
    }
}
