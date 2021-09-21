package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;

public class ModelCentipedeBody extends AdvancedEntityModel<EntityCentipedeBody> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox legfrontL1;
	private final AdvancedModelBox legfrontL2;
	private final AdvancedModelBox legfrontR1;
	private final AdvancedModelBox legfrontR2;
	private final AdvancedModelBox legbackL1;
	private final AdvancedModelBox legbackL2;
	private final AdvancedModelBox legbackR1;
	private final AdvancedModelBox legbackR2;

	public ModelCentipedeBody() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setPos(0.0F, -7.6F, 0.0F);
		root.addChild(body);
		body.texOffs(0, 0).addBox(-8.0F, -5.4F, -8.0F, 16.0F, 10.0F, 16.0F, 0.0F, false);

		legfrontL1 = new AdvancedModelBox(this);
		legfrontL1.setPos(7.6F, 3.6F, -4.0F);
		body.addChild(legfrontL1);
		setRotationAngle(legfrontL1, 0.0F, 0.0F, -0.3491F);
		legfrontL1.texOffs(21, 32).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);

		legfrontL2 = new AdvancedModelBox(this);
		legfrontL2.setPos(7.1F, -0.5F, 0.1F);
		legfrontL1.addChild(legfrontL2);
		setRotationAngle(legfrontL2, 0.0F, 0.0F, 1.0908F);
		legfrontL2.texOffs(23, 27).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, false);

		legfrontR1 = new AdvancedModelBox(this);
		legfrontR1.setPos(-7.6F, 3.6F, -4.0F);
		body.addChild(legfrontR1);
		setRotationAngle(legfrontR1, 0.0F, 0.0F, 0.3491F);
		legfrontR1.texOffs(21, 32).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, true);

		legfrontR2 = new AdvancedModelBox(this);
		legfrontR2.setPos(-7.1F, -0.5F, 0.1F);
		legfrontR1.addChild(legfrontR2);
		setRotationAngle(legfrontR2, 0.0F, 0.0F, -1.0908F);
		legfrontR2.texOffs(23, 27).addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, true);

		legbackL1 = new AdvancedModelBox(this);
		legbackL1.setPos(7.6F, 3.6F, 4.0F);
		body.addChild(legbackL1);
		setRotationAngle(legbackL1, 0.0F, 0.0F, -0.3491F);
		legbackL1.texOffs(0, 32).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);

		legbackL2 = new AdvancedModelBox(this);
		legbackL2.setPos(7.1F, -0.5F, 0.1F);
		legbackL1.addChild(legbackL2);
		setRotationAngle(legbackL2, 0.0F, 0.0F, 1.0908F);
		legbackL2.texOffs(0, 27).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, false);

		legbackR1 = new AdvancedModelBox(this);
		legbackR1.setPos(-7.6F, 3.6F, 4.0F);
		body.addChild(legbackR1);
		setRotationAngle(legbackR1, 0.0F, 0.0F, 0.3491F);
		legbackR1.texOffs(0, 32).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, true);

		legbackR2 = new AdvancedModelBox(this);
		legbackR2.setPos(-7.1F, -0.5F, 0.1F);
		legbackR1.addChild(legbackR2);
		setRotationAngle(legbackR2, 0.0F, 0.0F, -1.0908F);
		legbackR2.texOffs(0, 27).addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, true);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityCentipedeBody entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float walkSpeed = 1.5F;
		float walkDegree = 0.85F;
		float offset = (float) ((entityIn.getBodyIndex() + 1 ) * Math.PI * 0.5F);
		this.swing(legfrontL1, walkSpeed, walkDegree, true, offset, 0F, limbSwing, limbSwingAmount);
		this.flap(legfrontL2, walkSpeed, walkDegree * 0.5F, true, offset, 0.1F, limbSwing, limbSwingAmount);
		this.swing(legbackL1, walkSpeed, walkDegree, true, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
		this.flap(legbackL2, walkSpeed, walkDegree * 0.5F, true, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);
		this.swing(legfrontR1, walkSpeed, walkDegree, false, offset, 0F, limbSwing, limbSwingAmount);
		this.flap(legfrontR2, walkSpeed, walkDegree * 0.5F, false, offset, 0.1F, limbSwing, limbSwingAmount);
		this.swing(legbackR1, walkSpeed, walkDegree, false, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
		this.flap(legbackR2, walkSpeed, walkDegree * 0.5F, false, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);
		double walkOffset = (offset ) * Math.PI * 0.5F;
		this.body.y += (float)(Math.sin( (double)(limbSwing * walkSpeed) - walkOffset) * (double)limbSwingAmount * (double)walkDegree - (double)(limbSwingAmount * walkDegree) );
		this.body.y += (float)(Math.sin( (double)(ageInTicks * 0.1) - walkOffset) * (double)0.01 );
	}

	@Override
	public Iterable<ModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, legfrontL1, legfrontL2, legfrontR1, legfrontR2, legbackL1, legbackL2, legbackR1, legbackR2);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.xRot = x;
		AdvancedModelBox.yRot = y;
		AdvancedModelBox.zRot = z;
	}
}