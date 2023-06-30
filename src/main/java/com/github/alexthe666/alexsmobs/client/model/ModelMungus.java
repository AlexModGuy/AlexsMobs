package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ModelMungus extends AdvancedEntityModel<EntityMungus> {
	public final AdvancedModelBox root;
	public final AdvancedModelBox body;
	public final AdvancedModelBox hair;
	public final AdvancedModelBox eye;
	public final AdvancedModelBox leg_left;
	public final AdvancedModelBox leg_right;
	public final AdvancedModelBox nose;
	public final AdvancedModelBox sack;

	public ModelMungus(float f) {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this, "body");
		body.setPos(0.0F, -7.0F, 0.0F);
		root.addChild(body);
		body.setTextureOffset(0, 0).addBox(-6.0F, -16.0F, -4.0F, 12.0F, 16.0F, 8.0F, f, false);

		hair = new AdvancedModelBox(this, "hair");
		hair.setPos(0.0F, -16.0F, 0.0F);
		body.addChild(hair);
		hair.setTextureOffset(33, 0).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 5.0F, 0.0F, f, false);

		eye = new AdvancedModelBox(this, "eye");
		eye.setPos(0.0F, -11.0F, -4.1F);
		body.addChild(eye);
		eye.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 1.0F, f, false);

		leg_left = new AdvancedModelBox(this, "leg_left");
		leg_left.setPos(3.0F, 0.0F, 0.0F);
		body.addChild(leg_left);
		leg_left.setTextureOffset(0, 39).addBox(-2.0F, 0.0F, -3.0F, 5.0F, 7.0F, 6.0F, f, false);

		leg_right = new AdvancedModelBox(this, "leg_right");
		leg_right.setPos(-3.0F, 0.0F, 0.0F);
		body.addChild(leg_right);
		leg_right.setTextureOffset(0, 25).addBox(-3.0F, 0.0F, -3.0F, 5.0F, 7.0F, 6.0F, f, false);

		nose = new AdvancedModelBox(this, "nose");
		nose.setPos(0.0F, -9.0F, -4.0F);
		body.addChild(nose);
		nose.setTextureOffset(35, 43).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 2.0F, f, false);

		sack = new AdvancedModelBox(this, "sack");
		sack.setPos(0.0F, -7.0F, 4.0F);
		body.addChild(sack);
		sack.setTextureOffset(23, 25).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 10.0F, 3.0F, f, false);
		this.updateDefaultPose();
	}

	@Override
	public void setupAnim(EntityMungus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float walkSpeed = 0.7F;
		float walkDegree = 0.6F;
		float idleSpeed = 0.1F;
		float idleDegree = 0.1F;
		float swell = Math.min(entity.prevSwellProgress + (entity.swellProgress - entity.prevSwellProgress) * Minecraft.getInstance().getFrameTime(), 10F);
		float glowyBob = (swell * 0.22F) + 0.95F + (Mth.cos(ageInTicks * (0.1F + swell * 0.2F)) + 1F) * (0.05F + swell * 0.02F);
		BlockPos targetPos = entity.getBeamTarget();
		if(targetPos == null) {
			Entity look = Minecraft.getInstance().getCameraEntity();
			if (look != null) {
				Vec3 vector3d = look.getEyePosition(0.0F);
				Vec3 vector3d1 = entity.getEyePosition(0.0F);
				double d0 = vector3d.y - vector3d1.y;
				if (d0 > 0.0D) {
					this.eye.rotationPointY = -11.0F;
				} else {
					this.eye.rotationPointY = -10.0F;
				}

				Vec3 vector3d2 = entity.getViewVector(0.0F);
				vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
				Vec3 vector3d3 = (new Vec3(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot((Mth.PI / 2F));
				double d1 = vector3d2.dot(vector3d3);
				this.eye.rotationPointX += Mth.sqrt((float) Math.abs(d1)) * 2.0F * (float) Math.signum(d1);
			}
		}else{
			Vec3 vector3d = Vec3.atCenterOf(targetPos);
			Vec3 vector3d1 = entity.getEyePosition(0.0F);
			double d0 = vector3d.y - vector3d1.y;
			if (d0 > 0.0D) {
				this.eye.rotationPointY = -11.0F;
			} else {
				this.eye.rotationPointY = -10.0F;
			}

			Vec3 vector3d2 = entity.getViewVector(0.0F);
			vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
			Vec3 vector3d3 = (new Vec3(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot((Mth.PI / 2F));
			double d1 = vector3d2.dot(vector3d3);
			this.eye.rotationPointX += Mth.sqrt((float) Math.abs(d1)) * 2.0F * (float) Math.signum(d1);

		}
		this.walk(hair, idleSpeed, idleDegree, false, 1F, -0.1F, ageInTicks, 1);
		this.flap(nose, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1);
		sack.setScale(glowyBob, glowyBob, glowyBob + swell * 0.2F);
		this.sack.rotationPointZ += swell * 0.02F;
		progressRotationPrev(hair, limbSwingAmount, Maths.rad(-23), 0, 0, 1F);
		this.walk(leg_right, walkSpeed, walkDegree * 1.1F, true, 1, 0F, limbSwing, limbSwingAmount);
		this.bob(leg_right, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.walk(leg_left, walkSpeed, walkDegree * 1.1F, false, 1, 0F, limbSwing, limbSwingAmount);
		this.bob(leg_left, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
		this.flap(body, walkSpeed, walkDegree * 0.4F, false, 0.5F, 0, limbSwing, limbSwingAmount);
		this.flap(nose, walkSpeed, walkDegree * 0.2F, false, 1F, 0, limbSwing, limbSwingAmount);
		this.bob(body, walkSpeed, walkDegree * 3F, true, limbSwing, limbSwingAmount);

	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, hair, eye, leg_left, leg_right, sack, nose);
	}

	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.young) {
			this.eye.setScale(1.5F, 1.5F, 1.5F);
			this.nose.setScale(1.5F, 1.5F, 1.5F);
			matrixStackIn.pushPose();
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.translate(0.0D, 1.5D, 0.125D);
			parts().forEach((p_228292_8_) -> {
				p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
		} else {
			this.eye.setScale(1F, 1F, 1F);
			this.nose.setScale(1F, 1F, 1F);
			matrixStackIn.pushPose();
			parts().forEach((p_228290_8_) -> {
				p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.popPose();
		}

	}

	public void renderShoes(){
		this.leg_left.setScale(1.3F, 1.3F, 1.3F);
		this.leg_right.setScale(1.3F, 1.3F, 1.3F);
	}

	public void postRenderShoes(){
		this.leg_left.setScale(1F, 1F, 1F);
		this.leg_right.setScale(1F, 1F, 1F);
	}

	public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
		AdvancedModelBox.rotateAngleX = x;
		AdvancedModelBox.rotateAngleY = y;
		AdvancedModelBox.rotateAngleZ = z;
	}
}