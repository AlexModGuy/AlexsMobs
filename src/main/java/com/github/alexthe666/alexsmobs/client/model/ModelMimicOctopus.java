package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelMimicOctopus extends AdvancedEntityModel<EntityMimicOctopus> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftEye;
    private final AdvancedModelBox leftEyeSpike;
    private final AdvancedModelBox rightEye;
    private final AdvancedModelBox rightEyeSpike;
    private final AdvancedModelBox leftFrontArm1;
    private final AdvancedModelBox rightFrontArm1;
    private final AdvancedModelBox leftFrontArm2;
    private final AdvancedModelBox rightFrontArm2;
    private final AdvancedModelBox leftBackArm1;
    private final AdvancedModelBox rightBackArm1;
    private final AdvancedModelBox leftBackArm2;
    private final AdvancedModelBox rightBackArm2;
    private final AdvancedModelBox mantle;
    private final AdvancedModelBox creeperPivots1;
    private final AdvancedModelBox creeperPivots2;
    private final AdvancedModelBox creeperPivots3;
    private final AdvancedModelBox creeperPivots4;

    public ModelMimicOctopus() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -3.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(30, 24).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 4.0F, 7.0F, 0.0F, false);

        leftEye = new AdvancedModelBox(this, "leftEye");
        leftEye.setRotationPoint(2.0F, -2.0F, -2.5F);
        body.addChild(leftEye);
        leftEye.setTextureOffset(35, 18).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 1.0F, 3.0F, 0.0F, false);

        leftEyeSpike = new AdvancedModelBox(this, "leftEyeSpike");
        leftEyeSpike.setRotationPoint(-1.0F, -1.0F, -1.5F);
        leftEye.addChild(leftEyeSpike);
        leftEyeSpike.setTextureOffset(0, 0).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, false);

        rightEye = new AdvancedModelBox(this, "rightEye");
        rightEye.setRotationPoint(-2.0F, -2.0F, -2.5F);
        body.addChild(rightEye);
        rightEye.setTextureOffset(35, 18).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 1.0F, 3.0F, 0.0F, true);

        rightEyeSpike = new AdvancedModelBox(this, "rightEyeSpike");
        rightEyeSpike.setRotationPoint(1.0F, -1.0F, -1.5F);
        rightEye.addChild(rightEyeSpike);
        rightEyeSpike.setTextureOffset(0, 0).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, true);

        leftFrontArm1 = new AdvancedModelBox(this, "leftFrontArm1");
        leftFrontArm1.setRotationPoint(2.0F, 2.0F, -4.0F);
        setRotationAngle(leftFrontArm1, 0.0F, 0.6109F, 0.0F);
        leftFrontArm1.setTextureOffset(26, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);
        leftFrontArm1.setTextureOffset(35, 11).addBox(11.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);

        rightFrontArm1 = new AdvancedModelBox(this, "rightFrontArm1");
        rightFrontArm1.setRotationPoint(-2.0F, 2.0F, -4.0F);
        setRotationAngle(rightFrontArm1, 0.0F, -0.6109F, 0.0F);
        rightFrontArm1.setTextureOffset(26, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);
        rightFrontArm1.setTextureOffset(35, 11).addBox(-15.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, true);

        leftFrontArm2 = new AdvancedModelBox(this, "leftFrontArm2");
        leftFrontArm2.setRotationPoint(2.0F, 2.0F, -2.3F);
        setRotationAngle(leftFrontArm2, 0.0F, 0.3054F, 0.0F);
        leftFrontArm2.setTextureOffset(0, 26).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);
        leftFrontArm2.setTextureOffset(35, 5).addBox(11.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);

        rightFrontArm2 = new AdvancedModelBox(this, "rightFrontArm2");
        rightFrontArm2.setRotationPoint(-2.0F, 2.0F, -2.3F);
        setRotationAngle(rightFrontArm2, 0.0F, -0.3054F, 0.0F);
        rightFrontArm2.setTextureOffset(0, 26).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);
        rightFrontArm2.setTextureOffset(35, 5).addBox(-15.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, true);

        creeperPivots1 = new AdvancedModelBox(this, "creeperPivots1");
        creeperPivots2 = new AdvancedModelBox(this, "creeperPivots2");
        creeperPivots3 = new AdvancedModelBox(this, "creeperPivots3");
        creeperPivots4 = new AdvancedModelBox(this, "creeperPivots4");
        body.addChild(creeperPivots1);
        body.addChild(creeperPivots2);
        body.addChild(creeperPivots3);
        body.addChild(creeperPivots4);

        leftBackArm1 = new AdvancedModelBox(this, "leftBackArm1");
        leftBackArm1.setRotationPoint(2.0F, 2.0F, -1.0F);
        setRotationAngle(leftBackArm1, 0.0F, -0.2182F, 0.0F);
        leftBackArm1.setTextureOffset(0, 21).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);
        leftBackArm1.setTextureOffset(13, 31).addBox(11.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);

        rightBackArm1 = new AdvancedModelBox(this, "rightBackArm1");
        rightBackArm1.setRotationPoint(-2.0F, 2.0F, -1.0F);
        setRotationAngle(rightBackArm1, 0.0F, 0.2182F, 0.0F);
        rightBackArm1.setTextureOffset(0, 21).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);
        rightBackArm1.setTextureOffset(13, 31).addBox(-15.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, true);

        leftBackArm2 = new AdvancedModelBox(this, "leftBackArm2");
        leftBackArm2.setRotationPoint(2.0F, 2.0F, 1.0F);
        setRotationAngle(leftBackArm2, 0.0F, -0.6981F, 0.0F);
        leftBackArm2.setTextureOffset(0, 16).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);
        leftBackArm2.setTextureOffset(0, 31).addBox(11.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);

        rightBackArm2 = new AdvancedModelBox(this, "rightBackArm2");
        rightBackArm2.setRotationPoint(-2.0F, 2.0F, 1.0F);
        setRotationAngle(rightBackArm2, 0.0F, 0.6981F, 0.0F);
        rightBackArm2.setTextureOffset(0, 16).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);
        rightBackArm2.setTextureOffset(0, 31).addBox(-15.0F, -4.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, true);

        mantle = new AdvancedModelBox(this, "mantle");
        mantle.setRotationPoint(0.0F, -1.0F, 2.0F);
        body.addChild(mantle);
        mantle.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 6.0F, 9.0F, 0.0F, false);

        creeperPivots1.addChild(leftFrontArm1);
        creeperPivots1.addChild(leftFrontArm2);

        creeperPivots2.addChild(leftBackArm1);
        creeperPivots2.addChild(leftBackArm2);

        creeperPivots3.addChild(rightFrontArm1);
        creeperPivots3.addChild(rightFrontArm2);
        creeperPivots4.addChild(rightBackArm1);
        creeperPivots4.addChild(rightBackArm2);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityMimicOctopus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float transProgress = entity.prevTransProgress + (entity.transProgress - entity.prevTransProgress) * partialTicks;
        float groundProgress = entity.prevGroundProgress + (entity.groundProgress - entity.prevGroundProgress) * partialTicks;
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTicks;
        float notSitProgress = 1 - sitProgress * 0.2F;
        float swimProgress = (5F - groundProgress) * notSitProgress;
        float groundProgressNorm = groundProgress * 0.2F * notSitProgress;
        if(entity.getPrevMimicState() != null) {
            float progress = notSitProgress * (5 - transProgress);
            animateForMimicGround(entity.getPrevMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks,  progress * groundProgressNorm);
            if(sitProgress == 0){
                animateForMimicWater(entity.getPrevMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks, progress * (1 - groundProgressNorm));
            }
        }
        animateForMimicGround(entity.getMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks, notSitProgress * transProgress * groundProgressNorm);
        animateForMimicWater(entity.getMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks, notSitProgress * transProgress * (1 - groundProgressNorm));

        if(swimProgress > 0.0F){
            float rot = headPitch * ((float)Math.PI / 180F);
            this.body.rotationPointY += Math.abs(rot) * -7;
            this.body.rotateAngleX -= rot;
        }
        progressRotationPrev(mantle, sitProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(leftFrontArm1, sitProgress,  0,  (float)Math.toRadians(10),  0, 5F);
        progressRotationPrev(leftFrontArm2, sitProgress,  0,   (float)Math.toRadians(-5),  0, 5F);
        progressRotationPrev(leftBackArm1, sitProgress,  0,  (float)Math.toRadians(-10),  0, 5F);
        progressRotationPrev(leftBackArm2, sitProgress,  0,   (float)Math.toRadians(-15),  0, 5F);
        progressRotationPrev(rightFrontArm1, sitProgress,  0,  (float)Math.toRadians(-10),  0, 5F);
        progressRotationPrev(rightFrontArm2, sitProgress,  0,   (float)Math.toRadians(5),  0, 5F);
        progressRotationPrev(rightBackArm1, sitProgress,  0,  (float)Math.toRadians(10),  0, 5F);
        progressRotationPrev(rightBackArm2, sitProgress,  0,   (float)Math.toRadians(15),  0, 5F);

    }

    public void animateForMimicWater(EntityMimicOctopus.MimicState state, EntityMimicOctopus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float swimProgress) {
        limbSwingAmount = limbSwingAmount * swimProgress * 0.2F;
        progressRotationPrev(body, swimProgress, 0, (float) Math.toRadians(-180), 0, 5F);
        progressRotationPrev(leftFrontArm1, swimProgress, (float) Math.toRadians(-90),  (float) Math.toRadians(10), (float) Math.toRadians(-50), 5F);
        progressRotationPrev(leftFrontArm2, swimProgress, (float) Math.toRadians(-90),  (float) Math.toRadians(20), (float) Math.toRadians(-20), 5F);
        progressRotationPrev(leftBackArm1, swimProgress, (float) Math.toRadians(-90),  (float) Math.toRadians(50), (float) Math.toRadians(20), 5F);
        progressRotationPrev(leftBackArm2, swimProgress, (float) Math.toRadians(-90), (float) Math.toRadians(70), (float) Math.toRadians(50), 5F);
        progressPositionPrev(leftFrontArm1, swimProgress, -1, -1, 1, 5F);
        progressPosition(leftFrontArm2, swimProgress, leftFrontArm1.rotationPointX, leftFrontArm1.rotationPointY, leftFrontArm1.rotationPointZ, 5F);
        progressPosition(leftBackArm1, swimProgress, leftFrontArm1.rotationPointX, leftFrontArm1.rotationPointY, leftFrontArm1.rotationPointZ, 5F);
        progressPosition(leftBackArm2, swimProgress, leftFrontArm1.rotationPointX, leftFrontArm1.rotationPointY, leftFrontArm1.rotationPointZ, 5F);
        progressRotationPrev(rightFrontArm1, swimProgress, (float) Math.toRadians(-90),  (float) Math.toRadians(-10), (float) Math.toRadians(50), 5F);
        progressRotationPrev(rightFrontArm2, swimProgress, (float) Math.toRadians(-90),  (float) Math.toRadians(-20), (float) Math.toRadians(20), 5F);
        progressRotationPrev(rightBackArm1, swimProgress, (float) Math.toRadians(-90),  (float) Math.toRadians(-50), (float) Math.toRadians(-20), 5F);
        progressRotationPrev(rightBackArm2, swimProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-70), (float) Math.toRadians(-50), 5F);
        progressPositionPrev(rightFrontArm1, swimProgress, 1, -1, 1, 5F);
        progressPosition(rightFrontArm2, swimProgress, rightFrontArm1.rotationPointX, rightFrontArm1.rotationPointY, rightFrontArm1.rotationPointZ, 5F);
        progressPosition(rightBackArm1, swimProgress, rightFrontArm1.rotationPointX, rightFrontArm1.rotationPointY, rightFrontArm1.rotationPointZ, 5F);
        progressPosition(rightBackArm2, swimProgress, rightFrontArm1.rotationPointX, rightFrontArm1.rotationPointY, rightFrontArm1.rotationPointZ, 5F);

        if (state == EntityMimicOctopus.MimicState.GUARDIAN) {
            float degree = 1.6F;
            float speed = 0.5F;
            progressPositionPrev(body, swimProgress, 0, -4, 5, 5F);
            progressPositionPrev(mantle, swimProgress, 0, 2, 0, 5F);
            if(swimProgress > 0){
                this.mantle.setScale(1F + swimProgress * 0.1F, 1F + swimProgress * 0.1F, 1F + swimProgress * 0.1F);
            }
            progressRotationPrev(leftFrontArm1, swimProgress, (float) Math.toRadians(90),  (float) Math.toRadians(45), (float) Math.toRadians(50), 5F);
            progressRotationPrev(rightFrontArm1, swimProgress, (float) Math.toRadians(-90),  (float) Math.toRadians(-45), (float) Math.toRadians(-50), 5F);
            progressPositionPrev(leftFrontArm1, swimProgress, -1, -1, 0, 5F);
            progressPositionPrev(rightFrontArm1, swimProgress, 1, 1, 0, 5F);

            progressRotationPrev(leftFrontArm2, swimProgress, (float) Math.toRadians(90),  (float) Math.toRadians(-40), (float) Math.toRadians(-15), 5F);
            progressPositionPrev(leftFrontArm2, swimProgress, 0, 1, 5, 5F);
            progressRotationPrev(leftBackArm1, swimProgress, (float) Math.toRadians(90),  (float) Math.toRadians(-20), (float) Math.toRadians(10), 5F);
            progressPositionPrev(leftBackArm1, swimProgress, -1, 0, 2, 5F);
            progressRotationPrev(leftBackArm2, swimProgress, (float) Math.toRadians(90),  (float) Math.toRadians(-70), (float) Math.toRadians(-15), 5F);
            progressPositionPrev(leftBackArm2, swimProgress, 0, 1, 5, 5F);
            progressRotationPrev(rightFrontArm2, swimProgress, (float) Math.toRadians(90),  (float) Math.toRadians(40), (float) Math.toRadians(15), 5F);
            progressPositionPrev(rightFrontArm2, swimProgress, 0, 1, 5, 5F);
            progressRotationPrev(rightBackArm1, swimProgress, (float) Math.toRadians(90),  (float) Math.toRadians(20), (float) Math.toRadians(-10), 5F);
            progressPositionPrev(rightBackArm1, swimProgress, 1, 0, 2, 5F);
            progressRotationPrev(rightBackArm2, swimProgress, (float) Math.toRadians(90),  (float) Math.toRadians(70), (float) Math.toRadians(15), 5F);
            progressPositionPrev(rightBackArm2, swimProgress, 0, 1, 5, 5F);
            this.swing(leftFrontArm1, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.swing(rightFrontArm1, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
        } else {
            float f = 1.0F;
            if(state == EntityMimicOctopus.MimicState.CREEPER){
                progressPositionPrev(body, swimProgress, 0, -3, -2, 5F);
                progressPositionPrev(mantle, swimProgress, 0, -2, 1, 5F);
                progressRotationPrev(mantle, swimProgress, (float)Math.toRadians(-60), 0, 0, 5F);
                f = 0.5F;
            }
            float degree = 1.6F;
            float speed = 0.5F;
            this.bob(body, speed, degree * 2, false, limbSwing, limbSwingAmount);
            if(swimProgress > 0) {
                if (state == EntityMimicOctopus.MimicState.PUFFERFISH) {
                    float f2 = 1.4F + 0.15F * Mth.sin(speed * limbSwing - 2) * swimProgress * 0.2F;
                    progressPositionPrev(mantle, swimProgress, 0, 1, 0, 5F);
                    this.mantle.setScale(f2, f2, f2);
                } else {
                    float scale = 1.1F + 0.3F * f * Mth.sin(speed * limbSwing - 2);
                    this.mantle.setScale((scale - 1F) * swimProgress * 0.05F + 1F, (scale - 1F) * swimProgress * 0.05F + 1F, (scale - 1F) * swimProgress * 0.2F + 1F);
                }
            }
            this.walk(rightEyeSpike, speed, degree * 0.2F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.walk(leftEyeSpike, speed, degree * 0.2F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.swing(leftFrontArm1, speed, degree * 0.35F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.swing(leftFrontArm2, speed, degree * 0.35F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.swing(leftBackArm1, speed, degree * 0.35F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.swing(leftBackArm2, speed, degree * 0.35F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.swing(rightFrontArm1, speed, degree * 0.35F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.swing(rightFrontArm2, speed, degree * 0.35F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.swing(rightBackArm1, speed, degree * 0.35F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.swing(rightBackArm2, speed, degree * 0.35F, false, 0, -0.1F, limbSwing, limbSwingAmount);

        }
    }

    public void animateForMimicGround(EntityMimicOctopus.MimicState state, EntityMimicOctopus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float groundProgress){
        this.mantle.setScale(1, 1, 1);
        limbSwingAmount = limbSwingAmount * groundProgress * 0.2F;
        float degree = 0.8F;
        float speed = 0.8F;
        if(state == EntityMimicOctopus.MimicState.CREEPER){
            progressRotationPrev(body, groundProgress, 0, (float)Math.toRadians(-180), 0, 5F);
            progressRotationPrev(mantle, groundProgress, (float)Math.toRadians(-80), 0, 0, 5F);
            progressPositionPrev(mantle, groundProgress, 0, -3, -1, 5F);
            progressPositionPrev(body, groundProgress, 0, -13, -2, 5F);
            progressRotationPrev(leftFrontArm1, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(55),  (float)Math.toRadians(-20), 5F);
            progressPositionPrev(leftFrontArm1, groundProgress,  -1, 0,  0, 5F);
            progressRotationPrev(rightFrontArm1, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(-55),  (float)Math.toRadians(20), 5F);
            progressPositionPrev(rightFrontArm1, groundProgress,  1, 0,  0, 5F);
            progressRotationPrev(leftFrontArm2, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(73),  (float)Math.toRadians(-20), 5F);
            progressPositionPrev(leftFrontArm2, groundProgress,  1F, 0,  -1.65F, 5F);
            progressRotationPrev(rightFrontArm2, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(-73),  (float)Math.toRadians(20), 5F);
            progressPositionPrev(rightFrontArm2, groundProgress,  -1F, 0,  -1.65F, 5F);

            progressRotationPrev(leftBackArm1, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(-78),  (float)Math.toRadians(20), 5F);
            progressPositionPrev(leftBackArm1, groundProgress,  -1, 0,  0, 5F);
            progressRotationPrev(rightBackArm1, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(78),  (float)Math.toRadians(-20), 5F);
            progressPositionPrev(rightBackArm1, groundProgress,  1, 0,  0, 5F);
            progressRotationPrev(leftBackArm2, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(-51),  (float)Math.toRadians(20), 5F);
            progressPositionPrev(leftBackArm2, groundProgress,  1, 0,  -2, 5F);
            progressRotationPrev(rightBackArm2, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(51),  (float)Math.toRadians(-20), 5F);
            progressPositionPrev(rightBackArm2, groundProgress,  -1, 0,  -2, 5F);
            progressRotationPrev(creeperPivots1, groundProgress,  (float)Math.toRadians(90),  0,  0, 5F);
            progressPositionPrev(creeperPivots1, groundProgress,  0, -2,  -5, 5F);
            progressRotationPrev(creeperPivots3, groundProgress,  (float)Math.toRadians(90),  0,  0, 5F);
            progressPositionPrev(creeperPivots3, groundProgress,  0, -2,  -5, 5F);
            progressRotationPrev(creeperPivots2, groundProgress,  (float)Math.toRadians(-90),  0,  0, 5F);
            progressPositionPrev(creeperPivots2, groundProgress,  0, 3,  2, 5F);
            progressRotationPrev(creeperPivots4, groundProgress,  (float)Math.toRadians(-90),  0,  0, 5F);
            progressPositionPrev(creeperPivots4, groundProgress,  0, 3,  2, 5F);
            this.walk(creeperPivots1, speed, degree * 0.25F, true, 1, 0.1F, limbSwing, limbSwingAmount);
            this.walk(creeperPivots4, speed, degree * 0.25F, true, 1, -0.1F, limbSwing, limbSwingAmount);
            this.walk(creeperPivots2, speed, degree * 0.25F, false, 1, 0.1F, limbSwing, limbSwingAmount);
            this.walk(creeperPivots3, speed, degree * 0.25F, false, 1, -0.1F, limbSwing, limbSwingAmount);
            this.flap(mantle, speed, degree * 0.25F, true, 0, 0, limbSwing, limbSwingAmount);
        }else{
            float idleDegree = 0.02F;
            float idleSpeed = 0.05F;
            this.swing(leftFrontArm1, idleSpeed, idleDegree, true, 0, -0.05F, ageInTicks, groundProgress);
            this.swing(rightFrontArm1, idleSpeed, idleDegree, false, 0, -0.05F, ageInTicks, groundProgress);
            this.swing(leftFrontArm2, idleSpeed, idleDegree, true, -1, -0.02F, ageInTicks, groundProgress);
            this.swing(rightFrontArm2, idleSpeed, idleDegree, false, -1, -0.02F, ageInTicks, groundProgress);
            this.swing(leftBackArm1, idleSpeed, idleDegree, true, -2, 0.02F, ageInTicks, groundProgress);
            this.swing(rightBackArm1, idleSpeed, idleDegree, false, -2, -0.02F, ageInTicks, groundProgress);
            this.swing(leftBackArm2, idleSpeed, idleDegree, true, -3, 0.05F, ageInTicks, groundProgress);
            this.swing(rightBackArm2, idleSpeed, idleDegree, false, -3, -0.05F, ageInTicks, groundProgress);
            this.flap(leftEyeSpike, idleSpeed, idleDegree, false, -5, 0.1F, ageInTicks, groundProgress);
            this.flap(rightEyeSpike, idleSpeed, idleDegree, true, -5, 0.1F, ageInTicks, groundProgress);
            this.walk(mantle, speed, degree * 0.15F, true, 1, 0, limbSwing, limbSwingAmount);
            this.swing(mantle, speed, degree * 0.15F, true, 3, 0, limbSwing, limbSwingAmount);
            this.swing(body, speed, degree * 0.2F, true, -3, 0, limbSwing, limbSwingAmount);
            this.swing(leftFrontArm1, speed, degree * 0.3F, true, -1, -0.3F, limbSwing, limbSwingAmount);
            this.swing(leftFrontArm2, speed, degree * 0.3F, true, -2, -0.1F, limbSwing, limbSwingAmount);
            this.swing(leftBackArm1, speed, degree * 0.3F, true, -3, -0.1F, limbSwing, limbSwingAmount);
            this.swing(leftBackArm2, speed, degree * 0.3F, true, -4, -0.2F, limbSwing, limbSwingAmount);
            this.swing(rightFrontArm1, speed, degree * 0.3F, false, -1, -0.3F, limbSwing, limbSwingAmount);
            this.swing(rightFrontArm2, speed, degree * 0.3F, false, -2, -0.1F, limbSwing, limbSwingAmount);
            this.swing(rightBackArm1, speed, degree * 0.3F, false, -3, -0.1F, limbSwing, limbSwingAmount);
            this.swing(rightBackArm2, speed, degree * 0.3F, false, -4, 0.2F, limbSwing, limbSwingAmount);
            if(entity.hasGuardianLaser()){
                progressRotationPrev(body, groundProgress, 0, (float)Math.toRadians(180), 0, 5F);
            }
        }

    }

    @Override
    public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, mantle, rightEye, rightEyeSpike, leftEye, leftEyeSpike, body, leftFrontArm1, leftFrontArm2, rightFrontArm1, rightFrontArm2, leftBackArm1, leftBackArm2, rightBackArm1, rightBackArm2, creeperPivots1, creeperPivots2, creeperPivots3, creeperPivots4);

	}

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}