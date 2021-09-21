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
    public final AdvancedModelBox storage_chest_left;
    public final AdvancedModelBox storage_chest_right;
    public final AdvancedModelBox tail;
    public final AdvancedModelBox head;
    public final AdvancedModelBox trunk1;
    public final AdvancedModelBox trunk2;
    public final AdvancedModelBox ear_left;
    public final AdvancedModelBox ear_right;
    public final AdvancedModelBox tusk_left;
    public final AdvancedModelBox tusk_right;
    public final AdvancedModelBox frontleg_left;
    public final AdvancedModelBox frontleg_right;
    public final AdvancedModelBox backleg_left;
    public final AdvancedModelBox backleg_right;
    private ModelAnimator animator;

    public ModelElephant(float scale) {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -22.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-10.0F, -20.0F, -17.0F, 20.0F, 23.0F, 33.0F, scale, false);

        storage_chest_left = new AdvancedModelBox(this);
        storage_chest_left.setPos(9.0F, -17.5F, 5.5F);
        body.addChild(storage_chest_left);
        storage_chest_left.setTextureOffset(32, 101).addBox(1.0F, -0.5F, -7.5F, 6.0F, 11.0F, 15.0F, scale, false);

        storage_chest_right = new AdvancedModelBox(this);
        storage_chest_right.setPos(-9.0F, -17.5F, 5.5F);
        body.addChild(storage_chest_right);
        storage_chest_right.setTextureOffset(32, 101).addBox(-7.0F, -0.5F, -7.5F, 6.0F, 11.0F, 15.0F, scale, true);

        tail = new AdvancedModelBox(this);
        tail.setPos(0.0F, -10.0F, 16.0F);
        body.addChild(tail);
        setRotationAngle(tail, 0.0873F, 0.0F, 0.0F);
        tail.setTextureOffset(0, 85).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 20.0F, 0.0F, scale, false);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, -10.0F, -18.0F);
        body.addChild(head);
        head.setTextureOffset(0, 57).addBox(-7.0F, -9.0F, -10.0F, 14.0F, 16.0F, 11.0F, scale, false);

        trunk1 = new AdvancedModelBox(this);
        trunk1.setPos(0.0F, 4.0F, -7.0F);
        head.addChild(trunk1);
        setRotationAngle(trunk1, -0.2182F, 0.0F, 0.0F);
        trunk1.setTextureOffset(74, 0).addBox(-3.0F, -2.0F, -5.0F, 6.0F, 14.0F, 6.0F, scale, false);

        trunk2 = new AdvancedModelBox(this);
        trunk2.setPos(0.5F, 12.0F, -4.3F);
        trunk1.addChild(trunk2);
        setRotationAngle(trunk2, 0.3927F, 0.0F, 0.0F);
        trunk2.setTextureOffset(84, 57).addBox(-3.0F, 0.0F, 0.0F, 5.0F, 15.0F, 5.0F, scale, false);

        ear_left = new AdvancedModelBox(this);
        ear_left.setPos(7.0F, -8.0F, -2.5F);
        head.addChild(ear_left);
        setRotationAngle(ear_left, 0.0F, -0.5236F, 0.0F);
        ear_left.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -0.5F, 14.0F, 18.0F, 1.0F, scale, false);

        ear_right = new AdvancedModelBox(this);
        ear_right.setPos(-7.0F, -8.0F, -2.5F);
        head.addChild(ear_right);
        setRotationAngle(ear_right, 0.0F, 0.5236F, 0.0F);
        ear_right.setTextureOffset(0, 0).addBox(-14.0F, 0.0F, -0.5F, 14.0F, 18.0F, 1.0F, scale, true);

        tusk_left = new AdvancedModelBox(this);
        tusk_left.setPos(4.0F, 7.0F, -8.0F);
        head.addChild(tusk_left);
        setRotationAngle(tusk_left, -0.3491F, 0.0F, -0.2618F);
        tusk_left.setTextureOffset(0, 20).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 9.0F, 2.0F, scale, false);

        tusk_right = new AdvancedModelBox(this);
        tusk_right.setPos(-4.0F, 7.0F, -8.0F);
        head.addChild(tusk_right);
        setRotationAngle(tusk_right, -0.3491F, 0.0F, 0.2618F);
        tusk_right.setTextureOffset(0, 20).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 9.0F, 2.0F, scale, true);

        frontleg_left = new AdvancedModelBox(this);
        frontleg_left.setPos(8.0F, -0.5F, -12.0F);
        body.addChild(frontleg_left);
        frontleg_left.setTextureOffset(84, 84).addBox(-5.0F, -4.5F, -4.0F, 8.0F, 27.0F, 8.0F, scale, false);

        frontleg_right = new AdvancedModelBox(this);
        frontleg_right.setPos(-8.0F, -0.5F, -12.0F);
        body.addChild(frontleg_right);
        frontleg_right.setTextureOffset(84, 84).addBox(-3.0F, -4.5F, -4.0F, 8.0F, 27.0F, 8.0F, scale, true);

        backleg_left = new AdvancedModelBox(this);
        backleg_left.setPos(8.0F, -0.5F, 11.0F);
        body.addChild(backleg_left);
        backleg_left.setTextureOffset(51, 57).addBox(-5.0F, -5.5F, -4.0F, 8.0F, 28.0F, 8.0F, scale, false);

        backleg_right = new AdvancedModelBox(this);
        backleg_right.setPos(-8.0F, -0.5F, 11.0F);
        body.addChild(backleg_right);
        backleg_right.setTextureOffset(51, 57).addBox(-3.0F, -5.5F, -4.0F, 8.0F, 28.0F, 8.0F, scale, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityElephant.ANIMATION_TRUMPET_0);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, 0);
        animator.rotate(ear_left, 0, (float)Math.toRadians(25), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-55), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(-55), 0, 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-35), 0, 0);
        animator.rotate(ear_left, 0, (float)Math.toRadians(45), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-45), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-65), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(-75), 0, 0);
        animator.move(trunk2, 0,  -2, 1);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(7);
        animator.setAnimation(EntityElephant.ANIMATION_TRUMPET_1);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(-25));
        animator.rotate(ear_left, 0, (float)Math.toRadians(25), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75), (float)Math.toRadians(25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-55), (float)Math.toRadians(10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(25));
        animator.rotate(ear_left, 0, (float)Math.toRadians(25), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75),  (float)Math.toRadians(-25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-55), (float)Math.toRadians(-10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(-25));
        animator.rotate(ear_left, 0, (float)Math.toRadians(25), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75), (float)Math.toRadians(25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-55), (float)Math.toRadians(10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-25), 0, (float)Math.toRadians(25));
        animator.rotate(ear_left, 0, (float)Math.toRadians(25), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-75),  (float)Math.toRadians(-25), 0);
        animator.rotate(trunk2, (float)Math.toRadians(-55), (float)Math.toRadians(-10), 0);
        animator.move(trunk2, 0,  -2, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(EntityElephant.ANIMATION_CHARGE_PREPARE);
        animator.startKeyframe(10);
        animator.rotate(body, (float)Math.toRadians(15), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-20), 0);
        animator.rotate(ear_left, 0, (float)Math.toRadians(20), 0);
        animator.rotate(frontleg_left, (float)Math.toRadians(-15), 0, (float)Math.toRadians(-15));
        animator.rotate(frontleg_right, (float)Math.toRadians(-15), 0, (float)Math.toRadians(15));
        animator.rotate(backleg_left, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(backleg_right, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(trunk1, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(45), 0, 0);
        animator.move(frontleg_right, 0,  -3, 0);
        animator.move(frontleg_left, 0,  -3, 0);
        animator.move(backleg_left, 0,  3, 0);
        animator.move(backleg_right, 0,  3, 0);
        animator.move(head, 0,  2, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityElephant.ANIMATION_STOMP);
        animator.startKeyframe(10);
        animator.rotate(body, (float)Math.toRadians(-35), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(backleg_left, (float)Math.toRadians(35), 0, 0);
        animator.rotate(backleg_right, (float)Math.toRadians(35), 0, 0);
        animator.rotate(frontleg_left, (float)Math.toRadians(35), (float)Math.toRadians(-15), 0);
        animator.rotate(frontleg_right, (float)Math.toRadians(35), (float)Math.toRadians(15), 0);
        animator.rotate(trunk1, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(45), 0, 0);
        animator.rotate(tail, (float)Math.toRadians(45), 0, 0);
        animator.move(body, 0,  -6, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(7);
        animator.resetKeyframe(3);
        animator.setAnimation(EntityElephant.ANIMATION_FLING);
        animator.startKeyframe(10);
        animator.rotate(head, (float)Math.toRadians(15), 0, 0);
        animator.rotate(ear_left, 0, (float)Math.toRadians(25), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-25), 0);
        animator.rotate(trunk1, (float)Math.toRadians(25), 0, 0);
        animator.rotate(trunk2, (float)Math.toRadians(15), 0, 0);
        animator.move(head, 0,  3, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(head, 0,  -2, -1);
        animator.rotate(head, (float)Math.toRadians(-45), 0, 0);
        animator.rotate(ear_left, 0, (float)Math.toRadians(25), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-25), 0);
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
        animator.move(trunk2, 0,  3.5F, 4F);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(9);

        animator.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
        animator.startKeyframe(5);
        animator.rotate(head, (float)Math.toRadians(-5), 0, 0);
        animator.rotate(ear_left, 0, (float)Math.toRadians(5), 0);
        animator.rotate(ear_right, 0, (float)Math.toRadians(-5), 0);
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
            head.rotationPointY = -13F;
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
            head.rotationPointY = -10.0F;
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
        progressRotationPrev(frontleg_right, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(10), 0, 5F);
        progressRotationPrev(frontleg_left, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(-10), 0, 5F);
        progressRotationPrev(backleg_right, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(-15), 0, 5F);
        progressRotationPrev(backleg_left, standProgress, (float)Math.toRadians(60),  (float)Math.toRadians(15), 0, 5F);
        progressPositionPrev(body, standProgress, 0, -9, 0, 5F);
        progressPositionPrev(frontleg_right, standProgress, 0, 0, 2, 5F);
        progressPositionPrev(frontleg_left, standProgress, 0, 0, 2, 5F);

        progressRotationPrev(tail, limbSwingAmount, (float)Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(frontleg_right, sitProgress, (float)Math.toRadians(-90),  (float)Math.toRadians(10), 0, 5F);
        progressRotationPrev(frontleg_left, sitProgress, (float)Math.toRadians(-90),  (float)Math.toRadians(-10), 0, 5F);
        progressRotationPrev(backleg_right, sitProgress, (float)Math.toRadians(90),  (float)Math.toRadians(5), 0, 5F);
        progressRotationPrev(backleg_left, sitProgress, (float)Math.toRadians(90),  (float)Math.toRadians(-5), 0, 5F);
        progressPositionPrev(body, sitProgress, 0, 14, 0, 5F);
        progressPositionPrev(frontleg_right, sitProgress, 0, 5, 5, 5F);
        progressPositionPrev(frontleg_left, sitProgress, 0, 5, 5, 5F);
        progressPositionPrev(backleg_right, sitProgress, 0, 5, -3, 5F);
        progressPositionPrev(backleg_left, sitProgress, 0, 5, -3, 5F);
        progressPositionPrev(head, sitProgress, 0, -3, 0, 5F);
        progressPositionPrev(trunk1, sitProgress, 0, -2, 0, 5F);
        progressPositionPrev(trunk2, sitProgress, 0, 1, 0, 5F);
        progressRotationPrev(trunk1, sitProgress,(float)Math.toRadians(-45),  0, 0, 5F);
        progressRotationPrev(trunk2, sitProgress,(float)Math.toRadians(60),  0, 0, 5F);
        progressRotationPrev(tail, sitProgress,(float)Math.toRadians(50),  0, 0, 5F);
        this.swing(ear_right, idleSpeed, idleDegree, false, 1F, 0F, ageInTicks, 1);
        this.swing(ear_left, idleSpeed, idleDegree, true, 1F, 0F, ageInTicks, 1);
        this.walk(head, idleSpeed * 0.7F, idleDegree * 0.1F, false, -1F, 0.05F, ageInTicks, 1);
        this.walk(trunk1, idleSpeed * 0.4F, idleDegree * 0.7F, false, 0F, 0.1F, ageInTicks, 1);
        this.flap(trunk1, idleSpeed * 0.4F, idleDegree * 0.7F, false, 2F, 0F, ageInTicks, 1);
        this.walk(trunk2, idleSpeed * 0.4F, idleDegree * 0.35F, false, 0F, 0.05F, ageInTicks, 1);
        this.flap(tail, idleSpeed, idleDegree * 0.7F, false, -1, 0F, ageInTicks, 1);
        this.walk(frontleg_right, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(frontleg_left, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(backleg_right, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(backleg_left, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(head, walkSpeed, walkDegree * 0.1F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
        this.walk(trunk1, walkSpeed, walkDegree * 0.1F, true, 0F, -0.1F, limbSwing, limbSwingAmount);
        this.walk(trunk2, walkSpeed, walkDegree * 0.1F, true, 0F, -0.3F, limbSwing, limbSwingAmount);
        this.swing(ear_right, walkSpeed, walkDegree * 0.34F, false, 1F, 0.01F, limbSwing, limbSwingAmount);
        this.swing(ear_left, walkSpeed, walkDegree * 0.34F, true, 1F, 0.01F, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed * 2F, walkDegree * 2F, false, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 2, head);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, storage_chest_left, storage_chest_right, tail, head, trunk1, trunk2, tusk_left, tusk_right, tail, ear_left, ear_right, backleg_left, backleg_right, frontleg_left, frontleg_right);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}