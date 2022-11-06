package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;

public class ModelShoebill extends AdvancedEntityModel<EntityShoebill> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox wing_left;
    private final AdvancedModelBox wing_right;
    private final AdvancedModelBox wing_right_pivot;
    private final AdvancedModelBox wing_left_pivot;
    private final AdvancedModelBox leg_left;
    private final AdvancedModelBox leg_right;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox crest;
    private final AdvancedModelBox beak;
    private final AdvancedModelBox jaw;
    public ModelAnimator animator;

    public ModelShoebill() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -14.0F, 0.0F);
        root.addChild(body);
        setRotationAngle(body, 0.9599F, 0.0F, 0.0F);
        body.setTextureOffset(0, 15).addBox(-2.5F, -5.0F, -2.0F, 5.0F, 10.0F, 5.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.0F, 5.0F, 2.5F);
        body.addChild(tail);
        setRotationAngle(tail, -0.3054F, 0.0F, 0.0F);
        tail.setTextureOffset(0, 31).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 5.0F, 3.0F, 0.0F, false);

        wing_left_pivot = new AdvancedModelBox(this, "wing_left_pivot");
        wing_left_pivot.setPos(2.5F, -4.0F, 1.0F);
        body.addChild(wing_left_pivot);


        wing_left = new AdvancedModelBox(this, "wing_left");
        wing_left_pivot.addChild(wing_left);
        wing_left.setTextureOffset(21, 21).addBox(0.0F, 0.0F, -4.0F, 1.0F, 12.0F, 6.0F, 0.0F, false);

        wing_right_pivot = new AdvancedModelBox(this, "wing_right_pivot");
        wing_right_pivot.setPos(-2.5F, -4.0F, 1.0F);
        body.addChild(wing_right_pivot);

        wing_right = new AdvancedModelBox(this, "wing_right");
        wing_right_pivot.addChild(wing_right);
        wing_right.setTextureOffset(21, 21).addBox(-1.0F, 0.0F, -4.0F, 1.0F, 12.0F, 6.0F, 0.0F, true);

        leg_left = new AdvancedModelBox(this, "leg_left");
        leg_left.setPos(2.0F, 2.3F, -2.0F);
        body.addChild(leg_left);
        setRotationAngle(leg_left, 0.6109F, 0.0F, 0.0F);
        leg_left.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, -11.0F, 3.0F, 3.0F, 11.0F, 0.0F, false);

        leg_right = new AdvancedModelBox(this, "leg_right");
        leg_right.setPos(-2.0F, 2.3F, -2.0F);
        body.addChild(leg_right);
        setRotationAngle(leg_right, 0.6109F, 0.0F, 0.0F);
        leg_right.setTextureOffset(0, 0).addBox(-1.0F, -3.0F, -11.0F, 3.0F, 3.0F, 11.0F, 0.0F, true);

        neck = new AdvancedModelBox(this, "neck");
        neck.setPos(0.0F, -4.8F, 1.0F);
        body.addChild(neck);
        setRotationAngle(neck, -0.7418F, 0.0F, 0.0F);
        neck.setTextureOffset(35, 0).addBox(-1.5F, -4.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, -3.8F, -0.3F);
        neck.addChild(head);
        head.setTextureOffset(25, 11).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);

        crest = new AdvancedModelBox(this, "crest");
        crest.setPos(0.0F, -2.0F, 2.0F);
        head.addChild(crest);
        crest.setTextureOffset(0, 0).addBox(0.0F, -2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, false);

        beak = new AdvancedModelBox(this, "beak");
        beak.setPos(0.0F, -2.9F, -2.0F);
        head.addChild(beak);
        setRotationAngle(beak, 0.3491F, 0.0F, 0.0F);
        beak.setTextureOffset(18, 0).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 5.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this, "jaw");
        jaw.setPos(0.0F, 2.0F, 0.0F);
        beak.addChild(jaw);
        setRotationAngle(jaw, -0.1745F, 0.0F, 0.0F);
        jaw.setTextureOffset(41, 36).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 1.0F, 5.0F, -0.1F, false);
        this.updateDefaultPose();
        animator = new ModelAnimator();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityShoebill.ANIMATION_FISH);
        animator.startKeyframe(15);
        animator.rotate(neck, (float)Math.toRadians(-40), 0, 0);
        animator.rotate(head, (float)Math.toRadians(40), 0, 0);
        animator.move(head, 0, 0.5F, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, (float)Math.toRadians(40), 0, 0);
        animator.rotate(leg_left, (float)Math.toRadians(-40), 0, 0);
        animator.rotate(leg_right, (float)Math.toRadians(-40), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(70), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-50), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(20), 0, 0);
        animator.move(body, 0, 1F, 0);
        animator.move(neck, 0, 0, -3F);
        animator.move(head, 0, 0F, -2F);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(5);
        animator.setAnimation(EntityShoebill.ANIMATION_BEAKSHAKE);
        animator.startKeyframe(4);
        animator.rotate(crest, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(-40), (float)Math.toRadians(10), 0);
        animator.rotate(head, (float)Math.toRadians(40), (float)Math.toRadians(40), 0);
        animator.move(head, 0, 0.5F, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(crest, (float)Math.toRadians(30), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(-40), (float)Math.toRadians(-10), 0);
        animator.rotate(head, (float)Math.toRadians(40), (float)Math.toRadians(-40), 0);
        animator.move(head, 0, 0.5F, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(crest, (float)Math.toRadians(30), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(-40), (float)Math.toRadians(10), 0);
        animator.rotate(head, (float)Math.toRadians(40), (float)Math.toRadians(40), 0);
        animator.move(head, 0, 0.5F, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(crest, (float)Math.toRadians(30), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(-40), (float)Math.toRadians(-10), 0);
        animator.rotate(head, (float)Math.toRadians(40), (float)Math.toRadians(-40), 0);
        animator.move(head, 0, 0.5F, 0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
        animator.setAnimation(EntityShoebill.ANIMATION_ATTACK);
        animator.startKeyframe(5);
        animator.rotate(crest, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(-40), 0, 0);
        animator.rotate(head, (float)Math.toRadians(40), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(30), 0, 0);
        animator.move(head, 0, 0.5F, 0);
        animator.move(crest, 0, -0.5F, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, (float)Math.toRadians(70), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-80), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(5), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leg_left, leg_right, wing_left, wing_right, tail, head, neck, crest, beak, jaw, wing_left_pivot, wing_right_pivot);
    }

    @Override
    public void setupAnim(EntityShoebill entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.4F;
        float idleSpeed = 0.05F;
        float idleDegree = 0.2F;
        float flapSpeed = 0.4F;
        float flapDegree = 0.2F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
        float scaledLimbSwing = Math.min(1.0F, limbSwingAmount * 1.6F);
        float runProgress = Math.max(5F * scaledLimbSwing - flyProgress, 0);
        progressRotationPrev(body, runProgress, (float) Math.toRadians(25), 0, 0, 5F);
        progressRotationPrev(leg_right, runProgress, (float) Math.toRadians(-25), 0, 0, 5F);
        progressRotationPrev(leg_left, runProgress, (float) Math.toRadians(-25), 0, 0, 5F);
        progressRotationPrev(neck, runProgress, (float) Math.toRadians(-55), 0, 0, 5F);
        progressRotationPrev(head, runProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(body, flyProgress, (float) Math.toRadians(35), 0, 0, 5F);
        progressRotationPrev(leg_right, flyProgress, (float) Math.toRadians(25), 0, 0, 5F);
        progressRotationPrev(leg_left, flyProgress, (float) Math.toRadians(25), 0, 0, 5F);
        progressRotationPrev(wing_right, flyProgress, 0, (float) Math.toRadians(90), (float) Math.toRadians(80), 5F);
        progressRotationPrev(wing_left, flyProgress, 0, (float) Math.toRadians(-90), (float) Math.toRadians(-80), 5F);
        progressRotationPrev(neck, flyProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(head, flyProgress, (float) Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(tail, flyProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressPositionPrev(wing_right_pivot, flyProgress, 1F, 4F, 0, 5f);
        progressPositionPrev(wing_left_pivot, flyProgress, -1F, 4F, 0, 5f);
        progressPositionPrev(leg_right, flyProgress, 0, -1F, 0, 5f);
        progressPositionPrev(leg_left, flyProgress, 0, -1F, 0, 5f);
        progressPositionPrev(body, flyProgress, 0, 5F, 0, 5f);
        progressPositionPrev(head, flyProgress, 0, 1.5F, 0, 5f);

        this.walk(neck, idleSpeed, idleDegree, false, 1F, 0F, ageInTicks, 1);
        this.walk(head, -idleSpeed, idleDegree, false, 2F, 0F, ageInTicks, 1);
        this.flap(tail, idleSpeed * 2F, idleDegree * 0.5F, true, 0F, 0F, ageInTicks, 1);
        if(flyProgress > 0){
            this.walk(leg_right, walkSpeed, walkDegree * 0.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(leg_left, walkSpeed, walkDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.swing(wing_right_pivot, flapSpeed, flapDegree * 5, true, 0F, 0F, ageInTicks, 1);
            this.swing(wing_left_pivot, flapSpeed, flapDegree * 5, false, 0F, 0F, ageInTicks, 1);
            this.walk(neck, flapSpeed, flapDegree * 0.85F, false, 0F, 0.2F, ageInTicks, 1);
            this.walk(head, flapSpeed, flapDegree * 0.85F, true, 0F, 0F, ageInTicks, 1);
            this.bob(body, flapSpeed * 0.3F, flapDegree * 4, true, ageInTicks, 1);
        }else{
            this.walk(leg_right, walkSpeed, walkDegree * 1.85F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(leg_left, walkSpeed, walkDegree * 1.85F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(neck, walkSpeed, walkDegree * 0.85F, false, 2F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, walkDegree * 0.85F, true, 2F, 0F, limbSwing, limbSwingAmount);
            this.walk(tail, walkSpeed * 0.5F, walkDegree * 0.15F, true, -2F, 0.2F, limbSwing, limbSwingAmount);
        }
        this.head.rotateAngleY += netHeadYaw * ((float)Math.PI / 180F);

    }

}