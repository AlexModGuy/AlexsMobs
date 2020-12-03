package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCentipedeBody;
import com.github.alexthe666.alexsmobs.client.model.ModelCentipedeTail;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeTail;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCentipedeTail extends MobRenderer<EntityCentipedeTail, ModelCentipedeTail> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/centipede_tail.png");

    public RenderCentipedeTail(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCentipedeTail(), 0.5F);
    }

    public ResourceLocation getEntityTexture(EntityCentipedeTail entity) {
        return TEXTURE;
    }
}
