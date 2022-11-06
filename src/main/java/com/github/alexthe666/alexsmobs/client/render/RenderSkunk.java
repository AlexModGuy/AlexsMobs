package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelAnteater;
import com.github.alexthe666.alexsmobs.client.model.ModelSkunk;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerAnteaterBaby;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerAnteaterTongueItem;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntitySkunk;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderSkunk extends MobRenderer<EntitySkunk, ModelSkunk> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/skunk.png");

    public RenderSkunk(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSkunk(), 0.45F);
    }

    public ResourceLocation getTextureLocation(EntitySkunk entity) {
        return TEXTURE;
    }
}
