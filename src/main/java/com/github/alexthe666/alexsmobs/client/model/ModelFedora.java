package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ModelFedora  extends HumanoidModel {
    private final ModelPart fedora;
    private final ModelPart fedora_shade;

    public ModelFedora(float modelSize) {
        super(modelSize, 0, 64, 64);
        texWidth = 64;
        texHeight = 64;
        fedora = new ModelPart(this);
        fedora.setPos(0.0F, -8F, 0.0F);
        fedora.setTextureOffset(0, 44).addBox(-3.0F, -3.55F, -3.0F, 6.0F, 4.0F, 6.0F, modelSize, false);
        fedora_shade = new ModelPart(this);
        fedora_shade.setPos(0.0F, -0.05F, 0.0F);
        fedora_shade.setTextureOffset(0, 32).addBox(-5.0F, -0.5F, -5.0F, 10.0F, 1.0F, 10.0F, modelSize, false);
        head.addChild(fedora);
        fedora.addChild(fedora_shade);
    }

    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        fedora.setPos(0.0F, 8F, 0.0F);
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

    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}