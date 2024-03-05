package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelCatfishSmall extends AdvancedEntityModel<EntityCatfish> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox left_barbel;
	private final AdvancedModelBox right_barbel;
	private final AdvancedModelBox dorsal_fin;
	private final AdvancedModelBox left_fin;
	private final AdvancedModelBox right_fin;
	private final AdvancedModelBox tail;
	private final AdvancedModelBox tail_fin;

	public ModelCatfishSmall() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this, "root");
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this, "body");
		body.setRotationPoint(0.0F, -3.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 6.0F, 14.0F, 0.0F, false);

		left_barbel = new AdvancedModelBox(this, "left_barbel");
		left_barbel.setRotationPoint(4.0F, 1.0F, -6.0F);
		body.addChild(left_barbel);
		setRotationAngle(left_barbel, 0.0F, -0.6109F, 0.0F);
		left_barbel.setTextureOffset(0, 10).addBox(0.0F, 0.0F, 0.0F, 6.0F, 3.0F, 0.0F, 0.0F, false);

		right_barbel = new AdvancedModelBox(this, "right_barbel");
		right_barbel.setRotationPoint(-4.0F, 1.0F, -6.0F);
		body.addChild(right_barbel);
		setRotationAngle(right_barbel, 0.0F, 0.6109F, 0.0F);
		right_barbel.setTextureOffset(0, 10).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 3.0F, 0.0F, 0.0F, true);

		dorsal_fin = new AdvancedModelBox(this, "dorsal_fin");
		dorsal_fin.setRotationPoint(0.0F, -3.0F, -2.0F);
		body.addChild(dorsal_fin);
		setRotationAngle(dorsal_fin, -0.1309F, 0.0F, 0.0F);
		dorsal_fin.setTextureOffset(0, 21).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 5.0F, 0.0F, false);

		left_fin = new AdvancedModelBox(this, "left_fin");
		left_fin.setRotationPoint(4.0F, 2.0F, -3.0F);
		body.addChild(left_fin);
		setRotationAngle(left_fin, 0.0F, 0.0F, -0.9163F);
		left_fin.setTextureOffset(19, 21).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 4.0F, 0.0F, false);

		right_fin = new AdvancedModelBox(this, "right_fin");
		right_fin.setRotationPoint(-4.0F, 2.0F, -3.0F);
		body.addChild(right_fin);
		setRotationAngle(right_fin, 0.0F, 0.0F, 0.9163F);
		right_fin.setTextureOffset(19, 21).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 4.0F, 0.0F, true);

		tail = new AdvancedModelBox(this, "tail");
		tail.setRotationPoint(0.0F, 0.0F, 7.0F);
		body.addChild(tail);
		tail.setTextureOffset(0, 21).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 5.0F, 10.0F, 0.0F, false);
		tail.setTextureOffset(0, 0).addBox(0.0F, -4.0F, 2.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);
		tail.setTextureOffset(0, 0).addBox(0.0F, 2.0F, -1.0F, 0.0F, 2.0F, 6.0F, 0.0F, false);

		tail_fin = new AdvancedModelBox(this, "tail_fin");
		tail_fin.setRotationPoint(0.0F, 2.0F, 9.0F);
		tail.addChild(tail_fin);
		tail_fin.setTextureOffset(19, 27).addBox(0.0F, -7.0F, -2.0F, 0.0F, 9.0F, 10.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityCatfish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		//limbSwing = ageInTicks;
		//limbSwingAmount = 1;
		float idleSpeed = 0.2F;
		float idleDegree = 0.25F;
		float swimSpeed = 0.55F;
		float swimDegree = 0.75F;
		AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{body, tail, tail_fin};
		this.chainSwing(tailBoxes, swimSpeed, swimDegree * 0.9F, -3, limbSwing, limbSwingAmount);
		this.flap(left_fin, swimSpeed, swimDegree, false, 4, -0.6F, limbSwing, limbSwingAmount);
		this.flap(right_fin, swimSpeed, swimDegree, true, 4, -0.6F, limbSwing, limbSwingAmount);
		this.walk(dorsal_fin, idleSpeed, idleDegree * 0.2F, true, 2, 0.1F, ageInTicks, 1);
		this.bob(body, idleSpeed, idleDegree, false, ageInTicks, 1);
		this.flap(left_fin, idleSpeed, idleDegree, false, 4, -0.1F, ageInTicks, 1);
		this.flap(right_fin, idleSpeed, idleDegree, true, 4, -0.1F, ageInTicks, 1);
		this.swing(left_barbel, idleSpeed, idleDegree, false, 2, 0.1F, ageInTicks, 1);
		this.swing(right_barbel, idleSpeed, idleDegree, true, 2, 0.1F, ageInTicks, 1);
		this.chainSwing(tailBoxes, idleSpeed, idleDegree * 0.1F, -2.5F, ageInTicks, 1);
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, dorsal_fin, tail, left_fin, right_fin, left_barbel, right_barbel, tail_fin);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}