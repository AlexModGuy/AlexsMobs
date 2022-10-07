package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelElephant extends AdvancedEntityModel<EntityElephant> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox cabin;
    public final AdvancedModelBox left_chest;
    public final AdvancedModelBox right_chest;
    public final AdvancedModelBox tail;
    public final AdvancedModelBox left_arm;
    public final AdvancedModelBox right_arm;
    public final AdvancedModelBox left_leg;
    public final AdvancedModelBox right_leg;
    public final AdvancedModelBox head;
    public final AdvancedModelBox left_tusk;
    public final AdvancedModelBox right_tusk;
    public final AdvancedModelBox left_megatusk;
    public final AdvancedModelBox right_megatusk;
    public final AdvancedModelBox left_ear;
    public final AdvancedModelBox right_ear;
    public final AdvancedModelBox trunk1;
    public final AdvancedModelBox trunk2;
    private ModelAnimator animator;

    public ModelElephant(float f) {
        texWidth = 256;
        texHeight = 256;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -41.0F, 1.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-14.0F, -15.0F, -24.0F, 28.0F, 29.0F, 47.0F, f, false);

        cabin = new AdvancedModelBox(this, "cabin");
        cabin.setRotationPoint(0.0F, -14.0F, -11.5F);
        body.addChild(cabin);
        cabin.setTextureOffset(0, 165).addBox(-13.0F, -29.0F, -11.5F, 26.0F, 28.0F, 23.0F, f, false);
        cabin.setTextureOffset(109, 176).addBox(-16.0F, -29.1F, -13.5F, 32.0F, 7.0F, 27.0F, f, false);

        left_chest = new AdvancedModelBox(this, "left_chest");
        left_chest.setRotationPoint(14.0F, -8.0F, 10.0F);
        body.addChild(left_chest);
        left_chest.setTextureOffset(57, 125).addBox(0.0F, -2.0F, -10.0F, 9.0F, 13.0F, 19.0F, f, false);

        right_chest = new AdvancedModelBox(this, "right_chest");
        right_chest.setRotationPoint(-14.0F, -8.0F, 10.0F);
        body.addChild(right_chest);
        right_chest.setTextureOffset(57, 125).addBox(-9.0F, -2.0F, -10.0F, 9.0F, 13.0F, 19.0F, f, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -5.0F, 23.0F);
        body.addChild(tail);
        setRotationAngle(tail, 0.1745F, 0.0F, 0.0F);
        tail.setTextureOffset(42, 114).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 20.0F, 0.0F, f, false);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(8.1F, 9.5F, -18.2F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(0, 0).addBox(-5.5F, 4.5F, -5.5F, 11.0F, 27.0F, 11.0F, f, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-8.1F, 9.5F, -18.2F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(0, 0).addBox(-5.5F, 4.5F, -5.5F, 11.0F, 27.0F, 11.0F, f, true);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(8.2F, 11.5F, 17.2F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(71, 77).addBox(-5.5F, 2.5F, -5.5F, 11.0F, 27.0F, 11.0F, f, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-8.2F, 11.5F, 17.2F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(71, 77).addBox(-5.5F, 2.5F, -5.5F, 11.0F, 27.0F, 11.0F, f, true);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -2.0F, -25.0F);
        body.addChild(head);
        head.setTextureOffset(0, 77).addBox(-10.0F, -12.0F, -14.0F, 20.0F, 21.0F, 15.0F, f, false);

        left_tusk = new AdvancedModelBox(this, "left_tusk");
        left_tusk.setRotationPoint(6.5F, 8.0F, -11.5F);
        head.addChild(left_tusk);
        setRotationAngle(left_tusk, -0.4887F, -0.1571F, -0.2967F);
        left_tusk.setTextureOffset(104, 25).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, f, false);

        right_tusk = new AdvancedModelBox(this, "right_tusk");
        right_tusk.setRotationPoint(-6.5F, 8.0F, -11.5F);
        head.addChild(right_tusk);
        setRotationAngle(right_tusk, -0.4887F, 0.1571F, 0.2967F);
        right_tusk.setTextureOffset(104, 25).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, f, true);

        left_megatusk = new AdvancedModelBox(this, "left_megatusk");
        left_megatusk.setRotationPoint(6.5F, 8.0F, -11.5F);
        head.addChild(left_megatusk);
        setRotationAngle(left_megatusk, -0.2618F, 0.0524F, -0.2269F);
        left_megatusk.setTextureOffset(0, 114).addBox(-1.5F, 0.0F, -2.5F, 4.0F, 28.0F, 4.0F, f, false);
        left_megatusk.setTextureOffset(104, 25).addBox(-1.5F, 24.0F, -19.5F, 4.0F, 4.0F, 17.0F, f, false);

        right_megatusk = new AdvancedModelBox(this, "right_megatusk");
        right_megatusk.setRotationPoint(-6.5F, 8.0F, -11.5F);
        head.addChild(right_megatusk);
        setRotationAngle(right_megatusk, -0.2618F, -0.0524F, 0.2269F);
        right_megatusk.setTextureOffset(0, 114).addBox(-2.5F, 0.0F, -2.5F, 4.0F, 28.0F, 4.0F, f, true);
        right_megatusk.setTextureOffset(104, 25).addBox(-2.5F, 24.0F, -19.5F, 4.0F, 4.0F, 17.0F, f, true);

        left_ear = new AdvancedModelBox(this, "left_ear");
        left_ear.setRotationPoint(9.0F, -3.0F, -3.0F);
        head.addChild(left_ear);
        setRotationAngle(left_ear, 0.0F, -0.6981F, 0.0F);
        left_ear.setTextureOffset(104, 0).addBox(0.0F, -4.0F, -1.0F, 20.0F, 22.0F, 2.0F, f, false);

        right_ear = new AdvancedModelBox(this, "right_ear");
        right_ear.setRotationPoint(-9.0F, -3.0F, -3.0F);
        head.addChild(right_ear);
        setRotationAngle(right_ear, 0.0F, 0.6981F, 0.0F);
        right_ear.setTextureOffset(104, 0).addBox(-20.0F, -4.0F, -1.0F, 20.0F, 22.0F, 2.0F, f, true);

        trunk1 = new AdvancedModelBox(this, "trunk1");
        trunk1.setRotationPoint(0.0F, 3.0F, -16.0F);
        head.addChild(trunk1);
        trunk1.setTextureOffset(108, 108).addBox(-4.0F, -4.0F, -5.0F, 8.0F, 24.0F, 8.0F, f, false);

        trunk2 = new AdvancedModelBox(this, "trunk2");
        trunk2.setRotationPoint(0.0F, 20.0F, 0.0F);
        trunk1.addChild(trunk2);
        trunk2.setTextureOffset(17, 114).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, f, false);        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityElephant.ANIMATION_TRUMPET_0);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, 0);
        animator.rotate(left_ear, 0, (float)Math.toRadians(25), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-65), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(-35), 0, 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-35), 0, 0);
        animator.rotate(left_ear, 0, (float)Math.toRadians(45), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-45), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(-55), 0, 0);
        animator.move(trunk2, 0,  -2, 1);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(7);
        animator.setAnimation(EntityElephant.ANIMATION_TRUMPET_1);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(-25));
        animator.rotate(left_ear, 0, (float)Math.toRadians(25), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75), (float)Math.toRadians(25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-35), (float)Math.toRadians(10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(25));
        animator.rotate(left_ear, 0, (float)Math.toRadians(25), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75),  (float)Math.toRadians(-25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-35), (float)Math.toRadians(-10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(-25));
        animator.rotate(left_ear, 0, (float)Math.toRadians(25), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75), (float)Math.toRadians(25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-35), (float)Math.toRadians(10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(25));
        animator.rotate(left_ear, 0, (float)Math.toRadians(25), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75),  (float)Math.toRadians(-25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-35), (float)Math.toRadians(-10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(EntityElephant.ANIMATION_CHARGE_PREPARE);
        animator.startKeyframe(10);
        animator.rotate(body, (float)Math.toRadians(15), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-30), 0, 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-20), 0);
        animator.rotate(left_ear, 0, (float)Math.toRadians(20), 0);
        animator.rotate(left_arm, (float)Math.toRadians(-15), 0, (float)Math.toRadians(-15));
        animator.rotate(right_arm, (float)Math.toRadians(-15), 0, (float)Math.toRadians(15));
        animator.rotate(left_leg, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(right_leg, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(trunk1, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(45), 0, 0);
        animator.move(right_arm, 0,  -9, 0);
        animator.move(left_arm, 0,  -9, 0);
        animator.move(left_leg, 0,  -1, 0);
        animator.move(right_leg, 0,  -1, 0);
        animator.move(head, 0,  2, 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.move(body, 0,  6, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityElephant.ANIMATION_STOMP);
        animator.startKeyframe(10);
        animator.rotate(body, (float)Math.toRadians(-35), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(left_leg, (float)Math.toRadians(35), 0, 0);
        animator.rotate(right_leg, (float)Math.toRadians(35), 0, 0);
        animator.rotate(left_arm, (float)Math.toRadians(35), (float)Math.toRadians(-15), 0);
        animator.rotate(right_arm, (float)Math.toRadians(35), (float)Math.toRadians(15), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(45), 0, 0);
        animator.rotate(tail, (float)Math.toRadians(45), 0, 0);
        animator.move(body, 0,  -6, 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.move(left_leg, 0,  -1, 0);
        animator.move(right_leg, 0,  -1, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(7);
        animator.resetKeyframe(3);
        animator.setAnimation(EntityElephant.ANIMATION_FLING);
        animator.startKeyframe(10);
        animator.rotate(head, (float)Math.toRadians(15), 0, 0);
        animator.rotate(left_ear, 0, (float)Math.toRadians(25), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(10), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(15), 0, 0);
        animator.move(head, 0,  3, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(head, 0,  -2, -1);
        animator.rotate(head, (float)Math.toRadians(-45), 0, 0);
        animator.rotate(left_ear, 0, (float)Math.toRadians(25), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-55), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(-55), 0, 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(8);
        animator.setAnimation(EntityElephant.ANIMATION_EAT);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(trunk1, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(-45), 0, 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(8);
        animator.rotate(head, (float)Math.toRadians(10), 0, 0);
        animator.rotate(trunk1, (float)Math.toRadians(-45), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(120), 0, 0);
        animator.move(trunk1, 0,  -0, -1);
        animator.move(trunk2, 0,  -2F, 1F);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(9);

        animator.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-5), 0, 0);
        animator.rotate(left_ear, 0, (float)Math.toRadians(5), 0);
        animator.rotate(right_ear, 0, (float)Math.toRadians(-5), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-40), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(-60), 0, 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            float f2 = 0.75F;
            head.rotationPointY = -10;
            head.setScale(f, f, f);
            tail.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            trunk1.setScale(f2, f2, f2);
            trunk1.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.8D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
            tail.setScale(1, 1, 1);
            trunk1.setScale(1, 1, 1);
        } else {
            head.rotationPointY = -2.0F;
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }

    @Override
    public void setupAnim(EntityElephant entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.4F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.2F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float sitProgress = entityIn.prevSitProgress + (entityIn.sitProgress - entityIn.prevSitProgress) * partialTick;
        float standProgress = entityIn.prevStandProgress + (entityIn.standProgress - entityIn.prevStandProgress) * partialTick;
        progressRotationPrev(body, standProgress, (float)Math.toRadians(-60),  0, 0, 5F);
        progressRotationPrev(tail, standProgress, (float)Math.toRadians(60),  0, 0, 5F);
        progressRotationPrev(right_arm, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(10), 0, 5F);
        progressRotationPrev(left_arm, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(-10), 0, 5F);
        progressRotationPrev(right_leg, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(-15), 0, 5F);
        progressRotationPrev(left_leg, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(15), 0, 5F);
        progressPositionPrev(body, standProgress, 0, -9, 0, 5F);
        progressPositionPrev(right_arm, standProgress, 0, 0, 2, 5F);
        progressPositionPrev(left_arm, standProgress, 0, 0, 2, 5F);

        progressRotationPrev(tail, limbSwingAmount, (float)Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(right_arm, sitProgress, (float)Math.toRadians(-90),  (float)Math.toRadians(10), 0, 5F);
        progressRotationPrev(left_arm, sitProgress, (float)Math.toRadians(-90),  (float)Math.toRadians(-10), 0, 5F);
        progressRotationPrev(right_leg, sitProgress, (float)Math.toRadians(90),  (float)Math.toRadians(5), 0, 5F);
        progressRotationPrev(left_leg, sitProgress, (float)Math.toRadians(90),  (float)Math.toRadians(-5), 0, 5F);
        progressPositionPrev(body, sitProgress, 0, 14, 0, 5F);
        progressPositionPrev(right_arm, sitProgress, 0, 5, 5, 5F);
        progressPositionPrev(left_arm, sitProgress, 0, 5, 5, 5F);
        progressPositionPrev(right_leg, sitProgress, 0, 5, -3, 5F);
        progressPositionPrev(left_leg, sitProgress, 0, 5, -3, 5F);
        progressPositionPrev(head, sitProgress, 0, -3, 0, 5F);
        progressPositionPrev(trunk1, sitProgress, 0, -2, 0, 5F);
        progressPositionPrev(trunk2, sitProgress, 0, 1, 0, 5F);
        progressRotationPrev(trunk1, sitProgress,(float)Math.toRadians(-45),  0, 0, 5F);
        progressRotationPrev(trunk2, sitProgress,(float)Math.toRadians(60),  0, 0, 5F);
        progressRotationPrev(tail, sitProgress,(float)Math.toRadians(50),  0, 0, 5F);
        this.swing(right_ear, idleSpeed, idleDegree, false, 1F, -0.1F, ageInTicks, 1);
        this.swing(left_ear, idleSpeed, idleDegree, true, 1F, -0.1F, ageInTicks, 1);
        this.walk(head, idleSpeed * 0.7F, idleDegree * 0.1F, false, -1F, 0.05F, ageInTicks, 1);
        this.walk(trunk1, idleSpeed * 0.4F, idleDegree * 0.7F, false, 0F, 0.1F, ageInTicks, 1);
        this.flap(trunk1, idleSpeed * 0.4F, idleDegree * 0.7F, false, 2F, 0F, ageInTicks, 1);
        this.walk(trunk2, idleSpeed * 0.4F, idleDegree * 0.35F, false, 0F, 0.05F, ageInTicks, 1);
        this.flap(tail, idleSpeed, idleDegree * 0.7F, false, -1, 0F, ageInTicks, 1);
        this.walk(right_arm, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_arm, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_leg, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_leg, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(head, walkSpeed, walkDegree * 0.1F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
        this.walk(trunk1, walkSpeed, walkDegree * 0.1F, true, 0F, -0.1F, limbSwing, limbSwingAmount);
        this.walk(trunk2, walkSpeed, walkDegree * 0.1F, true, 0F, -0.3F, limbSwing, limbSwingAmount);
        this.swing(right_ear, walkSpeed, walkDegree * 0.34F, false, 1F, -0.01F, limbSwing, limbSwingAmount);
        this.swing(left_ear, walkSpeed, walkDegree * 0.34F, true, 1F, -0.01F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed * 2F, walkDegree * 2F, false, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 2, head);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, head, trunk1, trunk2, tail, left_ear, right_ear, left_leg, right_leg, left_arm, right_arm, cabin, left_chest, right_chest, left_tusk, right_tusk, left_megatusk, right_megatusk);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}