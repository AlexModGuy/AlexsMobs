package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityRockyRoller;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class ModelRockyRoller extends AdvancedEntityModel<EntityRockyRoller> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox HSpikes_r1;
    private final AdvancedModelBox HSpikes_r2;
    private final AdvancedModelBox VSpikes_r1;
    private final AdvancedModelBox VSpikes_r2;
    private final AdvancedModelBox VSpikes_r3;
    private final AdvancedModelBox VSpikes_r4;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox head;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox right_arm;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox right_leg;

    public ModelRockyRoller() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -16.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 29).addBox(-9.0F, -8.0F, -10.0F, 18.0F, 16.0F, 20.0F, 0.0F, false);
        body.setTextureOffset(64, 85).addBox(0.0F, 1.0F, -13.0F, 15.0F, 0.0F, 28.0F, 0.0F, false);
        body.setTextureOffset(64, 85).addBox(-15.0F, 1.0F, -13.0F, 15.0F, 0.0F, 28.0F, 0.0F, true);

        HSpikes_r1 = new AdvancedModelBox(this, "HSpikes_r1");
        HSpikes_r1.setRotationPoint(-0.5F, -4.0F, 1.0F);
        body.addChild(HSpikes_r1);
        setRotationAngle(HSpikes_r1, 0.0F, 0.0F, 0.0873F);
        HSpikes_r1.setTextureOffset(0, 0).addBox(-14.5F, 0.0F, -14.0F, 15.0F, 0.0F, 28.0F, 0.0F, true);

        HSpikes_r2 = new AdvancedModelBox(this, "HSpikes_r2");
        HSpikes_r2.setRotationPoint(0.5F, -4.0F, 1.0F);
        body.addChild(HSpikes_r2);
        setRotationAngle(HSpikes_r2, 0.0F, 0.0F, -0.0873F);
        HSpikes_r2.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -14.0F, 15.0F, 0.0F, 28.0F, 0.0F, false);

        VSpikes_r1 = new AdvancedModelBox(this, "VSpikes_r1");
        VSpikes_r1.setRotationPoint(-6.0F, 4.0F, 1.0F);
        body.addChild(VSpikes_r1);
        setRotationAngle(VSpikes_r1, 0.0F, 0.0F, -0.1745F);
        VSpikes_r1.setTextureOffset(1, 69).addBox(0.0F, -18.0F, -14.0F, 0.0F, 18.0F, 28.0F, 0.0F, false);

        VSpikes_r2 = new AdvancedModelBox(this, "VSpikes_r2");
        VSpikes_r2.setRotationPoint(6.0F, 4.0F, 1.0F);
        body.addChild(VSpikes_r2);
        setRotationAngle(VSpikes_r2, 0.0F, 0.0F, 0.1745F);
        VSpikes_r2.setTextureOffset(1, 69).addBox(0.0F, -18.0F, -14.0F, 0.0F, 18.0F, 28.0F, 0.0F, true);

        VSpikes_r3 = new AdvancedModelBox(this, "VSpikes_r3");
        VSpikes_r3.setRotationPoint(-2.0F, 2.0F, 1.0F);
        body.addChild(VSpikes_r3);
        setRotationAngle(VSpikes_r3, 0.0F, 0.0F, -0.0436F);
        VSpikes_r3.setTextureOffset(49, 38).addBox(0.0F, -16.0F, -14.0F, 0.0F, 18.0F, 28.0F, 0.0F, true);

        VSpikes_r4 = new AdvancedModelBox(this, "VSpikes_r4");
        VSpikes_r4.setRotationPoint(2.0F, 2.0F, 1.0F);
        body.addChild(VSpikes_r4);
        setRotationAngle(VSpikes_r4, 0.0F, 0.0F, 0.0436F);
        VSpikes_r4.setTextureOffset(49, 38).addBox(0.0F, -16.0F, -14.0F, 0.0F, 18.0F, 28.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, 7.0F, 10.0F);
        body.addChild(tail);
        setRotationAngle(tail, -0.6109F, 0.0F, 0.0F);
        tail.setTextureOffset(59, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 12.0F, 0.0F, false);
        tail.setTextureOffset(19, 8).addBox(0.0F, -4.0F, 8.0F, 0.0F, 2.0F, 4.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, 6.0F, -10.6F);
        body.addChild(head);
        setRotationAngle(head, 0.2618F, 0.0F, 0.0F);
        head.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(21, 15).addBox(0.0F, -5.0F, -4.0F, 0.0F, 2.0F, 3.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(4.0F, 8.0F, -7.0F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(0, 29).addBox(-1.0F, 0.0F, -1.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);
        left_arm.setTextureOffset(18, 0).addBox(0.0F, 3.0F, 2.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-4.0F, 8.0F, -7.0F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(0, 29).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 5.0F, 3.0F, 0.0F, true);
        right_arm.setTextureOffset(18, 0).addBox(-2.0F, 3.0F, 2.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(6.0F, 9.0F, 6.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(0, 12).addBox(-3.0F, -1.0F, -3.0F, 5.0F, 8.0F, 5.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-6.0F, 9.0F, 6.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(0, 12).addBox(-2.0F, -1.0F, -3.0F, 5.0F, 8.0F, 5.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, HSpikes_r1, HSpikes_r2, head, VSpikes_r1, VSpikes_r2, VSpikes_r3, VSpikes_r4, tail, left_arm, right_arm, left_leg, right_leg);
    }

    @Override
    public void setupAnim(EntityRockyRoller entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float rollProgress = entity.prevRollProgress + (entity.rollProgress - entity.prevRollProgress) * partialTick;
        float walkProgress = 5F - rollProgress;
        float walkSpeed = 1.2F;
        float walkDegree = 0.8F;
        float idleSpeed = 0.15F;
        float idleDegree = 0.3F;
        float rollDegree = 0.2F;
        float timeRolling = entity.rollCounter + partialTick;
        progressPositionPrev(body, rollProgress, 0, 6F, 0, 5f);
        progressRotationPrev(left_arm, walkProgress * limbSwingAmount, (float)Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(right_arm, walkProgress * limbSwingAmount, (float)Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(tail, walkProgress * limbSwingAmount, (float)Math.toRadians(30), 0, 0, 5F);
        progressPositionPrev(tail, walkProgress * limbSwingAmount, 0, -1, -1, 5f);
        if(entity.isRolling()){
            body.rotateAngleX = timeRolling * 0.2F * rollProgress * rollDegree;
            entity.clientRoll = body.rotateAngleX;
            this.bob(body, rollDegree, 10, true, timeRolling, 0.2F * rollProgress);
        }else{
            float rollDeg = (float)Mth.wrapDegrees(Math.toDegrees(entity.clientRoll));
            body.rotateAngleX = rollProgress * 0.2F * (float)Math.toRadians(rollDeg);
        }
        this.swing(tail, idleSpeed, idleDegree, false, 1F, 0F, ageInTicks, 1);
        this.bob(head, idleSpeed * 0.5F, idleDegree * 1.5F, false, ageInTicks, 1);
        this.bob(left_arm, idleSpeed * 0.25F, idleDegree * 2F, true, ageInTicks, 1);
        this.bob(right_arm, idleSpeed * 0.25F, idleDegree * 2F, true, ageInTicks, 1);
        this.walk(right_arm, idleSpeed, idleDegree, true, 1F, 0.1F, ageInTicks, 1);
        this.walk(left_arm, idleSpeed, idleDegree, false, 1F, -0.1F, ageInTicks, 1);
        this.walk(right_leg, walkSpeed, walkDegree * 1.25F, false, 0F, 0.2F, limbSwing, limbSwingAmount);
        this.walk(left_leg, walkSpeed, walkDegree * 1.25F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
        this.bob(right_leg, walkSpeed, walkDegree * 3, false, limbSwing, limbSwingAmount);
        this.bob(left_leg, -walkSpeed, walkDegree * 3, false, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed * 0.8F, walkDegree, true, limbSwing, limbSwingAmount * walkProgress * 0.2F);
        this.walk(tail, walkSpeed, walkDegree * 0.35F, false, 3F, 0F, limbSwing, limbSwingAmount);
        head.rotateAngleY += netHeadYaw / 57.295776F;
        head.rotateAngleX += headPitch / 57.295776F * 0.5F;

    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}