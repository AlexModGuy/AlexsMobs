package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
import com.github.alexthe666.alexsmobs.entity.EntityGazelle;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelFrilledShark extends AdvancedEntityModel<EntityFrilledShark> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox head;
	private final AdvancedModelBox jaw;
	private final AdvancedModelBox pectoralfin_left;
	private final AdvancedModelBox pectoralfin_right;
	private final AdvancedModelBox tail1;
	private final AdvancedModelBox pelvicfin_left;
	private final AdvancedModelBox pelvicfin_right;
	private final AdvancedModelBox tail2;
	private ModelAnimator animator;

	public ModelFrilledShark() {
		textureWidth = 128;
		textureHeight = 128;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -3.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -15.0F, 6.0F, 6.0F, 18.0F, 0.0F, false);
		body.setTextureOffset(66, 59).addBox(0.0F, -9.0F, -14.0F, 0.0F, 6.0F, 17.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -2.0F, -15.0F);
		body.addChild(head);
		head.setTextureOffset(31, 0).addBox(-3.0F, -1.0F, -7.0F, 6.0F, 3.0F, 7.0F, 0.0F, false);

		jaw = new AdvancedModelBox(this);
		jaw.setRotationPoint(0.0F, 2.4F, 0.4F);
		head.addChild(jaw);
		setRotationAngle(jaw, 0.2618F, 0.0F, 0.0F);
		jaw.setTextureOffset(41, 25).addBox(-2.5F, 0.0F, -7.0F, 5.0F, 2.0F, 7.0F, 0.0F, false);

		pectoralfin_left = new AdvancedModelBox(this);
		pectoralfin_left.setRotationPoint(3.0F, 2.4F, -10.0F);
		body.addChild(pectoralfin_left);
		setRotationAngle(pectoralfin_left, 0.0F, 0.0F, 0.48F);
		pectoralfin_left.setTextureOffset(41, 42).addBox(0.0F, 0.0F, 0.0F, 5.0F, 0.0F, 7.0F, 0.0F, false);

		pectoralfin_right = new AdvancedModelBox(this);
		pectoralfin_right.setRotationPoint(-3.0F, 2.4F, -10.0F);
		body.addChild(pectoralfin_right);
		setRotationAngle(pectoralfin_right, 0.0F, 0.0F, -0.48F);
		pectoralfin_right.setTextureOffset(41, 42).addBox(-5.0F, 0.0F, 0.0F, 5.0F, 0.0F, 7.0F, 0.0F, true);

		tail1 = new AdvancedModelBox(this);
		tail1.setRotationPoint(0.0F, -0.9F, 3.0F);
		body.addChild(tail1);
		tail1.setTextureOffset(21, 25).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 5.0F, 11.0F, 0.0F, false);
		tail1.setTextureOffset(0, 25).addBox(0.0F, -5.0F, 5.0F, 0.0F, 3.0F, 6.0F, 0.0F, false);

		pelvicfin_left = new AdvancedModelBox(this);
		pelvicfin_left.setRotationPoint(2.0F, 3.0F, 5.0F);
		tail1.addChild(pelvicfin_left);
		setRotationAngle(pelvicfin_left, 0.0F, 0.0F, -0.9599F);
		pelvicfin_left.setTextureOffset(21, 25).addBox(0.0F, 0.0F, -1.0F, 0.0F, 3.0F, 5.0F, 0.0F, false);

		pelvicfin_right = new AdvancedModelBox(this);
		pelvicfin_right.setRotationPoint(-2.0F, 3.0F, 5.0F);
		tail1.addChild(pelvicfin_right);
		setRotationAngle(pelvicfin_right, 0.0F, 0.0F, 0.9599F);
		pelvicfin_right.setTextureOffset(21, 25).addBox(0.0F, 0.0F, -1.0F, 0.0F, 3.0F, 5.0F, 0.0F, true);

		tail2 = new AdvancedModelBox(this);
		tail2.setRotationPoint(0.0F, 0.1F, 11.0F);
		tail1.addChild(tail2);
		tail2.setTextureOffset(0, 25).addBox(0.0F, -6.0F, 0.0F, 0.0F, 11.0F, 20.0F, 0.0F, false);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		animator.update(entity);
		animator.setAnimation(EntityFrilledShark.ANIMATION_ATTACK);
		animator.startKeyframe(5);
		animator.rotate(jaw, (float) Math.toRadians(-20), 0, 0);
		animator.move(head, 0, 0.5F, 3);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
		animator.rotate(jaw, (float) Math.toRadians(40), 0, 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(head, (float) Math.toRadians(5), 0, 0);
		animator.rotate(jaw, (float) Math.toRadians(-20), 0, 0);
		animator.endKeyframe();
		animator.resetKeyframe(2);
	}

	@Override
	public void setRotationAngles(EntityFrilledShark entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{head,body, tail1, tail2};
		float idleSpeed = 0.14F;
		float idleDegree = 0.25F;
		float swimSpeed = 0.8F;
		float swimDegree = 0.75F;
		float landProgress = entityIn.prevOnLandProgress + (entityIn.onLandProgress - entityIn.prevOnLandProgress) * (ageInTicks - entityIn.ticksExisted);
		progressRotationPrev(body, landProgress, 0, 0, (float) Math.toRadians(-100), 5F);
		progressRotationPrev(pectoralfin_right, landProgress, 0, 0, (float) Math.toRadians(-50), 5F);
		progressRotationPrev(pectoralfin_left, landProgress, 0, 0, (float) Math.toRadians(50), 5F);
		this.walk(this.jaw, idleSpeed, idleDegree, true, 1F, -0.1F, ageInTicks, 1);
		if(landProgress >= 5F){
			this.chainWave(tailBoxes, idleSpeed, idleDegree * 0.9F, -3, ageInTicks, 1);
			this.flap(this.pectoralfin_right, idleSpeed, idleDegree * 2F, true, 3, 0.3F, ageInTicks, 1);
			this.flap(this.pectoralfin_left, idleSpeed, idleDegree * -2F, true, 3, 0.1F, ageInTicks, 1);
		}else{
			this.chainSwing(tailBoxes, swimSpeed, swimDegree * 0.9F, -3, limbSwing, limbSwingAmount);
			this.flap(this.pectoralfin_right, swimSpeed, swimDegree, true, 1F, 0.3F, limbSwing, limbSwingAmount);
			this.flap(this.pectoralfin_left, swimSpeed, swimDegree, true, 1F, 0.3F, limbSwing, limbSwingAmount);
			this.flap(this.pelvicfin_right, swimSpeed, -swimDegree, true, 3, 0.1F, limbSwing, limbSwingAmount);
			this.flap(this.pelvicfin_left, swimSpeed, -swimDegree, true, 3, 0.1F, limbSwing, limbSwingAmount);
		}

	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, head, tail1, tail2, jaw, pectoralfin_left, pectoralfin_right, pelvicfin_left, pelvicfin_right);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}