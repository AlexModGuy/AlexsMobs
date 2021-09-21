package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEndergrade;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerEndergradeSaddle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderEndergrade extends MobRenderer<EntityEndergrade, ModelEndergrade> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/endergrade.png");

    public RenderEndergrade(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelEndergrade(), 0.6F);
        this.addLayer(new LayerEndergradeSaddle(this));
    }

    protected void scale(EntityEndergrade entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }


    public ResourceLocation getTextureLocation(EntityEndergrade entity) {
        return TEXTURE;
    }
}
