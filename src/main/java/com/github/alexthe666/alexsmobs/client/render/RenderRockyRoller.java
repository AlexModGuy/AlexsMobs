package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRockyRoller;
import com.github.alexthe666.alexsmobs.entity.EntityRockyRoller;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderRockyRoller extends MobRenderer<EntityRockyRoller, ModelRockyRoller> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/rocky_roller.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/rocky_roller_angry.png");
    private static final ResourceLocation TEXTURE_ROLLING = new ResourceLocation("alexsmobs:textures/entity/rocky_roller_rolling.png");

    public RenderRockyRoller(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelRockyRoller(), 0.7F);
    }

    protected void scale(EntityRockyRoller entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getTextureLocation(EntityRockyRoller entity) {
        return entity.isRolling() ? TEXTURE_ROLLING : entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
