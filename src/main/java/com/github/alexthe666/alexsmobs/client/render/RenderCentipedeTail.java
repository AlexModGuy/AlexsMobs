package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCentipedeTail;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeTail;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCentipedeTail extends MobRenderer<EntityCentipedeTail, ModelCentipedeTail> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/centipede_tail.png");

    public RenderCentipedeTail(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelCentipedeTail(), 0.5F);
    }

    public ResourceLocation getTextureLocation(EntityCentipedeTail entity) {
        return TEXTURE;
    }
}
