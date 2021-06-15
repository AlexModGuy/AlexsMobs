package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFrilledShark;
import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderFrilledShark extends MobRenderer<EntityFrilledShark, ModelFrilledShark> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/frilled_shark.png");
    private static final ResourceLocation TEXTURE_DEPRESSURIZED = new ResourceLocation("alexsmobs:textures/entity/frilled_shark_depressurized.png");
    private static final ResourceLocation TEXTURE_KAIJU = new ResourceLocation("alexsmobs:textures/entity/frilled_shark_kaiju.png");
    private static final ResourceLocation TEXTURE_KAIJU_DEPRESSURIZED = new ResourceLocation("alexsmobs:textures/entity/frilled_shark_kaiju_depressurized.png");
    private static final ResourceLocation TEXTURE_TEETH = new ResourceLocation("alexsmobs:textures/entity/frilled_shark_teeth.png");

    public RenderFrilledShark(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelFrilledShark(), 0.4F);
        this.addLayer(new TeethLayer(this));
    }

    protected void preRenderCallback(EntityFrilledShark entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }


    public ResourceLocation getEntityTexture(EntityFrilledShark entity) {
        return TEXTURE;
    }

    class TeethLayer extends LayerRenderer<EntityFrilledShark, ModelFrilledShark> {


        public TeethLayer(RenderFrilledShark render) {
            super(render);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, EntityFrilledShark entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder glintBuilder = buffer.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE_TEETH, 240));
            this.getEntityModel().render(matrixStackIn, glintBuilder, 240, NO_OVERLAY, 1, 1, 1, 1);

        }
    }
}
