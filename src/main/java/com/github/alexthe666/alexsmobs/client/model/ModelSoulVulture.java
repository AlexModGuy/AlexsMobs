package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelSoulVulture extends AdvancedEntityModel<EntitySoulVulture> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox heart;
	private final AdvancedModelBox leg_left;
	private final AdvancedModelBox foot_left;
	private final AdvancedModelBox leg_right;
	private final AdvancedModelBox foot_right;
	private final AdvancedModelBox wing_left;
	private final AdvancedModelBox wing_right;
	private final AdvancedModelBox neck1;
	private final AdvancedModelBox neck2;
	private final AdvancedModelBox head;
	private final AdvancedModelBox beak;
	private final AdvancedModelBox tail;

	public ModelSoulVulture() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setPos(0.0F, -12.0F, 3.5F);
		root.addChild(body);
		setRotationAngle(body, -0.4363F, 0.0F, 0.0F);
		body.texOffs(0, 0).addBox(-4.0F, -4.0F, -10.5F, 8.0F, 8.0F, 17.0F, 0.0F, false);

		heart = new AdvancedModelBox(this);
		heart.setPos(0.0F, 0.0F, -1.0F);
		body.addChild(heart);
		heart.texOffs(0, 26).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 5.0F, 0.0F, false);

		leg_left = new AdvancedModelBox(this);
		leg_left.setPos(2.5F, 4.0F, 0.7F);
		body.addChild(leg_left);
		setRotationAngle(leg_left, 0.4363F, 0.0F, 0.0F);
		leg_left.texOffs(23, 26).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);

		foot_left = new AdvancedModelBox(this);
		foot_left.setPos(0.0F, 7.0F, -0.4F);
		leg_left.addChild(foot_left);
		foot_left.texOffs(49, 27).addBox(-1.5F, 0.0F, -2.6F, 3.0F, 2.0F, 4.0F, 0.0F, false);

		leg_right = new AdvancedModelBox(this);
		leg_right.setPos(-2.5F, 4.0F, 0.7F);
		body.addChild(leg_right);
		setRotationAngle(leg_right, 0.4363F, 0.0F, 0.0F);
		leg_right.texOffs(23, 26).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 7.0F, 1.0F, 0.0F, true);

		foot_right = new AdvancedModelBox(this);
		foot_right.setPos(0.0F, 7.0F, -0.4F);
		leg_right.addChild(foot_right);
		foot_right.texOffs(49, 27).addBox(-1.5F, 0.0F, -2.6F, 3.0F, 2.0F, 4.0F, 0.0F, true);

		wing_left = new AdvancedModelBox(this);
		wing_left.setPos(4.0F, -3.0F, -10.0F);
		body.addChild(wing_left);
		wing_left.texOffs(5, 31).addBox(0.0F, -1.0F, -0.5F, 1.0F, 9.0F, 24.0F, 0.0F, false);

		wing_right = new AdvancedModelBox(this);
		wing_right.setPos(-4.0F, -3.0F, -10.0F);
		body.addChild(wing_right);
		wing_right.texOffs(5, 31).addBox(-1.0F, -1.0F, -0.5F, 1.0F, 9.0F, 24.0F, 0.0F, true);

		neck1 = new AdvancedModelBox(this);
		neck1.setPos(0.0F, -1.0F, -10.5F);
		body.addChild(neck1);
		setRotationAngle(neck1, 1.1345F, 0.0F, 0.0F);
		neck1.texOffs(0, 0).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

		neck2 = new AdvancedModelBox(this);
		neck2.setPos(0.0F, -1.0F, -3.0F);
		neck1.addChild(neck2);
		neck2.texOffs(34, 0).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, -4.0F, -1.5F);
		neck2.addChild(head);
		setRotationAngle(head, -0.6981F, 0.0F, 0.0F);
		head.texOffs(0, 9).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);

		beak = new AdvancedModelBox(this);
		beak.setPos(0.0F, -1.4F, -2.0F);
		head.addChild(beak);
		beak.texOffs(32, 32).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

		tail = new AdvancedModelBox(this);
		tail.setPos(0.0F, -3.0F, 6.5F);
		body.addChild(tail);
		setRotationAngle(tail, 0.0436F, 0.0F, 0.0F);
		tail.texOffs(38, 41).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 7.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, tail, neck1, neck2, wing_left, wing_right, leg_left, leg_right, foot_left, foot_right, head, heart, beak);
	}

	@Override
	public void setupAnim(EntitySoulVulture entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();
		float idleSpeed = 0.1F;
		float idleDegree = 0.1F;
		float flapSpeed = 0.4F;
		float flapDegree = 0.2F;
		float walkSpeed = 0.7F;
		float walkDegree = 0.4F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
		float tackleProgress = entity.prevTackleProgress + (entity.tackleProgress - entity.prevTackleProgress) * partialTick;
		progressRotationPrev(body, flyProgress, (float) Math.toRadians(15), 0, 0, 5F);
		progressRotationPrev(neck1, flyProgress, (float) Math.toRadians(-25), 0, 0, 5F);
		progressRotationPrev(neck2, flyProgress, (float) Math.toRadians(35), 0, 0, 5F);
		progressRotationPrev(head, flyProgress, (float) Math.toRadians(-25), 0, 0, 5F);
		progressRotationPrev(leg_left, flyProgress, (float) Math.toRadians(55), 0, 0, 5F);
		progressRotationPrev(leg_right, flyProgress, (float) Math.toRadians(55), 0, 0, 5F);
		progressRotationPrev(wing_right, flyProgress,  (float) Math.toRadians(-90), 0,  (float) Math.toRadians(90), 5F);
		progressRotationPrev(wing_left, flyProgress,  (float) Math.toRadians(-90), 0,  (float) Math.toRadians(-90), 5F);
		progressPositionPrev(wing_right, flyProgress, 0F, 2F, 1F, 5f);
		progressPositionPrev(wing_left, flyProgress, 0F, 2F, 1F, 5f);
		progressPositionPrev(body, flyProgress, 0F, 2F, 0, 5f);
		progressRotationPrev(body, tackleProgress, -(float) Math.toRadians(35), 0, 0, 5F);
		progressRotationPrev(neck1, tackleProgress, (float) Math.toRadians(-75), 0, 0, 5F);
		progressRotationPrev(neck2, tackleProgress, (float) Math.toRadians(125), 0, 0, 5F);
		progressPositionPrev(neck2, tackleProgress, 0F, 0.5F, -2F, 5f);
		progressRotationPrev(leg_right, tackleProgress, (float) Math.toRadians(-100), 0, 0, 5F);
		progressRotationPrev(leg_left, tackleProgress, (float) Math.toRadians(-100), 0, 0, 5F);
		if(flyProgress > 0){
			this.walk(leg_right, walkSpeed, walkDegree * 0.4F, false, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(leg_left, walkSpeed, walkDegree * 0.4F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.bob(body, flapSpeed * 0.5F, flapDegree * 8, true, ageInTicks, 1);
			this.walk(neck1, flapSpeed, flapDegree * 0.5F, false, 0F, 0F, ageInTicks, 1);
			this.walk(neck2, flapSpeed, flapDegree * 0.5F, false, 0F, -0.2F, ageInTicks, 1);
			this.walk(head, flapSpeed, flapDegree * 1, true, 0F, -0.1F, ageInTicks, 1);
			this.flap(wing_right, flapSpeed, flapDegree * 5, true, 0F, 0F, ageInTicks, 1);
			this.flap(wing_left, flapSpeed, flapDegree * 5, false, 0F, 0F, ageInTicks, 1);
		}else{
			this.walk(leg_right, walkSpeed, walkDegree * 1.85F, false, 0F, 0F, limbSwing, limbSwingAmount);
			this.walk(leg_left, walkSpeed, walkDegree * 1.85F, true, 0F, 0F, limbSwing, limbSwingAmount);
		}
		this.walk(heart, idleSpeed, idleDegree, false, 2F, 0F, ageInTicks, 1);
		this.bob(heart, idleSpeed, idleDegree * 5F, false, ageInTicks, 1);
		this.walk(neck1, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1);
		this.walk(neck2, idleSpeed, idleDegree * 1.1F, true, 1F, 0F, ageInTicks, 1);
		this.walk(head, idleSpeed, idleDegree * 0.5F, false, -1F, 0.2F, ageInTicks, 1);
		this.walk(tail, idleSpeed, idleDegree, false, 3F, 0F, ageInTicks, 1);
		this.faceTarget(netHeadYaw, headPitch, 2, neck2, head);
		float bloatScale = 1F + entity.getSoulLevel() * 0.1F;
		this.heart.setScale(bloatScale, bloatScale, bloatScale);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public Iterable<ModelPart> parts() {
		return ImmutableList.of(root);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.xRot = x;
		AdvancedModelBox.yRot = y;
		AdvancedModelBox.zRot = z;
	}
}