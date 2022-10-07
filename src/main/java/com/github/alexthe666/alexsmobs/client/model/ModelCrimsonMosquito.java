package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelCrimsonMosquito extends AdvancedEntityModel<EntityCrimsonMosquito> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox body;
	private final AdvancedModelBox wingL;
	private final AdvancedModelBox wingR;
	private final AdvancedModelBox legsL;
	private final AdvancedModelBox legL1;
	private final AdvancedModelBox legL2;
	private final AdvancedModelBox legL3;
	private final AdvancedModelBox legsR;
	private final AdvancedModelBox legR1;
	private final AdvancedModelBox legR2;
	private final AdvancedModelBox legR3;
	private final AdvancedModelBox tail;
	private final AdvancedModelBox head;
	private final AdvancedModelBox antennaL;
	private final AdvancedModelBox antennaR;
	private final AdvancedModelBox mouth;
	private ModelAnimator animator;

	public ModelCrimsonMosquito() {
		texWidth = 128;
		texHeight = 128;

		root = new AdvancedModelBox(this, "root");
		root.setPos(0.0F, 24.0F, 0.0F);
		

		body = new AdvancedModelBox(this, "body");
		body.setPos(0.0F, -9.5F, -0.25F);
		root.addChild(body);
		body.setTextureOffset(31, 65).addBox(-3.0F, -3.5F, -3.75F, 6.0F, 6.0F, 6.0F, 0.0F, false);

		wingL = new AdvancedModelBox(this, "wingL");
		wingL.setPos(2.7F, -3.2F, -0.75F);
		body.addChild(wingL);
		wingL.setTextureOffset(37, 59).addBox(0.0F, 0.0F, -1.0F, 18.0F, 0.0F, 5.0F, 0.0F, false);

		wingR = new AdvancedModelBox(this, "wingR");
		wingR.setPos(-2.7F, -3.2F, -0.75F);
		body.addChild(wingR);
		wingR.setTextureOffset(37, 53).addBox(-18.0F, 0.0F, -1.0F, 18.0F, 0.0F, 5.0F, 0.0F, false);

		legsL = new AdvancedModelBox(this, "legsL");
		legsL.setPos(3.0F, 2.5F, -2.75F);
		body.addChild(legsL);
		

		legL1 = new AdvancedModelBox(this, "legL1");
		legL1.setPos(0.0F, 0.0F, 0.0F);
		legsL.addChild(legL1);
		setRotationAngle(legL1, 0.0F, 0.5236F, 0.0F);
		legL1.setTextureOffset(0, 51).addBox(0.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);

		legL2 = new AdvancedModelBox(this, "legL2");
		legL2.setPos(0.0F, 0.0F, 0.4F);
		legsL.addChild(legL2);
		legL2.setTextureOffset(37, 16).addBox(0.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);

		legL3 = new AdvancedModelBox(this, "legL3");
		legL3.setPos(0.0F, 0.0F, 0.9F);
		legsL.addChild(legL3);
		setRotationAngle(legL3, 0.0F, -0.8727F, 0.0F);
		legL3.setTextureOffset(37, 0).addBox(0.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);

		legsR = new AdvancedModelBox(this, "legsR");
		legsR.setPos(-3.0F, 2.5F, -2.75F);
		body.addChild(legsR);
		

		legR1 = new AdvancedModelBox(this, "legR1");
		legR1.setPos(0.0F, 0.0F, 0.0F);
		legsR.addChild(legR1);
		setRotationAngle(legR1, 0.0F, -0.5236F, 0.0F);
		legR1.setTextureOffset(37, 37).addBox(-18.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);

		legR2 = new AdvancedModelBox(this, "legR2");
		legR2.setPos(0.0F, 0.0F, 0.4F);
		legsR.addChild(legR2);
		legR2.setTextureOffset(0, 35).addBox(-18.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);

		legR3 = new AdvancedModelBox(this, "legR3");
		legR3.setPos(0.0F, 0.0F, 0.9F);
		legsR.addChild(legR3);
		setRotationAngle(legR3, 0.0F, 0.8727F, 0.0F);
		legR3.setTextureOffset(0, 19).addBox(-18.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);

		tail = new AdvancedModelBox(this, "tail");
		tail.setPos(0.0F, -1.5F, 2.25F);
		body.addChild(tail);
		tail.setTextureOffset(48, 83).addBox(-2.0F, -1.4F, 0.0F, 4.0F, 4.0F, 16.0F, 0.0F, false);

		head = new AdvancedModelBox(this, "head");
		head.setPos(0.0F, 0.5F, -3.75F);
		body.addChild(head);
		head.setTextureOffset(56, 65).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

		antennaL = new AdvancedModelBox(this, "antennaL");
		antennaL.setPos(1.0F, -0.1F, -4.0F);
		head.addChild(antennaL);
		setRotationAngle(antennaL, 1.2217F, -0.48F, 0.0436F);
		antennaL.setTextureOffset(5, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F, false);

		antennaR = new AdvancedModelBox(this, "antennaR");
		antennaR.setPos(-1.0F, -0.1F, -4.0F);
		head.addChild(antennaR);
		setRotationAngle(antennaR, 1.2217F, 0.48F, -0.0436F);
		antennaR.setTextureOffset(0, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F, false);

		mouth = new AdvancedModelBox(this, "mouth");
		mouth.setPos(0.0F, 2.0F, -3.5F);
		head.addChild(mouth);
		setRotationAngle(mouth, -1.0036F, 0.0F, 0.0F);
		mouth.setTextureOffset(23, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 8.0F, 1.0F, 0.0F, false);
		animator = ModelAnimator.create();
		this.updateDefaultPose();
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public void setupAnim(EntityCrimsonMosquito entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float flySpeed = 0.5F;
		float flyDegree = 0.5F;

		float partialTick = Minecraft.getInstance().getFrameTime();
		float flyProgress = entityIn.prevFlyProgress + (entityIn.flyProgress - entityIn.prevFlyProgress) * partialTick;
		float shootProgress = entityIn.prevShootProgress + (entityIn.shootProgress - entityIn.prevShootProgress) * partialTick;
		this.walk(antennaR, flySpeed, flyDegree * 0.15F, false, 0, 0.1F, ageInTicks, 1);
		this.walk(antennaL, flySpeed, flyDegree * 0.15F, false, 0, 0.1F, ageInTicks, 1);
		boolean flappingWings = flyProgress > 0 || entityIn.randomWingFlapTick > 0;
		progressRotationPrev(head, shootProgress,   (float) Math.toRadians(-10), 0, 0, 5F);
		progressRotationPrev(mouth, shootProgress,   (float) Math.toRadians(-20), 0, 0, 5F);
		if(entityIn.isPassenger()){
			progressRotationPrev(body, 5F,   (float) Math.toRadians(-90), (float) Math.toRadians(180), 0, 5F);
			progressRotationPrev(head, 5F,   (float) Math.toRadians(40), 0, 0, 5F);
			progressRotationPrev(mouth, 5F,   (float) Math.toRadians(10), 0, 0, 5F);
			float legRot = 50;
			progressRotationPrev(legR1, 5F,   0, 0, (float) Math.toRadians(-legRot), 5F);
			progressRotationPrev(legR2, 5F,   0, 0, (float) Math.toRadians(-legRot), 5F);
			progressRotationPrev(legR3, 5F,   0, 0, (float) Math.toRadians(-legRot), 5F);
			progressRotationPrev(legL1, 5F,   0, 0, (float) Math.toRadians(legRot), 5F);
			progressRotationPrev(legL2, 5F,   0, 0, (float) Math.toRadians(legRot), 5F);
			progressRotationPrev(legL3, 5F,   0, 0, (float) Math.toRadians(legRot), 5F);
			this.mouth.setScale(1F, (float) (0.85F + Math.sin(ageInTicks) * 0.15F), 1F);
		}else{
			this.mouth.setScale(1F, 1F, 1F);
		}
		if(shootProgress > 0){
			this.mouth.setScale(1F + shootProgress * 0.1F, 1F - shootProgress * 0.1F, 1F + shootProgress * 0.1F);
		}
		if(flappingWings){
			this.flap(wingL, flySpeed * 3.3F, flyDegree, true, 0, 0.2F, ageInTicks, 1);
			this.flap(wingR, flySpeed * 3.3F, flyDegree, false, 0, 0.2F, ageInTicks, 1);
		}else{
			this.wingR.rotateAngleX = (float) Math.toRadians(30);
			this.wingR.rotateAngleY = (float) Math.toRadians(70);
			this.wingL.rotateAngleX = (float) Math.toRadians(30);
			this.wingL.rotateAngleY = (float) Math.toRadians(-70);
		}
		if(flyProgress > 0){
			progressPositionPrev(body, flyProgress, 0, -10F, 0F, 5F);
			progressRotationPrev(legL1, flyProgress,  0, (float) Math.toRadians(-30), (float) Math.toRadians(60), 5F);
			progressRotationPrev(legR1, flyProgress, 0, (float) Math.toRadians(30), (float) Math.toRadians(-60), 5F);
			progressRotationPrev(legL2, flyProgress, 0, (float) Math.toRadians(-20), (float) Math.toRadians(60), 5F);
			progressRotationPrev(legR2, flyProgress, 0, (float) Math.toRadians(20), (float) Math.toRadians(-60), 5F);
			progressRotationPrev(legL3, flyProgress, 0, (float) Math.toRadians(-5), (float) Math.toRadians(60), 5F);
			progressRotationPrev(legR3, flyProgress, 0, (float) Math.toRadians(5), (float) Math.toRadians(-60), 5F);
			this.bob(body, flySpeed * 0.5F, flyDegree * 5, false, ageInTicks, 1);
			this.flap(legL1, flySpeed, flyDegree * 0.5F, true, 1, 0.1F, ageInTicks, 1);
			this.flap(legR1, flySpeed, flyDegree * 0.5F, false, 1, 0.1F, ageInTicks, 1);
			this.flap(legL2, flySpeed, flyDegree * 0.5F, true, 2, 0.1F, ageInTicks, 1);
			this.flap(legR2, flySpeed, flyDegree * 0.5F, false, 2, 0.1F, ageInTicks, 1);
			this.flap(legL3, flySpeed, flyDegree * 0.5F, true, 2, 0.1F, ageInTicks, 1);
			this.flap(legR3, flySpeed, flyDegree * 0.5F, false, 2, 0.1F, ageInTicks, 1);
			this.walk(tail, flySpeed, flyDegree * 0.15F, false, 0, -0.1F, ageInTicks, 1);
		}
		float bloatScale = 1F + entityIn.getBloodLevel() * 0.1F;
		this.tail.rotateAngleX -= entityIn.getBloodLevel() * 0.05F;
		this.tail.setScale(bloatScale, bloatScale, bloatScale);
	}
		@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, body, wingL, wingR, legsL, legL1, legL2, legL3, legsR, legR1, legR2, legR3, tail, head, antennaL, antennaR, mouth);
	}

	public void setRotationAngle(AdvancedModelBox modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}