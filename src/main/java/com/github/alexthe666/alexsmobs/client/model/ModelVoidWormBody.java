package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class ModelVoidWormBody extends AdvancedEntityModel<EntityVoidWormPart> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox frillstop_left;
	private final AdvancedModelBox frillstop_left_r1;
	private final AdvancedModelBox frillstop_right;
	private final AdvancedModelBox frillstop_right_r1;
	private final AdvancedModelBox frillsbottom_left;
	private final AdvancedModelBox frillsbottom_left_r1;
	private final AdvancedModelBox frillsbottom_right;
	private final AdvancedModelBox frillsbottom_right_r1;

	public ModelVoidWormBody(float f) {
		texWidth = 128;
		texHeight = 128;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);


		body = new AdvancedModelBox(this, "body");
		body.setPos(0.0F, -14.0F, -10.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-7.0F, -12.0F, 0.0F, 14.0F, 26.0F, 21.0F, f, false);

		frillstop_left = new AdvancedModelBox(this, "frillstop_left");
		frillstop_left.setPos(7.0F, -12.0F, 10.0F);
		body.addChild(frillstop_left);


		frillstop_left_r1 = new AdvancedModelBox(this, "frillstop_left_r1");
		frillstop_left_r1.setPos(0.0F, 0.0F, 0.0F);
		frillstop_left.addChild(frillstop_left_r1);
		setRotationAngle(frillstop_left_r1, 0.0F, 0.0F, 0.7854F);
		frillstop_left_r1.setTextureOffset(0, 48).addBox(0.0F, -8.0F, -10.0F, 0.0F, 8.0F, 21.0F, f, false);

		frillstop_right = new AdvancedModelBox(this, "frillstop_right");
		frillstop_right.setPos(-7.0F, -12.0F, 10.0F);
		body.addChild(frillstop_right);


		frillstop_right_r1 = new AdvancedModelBox(this, "frillstop_right_r1");
		frillstop_right_r1.setPos(0.0F, 0.0F, 0.0F);
		frillstop_right.addChild(frillstop_right_r1);
		setRotationAngle(frillstop_right_r1, 0.0F, 0.0F, -0.7854F);
		frillstop_right_r1.setTextureOffset(0, 48).addBox(0.0F, -8.0F, -10.0F, 0.0F, 8.0F, 21.0F, f, true);

		frillsbottom_left = new AdvancedModelBox(this, "frillsbottom_left");
		frillsbottom_left.setPos(7.0F, 14.0F, 10.0F);
		body.addChild(frillsbottom_left);
		setRotationAngle(frillsbottom_left, 0.0F, 0.0F, 1.6581F);


		frillsbottom_left_r1 = new AdvancedModelBox(this, "frillsbottom_left_r1");
		frillsbottom_left_r1.setPos(0.0F, 0.0F, 0.0F);
		frillsbottom_left.addChild(frillsbottom_left_r1);
		setRotationAngle(frillsbottom_left_r1, 0.0F, 0.0F, 0.7854F);
		frillsbottom_left_r1.setTextureOffset(0, 48).addBox(0.0F, -8.0F, -10.0F, 0.0F, 8.0F, 21.0F, f, false);

		frillsbottom_right = new AdvancedModelBox(this, "frillsbottom_right");
		frillsbottom_right.setPos(-7.0F, 14.0F, 10.0F);
		body.addChild(frillsbottom_right);
		setRotationAngle(frillsbottom_right, 0.0F, 0.0F, -1.6581F);


		frillsbottom_right_r1 = new AdvancedModelBox(this, "frillsbottom_right_r1");
		frillsbottom_right_r1.setPos(0.0F, 0.0F, 0.0F);
		frillsbottom_right.addChild(frillsbottom_right_r1);
		setRotationAngle(frillsbottom_right_r1, 0.0F, 0.0F, -0.7854F);
		frillsbottom_right_r1.setTextureOffset(0, 48).addBox(0.0F, -8.0F, -10.0F, 0.0F, 8.0F, 21.0F, f, true);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityVoidWormPart entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		this.root.rotateAngleX += headPitch * Mth.DEG_TO_RAD;
		float yawAmount = (entityIn.prevWormAngle + (entityIn.getWormAngle() - entityIn.prevWormAngle) * (ageInTicks - entityIn.tickCount)) / 57.295776F * 0.5F;
		this.body.rotateAngleZ += yawAmount;
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, frillsbottom_left, frillsbottom_left_r1, frillsbottom_right, frillsbottom_right_r1, frillstop_left, frillstop_left_r1, frillstop_right, frillstop_right_r1);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}