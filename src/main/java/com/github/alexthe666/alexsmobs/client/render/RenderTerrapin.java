package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTerrapin;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderTerrapin extends MobRenderer<EntityTerrapin, ModelTerrapin> {

    private static final ResourceLocation[] SHELL_TEXTURES = {
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_0.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_1.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_2.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_3.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_4.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_5.png")
    };
    private static final ResourceLocation[] SKIN_PATTERN_TEXTURES = {
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_0.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_1.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_2.png"),
            new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_3.png")
    };

    public RenderTerrapin(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTerrapin(), 0.3F);
        this.addLayer(new TurtleOverlayLayer(this, 0));
        this.addLayer(new TurtleOverlayLayer(this, 1));
        this.addLayer(new TurtleOverlayLayer(this, 2));
    }

    protected void scale(EntityTerrapin entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }


    public ResourceLocation getTextureLocation(EntityTerrapin entity) {
        if(entity.isKoopa()){
            return TerrapinTypes.KOOPA.getTexture();
        }
        return entity.getTurtleType().getTexture();
    }

    protected void setupRotations(EntityTerrapin entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
        if (this.isShaking(entity)) {
            yawIn += (float)(Math.cos((double)entity.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }
        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING && !entity.isSpinning()) {
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
        }

        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.isAutoSpinAttack()) {
            stack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
            stack.mulPose(Axis.YP.rotationDegrees(((float)entity.tickCount + partialTickTime) * -75.0F));
        } else if (pose == Pose.SLEEPING) {
        } else if (isEntityUpsideDown(entity)) {
            stack.translate(0.0D, (double)(entity.getBbHeight() + 0.1F), 0.0D);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    class TurtleOverlayLayer extends RenderLayer<EntityTerrapin, ModelTerrapin> {

        private int layer;

        public TurtleOverlayLayer(RenderTerrapin render, int layer) {
            super(render);
            this.layer = layer;
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, EntityTerrapin turtle, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(turtle.getTurtleType() == TerrapinTypes.OVERLAY && !turtle.isKoopa()){
                ResourceLocation tex = layer == 0 ? this.getTextureLocation(turtle) : layer == 1 ? SHELL_TEXTURES[turtle.getShellType() % SHELL_TEXTURES.length] : SKIN_PATTERN_TEXTURES[turtle.getSkinType() % SKIN_PATTERN_TEXTURES.length];
                int color = layer == 0 ? turtle.getTurtleColor() : layer == 1 ? turtle.getShellColor() : turtle.getSkinColor();
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                renderColoredCutoutModel(getParentModel(), tex, matrixStackIn, buffer, packedLightIn, turtle, r, g, b);
            }
        }
    }

}
