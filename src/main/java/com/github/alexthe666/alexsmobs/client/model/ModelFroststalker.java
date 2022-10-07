package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.alexsmobs.entity.EntityGazelle;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelFroststalker extends AdvancedEntityModel<EntityFroststalker> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox icespikesleft;
    private final AdvancedModelBox icespikesright;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox horn;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox armleft;
    private final AdvancedModelBox armright;
    private final AdvancedModelBox legleft;
    private final AdvancedModelBox legright;
    private ModelAnimator animator;

    public ModelFroststalker() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -11.0F, 2.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 7.0F, 16.0F, 0.0F, false);

        tail1 = new AdvancedModelBox(this, "tail1");
        tail1.setRotationPoint(0.0F, -1.0F, 5.0F);
        body.addChild(tail1);
        tail1.setTextureOffset(34, 24).addBox(-2.0F, -3.0F, 1.0F, 4.0F, 5.0F, 9.0F, 0.0F, false);
        tail1.setTextureOffset(0, 41).addBox(-1.0F, -5.0F, 1.0F, 2.0F, 2.0F, 9.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this, "tail2");
        tail2.setRotationPoint(0.0F, -2.0F, 9.0F);
        tail1.addChild(tail2);
        tail2.setTextureOffset(29, 0).addBox(-1.0F, -1.0F, 1.0F, 2.0F, 3.0F, 11.0F, 0.0F, false);
        tail2.setTextureOffset(21, 29).addBox(0.0F, -7.0F, 1.0F, 0.0F, 8.0F, 12.0F, 0.0F, false);

        icespikesleft = new AdvancedModelBox(this, "icespikesleft");
        icespikesleft.setRotationPoint(0.0F, -4.0F, 0.5F);
        body.addChild(icespikesleft);
        setRotationAngle(icespikesleft, 0.0F, -0.3927F, 0.0F);
        icespikesleft.setTextureOffset(35, 39).addBox(0.0F, -6.0F, -5.5F, 0.0F, 6.0F, 11.0F, 0.0F, false);

        icespikesright = new AdvancedModelBox(this, "icespikesright");
        icespikesright.setRotationPoint(0.0F, -4.0F, 0.5F);
        body.addChild(icespikesright);
        setRotationAngle(icespikesright, 0.0F, 0.3927F, 0.0F);
        icespikesright.setTextureOffset(35, 39).addBox(0.0F, -6.0F, -5.5F, 0.0F, 6.0F, 11.0F, 0.0F, true);

        neck = new AdvancedModelBox(this, "neck");
        neck.setRotationPoint(0.0F, -2.0F, -8.0F);
        body.addChild(neck);
        neck.setTextureOffset(52, 18).addBox(-1.5F, -4.0F, -3.0F, 3.0F, 6.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -4.0F, -1.0F);
        neck.addChild(head);
        head.setTextureOffset(44, 62).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(5, 29).addBox(-2.0F, -4.0F, -9.0F, 4.0F, 3.0F, 7.0F, 0.0F, false);
        head.setTextureOffset(17, 50).addBox(-1.0F, -6.0F, 0.0F, 2.0F, 6.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(47, 39).addBox(-2.0F, -1.0F, -9.0F, 4.0F, 2.0F, 7.0F, 0.0F, false);

        horn = new AdvancedModelBox(this, "horn");
        horn.setRotationPoint(0.0F, -3.0F, -3.0F);
        head.addChild(horn);
        setRotationAngle(horn, -0.3927F, 0.0F, 0.0F);
        horn.setTextureOffset(47, 6).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 9.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this, "jaw");
        jaw.setRotationPoint(0.0F, -1.0F, -2.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(40, 79).addBox(-1.5F, 0.0F, -7.0F, 3.0F, 1.0F, 7.0F, 0.0F, false);

        armleft = new AdvancedModelBox(this, "armleft");
        armleft.setRotationPoint(3.0F, 2.0F, -7.5F);
        body.addChild(armleft);
        armleft.setTextureOffset(0, 53).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, false);
        armleft.setTextureOffset(0, 24).addBox(-3.0F, 7.0F, -1.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        armright = new AdvancedModelBox(this, "armright");
        armright.setRotationPoint(-3.0F, 2.0F, -7.5F);
        body.addChild(armright);
        armright.setTextureOffset(0, 53).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, 0.0F, true);
        armright.setTextureOffset(0, 24).addBox(1.0F, 7.0F, -1.5F, 2.0F, 2.0F, 2.0F, 0.0F, true);

        legleft = new AdvancedModelBox(this, "legleft");
        legleft.setRotationPoint(1.5F, 2.0F, 3.9F);
        body.addChild(legleft);
        legleft.setTextureOffset(0, 0).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.0F, false);

        legright = new AdvancedModelBox(this, "legright");
        legright.setRotationPoint(-1.5F, 2.0F, 3.9F);
        body.addChild(legright);
        legright.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            horn.showModel = false;
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            horn.showModel = true;
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        animator.update(entity);
        animator.setAnimation(EntityFroststalker.ANIMATION_BITE);
        animator.startKeyframe(4);
        animator.rotate(neck, (float)Math.toRadians(-30), 0, 0);
        animator.rotate(head, (float)Math.toRadians(40), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(40), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, (float)Math.toRadians(50), 0, 0);
        animator.rotate(head, (float)Math.toRadians(-50), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityFroststalker.ANIMATION_SPEAK);
        animator.startKeyframe(3);
        animator.rotate(head, (float)Math.toRadians(5), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(25), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(3);
        animator.setAnimation(EntityFroststalker.ANIMATION_SLASH_L);
        animator.startKeyframe(5);
        animator.rotate(body, (float)Math.toRadians(-15), (float)Math.toRadians(-15), 0);
        animator.rotate(neck, (float)Math.toRadians(15), (float)Math.toRadians(15), 0);
        animator.rotate(legright, (float)Math.toRadians(15), (float)Math.toRadians(15), 0);
        animator.rotate(legleft, (float)Math.toRadians(15), (float)Math.toRadians(15), 0);
        animator.rotate(armleft, (float)Math.toRadians(-50), 0, (float)Math.toRadians(-105));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(head, (float)Math.toRadians(5), 0, 0);
        animator.move(armleft, 0, 0, -2);
        animator.rotate(armleft, (float)Math.toRadians(-90), 0, (float)Math.toRadians(15));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityFroststalker.ANIMATION_SLASH_R);
        animator.startKeyframe(5);
        animator.rotate(body, (float)Math.toRadians(-15), (float)Math.toRadians(15), 0);
        animator.rotate(neck, (float)Math.toRadians(15), (float)Math.toRadians(-15), 0);
        animator.rotate(legright, (float)Math.toRadians(15), (float)Math.toRadians(-15), 0);
        animator.rotate(legleft, (float)Math.toRadians(15), (float)Math.toRadians(-15), 0);
        animator.rotate(armright, (float)Math.toRadians(-50), 0, (float)Math.toRadians(105));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(head, (float)Math.toRadians(5), 0, 0);
        animator.move(armright, 0, 0, -2);
        animator.rotate(armright, (float)Math.toRadians(-90), 0, (float)Math.toRadians(-15));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityFroststalker.ANIMATION_SHOVE);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, 4);
        animator.move(legright, 0, 0, -1);
        animator.move(legleft, 0, 0, -1);
        animator.rotate(legright, (float)Math.toRadians(-25), 0, 0);
        animator.rotate(legleft, (float)Math.toRadians(-25), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(35), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.move(body, 0, 0, -7);
        animator.rotate(legright, (float)Math.toRadians(25), 0, 0);
        animator.rotate(legleft, (float)Math.toRadians(25), 0, 0);
        animator.rotate(neck, (float)Math.toRadians(25), 0, 0);
        animator.rotate(head, (float)Math.toRadians(15), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);

    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail1, tail2, icespikesleft, icespikesright, neck, head, horn, jaw, armleft, armright, legleft, legright);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityFroststalker entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.7F;
        float walkDegree = 0.4F;
        float spikeSpeed = 0.9F;
        float spikeDegree = 0.4F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.4F;
        float partialTick = ageInTicks - entityIn.tickCount;
        float turnAngle = entityIn.prevTurnAngle + (entityIn.getTurnAngle() - entityIn.prevTurnAngle) * partialTick;
        float bipedProgress = entityIn.prevBipedProgress + (entityIn.bipedProgress - entityIn.prevBipedProgress) * partialTick;
        float quadProgress = 5.0F - bipedProgress;
        float tackleProgress = entityIn.prevTackleProgress + (entityIn.tackleProgress - entityIn.prevTackleProgress) * partialTick;
        float spikeProgress = entityIn.prevSpikeShakeProgress + (entityIn.spikeShakeProgress - entityIn.prevSpikeShakeProgress) * partialTick;
        AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{tail1, tail2};
        this.chainSwing(tailBoxes, idleSpeed, idleDegree * 0.3F, -2F, ageInTicks, 1.0F);
        this.walk(neck, idleSpeed * 0.4F, idleDegree * 0.2F, false, 1F, -0.01F, ageInTicks, 1);
        this.walk(head, idleSpeed * 0.4F, idleDegree * 0.2F, true, 1F, -0.01F, ageInTicks, 1);
        this.chainSwing(tailBoxes, walkSpeed, walkDegree, -3, limbSwing, limbSwingAmount);
        this.walk(body, walkSpeed, walkDegree * 0.1F, false, -1F, 0F, limbSwing, limbSwingAmount * bipedProgress * 0.2F);
        this.walk(legleft, walkSpeed, walkDegree * 1.85F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.walk(legright, walkSpeed, walkDegree * 1.85F, true, 3F, 0F, limbSwing, limbSwingAmount);
        this.walk(armleft, walkSpeed, walkDegree * 1.85F, true, 2F, 0F, limbSwing, limbSwingAmount * quadProgress * 0.2F);
        this.walk(armright, walkSpeed, walkDegree * 1.85F, false, 2F, 0F, limbSwing, limbSwingAmount * quadProgress * 0.2F);
        this.bob(body, walkSpeed * 0.5F, walkDegree * 4F, true, limbSwing, limbSwingAmount * bipedProgress * 0.2F);
        progressRotationPrev(armright, bipedProgress, (float)Math.toRadians(20), (float)Math.toRadians(10), 0, 5F);
        progressRotationPrev(armleft, bipedProgress, (float)Math.toRadians(20), (float)Math.toRadians(-10), 0, 5F);
        progressRotationPrev(neck, bipedProgress, (float)Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(head, bipedProgress, (float)Math.toRadians(-30), 0, 0, 5F);
        progressPositionPrev(neck, bipedProgress, 0, 0, -1, 5F);
        progressPositionPrev(head, bipedProgress, 0, 0, -1, 5F);
        progressPositionPrev(tail1, quadProgress, 0, 0, -1, 5F);
        progressRotationPrev(tail1, quadProgress, (float)Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(tail2, quadProgress, (float)Math.toRadians(10), 0, 0, 5F);
        this.flap(neck, walkSpeed, walkDegree * 0.5F, false, 4F, 0, limbSwing, limbSwingAmount * quadProgress * 0.2F);
        this.flap(head, walkSpeed, walkDegree * 0.5F, true, 4F, 0, limbSwing, limbSwingAmount * quadProgress * 0.2F);
        this.walk(armleft, walkSpeed, walkDegree * 0.5F, true, 3F, 0.12F, limbSwing, limbSwingAmount * bipedProgress * 0.2F);
        this.walk(armright, walkSpeed, walkDegree * 0.5F, true, 3F, 0.12F, limbSwing, limbSwingAmount * bipedProgress * 0.2F);
        this.walk(neck, walkSpeed, walkDegree * 0.5F, false, 2F, -0.1F, limbSwing, limbSwingAmount * bipedProgress * 0.2F);
        this.walk(head, walkSpeed, walkDegree * 0.5F, true, 2F, -0.1F, limbSwing, limbSwingAmount * bipedProgress * 0.2F);
        this.armleft.rotationPointY -= bipedProgress * 0.5F;
        this.armright.rotationPointY -= bipedProgress * 0.5F;
        float yawAmount = turnAngle / 57.295776F * 0.5F * bipedProgress * 0.2F * limbSwingAmount;
        head.rotateAngleY -= yawAmount * 0.5F;
        neck.rotateAngleY -= yawAmount;
        body.rotateAngleZ += yawAmount;
        legleft.rotateAngleZ -= yawAmount;
        legright.rotationPointY += 1.5F * yawAmount;
        legleft.rotationPointY -= 1.5F * yawAmount;
        legright.rotateAngleZ -= yawAmount;
        progressRotationPrev(body, tackleProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(neck, tackleProgress, (float) Math.toRadians(25), 0, 0, 5F);
        progressRotationPrev(head, tackleProgress, (float) Math.toRadians(25), 0, 0, 5F);
        progressRotationPrev(armright, tackleProgress, (float) Math.toRadians(-50), 0, (float) Math.toRadians(40), 5F);
        progressRotationPrev(armleft, tackleProgress, (float) Math.toRadians(-50), 0, (float) Math.toRadians(-40), 5F);
        progressRotationPrev(legright, tackleProgress, (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(legleft, tackleProgress, (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(tail1, tackleProgress, (float) Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(jaw, tackleProgress, (float) Math.toRadians(20), 0, 0, 5F);
        this.chainSwing(tailBoxes, spikeSpeed, spikeDegree, 0, ageInTicks, spikeProgress * 0.2F);
        this.flap(body, spikeSpeed, spikeDegree, false, 0F, 0F, ageInTicks, spikeProgress * 0.2F);
        this.flap(neck, spikeSpeed, spikeDegree, false, 1F, 0F, ageInTicks, spikeProgress * 0.2F);
        this.flap(head, spikeSpeed, spikeDegree, true, 2F, 0F, ageInTicks, spikeProgress * 0.2F);
        this.swing(body, spikeSpeed, spikeDegree * 0.5F, false, 1F, 0F, ageInTicks, spikeProgress * 0.2F);
        this.flap(legleft, spikeSpeed, spikeDegree, true, 0F, 0F, ageInTicks, spikeProgress * 0.2F);
        this.flap(legright, spikeSpeed, spikeDegree, true, 0F, 0F, ageInTicks, spikeProgress * 0.2F);
        this.faceTarget(netHeadYaw, headPitch, 1.0F, head, neck);
    }
}