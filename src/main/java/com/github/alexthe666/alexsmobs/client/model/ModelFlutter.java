package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ModelFlutter extends AdvancedEntityModel<EntityFlutter> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox eyes;
    private final AdvancedModelBox petals;
    private final AdvancedModelBox front_petal;
    private final AdvancedModelBox left_petal;
    private final AdvancedModelBox right_petal;
    private final AdvancedModelBox back_petal;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox right_leg;
    private final AdvancedModelBox right_arm;

    public ModelFlutter() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -3.9F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 13).addBox(-3.5F, -3.0F, -3.5F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-3.5F, -3.0F, -3.5F, 7.0F, 5.0F, 7.0F, -0.2F, false);

        eyes = new AdvancedModelBox(this, "eyes");
        eyes.setRotationPoint(0.0F, -1.0F, -3.0F);
        body.addChild(eyes);
        eyes.setTextureOffset(23, 30).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, 0.0F, false);

        petals = new AdvancedModelBox(this, "petals");
        petals.setRotationPoint(0.0F, -3.0F, 0.0F);
        body.addChild(petals);


        front_petal = new AdvancedModelBox(this, "front_petal");
        front_petal.setRotationPoint(0.0F, 0.0F, -1.5F);
        petals.addChild(front_petal);
        setRotationAngle(front_petal, 1.1781F, 0.0F, 0.0F);
        front_petal.setTextureOffset(0, 26).addBox(-3.5F, -7.0F, 0.0F, 7.0F, 7.0F, 0.0F, 0.0F, false);

        left_petal = new AdvancedModelBox(this, "left_petal");
        left_petal.setRotationPoint(1.5F, 0.0F, 0.0F);
        petals.addChild(left_petal);
        setRotationAngle(left_petal, 1.1781F, -1.5708F, 0.0F);
        left_petal.setTextureOffset(0, 26).addBox(-3.5F, -7.0F, 0.0F, 7.0F, 7.0F, 0.0F, 0.0F, false);

        right_petal = new AdvancedModelBox(this, "right_petal");
        right_petal.setRotationPoint(-1.5F, 0.0F, 0.0F);
        petals.addChild(right_petal);
        setRotationAngle(right_petal, 1.1781F, 1.5708F, 0.0F);
        right_petal.setTextureOffset(0, 26).addBox(-3.5F, -7.0F, 0.0F, 7.0F, 7.0F, 0.0F, 0.0F, true);

        back_petal = new AdvancedModelBox(this, "back_petal");
        back_petal.setRotationPoint(0.0F, 0.0F, 1.5F);
        petals.addChild(back_petal);
        setRotationAngle(back_petal, 1.1781F, 3.1416F, 0.0F);
        back_petal.setTextureOffset(0, 26).addBox(-3.5F, -7.0F, 0.0F, 7.0F, 7.0F, 0.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(3.0F, 1.9F, -3.0F);
        body.addChild(left_arm);
        setRotationAngle(left_arm, 0.0F, -0.7418F, 0.0F);
        left_arm.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(3.0F, 1.9F, 3.0F);
        body.addChild(left_leg);
        setRotationAngle(left_leg, -3.1416F, -0.7418F, 3.1416F);
        left_leg.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-3.0F, 1.9F, 3.0F);
        body.addChild(right_leg);
        setRotationAngle(right_leg, -3.1416F, 0.7418F, -3.1416F);
        right_leg.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, true);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-3.0F, 1.9F, -3.0F);
        body.addChild(right_arm);
        setRotationAngle(right_arm, 0.0F, 0.7418F, 0.0F);
        right_arm.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, eyes, petals, front_petal, left_petal, back_petal, right_petal, left_arm, right_arm, left_leg, right_leg);
    }

    @Override
    public void setupAnim(EntityFlutter entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.25F;
        float idleDegree = 0.1F;
        float walkSpeed = 1.6F;
        float walkDegree = 1.2F;
        float partialTicks = ageInTicks - entity.tickCount;
        float shootProgress = entity.prevShootProgress + (entity.shootProgress - entity.prevShootProgress) * partialTicks;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTicks;
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTicks;
        float groundProgress = (5F - flyProgress) * 0.2F;
        float tentacleProgress = (5F - limbSwingAmount * 5F) * flyProgress * 0.2F;
        float invertTentacle = (entity.prevTentacleProgress + (entity.tentacleProgress - entity.prevTentacleProgress) * partialTicks) * flyProgress * 0.2F;
        float flutterPitch = (float) Math.toRadians(Mth.rotLerp(partialTicks, entity.prevFlutterPitch, entity.getFlutterPitch()));
        Entity look = Minecraft.getInstance().getCameraEntity();
        if(entity.isShakingHead()){
            this.eyes.rotationPointX += Math.sin(ageInTicks);
            this.body.rotateAngleY += Math.sin(ageInTicks) * 0.1F;
            this.eyes.rotationPointY = -0.5F;
        }else if (look != null) {
            Vec3 vector3d = look.getEyePosition(0.0F);
            Vec3 vector3d1 = entity.getEyePosition(0.0F);
            double d0 = vector3d.y - vector3d1.y;
            float f1 = (float)Mth.clamp(-d0 - 0.5F, -2.0F, 0F);
            this.eyes.rotationPointY = f1;
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot((Mth.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            this.eyes.rotationPointX += Mth.sqrt((float) Math.abs(d1)) * 1.5F * (float) Math.signum(d1);
        }else{
            this.eyes.rotationPointY = -1.0F;
        }
        this.walk(right_arm, walkSpeed, walkDegree * 1.5F, true, 0, 0.3F, limbSwing, limbSwingAmount * groundProgress);
        this.walk(left_arm, walkSpeed, walkDegree * 1.5F, false, 0, -0.3F, limbSwing, limbSwingAmount * groundProgress);
        this.walk(right_leg, walkSpeed, walkDegree * 1.5F, false, 0, -0.3F, limbSwing, limbSwingAmount * groundProgress);
        this.walk(left_leg, walkSpeed, walkDegree * 1.5F, true, 0, 0.3F, limbSwing, limbSwingAmount * groundProgress);
        this.swing(body, walkSpeed, walkDegree * 0.2F, true, 0, 0, limbSwing, limbSwingAmount * groundProgress);
        this.flap(body, walkSpeed, walkDegree * 0.2F, true, 0, 0, limbSwing, limbSwingAmount * groundProgress);
        this.bob(body, walkSpeed * 1.5F, walkDegree, false, limbSwing, limbSwingAmount * groundProgress);
        this.walk(front_petal, idleSpeed, idleDegree, true, 1, 0.1F, ageInTicks, 1);
        this.walk(back_petal, idleSpeed, idleDegree, true, 1, 0.1F, ageInTicks, 1);
        this.flap(right_petal, idleSpeed, idleDegree, false, 1, 0.1F, ageInTicks, 1);
        this.flap(left_petal, idleSpeed, idleDegree, true, 1, 0.1F, ageInTicks, 1);
        progressRotationPrev(front_petal, Math.max(shootProgress, invertTentacle),  (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(back_petal, Math.max(shootProgress, invertTentacle),  (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(right_petal, Math.max(shootProgress, invertTentacle),  0, 0, (float) Math.toRadians(45), 5F);
        progressRotationPrev(left_petal, Math.max(shootProgress, invertTentacle), 0, 0, (float) Math.toRadians(-45), 5F);
        progressRotationPrev(front_petal, Math.max(invertTentacle - shootProgress, 0),  (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(back_petal, Math.max(invertTentacle - shootProgress, 0),  (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(right_petal, Math.max(invertTentacle - shootProgress, 0),  0, 0, (float) Math.toRadians(45), 5F);
        progressRotationPrev(left_petal, Math.max(invertTentacle - shootProgress, 0), 0, 0, (float) Math.toRadians(-45), 5F);
        progressRotationPrev(front_petal, flyProgress,  (float) Math.toRadians(15), 0, 0, 5F);
        progressRotationPrev(back_petal, flyProgress,  (float) Math.toRadians(15), 0, 0, 5F);
        progressRotationPrev(right_petal, flyProgress,  0, 0, (float) Math.toRadians(-15), 5F);
        progressRotationPrev(left_petal, flyProgress, 0, 0, (float) Math.toRadians(15), 5F);
        progressPositionPrev(body, tentacleProgress, 0, -3, 0, 5F);
        progressRotationPrev(right_leg, tentacleProgress, (float) Math.toRadians(105), 0, 0, 5F);
        progressRotationPrev(left_leg, tentacleProgress, (float) Math.toRadians(105), 0, 0, 5F);
        progressRotationPrev(right_arm, tentacleProgress, (float) Math.toRadians(105), 0, 0, 5F);
        progressRotationPrev(left_arm, tentacleProgress, (float) Math.toRadians(105), 0, 0, 5F);
        progressRotationPrev(front_petal, tentacleProgress, (float) Math.toRadians(5), 0, 0, 5F);
        progressRotationPrev(back_petal, tentacleProgress, (float) Math.toRadians(5), 0, 0, 5F);
        progressRotationPrev(right_petal, tentacleProgress, (float) Math.toRadians(5), 0, 0, 5F);
        progressRotationPrev(left_petal, tentacleProgress, (float) Math.toRadians(5), 0, 0, 5F);

        progressPositionPrev(body, sitProgress, 0, 2, 0, 5F);
        progressRotationPrev(right_leg, sitProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(left_leg, sitProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(right_arm, sitProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(left_arm, sitProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        this.root.rotateAngleX -= flutterPitch * flyProgress * 0.2F;
        this.body.rotateAngleY += Math.toRadians(Mth.wrapDegrees(shootProgress * 360 * 0.2F));
        float petalScale = 1 + invertTentacle * 0.05F;
        this.front_petal.setScale(1, petalScale, 1);
        this.back_petal.setScale(1, petalScale, 1);
        this.left_petal.setScale(1, petalScale, 1);
        this.right_petal.setScale(1, petalScale, 1);
        if(entity.isBaby()){
            this.root.rotationPointY += 1.5F;
            body.setScale(0.5F, 0.5F, 0.5F);
            body.setShouldScaleChildren(true);
        }else{
            body.setScale(1, 1, 1);
        }
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
