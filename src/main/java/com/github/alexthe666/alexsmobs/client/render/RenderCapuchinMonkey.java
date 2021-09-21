package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCapuchinItem;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderCapuchinMonkey extends MobRenderer<EntityCapuchinMonkey, ModelCapuchinMonkey> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/capuchin_monkey.png");

    public RenderCapuchinMonkey(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCapuchinMonkey(), 0.25F);
        this.addLayer(new LayerCapuchinItem(this));
    }

    protected void scale(EntityCapuchinMonkey entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getTextureLocation(EntityCapuchinMonkey entity) {
        return TEXTURE;
    }
}
