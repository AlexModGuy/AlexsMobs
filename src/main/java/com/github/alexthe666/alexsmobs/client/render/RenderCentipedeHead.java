package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCentipedeHead;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCentipedeHeadEyes;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCentipedeHead extends MobRenderer<EntityCentipedeHead, ModelCentipedeHead> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/centipede_head.png");

    public RenderCentipedeHead(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCentipedeHead(), 0.5F);
        this.addLayer(new LayerCentipedeHeadEyes(this));
    }

    public ResourceLocation getEntityTexture(EntityCentipedeHead entity) {
        return TEXTURE;
    }
}
