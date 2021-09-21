package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSombrero extends HumanoidModel {
    public ModelPart sombrero;

    public ModelSombrero(float modelSize) {
        super(modelSize, 0, 128, 128);
        this.texWidth = 128;
        this.texHeight = 128;
        this.sombrero = new ModelPart(this, 0, 64);
        this.sombrero.setPos(0.0F, 0.0F, 0.0F);
        this.sombrero.addBox(-4.0F, -11.0F, -4.0F, 8.0F, 6.0F, 8.0F, 0.5F, 0.5F, 0.5F);
        this.sombrero.setTextureOffset(22, 73).addBox(-11.0F, -8.0F, -11.0F, 22.0F, 3.0F, 22.0F, 0.5F, 0.5F, 0.5F);
        this.head.addChild(sombrero);
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
}
