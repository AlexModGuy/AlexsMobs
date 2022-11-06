package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderSharkToothArrow extends ArrowRenderer<EntitySharkToothArrow> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/shark_tooth_arrow.png");

    public RenderSharkToothArrow(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySharkToothArrow entity) {
        return TEXTURE;
    }
}
