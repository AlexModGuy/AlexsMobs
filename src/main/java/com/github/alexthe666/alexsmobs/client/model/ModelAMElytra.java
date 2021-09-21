package com.github.alexthe666.alexsmobs.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.Vec3;

public class ModelAMElytra extends HumanoidModel {

    private final ModelPart rightWing = new ModelPart(this, 22, 0);
    private final ModelPart leftWing = new ModelPart(this, 22, 0);

    public ModelAMElytra() {
        super(0);
        this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
        this.rightWing.mirror = true;
        this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.leftWing, this.rightWing);
    }


    public ModelAMElytra withAnimations(LivingEntity entity){
        float partialTick = Minecraft.getInstance().getFrameTime();
        float limbSwingAmount = entity.animationSpeedOld + (entity.animationSpeed - entity.animationSpeedOld) * partialTick;
        float limbSwing = entity.animationPosition + partialTick;
        setupAnim(entity, limbSwing, limbSwingAmount, entity.tickCount + partialTick, 0, 0);
        return  this;
    }

    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 0.2617994F;
        float f1 = -0.2617994F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (entityIn.isFallFlying()) {
            float f4 = 1.0F;
            Vec3 vector3d = entityIn.getDeltaMovement();
            if (vector3d.y < 0.0D) {
                Vec3 vector3d1 = vector3d.normalize();
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

        this.leftWing.x = 5.0F;
        this.leftWing.y = f2;
        if (entityIn instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayerentity = (AbstractClientPlayer)entityIn;
            abstractclientplayerentity.elytraRotX = (float)((double)abstractclientplayerentity.elytraRotX + (double)(f - abstractclientplayerentity.elytraRotX) * 0.1D);
            abstractclientplayerentity.elytraRotY = (float)((double)abstractclientplayerentity.elytraRotY + (double)(f3 - abstractclientplayerentity.elytraRotY) * 0.1D);
            abstractclientplayerentity.elytraRotZ = (float)((double)abstractclientplayerentity.elytraRotZ + (double)(f1 - abstractclientplayerentity.elytraRotZ) * 0.1D);
            this.leftWing.xRot = abstractclientplayerentity.elytraRotX;
            this.leftWing.yRot = abstractclientplayerentity.elytraRotY;
            this.leftWing.zRot = abstractclientplayerentity.elytraRotZ;
        } else {
            this.leftWing.xRot = f;
            this.leftWing.zRot = f1;
            this.leftWing.yRot = f3;
        }

        this.rightWing.x = -this.leftWing.x;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.y = this.leftWing.y;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.zRot = -this.leftWing.zRot;
    }
}
