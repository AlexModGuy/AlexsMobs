package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelGorilla;
import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGorillaItem;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGrizzlyItem;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderGorilla extends MobRenderer<EntityGorilla, ModelGorilla> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/gorilla.png");

    public RenderGorilla(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelGorilla(), 0.7F);
        this.addLayer(new LayerGorillaItem(this));
    }

    protected void preRenderCallback(EntityGorilla entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(entitylivingbaseIn.getGorillaScale(), entitylivingbaseIn.getGorillaScale(), entitylivingbaseIn.getGorillaScale());
    }


    public ResourceLocation getEntityTexture(EntityGorilla entity) {
        return TEXTURE;
    }
}
