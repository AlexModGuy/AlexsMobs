package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelBunfungus extends AdvancedEntityModel<EntityBunfungus> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox belly;
    public final AdvancedModelBox tail;
    public final AdvancedModelBox head;
    public final AdvancedModelBox left_brow;
    public final AdvancedModelBox right_brow;
    public final AdvancedModelBox shroom_cap;
    public final AdvancedModelBox left_ear;
    public final AdvancedModelBox right_ear;
    public final AdvancedModelBox snout;
    public final AdvancedModelBox snout_r1;
    public final AdvancedModelBox left_arm;
    public final AdvancedModelBox right_arm;
    public final AdvancedModelBox left_leg;
    public final AdvancedModelBox left_foot;
    public final AdvancedModelBox right_leg;
    public final AdvancedModelBox right_foot;
    private ModelAnimator animator;

    public ModelBunfungus() {
        texWidth = 256;
        texHeight = 256;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -13.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 19.0F, 0.0F, false);

        belly = new AdvancedModelBox(this);
        belly.setRotationPoint(0.0F, 4.0F, -4.3F);
        body.addChild(belly);
        belly.setTextureOffset(64, 25).addBox(-11.0F, -7.0F, -7.5F, 22.0F, 14.0F, 15.0F, -2.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 10.0F, 9.0F);
        body.addChild(tail);
        tail.setTextureOffset(60, 0).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -10.0F, -6.0F);
        body.addChild(head);
        head.setTextureOffset(0, 66).addBox(-6.0F, -5.0F, -9.0F, 12.0F, 8.0F, 13.0F, 0.0F, false);

        left_brow = new AdvancedModelBox(this);
        left_brow.setRotationPoint(3.5F, -3.5F, -9.1F);
        head.addChild(left_brow);
        left_brow.setTextureOffset(90, 2).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, 0.0F, false);

        right_brow = new AdvancedModelBox(this);
        right_brow.setRotationPoint(-3.5F, -3.5F, -9.1F);
        head.addChild(right_brow);
        right_brow.setTextureOffset(90, 2).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, 0.0F, true);

        shroom_cap = new AdvancedModelBox(this);
        shroom_cap.setRotationPoint(0.0F, -5.0F, -4.0F);
        head.addChild(shroom_cap);
        shroom_cap.setTextureOffset(0, 40).addBox(-10.0F, -5.0F, -8.0F, 20.0F, 5.0F, 20.0F, 0.0F, false);

        left_ear = new AdvancedModelBox(this);
        left_ear.setRotationPoint(3.0F, -4.0F, 1.0F);
        shroom_cap.addChild(left_ear);
        setRotationAngle(left_ear, 0.0F, -0.6981F, 0.2182F);
        left_ear.setTextureOffset(0, 0).addBox(-2.0F, -12.0F, -1.0F, 4.0F, 12.0F, 2.0F, 0.0F, false);

        right_ear = new AdvancedModelBox(this);
        right_ear.setRotationPoint(-3.0F, -4.0F, 1.0F);
        shroom_cap.addChild(right_ear);
        setRotationAngle(right_ear, 0.0F, 0.6981F, -0.2182F);
        right_ear.setTextureOffset(0, 0).addBox(-2.0F, -12.0F, -1.0F, 4.0F, 12.0F, 2.0F, 0.0F, true);

        snout = new AdvancedModelBox(this);
        snout.setRotationPoint(0.0F, 0.0F, -10.0F);
        head.addChild(snout);
        snout.setTextureOffset(0, 40).addBox(-3.0F, -1.0F, -1.0F, 6.0F, 4.0F, 2.0F, 0.0F, false);

        snout_r1 = new AdvancedModelBox(this);
        snout_r1.setRotationPoint(0.0F, 0.0F, -1.0F);
        snout.addChild(snout_r1);
        setRotationAngle(snout_r1, -0.1309F, 0.0F, 0.0F);
        snout_r1.setTextureOffset(0, 48).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this);
        left_arm.setRotationPoint(9.5F, -4.0F, -9.5F);
        body.addChild(left_arm);
        setRotationAngle(left_arm, 0.0F, 0.0F, 0.1745F);
        left_arm.setTextureOffset(51, 77).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 8.0F, 5.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this);
        right_arm.setRotationPoint(-9.5F, -4.0F, -9.5F);
        body.addChild(right_arm);
        setRotationAngle(right_arm, 0.0F, 0.0F, -0.1745F);
        right_arm.setTextureOffset(51, 77).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 8.0F, 5.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this);
        left_leg.setRotationPoint(6.0F, 2.0F, 0.0F);
        body.addChild(left_leg);


        left_foot = new AdvancedModelBox(this);
        left_foot.setRotationPoint(0.0F, 8.0F, 0.0F);
        left_leg.addChild(left_foot);
        setRotationAngle(left_foot, 0.0F, -1.1345F, 0.0F);
        left_foot.setTextureOffset(64, 55).addBox(-3.0F, -1.0F, -15.0F, 6.0F, 4.0F, 17.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this);
        right_leg.setRotationPoint(-6.0F, 2.0F, 0.0F);
        body.addChild(right_leg);


        right_foot = new AdvancedModelBox(this);
        right_foot.setRotationPoint(0.0F, 8.0F, 0.0F);
        right_leg.addChild(right_foot);
        setRotationAngle(right_foot, 0.0F, 1.1345F, 0.0F);
        right_foot.setTextureOffset(64, 55).addBox(-3.0F, -1.0F, -15.0F, 6.0F, 4.0F, 17.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        animator.update(entity);
        animator.setAnimation(EntityBunfungus.ANIMATION_EAT);
        animator.startKeyframe(4);
        animator.rotate(head, (float)Math.toRadians(30), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(-140), (float)Math.toRadians(-20), (float)Math.toRadians(70));
        animator.rotate(left_arm, (float)Math.toRadians(-140), (float)Math.toRadians(20), (float)Math.toRadians(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(head, (float)Math.toRadians(20), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(-140), (float)Math.toRadians(-10), (float)Math.toRadians(70));
        animator.rotate(left_arm, (float)Math.toRadians(-140), (float)Math.toRadians(10), (float)Math.toRadians(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(head, (float)Math.toRadians(30), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(-140), (float)Math.toRadians(-20), (float)Math.toRadians(70));
        animator.rotate(left_arm, (float)Math.toRadians(-140), (float)Math.toRadians(20), (float)Math.toRadians(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(head, (float)Math.toRadians(20), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(-140), (float)Math.toRadians(-10), (float)Math.toRadians(70));
        animator.rotate(left_arm, (float)Math.toRadians(-140), (float)Math.toRadians(10), (float)Math.toRadians(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
        animator.endKeyframe();
        animator.setAnimation(EntityBunfungus.ANIMATION_BELLY);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(20), 0, 0);
        animator.rotate(body, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(right_arm, (float)Math.toRadians(-120), (float)Math.toRadians(30), (float)Math.toRadians(-10));
        animator.rotate(left_arm, (float)Math.toRadians(-120), (float)Math.toRadians(-30), (float)Math.toRadians(10));
        animator.move(belly, 0, 0, -10);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityBunfungus.ANIMATION_SLAM);
        animator.startKeyframe(5);
        animator.move(root, 0, 0, -20);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);

    }
    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, shroom_cap, left_ear, right_ear, left_brow, right_brow, snout, snout_r1, left_arm, right_arm, left_leg, right_leg, tail, belly, left_foot, right_foot);
    }

    @Override
    public void setupAnim(EntityBunfungus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float walkSpeed = 0.7F;
        float walkDegree = 2F;
        float partialTicks = ageInTicks - entity.tickCount;
        float sleepProgress = entity.prevSleepProgress + (entity.sleepProgress - entity.prevSleepProgress) * partialTicks;
        float fallProgress = entity.prevReboundProgress + (entity.reboundProgress - entity.prevReboundProgress) * partialTicks;
        float jumpProgress = Math.max(0, entity.prevJumpProgress + (entity.jumpProgress - entity.prevJumpProgress) * partialTicks - fallProgress);
        float interestedProgress = entity.prevInterestedProgress + (entity.interestedProgress - entity.prevInterestedProgress) * partialTicks;
        float walkMod = 1F - (Math.max(jumpProgress, fallProgress) * 0.2F);
        float limbSwingMod = Math.min(limbSwingAmount, 0.38F) * walkMod;
        progressRotationPrev(body, sleepProgress, (float) Math.toRadians(90), 0, 0, 5F);
        progressRotationPrev(tail, sleepProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(right_ear, sleepProgress, (float) Math.toRadians(50), (float) Math.toRadians(80), 0, 5F);
        progressRotationPrev(left_ear, sleepProgress, (float) Math.toRadians(50), (float) Math.toRadians(-80), 0, 5F);
        progressRotationPrev(head, sleepProgress, (float) Math.toRadians(-95), 0, (float) Math.toRadians(-5), 5F);
        progressRotationPrev(left_arm, sleepProgress, (float) Math.toRadians(-170), (float) Math.toRadians(-10), (float) Math.toRadians(35), 5F);
        progressRotationPrev(right_arm, sleepProgress, (float) Math.toRadians(-170), (float) Math.toRadians(10), (float) Math.toRadians(-35), 5F);
        progressRotationPrev(left_foot, sleepProgress, (float) Math.toRadians(70), (float) Math.toRadians(-30), 0, 5F);
        progressRotationPrev(right_foot, sleepProgress, (float) Math.toRadians(70), (float) Math.toRadians(30), 0, 5F);
        progressPositionPrev(body, sleepProgress, 0, 3, 0, 5F);
        progressPositionPrev(left_leg, sleepProgress, 0, 0, -8, 5F);
        progressPositionPrev(right_leg, sleepProgress, 0, 0, -8, 5F);
        progressPositionPrev(left_arm, sleepProgress, 0, -3, 2, 5F);
        progressPositionPrev(right_arm, sleepProgress, 0, -3, 2, 5F);
        progressPositionPrev(head, sleepProgress, 0, -3, -1, 5F);
        progressRotationPrev(left_foot, limbSwingMod, 0, (float) Math.toRadians(40), 0, 0.38F);
        progressRotationPrev(right_foot, limbSwingMod, 0, (float) Math.toRadians(-40), 0, 0.38F);
        progressRotationPrev(left_ear, limbSwingMod, (float) Math.toRadians(-30), (float) Math.toRadians(30), 0, 0.38F);
        progressRotationPrev(right_ear, limbSwingMod, (float) Math.toRadians(-30), (float) Math.toRadians(-30), 0, 0.38F);
        progressRotationPrev(body, jumpProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(left_foot, jumpProgress, (float) Math.toRadians(70), (float) Math.toRadians(40), 0, 5F);
        progressRotationPrev(right_foot, jumpProgress, (float) Math.toRadians(70), (float) Math.toRadians(-40), 0, 5F);
        progressRotationPrev(right_arm, jumpProgress, (float) Math.toRadians(-70), (float) Math.toRadians(40), 0, 5F);
        progressRotationPrev(left_arm, jumpProgress, (float) Math.toRadians(-70), (float) Math.toRadians(-40), 0, 5F);
        progressPositionPrev(body, jumpProgress, 0, -3, 0, 5F);
        progressPositionPrev(head, jumpProgress, 0, -1, 3, 5F);
        progressRotationPrev(body, fallProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(left_foot, fallProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(right_foot, fallProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(tail, fallProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(head, fallProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(right_arm, fallProgress, (float) Math.toRadians(-130), (float) Math.toRadians(20), 0, 5F);
        progressRotationPrev(left_arm, fallProgress, (float) Math.toRadians(-130), (float) Math.toRadians(-20), 0, 5F);
        progressPositionPrev(body, fallProgress, 0, -1, 0, 5F);
        progressPositionPrev(left_foot, fallProgress, 0, 1, -1, 5F);
        progressPositionPrev(right_foot, fallProgress, 0, 1, -1, 5F);
        progressRotationPrev(head, interestedProgress, 0, (float) Math.toRadians(-20),  (float) Math.toRadians(-10), 5F);
        progressRotationPrev(right_brow, interestedProgress, 0, 0,  (float) Math.toRadians(10), 5F);
        progressPositionPrev(right_brow, interestedProgress, -0.5F,  -0.75F, 0, 5F);
        progressPositionPrev(left_brow, interestedProgress, 0, 0.5F, 0, 5F);
        if(sleepProgress == 0){
            this.faceTarget(netHeadYaw, headPitch, 1.3F, head);
        }
        this.flap(left_ear, idleSpeed, idleDegree, false, 1F, 0.2F, ageInTicks, 1);
        this.flap(right_ear, idleSpeed, idleDegree, true, 1F, 0.2F, ageInTicks, 1);
        this.swing(left_ear, idleSpeed, idleDegree, false, 2F, 0.2F, ageInTicks, 1);
        this.swing(right_ear, idleSpeed, idleDegree, true, 2F, 0.2F, ageInTicks, 1);
        this.walk(tail, idleSpeed, idleDegree, false, 2F, 0.2F, ageInTicks, 1);
        this.walk(right_arm, idleSpeed, idleDegree, false, -2F, -0.1F, ageInTicks, 1);
        this.walk(left_arm, idleSpeed, idleDegree, false, -2F, -0.1F, ageInTicks, 1);
        this.flap(snout_r1, idleSpeed * 8, idleDegree, false, -2F, 0F, ageInTicks, 1);
        this.flap(body, walkSpeed, walkDegree * 0.5F, false, 0F, 0F, limbSwing, limbSwingMod);
        this.swing(body, walkSpeed, walkDegree * 0.5F, false, 1F, 0F, limbSwing, limbSwingMod);
        this.swing(right_foot, walkSpeed, walkDegree * 0.5F, false, -2.5F, 0F, limbSwing, limbSwingMod);
        this.swing(left_foot, walkSpeed, walkDegree * 0.5F, false, -2.5F, 0F, limbSwing, limbSwingMod);
        this.left_foot.rotateAngleX -= (left_leg.rotateAngleX + body.rotateAngleX);
        this.left_foot.rotateAngleZ -= body.rotateAngleZ;
        this.right_foot.rotateAngleX -= (right_leg.rotateAngleX + body.rotateAngleX);
        this.right_foot.rotateAngleZ -= body.rotateAngleZ;
        this.left_leg.rotationPointY += 2F * (float) (Math.sin((double) (limbSwing * walkSpeed) + 2.5F) * (double) limbSwingMod * (double) walkDegree - (double) (limbSwingMod * walkDegree));
        this.right_leg.rotationPointY += 2F * (float) (Math.sin(-(double) (limbSwing * walkSpeed) + 2.5F) * (double) limbSwingMod * (double) walkDegree - (double) (limbSwingMod * walkDegree));
        this.flap(head, walkSpeed, walkDegree * 0.5F, true, 0F, 0F, limbSwing, limbSwingMod);
        this.swing(head, walkSpeed, walkDegree * 0.5F, true, 1F, 0F, limbSwing, limbSwingMod);
        this.flap(tail, walkSpeed, walkDegree * 0.5F, true, 0F, 0F, limbSwing, limbSwingMod);
        this.swing(tail, walkSpeed, walkDegree * 0.5F, false, 2F, 0F, limbSwing, limbSwingMod);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}