package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCaiman extends MobRenderer<EntityCaiman, ModelCaiman> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/caiman.png");

    public RenderCaiman(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCaiman(), 0.4F);
    }

    public ResourceLocation getTextureLocation(EntityCaiman entity) {
        return TEXTURE;
    }
}
