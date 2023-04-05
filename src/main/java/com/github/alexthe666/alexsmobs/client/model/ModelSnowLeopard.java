package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelSnowLeopard extends AdvancedEntityModel<EntitySnowLeopard> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tail3;
    private final AdvancedModelBox head;
    private final AdvancedModelBox bubble;
    private final AdvancedModelBox whiskersLeft;
    private final AdvancedModelBox whiskersRight;
    private final AdvancedModelBox armLeft;
    private final AdvancedModelBox armRight;
    private final AdvancedModelBox legLeft;
    private final AdvancedModelBox legRight;
    public ModelAnimator animator;

    public ModelSnowLeopard() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -11.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -9.0F, 8.0F, 9.0F, 18.0F, 0.0F, false);

        tail1 = new AdvancedModelBox(this);
        tail1.setRotationPoint(0.0F, -1.0F, 9.0F);
        body.addChild(tail1);
        setRotationAngle(tail1, -0.7418F, 0.0F, 0.0F);
        tail1.setTextureOffset(0, 28).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 3.0F, 13.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, -0.2F, 11.0F);
        tail1.addChild(tail2);
        setRotationAngle(tail2, -1.0472F, 0.0F, 0.0F);
        tail2.setTextureOffset(0, 28).addBox(-1.5F, -8.0F, -2.0F, 3.0F, 9.0F, 3.0F, 0.1F, false);

        tail3 = new AdvancedModelBox(this);
        tail3.setRotationPoint(0.0F, -7.3F, -1.3F);
        tail2.addChild(tail3);
        setRotationAngle(tail3, -0.9599F, 0.0F, 0.0F);
        tail3.setTextureOffset(20, 28).addBox(-1.5F, -2.0F, -8.0F, 3.0F, 3.0F, 9.0F, 0.2F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -1.0F, -9.0F);
        body.addChild(head);
        head.setTextureOffset(35, 0).addBox(-3.5F, -4.0F, -4.0F, 7.0F, 5.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(35, 11).addBox(-2.5F, -2.0F, -6.0F, 5.0F, 3.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(36, 28).addBox(1.5F, -6.0F, -3.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
        head.setTextureOffset(36, 28).addBox(-3.5F, -6.0F, -3.0F, 2.0F, 2.0F, 3.0F, 0.0F, true);

        bubble = new AdvancedModelBox(this);
        bubble.setRotationPoint(0.0F, -2.0F, -6.0F);
        head.addChild(bubble);
        bubble.setTextureOffset(7, 13).addBox(-2.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        whiskersLeft = new AdvancedModelBox(this);
        whiskersLeft.setRotationPoint(2.5F, -0.5F, -5.0F);
        head.addChild(whiskersLeft);
        setRotationAngle(whiskersLeft, 0.0F, -0.5236F, 0.0F);
        whiskersLeft.setTextureOffset(11, 0).addBox(0.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, 0.0F, false);

        whiskersRight = new AdvancedModelBox(this);
        whiskersRight.setRotationPoint(-2.5F, -0.5F, -5.0F);
        head.addChild(whiskersRight);
        setRotationAngle(whiskersRight, 0.0F, 0.5236F, 0.0F);
        whiskersRight.setTextureOffset(11, 0).addBox(-3.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, 0.0F, true);

        armLeft = new AdvancedModelBox(this);
        armLeft.setRotationPoint(2.9F, 4.0F, -6.0F);
        body.addChild(armLeft);
        armLeft.setTextureOffset(0, 0).addBox(-1.4F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, 0.0F, false);

        armRight = new AdvancedModelBox(this);
        armRight.setRotationPoint(-2.9F, 4.0F, -6.0F);
        body.addChild(armRight);
        armRight.setTextureOffset(0, 0).addBox(-1.6F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, 0.0F, true);

        legLeft = new AdvancedModelBox(this);
        legLeft.setRotationPoint(2.9F, 4.0F, 8.0F);
        body.addChild(legLeft);
        legLeft.setTextureOffset(29, 41).addBox(-1.4F, -1.0F, -2.0F, 3.0F, 8.0F, 4.0F, 0.0F, false);

        legRight = new AdvancedModelBox(this);
        legRight.setRotationPoint(-2.9F, 4.0F, 8.0F);
        body.addChild(legRight);
        legRight.setTextureOffset(29, 41).addBox(-1.6F, -1.0F, -2.0F, 3.0F, 8.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = new ModelAnimator();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, bubble, whiskersLeft, whiskersRight, armLeft, armRight, legLeft, legRight, tail1, tail2, tail3);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.update(entity);
        animator.setAnimation(EntitySnowLeopard.ANIMATION_ATTACK_R);
        animator.startKeyframe(3);
        animator.rotate(body, 0, (float) Math.toRadians(-10F), 0);
        animator.rotate(head, 0, (float) Math.toRadians(-10F), (float) Math.toRadians(-10F));
        animator.rotate(armRight, (float) Math.toRadians(25F), (float) Math.toRadians(-20F), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, 0, 0, (float) Math.toRadians(0));
        animator.rotate(armRight, (float) Math.toRadians(-90F), (float) Math.toRadians(-30F), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntitySnowLeopard.ANIMATION_ATTACK_L);
        animator.startKeyframe(3);
        animator.rotate(body, 0, (float) Math.toRadians(10F), 0);
        animator.rotate(head, 0, (float) Math.toRadians(10F), (float) Math.toRadians(10F));
        animator.rotate(armLeft, (float) Math.toRadians(25F), (float) Math.toRadians(20F), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, 0, 0, (float) Math.toRadians(0));
        animator.rotate(armLeft, (float) Math.toRadians(-90F), (float) Math.toRadians(30F), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(EntitySnowLeopard entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float runProgress = 5F * limbSwingAmount;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float stalkProgress = entity.prevSneakProgress + (entity.sneakProgress - entity.prevSneakProgress) * partialTick;
        float tackleProgress = entity.prevTackleProgress + (entity.tackleProgress - entity.prevTackleProgress) * partialTick;
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
        float sleepProgress = entity.prevSleepProgress + (entity.sleepProgress - entity.prevSleepProgress) * partialTick;
        float sitSleepProgress = Math.max(sitProgress, sleepProgress);
        this.swing(tail1, idleSpeed, idleDegree * 2F, false, 2F, 0F, ageInTicks, 1 - limbSwingAmount);
        this.swing(tail2, idleSpeed, idleDegree * 1.5F, false, 2F, 0F, ageInTicks, 1 - limbSwingAmount);
        this.flap(tail3, idleSpeed * 1.2F, idleDegree * 1.5F, false, 2F, 0F, ageInTicks, 1 - limbSwingAmount);
        this.swing(tail3, idleSpeed * 1.2F, idleDegree * 1.5F, false, 2F, 0F, ageInTicks, 1 - limbSwingAmount);
        this.walk(head, idleSpeed * 0.3F, idleDegree, false, 0F, 0F, ageInTicks, 1);
        this.walk(head, idleSpeed * 0.3F, -idleDegree, false, 0.5F, 0F, ageInTicks, 1);
        this.walk(armRight, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(armRight, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(armLeft, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(armLeft, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(legRight, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(legRight, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(legLeft, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(legLeft, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{tail1, tail2, tail3};
        this.chainSwing(tailBoxes, walkSpeed, walkDegree * 0.5F, -2.5F, limbSwing, limbSwingAmount);
        progressRotationPrev(tail1, runProgress, (float) Math.toRadians(40), 0, 0, 5F);
        progressRotationPrev(tail2, runProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(tail3, runProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(body, stalkProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(legLeft, stalkProgress, (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(legRight, stalkProgress, (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(armLeft, stalkProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(armRight, stalkProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(head, stalkProgress, (float) Math.toRadians(5), 0, 0, 5F);
        progressRotationPrev(head, stalkProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(tail1, stalkProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressPositionPrev(body, stalkProgress, 0, -0.5F, 4, 5F);
        progressPositionPrev(legLeft, stalkProgress, 0, 1.6F, -2, 5F);
        progressPositionPrev(legRight, stalkProgress, 0, 1.6F, -2, 5F);
        progressPositionPrev(armLeft, stalkProgress, 0, -0.4F, 0, 5F);
        progressPositionPrev(armRight, stalkProgress, 0, -0.4F, 0, 5F);
        progressRotationPrev(body, tackleProgress, (float) Math.toRadians(-45), 0, 0, 3F);
        progressRotationPrev(head, tackleProgress, (float) Math.toRadians(45), 0, 0, 3F);
        progressRotationPrev(tail1, tackleProgress, (float) Math.toRadians(60), 0, 0, 3F);
        progressRotationPrev(armRight, tackleProgress, (float) Math.toRadians(-25), 0, (float) Math.toRadians(45), 3F);
        progressRotationPrev(armLeft, tackleProgress, (float) Math.toRadians(-25), 0, (float) Math.toRadians(-45), 3F);
        progressRotationPrev(legLeft, tackleProgress, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-25), 3F);
        progressRotationPrev(legRight, tackleProgress, (float) Math.toRadians(-15), 0, (float) Math.toRadians(25), 3F);
        progressPositionPrev(body, tackleProgress, 0, -5F, 0, 3F);
        progressPositionPrev(head, tackleProgress, 0, 2, 0, 3F);
        progressPositionPrev(armLeft, tackleProgress, 1F, 2F, 0, 3F);
        progressPositionPrev(armRight, tackleProgress, -1F, 2F, 0, 3F);
        progressPositionPrev(tail1, tackleProgress, 0, 0F, -1F, 3F);
        float tailAngle = entity.getId() % 2 == 0 ? 1 : -1;
        progressRotationPrev(legLeft, sitSleepProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-20), 0, 5F);
        progressRotationPrev(legRight, sitSleepProgress, (float) Math.toRadians(-90), (float) Math.toRadians(20), 0, 5F);
        progressRotationPrev(armLeft, sitSleepProgress, (float) Math.toRadians(-90), 0, 0, 5F);
        progressRotationPrev(armRight, sitSleepProgress, (float) Math.toRadians(-90), 0, 0, 5F);
        progressPositionPrev(body, sitSleepProgress, 0, 3, 0, 5F);
        progressPositionPrev(armRight, sitSleepProgress, 0, 2F, 0, 5F);
        progressPositionPrev(armLeft, sitSleepProgress, 0, 2F, 0, 5F);
        progressPositionPrev(legRight, sitSleepProgress, 0, 2.8F, -0.5F, 5F);
        progressPositionPrev(legLeft, sitSleepProgress, 0, 2.8F, -0.5F, 5F);
        progressRotationPrev(tail1, sitProgress, (float) Math.toRadians(20), (float) Math.toRadians(tailAngle * 30), 0, 5F);
        progressRotationPrev(tail2, sitProgress, (float) Math.toRadians(-5), (float) Math.toRadians(tailAngle * 50), 0, 5F);
        progressRotationPrev(tail3, sitProgress, (float) Math.toRadians(10), (float) Math.toRadians(tailAngle * 20), (float) Math.toRadians(tailAngle * 20), 5F);
        progressRotationPrev(tail1, sleepProgress, (float) Math.toRadians(20), (float) Math.toRadians(tailAngle * -60), 0, 5F);
        progressRotationPrev(tail2, sleepProgress, (float) Math.toRadians(10), (float) Math.toRadians(tailAngle * -70), (float) Math.toRadians(tailAngle * -50), 5F);
        progressRotationPrev(tail3, sleepProgress, (float) Math.toRadians(-30), (float) Math.toRadians(tailAngle * -50), (float) Math.toRadians(tailAngle * -30), 5F);
        progressRotationPrev(body, sleepProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(armLeft, sleepProgress,  (float) Math.toRadians(-10), (float) Math.toRadians(-30), 0, 5F);
        progressRotationPrev(armRight, sleepProgress,  (float) Math.toRadians(-10), (float) Math.toRadians(30), 0, 5F);
        progressRotationPrev(legRight, sleepProgress,  (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(legRight, sleepProgress,  (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(tail1, sleepProgress,  (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(head, sleepProgress,  (float) Math.toRadians(-10), 0, (float) Math.toRadians(-10), 5F);
        progressPositionPrev(head, sleepProgress, 0, 5, -1, 5F);
        if(sleepProgress >= 5F){
            float f = (float) (sleepProgress * Math.max(Math.sin(ageInTicks * 0.05F), 0F) * 0.2F);
            this.bubble.showModel = true;
            this.bubble.setScale(f, f, f);
        }else{
            this.bubble.showModel = false;
        }
        if (sleepProgress <= 0.0F) {
            this.faceTarget(netHeadYaw, headPitch, 1, head);
        }
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.45F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
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

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}