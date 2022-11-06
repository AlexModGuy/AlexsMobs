package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityStradpole;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;

public class ModelStradpole extends AdvancedEntityModel<EntityStradpole> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox hair_left;
	private final AdvancedModelBox hair_right;
	private final AdvancedModelBox tail;

	public ModelStradpole() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this, "body");
		body.setPos(0.0F, -4.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		hair_left = new AdvancedModelBox(this, "hair_left");
		hair_left.setPos(4.0F, -4.0F, 0.0F);
		body.addChild(hair_left);
		setRotationAngle(hair_left, 0.0F, 0.0F, 1.1345F);
		hair_left.setTextureOffset(0, 17).addBox(0.0F, 0.0F, -3.0F, 9.0F, 0.0F, 8.0F, 0.0F, false);

		hair_right = new AdvancedModelBox(this, "hair_right");
		hair_right.setPos(-4.0F, -4.0F, 0.0F);
		body.addChild(hair_right);
		setRotationAngle(hair_right, 0.0F, 0.0F, -1.1345F);
		hair_right.setTextureOffset(0, 17).addBox(-9.0F, 0.0F, -3.0F, 9.0F, 0.0F, 8.0F, 0.0F, true);

		tail = new AdvancedModelBox(this, "tail");
		tail.setPos(0.0F, 0.0F, 4.0F);
		body.addChild(tail);
		tail.setTextureOffset(24, 24).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 14.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityStradpole entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float walkSpeed = 1F;
		float walkDegree = 0.4F;
		float idleSpeed = 0.1F;
		float idleDegree = 0.25F;
		this.flap(hair_right, idleSpeed, idleDegree, true, 1, 0F, ageInTicks, 1);
		this.flap(hair_left, idleSpeed, idleDegree, false, 1, 0F, ageInTicks, 1);
		this.flap(body, walkSpeed, walkDegree * 0.2F, true, 0, 0F, limbSwing, limbSwingAmount);
		this.swing(body, walkSpeed, walkDegree * 0.4F, true, 2, 0F, limbSwing, limbSwingAmount);
		this.swing(tail, walkSpeed * 1.4F, walkDegree * 2F, false, 2, 0F, limbSwing, limbSwingAmount);
		this.faceTarget(netHeadYaw, headPitch, 1.2F, body);
		float partialTick = Minecraft.getInstance().getFrameTime();
		float birdPitch = entity.prevSwimPitch + (entity.swimPitch - entity.prevSwimPitch) * partialTick;
		this.body.rotateAngleX += birdPitch * ((float)Math.PI / 180F);

	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, tail, body, hair_left, hair_right);
	}

	public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
		advancedModelBox.rotateAngleX = x;
		advancedModelBox.rotateAngleY = y;
		advancedModelBox.rotateAngleZ = z;
	}
}