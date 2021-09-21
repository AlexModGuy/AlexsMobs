package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ModelEnderiophage extends AdvancedEntityModel<EntityEnderiophage> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox mouth;
    private final AdvancedModelBox sheath;
    private final AdvancedModelBox collar;
    private final AdvancedModelBox capsid;
    private final AdvancedModelBox eye;
    private final AdvancedModelBox tailmid_left;
    private final AdvancedModelBox tailmid_right;
    private final AdvancedModelBox tailback_left;
    private final AdvancedModelBox tailback_right;
    private final AdvancedModelBox tailfront_left;
    private final AdvancedModelBox tailfront_right;
    private final AdvancedModelBox tailmid_leftPivot;
    private final AdvancedModelBox tailmid_rightPivot;
    private final AdvancedModelBox tailback_leftPivot;
    private final AdvancedModelBox tailback_rightPivot;
    private final AdvancedModelBox tailfront_leftPivot;
    private final AdvancedModelBox tailfront_rightPivot;

    public ModelEnderiophage() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -11.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 30).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);

        mouth = new AdvancedModelBox(this);
        mouth.setPos(0.0F, 1.0F, 0.0F);
        body.addChild(mouth);
        mouth.setTextureOffset(0, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        sheath = new AdvancedModelBox(this);
        sheath.setPos(0.0F, -2.0F, 0.0F);
        body.addChild(sheath);
        sheath.setTextureOffset(50, 43).addBox(-2.0F, -14.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, false);

        collar = new AdvancedModelBox(this);
        collar.setPos(0.0F, -14.0F, 0.0F);
        sheath.addChild(collar);
        collar.setTextureOffset(0, 55).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);

        capsid = new AdvancedModelBox(this);
        capsid.setPos(0.0F, -1.0F, 0.0F);
        collar.addChild(capsid);
        capsid.setTextureOffset(0, 0).addBox(-7.0F, -15.0F, -7.0F, 14.0F, 15.0F, 14.0F, 0.0F, false);

        eye = new AdvancedModelBox(this);
        eye.setPos(0.0F, -8.0F, 0.0F);
        capsid.addChild(eye);
        eye.setTextureOffset(43, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

        tailmid_leftPivot = new AdvancedModelBox(this);
        tailmid_leftPivot.setPos(4.0F, -1.0F, 0.0F);
        body.addChild(tailmid_leftPivot);

        tailmid_left = new AdvancedModelBox(this);
        tailmid_leftPivot.addChild(tailmid_left);
        tailmid_left.setTextureOffset(25, 43).addBox(0.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, 0.0F, false);

        tailmid_rightPivot = new AdvancedModelBox(this);
        tailmid_rightPivot.setPos(-4.0F, -1.0F, 0.0F);
        body.addChild(tailmid_rightPivot);

        tailmid_right = new AdvancedModelBox(this);
        tailmid_rightPivot.addChild(tailmid_right);
        tailmid_right.setTextureOffset(25, 43).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, 0.0F, true);

        tailback_leftPivot = new AdvancedModelBox(this);
        tailback_leftPivot.setPos(4.0F, -1.0F, 4.0F);
        body.addChild(tailback_leftPivot);
        setRotationAngle(tailback_leftPivot, 0.0F, -0.7854F, 0.0F);

        tailback_left = new AdvancedModelBox(this);
        tailback_leftPivot.addChild(tailback_left);
        tailback_left.setTextureOffset(33, 30).addBox(0.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, 0.0F, false);

        tailback_rightPivot = new AdvancedModelBox(this);
        tailback_rightPivot.setPos(-4.0F, -1.0F, 4.0F);
        body.addChild(tailback_rightPivot);
        setRotationAngle(tailback_rightPivot, 0.0F, 0.7854F, 0.0F);

        tailback_right = new AdvancedModelBox(this);
        tailback_rightPivot.addChild(tailback_right);
        tailback_right.setTextureOffset(33, 30).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, 0.0F, true);

        tailfront_leftPivot = new AdvancedModelBox(this);
        tailfront_leftPivot.setPos(4.0F, -1.0F, -4.0F);
        body.addChild(tailfront_leftPivot);
        setRotationAngle(tailfront_leftPivot, 0.0F, 0.6981F, 0.0F);

        tailfront_left = new AdvancedModelBox(this);
        tailfront_leftPivot.addChild(tailfront_left);
        tailfront_left.setTextureOffset(0, 42).addBox(0.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, 0.0F, false);


        tailfront_rightPivot = new AdvancedModelBox(this);
        tailfront_rightPivot.setPos(-4.0F, -1.0F, -4.0F);
        body.addChild(tailfront_rightPivot);
        setRotationAngle(tailfront_rightPivot, 0.0F, -0.6981F, 0.0F);

        tailfront_right = new AdvancedModelBox(this);
        tailfront_rightPivot.addChild(tailfront_right);
        tailfront_right.setTextureOffset(0, 42).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, tailback_left, tailback_right, tailfront_left, tailfront_right, tailmid_left, tailmid_right, tailback_leftPivot, tailback_rightPivot, tailfront_leftPivot, tailfront_rightPivot, tailmid_leftPivot, tailmid_rightPivot, body, capsid, eye, mouth, sheath, collar);
    }

    @Override
    public void setupAnim(EntityEnderiophage entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        Entity look = Minecraft.getInstance().getCameraEntity();
        float partialTicks = ageInTicks - entity.tickCount;
        float idleSpeed = 0.25F;
        float idleDegree = 0.1F;
        float walkSpeed = 2F;
        float walkDegree = 0.6F;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTicks;
        float phagePitch = (float) Math.toRadians(Mth.rotLerp(partialTicks, entity.prevPhagePitch, entity.getPhagePitch()));
        float totalYaw = (float) Math.toRadians(Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot));
        float tentacleProgress = (5F - limbSwingAmount * 10F) * flyProgress * 0.2F;

        this.bob(eye, idleSpeed, idleDegree * -8, false, ageInTicks, 1);
        this.flap(tailback_left, idleSpeed, idleDegree, true, -2, 0.5F, ageInTicks, 1);
        this.flap(tailback_right, idleSpeed, idleDegree, false, -2, 0.5F, ageInTicks, 1);
        this.walk(tailback_left, idleSpeed, idleDegree, false, -2, 0.25F, ageInTicks, 1);
        this.walk(tailback_right, idleSpeed, idleDegree, false, -2, 0.25F, ageInTicks, 1);
        this.flap(tailmid_left, idleSpeed, idleDegree, true, -2, 0.5F, ageInTicks, 1);
        this.flap(tailmid_right, idleSpeed, idleDegree, false, -2, 0.5F, ageInTicks, 1);
        this.flap(tailfront_left, idleSpeed, idleDegree, true, -2, 0.5F, ageInTicks, 1);
        this.flap(tailfront_right, idleSpeed, idleDegree, false, -2, 0.5F, ageInTicks, 1);
        this.walk(tailfront_left, idleSpeed, idleDegree, true, -2, 0.25F, ageInTicks, 1);
        this.walk(tailfront_right, idleSpeed, idleDegree, true, -2, 0.25F, ageInTicks, 1);
        this.bob(body, idleSpeed, idleDegree * 8, false, ageInTicks, 1);
        this.body.rotationPointY += 8F;

        if (flyProgress != 5) {
            limbSwingAmount = limbSwingAmount * (1 - (flyProgress * 0.2F));
            this.walk(sheath, walkSpeed, walkDegree * 0.2F, true, 1, 0.05F, limbSwing, limbSwingAmount);
            this.swing(tailfront_right, walkSpeed, walkDegree * -1.2F, false, 0, -0.3F, limbSwing, limbSwingAmount);
            this.swing(tailfront_left, walkSpeed, walkDegree * -1.2F, false, 0, 0.3F, limbSwing, limbSwingAmount);
            this.flap(tailfront_right, walkSpeed, walkDegree * -1.6F, false, 0, -0.2F, limbSwing, limbSwingAmount);
            this.flap(tailfront_left, walkSpeed, walkDegree * -1.6F, false, 0, 0.2F, limbSwing, limbSwingAmount);
            this.walk(tailfront_right, walkSpeed, walkDegree * -1.6F, true, 0, 0.3F, limbSwing, limbSwingAmount);
            this.walk(tailfront_left, walkSpeed, walkDegree * -1.6F, false, 0, -0.3F, limbSwing, limbSwingAmount);

            this.swing(tailmid_right, walkSpeed, walkDegree * -1.2F, false, -2.5F, 0.2F, limbSwing, limbSwingAmount);
            this.swing(tailmid_left, walkSpeed, walkDegree * -1.2F, false, -2.5F, -0.2F, limbSwing, limbSwingAmount);
            this.flap(tailmid_right, walkSpeed, walkDegree * -1.6F, false, -2.5F, 0.5F, limbSwing, limbSwingAmount);
            this.flap(tailmid_left, walkSpeed, walkDegree * -1.6F, false, -2.5F, -0.5F, limbSwing, limbSwingAmount);
            this.walk(tailmid_right, walkSpeed, walkDegree * -1.6F, true, -2.5F, 0.3F, limbSwing, limbSwingAmount);
            this.walk(tailmid_left, walkSpeed, walkDegree * -1.6F, false, -2.5F, -0.3F, limbSwing, limbSwingAmount);

            this.swing(tailback_right, walkSpeed, walkDegree * -1.2F, false, -5, -0.2F, limbSwing, limbSwingAmount);
            this.swing(tailback_left, walkSpeed, walkDegree * -1.2F, false, -5, 0.2F, limbSwing, limbSwingAmount);
            this.flap(tailback_right, walkSpeed, walkDegree * -1.6F, false, -5, 0.5F, limbSwing, limbSwingAmount);
            this.flap(tailback_left, walkSpeed, walkDegree * -1.6F, false, -5, -0.5F, limbSwing, limbSwingAmount);
            this.walk(tailback_right, walkSpeed, walkDegree * -1.6F, true, -5, 0.3F, limbSwing, limbSwingAmount);
            this.walk(tailback_left, walkSpeed, walkDegree * -1.6F, false, -5, -0.3F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed * 1.5F, walkDegree * 6, false, limbSwing, limbSwingAmount);
            progressRotationPrev(body, limbSwingAmount, (float) Math.toRadians(-15), 0, 0, 1F);
            progressRotationPrev(sheath, limbSwingAmount, (float) Math.toRadians(-15), 0, 0, 1F);
            progressRotationPrev(tailfront_left, limbSwingAmount, (float) Math.toRadians(15), 0, 0, 1F);
            progressRotationPrev(tailfront_right, limbSwingAmount, (float) Math.toRadians(15), 0, 0, 1F);
        }

        if(entity.isMissingEye()){
            this.eye.showModel = false;
        }else{
            this.eye.showModel = true;
        }
        if(entity.isPassenger()){
            this.body.rotateAngleX += Math.PI/2F;
            this.body.rotateAngleY += Math.PI/2F * entity.passengerIndex;
            this.sheath.setScale(1F, (float) (0.85F + Math.sin(ageInTicks) * 0.15F), 1F);
            this.collar.rotationPointY -= (float) (Math.sin(ageInTicks) * 0.15F - 0.15F) * 12F;
            this.capsid.setScale((float) (0.85F + Math.sin(ageInTicks + 2F) * 0.15F), (float) (1F + Math.sin(ageInTicks) * 0.15F), (float) (0.85F + Math.sin(ageInTicks + 2F) * 0.15F));
            this.mouth.rotationPointY += (Math.sin(ageInTicks) + 1F) * 2F;
            tentacleProgress = -2F;
        }else{
            this.sheath.setScale(1F,1F, 1F);
            this.capsid.setScale(1F,1F, 1F);
            this.body.rotateAngleX -= phagePitch * flyProgress * 0.2F;
        }
        progressPositionPrev(body, tentacleProgress, 0, -6, 0, 5F);
        progressRotationPrev(tailfront_left, tentacleProgress, 0, 0, (float) Math.toRadians(-45), 5F);
        progressRotationPrev(tailmid_left, tentacleProgress, 0, 0, (float) Math.toRadians(-45), 5F);
        progressRotationPrev(tailback_left, tentacleProgress, 0, 0, (float) Math.toRadians(-45), 5F);
        progressRotationPrev(tailfront_right, tentacleProgress, 0, 0, (float) Math.toRadians(45), 5F);
        progressRotationPrev(tailmid_right, tentacleProgress, 0, 0, (float) Math.toRadians(45), 5F);
        progressRotationPrev(tailback_right, tentacleProgress, 0, 0, (float) Math.toRadians(45), 5F);

        if (look != null) {
            Vec3 vector3d = look.getEyePosition(partialTicks);
            Vec3 vector3d1 = entity.getEyePosition(partialTicks);
            Vec3 vector3d2 = vector3d.subtract(vector3d1);
            float f = Mth.sqrt((float)(vector3d2.x * vector3d2.x + vector3d2.z * vector3d2.z)) - totalYaw;
            this.eye.rotateAngleY += -(float) (Mth.atan2(vector3d2.x, vector3d2.z)) - totalYaw;
            this.eye.rotateAngleX += -Mth.clamp(vector3d2.y * 0.5F, Math.PI * -0.5F, Math.PI * 0.5F) + phagePitch * flyProgress * 0.2F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}