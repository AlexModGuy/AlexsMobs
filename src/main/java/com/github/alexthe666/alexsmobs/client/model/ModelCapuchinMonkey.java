package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class ModelCapuchinMonkey extends AdvancedEntityModel<EntityCapuchinMonkey> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox arm_left;
	public final AdvancedModelBox arm_right;
	public final AdvancedModelBox leg_left;
	public final AdvancedModelBox leg_right;
	public final AdvancedModelBox tail1;
	public final AdvancedModelBox tail2;
	public final AdvancedModelBox tail2_r1;
	public final AdvancedModelBox head;
	public final AdvancedModelBox hair;
	public final AdvancedModelBox snout;
	public ModelAnimator animator;

	public ModelCapuchinMonkey() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this, "body");
		body.setPos(0.0F, -9.9F, 3.9F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-3.0F, -2.1F, -9.9F, 6.0F, 5.0F, 11.0F, 0.0F, false);

		arm_left = new AdvancedModelBox(this, "arm_left");
		arm_left.setPos(2.1F, 2.4F, -8.8F);
		body.addChild(arm_left);
		arm_left.setTextureOffset(28, 17).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

		arm_right = new AdvancedModelBox(this, "arm_right");
		arm_right.setPos(-2.1F, 2.4F, -8.8F);
		body.addChild(arm_right);
		arm_right.setTextureOffset(28, 17).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, true);

		leg_left = new AdvancedModelBox(this, "leg_left");
		leg_left.setPos(2.0F, 2.9F, 0.1F);
		body.addChild(leg_left);
		leg_left.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

		leg_right = new AdvancedModelBox(this, "leg_right");
		leg_right.setPos(-2.0F, 2.9F, 0.1F);
		body.addChild(leg_right);
		leg_right.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, true);

		tail1 = new AdvancedModelBox(this, "tail1");
		tail1.setPos(0.0F, -1.1F, 0.5F);
		body.addChild(tail1);
		setRotationAngle(tail1, 0.6981F, 0.0F, 0.0F);
		tail1.setTextureOffset(15, 22).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);

		tail2 = new AdvancedModelBox(this, "tail2");
		tail2.setPos(0.0F, -0.2F, 7.7F);
		tail1.addChild(tail2);
		setRotationAngle(tail2, 0.6981F, 0.0F, 0.0F);
		

		tail2_r1 = new AdvancedModelBox(this, "tail2_r1");
		tail2_r1.setPos(0.0F, -0.1875F, -0.0791F);
		tail2.addChild(tail2_r1);
		setRotationAngle(tail2_r1, -1.4399F, 0.0F, 0.0F);
		tail2_r1.setTextureOffset(0, 17).addBox(-1.0F, -0.8125F, -0.2209F, 2.0F, 3.0F, 9.0F, -0.1F, false);

		head = new AdvancedModelBox(this, "head");
		head.setPos(0.0F, -1.1F, -9.9F);
		body.addChild(head);
		head.setTextureOffset(24, 0).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);

		hair = new AdvancedModelBox(this, "hair");
		hair.setPos(0.0F, -1.0F, -2.0F);
		head.addChild(hair);
		hair.setTextureOffset(6, 38).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);

		snout = new AdvancedModelBox(this, "snout");
		snout.setPos(0.0F, 1.0F, -3.0F);
		head.addChild(snout);
		snout.setTextureOffset(0, 17).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
		this.updateDefaultPose();
		this.animator = ModelAnimator.create();
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityCapuchinMonkey.ANIMATION_THROW);
		animator.startKeyframe(3);
		animator.move(body, 0, 1, 0);
		animator.rotate(body, (float)Math.toRadians(-35), 0, 0);
		animator.rotate(head, (float)Math.toRadians(30), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(45), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(35), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(35), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(-70), 0, (float)Math.toRadians(25));
		animator.rotate(arm_left, (float)Math.toRadians(-70), 0,  (float)Math.toRadians(-25));
		animator.endKeyframe();
		animator.startKeyframe(2);
		animator.move(body, 0, 1, 0);
		animator.rotate(body, (float)Math.toRadians(-45), 0, 0);
		animator.rotate(head, (float)Math.toRadians(35), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(45), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(45), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(45), 0, 0);
		animator.move(arm_right, -1, -2, 1);
		animator.move(arm_left, 1, -2, 1);
		animator.rotate(arm_right, (float)Math.toRadians(-160), 0,  (float)Math.toRadians(5));
		animator.rotate(arm_left, (float)Math.toRadians(-160), 0,  (float)Math.toRadians(-5));
		animator.endKeyframe();
		animator.startKeyframe(2);
		animator.move(body, 0, 1, 0);
		animator.rotate(body, (float)Math.toRadians(-25), 0, 0);
		animator.rotate(head, (float)Math.toRadians(25), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(25), 0, 0);
		animator.move(arm_right, -1, -2, -1);
		animator.move(arm_left, 1, -2, -1);
		animator.rotate(arm_right, (float)Math.toRadians(-50), 0,  (float)Math.toRadians(5));
		animator.rotate(arm_left, (float)Math.toRadians(-50), 0,  (float)Math.toRadians(-5));
		animator.endKeyframe();
		animator.resetKeyframe(4);
		animator.setAnimation(EntityCapuchinMonkey.ANIMATION_SCRATCH);
		animator.startKeyframe(3);
		animator.move(body, 0, 1, 0);
		animator.rotate(body, (float)Math.toRadians(-25), 0, 0);
		animator.rotate(head, (float)Math.toRadians(25), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(25), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(-50), 0,  (float)Math.toRadians(15));
		animator.rotate(arm_left, (float)Math.toRadians(10), 0,  (float)Math.toRadians(-15));
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.move(body, 0, 1, 0);
		animator.rotate(body, (float)Math.toRadians(-25), 0, 0);
		animator.rotate(head, (float)Math.toRadians(25), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(25), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(10), 0,  (float)Math.toRadians(15));
		animator.rotate(arm_left, (float)Math.toRadians(-50), 0,  (float)Math.toRadians(-15));
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.move(body, 0, 1, 0);
		animator.rotate(body, (float)Math.toRadians(-25), 0, 0);
		animator.rotate(head, (float)Math.toRadians(25), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(25), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(-50), 0,  (float)Math.toRadians(15));
		animator.rotate(arm_left, (float)Math.toRadians(10), 0,  (float)Math.toRadians(-15));
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.move(body, 0, 1, 0);
		animator.rotate(body, (float)Math.toRadians(-25), 0, 0);
		animator.rotate(head, (float)Math.toRadians(25), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(25), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(25), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(10), 0,  (float)Math.toRadians(15));
		animator.rotate(arm_left, (float)Math.toRadians(-50), 0,  (float)Math.toRadians(-15));
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityCapuchinMonkey.ANIMATION_HEADTILT);
		animator.startKeyframe(5);
		animator.rotate(head, 0, 0, (float)Math.toRadians(25));
		animator.move(head, 0, 1, 0);
		animator.endKeyframe();
		animator.setStaticKeyframe(2);
		animator.startKeyframe(5);
		animator.move(head, 0, 1, 0);
		animator.rotate(head, 0, 0, (float)Math.toRadians(-25));
		animator.endKeyframe();
		animator.setStaticKeyframe(2);
		animator.resetKeyframe(5);

	}

		@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, leg_left, leg_right, tail1, tail2, tail2_r1, arm_left, arm_right, head, snout, hair);
	}

	@Override
	public void setupAnim(EntityCapuchinMonkey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float idleSpeed = 0.2F;
		float idleDegree = 0.4F;
		float walkSpeed = 0.8F;
		float walkDegree = 0.7F;
		float stillProgress = 5F * (1F - limbSwingAmount);
		float partialTick = Minecraft.getInstance().getFrameTime();
		float sitProgress = entity.isPassenger() ? 0 :entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
		float rideProgress = entity.isPassenger() && entity.getVehicle() instanceof LivingEntity && entity.isOwnedBy((LivingEntity) entity.getVehicle()) ? 10 : 0;
		progressPositionPrev(body, rideProgress, 3, 12F, 0, 10F);
		progressRotationPrev(body, rideProgress, 0,  (float)Math.toRadians(90), 0, 10F);
		progressRotationPrev(head, rideProgress, 0,  (float)Math.toRadians(-90), 0, 10F);
		progressRotationPrev(leg_right, rideProgress, 0, 0,  (float)Math.toRadians(-15), 10F);
		progressRotationPrev(arm_right, rideProgress, 0, 0,  (float)Math.toRadians(-15), 10F);

		progressRotationPrev(tail1, stillProgress, (float)Math.toRadians(-55), 0, 0, 5F);
		progressRotationPrev(tail2, stillProgress, (float)Math.toRadians(15), 0, 0, 5F);
		progressPositionPrev(body, sitProgress, 0, 6F, 0, 10F);
		progressRotationPrev(tail1, sitProgress, (float)Math.toRadians(-15),  (float)Math.toRadians(15), (float)Math.toRadians(90), 10F);
		progressRotationPrev(arm_left, sitProgress, (float)Math.toRadians(-85), (float)Math.toRadians(-15), 0, 10F);
		progressRotationPrev(arm_right, sitProgress, (float)Math.toRadians(-85), (float)Math.toRadians(15), 0, 10F);
		progressRotationPrev(leg_left, sitProgress, (float)Math.toRadians(85), (float)Math.toRadians(-15), 0, 10F);
		progressRotationPrev(leg_right, sitProgress, (float)Math.toRadians(85), (float)Math.toRadians(-15), 0, 10F);

		this.faceTarget(netHeadYaw, headPitch, 1, head);
		this.swing(tail1, idleSpeed, idleDegree * 0.2F, false, 0.3F, 0F, ageInTicks, 1);
		this.swing(tail2, idleSpeed, idleDegree * 0.2F, false, 0.3F, 0F, ageInTicks, 1);
		this.walk(tail1, walkSpeed, walkDegree * 0.2F, false, 1, 0F, limbSwing, limbSwingAmount);
		this.walk(tail2, walkSpeed, walkDegree * 0.2F, false, 1.3F, 0F, limbSwing, limbSwingAmount);
		this.walk(tail2_r1, walkSpeed, walkDegree * 0.2F, false, 1.5F, 0F, limbSwing, limbSwingAmount);
		this.walk(body, walkSpeed, walkDegree * 0.2F, false, 0, 0F, limbSwing, limbSwingAmount);
		this.bob(body, walkSpeed, walkDegree * 2F, false, limbSwing, limbSwingAmount);
		this.walk(arm_left, walkSpeed, walkDegree, false, 1.4F, 0F, limbSwing, limbSwingAmount);
		this.walk(arm_right, walkSpeed, walkDegree, false, 1.4F, 0F, limbSwing, limbSwingAmount);
		this.walk(leg_left, walkSpeed, walkDegree, false, -2F, 0F, limbSwing, limbSwingAmount);
		this.walk(leg_right, walkSpeed, walkDegree, false, -2F, 0F, limbSwing, limbSwingAmount);

	}

	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			float f = 1.75F;
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