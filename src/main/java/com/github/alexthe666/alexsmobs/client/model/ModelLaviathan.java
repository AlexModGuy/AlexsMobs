package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ModelLaviathan extends AdvancedEntityModel<EntityLaviathan> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox leftLeg;
    private final AdvancedModelBox rightLeg;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox neck2;
    private final AdvancedModelBox head;
    private final AdvancedModelBox bottom_jaw;
    private final AdvancedModelBox top_jaw;
    private final AdvancedModelBox shell;
    private final AdvancedModelBox vent1;
    private final AdvancedModelBox vent2;
    private final AdvancedModelBox vent3;
    private final AdvancedModelBox vent4;

    public ModelLaviathan() {
        texHeight = 256;
        texWidth = 256;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-20.0F, -27.0F, -31.0F, 40.0F, 27.0F, 64.0F, 0.0F, false);

        leftArm = new AdvancedModelBox(this);
        leftArm.setRotationPoint(20.0F, -9.0F, -28.0F);
        body.addChild(leftArm);
        leftArm.setTextureOffset(150, 151).addBox(0.0F, -2.0F, -3.0F, 21.0F, 4.0F, 13.0F, 0.0F, false);
        leftArm.setTextureOffset(0, 49).addBox(13.0F, -1.9F, 1.0F, 10.0F, 0.0F, 14.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this);
        rightArm.setRotationPoint(-20.0F, -9.0F, -28.0F);
        body.addChild(rightArm);
        rightArm.setTextureOffset(150, 151).addBox(-21.0F, -2.0F, -3.0F, 21.0F, 4.0F, 13.0F, 0.0F, true);
        rightArm.setTextureOffset(0, 49).addBox(-23.0F, -1.9F, 1.0F, 10.0F, 0.0F, 14.0F, 0.0F, true);

        leftLeg = new AdvancedModelBox(this);
        leftLeg.setRotationPoint(20.0F, -8.0F, 21.0F);
        body.addChild(leftLeg);
        leftLeg.setTextureOffset(65, 151).addBox(0.0F, -2.0F, -5.0F, 25.0F, 4.0F, 17.0F, 0.0F, false);
        leftLeg.setTextureOffset(0, 30).addBox(23.0F, -1.9F, -4.0F, 4.0F, 0.0F, 18.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this);
        rightLeg.setRotationPoint(-20.0F, -8.0F, 21.0F);
        body.addChild(rightLeg);
        rightLeg.setTextureOffset(65, 151).addBox(-25.0F, -2.0F, -5.0F, 25.0F, 4.0F, 17.0F, 0.0F, true);
        rightLeg.setTextureOffset(0, 30).addBox(-27.0F, -1.9F, -4.0F, 4.0F, 0.0F, 18.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -20.0F, 33.0F);
        body.addChild(tail);
        tail.setTextureOffset(92, 92).addBox(-7.0F, -4.0F, 0.0F, 14.0F, 9.0F, 49.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, -19.0F, -32.0F);
        body.addChild(neck);
        neck.setTextureOffset(0, 138).addBox(-7.0F, -5.0F, -35.0F, 14.0F, 11.0F, 36.0F, 0.0F, false);
        neck.setTextureOffset(0, 0).addBox(-1.0F, -7.0F, -34.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
        neck.setTextureOffset(0, 0).addBox(-1.0F, -7.0F, -25.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
        neck.setTextureOffset(0, 0).addBox(-1.0F, -7.0F, -16.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
        neck.setTextureOffset(0, 0).addBox(-1.0F, -7.0F, -7.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

        neck2 = new AdvancedModelBox(this);
        neck2.setRotationPoint(0.0F, 0.0F, -35.0F);
        neck.addChild(neck2);
        neck2.setTextureOffset(145, 0).addBox(-5.0F, -4.0F, -39.0F, 10.0F, 9.0F, 39.0F, 0.0F, false);
        neck2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -35.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
        neck2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -26.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
        neck2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -17.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
        neck2.setTextureOffset(0, 0).addBox(-1.0F, -6.0F, -8.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -1.0F, -40.0F);
        neck2.addChild(head);
        head.setTextureOffset(0, 0).addBox(-6.0F, -5.0F, -16.0F, 12.0F, 12.0F, 17.0F, 0.0F, false);
        head.setTextureOffset(0, 49).addBox(1.0F, -10.0F, -14.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);
        head.setTextureOffset(0, 41).addBox(-3.0F, -7.0F, -8.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(27, 30).addBox(0.0F, -8.0F, -4.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        bottom_jaw = new AdvancedModelBox(this);
        bottom_jaw.setRotationPoint(0.0F, 2.0F, -16.0F);
        head.addChild(bottom_jaw);
        bottom_jaw.setTextureOffset(27, 30).addBox(-4.0F, 0.0F, -9.0F, 8.0F, 3.0F, 9.0F, 0.0F, false);
        bottom_jaw.setTextureOffset(0, 92).addBox(-3.5F, -2.0F, -9.0F, 7.0F, 2.0F, 9.0F, 0.0F, false);

        top_jaw = new AdvancedModelBox(this);
        top_jaw.setRotationPoint(0.0F, 2.0F, -16.0F);
        head.addChild(top_jaw);
        top_jaw.setTextureOffset(103, 92).addBox(-3.0F, -4.0F, -9.0F, 6.0F, 4.0F, 9.0F, 0.0F, false);
        top_jaw.setTextureOffset(0, 104).addBox(-3.0F, 0.0F, -9.0F, 6.0F, 2.0F, 9.0F, 0.0F, false);
        top_jaw.setTextureOffset(0, 30).addBox(-2.0F, -4.0F, -11.0F, 4.0F, 8.0F, 2.0F, 0.0F, false);

        shell = new AdvancedModelBox(this);
        shell.setRotationPoint(0.0F, -27.0F, 1.0F);
        body.addChild(shell);
        shell.setTextureOffset(0, 92).addBox(-16.0F, -7.0F, -19.0F, 32.0F, 7.0F, 38.0F, 0.0F, false);

        vent1 = new AdvancedModelBox(this);
        vent1.setRotationPoint(10.5F, -27.0F, -26.5F);
        body.addChild(vent1);
        vent1.setTextureOffset(224, 65).addBox(-2.5F, -10.0F, -2.5F, 5.0F, 10.0F, 5.0F, 0.0F, false);

        vent2 = new AdvancedModelBox(this);
        vent2.setRotationPoint(7.0F, -27.0F, 27.0F);
        body.addChild(vent2);
        vent2.setTextureOffset(216, 104).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);

        vent3 = new AdvancedModelBox(this);
        vent3.setRotationPoint(-6.5F, -27.0F, 24.5F);
        body.addChild(vent3);
        vent3.setTextureOffset(182, 103).addBox(-2.5F, -14.0F, -2.5F, 5.0F, 14.0F, 5.0F, 0.0F, false);

        vent4 = new AdvancedModelBox(this);
        vent4.setRotationPoint(-6.0F, -27.0F, -23.0F);
        body.addChild(vent4);
        vent4.setTextureOffset(226, 124).addBox(-3.0F, -13.0F, -3.0F, 6.0F, 13.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityLaviathan entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = Minecraft.getInstance().getFrameTime();
        float hh1 = entity.prevHeadHeight / 3F;
        float hh2 = entity.getHeadHeight() / 3F;
        float rawHeadHeight = hh1 + (hh2 - hh1) * partialTick;
        float clampedNeckRot = Mth.clamp(-rawHeadHeight, -1, 1);
        float headStillProgress = 1F - Math.abs(clampedNeckRot);
        float swimProgress = entity.prevSwimProgress + (entity.swimProgress - entity.prevSwimProgress) * partialTick;
        float onLandProgress = 5F - swimProgress;
        this.neck.rotateAngleX += clampedNeckRot;
        this.neck.rotationPointZ += Math.abs(clampedNeckRot) * 2F;
        this.neck2.rotateAngleX -= clampedNeckRot * 0.4F;
        this.neck2.rotationPointZ += Math.abs(clampedNeckRot) * 2F;
        this.head.rotateAngleX -= clampedNeckRot * 0.6F;
        this.head.rotationPointZ += Math.abs(clampedNeckRot) * 2F;

        this.neck.rotateAngleY += Math.toRadians(entity.getHeadYaw(partialTick) * 0.65F);
        this.neck2.rotateAngleY += Math.toRadians(entity.getHeadYaw(partialTick) * 0.5F);
        this.head.rotateAngleY += Math.toRadians(entity.getHeadYaw(partialTick) * 0.25F);

        progressRotationPrev(rightLeg, onLandProgress, 0, 0, (float) Math.toRadians(-15), 5F);
        progressRotationPrev(leftLeg, onLandProgress, 0, 0, (float) Math.toRadians(15), 5F);
        progressRotationPrev(rightArm, onLandProgress, 0, 0, (float) Math.toRadians(-20), 5F);
        progressRotationPrev(leftArm, onLandProgress, 0, 0, (float) Math.toRadians(20), 5F);
        float idleSpeed = 0.04f;
        float idleDegree = 0.3f;
        float walkSpeed = 0.9f - 0.6F * 0.2F * swimProgress;
        float walkDegree = 0.5F + swimProgress * 0.05F;
        AdvancedModelBox[] neckBoxes = new AdvancedModelBox[]{neck, neck2, head};
        this.chainWave(neckBoxes, idleSpeed, idleDegree * 0.2F, 9, ageInTicks, 1.0F);
        this.walk(tail, idleSpeed, idleDegree * 0.4F, false, 1.3F, -0.2F, ageInTicks, 1.0F);
        this.walk(bottom_jaw, idleSpeed * 2F, idleDegree * 0.4F, false, 1F, 0.1F, ageInTicks, 1.0F);
        this.swing(leftLeg, walkSpeed, walkDegree, true, 2, 0.2F, limbSwing, limbSwingAmount * onLandProgress * 0.2F);
        this.swing(rightLeg, walkSpeed, walkDegree, false, 2, 0.2F, limbSwing, limbSwingAmount * onLandProgress * 0.2F);
        this.swing(leftArm, walkSpeed, walkDegree, false, 2, -0.25F, limbSwing, limbSwingAmount * onLandProgress * 0.2F);
        this.swing(rightArm, walkSpeed, walkDegree, true, 2, -0.25F, limbSwing, limbSwingAmount * onLandProgress * 0.2F);
        this.bob(body, -walkSpeed * 0.5F, walkDegree * 3, true, limbSwing, limbSwingAmount);
        this.chainSwing(neckBoxes, walkSpeed, walkDegree * 0.3F, -21, limbSwing, limbSwingAmount * swimProgress * 0.2F * headStillProgress);
        this.swing(tail, walkSpeed, walkDegree * 0.5F, false, -3, 0F, limbSwing, 1.0F * swimProgress * 0.2F);
        this.flap(leftLeg, walkSpeed, walkDegree, true, 2, 0.2F, limbSwing, limbSwingAmount * swimProgress * 0.2F);
        this.flap(rightLeg, walkSpeed, walkDegree, false, 2, 0.2F, limbSwing, limbSwingAmount * swimProgress * 0.2F);
        this.flap(leftArm, walkSpeed, walkDegree, false, 2, -0.25F, limbSwing, limbSwingAmount * swimProgress * 0.2F);
        this.flap(rightArm, walkSpeed, walkDegree, true, 2, -0.25F, limbSwing, limbSwingAmount * swimProgress * 0.2F);
        this.flap(leftLeg, idleSpeed, idleDegree, false, 1, 0.2F, ageInTicks, swimProgress * 0.2F);
        this.flap(rightLeg, idleSpeed, idleDegree, true, 1, 0.2F, ageInTicks, swimProgress * 0.2F);
        this.flap(leftArm, idleSpeed, idleDegree, false, 1, 0.2F, ageInTicks, swimProgress * 0.2F);
        this.flap(rightArm, idleSpeed, idleDegree, true, 1, 0.2F, ageInTicks, swimProgress * 0.2F);
        this.tail.rotationPointZ -= limbSwingAmount * swimProgress * 0.2F;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leftArm, rightArm, leftLeg, rightLeg, tail, neck, neck2, head, bottom_jaw, top_jaw, shell, vent1, vent2, vent3, vent4);
    }


}
