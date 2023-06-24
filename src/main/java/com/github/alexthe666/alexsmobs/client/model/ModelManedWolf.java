package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityManedWolf;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public class ModelManedWolf extends AdvancedEntityModel<EntityManedWolf> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head_pivot;
    private final AdvancedModelBox head;
    private final AdvancedModelBox left_ear_pivot;
    private final AdvancedModelBox right_ear_pivot;

    private final AdvancedModelBox left_ear;
    private final AdvancedModelBox right_ear;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox right_arm;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox right_leg;

    public ModelManedWolf() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -17.0F, 2.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 8.0F, 17.0F, 0.0F, false);
        body.setTextureOffset(0, 26).addBox(-3.5F, -4.4F, -10.1F, 7.0F, 5.0F, 8.0F, 0.0F, false);

        neck = new AdvancedModelBox(this, "neck");
        neck.setRotationPoint(0.0F, -1.0F, -9.0F);
        body.addChild(neck);
        setRotationAngle(neck, -0.6545F, 0.0F, 0.0F);
        neck.setTextureOffset(30, 0).addBox(-2.5F, -3.0F, -6.0F, 5.0F, 5.0F, 7.0F, 0.0F, false);
        neck.setTextureOffset(42, 39).addBox(-1.5F, -6.0F, -5.0F, 3.0F, 3.0F, 6.0F, 0.0F, false);

        head_pivot = new AdvancedModelBox(this, "head_pivot");
        head_pivot.setRotationPoint(0.0F, -0.8F, -6.0F);
        neck.addChild(head_pivot);
        setRotationAngle(head_pivot, 0.6545F, 0.0F, 0.0F);


        head = new AdvancedModelBox(this, "head");
        head_pivot.addChild(head);

        head.setTextureOffset(0, 40).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 5.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(44, 22).addBox(-1.5F, 0.0F, -8.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

        left_ear_pivot = new AdvancedModelBox(this, "left_ear_pivot");
        left_ear_pivot.setRotationPoint(2.0F, -2.0F, -1.6F);
        head.addChild(left_ear_pivot);

        right_ear_pivot = new AdvancedModelBox(this, "right_ear_pivot");
        right_ear_pivot.setRotationPoint(-2.0F, -2.0F, -1.6F);
        head.addChild(right_ear_pivot);

        left_ear = new AdvancedModelBox(this, "left_ear");
        left_ear_pivot.addChild(left_ear);
        setRotationAngle(left_ear, -0.0479F, -0.2129F, 0.2233F);
        left_ear.setTextureOffset(47, 13).addBox(-1.0F, -5.0F, -0.4F, 3.0F, 6.0F, 1.0F, 0.0F, false);

        right_ear = new AdvancedModelBox(this, "right_ear");
        right_ear_pivot.addChild(right_ear);
        setRotationAngle(right_ear, -0.0479F, 0.2129F, -0.2233F);
        right_ear.setTextureOffset(47, 13).addBox(-2.0F, -5.0F, -0.4F, 3.0F, 6.0F, 1.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -2.0F, 7.0F);
        body.addChild(tail);
        tail.setTextureOffset(31, 26).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 14.0F, 4.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(1.8F, 5.0F, -8.0F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 13.0F, 2.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-1.8F, 5.0F, -8.0F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 13.0F, 2.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(2.0F, 3.0F, 5.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(23, 42).addBox(-0.8F, -2.0F, -0.9F, 2.0F, 16.0F, 3.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-2.0F, 3.0F, 5.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(23, 42).addBox(-1.2F, -2.0F, -0.9F, 2.0F, 16.0F, 3.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head_pivot, head, neck, tail, left_ear, right_ear, left_arm, right_arm, left_leg, right_leg, right_ear_pivot, left_ear_pivot);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.35F;
            float feet = 0.8F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            left_arm.setScale(1, feet, 1);
            right_arm.setScale(1, feet, 1);
            left_leg.setScale(1, feet, 1);
            right_leg.setScale(1, feet, 1);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 1D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
            left_arm.setScale(1, 1, 1);
            right_arm.setScale(1, 1, 1);
            left_leg.setScale(1, 1, 1);
            right_leg.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }

    @Override
    public void setupAnim(EntityManedWolf entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float walkSpeed = 0.5F;
        float walkDegree = 0.8F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.2F;
        float shakeSpeed = 0.9F;
        float shakeDegree = 0.4F;
        float partialTick = ageInTicks - entity.tickCount;
        float runProgress = 5F * 2F * Math.min(limbSwingAmount, 0.5F);
        float danceProgress = entity.prevDanceProgress + (entity.danceProgress - entity.prevDanceProgress) * partialTick;
        float shakeProgress = entity.prevShakeProgress + (entity.shakeProgress - entity.prevShakeProgress) * partialTick;
        float earPitch = entity.prevEarPitch + (entity.getEarPitch() - entity.prevEarPitch) * partialTick;
        float earYaw = entity.prevEarYaw + (entity.getEarYaw() - entity.prevEarYaw) * partialTick;
        this.left_ear_pivot.rotateAngleX += earPitch * Mth.DEG_TO_RAD;
        this.left_ear_pivot.rotateAngleY += earYaw * Mth.DEG_TO_RAD;
        this.right_ear_pivot.rotateAngleX += earPitch * Mth.DEG_TO_RAD;
        this.right_ear_pivot.rotateAngleY -= earYaw * Mth.DEG_TO_RAD;
        this.head.rotateAngleY += netHeadYaw * 0.5F * Mth.DEG_TO_RAD;

        progressRotationPrev(tail, runProgress, (float) Math.toRadians(35), 0, 0, 5f);
        progressRotationPrev(neck, runProgress, (float) Math.toRadians(40), 0, 0, 5f);
        progressRotationPrev(head, runProgress, (float) Math.toRadians(-40), 0, 0, 5f);

        progressRotationPrev(body, danceProgress, (float) Math.toRadians(-40), 0, 0, 5f);
        progressRotationPrev(left_arm, danceProgress, (float) Math.toRadians(20), 0, 0, 5f);
        progressRotationPrev(right_arm, danceProgress, (float) Math.toRadians(20), 0, 0, 5f);
        progressRotationPrev(left_leg, danceProgress, (float) Math.toRadians(-30), 0, (float) Math.toRadians(-15), 5f);
        progressRotationPrev(right_leg, danceProgress, (float) Math.toRadians(-30), 0, (float) Math.toRadians(15), 5f);
        progressRotationPrev(head, danceProgress, (float) Math.toRadians(-20), 0, 0, 5f);
        progressRotationPrev(tail, danceProgress, (float) Math.toRadians(20), (float) Math.toRadians(20),  0, 5f);
        progressPositionPrev(body, danceProgress, 0, 6, 0, 5f);
        progressPositionPrev(left_arm, danceProgress, 0, -2, 1, 5f);
        progressPositionPrev(right_arm, danceProgress, 0, -2, 1, 5f);
        progressRotationPrev(neck, shakeProgress, (float) Math.toRadians(30), 0, 0, 5f);
        progressRotationPrev(head, shakeProgress, (float) Math.toRadians(-30), 0, 0, 5f);
        progressRotationPrev(tail, shakeProgress, (float) Math.toRadians(20), 0, 0, 5f);
        this.swing(body, 0.25F, 0.5F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.walk(body, 0.5F, 0.3F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.walk(left_leg, 0.5F, -0.3F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.walk(right_leg, 0.5F, -0.3F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.walk(left_arm, 0.5F, -0.6F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.walk(right_arm, 0.5F, -0.6F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.walk(neck, 0.5F, 0.3F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.flap(neck, 0.25F, 0.9F, false, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.swing(head, 0.25F, 0.9F, true, 0F, 0, ageInTicks, danceProgress * 0.2F);
        this.flap(tail, 0.25F, 0.9F, true, 1, 0, ageInTicks, danceProgress * 0.2F);
        float f1 = 0.2F * danceProgress * (float)(-Math.abs(Math.sin((double)(ageInTicks * 0.5F * 0.5F)) * (double)8F));
        float f2 = 0.2F * danceProgress * 6F * Mth.cos(ageInTicks * 0.5F + 0) * 0.3F * (danceProgress * 0.2F) + 0 * (danceProgress * 0.2F);
        this.body.rotationPointY += f1;
        this.body.rotationPointZ -= f2;
        this.right_leg.rotationPointY += 0.25F * f2;
        this.left_leg.rotationPointY += 0.25F * f2;
        this.right_leg.rotateAngleX -= Math.toRadians(f1 * 5);
        this.left_leg.rotateAngleX -= Math.toRadians(f1 * 5);
        this.right_arm.rotationPointY += f2;
        this.left_arm.rotationPointY += f2;
        this.right_arm.rotationPointZ -= -f2 + 0.25F * f1;
        this.left_arm.rotationPointZ -= -f2 + 0.25F * f1;
        this.flap(tail, idleSpeed, idleDegree * 0.2F, false, 1F, 0, ageInTicks, 1);
        this.walk(head, idleSpeed, idleDegree * 0.2F, true, 2F, 0, ageInTicks, 1);
        this.walk(neck, idleSpeed, idleDegree * 0.2F, false, 2F, 0, ageInTicks, 1);
        this.walk(right_leg, walkSpeed, walkDegree * 1.1F, true, -1F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_leg, walkSpeed, walkDegree * 1.1F, false, -1F, 0F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree, true, limbSwing, limbSwingAmount);
        this.walk(right_arm, walkSpeed, walkDegree * -1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(right_arm, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.bob(left_arm, -walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(left_arm, walkSpeed, walkDegree * -1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(neck, walkSpeed, walkDegree * 0.2F, false, -1F, 0, limbSwing, limbSwingAmount);
        this.walk(head, walkSpeed, walkDegree * 0.2F, true, -1F, 0, limbSwing, limbSwingAmount);
        if(danceProgress <= 0){
            this.faceTarget(netHeadYaw, headPitch, 1.0F, neck, head);
        }

        this.flap(body, shakeSpeed, shakeDegree, false, 0F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.flap(neck, shakeSpeed, shakeDegree, false, 1F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.flap(head, shakeSpeed, shakeDegree, true, 2F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.flap(tail, shakeSpeed, shakeDegree, true, 2F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.swing(body, shakeSpeed, shakeDegree * 0.5F, false, 1F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.flap(left_leg, shakeSpeed, shakeDegree, true, 0F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.flap(right_leg, shakeSpeed, shakeDegree, true, 0F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.flap(left_arm, shakeSpeed, shakeDegree, true, 0F, 0F, ageInTicks, shakeProgress * 0.2F);
        this.flap(right_arm, shakeSpeed, shakeDegree, true, 0F, 0F, ageInTicks, shakeProgress * 0.2F);

    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}