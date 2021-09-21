package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;

public class ModelVoidWormTail extends AdvancedEntityModel<EntityVoidWormPart> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox frillstop_left;
	private final AdvancedModelBox frillstop_right;
	private final AdvancedModelBox frillsbottom_left;
	private final AdvancedModelBox frillsbottom_right;

	public ModelVoidWormTail() {
		texWidth = 256;
		texHeight = 256;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);


		body = new AdvancedModelBox(this);
		body.setPos(0.0F, -19.0F, -16.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-8.0F, -19.0F, 0.0F, 16.0F, 38.0F, 35.0F, 0.0F, false);

		frillstop_left = new AdvancedModelBox(this);
		frillstop_left.setPos(8.0F, -19.0F, 16.0F);
		body.addChild(frillstop_left);
		setRotationAngle(frillstop_left, 0.0F, 0.0F, 0.7854F);
		frillstop_left.setTextureOffset(65, 36).addBox(0.0F, -14.0F, -16.0F, 0.0F, 14.0F, 38.0F, 0.0F, false);

		frillstop_right = new AdvancedModelBox(this);
		frillstop_right.setPos(-8.0F, -19.0F, 16.0F);
		body.addChild(frillstop_right);
		setRotationAngle(frillstop_right, 0.0F, 0.0F, -0.7854F);
		frillstop_right.setTextureOffset(65, 36).addBox(0.0F, -14.0F, -16.0F, 0.0F, 14.0F, 38.0F, 0.0F, true);

		frillsbottom_left = new AdvancedModelBox(this);
		frillsbottom_left.setPos(8.0F, 19.0F, 16.0F);
		body.addChild(frillsbottom_left);
		setRotationAngle(frillsbottom_left, 0.0F, 0.0F, 2.5307F);
		frillsbottom_left.setTextureOffset(65, 36).addBox(0.0F, -14.0F, -16.0F, 0.0F, 14.0F, 38.0F, 0.0F, false);

		frillsbottom_right = new AdvancedModelBox(this);
		frillsbottom_right.setPos(-8.0F, 19.0F, 16.0F);
		body.addChild(frillsbottom_right);
		setRotationAngle(frillsbottom_right, 0.0F, 0.0F, -2.5307F);
		frillsbottom_right.setTextureOffset(65, 36).addBox(0.0F, -14.0F, -16.0F, 0.0F, 14.0F, 38.0F, 0.0F, true);
		this.updateDefaultPose();
	}


	@Override
	public void setupAnim(EntityVoidWormPart entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float yawAmount = (entityIn.prevWormAngle + (entityIn.getWormAngle() - entityIn.prevWormAngle) * (ageInTicks - entityIn.tickCount)) / 57.295776F * 0.5F;
		this.body.rotateAngleZ += yawAmount;
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, frillsbottom_left, frillsbottom_right, frillstop_left, frillstop_right);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}