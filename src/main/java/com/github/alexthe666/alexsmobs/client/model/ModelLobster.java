package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelLobster extends AdvancedEntityModel<EntityLobster> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox antenna_left;
	private final AdvancedModelBox antenna_right;
	private final AdvancedModelBox arm_left;
	private final AdvancedModelBox hand_left;
	private final AdvancedModelBox arm_right;
	private final AdvancedModelBox hand_right;
	private final AdvancedModelBox tail;
	private final AdvancedModelBox tail2;
	private final AdvancedModelBox legs_left;
	private final AdvancedModelBox legs_right;

	public ModelLobster() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setPos(0.0F, -1.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 11).addBox(-2.0F, -1.4F, -7.0F, 4.0F, 2.0F, 7.0F, 0.0F, false);

		antenna_left = new AdvancedModelBox(this);
		antenna_left.setPos(1.4F, -0.5F, -7.0F);
		body.addChild(antenna_left);
		setRotationAngle(antenna_left, -0.3054F, -0.4363F, 0.0F);
		antenna_left.setTextureOffset(18, 18).addBox(0.0F, -0.9F, -5.0F, 0.0F, 1.0F, 5.0F, 0.0F, false);

		antenna_right = new AdvancedModelBox(this);
		antenna_right.setPos(-1.4F, -0.5F, -7.0F);
		body.addChild(antenna_right);
		setRotationAngle(antenna_right, -0.3054F, 0.4363F, 0.0F);
		antenna_right.setTextureOffset(18, 20).addBox(0.0F, -0.9F, -5.0F, 0.0F, 1.0F, 5.0F, 0.0F, true);

		arm_left = new AdvancedModelBox(this);
		arm_left.setPos(1.7F, -0.4F, -5.0F);
		body.addChild(arm_left);
		setRotationAngle(arm_left, 0.0F, 0.6981F, 0.3491F);
		arm_left.setTextureOffset(15, 5).addBox(0.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, 0.0F, false);

		hand_left = new AdvancedModelBox(this);
		hand_left.setPos(4.0F, 0.4F, 0.4F);
		arm_left.addChild(hand_left);
		setRotationAngle(hand_left, 0.0F, 0.6981F, 0.0F);
		hand_left.setTextureOffset(0, 21).addBox(0.0F, -1.0F, -1.9F, 3.0F, 1.0F, 2.0F, 0.0F, false);

		arm_right = new AdvancedModelBox(this);
		arm_right.setPos(-1.7F, -0.4F, -5.0F);
		body.addChild(arm_right);
		setRotationAngle(arm_right, 0.0F, -0.6981F, -0.3491F);
		arm_right.setTextureOffset(15, 6).addBox(-4.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, 0.0F, true);

		hand_right = new AdvancedModelBox(this);
		hand_right.setPos(-4.0F, 0.4F, 0.4F);
		arm_right.addChild(hand_right);
		setRotationAngle(hand_right, 0.0F, -0.6981F, 0.0F);
		hand_right.setTextureOffset(0, 25).addBox(-3.0F, -1.0F, -1.9F, 3.0F, 1.0F, 2.0F, 0.0F, true);

		tail = new AdvancedModelBox(this);
		tail.setPos(0.0F, -0.4F, 0.0F);
		body.addChild(tail);
		tail.setTextureOffset(0, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 8.0F, 0.0F, false);

		tail2 = new AdvancedModelBox(this);
		tail2.setPos(0.0F, 0.0F, 6.0F);
		tail.addChild(tail2);
		tail2.setTextureOffset(15, 0).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 4.0F, 0.0F, false);

		legs_left = new AdvancedModelBox(this);
		legs_left.setPos(2.0F, 0.1F, -1.45F);
		body.addChild(legs_left);
		setRotationAngle(legs_left, 0.0F, 0.0F, 0.3054F);
		legs_left.setTextureOffset(16, 11).addBox(0.0F, 0.0F, -3.55F, 3.0F, 0.0F, 5.0F, 0.0F, false);

		legs_right = new AdvancedModelBox(this);
		legs_right.setPos(-2.0F, 0.1F, -1.45F);
		body.addChild(legs_right);
		setRotationAngle(legs_right, 0.0F, 0.0F, -0.3054F);
		legs_right.setTextureOffset(25, 11).addBox(-3.0F, 0.0F, -3.55F, 3.0F, 0.0F, 5.0F, 0.0F, true);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityLobster entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float idleSpeed = 0.1f;
		float idleDegree = 0.3f;
		float walkSpeed = 3f;
		float walkDegree = 0.6F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float attackProgress = entityIn.prevAttackProgress + (entityIn.attackProgress - entityIn.prevAttackProgress) * partialTick;
		progressRotationPrev(arm_left, attackProgress, 0, (float)Math.toRadians(45), 0, 5F);
		progressRotationPrev(arm_right, attackProgress, 0, (float)Math.toRadians(-45), 0, 5F);
		progressRotationPrev(hand_left, attackProgress, 0, (float)Math.toRadians(-30), 0, 5F);
		progressRotationPrev(hand_right, attackProgress, 0, (float)Math.toRadians(30), 0, 5F);
		this.walk(antenna_left, idleSpeed * 1.5F, idleDegree, false, 0F, 0F, ageInTicks, 1);
		this.walk(antenna_right, idleSpeed * 1.5F, idleDegree, true, 0F, 0F, ageInTicks, 1);
		this.walk(tail, idleSpeed, idleDegree * 0.2F, false, 1F, -0.1F, ageInTicks, 1);
		this.walk(tail2, idleSpeed, idleDegree * 0.15F, false, 1F, 0.1F, ageInTicks, 1);
		this.walk(legs_left, walkSpeed, walkDegree * 0.8F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.swing(legs_left, walkSpeed, walkDegree * 1, false, 1F, 0.1F, limbSwing, limbSwingAmount);
		this.walk(legs_right, walkSpeed, walkDegree * 0.8F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.swing(legs_right, walkSpeed, walkDegree * 1, true, 1F, 0.1F, limbSwing, limbSwingAmount);
		this.bob(body, walkSpeed * 0.5F, walkDegree * 4F, true, limbSwing, limbSwingAmount);
		this.swing(arm_left, walkSpeed, walkDegree * 1, true, 2, 0, limbSwing, limbSwingAmount);
		this.swing(arm_right, walkSpeed, walkDegree * 1, true, 2, 0, limbSwing, limbSwingAmount);

	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, antenna_left, antenna_right, arm_left, arm_right, hand_left, hand_right, tail, tail2, legs_left, legs_right);
	}


	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}