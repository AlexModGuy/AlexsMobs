package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFrilledShark;
import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderFrilledShark extends MobRenderer<EntityFrilledShark, ModelFrilledShark> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("alexsmobs:textures/entity/frilled_shark.png");
    private static final ResourceLocation TEXTURE_DEPRESSURIZED = ResourceLocation.parse("alexsmobs:textures/entity/frilled_shark_depressurized.png");
    private static final ResourceLocation TEXTURE_KAIJU = ResourceLocation.parse("alexsmobs:textures/entity/frilled_shark_kaiju.png");
    private static final ResourceLocation TEXTURE_KAIJU_DEPRESSURIZED = ResourceLocation.parse("alexsmobs:textures/entity/frilled_shark_kaiju_depressurized.png");
    private static final ResourceLocation TEXTURE_TEETH = ResourceLocation.parse("alexsmobs:textures/entity/frilled_shark_teeth.png");

    public RenderFrilledShark(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFrilledShark(), 0.4F);
        this.addLayer(new TeethLayer(this));
    }

    protected void scale(EntityFrilledShark entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    public ResourceLocation getTextureLocation(EntityFrilledShark entity) {
        return entity.isKaiju() ? (entity.isDepressurized() ? TEXTURE_KAIJU_DEPRESSURIZED : TEXTURE_KAIJU) : (entity.isDepressurized() ? TEXTURE_DEPRESSURIZED : TEXTURE);
    }

    static class TeethLayer extends RenderLayer<EntityFrilledShark, ModelFrilledShark> {


        public TeethLayer(RenderFrilledShark render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, EntityFrilledShark entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer glintBuilder = buffer.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE_TEETH, 240));
            this.getParentModel().renderToBuffer(matrixStackIn, glintBuilder, 240, NO_OVERLAY, -1);

        }
    }
}
