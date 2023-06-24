package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public class ModelJerboa extends AdvancedEntityModel<EntityJerboa> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftEar;
    private final AdvancedModelBox rightEar;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox legs;
    private final AdvancedModelBox feet;

    public ModelJerboa() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -7.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 19).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 8.0F, 0.0F, false);

        leftEar = new AdvancedModelBox(this, "leftEar");
        leftEar.setRotationPoint(2.0F, -1.0F, -2.0F);
        body.addChild(leftEar);
        setRotationAngle(leftEar, -0.6981F, -0.6545F, 1.0036F);
        leftEar.setTextureOffset(0, 0).addBox(-2.0F, -5.0F, 0.0F, 4.0F, 5.0F, 0.0F, 0.0F, false);

        rightEar = new AdvancedModelBox(this, "rightEar");
        rightEar.setRotationPoint(-2.0F, -1.0F, -2.0F);
        body.addChild(rightEar);
        setRotationAngle(rightEar, -0.6981F, 0.6545F, -1.0036F);
        rightEar.setTextureOffset(0, 0).addBox(-2.0F, -5.0F, 0.0F, 4.0F, 5.0F, 0.0F, 0.0F, true);

        leftArm = new AdvancedModelBox(this, "leftArm");
        leftArm.setRotationPoint(1.0F, 2.0F, -2.0F);
        body.addChild(leftArm);
        setRotationAngle(leftArm, 1.309F, 0.0F, 0.0F);
        leftArm.setTextureOffset(0, 6).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this, "rightArm");
        rightArm.setRotationPoint(-1.0F, 2.0F, -2.0F);
        body.addChild(rightArm);
        setRotationAngle(rightArm, 1.309F, 0.0F, 0.0F);
        rightArm.setTextureOffset(0, 6).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, 1.0F, 4.0F);
        body.addChild(tail);
        setRotationAngle(tail, -0.3491F, 0.0F, 0.0F);
        tail.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, 0.0F, 3.0F, 6.0F, 12.0F, 0.0F, false);

        legs = new AdvancedModelBox(this, "legs");
        legs.setRotationPoint(0.0F, 1.9F, 2.8F);
        body.addChild(legs);
        setRotationAngle(legs, 0.5236F, 0.0F, 0.0F);
        legs.setTextureOffset(19, 0).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 3.0F, 5.0F, 0.0F, false);

        feet = new AdvancedModelBox(this, "feet");
        feet.setRotationPoint(0.0F, 3.0F, -5.0F);
        legs.addChild(feet);
        setRotationAngle(feet, -0.5236F, 0.0F, 0.0F);
        feet.setTextureOffset(19, 9).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leftEar, rightEar, leftArm, rightArm, tail, legs, feet);
    }

    @Override
    public void setupAnim(EntityJerboa entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float idleSpeed = 0.1F;
        float idleDegree = 0.2F;
        float sleepProgress = entity.prevSleepProgress + (entity.sleepProgress - entity.prevSleepProgress) * partialTicks;
        float reboundProgress = entity.prevReboundProgress + (entity.reboundProgress - entity.prevReboundProgress) * partialTicks;
        float jumpProgress = Math.max(0, entity.prevJumpProgress + (entity.jumpProgress - entity.prevJumpProgress) * partialTicks - reboundProgress);
        float begProgress = entity.prevBegProgress + (entity.begProgress - entity.prevBegProgress) * partialTicks;
        this.walk(leftArm, idleSpeed, idleDegree, true, 2F, 0.3F, ageInTicks, 1);
        this.walk(rightArm, idleSpeed, idleDegree, true, 2F, 0.3F, ageInTicks, 1);
        this.walk(tail, idleSpeed, idleDegree * 0.5F, true, 1F, -0.05F, ageInTicks, 1);
        this.walk(leftEar, idleSpeed, idleDegree * 0.5F, false,0F, 0F, ageInTicks, 1);
        this.walk(rightEar, idleSpeed, idleDegree * 0.5F, false, 0F, 0F, ageInTicks, 1);
        progressRotationPrev(legs, jumpProgress, (float)Math.toRadians(65), 0, 0, 5F);
        progressRotationPrev(body, jumpProgress, (float)Math.toRadians(-5), 0, 0, 5F);
        progressRotationPrev(tail, jumpProgress, (float)Math.toRadians(-20), 0, 0, 5F);
        progressPositionPrev(body, jumpProgress, 0, -2, 0, 5F);
        progressRotationPrev(legs, reboundProgress, (float)Math.toRadians(-30), 0, 0, 5F);
        progressRotationPrev(body, reboundProgress, (float)Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(tail, reboundProgress, (float)Math.toRadians(35), 0, 0, 5F);
        progressRotationPrev(leftEar, reboundProgress, 0,  (float)Math.toRadians(35), 0, 5F);
        progressRotationPrev(rightEar, reboundProgress, 0,  (float)Math.toRadians(-35), 0, 5F);
        progressPositionPrev(body, reboundProgress, 0, -1, 0, 5F);
        progressPositionPrev(body, sleepProgress, 0, 5F, 0, 5F);
        progressPositionPrev(legs, sleepProgress, 0, -2.2F, 0, 5F);
        progressRotationPrev(legs, sleepProgress, (float)Math.toRadians(-30), 0, 0, 5F);
        progressRotationPrev(tail, sleepProgress, (float)Math.toRadians(50), 0, (float)Math.toRadians(90), 5F);
        progressRotationPrev(leftEar, sleepProgress, 0,  (float)Math.toRadians(35), 0, 5F);
        progressRotationPrev(rightEar, sleepProgress, 0,  (float)Math.toRadians(-35), 0, 5F);
        progressRotationPrev(leftArm, begProgress, (float)Math.toRadians(-15),  0, 0, 5F);
        progressRotationPrev(rightArm, begProgress, (float)Math.toRadians(-15), 0, 0, 5F);

        if (begProgress > 0) {
            float f = body.rotateAngleX;
            this.walk(body, 0.7F, 0.1F, false, 2F, -0.7F, ageInTicks, begProgress * 0.2F);
            float f1 = body.rotateAngleX - f;
            this.legs.rotateAngleX -= f1;
            this.tail.rotateAngleX -= f1;
            this.legs.rotationPointY += f1 * 3F;
            this.walk(rightArm, 0.7F, 1.2F, false, 0F, -1.0F, ageInTicks, begProgress * 0.2F);
            this.flap(rightArm, 0.7F, 0.25F, false, -1F, 0.2F, ageInTicks, begProgress * 0.2F);
            this.walk(leftArm, 0.7F, 1.2F, false, 0F, -1.0F, ageInTicks, begProgress * 0.2F);
            this.flap(leftArm, 0.7F, 0.25F, true, -1F, 0.2F, ageInTicks, begProgress * 0.2F);
        }
        float headY = netHeadYaw * 0.35F * Mth.DEG_TO_RAD;
        float headZ = headPitch * 0.65F * Mth.DEG_TO_RAD;
        if(Math.max(sleepProgress, begProgress) == 0){
            this.body.rotateAngleY += headY;
            this.legs.rotateAngleY -= headY * 0.6F;
            this.body.rotateAngleX += headZ;
            this.legs.rotateAngleX -= headZ;
            this.tail.rotateAngleX -= headZ * 1.2F;
            if(headPitch > 0){
                this.body.rotationPointY += Math.abs(headPitch) * 0.015F;
                this.legs.rotationPointZ -= Math.abs(headPitch) * 0.02F;
            }else{
                this.legs.rotationPointY -= Math.abs(headPitch) * 0.0225F;
                this.legs.rotationPointZ -= Math.abs(headPitch) * 0.015F;
            }
        }

    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.75F;
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.815D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
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