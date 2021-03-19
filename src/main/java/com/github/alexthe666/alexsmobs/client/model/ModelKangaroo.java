package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ModelKangaroo extends AdvancedEntityModel<EntityKangaroo> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox pouch;
	public final AdvancedModelBox tail1;
	public final AdvancedModelBox tail2;
	public final AdvancedModelBox leg_left;
	public final AdvancedModelBox knee_left;
	public final AdvancedModelBox foot_left;
	public final AdvancedModelBox leg_right;
	public final AdvancedModelBox knee_right;
	public final AdvancedModelBox foot_right;
	public final AdvancedModelBox chest;
	public final AdvancedModelBox arm_left;
	public final AdvancedModelBox arm_right;
	public final AdvancedModelBox neck;
	public final AdvancedModelBox head;
	public final AdvancedModelBox ear_left;
	public final AdvancedModelBox ear_right;
	public final AdvancedModelBox snout;
	public static boolean renderOnlyHead = false;
	private ModelAnimator animator;

	public ModelKangaroo() {
		textureWidth = 128;
		textureHeight = 128;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this);
		body.setRotationPoint(0.0F, -15.0F, 4.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-5.0F, -6.0F, -6.0F, 10.0F, 11.0F, 13.0F, 0.0F, false);

		pouch = new AdvancedModelBox(this);
		pouch.setRotationPoint(0.0F, 2.7F, -2.2F);
		body.addChild(pouch);
		pouch.setTextureOffset(64, 6).addBox(-3.5F, -2.5F, -4.0F, 7.0F, 5.0F, 8.0F, -0.1F, false);

		tail1 = new AdvancedModelBox(this);
		tail1.setRotationPoint(0.0F, -5.0F, 7.0F);
		body.addChild(tail1);
		tail1.setTextureOffset(0, 25).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 6.0F, 15.0F, 0.0F, false);

		tail2 = new AdvancedModelBox(this);
		tail2.setRotationPoint(0.0F, 4.0F, 15.0F);
		tail1.addChild(tail2);
		tail2.setTextureOffset(26, 32).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 4.0F, 15.0F, 0.0F, false);

		leg_left = new AdvancedModelBox(this);
		leg_left.setRotationPoint(4.25F, 0.75F, -0.5F);
		body.addChild(leg_left);
		leg_left.setTextureOffset(48, 28).addBox(-1.25F, -3.75F, -3.5F, 3.0F, 7.0F, 8.0F, 0.0F, false);

		knee_left = new AdvancedModelBox(this);
		knee_left.setRotationPoint(0.25F, 3.25F, -3.5F);
		leg_left.addChild(knee_left);
		knee_left.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 9.0F, 3.0F, 0.0F, false);

		foot_left = new AdvancedModelBox(this);
		foot_left.setRotationPoint(0.0F, 9.0F, 1.0F);
		knee_left.addChild(foot_left);
		foot_left.setTextureOffset(35, 13).addBox(-1.5F, 0.0F, -10.0F, 3.0F, 2.0F, 12.0F, 0.0F, false);

		leg_right = new AdvancedModelBox(this);
		leg_right.setRotationPoint(-4.25F, 0.75F, -0.5F);
		body.addChild(leg_right);
		leg_right.setTextureOffset(48, 28).addBox(-1.75F, -3.75F, -3.5F, 3.0F, 7.0F, 8.0F, 0.0F, true);

		knee_right = new AdvancedModelBox(this);
		knee_right.setRotationPoint(-0.25F, 3.25F, -3.5F);
		leg_right.addChild(knee_right);
		knee_right.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 9.0F, 3.0F, 0.0F, true);

		foot_right = new AdvancedModelBox(this);
		foot_right.setRotationPoint(0.0F, 9.0F, 1.0F);
		knee_right.addChild(foot_right);
		foot_right.setTextureOffset(35, 13).addBox(-1.5F, 0.0F, -10.0F, 3.0F, 2.0F, 12.0F, 0.0F, true);

		chest = new AdvancedModelBox(this);
		chest.setRotationPoint(0.0F, -6.0F, -6.0F);
		body.addChild(chest);
		chest.setTextureOffset(0, 47).addBox(-4.0F, 0.0F, -9.0F, 8.0F, 9.0F, 9.0F, 0.0F, false);

		arm_left = new AdvancedModelBox(this);
		arm_left.setRotationPoint(4.0F, 6.0F, -6.0F);
		chest.addChild(arm_left);
		arm_left.setTextureOffset(71, 49).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 12.0F, 3.0F, 0.0F, false);

		arm_right = new AdvancedModelBox(this);
		arm_right.setRotationPoint(-4.0F, 6.0F, -6.0F);
		chest.addChild(arm_right);
		arm_right.setTextureOffset(71, 49).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 12.0F, 3.0F, 0.0F, true);

		neck = new AdvancedModelBox(this);
		neck.setRotationPoint(0.0F, 2.0F, -8.0F);
		chest.addChild(neck);
		neck.setTextureOffset(35, 52).addBox(-2.0F, -6.0F, -3.0F, 4.0F, 11.0F, 5.0F, 0.0F, false);

		head = new AdvancedModelBox(this);
		head.setRotationPoint(0.0F, -6.0F, -0.5F);
		neck.addChild(head);
		head.setTextureOffset(34, 0).addBox(-2.5F, -4.0F, -3.5F, 5.0F, 4.0F, 6.0F, 0.0F, false);

		ear_left = new AdvancedModelBox(this);
		ear_left.setRotationPoint(0.4F, -4.0F, 1.5F);
		head.addChild(ear_left);
		setRotationAngle(ear_left, -0.1745F, -0.3491F, 0.4363F);
		ear_left.setTextureOffset(0, 47).addBox(0.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, 0.0F, false);

		ear_right = new AdvancedModelBox(this);
		ear_right.setRotationPoint(-0.4F, -4.0F, 1.5F);
		head.addChild(ear_right);
		setRotationAngle(ear_right, -0.1745F, 0.3491F, -0.4363F);
		ear_right.setTextureOffset(0, 47).addBox(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, 0.0F, true);
		setRotationAngle(chest, 0.1745F, 0F, 0F);
		setRotationAngle(tail1, -0.1745F, 0F, 0F);
		snout = new AdvancedModelBox(this);
		snout.setRotationPoint(0.0F, -1.5F, -3.5F);
		head.addChild(snout);
		snout.setTextureOffset(0, 25).addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);
		this.updateDefaultPose();
		animator = ModelAnimator.create();
	}

	public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
		this.resetToDefaultPose();
		animator.update(entity);
		animator.setAnimation(EntityKangaroo.ANIMATION_EAT_GRASS);
		animator.startKeyframe(5);
		animator.move(neck, 0, 3, -2);
		animator.rotate(neck, (float)Math.toRadians(100), 0, 0);
		animator.rotate(chest, (float)Math.toRadians(10), 0, 0);
		animator.rotate(head, (float)Math.toRadians(-20), 0, 0);
		animator.rotate(arm_left, (float)Math.toRadians(-20), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(-20), 0, 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(neck, 0, 3, -2);
		animator.rotate(neck, (float)Math.toRadians(70), 0, 0);
		animator.rotate(chest, (float)Math.toRadians(10), 0, 0);
		animator.rotate(head, (float)Math.toRadians(-30), 0, 0);
		animator.rotate(arm_left, (float)Math.toRadians(-20), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(-20), 0, 0);
		animator.endKeyframe();
		animator.startKeyframe(5);
		animator.move(neck, 0, 3, -2);
		animator.rotate(neck, (float)Math.toRadians(100), 0, 0);
		animator.rotate(chest, (float)Math.toRadians(10), 0, 0);
		animator.rotate(head, (float)Math.toRadians(-20), 0, 0);
		animator.rotate(arm_left, (float)Math.toRadians(-20), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(-20), 0, 0);
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityKangaroo.ANIMATION_KICK);
		animator.startKeyframe(5);
		animator.move(head, 0, 1, -1);
		animator.rotate(body, (float)Math.toRadians(30), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(-30), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(-30), 0, 0);
		animator.rotate(chest, (float)Math.toRadians(10), 0, 0);
		animator.rotate(head, (float)Math.toRadians(-20), 0, 0);
		animator.rotate(neck, (float)Math.toRadians(-10), 0, 0);
		animator.rotate(arm_left, (float)Math.toRadians(-20), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(-20), 0, 0);
		animator.endKeyframe();
		animator.setStaticKeyframe(2);
		animator.startKeyframe(5);
		animator.move(body, 0, -4, -20);
		animator.move(chest, 0, 2, 2);
		animator.move(knee_right, 0, -1, 0);
		animator.move(knee_left, 0, -1, 0);
		animator.rotate(body, (float)Math.toRadians(-40), 0, 0);
		animator.rotate(neck, (float)Math.toRadians(50), 0, 0);
		animator.rotate(tail1, (float)Math.toRadians(20), 0, 0);
		animator.rotate(tail2, (float)Math.toRadians(20), 0, 0);
		animator.rotate(leg_right, (float)Math.toRadians(-10), 0, 0);
		animator.rotate(leg_left, (float)Math.toRadians(-10), 0, 0);
		animator.rotate(knee_left, (float)Math.toRadians(-40), 0, 0);
		animator.rotate(knee_right, (float)Math.toRadians(-40), 0, 0);
		animator.rotate(foot_left, (float)Math.toRadians(50), 0, 0);
		animator.rotate(foot_right, (float)Math.toRadians(50), 0, 0);
		animator.rotate(arm_right, 0, 0, (float)Math.toRadians(-15));
		animator.rotate(arm_left, 0, 0, (float)Math.toRadians(15));
		animator.endKeyframe();
		animator.resetKeyframe(3);
		animator.setAnimation(EntityKangaroo.ANIMATION_PUNCH_R);
		animator.startKeyframe(3);
		animator.rotate(chest, (float)Math.toRadians(-10), (float)Math.toRadians(-30), 0);
		animator.rotate(neck, 0, (float)Math.toRadians(30), 0);
		animator.rotate(arm_right, (float)Math.toRadians(15), 0, 0);
		animator.rotate(arm_left, (float)Math.toRadians(15), 0, 0);
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(chest, (float)Math.toRadians(10), (float)Math.toRadians(-10), 0);
		animator.rotate(neck, 0, (float)Math.toRadians(-40), 0);
		animator.rotate(head, 0, (float)Math.toRadians(30), 0);
		animator.rotate(arm_right, (float)Math.toRadians(-125), 0, 0);
		animator.rotate(arm_left, (float)Math.toRadians(15), 0, 0);
		animator.endKeyframe();
		animator.resetKeyframe(5);
		animator.setAnimation(EntityKangaroo.ANIMATION_PUNCH_L);
		animator.startKeyframe(3);
		animator.rotate(chest, (float)Math.toRadians(-10), (float)Math.toRadians(30), 0);
		animator.rotate(neck, 0, (float)Math.toRadians(30), 0);
		animator.rotate(arm_right, (float)Math.toRadians(15), 0, 0);
		animator.rotate(arm_left, (float)Math.toRadians(15), 0, 0);
		animator.endKeyframe();
		animator.startKeyframe(3);
		animator.rotate(chest, (float)Math.toRadians(10), (float)Math.toRadians(10), 0);
		animator.rotate(neck, 0, (float)Math.toRadians(-40), 0);
		animator.rotate(head, 0, (float)Math.toRadians(30), 0);
		animator.rotate(arm_left, (float)Math.toRadians(-125), 0, 0);
		animator.rotate(arm_right, (float)Math.toRadians(15), 0, 0);
		animator.endKeyframe();
		animator.resetKeyframe(5);
	}


	@Override
	public void setRotationAngles(EntityKangaroo entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float partialTick = Minecraft.getInstance().getRenderPartialTicks();
		float jumpRotation = MathHelper.sin(entity.getJumpCompletion(partialTick) * 3.1415927F);
		float walkSpeed = 1F;
		float walkDegree = 0.5F;
		float idleSpeed = 0.05F;
		float idleDegree = 0.1F;
		float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
		float pouchOpenProgress = entity.prevPouchProgress + (entity.pouchProgress - entity.prevPouchProgress) * partialTick;
		float moveProgress = entity.prevTotalMovingProgress + (entity.totalMovingProgress - entity.prevTotalMovingProgress) * partialTick;
		float stillProgress = Math.max(0, (entity.prevStandProgress + (entity.standProgress - entity.prevStandProgress) * partialTick) - moveProgress);
		if(entity.getVisualFlag() == 1){
			progressRotationPrev(arm_left, 1, (float)Math.toRadians(-65), 0, (float)Math.toRadians(-45), 1F);
			progressRotationPrev(arm_right, 1, (float)Math.toRadians(-65), 0, (float)Math.toRadians(45), 1F);
		}
		progressRotationPrev(knee_left, sitProgress, (float)Math.toRadians(65), 0, 0, 5F);
		progressRotationPrev(knee_right, sitProgress, (float)Math.toRadians(65), 0, 0, 5F);
		progressRotationPrev(foot_left, sitProgress, (float)Math.toRadians(-65), 0, 0, 5F);
		progressRotationPrev(foot_right, sitProgress, (float)Math.toRadians(-65), 0, 0, 5F);
		progressRotationPrev(arm_left, sitProgress, (float)Math.toRadians(-15), 0, 0, 5F);
		progressRotationPrev(arm_right, sitProgress, (float)Math.toRadians(-15), 0, 0, 5F);
		progressPositionPrev(foot_left, sitProgress, 0, -1F, 0.7F, 5F);
		progressPositionPrev(foot_right, sitProgress, 0, -1F, 0.7F, 5F);
		progressPositionPrev(body, sitProgress, 0, 7F, 0F, 5F);
		progressPositionPrev(arm_right, sitProgress, 0, -4.5F, 2F, 5F);
		progressPositionPrev(arm_left, sitProgress, 0, -4.5F, 2F, 5F);
		progressRotationPrev(body, stillProgress, (float)Math.toRadians(-35), 0, 0, 5F);
		progressRotationPrev(leg_left, stillProgress, (float)Math.toRadians(35), 0, 0, 5F);
		progressRotationPrev(leg_right, stillProgress, (float)Math.toRadians(35), 0, 0, 5F);
		progressRotationPrev(chest, stillProgress, (float)Math.toRadians(-10), 0, 0, 5F);
		progressRotationPrev(arm_left, stillProgress, (float)Math.toRadians(20), 0, 0, 5F);
		progressRotationPrev(arm_right, stillProgress, (float)Math.toRadians(20), 0, 0, 5F);
		progressRotationPrev(neck, stillProgress, (float)Math.toRadians(35), 0, 0, 5F);
		progressRotationPrev(tail1, stillProgress, (float)Math.toRadians(25), 0, 0, 5F);
		progressRotationPrev(tail2, stillProgress, (float)Math.toRadians(25), 0, 0, 5F);
		progressPositionPrev(tail1, stillProgress, 0, 0F, -2F, 5F);
		progressPositionPrev(pouch, pouchOpenProgress, 0, 3F, 0F, 5F);

		this.walk(arm_left, idleSpeed, idleDegree * 1.1F, true, 2F, 0F, ageInTicks, 1);
		this.walk(arm_right, idleSpeed, idleDegree * 1.1F, true, 2F, 0F, ageInTicks, 1);
		this.walk(chest, idleSpeed, idleDegree * 0.4F, true, 0F, -0.1F, ageInTicks, 1);
		this.walk(neck, idleSpeed, idleDegree * 0.4F, true, 1F, 0.1F, ageInTicks, 1);
		this.walk(tail1, idleSpeed, idleDegree * 1.1F, false, 2F, 0F, ageInTicks, 1);
		this.walk(tail2, idleSpeed, idleDegree * 1.1F, false, 1F, 0F, ageInTicks, 1);
		this.flap(ear_right, idleSpeed, idleDegree * -1.5F, false, 1F, -0.1F, ageInTicks, 1);
		this.flap(ear_left, idleSpeed, idleDegree * 1.5F, false, 1F, 0.1F, ageInTicks, 1);

		this.body.rotationPointY -= (jumpRotation * 4.0F);
		this.knee_left.rotationPointY -= (jumpRotation * 2F);
		this.knee_right.rotationPointY -= (jumpRotation * 2F);
		this.leg_right.rotationPointY -= (jumpRotation * 2F);
		this.leg_left.rotationPointY -= (jumpRotation * 2F);
		this.leg_right.rotationPointZ += (jumpRotation * 2F);
		this.leg_left.rotationPointZ += (jumpRotation * 2F);
		this.head.rotationPointY += (jumpRotation * 1F);
		this.leg_left.rotateAngleX += (jumpRotation * 50.0F) * 0.017453292F;
		this.leg_right.rotateAngleX += (jumpRotation * 50.0F) * 0.017453292F;
		this.foot_left.rotateAngleX += (jumpRotation * 25.0F) * 0.017453292F;
		this.foot_right.rotateAngleX += (jumpRotation * 25.0F) * 0.017453292F;
		this.knee_left.rotateAngleX += (jumpRotation * -25.0F) * 0.017453292F;
		this.knee_right.rotateAngleX += (jumpRotation * -25.0F) * 0.017453292F;
		this.neck.rotateAngleX += (jumpRotation * 15.0F) * 0.017453292F;
		this.head.rotateAngleX += (jumpRotation * -10.0F) * 0.017453292F;
		this.body.rotateAngleX += (jumpRotation * 10.0F) * 0.017453292F;
		this.arm_left.rotateAngleX += (jumpRotation * 20.0F) * 0.017453292F;
		this.arm_right.rotateAngleX += (jumpRotation * 20.0F) * 0.017453292F;
		this.chest.rotateAngleX += (jumpRotation * -5.0F) * 0.017453292F;

		this.foot_left.rotateAngleX += (Math.max(0, jumpRotation - 0.5F) * 25.0F) * 0.017453292F;
		this.foot_right.rotateAngleX += (Math.max(0, jumpRotation - 0.5F) * 25.0F) * 0.017453292F;
		ItemStack helmet = entity.getItemStackFromSlot(EquipmentSlotType.HEAD);
		ItemStack hand = entity.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if(!helmet.isEmpty()){
			this.ear_left.rotateAngleZ += 75 * 0.017453292F;
			this.ear_right.rotateAngleZ += -75 * 0.017453292F;
		}
		if(!hand.isEmpty()){
			if(entity.isLeftHanded()){
				this.arm_left.rotateAngleX -= 25 * 0.017453292F;
			}else{
				this.arm_right.rotateAngleX -= 25 * 0.017453292F;
			}
		}
		this.head.rotateAngleY += netHeadYaw * 0.35F * ((float)Math.PI / 180F);
		this.head.rotateAngleX += headPitch * 0.65F * ((float)Math.PI / 180F);
		this.neck.rotateAngleY += netHeadYaw * 0.15F * ((float)Math.PI / 180F);
		if(entity.isChild() && entity.isPassenger() && entity.getRidingEntity() instanceof EntityKangaroo) {
			this.head.rotateAngleX -= 50 * 0.017453292F;
			this.neck.rotateAngleX += 120 * 0.017453292F;
			progressPositionPrev(head, 1F, 0, 0F, -2F, 1F);
		}
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.isChild) {
			float f = 1.65F;
			head.setScale(f, f, f);
			head.setShouldScaleChildren(true);
			matrixStackIn.push();
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.translate(0.0D, 1.5D, 0D);
			if(renderOnlyHead){
				neck.setRotationPoint(0.0F, 0F, 0.0F);
				this.neck.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			}else{
				neck.setRotationPoint(0.0F, 2.0F, -8.0F);
				getParts().forEach((p_228292_8_) -> {
					p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				});
			}
			matrixStackIn.pop();
			head.setScale(1, 1, 1);
		} else {
			matrixStackIn.push();
			getParts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.pop();
		}
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, arm_left, arm_right, neck, head, ear_left, ear_right, snout, leg_left, leg_right, knee_left, knee_right, foot_left, foot_right, pouch, tail1, tail2, chest);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}