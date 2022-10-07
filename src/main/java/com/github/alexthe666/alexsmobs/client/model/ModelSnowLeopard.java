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
import net.minecraft.client.model.geom.ModelPart;

public class ModelSnowLeopard extends AdvancedEntityModel<EntitySnowLeopard> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tail3;
    private final AdvancedModelBox leg_front_left;
    private final AdvancedModelBox leg_front_right;
    private final AdvancedModelBox leg_back_left;
    private final AdvancedModelBox leg_back_right;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox whisker_left;
    private final AdvancedModelBox whisker_right;
    private final AdvancedModelBox ear_left;
    private final AdvancedModelBox ear_right;
    private final AdvancedModelBox snout;
    private ModelAnimator animator;

    public ModelSnowLeopard() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -11.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 32).addBox(-4.5F, 2.0F, -11.0F, 9.0F, 2.0F, 22.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-4.5F, -7.0F, -11.0F, 9.0F, 9.0F, 22.0F, 0.0F, false);

        tail1 = new AdvancedModelBox(this, "tail1");
        tail1.setPos(0.0F, -6.5F, 11.0F);
        body.addChild(tail1);
        setRotationAngle(tail1, -0.9599F, 0.0F, 0.0F);
        tail1.setTextureOffset(41, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 17.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this, "tail2");
        tail2.setPos(0.0F, 2.9F, 17.0F);
        tail1.addChild(tail2);
        setRotationAngle(tail2, 0.7854F, 0.0F, 0.0F);
        tail2.setTextureOffset(52, 52).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 11.0F, 0.1F, false);

        tail3 = new AdvancedModelBox(this, "tail3");
        tail3.setPos(0.0F, -0.2F, 11.1F);
        tail2.addChild(tail3);
        setRotationAngle(tail3, 0.6545F, 0.0F, 0.0F);
        tail3.setTextureOffset(41, 32).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 11.0F, 0.2F, false);

        leg_front_left = new AdvancedModelBox(this, "leg_front_left");
        leg_front_left.setPos(3.0F, 1.0F, -8.0F);
        body.addChild(leg_front_left);
        leg_front_left.setTextureOffset(0, 32).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 13.0F, 4.0F, 0.0F, false);

        leg_front_right = new AdvancedModelBox(this, "leg_front_right");
        leg_front_right.setPos(-3.0F, 1.0F, -8.0F);
        body.addChild(leg_front_right);
        leg_front_right.setTextureOffset(0, 32).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 13.0F, 4.0F, 0.0F, true);

        leg_back_left = new AdvancedModelBox(this, "leg_back_left");
        leg_back_left.setPos(3.0F, 0.0F, 8.0F);
        body.addChild(leg_back_left);
        leg_back_left.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, false);

        leg_back_right = new AdvancedModelBox(this, "leg_back_right");
        leg_back_right.setPos(-3.0F, 0.0F, 8.0F);
        body.addChild(leg_back_right);
        leg_back_right.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, true);

        neck = new AdvancedModelBox(this, "neck");
        neck.setPos(0.0F, -4.0F, -11.0F);
        body.addChild(neck);
        setRotationAngle(neck, -0.3054F, 0.0F, 0.0F);
        neck.setTextureOffset(27, 57).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 7.0F, 5.0F, 0.1F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, 0.1F, -3.1F);
        neck.addChild(head);
        setRotationAngle(head, 0.3054F, 0.0F, 0.0F);
        head.setTextureOffset(0, 57).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 7.0F, 0.0F, false);

        whisker_left = new AdvancedModelBox(this, "whisker_left");
        whisker_left.setPos(3.0F, 2.0F, -4.0F);
        head.addChild(whisker_left);
        setRotationAngle(whisker_left, 0.0F, -0.8727F, 0.0F);
        whisker_left.setTextureOffset(17, 17).addBox(0.0F, -3.0F, 0.0F, 2.0F, 4.0F, 0.0F, 0.0F, false);

        whisker_right = new AdvancedModelBox(this, "whisker_right");
        whisker_right.setPos(-3.0F, 2.0F, -4.0F);
        head.addChild(whisker_right);
        setRotationAngle(whisker_right, 0.0F, 0.8727F, 0.0F);
        whisker_right.setTextureOffset(17, 17).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 4.0F, 0.0F, 0.0F, true);

        ear_left = new AdvancedModelBox(this, "ear_left");
        ear_left.setPos(3.0F, -3.0F, -2.0F);
        head.addChild(ear_left);
        ear_left.setTextureOffset(41, 7).addBox(-1.0F, -2.0F, -2.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

        ear_right = new AdvancedModelBox(this, "ear_right");
        ear_right.setPos(-3.0F, -3.0F, -2.0F);
        head.addChild(ear_right);
        ear_right.setTextureOffset(41, 7).addBox(0.0F, -2.0F, -2.0F, 1.0F, 2.0F, 3.0F, 0.0F, true);

        snout = new AdvancedModelBox(this, "snout");
        snout.setPos(0.0F, 0.1F, -6.4F);
        head.addChild(snout);
        setRotationAngle(snout, 0.1745F, 0.0F, 0.0F);
        snout.setTextureOffset(41, 0).addBox(-2.0F, 0.0F, -2.2F, 4.0F, 3.0F, 3.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = new ModelAnimator();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(whisker_left, whisker_right, snout, root, body, neck, head, ear_left, ear_right, leg_back_left, leg_back_right, leg_front_left, leg_front_right, tail1, tail2, tail3);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.update(entity);
        animator.setAnimation(EntitySnowLeopard.ANIMATION_ATTACK_R);
        animator.startKeyframe(3);
        animator.rotate(body, 0, (float) Math.toRadians(-10F), 0);
        animator.rotate(neck, 0, (float) Math.toRadians(-10F), (float) Math.toRadians(-10F));
        animator.rotate(leg_front_right, (float) Math.toRadians(25F), (float) Math.toRadians(-20F), 0);
        animator.move(leg_front_right, 0, 1, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, 0, 0, (float) Math.toRadians(0));
        animator.rotate(leg_front_right, (float) Math.toRadians(-90F), (float) Math.toRadians(-30F), 0);
        animator.move(leg_front_right, 0, 1, -2);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntitySnowLeopard.ANIMATION_ATTACK_L);
        animator.startKeyframe(3);
        animator.rotate(body, 0, (float) Math.toRadians(10F), 0);
        animator.rotate(neck, 0, (float) Math.toRadians(10F), (float) Math.toRadians(10F));
        animator.rotate(leg_front_left, (float) Math.toRadians(25F), (float) Math.toRadians(20F), 0);
        animator.move(leg_front_left, 0, 1, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, 0, 0, (float) Math.toRadians(0));
        animator.rotate(leg_front_left, (float) Math.toRadians(-90F), (float) Math.toRadians(30F), 0);
        animator.move(leg_front_left, 0, 1, -2);
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
        this.walk(neck, idleSpeed * 0.3F, idleDegree, false, 0F, 0F, ageInTicks, 1);
        this.walk(head, idleSpeed * 0.3F, -idleDegree, false, 0.5F, 0F, ageInTicks, 1);
        this.walk(leg_front_right, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(leg_front_right, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(leg_front_left, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(leg_front_left, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(leg_back_right, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(leg_back_right, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(leg_back_left, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(leg_back_left, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{tail1, tail2, tail3};
        this.chainSwing(tailBoxes, walkSpeed, walkDegree * 0.5F, -2.5F, limbSwing, limbSwingAmount);
        progressRotationPrev(tail1, runProgress, (float) Math.toRadians(40), 0, 0, 5F);
        progressRotationPrev(tail2, runProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(tail3, runProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(body, stalkProgress, (float) Math.toRadians(15), 0, 0, 5F);
        progressRotationPrev(leg_back_left, stalkProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(leg_back_right, stalkProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(leg_front_left, stalkProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(leg_front_right, stalkProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(neck, stalkProgress, (float) Math.toRadians(5), 0, 0, 5F);
        progressRotationPrev(head, stalkProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(tail1, stalkProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressPositionPrev(leg_back_left, stalkProgress, 0, 2.1F, 0, 5F);
        progressPositionPrev(leg_back_right, stalkProgress, 0, 2.1F, 0, 5F);
        progressPositionPrev(leg_front_left, stalkProgress, 0, -1.9F, 0, 5F);
        progressPositionPrev(leg_front_right, stalkProgress, 0, -1.9F, 0, 5F);
        progressRotationPrev(body, tackleProgress, (float) Math.toRadians(-45), 0, 0, 3F);
        progressRotationPrev(neck, tackleProgress, (float) Math.toRadians(6), 0, 0, 3F);
        progressRotationPrev(head, tackleProgress, (float) Math.toRadians(45), 0, 0, 3F);
        progressRotationPrev(tail1, tackleProgress, (float) Math.toRadians(80), 0, 0, 3F);
        progressRotationPrev(leg_front_right, tackleProgress, (float) Math.toRadians(-25), 0, (float) Math.toRadians(45), 3F);
        progressRotationPrev(leg_front_left, tackleProgress, (float) Math.toRadians(-25), 0, (float) Math.toRadians(-45), 3F);
        progressRotationPrev(leg_back_left, tackleProgress, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-25), 3F);
        progressRotationPrev(leg_back_right, tackleProgress, (float) Math.toRadians(-15), 0, (float) Math.toRadians(25), 3F);
        progressPositionPrev(body, tackleProgress, 0, -5F, 0, 3F);
        progressPositionPrev(leg_front_left, tackleProgress, 1F, 2F, 0, 3F);
        progressPositionPrev(leg_front_right, tackleProgress, -1F, 2F, 0, 3F);
        progressPositionPrev(tail1, tackleProgress, 0, 0F, -1F, 3F);
        float tailAngle = entity.getId() % 2 == 0 ? 1 : -1;
        progressRotationPrev(leg_back_left, sitSleepProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-20), 0, 5F);
        progressRotationPrev(leg_back_right, sitSleepProgress, (float) Math.toRadians(-90), (float) Math.toRadians(20), 0, 5F);
        progressRotationPrev(leg_front_left, sitSleepProgress, (float) Math.toRadians(-90), 0, 0, 5F);
        progressRotationPrev(leg_front_right, sitSleepProgress, (float) Math.toRadians(-90), 0, 0, 5F);
        progressPositionPrev(body, sitSleepProgress, 0, 5F, 0, 5F);
        progressPositionPrev(leg_front_right, sitSleepProgress, 0, 2F, 0, 5F);
        progressPositionPrev(leg_front_left, sitSleepProgress, 0, 2F, 0, 5F);
        progressPositionPrev(leg_back_right, sitSleepProgress, 0, 2.8F, -0.5F, 5F);
        progressPositionPrev(leg_back_left, sitSleepProgress, 0, 2.8F, -0.5F, 5F);
        progressRotationPrev(tail1, sitProgress, (float) Math.toRadians(20), (float) Math.toRadians(tailAngle * 30), 0, 5F);
        progressRotationPrev(tail2, sitProgress, (float) Math.toRadians(-5), (float) Math.toRadians(tailAngle * 50), 0, 5F);
        progressRotationPrev(tail3, sitProgress, (float) Math.toRadians(10), (float) Math.toRadians(tailAngle * 20), (float) Math.toRadians(tailAngle * 20), 5F);
        progressRotationPrev(neck, sleepProgress, (float) Math.toRadians(20), tailAngle * (float) Math.toRadians(50), 0, 5F);
        progressRotationPrev(head, sleepProgress, (float) Math.toRadians(5), tailAngle * (float) Math.toRadians(20), 0, 5F);
        progressPositionPrev(head, sleepProgress, tailAngle * 0.5F, -1, 1, 5F);
        progressPositionPrev(neck, sleepProgress, 0, 1, -1, 5F);
        progressRotationPrev(tail1, sleepProgress, (float) Math.toRadians(20), (float) Math.toRadians(tailAngle * -60), 0, 5F);
        progressRotationPrev(tail2, sleepProgress, (float) Math.toRadians(10), (float) Math.toRadians(tailAngle * -70), (float) Math.toRadians(tailAngle * -50), 5F);
        progressRotationPrev(tail3, sleepProgress, (float) Math.toRadians(-30), (float) Math.toRadians(tailAngle * -50), (float) Math.toRadians(tailAngle * -30), 5F);

        if (sleepProgress <= 0.0F) {
            this.faceTarget(netHeadYaw, headPitch, 2, neck, head);
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