package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrimsonMosquitoBlood;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCrimsonMosquito extends MobRenderer<EntityCrimsonMosquito, ModelCrimsonMosquito> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito.png");
    private static final ResourceLocation TEXTURE_SICK = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_blue.png");

    public RenderCrimsonMosquito(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCrimsonMosquito(), 0.6F);
        this.addLayer(new LayerCrimsonMosquitoBlood(this));
    }

    protected void preRenderCallback(EntityCrimsonMosquito entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float mosScale = entitylivingbaseIn.prevMosquitoScale + (entitylivingbaseIn.getMosquitoScale() - entitylivingbaseIn.prevMosquitoScale) * partialTickTime;
        matrixStackIn.scale(mosScale * 1.2F, mosScale * 1.2F, mosScale * 1.2F);
    }

    protected boolean func_230495_a_(EntityCrimsonMosquito fly) {
        return fly.isSick();
    }

    protected void applyRotations(EntityCrimsonMosquito entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.func_230495_a_(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.ticksExisted * 7F) * Math.PI * (double)0.9F);
            float vibrate = 0.05F * entityLiving.getMosquitoScale();
            matrixStackIn.translate((entityLiving.getRNG().nextFloat() - 0.5F)* vibrate, (entityLiving.getRNG().nextFloat() - 0.5F) * vibrate, (entityLiving.getRNG().nextFloat() - 0.5F)* vibrate);
        }
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    public ResourceLocation getEntityTexture(EntityCrimsonMosquito entity) {
        return entity.isSick() ? TEXTURE_SICK : TEXTURE;
    }
}
