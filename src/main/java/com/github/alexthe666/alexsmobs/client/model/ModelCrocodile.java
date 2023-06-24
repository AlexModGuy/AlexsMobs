package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelCrocodile extends AdvancedEntityModel<EntityCrocodile> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox left_foot;
    private final AdvancedModelBox right_leg;
    private final AdvancedModelBox right_foot;
    private final AdvancedModelBox left_arm;
    private final AdvancedModelBox left_hand;
    private final AdvancedModelBox right_arm;
    private final AdvancedModelBox right_hand;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tail3;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox crown;
    private final AdvancedModelBox left_upperteeth;
    private final AdvancedModelBox right_upperteeth;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox left_lowerteeth;
    private final AdvancedModelBox right_lowerteeth;
    private ModelAnimator animator;

    public ModelCrocodile() {
        texWidth = 256;
        texHeight = 256;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -9.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 12.0F, 27.0F, 0.0F, false);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(8.0F, 3.0F, 10.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -5.0F, 5.0F, 8.0F, 8.0F, 0.0F, false);

        left_foot = new AdvancedModelBox(this, "left_foot");
        left_foot.setRotationPoint(2.0F, 6.0F, -3.0F);
        left_leg.addChild(left_foot);
        left_foot.setTextureOffset(45, 42).addBox(-2.0F, -0.01F, -5.0F, 5.0F, 0.0F, 6.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-8.0F, 3.0F, 10.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(0, 0).addBox(-3.0F, -2.0F, -5.0F, 5.0F, 8.0F, 8.0F, 0.0F, true);

        right_foot = new AdvancedModelBox(this, "right_foot");
        right_foot.setRotationPoint(-2.0F, 6.0F, -3.0F);
        right_leg.addChild(right_foot);
        right_foot.setTextureOffset(45, 42).addBox(-3.0F, -0.01F, -5.0F, 5.0F, 0.0F, 6.0F, 0.0F, true);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(9.0F, 1.0F, -9.0F);
        body.addChild(left_arm);
        left_arm.setTextureOffset(0, 40).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, 0.0F, false);

        left_hand = new AdvancedModelBox(this, "left_hand");
        left_hand.setRotationPoint(0.0F, 8.0F, 1.0F);
        left_arm.addChild(left_hand);
        left_hand.setTextureOffset(0, 17).addBox(-2.0F, -0.01F, -7.0F, 6.0F, 0.0F, 7.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-9.0F, 1.0F, -9.0F);
        body.addChild(right_arm);
        right_arm.setTextureOffset(0, 40).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, 0.0F, true);

        right_hand = new AdvancedModelBox(this, "right_hand");
        right_hand.setRotationPoint(0.0F, 8.0F, 1.0F);
        right_arm.addChild(right_hand);
        right_hand.setTextureOffset(0, 17).addBox(-4.0F, -0.01F, -7.0F, 6.0F, 0.0F, 7.0F, 0.0F, true);

        tail1 = new AdvancedModelBox(this, "tail1");
        tail1.setRotationPoint(0.0F, 0.0F, 16.0F);
        body.addChild(tail1);
        tail1.setTextureOffset(0, 40).addBox(-5.0F, -5.0F, -2.0F, 10.0F, 10.0F, 24.0F, 0.0F, false);
        tail1.setTextureOffset(45, 51).addBox(-5.0F, -7.0F, -2.0F, 10.0F, 2.0F, 24.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this, "tail2");
        tail2.setRotationPoint(0.0F, 1.0F, 24.0F);
        tail1.addChild(tail2);
        tail2.setTextureOffset(62, 15).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 7.0F, 25.0F, 0.0F, false);
        tail2.setTextureOffset(43, 78).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 2.0F, 20.0F, 0.0F, false);

        tail3 = new AdvancedModelBox(this, "tail3");
        tail3.setRotationPoint(0.0F, 0.0F, 18.0F);
        tail2.addChild(tail3);
        tail3.setTextureOffset(0, 75).addBox(0.0F, -6.0F, 0.0F, 0.0F, 10.0F, 21.0F, 0.0F, false);

        neck = new AdvancedModelBox(this, "neck");
        neck.setRotationPoint(0.0F, 0.0F, -15.0F);
        body.addChild(neck);
        neck.setTextureOffset(80, 89).addBox(-6.0F, -5.0F, -10.0F, 12.0F, 10.0F, 12.0F, 0.0F, false);
        neck.setTextureOffset(60, 0).addBox(-4.0F, -6.0F, -10.0F, 8.0F, 1.0F, 12.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, 1.0F, -11.0F);
        neck.addChild(head);
        head.setTextureOffset(72, 78).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(60, 14).addBox(-4.0F, -5.0F, -5.0F, 8.0F, 1.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(22, 78).addBox(-3.0F, -4.0F, -17.0F, 6.0F, 4.0F, 12.0F, 0.0F, false);

        crown = new AdvancedModelBox(this, "crown");
        crown.setRotationPoint(0.0F, -5.0F, -2.0F);
        head.addChild(crown);
        crown.setTextureOffset(49, 54).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        left_upperteeth = new AdvancedModelBox(this, "left_upperteeth");
        left_upperteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        head.addChild(left_upperteeth);
        setRotationAngle(left_upperteeth, 0.0F, 0.0F, -0.0873F);
        left_upperteeth.setTextureOffset(104, 23).addBox(0.0F, 0.0F, -0.025F, 3.0F, 2.0F, 11.0F, 0.0F, false);

        right_upperteeth = new AdvancedModelBox(this, "right_upperteeth");
        right_upperteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        head.addChild(right_upperteeth);
        setRotationAngle(right_upperteeth, 0.0F, 0.0F, 0.0873F);
        right_upperteeth.setTextureOffset(104, 23).addBox(-3.0F, 0.0F, -0.025F, 3.0F, 2.0F, 11.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this, "jaw");
        jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(100, 7).addBox(-5.5F, -2.0F, -6.0F, 11.0F, 5.0F, 7.0F, 0.001F, false);
        jaw.setTextureOffset(90, 48).addBox(-3.0F, 0.0F, -17.0F, 6.0F, 3.0F, 11.0F, 0.0F, false);

        left_lowerteeth = new AdvancedModelBox(this, "left_lowerteeth");
        left_lowerteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        jaw.addChild(left_lowerteeth);
        setRotationAngle(left_lowerteeth, 0.0F, 0.0F, 0.0873F);
        left_lowerteeth.setTextureOffset(105, 67).addBox(0.0F, -2.0F, -0.025F, 3.0F, 2.0F, 11.0F, 0.0F, false);

        right_lowerteeth = new AdvancedModelBox(this, "right_lowerteeth");
        right_lowerteeth.setRotationPoint(0.0F, 0.0F, -17.0F);
        jaw.addChild(right_lowerteeth);
        setRotationAngle(right_lowerteeth, 0.0F, 0.0F, -0.0873F);
        right_lowerteeth.setTextureOffset(105, 67).addBox(-3.0F, -2.0F, -0.025F, 3.0F, 2.0F, 11.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityCrocodile.ANIMATION_LUNGE);
        animator.startKeyframe(2);
        animator.move(body, 0, 0, 2);
        animator.rotate(head, Maths.rad(-55), 0, 0);
        animator.rotate(jaw, Maths.rad(60), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -14);
        animator.rotate(head, Maths.rad(-15), 0, 0);
        animator.rotate(jaw, Maths.rad(20), 0, 0);
        animator.rotate(right_arm, Maths.rad(45), 0, 0);
        animator.rotate(left_arm, Maths.rad(45), 0, 0);
        animator.rotate(right_leg, Maths.rad(45), 0, 0);
        animator.rotate(left_leg, Maths.rad(45), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, 0, 3);
        animator.rotate(head, Maths.rad(-10), 0, 0);
        animator.rotate(jaw, Maths.rad(15), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(EntityCrocodile.ANIMATION_DEATHROLL);
        animator.startKeyframe(30);
        int rolls = 3;
        animator.rotate(body, 0, 0, Maths.rad(-360 * rolls));
        animator.endKeyframe();

    }

    @Override
    public void setupAnim(EntityCrocodile entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        boolean swimAnimate = entityIn.isInWater();
        float walkSpeed = 0.7F;
        float walkDegree = 0.7F;
        float swimSpeed = 1.0F;
        float swimDegree = 0.2F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float swimProgress = entityIn.prevSwimProgress + (entityIn.swimProgress - entityIn.prevSwimProgress) * partialTick;
        float baskProgress = entityIn.prevBaskingProgress + (entityIn.baskingProgress - entityIn.prevBaskingProgress) * partialTick;
        float grabProgress = entityIn.prevGrabProgress + (entityIn.grabProgress - entityIn.prevGrabProgress) * partialTick;
        if (!swimAnimate && grabProgress <= 0) {
            this.faceTarget(netHeadYaw, headPitch, 2, neck, head);
        }
        progressRotationPrev(jaw, grabProgress, Maths.rad(30), 0, 0, 10F);
        progressRotationPrev(head, grabProgress, Maths.rad(-10), 0, 0, 10F);
        if (entityIn.baskingType == 0) {
            progressRotationPrev(body, baskProgress, 0, Maths.rad(-7), 0, 10F);
            progressRotationPrev(tail1, baskProgress, 0, Maths.rad(30), 0, 10F);
            progressRotationPrev(tail2, baskProgress, 0, Maths.rad(20), 0, 10F);
            progressRotationPrev(tail3, baskProgress, 0, Maths.rad(30), 0, 10F);
            progressRotationPrev(neck, baskProgress, 0, Maths.rad(-10), 0, 10F);
            progressRotationPrev(head, baskProgress, Maths.rad(-60), Maths.rad(-10), 0, 10F);
            progressRotationPrev(jaw, baskProgress, Maths.rad(60), 0, 0, 10F);
        } else if (entityIn.baskingType == 1) {
            progressRotationPrev(body, baskProgress, 0, Maths.rad(7), 0, 10F);
            progressRotationPrev(tail1, baskProgress, 0, Maths.rad(-30), 0, 10F);
            progressRotationPrev(tail2, baskProgress, 0, Maths.rad(-20), 0, 10F);
            progressRotationPrev(tail3, baskProgress, 0, Maths.rad(-30), 0, 10F);
            progressRotationPrev(neck, baskProgress, 0, Maths.rad(10), 0, 10F);
            progressRotationPrev(head, baskProgress, Maths.rad(-60), Maths.rad(10), 0, 10F);
            progressRotationPrev(jaw, baskProgress, Maths.rad(60), 0, 0, 10F);
        }
        progressPositionPrev(body, baskProgress, 0, 3, -3, 10F);
        progressPositionPrev(tail1, baskProgress, 0, 0, -3, 10F);
        progressPositionPrev(right_leg, baskProgress, 0, -3, 0, 10F);
        progressPositionPrev(left_leg, baskProgress, 0, -3, 0, 10F);
        progressPositionPrev(right_arm, baskProgress, 0, -3, 0, 10F);
        progressPositionPrev(left_arm, baskProgress, 0, -3, 0, 10F);
        progressPositionPrev(right_arm, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(left_arm, baskProgress, 0, 0, Maths.rad(-30), 10F);
        progressRotationPrev(left_hand, baskProgress, 0, 0, Maths.rad(30), 10F);
        progressRotationPrev(right_arm, baskProgress, 0, 0, Maths.rad(30), 10F);
        progressRotationPrev(right_hand, baskProgress, 0, 0, Maths.rad(-30), 10F);
        progressRotationPrev(left_leg, baskProgress, 0, 0, Maths.rad(-30), 10F);
        progressRotationPrev(left_foot, baskProgress, 0, 0, Maths.rad(30), 10F);
        progressRotationPrev(right_leg, baskProgress, 0, 0, Maths.rad(30), 10F);
        progressRotationPrev(right_foot, baskProgress, 0, 0, Maths.rad(-30), 10F);

        progressRotationPrev(right_arm, swimProgress, Maths.rad(75), 0, Maths.rad(90), 10F);
        progressPositionPrev(left_arm, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(left_arm, swimProgress, Maths.rad(75), 0, Maths.rad(-90), 10F);
        progressPositionPrev(right_leg, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(right_leg, swimProgress, Maths.rad(75), 0, Maths.rad(90), 10F);
        progressPositionPrev(left_leg, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(left_leg, swimProgress, Maths.rad(75), 0, Maths.rad(-90), 10F);
        progressPositionPrev(left_foot, swimProgress, -2, 0, 0, 10F);
        progressRotationPrev(left_foot, swimProgress, Maths.rad(75), 0, 0, 10F);
        progressPositionPrev(right_foot, swimProgress, 2, 0, 0, 10F);
        progressRotationPrev(right_foot, swimProgress, Maths.rad(75), 0, 0, 10F);
        progressPositionPrev(left_hand, swimProgress, -1, 0, 0, 10F);
        progressRotationPrev(left_hand, swimProgress, Maths.rad(75), 0, 0, 10F);
        progressPositionPrev(right_hand, swimProgress, 1, 0, 0, 10F);
        progressRotationPrev(right_hand, swimProgress, Maths.rad(75), 0, 0, 10F);
        AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{tail1, tail2, tail3};
        if (swimAnimate) {
            this.walk(right_arm, swimSpeed, swimDegree, false, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.walk(left_arm, swimSpeed, swimDegree, false, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.walk(right_leg, swimSpeed, swimDegree, true, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.walk(left_leg, swimSpeed, swimDegree, true, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.swing(body, swimSpeed, swimDegree * 0.7F, false, 3F, 0F, limbSwing, limbSwingAmount);
            this.swing(neck, swimSpeed, swimDegree * 0.5F, true, 2F, 0F, limbSwing, limbSwingAmount);
            this.swing(head, swimSpeed, swimDegree * 0.3F, true, 2F, 0F, limbSwing, limbSwingAmount);
            this.chainSwing(tailBoxes, swimSpeed, swimDegree * 2F, -2.5F, limbSwing, limbSwingAmount);
        } else {
            this.walk(right_arm, walkSpeed, walkDegree, false, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.walk(left_arm, walkSpeed, walkDegree, true, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.walk(right_leg, walkSpeed, walkDegree, true, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.walk(left_leg, walkSpeed, walkDegree, false, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.swing(body, walkSpeed, walkDegree * 0.1F, false, 3F, 0F, limbSwing, limbSwingAmount);
            this.swing(neck, walkSpeed, walkDegree * 0.1F, false, 2F, 0F, limbSwing, limbSwingAmount);
            this.chainSwing(tailBoxes, walkSpeed, walkDegree * 0.3F, -2.5F, limbSwing, limbSwingAmount);
        }
        if (baskProgress > 0) {
            this.walk(head, 0.1F, 0.1F, false, 1F, 0.1F, ageInTicks, 1);
            this.jaw.rotateAngleX = -head.rotateAngleX;

        }
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, neck, head, jaw, left_arm, right_arm, left_leg, right_leg, tail1, tail2, tail3, crown, left_foot, right_foot, left_hand, right_hand, left_upperteeth, right_upperteeth, left_lowerteeth, right_lowerteeth);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.15F, 0.15F, 0.15F);
            matrixStackIn.translate(0.0D, 8.5D, 0.125D);
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

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}