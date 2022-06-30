package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelVoidWorm extends AdvancedEntityModel<EntityVoidWorm> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox neck;
	private final AdvancedModelBox topfrills_left;
	private final AdvancedModelBox topfrills_right;
	private final AdvancedModelBox bottomfrills_left;
	private final AdvancedModelBox bottomfrills_right;
	private final AdvancedModelBox head;
	private final AdvancedModelBox eye_bottom_r1;
	private final AdvancedModelBox eye_top_r1;
	private final AdvancedModelBox topjaw;
	private final AdvancedModelBox bottomjaw;

	public ModelVoidWorm(float f) {
		texWidth = 256;
		texHeight = 256;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		neck = new AdvancedModelBox(this);
		neck.setPos(0.0F, -10.0F, 20.0F);
		root.addChild(neck);
		neck.setTextureOffset(0, 53).addBox(-10.0F, -10.0F, -28.0F, 20.0F, 20.0F, 28.0F, f, false);

		topfrills_left = new AdvancedModelBox(this);
		topfrills_left.setPos(10.0F, -10.0F, -20.0F);
		neck.addChild(topfrills_left);
		setRotationAngle(topfrills_left, 0.0F, 0.0F, 0.7854F);
		topfrills_left.setTextureOffset(71, 76).addBox(0.0F, -9.0F, -7.0F, 0.0F, 9.0F, 26.0F, f, false);

		topfrills_right = new AdvancedModelBox(this);
		topfrills_right.setPos(-10.0F, -10.0F, -20.0F);
		neck.addChild(topfrills_right);
		setRotationAngle(topfrills_right, 0.0F, 0.0F, -0.7854F);
		topfrills_right.setTextureOffset(71, 76).addBox(0.0F, -9.0F, -7.0F, 0.0F, 9.0F, 26.0F, f, true);

		bottomfrills_left = new AdvancedModelBox(this);
		bottomfrills_left.setPos(10.0F, 10.0F, -20.0F);
		neck.addChild(bottomfrills_left);
		setRotationAngle(bottomfrills_left, 0.0F, 0.0F, 2.3562F);
		bottomfrills_left.setTextureOffset(71, 76).addBox(0.0F, -9.0F, -7.0F, 0.0F, 9.0F, 26.0F, f, false);

		bottomfrills_right = new AdvancedModelBox(this);
		bottomfrills_right.setPos(-10.0F, 10.0F, -20.0F);
		neck.addChild(bottomfrills_right);
		setRotationAngle(bottomfrills_right, 0.0F, 0.0F, -2.3562F);
		bottomfrills_right.setTextureOffset(71, 76).addBox(0.0F, -9.0F, -7.0F, 0.0F, 9.0F, 26.0F, f, true);

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, 0.0F, -28.0F);
		neck.addChild(head);
		head.setTextureOffset(0, 0).addBox(-17.0F, -17.0F, -18.0F, 34.0F, 34.0F, 18.0F, f, false);
		head.setTextureOffset(25, 102).addBox(17.0F, -5.0F, -14.0F, 2.0F, 10.0F, 10.0F, f, false);
		head.setTextureOffset(0, 102).addBox(-19.0F, -5.0F, -14.0F, 2.0F, 10.0F, 10.0F, f, false);

		eye_bottom_r1 = new AdvancedModelBox(this);
		eye_bottom_r1.setPos(0.0F, 18.0F, -9.0F);
		head.addChild(eye_bottom_r1);
		setRotationAngle(eye_bottom_r1, 0.0F, 0.0F, 1.5708F);
		eye_bottom_r1.setTextureOffset(0, 53).addBox(-1.0F, -5.0F, -5.0F, 2.0F, 10.0F, 10.0F, f, false);

		eye_top_r1 = new AdvancedModelBox(this);
		eye_top_r1.setPos(0.0F, -18.0F, -9.0F);
		head.addChild(eye_top_r1);
		setRotationAngle(eye_top_r1, 0.0F, 0.0F, 1.5708F);
		eye_top_r1.setTextureOffset(69, 54).addBox(-1.0F, -5.0F, -5.0F, 2.0F, 10.0F, 10.0F, f, false);

		topjaw = new AdvancedModelBox(this);
		topjaw.setPos(0.0F, 3.0F, -18.0F);
		head.addChild(topjaw);
		topjaw.setTextureOffset(98, 64).addBox(-5.0F, -10.0F, -16.0F, 10.0F, 10.0F, 16.0F, f, false);

		bottomjaw = new AdvancedModelBox(this);
		bottomjaw.setPos(0.0F, -3.0F, -17.9F);
		head.addChild(bottomjaw);
		bottomjaw.setTextureOffset(89, 37).addBox(-5.0F, 0.0F, -16.0F, 10.0F, 10.0F, 16.0F, f - 0.1F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityVoidWorm entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		this.root.rotateAngleX += headPitch * ((float)Math.PI / 180F);
		this.root.rotationPointY += -3 - Mth.clamp(headPitch * -0.125F, -10, 10) * 0.3F;
		this.root.rotationPointZ += -15 + (limbSwingAmount * 10);
		float yawAmount = (entityIn.prevWormAngle + (entityIn.getWormAngle() - entityIn.prevWormAngle) * (ageInTicks - entityIn.tickCount)) / 57.295776F * 0.5F;
		neck.rotateAngleZ += yawAmount;
		float jawProgress = entityIn.prevJawProgress + (entityIn.jawProgress - entityIn.prevJawProgress) * (ageInTicks - entityIn.tickCount);
		progressRotationPrev(bottomjaw, jawProgress, (float) Math.toRadians(60), 0, 0, 5F);
		progressRotationPrev(topjaw, jawProgress, (float) Math.toRadians(-60), 0, 0, 5F);
		progressPositionPrev(bottomjaw, jawProgress, 0, 2, -5, 5F);
		progressPositionPrev(topjaw, jawProgress, 0, -2, -5, 5F);

	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, neck, head, bottomfrills_left, bottomfrills_right, eye_bottom_r1, eye_top_r1, topfrills_left, topfrills_right, topjaw, bottomjaw);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}