package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelSugarGlider extends AdvancedEntityModel<EntitySugarGlider> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox leftLeg;
    private final AdvancedModelBox rightLeg;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox head;
    private final AdvancedModelBox leftEar;
    private final AdvancedModelBox rightEar;

    public ModelSugarGlider() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -2.0F, -1.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 3.0F, 7.0F, 0.0F, false);

        leftArm = new AdvancedModelBox(this, "leftArm");
        leftArm.setRotationPoint(1.0F, 1.0F, -3.0F);
        body.addChild(leftArm);
        setRotationAngle(leftArm, 0.0F, 0.0F, 0.20944F);
        leftArm.setTextureOffset(12, 11).addBox(-1.0F, 0.0F, -2.0F, 6.0F, 0.0F, 6.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this, "rightArm");
        rightArm.setRotationPoint(-1.0F, 1.0F, -3.0F);
        body.addChild(rightArm);
        setRotationAngle(rightArm, 0.0F, 0.0F, -0.20944F);
        rightArm.setTextureOffset(12, 11).addBox(-5.0F, 0.0F, -2.0F, 6.0F, 0.0F, 6.0F, 0.0F, true);

        leftLeg = new AdvancedModelBox(this, "leftLeg");
        leftLeg.setRotationPoint(1.0F, 1.0F, 3.0F);
        body.addChild(leftLeg);
        setRotationAngle(leftLeg, 0.0F, 0.0F, 0.20944F);
        leftLeg.setTextureOffset(15, 0).addBox(-1.0F, 0.0F, -2.0F, 6.0F, 0.0F, 5.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this, "rightLeg");
        rightLeg.setRotationPoint(-1.0F, 1.0F, 3.0F);
        body.addChild(rightLeg);
        setRotationAngle(rightLeg, 0.0F, 0.0F, -0.20944F);
        rightLeg.setTextureOffset(15, 0).addBox(-5.0F, 0.0F, -2.0F, 6.0F, 0.0F, 5.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, 0.0F, 4.0F);
        body.addChild(tail);
        tail.setTextureOffset(0, 11).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, 0.0F, -3.0F);
        body.addChild(head);
        head.setTextureOffset(17, 18).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(0, 22).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);

        leftEar = new AdvancedModelBox(this, "leftEar");
        leftEar.setRotationPoint(2.2F, -1.6F, -2.9F);
        head.addChild(leftEar);
        setRotationAngle(leftEar, 0.0F, -0.6109F, 0.0F);
        leftEar.setTextureOffset(0, 0).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        rightEar = new AdvancedModelBox(this, "rightEar");
        rightEar.setRotationPoint(-2.2F, -1.6F, -2.9F);
        head.addChild(rightEar);
        setRotationAngle(rightEar, 0.0F, 0.6109F, 0.0F);
        rightEar.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, leftArm, rightArm, leftEar, rightEar, tail, leftLeg, rightLeg);
    }

    @Override
    public void setupAnim(EntitySugarGlider entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.1F;
        float idleDegree = 0.25F;
        float walkSpeed = 0.9F;
        float walkDegree = 0.5F;
        float glideSpeed = 1.3F;
        float glideDegree = 0.6F;
        float partialTick = ageInTicks - entityIn.tickCount;
        float glideProgress = entityIn.prevGlideProgress + (entityIn.glideProgress - entityIn.prevGlideProgress) * partialTick;
        float sitProgress = entityIn.prevSitProgress + (entityIn.sitProgress - entityIn.prevSitProgress) * partialTick;
        float forageProgress = entityIn.forageProgress + (entityIn.forageProgress - entityIn.prevForageProgress) * partialTick;
        float glideSwingAmount = glideProgress * 0.2F;
        float walkSwingAmount = (1f - glideSwingAmount) * limbSwingAmount;
        progressRotationPrev(body, glideProgress, (float)Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(tail, glideProgress, (float)Math.toRadians(12), 0, 0, 5F);
        progressRotationPrev(head, glideProgress, (float)Math.toRadians(12), 0, 0, 5F);
        progressRotationPrev(leftArm, glideProgress, 0, 0, (float)Math.toRadians(-20), 5F);
        progressRotationPrev(leftLeg, glideProgress, 0, 0, (float)Math.toRadians(-20), 5F);
        progressRotationPrev(rightArm, glideProgress, 0, 0, (float)Math.toRadians(20), 5F);
        progressRotationPrev(rightLeg, glideProgress, 0, 0, (float)Math.toRadians(20), 5F);
        progressPositionPrev(body, glideProgress, 0, -2, 2, 5F);
        progressPositionPrev(leftArm, glideProgress, 2, 0, 0, 5F);
        progressPositionPrev(rightArm, glideProgress, -2, 0, 0, 5F);
        progressPositionPrev(leftLeg, glideProgress, 2, 0, 0, 5F);
        progressPositionPrev(rightLeg, glideProgress, -2, 0, 0, 5F);
        progressRotationPrev(head, forageProgress, (float)Math.toRadians(35), 0, 0, 5F);
        progressRotationPrev(tail, forageProgress, (float)Math.toRadians(10), 0, 0, 5F);
        progressPositionPrev(head, forageProgress, 0, -1, 1, 5F);
        progressRotationPrev(body, sitProgress, (float)Math.toRadians(-170), 0, 0, 5F);
        progressRotationPrev(tail, sitProgress, (float)Math.toRadians(-50), 0, 0, 5F);
        progressRotationPrev(head, sitProgress, (float)Math.toRadians(150), 0, 0, 5F);
        progressRotationPrev(leftArm, sitProgress, 0, 0, (float)Math.toRadians(20), 5F);
        progressRotationPrev(leftLeg, sitProgress, 0, 0, (float)Math.toRadians(20), 5F);
        progressRotationPrev(rightArm, sitProgress, 0, 0, (float)Math.toRadians(-20), 5F);
        progressRotationPrev(rightLeg, sitProgress, 0, 0, (float)Math.toRadians(-20), 5F);
        progressPositionPrev(body, sitProgress, 0, 1, 1, 5F);
        progressPositionPrev(head, sitProgress, 0, 2, -2, 5F);
        this.flap(rightEar, idleSpeed, idleDegree, false, 0F, -0.05F, ageInTicks, 1);
        this.flap(leftEar, idleSpeed, idleDegree, true, 0F, -0.05F, ageInTicks, 1);
        this.swing(leftArm, walkSpeed, walkDegree, false, 1.5F, -0.2F, limbSwing, walkSwingAmount);
        this.swing(leftLeg, walkSpeed, walkDegree, true, 1.5F, -0.2F, limbSwing, walkSwingAmount);
        this.swing(rightArm, walkSpeed, walkDegree, false, 1.5F, -0.2F, limbSwing, walkSwingAmount);
        this.swing(rightLeg, walkSpeed, walkDegree, true, 1.5F, -0.2F, limbSwing, walkSwingAmount);
        this.swing(tail, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, walkSwingAmount);
        this.bob(head, walkSpeed * 0.5F, walkDegree, true, limbSwing, walkSwingAmount);
        this.flap(leftArm, glideSpeed, glideDegree * 0.1F, true, 0F, -0.05F, ageInTicks, glideSwingAmount);
        this.flap(leftLeg, glideSpeed, glideDegree * 0.1F, true, 0F, -0.05F, ageInTicks, glideSwingAmount);
        this.flap(rightArm, glideSpeed, glideDegree * 0.1F, false, 0F, 0.05F, ageInTicks, glideSwingAmount);
        this.flap(rightLeg, glideSpeed, glideDegree * 0.1F, false, 0F, 0.05F, ageInTicks, glideSwingAmount);
        this.swing(head, glideSpeed * 0.2F, glideDegree * 0.4F, false, 0F, 0F, ageInTicks, glideSwingAmount);
        this.swing(body, glideSpeed * 0.2F, glideDegree * 0.4F, true, 1F, 0F, ageInTicks, glideSwingAmount);
        this.swing(tail, glideSpeed * 0.2F, glideDegree, true, -1F, 0F, ageInTicks, glideSwingAmount);
        this.bob(head, 1F, 0.6F, false, ageInTicks, forageProgress * 0.2F);
        this.swing(head, 0.5F, 0.6F, true, -1F, 0F, ageInTicks, forageProgress * 0.2F);
        if(forageProgress == 0){
            this.faceTarget(netHeadYaw, headPitch, 1.2F, head);
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        if (this.young) {
            float f = 1.35F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            this.head.setScale(1F, 1F, 1F);
        } else {
            this.head.setScale(1F, 1F, 1F);
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
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

