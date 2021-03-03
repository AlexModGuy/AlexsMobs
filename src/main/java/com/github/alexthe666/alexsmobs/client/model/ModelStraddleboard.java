package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.5

import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelStraddleboard extends AdvancedEntityModel<EntityStraddleboard> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox hair_left;
	private final AdvancedModelBox hair_right;
	private final AdvancedModelBox spikes;
	private final AdvancedModelBox front;
	private final AdvancedModelBox spikes_front;

	public ModelStraddleboard() {
		textureWidth = 128;
		textureHeight = 128;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-6.0F, -2.0F, -10.0F, 12.0F, 2.0F, 26.0F, 0.0F, false);

		hair_left = new AdvancedModelBox(this);
		hair_left.setRotationPoint(6.0F, -2.0F, 0.0F);
		body.addChild(hair_left);
		setRotationAngle(hair_left, 0.0F, 0.0F, 0.8727F);
		hair_left.setTextureOffset(0, 29).addBox(0.0F, -10.0F, -2.0F, 0.0F, 10.0F, 24.0F, 0.0F, false);

		hair_right = new AdvancedModelBox(this);
		hair_right.setRotationPoint(-6.0F, -2.0F, 0.0F);
		body.addChild(hair_right);
		setRotationAngle(hair_right, 0.0F, 0.0F, -0.8727F);
		hair_right.setTextureOffset(0, 29).addBox(0.0F, -10.0F, -2.0F, 0.0F, 10.0F, 24.0F, 0.0F, true);

		spikes = new AdvancedModelBox(this);
		spikes.setRotationPoint(0.0F, -1.5F, 13.5F);
		body.addChild(spikes);
		spikes.setTextureOffset(25, 29).addBox(-4.0F, -5.5F, -3.5F, 8.0F, 11.0F, 7.0F, 0.0F, false);

		front = new AdvancedModelBox(this);
		front.setRotationPoint(0.0F, 0.0F, -10.0F);
		body.addChild(front);
		setRotationAngle(front, -0.1309F, 0.0F, 0.0F);
		front.setTextureOffset(48, 40).addBox(-5.0F, -2.0F, -8.0F, 10.0F, 2.0F, 8.0F, 0.0F, false);

		spikes_front = new AdvancedModelBox(this);
		spikes_front.setRotationPoint(0.0F, -4.0F, -3.5F);
		front.addChild(spikes_front);
		spikes_front.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -2.5F, 4.0F, 4.0F, 5.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, hair_left, hair_right, spikes, spikes_front, front);
	}

	@Override
	public void setRotationAngles(EntityStraddleboard entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();
	}

	public void animateBoard(EntityStraddleboard board, float ageInTicks){
		this.resetToDefaultPose();
		this.walk(hair_right, 0.1F, 0.01F, false, 0, -0.1F, ageInTicks, 1);
		this.walk(hair_left, 0.1F, 0.01F, false, 0, -0.1F, ageInTicks, 1);
		this.flap(hair_right, 0.1F, 0.1F, false, 0, -0.1F, ageInTicks, 1);
		this.flap(hair_left, 0.1F, 0.1F, true, 0, -0.1F, ageInTicks, 1);
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
		advancedModelBox.rotateAngleX = x;
		advancedModelBox.rotateAngleY = y;
		advancedModelBox.rotateAngleZ = z;
	}
}