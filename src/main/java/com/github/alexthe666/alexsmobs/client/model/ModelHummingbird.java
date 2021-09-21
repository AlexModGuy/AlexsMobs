package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelHummingbird extends AdvancedEntityModel<EntityHummingbird> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox wingL;
    private final AdvancedModelBox wingL_r1;
    private final AdvancedModelBox wingR;
    private final AdvancedModelBox wingR_r1;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox legL;
    private final AdvancedModelBox legR;

    public ModelHummingbird() {
        texWidth = 32;
        texHeight = 32;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -0.5F, -0.5F);
        root.addChild(body);
        setRotationAngle(body, 0.2618F, 0.0F, 0.0F);
        body.setTextureOffset(0, 6).addBox(-1.5F, -4.7F, -1.4F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, -4.4F, 0.6F);
        body.addChild(head);
        setRotationAngle(head, -0.2182F, 0.0F, 0.0F);
        head.setTextureOffset(10, 12).addBox(-1.5F, -3.1F, -2.1F, 3.0F, 3.0F, 3.0F, 0.1F, false);
        head.setTextureOffset(12, 0).addBox(-0.5F, -2.1F, -5.2F, 1.0F, 1.0F, 3.0F, 0.0F, false);

        wingL = new AdvancedModelBox(this);
        wingL.setPos(1.5F, -4.5F, 0.5F);
        body.addChild(wingL);


        wingL_r1 = new AdvancedModelBox(this);
        wingL_r1.setPos(-0.3F, 0.0F, -1.0F);
        wingL.addChild(wingL_r1);
        setRotationAngle(wingL_r1, 0.0F, 0.0F, -0.0873F);
        wingL_r1.setTextureOffset(0, 15).addBox(0.0F, 0.0F, -0.1F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        wingR = new AdvancedModelBox(this);
        wingR.setPos(-1.5F, -4.5F, 0.5F);
        body.addChild(wingR);


        wingR_r1 = new AdvancedModelBox(this);
        wingR_r1.setPos(0.3F, 0.0F, -1.0F);
        wingR.addChild(wingR_r1);
        setRotationAngle(wingR_r1, 0.0F, 0.0F, 0.0873F);
        wingR_r1.setTextureOffset(0, 15).addBox(-1.0F, 0.0F, -0.1F, 1.0F, 5.0F, 2.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setPos(0.0F, -0.1F, 1.5F);
        body.addChild(tail);
        setRotationAngle(tail, -0.48F, 0.0F, 0.0F);
        tail.setTextureOffset(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 5.0F, 0.0F, false);

        legL = new AdvancedModelBox(this);
        legL.setPos(0.9F, -0.7F, -1.1F);
        body.addChild(legL);
        setRotationAngle(legL, -0.2618F, 0.0F, 0.0F);
        legL.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        legR = new AdvancedModelBox(this);
        legR.setPos(-0.9F, -0.7F, -1.1F);
        body.addChild(legR);
        setRotationAngle(legR, -0.2618F, 0.0F, 0.0F);
        legR.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityHummingbird entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flySpeed = 1.0F;
        float flyDegree = 0.8F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float flyProgress = entityIn.prevFlyProgress + (entityIn.flyProgress - entityIn.prevFlyProgress) * partialTick;
        float zoomProgress = entityIn.prevMovingProgress + (entityIn.movingProgress - entityIn.prevMovingProgress) * partialTick;
        float sipProgress = entityIn.prevSipProgress + (entityIn.sipProgress - entityIn.prevSipProgress) * partialTick;
        progressRotationPrev(body, flyProgress, (float) Math.toRadians(30), 0, 0, 5F);
        progressRotationPrev(head, flyProgress, (float) Math.toRadians(-30), 0, 0, 5F);
        progressRotationPrev(wingL, flyProgress, (float) Math.toRadians(15), (float) Math.toRadians(55), (float) Math.toRadians(-100), 5F);
        progressRotationPrev(wingR, flyProgress, (float) Math.toRadians(15), (float) Math.toRadians(-55), (float) Math.toRadians(100), 5F);
        progressRotationPrev(tail, flyProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(legL, flyProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        progressRotationPrev(legR, flyProgress, (float) Math.toRadians(-45), 0, 0, 5F);
        progressPositionPrev(wingL, flyProgress, 0, 1, 0, 5F);
        progressPositionPrev(wingR, flyProgress, 0, 1, 0, 5F);
        progressPositionPrev(legL, flyProgress, 0, -0.4F, 0, 5F);
        progressPositionPrev(legR, flyProgress, 0, -0.4F, 0, 5F);
        progressPositionPrev(head, flyProgress, 0, -0.5F, -1F, 5F);
        if (flyProgress > 0) {
            progressRotationPrev(body, zoomProgress, (float) Math.toRadians(25), 0, 0, 5F);
            progressRotationPrev(head, zoomProgress, (float) Math.toRadians(-25), 0, 0, 5F);
            progressRotationPrev(wingL, zoomProgress, (float) Math.toRadians(20), 0, 0, 5F);
            progressRotationPrev(wingR, zoomProgress, (float) Math.toRadians(20), 0, 0, 5F);
        }
        progressPositionPrev(body, sipProgress, 0, -1, 3, 5F);
        progressRotationPrev(head, sipProgress, (float) Math.toRadians(60), 0, 0, 5F);
        if (entityIn.isFlying()) {
            this.flap(wingL, flySpeed * 2.3F, flyDegree * 2.3F, false, 0, 0F, ageInTicks, 1);
            this.walk(wingL, flySpeed * 2.3F, flyDegree, false, 0, -0.4F, ageInTicks, 1);
            this.flap(wingR, flySpeed * 2.3F, flyDegree * 2.3F, true, 0, 0F, ageInTicks, 1);
            this.walk(wingR, flySpeed * 2.3F, flyDegree, false, 0, -0.4F, ageInTicks, 1);
            this.bob(legL, flySpeed * 0.3F, flyDegree * -0.2F, false, ageInTicks, 1);
            this.bob(legR, flySpeed * 0.3F, flyDegree * -0.2F, false, ageInTicks, 1);
            this.walk(body, flySpeed * 0.3F, flyDegree * 0.05F, false, 0, 0.1F, ageInTicks, 1);
            this.walk(tail, flySpeed * 0.3F, flyDegree * 0.1F, false, 1, 0.1F, ageInTicks, 1);
            this.walk(head, flySpeed * 0.3F, flyDegree * 0.05F, true, 2, 0.1F, ageInTicks, 1);
            this.bob(body, flySpeed * 0.3F, flyDegree * 0.3F, true, ageInTicks, 1);

        }
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, wingL, wingL_r1, wingR, wingR_r1, tail, legL, legR);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.75F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.75D, 0.125D);
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

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}