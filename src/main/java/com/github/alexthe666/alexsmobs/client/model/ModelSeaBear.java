package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
import com.github.alexthe666.alexsmobs.entity.EntitySeaBear;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelSeaBear extends AdvancedEntityModel<EntitySeaBear> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox head;
	private final AdvancedModelBox snout;
	private final AdvancedModelBox right_ear;
	private final AdvancedModelBox left_ear;
	private final AdvancedModelBox tail;
	private final AdvancedModelBox left_arm;
	private final AdvancedModelBox right_arm;
	private final AdvancedModelBox left_leg;
	private final AdvancedModelBox right_leg;
	private ModelAnimator animator;

	public ModelSeaBear() {
		texWidth = 128;
		texHeight = 128;

		root = new AdvancedModelBox(this, "root");
		root.setRotationPoint(0.0F, 24.0F, 0.0F);

		body = new AdvancedModelBox(this, "body");
		body.setRotationPoint(0.0F, -15.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-8.0F, -13.0F, -20.0F, 16.0F, 28.0F, 41.0F, 0.0F, false);
		body.setTextureOffset(0, 70).addBox(0.0F, -22.0F, -19.0F, 0.0F, 9.0F, 39.0F, 0.0F, false);

		head = new AdvancedModelBox(this, "head");
		head.setRotationPoint(0.0F, 6.2F, -22.0F);
		body.addChild(head);
		head.setTextureOffset(0, 0).addBox(-5.0F, -5.0F, -6.0F, 10.0F, 10.0F, 8.0F, 0.0F, false);

		snout = new AdvancedModelBox(this, "snout");
		snout.setRotationPoint(0.0F, 0.0F, -6.0F);
		head.addChild(snout);
		snout.setTextureOffset(21, 19).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 5.0F, 5.0F, 0.0F, false);

		right_ear = new AdvancedModelBox(this, "right_ear");
		right_ear.setRotationPoint(-3.5F, -5.0F, -3.0F);
		head.addChild(right_ear);
		right_ear.setTextureOffset(11, 19).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, false);

		left_ear = new AdvancedModelBox(this, "left_ear");
		left_ear.setRotationPoint(3.5F, -5.0F, -3.0F);
		head.addChild(left_ear);
		left_ear.setTextureOffset(11, 19).addBox(-1.5F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, true);

		tail = new AdvancedModelBox(this, "tail");
		tail.setRotationPoint(0.0F, 1.0F, 21.0F);
		body.addChild(tail);
		tail.setTextureOffset(79, 70).addBox(0.0F, -13.0F, 0.0F, 0.0F, 25.0F, 17.0F, 0.0F, false);

		left_arm = new AdvancedModelBox(this, "left_arm");
		left_arm.setRotationPoint(6.6F, 15.0F, -10.0F);
		body.addChild(left_arm);
		left_arm.setTextureOffset(0, 70).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 13.0F, 9.0F, 0.0F, false);
		left_arm.setTextureOffset(0, 19).addBox(0.0F, 13.0F, -5.0F, 0.0F, 1.0F, 9.0F, 0.0F, false);

		right_arm = new AdvancedModelBox(this, "right_arm");
		right_arm.setRotationPoint(-6.6F, 15.0F, -10.0F);
		body.addChild(right_arm);
		right_arm.setTextureOffset(0, 70).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 13.0F, 9.0F, 0.0F, true);
		right_arm.setTextureOffset(0, 19).addBox(0.0F, 13.0F, -5.0F, 0.0F, 1.0F, 9.0F, 0.0F, true);

		left_leg = new AdvancedModelBox(this, "left_leg");
		left_leg.setRotationPoint(7.7F, 15.0F, 16.0F);
		body.addChild(left_leg);
		left_leg.setTextureOffset(40, 70).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 8.0F, 6.0F, 0.0F, false);
		left_leg.setTextureOffset(15, 30).addBox(0.0F, 8.0F, -3.0F, 0.0F, 1.0F, 6.0F, 0.0F, false);

		right_leg = new AdvancedModelBox(this, "right_leg");
		right_leg.setRotationPoint(-7.7F, 15.0F, 16.0F);
		body.addChild(right_leg);
		right_leg.setTextureOffset(40, 70).addBox(0.0F, 0.0F, -3.0F, 1.0F, 8.0F, 6.0F, 0.0F, true);
		right_leg.setTextureOffset(15, 30).addBox(0.0F, 8.0F, -3.0F, 0.0F, 1.0F, 6.0F, 0.0F, true);
		this.updateDefaultPose();
		this.animator = ModelAnimator.create();
	}


	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		animator.update(entity);
		animator.setAnimation(EntitySeaBear.ANIMATION_POINT);
		animator.startKeyframe(3);
		animator.rotate(head,  (float)Math.toRadians(50), 0, 0);
		animator.endKeyframe();
		animator.setStaticKeyframe(2);
		animator.startKeyframe(5);
		animator.rotate(body, 0, (float)Math.toRadians(-10), 0);
		animator.rotate(head, 0, (float)Math.toRadians(10), 0);
		animator.rotate(tail, 0, (float)Math.toRadians(10), 0);
		animator.move(right_arm, 1, 0, -5);
		animator.rotate(right_arm, (float)Math.toRadians(-110),  (float)Math.toRadians(10), (float)Math.toRadians(-10));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(body, 0, (float)Math.toRadians(-10), 0);
		animator.rotate(head, 0, (float)Math.toRadians(10), 0);
		animator.rotate(tail, 0, (float)Math.toRadians(10), 0);
		animator.move(right_arm, 1, 0, -7);
		animator.rotate(right_arm, (float)Math.toRadians(-90),  (float)Math.toRadians(10), (float)Math.toRadians(-10));
		animator.endKeyframe();
		animator.setStaticKeyframe(5);
		animator.resetKeyframe(5);
		animator.setAnimation(EntitySeaBear.ANIMATION_ATTACK);
		animator.startKeyframe(3);
		animator.rotate(body, 0, (float)Math.toRadians(-20), 0);
		animator.rotate(head, 0, (float)Math.toRadians(20), 0);
		animator.rotate(tail, 0, (float)Math.toRadians(20), 0);
		animator.move(right_arm, 1, 0, -5);
		animator.rotate(right_arm, (float)Math.toRadians(-110),  (float)Math.toRadians(10), (float)Math.toRadians(-10));
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(body, 0, (float)Math.toRadians(20), 0);
		animator.rotate(head, 0, (float)Math.toRadians(-20), 0);
		animator.rotate(tail, 0, (float)Math.toRadians(-20), 0);
		animator.move(left_arm, -1, 0, -5);
		animator.rotate(left_arm, (float)Math.toRadians(-110),  (float)Math.toRadians(-10), (float)Math.toRadians(10));
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(body, 0, (float)Math.toRadians(-20), 0);
		animator.rotate(head, 0, (float)Math.toRadians(20), 0);
		animator.rotate(tail, 0, (float)Math.toRadians(20), 0);
		animator.move(right_arm, 1, 0, -5);
		animator.rotate(right_arm, (float)Math.toRadians(-110),  (float)Math.toRadians(10), (float)Math.toRadians(-10));
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(body, 0, (float)Math.toRadians(20), 0);
		animator.rotate(head, 0, (float)Math.toRadians(-20), 0);
		animator.rotate(tail, 0, (float)Math.toRadians(-20), 0);
		animator.move(left_arm, -1, 0, -5);
		animator.rotate(left_arm, (float)Math.toRadians(-110),  (float)Math.toRadians(-10), (float)Math.toRadians(10));
		animator.endKeyframe();
		animator.resetKeyframe(5);

	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public void setupAnim(EntitySeaBear entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();
		animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float idleSpeed = 0.14F;
		float idleDegree = 0.25F;
		float swimSpeed = 0.8F;
		float swimDegree = 0.75F;
		float landProgress = entity.prevOnLandProgress + (entity.onLandProgress - entity.prevOnLandProgress) * (ageInTicks - entity.tickCount);
		progressRotationPrev(body, landProgress, 0, 0, (float) Math.toRadians(-90), 5F);
		progressPositionPrev(body, landProgress, 0, 8, 0, 5F);

		this.flap(this.left_arm, idleSpeed, idleDegree, true, 1F, 0.1F, ageInTicks, 1);
		this.flap(this.right_arm, idleSpeed, idleDegree, false, 1F, 0.1F, ageInTicks, 1);
		this.flap(this.left_leg, idleSpeed, idleDegree, true, 3F, 0.1F, ageInTicks, 1);
		this.flap(this.right_leg, idleSpeed, idleDegree, false, 3F, 0.1F, ageInTicks, 1);
		this.swing(this.tail, idleSpeed, idleDegree, true, 5F, 0.0F, ageInTicks, 1);
		this.bob(body, idleSpeed, idleDegree * 2F, false, ageInTicks, 1);
		this.walk(this.body, swimSpeed, swimDegree * 0.1F, false, -3F, 0F, limbSwing, limbSwingAmount);
		this.swing(this.body, swimSpeed, swimDegree * 0.2F, false, 2F, 0F, limbSwing, limbSwingAmount);
		this.swing(this.head, swimSpeed, swimDegree * 0.2F, true, 2F, 0F, limbSwing, limbSwingAmount);
		this.walk(this.left_arm, swimSpeed, swimDegree * 0.3F, false, -3F, 0.1F, limbSwing, limbSwingAmount);
		this.walk(this.right_arm, swimSpeed, swimDegree * 0.3F, false, -3F, 0.1F, limbSwing, limbSwingAmount);
		this.flap(this.left_arm, swimSpeed, swimDegree, true, 0F, 0.4F, limbSwing, limbSwingAmount);
		this.flap(this.right_arm, swimSpeed, swimDegree, false, 0F, 0.4F, limbSwing, limbSwingAmount);
		this.flap(this.left_leg, swimSpeed, swimDegree, true, 2F, 0.2F, limbSwing, limbSwingAmount);
		this.flap(this.right_leg, swimSpeed, swimDegree, false, 2F, 0.2F, limbSwing, limbSwingAmount);
		this.swing(this.tail, swimSpeed, swimDegree * 1.2F, true, 4F, 0F, limbSwing, limbSwingAmount);
		this.faceTarget(netHeadYaw, headPitch, 1.0F, head);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, head, snout, right_arm, right_leg, right_ear, left_arm, left_leg, left_ear, tail);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}