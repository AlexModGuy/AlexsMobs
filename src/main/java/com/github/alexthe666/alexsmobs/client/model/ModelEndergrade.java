package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelEndergrade extends AdvancedEntityModel<EntityEndergrade> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox bodymain;
	private final AdvancedModelBox legbackL;
	private final AdvancedModelBox legbackR;
	private final AdvancedModelBox legmidL;
	private final AdvancedModelBox legmidR;
	private final AdvancedModelBox bodyfront;
	private final AdvancedModelBox head;
	private final AdvancedModelBox mouth;
	private final AdvancedModelBox legfrontL;
	private final AdvancedModelBox legfrontR;
	private final AdvancedModelBox tail;

	public ModelEndergrade() {
		textureWidth = 64;
		textureHeight = 64;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		bodymain = new AdvancedModelBox(this);
		bodymain.setRotationPoint(0.0F, -9.0F, -1.0F);
		root.addChild(bodymain);
		bodymain.setTextureOffset(0, 0).addBox(-4.5F, -3.5F, 0.0F, 9.0F, 9.0F, 10.0F, 0.0F, false);

		legbackL = new AdvancedModelBox(this);
		legbackL.setRotationPoint(3.5F, 3.5F, 7.0F);
		bodymain.addChild(legbackL);
		legbackL.setTextureOffset(11, 45).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);

		legbackR = new AdvancedModelBox(this);
		legbackR.setRotationPoint(-3.5F, 3.5F, 7.0F);
		bodymain.addChild(legbackR);
		legbackR.setTextureOffset(11, 45).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, true);

		legmidL = new AdvancedModelBox(this);
		legmidL.setRotationPoint(3.5F, 3.5F, 1.0F);
		bodymain.addChild(legmidL);
		legmidL.setTextureOffset(39, 0).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);

		legmidR = new AdvancedModelBox(this);
		legmidR.setRotationPoint(-3.5F, 3.5F, 1.0F);
		bodymain.addChild(legmidR);
		legmidR.setTextureOffset(39, 0).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, true);

		bodyfront = new AdvancedModelBox(this);
		bodyfront.setRotationPoint(0.0F, 0.5F, 0.0F);
		bodymain.addChild(bodyfront);
		bodyfront.setTextureOffset(25, 29).addBox(-4.0F, -3.5F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -0.5F, -8.0F);
		bodyfront.addChild(head);
		head.setTextureOffset(35, 16).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 6.0F, 4.0F, 0.0F, false);

		mouth = new AdvancedModelBox(this);
		mouth.setRotationPoint(0.0F, 1.5F, -4.5F);
		head.addChild(mouth);
		mouth.setTextureOffset(26, 46).addBox(-1.5F, -1.5F, -2.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);

		legfrontL = new AdvancedModelBox(this);
		legfrontL.setRotationPoint(3.5F, 3.0F, -5.0F);
		bodyfront.addChild(legfrontL);
		legfrontL.setTextureOffset(0, 37).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);

		legfrontR = new AdvancedModelBox(this);
		legfrontR.setRotationPoint(-3.5F, 3.0F, -5.0F);
		bodyfront.addChild(legfrontR);
		legfrontR.setTextureOffset(0, 37).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, true);

		tail = new AdvancedModelBox(this);
		tail.setRotationPoint(0.5F, -1.0F, 9.9F);
		bodymain.addChild(tail);
		setRotationAngle(tail, -0.1745F, 0.0F, 0.0F);
		tail.setTextureOffset(0, 20).addBox(-4.0F, -1.5F, -2.4F, 7.0F, 7.0F, 9.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setRotationAngles(EntityEndergrade entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		AdvancedModelBox[] bodyParts = new AdvancedModelBox[]{bodyfront, bodymain, tail};
		AdvancedModelBox[] legPartsRight = new AdvancedModelBox[]{legfrontR, legmidR, legbackR};
		AdvancedModelBox[] legPartsLeft = new AdvancedModelBox[]{legfrontL, legmidL, legbackL};
		float walkSpeed = 1.7F;
		float walkDegree = 0.7F;
		float partialTick = Minecraft.getInstance().getRenderPartialTicks();
		float birdPitch = entityIn.prevTartigradePitch + (entityIn.tartigradePitch - entityIn.prevTartigradePitch) * partialTick;
		float biteProgress= entityIn.prevBiteProgress + (entityIn.biteProgress - entityIn.prevBiteProgress) * partialTick;
		this.mouth.setScale(1, 1, 1 + biteProgress * 0.4F);
		this.bodymain.rotateAngleX += birdPitch * ((float)Math.PI / 180F);
		this.mouth.rotationPointZ = -3 - biteProgress * 0.2F;
		this.chainWave(bodyParts, walkSpeed, walkDegree * 0.3F, -1, limbSwing, limbSwingAmount);
		this.chainWave(legPartsRight, walkSpeed, walkDegree, -1, limbSwing, limbSwingAmount);
		this.chainWave(legPartsLeft, walkSpeed, walkDegree, -1, limbSwing, limbSwingAmount);
		this.chainFlap(legPartsRight, walkSpeed, walkDegree, 3, limbSwing, limbSwingAmount);
		this.chainFlap(legPartsLeft, walkSpeed, -walkDegree, 3, limbSwing, limbSwingAmount);
		this.swing(tail, walkSpeed, walkDegree * 0.5F, false, 0f, 0f, limbSwing, limbSwingAmount);
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, bodymain, legbackL, legbackR, legfrontL, legfrontR, legmidL, legmidR, bodyfront, head, mouth, tail);
	}

	public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
		advancedModelBox.rotateAngleX = x;
		advancedModelBox.rotateAngleY = y;
		advancedModelBox.rotateAngleZ = z;
	}
}