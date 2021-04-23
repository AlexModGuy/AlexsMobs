package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class RenderBaldEagle extends MobRenderer<EntityBaldEagle, ModelBaldEagle> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/bald_eagle.png");
    private static final ResourceLocation TEXTURE_CAP = new ResourceLocation("alexsmobs:textures/entity/bald_eagle_hood.png");

    public RenderBaldEagle(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelBaldEagle(), 0.3F);
        this.addLayer(new CapLayer(this));
    }

    public boolean shouldRender(EntityBaldEagle baldEagle, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        if( baldEagle.isPassenger() && baldEagle.getRidingEntity() instanceof PlayerEntity && Minecraft.getInstance().player == baldEagle.getRidingEntity() && Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON){
            return false;
        }
        return super.shouldRender(baldEagle, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_);
    }

    protected void preRenderCallback(EntityBaldEagle eagle, MatrixStack matrixStackIn, float partialTickTime) {
        if(eagle.isPassenger() && eagle.getRidingEntity() != null) {
            if (eagle.getRidingEntity() instanceof PlayerEntity) {
                PlayerEntity mount = (PlayerEntity)eagle.getRidingEntity();
                boolean leftHand = false;
                if(mount.getHeldItem(Hand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE){
                    leftHand = mount.getPrimaryHand() == HandSide.LEFT;
                }else if(mount.getHeldItem(Hand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE){
                    leftHand = mount.getPrimaryHand() != HandSide.LEFT;
                }
                EntityRenderer playerRender = Minecraft.getInstance().getRenderManager().getRenderer(mount);
                if(Minecraft.getInstance().player == mount && Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON){
                    //handled via event
                }else if (playerRender instanceof LivingRenderer && ((LivingRenderer) playerRender).getEntityModel() instanceof BipedModel) {
                    if(leftHand){
                        matrixStackIn.translate(-0.3F, -0.7F, 0.5F);
                        ((BipedModel) ((LivingRenderer) playerRender).getEntityModel()).bipedLeftArm.translateRotate(matrixStackIn);
                        matrixStackIn.translate(-0.2F, 0.5F, -0.18F);
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(40F));
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(70F));
                    }else{
                        matrixStackIn.translate(0.3F, -0.7F, 0.5F);
                        ((BipedModel) ((LivingRenderer) playerRender).getEntityModel()).bipedRightArm.translateRotate(matrixStackIn);
                        matrixStackIn.translate(0.2F, 0.5F, -0.18F);
                        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(40F));
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-70F));
                    }
                }
            }
        }
    }


    public ResourceLocation getEntityTexture(EntityBaldEagle entity) {
        return TEXTURE;
    }

    class CapLayer extends LayerRenderer<EntityBaldEagle, ModelBaldEagle> {

        public CapLayer(RenderBaldEagle p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityBaldEagle entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.hasCap()) {
                IVertexBuilder lead = bufferIn.getBuffer(RenderType.getEntityTranslucent(TEXTURE_CAP));
                this.getEntityModel().render(matrixStackIn, lead, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
