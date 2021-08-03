package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRaccoonEyes;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRaccoonItem;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

public class RenderRaccoon extends MobRenderer<EntityRaccoon, ModelRaccoon> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/raccoon.png");
    private static final ResourceLocation TEXTURE_BANDANA = new ResourceLocation("alexsmobs:textures/entity/raccoon_bandana.png");

    public RenderRaccoon(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelRaccoon(), 0.4F);
        this.addLayer(new LayerRaccoonEyes(this));
        this.addLayer(new LayerRaccoonItem(this));
        this.addLayer(new BandanaLayer(this));
    }

    protected void preRenderCallback(EntityRaccoon entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);
    }


    public ResourceLocation getEntityTexture(EntityRaccoon entity) {
        return TEXTURE;
    }

    private class BandanaLayer extends LayerRenderer<EntityRaccoon, ModelRaccoon> {
        public BandanaLayer(RenderRaccoon renderRaccoon) {
            super(renderRaccoon);
        }

        public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, EntityRaccoon raccoon, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            if (raccoon.getColor() != null && !raccoon.isInvisible()) {
                float lvt_11_2_;
                float lvt_12_2_;
                float lvt_13_2_;
                if (raccoon.hasCustomName() && "jeb_".equals(raccoon.getName().getUnformattedComponentText())) {
                    int lvt_15_1_ = raccoon.ticksExisted / 25 + raccoon.getEntityId();
                    int lvt_16_1_ = DyeColor.values().length;
                    int lvt_17_1_ = lvt_15_1_ % lvt_16_1_;
                    int lvt_18_1_ = (lvt_15_1_ + 1) % lvt_16_1_;
                    float lvt_19_1_ = ((float)(raccoon.ticksExisted % 25) + p_225628_7_) / 25.0F;
                    float[] lvt_20_1_ = SheepEntity.getDyeRgb(DyeColor.byId(lvt_17_1_));
                    float[] lvt_21_1_ = SheepEntity.getDyeRgb(DyeColor.byId(lvt_18_1_));
                    lvt_11_2_ = lvt_20_1_[0] * (1.0F - lvt_19_1_) + lvt_21_1_[0] * lvt_19_1_;
                    lvt_12_2_ = lvt_20_1_[1] * (1.0F - lvt_19_1_) + lvt_21_1_[1] * lvt_19_1_;
                    lvt_13_2_ = lvt_20_1_[2] * (1.0F - lvt_19_1_) + lvt_21_1_[2] * lvt_19_1_;
                } else {
                    float[] lvt_14_2_ = SheepEntity.getDyeRgb(raccoon.getColor());
                    lvt_11_2_ = lvt_14_2_[0];
                    lvt_12_2_ = lvt_14_2_[1];
                    lvt_13_2_ = lvt_14_2_[2];
                }
                this.getEntityModel().render(p_225628_1_, p_225628_2_.getBuffer(AMRenderTypes.getEntityCutoutNoCull(TEXTURE_BANDANA)), p_225628_3_, OverlayTexture.NO_OVERLAY, lvt_11_2_, lvt_12_2_, lvt_13_2_, 1.0F);
            }
        }
    }
}
