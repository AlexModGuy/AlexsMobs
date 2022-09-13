package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurBody;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCockroachMaracas;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderMurmurBody extends MobRenderer<EntityMurmur, ModelMurmurBody> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/murmur.png");
    public static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/murmur_angry.png");

    public RenderMurmurBody(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelMurmurBody(), 0.5F);
    }

    protected void scale(EntityMurmur entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }


    public ResourceLocation getTextureLocation(EntityMurmur entity) {
        return TEXTURE;
    }
}
