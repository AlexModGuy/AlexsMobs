package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelBoneSerpentHead extends AdvancedEntityModel<EntityBoneSerpent> {
    private final AdvancedModelBox head;
    private final AdvancedModelBox hornL;
    private final AdvancedModelBox hornR;
    private final AdvancedModelBox middlehorn;
    private final AdvancedModelBox headtop;
    private final AdvancedModelBox jaw;

    public ModelBoneSerpentHead() {
        textureWidth = 128;
        textureHeight = 128;

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 16.0F, 8.0F);


        hornL = new AdvancedModelBox(this);
        hornL.setRotationPoint(4.1F, -7.0F, -1.0F);
        head.addChild(hornL);
        setRotationAngle(hornL, 0.6545F, 0.3927F, 0.0F);
        hornL.setTextureOffset(61, 42).addBox(-0.1F, -3.0F, -5.0F, 5.0F, 5.0F, 16.0F, 0.0F, false);

        hornR = new AdvancedModelBox(this);
        hornR.setRotationPoint(-4.1F, -7.0F, -1.0F);
        head.addChild(hornR);
        setRotationAngle(hornR, 0.6545F, -0.3927F, 0.0F);
        hornR.setTextureOffset(61, 42).addBox(-4.9F, -3.0F, -5.0F, 5.0F, 5.0F, 16.0F, 0.0F, true);

        middlehorn = new AdvancedModelBox(this);
        middlehorn.setRotationPoint(-2.9F, -6.0F, -1.0F);
        head.addChild(middlehorn);
        setRotationAngle(middlehorn, 0.6545F, 0.0F, 0.0F);
        middlehorn.setTextureOffset(67, 67).addBox(-0.1F, -4.0F, -5.0F, 6.0F, 6.0F, 22.0F, 0.0F, false);

        headtop = new AdvancedModelBox(this);
        headtop.setRotationPoint(0.0F, -2.5F, 0.0F);
        head.addChild(headtop);
        headtop.setTextureOffset(0, 0).addBox(-9.0F, -4.5F, -30.0F, 18.0F, 11.0F, 30.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 2.5F, -2.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(0, 42).addBox(-8.0F, -1.5F, -26.0F, 16.0F, 7.0F, 28.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setRotationAngles(EntityBoneSerpent entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        float walkSpeed = 0.35F;
        float walkDegree = 3F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.2F;
        double walkOffset = 0;
        this.walk(jaw, idleSpeed * 0.5F, idleDegree * 0.3F, false, -1.2F, 0F, ageInTicks, 1);
        this.head.rotationPointY += (float)(Math.sin( (double)(limbSwing * walkSpeed) + walkOffset) * (double)limbSwingAmount * (double)walkDegree - (double)(limbSwingAmount * walkDegree) );
        this.walk(jaw, walkSpeed * 1F, walkDegree * 0.03F, false, -1.2F, 0F, limbSwing, limbSwingAmount);
        this.head.rotationPointY += (float)(Math.sin( (double)(ageInTicks * idleSpeed) + walkOffset) * (double)1 * (double)idleDegree - (double)(1 * idleDegree) );

    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(head);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(head, hornL, hornR, middlehorn, headtop, jaw);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}