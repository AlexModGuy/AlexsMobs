package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelEmu extends AdvancedEntityModel<EntityEmu> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leg_left;
    private final AdvancedModelBox legfur_left;
    private final AdvancedModelBox foot_left;
    private final AdvancedModelBox leg_right;
    private final AdvancedModelBox legfur_right;
    private final AdvancedModelBox foot_right;
    private final AdvancedModelBox neck1;
    private final AdvancedModelBox neck2;
    private final AdvancedModelBox headPivot;
    private final AdvancedModelBox head;
    private final AdvancedModelBox beak;
    private final AdvancedModelBox tail;
    private ModelAnimator animator;

    public ModelEmu() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -19.625F, -0.125F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-6.0F, -4.375F, -10.875F, 12.0F, 11.0F, 21.0F, 0.0F, false);

        leg_left = new AdvancedModelBox(this, "leg_left");
        leg_left.setPos(3.0F, 6.625F, 0.125F);
        body.addChild(leg_left);
        leg_left.setTextureOffset(0, 55).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);

        legfur_left = new AdvancedModelBox(this, "legfur_left");
        legfur_left.setPos(0.0F, 0.0F, 0.0F);
        leg_left.addChild(legfur_left);
        legfur_left.setTextureOffset(31, 33).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);

        foot_left = new AdvancedModelBox(this, "foot_left");
        foot_left.setPos(0.0F, 11.0F, -1.0F);
        leg_left.addChild(foot_left);
        foot_left.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 2.0F, 6.0F, 0.0F, false);

        leg_right = new AdvancedModelBox(this, "leg_right");
        leg_right.setPos(-3.0F, 6.625F, 0.125F);
        body.addChild(leg_right);
        leg_right.setTextureOffset(0, 55).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, true);

        legfur_right = new AdvancedModelBox(this, "legfur_right");
        legfur_right.setPos(0.0F, 0.0F, 0.0F);
        leg_right.addChild(legfur_right);
        legfur_right.setTextureOffset(31, 33).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, true);

        foot_right = new AdvancedModelBox(this, "foot_right");
        foot_right.setPos(0.0F, 11.0F, -1.0F);
        leg_right.addChild(foot_right);
        foot_right.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 2.0F, 6.0F, 0.0F, true);

        neck1 = new AdvancedModelBox(this, "neck1");
        neck1.setPos(0.0F, 0.625F, -9.5F);
        body.addChild(neck1);
        neck1.setTextureOffset(41, 41).addBox(-3.0F, -9.0F, -6.0F, 6.0F, 12.0F, 6.0F, 0.0F, false);

        neck2 = new AdvancedModelBox(this, "neck2");
        neck2.setPos(0.0F, -8.5F, -2.0F);
        neck1.addChild(neck2);
        neck2.setTextureOffset(46, 0).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);

        headPivot = new AdvancedModelBox(this, "headPivot");
        headPivot.setPos(-0.5F, -6.5F, 0.0F);
        neck2.addChild(headPivot);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, 0.0F, 0.0F);
        headPivot.addChild(head);
        head.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -3.0F, 5.0F, 4.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(71, 54).addBox(0.5F, -6.0F, -3.0F, 0.0F, 6.0F, 7.0F, 0.0F, false);

        beak = new AdvancedModelBox(this, "beak");
        beak.setPos(0.5F, -1.0F, -3.0F);
        head.addChild(beak);
        beak.setTextureOffset(46, 12).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 3.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.0F, -0.875F, 9.125F);
        body.addChild(tail);
        tail.setTextureOffset(0, 33).addBox(-5.0F, -0.5F, -5.0F, 10.0F, 11.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityEmu.ANIMATION_DODGE_RIGHT);
        animator.startKeyframe(4);
        animator.move(body, 0, -5, 0);
        animator.rotate(leg_left, 0, 0, Maths.rad(-60));
        animator.rotate(leg_right, 0, 0, Maths.rad(-45));
        animator.rotate(body, 0, 0, Maths.rad(25));
        animator.rotate(neck1, Maths.rad(-15), 0, Maths.rad(20));
        animator.rotate(neck2, Maths.rad(-15), 0, Maths.rad(10));
        animator.rotate(headPivot, Maths.rad(-45), 0, 0);
        animator.rotate(foot_left, Maths.rad(15), 0, 0);
        animator.rotate(foot_right, Maths.rad(15), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityEmu.ANIMATION_DODGE_LEFT);
        animator.startKeyframe(4);
        animator.move(body, 0, -5, 0);
        animator.rotate(leg_left, 0, 0, Maths.rad(60));
        animator.rotate(leg_right, 0, 0, Maths.rad(45));
        animator.rotate(body, 0, 0, Maths.rad(-25));
        animator.rotate(neck1, Maths.rad(-15), 0, Maths.rad(-20));
        animator.rotate(neck2, Maths.rad(-15), 0, Maths.rad(-10));
        animator.rotate(headPivot, Maths.rad(-45), 0, 0);
        animator.rotate(foot_left, Maths.rad(15), 0, 0);
        animator.rotate(foot_right, Maths.rad(15), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityEmu.ANIMATION_PUZZLED);
        animator.startKeyframe(3);
        animator.rotate(body, Maths.rad(-10), 0, 0);
        animator.rotate(leg_left, Maths.rad(10), 0, 0);
        animator.rotate(leg_right, Maths.rad(10), 0, 0);
        animator.rotate(neck1, Maths.rad(15), 0, 0);
        animator.rotate(neck2, Maths.rad(15), 0, Maths.rad(-10));
        animator.rotate(headPivot, Maths.rad(-10),  Maths.rad(-10),  Maths.rad(-35));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(body, Maths.rad(-10), 0, 0);
        animator.rotate(leg_left, Maths.rad(10), 0, 0);
        animator.rotate(leg_right, Maths.rad(10), 0, 0);
        animator.rotate(neck1, Maths.rad(15), 0, 0);
        animator.rotate(neck2, Maths.rad(0), 0, Maths.rad(10));
        animator.rotate(headPivot, Maths.rad(-10),  Maths.rad(10),  Maths.rad(35));
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.startKeyframe(3);
        animator.rotate(body, Maths.rad(10), 0, 0);
        animator.rotate(leg_left, Maths.rad(-10), 0, 0);
        animator.rotate(leg_right, Maths.rad(-10), 0, 0);
        animator.rotate(neck1, Maths.rad(-15), 0, 0);
        animator.rotate(neck2, Maths.rad(15), 0, Maths.rad(-10));
        animator.rotate(headPivot, Maths.rad(-10),  Maths.rad(-10),  Maths.rad(-35));
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.startKeyframe(3);
        animator.rotate(body, Maths.rad(10), 0, 0);
        animator.rotate(leg_left, Maths.rad(-10), 0, 0);
        animator.rotate(leg_right, Maths.rad(-10), 0, 0);
        animator.rotate(neck1, Maths.rad(15), 0, 0);
        animator.rotate(neck2, Maths.rad(0), 0, Maths.rad(10));
        animator.rotate(headPivot, Maths.rad(-10),  Maths.rad(10),  Maths.rad(35));
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.startKeyframe(3);
        animator.rotate(body, Maths.rad(-2), 0, 0);
        animator.rotate(leg_left, Maths.rad(2), 0, 0);
        animator.rotate(leg_right, Maths.rad(2), 0, 0);
        animator.rotate(neck1, Maths.rad(-15), 0, 0);
        animator.rotate(neck2, Maths.rad(15), 0, Maths.rad(-10));
        animator.rotate(headPivot, Maths.rad(-10),  Maths.rad(-10),  Maths.rad(-35));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityEmu.ANIMATION_PECK_GROUND);
        animator.startKeyframe(10);
        animator.move(neck1, 0, -0.2F, -0.2F);
        animator.rotate(neck1,  Maths.rad(145), 0, 0);
        animator.rotate(neck2,  Maths.rad(15), 0, 0);
        animator.rotate(headPivot,  Maths.rad(-60), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck1, 0, -0.2F, -0.2F);
        animator.rotate(neck1,  Maths.rad(135), 0, 0);
        animator.rotate(neck2,  Maths.rad(15), 0, 0);
        animator.rotate(headPivot,  Maths.rad(-60), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck1, 0, -1, -0.2F);
        animator.rotate(neck1,  Maths.rad(145), 0, 0);
        animator.rotate(neck2,  Maths.rad(15), 0, 0);
        animator.rotate(headPivot,  Maths.rad(-60), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityEmu.ANIMATION_SCRATCH);
        animator.startKeyframe(5);
        animator.move(leg_right, 0, -0.5F, 2);
        animator.move(leg_left, 0, -0.5F, 2);
        animator.rotate(body, Maths.rad(-20), 0, 0);
        animator.rotate(leg_left, Maths.rad(20), 0, 0);
        animator.rotate(leg_right, Maths.rad(20), 0, 0);
        animator.rotate(neck1, Maths.rad(10), 0, 0);
        animator.rotate(neck2, Maths.rad(-20), 0, 0);
        animator.rotate(headPivot, Maths.rad(24), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.move(leg_right, 0, -0.5F, 2);
        animator.rotate(body, Maths.rad(-40), 0, Maths.rad(-10));
        animator.rotate(leg_left, Maths.rad(-70), 0, 0);
        animator.rotate(foot_left, Maths.rad(70), 0,  0);
        animator.rotate(leg_right, Maths.rad(40), 0,  Maths.rad(30));
        animator.rotate(neck1, Maths.rad(10), 0, 0);
        animator.rotate(neck2, Maths.rad(-20), 0, 0);
        animator.rotate(headPivot, Maths.rad(24), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(leg_right, 0, -0.5F, 2);
        animator.move(leg_left, 0, -0.5F, 2);
        animator.rotate(body, Maths.rad(-20), 0, 0);
        animator.rotate(leg_left, Maths.rad(20), 0, 0);
        animator.rotate(leg_right, Maths.rad(20), 0, 0);
        animator.rotate(neck1, Maths.rad(10), 0, 0);
        animator.rotate(neck2, Maths.rad(-20), 0, 0);
        animator.rotate(headPivot, Maths.rad(24), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.move(leg_right, 0, -0.5F, 2);
        animator.rotate(body, Maths.rad(-40), 0, Maths.rad(10));
        animator.rotate(leg_right, Maths.rad(-70), 0, 0);
        animator.rotate(foot_right, Maths.rad(70), 0,  0);
        animator.rotate(leg_left, Maths.rad(40), 0,  Maths.rad(30));
        animator.rotate(neck1, Maths.rad(10), 0, 0);
        animator.rotate(neck2, Maths.rad(-20), 0, 0);
        animator.rotate(headPivot, Maths.rad(24), 0, 0);
        animator.endKeyframe();

        animator.resetKeyframe(5);

    }

    @Override
    public void setupAnim(EntityEmu emu, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animate(emu, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.4F;
        float walkDegree = 0.4F;
        float idleSpeed = 0.05F;
        float idleDegree = 0.1F;
        this.walk(neck1, idleSpeed, idleDegree, false, 0F, -0.1F, ageInTicks, 1);
        this.walk(neck2, idleSpeed, idleDegree, true, 1F, 0.15F, ageInTicks, 1);
        this.walk(head, idleSpeed, idleDegree, false, 1F, 0.25F, ageInTicks, 1);
        this.walk(tail, idleSpeed, idleDegree, false, 2F, -0.05F, ageInTicks, 1);
        boolean running = true;
        if (running) {
            this.walk(leg_right, walkSpeed, walkDegree * 2F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(leg_left, walkSpeed, walkDegree * 2F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(foot_right, walkSpeed, walkDegree * 1.5F, false, 2F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(foot_left, walkSpeed, walkDegree * 1.5F, true, 2F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(neck1, walkSpeed, walkDegree * 1F, false, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(neck2, walkSpeed, walkDegree * 0.8F, true, 1.3F, 0F, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, walkDegree * 0.25F, true, 1.3F, 0F, limbSwing, limbSwingAmount);
            this.walk(tail, walkSpeed, walkDegree * 0.4F, true, 1.3F, -0.4F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed, walkDegree * 14F, true, limbSwing, limbSwingAmount);
            this.flap(body, walkSpeed, walkDegree * 0.7F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(leg_left, walkSpeed, walkDegree * 0.7F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(leg_right, walkSpeed, walkDegree * 0.7F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.flap(neck1, walkSpeed, walkDegree * 0.8F, true, 3F, 0F, limbSwing, limbSwingAmount);
            this.flap(neck2, walkSpeed, walkDegree * 0.4F, true, 3.2F, 0F, limbSwing, limbSwingAmount);
            this.swing(tail, walkSpeed, walkDegree * 0.8F, false, 1F, 0F, limbSwing, limbSwingAmount);

        } else {
            this.walk(leg_right, walkSpeed, walkDegree * 1.85F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(leg_left, walkSpeed, walkDegree * 1.85F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(foot_right, walkSpeed, walkDegree * 1.5F, false, 1.95F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(foot_left, walkSpeed, walkDegree * 1.5F, true, 1.95F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(neck1, walkSpeed, walkDegree * 0.6F, false, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(neck2, walkSpeed, walkDegree * 0.5F, true, 1.3F, 0F, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, walkDegree * 0.15F, true, 1.3F, 0F, limbSwing, limbSwingAmount);
            this.walk(tail, walkSpeed, walkDegree * 0.4F, true, 1.3F, -0.4F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed, walkDegree * 5F, true, limbSwing, limbSwingAmount);

        }
        this.faceTarget(netHeadYaw, headPitch, 1F, neck2, head);
        float runProgress = 5F * limbSwingAmount;
        if(emu.getAnimation() != EntityEmu.ANIMATION_PECK_GROUND){
            progressPositionPrev(neck1, runProgress, 0, -3, -1.5F, 5F);
        }
        progressPositionPrev(neck2, runProgress, 0, 0.5F, -1.5F, 5F);
        progressPositionPrev(headPivot, runProgress, 0, 0.5F, -0.5F, 5F);
        progressRotationPrev(neck1, runProgress, Maths.rad(120), 0, 0, 5F);
        progressRotationPrev(neck2, runProgress, Maths.rad(-60), 0, 0, 5F);
        progressRotationPrev(headPivot, runProgress, Maths.rad(-50), 0, 0, 5F);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.8D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leg_left, leg_right, legfur_left, legfur_right, tail, neck1, neck2, headPivot, head, beak, foot_left, foot_right);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}