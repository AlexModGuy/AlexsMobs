package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class ModelRaccoon extends AdvancedEntityModel<EntityRaccoon> {
    public AdvancedModelBox root;
    public AdvancedModelBox body;
    public AdvancedModelBox tail;
    public AdvancedModelBox arm_left;
    public AdvancedModelBox arm_right;
    public AdvancedModelBox leg_left;
    public AdvancedModelBox leg_right;
    public AdvancedModelBox head;
    public AdvancedModelBox ear_left;
    public AdvancedModelBox ear_right;
    public AdvancedModelBox snout;
    public ModelAnimator animator;

    public ModelRaccoon() {
        super();
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -11.0F, 0.5F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-5.5F, -4.0F, -7.5F, 11.0F, 8.0F, 15.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.5F, -1.0F, 7.5F);
        body.addChild(tail);
        tail.setTextureOffset(0, 24).addBox(-3.0F, -2.0F, 0.0F, 5.0F, 5.0F, 19.0F, 0.0F, false);

        arm_left = new AdvancedModelBox(this, "arm_left");
        arm_left.setPos(3.0F, 4.0F, -5.5F);
        body.addChild(arm_left);
        arm_left.setTextureOffset(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        arm_right = new AdvancedModelBox(this, "arm_right");
        arm_right.setPos(-3.0F, 4.0F, -5.5F);
        body.addChild(arm_right);
        arm_right.setTextureOffset(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, true);

        leg_left = new AdvancedModelBox(this, "leg_left");
        leg_left.setPos(3.0F, 4.0F, 6.5F);
        body.addChild(leg_left);
        leg_left.setTextureOffset(9, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        leg_right = new AdvancedModelBox(this, "leg_right");
        leg_right.setPos(-3.0F, 4.0F, 6.5F);
        body.addChild(leg_right);
        leg_right.setTextureOffset(9, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, true);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, 0.5F, -8.5F);
        body.addChild(head);
        head.setTextureOffset(30, 30).addBox(-4.5F, -4.0F, -4.0F, 9.0F, 7.0F, 5.0F, 0.0F, false);

        ear_left = new AdvancedModelBox(this, "ear_left");
        ear_left.setPos(3.5F, -4.0F, -2.0F);
        head.addChild(ear_left);
        ear_left.setTextureOffset(9, 24).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        ear_right = new AdvancedModelBox(this, "ear_right");
        ear_right.setPos(-3.5F, -4.0F, -2.0F);
        head.addChild(ear_right);
        ear_right.setTextureOffset(9, 24).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);

        snout = new AdvancedModelBox(this, "snout");
        snout.setPos(0.0F, 1.5F, -5.0F);
        head.addChild(snout);
        snout.setTextureOffset(0, 0).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 3.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, leg_left, leg_right, arm_left, arm_right, tail, head, ear_left, ear_right, snout);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityRaccoon.ANIMATION_ATTACK);
        animator.startKeyframe(3);
        animator.rotate(head, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(arm_right, (float)Math.toRadians(-120), 0, (float)Math.toRadians(30));
        animator.rotate(arm_left, (float)Math.toRadians(-40), 0, (float)Math.toRadians(-20));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(arm_right, (float)Math.toRadians(-40), 0, (float)Math.toRadians(20));
        animator.rotate(arm_left, (float)Math.toRadians(-120), 0, (float)Math.toRadians(-30));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, (float)Math.toRadians(-20), 0, 0);
        animator.rotate(arm_right, (float)Math.toRadians(-120), 0, (float)Math.toRadians(30));
        animator.rotate(arm_left, (float)Math.toRadians(-40), 0, (float)Math.toRadians(-20));
        animator.endKeyframe();
        animator.resetKeyframe(3);

    }

    @Override
    public void setupAnim(EntityRaccoon entityRaccoon, float limbSwing, float limbSwingAmount, float ageInTicks, float v3, float v4) {
        this.animate(entityRaccoon, limbSwing, limbSwingAmount, ageInTicks, v3, v4);
        float partialTicks = Minecraft.getInstance().getFrameTime();
        float normalProgress = 5F;
        float walkSpeed = 1F;
        float walkDegree = 0.8F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.2F;
        float runProgress = 5F * limbSwingAmount;
        float begProgress = entityRaccoon.prevBegProgress + (entityRaccoon.begProgress - entityRaccoon.prevBegProgress) * partialTicks;
        float standProgress0 = entityRaccoon.prevStandProgress + (entityRaccoon.standProgress - entityRaccoon.prevStandProgress) * partialTicks;
        float sitProgress = entityRaccoon.prevSitProgress + (entityRaccoon.sitProgress - entityRaccoon.prevSitProgress) * partialTicks;
        float standProgress = Math.max(Math.max(begProgress, standProgress0) - sitProgress, 0);
        float washProgress = entityRaccoon.prevWashProgress + (entityRaccoon.washProgress - entityRaccoon.prevWashProgress) * partialTicks;
        progressRotationPrev(body, standProgress, (float) Math.toRadians(-70), 0, 0, 5f);
        progressRotationPrev(arm_left, standProgress, (float) Math.toRadians(70), 0, 0, 5f);
        progressRotationPrev(arm_right, standProgress, (float) Math.toRadians(70), 0, 0, 5f);
        progressRotationPrev(leg_left, standProgress, (float) Math.toRadians(70), 0, 0, 5f);
        progressRotationPrev(leg_right, standProgress, (float) Math.toRadians(70), 0, 0, 5f);
        progressRotationPrev(head, standProgress, (float) Math.toRadians(70), 0, 0, 5f);

        progressRotationPrev(body, sitProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressRotationPrev(head, sitProgress, (float) Math.toRadians(10), 0, 0, 5f);
        progressRotationPrev(tail, sitProgress, (float) Math.toRadians(10), 0, 0, 5f);
        progressRotationPrev(arm_left, sitProgress, (float) Math.toRadians(-75), 0, 0, 5f);
        progressRotationPrev(arm_right, sitProgress, (float) Math.toRadians(-75), 0, 0, 5f);
        progressRotationPrev(leg_left, sitProgress, (float) Math.toRadians(-80), (float) Math.toRadians(-20), 0, 5f);
        progressRotationPrev(leg_right, sitProgress, (float) Math.toRadians(-80), (float) Math.toRadians(20), 0, 5f);
        progressPositionPrev(body, sitProgress, 0, 4, 0, 5f);
        progressPositionPrev(leg_left, sitProgress, 1.5F, 1, 0, 5f);
        progressPositionPrev(leg_right, sitProgress, -1.5F, 1, 0, 5f);
        progressPositionPrev(arm_left, sitProgress, 0, 2.5F, 1, 5f);
        progressPositionPrev(arm_right, sitProgress, 0, 2.5F, 1, 5f);

        progressPositionPrev(head, standProgress, 0, -2F, 0, 5f);
        progressPositionPrev(body, standProgress, 0, -3F, 0, 5f);
        progressPositionPrev(leg_left, standProgress, 0, -2F, 0, 5f);
        progressPositionPrev(leg_right, standProgress, 0, -2F, 0, 5f);
        progressPositionPrev(arm_left, standProgress, 0, 1F, 0, 5f);
        progressPositionPrev(arm_right, standProgress, 0, 1F, 0, 5f);
        progressRotationPrev(tail, standProgress, (float) Math.toRadians(80), 0, 0, 5f);
        progressRotationPrev(body, normalProgress, (float) Math.toRadians(10), 0, 0, 5f);
        progressRotationPrev(head, normalProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressRotationPrev(arm_left, normalProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressRotationPrev(arm_right, normalProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressRotationPrev(leg_left, normalProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressRotationPrev(leg_right, normalProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressPositionPrev(body, normalProgress, 0, 1F, 0, 5f);
        progressPositionPrev(arm_left, normalProgress, 0, -1.9F, 0, 5f);
        progressPositionPrev(arm_right, normalProgress, 0, -1.9F, 0, 5f);
        progressPositionPrev(leg_left, normalProgress, 0, 0F, -1, 5f);
        progressPositionPrev(leg_right, normalProgress, 0, 0F, -1, 5f);
        progressRotationPrev(tail, 5F - runProgress, (float) Math.toRadians(-35), 0, 0, 5f);
        progressRotationPrev(tail, runProgress, (float) Math.toRadians(-10), 0, 0, 5f);
        progressRotationPrev(ear_left, Math.max(runProgress, begProgress), (float) Math.toRadians(-20), 0, (float) Math.toRadians(20), 5f);
        progressRotationPrev(ear_right, Math.max(runProgress, begProgress), (float) Math.toRadians(-20), 0, (float) Math.toRadians(-20), 5f);
        progressRotationPrev(arm_right, begProgress, (float) Math.toRadians(-25), 0, 0, 5f);
        progressRotationPrev(arm_left, begProgress, (float) Math.toRadians(-25), 0, 0, 5f);
        if (begProgress > 0) {
            this.walk(head, 0.7F, 0.2F, false, 2F, -0.2F, ageInTicks, begProgress * 0.2F);
            this.walk(arm_right, 0.7F, 1.2F, false, 0F, -1.0F, ageInTicks, begProgress * 0.2F);
            this.flap(arm_right, 0.7F, 0.25F, false, -1F, 0.2F, ageInTicks, begProgress * 0.2F);
            this.walk(arm_left, 0.7F, 1.2F, false, 0F, -1.0F, ageInTicks, begProgress * 0.2F);
            this.flap(arm_left, 0.7F, 0.25F, true, -1F, 0.2F, ageInTicks, begProgress * 0.2F);
        }
        progressRotationPrev(body, washProgress, (float) Math.toRadians(15), 0, 0, 5f);
        progressRotationPrev(arm_left, washProgress, (float) Math.toRadians(-90), 0, 0, 5f);
        progressRotationPrev(arm_right, washProgress, (float) Math.toRadians(-90), 0, 0, 5f);
        progressRotationPrev(leg_left, washProgress, (float) Math.toRadians(-15), 0, 0, 5f);
        progressRotationPrev(leg_right, washProgress, (float) Math.toRadians(-15), 0, 0, 5f);
        progressRotationPrev(tail, washProgress, (float) Math.toRadians(-15), 0, 0, 5f);
        progressRotationPrev(head, washProgress, (float) Math.toRadians(15), 0, 0, 5f);
        progressPositionPrev(head, washProgress, 0, -3F, 0.4F, 5f);
        progressPositionPrev(body, washProgress, 0, 1.5F, -10, 5f);
        progressPositionPrev(arm_left, washProgress, 0, 1F, -1.4F, 5f);
        progressPositionPrev(arm_right, washProgress, 0, 1F, -1.4F, 5f);
        if (washProgress > 0) {
            this.arm_left.rotationPointY -= (float) (-Math.abs(Math.sin(ageInTicks * 0.5F) * (double) washProgress * 0.2D * 1));
            this.arm_left.rotationPointZ -= (float) (-Math.abs(Math.sin(ageInTicks * 0.25F) * (double) washProgress * 0.2D * 3));
            this.arm_right.rotationPointY -= (float) (-Math.abs(Math.sin(ageInTicks * 0.5F) * (double) washProgress * 0.2D * 1));
            this.arm_right.rotationPointZ -= (float) (-Math.abs(Math.cos(ageInTicks * 0.25F) * (double) washProgress * 0.2D * 3));
            this.swing(arm_right, 0.5F, 0.25F, false, 2F, -0.1F, ageInTicks, washProgress * 0.2F);
            this.swing(arm_left, 0.5F, 0.25F, true, 2F, -0.1F, ageInTicks, washProgress * 0.2F);
            float bodyFlap = (float) (Math.sin(ageInTicks * 0.5F) * (double) washProgress * 0.2D * 0.15F);
            body.rotateAngleZ += bodyFlap;
            tail.rotateAngleY += bodyFlap;
            head.rotateAngleZ -= bodyFlap;
            leg_left.rotateAngleZ -= bodyFlap;
            leg_right.rotateAngleZ -= bodyFlap;
        }else{
            this.faceTarget(v3, v4, 1.3F, head);
        }
        if (standProgress <= 0) {
            this.walk(arm_right, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
            this.walk(arm_left, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        }
        this.swing(tail, idleSpeed, idleDegree, false, 2F, 0F, ageInTicks, 1);
        this.swing(body, walkSpeed, walkDegree * 0.3F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.swing(tail, walkSpeed, walkDegree * 1, false, 4F, 0F, limbSwing, limbSwingAmount);
        this.walk(leg_right, walkSpeed, walkDegree * 1.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(leg_left, walkSpeed, walkDegree * 1.1F, true, 0F, 0F, limbSwing, limbSwingAmount);

    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.65F;
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

    public Vec3 getRidingPosition(Vec3 offsetIn){
        PoseStack armStack = new PoseStack();
        armStack.pushPose();
        root.translateAndRotate(armStack);
        body.translateAndRotate(armStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.transform(armStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
        armStack.popPose();
        return vec3;
    }
}
