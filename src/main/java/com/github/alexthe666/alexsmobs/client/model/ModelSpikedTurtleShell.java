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
		head.texOffs(34, 15).addBox(0.0F, -33F, -4.5F, 4.0F, 1.0F, 9.0F, modelSize - 0.1F, false);
		head.texOffs(34, 15).addBox(-4.0F, -33F, -4.5F, 4.0F, 1.0F, 9.0F, modelSize - 0.1F, true);
		this.head.addChild(head);
	}

	public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entityIn instanceof ArmorStand) {
			ArmorStand entityarmorstand = (ArmorStand) entityIn;
			this.head.xRot = 0.017453292F * entityarmorstand.getHeadPose().getX();
			this.head.yRot = 0.017453292F * entityarmorstand.getHeadPose().getY();
			this.head.zRot = 0.017453292F * entityarmorstand.getHeadPose().getZ();
			this.head.setPos(0.0F, 1.0F, 0.0F);
			this.body.xRot = 0.017453292F * entityarmorstand.getBodyPose().getX();
			this.body.yRot = 0.017453292F * entityarmorstand.getBodyPose().getY();
			this.body.zRot = 0.017453292F * entityarmorstand.getBodyPose().getZ();
			this.leftArm.xRot = 0.017453292F * entityarmorstand.getLeftArmPose().getX();
			this.leftArm.yRot = 0.017453292F * entityarmorstand.getLeftArmPose().getY();
			this.leftArm.zRot = 0.017453292F * entityarmorstand.getLeftArmPose().getZ();
			this.rightArm.xRot = 0.017453292F * entityarmorstand.getRightArmPose().getX();
			this.rightArm.yRot = 0.017453292F * entityarmorstand.getRightArmPose().getY();
			this.rightArm.zRot = 0.017453292F * entityarmorstand.getRightArmPose().getZ();
			this.leftLeg.xRot = 0.017453292F * entityarmorstand.getLeftLegPose().getX();
			this.leftLeg.yRot = 0.017453292F * entityarmorstand.getLeftLegPose().getY();
			this.leftLeg.zRot = 0.017453292F * entityarmorstand.getLeftLegPose().getZ();
			this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
			this.rightLeg.xRot = 0.017453292F * entityarmorstand.getRightLegPose().getX();
			this.rightLeg.yRot = 0.017453292F * entityarmorstand.getRightLegPose().getY();
			this.rightLeg.zRot = 0.017453292F * entityarmorstand.getRightLegPose().getZ();
			this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
			this.hat.copyFrom(this.head);
		} else {
			super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}