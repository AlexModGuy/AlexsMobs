package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelMoose extends AdvancedEntityModel<EntityMoose> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox body_front;
	private final AdvancedModelBox frontleg_left;
	private final AdvancedModelBox frontleg_right;
	private final AdvancedModelBox neck;
	private final AdvancedModelBox head;
	private final AdvancedModelBox beard;
	private final AdvancedModelBox horn_left;
	private final AdvancedModelBox horn_right;
	private final AdvancedModelBox snout;
	private final AdvancedModelBox tail;
	private final AdvancedModelBox backleg_left;
	private final AdvancedModelBox backleg_right;
	private ModelAnimator animator;

	public ModelMoose() {
		textureWidth = 128;
		textureHeight = 128;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -18.75F, 1.25F);
		root.addChild(body);
		body.setTextureOffset(71, 33).addBox(-4.5F, -7.25F, -3.25F, 9.0F, 11.0F, 14.0F, 0.0F, false);

		body_front = new AdvancedModelBox(this);
		body_front.setRotationPoint(0.0F, -0.25F, -4.25F);
		body.addChild(body_front);
		body_front.setTextureOffset(0, 69).addBox(-5.0F, -8.0F, -8.0F, 10.0F, 13.0F, 9.0F, 0.0F, false);

		frontleg_left = new AdvancedModelBox(this);
		frontleg_left.setRotationPoint(3.0F, 5.0F, -5.0F);
		body_front.addChild(frontleg_left);
		frontleg_left.setTextureOffset(17, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, false);

		frontleg_right = new AdvancedModelBox(this);
		frontleg_right.setRotationPoint(-3.0F, 5.0F, -5.0F);
		body_front.addChild(frontleg_right);
		frontleg_right.setTextureOffset(17, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, true);

		neck = new AdvancedModelBox(this);
		neck.setRotationPoint(0.0F, -4.5F, -8.0F);
		body_front.addChild(neck);
		setRotationAngle(neck, -0.1745F, 0.0F, 0.0F);
		neck.setTextureOffset(40, 17).addBox(-3.5F, -2.5F, -6.0F, 7.0F, 8.0F, 7.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, 1.0F, -7.0F);
		neck.addChild(head);
		setRotationAngle(head, 0.2182F, 0.0F, 0.0F);
		head.setTextureOffset(34, 52).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 5.0F, 0.0F, false);

		beard = new AdvancedModelBox(this);
		beard.setRotationPoint(0.0F, 3.0F, -1.0F);
		head.addChild(beard);
		beard.setTextureOffset(19, 30).addBox(0.0F, -2.0F, -3.0F, 0.0F, 7.0F, 6.0F, 0.0F, false);

		horn_left = new AdvancedModelBox(this);
		horn_left.setRotationPoint(3.0F, -3.0F, -2.0F);
		head.addChild(horn_left);
		horn_left.setTextureOffset(31, 37).addBox(0.0F, -6.0F, -3.0F, 10.0F, 6.0F, 8.0F, 0.0F, false);

		horn_right = new AdvancedModelBox(this);
		horn_right.setRotationPoint(-3.0F, -3.0F, -2.0F);
		head.addChild(horn_right);
		horn_right.setTextureOffset(31, 37).addBox(-10.0F, -6.0F, -3.0F, 10.0F, 6.0F, 8.0F, 0.0F, true);

		snout = new AdvancedModelBox(this);
		snout.setRotationPoint(0.0F, -2.0F, -3.0F);
		head.addChild(snout);
		setRotationAngle(snout, 0.1309F, 0.0F, 0.0F);
		snout.setTextureOffset(33, 0).addBox(-2.0F, 0.0F, -7.0F, 4.0F, 5.0F, 7.0F, 0.0F, false);

		tail = new AdvancedModelBox(this);
		tail.setRotationPoint(0.0F, -3.25F, 10.75F);
		body.addChild(tail);
		setRotationAngle(tail, 0.1745F, 0.0F, 0.0F);
		tail.setTextureOffset(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);

		backleg_left = new AdvancedModelBox(this);
		backleg_left.setRotationPoint(2.5F, 3.75F, 8.75F);
		body.addChild(backleg_left);
		backleg_left.setTextureOffset(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, 0.0F, false);

		backleg_right = new AdvancedModelBox(this);
		backleg_right.setRotationPoint(-2.5F, 3.75F, 8.75F);
		body.addChild(backleg_right);
		backleg_right.setTextureOffset(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, 0.0F, true);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, head, neck, body, body_front, tail, horn_left, horn_right, snout, beard, backleg_left, backleg_right, frontleg_left, frontleg_right);
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityMoose.ANIMATION_EAT_GRASS);
		animator.startKeyframe(5);
		animator.rotate(neck, (float)Math.toRadians(50), 0, 0);
		animator.rotate(head, (float)Math.toRadians(4), 0, 0);
		eatPose();
		animator.endKeyframe();
		animator.startKeyframe(4);
		animator.rotate(neck, (float)Math.toRadians(70), 0, 0);
		animator.rotate(head, (float)Math.toRadians(10), 0, 0);
		eatPose();
		animator.endKeyframe();
		animator.startKeyframe(4);
		animator.rotate(neck, (float)Math.toRadians(50), 0, 0);
		animator.rotate(head, (float)Math.toRadians(0), 0, 0);
		eatPose();
		animator.endKeyframe();
		animator.startKeyframe(4);
		animator.rotate(neck, (float)Math.toRadians(70), 0, 0);
		animator.rotate(head, (float)Math.toRadians(10), 0, 0);
		eatPose();
		animator.endKeyframe();
		animator.startKeyframe(4);
		animator.rotate(neck, (float)Math.toRadians(50), 0, 0);
		animator.rotate(head, (float)Math.toRadians(0), 0, 0);
		eatPose();
		animator.endKeyframe();
		animator.startKeyframe(4);
		animator.rotate(neck, (float)Math.toRadians(70), 0, 0);
		animator.rotate(head, (float)Math.toRadians(10), 0, 0);
		eatPose();
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityMoose.ANIMATION_ATTACK);
		animator.startKeyframe(8);
		eatPose();
		animator.rotate(neck, (float)Math.toRadians(50), 0, 0);
		animator.rotate(head, (float)Math.toRadians(10), 0, 0);
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(neck, (float)Math.toRadians(-34), 0, 0);
		animator.rotate(head, (float)Math.toRadians(-20), 0, 0);
		animator.endKeyframe();
		animator.resetKeyframe(4);
	}

	private void eatPose(){
		animator.rotate(body, (float)Math.toRadians(10), 0, 0);
		animator.move(body, 0, 2, 0);
		animator.rotate(backleg_left, (float)Math.toRadians(-10), 0, 0);
		animator.rotate(backleg_right, (float)Math.toRadians(-10), 0,  0);
		animator.rotate(frontleg_left, (float)Math.toRadians(-10), 0, (float)Math.toRadians(-10));
		animator.rotate(frontleg_right, (float)Math.toRadians(-10), 0,  (float)Math.toRadians(10));
		animator.move(frontleg_left, 0.1F, -3, 0F);
		animator.move(frontleg_right, -0.1F, -3, 0F);
		animator.move(backleg_left, 0, -0.2F, 0);
		animator.move(backleg_right, 0, -0.2F, 0);
		animator.move(neck, 0, 1, 0);
	}


	@Override
	public void setRotationAngles(EntityMoose entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float walkSpeed = 0.7F;
		float walkDegree = 0.6F;
		float idleSpeed = 0.1F;
		float idleDegree = 0.1F;
		float runProgress = 5F * limbSwingAmount;
		float partialTick = Minecraft.getInstance().getRenderPartialTicks();
		float jostleProgress = entityIn.prevJostleProgress + (entityIn.jostleProgress - entityIn.prevJostleProgress) * partialTick;
		float jostleAngle = entityIn.prevJostleAngle + (entityIn.getJostleAngle() - entityIn.prevJostleAngle) * partialTick;
		this.walk(tail, idleSpeed, idleDegree * 2, false, 1F, 0.1F, ageInTicks, 1);
		this.flap(beard, idleSpeed, idleDegree * 4, false, 0F, 0F, ageInTicks, 1);
		this.walk(neck, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1);
		this.walk(head, idleSpeed, -idleDegree, false, 0.5F, 0F, ageInTicks, 1);
		this.walk(body, walkSpeed, walkDegree * 0.05F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(body, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(neck, walkSpeed, walkDegree * 0.25F, true, 1F, 0F, limbSwing, limbSwingAmount);
		this.walk(head, walkSpeed, -walkDegree * 0.25F, true, 1F, 0F, limbSwing, limbSwingAmount);
		this.walk(tail, walkSpeed, walkDegree * 0.5F, true, 1F, -0.4F, limbSwing, limbSwingAmount);
		this.walk(frontleg_right, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(frontleg_right, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(frontleg_left, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(frontleg_left, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(backleg_right, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(backleg_right, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(backleg_left, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(backleg_left, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		progressRotationPrev(tail, runProgress, (float)Math.toRadians(23), 0, 0, 5F);
		progressRotationPrev(neck, jostleProgress, (float)Math.toRadians(7), 0, 0, 5F);
		progressRotationPrev(head, jostleProgress, (float)Math.toRadians(80), 0, 0, 5F);
		progressPositionPrev(neck, jostleProgress, 0, 0, 1, 5F);
		progressPositionPrev(head, jostleProgress, 0, 0, -1, 5F);
		if(jostleProgress > 0){
			float yawAmount = jostleAngle / 57.295776F * 0.5F * jostleProgress * 0.2F;
			neck.rotateAngleY += yawAmount;
			head.rotateAngleY += yawAmount;
			head.rotateAngleZ += yawAmount;
		}else{
			this.faceTarget(netHeadYaw, headPitch, 2, neck, head);
		}
	}

	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.isChild) {
			float f = 1.35F;
			float feet = 1.45F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			frontleg_right.setScale(1, feet, 1);
			frontleg_left.setScale(1, feet, 1);
			backleg_right.setScale(1, feet, 1);
			backleg_left.setScale(1, feet, 1);
			matrixStackIn.push();
			matrixStackIn.scale(0.35F, 0.35F, 0.35F);
			matrixStackIn.translate(0.0D, 2.25D, 0.125D);
			getParts().forEach((p_228292_8_) -> {
				p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.pop();
			head.setScale(1, 1, 1);
			frontleg_right.setScale(1, 1, 1);
			frontleg_left.setScale(1, 1, 1);
			backleg_right.setScale(1, 1, 1);
			backleg_left.setScale(1, 1, 1);
		} else {
			matrixStackIn.push();
			getParts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.pop();
		}

	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}