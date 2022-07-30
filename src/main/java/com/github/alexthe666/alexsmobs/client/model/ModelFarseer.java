package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.phys.Vec3;

public class ModelFarseer extends AdvancedEntityModel<EntityFarseer> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox bodyCube1;
    private final AdvancedModelBox head;
    private final AdvancedModelBox leftUpperMask;
    private final AdvancedModelBox rightUpperMask;
    private final AdvancedModelBox leftLowerMask;
    private final AdvancedModelBox rightLowerMask;
    private final AdvancedModelBox eye;
    private final AdvancedModelBox bodyCube2;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox leftElbow;
    private final AdvancedModelBox leftHand;
    private final AdvancedModelBox leftUpperRFinger;
    private final AdvancedModelBox leftLowerRFinger;
    private final AdvancedModelBox leftLowerLFinger;
    private final AdvancedModelBox leftUpperLFinger;
    private final AdvancedModelBox leftArm2;
    private final AdvancedModelBox leftElbow2;
    private final AdvancedModelBox leftHand2;
    private final AdvancedModelBox leftUpperRFinger2;
    private final AdvancedModelBox leftLowerRFinger2;
    private final AdvancedModelBox leftLowerLFinger2;
    private final AdvancedModelBox leftUpperLFinger2;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox rightElbow;
    private final AdvancedModelBox rightHand;
    private final AdvancedModelBox rightUpperRFinger;
    private final AdvancedModelBox rightLowerRFinger2;
    private final AdvancedModelBox rightLowerLFinger;
    private final AdvancedModelBox rightUpperLFinger;
    private final AdvancedModelBox rightArm2;
    private final AdvancedModelBox rightElbow2;
    private final AdvancedModelBox rightHand2;
    private final AdvancedModelBox rightUpperRFinger2;
    private final AdvancedModelBox rightLowerRFinger3;
    private final AdvancedModelBox rightLowerLFinger2;
    private final AdvancedModelBox rightUpperLFinger2;

    public ModelFarseer() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        bodyCube1 = new AdvancedModelBox(this);
        bodyCube1.setRotationPoint(2.0F, -8.0F, 0.0F);
        root.addChild(bodyCube1);
        bodyCube1.setTextureOffset(0, 56).addBox(-5.0F, -2.0F, -3.0F, 10.0F, 4.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -15.0F, 0.0F);
        root.addChild(head);
        head.setTextureOffset(0, 0).addBox(-10.5F, -5.0F, -6.0F, 21.0F, 9.0F, 13.0F, 0.0F, false);

        leftUpperMask = new AdvancedModelBox(this);
        leftUpperMask.setRotationPoint(0.0F, -5.0F, 1.0F);
        head.addChild(leftUpperMask);
        leftUpperMask.setTextureOffset(0, 23).addBox(0.0F, -0.5F, -7.9F, 11.0F, 7.0F, 8.0F, 0.0F, false);

        rightUpperMask = new AdvancedModelBox(this);
        rightUpperMask.setRotationPoint(0.0F, -5.0F, 1.0F);
        head.addChild(rightUpperMask);
        rightUpperMask.setTextureOffset(0, 23).addBox(-11.0F, -0.5F, -7.9F, 11.0F, 7.0F, 8.0F, 0.0F, true);

        leftLowerMask = new AdvancedModelBox(this);
        leftLowerMask.setRotationPoint(0.0F, 3.0F, 1.0F);
        head.addChild(leftLowerMask);
        leftLowerMask.setTextureOffset(31, 31).addBox(0.0F, -2.5F, -7.9F, 11.0F, 4.0F, 8.0F, 0.0F, false);

        rightLowerMask = new AdvancedModelBox(this);
        rightLowerMask.setRotationPoint(0.0F, 3.0F, 1.0F);
        head.addChild(rightLowerMask);
        rightLowerMask.setTextureOffset(31, 31).addBox(-11.0F, -2.5F, -7.9F, 11.0F, 4.0F, 8.0F, 0.0F, true);

        eye = new AdvancedModelBox(this);
        eye.setRotationPoint(0.0F, 4.0F, -1.0F);
        head.addChild(eye);
        eye.setTextureOffset(56, 0).addBox(-4.5F, -8.0F, -6.0F, 9.0F, 4.0F, 2.0F, 0.0F, false);

        bodyCube2 = new AdvancedModelBox(this);
        bodyCube2.setRotationPoint(1.0F, -2.0F, 1.0F);
        root.addChild(bodyCube2);
        bodyCube2.setTextureOffset(33, 44).addBox(-6.0F, -3.0F, -3.0F, 10.0F, 5.0F, 6.0F, 0.0F, false);

        leftArm = new AdvancedModelBox(this);
        leftArm.setRotationPoint(9.0F, -16.5F, 9.0F);
        root.addChild(leftArm);
        setRotationAngle(leftArm, 0.0F, 0.0F, -0.7854F);
        leftArm.setTextureOffset(31, 23).addBox(-1.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, 0.0F, false);

        leftElbow = new AdvancedModelBox(this);
        leftElbow.setRotationPoint(15.0F, 0.0F, 0.0F);
        leftArm.addChild(leftElbow);
        leftElbow.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, 0.0F, false);

        leftHand = new AdvancedModelBox(this);
        leftHand.setRotationPoint(0.0F, 0.0F, -12.0F);
        leftElbow.addChild(leftHand);


        leftUpperRFinger = new AdvancedModelBox(this);
        leftUpperRFinger.setRotationPoint(-1.4F, -1.4F, 0.0F);
        leftHand.addChild(leftUpperRFinger);
        setRotationAngle(leftUpperRFinger, 0.0F, 0.0F, -0.7854F);
        leftUpperRFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        leftLowerRFinger = new AdvancedModelBox(this);
        leftLowerRFinger.setRotationPoint(-1.4F, 1.4F, 0.0F);
        leftHand.addChild(leftLowerRFinger);
        setRotationAngle(leftLowerRFinger, 0.0F, 0.0F, -2.3562F);
        leftLowerRFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        leftLowerLFinger = new AdvancedModelBox(this);
        leftLowerLFinger.setRotationPoint(1.4F, 1.4F, 0.0F);
        leftHand.addChild(leftLowerLFinger);
        setRotationAngle(leftLowerLFinger, 0.0F, 0.0F, 2.3562F);
        leftLowerLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        leftUpperLFinger = new AdvancedModelBox(this);
        leftUpperLFinger.setRotationPoint(1.4F, -1.4F, 0.0F);
        leftHand.addChild(leftUpperLFinger);
        setRotationAngle(leftUpperLFinger, 0.0F, 0.0F, 0.7854F);
        leftUpperLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        leftArm2 = new AdvancedModelBox(this);
        leftArm2.setRotationPoint(6.0F, -13.5F, 9.0F);
        root.addChild(leftArm2);
        setRotationAngle(leftArm2, 0.0F, 0.0F, 0.6545F);
        leftArm2.setTextureOffset(31, 23).addBox(-1.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, 0.0F, false);

        leftElbow2 = new AdvancedModelBox(this);
        leftElbow2.setRotationPoint(15.0F, 0.0F, 0.0F);
        leftArm2.addChild(leftElbow2);
        leftElbow2.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, 0.0F, false);

        leftHand2 = new AdvancedModelBox(this);
        leftHand2.setRotationPoint(0.0F, 0.0F, -12.0F);
        leftElbow2.addChild(leftHand2);


        leftUpperRFinger2 = new AdvancedModelBox(this);
        leftUpperRFinger2.setRotationPoint(-1.4F, -1.4F, 0.0F);
        leftHand2.addChild(leftUpperRFinger2);
        setRotationAngle(leftUpperRFinger2, 0.0F, 0.0F, -0.7854F);
        leftUpperRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        leftLowerRFinger2 = new AdvancedModelBox(this);
        leftLowerRFinger2.setRotationPoint(-1.4F, 1.4F, 0.0F);
        leftHand2.addChild(leftLowerRFinger2);
        setRotationAngle(leftLowerRFinger2, 0.0F, 0.0F, -2.3562F);
        leftLowerRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        leftLowerLFinger2 = new AdvancedModelBox(this);
        leftLowerLFinger2.setRotationPoint(1.4F, 1.4F, 0.0F);
        leftHand2.addChild(leftLowerLFinger2);
        setRotationAngle(leftLowerLFinger2, 0.0F, 0.0F, 2.3562F);
        leftLowerLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        leftUpperLFinger2 = new AdvancedModelBox(this);
        leftUpperLFinger2.setRotationPoint(1.4F, -1.4F, 0.0F);
        leftHand2.addChild(leftUpperLFinger2);
        setRotationAngle(leftUpperLFinger2, 0.0F, 0.0F, 0.7854F);
        leftUpperLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this);
        rightArm.setRotationPoint(-9.0F, -16.5F, 9.0F);
        root.addChild(rightArm);
        setRotationAngle(rightArm, 0.0F, 0.0F, 0.7854F);
        rightArm.setTextureOffset(31, 23).addBox(-15.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, 0.0F, true);

        rightElbow = new AdvancedModelBox(this);
        rightElbow.setRotationPoint(-15.0F, 0.0F, 0.0F);
        rightArm.addChild(rightElbow);
        rightElbow.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, 0.0F, true);

        rightHand = new AdvancedModelBox(this);
        rightHand.setRotationPoint(0.0F, 0.0F, -12.0F);
        rightElbow.addChild(rightHand);


        rightUpperRFinger = new AdvancedModelBox(this);
        rightUpperRFinger.setRotationPoint(1.4F, -1.4F, 0.0F);
        rightHand.addChild(rightUpperRFinger);
        setRotationAngle(rightUpperRFinger, 0.0F, 0.0F, 0.7854F);
        rightUpperRFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);

        rightLowerRFinger2 = new AdvancedModelBox(this);
        rightLowerRFinger2.setRotationPoint(1.4F, 1.4F, 0.0F);
        rightHand.addChild(rightLowerRFinger2);
        setRotationAngle(rightLowerRFinger2, 0.0F, 0.0F, 2.3562F);
        rightLowerRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);

        rightLowerLFinger = new AdvancedModelBox(this);
        rightLowerLFinger.setRotationPoint(-1.4F, 1.4F, 0.0F);
        rightHand.addChild(rightLowerLFinger);
        setRotationAngle(rightLowerLFinger, 0.0F, 0.0F, -2.3562F);
        rightLowerLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);

        rightUpperLFinger = new AdvancedModelBox(this);
        rightUpperLFinger.setRotationPoint(-1.4F, -1.4F, 0.0F);
        rightHand.addChild(rightUpperLFinger);
        setRotationAngle(rightUpperLFinger, 0.0F, 0.0F, -0.7854F);
        rightUpperLFinger.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);

        rightArm2 = new AdvancedModelBox(this);
        rightArm2.setRotationPoint(-6.0F, -13.5F, 9.0F);
        root.addChild(rightArm2);
        setRotationAngle(rightArm2, 0.0F, 0.0F, -0.6545F);
        rightArm2.setTextureOffset(31, 23).addBox(-15.0F, -1.5F, -2.0F, 16.0F, 3.0F, 4.0F, 0.0F, true);

        rightElbow2 = new AdvancedModelBox(this);
        rightElbow2.setRotationPoint(-15.0F, 0.0F, 0.0F);
        rightArm2.addChild(rightElbow2);
        rightElbow2.setTextureOffset(0, 39).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 14.0F, 0.0F, true);

        rightHand2 = new AdvancedModelBox(this);
        rightHand2.setRotationPoint(0.0F, 0.0F, -12.0F);
        rightElbow2.addChild(rightHand2);


        rightUpperRFinger2 = new AdvancedModelBox(this);
        rightUpperRFinger2.setRotationPoint(1.4F, -1.4F, 0.0F);
        rightHand2.addChild(rightUpperRFinger2);
        setRotationAngle(rightUpperRFinger2, 0.0F, 0.0F, 0.7854F);
        rightUpperRFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);

        rightLowerRFinger3 = new AdvancedModelBox(this);
        rightLowerRFinger3.setRotationPoint(1.4F, 1.4F, 0.0F);
        rightHand2.addChild(rightLowerRFinger3);
        setRotationAngle(rightLowerRFinger3, 0.0F, 0.0F, 2.3562F);
        rightLowerRFinger3.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);

        rightLowerLFinger2 = new AdvancedModelBox(this);
        rightLowerLFinger2.setRotationPoint(-1.4F, 1.4F, 0.0F);
        rightHand2.addChild(rightLowerLFinger2);
        setRotationAngle(rightLowerLFinger2, 0.0F, 0.0F, -2.3562F);
        rightLowerLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);

        rightUpperLFinger2 = new AdvancedModelBox(this);
        rightUpperLFinger2.setRotationPoint(-1.4F, -1.4F, 0.0F);
        rightHand2.addChild(rightUpperLFinger2);
        setRotationAngle(rightUpperLFinger2, 0.0F, 0.0F, -0.7854F);
        rightUpperLFinger2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -3.0F, 2.0F, 7.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityFarseer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float idleSpeed = 0.15F;
        float idleDegree = 1F;
        float angryProgress = entity.prevAngryProgress + (entity.angryProgress - entity.prevAngryProgress) * partialTick;
        float clasp1Progress = entity.prevClaspProgress[0] + (entity.claspProgress[0] - entity.prevClaspProgress[0]) * partialTick;
        float clasp2Progress = entity.prevClaspProgress[1] + (entity.claspProgress[1] - entity.prevClaspProgress[1]) * partialTick;
        float clasp3Progress = entity.prevClaspProgress[2] + (entity.claspProgress[2] - entity.prevClaspProgress[2]) * partialTick;
        float clasp4Progress = entity.prevClaspProgress[3] + (entity.claspProgress[3] - entity.prevClaspProgress[3]) * partialTick;
        float armYaw = (float) Math.toRadians(entity.getLatencyVar(5, 3, partialTick) - entity.getLatencyVar(0, 3, partialTick));
        Vec3 topArmOffset = entity.getLatencyOffsetVec(4, partialTick).scale(-4.0F);
        Vec3 bottomArmOffset = entity.getLatencyOffsetVec(8, partialTick).scale(-5.0F);
        Vec3 body1Offset = entity.getLatencyOffsetVec(8, partialTick).scale(-3.0F);
        Vec3 body2Offset = entity.getLatencyOffsetVec(12, partialTick).scale(-5.0F);
        Vec3 angryShake = entity.angryShakeVec.scale(angryProgress * 0.1F);
        progressRotationPrev(rightUpperMask, angryProgress, (float) Math.toRadians(-35), (float) Math.toRadians(13), 0, 5F);
        progressRotationPrev(leftUpperMask, angryProgress, (float) Math.toRadians(-35), (float) Math.toRadians(-13), 0, 5F);
        progressRotationPrev(rightLowerMask, angryProgress, (float) Math.toRadians(35), (float) Math.toRadians(13), 0, 5F);
        progressRotationPrev(leftLowerMask, angryProgress, (float) Math.toRadians(35), (float) Math.toRadians(-13), 0, 5F);
        progressPositionPrev(bodyCube1, angryProgress, 0, 0, 4, 5F);
        progressPositionPrev(bodyCube2, angryProgress, 0, 0, 2, 5F);

        progressRotationPrev(leftUpperRFinger, clasp1Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(leftLowerRFinger, clasp1Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(leftLowerLFinger, clasp1Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(leftUpperLFinger, clasp1Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightUpperRFinger, clasp2Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightLowerRFinger2, clasp2Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightLowerLFinger, clasp2Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightUpperLFinger, clasp2Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(leftUpperRFinger2, clasp3Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(leftLowerRFinger2, clasp3Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(leftLowerLFinger2, clasp3Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(leftUpperLFinger2, clasp3Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightUpperRFinger2, clasp4Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightLowerRFinger3, clasp4Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightLowerLFinger2, clasp4Progress, (float) Math.toRadians(45), 0, 0, 5F);
        progressRotationPrev(rightUpperLFinger2, clasp4Progress, (float) Math.toRadians(45), 0, 0, 5F);

        leftArm.rotationPointX += (topArmOffset.x + Math.sin(ageInTicks * idleSpeed + 1.3F)) * idleDegree;
        leftArm.rotationPointY += (topArmOffset.y + Math.sin(ageInTicks * idleSpeed + 1.6F)) * idleDegree;
        leftArm.rotationPointZ += (topArmOffset.z + Math.cos(ageInTicks * idleSpeed + 1.9F)) * idleDegree;
        leftArm.rotateAngleY += armYaw;
        leftArm2.rotationPointX += (bottomArmOffset.x + Math.sin(ageInTicks * idleSpeed + 2.3F)) * idleDegree;
        leftArm2.rotationPointY += (bottomArmOffset.y + Math.sin(ageInTicks * idleSpeed + 2.6F)) * idleDegree;
        leftArm2.rotationPointZ += (bottomArmOffset.z + Math.cos(ageInTicks * idleSpeed + 2.9F)) * idleDegree;
        leftArm2.rotateAngleY += armYaw;
        rightArm.rotationPointX += (topArmOffset.x + Math.sin(ageInTicks * idleSpeed + 3.3F)) * idleDegree;
        rightArm.rotationPointY += (topArmOffset.y + Math.sin(ageInTicks * idleSpeed + 3.6F)) * idleDegree;
        rightArm.rotationPointZ += (topArmOffset.z + Math.cos(ageInTicks * idleSpeed + 3.9F)) * idleDegree;
        rightArm.rotateAngleY += armYaw;
        rightArm2.rotationPointX += (bottomArmOffset.x + Math.sin(ageInTicks * idleSpeed + 4.3F)) * idleDegree;
        rightArm2.rotationPointY += (bottomArmOffset.y + Math.sin(ageInTicks * idleSpeed + 4.6F)) * idleDegree;
        rightArm2.rotationPointZ += (bottomArmOffset.z + Math.cos(ageInTicks * idleSpeed + 4.9F)) * idleDegree;
        rightArm2.rotateAngleY += armYaw;
        bodyCube1.rotationPointX += (body1Offset.x + Math.sin(ageInTicks * idleSpeed + 7.3F)) * idleDegree;
        bodyCube1.rotationPointY += (body1Offset.y + Math.sin(ageInTicks * idleSpeed + 7.6F)) * idleDegree;
        bodyCube1.rotationPointZ += (body1Offset.z + Math.cos(ageInTicks * idleSpeed + 7.9F)) * idleDegree;
        bodyCube2.rotationPointX += (body2Offset.x + Math.sin(ageInTicks * idleSpeed + 5.3F)) * idleDegree;
        bodyCube2.rotationPointY += (body2Offset.y + Math.sin(ageInTicks * idleSpeed + 5.6F)) * idleDegree;
        bodyCube2.rotationPointZ += (body2Offset.z + Math.cos(ageInTicks * idleSpeed + 5.9F)) * idleDegree;
        head.rotationPointX += angryShake.x;
        head.rotationPointY += angryShake.y;
        head.rotationPointZ += angryShake.z;

        this.bob(root, idleSpeed, idleDegree, false, ageInTicks, 1);
        this.swing(leftArm, idleSpeed, idleDegree * 0.2F, true, 1, 0F, ageInTicks, 1);
        this.swing(rightArm, idleSpeed, idleDegree * 0.2F, false, 2, 0F, ageInTicks, 1);
        this.swing(leftArm2, idleSpeed, idleDegree * 0.2F, true, 3, 0F, ageInTicks, 1);
        this.swing(rightArm2, idleSpeed, idleDegree * 0.2F, false, 4, 0F, ageInTicks, 1);
        this.walk(leftUpperMask, idleSpeed * 8, 0.05F, true, 1, 0.2F, ageInTicks, angryProgress * 0.2F);
        this.walk(rightUpperMask, idleSpeed * 8, 0.05F, true, 2, 0.2F, ageInTicks, angryProgress * 0.2F);
        this.walk(rightLowerMask, idleSpeed * 8, 0.05F, false, 3, 0.2F, ageInTicks, angryProgress * 0.2F);
        this.walk(leftLowerMask, idleSpeed * 8, 0.05F, false, 4, 0.2F, ageInTicks, angryProgress * 0.2F);

    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, bodyCube1, head, leftUpperMask, rightUpperMask, leftLowerMask, rightLowerMask, eye, bodyCube2, leftArm, leftElbow, leftHand, leftUpperRFinger, leftLowerRFinger, leftLowerLFinger, leftUpperLFinger, leftArm2, leftElbow2, leftHand2, leftUpperRFinger2, leftLowerRFinger2, leftLowerLFinger2, leftUpperLFinger2, rightArm, rightElbow, rightHand, rightUpperRFinger, rightLowerRFinger2, rightLowerLFinger, rightUpperLFinger, rightArm2, rightElbow2, rightHand2, rightUpperRFinger2, rightLowerRFinger3, rightLowerLFinger2, rightUpperLFinger2);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}