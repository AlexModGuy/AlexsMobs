package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelAnteater extends AdvancedEntityModel<EntityAnteater> {

    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox head;
    public final AdvancedModelBox left_ear;
    public final AdvancedModelBox right_ear;
    public final AdvancedModelBox snout;
    public final AdvancedModelBox tongue1;
    public final AdvancedModelBox tongue2;
    public final AdvancedModelBox left_leg;
    public final AdvancedModelBox right_leg;
    public final AdvancedModelBox left_arm;
    public final AdvancedModelBox left_claws;
    public final AdvancedModelBox right_arm;
    public final AdvancedModelBox right_claws;
    public final AdvancedModelBox tail;
    public ModelAnimator animator;

    public ModelAnteater() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -13.0F, 4.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-4.0F, -5.0F, -14.0F, 8.0F, 10.0F, 21.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -4.0F, -15.0F);
        body.addChild(head);
        head.setTextureOffset(38, 0).addBox(-2.0F, -1.0F, -7.0F, 4.0F, 5.0F, 8.0F, 0.0F, false);

        left_ear = new AdvancedModelBox(this, "left_ear");
        left_ear.setRotationPoint(2.0F, 0.0F, -4.0F);
        head.addChild(left_ear);
        left_ear.setTextureOffset(11, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        right_ear = new AdvancedModelBox(this, "right_ear");
        right_ear.setRotationPoint(-2.0F, 0.0F, -4.0F);
        head.addChild(right_ear);
        right_ear.setTextureOffset(11, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, true);

        snout = new AdvancedModelBox(this, "snout");
        snout.setRotationPoint(0.0F, 1.5F, -6.5F);
        head.addChild(snout);
        setRotationAngle(snout, 0.2618F, 0.0F, 0.0F);
        snout.setTextureOffset(28, 32).addBox(-1.0F, -1.0F, -10.0F, 2.0F, 3.0F, 10.0F, 0.0F, false);

        tongue1 = new AdvancedModelBox(this, "tongue1");
        tongue1.setRotationPoint(0.0F, 1.0F, -10.0F);
        snout.addChild(tongue1);
        tongue1.setTextureOffset(43, 32).addBox(-0.5F, 0.0F, -6.0F, 1.0F, 0.0F, 6.0F, 0.0F, false);

        tongue2 = new AdvancedModelBox(this, "tongue2");
        tongue2.setRotationPoint(0.0F, 0.0F, -6.0F);
        tongue1.addChild(tongue2);
        tongue2.setTextureOffset(38, 14).addBox(-0.5F, 0.0F, -6.0F, 1.0F, 0.0F, 6.0F, 0.0F, false);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(2.5F, 4.0F, 5.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(0, 32).addBox(-1.5F, 1.0F, -2.0F, 3.0F, 8.0F, 4.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-2.5F, 4.0F, 5.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(0, 32).addBox(-1.5F, 1.0F, -2.0F, 3.0F, 8.0F, 4.0F, 0.0F, true);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(3.1F, 4.0F, -11.9F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(0, 0).addBox(-2.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, false);

        left_claws = new AdvancedModelBox(this, "left_claws");
        left_claws.setRotationPoint(-1.0F, 9.0F, 2.0F);
        left_arm.addChild(left_claws);
        left_claws.setTextureOffset(13, 13).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        left_claws.setTextureOffset(0, 15).addBox(-1.0F, -2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-3.1F, 4.0F, -11.9F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, true);

        right_claws = new AdvancedModelBox(this, "right_claws");
        right_claws.setRotationPoint(1.0F, 9.0F, 2.0F);
        right_arm.addChild(right_claws);
        right_claws.setTextureOffset(13, 13).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, 0.0F, true);
        right_claws.setTextureOffset(0, 15).addBox(1.0F, -2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -5.0F, 7.0F);
        body.addChild(tail);
        tail.setTextureOffset(0, 32).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 11.0F, 19.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void animate(EntityAnteater entity, float f, float f1, float f2, float f3, float f4) {
        float partialTick = f2 - entity.tickCount;
        float standProgress = (entity.prevStandProgress + (entity.standProgress - entity.prevStandProgress) * partialTick) * 0.2F;
        float inverStandProgress = 1 - standProgress;
        animator.update(entity);
        animator.setAnimation(EntityAnteater.ANIMATION_SLASH_L);
        animator.startKeyframe(5);
        animator.rotate(body, Maths.rad(-15), Maths.rad(-15), 0);
        animator.rotate(tail, Maths.rad(25), Maths.rad(15), 0);
        animator.rotate(head, Maths.rad(15), Maths.rad(15), 0);
        animator.rotate(right_leg, Maths.rad(15), 0, 0);
        animator.rotate(left_leg, Maths.rad(15), 0, 0);
        animator.rotate(left_arm, Maths.rad(-50), 0, Maths.rad(-45));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(head, Maths.rad(5), 0, 0);
        animator.move(left_arm, 0, 2, 0);
        animator.rotate(left_arm, Maths.rad(-10) + Maths.rad(-70) * inverStandProgress, 0, Maths.rad(65) * standProgress);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityAnteater.ANIMATION_SLASH_R);
        animator.startKeyframe(5);
        animator.rotate(body, Maths.rad(-15), Maths.rad(15), 0);
        animator.rotate(tail, Maths.rad(25), Maths.rad(-15), 0);
        animator.rotate(head, Maths.rad(15), Maths.rad(-15), 0);
        animator.rotate(right_leg, Maths.rad(15), 0, 0);
        animator.rotate(left_leg, Maths.rad(15), 0, 0);
        animator.rotate(right_arm, Maths.rad(-50), 0, Maths.rad(45));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(head, Maths.rad(5), 0, 0);
        animator.move(right_arm, 0, 2, 0);
        animator.rotate(right_arm, Maths.rad(-10) + Maths.rad(-70) * inverStandProgress, 0, Maths.rad(-65) * standProgress);
        animator.endKeyframe();
        animator.resetKeyframe(5);

    }


    @Override
    public void setupAnim(EntityAnteater entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float tongueSpeed = 0.7F;
        float tongueDegree = 0.35F;
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.2F;
        float partialTick = ageInTicks - entity.tickCount;
        float standProgress = entity.prevStandProgress + (entity.standProgress - entity.prevStandProgress) * partialTick;
        float feedProgress = entity.prevTongueProgress + (entity.tongueProgress - entity.prevTongueProgress) * partialTick;
        float leaningProgress = entity.prevLeaningProgress + (entity.leaningProgress - entity.prevLeaningProgress) * partialTick;
        progressRotationPrev(body, standProgress, Maths.rad(-80), 0, 0, 5F);
        progressRotationPrev(left_leg, standProgress, Maths.rad(80), Maths.rad(10), 0, 5F);
        progressRotationPrev(right_leg, standProgress, Maths.rad(80), Maths.rad(-10), 0, 5F);
        progressRotationPrev(tail, standProgress, Maths.rad(80), 0, 0, 5F);
        progressRotationPrev(left_arm, standProgress, Maths.rad(45), Maths.rad(-70), Maths.rad(-120), 5F);
        progressRotationPrev(right_arm, standProgress, Maths.rad(45), Maths.rad(70), Maths.rad(120), 5F);
        progressRotationPrev(head, standProgress, Maths.rad(80), 0, 0, 5F);
        progressPositionPrev(head, standProgress, 0, 1, -2, 5F);
        progressPositionPrev(body, standProgress, 0, -1, -2, 5F);
        progressPositionPrev(tail, standProgress, 0, 2, -3, 5F);
        progressPositionPrev(left_leg, standProgress, 0, -2, 0, 5F);
        progressPositionPrev(right_leg, standProgress, 0, -2, 0, 5F);
        progressPositionPrev(left_arm, standProgress, 1, -1, 2, 5F);
        progressPositionPrev(right_arm, standProgress, -1, -1, 2, 5F);
        if(entity.isBaby() && entity.isPassenger()){
            progressRotationPrev(left_arm, 1, 0,  Maths.rad(-90), Maths.rad(-60), 1);
            progressRotationPrev(right_arm, 1, 0,  Maths.rad(90), Maths.rad(60), 1);
            progressRotationPrev(left_leg, 1, Maths.rad(20), 0, Maths.rad(-60), 1);
            progressRotationPrev(right_leg, 1, Maths.rad(20), 0, Maths.rad(60), 1);
            progressRotationPrev(tail, 1, Maths.rad(-20), 0, 0, 1);
        }
        progressRotationPrev(head, leaningProgress, Maths.rad(50), 0, 0, 20F);
        double tongueM = Math.min(Math.sin(ageInTicks * 0.15F), 0);
        float toungeF = 12F + 12F * (float) tongueM * (feedProgress * 0.2F);
        float toungeMinus = (float) -tongueM * (feedProgress * 0.2F);
        this.walk(tongue1, tongueSpeed * 2F, tongueDegree, false, 0F, 0F, ageInTicks,  toungeMinus);
        this.walk(tongue2, tongueSpeed * 2F, tongueDegree, false, 0F, 0F, ageInTicks,  toungeMinus);
        this.tongue1.rotationPointZ += toungeF;
        this.walk(tail, idleSpeed, idleDegree, true, 2F, 0.2F, ageInTicks, 1);
        this.walk(right_arm, idleSpeed, idleDegree, true, 2F, 0.2F, ageInTicks, standProgress * 0.2F);
        this.walk(left_arm, idleSpeed, idleDegree, true, 2F, 0.2F, ageInTicks, standProgress * 0.2F);
        this.walk(right_leg, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_leg, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_arm, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_arm, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.2F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree * 2F, true, limbSwing, limbSwingAmount);
        this.bob(head, walkSpeed, walkDegree, true, limbSwing, limbSwingAmount);
        if(standProgress <= 0.0F){
            this.faceTarget(netHeadYaw, headPitch, 1, head);
        }

    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, head, left_ear, right_ear, left_arm, right_arm, left_leg, right_leg, left_claws, right_claws, snout, tongue1, tongue2);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        if (this.young) {
            float f = 1.35F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            this.head.setScale(1F, 1F, 1F);
        } else {
            this.head.setScale(1F, 1F, 1F);
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
