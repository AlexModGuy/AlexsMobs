package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelSeal extends AdvancedEntityModel<EntitySeal> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox arm_left;
    public final AdvancedModelBox arm_right;
    public final AdvancedModelBox head;
    public final AdvancedModelBox snout;
    public final AdvancedModelBox tail;
    public final AdvancedModelBox tail2;

    public ModelSeal() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -4.5F, -4.0F);
        root.addChild(body);
        body.texOffs(0, 0).addBox(-4.0F, -1.5F, -5.0F, 8.0F, 6.0F, 10.0F, 0.0F, false);

        arm_left = new AdvancedModelBox(this);
        arm_left.setPos(4.0F, 3.8F, -1.0F);
        body.addChild(arm_left);
        arm_left.texOffs(27, 29).addBox(0.0F, -0.5F, -2.0F, 5.0F, 1.0F, 4.0F, 0.0F, false);

        arm_right = new AdvancedModelBox(this);
        arm_right.setPos(-4.0F, 3.8F, -1.0F);
        body.addChild(arm_right);
        arm_right.texOffs(27, 29).addBox(-5.0F, -0.5F, -2.0F, 5.0F, 1.0F, 4.0F, 0.0F, true);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, 1.5F, -5.0F);
        body.addChild(head);
        head.texOffs(27, 0).addBox(-2.5F, -2.5F, -3.0F, 5.0F, 5.0F, 3.0F, 0.0F, false);

        snout = new AdvancedModelBox(this);
        snout.setPos(0.0F, 1.0F, -3.0F);
        head.addChild(snout);
        snout.texOffs(0, 33).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 2.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setPos(0.0F, 1.5F, 5.0F);
        body.addChild(tail);
        tail.texOffs(0, 17).addBox(-2.5F, -2.0F, 0.0F, 5.0F, 5.0F, 10.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setPos(0.0F, 1.7F, 9.0F);
        tail.addChild(tail2);
        tail2.texOffs(21, 17).addBox(-4.0F, -1.0F, -1.0F, 8.0F, 2.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, tail2, head, arm_left, arm_right, snout);
    }

    @Override
    public void setupAnim(EntitySeal entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = young ? 0.9F : 1.5F;
        float walkDegree = 1F;
        float swimSpeed = 0.5F;
        float swimDegree = 0.5F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float baskProgress = entity.prevBaskProgress + (entity.baskProgress - entity.prevBaskProgress) * partialTick;
        float swimAngle = entity.prevSwimAngle + (entity.getSwimAngle() - entity.prevSwimAngle) * partialTick;
        float diggingProgress = entity.prevDigProgress + (entity.digProgress - entity.prevDigProgress) * partialTick;
        int baskType = entity.getId() % 5;
        progressRotationPrev(body, diggingProgress,  (float) Math.toRadians(70), 0, 0, 5F);
        progressRotationPrev(head, diggingProgress,  (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(tail, diggingProgress,  (float) Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(arm_left, diggingProgress, 0,  (float) Math.toRadians(30), 0, 5F);
        progressRotationPrev(arm_right, diggingProgress,  0,  (float) Math.toRadians(-30), 0, 5F);
        progressPositionPrev(body, diggingProgress, 0, -6.7F, 5, 5F);
        progressPositionPrev(arm_left, diggingProgress, -1, 0, -2, 5F);
        progressPositionPrev(arm_right, diggingProgress, 1, 0, -2, 5F);
        if(diggingProgress > 0){
            float amount = diggingProgress * 0.2F;
            this.swing(arm_right, 0.6F, 0.85F, true, 1F, -0.1F, ageInTicks, amount);
            this.swing(arm_left, 0.6F, 0.85F, false, 1F, -0.1F, ageInTicks, amount);
            this.walk(tail, 0.6F, 0.1F, false, 3F, -0.1F, ageInTicks, amount);
            this.bob(body, 0.3F, 3F, true, ageInTicks, amount);
        }
        if (baskProgress > 0) {
            this.walk(head, 0.05F, 0.2F, true, 1F, -0.1F, ageInTicks, 1);
            if (baskType == 0) {
                progressRotationPrev(body, baskProgress, 0, 0, (float) Math.toRadians(70), 5F);
                progressRotationPrev(head, baskProgress, 0, (float) Math.toRadians(-20), (float) Math.toRadians(20), 5F);
                progressRotationPrev(arm_left, baskProgress, 0, 0, (float) Math.toRadians(110), 5F);
                progressRotationPrev(arm_right, baskProgress, 0, 0, (float) Math.toRadians(-120), 5F);
                progressRotationPrev(tail, baskProgress, 0, 0, (float) Math.toRadians(-20), 5F);
                progressRotationPrev(tail2, baskProgress, 0, 0, (float) Math.toRadians(-20), 5F);
                progressPositionPrev(arm_left, baskProgress, -1, 0, 0, 5F);
                progressPositionPrev(head, baskProgress, 0, 0, 1, 5F);
                this.flap(arm_right, 0.05F, 0.2F, true, 3F, -0.1F, ageInTicks, 1);
                this.flap(arm_left, 0.05F, 0.2F, true, 3F, -0.1F, ageInTicks, 1);
            } else if (baskType == 1) {
                progressRotationPrev(body, baskProgress, 0, 0, (float) Math.toRadians(-70), 5F);
                progressRotationPrev(head, baskProgress, 0, (float) Math.toRadians(20), (float) Math.toRadians(-20), 5F);
                progressRotationPrev(arm_right, baskProgress, 0, 0, (float) Math.toRadians(-110), 5F);
                progressRotationPrev(arm_left, baskProgress, 0, 0, (float) Math.toRadians(120), 5F);
                progressRotationPrev(tail, baskProgress, 0, 0, (float) Math.toRadians(20), 5F);
                progressRotationPrev(tail2, baskProgress, 0, 0, (float) Math.toRadians(20), 5F);
                progressPositionPrev(arm_right, baskProgress, 1, 0, 0, 5F);
                progressPositionPrev(head, baskProgress, 0, 0, 1, 5F);
                this.flap(arm_right, 0.05F, 0.2F, false, 3F, -0.1F, ageInTicks, 1);
                this.flap(arm_left, 0.05F, 0.2F, false, 3F, -0.1F, ageInTicks, 1);
            } else if (baskType == 2) {
                progressRotationPrev(arm_right, baskProgress, 0, 0, (float) Math.toRadians(30), 5F);
                progressRotationPrev(arm_left, baskProgress, 0, 0, (float) Math.toRadians(-40), 5F);
                progressRotationPrev(body, baskProgress, 0, 0, (float) Math.toRadians(160), 5F);
                progressRotationPrev(tail, baskProgress, (float) Math.toRadians(15), 0, 0, 5F);
                progressRotationPrev(tail2, baskProgress, (float) Math.toRadians(15), 0, 0, 5F);
                progressRotationPrev(head, baskProgress, (float) Math.toRadians(-10), (float) Math.toRadians(20), (float) Math.toRadians(30), 5F);
                progressPositionPrev(body, baskProgress, 0, 2, 0, 5F);
                this.flap(arm_right, 0.05F, 0.2F, true, 3F, -0.1F, ageInTicks, 1);
                this.flap(arm_left, 0.05F, 0.2F, false, 3F, -0.1F, ageInTicks, 1);
            } else if (baskType == 3) {
                progressRotationPrev(body, baskProgress, 0, (float) Math.toRadians(20), 0, 5F);
                progressRotationPrev(tail, baskProgress, 0, (float) Math.toRadians(25), 0, 5F);
                progressRotationPrev(tail2, baskProgress, 0, (float) Math.toRadians(30), 0, 5F);
                progressRotationPrev(head, baskProgress, 0, (float) Math.toRadians(-20), (float) Math.toRadians(25), 5F);
                progressRotationPrev(arm_right, baskProgress, 0, (float) Math.toRadians(-20), 0, 5F);
                progressRotationPrev(arm_left, baskProgress, 0, (float) Math.toRadians(30), 0, 5F);
                progressPositionPrev(head, baskProgress, 0, 0, 1, 5F);
                this.flap(arm_right, 0.05F, 0.2F, true, 3F, -0.1F, ageInTicks, 1);
                this.flap(arm_left, 0.05F, 0.2F, false, 3F, -0.1F, ageInTicks, 1);
            } else if (baskType == 4) {
                progressRotationPrev(body, baskProgress, 0, (float) Math.toRadians(-20), 0, 5F);
                progressRotationPrev(tail, baskProgress, 0, (float) Math.toRadians(-25), 0, 5F);
                progressRotationPrev(tail2, baskProgress, 0, (float) Math.toRadians(-30), 0, 5F);
                progressRotationPrev(head, baskProgress, 0, (float) Math.toRadians(20), (float) Math.toRadians(-25), 5F);
                progressRotationPrev(arm_right, baskProgress, 0, (float) Math.toRadians(30), 0, 5F);
                progressRotationPrev(arm_left, baskProgress, 0, (float) Math.toRadians(-20), 0, 5F);
                progressPositionPrev(head, baskProgress, 0, 0, 1, 5F);
                this.flap(arm_right, 0.05F, 0.2F, true, 3F, -0.1F, ageInTicks, 1);
                this.flap(arm_left, 0.05F, 0.2F, false, 3F, -0.1F, ageInTicks, 1);
            }
        }
        AdvancedModelBox[] bodyParts = new AdvancedModelBox[]{head, body, tail, tail2};
        if (!entity.isInWater()) {
            this.body.y += (float) (Math.sin((double) (limbSwing * walkSpeed) - (Math.PI * 0.1F)) * (double) limbSwingAmount * (double) walkDegree * 4D - (limbSwingAmount * walkDegree * 4D));
            this.chainWave(bodyParts, walkSpeed, walkDegree * 0.5F, -2F, limbSwing, limbSwingAmount);
            this.walk(tail2, walkSpeed, walkDegree, false, 9, 0, limbSwing, limbSwingAmount);
            this.flap(arm_right, walkSpeed, walkDegree * 1.8F, true, 8.7F, 0, limbSwing, limbSwingAmount);
            this.flap(arm_left, walkSpeed, walkDegree * 1.8F, false, 8.7F, 0, limbSwing, limbSwingAmount);
        } else {
            this.body.xRot += headPitch * ((float)Math.PI / 180F);
            this.body.y += (float) (Math.sin(limbSwing * swimSpeed) * (double) limbSwingAmount * (double) swimDegree * 9D - (limbSwingAmount * swimDegree * 9D));
            this.chainWave(bodyParts, swimSpeed, swimDegree, -3F, limbSwing, limbSwingAmount);
            this.walk(tail2, swimSpeed, swimDegree, false, -8F, 0, limbSwing, limbSwingAmount);
            this.flap(arm_right, swimSpeed * 0.5F, swimDegree * 4.5F, true, 3F, 0, limbSwing, limbSwingAmount);
            this.flap(arm_left, swimSpeed * 0.5F, swimDegree * 4.5F, false, 3F, 0, limbSwing, limbSwingAmount);
        }
        this.faceTarget(netHeadYaw, headPitch, 1, head);
        float yawAmount = swimAngle / 57.295776F * 0.5F;
        body.zRot += yawAmount;

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

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.xRot = x;
        advancedModelBox.yRot = y;
        advancedModelBox.zRot = z;
    }
}