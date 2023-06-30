package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelLeafcutterAnt extends AdvancedEntityModel<EntityLeafcutterAnt> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox legfront_left;
	private final AdvancedModelBox legfront_right;
	private final AdvancedModelBox legmid_left;
	private final AdvancedModelBox legmid_right;
	private final AdvancedModelBox legback_left;
	private final AdvancedModelBox legback_right;
	private final AdvancedModelBox abdomen;
	private final AdvancedModelBox head;
	private final AdvancedModelBox leaf;
	private final AdvancedModelBox leaf_r1;
	private final AdvancedModelBox antenna_left;
	private final AdvancedModelBox antenna_right;
	private final AdvancedModelBox fangs;
	private ModelAnimator animator;


	public ModelLeafcutterAnt() {
		texWidth = 32;
		texHeight = 32;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);


		body = new AdvancedModelBox(this, "body");
		body.setPos(0.0F, -4.0F, 0.125F);
		root.addChild(body);
		body.setTextureOffset(14, 5).addBox(-1.0F, -0.4F, -2.125F, 2.0F, 2.0F, 5.0F, 0.0F, false);

		legfront_left = new AdvancedModelBox(this, "legfront_left");
		legfront_left.setPos(1.0F, 1.0F, -1.125F);
		body.addChild(legfront_left);
		setRotationAngle(legfront_left, 0.0F, 0.2618F, 0.0F);
		legfront_left.setTextureOffset(0, 19).addBox(0.0F, -1.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, false);

		legfront_right = new AdvancedModelBox(this, "legfront_right");
		legfront_right.setPos(-1.0F, 1.0F, -1.125F);
		body.addChild(legfront_right);
		setRotationAngle(legfront_right, 0.0F, -0.2618F, 0.0F);
		legfront_right.setTextureOffset(0, 19).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, true);

		legmid_left = new AdvancedModelBox(this, "legmid_left");
		legmid_left.setPos(1.0F, 1.0F, 0.875F);
		body.addChild(legmid_left);
		legmid_left.setTextureOffset(0, 19).addBox(0.0F, -1.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, false);

		legmid_right = new AdvancedModelBox(this, "legmid_right");
		legmid_right.setPos(-1.0F, 1.0F, 0.875F);
		body.addChild(legmid_right);
		legmid_right.setTextureOffset(0, 19).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, true);

		legback_left = new AdvancedModelBox(this, "legback_left");
		legback_left.setPos(1.0F, 1.0F, 2.875F);
		body.addChild(legback_left);
		setRotationAngle(legback_left, 0.0F, -0.3491F, 0.0F);
		legback_left.setTextureOffset(0, 19).addBox(0.0F, -1.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, false);

		legback_right = new AdvancedModelBox(this, "legback_right");
		legback_right.setPos(-1.0F, 1.0F, 2.875F);
		body.addChild(legback_right);
		setRotationAngle(legback_right, 0.0F, 0.3491F, 0.0F);
		legback_right.setTextureOffset(0, 19).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, true);

		abdomen = new AdvancedModelBox(this, "abdomen");
		abdomen.setPos(0.0F, 0.0F, 2.875F);
		body.addChild(abdomen);
		abdomen.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, 0.0F, 4.0F, 4.0F, 5.0F, 0.0F, false);

		head = new AdvancedModelBox(this, "head");
		head.setPos(0.0F, -0.5F, -2.125F);
		body.addChild(head);
		head.setTextureOffset(0, 10).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 3.0F, 5.0F, 0.0F, false);

		leaf = new AdvancedModelBox(this, "leaf");
		leaf.setPos(0.0F, 1.0F, -5.0F);
		head.addChild(leaf);
		setRotationAngle(leaf, 0.3491F, 0.0F, 0.0F);


		leaf_r1 = new AdvancedModelBox(this, "leaf_r1");
		leaf_r1.setPos(0.0F, 0.0F, 2.0F);
		leaf.addChild(leaf_r1);
		setRotationAngle(leaf_r1, 0.5672F, 0.0F, 0.0F);
		leaf_r1.setTextureOffset(6, 5).addBox(0.0F, -14.0F, -6.0F, 0.0F, 14.0F, 13.0F, 0.0F, false);

		antenna_left = new AdvancedModelBox(this, "antenna_left");
		antenna_left.setPos(0.0F, -2.0F, -4.0F);
		head.addChild(antenna_left);
		setRotationAngle(antenna_left, -0.3927F, -0.2618F, 0.1745F);
		antenna_left.setTextureOffset(12, 13).addBox(0.0F, 0.0F, -6.0F, 5.0F, 0.0F, 6.0F, 0.0F, false);

		antenna_right = new AdvancedModelBox(this, "antenna_right");
		antenna_right.setPos(0.0F, -2.0F, -4.0F);
		head.addChild(antenna_right);
		setRotationAngle(antenna_right, -0.3927F, 0.2618F, -0.1745F);
		antenna_right.setTextureOffset(12, 13).addBox(-5.0F, 0.0F, -6.0F, 5.0F, 0.0F, 6.0F, 0.0F, true);

		fangs = new AdvancedModelBox(this, "fangs");
		fangs.setPos(0.0F, 1.0F, -5.0F);
		head.addChild(fangs);
		fangs.setTextureOffset(14, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			float f = 1.5F;
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

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(legback_left, leaf, leaf_r1, legback_right, root, body, legfront_left, legfront_right, legmid_left, legmid_right, abdomen, head, antenna_left, antenna_right, fangs);
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityLeafcutterAnt.ANIMATION_BITE);
		animator.startKeyframe(5);
		animator.move(body, 0, 0, -5);
		animator.rotate(head, Maths.rad(-25), 0, 0);
		animator.rotate(abdomen, Maths.rad(25), 0, 0);
		animator.rotate(antenna_left, Maths.rad(-25), Maths.rad(-25), 0);
		animator.rotate(antenna_right, Maths.rad(-25), Maths.rad(25), 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(body, 0, 0, 2);
		animator.rotate(head, Maths.rad(25), 0, 0);
		animator.endKeyframe();
		animator.resetKeyframe(3);

	}

	@Override
	public void setupAnim(EntityLeafcutterAnt entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float idleSpeed = 0.25F;
		float idleDegree = 0.25F;
		float walkSpeed = 1F;
		float walkDegree = 1F;
		this.swing(antenna_left, idleSpeed, idleDegree, true, 1, 0.1F, ageInTicks, 1);
		this.swing(antenna_right, idleSpeed, idleDegree, false, 1, 0.1F, ageInTicks, 1);
		this.walk(antenna_left, idleSpeed, idleDegree * 0.25F, false, -1, -0.05F, ageInTicks, 1);
		this.walk(antenna_right, idleSpeed, idleDegree * 0.25F, false, -1, -0.05F, ageInTicks, 1);
		this.swing(legback_right, walkSpeed, walkDegree * 1.2F, false, 0, 0.2F, limbSwing, limbSwingAmount);
		this.flap(legback_right, walkSpeed, walkDegree * 0.8F, false, -1.5F, 0.4F, limbSwing, limbSwingAmount);
		this.swing(legfront_right, walkSpeed, walkDegree, false, 0, -0.3F, limbSwing, limbSwingAmount);
		this.flap(legfront_right, walkSpeed, walkDegree * 0.8F, false, -1.5F, 0.4F, limbSwing, limbSwingAmount);
		this.swing(legmid_left, walkSpeed, walkDegree, false, 0, 0F, limbSwing, limbSwingAmount);
		this.flap(legmid_left, walkSpeed, walkDegree * 0.8F, false, -1.5F, -0.4F, limbSwing, limbSwingAmount);
		this.bob(body, walkSpeed * 2F, walkDegree * -0.6F, false, limbSwing, limbSwingAmount);
		float offsetleft = 2F;
		this.swing(legback_left, walkSpeed, -walkDegree * 1.2F, false, offsetleft, -0.2F, limbSwing, limbSwingAmount);
		this.flap(legback_left, walkSpeed, walkDegree * 0.8F, false, offsetleft-1.5F, -0.4F, limbSwing, limbSwingAmount);
		this.swing(legfront_left, walkSpeed, -walkDegree, false, offsetleft, 0.3F, limbSwing, limbSwingAmount);
		this.flap(legfront_left, walkSpeed, walkDegree * 0.8F, false, offsetleft + 1.5F, -0.4F, limbSwing, limbSwingAmount);
		this.swing(legmid_right, walkSpeed, -walkDegree, false, offsetleft,  0, limbSwing, limbSwingAmount);
		this.flap(legmid_right, walkSpeed, walkDegree * 0.8F, false, offsetleft-1.5F, 0.4F, limbSwing, limbSwingAmount);
		this.swing(abdomen, walkSpeed, walkDegree * 0.2F, false, 3, 0, limbSwing, limbSwingAmount);
		this.faceTarget(netHeadYaw, headPitch, 1.2F, head);

	}


	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}

	public void animateAnteater(EntityAnteater anteater, float partialTicks) {
		this.resetToDefaultPose();
		float ageInTicks = anteater.tickCount + partialTicks;
		float struggleSpeed = 0.5F;
		float struggleDegree = 1.0F;
		this.swing(root, struggleSpeed, struggleDegree * 0.8F, false, 0, 0, ageInTicks, 1);
	}
}