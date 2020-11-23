package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelRattlesnake extends AdvancedEntityModel<EntityRattlesnake> {
	private final AdvancedModelBox body;
	private final AdvancedModelBox tail1;
	private final AdvancedModelBox tail2;
	private final AdvancedModelBox neck1;
	private final AdvancedModelBox neck2;
	private final AdvancedModelBox head;
	private final AdvancedModelBox tongue;

	public ModelRattlesnake() {
		textureWidth = 64;
		textureHeight = 64;

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, 24.0F, 0.0F);
		body.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 3.0F, 7.0F, 0.0F, false);

		tail1 = new AdvancedModelBox(this);
		tail1.setRotationPoint(0.0F, -1.75F, 2.95F);
		body.addChild(tail1);
		tail1.setTextureOffset(0, 11).addBox(-1.5F, -1.25F, 0.05F, 3.0F, 3.0F, 7.0F, 0.0F, false);

		tail2 = new AdvancedModelBox(this);
		tail2.setRotationPoint(0.0F, 0.45F, 7.05F);
		tail1.addChild(tail2);
		tail2.setTextureOffset(15, 16).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

		neck1 = new AdvancedModelBox(this);
		neck1.setRotationPoint(0.0F, -1.5F, -4.0F);
		body.addChild(neck1);
		neck1.setTextureOffset(18, 6).addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 5.0F, 0.0F, false);

		neck2 = new AdvancedModelBox(this);
		neck2.setRotationPoint(0.0F, 0.0F, -4.9F);
		neck1.addChild(neck2);
		neck2.setTextureOffset(12, 25).addBox(-1.0F, -1.5F, -5.1F, 2.0F, 3.0F, 5.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, 0.0F, -5.0F);
		neck2.addChild(head);
		head.setTextureOffset(0, 22).addBox(-2.0F, -1.0F, -3.8F, 4.0F, 2.0F, 4.0F, 0.0F, false);

		tongue = new AdvancedModelBox(this);
		tongue.setRotationPoint(0.0F, 0.0F, -3.8F);
		head.addChild(tongue);
		tongue.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setRotationAngles(EntityRattlesnake entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();

	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(body);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(body, tail1, tail2, neck1, neck2, head, tongue);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}