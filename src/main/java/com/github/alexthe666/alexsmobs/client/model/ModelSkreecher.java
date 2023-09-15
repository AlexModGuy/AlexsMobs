package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntitySkreecher;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelSkreecher extends AdvancedEntityModel<EntitySkreecher> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox upperJaw;
    private final AdvancedModelBox leftEye;
    private final AdvancedModelBox rightEye;
    private final AdvancedModelBox leftLeg;
    private final AdvancedModelBox leftFoot;
    private final AdvancedModelBox rightLeg;
    private final AdvancedModelBox rightFoot;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox leftArmPivot;
    private final AdvancedModelBox leftHand;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox rightArmPivot;
    private final AdvancedModelBox rightHand;

    public ModelSkreecher() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -14.0F, 0.0F);
        root.addChild(body);
        setRotationAngle(body, -0.3927F, 0.0F, 0.0F);
        body.setTextureOffset(0, 26).addBox(-3.0F, -5.0F, -1.5F, 6.0F, 6.0F, 3.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -5.0F, -1.0F);
        body.addChild(head);
        setRotationAngle(head, 0.5236F, 0.0F, 0.0F);
        head.setTextureOffset(0, 13).addBox(-5.0F, -7.0F, -4.2F, 10.0F, 7.0F, 5.0F, -0.1F, false);

        upperJaw = new AdvancedModelBox(this, "upperJaw");
        upperJaw.setRotationPoint(0.0F, -7.0F, 0.8F);
        head.addChild(upperJaw);
        upperJaw.setTextureOffset(0, 0).addBox(-6.0F, 0.0F, -4.8F, 12.0F, 7.0F, 5.0F, 0.0F, false);

        leftEye = new AdvancedModelBox(this, "leftEye");
        leftEye.setRotationPoint(3.0F, 1.6F, -3.8F);
        upperJaw.addChild(leftEye);
        leftEye.setTextureOffset(34, 16).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        rightEye = new AdvancedModelBox(this, "rightEye");
        rightEye.setRotationPoint(-3.0F, 1.6F, -3.8F);
        upperJaw.addChild(rightEye);
        rightEye.setTextureOffset(34, 16).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);

        leftLeg = new AdvancedModelBox(this, "leftLeg");
        leftLeg.setRotationPoint(2.0F, 2.0F, 0.0F);
        body.addChild(leftLeg);
        setRotationAngle(leftLeg, 0.0F, -0.3054F, 0.0F);
        leftLeg.setTextureOffset(27, 25).addBox(0.0F, -1.0F, -5.0F, 0.0F, 6.0F, 7.0F, 0.0F, false);

        leftFoot = new AdvancedModelBox(this, "leftFoot");
        leftFoot.setRotationPoint(0.0F, 2.0F, -4.0F);
        leftLeg.addChild(leftFoot);
        leftFoot.setTextureOffset(1, 48).addBox(-1.0F, 0.0F, -4.0F, 1.0F, 6.0F, 6.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this, "rightLeg");
        rightLeg.setRotationPoint(-2.0F, 2.0F, 0.0F);
        body.addChild(rightLeg);
        setRotationAngle(rightLeg, 0.0F, 0.3054F, 0.0F);
        rightLeg.setTextureOffset(27, 25).addBox(0.0F, -1.0F, -5.0F, 0.0F, 6.0F, 7.0F, 0.0F, true);

        rightFoot = new AdvancedModelBox(this, "rightFoot");
        rightFoot.setRotationPoint(0.0F, 2.0F, -4.0F);
        rightLeg.addChild(rightFoot);
        rightFoot.setTextureOffset(1, 48).addBox(0.0F, 0.0F, -4.0F, 1.0F, 6.0F, 6.0F, 0.0F, true);

        leftArmPivot = new AdvancedModelBox(this, "leftArmPivot");
        leftArmPivot.setRotationPoint(4.0F, -3.0F, 0.0F);
        body.addChild(leftArmPivot);

        leftArm = new AdvancedModelBox(this, "leftArm");
        leftArmPivot.addChild(leftArm);
        setRotationAngle(leftArm, 1.5708F, -1.1781F, -1.5708F);
        leftArm.setTextureOffset(17, 34).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, false);

        leftHand = new AdvancedModelBox(this, "leftHand");
        leftHand.setRotationPoint(0.0F, 15.0F, 0.0F);
        leftArm.addChild(leftHand);
        leftHand.setTextureOffset(31, 9).addBox(-4.0F, -0.1F, -2.0F, 8.0F, 3.0F, 4.0F, 0.0F, false);

        rightArmPivot = new AdvancedModelBox(this, "rightArmPivot");
        rightArmPivot.setRotationPoint(-4.0F, -3.0F, 0.0F);
        body.addChild(rightArmPivot);

        rightArm = new AdvancedModelBox(this, "rightArm");
        rightArmPivot.addChild(rightArm);
        setRotationAngle(rightArm, 1.5708F, 1.1781F, 1.5708F);
        rightArm.setTextureOffset(17, 34).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, true);

        rightHand = new AdvancedModelBox(this, "rightHand");
        rightHand.setRotationPoint(0.0F, 15.0F, 0.0F);
        rightArm.addChild(rightHand);
        rightHand.setTextureOffset(31, 9).addBox(-4.0F, -0.1F, -2.0F, 8.0F, 3.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, upperJaw, leftEye, rightEye, leftLeg, leftArmPivot, leftArm, leftFoot, rightLeg, rightArmPivot, rightArm, rightFoot, leftHand, rightHand);
    }

    @Override
    public void setupAnim(EntitySkreecher entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float clingProgress = entity.prevClingProgress + (entity.clingProgress - entity.prevClingProgress) * partialTick;
        float groundProgress = 5f - clingProgress;
        float clapProgress = entity.prevClapProgress + (entity.clapProgress - entity.prevClapProgress) * partialTick;
        float distanceToCeiling = entity.prevDistanceToCeiling + (entity.getDistanceToCeiling() - entity.prevDistanceToCeiling) * partialTick;
        float armScale = 0.3F + distanceToCeiling * clingProgress * 0.2F + 0.7F * groundProgress * 0.2F;
        float armDegree = (5f - distanceToCeiling) * 0.2F;
        float walkSpeed = 1F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float groundSpeed = groundProgress * 0.2F;
        float feetStill = (5f - clapProgress) * 0.2F;
        float clingSpeed = clingProgress * 0.2F;
        this.rightArm.setShouldScaleChildren(true);
        this.rightArm.setScale(1.0F, armScale, 1.0F);
        this.leftArm.setShouldScaleChildren(true);
        this.leftArm.setScale(1.0F, armScale, 1.0F);
        progressRotationPrev(rightArmPivot, clingProgress, Maths.rad(-210), 0, 0, 5F);
        progressRotationPrev(leftArmPivot, clingProgress, Maths.rad(-210), 0, 0, 5F);
        progressRotationPrev(body, clingProgress, Maths.rad(30), 0, 0, 5F);
        progressRotationPrev(head, clingProgress, Maths.rad(-30), 0, 0, 5F);
        progressPositionPrev(body, clingProgress, 0, 8.5F, 0, 5F);
        progressPositionPrev(rightArmPivot, clingProgress, 0, -1F, 0, 5F);
        progressPositionPrev(leftArmPivot, clingProgress, 0, -1F, 0, 5F);
        progressPositionPrev(head, clingProgress, 0, 3F, 0F, 5F);
        progressRotationPrev(rightLeg, clapProgress, Maths.rad(-10), Maths.rad(-25), 0, 5F);
        progressRotationPrev(leftLeg, clapProgress, Maths.rad(-10), Maths.rad(25), 0, 5F);
        this.walk(body, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1F);
        this.walk(rightLeg, idleSpeed, idleDegree, false, -1F, 0F, ageInTicks, feetStill);
        this.walk(leftLeg, idleSpeed, idleDegree, false, -1F, 0F, ageInTicks, feetStill);
        this.walk(leftArmPivot, idleSpeed, idleDegree, true, 0F, 0F, ageInTicks, 1F);
        this.walk(rightArmPivot, idleSpeed, idleDegree, true, 0F, 0F, ageInTicks, 1F);
        this.bob(head, idleSpeed, -idleDegree * 2F, false, limbSwing, 1F);
        this.walk(body, walkSpeed, walkDegree, false, 0F, 0.3F, limbSwing, limbSwingAmount * groundSpeed);
        this.walk(head, walkSpeed, walkDegree * 0.5F, true, 0.3F, 0.3F, limbSwing, limbSwingAmount * groundSpeed);
        this.swing(leftArm, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount * groundSpeed);
        this.swing(rightArm, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount * groundSpeed);
        this.walk(rightLeg, walkSpeed, walkDegree, false, 1F, 0.3F, limbSwing, limbSwingAmount * groundSpeed * feetStill);
        this.walk(leftLeg, walkSpeed, walkDegree, false, 1F, 0.3F, limbSwing, limbSwingAmount * groundSpeed * feetStill);
        this.swing(leftArm, walkSpeed, walkDegree, false, -2F, -0.2F, limbSwing, limbSwingAmount * groundSpeed);
        this.swing(rightArm, walkSpeed, walkDegree, false, -2F, 0.2F, limbSwing, limbSwingAmount * groundSpeed);
        this.swing(leftArm, walkSpeed, armDegree * walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount * clingSpeed);
        this.swing(rightArm, walkSpeed, armDegree * walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount * clingSpeed);
        this.walk(leftArm, walkSpeed, armDegree * walkDegree * 0.2F, true, 1F, 0F, limbSwing, limbSwingAmount * clingSpeed);
        this.walk(rightArm, walkSpeed, armDegree * walkDegree * 0.2F, false, 1F, 0F, limbSwing, limbSwingAmount * clingSpeed);
        this.walk(rightLeg, walkSpeed, walkDegree, false, 2F, 0.4F, limbSwing, limbSwingAmount * clingSpeed * feetStill);
        this.walk(leftLeg, walkSpeed, walkDegree, true, 2F, -0.4F, limbSwing, limbSwingAmount * clingSpeed * feetStill);
        this.swing(body, walkSpeed, walkDegree * 0.2F, false, 0F, 0F, limbSwing, limbSwingAmount * clingSpeed);
        this.swing(head, walkSpeed, walkDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount * clingSpeed);
        this.swing(head, 10F, 0.05F, false, 0F, 0F, ageInTicks, clapProgress * 0.2F);
        this.flap(leftLeg, 0.8F, 0.5F, false, 0F, -0.45F, ageInTicks, clapProgress * 0.2F);
        this.flap(rightLeg, 0.8F, 0.5F, true, 0F, -0.45F, ageInTicks, clapProgress * 0.2F);
        this.swing(leftLeg, 0.8F, 0.35F, false, 0F, -0.15F, ageInTicks, clapProgress * 0.2F);
        this.swing(rightLeg, 0.8F, 0.35F, true, 0F, -0.15F, ageInTicks, clapProgress * 0.2F);
        this.bob(body, idleSpeed * 3F, -idleDegree * 5F, false, ageInTicks, clingSpeed);
        this.bob(rightArmPivot, idleSpeed * 3F, idleDegree * 8F, false, ageInTicks, clingSpeed);
        this.bob(leftArmPivot, idleSpeed * 3F, idleDegree * 8F, false, ageInTicks, clingSpeed);
        this.faceTarget(netHeadYaw, headPitch, 1F, head);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
