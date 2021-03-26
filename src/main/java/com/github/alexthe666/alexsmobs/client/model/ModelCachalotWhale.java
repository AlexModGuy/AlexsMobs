package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ModelCachalotWhale extends AdvancedEntityModel<EntityCachalotWhale> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox top_fin;
	private final AdvancedModelBox arm_left;
	private final AdvancedModelBox arm_right;
	private final AdvancedModelBox tail1;
	private final AdvancedModelBox tail2;
	private final AdvancedModelBox tail3;
	private final AdvancedModelBox head;
	private final AdvancedModelBox jaw;
	private final AdvancedModelBox teeth;

	public ModelCachalotWhale() {
		textureWidth = 512;
		textureHeight = 512;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -30.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-21.0F, -30.0F, -60.0F, 42.0F, 60.0F, 112.0F, 0.0F, false);

		top_fin = new AdvancedModelBox(this);
		top_fin.setRotationPoint(0.0F, -34.0F, 42.0F);
		body.addChild(top_fin);
		top_fin.setTextureOffset(0, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 8.0F, 20.0F, 0.0F, false);

		arm_left = new AdvancedModelBox(this);
		arm_left.setRotationPoint(21.0F, 26.0F, -38.0F);
		body.addChild(arm_left);
		arm_left.setTextureOffset(304, 220).addBox(0.0F, -2.0F, -3.0F, 36.0F, 4.0F, 21.0F, 0.0F, false);

		arm_right = new AdvancedModelBox(this);
		arm_right.setRotationPoint(-21.0F, 26.0F, -38.0F);
		body.addChild(arm_right);
		arm_right.setTextureOffset(304, 220).addBox(-36.0F, -2.0F, -3.0F, 36.0F, 4.0F, 21.0F, 0.0F, true);

		tail1 = new AdvancedModelBox(this);
		tail1.setRotationPoint(0.0F, -1.0F, 52.0F);
		body.addChild(tail1);
		tail1.setTextureOffset(163, 227).addBox(-15.0F, -22.0F, 0.0F, 30.0F, 45.0F, 80.0F, 0.0F, false);

		tail2 = new AdvancedModelBox(this);
		tail2.setRotationPoint(0.0F, -1.0F, 80.0F);
		tail1.addChild(tail2);
		tail2.setTextureOffset(197, 0).addBox(-9.0F, -14.0F, 0.0F, 18.0F, 28.0F, 65.0F, 0.0F, false);

		tail3 = new AdvancedModelBox(this);
		tail3.setRotationPoint(0.0F, 2.0F, 56.0F);
		tail2.addChild(tail3);
		tail3.setTextureOffset(158, 173).addBox(-33.0F, -5.0F, -5.0F, 66.0F, 9.0F, 37.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -2.0F, -60.0F);
		body.addChild(head);
		head.setTextureOffset(0, 173).addBox(-18.0F, -28.0F, -85.0F, 36.0F, 48.0F, 85.0F, 0.0F, false);

		jaw = new AdvancedModelBox(this);
		jaw.setRotationPoint(0.0F, 20.0F, 0.0F);
		head.addChild(jaw);
		jaw.setTextureOffset(293, 23).addBox(-7.0F, 0.0F, -71.0F, 14.0F, 9.0F, 71.0F, 0.0F, false);

		teeth = new AdvancedModelBox(this);
		teeth.setRotationPoint(0.0F, 0.0F, -7.0F);
		jaw.addChild(teeth);
		teeth.setTextureOffset(32, 370).addBox(-4.0F, -4.0F, -59.0F, 8.0F, 4.0F, 60.0F, 0.0F, false);
		this.updateDefaultPose();
	}
	
	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, head, body, tail1, tail2, tail3, top_fin, jaw, teeth, arm_left, arm_right);
	}

	@Override
	public void setRotationAngles(EntityCachalotWhale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();
		float partialTicks = ageInTicks - entity.ticksExisted;
		float renderYaw = (float)entity.getMovementOffsets(0, partialTicks)[0] ;
		float properPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
		float chargeProgress = entity.prevChargingProgress + (entity.chargeProgress - entity.prevChargingProgress) * partialTicks;
		float f = MathHelper.clamp((float)entity.getMovementOffsets(7, partialTicks)[0] - renderYaw, -50, 50);
		this.tail1.rotateAngleY += (float) MathHelper.clamp((float)entity.getMovementOffsets(15, partialTicks)[0] - renderYaw, -50, 50)  * 0.017453292F;
		this.tail2.rotateAngleY += (float) MathHelper.clamp((float)entity.getMovementOffsets(17, partialTicks)[0] - renderYaw, -50, 50)  * 0.017453292F;
		this.body.rotateAngleX += properPitch * ((float)Math.PI / 180F);
		this.body.rotateAngleZ += f * 0.017453292F;
		AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{tail1, tail2, tail3};
		float swimSpeed = 0.2F;
		float swimDegree = 0.4F;
		this.swing(arm_left, swimSpeed, swimDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.swing(arm_right, swimSpeed, swimDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.flap(arm_left, swimSpeed, swimDegree * 1.4F, true, 2.5F, 0F, limbSwing, limbSwingAmount);
		this.flap(arm_right, swimSpeed, swimDegree * 1.4F, false, 2.5F, 0F, limbSwing, limbSwingAmount);
		this.bob(body, swimSpeed, swimDegree * 8F, false, limbSwing, limbSwingAmount);
		this.chainWave(tailBoxes, swimSpeed, swimDegree * 0.8F, -2F, limbSwing, limbSwingAmount);
		this.walk(head, swimSpeed, swimDegree * 0.1F, false, 2F, 0, limbSwing, limbSwingAmount);
		this.tail1.rotationPointZ -= 4 * limbSwingAmount;
		this.tail2.rotationPointZ -= 2 * limbSwingAmount;
		progressRotationPrev(jaw, chargeProgress, (float)Math.toRadians(30), 0, 0, 10F);

	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay);

	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}