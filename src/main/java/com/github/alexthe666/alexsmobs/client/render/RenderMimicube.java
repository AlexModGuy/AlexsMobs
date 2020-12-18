package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerMimicubeHeldItem;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerMimicubeHelmet;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerMimicubeTexture;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderMimicube extends MobRenderer<EntityMimicube, ModelMimicube> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mimicube.png");

    public RenderMimicube(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelMimicube(), 0.5F);
        this.addLayer(new LayerMimicubeHelmet(this));
        this.addLayer(new LayerMimicubeHeldItem(this));
        this.addLayer(new LayerMimicubeTexture(this));
    }

    protected void preRenderCallback(EntityMimicube entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getEntityTexture(EntityMimicube entity) {
        return TEXTURE;
    }
}
