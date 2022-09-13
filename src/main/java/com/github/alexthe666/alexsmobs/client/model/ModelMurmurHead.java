package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.alexsmobs.entity.EntityMurmurHead;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class ModelMurmurHead extends AdvancedEntityModel<EntityMurmurHead> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox head;
    private final AdvancedModelBox backHair;
    private final AdvancedModelBox leftHair;
    private final AdvancedModelBox rightHair;

    public ModelMurmurHead() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -1.0F, 0.0F);
        root.addChild(head);
        head.setTextureOffset(37, 41).addBox(-4.5F, -8.0F, -4.5F, 9.0F, 9.0F, 9.0F, 0.0F, false);
        head.setTextureOffset(0, 41).addBox(-4.5F, -8.0F, -4.5F, 9.0F, 9.0F, 9.0F, 0.2F, false);

        backHair = new AdvancedModelBox(this);
        backHair.setRotationPoint(0.0F, -5.0F, 5.0F);
        head.addChild(backHair);
        backHair.setTextureOffset(49, 0).addBox(-5.5F, -2.0F, -1.5F, 11.0F, 20.0F, 3.0F, 0.0F, false);

        leftHair = new AdvancedModelBox(this);
        leftHair.setRotationPoint(4.5F, -5.0F, 1.0F);
        head.addChild(leftHair);
        leftHair.setTextureOffset(17, 60).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 16.0F, 5.0F, 0.0F, false);

        rightHair = new AdvancedModelBox(this);
        rightHair.setRotationPoint(-4.5F, -5.0F, 1.0F);
        head.addChild(rightHair);
        rightHair.setTextureOffset(17, 60).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 16.0F, 5.0F, 0.0F, true);

     this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, head, leftHair, rightHair, backHair);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityMurmurHead entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }
}
