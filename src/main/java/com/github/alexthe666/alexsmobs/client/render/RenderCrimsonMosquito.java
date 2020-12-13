package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBoneSerpentHead;
import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrimsonMosquitoBlood;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerGrizzlyHoney;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCrimsonMosquito extends MobRenderer<EntityCrimsonMosquito, ModelCrimsonMosquito> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito.png");

    public RenderCrimsonMosquito(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCrimsonMosquito(), 0.6F);
        this.addLayer(new LayerCrimsonMosquitoBlood(this));
    }

    protected void preRenderCallback(EntityCrimsonMosquito entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float mosScale = entitylivingbaseIn.prevMosquitoScale + (entitylivingbaseIn.getMosquitoScale() - entitylivingbaseIn.prevMosquitoScale) * partialTickTime;
        matrixStackIn.scale(mosScale * 1.2F, mosScale * 1.2F, mosScale * 1.2F);
    }


    public ResourceLocation getEntityTexture(EntityCrimsonMosquito entity) {
        return TEXTURE;
    }
}
