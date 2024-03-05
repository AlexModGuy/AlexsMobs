package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelTendonClaw extends AdvancedEntityModel<EntityTendonSegment> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox claw1;
    private final AdvancedModelBox claw2;
    private final AdvancedModelBox claw3;

    public ModelTendonClaw() {
        texWidth = 16;
        texHeight = 16;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 12.0F, 0.0F);


        claw1 = new AdvancedModelBox(this, "claw1");
        claw1.setRotationPoint(0.0F, 0.0F, 0.7F);
        root.addChild(claw1);
        setRotationAngle(claw1, 0.2618F, 0.0F, 0.0F);
        claw1.setTextureOffset(0, 0).addBox(-1.5F, -8.0F, -2.0F, 3.0F, 8.0F, 2.0F, 0.0F, false);

        claw2 = new AdvancedModelBox(this, "claw2");
        claw2.setRotationPoint(0.5F, 0.0F, 1.0F);
        root.addChild(claw2);
        setRotationAngle(claw2, 0.2618F, 0.0F, 2.1817F);
        claw2.setTextureOffset(0, 0).addBox(-1.5F, -8.0F, -2.0F, 3.0F, 8.0F, 2.0F, 0.0F, false);

        claw3 = new AdvancedModelBox(this, "claw3");
        claw3.setRotationPoint(-0.5F, 0.0F, 1.0F);
        root.addChild(claw3);
        setRotationAngle(claw3, 0.2618F, 0.0F, -2.1817F);
        claw3.setTextureOffset(0, 0).addBox(-1.5F, -8.0F, -2.0F, 3.0F, 8.0F, 2.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, claw1, claw2, claw3);
    }

    @Override
    public void setupAnim(EntityTendonSegment entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
    }

    public void setAttributes(float rotX, float rotY, float open){
        this.resetToDefaultPose();
        this.resetToDefaultPose();
        this.root.rotateAngleX = Maths.rad(rotX);
        this.root.rotateAngleY = Maths.rad(rotY);
        progressRotationPrev(claw1, open, Maths.rad(45F), 0, 0, 1);
        progressRotationPrev(claw2, open, Maths.rad(45F), 0, 0, 1);
        progressRotationPrev(claw3, open, Maths.rad(45F), 0, 0, 1);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}