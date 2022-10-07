package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelMurmurBody extends AdvancedEntityModel<EntityMurmur> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox arms;
    
    public ModelMurmurBody() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -14.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-7.0F, -13.0F, -5.0F, 14.0F, 14.0F, 10.0F, 0.0F, false);
        body.setTextureOffset(72, 20).addBox(-7.0F, 1.0F, -5.0F, 14.0F, 13.0F, 10.0F, 0.0F, false);

        arms = new AdvancedModelBox(this, "arms");
        arms.setRotationPoint(0.0F, -8.5F, -1.0F);
        body.addChild(arms);
        arms.rotateAngleX = 0.4363F;
        arms.setTextureOffset(0, 25).addBox(-9.0F, -2.5F, -8.0F, 18.0F, 5.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, arms);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityMurmur entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.9F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        this.body.rotationPointY -= Math.abs(Math.sin(0.9F * limbSwing) * limbSwingAmount * 4F);
        this.walk(arms, walkSpeed * 2F, walkDegree * 0.3F, false, -1, 0.15F, limbSwing, limbSwingAmount);
        this.swing(arms, walkSpeed * 1F, walkDegree * 0.3F, false, -3F, 0F, limbSwing, limbSwingAmount);
        progressRotationPrev(this.body, limbSwingAmount, (float)Math.toRadians(15), 0, 0, 1F);
        progressPositionPrev(this.body, limbSwingAmount, 0, 2F, 4F, 1F);
        this.walk(arms, idleSpeed, idleDegree, false, -1, 0.15F, ageInTicks, 1);
    }
}
