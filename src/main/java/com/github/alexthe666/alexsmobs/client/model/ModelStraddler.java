package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class ModelStraddler extends AdvancedEntityModel<EntityStraddler> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox hair;
    public final AdvancedModelBox horn_left;
    public final AdvancedModelBox hair_left;
    public final AdvancedModelBox horn_right;
    public final AdvancedModelBox hair_right;
    public final AdvancedModelBox leg_left;
    public final AdvancedModelBox leg_right;
    private ModelAnimator animator;

    public ModelStraddler() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -14.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-14.0F, -12.0F, -7.0F, 28.0F, 11.0F, 14.0F, 0.0F, false);

        hair = new AdvancedModelBox(this, "hair");
        hair.setPos(0.0F, -13.0F, 0.0F);
        body.addChild(hair);
        hair.setTextureOffset(23, 26).addBox(-6.0F, -4.0F, 0.0F, 12.0F, 5.0F, 0.0F, 0.0F, false);

        horn_left = new AdvancedModelBox(this, "horn_left");
        horn_left.setPos(9.5F, -12.0F, -4.0F);
        body.addChild(horn_left);
        horn_left.setTextureOffset(0, 26).addBox(-2.5F, -18.0F, 0.0F, 6.0F, 18.0F, 10.0F, 0.0F, false);

        hair_left = new AdvancedModelBox(this, "hair_left");
        hair_left.setPos(-2.5F, -17.0F, 10.0F);
        horn_left.addChild(hair_left);
        setRotationAngle(hair_left, -0.5672F, -0.2618F, 0.0F);
        hair_left.setTextureOffset(33, 33).addBox(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 16.0F, 0.0F, false);

        horn_right = new AdvancedModelBox(this, "horn_right");
        horn_right.setPos(-9.5F, -12.0F, -4.0F);
        body.addChild(horn_right);
        horn_right.setTextureOffset(0, 26).addBox(-3.5F, -18.0F, 0.0F, 6.0F, 18.0F, 10.0F, 0.0F, true);

        hair_right = new AdvancedModelBox(this, "hair_right");
        hair_right.setPos(2.5F, -17.0F, 10.0F);
        horn_right.addChild(hair_right);
        setRotationAngle(hair_right, -0.5672F, 0.2618F, 0.0F);
        hair_right.setTextureOffset(33, 33).addBox(0.0F, 0.0F, 0.0F, 0.0F, 6.0F, 16.0F, 0.0F, true);

        leg_left = new AdvancedModelBox(this, "leg_left");
        leg_left.setPos(7.0F, -0.5F, 0.0F);
        body.addChild(leg_left);
        leg_left.setTextureOffset(50, 26).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 15.0F, 6.0F, 0.0F, false);

        leg_right = new AdvancedModelBox(this, "leg_right");
        leg_right.setPos(-7.0F, -0.5F, 0.0F);
        body.addChild(leg_right);
        leg_right.setTextureOffset(50, 26).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 15.0F, 6.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityStraddler.ANIMATION_LAUNCH);
        animator.startKeyframe(5);
        animator.rotate(body, Maths.rad(-5F), 0, 0);
        animator.rotate(leg_right, Maths.rad(5F), 0, 0);
        animator.rotate(leg_left, Maths.rad(5F), 0, 0);
        animator.rotate(horn_right, 0, 0, Maths.rad(-10F));
        animator.rotate(horn_left, 0, 0, Maths.rad(10F));
        animator.rotate(hair_left, 0, Maths.rad(-70F), 0);
        animator.rotate(hair_right, 0, Maths.rad(70F), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(10);
        animator.rotate(body, Maths.rad(-5F), 0, 0);
        animator.rotate(leg_right, Maths.rad(5F), 0, 0);
        animator.rotate(leg_left, Maths.rad(5F), 0, 0);
        animator.rotate(horn_right, Maths.rad(-30F), 0, Maths.rad(-10F));
        animator.rotate(horn_left, Maths.rad(-30F), 0, Maths.rad(10F));
        animator.rotate(hair_left, Maths.rad(20F), Maths.rad(-15F), 0);
        animator.rotate(hair_right, Maths.rad(20F), Maths.rad(15F), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, Maths.rad(25F), 0, 0);
        animator.rotate(leg_right, Maths.rad(-25F), 0, 0);
        animator.rotate(leg_left, Maths.rad(-25F), 0, 0);
        animator.rotate(horn_right, Maths.rad(20F), 0, Maths.rad(0F));
        animator.rotate(horn_left, Maths.rad(20F), 0, Maths.rad(0F));
        animator.rotate(hair_left, Maths.rad(20F), Maths.rad(-160F), 0);
        animator.rotate(hair_right, Maths.rad(20F), Maths.rad(160F), 0);
        animator.move(horn_left, 0, 2.4F, 0);
        animator.move(horn_right, 0, 2.4F, 0);
        animator.endKeyframe();

        animator.resetKeyframe(5);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, hair, hair_left, hair_right, horn_left, horn_right, leg_left, leg_right);
    }

    @Override
    public void setupAnim(EntityStraddler entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.5F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        this.walk(hair_left, idleSpeed, idleDegree, false, 0, -0.1F, ageInTicks, 1);
        this.swing(hair_left, idleSpeed, idleDegree, false, 1, 0F, ageInTicks, 1);
        this.walk(hair_right, idleSpeed, idleDegree, false, 0, -0.1F, ageInTicks, 1);
        this.swing(hair_right, idleSpeed, idleDegree, true, 1, 0F, ageInTicks, 1);
        this.walk(hair, idleSpeed, idleDegree, false, 3, 0F, ageInTicks, 1);
        this.walk(leg_right, walkSpeed, walkDegree * 1.5F, false, 0, 0F, limbSwing, limbSwingAmount);
        this.walk(leg_left, walkSpeed, walkDegree * 1.5F, true, 0, 0F, limbSwing, limbSwingAmount);
        this.swing(body, walkSpeed, walkDegree * 0.3F, false, 0, 0F, limbSwing, limbSwingAmount);
        this.flap(body, walkSpeed, walkDegree * 0.3F, false, -2, 0F, limbSwing, limbSwingAmount);
        this.walk(body, walkSpeed, walkDegree * 0.3F, false, -1, 0F, limbSwing, limbSwingAmount);
        this.swing(hair_left, walkSpeed, walkDegree * 0.8F, false, -1, 0F, limbSwing, limbSwingAmount);
        this.swing(hair_right, walkSpeed, walkDegree * 0.8F, false, -1, 0F, limbSwing, limbSwingAmount);

        if (entity.getPassengers().size() <= 0) {
            this.body.rotateAngleX += headPitch * 0.5F * Mth.DEG_TO_RAD;
            this.leg_right.rotateAngleX -= headPitch * 0.5F * Mth.DEG_TO_RAD;
            this.leg_left.rotateAngleX -= headPitch * 0.5F * Mth.DEG_TO_RAD;
        }
    }


    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}