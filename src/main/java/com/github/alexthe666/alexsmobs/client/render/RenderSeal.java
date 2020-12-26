package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.client.model.ModelSeal;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerSealItem;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderSeal extends MobRenderer<EntitySeal, ModelSeal> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/seal.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/seal_arctic.png");

    public RenderSeal(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelSeal(), 0.45F);
        this.addLayer(new LayerSealItem(this));
    }

    protected void preRenderCallback(EntitySeal entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }

    public ResourceLocation getEntityTexture(EntitySeal entity) {
        return entity.isArctic() ? TEXTURE_1 : TEXTURE_0;
    }
}
