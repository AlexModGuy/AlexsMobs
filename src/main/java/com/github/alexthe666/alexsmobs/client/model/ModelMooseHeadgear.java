package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelMooseHeadgear extends HumanoidModel {
    public ModelPart hornL;
    public ModelPart hornR;

    public ModelMooseHeadgear(float modelSize) {
        super(modelSize, 0, 64, 32);
        this.texWidth = 64;
        this.texHeight = 32;
        this.hornL = new ModelPart(this, 0, 0);
        this.hornL.setPos(5.0F, -8.0F, 1.0F);
        this.hornL.setTextureOffset(3, 17).addBox(0.0F, -5.5F, -4.0F, 10.0F, 6.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.hornR = new ModelPart(this, 0, 0);
        this.hornR.mirror = true;
        this.hornR.setPos(-5.0F, -8.0F, 1.0F);
        this.hornR.setTextureOffset(3, 17).addBox(-10.0F, -5.5F, -4.0F, 10.0F, 6.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.head.addChild(this.hornL);
        this.head.addChild(this.hornR);
    }

    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn instanceof ArmorStand) {
            ArmorStand entityarmorstand = (ArmorStand) entityIn;
            this.head.rotateAngleX = 0.017453292F * entityarmorstand.getHeadPose().getX();
            this.head.rotateAngleY = 0.017453292F * entityarmorstand.getHeadPose().getY();
            this.head.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadPose().getZ();
            this.head.setPos(0.0F, 1.0F, 0.0F);
            this.body.rotateAngleX = 0.017453292F * entityarmorstand.getBodyPose().getX();
            this.body.rotateAngleY = 0.017453292F * entityarmorstand.getBodyPose().getY();
            this.body.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyPose().getZ();
            this.leftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmPose().getX();
            this.leftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmPose().getY();
            this.leftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmPose().getZ();
            this.rightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmPose().getX();
            this.rightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmPose().getY();
            this.rightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmPose().getZ();
            this.leftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegPose().getX();
            this.leftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegPose().getY();
            this.leftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegPose().getZ();
            this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
            this.rightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegPose().getX();
            this.rightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegPose().getY();
            this.rightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegPose().getZ();
            this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
            this.hat.copyFrom(this.head);
        } else {
            super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
