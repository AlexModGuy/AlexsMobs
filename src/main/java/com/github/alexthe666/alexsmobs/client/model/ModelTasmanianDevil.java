package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityTasmanianDevil;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelTasmanianDevil extends AdvancedEntityModel<EntityTasmanianDevil> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox armLeft;
	private final AdvancedModelBox armRight;
	private final AdvancedModelBox legLeft;
	private final AdvancedModelBox legRight;
	private final AdvancedModelBox head;
	private final AdvancedModelBox earLeft;
	private final AdvancedModelBox earRight;
	private final AdvancedModelBox tail;
	public ModelAnimator animator;

	public ModelTasmanianDevil() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -6.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-3.5F, -3.0F, -5.0F, 7.0F, 6.0F, 11.0F, 0.0F, false);

		armLeft = new AdvancedModelBox(this);
		armLeft.setRotationPoint(2.6F, 3.0F, -3.0F);
		body.addChild(armLeft);
		armLeft.setTextureOffset(26, 18).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		armRight = new AdvancedModelBox(this);
		armRight.setRotationPoint(-2.6F, 3.0F, -3.0F);
		body.addChild(armRight);
		armRight.setTextureOffset(26, 18).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);

		legLeft = new AdvancedModelBox(this);
		legLeft.setRotationPoint(2.6F, 3.0F, 4.0F);
		body.addChild(legLeft);
		legLeft.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		legRight = new AdvancedModelBox(this);
		legRight.setRotationPoint(-2.6F, 3.0F, 4.0F);
		body.addChild(legRight);
		legRight.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -2.0F, -6.0F);
		body.addChild(head);
		head.setTextureOffset(0, 18).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 4.0F, 0.0F, false);
		head.setTextureOffset(26, 0).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);

		earLeft = new AdvancedModelBox(this);
		earLeft.setRotationPoint(2.0F, -1.0F, 0.0F);
		head.addChild(earLeft);
		setRotationAngle(earLeft, 0.2182F, 0.0F, 0.3054F);
		earLeft.setTextureOffset(0, 27).addBox(-1.0F, -3.0F, -1.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);

		earRight = new AdvancedModelBox(this);
		earRight.setRotationPoint(-2.0F, -1.0F, 0.0F);
		head.addChild(earRight);
		setRotationAngle(earRight, 0.2182F, 0.0F, -0.3054F);
		earRight.setTextureOffset(0, 27).addBox(-2.0F, -3.0F, -1.0F, 3.0F, 3.0F, 1.0F, 0.0F, true);

		tail = new AdvancedModelBox(this);
		tail.setRotationPoint(0.0F, -2.0F, 6.0F);
		body.addChild(tail);
		setRotationAngle(tail, -0.5236F, 0.0F, 0.0F);
		tail.setTextureOffset(15, 21).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, head,  tail, earLeft, earRight, legLeft, legRight, armLeft, armRight);
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityTasmanianDevil.ANIMATION_ATTACK);
		animator.startKeyframe(3);
		animator.rotate(head, Maths.rad(10F), 0, 0);
		animator.move(head, 0, -1, 1.5F);
		animator.endKeyframe();
		animator.startKeyframe(2);
		animator.rotate(head, Maths.rad(-15F), 0, 0);
		animator.endKeyframe();
		animator.resetKeyframe(3);
		animator.endKeyframe();
		animator.setAnimation(EntityTasmanianDevil.ANIMATION_HOWL);
		animator.startKeyframe(10);
		animator.rotate(head, Maths.rad(-45F),  Maths.rad(45F), 0);
		animator.endKeyframe();
		animator.startKeyframe(20);
		animator.rotate(head, Maths.rad(-45F),  Maths.rad(-45F), 0);
		animator.endKeyframe();
		animator.setStaticKeyframe(5);
		animator.resetKeyframe(5);
	}

	@Override
	public void setupAnim(EntityTasmanianDevil entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float walkSpeed = 1F;
		float walkDegree = 0.5F;
		float idleSpeed = 0.1F;
		float idleDegree = 0.1F;
		float partialTick = Minecraft.getInstance().getFrameTime();
		float baskProgress0 = entity.prevBaskProgress + (entity.baskProgress - entity.prevBaskProgress) * partialTick;
		float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
		float baskProgress = Math.max(0, baskProgress0 - sitProgress);
		progressRotationPrev(tail, limbSwingAmount, Maths.rad(10), 0, 0, 1F);
		progressRotationPrev(body, sitProgress, Maths.rad(-25), 0, 0, 5F);
		progressRotationPrev(armRight, sitProgress, Maths.rad(25), 0, 0, 5F);
		progressRotationPrev(armLeft, sitProgress, Maths.rad(25), 0, 0, 5F);
		progressRotationPrev(tail, sitProgress, Maths.rad(40), 0, 0, 5F);
		progressRotationPrev(head, sitProgress, Maths.rad(25), 0, 0, 5F);
		progressRotationPrev(legRight, sitProgress, Maths.rad(-40),  Maths.rad(40), 0, 5F);
		progressRotationPrev(legLeft, sitProgress, Maths.rad(-40),  Maths.rad(-40), 0, 5F);
		progressPositionPrev(body, sitProgress, 0, 0.5F, 0F, 5F);
		progressPositionPrev(armRight, sitProgress, 0, 1F, 1.2F, 5F);
		progressPositionPrev(armLeft, sitProgress, 0, 1F, 1.2F, 5F);
		progressPositionPrev(legLeft, sitProgress, 1, -1.5F, 0F, 5F);
		progressPositionPrev(legRight, sitProgress, -1, -1.5F, 0F, 5F);
		progressRotationPrev(armRight, baskProgress, Maths.rad(-80),  Maths.rad(40), 0, 5F);
		progressRotationPrev(armLeft, baskProgress, Maths.rad(-80),  Maths.rad(-40), 0, 5F);
		progressRotationPrev(legRight, baskProgress, Maths.rad(80),  Maths.rad(-20), 0, 5F);
		progressRotationPrev(legLeft, baskProgress, Maths.rad(80),  Maths.rad(20), 0, 5F);
		progressRotationPrev(tail, baskProgress, Maths.rad(20), 0, 0, 5F);
		progressRotationPrev(head, baskProgress, Maths.rad(10), 0, 0, 5F);
		progressRotationPrev(earRight, baskProgress, 0,  Maths.rad(40), 0, 5F);
		progressRotationPrev(earLeft, baskProgress, 0,  Maths.rad(-40), 0, 5F);
		progressPositionPrev(body, baskProgress, 0, 3, 0F, 5F);
		progressPositionPrev(head, baskProgress, 0, 1.2F, 0F, 5F);
		progressPositionPrev(armRight, baskProgress, 0, -1, -1.2F, 5F);
		progressPositionPrev(armLeft, baskProgress, 0, -1, -1.2F, 5F);
		progressPositionPrev(legLeft, baskProgress, 1, -1, 0F, 5F);
		progressPositionPrev(legRight, baskProgress, -1, -1, 0F, 5F);

		this.walk(armRight, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(armRight, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(armLeft, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(armLeft, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(legRight, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(legRight, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(legLeft, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.bob(legLeft, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.swing(body, walkSpeed, walkDegree * 0.6F, true, 1F, 0F, limbSwing, limbSwingAmount);
		this.swing(tail, walkSpeed, walkDegree * 0.6F, false, 1F, 0F, limbSwing, limbSwingAmount);
		this.swing(head, walkSpeed, walkDegree * 0.6F, false, 1F, 0F, limbSwing, limbSwingAmount);
		this.walk(body, walkSpeed, walkDegree * 0.05F, true, 0F, 0F, limbSwing, limbSwingAmount);
		this.walk(tail, walkSpeed, walkDegree * 0.6F, true, -1F, 0F, limbSwing, limbSwingAmount);
		this.bob(body, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.bob(head, walkSpeed, walkDegree * 0.6F, false, limbSwing, limbSwingAmount);
		this.swing(earRight, walkSpeed, walkDegree * 0.6F, false, -1F, 0.3F, limbSwing, limbSwingAmount);
		this.swing(earLeft, walkSpeed, walkDegree * 0.6F, true, -1F, 0.3F, limbSwing, limbSwingAmount);
		this.swing(tail, idleSpeed, idleDegree * 0.9F, false, 1F, 0F, ageInTicks, 1);
		this.faceTarget(netHeadYaw, headPitch, 1, head);

	}

	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			float f = 1.65F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.translate(0.0D, 1.5D, 0D);
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
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
		advancedModelBox.rotateAngleX = x;
		advancedModelBox.rotateAngleY = y;
		advancedModelBox.rotateAngleZ = z;
	}
}