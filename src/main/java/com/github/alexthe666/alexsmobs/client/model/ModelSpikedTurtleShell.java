package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ModelSpikedTurtleShell extends HumanoidModel{
	private final ModelPart head;

	public ModelSpikedTurtleShell(float modelSize) {
		super(modelSize, 0, 64, 32);
		texWidth = 64;
		texHeight = 32;

		head = new ModelPart(this);
		head.setPos(0.0F, 24.0F, 0.0F);
		head.setTextureOffset(34, 15).addBox(0.0F, -33F, -4.5F, 4.0F, 1.0F, 9.0F, modelSize - 0.1F, false);
		head.setTextureOffset(34, 15).addBox(-4.0F, -33F, -4.5F, 4.0F, 1.0F, 9.0F, modelSize - 0.1F, true);
		this.head.addChild(head);
	}

	public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entityIn instanceof ArmorStand) {
			ArmorStand entityarmorstand = (ArmorStand) entityIn;
			this.head.rotateAngleX = 0.017453292F * entityarmorstand.getHeadPose().getX();
			this.head.rotateAngleY = 0.017453292F * entityarmorstand.getHeadPose().getY();
			this.head.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadPose().getZ();
			this.head.setPos(0.0F, 1.0F, 0.0F);
			this.body.rotateAngleX = 0.017453292F * entityarmorstand.getBodyPose().getX();
			this.body.rotateAngleY = 0.017453292F * entityarmorstand.getBodyPose().getY();
			this.body.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyPose().getZ();
			this.leftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmPose().getX();
			this.leftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmPose().getY();
			this.leftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmPose().getZ();
			this.rightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmPose().getX();
			this.rightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmPose().getY();
			this.rightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmPose().getZ();
			this.leftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegPose().getX();
			this.leftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegPose().getY();
			this.leftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegPose().getZ();
			this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
			this.rightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegPose().getX();
			this.rightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegPose().getY();
			this.rightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegPose().getZ();
			this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
			this.hat.copyFrom(this.head);
		} else {
			super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}