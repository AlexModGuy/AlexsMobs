package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelGorilla extends AdvancedEntityModel<EntityGorilla> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox bodyfront;
	public final AdvancedModelBox head;
	public final AdvancedModelBox head_r1;
	public final AdvancedModelBox forehead;
	public final AdvancedModelBox forehead_r1;
	public final AdvancedModelBox mouth;
	public final AdvancedModelBox armL;
	public final AdvancedModelBox armR;
	public final AdvancedModelBox legL;
	public final AdvancedModelBox legR;
	private ModelAnimator animator;

	public ModelGorilla() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setPos(0.0F, -10.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 17).addBox(-4.0F, -5.0F, 0.0F, 8.0F, 7.0F, 8.0F, 0.0F, false);

		bodyfront = new AdvancedModelBox(this);
		bodyfront.setPos(0.0F, -2.0F, 0.0F);
		body.addChild(bodyfront);
		bodyfront.setTextureOffset(0, 0).addBox(-5.0F, -4.0F, -8.0F, 10.0F, 8.0F, 8.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, -3.6667F, -7.6667F);
		bodyfront.addChild(head);
		

		head_r1 = new AdvancedModelBox(this);
		head_r1.setPos(0.0F, -0.3333F, -1.3333F);
		head.addChild(head_r1);
		setRotationAngle(head_r1, 0.0F, 0.0436F, 0.0F);
		head_r1.setTextureOffset(27, 27).addBox(-3.0F, -4.0F, -2.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

		forehead = new AdvancedModelBox(this);
		forehead.setPos(0.0F, -3.3333F, 0.6667F);
		head.addChild(forehead);
		

		forehead_r1 = new AdvancedModelBox(this);
		forehead_r1.setPos(0.0F, 1.182F, 0.4021F);
		forehead.addChild(forehead_r1);
		setRotationAngle(forehead_r1, 0.3142F, 0.0F, 0.0F);
		forehead_r1.setTextureOffset(31, 11).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, 0.0F, false);

		mouth = new AdvancedModelBox(this);
		mouth.setPos(0.0F, 0.6667F, -3.3333F);
		head.addChild(mouth);
		mouth.setTextureOffset(37, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

		armL = new AdvancedModelBox(this);
		armL.setPos(5.0F, -1.5F, -7.0F);
		bodyfront.addChild(armL);
		armL.setTextureOffset(0, 33).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 15.0F, 4.0F, 0.0F, false);

		armR = new AdvancedModelBox(this);
		armR.setPos(-5.0F, -1.5F, -7.0F);
		bodyfront.addChild(armR);
		armR.setTextureOffset(0, 33).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 15.0F, 4.0F, 0.0F, true);

		legL = new AdvancedModelBox(this);
		legL.setPos(2.0F, 2.0F, 6.0F);
		body.addChild(legL);
		legL.setTextureOffset(17, 40).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 8.0F, 4.0F, 0.0F, false);

		legR = new AdvancedModelBox(this);
		legR.setPos(-2.0F, 2.0F, 6.0F);
		body.addChild(legR);
		legR.setTextureOffset(17, 40).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 8.0F, 4.0F, 0.0F, true);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityGorilla.ANIMATION_BREAKBLOCK_R);
		animator.startKeyframe(7);
		animator.rotate(bodyfront, 0, (float)Math.toRadians(-10F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(-10F));
		animator.rotate(armR, (float)Math.toRadians(65F), (float)Math.toRadians(-20F), 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(bodyfront, 0, (float)Math.toRadians(20F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(0));
		animator.rotate(armR, (float)Math.toRadians(-80F),  (float)Math.toRadians(-30F),0);
		animator.endKeyframe();
		animator.resetKeyframe(6);
		animator.setAnimation(EntityGorilla.ANIMATION_BREAKBLOCK_L);
		animator.startKeyframe(7);
		animator.rotate(bodyfront, 0, (float)Math.toRadians(10F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(10F));
		animator.rotate(armL, (float)Math.toRadians(65F), (float)Math.toRadians(20F), 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(bodyfront, 0, (float)Math.toRadians(-20F), 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(0));
		animator.rotate(armL, (float)Math.toRadians(-80F),  (float)Math.toRadians(30F),0);
		animator.endKeyframe();
		animator.resetKeyframe(6);
		animator.setAnimation(EntityGorilla.ANIMATION_POUNDCHEST);
		animator.startKeyframe(5);
		animator.rotate(armR, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(armL, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-20F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(armR, 0, 3, 0);
		animator.rotate(head, (float)Math.toRadians(-0),  (float)Math.toRadians(10F), 0);
		animator.rotate(bodyfront, 0, 0,  (float)Math.toRadians(10F));
		animator.rotate(armR, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-80F));
		animator.rotate(armL, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-40F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(armL, 0, 3, 0);
		animator.rotate(head, (float)Math.toRadians(0),  (float)Math.toRadians(-10F), 0);
		animator.rotate(bodyfront, 0, 0,  (float)Math.toRadians(-10F));
		animator.rotate(armR, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(armL, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(60F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(armR, 0, 3, 0);
		animator.rotate(head, (float)Math.toRadians(-0),  (float)Math.toRadians(10F), 0);
		animator.rotate(armR, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-80F));
		animator.rotate(armL, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-40F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(armL, 0, 3, 0);
		animator.rotate(head, (float)Math.toRadians(0),  (float)Math.toRadians(-10F), 0);
		animator.rotate(bodyfront, 0, 0,  (float)Math.toRadians(-10F));
		animator.rotate(armR, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(armL, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(60F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(armR, 0, 3, 0);
		animator.rotate(bodyfront, 0, 0,  (float)Math.toRadians(10F));
		animator.rotate(head, (float)Math.toRadians(-0),  (float)Math.toRadians(10F), 0);
		animator.rotate(armR, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-80F));
		animator.rotate(armL, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(-40F));
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(armL, 0, 3, 0);
		animator.rotate(bodyfront, 0, 0,  (float)Math.toRadians(-10F));
		animator.rotate(head, (float)Math.toRadians(0),  (float)Math.toRadians(-10F), 0);
		animator.rotate(armR, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(20F));
		animator.rotate(armL, (float)Math.toRadians(-60F), 0, (float)Math.toRadians(60F));
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityGorilla.ANIMATION_ATTACK);
		animator.startKeyframe(7);
		animator.rotate(armL, (float)Math.toRadians(65F), (float)Math.toRadians(10F), 0);
		animator.rotate(armR, (float)Math.toRadians(65F), (float)Math.toRadians(-10F), 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.rotate(armL, (float)Math.toRadians(-90F),  (float)Math.toRadians(30F),0);
		animator.rotate(armR, (float)Math.toRadians(-90F),  (float)Math.toRadians(-30F),0);
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
		float rideProgress = entityIn.isPassenger() ? 5F : 0;
		this.faceTarget(netHeadYaw, headPitch, 1, head);
		progressRotationPrev(armL, rideProgress, (float)Math.toRadians(-20), (float)Math.toRadians(-20), (float)Math.toRadians(-40), 5F);
		progressRotationPrev(armR, rideProgress, (float)Math.toRadians(-20), (float)Math.toRadians(20), (float)Math.toRadians(40), 5F);
		progressRotationPrev(legL, rideProgress, (float)Math.toRadians(-20), 0, (float)Math.toRadians(-80), 5F);
		progressRotationPrev(legR, rideProgress, (float)Math.toRadians(-20), 0, (float)Math.toRadians(80), 5F);
		progressRotationPrev(head, rideProgress, (float)Math.toRadians(15), 0, 0, 5F);
		progressRotationPrev(body, rideProgress, (float)Math.toRadians(-10), 0, 0, 5F);
		progressPositionPrev(body, rideProgress, 0, 5, 3, 5F);

		progressRotationPrev(body, sitProgress, (float)Math.toRadians(-80), 0, 0, 10F);
		progressRotationPrev(legR, sitProgress, (float)Math.toRadians(-10), (float)Math.toRadians(-30), (float)Math.toRadians(30), 10F);
		progressRotationPrev(legL, sitProgress, (float)Math.toRadians(-10), (float)Math.toRadians(30), (float)Math.toRadians(-30), 10F);
		progressRotationPrev(head, sitProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(armL, sitProgress, (float)Math.toRadians(20), 0, 0, 10F);
		progressRotationPrev(armR, sitProgress, (float)Math.toRadians(20), 0, 0, 10F);
		progressPositionPrev(body, sitProgress, 0, 2, 0, 10F);
		progressPositionPrev(head, sitProgress, 0, 4, -2, 10F);
		progressPositionPrev(armL, sitProgress, 0, 0, 2, 10F);
		progressPositionPrev(armR, sitProgress, 0, 0, 2, 10F);
		progressRotationPrev(body, standProgress, (float)Math.toRadians(-80), 0, 0, 10F);
		progressRotationPrev(legR, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(legL, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(head, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(armL, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressRotationPrev(armR, standProgress, (float)Math.toRadians(80), 0, 0, 10F);
		progressPositionPrev(body, standProgress, 0, -5, 0, 10F);
		progressPositionPrev(legR, standProgress, -1, -3, 1.2F, 10F);
		progressPositionPrev(legL, standProgress, 1, -3, 1.2F, 10F);
		progressPositionPrev(armL, standProgress, 2, 1, 0, 10F);
		progressPositionPrev(armR, standProgress, -2, 1, 0, 10F);
		progressPositionPrev(head, standProgress, 0, 4, -2, 10F);

		this.walk(legL, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.walk(legR, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.walk(armL, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.walk(armR, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.flap(body, walkSpeed, walkDegree * 0.2F, true, 1F, 0F, limbSwing, limbSwingAmount);
		if(entityIn.isEating()){
			this.walk(armR, eatSpeed, eatDegree, false, 1F, -0.3F, ageInTicks, 1);
			this.walk(armL, eatSpeed, eatDegree, false, 1F, -0.3F, ageInTicks, 1);
			this.walk(bodyfront, eatSpeed, eatDegree * 0.1F, false, 2F, 0.3F, ageInTicks, 1);
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
		return ImmutableList.of(root, body, bodyfront, head, head_r1, forehead, forehead_r1, mouth, armL, armR, legR, legL);
	}

}