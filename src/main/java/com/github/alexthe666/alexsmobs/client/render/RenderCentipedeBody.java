package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaveCentipede;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCentipedeBody extends MobRenderer<EntityCentipedeBody, AdvancedEntityModel<EntityCentipedeBody>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede.png");

    public RenderCentipedeBody(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCaveCentipede<>(1), 0.5F);
    }

    public ResourceLocation getTextureLocation(EntityCentipedeBody entity) {
        return TEXTURE;
    }
}
