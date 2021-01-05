package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.RenderElephant;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.LlamaModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

public class LayerElephantOverlays extends LayerRenderer<EntityElephant, ModelElephant> {

    private static final ResourceLocation[] ELEPHANT_DECOR_TEXTURES = new ResourceLocation[]{new ResourceLocation("alexsmobs:textures/entity/elephant/decor/white.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/orange.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/magenta.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/light_blue.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/yellow.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/lime.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/pink.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/gray.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/light_gray.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/cyan.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/purple.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/blue.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/brown.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/green.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/red.png"), new ResourceLocation("alexsmobs:textures/entity/elephant/decor/black.png")};
    private static final ResourceLocation TRADER_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/elephant/decor/trader.png");

    private static final ResourceLocation TEXTURE_CHEST = new ResourceLocation("alexsmobs:textures/entity/elephant/elephant_chest.png");
    private final ModelElephant model = new ModelElephant(0.5F);

    public LayerElephantOverlays(RenderElephant renderElephant) {
        super(renderElephant);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityElephant elephant, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(elephant.isChested()){
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutout(TEXTURE_CHEST));
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(elephant, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
        DyeColor lvt_11_1_ = elephant.getColor();
        if(lvt_11_1_ != null || elephant.isTrader()) {
            ResourceLocation lvt_12_3_;
            if (lvt_11_1_ != null) {
                lvt_12_3_ = ELEPHANT_DECOR_TEXTURES[lvt_11_1_.getId()];
            } else {
                if (!elephant.isTrader()) {
                    return;
                }

                lvt_12_3_ = TRADER_TEXTURE;
            }
            ((ModelElephant) this.getEntityModel()).copyModelAttributesTo(this.model);
            this.model.setRotationAngles(elephant, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            IVertexBuilder lvt_13_1_ = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(lvt_12_3_));
            this.model.render(matrixStackIn, lvt_13_1_, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
