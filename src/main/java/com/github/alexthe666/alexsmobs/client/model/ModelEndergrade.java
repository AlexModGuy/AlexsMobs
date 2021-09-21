package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelEndergrade extends AdvancedEntityModel<EntityEndergrade> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox bodymain;
	private final AdvancedModelBox legbackL;
	private final AdvancedModelBox legbackR;
	private final AdvancedModelBox legmidL;
	private final AdvancedModelBox legmidR;
	private final AdvancedModelBox bodyfront;
	private final AdvancedModelBox head;
	private final AdvancedModelBox mouth;
	private final AdvancedModelBox legfrontL;
	private final AdvancedModelBox legfrontR;
	private final AdvancedModelBox tail;

	public ModelEndergrade() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		bodymain = new AdvancedModelBox(this);
		bodymain.setPos(0.0F, -9.0F, -1.0F);
		root.addChild(bodymain);
		bodymain.texOffs(0, 0).addBox(-4.5F, -3.5F, 0.0F, 9.0F, 9.0F, 10.0F, 0.0F, false);

		legbackL = new AdvancedModelBox(this);
		legbackL.setPos(3.5F, 3.5F, 7.0F);
		bodymain.addChild(legbackL);
		legbackL.texOffs(11, 45).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);

		legbackR = new AdvancedModelBox(this);
		legbackR.setPos(-3.5F, 3.5F, 7.0F);
		bodymain.addChild(legbackR);
		legbackR.texOffs(11, 45).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, true);

		legmidL = new AdvancedModelBox(this);
		legmidL.setPos(3.5F, 3.5F, 1.0F);
		bodymain.addChild(legmidL);
		legmidL.texOffs(39, 0).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);

		legmidR = new AdvancedModelBox(this);
		legmidR.setPos(-3.5F, 3.5F, 1.0F);
		bodymain.addChild(legmidR);
		legmidR.texOffs(39, 0).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, true);

		bodyfront = new AdvancedModelBox(this);
		bodyfront.setPos(0.0F, 0.5F, 0.0F);
		bodymain.addChild(bodyfront);
		bodyfront.texOffs(25, 29).addBox(-4.0F, -3.5F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, -0.5F, -8.0F);
		bodyfront.addChild(head);
		head.texOffs(35, 16).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 6.0F, 4.0F, 0.0F, false);

		mouth = new AdvancedModelBox(this);
		mouth.setPos(0.0F, 1.5F, -4.5F);
		head.addChild(mouth);
		mouth.texOffs(26, 46).addBox(-1.5F, -1.5F, -2.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);

		legfrontL = new AdvancedModelBox(this);
		legfrontL.setPos(3.5F, 3.0F, -5.0F);
		bodyfront.addChild(legfrontL);
		legfrontL.texOffs(0, 37).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);

		legfrontR = new AdvancedModelBox(this);
		legfrontR.setPos(-3.5F, 3.0F, -5.0F);
		bodyfront.addChild(legfrontR);
		legfrontR.texOffs(0, 37).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, true);

		tail = new AdvancedModelBox(this);
		tail.setPos(0.5F, -1.0F, 9.9F);
		bodymain.addChild(tail);
		setRotationAngle(tail, -0.1745F, 0.0F, 0.0F);
		tail.texOffs(0, 20).addBox(-4.0F, -1.5F, -2.4F, 7.0F, 7.0F, 9.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			float f = 1.75F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.35F, 0.35F, 0.35F);
			matrixStackIn.translate(0.0D, 2.75D, 0.125D);
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


	@Override
	public void setupAnim(EntityEndergrade entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		AdvancedModelBox[] bodyParts = new AdvancedModelBox[]{bodyfront, bodymain, tail};
		AdvancedModelBox[] legPartsRight = new AdvancedModelBox[]{legfrontR, legmidR, legbackR};
		AdvancedModelBox[] legPartsLeft = new AdvancedModelBox[]{legfrontL, legmidL, legbackL};
		float walkSpeed = 1.7F;
		float walkDegree = 0.7F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float birdPitch = entityIn.prevTartigradePitch + (entityIn.tartigradePitch - entityIn.prevTartigradePitch) * partialTick;
		float biteProgress= entityIn.prevBiteProgress + (entityIn.biteProgress - entityIn.prevBiteProgress) * partialTick;
		this.mouth.setScale(1, 1, 1 + biteProgress * 0.4F);
		this.bodymain.xRot += birdPitch * ((float)Math.PI / 180F);
		this.mouth.z = -3 - biteProgress * 0.2F;
		this.chainWave(bodyParts, walkSpeed, walkDegree * 0.3F, -1, limbSwing, limbSwingAmount);
		this.chainWave(legPartsRight, walkSpeed, walkDegree, -1, limbSwing, limbSwingAmount);
		this.chainWave(legPartsLeft, walkSpeed, walkDegree, -1, limbSwing, limbSwingAmount);
		this.chainFlap(legPartsRight, walkSpeed, walkDegree, 3, limbSwing, limbSwingAmount);
		this.chainFlap(legPartsLeft, walkSpeed, -walkDegree, 3, limbSwing, limbSwingAmount);
		this.swing(tail, walkSpeed, walkDegree * 0.5F, false, 0f, 0f, limbSwing, limbSwingAmount);
	}

	@Override
	public Iterable<ModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, bodymain, legbackL, legbackR, legfrontL, legfrontR, legmidL, legmidR, bodyfront, head, mouth, tail);
	}

	public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
		advancedModelBox.xRot = x;
		advancedModelBox.yRot = y;
		advancedModelBox.zRot = z;
	}
}