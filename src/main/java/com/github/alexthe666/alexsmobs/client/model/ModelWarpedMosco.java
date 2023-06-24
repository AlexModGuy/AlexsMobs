package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class ModelWarpedMosco extends AdvancedEntityModel<EntityWarpedMosco> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox back;
    private final AdvancedModelBox legfront_left;
    private final AdvancedModelBox kneefront_left;
    private final AdvancedModelBox legfront_right;
    private final AdvancedModelBox kneefront_right;
    private final AdvancedModelBox legback_left;
    private final AdvancedModelBox kneeback_left;
    private final AdvancedModelBox legback_right;
    private final AdvancedModelBox kneeback_right;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox wingtop_left;
    private final AdvancedModelBox wingtop_right;
    private final AdvancedModelBox wingbottom_left;
    private final AdvancedModelBox wingbottom_left_r1;
    private final AdvancedModelBox wingbottom_right;
    private final AdvancedModelBox wingbottom_right_r1;
    private final AdvancedModelBox shoulder_left;
    private final AdvancedModelBox shoulderspikes_left;
    private final AdvancedModelBox arm_left;
    private final AdvancedModelBox hand_left;
    private final AdvancedModelBox shoulder_right;
    private final AdvancedModelBox shoulderspikes_right;
    private final AdvancedModelBox arm_right;
    private final AdvancedModelBox hand_right;
    private final AdvancedModelBox head;
    private final AdvancedModelBox antenna_left;
    private final AdvancedModelBox antenna_right;
    private final AdvancedModelBox proboscis;
    private final AdvancedModelBox proboscis_r1;
    private ModelAnimator animator;

    public ModelWarpedMosco() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -24.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 31).addBox(-8.0F, -10.0F, -7.0F, 16.0F, 14.0F, 14.0F, 0.0F, false);

        back = new AdvancedModelBox(this, "back");
        back.setPos(0.0F, -4.0F, 7.0F);
        body.addChild(back);
        setRotationAngle(back, -0.4363F, 0.0F, 0.0F);
        back.setTextureOffset(44, 44).addBox(-5.0F, -1.0F, 0.0F, 10.0F, 10.0F, 17.0F, 0.0F, false);

        legfront_left = new AdvancedModelBox(this, "legfront_left");
        legfront_left.setPos(5.5F, 2.0F, -4.5F);
        body.addChild(legfront_left);
        setRotationAngle(legfront_left, 0.0F, -0.5236F, 0.0F);
        legfront_left.setTextureOffset(72, 86).addBox(-3.5F, -2.0F, -3.5F, 7.0F, 12.0F, 7.0F, 0.0F, false);

        kneefront_left = new AdvancedModelBox(this, "kneefront_left");
        kneefront_left.setPos(0.0F, 10.0F, 0.0F);
        legfront_left.addChild(kneefront_left);
        kneefront_left.setTextureOffset(101, 81).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F, 0.0F, false);

        legfront_right = new AdvancedModelBox(this, "legfront_right");
        legfront_right.setPos(-5.5F, 2.0F, -4.5F);
        body.addChild(legfront_right);
        setRotationAngle(legfront_right, 0.0F, 0.5236F, 0.0F);
        legfront_right.setTextureOffset(72, 86).addBox(-3.5F, -2.0F, -3.5F, 7.0F, 12.0F, 7.0F, 0.0F, true);

        kneefront_right = new AdvancedModelBox(this, "kneefront_right");
        kneefront_right.setPos(0.0F, 10.0F, 0.0F);
        legfront_right.addChild(kneefront_right);
        kneefront_right.setTextureOffset(101, 81).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F, 0.0F, true);

        legback_left = new AdvancedModelBox(this, "legback_left");
        legback_left.setPos(5.5F, 2.0F, 5.5F);
        body.addChild(legback_left);
        setRotationAngle(legback_left, 0.0F, 0.6981F, 0.0F);
        legback_left.setTextureOffset(72, 86).addBox(-3.5F, -2.0F, -3.5F, 7.0F, 12.0F, 7.0F, 0.0F, false);

        kneeback_left = new AdvancedModelBox(this, "kneeback_left");
        kneeback_left.setPos(0.0F, 10.0F, 0.0F);
        legback_left.addChild(kneeback_left);
        kneeback_left.setTextureOffset(101, 81).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F, 0.0F, false);

        legback_right = new AdvancedModelBox(this, "legback_right");
        legback_right.setPos(-5.5F, 2.0F, 5.5F);
        body.addChild(legback_right);
        setRotationAngle(legback_right, 0.0F, -0.6981F, 0.0F);
        legback_right.setTextureOffset(72, 86).addBox(-3.5F, -2.0F, -3.5F, 7.0F, 12.0F, 7.0F, 0.0F, true);

        kneeback_right = new AdvancedModelBox(this, "kneeback_right");
        kneeback_right.setPos(0.0F, 10.0F, 0.0F);
        legback_right.addChild(kneeback_right);
        kneeback_right.setTextureOffset(101, 81).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F, 0.0F, true);

        chest = new AdvancedModelBox(this, "chest");
        chest.setPos(0.0F, -10.0F, 0.0F);
        body.addChild(chest);
        chest.setTextureOffset(0, 0).addBox(-12.0F, -14.0F, -8.0F, 24.0F, 14.0F, 16.0F, 0.0F, false);

        wingtop_left = new AdvancedModelBox(this, "wingtop_left");
        wingtop_left.setPos(0.5F, -7.0F, 8.0F);
        chest.addChild(wingtop_left);
        setRotationAngle(wingtop_left, 0.0F, -0.0873F, -0.2182F);
        wingtop_left.setTextureOffset(24, 109).addBox(0.0F, -11.0F, 0.0F, 33.0F, 19.0F, 0.0F, 0.0F, false);

        wingtop_right = new AdvancedModelBox(this, "wingtop_right");
        wingtop_right.setPos(-0.5F, -7.0F, 8.0F);
        chest.addChild(wingtop_right);
        setRotationAngle(wingtop_right, 0.0F, 0.0873F, 0.2182F);
        wingtop_right.setTextureOffset(24, 109).addBox(-33.0F, -11.0F, 0.0F, 33.0F, 19.0F, 0.0F, 0.0F, true);

        wingbottom_left = new AdvancedModelBox(this, "wingbottom_left");
        wingbottom_left.setPos(0.5F, -6.0F, 8.0F);
        chest.addChild(wingbottom_left);
        setRotationAngle(wingbottom_left, 0.0F, -0.0873F, -0.2182F);


        wingbottom_left_r1 = new AdvancedModelBox(this, "wingbottom_left_r1");
        wingbottom_left_r1.setPos(0.0F, 0.0F, 0.0F);
        wingbottom_left.addChild(wingbottom_left_r1);
        setRotationAngle(wingbottom_left_r1, 0.0436F, 0.0F, 0.829F);
        wingbottom_left_r1.setTextureOffset(24, 109).addBox(0.0F, -11.0F, 0.0F, 33.0F, 19.0F, 0.0F, 0.0F, false);

        wingbottom_right = new AdvancedModelBox(this, "wingbottom_right");
        wingbottom_right.setPos(-0.5F, -6.0F, 8.0F);
        chest.addChild(wingbottom_right);
        setRotationAngle(wingbottom_right, 0.0F, 0.0873F, 0.2182F);


        wingbottom_right_r1 = new AdvancedModelBox(this, "wingbottom_right_r1");
        wingbottom_right_r1.setPos(0.0F, 0.0F, 0.0F);
        wingbottom_right.addChild(wingbottom_right_r1);
        setRotationAngle(wingbottom_right_r1, 0.0436F, 0.0F, -0.829F);
        wingbottom_right_r1.setTextureOffset(24, 109).addBox(-33.0F, -11.0F, 0.0F, 33.0F, 19.0F, 0.0F, 0.0F, true);

        shoulder_left = new AdvancedModelBox(this, "shoulder_left");
        shoulder_left.setPos(16.0F, -11.0F, 0.0F);
        chest.addChild(shoulder_left);
        shoulder_left.setTextureOffset(0, 60).addBox(-4.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, false);

        shoulderspikes_left = new AdvancedModelBox(this, "shoulderspikes_left");
        shoulderspikes_left.setPos(6.5F, -4.0F, 0.0F);
        shoulder_left.addChild(shoulderspikes_left);
        shoulderspikes_left.setTextureOffset(101, 101).addBox(-6.5F, -8.0F, 0.0F, 13.0F, 16.0F, 0.0F, 0.0F, false);

        arm_left = new AdvancedModelBox(this, "arm_left");
        arm_left.setPos(1.1F, 6.0F, 2.0F);
        shoulder_left.addChild(arm_left);
        arm_left.setTextureOffset(71, 21).addBox(-5.0F, 0.0F, -7.0F, 10.0F, 11.0F, 10.0F, 0.0F, false);

        hand_left = new AdvancedModelBox(this, "hand_left");
        hand_left.setPos(0.0F, 11.0F, 0.0F);
        arm_left.addChild(hand_left);
        hand_left.setTextureOffset(0, 85).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 12.0F, 10.0F, 0.0F, false);

        shoulder_right = new AdvancedModelBox(this, "shoulder_right");
        shoulder_right.setPos(-16.0F, -11.0F, 0.0F);
        chest.addChild(shoulder_right);
        shoulder_right.setTextureOffset(0, 60).addBox(-8.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, true);

        shoulderspikes_right = new AdvancedModelBox(this, "shoulderspikes_right");
        shoulderspikes_right.setPos(-6.5F, -4.0F, 0.0F);
        shoulder_right.addChild(shoulderspikes_right);
        shoulderspikes_right.setTextureOffset(101, 101).addBox(-6.5F, -8.0F, 0.0F, 13.0F, 16.0F, 0.0F, 0.0F, true);

        arm_right = new AdvancedModelBox(this, "arm_right");
        arm_right.setPos(-1.2F, 6.0F, 2.0F);
        shoulder_right.addChild(arm_right);
        arm_right.setTextureOffset(71, 21).addBox(-5.0F, 0.0F, -7.0F, 10.0F, 11.0F, 10.0F, 0.0F, true);

        hand_right = new AdvancedModelBox(this, "hand_right");
        hand_right.setPos(0.0F, 11.0F, 0.0F);
        arm_right.addChild(hand_right);
        hand_right.setTextureOffset(0, 85).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 12.0F, 10.0F, 0.0F, true);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, -14.0F, -1.0F);
        chest.addChild(head);
        head.setTextureOffset(82, 43).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 5.0F, 10.0F, 0.0F, false);

        antenna_left = new AdvancedModelBox(this, "antenna_left");
        antenna_left.setPos(2.0F, -5.0F, -5.0F);
        head.addChild(antenna_left);
        setRotationAngle(antenna_left, -0.829F, 0.0F, 0.0F);
        antenna_left.setTextureOffset(102, 59).addBox(-1.0F, -20.0F, 0.0F, 6.0F, 20.0F, 0.0F, 0.0F, false);

        antenna_right = new AdvancedModelBox(this, "antenna_right");
        antenna_right.setPos(-2.0F, -5.0F, -5.0F);
        head.addChild(antenna_right);
        setRotationAngle(antenna_right, -0.829F, 0.0F, 0.0F);
        antenna_right.setTextureOffset(102, 59).addBox(-5.0F, -20.0F, 0.0F, 6.0F, 20.0F, 0.0F, 0.0F, true);

        proboscis = new AdvancedModelBox(this, "proboscis");
        proboscis.setPos(0.0F, -1.0F, -6.0F);
        head.addChild(proboscis);


        proboscis_r1 = new AdvancedModelBox(this, "proboscis_r1");
        proboscis_r1.setPos(0.0F, -1.0F, 1.0F);
        proboscis.addChild(proboscis_r1);
        setRotationAngle(proboscis_r1, 0.3054F, 0.0F, 0.0F);
        proboscis_r1.setTextureOffset(37, 86).addBox(-1.0F, 0.0F, -15.0F, 2.0F, 1.0F, 15.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityWarpedMosco.ANIMATION_PUNCH_R);
        animator.startKeyframe(10);
        animator.rotate(chest,0,  (float)Math.toRadians(40F), 0);
        animator.rotate(shoulder_right, (float)Math.toRadians(40F),  (float)Math.toRadians(20F), 0);
        animator.rotate(arm_right, (float)Math.toRadians(-80F), 0, 0);
        animator.rotate(hand_right, (float)Math.toRadians(-30F), 0, 0);
        animator.rotate(shoulder_left, (float)Math.toRadians(20F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body,0,  (float)Math.toRadians(-20F), 0);
        animator.rotate(chest,0,  (float)Math.toRadians(-40F), 0);
        animator.rotate(head,0,  (float)Math.toRadians(30F), 0);
        animator.rotate(shoulder_right, (float)Math.toRadians(-20F),  (float)Math.toRadians(20F), (float)Math.toRadians(20F));
        animator.rotate(arm_right, (float)Math.toRadians(-20F), 0, 0);
        animator.rotate(hand_right, (float)Math.toRadians(-30F), (float)Math.toRadians(-30F), 0);
        animator.rotate(shoulder_left, (float)Math.toRadians(20F), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityWarpedMosco.ANIMATION_PUNCH_L);
        animator.startKeyframe(10);
        animator.rotate(chest,0,  (float)Math.toRadians(-40F), 0);
        animator.rotate(shoulder_left, (float)Math.toRadians(40F),  (float)Math.toRadians(-20F), 0);
        animator.rotate(arm_left, (float)Math.toRadians(-80F), 0, 0);
        animator.rotate(hand_right, (float)Math.toRadians(-30F), 0, 0);
        animator.rotate(shoulder_right, (float)Math.toRadians(20F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body,0,  (float)Math.toRadians(20F), 0);
        animator.rotate(chest,0,  (float)Math.toRadians(40F), 0);
        animator.rotate(head,0,  (float)Math.toRadians(-30F), 0);
        animator.rotate(shoulder_left, (float)Math.toRadians(-20F),  (float)Math.toRadians(-20F), (float)Math.toRadians(-20F));
        animator.rotate(arm_left, (float)Math.toRadians(-20F), 0, 0);
        animator.rotate(hand_left, (float)Math.toRadians(-30F), (float)Math.toRadians(30F), 0);
        animator.rotate(shoulder_right, (float)Math.toRadians(20F), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityWarpedMosco.ANIMATION_SLAM);
        animator.startKeyframe(10);
        animator.rotate(chest, (float)Math.toRadians(-30F), 0, 0);
        animator.rotate(shoulder_left, (float)Math.toRadians(-160F), (float)Math.toRadians(20F), (float)Math.toRadians(-10F));
        animator.rotate(shoulder_right, (float)Math.toRadians(-160F), (float)Math.toRadians(-20F), (float)Math.toRadians(10F));
        animator.rotate(arm_right, (float)Math.toRadians(-20F), 0, 0);
        animator.rotate(arm_left, (float)Math.toRadians(-20F), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(60F), 0, 0);
        animator.rotate(body, (float)Math.toRadians(40F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-60F), 0, 0);
        animator.rotate(legfront_left, (float)Math.toRadians(-40F), 0, 0);
        animator.rotate(legfront_right, (float)Math.toRadians(-40F), 0, 0);
        animator.rotate(legback_left, (float)Math.toRadians(-40F), 0, (float)Math.toRadians(-30F));
        animator.rotate(legback_right, (float)Math.toRadians(-40F), 0, (float)Math.toRadians(30F));
        animator.rotate(shoulder_left, (float)Math.toRadians(-100F), (float)Math.toRadians(20F), (float)Math.toRadians(-10F));
        animator.rotate(shoulder_right, (float)Math.toRadians(-100F), (float)Math.toRadians(-20F), (float)Math.toRadians(10F));
        animator.rotate(arm_right, (float)Math.toRadians(-20F), 0, 0);
        animator.rotate(arm_left, (float)Math.toRadians(-20F), 0, 0);
        animator.move(legfront_left, 0, -3, 0);
        animator.move(legfront_right, 0, -3, 0);
        animator.move(legback_left, 0, 6, 0);
        animator.move(legback_right, 0, 6, 0);
        animator.move(shoulder_left, 0, -3, 0);
        animator.move(shoulder_right, 0, -3, 0);
        animator.move(body, 0, 0, 2);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
        animator.setAnimation(EntityWarpedMosco.ANIMATION_SUCK);
        animator.startKeyframe(10);
        animator.rotate(chest, (float)Math.toRadians(60F), 0, 0);
        animator.rotate(body, (float)Math.toRadians(40F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-60F), 0, 0);
        animator.rotate(legfront_left, (float)Math.toRadians(-40F), 0, 0);
        animator.rotate(legfront_right, (float)Math.toRadians(-40F), 0, 0);
        animator.rotate(legback_left, (float)Math.toRadians(-40F), 0, (float)Math.toRadians(-30F));
        animator.rotate(legback_right, (float)Math.toRadians(-40F), 0, (float)Math.toRadians(30F));
        animator.rotate(shoulder_left, (float)Math.toRadians(-100F), (float)Math.toRadians(20F), (float)Math.toRadians(-10F));
        animator.rotate(shoulder_right, (float)Math.toRadians(-100F), (float)Math.toRadians(-20F), (float)Math.toRadians(10F));
        animator.rotate(arm_right, (float)Math.toRadians(-20F), 0, 0);
        animator.rotate(arm_left, (float)Math.toRadians(-20F), 0, 0);
        animator.move(legfront_left, 0, -3, 0);
        animator.move(legfront_right, 0, -3, 0);
        animator.move(legback_left, 0, 6, 0);
        animator.move(legback_right, 0, 6, 0);
        animator.move(shoulder_left, 0, -3, 0);
        animator.move(shoulder_right, 0, -3, 0);
        animator.move(body, 0, 0, 2);
        animator.endKeyframe();
        animator.startKeyframe(15);
        suckPose();
        animator.endKeyframe();
        for(int i = 0; i < 5; i++){
            animator.startKeyframe(5);
            suckPose();
            animator.move(proboscis, 0,  i % 2 == 0 ? -1 : 0, i % 2 == 0 ? 4 : 0);
            animator.endKeyframe();
        }
        animator.resetKeyframe(10);
        animator.setAnimation(EntityWarpedMosco.ANIMATION_SPIT);
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(20F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-30F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(20F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-30F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(20F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-30F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(-25F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-25F), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float)Math.toRadians(20F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-30F), 0, 0);
        animator.endKeyframe();

        animator.resetKeyframe(10);

    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, back, legfront_left, kneefront_left, legfront_right, kneefront_right,
                legback_left, kneeback_left, legback_right, kneeback_right, chest, wingtop_left, wingtop_right, wingbottom_left, wingbottom_left_r1, wingbottom_right, wingbottom_right_r1, shoulder_left,
                shoulderspikes_left, arm_left, hand_left, shoulder_right, shoulderspikes_right, arm_right, hand_right, head, antenna_left, antenna_right, proboscis, proboscis_r1);
    }

    private void suckPose(){
        animator.rotate(chest, (float)Math.toRadians(20F), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-60F), 0, 0);
        animator.rotate(shoulder_left, (float)Math.toRadians(-100F), (float)Math.toRadians(20F), (float)Math.toRadians(-10F));
        animator.rotate(shoulder_right, (float)Math.toRadians(-100F), (float)Math.toRadians(-20F), (float)Math.toRadians(10F));
        animator.rotate(arm_right, (float)Math.toRadians(-20F), 0, 0);
        animator.rotate(arm_left, (float)Math.toRadians(-20F), 0, 0);
    }

    @Override
    public void setupAnim(EntityWarpedMosco entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float flySpeed = 0.5F;
        float flyDegree = 0.5F;
        float walkSpeed = 0.5F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float partialTicks = ageInTicks - entity.tickCount;
        float flyLeftProgress = entity.prevLeftFlyProgress + (entity.flyLeftProgress - entity.prevLeftFlyProgress) * partialTicks;
        float flyRightProgress = entity.prevFlyRightProgress + (entity.flyRightProgress - entity.prevFlyRightProgress) * partialTicks;
        float flyProgress = Math.max(flyLeftProgress, flyRightProgress);
        float walkProgress = (5F - flyProgress) * limbSwingAmount;

        this.walk(antenna_left, idleSpeed, idleDegree, false, 0, -0.1F, ageInTicks, 1);
        this.walk(antenna_right, idleSpeed, idleDegree, false, 0, -0.1F, ageInTicks, 1);
        this.walk(shoulder_left, idleSpeed, idleDegree, false, 0, -0.1F, ageInTicks, 1);
        this.walk(shoulder_right, idleSpeed, idleDegree, false, 0, -0.1F, ageInTicks, 1);
        this.flap(shoulder_left, idleSpeed, idleDegree * 0.5F, false, 0, -0.1F, ageInTicks, 1);
        this.flap(shoulder_right, idleSpeed, idleDegree * 0.5F, true, 0, -0.1F, ageInTicks, 1);
        this.walk(hand_left, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        this.walk(hand_right, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        this.walk(head, idleSpeed, idleDegree * 0.25F, false, -1, 0.05F, ageInTicks, 1);
        this.walk(chest, idleSpeed, idleDegree * 0.15F, false, -2, 0.05F, ageInTicks, 1);
        this.bob(body, idleSpeed, idleDegree * 5, false, ageInTicks, 1);
        this.walk(legfront_right, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        this.walk(kneefront_right, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
        this.walk(legfront_left, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        this.walk(kneefront_left, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
        this.walk(legback_right, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
        this.walk(kneeback_right, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        this.walk(legback_left, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
        this.walk(kneeback_left, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        progressRotationPrev(chest, walkProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(wingbottom_left, walkProgress, 0, (float) Math.toRadians(-50), 0, 5F);
        progressRotationPrev(wingbottom_right, walkProgress, 0, (float) Math.toRadians(50), 0, 5F);
        progressRotationPrev(wingtop_left, walkProgress, 0, (float) Math.toRadians(-50), 0, 5F);
        progressRotationPrev(wingtop_right, walkProgress, 0, (float) Math.toRadians(50), 0, 5F);
        progressRotationPrev(head, walkProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(back, walkProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(head, flyProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(body, flyProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(legback_left, flyProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(legback_right, flyProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(legfront_left, flyProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(legfront_right, flyProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(chest, flyLeftProgress, 0, (float) Math.toRadians(30), 0, 5F);
        progressRotationPrev(head, flyLeftProgress, 0, (float) Math.toRadians(-30), 0, 5F);
        progressRotationPrev(shoulder_left, flyLeftProgress, (float) Math.toRadians(-30), (float) Math.toRadians(-30), 0, 5F);
        progressRotationPrev(arm_left, flyLeftProgress, (float) Math.toRadians(-40), (float) Math.toRadians(10), 0, 5F);
        progressRotationPrev(hand_left, flyLeftProgress, (float) Math.toRadians(-30), (float) Math.toRadians(60),  (float) Math.toRadians(20), 5F);
        progressRotationPrev(chest, flyRightProgress, 0, (float) Math.toRadians(-30), 0, 5F);
        progressRotationPrev(head, flyRightProgress, 0, (float) Math.toRadians(30), 0, 5F);
        progressRotationPrev(shoulder_right, flyRightProgress, (float) Math.toRadians(-30), (float) Math.toRadians(30), 0, 5F);
        progressRotationPrev(arm_right, flyRightProgress, (float) Math.toRadians(-40), (float) Math.toRadians(-10), 0, 5F);
        progressRotationPrev(hand_right, flyRightProgress, (float) Math.toRadians(-30), (float) Math.toRadians(-60),  (float) Math.toRadians(-20), 5F);
        if (flyProgress <= 0F) {
            this.walk(kneefront_left, walkSpeed, walkDegree * 0.4F, true, 0, -0.1F, limbSwing, limbSwingAmount);
            this.walk(legfront_left, walkSpeed, walkDegree * 0.8F, false, 2.0F, -0.3F, limbSwing, limbSwingAmount);
            this.walk(kneefront_right, walkSpeed, walkDegree * 0.4F, false, 0, 0.1F, limbSwing, limbSwingAmount);
            this.walk(legfront_right, walkSpeed, walkDegree * 0.8F, true, 2.0F, 0.3F, limbSwing, limbSwingAmount);
            this.walk(kneeback_left, walkSpeed, walkDegree * 0.4F, false, 0, -0.1F, limbSwing, limbSwingAmount);
            this.walk(legback_left, walkSpeed, walkDegree * 0.8F, true, 2.0F, -0.3F, limbSwing, limbSwingAmount);
            this.walk(kneeback_right, walkSpeed, walkDegree * 0.4F, true, 0, 0.1F, limbSwing, limbSwingAmount);
            this.walk(legback_right, walkSpeed, walkDegree * 0.8F, false, 2.0F, 0.3F, limbSwing, limbSwingAmount);
            this.swing(chest, walkSpeed, walkDegree * 0.3F, false, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(shoulder_left, walkSpeed, walkDegree * 0.6F, true, 0, 0F, limbSwing, limbSwingAmount);
            this.walk(shoulder_right, walkSpeed, walkDegree * 0.6F, false, 0, 0.0F, limbSwing, limbSwingAmount);
            this.walk(arm_left, walkSpeed, walkDegree * 0.6F, true, 0.6F, 0.9F, limbSwing, limbSwingAmount);
            this.walk(arm_right, walkSpeed, walkDegree * 0.6F, false, 0.6F, -0.9F, limbSwing, limbSwingAmount);
            this.walk(hand_left, walkSpeed, walkDegree * 0.6F, true, 0.6F, 0.3F, limbSwing, limbSwingAmount);
            this.walk(hand_right, walkSpeed, walkDegree * 0.6F, false, 0.6F, -0.3F, limbSwing, limbSwingAmount);
            this.swing(legfront_left, walkSpeed, walkDegree * 0.4F, false, 1F, 0F, limbSwing, limbSwingAmount);
            this.swing(legfront_right, walkSpeed, walkDegree * 0.4F, true, 1F, 0F, limbSwing, limbSwingAmount);
            this.swing(legback_left, walkSpeed, walkDegree * 0.4F, true, 1F, 0F, limbSwing, limbSwingAmount);
            this.swing(legback_right, walkSpeed, walkDegree * 0.4F, false, 1F, 0F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed, walkDegree * 5, false, limbSwing, limbSwingAmount);
        } else {
            this.swing(wingbottom_left, flySpeed * 3.3F, flyDegree, true, 0, 0.2F, ageInTicks, 1);
            this.swing(wingbottom_right, flySpeed * 3.3F, flyDegree, false, 0, 0.2F, ageInTicks, 1);
            this.swing(wingtop_left, flySpeed * 3.3F, flyDegree * 1.3F, true, 1, 0.5F, ageInTicks, 1);
            this.swing(wingtop_right, flySpeed * 3.3F, flyDegree * 1.3F, false, 1, 0.5F, ageInTicks, 1);
            this.bob(body, flySpeed, flyDegree * 5, false, ageInTicks, 1);
        }
        if(entity.getAnimation() != EntityWarpedMosco.ANIMATION_SUCK){
            this.head.rotateAngleY += netHeadYaw * 0.6F * Mth.DEG_TO_RAD;
            this.head.rotateAngleX += headPitch * 0.9F * Mth.DEG_TO_RAD;
        }
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}