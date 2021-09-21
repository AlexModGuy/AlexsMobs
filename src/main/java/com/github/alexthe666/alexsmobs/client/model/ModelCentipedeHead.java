package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;

public class ModelCentipedeHead extends AdvancedEntityModel<EntityCentipedeHead> {
	private final AdvancedModelBox bone;
	private final AdvancedModelBox head;
	private final AdvancedModelBox head2;
	private final AdvancedModelBox antenna_left;
	private final AdvancedModelBox antenna_left_r1;
	private final AdvancedModelBox antenna_right;
	private final AdvancedModelBox antenna_right_r1;
	private final AdvancedModelBox fangs;

	public ModelCentipedeHead() {
		texWidth = 128;
		texHeight = 128;

		bone = new AdvancedModelBox(this);
		bone.setPos(0.0F, 24.0F, 0.0F);
		

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, -7.875F, 0.375F);
		bone.addChild(head);
		head.setTextureOffset(0, 11).addBox(-7.0F, -3.125F, -5.375F, 14.0F, 7.0F, 13.0F, 0.0F, false);

		head2 = new AdvancedModelBox(this);
		head2.setPos(0.0F, -2.125F, -6.375F);
		head.addChild(head2);
		head2.setTextureOffset(0, 11).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

		antenna_left = new AdvancedModelBox(this);
		antenna_left.setPos(1.2F, -2.125F, -5.775F);
		head.addChild(antenna_left);
		setRotationAngle(antenna_left, -0.2618F, 0.48F, -0.2618F);
		

		antenna_left_r1 = new AdvancedModelBox(this);
		antenna_left_r1.setPos(0.5F, 0.0F, 0.0F);
		antenna_left.addChild(antenna_left_r1);
		setRotationAngle(antenna_left_r1, 0.1309F, 0.0F, 0.0873F);
		antenna_left_r1.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, -1.0F, 23.0F, 0.0F, 10.0F, 0.0F, false);

		antenna_right = new AdvancedModelBox(this);
		antenna_right.setPos(-1.2F, -2.125F, -5.775F);
		head.addChild(antenna_right);
		setRotationAngle(antenna_right, -0.2618F, -0.48F, 0.2618F);
		

		antenna_right_r1 = new AdvancedModelBox(this);
		antenna_right_r1.setPos(-0.5F, 0.0F, 0.0F);
		antenna_right.addChild(antenna_right_r1);
		setRotationAngle(antenna_right_r1, 0.1309F, 0.0F, -0.0873F);
		antenna_right_r1.setTextureOffset(0, 0).addBox(-22.0F, 0.0F, -1.0F, 23.0F, 0.0F, 10.0F, 0.0F, true);

		fangs = new AdvancedModelBox(this);
		fangs.setPos(0.0F, 1.875F, -6.375F);
		head.addChild(fangs);
		fangs.setTextureOffset(0, 32).addBox(-7.0F, 0.0F, -5.0F, 14.0F, 0.0F, 6.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityCentipedeHead entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float idleSpeed = 0.25F;
		float idleDegree = 0.5F;
		this.swing(antenna_left, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
		this.swing(antenna_right, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
		this.swing(fangs, idleSpeed, idleDegree * 0.1F, false, 0, 0, ageInTicks, 1);
		this.fangs.z = -6.2F;
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(bone);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(bone, head, head2, antenna_left, antenna_right, antenna_left_r1, antenna_right_r1, fangs);
	}

		public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}