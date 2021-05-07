package com.github.alexthe666.alexsmobs.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class ModelAMElytra extends BipedModel {

    private final ModelRenderer rightWing = new ModelRenderer(this, 22, 0);
    private final ModelRenderer leftWing = new ModelRenderer(this, 22, 0);

    public ModelAMElytra() {
        super(0);
        this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
        this.rightWing.mirror = true;
        this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
    }

    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.leftWing, this.rightWing);
    }


    public ModelAMElytra withAnimations(LivingEntity entity){
        float partialTick = Minecraft.getInstance().getRenderPartialTicks();
        float limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTick;
        float limbSwing = entity.limbSwing + partialTick;
        setRotationAngles(entity, limbSwing, limbSwingAmount, entity.ticksExisted + partialTick, 0, 0);
        return  this;
    }

    public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 0.2617994F;
        float f1 = -0.2617994F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (entityIn.isElytraFlying()) {
            float f4 = 1.0F;
            Vector3d vector3d = entityIn.getMotion();
            if (vector3d.y < 0.0D) {
                Vector3d vector3d1 = vector3d.normalize();
                f4 = 1.0F - (float)Math.pow(-vector3d1.y, 1.5D);
            }

            f = f4 * 0.34906584F + (1.0F - f4) * f;
            f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
        } else if (entityIn.isCrouching()) {
            f = 0.6981317F;
            f1 = (-(float)Math.PI / 4F);
            f2 = 3.0F;
            f3 = 0.08726646F;
        }

        this.leftWing.rotationPointX = 5.0F;
        this.leftWing.rotationPointY = f2;
        if (entityIn instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)entityIn;
            abstractclientplayerentity.rotateElytraX = (float)((double)abstractclientplayerentity.rotateElytraX + (double)(f - abstractclientplayerentity.rotateElytraX) * 0.1D);
            abstractclientplayerentity.rotateElytraY = (float)((double)abstractclientplayerentity.rotateElytraY + (double)(f3 - abstractclientplayerentity.rotateElytraY) * 0.1D);
            abstractclientplayerentity.rotateElytraZ = (float)((double)abstractclientplayerentity.rotateElytraZ + (double)(f1 - abstractclientplayerentity.rotateElytraZ) * 0.1D);
            this.leftWing.rotateAngleX = abstractclientplayerentity.rotateElytraX;
            this.leftWing.rotateAngleY = abstractclientplayerentity.rotateElytraY;
            this.leftWing.rotateAngleZ = abstractclientplayerentity.rotateElytraZ;
        } else {
            this.leftWing.rotateAngleX = f;
            this.leftWing.rotateAngleZ = f1;
            this.leftWing.rotateAngleY = f3;
        }

        this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
        this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
        this.rightWing.rotationPointY = this.leftWing.rotationPointY;
        this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
        this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
    }
}
