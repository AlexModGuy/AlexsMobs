package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRoadrunnerBoots extends HumanoidModel {
    public ModelPart FeatherR;
    public ModelPart FeatherL;

    public ModelRoadrunnerBoots(float modelSize) {
        super(modelSize, 0, 64, 32);
        this.texWidth = 64;
        this.texHeight = 32;
        this.FeatherR = new ModelPart(this, 0, 0);
        this.FeatherR.mirror = true;
        this.FeatherR.setPos(-2.5F, 9.5F, 0.4F);
        this.FeatherR.texOffs(20, 22).addBox(-3.0F, -7.5F, 0.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(FeatherR, 0.0F, 0.9773843811168246F, -0.3127630032889644F);
        this.FeatherL = new ModelPart(this, 0, 0);
        this.FeatherL.setPos(2.5F, 9.5F, -0.4F);
        this.FeatherL.texOffs(20, 22).addBox(0.0F, -7.4F, 0.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(FeatherL, 0.0F, -0.9773843811168246F, 0.3127630032889644F);
        this.leftLeg.addChild(this.FeatherL);
        this.rightLeg.addChild(this.FeatherR);
    }

    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn instanceof ArmorStand) {
            ArmorStand entityarmorstand = (ArmorStand) entityIn;
            this.head.xRot = 0.017453292F * entityarmorstand.getHeadPose().getX();
            this.head.yRot = 0.017453292F * entityarmorstand.getHeadPose().getY();
            this.head.zRot = 0.017453292F * entityarmorstand.getHeadPose().getZ();
            this.head.setPos(0.0F, 1.0F, 0.0F);
            this.body.xRot = 0.017453292F * entityarmorstand.getBodyPose().getX();
            this.body.yRot = 0.017453292F * entityarmorstand.getBodyPose().getY();
            this.body.zRot = 0.017453292F * entityarmorstand.getBodyPose().getZ();
            this.leftArm.xRot = 0.017453292F * entityarmorstand.getLeftArmPose().getX();
            this.leftArm.yRot = 0.017453292F * entityarmorstand.getLeftArmPose().getY();
            this.leftArm.zRot = 0.017453292F * entityarmorstand.getLeftArmPose().getZ();
            this.rightArm.xRot = 0.017453292F * entityarmorstand.getRightArmPose().getX();
            this.rightArm.yRot = 0.017453292F * entityarmorstand.getRightArmPose().getY();
            this.rightArm.zRot = 0.017453292F * entityarmorstand.getRightArmPose().getZ();
            this.leftLeg.xRot = 0.017453292F * entityarmorstand.getLeftLegPose().getX();
            this.leftLeg.yRot = 0.017453292F * entityarmorstand.getLeftLegPose().getY();
            this.leftLeg.zRot = 0.017453292F * entityarmorstand.getLeftLegPose().getZ();
            this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
            this.rightLeg.xRot = 0.017453292F * entityarmorstand.getRightLegPose().getX();
            this.rightLeg.yRot = 0.017453292F * entityarmorstand.getRightLegPose().getY();
            this.rightLeg.zRot = 0.017453292F * entityarmorstand.getRightLegPose().getZ();
            this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
            this.hat.copyFrom(this.head);
        } else {
            super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
