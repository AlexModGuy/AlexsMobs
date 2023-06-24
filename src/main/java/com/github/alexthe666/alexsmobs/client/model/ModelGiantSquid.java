package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ModelGiantSquid extends AdvancedEntityModel<EntityGiantSquid> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox head;
    private final AdvancedModelBox beak;
    private final AdvancedModelBox left_FrontTentacle;
    private final AdvancedModelBox left_FrontTentacleEnd;
    private final AdvancedModelBox left_FrontMidTentacle;
    private final AdvancedModelBox left_FrontMidTentacleEnd;
    private final AdvancedModelBox right_FrontMidTentacle;
    private final AdvancedModelBox right_FrontMidTentacleEnd;
    private final AdvancedModelBox left_BackMidTentacle;
    private final AdvancedModelBox left_BackMidTentacleEnd;
    private final AdvancedModelBox right_BackMidTentacle;
    private final AdvancedModelBox right_BackMidTentacleEnd;
    private final AdvancedModelBox left_BackTentacle;
    private final AdvancedModelBox left_BackTentacleEnd;
    private final AdvancedModelBox right_BackTentacle;
    private final AdvancedModelBox right_BackTentacleEnd;
    private final AdvancedModelBox right_FrontTentacle;
    private final AdvancedModelBox right_tentacleEnd;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox left_arm2;
    private final AdvancedModelBox left_arm3;
    private final AdvancedModelBox left_arm4;
    private final AdvancedModelBox left_hand;
    private final AdvancedModelBox right_arm;
    private final AdvancedModelBox right_arm2;
    private final AdvancedModelBox right_arm3;
    private final AdvancedModelBox right_arm4;
    private final AdvancedModelBox right_hand;
    private final AdvancedModelBox left_eye;
    private final AdvancedModelBox left_pupil;
    private final AdvancedModelBox left_pupil_pivot;
    private final AdvancedModelBox right_pupil_pivot;
    private final AdvancedModelBox right_eye;
    private final AdvancedModelBox right_pupil;
    private final AdvancedModelBox mantle;
    private final AdvancedModelBox mantle_end;
    private final AdvancedModelBox left_membrane;
    private final AdvancedModelBox right_membrane;

    public ModelGiantSquid() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -5.0F, 0.0F);
        root.addChild(head);
        head.setTextureOffset(43, 35).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 14.0F, 10.0F, 0.0F, false);

        beak = new AdvancedModelBox(this, "beak");
        beak.setRotationPoint(0.0F, 5.0F, 0.0F);
        head.addChild(beak);
        beak.setTextureOffset(41, 0).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 4.0F, 0.0F, false);

        left_FrontTentacle = new AdvancedModelBox(this, "left_FrontTentacle");
        left_FrontTentacle.setRotationPoint(1.5F, 5.0F, -4.0F);
        head.addChild(left_FrontTentacle);
        left_FrontTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        left_FrontTentacleEnd = new AdvancedModelBox(this, "left_FrontTentacleEnd");
        left_FrontTentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        left_FrontTentacle.addChild(left_FrontTentacleEnd);
        left_FrontTentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        left_FrontMidTentacle = new AdvancedModelBox(this, "left_FrontMidTentacle");
        left_FrontMidTentacle.setRotationPoint(4.0F, 5.0F, -2.0F);
        head.addChild(left_FrontMidTentacle);
        setRotationAngle(left_FrontMidTentacle, 0.0F, -1.5708F, 0.0F);
        left_FrontMidTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        left_FrontMidTentacleEnd = new AdvancedModelBox(this, "left_FrontMidTentacleEnd");
        left_FrontMidTentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        left_FrontMidTentacle.addChild(left_FrontMidTentacleEnd);
        left_FrontMidTentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        right_FrontMidTentacle = new AdvancedModelBox(this, "right_FrontMidTentacle");
        right_FrontMidTentacle.setRotationPoint(-4.0F, 5.0F, -2.0F);
        head.addChild(right_FrontMidTentacle);
        setRotationAngle(right_FrontMidTentacle, 0.0F, 1.5708F, 0.0F);
        right_FrontMidTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        right_FrontMidTentacleEnd = new AdvancedModelBox(this, "right_FrontMidTentacleEnd");
        right_FrontMidTentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        right_FrontMidTentacle.addChild(right_FrontMidTentacleEnd);
        right_FrontMidTentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        left_BackMidTentacle = new AdvancedModelBox(this, "left_BackMidTentacle");
        left_BackMidTentacle.setRotationPoint(4.0F, 5.0F, 2.0F);
        head.addChild(left_BackMidTentacle);
        setRotationAngle(left_BackMidTentacle, 0.0F, -1.3963F, 0.0F);
        left_BackMidTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        left_BackMidTentacleEnd = new AdvancedModelBox(this, "left_BackMidTentacleEnd");
        left_BackMidTentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        left_BackMidTentacle.addChild(left_BackMidTentacleEnd);
        left_BackMidTentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        right_BackMidTentacle = new AdvancedModelBox(this, "right_BackMidTentacle");
        right_BackMidTentacle.setRotationPoint(-4.0F, 5.0F, 2.0F);
        head.addChild(right_BackMidTentacle);
        setRotationAngle(right_BackMidTentacle, 0.0F, 1.3963F, 0.0F);
        right_BackMidTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        right_BackMidTentacleEnd = new AdvancedModelBox(this, "right_BackMidTentacleEnd");
        right_BackMidTentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        right_BackMidTentacle.addChild(right_BackMidTentacleEnd);
        right_BackMidTentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        left_BackTentacle = new AdvancedModelBox(this, "left_BackTentacle");
        left_BackTentacle.setRotationPoint(2.0F, 5.0F, 4.0F);
        head.addChild(left_BackTentacle);
        setRotationAngle(left_BackTentacle, 0.0F, 3.1416F, 0.0F);
        left_BackTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        left_BackTentacleEnd = new AdvancedModelBox(this, "left_BackTentacleEnd");
        left_BackTentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        left_BackTentacle.addChild(left_BackTentacleEnd);
        left_BackTentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        right_BackTentacle = new AdvancedModelBox(this, "right_BackTentacle");
        right_BackTentacle.setRotationPoint(-2.0F, 5.0F, 4.0F);
        head.addChild(right_BackTentacle);
        setRotationAngle(right_BackTentacle, 0.0F, -3.1416F, 0.0F);
        right_BackTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        right_BackTentacleEnd = new AdvancedModelBox(this, "right_BackTentacleEnd");
        right_BackTentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        right_BackTentacle.addChild(right_BackTentacleEnd);
        right_BackTentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        right_FrontTentacle = new AdvancedModelBox(this, "right_FrontTentacle");
        right_FrontTentacle.setRotationPoint(-1.5F, 5.0F, -4.0F);
        head.addChild(right_FrontTentacle);
        right_FrontTentacle.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        right_tentacleEnd = new AdvancedModelBox(this, "right_tentacleEnd");
        right_tentacleEnd.setRotationPoint(0.0F, 20.0F, 0.0F);
        right_FrontTentacle.addChild(right_tentacleEnd);
        right_tentacleEnd.setTextureOffset(18, 70).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(3.0F, 5.0F, 0.0F);
        head.addChild(left_arm);
        setRotationAngle(left_arm, 0.0F, -1.5708F, 0.0F);
        left_arm.setTextureOffset(32, 66).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);

        left_arm2 = new AdvancedModelBox(this, "left_arm2");
        left_arm2.setRotationPoint(0.0F, 20.0F, 0.0F);
        left_arm.addChild(left_arm2);
        left_arm2.setTextureOffset(45, 60).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 35.0F, 2.0F, 0.0F, false);

        left_arm3 = new AdvancedModelBox(this, "left_arm3");
        left_arm3.setRotationPoint(0.0F, 35.0F, 0.0F);
        left_arm2.addChild(left_arm3);
        left_arm3.setTextureOffset(45, 60).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 35.0F, 2.0F, 0.0F, false);

        left_arm4 = new AdvancedModelBox(this, "left_arm4");
        left_arm4.setRotationPoint(0.0F, 35.0F, 0.0F);
        left_arm3.addChild(left_arm4);
        left_arm4.setTextureOffset(45, 60).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 35.0F, 2.0F, 0.0F, false);

        left_hand = new AdvancedModelBox(this, "left_hand");
        left_hand.setRotationPoint(0.0F, 35.0F, 0.0F);
        left_arm4.addChild(left_hand);
        left_hand.setTextureOffset(54, 60).addBox(-3.0F, 0.0F, -1.3F, 6.0F, 14.0F, 3.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-3.0F, 5.0F, 0.0F);
        head.addChild(right_arm);
        setRotationAngle(right_arm, 0.0F, 1.5708F, 0.0F);
        right_arm.setTextureOffset(32, 66).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, true);

        right_arm2 = new AdvancedModelBox(this, "right_arm2");
        right_arm2.setRotationPoint(0.0F, 20.0F, 0.0F);
        right_arm.addChild(right_arm2);
        right_arm2.setTextureOffset(45, 60).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 35.0F, 2.0F, 0.0F, true);

        right_arm3 = new AdvancedModelBox(this, "right_arm3");
        right_arm3.setRotationPoint(0.0F, 35.0F, 0.0F);
        right_arm2.addChild(right_arm3);
        right_arm3.setTextureOffset(45, 60).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 35.0F, 2.0F, 0.0F, true);

        right_arm4 = new AdvancedModelBox(this, "right_arm4");
        right_arm4.setRotationPoint(0.0F, 35.0F, 0.0F);
        right_arm3.addChild(right_arm4);
        right_arm4.setTextureOffset(45, 60).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 35.0F, 2.0F, 0.0F, true);

        right_hand = new AdvancedModelBox(this, "right_hand");
        right_hand.setRotationPoint(0.0F, 35.0F, 0.0F);
        right_arm4.addChild(right_hand);
        right_hand.setTextureOffset(54, 60).addBox(-3.0F, 0.0F, -1.3F, 6.0F, 14.0F, 3.0F, 0.0F, true);

        left_eye = new AdvancedModelBox(this, "left_eye");
        left_eye.setRotationPoint(5.9F, 0.5F, 0.0F);
        head.addChild(left_eye);
        left_eye.setTextureOffset(53, 19).addBox(-1.5F, -3.5F, -3.5F, 3.0F, 7.0F, 7.0F, 0.0F, false);

        left_pupil_pivot = new AdvancedModelBox(this, "left_pupil_pivot");
        left_pupil_pivot.setRotationPoint(1.55F, 0.0F, 0.0F);
        left_eye.addChild(left_pupil_pivot);

        left_pupil = new AdvancedModelBox(this, "left_pupil");
        left_pupil_pivot.addChild(left_pupil);
        left_pupil.setTextureOffset(0, 0).addBox(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, false);

        right_eye = new AdvancedModelBox(this, "right_eye");
        right_eye.setRotationPoint(-5.9F, 0.5F, 0.0F);
        head.addChild(right_eye);
        right_eye.setTextureOffset(53, 19).addBox(-1.5F, -3.5F, -3.5F, 3.0F, 7.0F, 7.0F, 0.0F, true);

        right_pupil_pivot = new AdvancedModelBox(this, "right_pupil_pivot");
        right_pupil_pivot.setRotationPoint(-1.55F, 0.0F, 0.0F);
        right_eye.addChild(right_pupil_pivot);

        right_pupil = new AdvancedModelBox(this, "right_pupil");
        right_pupil_pivot.addChild(right_pupil);
        right_pupil.setTextureOffset(0, 0).addBox(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, true);

        mantle = new AdvancedModelBox(this, "mantle");
        mantle.setRotationPoint(0.0F, -5.0F, 0.0F);
        head.addChild(mantle);
        mantle.setTextureOffset(0, 0).addBox(-7.0F, -31.0F, -6.0F, 14.0F, 32.0F, 12.0F, 0.0F, false);

        mantle_end = new AdvancedModelBox(this, "mantle_end");
        mantle_end.setRotationPoint(0.0F, -31.0F, 0.0F);
        mantle.addChild(mantle_end);
        mantle_end.setTextureOffset(53, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);

        left_membrane = new AdvancedModelBox(this, "left_membrane");
        left_membrane.setRotationPoint(0.0F, -2.0F, 0.0F);
        mantle_end.addChild(left_membrane);
        left_membrane.setTextureOffset(0, 45).addBox(0.0F, -12.0F, 0.0F, 17.0F, 24.0F, 0.0F, 0.0F, false);

        right_membrane = new AdvancedModelBox(this, "right_membrane");
        right_membrane.setRotationPoint(0.0F, -2.0F, 0.0F);
        mantle_end.addChild(right_membrane);
        right_membrane.setTextureOffset(0, 45).addBox(-17.0F, -12.0F, 0.0F, 17.0F, 24.0F, 0.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityGiantSquid entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float swimSpeed = 0.2F;
        float swimDegree = 0.4F;
        float idleSpeed = 0.05F;
        float idleDegree = 0.02F;
        float flailSpeed = 0.3F;
        float flailDegree = 0.4F;
        float partialTick = ageInTicks - entity.tickCount;
        float dryProgress = entity.prevDryProgress + (entity.dryProgress - entity.prevDryProgress) * partialTick;
        float capturedProgress = entity.prevCapturedProgress + (entity.capturedProgress - entity.prevCapturedProgress) * partialTick;
        float grabProgress = entity.prevGrabProgress + (entity.grabProgress - entity.prevGrabProgress) * partialTick;
        float pitch = entity.prevSquidPitch + (entity.getSquidPitch() - entity.prevSquidPitch) * partialTick;
        float f = (pitch - 90) * Mth.DEG_TO_RAD;
        float f1 = (float) Math.sin(swimSpeed * limbSwing) * swimDegree * limbSwingAmount;
        float stretchy = 0.9F + 0.2F * f1;
        float stretchyXZ = 0.95F - 0.1F * f1;
        float flailAmount = capturedProgress * 0.2F;
        this.head.rotateAngleX += f;
        this.right_pupil_pivot.rotateAngleX -= f;
        this.left_pupil_pivot.rotateAngleX -= f;
        this.mantle.setScale(stretchyXZ, stretchy, stretchyXZ - (dryProgress * 0.05F));
        this.mantle.setShouldScaleChildren(true);
        float contractFromGrab = 1F - (0.2F * grabProgress);
        this.right_arm2.rotateAngleX -= contractFromGrab * getArmRot(entity, 2, partialTick, false);
        this.left_arm2.rotateAngleX += contractFromGrab * getArmRot(entity, 2, partialTick, false);
        this.right_arm2.rotateAngleX -= contractFromGrab * getArmRot(entity, 4, partialTick, false);
        this.left_arm2.rotateAngleX += contractFromGrab * getArmRot(entity, 4, partialTick, false);
        this.right_arm3.rotateAngleX -= contractFromGrab * getArmRot(entity, 8, partialTick, false);
        this.left_arm3.rotateAngleX += contractFromGrab * getArmRot(entity, 8, partialTick, false);
        this.right_arm4.rotateAngleX -= contractFromGrab * getArmRot(entity, 12, partialTick, false);
        this.left_arm4.rotateAngleX += contractFromGrab * getArmRot(entity, 12, partialTick, false);

        this.right_arm2.rotateAngleZ += contractFromGrab * getArmRot(entity, 2, partialTick, true);
        this.left_arm2.rotateAngleZ -= contractFromGrab * getArmRot(entity, 2, partialTick, true);
        this.right_arm2.rotateAngleZ += contractFromGrab * getArmRot(entity, 4, partialTick, true);
        this.left_arm2.rotateAngleZ -= contractFromGrab * getArmRot(entity, 4, partialTick, true);
        this.right_arm3.rotateAngleZ += contractFromGrab * getArmRot(entity, 6, partialTick, true);
        this.left_arm3.rotateAngleZ -= contractFromGrab * getArmRot(entity, 6, partialTick, true);
        this.right_arm4.rotateAngleZ += contractFromGrab * getArmRot(entity, 8, partialTick, true);
        this.left_arm4.rotateAngleZ -= contractFromGrab * getArmRot(entity, 8, partialTick, true);

        progressRotationPrev(mantle_end, dryProgress, Maths.rad(-10), 0, 0, 5F);
        progressRotationPrev(right_membrane, dryProgress, 0, Maths.rad(20), 0, 5F);
        progressRotationPrev(left_membrane, dryProgress, 0, Maths.rad(-20), 0, 5F);
        progressRotationPrev(mantle, dryProgress, 0, 0, Maths.rad(15), 5F);
        progressRotationPrev(left_FrontTentacle, dryProgress, Maths.rad(10), 0, Maths.rad(-25), 5F);
        progressRotationPrev(left_FrontTentacleEnd, dryProgress, Maths.rad(5), 0, Maths.rad(-15), 5F);
        progressRotationPrev(right_FrontTentacle, dryProgress, Maths.rad(10), 0, Maths.rad(15), 5F);
        progressRotationPrev(right_tentacleEnd, dryProgress, Maths.rad(5), 0, Maths.rad(15), 5F);
        progressRotationPrev(left_BackTentacle, dryProgress, 0, 0, Maths.rad(-15), 5F);
        progressRotationPrev(left_BackTentacleEnd, dryProgress, 0, 0, Maths.rad(-15), 5F);
        progressRotationPrev(right_BackTentacle, dryProgress, 0, 0, Maths.rad(20), 5F);
        progressRotationPrev(right_BackTentacleEnd, dryProgress, 0, 0, Maths.rad(25), 5F);
        progressRotationPrev(right_BackMidTentacle, dryProgress, Maths.rad(20), 0, Maths.rad(20), 5F);
        progressRotationPrev(left_BackMidTentacle, dryProgress, Maths.rad(15), 0, Maths.rad(-50), 5F);
        progressRotationPrev(left_FrontMidTentacle, dryProgress, Maths.rad(35), Maths.rad(15), Maths.rad(-50), 5F);
        progressRotationPrev(left_FrontMidTentacleEnd, dryProgress, Maths.rad(35), Maths.rad(-15), 0, 5F);
        progressRotationPrev(right_FrontMidTentacle, dryProgress, Maths.rad(35), Maths.rad(-15), Maths.rad(50), 5F);
        progressRotationPrev(right_FrontMidTentacleEnd, dryProgress, Maths.rad(35), Maths.rad(15), 0, 5F);
        progressRotationPrev(left_arm, dryProgress, Maths.rad(10), Maths.rad(90), 0, 5F);
        progressRotationPrev(left_arm2, dryProgress, Maths.rad(-10), 0, Maths.rad(2), 5F);
        progressRotationPrev(left_arm3, dryProgress, 0, 0, Maths.rad(-20), 5F);
        progressRotationPrev(left_arm4, dryProgress, 0, 0, Maths.rad(-8), 5F);
        progressRotationPrev(right_arm, dryProgress, Maths.rad(10), Maths.rad(-90), 0, 5F);
        progressRotationPrev(right_arm2, dryProgress, Maths.rad(-10), 0, Maths.rad(-4), 5F);
        progressRotationPrev(right_arm3, dryProgress, 0, 0, Maths.rad(-12), 5F);
        progressRotationPrev(right_arm4, dryProgress, 0, 0, Maths.rad(20), 5F);
        progressRotationPrev(left_arm, grabProgress, Maths.rad(-110), 0, 0, 5F);
        progressRotationPrev(right_arm, grabProgress, Maths.rad(-110), 0, 0, 5F);
        progressRotationPrev(left_arm2, grabProgress, Maths.rad(40), 0, 0, 5F);
        progressRotationPrev(right_arm2, grabProgress, Maths.rad(40), 0, 0, 5F);
        progressRotationPrev(left_arm3, grabProgress, Maths.rad(100), 0, 0, 5F);
        progressRotationPrev(right_arm3, grabProgress, Maths.rad(100), 0, 0, 5F);
        progressRotationPrev(left_arm4, grabProgress, Maths.rad(70), 0, 0, 5F);
        progressRotationPrev(right_arm4, grabProgress, Maths.rad(70), 0, 0, 5F);
        progressRotationPrev(left_hand, grabProgress, Maths.rad(-120), 0, 0, 5F);
        progressRotationPrev(right_hand, grabProgress, Maths.rad(-120), 0, 0, 5F);
        progressPositionPrev(left_arm, grabProgress, 0, 2, 0, 5F);
        progressPositionPrev(right_arm, grabProgress, 0, 2, 0, 5F);
        progressPositionPrev(left_arm4, grabProgress, 0, -2, 1, 5F);
        progressPositionPrev(right_arm4, grabProgress, 0, -2, 1, 5F);
        progressRotationPrev(left_FrontTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(-20), 0, 5F);
        progressRotationPrev(right_FrontTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(20), 0, 5F);
        progressRotationPrev(left_BackTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(20), 0, 5F);
        progressRotationPrev(right_BackTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(-20), 0, 5F);
        progressRotationPrev(left_FrontMidTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(20), 0, 5F);
        progressRotationPrev(right_FrontMidTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(-20), 0, 5F);
        progressRotationPrev(left_BackMidTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(-20), 0, 5F);
        progressRotationPrev(right_BackMidTentacle, Math.max(grabProgress, capturedProgress), Maths.rad(-20), Maths.rad(20), 0, 5F);
        progressRotationPrev(mantle, capturedProgress, Maths.rad(-20), 0, 0, 5F);
        progressPositionPrev(mantle, capturedProgress, 0, -2, 0, 5F);
        progressRotationPrev(head, capturedProgress, Maths.rad(20), 0, 0, 5F);

        this.swing(right_membrane, swimSpeed, swimDegree, false, 2F, 0F, limbSwing, limbSwingAmount);
        this.swing(left_membrane, swimSpeed, swimDegree, true, 2F, 0F, limbSwing, limbSwingAmount);

        this.walk(left_FrontTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.flap(left_FrontTentacle, swimSpeed, swimDegree, true, 0F, 0.35F, limbSwing, limbSwingAmount);
        this.walk(right_FrontTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.flap(right_FrontTentacle, swimSpeed, swimDegree, false, 0F, 0.35F, limbSwing, limbSwingAmount);

        this.walk(left_BackTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.flap(left_BackTentacle, swimSpeed, swimDegree, true, 0F, 0.35F, limbSwing, limbSwingAmount);
        this.walk(right_BackTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.flap(right_BackTentacle, swimSpeed, swimDegree, false, 0F, 0.35F, limbSwing, limbSwingAmount);

        this.walk(right_BackMidTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.swing(right_BackMidTentacle, swimSpeed, swimDegree, true, 0F, -0.75F, limbSwing, limbSwingAmount);
        this.walk(right_FrontMidTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.swing(right_FrontMidTentacle, swimSpeed, swimDegree, false, 0F, -0.75F, limbSwing, limbSwingAmount);

        this.walk(left_BackMidTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.swing(left_BackMidTentacle, swimSpeed, swimDegree, false, 0F, -0.75F, limbSwing, limbSwingAmount);
        this.walk(left_FrontMidTentacle, swimSpeed, swimDegree, true, 0F, 0.5F, limbSwing, limbSwingAmount);
        this.swing(left_FrontMidTentacle, swimSpeed, swimDegree, true, 0F, -0.75F, limbSwing, limbSwingAmount);

        this.walk(left_FrontTentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_tentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_BackTentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_BackTentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_BackMidTentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_BackMidTentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_FrontMidTentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_FrontMidTentacleEnd, swimSpeed, swimDegree, true, -2F, 0F, limbSwing, limbSwingAmount);

        this.walk(left_arm, swimSpeed, swimDegree * 0.1F, true, 0F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(right_arm, swimSpeed, swimDegree * 0.1F, true, 0F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(left_arm2, swimSpeed, swimDegree * 0.1F, false, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_arm2, swimSpeed, swimDegree * 0.1F, false, -2F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_arm3, swimSpeed, swimDegree * 0.1F, true, -4F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_arm3, swimSpeed, swimDegree * 0.1F, true, -4F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_arm4, swimSpeed, swimDegree * 0.2F, true, -6F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_arm4, swimSpeed, swimDegree * 0.2F, true, -6F, 0F, limbSwing, limbSwingAmount);

        this.walk(left_arm, flailSpeed, flailDegree, true, 2F, 0.7F, ageInTicks, flailAmount);
        this.swing(left_arm, flailSpeed, flailDegree, true, 6F, 0.7F, ageInTicks, flailAmount);
        this.walk(right_arm, flailSpeed, flailDegree, false, 2F, -0.7F, ageInTicks, flailAmount);
        this.swing(right_arm, flailSpeed, flailDegree, false, 6F, -0.7F, ageInTicks, flailAmount);
        this.walk(left_arm2, flailSpeed, flailDegree, true, 0F, 0.7F, ageInTicks, flailAmount);
        this.walk(right_arm2, flailSpeed, flailDegree, false, 0F, -0.7F, ageInTicks, flailAmount);
        this.walk(left_arm3, flailSpeed, flailDegree, true, 0F, 0.7F, ageInTicks, flailAmount);
        this.walk(right_arm3, flailSpeed, flailDegree, false, 0F, -0.7F, ageInTicks, flailAmount);
        this.walk(left_arm4, flailSpeed, flailDegree, true, 0F, 0.2F, ageInTicks, flailAmount);
        this.walk(right_arm4, flailSpeed, flailDegree, false, 0F, -0.2F, ageInTicks, flailAmount);
        this.walk(left_hand, flailSpeed, flailDegree, true, 0F, 0.2F, ageInTicks, flailAmount);
        this.walk(right_hand, flailSpeed, flailDegree, false, 0F, -0.2F, ageInTicks, flailAmount);
        this.walk(left_FrontTentacle, flailSpeed, flailDegree * 0.5F, false, 0F, -0.5F, ageInTicks, flailAmount);
        this.flap(left_FrontTentacle, flailSpeed, flailDegree * 0.5F, false, 0F, -0.15F, ageInTicks, flailAmount);
        this.walk(left_FrontTentacleEnd, flailSpeed, flailDegree * 0.5F, false, 0F, -0.85F, ageInTicks, flailAmount);
        this.walk(right_FrontTentacle, flailSpeed, flailDegree * 0.5F, false, 2F, -0.5F, ageInTicks, flailAmount);
        this.flap(right_FrontTentacle, flailSpeed, flailDegree * 0.5F, true, 2F, -0.15F, ageInTicks, flailAmount);
        this.walk(right_tentacleEnd, flailSpeed, flailDegree * 0.5F, false, 2F, -0.85F, ageInTicks, flailAmount);
        this.walk(left_BackTentacle, flailSpeed, flailDegree * 0.5F, false, 4F, -0.5F, ageInTicks, flailAmount);
        this.flap(left_BackTentacle, flailSpeed, flailDegree * 0.5F, false, 4F, -0.15F, ageInTicks, flailAmount);
        this.walk(left_BackTentacleEnd, flailSpeed, flailDegree * 0.5F, false, 4F, -0.85F, ageInTicks, flailAmount);
        this.walk(right_BackTentacle, flailSpeed, flailDegree * 0.5F, false, 8F, -0.5F, ageInTicks, flailAmount);
        this.flap(right_BackTentacle, flailSpeed, flailDegree * 0.5F, true, 8F, -0.15F, ageInTicks, flailAmount);
        this.walk(right_BackTentacleEnd, flailSpeed, flailDegree * 0.5F, false, 8F, -0.85F, ageInTicks, flailAmount);
        this.walk(left_BackMidTentacle, flailSpeed, flailDegree * 0.5F, false, 10F, -0.5F, ageInTicks, flailAmount);
        this.flap(left_BackMidTentacle, flailSpeed, flailDegree * 0.5F, false, 10F, -0.15F, ageInTicks, flailAmount);
        this.walk(left_BackMidTentacleEnd, flailSpeed, flailDegree * 0.5F, false, 10F, -0.85F, ageInTicks, flailAmount);
        this.walk(right_BackMidTentacle, flailSpeed, flailDegree * 0.5F, false, 12F, -0.5F, ageInTicks, flailAmount);
        this.flap(right_BackMidTentacle, flailSpeed, flailDegree * 0.5F, true, 12F, -0.15F, ageInTicks, flailAmount);
        this.walk(right_BackMidTentacleEnd, flailSpeed, flailDegree * 0.5F, false, 12F, -0.85F, ageInTicks, flailAmount);
        this.walk(left_FrontMidTentacle, flailSpeed, flailDegree * 0.5F, false, 14F, -0.5F, ageInTicks, flailAmount);
        this.flap(left_FrontMidTentacle, flailSpeed, flailDegree * 0.5F, false, 14F, -0.15F, ageInTicks, flailAmount);
        this.walk(left_FrontMidTentacleEnd, flailSpeed, flailDegree * 0.5F, false, 14F, -0.85F, ageInTicks, flailAmount);
        this.walk(right_FrontMidTentacle, flailSpeed, flailDegree * 0.5F, false, 16F, -0.5F, ageInTicks, flailAmount);
        this.flap(right_FrontMidTentacle, flailSpeed, flailDegree * 0.5F, true, 16F, -0.15F, ageInTicks, flailAmount);
        this.walk(right_FrontMidTentacleEnd, flailSpeed, flailDegree * 0.5F, false, 16F, -0.85F, ageInTicks, flailAmount);

        this.walk(left_FrontTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.flap(left_FrontTentacle, idleSpeed, idleDegree, true, 0F, 0.035F, ageInTicks, 1);
        this.walk(right_FrontTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.flap(right_FrontTentacle, idleSpeed, idleDegree, false, 0F, 0.035F, ageInTicks, 1);

        this.walk(left_BackTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.flap(left_BackTentacle, idleSpeed, idleDegree, true, 0F, 0.035F, ageInTicks, 1);
        this.walk(right_BackTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.flap(right_BackTentacle, idleSpeed, idleDegree, false, 0F, 0.035F, ageInTicks, 1);

        this.walk(right_BackMidTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.swing(right_BackMidTentacle, idleSpeed, idleDegree, true, 0F, -0.075F, ageInTicks, 1);
        this.walk(right_FrontMidTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.swing(right_FrontMidTentacle, idleSpeed, idleDegree, false, 0F, -0.075F, ageInTicks, 1);

        this.walk(left_BackMidTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.swing(left_BackMidTentacle, idleSpeed, idleDegree, false, 0F, -0.075F, ageInTicks, 1);
        this.walk(left_FrontMidTentacle, idleSpeed, idleDegree, true, 0F, 0.05F, ageInTicks, 1);
        this.swing(left_FrontMidTentacle, idleSpeed, idleDegree, true, 0F, -0.075F, ageInTicks, 1);

        this.walk(left_FrontTentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);
        this.walk(right_tentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);
        this.walk(left_BackTentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);
        this.walk(right_BackTentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);
        this.walk(right_BackMidTentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);
        this.walk(left_BackMidTentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);
        this.walk(right_FrontMidTentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);
        this.walk(left_FrontMidTentacleEnd, idleSpeed, idleDegree, true, -2F, 0F, ageInTicks, 1);

        this.walk(left_arm, idleSpeed, idleDegree * 0.1F, true, 0F, 0.01F, ageInTicks, 1);
        this.walk(right_arm, idleSpeed, idleDegree * 0.1F, true, 0F, 0.01F, ageInTicks, 1);
        this.walk(left_arm2, idleSpeed, idleDegree * 0.1F, false, -2F, 0F, ageInTicks, 1);
        this.walk(right_arm2, idleSpeed, idleDegree * 0.1F, false, -2F, 0F, ageInTicks, 1);
        this.walk(left_arm3, idleSpeed, idleDegree * 0.1F, true, -4F, 0F, ageInTicks, 1);
        this.walk(right_arm3, idleSpeed, idleDegree * 0.1F, true, -4F, 0F, ageInTicks, 1);
        this.walk(left_arm4, idleSpeed, idleDegree * 0.2F, true, -6F, 0F, ageInTicks, 1);
        this.walk(right_arm4, idleSpeed, idleDegree * 0.2F, true, -6F, 0F, ageInTicks, 1);

        this.left_pupil.rotateAngleX += f;
        this.right_pupil.rotateAngleX += f;
        if(grabProgress >= 5F){
            this.walk(beak, 0.7F, 0.35F, true, 0F, 0F, ageInTicks, 1);
            this.bob(beak, 0.7F, 0.35F, true, ageInTicks, 1);
        }
        Entity look = Minecraft.getInstance().getCameraEntity();
        if (look != null) {
            Vec3 vector3d = look.getEyePosition(partialTick);
            Vec3 vector3d1 = entity.getEyePosition(partialTick);
            float dist = Mth.clamp((float) vector3d.subtract(vector3d1).length() * 0.2F, 0.4F, 1.0F);
            float eyeScale = 1.4F - dist;
            float maxEyeDist = 0.7F;
            double d0 = (vector3d.y - vector3d1.y);
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d.x - vector3d1.x, 0.0D, vector3d.z - vector3d1.z)).normalize();
            double d1 = vector3d2.dot(vector3d3);
            double eyeXz = Mth.sqrt((float) Math.abs(d1)) * -2F * (float) Math.signum(d1);
            this.left_pupil.setScale(eyeScale, eyeScale, eyeScale);
            this.left_pupil.rotationPointZ -= (float) Mth.clamp(-eyeXz, -maxEyeDist / eyeScale, maxEyeDist / eyeScale);
            this.left_pupil.rotationPointY += (float) Mth.clamp(-d0, -maxEyeDist / eyeScale, maxEyeDist / eyeScale);
            this.right_pupil.setScale(eyeScale, eyeScale, eyeScale);
            this.right_pupil.rotationPointZ += (float) Mth.clamp(eyeXz, -maxEyeDist / eyeScale, maxEyeDist / eyeScale);
            this.right_pupil.rotationPointY += (float) Mth.clamp(-d0, -maxEyeDist / eyeScale, maxEyeDist / eyeScale);
        }
    }

    private float getArmRot(EntityGiantSquid entity, int offset, float partialTick, boolean pitch) {
        float rotWrap = Mth.wrapDegrees(entity.getRingBuffer(offset, partialTick, pitch) - entity.getRingBuffer(0, partialTick, pitch));
        return (Mth.clamp(rotWrap, -50, 50) * 0.4F) * Mth.DEG_TO_RAD;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, head, beak, right_pupil_pivot, left_pupil_pivot, left_FrontTentacle, left_FrontTentacleEnd, left_FrontMidTentacle, left_FrontMidTentacleEnd, right_FrontMidTentacle, right_FrontMidTentacleEnd, left_BackMidTentacle, left_BackMidTentacleEnd, right_BackMidTentacle, right_BackMidTentacleEnd, left_BackTentacle, left_BackTentacleEnd, right_BackTentacle, right_BackTentacleEnd, right_FrontTentacle, right_tentacleEnd, left_arm, left_arm2, left_arm3, left_arm4, left_hand, right_arm, right_arm2, right_arm3, right_arm4, right_hand, left_eye, left_pupil, right_eye, right_pupil, mantle, mantle_end, left_membrane, right_membrane);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}