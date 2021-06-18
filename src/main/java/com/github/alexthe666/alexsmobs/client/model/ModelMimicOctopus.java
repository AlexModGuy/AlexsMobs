package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;

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

    public ModelMimicOctopus() {
        textureWidth = 64;
        textureHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(head);
        head.setTextureOffset(18, 23).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        tentacles_left = new AdvancedModelBox(this);
        tentacles_left.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(tentacles_left);


        armstart4_left = new AdvancedModelBox(this);
        armstart4_left.setRotationPoint(1.6F, -0.7F, -1.8F);
        tentacles_left.addChild(armstart4_left);
        setRotationAngle(armstart4_left, 0.6981F, 0.0F, -1.5708F);
        armstart4_left.setTextureOffset(41, 30).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart4_left.setTextureOffset(30, 0).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armmid4_left = new AdvancedModelBox(this);
        armmid4_left.setRotationPoint(-1.0F, 6.0F, 0.0F);
        armstart4_left.addChild(armmid4_left);
        armmid4_left.setTextureOffset(0, 23).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid4_left.setTextureOffset(25, 10).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend4_left = new AdvancedModelBox(this);
        armend4_left.setRotationPoint(1.0F, 6.0F, -0.5F);
        armmid4_left.addChild(armend4_left);
        armend4_left.setTextureOffset(0, 14).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend4_left.setTextureOffset(39, 0).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        armstart3_left = new AdvancedModelBox(this);
        armstart3_left.setRotationPoint(1.6F, -1.0F, -1.8F);
        tentacles_left.addChild(armstart3_left);
        setRotationAngle(armstart3_left, 0.0F, 0.0F, -1.5708F);
        armstart3_left.setTextureOffset(7, 48).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart3_left.setTextureOffset(9, 37).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armmid3_left = new AdvancedModelBox(this);
        armmid3_left.setRotationPoint(-1.0F, 6.0F, 0.0F);
        armstart3_left.addChild(armmid3_left);
        armmid3_left.setTextureOffset(0, 48).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid3_left.setTextureOffset(0, 37).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend3_left = new AdvancedModelBox(this);
        armend3_left.setRotationPoint(1.0F, 6.0F, -0.5F);
        armmid3_left.addChild(armend3_left);
        armend3_left.setTextureOffset(37, 46).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend3_left.setTextureOffset(18, 43).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        armstart2_left = new AdvancedModelBox(this);
        armstart2_left.setRotationPoint(1.3F, -0.8F, -2.1F);
        tentacles_left.addChild(armstart2_left);
        setRotationAngle(armstart2_left, -0.5672F, 0.0F, -1.5708F);
        armstart2_left.setTextureOffset(43, 44).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart2_left.setTextureOffset(34, 11).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armmid2_left = new AdvancedModelBox(this);
        armmid2_left.setRotationPoint(-1.0F, 6.0F, 0.0F);
        armstart2_left.addChild(armmid2_left);
        armmid2_left.setTextureOffset(30, 44).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid2_left.setTextureOffset(31, 28).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend2_left = new AdvancedModelBox(this);
        armend2_left.setRotationPoint(1.0F, 6.0F, -0.5F);
        armmid2_left.addChild(armend2_left);
        armend2_left.setTextureOffset(21, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend2_left.setTextureOffset(40, 19).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        armstart1_left = new AdvancedModelBox(this);
        armstart1_left.setRotationPoint(0.6F, -0.9F, -2.5F);
        tentacles_left.addChild(armstart1_left);
        setRotationAngle(armstart1_left, -0.9163F, 0.0F, -1.5708F);
        armstart1_left.setTextureOffset(47, 19).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armstart1_left.setTextureOffset(36, 35).addBox(-1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armmid1_left = new AdvancedModelBox(this);
        armmid1_left.setRotationPoint(-1.0F, 6.0F, 0.0F);
        armstart1_left.addChild(armmid1_left);
        armmid1_left.setTextureOffset(47, 0).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, false);
        armmid1_left.setTextureOffset(23, 35).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, false);

        armend1_left = new AdvancedModelBox(this);
        armend1_left.setRotationPoint(1.0F, 6.0F, -0.5F);
        armmid1_left.addChild(armend1_left);
        armend1_left.setTextureOffset(25, 46).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
        armend1_left.setTextureOffset(43, 8).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, false);

        tentacles_right = new AdvancedModelBox(this);
        tentacles_right.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(tentacles_right);


        armstart4_right = new AdvancedModelBox(this);
        armstart4_right.setRotationPoint(-1.6F, -0.7F, -1.8F);
        tentacles_right.addChild(armstart4_right);
        setRotationAngle(armstart4_right, 0.6981F, 0.0F, 1.5708F);
        armstart4_right.setTextureOffset(41, 30).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart4_right.setTextureOffset(30, 0).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid4_right = new AdvancedModelBox(this);
        armmid4_right.setRotationPoint(1.0F, 6.0F, 0.0F);
        armstart4_right.addChild(armmid4_right);
        armmid4_right.setTextureOffset(0, 23).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid4_right.setTextureOffset(25, 10).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend4_right = new AdvancedModelBox(this);
        armend4_right.setRotationPoint(-1.0F, 6.0F, -0.5F);
        armmid4_right.addChild(armend4_right);
        armend4_right.setTextureOffset(0, 14).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend4_right.setTextureOffset(39, 0).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        armstart3_right = new AdvancedModelBox(this);
        armstart3_right.setRotationPoint(-1.6F, -1.0F, -1.8F);
        tentacles_right.addChild(armstart3_right);
        setRotationAngle(armstart3_right, 0.0F, 0.0F, 1.5708F);
        armstart3_right.setTextureOffset(7, 48).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart3_right.setTextureOffset(9, 37).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid3_right = new AdvancedModelBox(this);
        armmid3_right.setRotationPoint(1.0F, 6.0F, 0.0F);
        armstart3_right.addChild(armmid3_right);
        armmid3_right.setTextureOffset(0, 48).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid3_right.setTextureOffset(0, 37).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend3_right = new AdvancedModelBox(this);
        armend3_right.setRotationPoint(-1.0F, 6.0F, -0.5F);
        armmid3_right.addChild(armend3_right);
        armend3_right.setTextureOffset(37, 46).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend3_right.setTextureOffset(18, 43).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        armstart_right2 = new AdvancedModelBox(this);
        armstart_right2.setRotationPoint(-1.3F, -0.8F, -2.1F);
        tentacles_right.addChild(armstart_right2);
        setRotationAngle(armstart_right2, -0.5672F, 0.0F, 1.5708F);
        armstart_right2.setTextureOffset(43, 44).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart_right2.setTextureOffset(34, 11).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid_right2 = new AdvancedModelBox(this);
        armmid_right2.setRotationPoint(1.0F, 6.0F, 0.0F);
        armstart_right2.addChild(armmid_right2);
        armmid_right2.setTextureOffset(30, 44).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid_right2.setTextureOffset(31, 28).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend_right2 = new AdvancedModelBox(this);
        armend_right2.setRotationPoint(-1.0F, 6.0F, -0.5F);
        armmid_right2.addChild(armend_right2);
        armend_right2.setTextureOffset(21, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend_right2.setTextureOffset(40, 19).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        armstart1_right = new AdvancedModelBox(this);
        armstart1_right.setRotationPoint(-0.6F, -0.9F, -2.5F);
        tentacles_right.addChild(armstart1_right);
        setRotationAngle(armstart1_right, -0.9163F, 0.0F, 1.5708F);
        armstart1_right.setTextureOffset(47, 19).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armstart1_right.setTextureOffset(36, 35).addBox(1.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armmid1_right = new AdvancedModelBox(this);
        armmid1_right.setRotationPoint(1.0F, 6.0F, 0.0F);
        armstart1_right.addChild(armmid1_right);
        armmid1_right.setTextureOffset(47, 0).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, 0.0F, true);
        armmid1_right.setTextureOffset(23, 35).addBox(0.0F, 0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, true);

        armend1_right = new AdvancedModelBox(this);
        armend1_right.setRotationPoint(-1.0F, 6.0F, -0.5F);
        armmid1_right.addChild(armend1_right);
        armend1_right.setTextureOffset(25, 46).addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
        armend1_right.setTextureOffset(43, 8).addBox(1.0F, 0.0F, -1.0F, 0.0F, 7.0F, 3.0F, 0.0F, true);

        mantle = new AdvancedModelBox(this);
        mantle.setRotationPoint(0.0F, -1.9F, 0.0F);
        head.addChild(mantle);
        setRotationAngle(mantle, -0.3491F, 0.0F, 0.0F);
        mantle.setTextureOffset(0, 0).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 5.0F, 8.0F, 0.0F, false);
        mantle.setTextureOffset(0, 23).addBox(-2.0F, -3.0F, 1.0F, 4.0F, 4.0F, 9.0F, 0.0F, false);
        mantle.setTextureOffset(0, 14).addBox(-4.0F, 2.0F, 1.0F, 8.0F, 0.0F, 8.0F, 0.0F, false);

        eye_spike_left = new AdvancedModelBox(this);
        eye_spike_left.setRotationPoint(2.0F, -4.0F, -2.0F);
        head.addChild(eye_spike_left);
        eye_spike_left.setTextureOffset(0, 0).addBox(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 3.0F, 0.0F, false);

        eye_spike_right = new AdvancedModelBox(this);
        eye_spike_right.setRotationPoint(-2.0F, -4.0F, -2.0F);
        head.addChild(eye_spike_right);
        eye_spike_right.setTextureOffset(0, 0).addBox(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 3.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setRotationAngles(EntityMimicOctopus entity, float v, float v1, float v2, float v3, float v4) {
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, head, tentacles_left, armstart4_left, armmid4_left, armend4_left, armstart3_left, armmid3_left, armend3_left, armstart2_left, armmid2_left, armend2_left, armstart1_left, armmid1_left, armend1_left, tentacles_right, armstart4_right,
				armmid4_right, armend4_right, armstart3_right, armmid3_right, armend3_right, armstart_right2, armmid_right2, armend_right2, armstart1_right, armmid1_right, armend1_right, mantle, eye_spike_left, eye_spike_right);

	}

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}