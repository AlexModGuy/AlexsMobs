package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeal;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerSealItem;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class RenderSeal extends MobRenderer<EntitySeal, ModelSeal> {
    private static final ResourceLocation TEXTURE_BROWN_0 = new ResourceLocation("alexsmobs:textures/entity/seal/seal_brown_0.png");
    private static final ResourceLocation TEXTURE_BROWN_1 = new ResourceLocation("alexsmobs:textures/entity/seal/seal_brown_1.png");
    private static final ResourceLocation TEXTURE_ARCTIC_0 = new ResourceLocation("alexsmobs:textures/entity/seal/seal_arctic_0.png");
    private static final ResourceLocation TEXTURE_ARCTIC_1 = new ResourceLocation("alexsmobs:textures/entity/seal/seal_arctic_1.png");
    private static final ResourceLocation TEXTURE_ARCTIC_BABY = new ResourceLocation("alexsmobs:textures/entity/seal/seal_arctic_baby.png");
    private static final ResourceLocation TEXTURE_TEARS = new ResourceLocation("alexsmobs:textures/entity/seal/seal_crying.png");
    private static final ResourceLocation TEXTURE_TONGUE = new ResourceLocation("alexsmobs:textures/entity/seal/seal_tongue.png");

    public RenderSeal(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSeal(), 0.45F);
        this.addLayer(new LayerSealItem(this));
        this.addLayer(new SealTearsLayer(this));
    }

    protected boolean shouldShowName(EntitySeal seal) {
        return super.shouldShowName(seal) || seal.isTearsEasterEgg();
    }

    public ResourceLocation getTextureLocation(EntitySeal entity) {
        if(entity.isArctic()){
            return entity.isBaby() ? TEXTURE_ARCTIC_BABY : entity.getVariant() == 1 ? TEXTURE_ARCTIC_1 : TEXTURE_ARCTIC_0;
        }
        return entity.getVariant() == 1 ? TEXTURE_BROWN_1 : TEXTURE_BROWN_0;
    }

    @Override
    protected void renderNameTag(EntitySeal seal, Component text, PoseStack poseStack, MultiBufferSource bufferSrc, int numberIn) {
        if(seal.isTearsEasterEgg()){
            double d0 = this.entityRenderDispatcher.distanceToSqr(seal);
            if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(seal, d0)) {
                boolean flag = !seal.isDiscrete();
                float f = seal.getBbHeight() + 0.5F;
                String[] split = text.getString(512).split(" ");
                StringBuilder recombined = new StringBuilder();
                List<String> strings = new ArrayList<>();
                for(int wordIndex = 0; wordIndex < split.length; wordIndex++){
                    recombined.append(split[wordIndex]).append(" ");
                    if(recombined.length() > 15 || wordIndex == split.length - 1){
                        strings.add(recombined.toString());
                        recombined = new StringBuilder();
                    }
                }
                int i = 10 - 10 * strings.size();

                poseStack.pushPose();
                poseStack.translate(0.0D, (double)f, 0.0D);
                poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                poseStack.scale(-0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = poseStack.last().pose();
                float f1 = 1F;//Minecraft.getInstance().options.getBackgroundOpacity(1.25F);
                int j = 0XFFFFFFFF;
                Font font = this.getFont();
                String widest = "";
                for(String print : strings) {
                    if(font.width(widest) < font.width(print)){
                        widest = print;
                    }
                }
                float widestCenter = (float)(-font.width(widest) / 2);
                for(String print : strings){
                    float f2 = (float)(-font.width(print) / 2);
                    poseStack.translate(0.0D, 0.0D, 0.1D);
                    font.drawInBatch(widest, widestCenter, (float)i, j, false, matrix4f, bufferSrc, Font.DisplayMode.NORMAL, j, 240);
                    poseStack.translate(0.0D, 0.0D, -0.1D);
                    font.drawInBatch(print, f2, (float)i, 1, false, matrix4f, bufferSrc, Font.DisplayMode.NORMAL, j, 240);
                    font.drawInBatch(print, f2, (float)i, 0, false, matrix4f, bufferSrc, Font.DisplayMode.NORMAL, j, 240);
                    i += 10;
                }

                poseStack.popPose();
            }
        }else{
            super.renderNameTag(seal, text, poseStack, bufferSrc, numberIn);
        }
    }

    static class SealTearsLayer extends RenderLayer<EntitySeal, ModelSeal> {

        public SealTearsLayer(RenderSeal p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntitySeal entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(entitylivingbaseIn.isTearsEasterEgg()){
                VertexConsumer lead = bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_TEARS));
                this.getParentModel().renderToBuffer(matrixStackIn, lead, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
