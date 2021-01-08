package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerElephantItem;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerElephantOverlays;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderElephant extends MobRenderer<EntityElephant, ModelElephant> {
    private static final ResourceLocation TEXTURE_TUSK = new ResourceLocation("alexsmobs:textures/entity/elephant/elephant_tusks.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/elephant/elephant.png");

    public RenderElephant(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelElephant(0), 0.8F);
        this.addLayer(new LayerElephantOverlays(this));
        this.addLayer(new LayerElephantItem(this));
    }

    protected void preRenderCallback(EntityElephant entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
       if(entitylivingbaseIn.isTusked()){
           matrixStackIn.scale(1.1F, 1.1F, 1.1F);
       }
    }


    public ResourceLocation getEntityTexture(EntityElephant entity) {
        return entity.isTusked() && !entity.isChild() ? TEXTURE_TUSK : TEXTURE;
    }
}
