package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelMimicOctopus extends AdvancedEntityModel<EntityMimicOctopus> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox head;
    private final AdvancedModelBox tentacles_left;
    private final AdvancedModelBox armstart4_left;
    private final AdvancedModelBox armmid4_left;
    private final AdvancedModelBox armend4_left;
    private final AdvancedModelBox armstart3_left;
    private final AdvancedModelBox armmid3_left;
    private final AdvancedModelBox armend3_left;
    private final AdvancedModelBox armstart2_left;
    private final AdvancedModelBox armmid2_left;
    private final AdvancedModelBox armend2_left;
    private final AdvancedModelBox armstart1_left;
    private final AdvancedModelBox armmid1_left;
    private final AdvancedModelBox armend1_left;
    private final AdvancedModelBox tentacles_right;
    private final AdvancedModelBox armstart4_right;
    private final AdvancedModelBox armmid4_right;
    private final AdvancedModelBox armend4_right;
    private final AdvancedModelBox armstart3_right;
    private final AdvancedModelBox armmid3_right;
    private final AdvancedModelBox armend3_right;
    private final AdvancedModelBox armstart_right2;
    private final AdvancedModelBox armmid_right2;
    private final AdvancedModelBox armend_right2;
    private final AdvancedModelBox armstart1_right;
    private final AdvancedModelBox armmid1_right;
    private final AdvancedModelBox armend1_right;
    private final AdvancedModelBox mantle;
    private final AdvancedModelBox eye_spike_left;
    private final AdvancedModelBox eye_spike_right;
    private final AdvancedModelBox arm1_left_pivot;
    private final AdvancedModelBox arm2_left_pivot;
    private final AdvancedModelBox arm3_left_pivot;
    private final AdvancedModelBox arm4_left_pivot;
    private final AdvancedModelBox arm1_right_pivot;
    private final AdvancedModelBox arm2_right_pivot;
    private final AdvancedModelBox arm3_right_pivot;
    private final AdvancedModelBox arm4_right_pivot;

    public ModelMimicOctopus() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        head = new AdvancedModelBox(this);
        head.setPos(0.0F, 0.0F, 0.0F);
        root.addChild(head);
        head.setTextureOffset(18, 23).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        tentacles_left = new AdvancedModelBox(this);
        tentacles_left.setPos(0.0F, 0.0F, 0.0F);
        head.addChild(tentacles_left);

        arm4_left_pivot = new AdvancedModelBox(this);
        arm4_left_pivot.setPos(1.6F, -0.7F, -1.8F);
        tentacles_left.addChild(arm4_left_pivot);

        armstart4_left = new AdvancedModelBox(this);
        setRotationAngle(armstart4_left, 0.6981F, 0.0F, -1.5708F);
        armstart4_left.setTextureOffset(41, 30).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart4_left.setTextureOffset(30, 0).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);
        arm4_left_pivot.addChild(armstart4_left);

        armmid4_left = new AdvancedModelBox(this);
        armmid4_left.setPos(-1.0F, 6.0F, 0.0F);
        armstart4_left.addChild(armmid4_left);
        armmid4_left.setTextureOffset(0, 23).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid4_left.setTextureOffset(25, 10).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend4_left = new AdvancedModelBox(this);
        armend4_left.setPos(1.0F, 6.0F, -0.5F);
        armmid4_left.addChild(armend4_left);
        armend4_left.setTextureOffset(0, 14).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend4_left.setTextureOffset(39, 0).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        arm3_left_pivot = new AdvancedModelBox(this);
        arm3_left_pivot.setPos(1.6F, -1.0F, -1.8F);
        tentacles_left.addChild(arm3_left_pivot);

        armstart3_left = new AdvancedModelBox(this);
        arm3_left_pivot.addChild(armstart3_left);
        setRotationAngle(armstart3_left, 0.0F, 0.0F, -1.5708F);
        armstart3_left.setTextureOffset(7, 48).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart3_left.setTextureOffset(9, 37).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armmid3_left = new AdvancedModelBox(this);
        armmid3_left.setPos(-1.0F, 6.0F, 0.0F);
        armstart3_left.addChild(armmid3_left);
        armmid3_left.setTextureOffset(0, 48).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid3_left.setTextureOffset(0, 37).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend3_left = new AdvancedModelBox(this);
        armend3_left.setPos(1.0F, 6.0F, -0.5F);
        armmid3_left.addChild(armend3_left);
        armend3_left.setTextureOffset(37, 46).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend3_left.setTextureOffset(18, 43).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        arm2_left_pivot = new AdvancedModelBox(this);
        arm2_left_pivot.setPos(1.3F, -0.8F, -2.1F);
        tentacles_left.addChild(arm2_left_pivot);

        armstart2_left = new AdvancedModelBox(this);
        arm2_left_pivot.addChild(armstart2_left);
        setRotationAngle(armstart2_left, -0.5672F, 0.0F, -1.5708F);
        armstart2_left.setTextureOffset(43, 44).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart2_left.setTextureOffset(34, 11).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armmid2_left = new AdvancedModelBox(this);
        armmid2_left.setPos(-1.0F, 6.0F, 0.0F);
        armstart2_left.addChild(armmid2_left);
        armmid2_left.setTextureOffset(30, 44).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid2_left.setTextureOffset(31, 28).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend2_left = new AdvancedModelBox(this);
        armend2_left.setPos(1.0F, 6.0F, -0.5F);
        armmid2_left.addChild(armend2_left);
        armend2_left.setTextureOffset(21, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend2_left.setTextureOffset(40, 19).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        arm1_left_pivot = new AdvancedModelBox(this);
        arm1_left_pivot.setPos(0.6F, -0.9F, -2.5F);
        tentacles_left.addChild(arm1_left_pivot);

        armstart1_left = new AdvancedModelBox(this);
        arm1_left_pivot.addChild(armstart1_left);
        setRotationAngle(armstart1_left, -0.9163F, 0.0F, -1.5708F);
        armstart1_left.setTextureOffset(47, 19).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart1_left.setTextureOffset(36, 35).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armmid1_left = new AdvancedModelBox(this);
        armmid1_left.setPos(-1.0F, 6.0F, 0.0F);
        armstart1_left.addChild(armmid1_left);
        armmid1_left.setTextureOffset(47, 0).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid1_left.setTextureOffset(23, 35).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend1_left = new AdvancedModelBox(this);
        armend1_left.setPos(1.0F, 6.0F, -0.5F);
        armmid1_left.addChild(armend1_left);
        armend1_left.setTextureOffset(25, 46).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend1_left.setTextureOffset(43, 8).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        tentacles_right = new AdvancedModelBox(this);
        tentacles_right.setPos(0.0F, 0.0F, 0.0F);
        head.addChild(tentacles_right);

        arm4_right_pivot = new AdvancedModelBox(this);
        arm4_right_pivot.setPos(-1.6F, -0.7F, -1.8F);
        tentacles_right.addChild(arm4_right_pivot);

        armstart4_right = new AdvancedModelBox(this);
        arm4_right_pivot.addChild(armstart4_right);
        setRotationAngle(armstart4_right, 0.6981F, 0.0F, 1.5708F);
        armstart4_right.setTextureOffset(41, 30).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart4_right.setTextureOffset(30, 0).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid4_right = new AdvancedModelBox(this);
        armmid4_right.setPos(1.0F, 6.0F, 0.0F);
        armstart4_right.addChild(armmid4_right);
        armmid4_right.setTextureOffset(0, 23).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid4_right.setTextureOffset(25, 10).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend4_right = new AdvancedModelBox(this);
        armend4_right.setPos(-1.0F, 6.0F, -0.5F);
        armmid4_right.addChild(armend4_right);
        armend4_right.setTextureOffset(0, 14).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend4_right.setTextureOffset(39, 0).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        arm3_right_pivot = new AdvancedModelBox(this);
        arm3_right_pivot.setPos(-1.6F, -1.0F, -1.8F);
        tentacles_right.addChild(arm3_right_pivot);

        armstart3_right = new AdvancedModelBox(this);
        arm3_right_pivot.addChild(armstart3_right);
        setRotationAngle(armstart3_right, 0.0F, 0.0F, 1.5708F);
        armstart3_right.setTextureOffset(7, 48).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart3_right.setTextureOffset(9, 37).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid3_right = new AdvancedModelBox(this);
        armmid3_right.setPos(1.0F, 6.0F, 0.0F);
        armstart3_right.addChild(armmid3_right);
        armmid3_right.setTextureOffset(0, 48).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid3_right.setTextureOffset(0, 37).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend3_right = new AdvancedModelBox(this);
        armend3_right.setPos(-1.0F, 6.0F, -0.5F);
        armmid3_right.addChild(armend3_right);
        armend3_right.setTextureOffset(37, 46).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend3_right.setTextureOffset(18, 43).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        arm2_right_pivot = new AdvancedModelBox(this);
        arm2_right_pivot.setPos(-1.3F, -0.8F, -2.1F);
        tentacles_right.addChild(arm2_right_pivot);

        armstart_right2 = new AdvancedModelBox(this);
        arm2_right_pivot.addChild(armstart_right2);
        setRotationAngle(armstart_right2, -0.5672F, 0.0F, 1.5708F);
        armstart_right2.setTextureOffset(43, 44).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart_right2.setTextureOffset(34, 11).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid_right2 = new AdvancedModelBox(this);
        armmid_right2.setPos(1.0F, 6.0F, 0.0F);
        armstart_right2.addChild(armmid_right2);
        armmid_right2.setTextureOffset(30, 44).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid_right2.setTextureOffset(31, 28).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend_right2 = new AdvancedModelBox(this);
        armend_right2.setPos(-1.0F, 6.0F, -0.5F);
        armmid_right2.addChild(armend_right2);
        armend_right2.setTextureOffset(21, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend_right2.setTextureOffset(40, 19).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        arm1_right_pivot = new AdvancedModelBox(this);
        arm1_right_pivot.setPos(-0.6F, -0.9F, -2.5F);
        tentacles_right.addChild(arm1_right_pivot);

        armstart1_right = new AdvancedModelBox(this);
        arm1_right_pivot.addChild(armstart1_right);
        setRotationAngle(armstart1_right, -0.9163F, 0.0F, 1.5708F);
        armstart1_right.setTextureOffset(47, 19).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart1_right.setTextureOffset(36, 35).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid1_right = new AdvancedModelBox(this);
        armmid1_right.setPos(1.0F, 6.0F, 0.0F);
        armstart1_right.addChild(armmid1_right);
        armmid1_right.setTextureOffset(47, 0).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid1_right.setTextureOffset(23, 35).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend1_right = new AdvancedModelBox(this);
        armend1_right.setPos(-1.0F, 6.0F, -0.5F);
        armmid1_right.addChild(armend1_right);
        armend1_right.setTextureOffset(25, 46).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend1_right.setTextureOffset(43, 8).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        mantle = new AdvancedModelBox(this);
        mantle.setPos(0.0F, -1.9F, 0.0F);
        head.addChild(mantle);
        setRotationAngle(mantle, -0.3491F, 0.0F, 0.0F);
        mantle.setTextureOffset(0, 0).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 5.0F, 8.0F, 0.0F, false);
        mantle.setTextureOffset(0, 23).addBox(-2.0F, -3.0F, 1.0F, 4.0F, 4.0F, 9.0F, 0.0F, false);
        mantle.setTextureOffset(0, 14).addBox(-4.0F, 2.0F, 1.0F, 8.0F, 0.0F, 8.0F, 0.0F, false);

        eye_spike_left = new AdvancedModelBox(this);
        eye_spike_left.setPos(2.0F, -4.0F, -2.0F);
        head.addChild(eye_spike_left);
        eye_spike_left.setTextureOffset(0, 0).addBox(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 3.0F, 0.0F, false);

        eye_spike_right = new AdvancedModelBox(this);
        eye_spike_right.setPos(-2.0F, -4.0F, -2.0F);
        head.addChild(eye_spike_right);
        eye_spike_right.setTextureOffset(0, 0).addBox(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 3.0F, 0.0F, true);
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
            animateForMimicGround(entity.getPrevMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks, progress * groundProgressNorm);
            if(sitProgress == 0){
                animateForMimicWater(entity.getPrevMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks, progress * (1 - groundProgressNorm));
            }
        }
        animateForMimicGround(entity.getMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks, notSitProgress * transProgress * groundProgressNorm);
        animateForMimicWater(entity.getMimicState(), entity, limbSwing, limbSwingAmount, ageInTicks, notSitProgress * transProgress * (1 - groundProgressNorm));

        if(swimProgress > 0.0F){
            float rot = headPitch * ((float)Math.PI / 180F);
            this.head.y += Math.abs(rot) * -7;
            this.head.rotateAngleX -= rot;
        }
        progressRotationPrev(mantle, sitProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressPositionPrev(mantle, sitProgress, 0, -2, 0, 5F);
        progressRotationPrev(armstart1_left, sitProgress,  (float)Math.toRadians(-10),  0,  0, 5F);
        progressRotationPrev(armmid1_left, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armend1_left, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armstart2_left, sitProgress,  (float)Math.toRadians(5),  0,  0, 5F);
        progressRotationPrev(armmid2_left, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armend2_left, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armstart3_left, sitProgress,  (float)Math.toRadians(-5),  0,  0, 5F);
        progressRotationPrev(armmid3_left, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armend3_left, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armstart4_left, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armmid4_left, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armend4_left, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);

        progressRotationPrev(armstart1_right, sitProgress,  (float)Math.toRadians(-10),  0,  0, 5F);
        progressRotationPrev(armmid1_right, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armend1_right, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armstart_right2, sitProgress,  (float)Math.toRadians(5),  0,  0, 5F);
        progressRotationPrev(armmid_right2, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armend_right2, sitProgress,  (float)Math.toRadians(-20),  0,  0, 5F);
        progressRotationPrev(armstart3_right, sitProgress,  (float)Math.toRadians(-5),  0,  0, 5F);
        progressRotationPrev(armmid3_right, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armend3_right, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armstart4_right, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armmid4_right, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);
        progressRotationPrev(armend4_right, sitProgress,  (float)Math.toRadians(20),  0,  0, 5F);

    }

    public void animateForMimicWater(EntityMimicOctopus.MimicState state, EntityMimicOctopus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float swimProgress) {
        limbSwingAmount = limbSwingAmount * swimProgress * 0.2F;
        progressPositionPrev(tentacles_left, swimProgress, 0, 1, -2, 5F);
        progressPositionPrev(tentacles_right, swimProgress, 0, 1, -2, 5F);
        progressRotationPrev(mantle, swimProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(tentacles_right, swimProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(tentacles_left, swimProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(head, swimProgress, 0, (float) Math.toRadians(-180), 0, 5F);

        progressRotationPrev(arm1_left_pivot, swimProgress, (float) Math.toRadians(-20), (float) Math.toRadians(35), (float) Math.toRadians(-30), 5F);
        progressRotationPrev(arm2_left_pivot, swimProgress, (float) Math.toRadians(-10), (float) Math.toRadians(0), (float) Math.toRadians(-20), 5F);
        progressRotationPrev(arm3_left_pivot, swimProgress, (float) Math.toRadians(5), (float) Math.toRadians(-15), (float) Math.toRadians(-20), 5F);
        progressRotationPrev(arm4_left_pivot, swimProgress, (float) Math.toRadians(25), (float) Math.toRadians(-25), (float) Math.toRadians(-30), 5F);
        progressRotation(armstart1_left, swimProgress, 0, 0, 0, 5F);
        progressRotation(armstart2_left, swimProgress, 0, 0, 0, 5F);
        progressRotation(armstart3_left, swimProgress, 0, 0, 0, 5F);
        progressRotation(armstart4_left, swimProgress, 0, 0, 0, 5F);

        progressRotationPrev(arm1_right_pivot, swimProgress, (float) Math.toRadians(-20), (float) Math.toRadians(-35), (float) Math.toRadians(30), 5F);
        progressRotationPrev(arm2_right_pivot, swimProgress, (float) Math.toRadians(-10), (float) Math.toRadians(0), (float) Math.toRadians(20), 5F);
        progressRotationPrev(arm3_right_pivot, swimProgress, (float) Math.toRadians(5), (float) Math.toRadians(15), (float) Math.toRadians(20), 5F);
        progressRotationPrev(arm4_right_pivot, swimProgress, (float) Math.toRadians(25), (float) Math.toRadians(25), (float) Math.toRadians(30), 5F);
        progressRotation(armstart1_right, swimProgress, 0, 0, 0, 5F);
        progressRotation(armstart_right2, swimProgress, 0, 0, 0, 5F);
        progressRotation(armstart3_right, swimProgress, 0, 0, 0, 5F);
        progressRotation(armstart4_right, swimProgress, 0, 0, 0, 5F);
        if (state == EntityMimicOctopus.MimicState.GUARDIAN) {
            float degree = 1.2F;
            float speed = 0.65F;
            progressPositionPrev(head, swimProgress, 0, -3, -2, 5F);
            progressPositionPrev(mantle, swimProgress, 0, -2, 0, 5F);
            if(swimProgress > 0){
                this.mantle.setScale(1F + swimProgress * 0.1F, 1F + swimProgress * 0.1F, 1F + swimProgress * 0.1F);
            }
            progressRotationPrev(arm2_right_pivot, swimProgress, (float) Math.toRadians(-30), (float) Math.toRadians(0), (float) Math.toRadians(90), 5F);
            progressRotationPrev(arm3_right_pivot, swimProgress, (float) Math.toRadians(0), (float) Math.toRadians(0), (float) Math.toRadians(80), 5F);
            progressRotationPrev(arm4_right_pivot, swimProgress, (float) Math.toRadians(30), (float) Math.toRadians(0), (float) Math.toRadians(80), 5F);
            progressRotationPrev(arm2_left_pivot, swimProgress, (float) Math.toRadians(-30), (float) Math.toRadians(0), (float) Math.toRadians(-90), 5F);
            progressRotationPrev(arm3_left_pivot, swimProgress, (float) Math.toRadians(0), (float) Math.toRadians(0), (float) Math.toRadians(-80), 5F);
            progressRotationPrev(arm4_left_pivot, swimProgress, (float) Math.toRadians(30), (float) Math.toRadians(0), (float) Math.toRadians(-80), 5F);
            progressRotationPrev(arm1_right_pivot, swimProgress, (float) Math.toRadians(10), (float) Math.toRadians(30), (float) Math.toRadians(-23), 5F);
            progressRotationPrev(arm1_left_pivot, swimProgress, (float) Math.toRadians(10), (float) Math.toRadians(-30), (float) Math.toRadians(23), 5F);
            this.flap(armstart1_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart1_right, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid1_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid1_right, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armend1_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armend1_right, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);


        } else {
            float f = 1.0F;
            if(state == EntityMimicOctopus.MimicState.CREEPER){
                progressPositionPrev(head, swimProgress, 0, -3, -2, 5F);
                progressPositionPrev(mantle, swimProgress, 0, -2, 2, 5F);
                progressRotationPrev(mantle, swimProgress, (float)Math.toRadians(-60), 0, 0, 5F);
                f = 0.5F;
            }
            float degree = 1.2F;
            float speed = 0.35F;
            this.bob(head, speed, degree * 2, false, limbSwing, limbSwingAmount);
            if(swimProgress > 0) {
                if (state == EntityMimicOctopus.MimicState.PUFFERFISH) {
                    float f2 = 1.4F + 0.5F * Mth.sin(speed * limbSwing - 2) * swimProgress * 0.2F;
                    progressPositionPrev(mantle, swimProgress, 0, -2, 0, 5F);
                    this.mantle.setScale(f2, f2, f2);
                } else {
                    float scale = 1.5F + 0.5F * f * Mth.sin(speed * limbSwing - 2);
                    this.mantle.setScale(1F, 1F, (scale - 1F) * swimProgress * 0.2F + 1F);
                }
            }
            this.walk(eye_spike_left, speed, degree * 0.2F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.walk(eye_spike_right, speed, degree * 0.2F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart1_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart2_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart3_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart4_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid1_left, speed, degree * 0.5F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid2_left, speed, degree * 0.5F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid3_left, speed, degree * 0.5F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid4_left, speed, degree * 0.5F, true, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armend1_left, speed, degree * 0.15F, true, -1, 0, limbSwing, limbSwingAmount);
            this.flap(armend2_left, speed, degree * 0.15F, true, -1, 0, limbSwing, limbSwingAmount);
            this.flap(armend3_left, speed, degree * 0.15F, true, -1, 0, limbSwing, limbSwingAmount);
            this.flap(armend4_left, speed, degree * 0.15F, true, -1, 0, limbSwing, limbSwingAmount);
            this.swing(armstart1_left, speed, degree * 0.25F, true, 1, -0.1F, limbSwing, limbSwingAmount);
            this.swing(armstart2_left, speed, degree * 0.25F, true, 1, 0, limbSwing, limbSwingAmount);
            this.swing(armstart3_left, speed, degree * 0.25F, true, 1, 0, limbSwing, limbSwingAmount);
            this.swing(armstart4_left, speed, degree * 0.25F, true, 1, 0.1F, limbSwing, limbSwingAmount);

            this.flap(armstart1_right, speed, degree * 0.25F, false, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart_right2, speed, degree * 0.25F, false, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart3_right, speed, degree * 0.25F, false, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart4_right, speed, degree * 0.25F, false, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid1_right, speed, degree * 0.5F, false, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid_right2, speed, degree * 0.5F, false, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid3_right, speed, degree * 0.5F, false, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armmid4_right, speed, degree * 0.5F, false, -2, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armend1_right, speed, degree * 0.15F, false, -1, 0, limbSwing, limbSwingAmount);
            this.flap(armend_right2, speed, degree * 0.15F, false, -1, 0, limbSwing, limbSwingAmount);
            this.flap(armend3_right, speed, degree * 0.15F, false, -1, 0, limbSwing, limbSwingAmount);
            this.flap(armend4_right, speed, degree * 0.15F, false, -1, 0, limbSwing, limbSwingAmount);
            this.swing(armstart1_right, speed, degree * 0.25F, false, 1, -0.1F, limbSwing, limbSwingAmount);
            this.swing(armstart_right2, speed, degree * 0.25F, false, 1, 0, limbSwing, limbSwingAmount);
            this.swing(armstart3_right, speed, degree * 0.25F, false, 1, 0, limbSwing, limbSwingAmount);
            this.swing(armstart4_right, speed, degree * 0.25F, false, 1, 0.1F, limbSwing, limbSwingAmount);

        }
    }

    public void animateForMimicGround(EntityMimicOctopus.MimicState state, EntityMimicOctopus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float groundProgress){
        this.mantle.setScale(1, 1, 1);
        limbSwingAmount = limbSwingAmount * groundProgress * 0.2F;
        float degree = 0.7F;
        float speed = 0.8F;
        if(state == EntityMimicOctopus.MimicState.CREEPER){
            progressRotationPrev(head, groundProgress, 0, (float)Math.toRadians(-180), 0, 5F);
            progressRotationPrev(mantle, groundProgress, (float)Math.toRadians(-60), 0, 0, 5F);
            progressPositionPrev(mantle, groundProgress, 0, -4, 3, 5F);
            progressPositionPrev(head, groundProgress, 0, -15, -2, 5F);
            progressRotationPrev(arm1_left_pivot, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(45),  (float)Math.toRadians(-20), 5F);
            progressRotationPrev(arm2_left_pivot, groundProgress,  (float)Math.toRadians(-10),  (float)Math.toRadians(0),  (float)Math.toRadians(-10), 5F);
            progressRotationPrev(arm3_left_pivot, groundProgress,  (float)Math.toRadians(5),  (float)Math.toRadians(-25),  (float)Math.toRadians(-10), 5F);
            progressRotationPrev(arm4_left_pivot, groundProgress,  (float)Math.toRadians(25),  (float)Math.toRadians(-35),  (float)Math.toRadians(-20), 5F);
            progressRotation(armstart1_left, groundProgress,  0, 0,  0, 5F);
            progressRotation(armstart2_left, groundProgress,  0, 0,  0, 5F);
            progressRotation(armstart3_left, groundProgress,  0, 0,  0, 5F);
            progressRotation(armstart4_left, groundProgress,  0, 0,  0, 5F);
            progressRotationPrev(arm1_right_pivot, groundProgress,  (float)Math.toRadians(-20),  (float)Math.toRadians(-45),  (float)Math.toRadians(20), 5F);
            progressRotationPrev(arm2_right_pivot, groundProgress,  (float)Math.toRadians(-10),  (float)Math.toRadians(0),  (float)Math.toRadians(10), 5F);
            progressRotationPrev(arm3_right_pivot, groundProgress,  (float)Math.toRadians(5),  (float)Math.toRadians(25),  (float)Math.toRadians(10), 5F);
            progressRotationPrev(arm4_right_pivot, groundProgress,  (float)Math.toRadians(25),  (float)Math.toRadians(35),  (float)Math.toRadians(20), 5F);
            progressRotation(armstart1_right, groundProgress,  0, 0,  0, 5F);
            progressRotation(armstart_right2, groundProgress,  0, 0,  0, 5F);
            progressRotation(armstart3_right, groundProgress,  0, 0,  0, 5F);
            progressRotation(armstart4_right, groundProgress,  0, 0,  0, 5F);
            this.flap(mantle, speed, degree * 0.25F, true, 0, 0, limbSwing, limbSwingAmount);
            this.walk(armstart1_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart2_left, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart1_left, speed, degree * 0.1F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart2_left, speed, degree * 0.1F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart3_left, speed, degree * 0.25F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart4_left, speed, degree * 0.25F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart3_left, speed, degree * 0.1F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart4_left, speed, degree * 0.1F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart1_right, speed, degree * 0.25F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart_right2, speed, degree * 0.25F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart1_right, speed, degree * 0.1F, false, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart_right2, speed, degree * 0.1F, false, 0, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart3_right, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart4_right, speed, degree * 0.25F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart3_right, speed, degree * 0.1F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.flap(armstart4_right, speed, degree * 0.1F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.bob(head, speed, degree * 2, true, limbSwing, limbSwingAmount);
        }else{
            if(entity.hasGuardianLaser()){
                progressRotationPrev(head, groundProgress, 0, (float)Math.toRadians(180), 0, 5F);
            }
            float idleDegree = 0.03F;
            float idleSpeed = 0.07F;
            this.walk(armstart1_left, idleSpeed, idleDegree, true, 0, 0.05F, ageInTicks, groundProgress);
            this.walk(armmid1_left, idleSpeed, idleDegree * 0.5F, false, 2, 0, ageInTicks, groundProgress);
            this.walk(armend1_left, idleSpeed, idleDegree * 0.75F, true, 0, 0, ageInTicks, groundProgress);
            this.walk(armstart2_left,idleSpeed, idleDegree, true, 1, 0, ageInTicks, groundProgress);
            this.walk(armmid2_left, idleSpeed, idleDegree * 0.5F, false, 3, 0, ageInTicks, groundProgress);
            this.walk(armend2_left, idleSpeed, idleDegree * 0.75F, true, 1, 0, ageInTicks, groundProgress);
            this.walk(armstart3_left,idleSpeed, idleDegree, true, 2, -0.02F, ageInTicks, groundProgress);
            this.walk(armmid3_left, idleSpeed, idleDegree * 0.5F, false, 4, 0, ageInTicks, groundProgress);
            this.walk(armend3_left, idleSpeed, idleDegree * 0.75F, true, 2, 0, ageInTicks, groundProgress);
            this.walk(armstart4_left,idleSpeed, idleDegree, true, 3, -0.05F, ageInTicks, groundProgress);
            this.walk(armmid4_left, idleSpeed, idleDegree * 0.5F, false, 5, 0, ageInTicks, groundProgress);
            this.walk(armend4_left, idleSpeed, idleDegree * 0.75F, true, 3, 0, ageInTicks, groundProgress);

            this.walk(armstart1_right, idleSpeed, idleDegree, true, 0, 0.05F, ageInTicks, groundProgress);
            this.walk(armmid1_right, idleSpeed, idleDegree * 0.5F, false, 2, 0, ageInTicks, groundProgress);
            this.walk(armend1_right, idleSpeed, idleDegree * 0.75F, true, 0, 0, ageInTicks, groundProgress);
            this.walk(armstart_right2,idleSpeed, idleDegree, true, 1, 0, ageInTicks, groundProgress);
            this.walk(armmid_right2, idleSpeed, idleDegree * 0.5F, false, 3, 0, ageInTicks, groundProgress);
            this.walk(armend_right2, idleSpeed, idleDegree * 0.75F, true, 1, 0, ageInTicks, groundProgress);
            this.walk(armstart3_right,idleSpeed, idleDegree, true, 2, -0.02F, ageInTicks, groundProgress);
            this.walk(armmid3_right, idleSpeed, idleDegree * 0.5F, false, 4, 0, ageInTicks, groundProgress);
            this.walk(armend3_right, idleSpeed, idleDegree * 0.75F, true, 2, 0, ageInTicks, groundProgress);
            this.walk(armstart4_right,idleSpeed, idleDegree, true, 3, -0.05F, ageInTicks, groundProgress);
            this.walk(armmid4_right, idleSpeed, idleDegree * 0.5F, false, 5, 0, ageInTicks, groundProgress);
            this.walk(armend4_right, idleSpeed, idleDegree * 0.75F, true, 3, 0, ageInTicks, groundProgress);


            progressRotationPrev(mantle, groundProgress, (float)Math.toRadians(20), 0, 0, 5F);
            progressPositionPrev(mantle, groundProgress, 0, -2, 0, 5F);
            this.flap(eye_spike_left, speed, degree * 0.2F, false, -5, 0.2F, limbSwing, limbSwingAmount);
            this.flap(eye_spike_right, speed, degree * 0.2F, true, -5, 0.2F, limbSwing, limbSwingAmount);
            this.swing(head, speed, degree * 0.2F, true, -12, 0, limbSwing, limbSwingAmount);
            this.walk(mantle, speed, degree * 0.25F, true, 1, 0, limbSwing, limbSwingAmount);
            this.swing(mantle, speed, degree * 0.25F, true, 3, 0, limbSwing, limbSwingAmount);

            this.walk(armstart1_left, speed, degree * 0.25F, true, 3, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart2_left, speed, degree * 0.25F, true, 2, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart3_left, speed, degree * 0.25F, true, 1, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armstart4_left, speed, degree * 0.25F, true, 0, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armmid1_left, speed, degree * 0.5F, true, 2, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armmid2_left, speed, degree * 0.5F, true, 1, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armmid3_left, speed, degree * 0.5F, true, 0, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armmid4_left, speed, degree * 0.5F, true, -1, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armend1_left, speed, degree * 0.5F, true, 1, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armend2_left, speed, degree * 0.5F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armend3_left, speed, degree * 0.5F, true, -1, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armend4_left, speed, degree * 0.5F, true, -2, -0.2F, limbSwing, limbSwingAmount);

            this.walk(armstart1_right, speed, degree * 0.25F, true, 3, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart_right2, speed, degree * 0.25F, true, 2, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armstart3_right, speed, degree * 0.25F, true, 1, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armstart4_right, speed, degree * 0.25F, true, 0, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armmid1_right, speed, degree * 0.5F, true, 2, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armmid_right2, speed, degree * 0.5F, true, 1, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armmid3_right, speed, degree * 0.5F, true, 0, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armmid4_right, speed, degree * 0.5F, true, -1, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armend1_right, speed, degree * 0.5F, true, 1, 0.1F, limbSwing, limbSwingAmount);
            this.walk(armend_right2, speed, degree * 0.5F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.walk(armend3_right, speed, degree * 0.5F, true, -1, -0.2F, limbSwing, limbSwingAmount);
            this.walk(armend4_right, speed, degree * 0.5F, true, -2, -0.2F, limbSwing, limbSwingAmount);
        }

    }

    @Override
    public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, head, tentacles_left, armstart4_left, armmid4_left, armend4_left, armstart3_left, armmid3_left, armend3_left, armstart2_left, armmid2_left, armend2_left, armstart1_left, armmid1_left, armend1_left, tentacles_right, armstart4_right,
				armmid4_right, armend4_right, armstart3_right, armmid3_right, armend3_right, armstart_right2, armmid_right2, armend_right2, armstart1_right, armmid1_right, armend1_right, mantle, eye_spike_left, eye_spike_right,
                arm1_left_pivot, arm2_left_pivot, arm3_left_pivot, arm4_left_pivot, arm1_right_pivot, arm2_right_pivot, arm3_right_pivot, arm4_right_pivot);

	}

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}