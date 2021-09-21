package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelWarpedToad extends AdvancedEntityModel<EntityWarpedToad> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox body_r1;
    private final AdvancedModelBox glowy_glands_left;
    private final AdvancedModelBox glowy_glands_left_r1;
    private final AdvancedModelBox glowy_glands_right;
    private final AdvancedModelBox glowy_glands_right_r1;
    private final AdvancedModelBox head;
    private final AdvancedModelBox eye_left;
    private final AdvancedModelBox eye_right;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox tongue;
    private final AdvancedModelBox leg_left;
    private final AdvancedModelBox foot_left;
    private final AdvancedModelBox leg_right;
    private final AdvancedModelBox foot_right;
    private final AdvancedModelBox arm_left;
    private final AdvancedModelBox arm_right;

    public ModelWarpedToad() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -8.0F, 2.0F);
        root.addChild(body);


        body_r1 = new AdvancedModelBox(this);
        body_r1.setPos(0.0F, -1.0F, -1.0F);
        body.addChild(body_r1);
        setRotationAngle(body_r1, -0.4363F, 0.0F, 0.0F);
        body_r1.setTextureOffset(0, 0).addBox(-7.0F, -5.0F, -8.0F, 14.0F, 10.0F, 16.0F, 0.0F, false);

        glowy_glands_left = new AdvancedModelBox(this);
        glowy_glands_left.setPos(5.5F, -8.0F, -3.5F);
        body.addChild(glowy_glands_left);


        glowy_glands_left_r1 = new AdvancedModelBox(this);
        glowy_glands_left_r1.setPos(0.0F, 0.8921F, 0.0239F);
        glowy_glands_left.addChild(glowy_glands_left_r1);
        setRotationAngle(glowy_glands_left_r1, -0.4363F, 0.0F, 0.0F);
        glowy_glands_left_r1.setTextureOffset(0, 41).addBox(-2.5F, -2.0F, -3.5F, 5.0F, 5.0F, 9.0F, 0.0F, false);

        glowy_glands_right = new AdvancedModelBox(this);
        glowy_glands_right.setPos(-5.5F, -8.0F, -3.5F);
        body.addChild(glowy_glands_right);


        glowy_glands_right_r1 = new AdvancedModelBox(this);
        glowy_glands_right_r1.setPos(0.0F, 0.8921F, 0.0239F);
        glowy_glands_right.addChild(glowy_glands_right_r1);
        setRotationAngle(glowy_glands_right_r1, -0.4363F, 0.0F, 0.0F);
        glowy_glands_right_r1.setTextureOffset(0, 41).addBox(-2.5F, -2.0F, -3.5F, 5.0F, 5.0F, 9.0F, 0.0F, true);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, -4.5F, -7.0F);
        body.addChild(head);
        head.setTextureOffset(30, 32).addBox(-5.0F, -3.5F, -9.0F, 10.0F, 4.0F, 9.0F, 0.0F, false);

        eye_left = new AdvancedModelBox(this);
        eye_left.setPos(5.0F, -4.0F, -4.5F);
        head.addChild(eye_left);
        eye_left.setTextureOffset(20, 43).addBox(-1.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 0.0F, false);

        eye_right = new AdvancedModelBox(this);
        eye_right.setPos(-5.0F, -4.0F, -4.5F);
        head.addChild(eye_right);
        eye_right.setTextureOffset(20, 43).addBox(-1.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this);
        jaw.setPos(0.0F, 0.5F, -1.5F);
        head.addChild(jaw);
        jaw.setTextureOffset(0, 27).addBox(-5.0F, -0.1F, -7.5F, 10.0F, 4.0F, 9.0F, -0.1F, false);

        tongue = new AdvancedModelBox(this);
        tongue.setPos(0.0F, 1.0F, 0.5F);
        jaw.addChild(tongue);
        setRotationAngle(tongue, -0.6981F, 0.0F, 0.0F);
        tongue.setTextureOffset(52, 52).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

        leg_left = new AdvancedModelBox(this);
        leg_left.setPos(7.0F, 1.4F, 3.5F);
        body.addChild(leg_left);
        setRotationAngle(leg_left, -0.48F, 0.0F, 0.0F);
        leg_left.setTextureOffset(29, 46).addBox(-1.0F, -2.5F, -3.5F, 4.0F, 8.0F, 7.0F, 0.0F, false);

        foot_left = new AdvancedModelBox(this);
        foot_left.setPos(0.5F, 4.5F, 3.5F);
        leg_left.addChild(foot_left);
        setRotationAngle(foot_left, 0.48F, 0.0F, 0.0F);
        foot_left.setTextureOffset(45, 0).addBox(-1.5F, 0.0F, -10.0F, 3.0F, 1.0F, 10.0F, 0.0F, false);

        leg_right = new AdvancedModelBox(this);
        leg_right.setPos(-7.0F, 1.4F, 3.5F);
        body.addChild(leg_right);
        setRotationAngle(leg_right, -0.48F, 0.0F, 0.0F);
        leg_right.setTextureOffset(29, 46).addBox(-3.0F, -2.5F, -3.5F, 4.0F, 8.0F, 7.0F, 0.0F, true);

        foot_right = new AdvancedModelBox(this);
        foot_right.setPos(-0.5F, 4.5F, 3.5F);
        leg_right.addChild(foot_right);
        setRotationAngle(foot_right, 0.48F, 0.0F, 0.0F);
        foot_right.setTextureOffset(45, 0).addBox(-1.5F, 0.0F, -10.0F, 3.0F, 1.0F, 10.0F, 0.0F, true);

        arm_left = new AdvancedModelBox(this);
        arm_left.setPos(5.4F, 0.5F, -8.0F);
        body.addChild(arm_left);
        arm_left.setTextureOffset(0, 0).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 9.0F, 3.0F, 0.0F, false);

        arm_right = new AdvancedModelBox(this);
        arm_right.setPos(-5.4F, 0.5F, -8.0F);
        body.addChild(arm_right);
        arm_right.setTextureOffset(0, 0).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 9.0F, 3.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, body_r1, glowy_glands_left, glowy_glands_left_r1, glowy_glands_right, glowy_glands_right_r1, eye_left, eye_right, jaw, tongue, leg_left, leg_right, foot_left, foot_right, arm_left, arm_right);
    }

    @Override
    public void setupAnim(EntityWarpedToad entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = Minecraft.getInstance().getFrameTime();
        float attackProgress = entity.prevAttackProgress + (entity.attackProgress - entity.prevAttackProgress) * partialTick;
        float swimSpeed = 0.8f;
        float swimDegree = 0.5f;
        float blinkProgress = Math.max(attackProgress, entity.prevBlinkProgress + (entity.blinkProgress - entity.prevBlinkProgress) * partialTick);
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
        float swimProgress = entity.prevSwimProgress + (entity.swimProgress - entity.prevSwimProgress) * partialTick;
        float eyeBlinkScaleY = 1F - blinkProgress * 0.12F;
        float eyeBlinkScaleXZ = 1F - blinkProgress * 0.015F;
        float glowyBob = 0.75F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.125F + attackProgress * 0.1F;
        float toungeScale = 1F + attackProgress * 0.285F * entity.getTongueLength();
        float toungeScaleCorners = 1F - (attackProgress * 0.1F);
        glowy_glands_right_r1.setScale(glowyBob, glowyBob, glowyBob);
        glowy_glands_left_r1.setScale(glowyBob, glowyBob, glowyBob);
        eye_left.setScale(eyeBlinkScaleXZ, eyeBlinkScaleY, eyeBlinkScaleXZ);
        eye_right.setScale(eyeBlinkScaleXZ, eyeBlinkScaleY, eyeBlinkScaleXZ);
        tongue.setScale(toungeScaleCorners, toungeScaleCorners, toungeScale);
        tongue.rotateAngleX = -jaw.rotateAngleX;
        progressPositionPrev(eye_right, blinkProgress, 0, 1.45F, 0, 5f);
        progressPositionPrev(eye_left, blinkProgress, 0, 1.45F, 0, 5f);
        progressRotationPrev(eye_right, blinkProgress, 0, 0, (float) Math.toRadians(15), 5f);
        progressRotationPrev(eye_left, blinkProgress, 0, 0, (float) Math.toRadians(-15), 5f);
        progressRotationPrev(head, attackProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(tongue, attackProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressPositionPrev(head, attackProgress, 0, 0, -1.3F, 5f);
        progressRotationPrev(jaw, attackProgress, (float) Math.toRadians(65), 0, 0, 5f);
        progressRotationPrev(body, Math.max(sitProgress, swimProgress), 0.4363F, 0, 0, 5f);
        progressRotationPrev(leg_right, sitProgress, -0.4363F, 0, 0, 5f);
        progressRotationPrev(leg_left, sitProgress, -0.4363F, 0, 0, 5f);
        progressRotationPrev(arm_left, Math.max(sitProgress, swimProgress), (float) Math.toRadians(-90), (float) Math.toRadians(-20), 0, 5f);
        progressRotationPrev(arm_right, Math.max(sitProgress, swimProgress), (float) Math.toRadians(-90), (float) Math.toRadians(20), 0, 5f);
        progressRotationPrev(head, sitProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressPositionPrev(body, sitProgress, 0, 2F, 0, 5f);
        progressPositionPrev(arm_right, sitProgress, 0, -2F, 0, 5f);
        progressPositionPrev(arm_left, sitProgress, 0, -2F, 0, 5f);
        progressRotationPrev(leg_left, swimProgress, (float) Math.toRadians(60), (float) Math.toRadians(-40), (float) Math.toRadians(-80), 5f);
        progressRotationPrev(leg_right, swimProgress, (float) Math.toRadians(60), (float) Math.toRadians(40), (float) Math.toRadians(80), 5f);
        progressRotationPrev(foot_left, swimProgress, (float) Math.toRadians(25), 0, 0, 5f);
        progressRotationPrev(foot_right, swimProgress, (float) Math.toRadians(25), 0, 0, 5f);
        progressRotationPrev(head, swimProgress, (float) Math.toRadians(-20), 0, 0, 5f);
        progressPositionPrev(leg_right, swimProgress, 0, 2F, 0, 5f);
        progressPositionPrev(leg_left, swimProgress, 0, 2F, 0, 5f);
        if (swimProgress > 2F) {
            this.swing(arm_right, swimSpeed, swimDegree, true, 0, -0.2F, limbSwing, limbSwingAmount);
            this.swing(arm_left, swimSpeed, swimDegree, false, 0, -0.2F, limbSwing, limbSwingAmount);
            this.walk(leg_right, swimSpeed, swimDegree, false, 0, 0.7F, limbSwing, limbSwingAmount);
            this.walk(leg_left, swimSpeed, swimDegree, false, 0, 0.7F, limbSwing, limbSwingAmount);
            this.walk(foot_right, swimSpeed, swimDegree * 0.7F, false, 0.4F, -0.1F, limbSwing, limbSwingAmount);
            this.walk(foot_left, swimSpeed, swimDegree * 0.7F, false, 0.4F, -0.1F, limbSwing, limbSwingAmount);
        } else {
            float jumpRotation = Mth.sin(entity.getJumpCompletion(partialTick) * 3.1415927F);
            this.leg_left.rotateAngleX += (jumpRotation * 50.0F) * 0.017453292F;
            this.leg_right.rotateAngleX += (jumpRotation * 50.0F) * 0.017453292F;
            this.foot_right.rotateAngleX += jumpRotation * 25.0F * 0.017453292F;
            this.foot_left.rotateAngleX += jumpRotation * 25.0F * 0.017453292F;
            this.arm_right.rotateAngleX += (jumpRotation * -70.0F) * 0.017453292F;
            this.arm_left.rotateAngleX += (jumpRotation * -70.0F) * 0.017453292F;
            this.body.rotateAngleX += (jumpRotation * 30.0F) * 0.017453292F;
            this.head.rotateAngleX += (jumpRotation * -10.0F) * 0.017453292F;
        }
        if(attackProgress > 0){
            this.head.rotateAngleX += headPitch * ((float)Math.PI / 180F);
        }

    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.6F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.75D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }


    public void setRotationAngle(AdvancedModelBox modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}