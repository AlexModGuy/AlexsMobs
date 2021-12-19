package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityGeladaMonkey;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelGeladaMonkey extends AdvancedEntityModel<EntityGeladaMonkey> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox tail;
	private final AdvancedModelBox torso;
	private final AdvancedModelBox neck;
	private final AdvancedModelBox head;
	private final AdvancedModelBox mouth;
	private final AdvancedModelBox left_arm;
	private final AdvancedModelBox right_arm;
	private final AdvancedModelBox left_leg;
	private final AdvancedModelBox right_leg;
	public final ModelAnimator animator;

	public ModelGeladaMonkey() {
		texWidth = 128;
		texHeight = 128;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -11.0F, 4.0F);
		root.addChild(body);
		body.setTextureOffset(30, 36).addBox(-3.5F, -3.0F, -5.0F, 7.0F, 7.0F, 9.0F, 0.0F, false);

		tail = new AdvancedModelBox(this);
		tail.setRotationPoint(0.0F, -1.5F, 3.0F);
		body.addChild(tail);
		setRotationAngle(tail, 0.5672F, 0.0F, 0.0F);
		tail.setTextureOffset(0, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 20.0F, 0.0F, false);

		torso = new AdvancedModelBox(this);
		torso.setRotationPoint(0.0F, 0.0F, -4.0F);
		body.addChild(torso);
		torso.setTextureOffset(0, 24).addBox(-4.5F, -5.0F, -9.0F, 9.0F, 10.0F, 10.0F, 0.0F, false);
		torso.setTextureOffset(27, 0).addBox(-4.5F, 5.0F, -9.0F, 9.0F, 3.0F, 10.0F, 0.0F, false);

		neck = new AdvancedModelBox(this);
		neck.setRotationPoint(0.0F, -0.9F, -8.6F);
		torso.addChild(neck);
		neck.setTextureOffset(39, 24).addBox(-5.0F, -5.0F, -3.0F, 10.0F, 7.0F, 4.0F, 0.0F, false);
		neck.setTextureOffset(50, 67).addBox(-8.0F, -5.0F, -2.0F, 16.0F, 11.0F, 0.0F, 0.0F, false);
		neck.setTextureOffset(25, 60).addBox(-4.0F, -5.0F, -2.4F, 8.0F, 7.0F, 3.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -1.0F, -2.0F);
		neck.addChild(head);
		head.setTextureOffset(0, 0).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 3.0F, 0.0F, false);
		
		mouth = new AdvancedModelBox(this);
		mouth.setRotationPoint(0.0F, 1.0F, -1.0F);
		head.addChild(mouth);
		setRotationAngle(mouth, -1.0908F, 0.0F, 0.0F);
		mouth.setTextureOffset(0, 8).addBox(-1.5F, -1.5F, -1.2F, 3.0F, 6.0F, 4.0F, 0.0F, false);

		left_arm = new AdvancedModelBox(this);
		left_arm.setRotationPoint(2.0F, 3.0F, -7.0F);
		torso.addChild(left_arm);
		left_arm.setTextureOffset(11, 45).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

		right_arm = new AdvancedModelBox(this);
		right_arm.setRotationPoint(-2.0F, 3.0F, -7.0F);
		torso.addChild(right_arm);
		right_arm.setTextureOffset(11, 45).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, true);

		left_leg = new AdvancedModelBox(this);
		left_leg.setRotationPoint(2.3F, 5.0F, 2.0F);
		body.addChild(left_leg);
		left_leg.setTextureOffset(0, 45).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 3.0F, 0.0F, false);

		right_leg = new AdvancedModelBox(this);
		right_leg.setRotationPoint(-2.3F, 5.0F, 2.0F);
		body.addChild(right_leg);
		right_leg.setTextureOffset(0, 45).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 3.0F, 0.0F, true);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityGeladaMonkey.ANIMATION_SWIPE_L);
		animator.startKeyframe(5);
		animator.rotate(body, 0, (float)Math.toRadians(5F), 0);
		animator.rotate(head, 0, (float)Math.toRadians(-5F), 0);
		animator.rotate(left_arm, (float)Math.toRadians(25F), (float)Math.toRadians(10F), 0);
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(body, (float)Math.toRadians(-15F), 0, 0);
		animator.rotate(head, (float)Math.toRadians(5F), 0, 0);
		animator.rotate(right_arm, (float)Math.toRadians(15F), 0, 0);
		animator.rotate(right_leg, (float)Math.toRadians(15F), 0, 0);
		animator.rotate(left_leg, (float)Math.toRadians(15F), 0, 0);
		animator.rotate(left_arm, (float)Math.toRadians(-80F),  (float)Math.toRadians(-20F),0);
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityGeladaMonkey.ANIMATION_SWIPE_R);
		animator.startKeyframe(5);
		animator.rotate(body, 0, (float)Math.toRadians(-5F), 0);
		animator.rotate(head, 0, (float)Math.toRadians(5F), 0);
		animator.rotate(right_arm, (float)Math.toRadians(25F), (float)Math.toRadians(-10F), 0);
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(body, (float)Math.toRadians(-15F), 0, 0);
		animator.rotate(head, (float)Math.toRadians(5F), 0, 0);
		animator.rotate(left_arm, (float)Math.toRadians(15F), 0, 0);
		animator.rotate(right_leg, (float)Math.toRadians(15F), 0, 0);
		animator.rotate(left_leg, (float)Math.toRadians(15F), 0, 0);
		animator.rotate(right_arm, (float)Math.toRadians(-80F),  (float)Math.toRadians(20F),0);
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityGeladaMonkey.ANIMATION_CHEST);
		animator.startKeyframe(5);
		standPose();
		animator.rotate(right_arm, 0, 0,  (float)Math.toRadians(25F));
		animator.rotate(left_arm, 0, 0,  (float)Math.toRadians(-25F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		standPose();
		animator.rotate(neck, 0, 0,  (float)Math.toRadians(25F));
		animator.rotate(right_arm, 0, 0,  (float)Math.toRadians(25F));
		animator.rotate(left_arm, 0, 0,  (float)Math.toRadians(-25F));
		animator.endKeyframe();
		animator.startKeyframe(10);
		standPose();
		animator.rotate(neck, 0, 0,  (float)Math.toRadians(-25F));
		animator.rotate(right_arm, 0, 0,  (float)Math.toRadians(5F));
		animator.rotate(left_arm, 0, 0,  (float)Math.toRadians(-5F));
		animator.endKeyframe();
		animator.startKeyframe(10);
		standPose();
		animator.rotate(neck, 0, 0,  (float)Math.toRadians(25F));
		animator.rotate(right_arm, 0, 0,  (float)Math.toRadians(25F));
		animator.rotate(left_arm, 0, 0,  (float)Math.toRadians(-25F));
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityGeladaMonkey.ANIMATION_GROOM);
		animator.startKeyframe(3);
		animator.rotate(left_arm, (float)Math.toRadians(-10F), 0, 0);
		animator.rotate(right_arm, (float)Math.toRadians(-90F),  (float)Math.toRadians(-10F),0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(neck, (float)Math.toRadians(20F), 0, (float)Math.toRadians(20F));
		animator.rotate(left_arm, (float)Math.toRadians(-10F), 0, 0);
		animator.rotate(right_arm, (float)Math.toRadians(-90F),  (float)Math.toRadians(-10F),0);
		animator.move(neck, 1, 0, -1);
		animator.move(right_arm, 0, 0, 3);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(neck, (float)Math.toRadians(5F), 0, (float)Math.toRadians(10F));
		animator.rotate(left_arm, (float)Math.toRadians(-10F), 0, 0);
		animator.rotate(right_arm, (float)Math.toRadians(-130F),  (float)Math.toRadians(-10F),0);
		animator.move(neck, 1, 0, -1);
		animator.move(right_arm, 0, 0, 2);
		animator.endKeyframe();
		animator.startKeyframe(4);
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(right_arm, (float)Math.toRadians(-10F), 0, 0);
		animator.rotate(left_arm, (float)Math.toRadians(-90F),  (float)Math.toRadians(10F),0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(neck, (float)Math.toRadians(20F), 0, (float)Math.toRadians(-20F));
		animator.rotate(right_arm, (float)Math.toRadians(-10F), 0, 0);
		animator.rotate(left_arm, (float)Math.toRadians(-90F),  (float)Math.toRadians(10F),0);
		animator.move(neck, -1, 0, -1);
		animator.move(right_arm, 0, 0, 3);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(neck, (float)Math.toRadians(5F), 0, (float)Math.toRadians(-10F));
		animator.rotate(right_arm, (float)Math.toRadians(-10F), 0, 0);
		animator.rotate(left_arm, (float)Math.toRadians(-130F),  (float)Math.toRadians(10F),0);
		animator.move(neck, -1, 0, -1);
		animator.move(right_arm, 0, 0, 2);
		animator.endKeyframe();
		animator.resetKeyframe(5);
	}

	private void standPose(){
		animator.rotate(body,  (float)Math.toRadians(-65F), 0, 0);
		animator.rotate(neck,  (float)Math.toRadians(65F), 0, 0);
		animator.rotate(right_leg,  (float)Math.toRadians(65F), 0, 0);
		animator.rotate(left_leg,  (float)Math.toRadians(65F), 0, 0);
		animator.rotate(right_arm,  (float)Math.toRadians(65F), 0, 0);
		animator.rotate(left_arm,  (float)Math.toRadians(65F), 0, 0);
		animator.rotate(tail,  (float)Math.toRadians(50F), 0, 0);
		animator.move(body, 0, 0.5F, -2);
		animator.move(neck, 0, -1, -2);
		animator.move(right_leg, 0, -1, 1);
		animator.move(left_leg, 0, -1, 1);
		animator.move(right_arm, -1, 1, -1);
		animator.move(left_arm, 1, 1, -1);
	}
	@Override
	public void setupAnim(EntityGeladaMonkey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		boolean running = entity.isAggro();
		float runSpeed = 0.7F;
		float runDegree = 0.7F;
		float walkSpeed = 0.9F;
		float walkDegree = 0.6F;
		float idleSpeed = 0.15F;
		float idleDegree = 0.5F;
		float stillProgress = (1F - limbSwingAmount) * 5F;
		float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * (ageInTicks - entity.tickCount);
		progressRotationPrev(tail, stillProgress, (float)Math.toRadians(-40), 0, 0, 5F);
		this.swing(tail, idleSpeed, idleDegree, true, 0, 0F, ageInTicks, 1);
		this.bob(neck, idleSpeed * 0.5F, idleDegree * 0.25F, false, ageInTicks, 1);
		if(running){
			this.walk(body, runSpeed, runDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(tail, runSpeed, runDegree * 0.5F, true, 0F, 0.3F, limbSwing, limbSwingAmount);
			this.walk(neck, runSpeed, runDegree * 0.2F, true, 1F, 0F, limbSwing, limbSwingAmount);
			this.walk(right_arm, runSpeed, runDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(left_arm, runSpeed, runDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(right_leg, runSpeed, runDegree * 1.2F, false, 0, 0F, limbSwing, limbSwingAmount);
			this.walk(left_leg, runSpeed, runDegree * 1.2F, false, 0, 0F, limbSwing, limbSwingAmount);
			this.flap(right_leg, runSpeed, runDegree * 0.2F, true, 0, -0.2F, limbSwing, limbSwingAmount);
			this.flap(left_leg, runSpeed, runDegree * 0.2F, false, 0, -0.2F, limbSwing, limbSwingAmount);
			this.bob(body, runSpeed, runDegree * 3F, false, limbSwing, limbSwingAmount);
		}else {
			this.walk(tail, walkSpeed, -walkDegree * 0.2F, true, 1F, 0.1F, limbSwing, limbSwingAmount);
			this.walk(body, walkSpeed, walkDegree * 0.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(neck, walkSpeed, -walkDegree * 0.1F, true, 1F, 0F, limbSwing, limbSwingAmount);
			this.walk(right_arm, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(left_arm, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(right_leg, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(left_leg, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.bob(right_leg, walkSpeed, walkDegree * -1.2F, true, limbSwing, limbSwingAmount);
			this.bob(left_leg, walkSpeed, walkDegree * -1.2F, true, limbSwing, limbSwingAmount);
		}

		progressRotationPrev(body, sitProgress, (float)Math.toRadians(-40), 0, 0, 5F);
		progressRotationPrev(left_leg, sitProgress, (float)Math.toRadians(-45), (float)Math.toRadians(-20), 0, 5F);
		progressRotationPrev(right_leg, sitProgress, (float)Math.toRadians(-45), (float)Math.toRadians(20), 0, 5F);
		progressRotationPrev(left_arm, sitProgress, (float)Math.toRadians(40), 0, 0, 5F);
		progressRotationPrev(right_arm, sitProgress, (float)Math.toRadians(40), 0, 0, 5F);
		progressRotationPrev(tail, sitProgress, (float)Math.toRadians(50), 0, 0, 5F);
		progressRotationPrev(neck, sitProgress, (float)Math.toRadians(40), 0, 0, 5F);
		progressPositionPrev(body, sitProgress, 0, 5F, 0, 5F);
		progressPositionPrev(left_leg, sitProgress, 0, -2.5F, 0, 5F);
		progressPositionPrev(right_leg, sitProgress, 0, -2.5F, 0, 5F);
		progressPositionPrev(left_arm, sitProgress, 0, 2, 2, 5F);
		progressPositionPrev(right_arm, sitProgress, 0, 2, 2, 5F);
		neck.rotateAngleY += netHeadYaw / 57.295776F * 0.5F;
		neck.rotateAngleX += headPitch / 57.295776F;
		if(entity.isBaby()){
			head.setScale(1.3F, 1.3F, 1.3F);
			neck.setScale(1.25F, 1.25F, 1.25F);
		}else{
			neck.setScale(1.0F, 1.0F, 1.0F);
			head.setScale(1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, tail, torso, neck, head, left_arm, left_leg, right_arm, right_leg, mouth);
	}


	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}