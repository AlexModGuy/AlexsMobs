package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelAlligatorSnappingTurtle extends AdvancedEntityModel<EntityAlligatorSnappingTurtle> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox arm_left;
	private final AdvancedModelBox arm_right;
	private final AdvancedModelBox leg_left;
	private final AdvancedModelBox leg_right;
	private final AdvancedModelBox shell;
	private final AdvancedModelBox spikes_left;
	private final AdvancedModelBox spikes_right;
	private final AdvancedModelBox neck;
	private final AdvancedModelBox head;
	private final AdvancedModelBox jaw;
	private final AdvancedModelBox tail;

	public ModelAlligatorSnappingTurtle() {
		textureWidth = 128;
		textureHeight = 128;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 22).addBox(-7.0F, -6.0F, -8.0F, 14.0F, 6.0F, 16.0F, 0.0F, false);

		arm_left = new AdvancedModelBox(this);
		arm_left.setRotationPoint(6.1F, -1.7F, -6.4F);
		body.addChild(arm_left);
		setRotationAngle(arm_left, 0.0F, 0.5672F, 0.0436F);
		arm_left.setTextureOffset(47, 45).addBox(-0.5F, -1.5F, -2.0F, 9.0F, 3.0F, 4.0F, 0.0F, false);

		arm_right = new AdvancedModelBox(this);
		arm_right.setRotationPoint(-6.1F, -1.7F, -6.4F);
		body.addChild(arm_right);
		setRotationAngle(arm_right, 0.0F, -0.5672F, -0.0436F);
		arm_right.setTextureOffset(47, 45).addBox(-8.5F, -1.5F, -2.0F, 9.0F, 3.0F, 4.0F, 0.0F, true);

		leg_left = new AdvancedModelBox(this);
		leg_left.setRotationPoint(6.1F, -1.7F, 6.6F);
		body.addChild(leg_left);
		setRotationAngle(leg_left, 0.0F, -0.6109F, 0.0436F);
		leg_left.setTextureOffset(45, 22).addBox(-0.5F, -1.5F, -3.0F, 8.0F, 3.0F, 5.0F, 0.0F, false);

		leg_right = new AdvancedModelBox(this);
		leg_right.setRotationPoint(-6.1F, -1.7F, 6.6F);
		body.addChild(leg_right);
		setRotationAngle(leg_right, 0.0F, 0.6109F, -0.0436F);
		leg_right.setTextureOffset(45, 22).addBox(-7.5F, -1.5F, -3.0F, 8.0F, 3.0F, 5.0F, 0.0F, true);

		shell = new AdvancedModelBox(this);
		shell.setRotationPoint(0.0F, -6.0F, 0.0F);
		body.addChild(shell);
		shell.setTextureOffset(0, 0).addBox(-8.0F, -1.0F, -9.0F, 16.0F, 3.0F, 18.0F, 0.0F, false);

		spikes_left = new AdvancedModelBox(this);
		spikes_left.setRotationPoint(4.0F, -2.0F, 0.0F);
		shell.addChild(spikes_left);
		spikes_left.setTextureOffset(0, 45).addBox(-4.0F, -1.0F, -8.0F, 7.0F, 2.0F, 16.0F, 0.0F, false);

		spikes_right = new AdvancedModelBox(this);
		spikes_right.setRotationPoint(-4.0F, -2.0F, 0.0F);
		shell.addChild(spikes_right);
		spikes_right.setTextureOffset(0, 45).addBox(-3.0F, -1.0F, -8.0F, 7.0F, 2.0F, 16.0F, 0.0F, true);

		neck = new AdvancedModelBox(this);
		neck.setRotationPoint(0.0F, -2.0F, -8.0F);
		body.addChild(neck);
		neck.setTextureOffset(51, 9).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 5.0F, 3.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -0.75F, -3.05F);
		neck.addChild(head);
		head.setTextureOffset(51, 0).addBox(-3.0F, -2.25F, -4.95F, 6.0F, 3.0F, 5.0F, 0.0F, false);

		jaw = new AdvancedModelBox(this);
		jaw.setRotationPoint(0.0F, 1.15F, 0.15F);
		head.addChild(jaw);
		setRotationAngle(jaw, -0.2182F, 0.0F, 0.0F);
		jaw.setTextureOffset(51, 53).addBox(-2.5F, -0.5F, -5.0F, 5.0F, 2.0F, 5.0F, 0.0F, false);

		tail = new AdvancedModelBox(this);
		tail.setRotationPoint(0.0F, -2.5F, 8.0F);
		body.addChild(tail);
		tail.setTextureOffset(31, 45).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 9.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setRotationAngles(EntityAlligatorSnappingTurtle entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float idleSpeed = 0.05F;
		float idleDegree = 0.25F;
		float walkSpeed = entityIn.isInWater() ? 0.5F : 1F;
		float walkDegree = 0.75F;
		float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
		float openProgress = entityIn.prevOpenMouthProgress + (entityIn.openMouthProgress - entityIn.prevOpenMouthProgress) * partialTicks;
		float snapProgress = entityIn.prevAttackProgress + (entityIn.attackProgress - entityIn.prevAttackProgress) * partialTicks;
		progressRotationPrev(neck, openProgress, (float) Math.toRadians(-10), 0, 0, 5F);
		progressRotationPrev(head, openProgress, (float) Math.toRadians(-35), 0, 0, 5F);
		progressRotationPrev(jaw, openProgress, (float) Math.toRadians(65), 0, 0, 5F);
		progressPositionPrev(jaw, openProgress, 0, -1, 0, 5F);
		progressPositionPrev(neck, snapProgress, 0, 0, 0, 5F);
		neck.setScale((1 - snapProgress * 0.05F), (1- snapProgress * 0.05F), (1 + snapProgress * 0.5F));
		head.rotationPointZ -= 1.45F * snapProgress;
		progressRotationPrev(head, snapProgress, (float) Math.toRadians(10), 0, 0, 5F);
		progressRotationPrev(jaw, snapProgress, (float) Math.toRadians(-10), 0, 0, 5F);
		this.swing(tail, idleSpeed, idleDegree * 1.15F, false, 3, 0F, ageInTicks, 1);
		this.swing(leg_right, walkSpeed, walkDegree, true, 1, 0F, limbSwing, limbSwingAmount);
		this.swing(leg_left, walkSpeed, walkDegree, false, 1, 0F, limbSwing, limbSwingAmount);
		this.swing(arm_right, walkSpeed, walkDegree, false, 0, 0.1F, limbSwing, limbSwingAmount);
		this.swing(arm_left, walkSpeed, walkDegree, true, 0, 0.1F, limbSwing, limbSwingAmount);
		this.swing(tail, walkSpeed * 1.35F, walkDegree * 1.15F, false, 3, 0F, limbSwing, limbSwingAmount);
		this.swing(neck, walkSpeed * 0.75F, walkDegree * 0.15F, false, -2, 0F, limbSwing, limbSwingAmount);
		this.swing(head, walkSpeed * 0.75F, walkDegree * 0.15F, false, -2, 0F, limbSwing, limbSwingAmount);

	}

	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.isChild) {
			this.head.setScale(1.5F, 1.5F, 1.5F);
			matrixStackIn.push();
			matrixStackIn.scale(0.25F, 0.25F, 0.25F);
			matrixStackIn.translate(0.0D, 4.5, 0.125D);
			getParts().forEach((p_228292_8_) -> {
				p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.pop();
		} else {
			this.head.setScale(1F, 1F, 1F);
			matrixStackIn.push();
			getParts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.pop();
		}

	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, shell, spikes_left, spikes_right, neck, jaw, head, leg_left, leg_right, arm_left, arm_right, tail);
	}


	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}