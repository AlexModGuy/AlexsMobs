package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelCrocodile extends AdvancedEntityModel<EntityCrocodile> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox snout;
    private final AdvancedModelBox TeethLTop;
    private final AdvancedModelBox TeethRTop;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox jaw2;
    private final AdvancedModelBox TeethLBottom;
    private final AdvancedModelBox TeethRBottom;
    private final AdvancedModelBox LLegFront;
    private final AdvancedModelBox RLegFront;
    private final AdvancedModelBox LLegBack;
    private final AdvancedModelBox RLegBack;
    private final AdvancedModelBox Tail1;
    private final AdvancedModelBox Tail2;
    private final AdvancedModelBox Tail3;
    private ModelAnimator animator;

    public ModelCrocodile() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -9.375F, 0.125F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-8.0F, -4.625F, -14.125F, 16.0F, 11.0F, 35.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setPos(0.0F, 1.125F, -14.375F);
        body.addChild(neck);
        neck.setTextureOffset(70, 47).addBox(-6.0F, -4.75F, -8.75F, 12.0F, 9.0F, 9.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, -0.9167F, -8.4167F);
        neck.addChild(head);
        head.setTextureOffset(32, 90).addBox(-5.0F, -3.8333F, -9.3333F, 10.0F, 5.0F, 9.0F, 0.0F, false);

        snout = new AdvancedModelBox(this);
        snout.setPos(0.0F, 1.1667F, -1.3333F);
        head.addChild(snout);
        snout.setTextureOffset(0, 0).addBox(-3.0F, -4.0F, -19.0F, 6.0F, 4.0F, 11.0F, 0.0F, false);

        TeethLTop = new AdvancedModelBox(this);
        TeethLTop.setPos(2.1F, -0.4F, -13.4F);
        snout.addChild(TeethLTop);
        setRotationAngle(TeethLTop, 0.0F, 0.0F, -0.1309F);
        TeethLTop.setTextureOffset(0, 16).addBox(-2.1F, 0.1F, -5.5F, 3.0F, 2.0F, 11.0F, 0.0F, false);

        TeethRTop = new AdvancedModelBox(this);
        TeethRTop.setPos(-2.1F, -0.4F, -13.4F);
        snout.addChild(TeethRTop);
        setRotationAngle(TeethRTop, 0.0F, 0.0F, 0.1309F);
        TeethRTop.setTextureOffset(0, 16).addBox(-0.9F, 0.1F, -5.5F, 3.0F, 2.0F, 11.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this);
        jaw.setPos(0.0F, 0.9167F, -3.0833F);
        head.addChild(jaw);
        jaw.setTextureOffset(0, 78).addBox(-5.5F, -0.75F, -6.05F, 11.0F, 4.0F, 9.0F, 0.0F, false);

        jaw2 = new AdvancedModelBox(this);
        jaw2.setPos(0.5F, 6.25F, 1.75F);
        jaw.addChild(jaw2);
        jaw2.setTextureOffset(0, 93).addBox(-3.5F, -6.0F, -18.8F, 6.0F, 3.0F, 12.0F, 0.0F, false);

        TeethLBottom = new AdvancedModelBox(this);
        TeethLBottom.setPos(1.0F, -6.7F, -13.2F);
        jaw2.addChild(TeethLBottom);
        setRotationAngle(TeethLBottom, 0.0F, 0.0F, 0.1309F);
        TeethLBottom.setTextureOffset(93, 66).addBox(-1.4F, -0.9F, -5.5F, 3.0F, 2.0F, 11.0F, 0.0F, false);

        TeethRBottom = new AdvancedModelBox(this);
        TeethRBottom.setPos(-2.0F, -6.7F, -13.2F);
        jaw2.addChild(TeethRBottom);
        setRotationAngle(TeethRBottom, 0.0F, 0.0F, -0.1309F);
        TeethRBottom.setTextureOffset(93, 66).addBox(-1.6F, -0.9F, -5.5F, 3.0F, 2.0F, 11.0F, 0.0F, true);

        LLegFront = new AdvancedModelBox(this);
        LLegFront.setPos(8.0F, 0.875F, -9.625F);
        body.addChild(LLegFront);
        LLegFront.setTextureOffset(0, 47).addBox(-2.0F, -2.5F, -2.5F, 4.0F, 11.0F, 5.0F, 0.0F, false);

        RLegFront = new AdvancedModelBox(this);
        RLegFront.setPos(-8.0F, 0.875F, -9.625F);
        body.addChild(RLegFront);
        RLegFront.setTextureOffset(0, 47).addBox(-2.0F, -2.5F, -2.5F, 4.0F, 11.0F, 5.0F, 0.0F, true);

        LLegBack = new AdvancedModelBox(this);
        LLegBack.setPos(8.0F, 0.875F, 13.875F);
        body.addChild(LLegBack);
        LLegBack.setTextureOffset(65, 99).addBox(-2.0F, -2.5F, -3.0F, 4.0F, 11.0F, 6.0F, 0.0F, false);

        RLegBack = new AdvancedModelBox(this);
        RLegBack.setPos(-8.0F, 0.875F, 13.875F);
        body.addChild(RLegBack);
        RLegBack.setTextureOffset(65, 99).addBox(-2.0F, -2.5F, -3.0F, 4.0F, 11.0F, 6.0F, 0.0F, true);

        Tail1 = new AdvancedModelBox(this);
        Tail1.setPos(0.0F, 0.875F, 23.375F);
        body.addChild(Tail1);
        Tail1.setTextureOffset(0, 47).addBox(-5.0F, -4.5F, -2.5F, 10.0F, 10.0F, 20.0F, 0.0F, false);

        Tail2 = new AdvancedModelBox(this);
        Tail2.setPos(0.0F, 0.25F, 19.5F);
        Tail1.addChild(Tail2);
        Tail2.setTextureOffset(68, 0).addBox(-3.0F, -3.75F, -2.0F, 6.0F, 9.0F, 18.0F, 0.0F, false);
        Tail2.setTextureOffset(72, 102).addBox(-3.0F, -6.75F, -2.0F, 6.0F, 3.0F, 18.0F, 0.0F, false);

        Tail3 = new AdvancedModelBox(this);
        Tail3.setPos(0.0F, 1.25F, 17.0F);
        Tail2.addChild(Tail3);
        Tail3.setTextureOffset(70, 70).addBox(0.0F, -10.0F, -1.0F, 0.0F, 6.0F, 22.0F, 0.0F, false);
        Tail3.setTextureOffset(39, 56).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 8.0F, 22.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityCrocodile.ANIMATION_LUNGE);
        animator.startKeyframe(2);
        animator.move(body, 0, 0, 2);
        animator.rotate(head, (float)Math.toRadians(-55), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(60), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -14);
        animator.rotate(head, (float)Math.toRadians(-15), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(20), 0, 0);
        animator.rotate(RLegFront, (float)Math.toRadians(45), 0, 0);
        animator.rotate(LLegFront, (float)Math.toRadians(45), 0, 0);
        animator.rotate(RLegBack, (float)Math.toRadians(45), 0, 0);
        animator.rotate(LLegBack, (float)Math.toRadians(45), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, 0, 3);
        animator.rotate(head, (float)Math.toRadians(-10), 0, 0);
        animator.rotate(jaw, (float)Math.toRadians(15), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(EntityCrocodile.ANIMATION_DEATHROLL);
        animator.startKeyframe(30);
        int rolls = 3;
        animator.rotate(body, 0, 0, (float)Math.toRadians(-360 * rolls));
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
        float groundProgress = entityIn.prevGroundProgress + (entityIn.groundProgress - entityIn.prevGroundProgress) * partialTick;
        float swimProgress = entityIn.prevSwimProgress + (entityIn.swimProgress - entityIn.prevSwimProgress) * partialTick;
        float baskProgress = entityIn.prevBaskingProgress + (entityIn.baskingProgress - entityIn.prevBaskingProgress) * partialTick;
        float grabProgress = entityIn.prevGrabProgress + (entityIn.grabProgress - entityIn.prevGrabProgress) * partialTick;
        if (!swimAnimate && grabProgress <= 0) {
            this.faceTarget(netHeadYaw, headPitch, 2, neck, head);
        }
        progressRotationPrev(jaw, grabProgress, (float) Math.toRadians(30), 0, 0, 10F);
        progressRotationPrev(head, grabProgress, (float) Math.toRadians(-10), 0, 0, 10F);
        if (entityIn.baskingType == 0) {
            progressRotationPrev(body, baskProgress, 0, (float) Math.toRadians(-7), 0, 10F);
            progressRotationPrev(Tail1, baskProgress, 0, (float) Math.toRadians(30), 0, 10F);
            progressRotationPrev(Tail2, baskProgress, 0, (float) Math.toRadians(20), 0, 10F);
            progressRotationPrev(Tail3, baskProgress, 0, (float) Math.toRadians(30), 0, 10F);
            progressRotationPrev(neck, baskProgress, 0, (float) Math.toRadians(-10), 0, 10F);
            progressRotationPrev(head, baskProgress, (float) Math.toRadians(-60), (float) Math.toRadians(-10), 0, 10F);
            progressRotationPrev(jaw, baskProgress, (float) Math.toRadians(60), 0, 0, 10F);
        } else if (entityIn.baskingType == 1) {
            progressRotationPrev(body, baskProgress, 0, (float) Math.toRadians(7), 0, 10F);
            progressRotationPrev(Tail1, baskProgress, 0, (float) Math.toRadians(-30), 0, 10F);
            progressRotationPrev(Tail2, baskProgress, 0, (float) Math.toRadians(-20), 0, 10F);
            progressRotationPrev(Tail3, baskProgress, 0, (float) Math.toRadians(-30), 0, 10F);
            progressRotationPrev(neck, baskProgress, 0, (float) Math.toRadians(10), 0, 10F);
            progressRotationPrev(head, baskProgress, (float) Math.toRadians(-60), (float) Math.toRadians(10), 0, 10F);
            progressRotationPrev(jaw, baskProgress, (float) Math.toRadians(60), 0, 0, 10F);
        }
        progressPositionPrev(Tail1, baskProgress, 0, 0, -3, 10F);
        progressPositionPrev(head, baskProgress, 0, 3, -1, 10F);
        progressPositionPrev(body, groundProgress, 0, 2, 0, 10F);
        progressPositionPrev(neck, groundProgress, 0, 1, 0, 10F);
        progressPositionPrev(jaw, groundProgress, 0, 0, 1, 10F);
        progressPositionPrev(RLegFront, groundProgress, 0, 2, 0, 10F);
        progressPositionPrev(LLegFront, groundProgress, 0, 2, 0, 10F);
        progressPositionPrev(RLegBack, groundProgress, 0, 2, 0, 10F);
        progressPositionPrev(LLegBack, groundProgress, 0, 2, 0, 10F);
        progressRotationPrev(RLegFront, groundProgress, (float) Math.toRadians(-25), 0, (float) Math.toRadians(60), 10F);
        progressRotationPrev(LLegFront, groundProgress, (float) Math.toRadians(-25), 0, (float) Math.toRadians(-60), 10F);
        progressRotationPrev(RLegBack, groundProgress, (float) Math.toRadians(25), 0, (float) Math.toRadians(60), 10F);
        progressRotationPrev(LLegBack, groundProgress, (float) Math.toRadians(25), 0, (float) Math.toRadians(-60), 10F);
        progressPositionPrev(RLegFront, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(RLegFront, swimProgress, (float) Math.toRadians(75), 0, (float) Math.toRadians(90), 10F);
        progressPositionPrev(LLegFront, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(LLegFront, swimProgress, (float) Math.toRadians(75), 0, (float) Math.toRadians(-90), 10F);
        progressPositionPrev(RLegBack, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(RLegBack, swimProgress, (float) Math.toRadians(75), 0, (float) Math.toRadians(90), 10F);
        progressPositionPrev(LLegBack, swimProgress, 0, 2, 0, 10F);
        progressRotationPrev(LLegBack, swimProgress, (float) Math.toRadians(75), 0, (float) Math.toRadians(-90), 10F);
        AdvancedModelBox[] tailBoxes = new AdvancedModelBox[]{Tail1, Tail2, Tail3};
        if (swimAnimate) {
            this.walk(RLegFront, swimSpeed, swimDegree, false, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.walk(LLegFront, swimSpeed, swimDegree, false, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.walk(RLegBack, swimSpeed, swimDegree, true, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.walk(LLegBack, swimSpeed, swimDegree, true, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.swing(body, swimSpeed, swimDegree * 0.7F, false, 3F, 0F, limbSwing, limbSwingAmount);
            this.swing(neck, swimSpeed, swimDegree * 0.5F, true, 2F, 0F, limbSwing, limbSwingAmount);
            this.swing(head, swimSpeed, swimDegree * 0.3F, true, 2F, 0F, limbSwing, limbSwingAmount);
            this.chainSwing(tailBoxes, swimSpeed, swimDegree * 2F, -2.5F, limbSwing, limbSwingAmount);
        } else {
            this.walk(RLegFront, walkSpeed, walkDegree, false, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.walk(LLegFront, walkSpeed, walkDegree, true, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.walk(RLegBack, walkSpeed, walkDegree, true, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.walk(LLegBack, walkSpeed, walkDegree, false, 0F, -0.25F, limbSwing, limbSwingAmount);
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
        return ImmutableList.of(root, body, neck, head, snout, TeethLTop, TeethRTop, jaw, jaw2, TeethLBottom, TeethRBottom, LLegFront, RLegFront, LLegBack, RLegBack, Tail1, Tail2, Tail3);
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