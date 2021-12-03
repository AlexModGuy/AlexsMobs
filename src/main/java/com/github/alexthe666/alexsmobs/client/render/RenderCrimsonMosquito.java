package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrimsonMosquitoBlood;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderCrimsonMosquito extends MobRenderer<EntityCrimsonMosquito, ModelCrimsonMosquito> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito.png");
    private static final ResourceLocation TEXTURE_SICK = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_blue.png");
    private static final ResourceLocation TEXTURE_FLY = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_fly.png");
    private static final ResourceLocation TEXTURE_SICK_FLY = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_fly_blue.png");

    public RenderCrimsonMosquito(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCrimsonMosquito(), 0.6F);
        this.addLayer(new LayerCrimsonMosquitoBlood(this));
    }

    protected void scale(EntityCrimsonMosquito entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float mosScale = entitylivingbaseIn.prevMosquitoScale + (entitylivingbaseIn.getMosquitoScale() - entitylivingbaseIn.prevMosquitoScale) * partialTickTime;
        matrixStackIn.scale(mosScale * 1.2F, mosScale * 1.2F, mosScale * 1.2F);
    }

    protected boolean isShaking(EntityCrimsonMosquito fly) {
        return fly.isSick();
    }

    protected void setupRotations(EntityCrimsonMosquito entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.tickCount * 7F) * Math.PI * (double)0.9F);
            float vibrate = 0.05F * entityLiving.getMosquitoScale();
            matrixStackIn.translate((entityLiving.getRandom().nextFloat() - 0.5F)* vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F)* vibrate);
        }
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    public ResourceLocation getTextureLocation(EntityCrimsonMosquito entity) {
        if (entity.isSick()) {
            return entity.isFromFly() ? TEXTURE_SICK_FLY : TEXTURE_SICK;
        }
        return entity.isFromFly() ? TEXTURE_FLY : TEXTURE;
    }
}
