package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntitySkunk;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelSkunk extends AdvancedEntityModel<EntitySkunk> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftLeg;
    private final AdvancedModelBox rightLeg;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox head;

    public ModelSkunk() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this,"body");
        body.setRotationPoint(0.0F, -3.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-3.5F, -4.0F, -4.5F, 7.0F, 6.0F, 9.0F, 0.0F, false);

        leftLeg = new AdvancedModelBox(this, "leftLeg");
        leftLeg.setRotationPoint(4.0F, 2.0F, 4.0F);
        body.addChild(leftLeg);
        setRotationAngle(leftLeg, 0.0F, -0.7418F, 0.0F);
        leftLeg.setTextureOffset(0, 33).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this, "rightLeg");
        rightLeg.setRotationPoint(-4.0F, 2.0F, 4.0F);
        body.addChild(rightLeg);
        setRotationAngle(rightLeg, 0.0F, 0.7418F, 0.0F);
        rightLeg.setTextureOffset(0, 33).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 4.0F, 0.0F, true);

        leftArm = new AdvancedModelBox(this, "leftArm");
        leftArm.setRotationPoint(3.5F, 2.0F, -3.0F);
        body.addChild(leftArm);
        setRotationAngle(leftArm, 0.0F, -0.5672F, 0.0F);
        leftArm.setTextureOffset(32, 31).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this, "rightArm");
        rightArm.setRotationPoint(-3.5F, 2.0F, -3.0F);
        body.addChild(rightArm);
        setRotationAngle(rightArm, 0.0F, 0.5672F, 0.0F);
        rightArm.setTextureOffset(32, 31).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 4.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -1.0F, 4.5F);
        body.addChild(tail);
        tail.setTextureOffset(0, 16).addBox(-3.0F, -10.0F, 0.0F, 6.0F, 12.0F, 4.0F, 0.0F, false);
        tail.setTextureOffset(21, 16).addBox(-3.0F, -10.0F, 4.0F, 6.0F, 7.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, 0.0F, -5.5F);
        body.addChild(head);
        head.setTextureOffset(24, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(21, 29).addBox(-2.0F, 0.0F, -6.0F, 4.0F, 2.0F, 3.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntitySkunk entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float idleSpeed =  0.1F;
        float idleDegree = 0.15f;
        float walkSpeed =  1.25F;
        float walkDegree = 0.5f;
        float partialTicks = ageInTicks - entity.tickCount;
        float sprayProgress = entity.prevSprayProgress + (entity.sprayProgress - entity.prevSprayProgress) * partialTicks;
        float legsStill = Math.max(sprayProgress * 0.2F, limbSwingAmount);
        progressRotationPrev(leftArm, sprayProgress, Maths.rad(80F), 0, 0, 5F);
        progressRotationPrev(rightArm, sprayProgress, Maths.rad(80F), 0, 0, 5F);
        progressRotationPrev(leftLeg, sprayProgress, Maths.rad(100F), 0, 0, 5F);
        progressRotationPrev(rightLeg, sprayProgress, Maths.rad(100F), 0, 0, 5F);
        progressRotationPrev(tail, sprayProgress, Maths.rad(30F), 0, 0, 5F);
        progressPositionPrev(body, sprayProgress, 0, -2.4F, 0, 5F);
        progressPositionPrev(tail, sprayProgress, 0, -2F, -1F, 5F);
        this.walk(body, 0.5F, 0.2F, true, 4F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.swing(body, 0.5F, 0.2F, true, 1.5F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.walk(head, 0.5F, 0.2F, false, 4F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.swing(head, 0.5F, 0.2F, false, 1.5F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.walk(leftArm, 0.5F, 0.2F, false, 4F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.swing(leftArm, 0.5F, 0.2F, false, 1.5F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.walk(rightArm, 0.5F, 0.2F, false, 4F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.swing(rightArm, 0.5F, 0.2F, false, 1.5F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.walk(leftLeg, 0.5F, 0.2F, false, 4F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.swing(leftLeg, 0.5F, 0.2F, false, 1.5F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.walk(rightLeg, 0.5F, 0.2F, false, 4F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.swing(rightLeg, 0.5F, 0.2F, false, 1.5F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.flap(tail, 0.5F, 0.5F, false, 2.5F, 0F, ageInTicks, sprayProgress * 0.2F);
        this.walk(tail, idleSpeed, idleDegree, false, 1F, 0F, ageInTicks, 1);
        progressRotationPrev(leftArm, Math.min(legsStill, 0.5F), 0, Maths.rad(30F), 0, 0.5F);
        progressRotationPrev(rightArm, Math.min(legsStill, 0.5F), 0, Maths.rad(-30F), 0, 0.5F);
        progressRotationPrev(leftLeg, Math.min(legsStill, 0.5F), 0, Maths.rad(40F), 0, 0.5F);
        progressRotationPrev(rightLeg, Math.min(legsStill, 0.5F), 0, Maths.rad(-40F), 0, 0.5F);
        progressPositionPrev(head, Math.min(legsStill, 0.5F), 0, -1F, 0, 0.5F);
        this.swing(body, walkSpeed, walkDegree * 0.5F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.swing(head, walkSpeed, walkDegree * 0.5F, true, 2F, 0F, limbSwing, limbSwingAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.5F, false, 4F, 0F, limbSwing, limbSwingAmount);
        this.walk(tail, walkSpeed, walkDegree * 0.2F, true, 2F, 0.3F, limbSwing, limbSwingAmount);
        this.walk(leftArm, walkSpeed, walkDegree * 1.2F, true, -2.5F, -0.2F, limbSwing, limbSwingAmount);
        this.walk(rightArm, walkSpeed, walkDegree * 1.2F, false, -2.5F, 0.2F, limbSwing, limbSwingAmount);
        this.walk(rightLeg, walkSpeed, walkDegree * 1.2F, true, -2.5F, -0.2F, limbSwing, limbSwingAmount);
        this.walk(leftLeg, walkSpeed, walkDegree * 1.2F, false, -2.5F, 0.2F, limbSwing, limbSwingAmount);
        this.flap(body, walkSpeed, walkSpeed * 0.3F, false, -1, 0, limbSwing, limbSwingAmount);
        this.flap(rightLeg, walkSpeed, walkSpeed * 0.3F, true, -1, 0, limbSwing, limbSwingAmount);
        this.flap(leftLeg, walkSpeed, walkSpeed * 0.3F, true, -1, 0, limbSwing, limbSwingAmount);
        this.flap(rightArm, walkSpeed, walkSpeed * 0.3F, true, -1, 0, limbSwing, limbSwingAmount);
        this.flap(leftArm, walkSpeed, walkSpeed * 0.3F, true, -1, 0, limbSwing, limbSwingAmount);
        this.flap(head, walkSpeed, walkSpeed * 0.3F, true, -1, 0, limbSwing, limbSwingAmount);
        this.flap(tail, walkSpeed, walkSpeed * 0.2F, true, -1, 0, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 1.2F, head);
        float leftLegS = (float) (Math.sin((double) (limbSwing * walkSpeed) - 2.5F) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));
        float rightLegS = (float) (Math.sin(-(double) (limbSwing * walkSpeed) + 2.5F) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));
        this.rightArm.rotationPointY += 3 * leftLegS;
        this.leftArm.rotationPointY += 3 * rightLegS;
        this.rightArm.rotationPointZ += 1F * leftLegS;
        this.leftArm.rotationPointZ += 1F * rightLegS;
        this.leftLeg.rotationPointY += 3 * leftLegS;
        this.rightLeg.rotationPointY += 3 * rightLegS;
        this.leftLeg.rotationPointZ += 1F * leftLegS;
        this.rightLeg.rotationPointZ += 1F * rightLegS;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leftArm, rightArm, leftLeg, rightLeg, tail, head);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            this.head.setScale(1.5F, 1.5F, 1.5F);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.815D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        } else {
            this.head.setScale(1F, 1F, 1F);
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }
}