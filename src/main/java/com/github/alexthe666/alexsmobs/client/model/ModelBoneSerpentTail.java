package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;

public class ModelBoneSerpentTail extends AdvancedEntityModel<EntityBoneSerpentPart> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox tail;

    public ModelBoneSerpentTail() {
        texWidth = 64;
        texHeight = 64;
        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);
        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.0F, -4.75F, 0.0F);
        root.addChild(tail);
        tail.setTextureOffset(0, 0).addBox(-5.0F, -5.25F, -8.0F, 10.0F, 10.0F, 16.0F, 0.0F, false);
        tail.setTextureOffset(0, 27).addBox(-1.0F, -6.25F, -8.0F, 2.0F, 1.0F, 16.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityBoneSerpentPart entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.tail.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.tail.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        float walkSpeed = 0.35F;
        float walkDegree = 3F;
        float idleDegree = 0.7F;
        float idleSpeed = 0.2F;
        double walkOffset = entityIn.getBodyIndex() + 1;
        this.tail.rotationPointY += (float)(Math.sin( (double)(limbSwing * walkSpeed) - walkOffset) * (double)limbSwingAmount * (double)walkDegree - (double)(limbSwingAmount * walkDegree) );
        this.tail.rotationPointY += (float)(Math.sin( (double)(ageInTicks * idleSpeed) - walkOffset) * (double)1 * (double)idleDegree - (double)(1 * idleDegree) );

    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, tail);
    }

}