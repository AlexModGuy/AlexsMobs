package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaveCentipede;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeTail;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCentipedeTail extends MobRenderer<EntityCentipedeTail, AdvancedEntityModel<EntityCentipedeTail>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede.png");

    public RenderCentipedeTail(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCaveCentipede<>(2), 0.5F);
    }

    public ResourceLocation getTextureLocation(EntityCentipedeTail entity) {
        return TEXTURE;
    }
}
