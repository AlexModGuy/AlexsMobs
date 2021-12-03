package com.github.alexthe666.alexsmobs.client.model;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelGorilla extends AdvancedEntityModel<EntityGorilla> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox chest;
	public final AdvancedModelBox head;
	public final AdvancedModelBox foreheadDK_r1;
	public final AdvancedModelBox mouth;
	public final AdvancedModelBox leftArm;
	public final AdvancedModelBox rightArm;
	public final AdvancedModelBox leftLeg;
	public final AdvancedModelBox rightLeg;
	public final ModelAnimator animator;
	
	public ModelGorilla() {
		texWidth = 128;
		texHeight = 128;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);


		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -14.0F, 3.0F);
		root.addChild(body);
		body.setTextureOffset(0, 23).addBox(-4.5F, -4.0F, -3.0F, 9.0F, 9.0F, 7.0F, 0.0F, false);

		chest = new AdvancedModelBox(this);
		chest.setRotationPoint(0.0F, 4.0F, -3.0F);
		body.addChild(chest);
		chest.setTextureOffset(0, 0).addBox(-6.5F, -8.0F, -11.0F, 13.0F, 11.0F, 11.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -8.0F, -11.0F);
		chest.addChild(head);
		head.setTextureOffset(22, 48).addBox(-3.0F, -5.0F, -4.0F, 6.0F, 7.0F, 7.0F, 0.0F, false);
		head.setTextureOffset(38, 0).addBox(-3.0F, -6.0F, -4.0F, 6.0F, 1.0F, 7.0F, 0.0F, false);
		head.setTextureOffset(0, 40).addBox(-3.0F, -8.0F, -5.0F, 6.0F, 4.0F, 8.0F, 0.0F, false);

		foreheadDK_r1 = new AdvancedModelBox(this);
		foreheadDK_r1.setRotationPoint(0.0F, -6.0F, -2.0F);
		head.addChild(foreheadDK_r1);
		setRotationAngle(foreheadDK_r1, 0.0F, 0.2182F, 0.0F);
		foreheadDK_r1.setTextureOffset(0, 56).addBox(0.0F, -6.0F, -4.0F, 0.0F, 6.0F, 10.0F, 0.0F, false);

		mouth = new AdvancedModelBox(this);
		mouth.setRotationPoint(0.0F, 0.0F, -1.0F);
		head.addChild(mouth);
		mouth.setTextureOffset(49, 9).addBox(-2.0F, -2.0F, -5.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);

		leftArm = new AdvancedModelBox(this);
		leftArm.setRotationPoint(6.0F, -6.5F, -9.5F);
		chest.addChild(leftArm);
		leftArm.setTextureOffset(33, 23).addBox(-3.0F, -2.5F, -2.5F, 6.0F, 19.0F, 5.0F, 0.0F, false);

		rightArm = new AdvancedModelBox(this);
		rightArm.setRotationPoint(-6.0F, -6.5F, -9.5F);
		chest.addChild(rightArm);
		rightArm.setTextureOffset(33, 23).addBox(-3.0F, -2.5F, -2.5F, 6.0F, 19.0F, 5.0F, 0.0F, true);

		leftLeg = new AdvancedModelBox(this);
		leftLeg.setRotationPoint(3.0F, 4.5F, 2.5F);
		body.addChild(leftLeg);
		leftLeg.setTextureOffset(49, 48).addBox(-2.5F, 0.5F, -2.5F, 5.0F, 9.0F, 5.0F, 0.0F, false);

		rightLeg = new AdvancedModelBox(this);
		rightLeg.setRotationPoint(-3.0F, 4.5F, 2.5F);
		body.addChild(rightLeg);
		rightLeg.setTextureOffset(49, 48).addBox(-2.5F, 0.5F, -2.5F, 5.0F, 9.0F, 5.0F, 0.0F, true);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityGorilla.ANIMATION_BREAKBLOCK_R);
		animator.startKeyframe(7);
		animator.rotate(chest, 0, (float)Math.toRadians(-10F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(-10F));
		animator.rotate(rightArm, (float)Math.toRadians(65F), (float)Math.toRadians(-20F), 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(chest, 0, (float)Math.toRadians(20F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(0));
		animator.rotate(rightArm, (float)Math.toRadians(-80F),  (float)Math.toRadians(-30F),0);
		animator.endKeyframe();
		animator.resetKeyframe(6);
		animator.setAnimation(EntityGorilla.ANIMATION_BREAKBLOCK_L);
		animator.startKeyframe(7);
		animator.rotate(chest, 0, (float)Math.toRadians(10F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(10F));
		animator.rotate(leftArm, (float)Math.toRadians(65F), (float)Math.toRadians(20F), 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(chest, 0, (float)Math.toRadians(-20F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(0));
		animator.rotate(leftArm, (float)Math.toRadians(-80F),  (float)Math.toRadians(30F),0);
		animator.endKeyframe();
		animator.resetKeyframe(6);
		animator.setAnimation(EntityGorilla.ANIMATION_POUNDCHEST);
		animator.startKeyframe(5);
		animator.rotate(rightArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(leftArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-20F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(rightArm, 0, 5, 0);
		animator.rotate(head, (float)Math.toRadians(-0),  (float)Math.toRadians(10F), 0);
		animator.rotate(chest, 0, 0,  (float)Math.toRadians(10F));
		animator.rotate(rightArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-80F));
		animator.rotate(leftArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-40F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(leftArm, 0, 5, 0);
		animator.rotate(head, (float)Math.toRadians(0),  (float)Math.toRadians(-10F), 0);
		animator.rotate(chest, 0, 0,  (float)Math.toRadians(-10F));
		animator.rotate(rightArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(leftArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(60F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(rightArm, 0, 5, 0);
		animator.rotate(head, (float)Math.toRadians(-0),  (float)Math.toRadians(10F), 0);
		animator.rotate(rightArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-80F));
		animator.rotate(leftArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-40F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(leftArm, 0, 5, 0);
		animator.rotate(head, (float)Math.toRadians(0),  (float)Math.toRadians(-10F), 0);
		animator.rotate(chest, 0, 0,  (float)Math.toRadians(-10F));
		animator.rotate(rightArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(leftArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(60F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(rightArm, 0, 5, 0);
		animator.rotate(chest, 0, 0,  (float)Math.toRadians(10F));
		animator.rotate(head, (float)Math.toRadians(-0),  (float)Math.toRadians(10F), 0);
		animator.rotate(rightArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-80F));
		animator.rotate(leftArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-40F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(leftArm, 0, 5, 0);
		animator.rotate(chest, 0, 0,  (float)Math.toRadians(-10F));
		animator.rotate(head, (float)Math.toRadians(0),  (float)Math.toRadians(-10F), 0);
		animator.rotate(rightArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(leftArm, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(60F));
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityGorilla.ANIMATION_ATTACK);
		animator.startKeyframe(7);
		animator.rotate(leftArm, (float)Math.toRadians(65F), (float)Math.toRadians(10F), 0);
		animator.rotate(rightArm, (float)Math.toRadians(65F), (float)Math.toRadians(-10F), 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(leftArm, (float)Math.toRadians(-90F),  (float)Math.toRadians(30F),0);
		animator.rotate(rightArm, (float)Math.toRadians(-90F),  (float)Math.toRadians(-30F),0);
		animator.endKeyframe();
		animator.resetKeyframe(6);
	}

	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			float f = 1.35F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			matrixStackIn.pushPose();
			parts().forEach((p_228292_8_) -> {
				p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
			head.setScale(1, 1, 1);
		} else {
			matrixStackIn.pushPose();
			parts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
		}
	}

	@Override
	public void setupAnim(EntityGorilla entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float walkSpeed = 0.7F;
		float walkDegree = 0.5F;
		float eatSpeed = 0.8F;
		float eatDegree = 0.3F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float sitProgress = entityIn.prevSitProgress + (entityIn.sitProgress - entityIn.prevSitProgress) * partialTick;
		float standProgress = entityIn.prevStandProgress + (entityIn.standProgress - entityIn.prevStandProgress) * partialTick;
		float rideProgress = entityIn.isPassenger() && entityIn.isBaby() ? 5F : 0;
		this.faceTarget(netHeadYaw, headPitch, 1, head);
		progressRotationPrev(leftArm, rideProgress, (float)Math.toRadians(-20), (float)Math.toRadians(-20), (float)Math.toRadians(-40), 5F);
		progressRotationPrev(rightArm, rideProgress, (float)Math.toRadians(-20), (float)Math.toRadians(20), (float)Math.toRadians(40), 5F);
		progressRotationPrev(leftLeg, rideProgress, (float)Math.toRadians(-20), 0, (float)Math.toRadians(-80), 5F);
		progressRotationPrev(rightLeg, rideProgress, (float)Math.toRadians(-20), 0, (float)Math.toRadians(80), 5F);
		progressRotationPrev(head, rideProgress, (float)Math.toRadians(15), 0, 0, 5F);
		progressRotationPrev(body, rideProgress, (float)Math.toRadians(-10), 0, 0, 5F);
		progressPositionPrev(body, rideProgress, 0, 5, 3, 5F);

		progressRotationPrev(body, sitProgress, (float)Math.toRadians(-80), 0, 0, 10F);
		progressRotationPrev(rightLeg, sitProgress, (float)Math.toRadians(-10), (float)Math.toRadians(-30), (float)Math.toRadians(30), 10F);
		progressRotationPrev(leftLeg, sitProgress, (float)Math.toRadians(-10), (float)Math.toRadians(30), (float)Math.toRadians(-30), 10F);
		progressRotationPrev(head, sitProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(leftArm, sitProgress, (float)Math.toRadians(20), 0, 0, 10F);
		progressRotationPrev(rightArm, sitProgress, (float)Math.toRadians(20), 0, 0, 10F);
		progressPositionPrev(body, sitProgress, 0, 8, 0, 10F);
		progressPositionPrev(head, sitProgress, 0, 4, -2, 10F);
		progressPositionPrev(leftArm, sitProgress, 0, 0, 2, 10F);
		progressPositionPrev(rightArm, sitProgress, 0, 0, 2, 10F);
		progressRotationPrev(body, standProgress, (float)Math.toRadians(-80), 0, 0, 10F);
		progressRotationPrev(rightLeg, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(leftLeg, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(head, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(leftArm, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(rightArm, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressPositionPrev(body, standProgress, 0, 1, 0, 10F);
		progressPositionPrev(rightLeg, standProgress, -1, -3, 1.2F, 10F);
		progressPositionPrev(leftLeg, standProgress, 1, -3, 1.2F, 10F);
		progressPositionPrev(leftArm, standProgress, 2, 1, 0, 10F);
		progressPositionPrev(rightArm, standProgress, -2, 1, 0, 10F);
		progressPositionPrev(head, standProgress, 0, 4, -2, 10F);

		this.walk(leftLeg, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.walk(rightLeg, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.walk(leftArm, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.walk(rightArm, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.flap(body, walkSpeed, walkDegree * 0.2F, true, 1F, 0F, limbSwing, limbSwingAmount);
		if(entityIn.isEating()){
			this.walk(rightArm, eatSpeed, eatDegree, false, 1F, -0.3F, ageInTicks, 1);
			this.walk(leftArm, eatSpeed, eatDegree, false, 1F, -0.3F, ageInTicks, 1);
			this.walk(chest, eatSpeed, eatDegree * 0.1F, false, 2F, 0.3F, ageInTicks, 1);
			this.walk(head, eatSpeed, eatDegree * 0.3F, true, 1F, 0.3F, ageInTicks, 1);
		}
	}


	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, chest, head, foreheadDK_r1, mouth, leftArm, rightArm, rightLeg, leftLeg);
	}

}