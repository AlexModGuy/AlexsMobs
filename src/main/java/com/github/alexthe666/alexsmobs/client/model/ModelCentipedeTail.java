package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCentipedeTail;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelCentipedeTail extends AdvancedEntityModel<EntityCentipedeTail> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox end_leg_L1;
	private final AdvancedModelBox end_leg_L2;
	private final AdvancedModelBox end_leg_L3;
	private final AdvancedModelBox end_leg_R1;
	private final AdvancedModelBox end_leg_R2;
	private final AdvancedModelBox end_leg_R3;
	private final AdvancedModelBox legfrontL1;
	private final AdvancedModelBox legfrontL2;
	private final AdvancedModelBox legfrontR1;
	private final AdvancedModelBox legfrontR2;
	private final AdvancedModelBox legbackL1;
	private final AdvancedModelBox legbackL2;
	private final AdvancedModelBox legbackR1;
	private final AdvancedModelBox legbackR2;

	public ModelCentipedeTail() {
		textureWidth = 64;
		textureHeight = 64;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -7.6F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-7.0F, -4.2F, -8.0F, 14.0F, 9.0F, 16.0F, 0.0F, false);

		end_leg_L1 = new AdvancedModelBox(this);
		end_leg_L1.setRotationPoint(2.5F, -0.1F, 8.0F);
		body.addChild(end_leg_L1);
		setRotationAngle(end_leg_L1, 0.3054F, 0.3927F, 0.0F);
		end_leg_L1.setTextureOffset(0, 26).addBox(-1.5F, -1.1F, -1.0F, 3.0F, 3.0F, 12.0F, 0.0F, false);

		end_leg_L2 = new AdvancedModelBox(this);
		end_leg_L2.setRotationPoint(0.0F, 0.2F, 11.0F);
		end_leg_L1.addChild(end_leg_L2);
		setRotationAngle(end_leg_L2, -0.6981F, 0.0F, 0.0F);
		end_leg_L2.setTextureOffset(31, 31).addBox(-1.0F, -0.8F, -0.3F, 2.0F, 2.0F, 10.0F, 0.0F, false);

		end_leg_L3 = new AdvancedModelBox(this);
		end_leg_L3.setRotationPoint(0.0F, 0.2F, 9.5F);
		end_leg_L2.addChild(end_leg_L3);
		setRotationAngle(end_leg_L3, -0.4363F, 0.0F, 0.0F);
		end_leg_L3.setTextureOffset(19, 26).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 9.0F, 0.0F, false);

		end_leg_R1 = new AdvancedModelBox(this);
		end_leg_R1.setRotationPoint(-2.5F, -0.1F, 8.0F);
		body.addChild(end_leg_R1);
		setRotationAngle(end_leg_R1, 0.3054F, -0.3927F, 0.0F);
		end_leg_R1.setTextureOffset(0, 26).addBox(-1.5F, -1.1F, -1.0F, 3.0F, 3.0F, 12.0F, 0.0F, true);

		end_leg_R2 = new AdvancedModelBox(this);
		end_leg_R2.setRotationPoint(0.0F, 0.2F, 11.0F);
		end_leg_R1.addChild(end_leg_R2);
		setRotationAngle(end_leg_R2, -0.6981F, 0.0F, 0.0F);
		end_leg_R2.setTextureOffset(31, 31).addBox(-1.0F, -0.8F, -0.3F, 2.0F, 2.0F, 10.0F, 0.0F, true);

		end_leg_R3 = new AdvancedModelBox(this);
		end_leg_R3.setRotationPoint(0.0F, 0.2F, 9.5F);
		end_leg_R2.addChild(end_leg_R3);
		setRotationAngle(end_leg_R3, -0.4363F, 0.0F, 0.0F);
		end_leg_R3.setTextureOffset(19, 26).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 9.0F, 0.0F, true);

		legfrontL1 = new AdvancedModelBox(this);
		legfrontL1.setRotationPoint(6.6F, 3.6F, -4.0F);
		body.addChild(legfrontL1);
		setRotationAngle(legfrontL1, 0.0F, -0.2618F, -0.3491F);
		legfrontL1.setTextureOffset(44, 44).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);

		legfrontL2 = new AdvancedModelBox(this);
		legfrontL2.setRotationPoint(7.1F, -0.5F, 0.1F);
		legfrontL1.addChild(legfrontL2);
		setRotationAngle(legfrontL2, 0.0F, 0.0F, 1.0908F);
		legfrontL2.setTextureOffset(0, 42).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, false);

		legfrontR1 = new AdvancedModelBox(this);
		legfrontR1.setRotationPoint(-6.6F, 3.6F, -4.0F);
		body.addChild(legfrontR1);
		setRotationAngle(legfrontR1, 0.0F, 0.2618F, 0.3491F);
		legfrontR1.setTextureOffset(44, 44).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, true);

		legfrontR2 = new AdvancedModelBox(this);
		legfrontR2.setRotationPoint(-7.1F, -0.5F, 0.1F);
		legfrontR1.addChild(legfrontR2);
		setRotationAngle(legfrontR2, 0.0F, 0.0F, -1.0908F);
		legfrontR2.setTextureOffset(0, 42).addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, true);

		legbackL1 = new AdvancedModelBox(this);
		legbackL1.setRotationPoint(6.6F, 3.6F, 4.0F);
		body.addChild(legbackL1);
		setRotationAngle(legbackL1, 0.0F, -0.5672F, -0.3491F);
		legbackL1.setTextureOffset(23, 44).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);

		legbackL2 = new AdvancedModelBox(this);
		legbackL2.setRotationPoint(7.1F, -0.5F, 0.1F);
		legbackL1.addChild(legbackL2);
		setRotationAngle(legbackL2, 0.0F, 0.0F, 1.0036F);
		legbackL2.setTextureOffset(31, 26).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, false);

		legbackR1 = new AdvancedModelBox(this);
		legbackR1.setRotationPoint(-6.6F, 3.6F, 4.0F);
		body.addChild(legbackR1);
		setRotationAngle(legbackR1, 0.0F, 0.5672F, 0.3491F);
		legbackR1.setTextureOffset(23, 44).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, true);

		legbackR2 = new AdvancedModelBox(this);
		legbackR2.setRotationPoint(-7.1F, -0.5F, 0.1F);
		legbackR1.addChild(legbackR2);
		setRotationAngle(legbackR2, 0.0F, 0.0F, -1.0036F);
		legbackR2.setTextureOffset(31, 26).addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, 0.0F, true);
		this.updateDefaultPose();
	}

	@Override
	public void setRotationAngles(EntityCentipedeTail entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float idleSpeed = 0.25F;
		float idleDegree = 0.35F;
		float walkSpeed = 1.5F;
		float walkDegree = 0.85F;
		float offset = (float) ((entityIn.getBodyIndex() + 1 ) * Math.PI * 0.5F);
		this.swing(end_leg_L1, walkSpeed, walkDegree * 0.2F, true, offset + 1F, 0F, limbSwing, limbSwingAmount);
		this.swing(end_leg_R1, walkSpeed, walkDegree * 0.2F, false, offset + 1F, 0F, limbSwing, limbSwingAmount);
		this.walk(end_leg_L1, idleSpeed, idleDegree, true, offset + 1.5F, -0.5F, ageInTicks, 1);
		this.walk(end_leg_R1, idleSpeed, idleDegree, false, offset + 1.5F, 0.5F, ageInTicks, 1);

		this.swing(legfrontL1, walkSpeed, walkDegree, true, offset, 0F, limbSwing, limbSwingAmount);
		this.flap(legfrontL2, walkSpeed, walkDegree * 0.5F, true, offset, 0.1F, limbSwing, limbSwingAmount);
		this.swing(legbackL1, walkSpeed, walkDegree, true, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
		this.flap(legbackL2, walkSpeed, walkDegree * 0.5F, true, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);
		this.swing(legfrontR1, walkSpeed, walkDegree, false, offset, 0F, limbSwing, limbSwingAmount);
		this.flap(legfrontR2, walkSpeed, walkDegree * 0.5F, false, offset, 0.1F, limbSwing, limbSwingAmount);
		this.swing(legbackR1, walkSpeed, walkDegree, false, offset + 0.5F, 0F, limbSwing, limbSwingAmount);
		this.flap(legbackR2, walkSpeed, walkDegree * 0.5F, false, offset + 0.5F, 0.1F, limbSwing, limbSwingAmount);

	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, end_leg_L1, end_leg_L2, end_leg_L3, end_leg_R1, end_leg_R2, end_leg_R3, legfrontL1, legfrontL2, legfrontR1, legfrontR2, legbackL1, legbackL2, legbackR1, legbackR2);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}