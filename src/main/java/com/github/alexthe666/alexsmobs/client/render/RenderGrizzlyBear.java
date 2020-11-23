package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGrizzlyHoney;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGrizzlyItem;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderGrizzlyBear extends MobRenderer<EntityGrizzlyBear, ModelGrizzlyBear> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/grizzly_bear.png");

    public RenderGrizzlyBear(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelGrizzlyBear(), 0.8F);
        this.addLayer(new LayerGrizzlyHoney(this));
        this.addLayer(new LayerGrizzlyItem(this));
    }

    protected void preRenderCallback(EntityGrizzlyBear entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getEntityTexture(EntityGrizzlyBear entity) {
        return TEXTURE;
    }
}
