package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelFrontierCap extends HumanoidModel {
    public ModelPart tail;
    public ModelPart hat;

    public ModelFrontierCap(float modelSize) {
        super(modelSize, 0, 64, 64);
        this.texWidth = 64;
        this.texHeight = 64;
        this.tail = new ModelPart(this, 36, 46);
        this.tail.setPos(4.4F, -7.5F, 4.5F);
        this.tail.addBox(-1.5F, -0.3F, -1.5F, 3.0F, 13.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tail, 0.1956514098143546F, -0.03909537541112055F, -0.11728612207217244F);
        this.hat = new ModelPart(this, 32, 32);
        this.hat.setPos(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-4.0F, -10.5F, -4.0F, 8.0F, 4.0F, 8.0F, 1.0F, 1.0F, 1.0F);
        this.head.addChild(this.tail);
        this.head.addChild(this.hat);
    }

    public ModelFrontierCap withAnimations(LivingEntity entity){
        float partialTick = Minecraft.getInstance().getFrameTime();
        float limbSwingAmount = entity.animationSpeedOld + (entity.animationSpeed - entity.animationSpeedOld) * partialTick;
        float limbSwing = entity.animationPosition + partialTick;
        tail.xRot = 0.1956514098143546F + limbSwingAmount * (float) Math.toRadians(80) + Mth.cos(limbSwing * 0.3F) * 0.2F * limbSwingAmount;
        tail.yRot = -0.03909537541112055F + limbSwingAmount * (float) Math.toRadians(10) - Mth.cos(limbSwing * 0.4F) * 0.3F * limbSwingAmount;
        tail.zRot = -0.11728612207217244F + limbSwingAmount * (float) Math.toRadians(10);
        return  this;
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
