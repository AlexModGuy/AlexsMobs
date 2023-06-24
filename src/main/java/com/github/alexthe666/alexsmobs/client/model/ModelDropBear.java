package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityDropBear;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class ModelDropBear extends AdvancedEntityModel<EntityDropBear> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leg_left;
    private final AdvancedModelBox leg_right;
    private final AdvancedModelBox front_body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox ear_left;
    private final AdvancedModelBox ear_right;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox arm_left;
    private final AdvancedModelBox claws_left;
    private final AdvancedModelBox arm_right;
    private final AdvancedModelBox claws_right;
    private ModelAnimator animator;

    public ModelDropBear() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -13.0F, 8.0F);
        root.addChild(body);
        body.setTextureOffset(0, 31).addBox(-6.0F, -8.0F, -7.0F, 12.0F, 13.0F, 13.0F, 0.0F, false);

        leg_left = new AdvancedModelBox(this, "leg_left");
        leg_left.setPos(3.4F, 5.0F, 2.5F);
        body.addChild(leg_left);
        leg_left.setTextureOffset(0, 58).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, 0.0F, false);

        leg_right = new AdvancedModelBox(this, "leg_right");
        leg_right.setPos(-3.4F, 5.0F, 2.5F);
        body.addChild(leg_right);
        leg_right.setTextureOffset(0, 58).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, 0.0F, true);

        front_body = new AdvancedModelBox(this, "front_body");
        front_body.setPos(0.0F, -2.0F, -7.0F);
        body.addChild(front_body);
        front_body.setTextureOffset(0, 0).addBox(-8.0F, -8.0F, -14.0F, 16.0F, 16.0F, 14.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, -1.0F, -14.0F);
        front_body.addChild(head);
        head.setTextureOffset(42, 49).addBox(-5.0F, -5.0F, -9.0F, 10.0F, 8.0F, 9.0F, 0.0F, false);

        nose = new AdvancedModelBox(this, "nose");
        nose.setPos(0.0F, -0.5F, -9.5F);
        head.addChild(nose);
        nose.setTextureOffset(0, 7).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);

        ear_left = new AdvancedModelBox(this, "ear_left");
        ear_left.setPos(2.75F, -2.75F, -3.5F);
        head.addChild(ear_left);
        ear_left.setTextureOffset(21, 58).addBox(-0.75F, -5.25F, -1.5F, 6.0F, 6.0F, 3.0F, 0.0F, false);
        ear_left.setTextureOffset(0, 0).addBox(2.25F, 0.75F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);

        ear_right = new AdvancedModelBox(this, "ear_right");
        ear_right.setPos(-2.75F, -2.75F, -3.5F);
        head.addChild(ear_right);
        ear_right.setTextureOffset(21, 58).addBox(-5.25F, -5.25F, -1.5F, 6.0F, 6.0F, 3.0F, 0.0F, true);
        ear_right.setTextureOffset(0, 0).addBox(-5.25F, 0.75F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this, "jaw");
        jaw.setPos(0.0F, 1.0F, 0.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(47, 0).addBox(-5.0F, 0.0F, -9.0F, 10.0F, 4.0F, 9.0F, 0.0F, false);

        arm_left = new AdvancedModelBox(this, "arm_left");
        arm_left.setPos(6.75F, 3.0F, -8.75F);
        front_body.addChild(arm_left);
        arm_left.setTextureOffset(56, 26).addBox(-1.75F, -3.0F, -2.25F, 5.0F, 14.0F, 5.0F, 0.0F, false);

        claws_left = new AdvancedModelBox(this, "claws_left");
        claws_left.setPos(0.25F, 11.0F, -2.25F);
        arm_left.addChild(claws_left);
        claws_left.setTextureOffset(61, 14).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 2.0F, 5.0F, 0.0F, false);

        arm_right = new AdvancedModelBox(this, "arm_right");
        arm_right.setPos(-6.75F, 3.0F, -8.75F);
        front_body.addChild(arm_right);
        arm_right.setTextureOffset(56, 26).addBox(-3.25F, -3.0F, -2.25F, 5.0F, 14.0F, 5.0F, 0.0F, true);

        claws_right = new AdvancedModelBox(this, "claws_right");
        claws_right.setPos(-0.25F, 11.0F, -2.25F);
        arm_right.addChild(claws_right);
        claws_right.setTextureOffset(61, 14).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 2.0F, 5.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityDropBear.ANIMATION_BITE);
        animator.startKeyframe(5);
        animator.rotate(head, Maths.rad(-40), 0, 0);
        animator.rotate(jaw, Maths.rad(80), 0, 0);
        animator.move(head, 0, 0, 2F);
        animator.move(ear_left, 0, 0, -2F);
        animator.move(ear_right, 0, 0, -2F);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.startKeyframe(2);
        animator.rotate(head, Maths.rad(-5), 0, 0);
        animator.rotate(jaw, Maths.rad(10), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(1);
        animator.setAnimation(EntityDropBear.ANIMATION_SWIPE_L);
        animator.startKeyframe(7);
        animator.rotate(front_body, 0, Maths.rad(10F), 0);
        animator.rotate(head, 0, 0, Maths.rad(10F));
        animator.rotate(arm_left, Maths.rad(65F), 0, Maths.rad(-100F));
        animator.rotate(arm_right, Maths.rad(-15F), 0, Maths.rad(10F));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(front_body, 0, Maths.rad(-20F), 0);
        animator.rotate(arm_left, Maths.rad(-90F), 0, Maths.rad(20F));
        animator.rotate(arm_right, Maths.rad(-15F), 0, Maths.rad(20F));
        animator.move(arm_left, 0, 0, -6F);
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(EntityDropBear.ANIMATION_SWIPE_R);
        animator.startKeyframe(7);
        animator.rotate(front_body, 0, Maths.rad(10F), 0);
        animator.rotate(head, 0, 0, Maths.rad(10F));
        animator.rotate(arm_right, Maths.rad(65F), 0, Maths.rad(100F));
        animator.rotate(arm_left, Maths.rad(-15F), 0, Maths.rad(-10F));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(front_body, 0, Maths.rad(-20F), 0);
        animator.rotate(arm_right, Maths.rad(-90F), 0, Maths.rad(-20F));
        animator.rotate(arm_left, Maths.rad(-15F), 0, Maths.rad(-20F));
        animator.move(arm_right, 0, 0, -6F);
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(EntityDropBear.ANIMATION_JUMPUP);
        animator.startKeyframe(10);
        animator.move(body, 0, 5, 0);
        animator.rotate(arm_right, 0, 0, Maths.rad(40F));
        animator.rotate(arm_left, 0, 0, Maths.rad(-40F));
        animator.rotate(leg_right, 0, 0, Maths.rad(40F));
        animator.rotate(leg_left, 0, 0, Maths.rad(-40F));
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);

    }

    @Override
    public void setupAnim(EntityDropBear entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float upsideDownProgress = entityIn.prevUpsideDownProgress + (entityIn.upsideDownProgress - entityIn.prevUpsideDownProgress) * (ageInTicks - entityIn.tickCount);
        float walkSpeed = 0.7F;
        float walkDegree = 0.7F;
        float idleSpeed = 0.2F;
        float idleDegree = 0.1F;
        float invert = upsideDownProgress > 0 ? -1F : 1F;
        progressPositionPrev(body, upsideDownProgress, 0, 1, 0, 5f);
        progressRotationPrev(body, upsideDownProgress, 0, 0, Maths.rad(180) * (entityIn.fallRotation ? -1F : 1F), 5f);
        this.walk(leg_left, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(leg_left, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(leg_right, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(leg_right, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.walk(arm_right, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(arm_left, walkSpeed, walkDegree, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.bob(arm_left, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.bob(arm_right, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.flap(front_body, walkSpeed, walkDegree * 0.2F, false, -2F, 0, limbSwing, limbSwingAmount);
        this.flap(head, walkSpeed, walkDegree * 0.2F, true, -2F, 0, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree, false, limbSwing, limbSwingAmount);
        this.flap(ear_right, walkSpeed, walkDegree * 0.2F, false, -1F, 0, limbSwing, limbSwingAmount);
        this.flap(ear_left, walkSpeed, walkDegree * 0.2F, true, -1F, 0, limbSwing, limbSwingAmount);
        this.flap(ear_right, idleSpeed, idleDegree, false, -1F, 0, ageInTicks, 1);
        this.flap(ear_left, idleSpeed, idleDegree, true, -1F, 0, ageInTicks, 1);
        this.flap(nose, idleSpeed * 0.5F, idleDegree, false, 0F, 0F, ageInTicks, 1);
        this.head.rotateAngleY += netHeadYaw * 0.9F * invert * Mth.DEG_TO_RAD;
        this.head.rotateAngleX += headPitch * 0.9F *invert * Mth.DEG_TO_RAD;

    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leg_left, leg_right, arm_left, arm_right, head, ear_left, ear_right, nose, jaw, front_body, claws_left, claws_right);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}