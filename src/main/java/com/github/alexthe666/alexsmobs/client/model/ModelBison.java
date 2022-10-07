package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBison;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;

public class ModelBison extends AdvancedEntityModel<EntityBison> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox right_leg;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail_r1;
    private final AdvancedModelBox torso;
    private final AdvancedModelBox head;
    private final AdvancedModelBox horn_r1;
    private final AdvancedModelBox left_ear;
    private final AdvancedModelBox right_ear;
    private final AdvancedModelBox beard;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox right_arm;
    private final ModelAnimator animator;

    public ModelBison() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -23.0F, 4.0F);
        root.addChild(body);
        body.setTextureOffset(0, 54).addBox(-9.0F, -11.0F, -1.0F, 18.0F, 20.0F, 19.0F, 0.0F, false);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(5.8F, 5.0F, 14.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(75, 80).addBox(-3.0F, 4.0F, -3.0F, 6.0F, 14.0F, 7.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-5.8F, 5.0F, 14.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(75, 80).addBox(-3.0F, 4.0F, -3.0F, 6.0F, 14.0F, 7.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -6.0F, 18.0F);
        body.addChild(tail);

        tail_r1 = new AdvancedModelBox(this, "tail_r1");
        tail_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        tail.addChild(tail_r1);
        setRotationAngle(tail_r1, 0.0436F, 0.0F, 0.0F);
        tail_r1.setTextureOffset(0, 54).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F, 0.0F, false);

        torso = new AdvancedModelBox(this, "torso");
        torso.setRotationPoint(0.0F, -3.0F, 1.0F);
        body.addChild(torso);
        torso.setTextureOffset(0, 0).addBox(-10.0F, -14.0F, -28.0F, 20.0F, 27.0F, 26.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, 0.0F, -27.0F);
        torso.addChild(head);
        setRotationAngle(head, -0.2618F, 0.0F, 0.0F);
        head.setTextureOffset(76, 54).addBox(-4.0F, 0.0F, -8.0F, 8.0F, 15.0F, 10.0F, 0.0F, false);
        head.setTextureOffset(67, 0).addBox(-6.0F, -6.0F, -9.9F, 12.0F, 10.0F, 12.0F, 0.0F, false);

        horn_r1 = new AdvancedModelBox(this, "horn_r1");
        horn_r1.setRotationPoint(-7.0F, 0.5F, -5.0F);
        head.addChild(horn_r1);
        setRotationAngle(horn_r1, 0.3927F, 0.0F, 0.0F);
        horn_r1.setTextureOffset(0, 0).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, true);
        horn_r1.setTextureOffset(11, 1).addBox(1.0F, -0.5F, -1.0F, 3.0F, 3.0F, 2.0F, 0.0F, true);
        horn_r1.setTextureOffset(11, 1).addBox(10.0F, -0.5F, -1.0F, 3.0F, 3.0F, 2.0F, 0.0F, false);
        horn_r1.setTextureOffset(0, 0).addBox(13.0F, -4.5F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        left_ear = new AdvancedModelBox(this, "left_ear");
        left_ear.setRotationPoint(4.0F, 3.0F, -3.0F);
        head.addChild(left_ear);
        setRotationAngle(left_ear, 0.0F, -0.6981F, 0.4363F);
        left_ear.setTextureOffset(0, 23).addBox(0.0F, -1.0F, 0.0F, 5.0F, 2.0F, 1.0F, 0.0F, false);

        right_ear = new AdvancedModelBox(this, "right_ear");
        right_ear.setRotationPoint(-4.0F, 3.0F, -3.0F);
        head.addChild(right_ear);
        setRotationAngle(right_ear, 0.0F, 0.6981F, -0.4363F);
        right_ear.setTextureOffset(0, 23).addBox(-5.0F, -1.0F, 0.0F, 5.0F, 2.0F, 1.0F, 0.0F, true);

        beard = new AdvancedModelBox(this, "beard");
        beard.setRotationPoint(0.0F, 15.0F, 1.0F);
        head.addChild(beard);
        setRotationAngle(beard, 0.2182F, 0.0F, 0.0F);
        beard.setTextureOffset(0, 0).addBox(0.0F, -5.0F, -5.0F, 0.0F, 13.0F, 10.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(7.8F, 10.0F, -15.0F);
        torso.addChild(left_arm);
        left_arm.setTextureOffset(93, 23).addBox(-3.0F, 3.0F, -3.0F, 5.0F, 13.0F, 5.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-7.8F, 10.0F, -15.0F);
        torso.addChild(right_arm);
        right_arm.setTextureOffset(93, 23).addBox(-2.0F, 3.0F, -3.0F, 5.0F, 13.0F, 5.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityBison.ANIMATION_PREPARE_CHARGE);
        animator.startKeyframe(5);
        animator.rotate(head, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(torso, 0, (float) Math.toRadians(15), 0);
        animator.rotate(body, 0, (float) Math.toRadians(5), 0);
        animator.rotate(right_arm, (float) Math.toRadians(30), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.startKeyframe(5);
        animator.rotate(head, 0, (float) Math.toRadians(20), 0);
        animator.rotate(torso, 0, (float) Math.toRadians(-15), 0);
        animator.rotate(body, 0, (float) Math.toRadians(-5), 0);
        animator.rotate(left_arm, (float) Math.toRadians(30), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);

        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(40), (float) Math.toRadians(-20), 0);
        animator.rotate(torso, 0, (float) Math.toRadians(15), 0);
        animator.rotate(body, 0, (float) Math.toRadians(5), 0);
        animator.rotate(right_arm, (float) Math.toRadians(30), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(40), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(40), (float) Math.toRadians(20), 0);
        animator.rotate(torso, 0, (float) Math.toRadians(-15), 0);
        animator.rotate(body, 0, (float) Math.toRadians(-5), 0);
        animator.rotate(left_arm, (float) Math.toRadians(30), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityBison.ANIMATION_EAT);
        animator.startKeyframe(5);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(5);
        eatPose();
        animator.move(head, 0, 1, 1);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(5);
        eatPose();
        animator.move(head, 0, 1, 1);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(5);
        eatPose();
        animator.move(head, 0, 1, 1);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityBison.ANIMATION_ATTACK);
        animator.startKeyframe(5);
        animator.move(head, 0, 2, 1);
        animator.rotate(head, (float) Math.toRadians(30), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, -2, 0);
        animator.move(left_arm, 0, -1, 0);
        animator.move(right_arm, 0, -1, 0);
        animator.rotate(head, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(body, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(left_leg, (float) Math.toRadians(10), 0, 0);
        animator.rotate(right_leg, (float) Math.toRadians(10), 0, 0);
        animator.rotate(left_arm, (float) Math.toRadians(20), 0, 0);
        animator.rotate(right_arm, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);

    }

    private void eatPose(){
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(torso, (float) Math.toRadians(15), 0, 0);
        animator.move(torso, 0, 0, 2);
        animator.rotate(left_arm, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(right_arm, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(beard, (float) Math.toRadians(20), 0, 0);
        animator.move(left_arm, 0, -5F, -1F);
        animator.move(right_arm, 0, -5F, -1F);
        animator.move(head, 0, 4, 1);
    }

    @Override
    public void setupAnim(EntityBison entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float partialTick = ageInTicks - entity.tickCount;
        float runProgress = entity.prevChargeProgress + (entity.chargeProgress - entity.prevChargeProgress) * partialTick;
        progressPositionPrev(head, runProgress, 0, 1, -3.5F, 5F);
        progressRotationPrev(head, runProgress, (float) Math.toRadians(30), 0, 0, 5F);
        if (runProgress > 0) {
            this.walk(right_arm, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(left_arm, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(right_arm, walkSpeed, walkDegree * 0.25F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(left_arm, walkSpeed, walkDegree * 0.25F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(right_leg, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(left_leg, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(right_leg, walkSpeed, walkDegree * 0.25F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(left_leg, walkSpeed, walkDegree * 0.25F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(tail, walkSpeed, walkDegree * 0.2F, true, 1F, -0.6F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed * 0.5F, walkDegree * 5F, true, limbSwing, limbSwingAmount);
            this.bob(head, walkSpeed * 0.5F, -walkDegree * 2F, false, limbSwing, limbSwingAmount);
        } else {
            this.walk(right_arm, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(left_arm, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(right_leg, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(left_leg, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(tail, walkSpeed, walkDegree * 0.1F, true, 1F, -0.6F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed, walkDegree, true, limbSwing, limbSwingAmount);
            this.bob(head, walkSpeed, -walkDegree, false, limbSwing, limbSwingAmount);
        }
        this.flap(beard, idleSpeed, idleDegree, false, 2F, 0F, ageInTicks, 1);
        this.swing(left_ear, idleSpeed, idleDegree * 0.5F, true, 3F, -0.2F, ageInTicks, 1);
        this.swing(right_ear, idleSpeed, idleDegree * 0.5F, true, 3F, 0.2F, ageInTicks, 1);
        this.walk(tail, idleSpeed, idleDegree, false, 1F, 0.1F, ageInTicks, 1);
        this.bob(head, idleSpeed, idleDegree, false, ageInTicks, 1);
        this.head.rotateAngleY += netHeadYaw * 0.35F * ((float)Math.PI / 180F);
        this.head.rotateAngleX += headPitch * ((float)Math.PI / 180F);

    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, left_arm, right_arm, head, tail, tail_r1, horn_r1, beard, left_leg, right_leg, left_ear, right_ear, torso);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }
}