package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCentipedeBody;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCentipedeBody extends MobRenderer<EntityCentipedeBody, ModelCentipedeBody> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/centipede_body.png");

    public RenderCentipedeBody(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelCentipedeBody(), 0.5F);
    }

    public ResourceLocation getTextureLocation(EntityCentipedeBody entity) {
        return TEXTURE;
    }
}
