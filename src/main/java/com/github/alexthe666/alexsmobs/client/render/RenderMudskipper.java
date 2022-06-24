package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMudskipper;
import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderMudskipper extends MobRenderer<EntityMudskipper, ModelMudskipper> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mudskipper.png");
    private static final ResourceLocation TEXTURE_SPIT = new ResourceLocation("alexsmobs:textures/entity/mudskipper_spit.png");

    public RenderMudskipper(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelMudskipper(), 0.25F);
    }


    protected void scale(EntityMudskipper rabbit, PoseStack matrixStackIn, float partialTickTime) {

    }

    public ResourceLocation getTextureLocation(EntityMudskipper entity) {
        return entity.isMouthOpen() ? TEXTURE_SPIT : TEXTURE;
    }
}
