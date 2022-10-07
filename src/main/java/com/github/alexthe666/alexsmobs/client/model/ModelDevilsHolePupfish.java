package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityDevilsHolePupfish;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelDevilsHolePupfish extends AdvancedEntityModel<EntityDevilsHolePupfish> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox bottom_fin;
    private final AdvancedModelBox dorsal_fin;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox left_fin;
    private final AdvancedModelBox right_fin;

    public ModelDevilsHolePupfish() {
        texHeight = 32;
        texWidth = 32;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -2.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 9.0F, 0.0F, false);

        bottom_fin = new AdvancedModelBox(this, "bottom_fin");
        bottom_fin.setRotationPoint(0.0F, 2.0F, 1.0F);
        body.addChild(bottom_fin);
        bottom_fin.setTextureOffset(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F, 0.0F, false);

        dorsal_fin = new AdvancedModelBox(this, "dorsal_fin");
        dorsal_fin.setRotationPoint(0.0F, -2.0F, 0.0F);
        body.addChild(dorsal_fin);
        dorsal_fin.setTextureOffset(11, 14).addBox(0.0F, -3.0F, -2.0F, 0.0F, 3.0F, 5.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -1.0F, 4.0F);
        body.addChild(tail);
        tail.setTextureOffset(0, 14).addBox(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 5.0F, 0.0F, false);

        left_fin = new AdvancedModelBox(this, "left_fin");
        left_fin.setRotationPoint(1.5F, 1.0F, -2.0F);
        body.addChild(left_fin);
        setRotationAngle(left_fin, 0.0F, 0.48F, 0.0F);
        left_fin.setTextureOffset(0, 14).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);

        right_fin = new AdvancedModelBox(this, "right_fin");
        right_fin.setRotationPoint(-1.5F, 1.0F, -2.0F);
        body.addChild(right_fin);
        setRotationAngle(right_fin, 0.0F, -0.48F, 0.0F);
        right_fin.setTextureOffset(0, 14).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityDevilsHolePupfish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.3F;
        float idleDegree = 0.5F;
        float swimSpeed = 1.0F;
        float swimDegree = 0.5F;
        float partialTick = ageInTicks - entity.tickCount;
        float landProgress = entity.prevOnLandProgress + (entity.onLandProgress - entity.prevOnLandProgress) * partialTick;
        float feedingProgress = entity.prevFeedProgress + (entity.feedProgress - entity.prevFeedProgress) * partialTick;
        this.progressRotationPrev(dorsal_fin, limbSwingAmount, (float) Math.toRadians(-20), 0, 0, 1F);
        this.progressPositionPrev(dorsal_fin, limbSwingAmount,0, 0.5F, 0, 1F);
        this.progressRotationPrev(bottom_fin, limbSwingAmount, (float) Math.toRadians(10), 0, 0, 1F);
        this.progressPositionPrev(bottom_fin, limbSwingAmount,0, -0.5F, -0.5F, 1F);
        this.progressRotationPrev(body, landProgress, 0, 0, (float) Math.toRadians(90), 5F);
        this.bob(body, idleSpeed, idleDegree, false, ageInTicks, 1F);
        this.swing(body, idleSpeed, idleDegree * 0.1F, false, 1F, 0, ageInTicks, 1F);
        this.swing(tail, idleSpeed, idleDegree * 0.3F, false, -1F, 0, ageInTicks, 1F);
        this.swing(tail, swimSpeed, swimDegree, false, 0, 0, limbSwing, limbSwingAmount);
        this.swing(body, swimSpeed, swimDegree * 0.3F, false, 1F, 0, limbSwing, limbSwingAmount);
        this.swing(left_fin, swimSpeed, swimDegree, false, 3F, 0.6F, limbSwing, limbSwingAmount);
        this.swing(right_fin, swimSpeed, swimDegree, true, 3F, 0.6F, limbSwing, limbSwingAmount);
        this.body.rotateAngleX += headPitch * ((float)Math.PI / 180F);
        this.body.rotateAngleX += feedingProgress * Math.cos(ageInTicks * 0.3F) * 0.2F * Math.PI * 0.1F;
        this.body.rotationPointZ += feedingProgress * Math.abs(Math.sin(ageInTicks * 0.3F));

    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, dorsal_fin, bottom_fin, tail, left_fin, right_fin);
    }

    public void setRotationAngle(AdvancedModelBox modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
