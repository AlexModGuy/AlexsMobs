package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerElephantItem;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerElephantOverlays;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderElephant extends MobRenderer<EntityElephant, ModelElephant> {
    private static final ResourceLocation TEXTURE_TUSK = new ResourceLocation("alexsmobs:textures/entity/elephant/elephant_tusks.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/elephant/elephant.png");

    public RenderElephant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelElephant(0), 0.8F);
        this.addLayer(new LayerElephantOverlays(this));
        this.addLayer(new LayerElephantItem(this));
    }

    protected void scale(EntityElephant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
       if(entitylivingbaseIn.isTusked()){
           matrixStackIn.scale(1.1F, 1.1F, 1.1F);
       }
    }


    public ResourceLocation getTextureLocation(EntityElephant entity) {
        return entity.isTusked() && !entity.isBaby() ? TEXTURE_TUSK : TEXTURE;
    }
}
