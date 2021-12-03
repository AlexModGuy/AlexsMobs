package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelJerboa extends AdvancedEntityModel<EntityJerboa> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftEar;
    private final AdvancedModelBox rightEar;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox legs;
    private final AdvancedModelBox feet;
    private final ModelAnimator animator;

    public ModelJerboa() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -7.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 19).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 8.0F, 0.0F, false);

        leftEar = new AdvancedModelBox(this);
        leftEar.setRotationPoint(2.0F, -1.0F, -2.0F);
        body.addChild(leftEar);
        setRotationAngle(leftEar, -0.6981F, -0.6545F, 1.0036F);
        leftEar.setTextureOffset(0, 0).addBox(-2.0F, -5.0F, 0.0F, 4.0F, 5.0F, 0.0F, 0.0F, false);

        rightEar = new AdvancedModelBox(this);
        rightEar.setRotationPoint(-2.0F, -1.0F, -2.0F);
        body.addChild(rightEar);
        setRotationAngle(rightEar, -0.6981F, 0.6545F, -1.0036F);
        rightEar.setTextureOffset(0, 0).addBox(-2.0F, -5.0F, 0.0F, 4.0F, 5.0F, 0.0F, 0.0F, true);

        leftArm = new AdvancedModelBox(this);
        leftArm.setRotationPoint(1.0F, 2.0F, -2.0F);
        body.addChild(leftArm);
        setRotationAngle(leftArm, 1.309F, 0.0F, 0.0F);
        leftArm.setTextureOffset(0, 6).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this);
        rightArm.setRotationPoint(-1.0F, 2.0F, -2.0F);
        body.addChild(rightArm);
        setRotationAngle(rightArm, 1.309F, 0.0F, 0.0F);
        rightArm.setTextureOffset(0, 6).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 1.0F, 4.0F);
        body.addChild(tail);
        setRotationAngle(tail, -0.3491F, 0.0F, 0.0F);
        tail.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, 0.0F, 3.0F, 6.0F, 12.0F, 0.0F, false);

        legs = new AdvancedModelBox(this);
        legs.setRotationPoint(0.0F, 1.9F, 2.8F);
        body.addChild(legs);
        setRotationAngle(legs, 0.5236F, 0.0F, 0.0F);
        legs.setTextureOffset(19, 0).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 3.0F, 5.0F, 0.0F, false);

        feet = new AdvancedModelBox(this);
        feet.setRotationPoint(0.0F, 3.0F, -5.0F);
        legs.addChild(feet);
        setRotationAngle(feet, -0.5236F, 0.0F, 0.0F);
        feet.setTextureOffset(19, 9).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leftEar, rightEar, leftArm, rightArm, tail, legs, feet);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);

    }

    @Override
    public void setupAnim(EntityJerboa entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float sleepProgress = 0;

    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}