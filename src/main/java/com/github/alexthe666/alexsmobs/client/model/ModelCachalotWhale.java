package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelCachalotWhale extends AdvancedEntityModel<EntityCachalotWhale> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox top_fin;
	public final AdvancedModelBox arm_left;
	public final AdvancedModelBox arm_right;
	public final AdvancedModelBox tail1;
	public final AdvancedModelBox tail2;
	public final AdvancedModelBox tail3;
	public final AdvancedModelBox head;
	public final AdvancedModelBox jaw;
	public final AdvancedModelBox teeth;

	public ModelCachalotWhale() {
		texWidth = 512;
		texHeight = 512;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setPos(0.0F, -30.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-21.0F, -30.0F, -60.0F, 42.0F, 60.0F, 112.0F, 0.0F, false);

		top_fin = new AdvancedModelBox(this);
		top_fin.setPos(0.0F, -34.0F, 42.0F);
		body.addChild(top_fin);
		top_fin.setTextureOffset(0, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 8.0F, 20.0F, 0.0F, false);

		arm_left = new AdvancedModelBox(this);
		arm_left.setPos(21.0F, 26.0F, -38.0F);
		body.addChild(arm_left);
		arm_left.setTextureOffset(304, 220).addBox(0.0F, -2.0F, -3.0F, 36.0F, 4.0F, 21.0F, 0.0F, false);

		arm_right = new AdvancedModelBox(this);
		arm_right.setPos(-21.0F, 26.0F, -38.0F);
		body.addChild(arm_right);
		arm_right.setTextureOffset(304, 220).addBox(-36.0F, -2.0F, -3.0F, 36.0F, 4.0F, 21.0F, 0.0F, true);

		tail1 = new AdvancedModelBox(this);
		tail1.setPos(0.0F, -1.0F, 52.0F);
		body.addChild(tail1);
		tail1.setTextureOffset(163, 227).addBox(-15.0F, -22.0F, 0.0F, 30.0F, 45.0F, 80.0F, 0.0F, false);

		tail2 = new AdvancedModelBox(this);
		tail2.setPos(0.0F, -1.0F, 80.0F);
		tail1.addChild(tail2);
		tail2.setTextureOffset(197, 0).addBox(-9.0F, -14.0F, 0.0F, 18.0F, 28.0F, 65.0F, 0.0F, false);

		tail3 = new AdvancedModelBox(this);
		tail3.setPos(0.0F, 2.0F, 56.0F);
		tail2.addChild(tail3);
		tail3.setTextureOffset(158, 173).addBox(-33.0F, -5.0F, -5.0F, 66.0F, 9.0F, 37.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, -2.0F, -60.0F);
		body.addChild(head);
		head.setTextureOffset(0, 173).addBox(-18.0F, -28.0F, -85.0F, 36.0F, 48.0F, 85.0F, 0.0F, false);

		jaw = new AdvancedModelBox(this);
		jaw.setPos(0.0F, 20.0F, 0.0F);
		head.addChild(jaw);
		jaw.setTextureOffset(293, 23).addBox(-7.0F, 0.0F, -71.0F, 14.0F, 9.0F, 71.0F, 0.0F, false);

		teeth = new AdvancedModelBox(this);
		teeth.setPos(0.0F, 0.0F, -7.0F);
		jaw.addChild(teeth);
		teeth.setTextureOffset(32, 370).addBox(-4.0F, -4.0F, -59.0F, 8.0F, 4.0F, 60.0F, 0.0F, false);
		this.updateDefaultPose();
	}
	
	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, head, body, tail1, tail2, tail3, top_fin, jaw, teeth, arm_left, arm_right);
	}

	@Override
	public void setupAnim(EntityCachalotWhale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();
		float partialTicks = ageInTicks - entity.tickCount;
		float renderYaw = (float)entity.getMovementOffsets(0, partialTicks)[0] ;
		float properPitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
		float chargeProgress = entity.prevChargingProgress + (entity.chargeProgress - entity.prevChargingProgress) * partialTicks;
		float sleepProgress = entity.prevSleepProgress + (entity.sleepProgress - entity.prevSleepProgress) * partialTicks;
		float beachedProgress = entity.prevBeachedProgress + (entity.beachedProgress - entity.prevBeachedProgress) * partialTicks;
		float grabProgress = entity.prevGrabProgress + (entity.grabProgress - entity.prevGrabProgress) * partialTicks;
		float f = Mth.clamp((float)entity.getMovementOffsets(7, partialTicks)[0] - renderYaw, -50, 50);
		this.tail1.rotateAngleY += (float) Mth.clamp((float)entity.getMovementOffsets(15, partialTicks)[0] - renderYaw, -50, 50)  * 0.017453292F;
		this.tail2.rotateAngleY += (float) Mth.clamp((float)entity.getMovementOffsets(17, partialTicks)[0] - renderYaw, -50, 50)  * 0.017453292F;
		this.body.rotateAngleX += Math.min(properPitch, sleepProgress * -9) * ((float)Math.PI / 180F);
		this.body.rotateAngleZ += f * 0.017453292F;
		this.head.rotateAngleY += Math.sin((entity.grabTime + partialTicks) * 0.3F) * 0.1F * grabProgress;
		AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{tail1, tail2, tail3};
		float swimSpeed = 0.2F + 0;
		float swimDegree = 0.4F;
		float beachedSpeed = 0.05F;
		float beachedIdle = 0.4F;
		progressRotationPrev(jaw, Math.max(chargeProgress, grabProgress * 0.8F), (float)Math.toRadians(30), 0, 0, 10F);
		progressRotationPrev(jaw, beachedProgress, (float)Math.toRadians(20), (float)Math.toRadians(5), 0, 10F);
		progressRotationPrev(body, beachedProgress, 0, 0,  (float)Math.toRadians(80), 10F);
		progressRotationPrev(tail1, beachedProgress, (float)Math.toRadians(-30), (float)Math.toRadians(10),  0, 10F);
		progressRotationPrev(tail2, beachedProgress, (float)Math.toRadians(-30), (float)Math.toRadians(-30),  (float)Math.toRadians(-30), 10F);
		progressRotationPrev(tail3, beachedProgress, 0, (float)Math.toRadians(-10),  (float)Math.toRadians(-60), 10F);
		progressRotationPrev(head, beachedProgress, 0, (float)Math.toRadians(-10), 0, 10F);
		progressRotationPrev(arm_right, beachedProgress, 0, 0,  (float)Math.toRadians(-110), 10F);
		progressRotationPrev(arm_left, beachedProgress, 0, 0,  (float)Math.toRadians(110), 10F);
		progressPositionPrev(tail1, beachedProgress, -2F, -1F, -10F, 10F);
		progressPositionPrev(tail2, beachedProgress, 0F, -1F, -4F, 10F);
		progressPositionPrev(tail3, beachedProgress, 0F, 2F, 0F, 10F);
		progressPositionPrev(body, beachedProgress, 0F, 5F, 0F, 10F);
		progressPositionPrev(head, beachedProgress, 0F, 0F, 3F, 10F);
		if(beachedProgress > 0){
			this.swing(arm_left, beachedSpeed, beachedIdle * 0.2F, true, 1F, 0F, ageInTicks, 1);
			this.flap(arm_right, beachedSpeed, beachedIdle * 0.2F, true, 3F, 0.06F, ageInTicks, 1);
			this.walk(jaw, beachedSpeed, beachedIdle * 0.2F, true, 2F, 0.06F, ageInTicks, 1);
			this.walk(tail1, beachedSpeed, beachedIdle * 0.2F, false, 4F, 0.06F, ageInTicks, 1);
			this.walk(tail2, beachedSpeed, beachedIdle * 0.2F, false, 4F, 0.06F, ageInTicks, 1);
		}else{
			this.walk(jaw, swimSpeed * 0.4F, swimDegree * 0.15F, true, 1F, -0.01F, ageInTicks, 1);
			this.flap(arm_left, swimSpeed * 0.4F, swimDegree * 0.5F, true, 2.5F, -0.4F, ageInTicks, 1);
			this.flap(arm_right, swimSpeed * 0.4F, swimDegree * 0.5F, false, 2.5F, -0.4F, ageInTicks, 1);
			this.swing(arm_left, swimSpeed, swimDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.swing(arm_right, swimSpeed, swimDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
			this.flap(arm_left, swimSpeed, swimDegree * 1.4F, true, 2.5F, 0F, limbSwing, limbSwingAmount);
			this.flap(arm_right, swimSpeed, swimDegree * 1.4F, false, 2.5F, 0F, limbSwing, limbSwingAmount);
			this.bob(body, swimSpeed, swimDegree * 20, false, limbSwing, limbSwingAmount);
			this.chainWave(tailBoxes, swimSpeed, swimDegree * 0.8F, -2F, limbSwing, limbSwingAmount);
			this.walk(head, swimSpeed, swimDegree * 0.1F, false, 2F, 0, limbSwing, limbSwingAmount);
			this.tail1.rotationPointZ -= 4 * limbSwingAmount;
			this.tail2.rotationPointZ -= 2 * limbSwingAmount;
		}
	}

	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			float f = 1.25F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.translate(0.0D, 1.5D, 0.125D);
			parts().forEach((p_228292_8_) -> {
				p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
			head.setScale(1, 1, 1);
		} else {
			matrixStackIn.pushPose();
			parts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
		}

	}
	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}