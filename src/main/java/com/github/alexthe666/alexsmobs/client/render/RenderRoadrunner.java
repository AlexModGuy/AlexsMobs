package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderRoadrunner extends MobRenderer<EntityRoadrunner, ModelRoadrunner> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/roadrunner.png");
    private static final ResourceLocation TEXTURE_MEEP = new ResourceLocation("alexsmobs:textures/entity/roadrunner_meep.png");

    public RenderRoadrunner(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelRoadrunner(), 0.3F);
    }

    public ResourceLocation getTextureLocation(EntityRoadrunner entity) {
        return entity.isMeep() ? TEXTURE_MEEP : TEXTURE;
    }
}
