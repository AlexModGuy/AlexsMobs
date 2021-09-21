package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityHammerheadShark;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;

public class ModelHammerheadShark extends AdvancedEntityModel<EntityHammerheadShark> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox main_body;
	private final AdvancedModelBox head;
	private final AdvancedModelBox head_hammer;
	private final AdvancedModelBox finL;
	private final AdvancedModelBox finR;
	private final AdvancedModelBox topfin;
	private final AdvancedModelBox tail1;
	private final AdvancedModelBox tail_finL;
	private final AdvancedModelBox tail_finR;
	private final AdvancedModelBox topfintail;
	private final AdvancedModelBox tail2;
	private final AdvancedModelBox tail3;
	private final AdvancedModelBox tailbottomend;
	private final AdvancedModelBox tailtopend;

	public ModelHammerheadShark() {
		texWidth = 128;
		texHeight = 128;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		main_body = new AdvancedModelBox(this);
		main_body.setPos(0.0F, -6.0F, 0.0F);
		root.addChild(main_body);
		main_body.texOffs(0, 0).addBox(-5.0F, -4.0F, -14.0F, 10.0F, 10.0F, 25.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, -1.0F, -14.5F);
		main_body.addChild(head);
		head.texOffs(40, 55).addBox(-4.0F, -1.0F, -6.5F, 8.0F, 7.0F, 7.0F, 0.0F, false);

		head_hammer = new AdvancedModelBox(this);
		head_hammer.setPos(0.0F, 1.5F, -7.0F);
		head.addChild(head_hammer);
		head_hammer.texOffs(32, 36).addBox(-11.0F, -1.5F, -3.5F, 22.0F, 3.0F, 7.0F, 0.0F, false);

		finL = new AdvancedModelBox(this);
		finL.setPos(6.0F, 6.0F, -6.5F);
		main_body.addChild(finL);
		setRotationAngle(finL, 0.0F, -0.2182F, 0.2618F);
		finL.texOffs(47, 47).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 1.0F, 6.0F, 0.0F, false);

		finR = new AdvancedModelBox(this);
		finR.setPos(-6.0F, 6.0F, -6.5F);
		main_body.addChild(finR);
		setRotationAngle(finR, 0.0F, 0.2182F, -0.2618F);
		finR.texOffs(47, 47).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 1.0F, 6.0F, 0.0F, true);

		topfin = new AdvancedModelBox(this);
		topfin.setPos(0.0F, -4.0F, -3.5F);
		main_body.addChild(topfin);
		setRotationAngle(topfin, -0.2182F, 0.0F, 0.0F);
		topfin.texOffs(0, 0).addBox(-1.0F, -13.0F, -2.0F, 2.0F, 14.0F, 7.0F, 0.0F, false);

		tail1 = new AdvancedModelBox(this);
		tail1.setPos(0.0F, -0.3F, 11.75F);
		main_body.addChild(tail1);
		tail1.texOffs(0, 36).addBox(-4.0F, -2.5F, -0.75F, 8.0F, 8.0F, 15.0F, 0.0F, false);

		tail_finL = new AdvancedModelBox(this);
		tail_finL.setPos(3.0F, 5.3F, 5.75F);
		tail1.addChild(tail_finL);
		setRotationAngle(tail_finL, 0.0F, -0.48F, 1.0036F);
		tail_finL.texOffs(64, 55).addBox(0.0F, -1.0F, 0.0F, 8.0F, 1.0F, 4.0F, 0.0F, false);

		tail_finR = new AdvancedModelBox(this);
		tail_finR.setPos(-3.0F, 5.3F, 5.75F);
		tail1.addChild(tail_finR);
		setRotationAngle(tail_finR, 0.0F, 0.48F, -1.0036F);
		tail_finR.texOffs(64, 55).addBox(-8.0F, -1.0F, 0.0F, 8.0F, 1.0F, 4.0F, 0.0F, true);

		topfintail = new AdvancedModelBox(this);
		topfintail.setPos(0.0F, -2.7F, 9.75F);
		tail1.addChild(topfintail);
		setRotationAngle(topfintail, -0.2182F, 0.0F, 0.0F);
		topfintail.texOffs(0, 36).addBox(-0.5F, -4.0237F, -1.7836F, 1.0F, 5.0F, 4.0F, 0.0F, false);

		tail2 = new AdvancedModelBox(this);
		tail2.setPos(0.0F, 0.3F, 14.75F);
		tail1.addChild(tail2);
		tail2.texOffs(46, 0).addBox(-2.5F, -2.0F, -0.5F, 5.0F, 6.0F, 14.0F, 0.0F, false);

		tail3 = new AdvancedModelBox(this);
		tail3.setPos(0.0F, 0.0F, 12.0F);
		tail2.addChild(tail3);
		

		tailbottomend = new AdvancedModelBox(this);
		tailbottomend.setPos(-0.5F, 1.5F, 0.0F);
		tail3.addChild(tailbottomend);
		setRotationAngle(tailbottomend, -2.7489F, 0.0F, 0.0F);
		tailbottomend.texOffs(17, 60).addBox(0.0F, -11.5F, -2.5F, 1.0F, 14.0F, 6.0F, 0.0F, false);

		tailtopend = new AdvancedModelBox(this);
		tailtopend.setPos(0.0F, -0.5F, 0.0F);
		tail3.addChild(tailtopend);
		setRotationAngle(tailtopend, -0.829F, 0.0F, 0.0F);
		tailtopend.texOffs(0, 60).addBox(-1.0F, -16.5F, -1.5F, 2.0F, 19.0F, 6.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityHammerheadShark entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{main_body, tail1, tail2, tail3};
		float swimSpeed = 0.4F;
		float swimDegree = 0.5F;
		this.chainSwing(tailBoxes, swimSpeed, swimDegree * 0.9F, -2, limbSwing, limbSwingAmount);
		this.bob(main_body, swimSpeed * 0.5F, swimDegree * 5F, false, limbSwing, limbSwingAmount);
		this.walk(topfin, swimSpeed, swimDegree * 0.1F, true, 1F, 0.2F, limbSwing, limbSwingAmount);
		this.walk(tail_finL, swimSpeed, swimDegree * 0.2F, true, 2F, 0.2F, limbSwing, limbSwingAmount);
		this.walk(tail_finR, swimSpeed, swimDegree * 0.2F, true, 2F, 0.2F, limbSwing, limbSwingAmount);
		this.flap(finL, swimSpeed, swimDegree * 0.6F, false, 1F, 0.1F, limbSwing, limbSwingAmount);
		this.flap(finR, swimSpeed, swimDegree * 0.6F, true, 1F, 0.1F, limbSwing, limbSwingAmount);
		this.swing(tail_finL, swimSpeed, swimDegree * 0.1F, false, 3F, -0.1F, limbSwing, limbSwingAmount);
		this.swing(tail_finR, swimSpeed, swimDegree * 0.1F, true, 3F, -0.1F, limbSwing, limbSwingAmount);
		this.swing(head, swimSpeed, swimDegree * 0.2F, true, 2F, 0, limbSwing, limbSwingAmount);


	}

	@Override
	public Iterable<ModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, main_body, head, head_hammer, finL, finR, tail1, tail2,tail3, tail_finL, tail_finR, tailbottomend, tailtopend, topfintail, topfin);
	}


	public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
		advancedModelBox.xRot = x;
		advancedModelBox.yRot = y;
		advancedModelBox.zRot = z;
	}
}