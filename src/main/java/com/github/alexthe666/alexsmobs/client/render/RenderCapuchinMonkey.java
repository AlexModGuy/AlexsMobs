package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCapuchinItem;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCapuchinMonkey extends MobRenderer<EntityCapuchinMonkey, ModelCapuchinMonkey> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/capuchin_monkey.png");

    public RenderCapuchinMonkey(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCapuchinMonkey(), 0.25F);
        this.addLayer(new LayerCapuchinItem(this));
    }

    protected void preRenderCallback(EntityCapuchinMonkey entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }


    public ResourceLocation getEntityTexture(EntityCapuchinMonkey entity) {
        return TEXTURE;
    }
}
