package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelTiger extends AdvancedEntityModel<EntityTiger> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox head;
    private final AdvancedModelBox earleft;
    private final AdvancedModelBox earright;
    private final AdvancedModelBox snout;
    private final AdvancedModelBox legleft;
    private final AdvancedModelBox legright;
    private final AdvancedModelBox armleft;
    private final AdvancedModelBox armright;
    private ModelAnimator animator;

    public ModelTiger() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -14.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-5.0F, -6.0F, -12.0F, 10.0F, 11.0F, 22.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.0F, -4.0F, 8.6F);
        body.addChild(tail);
        setRotationAngle(tail, 0.0873F, 0.0F, 0.0F);
        tail.setTextureOffset(46, 34).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 9.0F, 3.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this, "tail2");
        tail2.setPos(0.0F, 7.9F, 0.0F);
        tail.addChild(tail2);
        setRotationAngle(tail2, 0.2182F, 0.0F, 0.0F);
        tail2.setTextureOffset(43, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 9.0F, 3.0F, -0.1F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, -4.0F, -12.0F);
        body.addChild(head);
        head.setTextureOffset(0, 34).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 7.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(9, 15).addBox(4.0F, -1.0F, -5.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(9, 15).addBox(-5.0F, -1.0F, -5.0F, 1.0F, 4.0F, 2.0F, 0.0F, true);

        earleft = new AdvancedModelBox(this, "earleft");
        earleft.setPos(3.0F, -4.0F, -2.0F);
        head.addChild(earleft);
        earleft.setTextureOffset(0, 15).addBox(0.0F, -2.0F, -2.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

        earright = new AdvancedModelBox(this, "earright");
        earright.setPos(-3.0F, -4.0F, -2.0F);
        head.addChild(earright);
        earright.setTextureOffset(0, 15).addBox(-1.0F, -2.0F, -2.0F, 1.0F, 2.0F, 3.0F, 0.0F, true);

        snout = new AdvancedModelBox(this, "snout");
        snout.setPos(0.0F, -1.0F, -6.0F);
        head.addChild(snout);
        setRotationAngle(snout, 0.1745F, 0.0F, 0.0F);
        snout.setTextureOffset(43, 13).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);

        legleft = new AdvancedModelBox(this, "legleft");
        legleft.setPos(2.9F, 5.0F, 7.9F);
        body.addChild(legleft);
        legleft.setTextureOffset(0, 48).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 11.0F, 5.0F, 0.0F, false);

        legright = new AdvancedModelBox(this, "legright");
        legright.setPos(-2.9F, 5.0F, 7.9F);
        body.addChild(legright);
        legright.setTextureOffset(0, 48).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 11.0F, 5.0F, 0.0F, true);

        armleft = new AdvancedModelBox(this, "armleft");
        armleft.setPos(3.5F, -1.5F, -9.0F);
        body.addChild(armleft);
        armleft.setTextureOffset(29, 34).addBox(-2.0F, -5.5F, -2.0F, 4.0F, 21.0F, 4.0F, 0.0F, false);

        armright = new AdvancedModelBox(this, "armright");
        armright.setPos(-3.5F, -1.5F, -9.0F);
        body.addChild(armright);
        armright.setTextureOffset(29, 34).addBox(-2.0F, -5.5F, -2.0F, 4.0F, 21.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityTiger.ANIMATION_PAW_R);
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(10F), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(10F), 0, 0);
        animator.rotate(armleft, (float) Math.toRadians(-20F), 0, 0);
        animator.rotate(armright, (float) Math.toRadians(-20F), 0, 0);
        animator.move(body, 0, 0, 3F);
        animator.move(armright, 0, 1, 0);
        animator.move(armleft, 0, 1, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, (float) Math.toRadians(35F), 0, 0);
        animator.rotate(armleft, (float) Math.toRadians(-30F), 0, 0);
        animator.rotate(armright, (float) Math.toRadians(-70F), 0, (float) Math.toRadians(20F));
        animator.rotate(tail, (float) Math.toRadians(50F), 0, 0);
        animator.rotate(tail2, (float) Math.toRadians(10F), 0, 0);
        animator.move(body, 0, -3, -3);
        animator.rotate(body, (float) Math.toRadians(-30F), 0, 0);
        animator.rotate(legleft, (float) Math.toRadians(30F), 0, 0);
        animator.rotate(legright, (float) Math.toRadians(30F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(head, (float) Math.toRadians(35F), 0, 0);
        animator.rotate(armleft, (float) Math.toRadians(10F), 0, (float) Math.toRadians(-10F));
        animator.rotate(armright, (float) Math.toRadians(-40F), 0, (float) Math.toRadians(-20F));
        animator.rotate(tail, (float) Math.toRadians(50F), 0, 0);
        animator.rotate(tail2, (float) Math.toRadians(10F), 0, 0);
        animator.move(body, 0, -3, -3);
        animator.move(armright, -1, 0, 0);
        animator.rotate(body, (float) Math.toRadians(-30F), 0, 0);
        animator.rotate(legleft, (float) Math.toRadians(30F), 0, 0);
        animator.rotate(legright, (float) Math.toRadians(30F), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityTiger.ANIMATION_PAW_L);
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(10F), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(10F), 0, 0);
        animator.rotate(armleft, (float) Math.toRadians(-20F), 0, 0);
        animator.rotate(armright, (float) Math.toRadians(-20F), 0, 0);
        animator.move(body, 0, 0, 3F);
        animator.move(armright, 0, 1, 0);
        animator.move(armleft, 0, 1, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, (float) Math.toRadians(35F), 0, 0);
        animator.rotate(armleft, (float) Math.toRadians(-70F), 0, (float) Math.toRadians(-20F));
        animator.rotate(armright, (float) Math.toRadians(-30F), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(50F), 0, 0);
        animator.rotate(tail2, (float) Math.toRadians(10F), 0, 0);
        animator.move(body, 0, -3, -3);
        animator.rotate(body, (float) Math.toRadians(-30F), 0, 0);
        animator.rotate(legleft, (float) Math.toRadians(30F), 0, 0);
        animator.rotate(legright, (float) Math.toRadians(30F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(head, (float) Math.toRadians(35F), 0, 0);
        animator.rotate(armleft, (float) Math.toRadians(-40F), 0, (float) Math.toRadians(10F));
        animator.rotate(armright, (float) Math.toRadians(10F), 0, (float) Math.toRadians(20F));
        animator.rotate(tail, (float) Math.toRadians(50F), 0, 0);
        animator.rotate(tail2, (float) Math.toRadians(10F), 0, 0);
        animator.move(body, 0, -3, -3);
        animator.move(armleft, 1, 0, 0);
        animator.rotate(body, (float) Math.toRadians(-30F), 0, 0);
        animator.rotate(legleft, (float) Math.toRadians(30F), 0, 0);
        animator.rotate(legright, (float) Math.toRadians(30F), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityTiger.ANIMATION_TAIL_FLICK);
        animator.startKeyframe(5);
        animator.rotate(tail, (float) Math.toRadians(10F), 0, (float) Math.toRadians(30F));
        animator.rotate(tail2, (float) Math.toRadians(10F), 0, (float) Math.toRadians(20F));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(tail, (float) Math.toRadians(10F), 0, (float) Math.toRadians(-30F));
        animator.rotate(tail2, (float) Math.toRadians(10F), 0, (float) Math.toRadians(-20F));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(tail, (float) Math.toRadians(10F), 0, (float) Math.toRadians(30F));
        animator.rotate(tail2, (float) Math.toRadians(10F), 0, (float) Math.toRadians(20F));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityTiger.ANIMATION_LEAP);
        animator.startKeyframe(5);
        animator.move(body, 0, 1, 3F);
        animator.move(head, 0, 2, 0);
        animator.move(armright, 0, 1, 2);
        animator.move(armleft, 0, 1, 2);
        animator.rotate(head, (float) Math.toRadians(-15F), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(10F), 0, 0);
        animator.rotate(body, (float) Math.toRadians(10F), 0, 0);
        animator.rotate(armleft, (float) Math.toRadians(-50F), 0, 0);
        animator.rotate(armright, (float) Math.toRadians(-50F), 0, 0);
        animator.rotate(legright, (float) Math.toRadians(-20F), 0, 0);
        animator.rotate(legleft, (float) Math.toRadians(-20F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(tail, (float) Math.toRadians(90F), 0, 0);
        animator.rotate(tail2, (float) Math.toRadians(-10F), 0, 0);
        animator.rotate(legleft, (float) Math.toRadians(30F), 0, (float) Math.toRadians(-10F));
        animator.rotate(legright, (float) Math.toRadians(30F), 0, (float) Math.toRadians(10F));
        animator.rotate(armright, (float) Math.toRadians(-60F), 0, (float) Math.toRadians(30F));
        animator.rotate(armleft, (float) Math.toRadians(-60F), 0, (float) Math.toRadians(-30F));
        animator.move(armright, -1, -1, 1);
        animator.move(armleft, 1, -1, 1);
        animator.move(legright, 0, -4, -1);
        animator.move(legleft, 0, -4, -1);
        animator.move(body, 0, -5, 4);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);


    }

    @Override
    public void setupAnim(EntityTiger entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.8F;
        float runSpeed = 1.0F;
        float runDegree = 0.8F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float moveProgress = 5F * limbSwingAmount;
        float partialTick = ageInTicks - entity.tickCount;
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
        float holdProgress = entity.prevHoldProgress + (entity.holdProgress - entity.prevHoldProgress) * partialTick;
        float sleepProgress = entity.prevSleepProgress + (entity.sleepProgress - entity.prevSleepProgress) * partialTick;
        boolean leftSleep = entity.getId() % 2 == 0;
        this.walk(tail, idleSpeed, idleDegree * 1F, false, -2F, 0.1F, ageInTicks, 1);
        this.flap(tail, idleSpeed, idleDegree * 1.2F, false, 2F, 0F, ageInTicks, 1);
        this.flap(tail2, idleSpeed, idleDegree * 2F, false, 2F, 0F, ageInTicks, 1);
        AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{tail, tail2};
        progressRotationPrev(tail, moveProgress, (float) Math.toRadians(40), 0, 0, 5F);
        progressPositionPrev(head, Math.min(moveProgress * 2F, 5F), 0, 2, 0, 5F);
        progressPositionPrev(tail, moveProgress, 0, 1, 0, 5F);
        progressPositionPrev(body, moveProgress, 0, 1, 0, 5F);
        progressPositionPrev(armleft, moveProgress, 0, -1, 0, 5F);
        progressPositionPrev(armright, moveProgress, 0, -1, 0, 5F);
        progressPositionPrev(legleft, moveProgress, 0, -1, 0, 5F);
        progressPositionPrev(legright, moveProgress, 0, -1, 0, 5F);
        if (entity.isRunning()) {
            this.chainFlap(tailBoxes, runSpeed, runDegree * 0.5F, -1, limbSwing, limbSwingAmount);
            this.bob(body, runSpeed, runDegree * 2, false, limbSwing, limbSwingAmount);
            this.bob(head, runSpeed, runDegree * -1, false, limbSwing, limbSwingAmount);
            this.walk(armleft, runSpeed, runDegree * 0.75F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(armright, runSpeed, runDegree * 0.75F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(legright, runSpeed, runDegree * 1F, false, 0.5F, 0F, limbSwing, limbSwingAmount);
            this.walk(legleft, runSpeed, runDegree * 1F, false, 0.5F, 0F, limbSwing, limbSwingAmount);
        } else {
            this.chainFlap(tailBoxes, walkSpeed, walkDegree * 1F, -1, limbSwing, limbSwingAmount);
            this.flap(body, walkSpeed, walkDegree * 0.3F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(head, walkSpeed, -walkDegree * 0.3F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(armleft, walkSpeed, -walkDegree * 0.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(armright, walkSpeed, -walkDegree * 0.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(legleft, walkSpeed, -walkDegree * 0.3F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(legright, walkSpeed, -walkDegree * 0.3F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(armright, walkSpeed, walkDegree * 0.5F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.bob(armright, walkSpeed, -walkDegree, false, limbSwing, limbSwingAmount);
            this.walk(armleft, walkSpeed, walkDegree * 0.5F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.bob(armleft, walkSpeed, -walkDegree, false, limbSwing, limbSwingAmount);
            this.walk(legright, walkSpeed, walkDegree * 0.8F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.bob(legright, walkSpeed, -walkDegree, false, limbSwing, limbSwingAmount);
            this.walk(legleft, walkSpeed, walkDegree * 0.8F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.bob(legleft, walkSpeed, -walkDegree, false, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        }
        progressRotationPrev(legleft, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-20), 0, 5F);
        progressRotationPrev(legright, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(20), 0, 5F);
        progressRotationPrev(armleft, sitProgress, (float) Math.toRadians(-50), 0, 0, 5F);
        progressRotationPrev(armright, sitProgress, (float) Math.toRadians(-50), 0, 0, 5F);
        float tailAngle = entity.getId() % 2 == 0 ? 1 : -1;
        progressRotationPrev(tail, sitProgress, (float) Math.toRadians(20), (float) Math.toRadians(tailAngle * -15), (float) Math.toRadians(tailAngle * 15), 5F);
        progressRotationPrev(tail2, sitProgress, (float) Math.toRadians(20), (float) Math.toRadians(tailAngle * -30), (float) Math.toRadians(tailAngle * 30), 5F);
        progressPositionPrev(body, sitProgress, 0, 5F, 0, 5F);
        progressPositionPrev(tail, sitProgress, 0, 2F, 0, 5F);
        progressPositionPrev(tail2, sitProgress, tailAngle, 0, 0, 5F);
        progressPositionPrev(armright, sitProgress, 0, -1F, 4, 5F);
        progressPositionPrev(armleft, sitProgress, 0, -1F, 4, 5F);
        progressPositionPrev(legright, sitProgress, 0, 2.8F, -0.5F, 5F);
        progressPositionPrev(legleft, sitProgress, 0, 2.8F, -0.5F, 5F);
        if(leftSleep){
			progressRotationPrev(body, sleepProgress, 0, 0, (float) Math.toRadians(-90), 5F);
			progressRotationPrev(head, sleepProgress, 0, 0, (float) Math.toRadians(73), 5F);
			progressRotationPrev(tail, sleepProgress, 0, 0, (float) Math.toRadians(20), 5F);
			progressRotationPrev(tail2, sleepProgress, 0, 0, (float) Math.toRadians(-20), 5F);
			progressRotationPrev(armleft, sleepProgress, (float) Math.toRadians(-10), 0, (float) Math.toRadians(10), 5F);
			progressRotationPrev(armright, sleepProgress, (float) Math.toRadians(-20), 0, 0, 5F);
			progressRotationPrev(legright, sleepProgress, (float) Math.toRadians(-20), 0, 0, 5F);
			progressRotationPrev(legleft, sleepProgress, (float) Math.toRadians(10), 0, (float) Math.toRadians(20), 5F);
			progressPositionPrev(armleft, sleepProgress, 1F, -1F, 0, 5F);
			progressPositionPrev(armright, sleepProgress, 0.5F, -1, 1, 5F);
			progressPositionPrev(body, sleepProgress, 0, 9, 0, 5F);
			progressPositionPrev(head, sleepProgress, 0, 1, 0, 5F);
		}else{
			progressRotationPrev(body, sleepProgress, 0, 0, (float) Math.toRadians(90), 5F);
			progressRotationPrev(head, sleepProgress, 0, 0, (float) Math.toRadians(-73), 5F);
			progressRotationPrev(tail, sleepProgress, 0, 0, (float) Math.toRadians(-20), 5F);
			progressRotationPrev(tail2, sleepProgress, 0, 0, (float) Math.toRadians(20), 5F);
			progressRotationPrev(armright, sleepProgress, (float) Math.toRadians(-10), 0, (float) Math.toRadians(-10), 5F);
			progressRotationPrev(armleft, sleepProgress, (float) Math.toRadians(-20), 0, 0, 5F);
			progressRotationPrev(legleft, sleepProgress, (float) Math.toRadians(-20), 0, 0, 5F);
			progressRotationPrev(legright, sleepProgress, (float) Math.toRadians(10), 0, (float) Math.toRadians(-20), 5F);
			progressPositionPrev(armright, sleepProgress, -1, -1F, 0, 5F);
			progressPositionPrev(armleft, sleepProgress, -0.5F, -1, 1, 5F);
			progressPositionPrev(body, sleepProgress, 0, 9, 0, 5F);
			progressPositionPrev(head, sleepProgress, 0, 1, 0, 5F);
		}
        progressRotationPrev(body, holdProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(tail, holdProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(legleft, holdProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(legright, holdProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(armright, holdProgress, (float) Math.toRadians(-60), (float) Math.toRadians(-5), 0, 5F);
        progressRotationPrev(armleft, holdProgress, (float) Math.toRadians(-60), (float) Math.toRadians(5), 0, 5F);
        progressPositionPrev(body, holdProgress, 0, 3, 0, 5F);
        progressPositionPrev(head, holdProgress, 0, -1, 1, 5F);
        progressPositionPrev(armleft, holdProgress, 2, -2, -1, 5F);
        progressPositionPrev(armright, holdProgress, -2, -2, -1, 5F);
        this.flap(head, 0.85F, 0.3F, false, 0F, 0F, ageInTicks, holdProgress * 0.2F);
        this.flap(tail, 0.85F, 0.3F, false, 0F, 0F, ageInTicks, holdProgress * 0.2F);
        this.flap(tail2, 0.85F, 0.3F, false, 0F, 0F, ageInTicks, holdProgress * 0.2F);
        this.flap(earleft, 0.85F, 0.3F, false, -1, 0F, ageInTicks, holdProgress * 0.2F);
        this.flap(earright, 0.85F, 0.3F, false, -1, 0F, ageInTicks, holdProgress * 0.2F);
		if(sleepProgress == 0){
			this.faceTarget(netHeadYaw, headPitch, 1.2F, head);
		}
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, tail, tail2, snout, earleft, earright, legleft, legright, armleft, armright);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
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