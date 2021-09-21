package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelPlatypus extends AdvancedEntityModel<EntityPlatypus> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox beak;
    private final AdvancedModelBox fedora;
    private final AdvancedModelBox arm_left;
    private final AdvancedModelBox arm_right;
    private final AdvancedModelBox leg_left;
    private final AdvancedModelBox leg_right;
    private final AdvancedModelBox tail;

    public ModelPlatypus() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -2.6F, -0.1F);
        root.addChild(body);
        body.setTextureOffset(0, 2).addBox(-3.5F, -3.4F, -5.9F, 7.0F, 6.0F, 11.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, -0.4F, -5.9F);
        body.addChild(head);
        head.setTextureOffset(5, 51).addBox(-3.0F, -2.5F, -4.0F, 6.0F, 5.0F, 4.0F, 0.0F, false);

        beak = new AdvancedModelBox(this);
        beak.setPos(0.0F, 2.0F, -5.0F);
        head.addChild(beak);
        beak.setTextureOffset(28, 0).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);

        fedora = new AdvancedModelBox(this);
        fedora.setPos(0.0F, -2.6F, -1.9F);
        head.addChild(fedora);
        fedora.setTextureOffset(23, 20).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, 0.0F, false);
        fedora.setTextureOffset(29, 30).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);

        arm_left = new AdvancedModelBox(this);
        arm_left.setPos(3.5F, 2.1F, -4.9F);
        body.addChild(arm_left);
        arm_left.setTextureOffset(7, 39).addBox(0.0F, -0.5F, 0.0F, 3.0F, 1.0F, 3.0F, 0.0F, false);

        arm_right = new AdvancedModelBox(this);
        arm_right.setPos(-3.5F, 2.1F, -4.9F);
        body.addChild(arm_right);
        arm_right.setTextureOffset(7, 39).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 1.0F, 3.0F, 0.0F, true);

        leg_left = new AdvancedModelBox(this);
        leg_left.setPos(3.5F, 2.1F, 2.6F);
        body.addChild(leg_left);
        leg_left.setTextureOffset(27, 43).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);

        leg_right = new AdvancedModelBox(this);
        leg_right.setPos(-3.5F, 2.1F, 2.6F);
        body.addChild(leg_right);
        leg_right.setTextureOffset(27, 43).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 3.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setPos(0.0F, -0.4F, 5.1F);
        body.addChild(tail);
        tail.setTextureOffset(0, 24).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 3.0F, 8.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, head, beak, fedora, arm_left, arm_right, leg_left, leg_right);
    }

    @Override
    public void setupAnim(EntityPlatypus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float walkSpeed = 1F;
        float walkDegree = 1.3F;
        float idleSpeed = 0.3F;
        float idleDegree = 0.2F;
        float swimSpeed = 1.3F;
        float swimDegree = 1.3F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float digProgress = entity.prevDigProgress + (entity.digProgress - entity.prevDigProgress) * partialTick;
        float swimProgress = entity.prevInWaterProgress + (entity.inWaterProgress - entity.prevInWaterProgress) * partialTick;
        progressPositionPrev(body, swimProgress, 0, -3.5F, 0, 5f);
        progressRotationPrev(arm_left, swimProgress, (float) Math.toRadians(-5), 0, (float) Math.toRadians(75), 5f);
        progressRotationPrev(arm_right, swimProgress, (float) Math.toRadians(-5), 0, (float) Math.toRadians(-75), 5f);
        progressRotationPrev(leg_left, swimProgress, (float) Math.toRadians(-5), 0, (float) Math.toRadians(75), 5f);
        progressRotationPrev(leg_right, swimProgress, (float) Math.toRadians(-5), 0, (float) Math.toRadians(-75), 5f);
        progressRotationPrev(tail, swimProgress, (float) Math.toRadians(-10), 0, 0, 5f);

        progressPositionPrev(body, digProgress, 0, -1.5F, 0, 5f);
        progressPositionPrev(arm_right, digProgress, 1, -1, -0.5F, 5f);
        progressPositionPrev(arm_left, digProgress, -1, -1, -0.5F, 5f);
        progressRotationPrev(body, digProgress, (float) Math.toRadians(35), 0, 0, 5f);
        progressRotationPrev(tail, digProgress, (float) Math.toRadians(10), 0, 0, 5f);
        progressRotationPrev(head, digProgress, (float) Math.toRadians(-20), 0, 0, 5f);
        progressRotationPrev(arm_left, digProgress, (float) Math.toRadians(-30),  (float) Math.toRadians(10),  (float) Math.toRadians(-65), 5f);
        progressRotationPrev(arm_right, digProgress, (float) Math.toRadians(-30), (float) Math.toRadians(-10),  (float) Math.toRadians(65), 5f);

        if(digProgress > 0F){
            this.swing(body, 0.8F, idleDegree * 1.2F, false, 3F, 0F, ageInTicks, 1);
            this.swing(head, 0.8F, idleDegree * 0.7F, false, 3F, 0F, ageInTicks, 1);
            this.swing(arm_right, 0.8F, idleDegree * 2.6F, false, 1F, -0.25F, ageInTicks, 1);
            this.swing(arm_left, 0.8F, idleDegree * 2.6F, true, 1F, -0.25F, ageInTicks, 1);
        }else if(entity.isSensing() || entity.isSensingVisual()){
            this.swing(head, swimSpeed, swimDegree * 0.25F, false, 2F, 0F, ageInTicks, 1);
            this.walk(head, swimSpeed, swimDegree * 0.25F, false, 1F, 0F, ageInTicks, 1);
        }else{
            this.faceTarget(netHeadYaw, headPitch, 1.2F, head);
        }
        if(swimProgress > 0F){
            this.bob(body, idleSpeed, idleDegree * 2F, false, ageInTicks, 1);
            this.walk(tail, swimSpeed, swimDegree * 0.1F, false, 3F, 0.25F, limbSwing, limbSwingAmount);
            this.swing(tail, swimSpeed, swimDegree * 0.5F, false, 2F, 0F, limbSwing, limbSwingAmount);
            this.swing(body, swimSpeed, swimDegree * 0.3F, false, 3F, 0F, limbSwing, limbSwingAmount);
            this.swing(head, swimSpeed, swimDegree * 0.5F, true, 3F, 0F, limbSwing, limbSwingAmount);
            this.flap(arm_right, swimSpeed, swimDegree * 0.9F, false, 1F, 0.85F, limbSwing, limbSwingAmount);
            this.flap(arm_left, swimSpeed, swimDegree * 0.9F, true, 1F, 0.85F, limbSwing, limbSwingAmount);
            this.flap(leg_right, swimSpeed, swimDegree * 0.9F, false, 3F, 0.85F, limbSwing, limbSwingAmount);
            this.flap(leg_left, swimSpeed, swimDegree * 0.9F, true, 3F, 0.85F, limbSwing, limbSwingAmount);
            this.walk(body, swimSpeed, swimDegree * 0.2F, false, 0F, 0F, limbSwing, limbSwingAmount);

        }else{
            this.swing(tail, idleSpeed * 0.5F, idleDegree, false, 0F, 0F, ageInTicks, 1);
            this.bob(body, walkSpeed * 1.75F, walkDegree * 1F, false, limbSwing, limbSwingAmount);
            this.swing(body, walkSpeed, walkDegree * 0.3F, false, 3F, 0F, limbSwing, limbSwingAmount);
            this.swing(head, walkSpeed, walkDegree * 0.2F, true, 3F, 0F, limbSwing, limbSwingAmount);
            this.walk(tail, walkSpeed, walkDegree * 0.3F, false, 3F, 0.1F, limbSwing, limbSwingAmount);
            this.swing(arm_left, walkSpeed, walkDegree * 0.5F, true, 1F, 0.15F, limbSwing, limbSwingAmount);
            this.flap(arm_left, walkSpeed, walkDegree * 0.5F, true, 0F, 0.25F, limbSwing, limbSwingAmount);
            this.swing(arm_right, walkSpeed, walkDegree * 0.5F, true, 1F, -0.15F, limbSwing, limbSwingAmount);
            this.flap(arm_right, walkSpeed, walkDegree * 0.5F, true, 0F, -0.25F, limbSwing, limbSwingAmount);
            this.swing(leg_left, walkSpeed, walkDegree * 0.5F, false, 1F, -0.15F, limbSwing, limbSwingAmount);
            this.flap(leg_left, walkSpeed, walkDegree * 0.5F, false, 0F, -0.15F, limbSwing, limbSwingAmount);
            this.swing(leg_right, walkSpeed, walkDegree * 0.5F, false, 1F, 0.15F, limbSwing, limbSwingAmount);
            this.flap(leg_right, walkSpeed, walkDegree * 0.5F, false, 0F, 0.15F, limbSwing, limbSwingAmount);
        }
    }

    @Override
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


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}