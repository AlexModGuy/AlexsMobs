package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ModelCosmaw extends AdvancedEntityModel<EntityCosmaw> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox leftArm;
    public final AdvancedModelBox rightArm;
    public final AdvancedModelBox leftFin;
    public final AdvancedModelBox rightFin;
    public final AdvancedModelBox mouthArm1;
    public final AdvancedModelBox mouthArm2;
    public final AdvancedModelBox mouth;
    public final AdvancedModelBox topJaw;
    public final AdvancedModelBox lowerJaw;
    public final AdvancedModelBox eyesBase;
    public final AdvancedModelBox leftEye;
    public final AdvancedModelBox rightEye;
    public final AdvancedModelBox tail;
    public final AdvancedModelBox leftLeg;
    public final AdvancedModelBox rightLeg;
    public final AdvancedModelBox tailFin;

    public ModelCosmaw() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -10.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-6.5F, -7.0F, -20.0F, 13.0F, 14.0F, 32.0F, 0.0F, false);

        leftArm = new AdvancedModelBox(this);
        leftArm.setRotationPoint(5.0F, 7.0F, -12.2F);
        body.addChild(leftArm);
        leftArm.setTextureOffset(17, 47).addBox(-1.0F, 0.0F, -0.8F, 2.0F, 4.0F, 3.0F, 0.0F, false);
        leftArm.setTextureOffset(0, 13).addBox(0.0F, 2.0F, 1.2F, 0.0F, 4.0F, 3.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this);
        rightArm.setRotationPoint(-5.0F, 7.0F, -12.2F);
        body.addChild(rightArm);
        rightArm.setTextureOffset(17, 47).addBox(-1.0F, 0.0F, -0.8F, 2.0F, 4.0F, 3.0F, 0.0F, true);
        rightArm.setTextureOffset(0, 13).addBox(0.0F, 2.0F, 1.2F, 0.0F, 4.0F, 3.0F, 0.0F, true);

        leftFin = new AdvancedModelBox(this);
        leftFin.setRotationPoint(4.5F, -6.0F, 0.0F);
        body.addChild(leftFin);
        leftFin.setTextureOffset(33, 47).addBox(0.0F, 0.0F, -5.0F, 9.0F, 0.0F, 27.0F, 0.0F, false);

        rightFin = new AdvancedModelBox(this);
        rightFin.setRotationPoint(-4.5F, -6.0F, 0.0F);
        body.addChild(rightFin);
        rightFin.setTextureOffset(33, 47).addBox(-9.0F, 0.0F, -5.0F, 9.0F, 0.0F, 27.0F, 0.0F, true);

        mouthArm1 = new AdvancedModelBox(this);
        mouthArm1.setRotationPoint(0.0F, -3.0F, -20.0F);
        body.addChild(mouthArm1);
        setRotationAngle(mouthArm1, 1.0908F, 0.0F, 0.0F);
        mouthArm1.setTextureOffset(65, 75).addBox(-2.0F, -1.0F, -20.0F, 4.0F, 4.0F, 22.0F, 0.0F, false);

        mouthArm2 = new AdvancedModelBox(this);
        mouthArm2.setRotationPoint(0.0F, 3.0F, -20.0F);
        mouthArm1.addChild(mouthArm2);
        setRotationAngle(mouthArm2, -1.0472F, 0.0F, 0.0F);
        mouthArm2.setTextureOffset(79, 32).addBox(-2.0F, -4.0F, -17.0F, 4.0F, 4.0F, 17.0F, -0.1F, false);

        mouth = new AdvancedModelBox(this);
        mouth.setRotationPoint(0.0F, -1.4F, -16.7F);
        mouthArm2.addChild(mouth);


        topJaw = new AdvancedModelBox(this);
        topJaw.setRotationPoint(0.0F, 0.0F, -1.0F);
        mouth.addChild(topJaw);
        topJaw.setTextureOffset(0, 13).addBox(-2.5F, -3.0F, -7.0F, 5.0F, 3.0F, 8.0F, 0.0F, false);

        lowerJaw = new AdvancedModelBox(this);
        lowerJaw.setRotationPoint(0.0F, -1.0F, 0.3F);
        mouth.addChild(lowerJaw);
        lowerJaw.setTextureOffset(0, 0).addBox(-3.0F, 0.0F, -9.0F, 6.0F, 3.0F, 9.0F, 0.0F, false);

        eyesBase = new AdvancedModelBox(this);
        eyesBase.setRotationPoint(0.0F, -7.0F, -11.0F);
        body.addChild(eyesBase);
        eyesBase.setTextureOffset(3, 69).addBox(-11.0F, -1.0F, -1.0F, 23.0F, 2.0F, 2.0F, 0.0F, false);

        leftEye = new AdvancedModelBox(this);
        leftEye.setRotationPoint(13.0F, 0.0F, 0.0F);
        eyesBase.addChild(leftEye);
        leftEye.setTextureOffset(0, 47).addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);

        rightEye = new AdvancedModelBox(this);
        rightEye.setRotationPoint(-12.0F, 0.0F, 1.0F);
        eyesBase.addChild(rightEye);
        rightEye.setTextureOffset(0, 47).addBox(-1.0F, -3.0F, -4.0F, 2.0F, 6.0F, 6.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -1.8F, 11.6F);
        body.addChild(tail);
        tail.setTextureOffset(59, 0).addBox(-4.5F, -5.0F, 0.0F, 9.0F, 11.0F, 20.0F, 0.0F, false);

        leftLeg = new AdvancedModelBox(this);
        leftLeg.setRotationPoint(3.0F, 5.8F, 3.2F);
        tail.addChild(leftLeg);
        leftLeg.setTextureOffset(19, 13).addBox(-1.0F, 0.0F, -0.8F, 2.0F, 4.0F, 3.0F, 0.0F, false);
        leftLeg.setTextureOffset(0, 0).addBox(0.0F, 2.0F, 1.2F, 0.0F, 4.0F, 3.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this);
        rightLeg.setRotationPoint(-3.0F, 5.8F, 3.2F);
        tail.addChild(rightLeg);
        rightLeg.setTextureOffset(19, 13).addBox(-1.0F, 0.0F, -0.8F, 2.0F, 4.0F, 3.0F, 0.0F, true);
        rightLeg.setTextureOffset(0, 0).addBox(0.0F, 2.0F, 1.2F, 0.0F, 4.0F, 3.0F, 0.0F, true);

        tailFin = new AdvancedModelBox(this);
        tailFin.setRotationPoint(0.0F, 1.0F, 7.0F);
        tail.addChild(tailFin);
        tailFin.setTextureOffset(0, 47).addBox(0.0F, -10.0F, -3.0F, 0.0F, 19.0F, 32.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, tail, tailFin, body, leftLeg, rightLeg, eyesBase, leftEye, rightEye, leftArm, leftFin, rightArm, rightFin, mouth, mouthArm1, mouthArm2, lowerJaw, topJaw);
    }

    @Override
    public void setupAnim(EntityCosmaw entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.7F;
        float walkDegree = 0.4F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.2F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float clutchProgress = entity.prevClutchProgress + (entity.clutchProgress - entity.prevClutchProgress) * partialTick;
        float biteProgress = entity.prevBiteProgress + (entity.biteProgress - entity.prevBiteProgress) * partialTick;
        float openProgress = Math.max(Math.max(entity.prevOpenProgress + (entity.openProgress - entity.prevOpenProgress) * partialTick, clutchProgress), biteProgress);
        float cosmawPitch = (float) (Math.toRadians(entity.getClampedCosmawPitch(partialTick)) * (5F - clutchProgress) * 0.2F);
        float cosmawPitchPos = (float) ((entity.getClampedCosmawPitch(partialTick) / 90F) * (5F - clutchProgress) * 0.2F);
        body.rotateAngleX += cosmawPitch;
        eyesBase.rotateAngleX -= cosmawPitch;
        mouthArm1.rotateAngleX -= cosmawPitch * 0.2F;
        mouthArm2.rotateAngleX -= cosmawPitch * 1.7F;
        lowerJaw.rotateAngleX -= cosmawPitch * 0.3F;
        topJaw.rotateAngleX -= cosmawPitch * 0.3F;
        if(cosmawPitchPos > 0){
            mouthArm2.rotationPointY -= Math.min(cosmawPitchPos * 6, 3F);
        }else{
            mouthArm2.rotationPointZ -= cosmawPitchPos * 3;
            mouthArm2.rotationPointY += cosmawPitchPos;

        }
        this.walk(body, idleSpeed, idleDegree * 0.1F, false, -1F, 0.05F, ageInTicks, 1);
        this.walk(tail, idleSpeed, idleDegree * -0.15F, true, -1F, 0.05F, ageInTicks, 1);
        this.swing(leftFin, idleSpeed, idleDegree * 0.22F, false, -2F, 0.05F, ageInTicks, 1);
        this.swing(rightFin, idleSpeed, idleDegree * 0.22F, true, -2F, 0.05F, ageInTicks, 1);
        this.walk(eyesBase, idleSpeed, idleDegree * 0.1F, true, -1F, 0.05F, ageInTicks, 1);
        this.walk(lowerJaw, idleSpeed, idleDegree * 0.2F, true, 1F, -0.05F, ageInTicks, 1);
        this.walk(topJaw, idleSpeed, idleDegree * 0.2F, true, 1F, 0.05F, ageInTicks, 1);
        this.walk(mouthArm1, idleSpeed, idleDegree * 0.4F, false, 2F, 0.05F, ageInTicks, 1);
        this.walk(mouthArm2, idleSpeed, idleDegree * 0.6F, true, 2F, 0.05F, ageInTicks, 1);
        this.bob(body, idleSpeed * 2F, idleDegree * 4F, false, ageInTicks, 1);
        this.walk(leftArm, idleSpeed, idleDegree * 0.3F, false, -2F, -0.05F, ageInTicks, 1);
        this.walk(rightArm, idleSpeed, idleDegree * 0.3F, false, -2F, -0.05F, ageInTicks, 1);
        this.walk(leftLeg, idleSpeed, idleDegree * 0.3F, false, -3F, -0.05F, ageInTicks, 1);
        this.walk(rightLeg, idleSpeed, idleDegree * 0.3F, false, -3F, -0.05F, ageInTicks, 1);
        this.swing(tail, walkSpeed, walkDegree * 0.5F, false, -1F, 0F, limbSwing, limbSwingAmount);
        this.swing(tailFin, walkSpeed, walkDegree * 0.25F, false, -1F, 0F, limbSwing, limbSwingAmount);
        this.flap(leftFin, walkSpeed, walkDegree * 0.7F, false, -2F, 0.05F, limbSwing, limbSwingAmount);
        this.flap(rightFin, walkSpeed, walkDegree * 0.7F, true, -2F, 0.05F, limbSwing, limbSwingAmount);
        this.walk(leftArm, walkSpeed, walkDegree * 0.3F, false, -2F, -0.05F, limbSwing, limbSwingAmount);
        this.walk(rightArm, walkSpeed, walkDegree * 0.3F, false, -2F, -0.05F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree * 4F, false, limbSwing, limbSwingAmount);
        progressRotationPrev(topJaw, openProgress, (float) Math.toRadians(-30), 0, 0, 5F);
        progressRotationPrev(lowerJaw, openProgress, (float) Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(body, clutchProgress, (float) Math.toRadians(-30), 0, 0, 5F);
        progressRotationPrev(eyesBase, clutchProgress, (float) Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(mouthArm1, clutchProgress, (float) Math.toRadians(-5), 0, 0, 5F);
        progressRotationPrev(mouthArm2, clutchProgress, (float) Math.toRadians(120), 0, 0, 5F);
        progressPositionPrev(mouthArm2, clutchProgress, 0, -2, 3, 5F);
        progressPositionPrev(body, clutchProgress, 0, -10, 33, 5F);
        progressPositionPrev(body, biteProgress, 0, 0, 20, 5F);
        progressRotationPrev(mouthArm1, biteProgress, (float) Math.toRadians(-35), 0, 0, 5F);
        progressRotationPrev(mouthArm2, biteProgress, (float) Math.toRadians(50), 0, 0, 5F);
        progressRotationPrev(leftArm, biteProgress, (float) Math.toRadians(10), 0, (float) Math.toRadians(-30), 5F);
        progressRotationPrev(rightArm, biteProgress, (float) Math.toRadians(10), 0, (float) Math.toRadians(30), 5F);
        float eyeYaw = (Mth.clamp(netHeadYaw, -40, 40) / 57.295776F);
        this.eyesBase.rotateAngleY += eyeYaw * 0.35F;
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            eyesBase.setScale(f, f, f);
            eyesBase.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            eyesBase.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }
}