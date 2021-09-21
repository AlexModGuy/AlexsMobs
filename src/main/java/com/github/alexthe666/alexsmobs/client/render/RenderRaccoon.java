package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRaccoonEyes;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRaccoonItem;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.resources.ResourceLocation;

public class RenderRaccoon extends MobRenderer<EntityRaccoon, ModelRaccoon> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/raccoon.png");
    private static final ResourceLocation TEXTURE_BANDANA = new ResourceLocation("alexsmobs:textures/entity/raccoon_bandana.png");

    public RenderRaccoon(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelRaccoon(), 0.4F);
        this.addLayer(new LayerRaccoonEyes(this));
        this.addLayer(new LayerRaccoonItem(this));
        this.addLayer(new BandanaLayer(this));
    }

    protected void scale(EntityRaccoon entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);
    }


    public ResourceLocation getTextureLocation(EntityRaccoon entity) {
        return TEXTURE;
    }

    private class BandanaLayer extends RenderLayer<EntityRaccoon, ModelRaccoon> {
        public BandanaLayer(RenderRaccoon renderRaccoon) {
            super(renderRaccoon);
        }

        public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, EntityRaccoon raccoon, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            if (raccoon.getColor() != null && !raccoon.isInvisible()) {
                float lvt_11_2_;
                float lvt_12_2_;
                float lvt_13_2_;
                if (raccoon.hasCustomName() && "jeb_".equals(raccoon.getName().getContents())) {
                    int lvt_15_1_ = raccoon.tickCount / 25 + raccoon.getId();
                    int lvt_16_1_ = DyeColor.values().length;
                    int lvt_17_1_ = lvt_15_1_ % lvt_16_1_;
                    int lvt_18_1_ = (lvt_15_1_ + 1) % lvt_16_1_;
                    float lvt_19_1_ = ((float)(raccoon.tickCount % 25) + p_225628_7_) / 25.0F;
                    float[] lvt_20_1_ = Sheep.getColorArray(DyeColor.byId(lvt_17_1_));
                    float[] lvt_21_1_ = Sheep.getColorArray(DyeColor.byId(lvt_18_1_));
                    lvt_11_2_ = lvt_20_1_[0] * (1.0F - lvt_19_1_) + lvt_21_1_[0] * lvt_19_1_;
                    lvt_12_2_ = lvt_20_1_[1] * (1.0F - lvt_19_1_) + lvt_21_1_[1] * lvt_19_1_;
                    lvt_13_2_ = lvt_20_1_[2] * (1.0F - lvt_19_1_) + lvt_21_1_[2] * lvt_19_1_;
                } else {
                    float[] lvt_14_2_ = Sheep.getColorArray(raccoon.getColor());
                    lvt_11_2_ = lvt_14_2_[0];
                    lvt_12_2_ = lvt_14_2_[1];
                    lvt_13_2_ = lvt_14_2_[2];
                }
                this.getParentModel().renderToBuffer(p_225628_1_, p_225628_2_.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_BANDANA)), p_225628_3_, OverlayTexture.NO_OVERLAY, lvt_11_2_, lvt_12_2_, lvt_13_2_, 1.0F);
            }
        }
    }
}
