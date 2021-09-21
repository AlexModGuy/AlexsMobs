package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelKomodoDragon;
import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderKomodoDragon extends MobRenderer<EntityKomodoDragon, ModelKomodoDragon> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/komodo_dragon.png");

    public RenderKomodoDragon(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelKomodoDragon(), 0.6F);
    }

    protected void scale(EntityKomodoDragon entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getTextureLocation(EntityKomodoDragon entity) {
        return TEXTURE;
    }
}
