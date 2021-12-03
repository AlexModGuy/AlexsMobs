package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelCrow extends AdvancedEntityModel<EntityCrow> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox leg_left;
	public final AdvancedModelBox leg_right;
	public final AdvancedModelBox wing_left;
	public final AdvancedModelBox wing_right;
	public final AdvancedModelBox tail;
	public final AdvancedModelBox head;
	public final AdvancedModelBox beak;

	public ModelCrow() {
		texWidth = 32;
		texHeight = 32;

		root = new AdvancedModelBox(this);
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setPos(0.0F, -2.1F, 0.0F);
		root.addChild(body);
		setRotationAngle(body, 1.0036F, 0.0F, 0.0F);
		body.setTextureOffset(0, 0).addBox(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);

		leg_left = new AdvancedModelBox(this);
		leg_left.setPos(0.9F, 0.0F, 0.0F);
		body.addChild(leg_left);
		setRotationAngle(leg_left, 0.5672F, 0.0F, 0.0F);
		leg_left.setTextureOffset(0, 17).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		leg_right = new AdvancedModelBox(this);
		leg_right.setPos(-0.9F, 0.0F, 0.0F);
		body.addChild(leg_right);
		setRotationAngle(leg_right, 0.5672F, 0.0F, 0.0F);
		leg_right.setTextureOffset(0, 17).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 2.0F, 3.0F, 0.0F, true);

		wing_left = new AdvancedModelBox(this);
		wing_left.setPos(1.5F, -4.9F, 1.7F);
		body.addChild(wing_left);
		setRotationAngle(wing_left, 0.0436F, 0.0F, 0.0F);
		wing_left.setTextureOffset(13, 13).addBox(-0.5F, 0.0F, -1.7F, 1.0F, 6.0F, 3.0F, 0.0F, false);

		wing_right = new AdvancedModelBox(this);
		wing_right.setPos(-1.5F, -4.9F, 1.7F);
		body.addChild(wing_right);
		setRotationAngle(wing_right, 0.0436F, 0.0F, 0.0F);
		wing_right.setTextureOffset(13, 13).addBox(-0.5F, 0.0F, -1.7F, 1.0F, 6.0F, 3.0F, 0.0F, true);

		tail = new AdvancedModelBox(this);
		tail.setPos(0.0F, -0.1F, 3.0F);
		body.addChild(tail);
		setRotationAngle(tail, -0.1309F, 0.0F, 0.0F);
		tail.setTextureOffset(13, 0).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 4.0F, 2.0F, -0.1F, false);

		head = new AdvancedModelBox(this);
		head.setPos(0.0F, -4.8F, 1.7F);
		body.addChild(head);
		setRotationAngle(head, -0.7418F, 0.0F, 0.0F);
		head.setTextureOffset(0, 9).addBox(-1.5F, -2.8F, -1.5F, 3.0F, 4.0F, 3.0F, -0.2F, false);

		beak = new AdvancedModelBox(this);
		beak.setPos(0.0F, -1.4F, -1.9F);
		head.addChild(beak);
		beak.setTextureOffset(13, 7).addBox(-0.5F, -1.0F, -1.8F, 1.0F, 2.0F, 3.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, head, beak, leg_left, leg_right, tail, body, wing_left, wing_right);
	}

	@Override
	public void setupAnim(EntityCrow entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.resetToDefaultPose();
		float flapSpeed = 0.8F;
		float flapDegree = 0.2F;
		float walkSpeed = 1.2F;
		float walkDegree = 0.78F;
		float idleSpeed = 0.1F;
		float idleDegree = 0.1F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
		float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
		float runProgress = Math.max(0, (limbSwingAmount * 5F) - flyProgress);
		float biteProgress = entity.prevAttackProgress + (entity.attackProgress - entity.prevAttackProgress) * partialTick;
		progressRotationPrev(head, biteProgress, (float)Math.toRadians(60), 0, 0, 5F);
		progressRotationPrev(body, biteProgress, (float) Math.toRadians(25), 0, 0, 5F);
		progressRotationPrev(leg_left, biteProgress, (float) Math.toRadians(-25), 0, 0, 5F);
		progressRotationPrev(leg_right, biteProgress, (float) Math.toRadians(-25), 0, 0, 5F);
		this.walk(head, idleSpeed * 0.7F, idleDegree, false, -1F, 0.05F, ageInTicks, 1);
		this.walk(tail, idleSpeed * 0.7F, idleDegree, false, 1F, 0.05F, ageInTicks, 1);
		progressRotationPrev(body, flyProgress, (float) Math.toRadians(20), 0, 0, 5F);
		progressRotationPrev(head, flyProgress, (float) Math.toRadians(-15), 0, 0, 5F);
		progressRotationPrev(leg_left, flyProgress, (float) Math.toRadians(55), 0, 0, 5F);
		progressRotationPrev(leg_right, flyProgress, (float) Math.toRadians(55), 0, 0, 5F);
		progressRotationPrev(wing_right, flyProgress,  (float) Math.toRadians(-90),  (float) Math.toRadians(90),  0, 5F);
		progressRotationPrev(wing_left, flyProgress,  (float) Math.toRadians(-90),  (float) Math.toRadians(-90), 0, 5F);
		progressPositionPrev(wing_right, flyProgress, 0F, 2F, 1F, 5f);
		progressPositionPrev(wing_left, flyProgress, 0F, 2F, 1F, 5f);
		progressRotationPrev(body, runProgress, (float)Math.toRadians(15), 0, 0, 5F);
		progressRotationPrev(head, runProgress, (float)Math.toRadians(-20), 0, 0, 5F);
		progressRotationPrev(leg_left, runProgress, (float)Math.toRadians(-15), 0, 0, 5F);
		progressRotationPrev(leg_right, runProgress, (float)Math.toRadians(-15), 0, 0, 5F);
		if(flyProgress > 0) {
			this.swing(wing_right, flapSpeed, flapDegree * 5, true, 0F, 0F, ageInTicks, 1);
			this.swing(wing_left, flapSpeed, flapDegree * 5, false, 0F, 0F, ageInTicks, 1);
			this.bob(body, flapSpeed * 0.5F, flapDegree * 4, true, ageInTicks, 1);
			this.walk(head, flapSpeed, flapDegree * 0.2F, true, 2F, -0.1F, ageInTicks, 1);

		}else{
			this.bob(body, walkSpeed * 1F, walkDegree * 1.3F, true, limbSwing, limbSwingAmount);
			this.walk(leg_right, walkSpeed, walkDegree * 1.85F, false, 0F, 0.2F, limbSwing, limbSwingAmount);
			this.walk(leg_left, walkSpeed, walkDegree * 1.85F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
			this.walk(head, walkSpeed, walkDegree * 0.4F, false, 2F, -0.01F, limbSwing, limbSwingAmount);
			this.flap(tail, walkSpeed, walkDegree * 0.2F, false, 1F, 0F, limbSwing, limbSwingAmount);

		}
		progressRotationPrev(body, sitProgress, (float)Math.toRadians(-25), 0, 0, 5F);
		progressRotationPrev(leg_left, sitProgress, (float)Math.toRadians(25), 0, 0, 5F);
		progressRotationPrev(leg_right, sitProgress, (float)Math.toRadians(25), 0, 0, 5F);
		progressRotationPrev(head, sitProgress, (float)Math.toRadians(25), 0, 0, 5F);
		head.rotateAngleY += netHeadYaw / 57.295776F;
		head.rotateAngleZ += headPitch / 57.295776F;

	}

	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		if (this.young) {
			float f = 1.45F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.translate(0.0D, 1.5D, 0D);
			parts().forEach((p_228292_8_) -> {
				p_228292_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
			this.head.setScale(0.9F, 0.9F, 0.9F);
		} else {
			this.head.setScale(0.9F, 0.9F, 0.9F);
			matrixStackIn.pushPose();
			parts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
		}
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}