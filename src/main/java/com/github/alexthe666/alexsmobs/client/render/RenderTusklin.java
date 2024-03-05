package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTusklin;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerTusklinGear;
import com.github.alexthe666.alexsmobs.entity.EntityTusklin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderTusklin extends MobRenderer<EntityTusklin, ModelTusklin> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tusklin.png");

    public RenderTusklin(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTusklin(), 1.0F);
        this.addLayer(new LayerTusklinGear(this));
    }

    protected boolean isShaking(EntityTusklin entity) {
        return entity.isInNether();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityTusklin tusklin) {
        return TEXTURE;
    }
}
