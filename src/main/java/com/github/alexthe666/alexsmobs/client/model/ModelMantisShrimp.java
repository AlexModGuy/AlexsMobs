package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelMantisShrimp extends AdvancedEntityModel<EntityMantisShrimp> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox tail;
	public final AdvancedModelBox legs_front;
	public final AdvancedModelBox legs_back;
	public final AdvancedModelBox head;
	public final AdvancedModelBox flapper_left;
	public final AdvancedModelBox flapper_right;
	public final AdvancedModelBox eye_left;
	public final AdvancedModelBox eye_right;
	public final AdvancedModelBox arm_left;
	public final AdvancedModelBox fist_left;
	public final AdvancedModelBox arm_right;
	public final AdvancedModelBox fist_right;
	public final AdvancedModelBox whisker_left;
	public final AdvancedModelBox whisker_right;

	public ModelMantisShrimp() {
		texWidth = 128;
		texHeight = 128;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this, "body");
		body.setPos(0.0F, -14.0F, -5.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-6.0F, -2.0F, 0.0F, 12.0F, 10.0F, 25.0F, 0.0F, false);

		tail = new AdvancedModelBox(this, "tail");
		tail.setPos(0.0F, 0.0F, 21.0F);
		body.addChild(tail);
		setRotationAngle(tail, -0.2182F, 0.0F, 0.0F);
		tail.setTextureOffset(50, 0).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 9.0F, 9.0F, 0.0F, false);

		legs_front = new AdvancedModelBox(this, "legs_front");
		legs_front.setPos(0.0F, 5.0F, -7.0F);
		body.addChild(legs_front);
		legs_front.setTextureOffset(0, 61).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 9.0F, 8.0F, 0.0F, false);

		legs_back = new AdvancedModelBox(this, "legs_back");
		legs_back.setPos(0.0F, 8.0F, 1.0F);
		body.addChild(legs_back);
		legs_back.setTextureOffset(0, 36).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 6.0F, 18.0F, 0.0F, false);

		head = new AdvancedModelBox(this, "head");
		head.setPos(0.0F, 4.0F, 2.0F);
		body.addChild(head);
		setRotationAngle(head, 0.3491F, 0.0F, 0.0F);
		head.setTextureOffset(49, 53).addBox(-6.0F, -14.0F, -8.0F, 12.0F, 14.0F, 8.0F, 0.1F, false);

		flapper_left = new AdvancedModelBox(this, "flapper_left");
		flapper_left.setPos(4.0F, -14.0F, -8.0F);
		head.addChild(flapper_left);
		setRotationAngle(flapper_left, -0.48F, 0.2182F, 0.0F);
		flapper_left.setTextureOffset(50, 19).addBox(0.0F, 0.0F, 0.0F, 14.0F, 5.0F, 0.0F, 0.0F, false);

		flapper_right = new AdvancedModelBox(this, "flapper_right");
		flapper_right.setPos(-4.0F, -14.0F, -8.0F);
		head.addChild(flapper_right);
		setRotationAngle(flapper_right, -0.48F, -0.2182F, 0.0F);
		flapper_right.setTextureOffset(50, 19).addBox(-14.0F, 0.0F, 0.0F, 14.0F, 5.0F, 0.0F, 0.0F, true);

		eye_left = new AdvancedModelBox(this, "eye_left");
		eye_left.setPos(3.0F, -14.0F, -4.0F);
		head.addChild(eye_left);
		eye_left.setTextureOffset(0, 15).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

		eye_right = new AdvancedModelBox(this, "eye_right");
		eye_right.setPos(-3.0F, -14.0F, -4.0F);
		head.addChild(eye_right);
		eye_right.setTextureOffset(0, 15).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);

		arm_left = new AdvancedModelBox(this, "arm_left");
		arm_left.setPos(4.0F, -1.0F, -8.0F);
		head.addChild(arm_left);
		arm_left.setTextureOffset(0, 36).addBox(-2.5F, -9.0F, -2.0F, 5.0F, 10.0F, 3.0F, 0.0F, false);

		fist_left = new AdvancedModelBox(this, "fist_left");
		fist_left.setPos(-1.0F, -7.0F, -1.0F);
		arm_left.addChild(fist_left);
		fist_left.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -4.0F, 4.0F, 10.0F, 4.0F, 0.0F, false);

		arm_right = new AdvancedModelBox(this, "arm_right");
		arm_right.setPos(-4.0F, -1.0F, -8.0F);
		head.addChild(arm_right);
		arm_right.setTextureOffset(0, 36).addBox(-2.5F, -9.0F, -2.0F, 5.0F, 10.0F, 3.0F, 0.0F, true);

		fist_right = new AdvancedModelBox(this, "fist_right");
		fist_right.setPos(1.0F, -7.0F, -1.0F);
		arm_right.addChild(fist_right);
		fist_right.setTextureOffset(0, 0).addBox(-3.0F, -1.0F, -4.0F, 4.0F, 10.0F, 4.0F, 0.0F, true);

		whisker_left = new AdvancedModelBox(this, "whisker_left");
		whisker_left.setPos(1.0F, -14.0F, -8.0F);
		head.addChild(whisker_left);
		setRotationAngle(whisker_left, 0.0F, -0.3927F, 0.0F);
		whisker_left.setTextureOffset(39, 39).addBox(0.0F, 0.0F, -13.0F, 8.0F, 0.0F, 13.0F, 0.0F, false);

		whisker_right = new AdvancedModelBox(this, "whisker_right");
		whisker_right.setPos(-1.0F, -14.0F, -8.0F);
		head.addChild(whisker_right);
		setRotationAngle(whisker_right, 0.0F, 0.3927F, 0.0F);
		whisker_right.setTextureOffset(39, 39).addBox(-8.0F, 0.0F, -13.0F, 8.0F, 0.0F, 13.0F, 0.0F, true);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, head, eye_left, eye_right, fist_left, fist_right, arm_left, arm_right, whisker_left, whisker_right, flapper_left, flapper_right, tail, legs_back, legs_front);
	}

	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			this.eye_left.setScale(1.15F, 1.15F, 1.15F);
			this.eye_right.setScale(1.15F, 1.15F, 1.15F);
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.translate(0.0D, 1.5D, 0.125D);
			parts().forEach((p_228292_8_) -> {
				p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
		} else {
			this.eye_left.setScale(1F, 1F, 1F);
			this.eye_right.setScale(1F, 1F, 1F);
			matrixStackIn.pushPose();
			parts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
		}

	}


	@Override
	public void setupAnim(EntityMantisShrimp entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();
		float idleSpeed = 0.1f;
		float idleDegree = 0.3f;
		float walkSpeed = 0.9f;
		float walkDegree = 0.6F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float swimProgress = (Math.min(limbSwingAmount, 0.25F) * 4F) * (entity.prevInWaterProgress + (entity.inWaterProgress - entity.prevInWaterProgress) * partialTick);
		float punchProgress = entity.prevPunchProgress + (entity.punchProgress - entity.prevPunchProgress) * partialTick;
		float leftEyePitch = entity.prevLeftPitch + (entity.getEyePitch(true) - entity.prevLeftPitch) * partialTick;
		float rightEyePitch = entity.prevRightPitch + (entity.getEyePitch(false) - entity.prevRightPitch) * partialTick;
		float leftEyeYaw = entity.prevLeftYaw + (entity.getEyeYaw(true) - entity.prevLeftYaw) * partialTick;
		float rightEyeYaw = entity.prevRightYaw + (entity.getEyeYaw(false) - entity.prevRightYaw) * partialTick;
		this.eye_left.rotateAngleX += leftEyePitch * ((float)Math.PI / 180F);
		this.eye_left.rotateAngleY += leftEyeYaw * ((float)Math.PI / 180F);
		this.eye_right.rotateAngleX += rightEyePitch * ((float)Math.PI / 180F);
		this.eye_right.rotateAngleY += rightEyeYaw * ((float)Math.PI / 180F);
		this.head.rotateAngleY += netHeadYaw * 0.5F * ((float)Math.PI / 180F);
		this.head.rotateAngleX += headPitch * 0.8F * ((float)Math.PI / 180F);
		this.walk(whisker_left, idleSpeed * 1.5F, idleDegree, false, 0F, -0.25F, ageInTicks, 1);
		this.walk(whisker_right, idleSpeed * 1.5F, idleDegree, true, 0F, 0.25F, ageInTicks, 1);
		this.swing(whisker_left, idleSpeed * 1F, idleDegree * 0.75F, false, 1F, 0F, ageInTicks, 1);
		this.swing(whisker_right, idleSpeed * 1F, idleDegree * 0.75F, false, 1F, 0F, ageInTicks, 1);
		this.swing(flapper_left, idleSpeed * 1F, idleDegree * 0.75F, false, 2F, -0.3F, ageInTicks, 1);
		this.swing(flapper_right, idleSpeed * 1F, idleDegree * 0.75F, true, 2F, -0.3F, ageInTicks, 1);
		this.swing(arm_left, idleSpeed * 1F, idleDegree * 0.5F, false, 1F, -0.2F, ageInTicks, 1);
		this.swing(arm_right, idleSpeed * 1F, idleDegree * 0.5F, true, 1F, -0.2F, ageInTicks, 1);
		this.walk(legs_front, walkSpeed, walkDegree * 1, true, 2, 0, limbSwing, limbSwingAmount);
		this.bob(legs_front, walkSpeed * 0.5F, walkDegree * 4F, true, limbSwing, limbSwingAmount);
		this.walk(legs_back, walkSpeed, walkDegree * 0.2F, true, 2, 0, limbSwing, limbSwingAmount);
		this.bob(legs_back, walkSpeed * 0.5F, walkDegree * 4F, true, limbSwing, limbSwingAmount);
		progressRotationPrev(head, Math.max(0, swimProgress - punchProgress * 2.5F), (float)Math.toRadians(45), 0, 0, 5F);
		progressRotationPrev(whisker_left, swimProgress, (float)Math.toRadians(-45), 0, 0, 5F);
		progressRotationPrev(whisker_right, swimProgress, (float)Math.toRadians(-45), 0, 0, 5F);
		progressRotationPrev(arm_left, swimProgress, (float)Math.toRadians(20), 0, 0, 5F);
		progressRotationPrev(arm_right, swimProgress, (float)Math.toRadians(20), 0, 0, 5F);
		progressPositionPrev(head, swimProgress, 0, -6, 0, 5F);
		progressPositionPrev(arm_left, swimProgress, 0, -3, 0, 5F);
		progressPositionPrev(arm_right, swimProgress, 0, -3, 0, 5F);
		if(swimProgress > 0){
			this.bob(body, walkSpeed * 0.5F, walkDegree * 4F, true, limbSwing, limbSwingAmount);
			this.walk(body, walkSpeed * 1F, walkDegree * 0.2F, true, 3F, 0F, limbSwing, limbSwingAmount);
			this.walk(tail, walkSpeed * 1F, walkDegree * 0.5F, true, 3F, -0.2F, limbSwing, limbSwingAmount);
			this.walk(head, walkSpeed * 1F, walkDegree * 0.1F, true, 2F, 0F, limbSwing, limbSwingAmount);

		}
		progressPositionPrev(arm_right, punchProgress, 1, -7, 0, 2F);
		progressPositionPrev(arm_left, punchProgress, -1, -7, 0, 2F);
		progressPositionPrev(fist_right, punchProgress, 0, -2, -2, 2F);
		progressPositionPrev(fist_left, punchProgress, 0, -2, -2, 2F);
		progressRotationPrev(arm_right, punchProgress, (float)Math.toRadians(70), 0, 0, 2F);
		progressRotationPrev(fist_right, punchProgress, (float)Math.toRadians(-240),0,  (float)Math.toRadians(10), 2F);
		progressRotationPrev(arm_left, punchProgress, (float)Math.toRadians(70), 0, 0, 2F);
		progressRotationPrev(fist_left, punchProgress, (float)Math.toRadians(-240), 0,  (float)Math.toRadians(-10), 2F);
		progressRotationPrev(flapper_right, punchProgress, 0, (float)Math.toRadians(50), 0, 2F);
		progressRotationPrev(flapper_left, punchProgress, 0, (float)Math.toRadians(-50), 0, 2F);

	}


	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}