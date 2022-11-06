package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelKomodoDragon extends AdvancedEntityModel<EntityKomodoDragon> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox neck;
	private final AdvancedModelBox head;
	private final AdvancedModelBox tongue;
	private final AdvancedModelBox tail1;
	private final AdvancedModelBox tail2;
	private final AdvancedModelBox left_shoulder;
	private final AdvancedModelBox left_arm;
	private final AdvancedModelBox right_shoulder;
	private final AdvancedModelBox right_arm;
	private final AdvancedModelBox left_hip;
	private final AdvancedModelBox left_leg;
	private final AdvancedModelBox right_hip;
	private final AdvancedModelBox right_leg;

	public ModelKomodoDragon(float scale) {
		texHeight = 128;
		texWidth = 128;

		root = new AdvancedModelBox(this, "root");
		root.setRotationPoint(0.0F, 24.0F, 0.0F);

		body = new AdvancedModelBox(this, "body");
		body.setRotationPoint(0.0F, -11.0F, -1.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-5.0F, -4.0F, -11.0F, 10.0F, 9.0F, 23.0F, scale, false);
		neck = new AdvancedModelBox(this, "neck");
		neck.setRotationPoint(0.0F, -1.0F, -10.0F);
		body.addChild(neck);
		neck.setTextureOffset(0, 60).addBox(-3.5F, -3.0F, -7.0F, 7.0F, 7.0F, 6.0F, scale, false);

		head = new AdvancedModelBox(this, "head");
		head.setRotationPoint(0.0F, -1.0F, -7.0F);
		neck.addChild(head);
		head.setTextureOffset(44, 0).addBox(-3.5F, -2.0F, -10.0F, 7.0F, 5.0F, 10.0F, scale, false);

		tongue = new AdvancedModelBox(this, "tongue");
		tongue.setRotationPoint(0.0F, 1.0F, -10.0F);
		head.addChild(tongue);
		tongue.setTextureOffset(60, 26).addBox(-1.5F, 0.0F, -7.0F, 3.0F, 0.0F, 7.0F, scale, false);

		tail1 = new AdvancedModelBox(this, "tail1");
		tail1.setRotationPoint(0.0F, -1.0F, 11.0F);
		body.addChild(tail1);
		tail1.setTextureOffset(0, 33).addBox(-3.0F, -2.0F, 1.0F, 6.0F, 6.0F, 20.0F, scale, false);

		tail2 = new AdvancedModelBox(this, "tail2");
		tail2.setRotationPoint(0.0F, 1.0F, 20.0F);
		tail1.addChild(tail2);
		tail2.setTextureOffset(35, 42).addBox(-1.5F, -2.0F, 1.0F, 3.0F, 4.0F, 18.0F, scale, false);

		left_shoulder = new AdvancedModelBox(this, "left_shoulder");
		left_shoulder.setRotationPoint(1.5F, 2.0F, -5.5F);
		body.addChild(left_shoulder);

		left_arm = new AdvancedModelBox(this, "left_arm");
		left_arm.setRotationPoint(4.5F, 0.0F, -0.5F);
		left_shoulder.addChild(left_arm);
		left_arm.setTextureOffset(0, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale, false);
		left_arm.setTextureOffset(57, 34).addBox(-4.0F, 8.99F, -5.0F, 8.0F, 0.0F, 7.0F, scale, false);

		right_shoulder = new AdvancedModelBox(this, "right_shoulder");
		right_shoulder.setRotationPoint(-1.5F, 2.0F, -5.5F);
		body.addChild(right_shoulder);

		right_arm = new AdvancedModelBox(this, "right_arm");
		right_arm.setRotationPoint(-4.5F, 0.0F, -0.5F);
		right_shoulder.addChild(right_arm);
		right_arm.setTextureOffset(0, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale, true);
		right_arm.setTextureOffset(57, 34).addBox(-4.0F, 8.99F, -5.0F, 8.0F, 0.0F, 7.0F, scale, true);

		left_hip = new AdvancedModelBox(this, "left_hip");
		left_hip.setRotationPoint(1.5F, 2.0F, 7.5F);
		body.addChild(left_hip);

		left_leg = new AdvancedModelBox(this, "left_leg");
		left_leg.setRotationPoint(2.5F, 0.0F, 0.5F);
		left_hip.addChild(left_leg);
		left_leg.setTextureOffset(0, 0).addBox(-1.0F, -3.0F, -2.0F, 4.0F, 12.0F, 5.0F, scale, false);
		left_leg.setTextureOffset(33, 33).addBox(-3.0F, 8.99F, -5.0F, 8.0F, 0.0F, 7.0F, scale, false);

		right_hip = new AdvancedModelBox(this, "right_hip");
		right_hip.setRotationPoint(-1.5F, 2.0F, 7.5F);
		body.addChild(right_hip);

		right_leg = new AdvancedModelBox(this, "right_leg");
		right_leg.setRotationPoint(-2.5F, 0.0F, 0.5F);
		right_hip.addChild(right_leg);
		right_leg.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -2.0F, 4.0F, 12.0F, 5.0F, scale, true);
		right_leg.setTextureOffset(33, 33).addBox(-5.0F, 8.99F, -5.0F, 8.0F, 0.0F, 7.0F, scale, true);
		this.setRotationAngle(tail1, (float)Math.toRadians(-23F), 0, 0);
		this.setRotationAngle(tail2, (float)Math.toRadians(23F), 0, 0);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityKomodoDragon entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float idleSpeed = 0.7F;
		float idleDegree = 0.7F;
		float walkSpeed = 0.5F;
		float walkDegree = 0.7F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float sitProgress = entityIn.prevSitProgress + (entityIn.sitProgress - entityIn.prevSitProgress) * partialTick;
		float jostleProgress = entityIn.prevJostleProgress + (entityIn.jostleProgress - entityIn.prevJostleProgress) * partialTick;
		float jostleAngle = entityIn.prevJostleAngle + (entityIn.getJostleAngle() - entityIn.prevJostleAngle) * partialTick;
		float toungeF = (float) Math.min(Math.sin(ageInTicks * 0.3F), 0) * 9F;
		float toungeMinus = (float) Math.max(Math.sin(ageInTicks * 0.3F), 0);
		progressRotationPrev(tail1, limbSwingAmount, (float) Math.toRadians(13), 0,  0, 1F);
		progressRotationPrev(tail2, limbSwingAmount, (float) Math.toRadians(-13), 0,  0, 1F);
		progressPositionPrev(body, sitProgress, 0, 1, 0, 5F);
		progressPositionPrev(right_leg, sitProgress, 0, 3, 0, 5F);
		progressPositionPrev(left_leg, sitProgress, 0, 3, 0, 5F);
		progressPositionPrev(left_arm, sitProgress, 0, 1.5F, 0, 5F);
		progressPositionPrev(right_arm, sitProgress, 0, 1.5F, 0, 5F);
		progressRotationPrev(body, sitProgress, (float) Math.toRadians(-25), 0, 0, 5F);
		progressRotationPrev(head, sitProgress, (float) Math.toRadians(8), 0, 0, 5F);
		progressRotationPrev(tail1, sitProgress, (float) Math.toRadians(35),  (float) Math.toRadians(15), 0, 5F);
		progressRotationPrev(right_leg, sitProgress, (float) Math.toRadians(25), (float) Math.toRadians(-25), (float) Math.toRadians(70), 5F);
		progressRotationPrev(left_leg, sitProgress, (float) Math.toRadians(25), (float) Math.toRadians(25), (float) Math.toRadians(-70), 5F);
		progressRotationPrev(right_arm, sitProgress, (float) Math.toRadians(25),  (float) Math.toRadians(-15), 0, 5F);
		progressRotationPrev(left_arm, sitProgress, (float) Math.toRadians(25),  (float) Math.toRadians(15), 0, 5F);
		progressRotationPrev(body, jostleProgress, (float) Math.toRadians(-55), 0, 0, 5F);
		progressPositionPrev(body, jostleProgress, 0, -6, -3, 5F);
		progressRotationPrev(right_leg, jostleProgress, (float) Math.toRadians(55),  -(float) Math.toRadians(15), 0, 5F);
		progressRotationPrev(left_leg, jostleProgress, (float) Math.toRadians(55),  (float) Math.toRadians(15), 0, 5F);
		progressRotationPrev(tail1, jostleProgress, (float) Math.toRadians(60), 0, 0, 5F);
		progressRotationPrev(neck, jostleProgress, (float) Math.toRadians(2), 0, 0, 5F);
		progressRotationPrev(head, jostleProgress, (float) Math.toRadians(14), 0, 0, 5F);
		progressPositionPrev(right_arm, jostleProgress, 0, 0, -2, 5F);
		progressPositionPrev(left_arm, jostleProgress, 0, 0, -2, 5F);
		progressRotationPrev(right_arm, jostleProgress, (float) Math.toRadians(-10), (float) Math.toRadians(90), 0, 5F);
		progressRotationPrev(left_arm, jostleProgress, (float) Math.toRadians(-10), (float) Math.toRadians(-90), 0, 5F);


		this.flap(body, walkSpeed, walkDegree * 0.5F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.swing(body, walkSpeed, walkDegree * 0.5F, false, 1F, 0F, limbSwing, limbSwingAmount);
		this.flap(neck, walkSpeed, walkDegree * -0.25F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.swing(neck, walkSpeed, walkDegree * -0.25F, false, 1F, 0F, limbSwing, limbSwingAmount);
		this.flap(head, walkSpeed, walkDegree * -0.25F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.swing(head, walkSpeed, walkDegree * -0.25F, false, 1F, 0F, limbSwing, limbSwingAmount);
		this.flap(tail1, walkSpeed, walkDegree * -0.5F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.swing(tail1, walkSpeed, walkDegree * 0.5F, false, 2F, 0F, limbSwing, limbSwingAmount);
		this.swing(tail2, walkSpeed, walkDegree * 0.5F, false, 2F, 0F, limbSwing, limbSwingAmount);
		this.walk(left_arm, walkSpeed, walkDegree * 1.2F, false, -2.5F, -0.25F, limbSwing, limbSwingAmount);
		this.walk(right_arm, walkSpeed, walkDegree * 1.2F, true, -2.5F, 0.25F, limbSwing, limbSwingAmount);
		this.walk(right_leg, walkSpeed, walkDegree * 1.2F, false, -2.5F, 0.25F, limbSwing, limbSwingAmount);
		this.walk(left_leg, walkSpeed, walkDegree * 1.2F, true, -2.5F, -0.25F, limbSwing, limbSwingAmount);

		this.flap(left_arm, walkSpeed, walkDegree, false, -2.5F, -0.25F, limbSwing, limbSwingAmount);
		this.flap(right_arm, walkSpeed, walkDegree, false, -2.5F, 0.25F, limbSwing, limbSwingAmount);
		this.flap(right_leg, walkSpeed, walkDegree, false, -2.5F, 0.25F, limbSwing, limbSwingAmount);
		this.flap(left_leg, walkSpeed, walkDegree, false, -2.5F, -0.25F, limbSwing, limbSwingAmount);

		this.left_arm.rotationPointY += 1.5F * (float) (Math.sin((double) (limbSwing * walkSpeed) - 2.5F) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));
		this.right_arm.rotationPointY += 1.5F * (float) (Math.sin(-(double) (limbSwing * walkSpeed) + 2.5F) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));
		this.left_leg.rotationPointY += 1.5F * (float) (Math.sin((double) (limbSwing * walkSpeed) - 2.5F) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));
		this.right_leg.rotationPointY += 1.5F * (float) (Math.sin(-(double) (limbSwing * walkSpeed) + 2.5F) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));

		this.walk(tongue, idleSpeed * 2F, idleDegree, false, 0F, 0F, ageInTicks,  toungeMinus);
		this.walk(neck, idleSpeed  * 0.1F, idleDegree * 0.1F, false, 2F, 0F, ageInTicks,  1F);
		this.walk(head, idleSpeed * 0.1F, idleDegree * 0.1F, false, 4F, 0F, ageInTicks,  1F);
		this.tongue.rotationPointZ -= toungeF;
		progressPositionPrev(neck, jostleProgress, 0, 0, 1, 5F);
		progressPositionPrev(head, jostleProgress, 0, 0, 1, 5F);
		if (jostleProgress > 0) {
			float jostleScale = 2.5F;
			float yawAmount = jostleAngle / 57.295776F * jostleProgress * 0.2F * jostleScale;
			float inv = (10F - Math.abs(jostleAngle)) / 57.295776F * jostleProgress * 0.2F * jostleScale * 0.5F;
			this.right_shoulder.rotateAngleX += yawAmount;
			this.left_shoulder.rotateAngleX += yawAmount;
			this.neck.rotateAngleY += yawAmount;
			this.head.rotateAngleY += yawAmount;
			this.neck.rotateAngleX -= inv;
			this.head.rotateAngleX -= inv;
		} else {
			this.faceTarget(netHeadYaw, headPitch, 1, neck, head);
		}

	}

	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			float f = 1.75F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.35F, 0.35F, 0.35F);
			matrixStackIn.translate(0.0D, 2.75D, 0.125D);
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
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, tail2, tail1, left_leg, right_leg, left_shoulder, right_shoulder, left_arm, right_arm, left_hip, right_hip, neck, head, tongue);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}