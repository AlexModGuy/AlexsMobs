package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFart;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelFart extends AdvancedEntityModel<EntityFart> {
    private final AdvancedModelBox main;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;

    public ModelFart() {
        texWidth = 64;
        texHeight = 64;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 0.0F, 0.0F);
        main.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -5.0F, 8.0F, 8.0F, 11.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.5F);
        main.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
        cube_r1.setTextureOffset(0, 20).addBox(0.0F, -4.0F, -2.5F, 0.0F, 8.0F, 11.0F, 0.0F, true);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.5F);
        main.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 0.7854F);
        cube_r2.setTextureOffset(0, 20).addBox(0.0F, -4.0F, -2.5F, 0.0F, 8.0F, 11.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, cube_r1, cube_r2);
    }

    @Override
    public void setupAnim(EntityFart entity, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float f = Math.min(entity.tickCount + partialTicks, 30) / 30F;
        float expand = 1.5F * f;
        this.main.setScale(expand * 2F + 1F, expand * 2F + 1F, 1F);
        this.cube_r1.setScale(1F, 1F, expand * 1.5F + 1F);
        this.cube_r2.setScale(1F, 1F, expand * 1.5F + 1F);
        this.cube_r1.rotationPointZ += expand * 3;
        this.cube_r2.rotationPointZ += expand * 3;
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}