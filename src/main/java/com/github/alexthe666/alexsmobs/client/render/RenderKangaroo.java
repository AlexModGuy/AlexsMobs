package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerKangarooArmor;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerKangarooBaby;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerKangarooItem;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerMimicubeHelmet;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderKangaroo extends MobRenderer<EntityKangaroo, ModelKangaroo> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/kangaroo.png");

    public RenderKangaroo(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelKangaroo(), 0.5F);
        this.addLayer(new LayerKangarooItem(this));
        this.addLayer(new LayerKangarooArmor(this));
        this.addLayer(new LayerKangarooBaby(this));

    }

    public boolean shouldRender(EntityKangaroo kangaroo, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        if(kangaroo.isChild() && kangaroo.isPassenger() && kangaroo.getRidingEntity() instanceof EntityKangaroo){
            return false;
        }
        return super.shouldRender(kangaroo, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_);
    }

    protected void preRenderCallback(EntityKangaroo entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }

    public ResourceLocation getEntityTexture(EntityKangaroo entity) {
        return TEXTURE;
    }
}
