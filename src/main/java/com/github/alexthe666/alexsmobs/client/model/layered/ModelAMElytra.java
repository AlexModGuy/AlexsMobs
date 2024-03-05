package com.github.alexthe666.alexsmobs.client.model.layered;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ModelAMElytra extends HumanoidModel {
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ModelAMElytra(ModelPart part) {
        super(part);
        this.leftWing = part.getChild("body").getChild("left_wing");
        this.rightWing = part.getChild("body").getChild("right_wing");
    }

    public static LayerDefinition createLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot().getChild("body");
        CubeDeformation cubedeformation = new CubeDeformation(1.0F);
        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(32, 32).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation), PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, -0.2617994F));
        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(32, 32).mirror().addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, 0.2617994F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public ModelAMElytra withAnimations(LivingEntity entity){
        if(entity != null) {
            final float partialTick = Minecraft.getInstance().getFrameTime();
            final float limbSwingAmount = entity.walkAnimation.speed(partialTick);
            final float limbSwing = entity.walkAnimation.position() + partialTick;
            setupAnim(entity, limbSwing, limbSwingAmount, entity.tickCount + partialTick, 0, 0);
        }
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
            f1 = f4 * (-Mth.HALF_PI) + (1.0F - f4) * f1;
        } else if (entityIn.isCrouching()) {
            f = 0.6981317F;
            f1 = -Maths.QUARTER_PI;
            f2 = -1.0F;
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
