package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelWarpedToad extends AdvancedEntityModel<EntityWarpedToad> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tongue;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox right_arm;
    private final AdvancedModelBox sac;
    private final AdvancedModelBox left_gland;
    private final AdvancedModelBox right_gland;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox left_foot_pivot;
    private final AdvancedModelBox left_foot;
    private final AdvancedModelBox right_leg;
    private final AdvancedModelBox right_foot;
    private final AdvancedModelBox right_foot_pivot;
    private final AdvancedModelBox head;
    private final AdvancedModelBox left_eye;
    private final AdvancedModelBox right_eye;

    public ModelWarpedToad() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -8.0F, 1.0F);
        root.addChild(body);
        body.setTextureOffset(0, 24).addBox(-7.0F, -2.0F, -12.0F, 14.0F, 7.0F, 11.0F, -0.001F, false);
        body.setTextureOffset(0, 43).addBox(-7.0F, 0.0F, -12.0F, 14.0F, 0.0F, 11.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-7.0F, -6.0F, -1.0F, 14.0F, 11.0F, 12.0F, -0.001F, false);

        tongue = new AdvancedModelBox(this);
        tongue.setPos(0.0F, -1.0F, -1.0F);
        body.addChild(tongue);
        tongue.setTextureOffset(0, 55).addBox(-4.0F, 0.0F, -10.0F, 8.0F, 0.0F, 10.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this);
        left_arm.setPos(6.4F, 2.0F, -4.0F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(50, 69).addBox(-1.0F, -1.01F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);
        left_arm.setTextureOffset(27, 55).addBox(-4.0F, 6.0F, -3.0F, 6.0F, 0.0F, 5.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this);
        right_arm.setPos(-6.4F, 2.0F, -4.0F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(50, 69).addBox(-2.0F, -1.01F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, true);
        right_arm.setTextureOffset(27, 55).addBox(-2.0F, 6.0F, -3.0F, 6.0F, 0.0F, 5.0F, 0.0F, true);

        sac = new AdvancedModelBox(this);
        sac.setPos(0.0F, 5.0F, -1.0F);
        body.addChild(sac);
        sac.setTextureOffset(42, 13).addBox(-7.0F, -5.0F, -11.0F, 14.0F, 5.0F, 11.0F, -0.1F, false);

        left_gland = new AdvancedModelBox(this);
        left_gland.setPos(5.0F, -5.0F, 3.1F);
        body.addChild(left_gland);
        left_gland.setTextureOffset(0, 66).addBox(-2.0F, -2.0F, -4.0F, 5.0F, 4.0F, 8.0F, 0.0F, false);

        right_gland = new AdvancedModelBox(this);
        right_gland.setPos(-5.0F, -5.0F, 3.1F);
        body.addChild(right_gland);
        right_gland.setTextureOffset(0, 66).addBox(-3.0F, -2.0F, -4.0F, 5.0F, 4.0F, 8.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this);
        left_leg.setPos(6.0F, 1.5F, 8.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(40, 50).addBox(-4.0F, -1.0F, -6.0F, 6.0F, 7.0F, 11.0F, 0.0F, false);

        left_foot_pivot = new AdvancedModelBox(this);
        left_foot_pivot.setPos(0.0F, 5.5F, -3.0F);
        left_leg.addChild(left_foot_pivot);

        left_foot = new AdvancedModelBox(this);
        left_foot_pivot.addChild(left_foot);
        left_foot.setTextureOffset(64, 50).addBox(-2.0F, 0.0F, -2.0F, 10.0F, 1.0F, 4.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this);
        right_leg.setPos(-6.0F, 1.5F, 8.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(40, 50).addBox(-2.0F, -1.0F, -6.0F, 6.0F, 7.0F, 11.0F, 0.0F, true);

        right_foot_pivot = new AdvancedModelBox(this);
        right_foot_pivot.setPos(0.0F, 5.5F, -3.0F);
        right_leg.addChild(right_foot_pivot);

        right_foot = new AdvancedModelBox(this);
        right_foot_pivot.addChild(right_foot);
        right_foot.setTextureOffset(64, 50).addBox(-8.0F, 0.0F, -2.0F, 10.0F, 1.0F, 4.0F, 0.0F, true);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, -2.0F, -3.0F);
        body.addChild(head);
        head.setTextureOffset(40, 32).addBox(-7.0F, -4.0F, -9.0F, 14.0F, 6.0F, 11.0F, 0.0F, false);
        head.setTextureOffset(41, 0).addBox(-7.0F, -1.0F, -9.0F, 14.0F, 0.0F, 11.0F, 0.0F, false);

        left_eye = new AdvancedModelBox(this);
        left_eye.setPos(7.0F, -4.0F, -6.0F);
        head.addChild(left_eye);
        left_eye.setTextureOffset(27, 69).addBox(-5.0F, -2.0F, 0.0F, 5.0F, 2.0F, 6.0F, 0.0F, false);
        left_eye.setTextureOffset(19, 66).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

        right_eye = new AdvancedModelBox(this);
        right_eye.setPos(-7.0F, -4.0F, -6.0F);
        head.addChild(right_eye);
        right_eye.setTextureOffset(27, 69).addBox(0.0F, -2.0F, 0.0F, 5.0F, 2.0F, 6.0F, 0.0F, true);
        right_eye.setTextureOffset(19, 66).addBox(0.0F, -4.0F, 0.0F, 2.0F, 2.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, right_eye, left_eye, sac, right_arm, right_leg, right_foot, right_gland, left_arm, left_leg, left_foot, left_gland, tongue, right_foot_pivot, left_foot_pivot);
    }

    @Override
    public void setupAnim(EntityWarpedToad entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = Minecraft.getInstance().getFrameTime();
        float attackProgress = entity.prevAttackProgress + (entity.attackProgress - entity.prevAttackProgress) * partialTick;
        float walkSpeed = 1.2f;
        float walkDegree = 0.5f;
        float swimSpeed = 0.4f;
        float swimDegree = 0.9f;
        float blinkProgress = Math.max(attackProgress, entity.prevBlinkProgress + (entity.blinkProgress - entity.prevBlinkProgress) * partialTick);
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
        float swimProgress = entity.prevSwimProgress + (entity.swimProgress - entity.prevSwimProgress) * partialTick;
        float fallProgress = entity.prevReboundProgress + (entity.reboundProgress - entity.prevReboundProgress) * partialTick;
        float jumpProgress = Math.max(0, entity.prevJumpProgress + (entity.jumpProgress - entity.prevJumpProgress) * partialTick - fallProgress);
        float glowyBob = 0.75F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.125F + attackProgress * 0.1F;
        float toungeScale = 1F + attackProgress * 0.285F * entity.getTongueLength();
        float toungeScaleCorners = 1F - (attackProgress * 0.1F);
        right_gland.setScale(glowyBob, glowyBob, glowyBob);
        left_gland.setScale(glowyBob, glowyBob, glowyBob);
        tongue.setScale(toungeScaleCorners, toungeScaleCorners, toungeScale);
        progressPositionPrev(right_eye, blinkProgress, 0.1F, 1.5F, 0, 5f);
        progressPositionPrev(left_eye, blinkProgress, -0.1F, 1.5F, 0, 5f);
        progressRotationPrev(head, attackProgress, (float) Math.toRadians(-45), 0, 0, 5f);
        progressRotationPrev(tongue, attackProgress, (float) Math.toRadians(-5), 0, 0, 5f);
        progressPositionPrev(head, attackProgress, 0, 0, -1.3F, 5f);

        progressPositionPrev(left_arm, sitProgress, 0, 1, 0F, 5f);
        progressPositionPrev(right_arm, sitProgress, 0, 1, 0F, 5f);
        progressPositionPrev(left_leg, sitProgress, 0, -2, 0F, 5f);
        progressPositionPrev(right_leg, sitProgress, 0, -2, 0F, 5f);
        progressRotationPrev(body, sitProgress, (float) Math.toRadians(-15), 0, 0, 5f);
        progressRotationPrev(left_arm, sitProgress, (float) Math.toRadians(15), 0, 0, 5f);
        progressRotationPrev(right_arm, sitProgress, (float) Math.toRadians(15), 0, 0, 5f);
        progressRotationPrev(left_leg, sitProgress, (float) Math.toRadians(15), 0, 0, 5f);
        progressRotationPrev(right_leg, sitProgress, (float) Math.toRadians(15), 0, 0, 5f);

        progressRotationPrev(left_leg, swimProgress, (float) Math.toRadians(90), (float) Math.toRadians(-20), 0, 5f);
        progressRotationPrev(right_leg, swimProgress, (float) Math.toRadians(90), (float) Math.toRadians(20), 0, 5f);
        progressPositionPrev(left_leg, swimProgress, 3F, -1F, 1F, 5f);
        progressPositionPrev(right_leg, swimProgress, -3F, -1F, 1F, 5f);
        progressRotationPrev(left_arm, swimProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-60), (float) Math.toRadians(30), 5f);
        progressRotationPrev(right_arm, swimProgress, (float) Math.toRadians(-90), (float) Math.toRadians(60), (float) Math.toRadians(-30), 5f);
        progressPositionPrev(left_arm, swimProgress, 0, 0F, 1, 5f);
        progressPositionPrev(right_arm, swimProgress, 0, 0F, 1, 5f);
        progressPositionPrev(body, swimProgress, 0, 1F, 0F, 5f);

        float walkAmount = (5F - swimProgress) * 0.2F - (Math.max(jumpProgress, fallProgress) * 0.2F);
        float walkSwingAmount = limbSwingAmount * walkAmount;
        float swimSwingAmount = limbSwingAmount * 0.2F * swimProgress;
        this.swing(right_leg, swimSpeed, swimDegree, false, -2.5F, -0.3F, limbSwing, swimSwingAmount);
        this.swing(left_leg, swimSpeed, swimDegree, true, -2.5F, -0.3F, limbSwing, swimSwingAmount);
        this.flap(right_foot, swimSpeed, swimDegree, false, -1, -0.3F, limbSwing, swimSwingAmount);
        this.flap(left_foot, swimSpeed, swimDegree, true, -1, -0.3F, limbSwing, swimSwingAmount);
        this.flap(right_arm, swimSpeed, swimDegree, false, -2.5F, -0.1F, limbSwing, swimSwingAmount);
        this.flap(left_arm, swimSpeed, swimDegree, true, -2.5F, -0.1F, limbSwing, swimSwingAmount);
        this.swing(right_arm, swimSpeed, swimDegree, false, -2.5F, 0.3F, limbSwing, swimSwingAmount);
        this.swing(left_arm, swimSpeed, swimDegree, true, -2.5F, 0.3F, limbSwing, swimSwingAmount);
        this.swing(body, swimSpeed, swimDegree * 0.1F, false, 0, 0F, limbSwing, swimSwingAmount);

        progressRotationPrev(left_foot, Math.min(walkSwingAmount, 0.5F), 0, (float) Math.toRadians(80), 0, 0.5F);
        progressRotationPrev(right_foot, Math.min(walkSwingAmount, 0.5F), 0, (float) Math.toRadians(-80), 0, 0.5F);
        this.flap(body, walkSpeed, walkDegree * 0.35F, false, 0F, 0F, limbSwing, walkSwingAmount);
        this.swing(body, walkSpeed, walkDegree * 0.35F, false, 1F, 0F, limbSwing, walkSwingAmount);
        this.walk(left_arm, walkSpeed, walkDegree, false, -2.5F, -0.3F, limbSwing, walkSwingAmount);
        this.walk(right_arm, walkSpeed, walkDegree, true, -2.5F, 0.3F, limbSwing, walkSwingAmount);
        this.walk(right_leg, walkSpeed, walkDegree, false, -2.5F, 0.1F, limbSwing, walkSwingAmount);
        this.walk(left_leg, walkSpeed, walkDegree, true, -2.5F, -0.1F, limbSwing, walkSwingAmount);

        this.left_foot_pivot.rotateAngleX -= walkAmount * (left_leg.rotateAngleX + body.rotateAngleX);
        this.left_foot_pivot.rotateAngleZ -= walkAmount * body.rotateAngleZ;
        this.right_foot_pivot.rotateAngleX -= walkAmount * (right_leg.rotateAngleX + body.rotateAngleX);
        this.right_foot_pivot.rotateAngleZ -= walkAmount * body.rotateAngleZ;
        this.left_arm.rotationPointZ += 1.5F * (float) (Math.sin((double) (limbSwing * walkSpeed) - 2.5F) * (double) walkSwingAmount * (double) walkDegree - (double) (walkSwingAmount * walkDegree));
        this.left_arm.rotationPointY += 0.5F * (float) (Math.sin((double) (limbSwing * walkSpeed) - 2.5F) * (double) walkSwingAmount * (double) walkDegree - (double) (walkSwingAmount * walkDegree));
        this.right_arm.rotationPointZ += 1.5F * (float) (Math.sin(-(double) (limbSwing * walkSpeed) + 2.5F) * (double) walkSwingAmount * (double) walkDegree - (double) (walkSwingAmount * walkDegree));
        this.right_arm.rotationPointY += 0.5F * (float) (Math.sin(-(double) (limbSwing * walkSpeed) + 2.5F) * (double) walkSwingAmount * (double) walkDegree - (double) (walkSwingAmount * walkDegree));
        float leftLegS = (float) (Math.sin((double) (limbSwing * walkSpeed) - 2.5F) * (double) walkSwingAmount * (double) walkDegree - (double) (walkSwingAmount * walkDegree));
        float rightLegs = (float) (Math.sin(-(double) (limbSwing * walkSpeed) + 2.5F) * (double) walkSwingAmount * (double) walkDegree - (double) (walkSwingAmount * walkDegree));
        this.left_leg.rotationPointY += 1.5F * leftLegS;
        this.right_leg.rotationPointY += 1.5F * rightLegs;
        this.left_leg.rotationPointZ -= 3 * leftLegS;
        this.right_leg.rotationPointZ -= 3 * rightLegs;
        this.left_foot_pivot.rotationPointZ -= 1.5F * leftLegS;
        this.right_foot_pivot.rotationPointZ -= 1.5F * rightLegs;

        if (attackProgress > 0) {
            this.tongue.rotateAngleX += headPitch * ((float) Math.PI / 180F);
        }
        progressRotationPrev(body, fallProgress, (float) Math.toRadians(15), 0, 0, 5f);
        progressRotationPrev(left_arm, fallProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(right_arm, fallProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(left_leg, fallProgress, (float) Math.toRadians(35), 0, 0, 5f);
        progressRotationPrev(right_leg, fallProgress, (float) Math.toRadians(15), 0, 0, 5f);
        progressRotationPrev(left_foot_pivot, fallProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(right_foot_pivot, fallProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(left_foot, fallProgress, 0, (float) Math.toRadians(70), 0, 5F);
        progressRotationPrev(right_foot, fallProgress, 0, (float) Math.toRadians(-70), 0, 5F);
        progressPositionPrev(left_foot_pivot, fallProgress, 0, 1, -1, 5f);
        progressPositionPrev(right_foot_pivot, fallProgress, 0, 1, -1, 5f);
        progressRotationPrev(body, jumpProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(left_arm, jumpProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(right_arm, jumpProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(left_leg, jumpProgress, (float) Math.toRadians(150F), 0, 0, 5f);
        progressRotationPrev(right_leg, jumpProgress, (float) Math.toRadians(150F), 0, 0, 5f);
        progressRotationPrev(left_foot_pivot, fallProgress, (float) Math.toRadians(35), 0, 0, 5f);
        progressRotationPrev(right_foot_pivot, fallProgress, (float) Math.toRadians(35), 0, 0, 5f);
        progressRotationPrev(left_foot, jumpProgress, (float) Math.toRadians(20F), (float) Math.toRadians(70), 0, 5F);
        progressRotationPrev(right_foot, jumpProgress, (float) Math.toRadians(20F), (float) Math.toRadians(-70), 0, 5F);
        progressPositionPrev(left_leg, jumpProgress, 0, 1, 2, 5f);
        progressPositionPrev(right_leg, jumpProgress, 0, 1, 2, 5f);
        progressPositionPrev(left_arm, jumpProgress, 0, 1, 0, 5f);
        progressPositionPrev(right_arm, jumpProgress, 0, 1, 0, 5f);

    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.75D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}