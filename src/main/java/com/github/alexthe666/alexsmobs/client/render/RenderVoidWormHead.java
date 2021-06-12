package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderVoidWormHead extends MobRenderer<EntityVoidWorm, ModelVoidWorm> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm_head.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/void_worm_head_glow.png");

    public RenderVoidWormHead(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelVoidWorm(), 1F);
        this.addLayer(new LayerBasicGlow(this, TEXTURE_GLOW));
    }

    public ResourceLocation getEntityTexture(EntityVoidWorm entity) {
        return TEXTURE;
    }
}
