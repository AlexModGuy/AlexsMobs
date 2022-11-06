package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelMoose extends AdvancedEntityModel<EntityMoose> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox right_arm;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox right_leg;
    private final AdvancedModelBox upper_body;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox left_ear;
    private final AdvancedModelBox right_ear;
    private final AdvancedModelBox beard;
    private final ModelAnimator animator;

    public ModelMoose() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -28.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(37, 80).addBox(-6.0F, -8.0F, -4.0F, 12.0F, 15.0F, 20.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setPos(4.7F, 6.0F, -13.0F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(19, 58).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 21.0F, 4.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setPos(-4.7F, 6.0F, -13.0F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(19, 58).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 21.0F, 4.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setPos(3.7F, 6.0F, 14.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(0, 58).addBox(-2.0F, 1.0F, -3.0F, 4.0F, 21.0F, 5.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setPos(-3.7F, 6.0F, 14.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(0, 58).addBox(-2.0F, 1.0F, -3.0F, 4.0F, 21.0F, 5.0F, 0.0F, true);

        upper_body = new AdvancedModelBox(this, "upper_body");
        upper_body.setPos(0.0F, -1.0F, -4.0F);
        body.addChild(upper_body);
        upper_body.setTextureOffset(52, 45).addBox(-7.0F, -10.0F, -13.0F, 14.0F, 18.0F, 13.0F, 0.0F, false);

        neck = new AdvancedModelBox(this, "neck");
        neck.setPos(0.0F, -6.0F, -14.0F);
        upper_body.addChild(neck);
        neck.setTextureOffset(45, 0).addBox(-4.0F, -3.0F, -6.0F, 8.0F, 9.0F, 7.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, 0.0F, -7.0F);
        neck.addChild(head);
        head.setTextureOffset(51, 18).addBox(-3.0F, -3.0F, -15.0F, 6.0F, 7.0F, 16.0F, 0.0F, false);
        head.setTextureOffset(0, 34).addBox(3.0F, -12.0F, -7.0F, 18.0F, 9.0F, 14.0F, 0.0F, false);
        head.setTextureOffset(0, 34).addBox(-21.0F, -12.0F, -7.0F, 18.0F, 9.0F, 14.0F, 0.0F, true);

        left_ear = new AdvancedModelBox(this, "left_ear");
        left_ear.setPos(1.3F, -3.0F, 0.5F);
        head.addChild(left_ear);
        setRotationAngle(left_ear, -0.3054F, -0.2618F, 0.3927F);
        left_ear.setTextureOffset(11, 0).addBox(-0.3F, -4.0F, -0.5F, 2.0F, 4.0F, 1.0F, 0.0F, false);

        right_ear = new AdvancedModelBox(this, "right_ear");
        right_ear.setPos(-1.3F, -3.0F, 0.5F);
        head.addChild(right_ear);
        setRotationAngle(right_ear, -0.3054F, 0.2618F, -0.3927F);
        right_ear.setTextureOffset(11, 0).addBox(-1.7F, -4.0F, -0.5F, 2.0F, 4.0F, 1.0F, 0.0F, true);

        beard = new AdvancedModelBox(this, "beard");
        beard.setPos(0.0F, 4.0F, 0.0F);
        head.addChild(beard);
        beard.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -4.0F, 0.0F, 6.0F, 5.0F, 0.0F, false);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, left_ear, right_ear, head, neck, body, upper_body, beard, left_leg, right_leg, left_arm, right_arm);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityMoose.ANIMATION_EAT_GRASS);
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(50), 0, 0);
        animator.rotate(head, (float) Math.toRadians(4), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float) Math.toRadians(70), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float) Math.toRadians(50), 0, 0);
        animator.rotate(head, (float) Math.toRadians(0), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float) Math.toRadians(70), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float) Math.toRadians(50), 0, 0);
        animator.rotate(head, (float) Math.toRadians(0), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float) Math.toRadians(70), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityMoose.ANIMATION_ATTACK);
        animator.startKeyframe(8);
        eatPose();
        animator.rotate(neck, (float) Math.toRadians(50), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(neck, (float) Math.toRadians(-34), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
    }

    private void eatPose() {
        animator.rotate(body, (float) Math.toRadians(10), 0, 0);
        animator.move(body, 0, 2, 0);
        animator.rotate(left_leg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(right_leg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(left_arm, (float) Math.toRadians(-10), 0, (float) Math.toRadians(-10));
        animator.rotate(right_arm, (float) Math.toRadians(-10), 0, (float) Math.toRadians(10));
        animator.move(left_arm, 0.1F, -3, 0F);
        animator.move(right_arm, -0.1F, -3, 0F);
        animator.move(left_leg, 0, -0.2F, 0);
        animator.move(right_leg, 0, -0.2F, 0);
        animator.move(neck, 0, 1, 0);
    }


    @Override
    public void setupAnim(EntityMoose entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float runProgress = 5F * limbSwingAmount;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float jostleProgress = entityIn.prevJostleProgress + (entityIn.jostleProgress - entityIn.prevJostleProgress) * partialTick;
        float jostleAngle = entityIn.prevJostleAngle + (entityIn.getJostleAngle() - entityIn.prevJostleAngle) * partialTick;
        //this.walk(tail, idleSpeed, idleDegree * 2, false, 1F, 0.1F, ageInTicks, 1);
        this.flap(beard, idleSpeed, idleDegree * 4, false, 0F, 0F, ageInTicks, 1);
        this.flap(left_ear, idleSpeed, idleDegree, false, 1F, -0.2F, ageInTicks, 1);
        this.flap(right_ear, idleSpeed, idleDegree, true, 1F, 0.2F, ageInTicks, 1);
        this.walk(neck, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1);
        this.walk(head, idleSpeed, -idleDegree, false, 0.5F, 0F, ageInTicks, 1);
        this.walk(body, walkSpeed, walkDegree * 0.05F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(neck, walkSpeed, walkDegree * 0.25F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(head, walkSpeed, -walkDegree * 0.25F, true, 1F, 0F, limbSwing, limbSwingAmount);
        //this.walk(tail, walkSpeed, walkDegree * 0.5F, true, 1F, -0.4F, limbSwing, limbSwingAmount);
        this.walk(right_arm, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(right_arm, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(left_arm, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(left_arm, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(right_leg, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(right_leg, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(left_leg, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(left_leg, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        //progressRotationPrev(tail, runProgress, (float) Math.toRadians(23), 0, 0, 5F);
        progressRotationPrev(neck, jostleProgress, (float) Math.toRadians(7), 0, 0, 5F);
        progressRotationPrev(head, jostleProgress, (float) Math.toRadians(80), 0, 0, 5F);
        progressPositionPrev(neck, jostleProgress, 0, 0, 1, 5F);
        progressPositionPrev(head, jostleProgress, 0, 0, -1, 5F);
        if (jostleProgress > 0) {
            float yawAmount = jostleAngle / 57.295776F * 0.5F * jostleProgress * 0.2F;
            neck.rotateAngleY += yawAmount;
            head.rotateAngleY += yawAmount;
            head.rotateAngleZ += yawAmount;
        } else {
            this.faceTarget(netHeadYaw, headPitch, 2, neck, head);
        }
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.35F;
            float feet = 1.45F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            right_arm.setScale(1, feet, 1);
            left_arm.setScale(1, feet, 1);
            right_leg.setScale(1, feet, 1);
            left_leg.setScale(1, feet, 1);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.25D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
            right_arm.setScale(1, 1, 1);
            left_arm.setScale(1, 1, 1);
            right_leg.setScale(1, 1, 1);
            left_leg.setScale(1, 1, 1);
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

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}