package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityGazelle;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public class ModelGazelle extends AdvancedEntityModel<EntityGazelle> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox earL;
    private final AdvancedModelBox earR;
    private final AdvancedModelBox snout;
    private final AdvancedModelBox hornL;
    private final AdvancedModelBox hornR;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox frontlegR;
    private final AdvancedModelBox frontlegL;
    private final AdvancedModelBox backlegL;
    private final AdvancedModelBox backlegR;
    private ModelAnimator animator;

    public ModelGazelle() {
        texWidth = 64;
        texHeight = 64;
        body = new AdvancedModelBox(this);
        body.setPos(0.0F, 20.8F, 0.0F);
        body.texOffs(0, 0).addBox(-4.0F, -16.8F, -9.0F, 8.0F, 8.0F, 18.0F, 0.0F, false);
        neck = new AdvancedModelBox(this);
        neck.setPos(0.0F, -14.8F, -8.0F);
        body.addChild(neck);
        setRotationAngle(neck, 0.2618F, 0.0F, 0.0F);
        neck.texOffs(0, 0).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 9.0F, 4.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, -7.0F, 0.0F);
        neck.addChild(head);
        setRotationAngle(head, -0.2618F, 0.0F, 0.0F);
        head.texOffs(0, 27).addBox(-2.5F, -4.0F, -3.0F, 5.0F, 5.0F, 5.0F, 0.0F, false);

        earL = new AdvancedModelBox(this);
        earL.setPos(1.5F, -3.3F, 0.5F);
        head.addChild(earL);
        setRotationAngle(earL, -0.2618F, -0.5236F, 0.6109F);
        earL.texOffs(0, 38).addBox(-0.5F, -3.7F, -0.5F, 2.0F, 4.0F, 1.0F, 0.0F, false);

        earR = new AdvancedModelBox(this);
        earR.setPos(-1.5F, -3.3F, 0.5F);
        head.addChild(earR);
        setRotationAngle(earR, -0.2618F, 0.5236F, -0.6109F);
        earR.texOffs(0, 38).addBox(-1.5F, -3.7F, -0.5F, 2.0F, 4.0F, 1.0F, 0.0F, true);

        snout = new AdvancedModelBox(this);
        snout.setPos(0.0F, -0.5F, -2.9F);
        head.addChild(snout);
        snout.texOffs(34, 27).addBox(-1.5F, -1.5F, -3.1F, 3.0F, 3.0F, 3.0F, 0.0F, false);

        hornL = new AdvancedModelBox(this);
        hornL.setPos(1.3F, -3.4F, -1.9F);
        head.addChild(hornL);
        setRotationAngle(hornL, -0.2618F, 0.0F, 0.2618F);
        hornL.texOffs(35, 0).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        hornR = new AdvancedModelBox(this);
        hornR.setPos(-1.3F, -3.4F, -1.9F);
        head.addChild(hornR);
        setRotationAngle(hornR, -0.2618F, 0.0F, -0.2618F);
        hornR.texOffs(35, 0).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setPos(0.0F, -13.8F, 9.0F);
        body.addChild(tail);
        setRotationAngle(tail, 0.3491F, 0.0F, 0.0F);
        tail.texOffs(35, 12).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 0.0F, 0.0F, false);

        frontlegR = new AdvancedModelBox(this);
        frontlegR.setPos(2.5F, -6.8F, -6.5F);
        body.addChild(frontlegR);
        frontlegR.texOffs(34, 34).addBox(-6.5F, -2.0F, -1.5F, 3.0F, 12.0F, 3.0F, 0.0F, true);

        frontlegL = new AdvancedModelBox(this);
        frontlegL.setPos(2.5F, -6.8F, -6.5F);
        body.addChild(frontlegL);
        frontlegL.texOffs(34, 34).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 12.0F, 3.0F, 0.0F, false);

        backlegL = new AdvancedModelBox(this);
        backlegL.setPos(2.5F, -7.8F, 7.5F);
        body.addChild(backlegL);
        backlegL.texOffs(21, 27).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 12.0F, 3.0F, 0.0F, false);

        backlegR = new AdvancedModelBox(this);
        backlegR.setPos(-2.5F, -7.8F, 7.5F);
        body.addChild(backlegR);
        backlegR.texOffs(21, 27).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 12.0F, 3.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        animator.update(entity);
        animator.setAnimation(EntityGazelle.ANIMATION_FLICK_TAIL);
        animator.startKeyframe(2);
        animator.rotate(tail, 0, 0, (float)Math.toRadians(50));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(tail, 0, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(tail, 0, 0, (float)Math.toRadians(-50));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(tail, 0, 0, (float)Math.toRadians(50));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(tail, 0, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(tail, 0, 0, (float)Math.toRadians(-50));
        animator.endKeyframe();
        animator.resetKeyframe(2);
        animator.setAnimation(EntityGazelle.ANIMATION_FLICK_EARS);
        animator.startKeyframe(2);
        animator.rotate(neck, (float)Math.toRadians(25), (float)Math.toRadians(20), 0);
        animator.rotate(head, (float)Math.toRadians(5), 0, (float)Math.toRadians(10));
        animator.rotate(body, 0, (float)Math.toRadians(5), 0);
        animator.rotate(earR, 0, (float)Math.toRadians(25), (float)Math.toRadians(40));
        animator.rotate(earL, 0, (float)Math.toRadians(-25), (float)Math.toRadians(-40));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float)Math.toRadians(25), (float)Math.toRadians(-20), 0);
        animator.rotate(head, (float)Math.toRadians(5), 0, (float)Math.toRadians(-10));
        animator.rotate(body, 0, (float)Math.toRadians(-5), 0);
        animator.rotate(earR, 0, 0, 0);
        animator.rotate(earL, 0, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float)Math.toRadians(25), (float)Math.toRadians(20), 0);
        animator.rotate(head, (float)Math.toRadians(5), 0, (float)Math.toRadians(10));
        animator.rotate(body, 0, (float)Math.toRadians(5), 0);
        animator.rotate(earR, 0, (float)Math.toRadians(5), (float)Math.toRadians(-40));
        animator.rotate(earL, 0, (float)Math.toRadians(-5), (float)Math.toRadians(40));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float)Math.toRadians(25), (float)Math.toRadians(-20), 0);
        animator.rotate(head, (float)Math.toRadians(5), 0, (float)Math.toRadians(-10));
        animator.rotate(earR, 0, (float)Math.toRadians(25), (float)Math.toRadians(40));
        animator.rotate(earL, 0, (float)Math.toRadians(-25), (float)Math.toRadians(-40));
        animator.rotate(body, 0, (float)Math.toRadians(-5), 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float)Math.toRadians(25), (float)Math.toRadians(20), 0);
        animator.rotate(head, (float)Math.toRadians(5), 0, (float)Math.toRadians(10));
        animator.rotate(body, 0, (float)Math.toRadians(5), 0);
        animator.rotate(earR, 0, 0, 0);
        animator.rotate(earL, 0, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, 0, 0, 0);
        animator.rotate(head, 0, 0, 0);
        animator.rotate(body, 0, (float)Math.toRadians(-5), 0);
        animator.rotate(earR, 0, (float)Math.toRadians(5), (float)Math.toRadians(-40));
        animator.rotate(earL, 0, (float)Math.toRadians(-5), (float)Math.toRadians(40));
        animator.endKeyframe();
        animator.resetKeyframe(7);
        animator.setAnimation(EntityGazelle.ANIMATION_EAT_GRASS);
        animator.startKeyframe(5);
        animator.rotate(neck, (float)Math.toRadians(100), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-40), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float)Math.toRadians(120), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-50), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float)Math.toRadians(100), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-40), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float)Math.toRadians(120), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-50), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float)Math.toRadians(100), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-40), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float)Math.toRadians(120), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-50), 0, 0);
        eatPose();
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    private void eatPose(){
        animator.rotate(body, (float)Math.toRadians(10), 0, 0);
        animator.move(body, 0, 2, 0);
        animator.rotate(backlegL, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(backlegR, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(frontlegL, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(frontlegR, (float)Math.toRadians(-10), 0, 0);
        animator.move(frontlegL, 0.1F, -3, 0F);
        animator.move(frontlegR, -0.1F, -3, 0F);
        animator.move(backlegL, 0, -1, 0);
        animator.move(backlegR, 0, -1, 0);
        animator.move(neck, 0, 1, 0);
    }

    @Override
    public void setupAnim(EntityGazelle entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        boolean running = entityIn.isRunning();
        float runSpeed = 0.7F;
        float runDegree = 0.7F;
        float walkSpeed = 0.7F;
        float walkDegree = 0.4F;
        float idleSpeed = 0.05F;
        float idleDegree = 0.1F;
        this.faceTarget(netHeadYaw, headPitch, 2, neck, head);

        this.walk(neck, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1);
        this.flap(tail, idleSpeed * 3, idleDegree, false, 1F, 0F, ageInTicks, 1);
        this.walk(head, idleSpeed, -idleDegree, false, 0.5F, 0F, ageInTicks, 1);
        if(running){
            this.walk(body, runSpeed, runDegree * 0.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(neck, runSpeed, runDegree * 0.2F, true, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(frontlegR, runSpeed, runDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(frontlegL, runSpeed, runDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.bob(frontlegR, runSpeed, runDegree * 2F, true, limbSwing, limbSwingAmount);
            this.bob(frontlegL, runSpeed, runDegree * 2F, true, limbSwing, limbSwingAmount);
            this.walk(backlegR, runSpeed, runDegree * 1.2F, false, 0, 0F, limbSwing, limbSwingAmount);
            this.walk(backlegL, runSpeed, runDegree * 1.2F, false, 0, 0F, limbSwing, limbSwingAmount);
            this.flap(backlegR, runSpeed, runDegree * 0.2F, true, 0, -0.2F, limbSwing, limbSwingAmount);
            this.flap(backlegL, runSpeed, runDegree * 0.2F, false, 0, -0.2F, limbSwing, limbSwingAmount);
            this.bob(body, runSpeed, runDegree * 8F, false, limbSwing, limbSwingAmount);
        }else{
            this.walk(body, walkSpeed, walkDegree * 0.05F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(neck, walkSpeed, walkDegree * 0.5F, true, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, -walkDegree * 0.5F, true, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(frontlegR, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(frontlegL, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(backlegR, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(backlegL, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);

        }
        //previously the render function, render code was moved to a method below
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.75F;
            head.setScale(f, f, f);
            hornL.setScale(0.4F, 0.4F, 0.4F);
            hornR.setScale(0.4F, 0.4F, 0.4F);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
            hornL.setScale(1, 1, 1);
            hornR.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, neck, head, earL, earR, backlegL, backlegR, frontlegL, frontlegR, snout, hornL, hornR, tail);
    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.xRot = x;
        advancedModelBox.yRot = y;
        advancedModelBox.zRot = z;
    }
}