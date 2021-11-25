package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaveCentipede;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCentipedeHeadEyes;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCentipedeHead extends MobRenderer<EntityCentipedeHead, AdvancedEntityModel<EntityCentipedeHead>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede.png");

    public RenderCentipedeHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCaveCentipede<>(0), 0.5F);
        this.addLayer(new LayerCentipedeHeadEyes(this));
    }

    public ResourceLocation getTextureLocation(EntityCentipedeHead entity) {
        return TEXTURE;
    }
}
